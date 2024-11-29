package com.zigythebird.advanced_hitboxes.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.advanced_hitboxes.client.renderers.AdvancedHitboxRenderer;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderEntity", at = @At("TAIL"))
    private void inject(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        HitboxUtils.tick(entity);
        if (this.entityRenderDispatcher.shouldRenderHitBoxes() && !entity.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
            AdvancedHitboxRenderer.renderAdvancedHitboxes(bufferSource.getBuffer(RenderType.lines()), entity, camX, camY, camZ);
        }
    }
}