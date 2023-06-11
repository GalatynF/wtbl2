package com.example;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Tool {
    public static boolean isPlayerStaring(LivingEntity target, PlayerEntity player) {
        Vec3d vec3d = player.getRotationVec(1.0F).normalize();
        Vec3d vec3d2 = new Vec3d(target.getX() - player.getX(), target.getEyeY() - player.getEyeY(), target.getZ() - player.getZ());
        double d = vec3d2.length();
        vec3d2 = vec3d2.normalize();
        double e = vec3d.dotProduct(vec3d2);
        return e > 1.0D - 0.025D / d && player.canSee(target);
    }

    public static boolean isPlayerLookingAt(LivingEntity target, PlayerEntity player) {
        Vec3d vec3d = player.getRotationVec(1.0F).normalize();
        for (float i = 0; i < 3; i = i + 0.5F) {
            Vec3d vec3d2 = new Vec3d(target.getX() - player.getX(), target.getEyeY() - player.getEyeY() - i, target.getZ() - player.getZ());
            double d = vec3d2.length();
            vec3d2 = vec3d2.normalize();
            double e = vec3d.dotProduct(vec3d2);
            if (e > 1.0D - 0.025D / d && player.canSee(target)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLight(LivingEntity entity) {
        return (entity.getMainHandStack().getItem().equals(Items.TORCH))
                || entity.getMainHandStack().getItem().equals(Items.LANTERN)
                || entity.getOffHandStack().getItem().equals(Items.TORCH)
                || entity.getOffHandStack().getItem().equals(Items.LANTERN);
    }

    public static void dashTo(Entity attacker, Entity target, float force) {
        double x = (force * (target.getX() - attacker.getX()));
        double y = (force * (target.getY() - attacker.getY()));
        double z = (force * (target.getZ() - attacker.getZ()));
        attacker.setVelocity(x, y, z);
        attacker.velocityModified = true;
    }

    public static void dashTo(Entity attacker, Vec3d target, float force) {
        double x = (force * (target.getX() - attacker.getX()));
        double y = (force * (target.getY() - attacker.getY()));
        double z = (force * (target.getZ() - attacker.getZ()));
        attacker.setVelocity(x, y, z);
        attacker.velocityModified = true;
    }

    public static void addStatus(LivingEntity entity, StatusEffect statusEffect, int duration, int amplifier, boolean showParticles, boolean showIcon) {
        entity.addStatusEffect(new StatusEffectInstance(statusEffect, duration, amplifier, true, showParticles, showIcon));
    }

    public static void fireArrow(PlayerEntity user, boolean canPickup, boolean invisible, boolean noGravity, @Nullable String customName) {
        World world = user.getWorld();
        if (!world.isClient()) {
            ArrowItem arrowItem = (ArrowItem)Items.ARROW;
            ItemStack itemStack = arrowItem.getDefaultStack();
            PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, itemStack, user);
            persistentProjectileEntity.setInvisible(invisible);
            persistentProjectileEntity.setNoGravity(noGravity);
            if(customName != null)
                persistentProjectileEntity.setCustomName(Text.of(customName));
            persistentProjectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 3.0f, 1.0f);
            if (!canPickup) {
                persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
            world.spawnEntity(persistentProjectileEntity);
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + 0.5f);
    }

    public static void print(Object msg) {
        System.out.println(msg);
    }

    public static BlockPos toBlockPos(Vec3d vec) {
        return new BlockPos((int)vec.x, (int)vec.y, (int)vec.z);
    }
}