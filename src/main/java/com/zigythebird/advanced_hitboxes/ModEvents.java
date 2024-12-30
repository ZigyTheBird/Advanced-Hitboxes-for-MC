package com.zigythebird.advanced_hitboxes;

import com.zigythebird.advanced_hitboxes.command.TransformCommand;
import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.network.MessageControlCar;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModEvents {
    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class modEventBus {
        @SubscribeEvent
        public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.EXAMPLE_ENTITY.get(), ExampleAdvancedHitboxEntity.createMobAttributes().build());
        }

        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(
                    MessageControlCar.TYPE,
                    StreamCodec.of(MessageControlCar::toBytes, MessageControlCar::fromBytes),
                    MessageControlCar::executeServerSide
            );
        }
    }

    @EventBusSubscriber(modid = AdvancedHitboxesMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class GameEvents {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event){
            TransformCommand.register(event.getDispatcher());
        }
    }
}
