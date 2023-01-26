package com.acikek.ended.mixin;

import com.acikek.ended.api.EndrousEdiblesAPI;
import com.acikek.ended.edible.Edible;
import com.acikek.ended.load.EdibleLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    private Edible ended$edible;
    private long ended$lastCheckedTime;

    @Inject(method = "finishUsing", at = @At("HEAD"))
    public void ended$teleport(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof ServerPlayerEntity player) {
            if (ended$lastCheckedTime < EdibleLoader.lastReloadTime) {
                ended$edible = null;
                ItemStack defaultStack = ((Item) (Object) this).getDefaultStack();
                ended$edible = EndrousEdiblesAPI.getEdibleFromStack(defaultStack);
                ended$lastCheckedTime = System.currentTimeMillis();
            }
            if (ended$edible != null && ended$edible.trigger(player) == Edible.TriggerResult.FAIL) {
                ended$edible = null;
            }
        }
    }
}
