package com.example.mixin;

import com.example.Tool;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonEntity.class)
public abstract class DragonVexSpawnMixin extends MobEntity {

    protected DragonVexSpawnMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at=@At("HEAD"))
    private void spawnVex(CallbackInfo ci) {
        World world = this.getWorld();
        Vec3d pos = this.getPos();
        BlockPos bPos = this.getBlockPos().add(0, -30, 0);
        PlayerEntity p = world.getClosestPlayer((EnderDragonEntity)(Object)this, 200);
        if(!world.isClient() && world.getTime()%100 == 0) {
            ServerWorld serverWorld = (ServerWorld)this.getWorld();
            VexEntity vexEntity = EntityType.VEX.create(world);
            if (vexEntity == null) return;
            vexEntity.refreshPositionAndAngles(bPos, 0.0f, 0.0f);
            vexEntity.initialize(serverWorld, world.getLocalDifficulty(bPos), SpawnReason.MOB_SUMMONED, null, null);
            vexEntity.setOwner((EnderDragonEntity)(Object)this);
            vexEntity.setBounds(p == null ? bPos : p.getBlockPos());
            vexEntity.setLifeTicks(400);

            vexEntity.setStackInHand(Hand.MAIN_HAND, Items.TNT.getDefaultStack());
            vexEntity.setStackInHand(Hand.OFF_HAND, Items.TNT.getDefaultStack());

            serverWorld.spawnEntityAndPassengers(vexEntity);
        }
    }
}
