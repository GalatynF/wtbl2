package com.github.galatynf.wtbl2.cardinal;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public interface StandAttackMannequinComponent extends ComponentV3 {
    void setOwner(UUID newOwner);
    UUID getOwner();
    void initialiseAttack(int duration);
    int getRemainingDuration();
    void decrementRemainingDuration();
}

class StandAttackMannequin implements StandAttackMannequinComponent {

    private UUID ownerUuid = null;
    private int remainingDuration = -1;

    @Override
    public void setOwner(UUID newOwner) {
        this.ownerUuid = newOwner;
    }

    @Override
    public UUID getOwner() {
        return this.ownerUuid;
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
    public void readFromNbt(NbtCompound tag) {
        this.ownerUuid = tag.getUuid("ownerUuid");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putUuid("ownerUuid", this.ownerUuid);
    }
}