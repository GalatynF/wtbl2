package com.github.galatynf.wtbl2.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderPearlItem.class)
public class MultishotPearlMixin {
    @Inject(method = "use", at = @At("TAIL"))
    private void multishot(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            for (int i = -5; i < 5; ++i) {
                int randhomme = (int) (Math.random() * 11) - 5;
                EnderPearlEntity enderPearlEntity = new EnderPearlEntity(world, user);
                enderPearlEntity.setItem(itemStack);
                enderPearlEntity.setVelocity(user, user.getPitch(), user.getYaw() + randhomme, 0.0f, (float) (1.5 + i / 10F), (float) (1.0 + i / 10F));
                world.spawnEntity(enderPearlEntity);
            }
        }
    }
}