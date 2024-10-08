package com.zigythebird.advanced_hitboxes;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimationState;
import com.zigythebird.advanced_hitboxes.geckolib.constant.DataTickets;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.data.EntityModelData;
import com.zigythebird.advanced_hitboxes.interfaces.EntityInterface;
import com.zigythebird.advanced_hitboxes.interfaces.LivingEntityInterface;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import com.zigythebird.advanced_hitboxes.utils.ClientUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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
        private static <T extends Entity & AdvancedHitboxEntity> void entityTick(EntityTickEvent.Post event) {
            if (event.getEntity() instanceof AdvancedHitboxEntity || event.getEntity() instanceof Player) {
                Entity animatable = event.getEntity();

                LivingEntity livingEntity = animatable instanceof LivingEntity entity ? entity : null;
                boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null);
                float lerpBodyRot = livingEntity == null ? 0 : ((LivingEntityInterface)livingEntity).advanced_Hitboxes$commonYBodyRot();
                float lerpHeadRot = livingEntity == null ? 0 : livingEntity.yHeadRot;
                float netHeadYaw = lerpHeadRot - lerpBodyRot;

                float limbSwingAmount = 0;
                float limbSwing = 0;

//                if (!shouldSit && animatable.isAlive() && livingEntity != null) {
//                    limbSwingAmount = livingEntity.walkAnimation.speed();
//                    limbSwing = livingEntity.walkAnimation.position();
//
//                    if (livingEntity.isBaby())
//                        limbSwing *= 3f;
//
//                    if (limbSwingAmount > 1f)
//                        limbSwingAmount = 1f;
//                }

                float headPitch = animatable.getXRot();
                float motionThreshold = 0.015f;
                Vec3 velocity = animatable.getDeltaMovement();
                float avgVelocity = (float)(Math.abs(velocity.x) + Math.abs(velocity.z) / 2f);
                AnimationState<T> animationState = new AnimationState<>(((T)animatable), limbSwing, limbSwingAmount, 1, avgVelocity >= motionThreshold && limbSwingAmount != 0);
                long instanceId = animatable.getId();
                HitboxModel<T> currentModel = ((AdvancedHitboxEntity)animatable).getHitboxModel();

                animationState.setData(DataTickets.TICK, ((AdvancedHitboxEntity)animatable).getTick(animatable));
                animationState.setData(DataTickets.ENTITY, animatable);
                animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, event.getEntity() instanceof LivingEntity && ((LivingEntity)event.getEntity()).isBaby(), -netHeadYaw, -headPitch));
                currentModel.addAdditionalStateData(((T)animatable), instanceId, animationState::setData);
                currentModel.handleAnimations(((T)animatable), instanceId, animationState, 1);

                if (animatable.level().isClientSide) {
                    ClientUtils.updateHitboxIfRendering((T)animatable);
                }
            }
        }
    }
}
