package com.zigythebird.advanced_hitboxes.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ExampleAdvancedHitboxEntityModel extends EntityModel<ExampleAdvancedHitboxEntity> {
    private final ModelPart bone;
    private final ModelPart bone_hitbox;

    public ExampleAdvancedHitboxEntityModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.bone_hitbox = bone.getChild("bone_hitbox");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone_hitbox = bone.addOrReplaceChild("bone_hitbox", CubeListBuilder.create().texOffs(0, 0).addBox(-20.0F, -22.0F, -8.0F, 40.0F, 22.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(ExampleAdvancedHitboxEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}