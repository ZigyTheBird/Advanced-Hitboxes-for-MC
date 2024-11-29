package com.zigythebird.advanced_hitboxes.geckolib.animation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class PlayerRawAnimation extends RawAnimation {
    private final List<Stage> animationList = new ObjectArrayList<>();
    private final double speed;
    private final int priority;

    public PlayerRawAnimation(String name, double speed, int priority) {
        super();
        animationList.add(new Stage(name, Animation.LoopType.DEFAULT));
        this.speed = speed;
        this.priority = priority;
    }

    public List<Stage> getAnimationStages() {
        return this.animationList;
    }

    public int getPriority() {
        return priority;
    }

    public double getSpeed() {
        return speed;
    }
}
