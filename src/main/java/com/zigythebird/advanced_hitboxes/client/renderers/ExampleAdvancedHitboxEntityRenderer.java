package com.zigythebird.advanced_hitboxes.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.client.models.ExampleAdvancedHitboxEntityModel;
import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ExampleAdvancedHitboxEntityRenderer extends MobRenderer<ExampleAdvancedHitboxEntity, ExampleAdvancedHitboxEntityModel> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(AdvancedHitboxesMod.id("hitbox_test"), "main");

    public ExampleAdvancedHitboxEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ExampleAdvancedHitboxEntityModel(context.bakeLayer(LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ExampleAdvancedHitboxEntity entity) {
        return AdvancedHitboxesMod.id("textures/entity/hitbox_test.png");
    }

    @Override
    protected void setupRotations(ExampleAdvancedHitboxEntity entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        poseStack.mulPose(Axis.YP.rotation(Mth.DEG_TO_RAD * Mth.wrapDegrees(entity.getYRot())));
    }
}
