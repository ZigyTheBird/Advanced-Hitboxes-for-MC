package com.zigythebird.advanced_hitboxes.registry;

import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod.MOD_ID;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            BuiltInRegistries.ENTITY_TYPE,
            MOD_ID
    );

    public static final DeferredHolder<EntityType<?>, EntityType<ExampleAdvancedHitboxEntity>> EXAMPLE_ENTITY = ENTITIES.register(
            "example_entity",
            () -> EntityType.Builder.of(ExampleAdvancedHitboxEntity::new, MobCategory.MISC).sized(1, 1).build("example_entity")
    );
}
