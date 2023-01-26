package com.acikek.ended.api.builder;

import com.acikek.ended.api.impl.builder.DestinationBuilderImpl;
import com.acikek.ended.edible.Edible;
import com.acikek.ended.edible.rule.destination.Destination;
import com.acikek.ended.edible.rule.destination.Location;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface DestinationBuilder {

    static DestinationBuilder create() {
        return new DestinationBuilderImpl();
    }

    /**
     * @param world The destination world key. Validated upon teleportation.
     * @see Edible#trigger(ServerPlayerEntity)
     */
    DestinationBuilder world(RegistryKey<World> world);

    /**
     * @see DestinationBuilder#world(RegistryKey)
     */
    default DestinationBuilder world(Identifier world) {
        return world(RegistryKey.of(Registry.WORLD_KEY, world));
    }

    /**
     * Sets the location type to {@link Location.Type#POSITION} with the given position.
     */
    DestinationBuilder location(BlockPos pos);

    /**
     * @see DestinationBuilder#location(BlockPos)
     */
    default DestinationBuilder location(int x, int y, int z) {
        return location(new BlockPos(x, y, z));
    }

    /**
     * Sets the location type to {@link Location.Type#WORLD_SPAWN}. The player will always appear at the world's spawn.
     */
    DestinationBuilder worldSpawn();

    /**
     * Sets the location type to {@link Location.Type#PLAYER_SPAWN} The player will appear at their set spawn if it exists in the destination world; otherwise, they will appear at the world's spawn.
     */
    DestinationBuilder playerSpawn();

    /**
     * @param text The message to display after teleportation
     */
    DestinationBuilder message(Text text);

    Destination build();
}
