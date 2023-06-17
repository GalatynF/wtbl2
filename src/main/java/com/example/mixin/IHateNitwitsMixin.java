package com.example.mixin;

import com.example.iMixin.ICauseImTNTMixin;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class IHateNitwitsMixin extends MerchantEntity {

    public IHateNitwitsMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract VillagerData getVillagerData();

    @Inject(at = @At("HEAD"), method = "interactMob")
    private void boomBox(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        World world = this.getWorld();
        if (!world.isClient) {
            if (this.getVillagerData().getProfession().equals(VillagerProfession.NITWIT)
                || this.getVillagerData().getProfession().equals(VillagerProfession.NONE)) {
                TntEntity tntEntity = new TntEntity(world, this.getX() + 0.5D, this.getY(), this.getZ() + 0.5D, null);
                tntEntity.setFuse(200);
                ((ICauseImTNTMixin) tntEntity).setBlackHole();
                world.spawnEntity(tntEntity);


                AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world, this.getX(), this.getY(), this.getZ());
                areaEffectCloudEntity.setRadius(5);
                areaEffectCloudEntity.setDuration(200);
                areaEffectCloudEntity.setRadiusGrowth(0.1f);
                areaEffectCloudEntity.setParticleType(ParticleTypes.SOUL_FIRE_FLAME);
                world.spawnEntity(areaEffectCloudEntity);

                world.playSound(tntEntity ,tntEntity.getBlockPos(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 5, 1);

                world.sendEntityStatus(this, (byte) 3);
                }
        }
    }
}