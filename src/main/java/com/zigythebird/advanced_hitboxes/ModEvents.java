package com.zigythebird.advanced_hitboxes;

import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class ModEvents {
    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class modEventBus {
        @SubscribeEvent
        public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.EXAMPLE_ENTITY.get(), ExampleAdvancedHitboxEntity.createMobAttributes().build());
        }
    }

//    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
//    public static class GameEvents {}
}
