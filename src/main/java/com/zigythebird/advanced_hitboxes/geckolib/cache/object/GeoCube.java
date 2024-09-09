package com.zigythebird.advanced_hitboxes.geckolib.cache.object;

import net.minecraft.world.phys.Vec3;

/**
 * Baked cuboid for a {@link GeoBone}
 */
public record GeoCube(Vec3 origin, Vec3 pivot, Vec3 rotation, Vec3 size) {}
