package com.example.mixin;

import com.example.Tool;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class WaterPressureMixin extends LivingEntity {
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract boolean isCreative();

    protected WaterPressureMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    private int getNumberOfWaterUp(World world) {
        int result = 0;
        int y = 0;
        BlockState water = Blocks.WATER.getDefaultState();
        BlockState air = Blocks.AIR.getDefaultState();
        do {
            if(world.getBlockState(new BlockPos(this.getBlockPos().add(0, y, 0))).equals(water)) {
                result++;
            }
            y++;
        } while(!world.getBlockState(new BlockPos(this.getBlockPos().add(0, y, 0))).equals(air));
        return result;
    }

    @Inject(method="tick", at=@At("HEAD"))
    private void underPressure(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient() && this.isSubmergedInWater() && !this.isCreative()) {
            int depth = getNumberOfWaterUp(world);
            if (depth >= 10) {
                WardenEntity.addDarknessToClosePlayers((ServerWorld) world, this.getPos(), (Entity) this, 1);
            }
            if (depth >= 20) {
                Tool.addStatus((LivingEntity) this, StatusEffects.BLINDNESS, 150, 0, false, false);
            }
            if(depth >= 30) {
                this.damage(world.getDamageSources().drown(), 1.0F);
            }
        }
    }
}
