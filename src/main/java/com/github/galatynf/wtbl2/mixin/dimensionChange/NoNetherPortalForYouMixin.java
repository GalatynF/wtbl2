package com.github.galatynf.wtbl2.mixin.dimensionChange;

import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.cardinal.MyComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public abstract class NoNetherPortalForYouMixin extends Block {
    public NoNetherPortalForYouMixin(Settings settings) {
        super(settings);
    }

    private void spawnGiantBoss(World world, BlockPos pos) {
        GiantEntity giant = new GiantEntity(EntityType.GIANT, world);
        giant.setPosition(pos.toCenterPos());
        giant.setPersistent();
        world.spawnEntity(giant);
        Tool.addStatus(giant, StatusEffects.ABSORPTION, 99999, 64, true, false);
    }

    @Inject(method="onEntityCollision", at=@At("INVOKE"), cancellable = true)
    private void cantHaveShitInOverworld(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!world.isClient() && entity.isPlayer() && world.getRegistryKey() == World.OVERWORLD) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            WardenEntity.addDarknessToClosePlayers((ServerWorld)world, Vec3d.ofCenter(pos), null, 40);
            //LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, SpawnReason.TRIGGERED, (ServerWorld)world, pos, 20, 5, 6, LargeEntitySpawnHelper.Requirements.WARDEN);
            MyComponents.CURSED.get(entity).setMannequinCursed(true);
            spawnGiantBoss(world, pos);
            ci.cancel();
        }
    }

}
