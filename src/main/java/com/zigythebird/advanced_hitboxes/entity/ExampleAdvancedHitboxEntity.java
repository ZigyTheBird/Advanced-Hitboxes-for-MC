package com.zigythebird.advanced_hitboxes.entity;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.instance.AdvancedHitboxInstanceCache;
import com.zigythebird.advanced_hitboxes.geckolib.model.DefaultedEntityHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.util.HitboxModelUtil;
import com.zigythebird.advanced_hitboxes.mixin.accessors.EntityAccessor;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class ExampleAdvancedHitboxEntity extends Mob implements AdvancedHitboxEntity {

    public ExampleAdvancedHitboxEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    private static final HitboxModel<ExampleAdvancedHitboxEntity> hitboxModel = new DefaultedEntityHitboxModel<>(AdvancedHitboxesMod.id("hitbox_test"));

    @Override
    public HitboxModel<ExampleAdvancedHitboxEntity> getHitboxModel() {
        return hitboxModel;
    }

    private final AdvancedHitboxInstanceCache hitbox_cache = HitboxModelUtil.createInstanceCache(this);

    @Override
    public AdvancedHitboxInstanceCache getHitboxInstanceCache() {
        return hitbox_cache;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        this.yRotO = this.getYRot();
        this.setYRot(Mth.wrapDegrees(this.getYRot() + 10));
        HitboxUtils.tickAndUpdateHitboxesForEntity(this);
        this.setPos(this.position().add(((EntityAccessor)this).callCollide(Vec3.ZERO)));
        this.markHurt();
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean useAdvancedHitboxesForCollision() {
        return true;
    }

    @Override
    public void applyTransformationsToBone(HitboxGeoBone bone) {
        if (Objects.equals(bone.getName(), "bone")) {
            bone.setRotY(Mth.DEG_TO_RAD * Mth.wrapDegrees(this.getYRot()));
        }
    }
}
