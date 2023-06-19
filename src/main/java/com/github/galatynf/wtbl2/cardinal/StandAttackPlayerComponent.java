package com.github.galatynf.wtbl2.cardinal;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;

public interface StandAttackPlayerComponent extends ComponentV3 {
    void setStandAttack(int mannequinId);
}

class StandAttackPlayer implements StandAttackPlayerComponent {

    private int standId = -1;

    @Override
    public void setStandAttack(int mannequinId) {
        this.standId = mannequinId;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.standId = tag.getInt("standId");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("standId", this.standId);
    }
}
