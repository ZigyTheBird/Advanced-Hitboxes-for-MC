package com.zigythebird.advanced_hitboxes.mixin.accessors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker
    static List<VoxelShape> callCollectColliders(@Nullable Entity entity, Level level, List<VoxelShape> collisions, AABB boundingBox) {
        throw new UnsupportedOperationException();
    }

    @Invoker
    Vec3 callCollide(Vec3 vec);
}
