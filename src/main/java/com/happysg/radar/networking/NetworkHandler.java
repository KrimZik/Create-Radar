package com.happysg.radar.networking;

import com.happysg.radar.networking.packets.BoolListPacket;
import com.happysg.radar.networking.packets.SaveListsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static com.happysg.radar.CreateRadar.MODID;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    /** The main channel used for sending packets */
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    /** Call during mod setup to register all packets */
    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                SaveListsPacket.class,
                SaveListsPacket::encode,
                SaveListsPacket::decode,
                SaveListsPacket::handle
        );

        // BoolListPacket
        CHANNEL.registerMessage(
                packetId++,
                BoolListPacket.class,
                BoolListPacket::encode,
                BoolListPacket::decode,
                BoolListPacket::handle
        );
    }
}
