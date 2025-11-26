package com.zigythebird.advanced_hitboxes.geckolib.cache.object;

import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HitboxBone extends PlayerAnimBone {
    private final Vec3 pivot;
    private final HitboxBone parent;
    private final List<HitboxBone> children = new ArrayList<>();
    private final List<GeoCube> cubes = new ArrayList<>();
    public final String hitboxType;
    private final Double inflate;

    public HitboxBone(String name, Vec3 pivot, HitboxBone parent, String hitboxType, Double inflate) {
        super(name);
        this.pivot = pivot;
        this.parent = parent;
        this.hitboxType = hitboxType;
        this.inflate = inflate;
    }

    public List<HitboxBone> getChildBones() {
        return this.children;
    }

    public List<GeoCube> getCubes() {
        return this.cubes;
    }

    public Vec3 getPivot() {
        return this.pivot;
    }

    public double getPivotX() {
        return this.pivot.x;
    }

    public double getPivotY() {
        return this.pivot.y;
    }

    public double getPivotZ() {
        return this.pivot.z;
    }

    public HitboxBone getParent() {
        return this.parent;
    }

    public Double getInflate() {
        return this.inflate;
    }
}
