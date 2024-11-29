package com.zigythebird.advanced_hitboxes.compat;

import com.zigythebird.advanced_hitboxes.geckolib.animation.PlayerRawAnimation;
import com.zigythebird.playeranimatorapi.data.PlayerParts;

public class PAAPIRawAnimation extends PlayerRawAnimation {
    private final PlayerParts parts;

    public PAAPIRawAnimation(String name, double speed, int priority, PlayerParts parts) {
        super(name, speed, priority);
        this.parts = parts;
    }

    public PlayerParts getParts() {return parts;}
}
