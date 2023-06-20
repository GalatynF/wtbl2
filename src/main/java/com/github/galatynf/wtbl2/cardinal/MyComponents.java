package com.github.galatynf.wtbl2.cardinal;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;

public class MyComponents implements EntityComponentInitializer {

    public static final ComponentKey<CursedComponent> CURSED =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("wtbl2:cursed"), CursedComponent.class);

    public static final ComponentKey<CursedMannequinOwnerComponent> CURSED_MANNEQUIN =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("wtbl2:cursed_mannequin"), CursedMannequinOwnerComponent.class);

    public static final ComponentKey<StandAttackPlayerComponent> STAND_ATTACKER =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("wtbl2:attack_stand"), StandAttackPlayerComponent.class);
    public static final ComponentKey<StandAttackMannequinComponent> STAND_ATTACK_MANNEQUIN =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("wtbl2:attack_stand_mannequin"), StandAttackMannequinComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(CURSED, player -> new CursedMannequinComponent(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerFor(ArmorStandEntity.class, CURSED_MANNEQUIN, mannequin -> new CursedMannequinOwnerClassComponent());

        registry.registerForPlayers(STAND_ATTACKER, player -> new StandAttackPlayer(), RespawnCopyStrategy.NEVER_COPY);
        registry.registerFor(ArmorStandEntity.class, STAND_ATTACK_MANNEQUIN, mannequin -> new StandAttackMannequin());

    }
}
