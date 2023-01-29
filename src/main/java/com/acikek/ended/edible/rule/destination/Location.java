package com.acikek.ended.edible.rule.destination;

import com.acikek.ended.api.location.LocationType;
import com.acikek.ended.api.location.PositionProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.commons.lang3.EnumUtils;

public record Location(LocationType type, PositionProvider pos, RegistryKey<World> world) {

    public BlockPos getPos(ServerWorld destinationWorld, ServerPlayerEntity player) {
        return switch (type) {
            case POSITION -> pos.getPosition(destinationWorld, player);
            case WORLD_SPAWN -> destinationWorld.getSpawnPos();
            case PLAYER_SPAWN -> player.getSpawnPointPosition() != null && player.getSpawnPointDimension() == destinationWorld.getRegistryKey()
                    ? player.getSpawnPointPosition()
                    : destinationWorld.getSpawnPos();
        };
    }

    public static BlockPos getBlockPos(JsonObject obj) {
        if (JsonHelper.hasArray(obj, "pos")) {
            JsonArray array = JsonHelper.getArray(obj, "pos");
            return new BlockPos(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());
        }
        int x = JsonHelper.getInt(obj, "x");
        int y = JsonHelper.getInt(obj, "y");
        int z = JsonHelper.getInt(obj, "z");
        return new BlockPos(x, y, z);
    }

    public static LocationType typeFromJson(boolean isDefault, Location defaultLocation, JsonObject obj) {
        String typeString = JsonHelper.getString(obj, "location", "");
        LocationType type = EnumUtils.getEnumIgnoreCase(LocationType.class, typeString, isDefault ? null : LocationType.POSITION);
        if (type == null && !isDefault) {
            if (defaultLocation != null && defaultLocation.type != null) {
                return defaultLocation.type;
            }
            throw new JsonSyntaxException("location must be 'position', 'world_spawn', or 'player_spawn'");
        }
        return type;
    }

    public static RegistryKey<World> worldFromJson(Location defaultLocation, JsonObject obj) {
        if (!JsonHelper.hasString(obj, "world")) {
            return defaultLocation != null
                    ? defaultLocation.world
                    : null;
        }
        return RegistryKey.of(Registry.WORLD_KEY, new Identifier(JsonHelper.getString(obj, "world")));
    }
}
