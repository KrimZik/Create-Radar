package com.happysg.radar.networking.packets;

import com.happysg.radar.block.controller.id.IDManager;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class IDRecordPacket extends SimplePacketBase {

    private static final int MAX_LEN = 128;

    private final String shipSlug;
    private final String secretID;
    private final String name;

    public IDRecordPacket(String shipSlug, String secretID, String name) {
        this.shipSlug = shipSlug;
        this.secretID = secretID;
        this.name = name;
    }

    public IDRecordPacket(FriendlyByteBuf buffer) {
        this.shipSlug = buffer.readUtf(MAX_LEN);
        this.secretID = buffer.readUtf(MAX_LEN);
        this.name = buffer.readUtf(MAX_LEN);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(shipSlug);
        buffer.writeUtf(secretID);
        buffer.writeUtf(name);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {

        // Enforce server-side handling
        if (!context.getDirection().getReceptionSide().isServer()) {
            return true;
        }

        context.enqueueWork(() -> {
            if (shipSlug.isEmpty() || secretID.isEmpty()) return;
            IDManager.addIDRecord(shipSlug, secretID, name);
        });

        return true;
    }
}
