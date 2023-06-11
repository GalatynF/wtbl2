package com.example.mixin;

import com.example.Tool;
import com.example.iMixin.IEntityValuesMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class AccioedMixin extends Entity implements IEntityValuesMixin {
    private int accioCooldown = 0;

    public AccioedMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean canBeAccioed() {
        return accioCooldown == 0;
    }

    @Override
    public void setAccioed(int newCooldown) {
        this.accioCooldown = newCooldown;
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void manageValues(CallbackInfo ci) {
        if(accioCooldown > 0) {
            accioCooldown -= 1;
        }
    }

    @Inject(method="damage", at=@At("INVOKE"), cancellable = true)
    private void specialArrowsAreNothing(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.getWorld().isClient()
                && source.isOf(DamageTypes.ARROW)
                && accioCooldown > 0) {
            cir.setReturnValue(false);
        }
    }
}
