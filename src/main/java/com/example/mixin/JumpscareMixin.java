package com.example.mixin;

import com.example.Tool;
import com.example.iMixin.IArmorstandMixin;
import com.example.iMixin.IPlayerMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class JumpscareMixin extends LivingEntity implements IPlayerMixin {
    @Shadow public abstract boolean isCreative();

    protected JumpscareMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setHasMannequin(boolean hasMann) {
        this.hasAMannequin = hasMann;
    }

    private boolean hasAMannequin = false;

    @Inject(method="tick", at=@At("HEAD"))
    private void boo(CallbackInfo ci) {
        World world = this.getWorld();
        Tool.print(world.getTime());
        if(!world.isClient()
                && world.getTime()%6000 == 0
                && !this.isCreative()
                && world.getLightLevel(this.getBlockPos()) < 5
                && !this.hasAMannequin) {
            for (PlayerEntity p : world.getPlayers()) {
                if (this.distanceTo(p) < 50 && !this.getUuid().equals(p.getUuid())) {
                    return;
                }
            }
            Vec3d rotation = this.getRotationVector().normalize();
            ArmorStandEntity armorStand = new ArmorStandEntity(world, this.getX()-2*rotation.x, this.getY(), this.getZ()-2*rotation.z);
            armorStand.setYaw(this.getYaw());
            armorStand.equipStack(EquipmentSlot.HEAD, Items.PLAYER_HEAD.getDefaultStack());
            ((IArmorstandMixin)armorStand).setOwner(this.getUuid());
            world.spawnEntity(armorStand);
            this.hasAMannequin = true;
        }
    }
}
