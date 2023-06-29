package com.github.galatynf.wtbl2.mixin.dimensionChange;

import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.cardinal.MyComponents;
import com.github.galatynf.wtbl2.iMixin.IGiantFollowerMixin;
import com.github.galatynf.wtbl2.iMixin.IServerWorldGiantMixin;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(NetherPortalBlock.class)
public abstract class NoNetherPortalForYouMixin extends Block {

    private static final List<String> horseNames = List.of("Lightning", "Thunder", "Horsy McHorseFace", "Tornado", "Hurricane", "Cyclone", "Kamikaze", "Jetstream", "Gust");
    public NoNetherPortalForYouMixin(Settings settings) {
        super(settings);
    }

    private int spawnGiantBoss(World world, BlockPos pos) {
        GiantEntity giant = new GiantEntity(EntityType.GIANT, world);
        giant.setPosition(pos.toCenterPos());
        giant.setPersistent();
        world.spawnEntity(giant);
        Tool.addStatus(giant, StatusEffects.ABSORPTION, 99999, 40, true, false);
        Tool.addStatus(giant, StatusEffects.GLOWING, 99999, 0, false, false);

        return giant.getId();
    }

    private void spawnRavagerMinion(World world, BlockPos pos, int giantId, String rider, int idealdistance) {
        RavagerEntity ravager = new RavagerEntity(EntityType.RAVAGER, world);
        ravager.setPosition(pos.toCenterPos());
        ((IGiantFollowerMixin) ravager).setGiantId(giantId);
        ((IGiantFollowerMixin) ravager).setIdealDistance(idealdistance);
        Tool.addStatus(ravager, StatusEffects.SLOWNESS, 99999, 0, false, false);
        Tool.addStatus(ravager, StatusEffects.RESISTANCE, 99999, 4, false, false);
        //ravager.setAiDisabled(true);
        world.spawnEntity(ravager);

        if (rider.equals("pillager")) {
            spawnPillager(world, ravager);
        }
        else if (rider.equals("evoker")){
            spawnEvoker(world, ravager);
        }

    }

    private void spawnPillager(World world, LivingEntity mount) {
        PillagerEntity pillager = new PillagerEntity(EntityType.PILLAGER, world);
        pillager.setStackInHand(Hand.MAIN_HAND, Items.CROSSBOW.getDefaultStack());
        pillager.setPersistent();
        pillager.startRiding(mount, true);
        Tool.addStatus(pillager, StatusEffects.RESISTANCE, 99999, 2, false, false);
        world.spawnEntity(pillager);
    }

    private void spawnEvoker(World world, LivingEntity mount) {
        EvokerEntity evoker = new EvokerEntity(EntityType.EVOKER, world);
        evoker.setPersistent();
        evoker.startRiding(mount, true);
        world.spawnEntity(evoker);
    }

    private void spawnBlaze(World world, BlockPos pos, int giantId) {
        BlazeEntity blaze = new BlazeEntity(EntityType.BLAZE, world);
        blaze.setPos(pos.getX() + 5,pos.getY(), pos.getZ());
        ((IGiantFollowerMixin)blaze).setGiantId(giantId);
        ((IGiantFollowerMixin)blaze).setIdealDistance(4);
        world.spawnEntity(blaze);
    }

    private void spawnHorsesAndRide(World world) {
        int number = 0;
        for (PlayerEntity p : world.getPlayers()) {
            List<String> namesRand = new java.util.ArrayList<>(horseNames);
            Collections.shuffle(namesRand);

            if(!p.isCreative()) {
                HorseEntity horsy = new HorseEntity(EntityType.HORSE, world);
                horsy.setPosition(p.getPos());
                horsy.setInvulnerable(true);
                horsy.setCustomName(Text.of(namesRand.get(number)));
                horsy.setVariant(Tool.randomEnum(HorseColor.class, world));
                horsy.saddle(SoundCategory.NEUTRAL);
                horsy.setTame(true);
                world.spawnEntity(horsy);
                p.startRiding(horsy);
                number++;
            }
        }
    }

    @Inject(method="onEntityCollision", at=@At("INVOKE"), cancellable = true)
    private void cantHaveShitInOverworld(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!world.isClient() && entity.isPlayer() && world.getRegistryKey() == World.OVERWORLD) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            if(((IServerWorldGiantMixin)world).hasSpawnedGiant()) {
                return;
            }
            ((IServerWorldGiantMixin)world).setHasSpawnedGiant(true);
            WardenEntity.addDarknessToClosePlayers((ServerWorld)world, Vec3d.ofCenter(pos), null, 40);
            //LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, SpawnReason.TRIGGERED, (ServerWorld)world, pos, 20, 5, 6, LargeEntitySpawnHelper.Requirements.WARDEN);
            MyComponents.CURSED.get(entity).setMannequinCursed(true);
            int giantId = spawnGiantBoss(world, pos);

            for (int i = 0 ; i < 10 ; ++i)
                spawnRavagerMinion(world, pos.add(-5, 0, 0), giantId, "pillager", 5+(i%5*5));
            for (int i = 0 ; i < 4 ; ++i)
                spawnRavagerMinion(world, pos.add(-5, 0, 0), giantId, "evoker", 7+i%2*5);
            spawnBlaze(world, pos, giantId);

            spawnHorsesAndRide(world);

            ci.cancel();
        }
    }

}
