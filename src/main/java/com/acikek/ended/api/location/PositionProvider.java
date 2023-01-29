package com.acikek.ended.api.location;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface PositionProvider {

    BlockPos getPosition(ServerWorld world, ServerPlayerEntity player);
}
