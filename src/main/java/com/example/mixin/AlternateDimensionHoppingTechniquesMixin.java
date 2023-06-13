package com.example.mixin;

import com.example.cardinal.MyComponents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class AlternateDimensionHoppingTechniquesMixin extends LivingEntity {
    protected AlternateDimensionHoppingTechniquesMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="tick", at=@At("HEAD"))
    private void enterTheNether(CallbackInfo ci) {
        World world = this.getWorld();
        if (!world.isClient()) {
            if (world.getBlockState(this.getBlockPos()).equals(Blocks.LAVA.getDefaultState())
            && world.getBlockState(this.getBlockPos().add(0, 1, 0)).equals(Blocks.LAVA.getDefaultState())
            && world.getBlockState(this.getBlockPos().add(0, -1, 0)).equals(Blocks.CRYING_OBSIDIAN.getDefaultState())) {
                if (this.canUsePortals()) {
                    MyComponents.CURSED.get(this).setMannequinCursed(false);
                    this.setInNetherPortal(this.getBlockPos());
                }
            }

            if(this.getY() > 1000 && world instanceof ServerWorld && this.canUsePortals()) {
                RegistryKey<World> registryKey = world.getRegistryKey() == World.END ? World.OVERWORLD : World.END;
                ServerWorld serverWorld = ((ServerWorld)world).getServer().getWorld(registryKey);
                if (serverWorld == null) {
                    return;
                }
                this.removeStatusEffect(StatusEffects.LEVITATION);
                this.moveToWorld(serverWorld);
            }
        }
    }
}
