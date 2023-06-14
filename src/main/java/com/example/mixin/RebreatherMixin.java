package com.example.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class RebreatherMixin {
    @Shadow public abstract ItemStack getDefaultStack();

    @Inject(method="use", at=@At("HEAD"), cancellable = true)
    private void putHelmetOn(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient()
                && this.getDefaultStack().getItem().equals(Items.GLASS)
                && user.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            ItemStack itemStack = user.getStackInHand(hand);
            ItemStack itemStack2 = user.getEquippedStack(EquipmentSlot.HEAD);
            user.equipStack(EquipmentSlot.HEAD, itemStack.getItem().getDefaultStack());
            user.getMainHandStack().decrement(1);
            cir.setReturnValue(TypedActionResult.success(itemStack2.isEmpty() ? itemStack : itemStack2.copyAndEmpty()));
        }
    }
}
