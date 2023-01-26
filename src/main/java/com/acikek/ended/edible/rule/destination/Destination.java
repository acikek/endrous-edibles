package com.acikek.ended.edible.rule.destination;

import com.acikek.ended.api.builder.DestinationBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record Destination(Location location, Text message) {

    public static Text getTypeMessage(String type, String langKeyId, String destinationName) {
        Text destinationText = Text.translatable("ended.destination." + langKeyId + "." + destinationName);
        return Text.translatable("ended.type." + langKeyId + "." + type, destinationText)
                .styled(style -> style.withItalic(true).withFormatting(Formatting.GRAY));
    }

    public static Text messageFromJson(String langKeyId, String destinationName, Destination defaultDestination, JsonObject obj) {
        JsonElement element = obj.get("message");
        if (element == null) {
            return defaultDestination != null
                    ? defaultDestination.message
                    : null;
        }
        if (element.isJsonPrimitive()) {
            return getTypeMessage(element.getAsString(), langKeyId, destinationName);
        }
        return Text.Serializer.fromJson(element);
    }

    public static Destination fromJson(String langKeyId, String destinationName, boolean isDefault, Destination defaultDestination, JsonObject obj) {
        DestinationBuilder builder = DestinationBuilder.create();
        Location defaultLocation = defaultDestination != null ? defaultDestination.location : null;
        Location.Type type = Location.typeFromJson(isDefault, defaultDestination != null ? defaultDestination.location : null, obj);
        // For default destination types. Non-default destinations would throw if the type was null in the previous call.
        if (type != null) {
            switch (type) {
                case POSITION -> builder.location(Location.getBlockPos(obj));
                case WORLD_SPAWN -> builder.worldSpawn();
                case PLAYER_SPAWN -> builder.playerSpawn();
            }
        }
        return builder
                .world(Location.worldFromJson(defaultLocation, obj))
                .message(messageFromJson(langKeyId, destinationName, defaultDestination, obj))
                // Deprecated for internal use
                .build(isDefault);
    }
}
