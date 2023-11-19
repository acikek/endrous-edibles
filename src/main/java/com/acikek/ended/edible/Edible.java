package com.acikek.ended.edible;

import com.acikek.ended.EndrousEdibles;
import com.acikek.ended.api.builder.EdibleBuilder;
import com.acikek.ended.api.location.EdibleMode;
import com.acikek.ended.edible.rule.EdibleRule;
import com.acikek.ended.edible.rule.WorldSource;
import com.acikek.ended.edible.rule.destination.Destination;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
import java.util.stream.IntStream;

public record Edible(Identifier id, EdibleMode mode, Ingredient edible, List<EdibleRule> rules) {

    public static final Identifier TELEPORT_SOUND = EndrousEdibles.id("teleport_sound");

    public enum TriggerResult {
        SUCCESS,
        PASS,
        FAIL
    }

    /**
     * Filters {@link Edible#rules} to a single entry, chooses a random destination, and attempts to teleport the player there.
     * @return {@link TriggerResult#PASS} if no suitable rules were found; {@link TriggerResult#FAIL} if the world was invalid; {@link TriggerResult#SUCCESS} otherwise.
     */
    public TriggerResult trigger(ServerPlayerEntity player) {
        List<Pair<String, EdibleRule>> filtered = IntStream.range(0, rules.size())
                .mapToObj(i -> new Pair<>("#" + (i + 1), rules.get(i)))
                .filter(pair -> WorldSource.test(player.getWorld(), pair.getRight().from()))
                .toList();
        if (filtered.isEmpty()) {
            return TriggerResult.PASS;
        }
        if (filtered.size() > 1) {
            List<String> indices = filtered.stream().map(Pair::getLeft).toList();
            EndrousEdibles.LOGGER.warn("Edible '" + id + "' matched more than one rule (" + String.join(", ", indices) + ")");
        }
        var rule = filtered.get(0);
        var destinations = rule.getRight().destinations().entrySet().stream().toList();
        var entry = destinations.get(player.getWorld().random.nextInt(destinations.size()));
        Destination destination = entry.getValue();
        RegistryKey<World> worldKey = destination.location().world();
        ServerWorld destinationWorld = player.server.getWorld(worldKey);
        if (destinationWorld == null) {
            String source = "Destination '" + entry.getKey() + "' in rule " + rule.getLeft() + " of edible '" + id + "'";
            EndrousEdibles.LOGGER.error(source + " tried to teleport to invalid world '" + worldKey.getValue() + "'!");
            return TriggerResult.FAIL;
        }
        TeleportTarget target = destination.location().getPos(destinationWorld, player);
        // FabricDimensions call has a pre-existing sound effect
        player.teleport(destinationWorld, target.position.getX(), target.position.getY(), target.position.getZ(), target.yaw, target.pitch);
        if (destination.message() != null) {
            player.sendMessage(destination.message());
        }
        if (destination.sound() != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            destination.sound().writeBuf(buf);
            ServerPlayNetworking.send(player, TELEPORT_SOUND, buf);
        }
        return TriggerResult.SUCCESS;
    }

    public static Edible fromJson(Identifier id, JsonObject obj) {
        EdibleBuilder builder = EdibleBuilder.create();
        if (obj.has("edible")) {
            builder.edible(Ingredient.fromJson(obj.get("edible")));
        }
        String modeString = JsonHelper.getString(obj, "mode", null);
        if (modeString != null) {
            EdibleMode mode = EnumUtils.getEnumIgnoreCase(EdibleMode.class, modeString);
            if (mode == null) {
                throw new JsonSyntaxException("mode must be 'consume' or 'interact'");
            }
            builder.mode(mode);
        }
        String langKeyId = id.getNamespace() + "." + id.getPath().replace('/', '.');
        if (obj.has("rules")) {
            for (JsonElement element : JsonHelper.getArray(obj, "rules")) {
                builder.addRule(EdibleRule.fromJson(element.getAsJsonObject(), langKeyId));
            }
        }
        else {
            builder.addRule(EdibleRule.fromJson(obj, langKeyId));
        }
        return builder.build(id);
    }
}
