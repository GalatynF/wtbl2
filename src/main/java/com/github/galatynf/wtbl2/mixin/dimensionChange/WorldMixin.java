package com.github.galatynf.wtbl2.mixin.dimensionChange;

import com.github.galatynf.wtbl2.iMixin.IServerWorldGiantMixin;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public class WorldMixin implements IServerWorldGiantMixin {
    @Unique
    private boolean wtbl2_hasSpawnedGiant = false;

    @Override
    public void setHasSpawnedGiant(boolean hasSpawned) {
        this.wtbl2_hasSpawnedGiant = hasSpawned;
    }

    @Override
    public boolean hasSpawnedGiant() {
        return this.wtbl2_hasSpawnedGiant;
    }
}
