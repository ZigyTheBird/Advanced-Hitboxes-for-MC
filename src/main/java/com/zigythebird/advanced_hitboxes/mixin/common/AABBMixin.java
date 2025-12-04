package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.interfaces.AABBAccessor;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(AABB.class)
public abstract class AABBMixin implements AABBAccessor {
    @Shadow public abstract boolean contains(double x, double y, double z);

    @Shadow public abstract Optional<Vec3> clip(Vec3 from, Vec3 to);

    @Unique
    private String advanced_Hitboxes$name = "";

    @Override
    public @NotNull String getName() {
        return advanced_Hitboxes$name;
    }

    @Override
    public RaycastResult raycast(Vec3 origin, Vec3 direction) {
        return new RaycastResult(this.clip(origin, origin.add(direction.scale(100))), -1);
    }

    @Override
    public Optional<Vec3> linetest(Vec3 start, Vec3 end) {
        return this.clip(start, end);
    }

    @Override
    public boolean intersects(OBB obb) {
        return new OBB(null, (AABB)(Object)this).intersects(obb);
    }

    @Override
    public void setName(String name) {
        this.advanced_Hitboxes$name = name;
    }
}
