package com.acikek.ended.edible.rule.destination;

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
        Location location = Location.fromJson(isDefault, defaultDestination != null ? defaultDestination.location : null, obj);
        Text message = defaultDestination != null && defaultDestination.message != null
                ? defaultDestination.message
                : messageFromJson(langKeyId, destinationName, defaultDestination, obj);
        return new Destination(location, message);
    }
}
