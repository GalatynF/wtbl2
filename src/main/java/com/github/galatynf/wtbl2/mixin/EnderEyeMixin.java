package com.github.galatynf.wtbl2.mixin;

import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnderEyeItem.class)
public abstract class EnderEyeMixin extends Item {
    public EnderEyeMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EyeOfEnderEntity;initTargetPos(Lnet/minecraft/util/math/BlockPos;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void rideEye(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack itemStack, HitResult hitResult, ServerWorld serverWorld, BlockPos blockPos, EyeOfEnderEntity eyeOfEnderEntity) {
        if(!world.isClient() && !user.isCreative())
            user.startRiding(eyeOfEnderEntity);
    }
}
