package com.example.cardinal;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;

import java.awt.*;

public class MyComponents implements EntityComponentInitializer {

    public static final ComponentKey<Cursedcomponent> CURSED =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("wtbl2:cursed"), Cursedcomponent.class);

    public static final ComponentKey<CursedMannequinOwnerComponent> CURSED_MANNEQUIN =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("wtbl2:cursed_mannequin"), CursedMannequinOwnerComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(CURSED, player -> new CursedMannequinComponent(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerFor(ArmorStandEntity.class, CURSED_MANNEQUIN, mannequin -> new CursedMannequinOwnerClassComponent());
    }
}
