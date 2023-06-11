package com.example;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

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

    public static void addStatus(LivingEntity entity, StatusEffect statusEffect, int duration, int amplifier, boolean showParticles, boolean showIcon) {
        entity.addStatusEffect(new StatusEffectInstance(statusEffect, duration, amplifier, true, showParticles, showIcon));
    }

    public static void print(Object msg) {
        System.out.println(msg);
    }
}