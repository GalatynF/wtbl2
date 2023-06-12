package com.example.mixin;

import com.example.Tool;
import com.example.iMixin.IArmorstandMixin;
import com.example.iMixin.IPlayerMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorstandMixin extends LivingEntity implements IArmorstandMixin {
    protected ArmorstandMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    private UUID playerOwner = null;

    public void setOwner(UUID owner) {
        this.playerOwner = owner;
    }

    public UUID getOwner() {
        return this.playerOwner;
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void disappear(CallbackInfo ci) {
        if(this.playerOwner == null)
            return;
        World world = this.getWorld();
        PlayerEntity owner = world.getPlayerByUuid(this.playerOwner);
        if(!world.isClient() && owner!=null) {
            boolean remove = false;
            for (PlayerEntity p : world.getPlayers()) {
                if(owner != p && this.distanceTo(p) < 10) {
                    remove = true;
                }
            }
            if((this.distanceTo(owner) > 5)
                        || Tool.isPlayerLookingAt((LivingEntity) this, owner)) {
                owner.playSound(SoundEvents.AMBIENT_CAVE.value(), SoundCategory.AMBIENT, 5f, 1f);
                remove = true;
            }
            if(remove) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }
}
