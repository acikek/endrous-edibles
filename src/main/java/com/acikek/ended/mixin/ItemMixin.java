package com.acikek.ended.mixin;

import com.acikek.ended.api.EndrousEdiblesAPI;
import com.acikek.ended.api.location.EdibleMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Shadow public abstract boolean isFood();

    @Inject(method = "finishUsing", at = @At("HEAD"))
    public void ended$consumableTeleport(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (isFood() && user instanceof ServerPlayerEntity player) {
            EndrousEdiblesAPI.getEdibleFromItem((Item) (Object) this).ifPresent(edible ->
                    edible.trigger(player)
            );
        }
    }

    @Inject(method = "getMaxUseTime", cancellable = true, at = @At("HEAD"))
    public void ended$interactUseTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        EndrousEdiblesAPI.getEdibleFromItem((Item) (Object) this).ifPresent(edible -> {
            if (edible.mode() == EdibleMode.INTERACT) {
                cir.setReturnValue(72000);
            }
        });
    }

    @Inject(method = "getUseAction", cancellable = true, at = @At("HEAD"))
    public void ended$interactUseAction(ItemStack stack, CallbackInfoReturnable<UseAction> cir) {
        EndrousEdiblesAPI.getEdibleFromItem((Item) (Object) this).ifPresent(edible -> {
            if (edible.mode() == EdibleMode.INTERACT) {
                cir.setReturnValue(UseAction.BOW);
            }
        });
    }

    @Inject(method = "use", cancellable = true, at = @At("HEAD"))
    public void ended$interactUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        EndrousEdiblesAPI.getEdibleFromItem((Item) (Object) this).ifPresent(edible -> {
            if (edible.mode() == EdibleMode.INTERACT) {
                user.setCurrentHand(hand);
                cir.setReturnValue(TypedActionResult.consume(user.getStackInHand(hand)));
            }
        });
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    public void ended$interactTeleport(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (user instanceof ServerPlayerEntity player) {
            EndrousEdiblesAPI.getEdibleFromItem((Item) (Object) this).ifPresent(edible -> {
                if (edible.mode() == EdibleMode.INTERACT) {
                    edible.trigger(player);
                }
            });
        }
    }
}
