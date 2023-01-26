package com.acikek.ended.api.builder;

import com.acikek.ended.api.EndrousEdiblesAPI;
import com.acikek.ended.api.impl.builder.EdibleBuilderImpl;
import com.acikek.ended.edible.Edible;
import com.acikek.ended.edible.rule.EdibleRule;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface EdibleBuilder {

    static EdibleBuilder create() {
        return new EdibleBuilderImpl();
    }

    /**
     * Sets the ingredient matcher for this edible.<br>
     * If this edible is registered with {@link EndrousEdiblesAPI#registerEdible(Edible)}, it will be exposed to ingredient matching when eating an item.
     */
    EdibleBuilder edible(Ingredient ingredient);

    /**
     * Adds a conditional-based destination set rule. Edibles must have at least one rule.
     * @see RuleBuilder#create()
     */
    EdibleBuilder addRule(EdibleRule rule);

    /**
     * @see EdibleBuilder#addRule(EdibleRule)
     */
    EdibleBuilder addRules(Collection<EdibleRule> rules);

    Edible build(Identifier id);
}
