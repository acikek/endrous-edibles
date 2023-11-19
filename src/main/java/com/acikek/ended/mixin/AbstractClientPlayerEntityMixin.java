package com.acikek.ended.mixin;

import com.acikek.ended.api.EndrousEdiblesAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {

    @Inject(method = "getFovMultiplier", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingItem()Z"))
    private void ended$usingFov(CallbackInfoReturnable<Float> cir, float f, ItemStack itemStack) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (!entity.isUsingItem()) {
            return;
        }
        EndrousEdiblesAPI.getEdibleFromItem(itemStack.getItem()).ifPresent(edible -> {
            float s = (float) entity.getItemUseTime() / 20.0f;
            float cap = s > 1.0f ? 1.0f : (s * s);
            float mod = f * (1.0f - cap * 0.15f);
            cir.setReturnValue(MathHelper.lerp(MinecraftClient.getInstance().options.getFovEffectScale().getValue().floatValue(), 1.0f, mod));
        });
    }
}
