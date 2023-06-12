package com.example.mixin;

import com.example.Tool;
import com.example.iMixin.IArmorstandMixin;
import com.example.iMixin.IPlayerMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ArmorStandMixin2 extends Entity {
    public ArmorStandMixin2(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method="remove", at=@At("HEAD"))
    private void notifyOwnerOfArmorStand(RemovalReason reason, CallbackInfo ci) {
        World world = this.getWorld();
        if (!world.isClient && (LivingEntity)(Object)this instanceof ArmorStandEntity && ((IArmorstandMixin)this).getOwner()!=null) {
            ((IPlayerMixin)world.getPlayerByUuid(((IArmorstandMixin)this).getOwner())).setHasMannequin(false);
        }
    }
}
