package com.zigythebird.advanced_hitboxes.entity;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.animation.HitboxAnimManager;
import com.zigythebird.advanced_hitboxes.geckolib.model.DefaultedEntityHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.mixin.accessors.EntityAccessor;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExampleAdvancedHitboxEntity extends Mob implements AdvancedHitboxEntity {
    public float hitboxRot;

    public ExampleAdvancedHitboxEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    private static final HitboxModel<ExampleAdvancedHitboxEntity> hitboxModel = new DefaultedEntityHitboxModel<>(AdvancedHitboxesMod.id("hitbox_test"));

    @Override
    public HitboxModel<ExampleAdvancedHitboxEntity> getHitboxModel() {
        return hitboxModel;
    }

    @Override
    public void tick() {
        this.yBodyRot = 0;
        this.yBodyRotO = 0;
        super.tick();
    }

    @Override
    public void registerHitboxControllers(HitboxAnimManager stack) {
        stack.addAnimLayer(0, new IAnimation() {
            @Override
            public boolean isActive() {
                return true;
            }

            @Override
            public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
                if (Objects.equals(bone.getName(), "bone")) {
                    bone.setRotY(Mth.DEG_TO_RAD * Mth.wrapDegrees(hitboxRot));
                }
                return bone;
            }
        });
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        hitboxRot += 10;
        hitboxRot = Mth.wrapDegrees(hitboxRot);
        HitboxUtils.tickAndUpdateHitboxesForEntity(this, false);
        this.setPos(this.position().add(((EntityAccessor)this).callCollide(Vec3.ZERO)));
        this.markHurt();
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean useAdvancedHitboxesForCollision() {
        return true;
    }
}
