package com.example.iMixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAnvilMixin {
    void turnToAnvilAndUpdate(World world, BlockPos pos, boolean turnTo, boolean checkIfLeaf);
}
