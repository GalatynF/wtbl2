package com.github.galatynf.wtbl2.cardinal;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public interface CursedMannequinOwnerComponent extends ComponentV3 {
    UUID getOwner();
    void setOwner(UUID ownerUuid);
}

class CursedMannequinOwnerClassComponent implements CursedMannequinOwnerComponent {

    private UUID owner = null;

    @Override
    public UUID getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(UUID ownerUuid) {
        this.owner = ownerUuid;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if(!tag.getBoolean("hasOwner")) {
            this.owner = null;
        }
        else {
            this.owner = tag.getUuid("ownerUuid");
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (this.owner == null) {
            tag.putBoolean("hasOwner", false);
        }
        else {
            tag.putBoolean("hasOwner", true);
            tag.putUuid("ownerUuid", this.owner);
        }
    }
}
