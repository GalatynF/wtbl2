package com.github.galatynf.wtbl2.mixin.worldevents;

import com.github.galatynf.wtbl2.MusicPlayer;
import com.github.galatynf.wtbl2.iMixin.IFireworkMixin;
import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.cardinal.MyComponents;
import com.github.galatynf.wtbl2.enums.Wtbl2OverworldEvents;
import com.github.galatynf.wtbl2.iMixin.IServerWorldMixin;
import com.github.galatynf.wtbl2.iMixin.ISongMixin;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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
public abstract class ServerWorldEventsMixin extends World implements IServerWorldMixin {
    @Shadow @Final private ServerWorldProperties worldProperties;

    @Shadow public abstract List<ServerPlayerEntity> getPlayers();

    @Shadow public abstract void setWeather(int clearDuration, int rainDuration, boolean raining, boolean thundering);

    protected ServerWorldEventsMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Unique
    private int wtbl2_ticksBeforeEvent = -1;

    @Unique
    private Wtbl2OverworldEvents wtbl2_event = Wtbl2OverworldEvents.NONE;

    @Unique
    private boolean wtbl2_acidRain = false;

    @Override
    public Wtbl2OverworldEvents getCurrentEvent() {
        return this.wtbl2_event;
    }

    @Override
    public boolean isAcidRaining() {
        return this.wtbl2_acidRain;
    }

    private void superpowerPlayer(boolean availablePlayers, List<PlayerEntity> playersNotCreative) {
        if (!availablePlayers)
            return;
        PlayerEntity p2 = playersNotCreative.get((int) (Math.random() * playersNotCreative.size()));
        Tool.addStatus(p2, StatusEffects.SPEED, 1200, 2, false, true);
        Tool.addStatus(p2, StatusEffects.RESISTANCE, 1200, 4, false, true);
        Tool.addStatus(p2, StatusEffects.JUMP_BOOST, 1200, 3, false, true);
        Tool.addStatus(p2, StatusEffects.STRENGTH, 1200, 2, false, true);
        Tool.addStatus(p2, StatusEffects.GLOWING, 1200, 0, false, false);
        Tool.addStatus(p2, StatusEffects.INSTANT_HEALTH, 1, 20, false, false);
        p2.sendMessage(Text.of("The god of badassery lends you their power for 60 seconds"), true);
        LightningEntity lightningEntity;
        BlockPos blockPos = p2.getBlockPos();
        if (this.isSkyVisible(blockPos) && (lightningEntity = EntityType.LIGHTNING_BOLT.create((World) (Object) this)) != null) {
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
            this.spawnEntity(lightningEntity);
        }
    }

    private void swapGear(List<PlayerEntity> playersNotCreative) {
        if(playersNotCreative.size() < 2)
            return;
        List<PlayerInventory> inventories = new java.util.ArrayList<>(List.of());
        int nbPlayers = playersNotCreative.size();
        for (PlayerEntity p : playersNotCreative) {
            inventories.add(p.getInventory());
        }
        // Save an inventory for swapping
        List<ItemStack> saveSecondInv = new java.util.ArrayList<>(List.of());
        for (int i = 0 ; i < inventories.get(1).size() ; ++i) {
            saveSecondInv.add(inventories.get(1).getStack(i));
        }

        for (int i = 1 ; i < nbPlayers ; ++i) {
            PlayerInventory nextInventory = playersNotCreative.get((i+1)%nbPlayers).getInventory();
            inventories.get(i).clone(nextInventory);
        }

        for (int i = 0 ; i < saveSecondInv.size() ; ++i) {
            playersNotCreative.get(0).getInventory().setStack(i, saveSecondInv.get(i));
        }
    }

