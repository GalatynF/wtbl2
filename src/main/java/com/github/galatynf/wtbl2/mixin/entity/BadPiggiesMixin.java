package com.github.galatynf.wtbl2.mixin.entity;

import com.github.galatynf.wtbl2.MusicPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class BadPiggiesMixin extends Entity {
    public BadPiggiesMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    MusicPlayer wtbl2_musicPlayer_BP = MusicPlayer.BAD_PIGGIES;

    @Inject(method="tick", at=@At("HEAD"))
    private void playTheme(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient() && world.getTime()%2 == 0 && this.getVehicle() != null) {
            Entity vehicle = this.getVehicle();
            if(vehicle.getType().equals(EntityType.BOAT) || vehicle.getType().equals(EntityType.MINECART)) {
                this.wtbl2_musicPlayer_BP.playNextNotes(world, (Entity)this, true);
            }
        }
    }
}
