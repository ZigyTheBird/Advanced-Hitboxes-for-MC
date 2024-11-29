package com.zigythebird.advanced_hitboxes.client;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.client.models.ExampleAdvancedHitboxEntityModel;
import com.zigythebird.advanced_hitboxes.client.renderers.ExampleAdvancedHitboxEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public class ModClientEvents {
    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class modEventBus {
        @SubscribeEvent
        public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ExampleAdvancedHitboxEntityRenderer.LAYER_LOCATION, ExampleAdvancedHitboxEntityModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerListeners(RegisterClientReloadListenersEvent event) {
            if (ModList.get().isLoaded("playeranimatorapi")) {
                event.registerReloadListener(new ResourceReloadListener());
            }
        }
    }

    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static class GameEvents {
        @SubscribeEvent
        public static void onPlayerjoin(ClientPlayerNetworkEvent.LoggingIn event) {
            ResourceReloadListener.onPlayerJoin();
        }
    }
}
