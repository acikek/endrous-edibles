package com.acikek.ended.api;

import com.acikek.ended.api.impl.EndrousEdiblesAPIImpl;
import com.acikek.ended.edible.Edible;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class EndrousEdiblesAPI {

    /**
     * Registers an edible to be exposed to {@link EndrousEdiblesAPI#getEdibleById(Identifier)} and {@link EndrousEdiblesAPI#getEdibleFromItem(Item)}.
     */
    public static void registerEdible(Edible edible) {
        EndrousEdiblesAPIImpl.registerEdible(edible);
    }

    /**
     * @return the edible if found, or {@code null} if not
     */
    public static Edible getEdibleById(Identifier id) {
        return EndrousEdiblesAPIImpl.getEdibleById(id);
    }

    /**
     * @return all registered edibles, both loaded from JSON and registered via {@link EndrousEdiblesAPI#registerEdible(Edible)}
     */
    public static List<Edible> getEdibles() {
        return EndrousEdiblesAPIImpl.getEdibles();
    }

    /**
     * Tests against edible {@link Ingredient}s and returns the first one that matches.<br>
     * Ignores edibles that do not have an {@code edible} field.<br>
     * Keeps track of edible matches even as reloads occur, so an item is only matched against edibles the first time this method is called after a reload.
     * @return the found edible
     */
    public static Optional<Edible> getEdibleFromItem(Item item) {
        return EndrousEdiblesAPIImpl.getEdibleFromItem(item);
    }
}
