package com.acikek.ended.edible.rule.destination;

import com.acikek.ended.api.location.LocationType;
import com.acikek.ended.api.builder.DestinationBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public record Destination(Location location, Text message, SoundEvent sound) {

    public static final Destination NULL = new Destination(Location.NULL, null, null);

    public static Text getTypeMessage(String type, String langKeyId, String destinationName) {
        Text destinationText = Text.translatable("ended.destination." + langKeyId + "." + destinationName);
        return Text.translatable("ended.type." + langKeyId + "." + type, destinationText)
                .styled(style -> style.withItalic(true).withFormatting(Formatting.GRAY));
    }

    public static Text messageFromJson(JsonElement element, String langKeyId, String destinationName) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonPrimitive()) {
            return getTypeMessage(element.getAsString(), langKeyId, destinationName);
        }
        return Text.Serializer.fromJson(element);
    }

    public static SoundEvent soundFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (JsonHelper.isBoolean(element) && JsonHelper.asBoolean(element, "sound key")) {
            return SoundEvents.BLOCK_PORTAL_TRAVEL;
        }
        return SoundEvent.of(new Identifier(JsonHelper.asString(element, "sound key")));
    }

    public static DestinationBuilder fromJson(JsonObject obj, String langKeyId, String destinationName) {
        DestinationBuilder builder = DestinationBuilder.create();
        if (obj.has("pos")) {
            builder.location(Location.providerFromObj(obj));
        }
        else {
            builder.location(Location.typeFromJson(obj.get("location")));
        }
        return builder
                .world(Location.worldFromJson(obj.get("world")))
                .message(messageFromJson(obj.get("message"), langKeyId, destinationName))
                .sound(soundFromJson(obj.get("sound")));
    }
}
