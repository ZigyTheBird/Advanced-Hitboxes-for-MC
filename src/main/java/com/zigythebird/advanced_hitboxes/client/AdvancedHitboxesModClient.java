package com.zigythebird.advanced_hitboxes.client;

import com.zigythebird.advanced_hitboxes.client.renderers.ExampleAdvancedHitboxEntityRenderer;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AdvancedHitboxesModClient {
    public static void init() {
        EntityRenderers.register(ModEntities.EXAMPLE_ENTITY.get(), ExampleAdvancedHitboxEntityRenderer::new);
    }
}
