package com.zigythebird.advanced_hitboxes.client;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.client.models.ExampleAdvancedHitboxEntityModel;
import com.zigythebird.advanced_hitboxes.client.renderers.ExampleAdvancedHitboxEntityRenderer;
import com.zigythebird.advanced_hitboxes.client.renderers.TransformerRenderer;
import com.zigythebird.advanced_hitboxes.entity.TransformerEntity;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public class ModClientEvents {
    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class modEventBus {
        @SubscribeEvent
        public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ExampleAdvancedHitboxEntityRenderer.LAYER_LOCATION, ExampleAdvancedHitboxEntityModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.TRANSFORMER.get(), TransformerRenderer::new);
        }

        @SubscribeEvent
        public static void registerListeners(RegisterClientReloadListenersEvent event) {
            if (ModList.get().isLoaded("playeranimatorapi")) {
                event.registerReloadListener(new PlayerAnimResourceReloadListener());
            }
        }
    }

    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static class GameEvents {
        @SubscribeEvent
        public static void onPlayerjoin(ClientPlayerNetworkEvent.LoggingIn event) {
            PlayerAnimResourceReloadListener.onPlayerJoin();
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();

            Player player = minecraft.player;

            if (player == null) {
                return;
            }

            Entity riding = player.getVehicle();

            if (!(riding instanceof TransformerEntity)) {
                return;
            }

            TransformerEntity car = (TransformerEntity) riding;

            Minecraft mc = Minecraft.getInstance();

            car.updateControls(mc.options.keyUp.isDown(), mc.options.keyDown.isDown(), mc.options.keyLeft.isDown(), mc.options.keyRight.isDown(), player);
        }
    }
}
