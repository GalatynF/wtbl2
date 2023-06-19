package com.github.galatynf.wtbl2.mixin.worldevents;

import com.github.galatynf.wtbl2.iMixin.IFireworkMixin;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FireworkRocketEntity.class)
public class LongerFireworkMixin implements IFireworkMixin {

    @Shadow private int lifeTime;

    @Override
    public void setLifetime(int time) {
        this.lifeTime = time;
    }
}
