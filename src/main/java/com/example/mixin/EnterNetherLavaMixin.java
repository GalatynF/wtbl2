package com.example.mixin;

import com.example.Tool;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class EnterNetherLavaMixin extends LivingEntity {
    protected EnterNetherLavaMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="tick", at=@At("HEAD"))
    private void enterTheNether(CallbackInfo ci) {
        World world = this.getWorld();
        if (!world.isClient()
                && world.getBlockState(this.getBlockPos()).equals(Blocks.LAVA.getDefaultState())
                && world.getBlockState(this.getBlockPos().add(0, 1, 0)).equals(Blocks.LAVA.getDefaultState())
                && world.getBlockState(this.getBlockPos().add(0, -1, 0)).equals(Blocks.CRYING_OBSIDIAN.getDefaultState())) {
            if (this.canUsePortals()) {
                this.setInNetherPortal(this.getBlockPos());
            }
        }
    }
}
