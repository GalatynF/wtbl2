package com.example.mixin;

import com.example.Tool;
import com.example.cardinal.MyComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class ArmorStandMixin2 extends Entity {
    public ArmorStandMixin2(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method="remove", at=@At("HEAD"))
    private void notifyOwnerOfArmorStand(RemovalReason reason, CallbackInfo ci) {
        World world = this.getWorld();
        Tool.print("EZZZZ");
        UUID owner = MyComponents.CURSED_MANNEQUIN.get((ArmorStandEntity)(Object)this).getOwner();
        Tool.print("EZZZZRRRRRRRRRRR");
        if (!world.isClient
                && (LivingEntity)(Object)this instanceof ArmorStandEntity
                && owner != null) {
            MyComponents.CURSED.get(world.getPlayerByUuid(owner)).setHasMannequin(false);
        }
    }
}
