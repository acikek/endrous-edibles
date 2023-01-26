package com.acikek.ended.edible.rule;

import com.acikek.ended.api.builder.RuleBuilder;
import com.acikek.ended.edible.rule.destination.Destination;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;

import java.util.List;
import java.util.Map;

public record EdibleRule(List<WorldSource> from, Map<String, Destination> destinations) {

    public static EdibleRule fromJson(String langKeyId, JsonObject obj) {
        RuleBuilder builder = RuleBuilder.create()
                .addSources(WorldSource.fromJson(JsonHelper.getArray(obj, "from")));
        Destination defaultDestination = obj.has("default")
                ? Destination.fromJson(langKeyId, "default", true, null, JsonHelper.getObject(obj, "default"))
                : null;
        for (Map.Entry<String, JsonElement> entry : JsonHelper.getObject(obj, "destinations").entrySet()) {
            JsonObject destinationObj = entry.getValue().getAsJsonObject();
            Destination destination = Destination.fromJson(langKeyId, entry.getKey(), false, defaultDestination, destinationObj);
            builder.addDestination(entry.getKey(), destination);
        }
        return builder.build();
    }
}
