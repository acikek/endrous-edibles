package com.acikek.ended.api.impl.builder;

import com.acikek.ended.api.builder.DestinationBuilder;
import com.acikek.ended.edible.rule.destination.Destination;
import com.acikek.ended.edible.rule.destination.Location;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DestinationBuilderImpl implements DestinationBuilder {

    public Location.Type type;
    public BlockPos pos;
    public RegistryKey<World> world;
    public Text message;

    public void tryChangeType(Location.Type newType) {
        if (type != null) {
            throw new IllegalStateException("destination type is already " + type);
        }
        type = newType;
    }

    @Override
    public DestinationBuilder location(BlockPos pos) {
        this.pos = pos;
        tryChangeType(Location.Type.POSITION);
        return this;
    }

    @Override
    public DestinationBuilder world(RegistryKey<World> world) {
        this.world = world;
        return this;
    }

    @Override
    public DestinationBuilder worldSpawn() {
        tryChangeType(Location.Type.WORLD_SPAWN);
        return this;
    }

    @Override
    public DestinationBuilder playerSpawn() {
        tryChangeType(Location.Type.PLAYER_SPAWN);
        return this;
    }

    @Override
    public DestinationBuilder message(Text text) {
        message = text;
        return this;
    }

    @Override
    public Destination build(boolean isDefault) {
        if (type == null && !isDefault) {
            throw new IllegalStateException("No location type specified for destination");
        }
        return new Destination(new Location(type, pos, world), message);
    }
}
