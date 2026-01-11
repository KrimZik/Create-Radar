package com.happysg.radar.networking;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.networking.packets.IDRecordPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {

    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    /** Call during mod setup */
    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(CreateRadar.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        // Only packet we currently use: IDRecordPacket
        INSTANCE.messageBuilder(IDRecordPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(IDRecordPacket::new)
                .encoder(IDRecordPacket::write)
                .consumerMainThread((pkt, ctxSupplier) -> pkt.handle(ctxSupplier.get()))
                .add();
    }

    /* ---------------- Send ---------------- */

    public static void sendToServer(SimplePacketBase message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToPlayer(SimplePacketBase message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToClients(SimplePacketBase message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
