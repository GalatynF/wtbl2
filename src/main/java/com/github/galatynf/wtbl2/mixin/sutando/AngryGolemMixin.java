package com.github.galatynf.wtbl2.mixin.sutando;

import com.github.galatynf.wtbl2.MusicPlayer;
import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.cardinal.MyComponents;
import com.github.galatynf.wtbl2.iMixin.ISongMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolemEntity.class)
public abstract class AngryGolemMixin extends GolemEntity {

    protected AngryGolemMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "damage", at=@At("HEAD"))
    private void invokeStand(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        World world = this.getWorld();
        if(!world.isClient() && source.getAttacker()!=null && source.getAttacker().getType().equals(EntityType.PLAYER)) {
            Tool.print("HEUJSFD");
            Vec3d rotation = this.getRotationVector().normalize();
            ArmorStandEntity armorStand = new ArmorStandEntity(world, this.getX()-1.5*rotation.x, this.getY()+1, this.getZ()-1.5*rotation.z);
            armorStand.setYaw(this.getYaw());
            armorStand.setNoGravity(true);
            armorStand.setInvulnerable(true);
            armorStand.setShowArms(true);
            armorStand.setHideBasePlate(true);
            //armorStand.setInvisible(true);
            // Armour
            armorStand.equipStack(EquipmentSlot.HEAD, Items.IRON_HELMET.getDefaultStack());
            armorStand.equipStack(EquipmentSlot.CHEST, Items.IRON_CHESTPLATE.getDefaultStack());
            ((ISongMixin)armorStand).setSong(MusicPlayer.STARDUST_CRUSADERS);
            armorStand.setLeftArmRotation(new EulerAngle(310, 0, 270));
            armorStand.setRightArmRotation(new EulerAngle(310, 0, 90));
            armorStand.setLeftLegRotation(new EulerAngle(14, 0, 0));
            armorStand.setRightLegRotation(new EulerAngle(14, 0, 0));

            MyComponents.STAND_ATTACK_MANNEQUIN.get(armorStand).setOwner(this.getUuid(), this.getId(), false);
            MyComponents.STAND_ATTACK_MANNEQUIN.get(armorStand).initialiseAttack(200);


            world.spawnEntity(armorStand);
            MyComponents.STAND_ATTACKER.get((LivingEntity)(Object)this).setStandAttack(armorStand.getId());
        }
    }
}
