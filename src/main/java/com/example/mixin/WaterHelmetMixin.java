package com.example.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class WaterHelmetMixin extends LivingEntity {
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    protected WaterHelmetMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="tick", at=@At("HEAD"))
    private void letMeBreathe(CallbackInfo ci) {
        if (!this.getWorld().isClient() && this.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(Items.GLASS)) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 20, 0, true, false, false));
        }
    }
}
