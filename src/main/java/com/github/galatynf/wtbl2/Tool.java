package com.github.galatynf.wtbl2;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

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
        if(player == null) {
            return false;
        }
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

    public static void sendGlobalMessage(ServerWorld world, String content) {
        sendGlobalMessage(world, content, "white");
    }

    public static void sendGlobalMessage(ServerWorld world, String content, String colour) {
        List<ServerPlayerEntity> players = world.getPlayers();
        MutableText mess = Text.of(content).copy();
        Style style = Style.EMPTY.withColor(TextColor.parse(colour)).withBold(true);

        for (PlayerEntity p : players) {
            p.sendMessage(mess.setStyle(style), true);
        }
    }

    public static List<PlayerEntity> getPlayersNotCreative(World world) {
        List<PlayerEntity> result = new java.util.ArrayList<>(List.of());
        for (PlayerEntity p : world.getPlayers()) {
            if(!p.isCreative()) {
                result.add(p);
            }
        }
        return result;
    }

    public static void moveTo(Entity mover, BlockPos target) {
        Vec3d vector = new Vec3d(target.getX()-mover.getX(), 0, target.getZ()-mover.getZ()).normalize();
        mover.move(MovementType.SELF, vector);
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

    public static Vec3d toVec3d (BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void fillCube(World world, BlockPos corner1, BlockPos corner2, BlockState block) {
        int minX = Math.min(corner1.getX(), corner2.getX());
        int maxX = Math.max(corner1.getX(), corner2.getX());
        int minZ = Math.min(corner1.getZ(), corner2.getZ());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());
        int minY = Math.min(corner1.getY(), corner2.getY());
        int maxY = Math.max(corner1.getY(), corner2.getY());
        for (int i = minX ; i < maxX ; ++i) {
            for(int j = minZ ; j < maxZ ; ++j) {
                for(int k = minY ; k < maxY ; ++k) {
                    world.setBlockState(new BlockPos(i, k, j), block);
                }
            }
        }
    }

    public static void summonWither(World world, BlockPos pos, @Nullable String customName) {
        WitherEntity witherEntity = EntityType.WITHER.create(world);
        if (witherEntity != null) {
            if(customName != null)
                witherEntity.setCustomName(Text.of(customName));
            witherEntity.refreshPositionAndAngles((double) pos.getX() + 0.5, (double) pos.getY() + 0.55, (double) pos.getZ() + 0.5, 0.0f, 0.0f);
            witherEntity.onSummoned();
            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, witherEntity.getBoundingBox().expand(50.0))) {
                Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, witherEntity);
            }
            world.spawnEntity(witherEntity);
        }
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz, World world){
        int x = world.random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}