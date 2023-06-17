package com.example.mixin;

import com.example.Tool;
import com.example.cardinal.MyComponents;
import com.example.enums.Wtbl2WorldEvents;
import com.example.iMixin.IFireworkMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldEventsMixin extends World {
    @Shadow @Final private ServerWorldProperties worldProperties;

    @Shadow public abstract List<ServerPlayerEntity> getPlayers();

    protected ServerWorldEventsMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Unique
    private int wtbl2_ticksBeforeEvent = -1;

    @Unique
    private Wtbl2WorldEvents wtbl2_event = Wtbl2WorldEvents.NONE;

    @Inject(method = "tick", at=@At("TAIL"))
    private void manageWorldEvents(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(this.wtbl2_ticksBeforeEvent > 0 && this.wtbl2_event != null)
            this.wtbl2_ticksBeforeEvent--;
        // Trigger new event
        if(this.worldProperties.getTime() % 400 == 0) {  // 5min * 60 sec * 20 ticks = 6000
            if (this.getRegistryKey().equals(World.OVERWORLD)) {
                int nbPossibleOutcomes = 2;
                int rand = (int) (Math.random() * nbPossibleOutcomes);
                switch (rand) {
                    case(0):
                        Tool.sendGlobalMessage((ServerWorld)(Object) this, "Let the voice of love take you higheeeer");
                        this.wtbl2_event = Wtbl2WorldEvents.ROCKET_PLAYERS;
                        break;
                    case(1):
                        Tool.sendGlobalMessage((ServerWorld)(Object) this, "The gods are ready to reveal the Chosen One");
                        this.wtbl2_event = Wtbl2WorldEvents.SUPERPOWERED_PLAYER;
                        break;
                }
                this.wtbl2_ticksBeforeEvent = 200;
            }
        }

        // Activate new event
        if(this.wtbl2_ticksBeforeEvent == 0) {
            List<PlayerEntity> playersNotCreative = Tool.getPlayersNotCreative((World)(Object)this);
            boolean availablePlayers = playersNotCreative.size() != 0;
            switch (this.wtbl2_event) {
                case ROCKET_PLAYERS:
                    for (PlayerEntity p : playersNotCreative) {
                        ItemStack itemStack = Items.FIREWORK_ROCKET.getDefaultStack();
                        Vec3d vec3d = p.getPos();
                        FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity((World)(Object) this, p, vec3d.x, vec3d.y, vec3d.z, itemStack);
                        fireworkRocketEntity.setInvisible(true);
                        ((IFireworkMixin)fireworkRocketEntity).setLifetime(50);
                        this.spawnEntity(fireworkRocketEntity);

                        p.startRiding(fireworkRocketEntity);
                    }
                    break;
                case MANNEQUIN_CURSE:
                    if (!availablePlayers)
                        break;
                    PlayerEntity p = playersNotCreative.get((int) (Math.random()*playersNotCreative.size()));
                    MyComponents.CURSED.get(p).setMannequinCursed(true);
                    break;
                case SUPERPOWERED_PLAYER:
                    if(!availablePlayers)
                        break;
                    PlayerEntity p2 = playersNotCreative.get((int) (Math.random()*playersNotCreative.size()));
                    Tool.addStatus(p2, StatusEffects.SPEED, 600,2,false, true);
                    Tool.addStatus(p2, StatusEffects.RESISTANCE, 600,4,false, true);
                    Tool.addStatus(p2, StatusEffects.JUMP_BOOST, 600,3,false, true);
                    Tool.addStatus(p2, StatusEffects.STRENGTH, 600,2,false, true);
                    Tool.addStatus(p2, StatusEffects.GLOWING, 600,0,false, false);
                    Tool.addStatus(p2, StatusEffects.INSTANT_HEALTH, 1,20,false, false);
                    p2.sendMessage(Text.of("The god of badassery lends you their power for 30 seconds"), true);
                    LightningEntity lightningEntity;
                    BlockPos blockPos= p2.getBlockPos();
                    if (this.isSkyVisible(blockPos) && (lightningEntity = EntityType.LIGHTNING_BOLT.create((World)(Object)this)) != null) {
                        lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                        this.spawnEntity(lightningEntity);
                    }
                default:
                    //Tool.sendGlobalMessage((ServerWorld)(Object) this, "Error case Event");
            }
            this.wtbl2_event = Wtbl2WorldEvents.NONE;
        }
    }
}
