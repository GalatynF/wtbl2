package com.github.galatynf.wtbl2.mixin.worldevents;

import com.github.galatynf.wtbl2.enums.Wtbl2OverworldEvents;
import com.github.galatynf.wtbl2.iMixin.IServerWorldMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ArrowRainMixin extends LivingEntity {
    @Shadow public abstract boolean isCreative();

    protected ArrowRainMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private int wtbl2_arrowCooldown = 0;

    @Inject(method = "tick", at=@At("HEAD"))
    private void spawnArrow(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient()
            && ((IServerWorldMixin)world).getCurrentEvent().equals(Wtbl2OverworldEvents.ARROW_RAIN)
            && world.getTime()%2==0
            && !this.isCreative()
            && this.wtbl2_arrowCooldown < 0) {
            ArrowEntity arrow = new ArrowEntity(world, this.getX(), this.getY()+4, this.getZ());
            arrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
            arrow.setPitch(0);
            world.spawnEntity(arrow);

            if(this.wtbl2_arrowCooldown == -300) {
                this.wtbl2_arrowCooldown = 1200;
            }
        }
        this.wtbl2_arrowCooldown -= this.wtbl2_arrowCooldown > -300 ? 1 : 0;
    }
}
