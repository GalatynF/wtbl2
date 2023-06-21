package com.github.galatynf.wtbl2.cardinal;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public interface StandAttackMannequinComponent extends ComponentV3 {
    void setOwner(UUID newOwnerUuid, int newOwnerId, boolean isOwnerPlayer);
    UUID getOwnerUuid();
    int getOwnerId();
    void initialiseAttack(int duration);
    int getRemainingDuration();
    void decrementRemainingDuration();
    boolean isOwnerPlayer();
}

class StandAttackMannequin implements StandAttackMannequinComponent {

    private UUID ownerUuid = null;
    private int ownerId = -1;
    private int remainingDuration = -1;
    private boolean isOwnerPlayer = false;

    @Override
    public void setOwner(UUID newOwnerUuid, int newOwnerId, boolean isOwnerPlayer) {
        this.ownerUuid = newOwnerUuid;
        this.ownerId = newOwnerId;
        this.isOwnerPlayer = isOwnerPlayer;
    }

    @Override
    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    @Override
    public int getOwnerId() {
        return this.ownerId;
    }

    @Override
    public void initialiseAttack(int duration) {
        this.remainingDuration = duration;
    }

    @Override
    public int getRemainingDuration() {
        return this.remainingDuration;
    }

    @Override
    public void decrementRemainingDuration() {
        this.remainingDuration--;
    }

    @Override
    public boolean isOwnerPlayer() {
        return this.isOwnerPlayer;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.ownerUuid = tag.getUuid("ownerUuid");
        this.ownerId = tag.getInt("ownerId");
        this.isOwnerPlayer = tag.getBoolean("isOwnerPlayer");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putUuid("ownerUuid", this.ownerUuid);
        tag.putInt("ownerId", this.ownerId);
        tag.putBoolean("isOwnerPlayer", this.isOwnerPlayer);
    }
}