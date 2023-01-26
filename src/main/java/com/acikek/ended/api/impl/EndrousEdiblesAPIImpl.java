package com.acikek.ended.api.impl;

import com.acikek.ended.edible.Edible;
import com.acikek.ended.load.EdibleLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class EndrousEdiblesAPIImpl {

    public static Map<Identifier, Edible> registeredEdibles = new HashMap<>();
    public static Map<Identifier, Edible> allEdibles = new HashMap<>();

    public static void registerEdible(Edible edible) {
        registeredEdibles.put(edible.id(), edible);
    }

    public static void refreshAllEdibles() {
        allEdibles.clear();
        allEdibles.putAll(EdibleLoader.loadedEdibles);
        allEdibles.putAll(registeredEdibles);
    }

    public static Edible getEdibleById(Identifier id) {
        Edible edible = allEdibles.get(id);
        if (edible == null) {
            throw new IllegalArgumentException("'" + id + "' is not a valid edible");
        }
        return edible;
    }

    public static Edible getEdibleFromStack(ItemStack stack) {
        for (Edible edible : allEdibles.values()) {
            if (edible.edible() != null && edible.edible().test(stack)) {
                return edible;
            }
        }
        return null;
    }
}
