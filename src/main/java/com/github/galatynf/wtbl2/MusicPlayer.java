package com.github.galatynf.wtbl2;

import com.mojang.datafixers.kinds.IdF;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static java.util.Map.entry;

public class MusicPlayer {
    private static final Map<Character, Float> notesMap = Map.ofEntries(
            entry('0', 0.5F),
            entry('1', 0.529732F),  //sol
            entry('2', 0.561231F),
            entry('3', 0.594604F),  //la
            entry('4', 0.629961F),
            entry('5', 0.667420F),  //si
            entry('6', 0.707107F),  //do
            entry('7', 0.749154F),
            entry('8', 0.793701F),  //re
            entry('9', 0.840896F),
            entry('A', 0.890899F),  //mi
            entry('B', 0.943874F),  //fa
            entry('C', 1F),
            entry('D', 1.059463F),   //sol
            entry('E', 1.122462F),
            entry('F', 1.189207F),  //la
            entry('G', 1.259921F),
            entry('H', 1.334840F),  //si
            entry('I', 1.414214F),  //do
            entry('J', 1.498307F),
            entry('K', 1.587401F),  //re
            entry('L', 1.681793F),
            entry('M', 1.781797F),  //mi
            entry('N', 1.887749F),  //fa
            entry('O', 2F),
            entry('P', 2.3F) //sol
    );
    public static class Track {
        public final net.minecraft.sound.SoundEvent instrument;
        public final java.lang.String notes;
        public Track(net.minecraft.sound.SoundEvent instrument, java.lang.String notes) {
            this.instrument = instrument;
            this.notes = notes;
        }
    }
    private ArrayList<Track> tracks = new ArrayList<>();
    private int noteIndex = 0;

    public MusicPlayer() {

    }

    public MusicPlayer(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }


    public int getNoteIndex() {
        return this.noteIndex;
    }

    public void addTrack(Track newtrack) {
        this.tracks.add(newtrack);
    }

    public boolean playNextNotes(World world, Entity musician, boolean loop) {
        boolean songEnded = true;
        for (Track track : this.tracks) {
            if(this.noteIndex < track.notes.length()) {
                songEnded = false;
                if(track.notes.charAt(this.noteIndex)!='_') {
                    world.playSound(null, musician.getBlockPos(), track.instrument, SoundCategory.PLAYERS, 120f, notesMap.get(track.notes.charAt(this.noteIndex)));
                }
            }
        }
        this.noteIndex++;
        if(songEnded && loop)
            this.noteIndex = 0;
        return songEnded;
    }

    public static final MusicPlayer IL_VENTO_DORO = new MusicPlayer(new ArrayList<>(Arrays.asList(
            new Track(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), "6_6_64_6_9_6_14_6_6_64_6_C_B_94"),
            new Track(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), "6_6_64_6_9_6_14_6_6_64_6_C_B_94"),
            new Track(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM.value(), "0___0___0___0___0___0___0___0__")
    )));

    public static final MusicPlayer AMOGUS_DRIP = new MusicPlayer(new ArrayList<>(Arrays.asList(
            new Track(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(), "6_9_B_C_B_9_6_____486____________6_9_B_C_B_9_C_____C_B_9_C_B_9_6____"),
            new Track(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(),     "__________________________1_6______________________C_____C__________")
    )));

    public static final MusicPlayer STARDUST_CRUSADERS = new MusicPlayer(new ArrayList<>(Arrays.asList(
            new Track(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value(),      "B_____E___I___N__D_____E_G_H_I__H__E___B_6_I_H__E__B___L_K_____"),
            new Track(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(),  "B_____E___I___N__D_____E_G_H_I__H__E___B_6_I_H__E__B___L_K_____"),
            new Track(SoundEvents.BLOCK_NOTE_BLOCK_SNARE.value(),      "B_____E___I___N__N__N__N_N_NN____I_I___N_N_NN____I_I____L_K__KKK")
    )));
}
