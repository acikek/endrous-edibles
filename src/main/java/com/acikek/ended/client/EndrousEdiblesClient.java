package com.acikek.ended.client;

import com.acikek.ended.edible.Edible;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;

public class EndrousEdiblesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Edible.TELEPORT_SOUND, (client, handler, buf, responseSender) -> {
            final SoundEvent event = SoundEvent.fromBuf(buf);
            client.execute(() -> {
                client.getSoundManager().play(PositionedSoundInstance.ambient(event, client.world.random.nextFloat() * 0.4f + 0.8f, 0.25f));
            });
        });
    }
}
