package com.zigythebird.advanced_hitboxes;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class ModEvents {
    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class modEventBus {
        @SubscribeEvent
        public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.EXAMPLE_ENTITY.get(), ExampleAdvancedHitboxEntity.createMobAttributes().build());
        }
    }

    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class GameEvents {

        @SubscribeEvent
        public static void entityTick(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof AdvancedHitboxEntity || event.getEntity() instanceof Player) {
                HitboxUtils.tick(event.getEntity(), true);
            }
        }
    }
}
