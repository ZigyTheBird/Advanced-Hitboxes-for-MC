package com.zigythebird.advanced_hitboxes.registry;

import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.entity.TransformerEntity;
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

    public static final DeferredHolder<EntityType<?>, EntityType<TransformerEntity>> TRANSFORMER = ENTITIES.register(
            "transformer",
            () -> EntityType.Builder.of(TransformerEntity::new, MobCategory.MISC).sized(4, 2).passengerAttachments(0.1875F).clientTrackingRange(8).build("transformer")
    );
}
