package com.acikek.ended.api.impl.builder;

import com.acikek.ended.api.builder.EdibleBuilder;
import com.acikek.ended.edible.Edible;
import com.acikek.ended.edible.rule.EdibleRule;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EdibleBuilderImpl implements EdibleBuilder {

    public Ingredient edible;
    public List<EdibleRule> rules = new ArrayList<>();

    @Override
    public EdibleBuilder edible(Ingredient ingredient) {
        edible = ingredient;
        return this;
    }

    @Override
    public EdibleBuilder addRule(EdibleRule rule) {
        rules.add(rule);
        return this;
    }

    @Override
    public EdibleBuilder addRules(Collection<EdibleRule> rules) {
        this.rules.addAll(rules);
        return this;
    }

    @Override
    public Edible build(Identifier id) {
        if (rules.isEmpty()) {
            throw new IllegalStateException("Edibles must have at least one rule");
        }
        return new Edible(id, edible, rules);
    }
}
