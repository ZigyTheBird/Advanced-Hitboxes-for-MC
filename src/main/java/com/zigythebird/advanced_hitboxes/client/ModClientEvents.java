package com.zigythebird.advanced_hitboxes.client;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.client.models.ExampleAdvancedHitboxEntityModel;
import com.zigythebird.advanced_hitboxes.client.renderers.ExampleAdvancedHitboxEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ModClientEvents {
    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class modEventBus {
        @SubscribeEvent
        public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ExampleAdvancedHitboxEntityRenderer.LAYER_LOCATION, ExampleAdvancedHitboxEntityModel::createBodyLayer);
        }
    }

//    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
//    public static class GameEvents {}
}
