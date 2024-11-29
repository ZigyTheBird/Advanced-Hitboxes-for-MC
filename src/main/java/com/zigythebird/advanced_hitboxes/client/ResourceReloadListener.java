package com.zigythebird.advanced_hitboxes.client;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.old_fabric_networking.network.ModCodecs;
import com.zigythebird.old_fabric_networking.network.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ResourceReloadListener implements ResourceManagerReloadListener {

    private static List<ResourceLocation> resources = null;

    public static void onPlayerJoin() {
        if (ModList.get().isLoaded("playeranimatorapi") && resources != null) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeCollection(resources, ModCodecs.RESOURCELOCATION);
            NetworkManager.sendToServer(AdvancedHitboxesMod.PLAYER_ANIMATION_RESOURCES_SYNC_PACKET, buf);
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (resources == null) {
            resources = new ArrayList<>();
            for (var resource : resourceManager.listResources("player_animation", location -> location.getPath().endsWith(".json")).entrySet()) {
                resources.add(resource.getKey());
            }
            for (var resource : resourceManager.listResources("player_animations", location -> location.getPath().endsWith(".json")).entrySet()) {
                resources.add(resource.getKey());
            }
        }
    }
}
