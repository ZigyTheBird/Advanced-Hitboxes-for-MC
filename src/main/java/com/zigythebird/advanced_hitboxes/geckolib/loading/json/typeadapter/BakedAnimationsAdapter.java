package com.zigythebird.advanced_hitboxes.geckolib.loading.json.typeadapter;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.geckolib.animation.Animation;
import com.zigythebird.advanced_hitboxes.geckolib.animation.EasingType;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.BoneAnimation;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.Keyframe;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.KeyframeStack;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathParser;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.value.Constant;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedAnimations;
import com.zigythebird.advanced_hitboxes.geckolib.util.CompoundException;
import com.zigythebird.advanced_hitboxes.geckolib.util.JsonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * {@link Gson} {@link JsonDeserializer} for {@link BakedAnimations}.<br>
 * Acts as the deserialization interface for {@code BakedAnimations}
 */
public  class BakedAnimationsAdapter implements JsonDeserializer<BakedAnimations> {
    @Override
    public BakedAnimations deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws RuntimeException {
        JsonObject obj = json.getAsJsonObject();
        Map<String, Animation> animations = new Object2ObjectOpenHashMap<>(obj.size());

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            try {
                animations.put(entry.getKey(), bakeAnimation(entry.getKey(), entry.getValue().getAsJsonObject(), context));
            }
            catch (Exception ex) {
                if (ex instanceof CompoundException compoundEx) {
                    AdvancedHitboxesMod.LOGGER.error(compoundEx.withMessage("Unable to parse animation: " + entry.getKey()).getLocalizedMessage());
                }
                else {
                    AdvancedHitboxesMod.LOGGER.error("Unable to parse animation: " + entry.getKey());
                }

                ex.printStackTrace();
            }
        }

