package com.example.mixin;

import com.example.Tool;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static java.lang.Math.random;

@Mixin(Item.class)
public abstract class ParryHotterMixin {
    @Shadow public abstract ItemStack getDefaultStack();

    @Inject(method="use", at=@At("HEAD"), cancellable = true)
    private void dash(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient() && this.getDefaultStack().getItem().equals(Items.BLAZE_ROD) && !user.getItemCooldownManager().isCoolingDown(Items.BLAZE_ROD)) {
            if (!user.isCreative() && (double) 1 / (1.5*(user.experienceLevel+50)) > Math.random()) {
                world.createExplosion(null, user.getX(), user.getY(), user.getZ(), 2, false, World.ExplosionSourceType.MOB);
                return;
            }
            int xpToRemove = -1;
            if(!user.isSneaking() && !user.isOnGround() && user.totalExperience >= 10) {
                Tool.dashTo(user, user.getPos().add(user.getRotationVec(1.0F).normalize()), 3);
                xpToRemove = 10;
            }
            if(user.isSneaking() && user.isOnGround() && user.totalExperience >= 4) {
                Tool.fireArrow(user, false, false, false, null);
                xpToRemove = 4;
            }
            if(user.isSneaking() && !user.isOnGround() && user.totalExperience >= 30) {
                Tool.fireArrow(user, false, true, true, "wtbl2_lightning");
                xpToRemove = 30;
            }
            if(!user.isSneaking() && user.isOnGround() && user.totalExperience >= 10) {
                Tool.fireArrow(user, false, true, true, "wtbl2_accio");
                xpToRemove = 10;
            }
            if (xpToRemove != -1) {
                user.addExperience(-xpToRemove);
                user.getItemCooldownManager().set(Items.BLAZE_ROD, xpToRemove); // xpToRemove can double as cooldown
                cir.setReturnValue(TypedActionResult.success(this.getDefaultStack()));
            }
        }
    }
}
