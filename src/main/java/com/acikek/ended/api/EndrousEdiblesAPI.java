package com.acikek.ended.api;

import com.acikek.ended.api.impl.EndrousEdiblesAPIImpl;
import com.acikek.ended.edible.Edible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class EndrousEdiblesAPI {

    /**
     * Registers an edible to be exposed to {@link EndrousEdiblesAPI#getEdibleById(Identifier)} and {@link EndrousEdiblesAPI#getEdibleFromStack(ItemStack)}.
     */
    public static void registerEdible(Edible edible) {
        EndrousEdiblesAPIImpl.registerEdible(edible);
    }

    /**
     * @throws IllegalArgumentException if the edible does not exist
     */
    public static Edible getEdibleById(Identifier id) {
        return EndrousEdiblesAPIImpl.getEdibleById(id);
    }

    /**
     * Tests against edible {@link Ingredient}s and returns the first one that matches.
     * Ignores edibles that do not have an {@code edible} field.
     * @return the found edible
     */
    public static Edible getEdibleFromStack(ItemStack stack) {
        return EndrousEdiblesAPIImpl.getEdibleFromStack(stack);
    }
}