    @Inject(method = "tick", at=@At("TAIL"))
    private void manageWorldEvents(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(this.wtbl2_ticksBeforeEvent > 0)
            this.wtbl2_ticksBeforeEvent--;
        // Trigger new event
        if(this.worldProperties.getTime() % 12000 == 0) {
            this.wtbl2_acidRain = false;

            if (this.getRegistryKey().equals(World.OVERWORLD)) {
                this.wtbl2_ticksBeforeEvent = 200;
                int nbPossibleOutcomes = Wtbl2OverworldEvents.values().length-1;    //-1 because NONE
                Wtbl2OverworldEvents randEvent = Wtbl2OverworldEvents.values()[(int) (Math.random() * nbPossibleOutcomes)];
                //randEvent = Wtbl2OverworldEvents.NINJAGO;
                switch (randEvent) {
                    case ROCKET_PLAYERS -> {
                        Tool.sendGlobalMessage((ServerWorld) (Object) this, "Let the voice of love take you higheeeer", "blue");
                    }
                    case SUPERPOWERED_PLAYER -> {
                        Tool.sendGlobalMessage((ServerWorld) (Object) this, "The gods are ready to reveal the Chosen One", "green");
                    }
                    case MANNEQUIN_CURSE -> {
                        Tool.sendGlobalMessage((ServerWorld) (Object) this, "Don't look behind you :)", "red");
                    }
                    case ACID_RAIN -> {
                        Tool.sendGlobalMessage((ServerWorld) (Object) this, "WEATHER FORECAST : Acid rain and scary thunder", "red");
                    }
                    case BIND_GEAR -> {
                        Tool.sendGlobalMessage((ServerWorld) (Object) this, "A leprechaun has sewn your clothes to your skin :(", "red");
                        this.wtbl2_ticksBeforeEvent = 1;
                    }
                    case SWAP_GEAR -> {
                        Tool.sendGlobalMessage((ServerWorld) (Object) this, "Remember, sharing is caring", "red");
                        this.wtbl2_ticksBeforeEvent = 1;
                    }
                    case EAR_WORM -> {
                        Tool.sendGlobalMessage((ServerWorld) (Object) this, "A M O G U S", "red");
                    }
                    default -> {
                        this.wtbl2_event = Wtbl2OverworldEvents.NONE;
                    }
                }
                this.wtbl2_event = randEvent;
            }
        }

        // Activate new event
        if(this.wtbl2_ticksBeforeEvent == 0 && !this.wtbl2_event.equals(Wtbl2OverworldEvents.NONE)) {
            List<PlayerEntity> playersNotCreative = Tool.getPlayersNotCreative((World)(Object)this);
            boolean availablePlayers = playersNotCreative.size() != 0;
            switch (this.wtbl2_event) {
                case ROCKET_PLAYERS -> {
                    for (PlayerEntity p : playersNotCreative) {
                        ItemStack itemStack = Items.FIREWORK_ROCKET.getDefaultStack();
                        Vec3d vec3d = p.getPos();
                        FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity((World) (Object) this, p, vec3d.x, vec3d.y, vec3d.z, itemStack);
                        fireworkRocketEntity.setInvisible(true);
                        ((IFireworkMixin) fireworkRocketEntity).setLifetime(50);
                        this.spawnEntity(fireworkRocketEntity);

                        p.startRiding(fireworkRocketEntity);
                    }
                }
                case MANNEQUIN_CURSE -> {
                    if (!availablePlayers)
                        break;
                    PlayerEntity p = playersNotCreative.get((int) (Math.random() * playersNotCreative.size()));
                    MyComponents.CURSED.get(p).setMannequinCursed(true);
                }
                case SUPERPOWERED_PLAYER -> {
                    superpowerPlayer(availablePlayers, playersNotCreative);
                }
                case ACID_RAIN -> {
                    this.setWeather(0, 3600, true, true);
                    this.wtbl2_acidRain = true;
                }
                case BIND_GEAR -> {
                    for (PlayerEntity p : playersNotCreative) {
                        for (ItemStack i : p.getArmorItems()) {
                            if(!i.equals(Items.GLASS.getDefaultStack()) && !EnchantmentHelper.hasBindingCurse(i))
                                i.addEnchantment(Enchantments.BINDING_CURSE, 1);
                        }
                    }
                }
                case SWAP_GEAR -> {
                    swapGear(playersNotCreative);
                }
                case EAR_WORM -> {
                    for (PlayerEntity p : playersNotCreative) {
                        ((ISongMixin)p).setSong(MusicPlayer.AMOGUS_DRIP);
                    }
                }
                default -> {
                    Tool.sendGlobalMessage((ServerWorld) (Object) this, "No event this time...");
                }
            }
            this.wtbl2_event = Wtbl2OverworldEvents.NONE;
        }
    }
}
