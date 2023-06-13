package com.example.cardinal;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;

public interface Cursedcomponent extends ComponentV3 {
    boolean isMannequinCursed();
    void setMannequinCursed(boolean cursed);

    boolean hasMannequin();
    void setHasMannequin(boolean has);
}

class CursedMannequinComponent implements Cursedcomponent {

    private boolean isMannequinCursed = false;
    private boolean hasMannequin = false;

    @Override
    public boolean isMannequinCursed() {
        return this.isMannequinCursed;
    }

    @Override
    public void setMannequinCursed(boolean cursed) {
        this.isMannequinCursed = cursed;
    }

    @Override
    public boolean hasMannequin() {
        return this.hasMannequin;
    }

    @Override
    public void setHasMannequin(boolean has) {
        this.hasMannequin = has;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.isMannequinCursed = tag.getBoolean("isMannequinCursed");
        this.hasMannequin = tag.getBoolean("hasMannequin");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("isMannequinCursed", this.isMannequinCursed);
        tag.putBoolean("hasMannequin", this.hasMannequin);
    }
}
