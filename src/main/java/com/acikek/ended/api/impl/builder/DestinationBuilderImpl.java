package com.acikek.ended.api.impl.builder;

import com.acikek.ended.api.builder.DestinationBuilder;
import com.acikek.ended.api.location.LocationType;
import com.acikek.ended.api.location.PositionProvider;
import com.acikek.ended.edible.rule.destination.Destination;
import com.acikek.ended.edible.rule.destination.Location;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class DestinationBuilderImpl implements DestinationBuilder {

    public LocationType type;
    public PositionProvider pos;
    public RegistryKey<World> world;
    public Text message;

    public void tryChangeType(LocationType newType) {
        if (type != null) {
            throw new IllegalStateException("destination type is already " + type);
        }
        type = newType;
    }

    @Override
    public DestinationBuilder world(RegistryKey<World> world) {
        this.world = world;
        return this;
    }

    @Override
    public DestinationBuilder location(PositionProvider provider) {
        pos = provider;
        tryChangeType(LocationType.POSITION);
        return this;
    }

    @Override
    public DestinationBuilder location(LocationType type) {
        tryChangeType(type);
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
