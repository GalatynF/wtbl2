package com.example.cardinal;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;

public interface Cursedcomponent extends ComponentV3 {
    boolean isMannequinCursed();
    void setMannequinCursed(boolean cursed);

    int getMannequinId();
    void setMannequinId(int mann);
}

class CursedMannequinComponent implements Cursedcomponent {

    private boolean isMannequinCursed = false;
    private int mannequinId = -1;

    @Override
    public boolean isMannequinCursed() {
        return this.isMannequinCursed;
    }

    @Override
    public void setMannequinCursed(boolean cursed) {
        this.isMannequinCursed = cursed;
    }

    @Override
    public int getMannequinId() {
        return this.mannequinId;
    }

    @Override
    public void setMannequinId(int mann) {
        this.mannequinId = mann;
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        this.isMannequinCursed = tag.getBoolean("isMannequinCursed");
        this.mannequinId = tag.getInt("mannequinId");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("isMannequinCursed", this.isMannequinCursed);
        tag.putInt("mannequinId", this.mannequinId);
    }
}
