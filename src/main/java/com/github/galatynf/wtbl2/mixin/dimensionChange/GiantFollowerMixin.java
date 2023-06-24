package com.github.galatynf.wtbl2.mixin.dimensionChange;

import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.iMixin.IGiantFollowerMixin;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class GiantFollowerMixin extends Entity implements IGiantFollowerMixin {
    @Unique
    private int wtbl2_giantId = -1;

    @Unique
    private int wtbl2_idealDistance = 10;

    public GiantFollowerMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @Override
    public void setGiantId(int id) {
        this.wtbl2_giantId = id;
    }

    @Override
    public int getGiantId() {
        return this.wtbl2_giantId;
    }

    @Override
    public void setIdealDistance(int dist) {
        this.wtbl2_idealDistance = dist;
    }

    @Inject(method = "tick", at=@At("HEAD"), cancellable = true)
    private void moveToGiant(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient() && this.wtbl2_giantId != -1) {
            GiantEntity giant = (GiantEntity) world.getEntityById(this.wtbl2_giantId);
            if (giant != null) {
                if (this.distanceTo(giant) > this.wtbl2_idealDistance) {
                    Tool.moveTo((LivingEntity) (Object) this, giant.getBlockPos());
                    ci.cancel();
                }
                this.setYaw(giant.getYaw());
            }
            else {
                this.wtbl2_giantId = -1;
            }
            this.destroyBlocks(this.getBoundingBox().expand(1,0,1));
        }
    }

    private boolean destroyBlocks(Box box) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.floor(box.minY);
        int k = MathHelper.floor(box.minZ);
        int l = MathHelper.floor(box.maxX);
        int m = MathHelper.floor(box.maxY);
        int n = MathHelper.floor(box.maxZ);
        boolean slowedDownByBlock = false;
        boolean bl2 = false;

        for(int o = i; o <= l; ++o) {
            for(int p = j; p <= m; ++p) {
                for(int q = k; q <= n; ++q) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = this.world.getBlockState(blockPos);
                    if (!blockState.isAir()) {
                        if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                                && this.getY() < p) {
                            bl2 = this.world.breakBlock(blockPos, false, (Entity)(Object)this) || bl2;

                        } else {
                            slowedDownByBlock = true;
                        }
                    }
                }
            }
        }

        if (bl2) {
            BlockPos blockPos2 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
            this.world.syncWorldEvent(2008, blockPos2, 0);
        }

        return slowedDownByBlock;
    }
}
