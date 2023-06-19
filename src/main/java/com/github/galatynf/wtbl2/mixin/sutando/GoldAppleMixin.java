package com.github.galatynf.wtbl2.mixin.sutando;

import com.github.galatynf.wtbl2.cardinal.MyComponents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class GoldAppleMixin extends LivingEntity {
    @Shadow @Final private PlayerAbilities abilities;

    protected GoldAppleMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "eatFood", at=@At("TAIL"))
    private void setStand(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if(!world.isClient()
                && stack.getItem().equals(Items.GOLDEN_APPLE)
                && stack.getName().getString().toLowerCase().contains("ora")) {
            Vec3d rotation = this.getRotationVector().normalize();
            ArmorStandEntity armorStand = new ArmorStandEntity(world, this.getX()-1.5*rotation.x, this.getY()+1, this.getZ()-1.5*rotation.z);
            armorStand.setYaw(this.getYaw());
            armorStand.setNoGravity(true);
            armorStand.setInvulnerable(true);
            armorStand.setShowArms(true);
            armorStand.setHideBasePlate(true);
            //armorStand.setInvisible(true);
            // Armour
            armorStand.equipStack(EquipmentSlot.HEAD, Items.SKELETON_SKULL.getDefaultStack());
            armorStand.equipStack(EquipmentSlot.CHEST, Items.GOLDEN_CHESTPLATE.getDefaultStack());
            // Pose
            armorStand.setLeftArmRotation(new EulerAngle(310, 0, 270));
            armorStand.setRightArmRotation(new EulerAngle(310, 0, 90));
            armorStand.setLeftLegRotation(new EulerAngle(14, 0, 0));
            armorStand.setRightLegRotation(new EulerAngle(14, 0, 0));

            MyComponents.STAND_ATTACK_MANNEQUIN.get(armorStand).setOwner(this.getUuid());
            MyComponents.STAND_ATTACK_MANNEQUIN.get(armorStand).initialiseAttack(200);

            MyComponents.MUSIC_PLAYER.get(armorStand).startSong("6 6 64 6 9 6 14 6 6 64 6 C B 94 ");

            world.spawnEntity(armorStand);
            MyComponents.STAND_ATTACKER.get((PlayerEntity)(Object)this).setStandAttack(armorStand.getId());
        }
    }
}
