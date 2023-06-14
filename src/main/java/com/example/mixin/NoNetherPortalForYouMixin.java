package com.example.mixin;

import com.example.cardinal.MyComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.SpawnReason;
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

    @Inject(method="onEntityCollision", at=@At("INVOKE"), cancellable = true)
    private void cantHaveShitInOverworld(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!world.isClient() && entity.isPlayer()) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            WardenEntity.addDarknessToClosePlayers((ServerWorld)world, Vec3d.ofCenter(pos), null, 40);
            LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, SpawnReason.TRIGGERED, (ServerWorld)world, pos, 20, 5, 6, LargeEntitySpawnHelper.Requirements.WARDEN);
            MyComponents.CURSED.get(entity).setMannequinCursed(true);
            ci.cancel();
        }
    }

}
