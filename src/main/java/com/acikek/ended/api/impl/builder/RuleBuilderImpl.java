package com.acikek.ended.api.impl.builder;

import com.acikek.ended.api.builder.RuleBuilder;
import com.acikek.ended.edible.rule.EdibleRule;
import com.acikek.ended.edible.rule.WorldSource;
import com.acikek.ended.edible.rule.destination.Destination;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleBuilderImpl implements RuleBuilder {

    public List<WorldSource> from = new ArrayList<>();
    public Map<String, Destination> destinations = new HashMap<>();

    @Override
    public RuleBuilder addSource(Identifier id, boolean accept) {
        from.add(new WorldSource(id, accept));
        return this;
    }

    @Override
    public RuleBuilder addDestination(String name, Destination destination) {
        destinations.put(name, destination);
        return this;
    }

    @Override
    public RuleBuilder addDestinations(Map<String, Destination> destinations) {
        this.destinations.putAll(destinations);
        return this;
    }

    @Override
    public EdibleRule build() {
        if (destinations.isEmpty()) {
            throw new IllegalStateException("Rules must have at least one destination");
        }
        return new EdibleRule(from, destinations);
    }
}
