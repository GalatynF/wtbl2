package com.example.mixin;

import com.example.Tool;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public abstract class NoEndPortalForYouMixin extends BlockWithEntity {

    protected NoEndPortalForYouMixin(Settings settings) {
        super(settings);
    }

    private void createArena(World world, BlockPos centre) {
        int groundRadius = 30;
        BlockState obsidian = Blocks.OBSIDIAN.getDefaultState();
        BlockState air = Blocks.AIR.getDefaultState();
        BlockState copper = Blocks.OXIDIZED_CUT_COPPER.getDefaultState();
        //Ground
        Tool.fillCube(world, centre.add(-groundRadius, -3, -groundRadius), centre.add(groundRadius, 100, groundRadius), air);
        Tool.fillCube(world, centre.add(-groundRadius, -3, -groundRadius), centre.add(groundRadius, -2, groundRadius), obsidian);
        // Random pillars
        for (int i = -groundRadius ; i < groundRadius ; ++i) {
            for (int j = -groundRadius ; j < groundRadius ; ++j) {
                if (Math.random() < 0.03) {
                    Tool.print("THERE");
                    Tool.fillCube(world,
                                    new BlockPos((int) (i + Math.random()*3), -3, (int) (j + Math.random()*3)).add(centre.getX(), centre.getY(), centre.getZ()),
                                    new BlockPos(i, (int) (Math.random()*10-1), j).add(centre.getX(), centre.getY(), centre.getZ()),
                                    copper);
                }
            }
        }

    }

    @Inject(method="onEntityCollision", at=@At("INVOKE"), cancellable = true)
    private void cantHaveShitInOverworld(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!world.isClient() && entity.isPlayer()) {
            world.playSound((PlayerEntity) entity, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS);
            // Don't call this more than once lol
            Tool.fillCube(world, pos.add(-4, -1, -4), pos.add(4, 1, 4), Blocks.AIR.getDefaultState());
            createArena(world, pos);
            //Tool.summonWither(world, pos);
            ci.cancel();
        }
    }
}
