package com.zigythebird.advanced_hitboxes.client;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.client.renderers.ExampleAdvancedHitboxEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AdvancedHitboxesModClient {
    public static void init() {
        EntityRenderers.register(AdvancedHitboxesMod.EXAMPLE_ENTITY.get(), ExampleAdvancedHitboxEntityRenderer::new);
    }
}
