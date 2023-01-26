package com.acikek.ended.edible.rule;

import com.acikek.ended.edible.rule.destination.Destination;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EdibleRule(List<WorldSource> from, Map<String, Destination> destinations) {

    public static EdibleRule fromJson(String langKeyId, JsonObject obj) {
        List<WorldSource> from = WorldSource.fromJson(JsonHelper.getArray(obj, "from"));
        Destination defaultDestination = obj.has("default")
                ? Destination.fromJson(langKeyId, "default", true, null, JsonHelper.getObject(obj, "default"))
                : null;
        Map<String, Destination> destinations = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : JsonHelper.getObject(obj, "destinations").entrySet()) {
            JsonObject destinationObj = entry.getValue().getAsJsonObject();
            Destination destination = Destination.fromJson(langKeyId, entry.getKey(), false, defaultDestination, destinationObj);
            destinations.put(entry.getKey(), destination);
        }
        return new EdibleRule(from, destinations);
    }
}
