package com.zigythebird.advanced_hitboxes.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.instance.AdvancedHitboxInstanceCache;
import com.zigythebird.advanced_hitboxes.geckolib.model.DefaultedEntityHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.util.HitboxModelUtil;
import com.zigythebird.advanced_hitboxes.mixin.accessors.EntityAccessor;
import com.zigythebird.advanced_hitboxes.network.MessageControlCar;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class TransformerEntity extends Entity implements AdvancedHitboxEntity, GeoEntity {
    private static final HitboxModel<ExampleAdvancedHitboxEntity> hitboxModel = new DefaultedEntityHitboxModel<>(AdvancedHitboxesMod.id("transformer"));

    private float wheelRotation;
    private boolean peaked;
    private float peakedYRot;

    @OnlyIn(Dist.CLIENT)
    private boolean collidedLastTick;

    private int steps;
    private double clientX;
    private double clientY;
    private double clientZ;
    private double clientYaw;
    private double clientPitch;

    protected float deltaRotation;

    public float getMaxSpeed() {
        return 1;
    }

    public float getMaxReverseSpeed() {
        return 0.5F;
    }

    public float getAcceleration() {
        return 0.5F;
    }

    public float getMaxRotationSpeed() {
        return 10;
    }

    public float getMinRotationSpeed() {
        return 3;
    }

    public float getRollResistance() {
        return 0;
    }

    public float getRotationModifier() {
        return 0.5F;
    }

    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(TransformerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> STARTED = SynchedEntityData.defineId(TransformerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FORWARD = SynchedEntityData.defineId(TransformerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BACKWARD = SynchedEntityData.defineId(TransformerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEFT = SynchedEntityData.defineId(TransformerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RIGHT = SynchedEntityData.defineId(TransformerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(
            Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1)
    );

    public TransformerEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STARTED, false);
        builder.define(SPEED, 0F);
        builder.define(FORWARD, false);
        builder.define(BACKWARD, false);
        builder.define(LEFT, false);
        builder.define(RIGHT, false);
    }

    @Override
    protected double getDefaultGravity() {
        return this.isInWater() ? 0.005 : 0.04;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return Boat.canVehicleCollide(this, entity);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public float maxUpStep() {
        return 0.0F;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        return this.position().add(0, 0.1, 0);
    }

    @Override
    public void tick() {
        if (!this.firstTick && (this.getControllingPassenger() == null || this.getControllingPassenger().isRemoved())) {
            this.discard();
            return;
        }

        if (!level().isClientSide) {
            this.xo = getX();
            this.yo = getY();
            this.zo = getZ();
        }

        super.tick();
        tickLerp();

        updateGravity();
        controlCar();

        Vec3 expectedPosition = this.position().add(getDeltaMovement());

        move(MoverType.SELF, getDeltaMovement().scale(0.25));
        move(MoverType.SELF, getDeltaMovement().scale(0.25));
        move(MoverType.SELF, getDeltaMovement().scale(0.25));
        move(MoverType.SELF, getDeltaMovement().scale(0.25));

        Vec3 position = this.position();
        float rotXOld = this.getXRot();
        float rotYOld = this.getYRot();

        if (this.getXRot() == 0 && this.peakedYRot != this.getYRot()) peaked = false;

        if (!peaked && (position.x() != expectedPosition.x() || position.z() != expectedPosition.z())) {
            if (!(Mth.abs(this.getXRot()) > 20)) {
                if (isForward()) {
                    this.setXRot(Mth.wrapDegrees(this.getXRot() + 4));
                } else if (isBackward()) {
                    this.setXRot(Mth.wrapDegrees(this.getXRot() - 4));
                }
            }
            else {
                peaked = true;
                peakedYRot = this.getYRot();
            }
        }
        else if (Math.abs(this.getXRot()) > 0) {
            this.setXRot(this.getXRot() + Math.signum(this.getXRot()) * -4);
            if (this.getXRot() == 0 && peakedYRot != this.getYRot()) peaked = false;
        }

        controlCarRotation();

        if (this.getYRot() != rotYOld || this.getXRot() != rotXOld) {
            HitboxUtils.tickAndUpdateHitboxesForEntity(this);

            Vec3 vec3 = ((EntityAccessor) this).callCollide(Vec3.ZERO);
            double d0 = vec3.lengthSqr();
            if (d0 > 1.0E-7) {
                this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
            }
        }



        updateWheelRotation();
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.steps = 0;
        }

        if (this.steps > 0) {
            double d0 = getX() + (clientX - getX()) / (double) steps;
            double d1 = getY() + (clientY - getY()) / (double) steps;
            double d2 = getZ() + (clientZ - getZ()) / (double) steps;
            double d3 = Mth.wrapDegrees(clientYaw - (double) getYRot());
            setYRot((float) ((double) getYRot() + d3 / (double) steps));
            setXRot((float) ((double) getXRot() + (clientPitch - (double) getXRot()) / (double) steps));
            --steps;
            setPos(d0, d1, d2);
            setRot(getYRot(), getXRot());
        }
    }

    private void updateGravity() {
        if (isNoGravity()) {
            setDeltaMovement(getDeltaMovement().x, 0D, getDeltaMovement().z);
            return;
        }
        setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y - 0.2D, getDeltaMovement().z);
    }

    public void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    public float getWheelRotationAmount() {
        return 120F * getSpeed();
    }

    public void updateWheelRotation() {
        wheelRotation += getWheelRotationAmount();
    }

    private void controlCar() {
        if (!isVehicle()) {
            setForward(false);
            setBackward(false);
            setLeft(false);
            setRight(false);
        }

        //Todo: Make this change depending on the type of block being driven on
        float modifier = 1;

        float maxSp = getMaxSpeed() * modifier;
        float maxBackSp = getMaxReverseSpeed() * modifier;

        float speed = Math.max(0, getSpeed() - getRollResistance());

        if (isForward()) {
            if (speed <= maxSp) {
                speed = Math.min(speed + getAcceleration(), maxSp);
            }
        }

        if (isBackward()) {
            if (speed >= -maxBackSp) {
                speed = Math.max(speed - getAcceleration(), -maxBackSp);
            }
        }

        setSpeed(speed);

        if (horizontalCollision) {
            if (level().isClientSide && !collidedLastTick) {
//                onCollision(speed);
                collidedLastTick = true;
            }
        } else {
            setDeltaMovement(calculateMotionX(getSpeed(), getYRot()), getDeltaMovement().y, calculateMotionZ(getSpeed(), getYRot()));
            if (level().isClientSide) {
                collidedLastTick = false;
            }
        }
    }

    private void controlCarRotation() {
        float speed = getSpeed();
        float rotationSpeed = 0;
        if (Math.abs(speed) > 0.02F) {
            rotationSpeed = Mth.abs(getRotationModifier() / (float) Math.pow(speed, 2));

            rotationSpeed = Mth.clamp(rotationSpeed, getMinRotationSpeed(), getMaxRotationSpeed());
        }

        deltaRotation = 0;

        if (speed < 0) {
            rotationSpeed = -rotationSpeed;
        }

        if (isLeft()) {
            deltaRotation -= rotationSpeed;
        }
        if (isRight()) {
            deltaRotation += rotationSpeed;
        }

        setYRot(getYRot() + deltaRotation);
        float delta = Math.abs(getYRot() - yRotO);
        while (getYRot() > 180F) {
            setYRot(getYRot() - 360F);
            yRotO = getYRot() - delta;
        }
        while (getYRot() <= -180F) {
            setYRot(getYRot() + 360F);
            yRotO = delta + getYRot();
        }
    }

    public static double calculateMotionX(float speed, float rotationYaw) {
        return Mth.sin(-rotationYaw * 0.017453292F) * speed;
    }

    public static double calculateMotionZ(float speed, float rotationYaw) {
        return Mth.cos(rotationYaw * 0.017453292F) * speed;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements) {
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
        this.clientYaw = yaw;
        this.clientPitch = pitch;
        this.steps = 10;
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        this.discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getControllingPassenger() != null) {
            return this.getControllingPassenger().hurt(source, amount);
        }
        return false;
    }

    @Override
    public HitboxModel getHitboxModel() {
        return hitboxModel;
    }

    private final AdvancedHitboxInstanceCache hitbox_cache = HitboxModelUtil.createInstanceCache(this);

    @Override
    public AdvancedHitboxInstanceCache getHitboxInstanceCache() {
        return hitbox_cache;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        try {
            return (LivingEntity) this.getPassengers().get(0);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public boolean isPickable() {
        return isAlive();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    @Override
    public boolean useAdvancedHitboxesForCollision() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setForward(boolean forward) {
        entityData.set(FORWARD, forward);
    }

    public boolean isForward() {
        if (this.getControllingPassenger() == null) {
            return false;
        }
        return entityData.get(FORWARD);
    }

    public void setBackward(boolean backward) {
        entityData.set(BACKWARD, backward);
    }

    public boolean isBackward() {
        if (this.getControllingPassenger() == null) {
            return false;
        }
        return entityData.get(BACKWARD);
    }

    public void setLeft(boolean left) {
        entityData.set(LEFT, left);
    }

    public boolean isLeft() {
        return entityData.get(LEFT);
    }

    public void setRight(boolean right) {
        entityData.set(RIGHT, right);
    }

    public boolean isRight() {
        return entityData.get(RIGHT);
    }

    public void updateControls(boolean forward, boolean backward, boolean left, boolean right, Player player) {
        boolean needsUpdate = false;

        if (isForward() != forward) {
            setForward(forward);
            needsUpdate = true;
        }

        if (isBackward() != backward) {
            setBackward(backward);
            needsUpdate = true;
        }

        if (isLeft() != left) {
            setLeft(left);
            needsUpdate = true;
        }

        if (isRight() != right) {
            setRight(right);
            needsUpdate = true;
        }
        if (level().isClientSide && needsUpdate) {
            PacketDistributor.sendToServer(new MessageControlCar(forward, backward, left, right, player));
        }
    }

    @Override
    public void applyTransformationsToBone(HitboxGeoBone bone) {
        if (Objects.equals(bone.getName(), "root")) {
            bone.setRotY(Mth.DEG_TO_RAD * -this.getYRot());
            bone.setRotX(Mth.DEG_TO_RAD * this.getXRot());
        }
    }
}
