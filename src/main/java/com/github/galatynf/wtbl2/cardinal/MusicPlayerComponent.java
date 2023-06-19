package com.github.galatynf.wtbl2.cardinal;

import com.github.galatynf.wtbl2.Tool;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public interface MusicPlayerComponent extends ComponentV3 {
    void startSong(String song);
    String getSong();
    char nextNote();
}

class MusicPlayer implements MusicPlayerComponent {
    String song = "";
    int currentNoteIndex = 0;

    @Override
    public void startSong(String song) {
        this.song = song;
    }

    @Override
    public String getSong() {
        return this.song;
    }


    @Override
    public char nextNote() {
        if(this.currentNoteIndex < this.song.length())
            return this.song.charAt(this.currentNoteIndex++);
        return '!';
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        this.song = tag.getString("song");
        this.currentNoteIndex = tag.getInt("noteIndex");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("song", this.song);
        tag.putInt("noteIndex", this.currentNoteIndex);
    }
}