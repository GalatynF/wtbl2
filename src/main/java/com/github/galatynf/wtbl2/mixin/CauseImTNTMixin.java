package com.github.galatynf.wtbl2.mixin;

import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.iMixin.ICauseImTNTMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TntEntity.class)
public abstract class CauseImTNTMixin extends Entity implements ICauseImTNTMixin {

    @Unique
    private boolean wtbl_isBlackHole = false;

    public CauseImTNTMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void setBlackHole() {
        wtbl_isBlackHole = true;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void imDynamite(CallbackInfo ci) {
        World world = this.getWorld();
        if (!wtbl_isBlackHole || this.age % 5 != 0) {
            return;
        }

        if (!world.isClient()) {
            int adder = this.age / 10;
            Box box;

            box = this.getBoundingBox().expand(adder + 5, adder + 5, adder + 5);

            List<Entity> list = world.getOtherEntities(this, box);

            for (Entity entity : list) {
                if (!entity.isRemoved()
                        && !(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative())) {
                    Tool.dashTo(entity, this, 0.5f);
                }
            }
        }
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void bigBoom(CallbackInfo ci) {
        if (wtbl_isBlackHole) {
            this.getWorld().createExplosion(this, this.getX(), this.getBodyY(0.0625D), this.getZ(), 15.0F, World.ExplosionSourceType.TNT);
        }
        ci.cancel();
    }
}