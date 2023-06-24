package com.github.galatynf.wtbl2.mixin.dimensionChange;

import com.github.galatynf.wtbl2.Tool;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static java.util.Map.entry;

@Mixin(LivingEntity.class)
public abstract class GiantMixin extends Entity {

    @Shadow public abstract float getYaw(float tickDelta);

    @Shadow public abstract boolean isDead();

    @Shadow public abstract float getHealth();

    public GiantMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private int wtbl2_blocksTraveled = 0;
    @Unique
    private char wtbl2_direction = 'n';

    @Inject(method = "tick", at=@At("HEAD"))
    private void move(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient() && this.getType().equals(EntityType.GIANT) && !this.isDead()) {

            float force = 0.3F;
            switch (wtbl2_direction) {
                case('n') -> {
                    this.move(MovementType.SELF, new Vec3d(0, 0, force));
                }
                case ('s') -> {
                    this.move(MovementType.SELF, new Vec3d(0, 0, -force));
                }
                case('o') -> {
                    this.move(MovementType.SELF, new Vec3d(-force, 0, 0));
                }
                case ('e') -> {
                    this.move(MovementType.SELF, new Vec3d(force, 0, 0));
                }
            }
            this.wtbl2_blocksTraveled++;
            if(this.wtbl2_blocksTraveled >= 200) {
                this.wtbl2_direction = this.wtbl2_nextDir.get(this.wtbl2_direction);
                this.setRotation(this.getYaw()-90, this.getPitch());
                this.wtbl2_blocksTraveled = 0;
            }

            this.giantDestroyBlocks(this.getBoundingBox().expand(2,0,2));
        }
    }

    @Inject(method = "onDeath", at=@At("TAIL"))
    private void drop(DamageSource damageSource, CallbackInfo ci) {
        ItemEntity item = new ItemEntity(EntityType.ITEM, getWorld());
        item.setStack(Items.ORANGE_STAINED_GLASS.getDefaultStack());
        item.setPosition(this.getPos());
        item.setNeverDespawn();
        item.setInvulnerable(true);
        world.spawnEntity(item);
    }

    private boolean giantDestroyBlocks(Box box) {
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

    @Inject(method = "getStepHeight", at=@At("HEAD"), cancellable = true)
    private void returnOne(CallbackInfoReturnable<Float> cir) {
        World world = this.getWorld();
        if (!world.isClient() && this.getType().equals(EntityType.GIANT)) {
            float step = super.getStepHeight();
            cir.setReturnValue(Math.max(step, 1.5f));
        }
    }


    private final Map<Character, Character> wtbl2_nextDir = Map.ofEntries(entry('n', 'e'), entry('e', 's'), entry('s', 'o'), entry('o', 'n'));
}
