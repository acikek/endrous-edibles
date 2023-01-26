package com.acikek.ended.api.builder;

import com.acikek.ended.api.impl.builder.RuleBuilderImpl;
import com.acikek.ended.edible.rule.EdibleRule;
import com.acikek.ended.edible.rule.destination.Destination;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface RuleBuilder {

    static RuleBuilder create() {
        return new RuleBuilderImpl();
    }

    /**
     * Adds a world source condition. Rules are filtered based on these sources upon trigger.<br>
     * For example, if a player triggers an edible in {@code minecraft:overworld}, rules with that source world will pass. Source filtering is an {@code OR} operation.<br>
     * Rules always pass source checking when no source conditions are added.
     * @param accept Whether this source should be an inverse entry. For example, an inverse entry of {@code minecraft:overworld} would match any world that is not the overworld.
     */
    RuleBuilder addSource(Identifier world, boolean accept);

    /**
     * @see RuleBuilder#addSource(Identifier, boolean)
     */
    default RuleBuilder addSource(Identifier world) {
        return addSource(world, true);
    }

    /**
     * Adds a destination to this rule. Rules must have at least one destination.
     * @see DestinationBuilder#create()
     */
    RuleBuilder addDestination(String name, Destination destination);

    /**
     * @see RuleBuilder#addDestination(String, Destination)
     */
    RuleBuilder addDestinations(Map<String, Destination> destinations);

    EdibleRule build();
}
