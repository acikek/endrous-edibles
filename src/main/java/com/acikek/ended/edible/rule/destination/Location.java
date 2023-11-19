package com.acikek.ended.edible.rule.destination;

import com.acikek.ended.api.location.LocationType;
import com.acikek.ended.api.location.PositionProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.apache.commons.lang3.EnumUtils;

public record Location(LocationType type, PositionProvider pos, RegistryKey<World> world) {

    public static final Location NULL = new Location(LocationType.POSITION, null, null);

    public BlockPos getPos(ServerWorld destinationWorld, ServerPlayerEntity player) {
        return switch (type) {
            case POSITION -> pos.getPosition(destinationWorld, player);
            case WORLD_SPAWN -> destinationWorld.getSpawnPos();
            case PLAYER_SPAWN -> player.getSpawnPointPosition() != null && player.getSpawnPointDimension() == destinationWorld.getRegistryKey()
                    ? player.getSpawnPointPosition()
                    : destinationWorld.getSpawnPos();
            case MIRROR -> player.getBlockPos().withY(destinationWorld.getTopY(Heightmap.Type.WORLD_SURFACE, player.getBlockX(), player.getBlockZ()));
        };
    }

    public static BlockPos getBlockPos(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray array = JsonHelper.asArray(element, "pos array");
            return new BlockPos(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());
        }
        if (element.isJsonObject()) {
            JsonObject obj = JsonHelper.asObject(element, "pos object");
            int x = JsonHelper.getInt(obj, "x");
            int y = JsonHelper.getInt(obj, "y");
            int z = JsonHelper.getInt(obj, "z");
            return new BlockPos(x, y, z);
        }
        throw new JsonSyntaxException("location must have a position (cannot be defaulted)");
    }

    public static LocationType typeFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        String typeString = JsonHelper.asString(element, "location type");
        LocationType type = EnumUtils.getEnumIgnoreCase(LocationType.class, typeString, LocationType.POSITION);
        if (type == null) {
            throw new JsonSyntaxException("location must be 'position', 'world_spawn', 'player_spawn', or 'mirror'");
        }
        return type;
    }

    public static RegistryKey<World> worldFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        String idString = JsonHelper.asString(element, "world id");
        return RegistryKey.of(RegistryKeys.WORLD, new Identifier(idString));
    }
}
