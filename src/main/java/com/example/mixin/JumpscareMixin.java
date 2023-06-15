package com.example.mixin;

import com.example.cardinal.MyComponents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class JumpscareMixin extends LivingEntity {
    @Shadow public abstract boolean isCreative();

    protected JumpscareMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="tick", at=@At("HEAD"))
    private void boo(CallbackInfo ci) {
        World world = this.getWorld();
        if(world.isClient())
                return;

        // If the mannequin disappeard somehow
        if(world.getEntityById(MyComponents.CURSED.get((PlayerEntity)(Object)this).getMannequinId()) == null)
            MyComponents.CURSED.get((PlayerEntity)(Object)this).setMannequinId(-1);

        if((world.getTime()%6000 == 0 || MyComponents.CURSED.get((PlayerEntity)(Object)this).isMannequinCursed())
                && !this.isCreative()
                && world.getLightLevel(this.getBlockPos()) < 5
                && MyComponents.CURSED.get((PlayerEntity)(Object)this).getMannequinId() == -1) {
            // return if there's a player too close
            for (PlayerEntity p : world.getPlayers()) {
                if (this.distanceTo(p) < 30 && !this.getUuid().equals(p.getUuid())) {
                    return;
                }
            }
            Vec3d rotation = this.getRotationVector().normalize();
            ArmorStandEntity armorStand = new ArmorStandEntity(world, this.getX()-1.5*rotation.x, this.getY(), this.getZ()-1.5*rotation.z);
            armorStand.setYaw(this.getYaw());
            if(MyComponents.CURSED.get((PlayerEntity)(Object)this).isMannequinCursed() || Math.random() < 0.5) {
                armorStand.equipStack(EquipmentSlot.HEAD, Items.PLAYER_HEAD.getDefaultStack());
            }
            else {
                armorStand.equipStack(EquipmentSlot.HEAD, Items.CREEPER_HEAD.getDefaultStack());
            }
            MyComponents.CURSED_MANNEQUIN.get(armorStand).setOwner(this.getUuid());
            world.spawnEntity(armorStand);
            MyComponents.CURSED.get((PlayerEntity)(Object)this).setMannequinId(armorStand.getId());
        }
    }
}
