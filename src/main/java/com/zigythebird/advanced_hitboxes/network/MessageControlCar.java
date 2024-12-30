package com.zigythebird.advanced_hitboxes.network;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.entity.TransformerEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class MessageControlCar implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageControlCar> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AdvancedHitboxesMod.MOD_ID, "control_car"));

    private boolean forward, backward, left, right;
    private UUID uuid;

    public MessageControlCar() {}

    public MessageControlCar(boolean forward, boolean backward, boolean left, boolean right, Player player) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.uuid = player.getUUID();
    }

    public void executeServerSide(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer sender)) {
            return;
        }

        if (!sender.getUUID().equals(uuid)) {
            AdvancedHitboxesMod.LOGGER.error("The UUID of the sender was not equal to the packet UUID");
            return;
        }

        if (!(sender.getVehicle() instanceof TransformerEntity car)) {
            return;
        }

        car.updateControls(forward, backward, left, right, sender);
    }

    public static MessageControlCar fromBytes(RegistryFriendlyByteBuf buf) {
        MessageControlCar packet = new MessageControlCar();
        packet.forward = buf.readBoolean();
        packet.backward = buf.readBoolean();
        packet.left = buf.readBoolean();
        packet.right = buf.readBoolean();
        packet.uuid = buf.readUUID();
        return packet;
    }

    public static void toBytes(RegistryFriendlyByteBuf buf, MessageControlCar packet) {
        buf.writeBoolean(packet.forward);
        buf.writeBoolean(packet.backward);
        buf.writeBoolean(packet.left);
        buf.writeBoolean(packet.right);
        buf.writeUUID(packet.uuid);
    }

    @Override
    public Type<MessageControlCar> type() {
        return TYPE;
    }
}