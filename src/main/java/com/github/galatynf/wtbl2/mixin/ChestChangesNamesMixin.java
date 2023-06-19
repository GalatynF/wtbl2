package com.github.galatynf.wtbl2.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChestBlock.class)
public class ChestChangesNamesMixin {
    private static final List<String> syllables = List.of("po", "ya", "yo", "lo", "ru", "cha", "pa", "pu", "kri", "bo", "bu", "kol", "blu", "fo","hy", "moon", "foo", "car", " ", "-");
    private Text generateRandomName() {
        int length = (int) (Math.random() * 5 + 1);
        String result = "";
        for (int i = 0 ; i < length ; ++i) {
            result = result.concat(syllables.get((int) (Math.random()*syllables.size())));
        }
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return Text.of(result);
    }
    @Inject(method = "onUse", at=@At("HEAD"))
    private void changeInventoryNames(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if(!world.isClient() && !player.isCreative()) {
            PlayerInventory inventory = player.getInventory();
            for (int i = 0 ; i < 27 ; ++i) {
                if(!inventory.getStack(i).isStackable())
                    inventory.getStack(i).setCustomName(generateRandomName());
            }
        }
    }
}
