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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.apache.commons.lang3.EnumUtils;

public record Location(LocationType type, PositionProvider pos, RegistryKey<World> world) {

    public static final Location NULL = new Location(LocationType.POSITION, null, null);

    public TeleportTarget getPos(ServerWorld destinationWorld, ServerPlayerEntity player) {
        return switch (type) {
            case POSITION -> pos.getPosition(destinationWorld, player);
            case WORLD_SPAWN -> PositionProvider.getFromBlockPos(destinationWorld.getSpawnPos(), player);
            case PLAYER_SPAWN -> {
                BlockPos pos = player.getSpawnPointPosition() != null && player.getSpawnPointDimension() == destinationWorld.getRegistryKey()
                        ? player.getSpawnPointPosition()
                        : destinationWorld.getSpawnPos();
                yield PositionProvider.getFromBlockPos(pos, player);
            }
            // TODO: Make this actually work for nether.
            // TODO: Dimension coordinates scaling
            case MIRROR -> PositionProvider.getFromBlockPos(destinationWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, player.getBlockPos()), player);
        };
    }

    public static Vec3d posFromObj(JsonObject obj) {
        if (JsonHelper.hasArray(obj, "pos")) {
            JsonArray array = obj.getAsJsonArray("pos");
            return new Vec3d(array.get(0).getAsDouble(), array.get(1).getAsDouble(), array.get(2).getAsDouble());
        }
        double x = JsonHelper.getDouble(obj, "x");
        double y = JsonHelper.getDouble(obj, "y");
        double z = JsonHelper.getDouble(obj, "z");
        return new Vec3d(x, y, z);
    }

    public static PositionProvider providerFromObj(JsonObject obj) {
        Vec3d pos = posFromObj(obj);
        if (!obj.has("yaw") && !obj.has("pitch")) {
            return new PositionProvider.Position(pos);
        }
        float yaw = JsonHelper.getFloat(obj, "yaw", 0.0f);
        float pitch = JsonHelper.getFloat(obj, "pitch", 0.0f);
        return new PositionProvider.Instance(new TeleportTarget(pos, Vec3d.ZERO, yaw, pitch));
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
