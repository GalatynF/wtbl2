package com.example.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class RocketAnimalsMixin extends LivingEntity {
    protected RocketAnimalsMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "collideWithEntity", at=@At("TAIL"))
    private void rocket(Entity entity, CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient() && entity instanceof AnimalEntity && entity.isOnGround()) {
            ItemStack itemStack = Items.FIREWORK_ROCKET.getDefaultStack();
            Vec3d vec3d = this.getPos();
            Direction direction = entity.getMovementDirection();
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(world, entity, vec3d.x, vec3d.y, vec3d.z, itemStack);
            world.spawnEntity(fireworkRocketEntity);

            entity.startRiding(fireworkRocketEntity);
        }
    }
}
