package com.acikek.ended.edible.rule;

import com.acikek.ended.api.builder.RuleBuilder;
import com.acikek.ended.edible.rule.destination.Destination;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;

import java.util.List;
import java.util.Map;

public record EdibleRule(List<WorldSource> from, Map<String, Destination> destinations) {

    public static EdibleRule fromJson(JsonObject obj, String langKeyId) {
        RuleBuilder builder = RuleBuilder.create();
        if (obj.has("from")) {
            builder.addSources(WorldSource.fromJson(JsonHelper.getArray(obj, "from")));
        }
        if (!obj.has("destinations")) {
            Destination destination = Destination.fromJson(obj, langKeyId, "destination").build();
            return builder
                    .addDestination("destination", destination)
                    .build();
        }
        Destination defaultDestination = obj.has("default")
                ? Destination.fromJson(JsonHelper.getObject(obj, "default"), langKeyId, "default").build(true)
                : Destination.NULL;
        for (Map.Entry<String, JsonElement> entry : JsonHelper.getObject(obj, "destinations").entrySet()) {
            JsonObject destinationObj = entry.getValue().getAsJsonObject();
            Destination destination = Destination.fromJson(destinationObj, langKeyId, entry.getKey())
                    .withDefault(defaultDestination)
                    .build();
            builder.addDestination(entry.getKey(), destination);
        }
        return builder.build();
    }
}
