package com.zigythebird.advanced_hitboxes.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zigythebird.advanced_hitboxes.client.models.TransformerModel;
import com.zigythebird.advanced_hitboxes.entity.TransformerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TransformerRenderer extends GeoEntityRenderer<TransformerEntity> {
    public TransformerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TransformerModel());
    }

    @Override
    public void preRender(PoseStack poseStack, TransformerEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        GeoBone bone = model.getBone("front_left_hitbox").get();
        bone.setScaleX(0);
        bone.setScaleY(0);
        bone.setScaleZ(0);


        bone = model.getBone("back_left_hitbox").get();
        bone.setScaleX(0);
        bone.setScaleY(0);
        bone.setScaleZ(0);

        bone = model.getBone("front_right_hitbox").get();
        bone.setScaleX(0);
        bone.setScaleY(0);
        bone.setScaleZ(0);

        try {
            bone = model.getBone("middle_right_hitbox").get();
            bone.setScaleX(0);
            bone.setScaleY(0);
            bone.setScaleZ(0);

            bone = model.getBone("middle_left_hitbox").get();
            bone.setScaleX(0);
            bone.setScaleY(0);
            bone.setScaleZ(0);
        }
        catch (Exception ignore) {}

        bone = model.getBone("back_right_hitbox").get();
        bone.setScaleX(0);
        bone.setScaleY(0);
        bone.setScaleZ(0);

        bone = model.getBone("main_hitbox").get();
        bone.setScaleX(0);
        bone.setScaleY(0);
        bone.setScaleZ(0);

        poseStack.mulPose(Axis.YP.rotationDegrees(-animatable.getYRot()));
    }
}
