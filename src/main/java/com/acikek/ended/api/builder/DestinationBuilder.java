package com.acikek.ended.api.builder;

import com.acikek.ended.api.location.LocationType;
import com.acikek.ended.api.impl.builder.DestinationBuilderImpl;
import com.acikek.ended.api.location.PositionProvider;
import com.acikek.ended.edible.Edible;
import com.acikek.ended.edible.rule.destination.Destination;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

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
        return world(RegistryKey.of(RegistryKeys.WORLD, world));
    }

    /**
     * Sets the location type to {@link LocationType#POSITION} with the given position provider.
     */
    DestinationBuilder location(PositionProvider provider);

    /**
     * Provides a static {@link BlockPos} for the position callback.
     * @see DestinationBuilder#location(PositionProvider)
     */
    default DestinationBuilder location(BlockPos pos) {
        return location(Vec3d.ofBottomCenter(pos));
    }

    /**
     * @see DestinationBuilder#location(BlockPos)
     * @deprecated in favor of {@link DestinationBuilder#location(double, double, double)}
     */
    @Deprecated
    default DestinationBuilder location(int x, int y, int z) {
        return location(new BlockPos(x, y, z));
    }

    /**
     * Provides a static {@link TeleportTarget} for the position callback.
     * @see DestinationBuilder#location(PositionProvider)
     */
    default DestinationBuilder location(TeleportTarget target) {
        return location(new PositionProvider.Instance(target));
    }

    /**
     * @see DestinationBuilder#location(TeleportTarget)
     */
    default DestinationBuilder location(Vec3d pos, float yaw, float pitch) {
        return location(new TeleportTarget(pos, Vec3d.ZERO, yaw, pitch));
    }

    /**
     * @see DestinationBuilder#location(TeleportTarget)
     */
    default DestinationBuilder location(double x, double y, double z, float yaw, float pitch) {
        return location(new TeleportTarget(new Vec3d(x, y, z), Vec3d.ZERO, yaw, pitch));
    }

    /**
     * Provides a static {@link Vec3d} for the position callback.
     * @see DestinationBuilder#location(PositionProvider)
     */
    default DestinationBuilder location(Vec3d pos) {
        return location(new PositionProvider.Position(pos));
    }

    /**
     * @see DestinationBuilder#location(Vec3d)
     */
    default DestinationBuilder location(double x, double y, double z) {
        return location(new Vec3d(x, y, z));
    }

    /**
     * Sets the location type.<br>
     * Use {@link DestinationBuilder#location(PositionProvider)} or its equivalents to set the type to {@link LocationType#POSITION} given a block position.
     */
    DestinationBuilder location(LocationType type);

    /**
     * @param text The message to display after teleportation
     */
    DestinationBuilder message(Text text);

    /**
     * @param event The sound to play after teleportation
     */
    DestinationBuilder sound(SoundEvent event);

    /**
     *
     * @param destination The default destination values
     */
    DestinationBuilder withDefault(Destination destination);

    /**
     * @param isDefault whether to check for a valid location type
     */
    @ApiStatus.Internal
    Destination build(boolean isDefault);

    default Destination build() {
        return build(false);
    }
}
