package com.zigythebird.advanced_hitboxes.animation;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class PlayerHitboxAnimationController extends HitboxAnimationController {
    private float torsoBend;
    private float torsoBendYPosMultiplier;
    private float torsoBendZPosMultiplier;
    private int torsoBendSign;

    /**
     * Instantiates a new {@code AnimationController}
     *
     * @param entity           The object that will be animated by this controller
     * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
     */
    public PlayerHitboxAnimationController(AdvancedHitboxEntity entity, AnimationStateHandler animationHandler) {
        super(entity, animationHandler);
    }

    @Override
    public void process(AnimationData state) {
        super.process(state);
        this.torsoBend = bones.get("torso").getBend();
        float absBend = Mth.abs(this.torsoBend);
        if (absBend > 0.001 && (this.currentAnimation != null && this.currentAnimation.animation().data().getNullable(ExtraAnimationData.APPLY_BEND_TO_OTHER_BONES_KEY) == Boolean.TRUE)) {
            this.torsoBendSign = Mth.sign(this.torsoBend);
            this.torsoBendYPosMultiplier = -(1 - Mth.cos(absBend));
            this.torsoBendZPosMultiplier = 1 - Mth.sin(absBend);
        } else this.torsoBendSign = 0;
    }

    @Override
    public PlayerAnimBone get3DTransformRaw(@NotNull PlayerAnimBone bone) {
        bone = super.get3DTransformRaw(bone);
        String name = bone.getName();
        if (this.torsoBendSign != 0 && (bone.getName().equals("head") || bone.getName().equals("right_arm") || bone.getName().equals("left_arm"))) {
            float offset = getBonePosition(name).y() - 18;
            bone.rotX += this.torsoBend;
            bone.positionZ += (offset * this.torsoBendZPosMultiplier - offset) * this.torsoBendSign;
            bone.positionY += offset * this.torsoBendYPosMultiplier;
        }
        return bone;
    }
}
