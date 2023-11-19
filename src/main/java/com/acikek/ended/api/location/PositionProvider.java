package com.acikek.ended.api.location;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

@FunctionalInterface
public interface PositionProvider {

    TeleportTarget getPosition(ServerWorld world, ServerPlayerEntity player);

    record Instance(TeleportTarget target) implements PositionProvider {
        @Override
        public TeleportTarget getPosition(ServerWorld world, ServerPlayerEntity player) {
            return target;
        }
    }

    static TeleportTarget getFromPos(Vec3d pos, ServerPlayerEntity player) {
        return new TeleportTarget(pos, Vec3d.ZERO, player.getYaw(), player.getPitch());
    }

    static TeleportTarget getFromBlockPos(BlockPos pos, ServerPlayerEntity player) {
        return getFromPos(Vec3d.ofBottomCenter(pos), player);
    }

    record Position(Vec3d pos) implements PositionProvider {
        @Override
        public TeleportTarget getPosition(ServerWorld world, ServerPlayerEntity player) {
            return getFromPos(pos, player);
        }
    }
}
