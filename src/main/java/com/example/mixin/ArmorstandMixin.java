package com.example.mixin;

import com.example.Tool;
import com.example.cardinal.MyComponents;
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

@Mixin(ArmorStandEntity.class)
public abstract class ArmorstandMixin extends LivingEntity {
    protected ArmorstandMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void disappear(CallbackInfo ci) {
        if(MyComponents.CURSED_MANNEQUIN.get((ArmorStandEntity)(Object)this).getOwner() == null)
            return;
        World world = this.getWorld();
        PlayerEntity owner = world.getPlayerByUuid(MyComponents.CURSED_MANNEQUIN.get((ArmorStandEntity)(Object)this).getOwner());
        if(!world.isClient() && owner!=null) {
            boolean remove = false;
            for (PlayerEntity p : world.getPlayers()) {
                if(owner != p && this.distanceTo(p) < 10) {
                    remove = true;
                }
            }
            if(this.distanceTo(owner) > 5) {
                remove = true;
            }
            if (Tool.isPlayerLookingAt((LivingEntity) this, owner)) {
                owner.playSound(SoundEvents.AMBIENT_CAVE.value(), SoundCategory.AMBIENT, 5f, 1f);
                remove = true;
            }
            if(remove) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }
}
