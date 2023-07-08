package com.github.galatynf.wtbl2.mixin.entity;

import com.github.galatynf.wtbl2.MusicPlayer;
import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.iMixin.ISongMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class SuspiciousStewMixin extends LivingEntity implements ISongMixin {
    protected SuspiciousStewMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private MusicPlayer wtbl2_musicPlayer = null;

    @Inject(method="eatFood", at=@At("HEAD"))
    private void setAmogus(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if(!world.isClient()) {
            if (stack.getItem().equals(Items.SUSPICIOUS_STEW) || stack.getName().toString().toLowerCase().contains("sus")) {
                this.wtbl2_musicPlayer = MusicPlayer.AMOGUS_DRIP;
            }
        }
    }

    @Override
    public void setSong(MusicPlayer mPlayer) {
        this.wtbl2_musicPlayer = mPlayer;
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void playSong(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient()) {
            if (this.wtbl2_musicPlayer != null && world.getTime()%3 == 0) {
                this.wtbl2_musicPlayer.playNextNotes(world, (Entity) (Object) this, true);
            }
            if(this.getY() > 100) {
                this.wtbl2_musicPlayer = null;
            }
        }
    }
}