        return new BakedAnimations(animations);
    }

    private Animation bakeAnimation(String name, JsonObject animationObj, JsonDeserializationContext context) throws CompoundException {
        double length = animationObj.has("animation_length") ? GsonHelper.getAsDouble(animationObj, "animation_length") * 20d : -1;
        Animation.LoopType loopType = Animation.LoopType.fromJson(animationObj.get("loop"));
        BoneAnimation[] boneAnimations = bakeBoneAnimations(GsonHelper.getAsJsonObject(animationObj, "bones", new JsonObject()));

        if (length == -1)
            length = calculateAnimationLength(boneAnimations);

        return new Animation(name, length, loopType, boneAnimations);
    }

    private BoneAnimation[] bakeBoneAnimations(JsonObject bonesObj) throws CompoundException {
        BoneAnimation[] animations = new BoneAnimation[bonesObj.size()];
        int index = 0;

        for (Map.Entry<String, JsonElement> entry : bonesObj.entrySet()) {
            JsonObject entryObj = entry.getValue().getAsJsonObject();
            KeyframeStack<Keyframe<MathValue>> scaleFrames = buildKeyframeStack(getTripletObj(entryObj.get("scale")), false);
            KeyframeStack<Keyframe<MathValue>> positionFrames = buildKeyframeStack(getTripletObj(entryObj.get("position")), false);
            KeyframeStack<Keyframe<MathValue>> rotationFrames = buildKeyframeStack(getTripletObj(entryObj.get("rotation")), true);

            animations[index] = new BoneAnimation(entry.getKey(), rotationFrames, positionFrames, scaleFrames);
            index++;
        }

        return animations;
    }

    private static List<Pair<String, JsonElement>> getTripletObj(JsonElement element) {
        if (element == null)
            return List.of();

        if (element instanceof JsonPrimitive primitive) {
            JsonArray array = new JsonArray(3);

            array.add(primitive);
            array.add(primitive);
            array.add(primitive);

            element = array;
        }

        if (element instanceof JsonArray array)
            return ObjectArrayList.of(Pair.of("0", array));

        if (element instanceof JsonObject obj) {
            List<Pair<String, JsonElement>> list = new ObjectArrayList<>();

            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if (entry.getValue() instanceof JsonObject entryObj && !entryObj.has("vector")) {
                    list.add(getTripletObjBedrock(entry.getKey(), entryObj));

                    continue;
                }

                list.add(Pair.of(entry.getKey(), entry.getValue()));
            }

            return list;
        }

        throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + element);
    }

    private static Pair<String, JsonElement> getTripletObjBedrock(String timestamp, JsonObject keyframe) {
        JsonArray keyframeValues = null;

        if (keyframe.has("pre")) {
            JsonElement pre = keyframe.get("pre");
            keyframeValues = pre.isJsonArray() ? pre.getAsJsonArray() : GsonHelper.getAsJsonArray(pre.getAsJsonObject(), "vector");
        }
        else if (keyframe.has("post")) {
            JsonElement post = keyframe.get("post");
            keyframeValues = post.isJsonArray() ? post.getAsJsonArray() : GsonHelper.getAsJsonArray(post.getAsJsonObject(), "vector");
        }

        if (keyframeValues != null)
            return Pair.of(NumberUtils.isCreatable(timestamp) ? timestamp : "0", keyframeValues);

        throw new JsonParseException("Invalid keyframe data - expected array, found " + keyframe);
    }

    private KeyframeStack<Keyframe<MathValue>> buildKeyframeStack(List<Pair<String, JsonElement>> entries, boolean isForRotation) throws CompoundException {
        if (entries.isEmpty())
            return new KeyframeStack<>();

        List<Keyframe<MathValue>> xFrames = new ObjectArrayList<>();
        List<Keyframe<MathValue>> yFrames = new ObjectArrayList<>();
        List<Keyframe<MathValue>> zFrames = new ObjectArrayList<>();

        MathValue xPrev = null;
        MathValue yPrev = null;
        MathValue zPrev = null;
        Pair<String, JsonElement> prevEntry = null;

        for (Pair<String, JsonElement> entry : entries) {
            String key = entry.getFirst();
            JsonElement element = entry.getSecond();

            if (key.equals("easing") || key.equals("easingArgs") || key.equals("lerp_mode"))
                continue;

            double prevTime = prevEntry != null ? Double.parseDouble(prevEntry.getFirst()) : 0;
            double curTime = NumberUtils.isCreatable(key) ? Double.parseDouble(entry.getFirst()) : 0;
            double timeDelta = curTime - prevTime;

            JsonArray keyFrameVector = element instanceof JsonArray array ? array : GsonHelper.getAsJsonArray(element.getAsJsonObject(), "vector");
            MathValue rawXValue = MathParser.parseJson(keyFrameVector.get(0));
            MathValue rawYValue = MathParser.parseJson(keyFrameVector.get(1));
            MathValue rawZValue = MathParser.parseJson(keyFrameVector.get(2));
            MathValue xValue = isForRotation && rawXValue instanceof Constant ? new Constant(Math.toRadians(-rawXValue.get())) : rawXValue;
            MathValue yValue = isForRotation && rawYValue instanceof Constant ? new Constant(Math.toRadians(-rawYValue.get())) : rawYValue;
            MathValue zValue = isForRotation && rawZValue instanceof Constant ? new Constant(Math.toRadians(rawZValue.get())) : rawZValue;

            JsonObject entryObj = element instanceof JsonObject obj ? obj : null;
            EasingType easingType = entryObj != null && entryObj.has("easing") ? EasingType.fromJson(entryObj.get("easing")) : EasingType.LINEAR;
            List<MathValue> easingArgs = entryObj != null && entryObj.has("easingArgs") ?
                    JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "easingArgs"), ele -> new Constant(ele.getAsDouble())) :
                    new ObjectArrayList<>();

            xFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? xValue : xPrev, xValue, easingType, easingArgs));
            yFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? yValue : yPrev, yValue, easingType, easingArgs));
            zFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? zValue : zPrev, zValue, easingType, easingArgs));

            xPrev = xValue;
            yPrev = yValue;
            zPrev = zValue;
            prevEntry = entry;
        }

        return new KeyframeStack<>(xFrames, yFrames, zFrames);
    }

    private static double calculateAnimationLength(BoneAnimation[] boneAnimations) {
        double length = 0;

        for (BoneAnimation animation : boneAnimations) {
            length = Math.max(length, animation.rotationKeyFrames().getLastKeyframeTime());
            length = Math.max(length, animation.positionKeyFrames().getLastKeyframeTime());
            length = Math.max(length, animation.scaleKeyFrames().getLastKeyframeTime());
        }

        return length == 0 ? Double.MAX_VALUE : length;
    }
}
