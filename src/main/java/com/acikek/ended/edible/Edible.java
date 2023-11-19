package com.acikek.ended.edible;

import com.acikek.ended.EndrousEdibles;
import com.acikek.ended.api.builder.EdibleBuilder;
import com.acikek.ended.api.location.EdibleMode;
import com.acikek.ended.api.location.LocationType;
import com.acikek.ended.edible.rule.EdibleRule;
import com.acikek.ended.edible.rule.WorldSource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
import java.util.stream.IntStream;

public record Edible(Identifier id, EdibleMode mode, Ingredient edible, List<EdibleRule> rules) {

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
        RegistryKey<World> worldKey = entry.getValue().location().world();
        ServerWorld destinationWorld = player.server.getWorld(worldKey);
        if (destinationWorld == null) {
            String source = "Destination '" + entry.getKey() + "' in rule " + rule.getLeft() + " of edible '" + id + "'";
            EndrousEdibles.LOGGER.error(source + " tried to teleport to invalid world '" + worldKey.getValue() + "'!");
            return TriggerResult.FAIL;
        }
        BlockPos blockPos = entry.getValue().location().getPos(destinationWorld, player);
        Vec3d pos = Vec3d.ofBottomCenter(blockPos);
        player.teleport(destinationWorld, pos.x, pos.y, pos.z, player.getYaw(), player.getPitch());
        if (entry.getValue().message() != null) {
            player.sendMessage(entry.getValue().message());
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
