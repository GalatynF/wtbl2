package com.github.galatynf.wtbl2.mixin.sutando;

import com.github.galatynf.wtbl2.MusicPlayer;
import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.cardinal.MyComponents;
import com.github.galatynf.wtbl2.iMixin.ISongMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
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
                && (stack.getName().getString().toLowerCase().contains("ora") || stack.getName().getString().toLowerCase().contains("muda"))) {
            if(MyComponents.STAND_ATTACKER.get((Entity)(Object)this).getStandId() != -1) {
                if (world.getEntityById(MyComponents.STAND_ATTACKER.get((Entity)(Object)this).getStandId()) != null) {
                    MyComponents.STAND_ATTACK_MANNEQUIN.get(world.getEntityById(MyComponents.STAND_ATTACKER.get((Entity) (Object) this).getStandId())).addRemainingDuration(200);
                    return;
                }
            }
            Vec3d rotation = this.getRotationVector().normalize();
            ArmorStandEntity armorStand = new ArmorStandEntity(world, this.getX()-1.5*rotation.x, this.getY()+1, this.getZ()-1.5*rotation.z);
            armorStand.setYaw(this.getYaw());
            armorStand.setNoGravity(true);
            armorStand.setInvulnerable(true);
            armorStand.setShowArms(true);
            armorStand.setHideBasePlate(true);
            //armorStand.setInvisible(true);
            // Armour
            if (stack.getName().getString().toLowerCase().contains("ora")) {
                armorStand.equipStack(EquipmentSlot.HEAD, Items.IRON_HELMET.getDefaultStack());
                armorStand.equipStack(EquipmentSlot.CHEST, Items.IRON_CHESTPLATE.getDefaultStack());
                ((ISongMixin)armorStand).setSong(MusicPlayer.STARDUST_CRUSADERS);
            }
            else {
                armorStand.equipStack(EquipmentSlot.HEAD, Items.GOLDEN_APPLE.getDefaultStack());
                armorStand.equipStack(EquipmentSlot.CHEST, Items.GOLDEN_CHESTPLATE.getDefaultStack());
                ((ISongMixin)armorStand).setSong(MusicPlayer.IL_VENTO_DORO);
            }
            // Pose
            armorStand.setLeftArmRotation(new EulerAngle(310, 0, 270));
            armorStand.setRightArmRotation(new EulerAngle(310, 0, 90));
            armorStand.setLeftLegRotation(new EulerAngle(14, 0, 0));
            armorStand.setRightLegRotation(new EulerAngle(14, 0, 0));

            MyComponents.STAND_ATTACK_MANNEQUIN.get(armorStand).setOwner(this.getUuid(), this.getId(), true);
            MyComponents.STAND_ATTACK_MANNEQUIN.get(armorStand).initialiseAttack(200);

            world.spawnEntity(armorStand);
            MyComponents.STAND_ATTACKER.get((LivingEntity)(Object)this).setStandAttack(armorStand.getId());
        }
    }
}
