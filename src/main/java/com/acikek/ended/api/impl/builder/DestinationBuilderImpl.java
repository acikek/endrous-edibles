package com.acikek.ended.api.impl.builder;

import com.acikek.ended.api.builder.DestinationBuilder;
import com.acikek.ended.api.location.LocationType;
import com.acikek.ended.api.location.PositionProvider;
import com.acikek.ended.edible.rule.destination.Destination;
import com.acikek.ended.edible.rule.destination.Location;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class DestinationBuilderImpl implements DestinationBuilder {

    public LocationType type;
    public PositionProvider pos;
    public RegistryKey<World> world;
    public Text message;
    public SoundEvent sound;

    @Override
    public DestinationBuilder world(RegistryKey<World> world) {
        this.world = world;
        return this;
    }

    @Override
    public DestinationBuilder location(PositionProvider provider) {
        pos = provider;
        type = LocationType.POSITION;
        return this;
    }

    @Override
    public DestinationBuilder location(LocationType type) {
        this.type = type;
        return this;
    }

    @Override
    public DestinationBuilder message(Text text) {
        message = text;
        return this;
    }

    @Override
    public DestinationBuilder sound(SoundEvent event) {
        sound = event;
        return this;
    }

    @Override
    public DestinationBuilder withDefault(Destination destination) {
        if (type == null && destination.location().type() != null) {
            type = destination.location().type();
        }
        if (world == null && destination.location().world() != null) {
            world = destination.location().world();
        }
        if (message == null && destination.message() != null) {
            message = destination.message();
        }
        if (sound == null && destination.sound() != null) {
            sound = destination.sound();
        }
        return this;
    }

    @Override
    public Destination build(boolean isDefault) {
        if (type == null && !isDefault) {
            throw new IllegalStateException("No location type specified for destination");
        }
        return new Destination(new Location(type, pos, world), message, sound);
    }
}
