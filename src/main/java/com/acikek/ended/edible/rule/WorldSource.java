package com.acikek.ended.edible.rule;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public record WorldSource(Identifier world, boolean accept) {

    public static boolean test(World world, List<WorldSource> sources) {
        if (sources.isEmpty()) {
            return true;
        }
        RegistryKey<World> key = world.getRegistryKey();
        for (WorldSource source : sources) {
            boolean matches = source.world.equals(key.getValue());
            if (matches == source.accept) {
                return true;
            }
        }
        return false;
    }

    public static List<WorldSource> fromJson(JsonArray array) {
        List<WorldSource> entries = new ArrayList<>();
        for (JsonElement element : array) {
            String string = element.getAsString();
            boolean accept = !string.startsWith("!");
            Identifier world = new Identifier(accept ? string : string.substring(1));
            entries.add(new WorldSource(world, accept));
        }
        return entries;
    }
}
