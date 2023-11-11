package com.acikek.ended.api.impl;

import com.acikek.ended.edible.Edible;
import com.acikek.ended.load.EdibleLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EndrousEdiblesAPIImpl {

    public static Map<Identifier, Edible> registeredEdibles = new HashMap<>();
    public static Map<Identifier, Edible> allEdibles = new HashMap<>();

    public static class MatchData {
        public long lastCheckTime;
        public Edible edible;
    }

    public static Map<Item, MatchData> itemMatches = new HashMap<>();

    public static void registerEdible(Edible edible) {
        registeredEdibles.put(edible.id(), edible);
    }

    public static void refreshAllEdibles() {
        allEdibles.clear();
        allEdibles.putAll(EdibleLoader.loadedEdibles);
        allEdibles.putAll(registeredEdibles);
    }

    public static Edible getEdibleById(Identifier id) {
        return allEdibles.get(id);
    }

    public static List<Edible> getEdibles() {
        return allEdibles.values().stream().toList();
    }

    public static Optional<Edible> getEdibleFromItem(Item item) {
        MatchData data = itemMatches.computeIfAbsent(item, k -> new MatchData());
        if (data.lastCheckTime < EdibleLoader.lastReloadTime) {
            ItemStack stack = item.getDefaultStack();
            for (Edible edible : allEdibles.values()) {
                if (edible.edible() != null && edible.edible().test(stack)) {
                    data.edible = edible;
                }
            }
            data.lastCheckTime = System.currentTimeMillis();
        }
        return Optional.ofNullable(data.edible);
    }
}
