package com.zigythebird.advanced_hitboxes.geckolib.loading.json.typeadapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.*;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedAnimations;

public class GeoAdapter {
    public static final Gson GEO_GSON = new GsonBuilder().setLenient()
            .registerTypeAdapter(Bone.class, Bone.deserializer())
            .registerTypeAdapter(Cube.class, Cube.deserializer())
            .registerTypeAdapter(FaceUV.class, FaceUV.deserializer())
            .registerTypeAdapter(LocatorClass.class, LocatorClass.deserializer())
            .registerTypeAdapter(LocatorValue.class, LocatorValue.deserializer())
            .registerTypeAdapter(MinecraftGeometry.class, MinecraftGeometry.deserializer())
            .registerTypeAdapter(Model.class, Model.deserializer())
            .registerTypeAdapter(ModelProperties.class, ModelProperties.deserializer())
            .registerTypeAdapter(PolyMesh.class, PolyMesh.deserializer())
            .registerTypeAdapter(PolysUnion.class, PolysUnion.deserializer())
            .registerTypeAdapter(TextureMesh.class, TextureMesh.deserializer())
            .registerTypeAdapter(UVFaces.class, UVFaces.deserializer())
            .registerTypeAdapter(UVUnion.class, UVUnion.deserializer())
            .registerTypeAdapter(BakedAnimations.class, new BakedAnimationsAdapter())
            .create();
}
