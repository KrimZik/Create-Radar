package com.happysg.radar.networking.packets;

import com.happysg.radar.networking.networkhandlers.ListNBTHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SaveListsPacket {
    private static final int MAX_STR_LEN = 128;

    private final List<String> entries;
    private final byte[] friendMask;
    private final String idString;
    private final boolean isIdString;

    /** Constructor for list mode **/
    public SaveListsPacket(List<String> entries, List<Boolean> friendOrFoe) {
        if (entries == null || friendOrFoe == null)
            throw new IllegalArgumentException("Lists cannot be null");
        if (entries.size() != friendOrFoe.size())
            throw new IllegalArgumentException("Lists must match length");

        this.entries = List.copyOf(entries);
        this.friendMask = new byte[(entries.size() + 7) / 8];

        for (int i = 0; i < friendOrFoe.size(); i++) {
            if (friendOrFoe.get(i)) {
                friendMask[i >> 3] |= (byte) (1 << (i & 7));
            }
        }

        this.idString = null;
        this.isIdString = false;
    }

    /** Constructor for single‐string mode **/
    public SaveListsPacket(String idString) {
        if (idString == null || idString.isEmpty())
            throw new IllegalArgumentException("idString cannot be null/empty");

        this.entries = List.of();
        this.friendMask = null;
        this.idString = idString;
        this.isIdString = true;
    }

    /** Encode either the string or the two lists, prefixed by a mode‐flag **/
    public static void encode(SaveListsPacket pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.isIdString);

        if (pkt.isIdString) {
            buf.writeUtf(pkt.idString, MAX_STR_LEN);
            return;
        }

        buf.writeVarInt(pkt.entries.size());
        for (String s : pkt.entries) {
            buf.writeUtf(s, MAX_STR_LEN);
        }

        buf.writeByteArray(pkt.friendMask);
    }

    /** Decode in the same order: read flag, then either string or lists **/
    public static SaveListsPacket decode(FriendlyByteBuf buf) {
        boolean isId = buf.readBoolean();

        if (isId) {
            return new SaveListsPacket(buf.readUtf(MAX_STR_LEN));
        }

        int size = buf.readVarInt();
        List<String> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            entries.add(buf.readUtf(MAX_STR_LEN));
        }

        byte[] mask = buf.readByteArray();
        List<Boolean> flags = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            flags.add((mask[i >> 3] & (1 << (i & 7))) != 0);
        }

        return new SaveListsPacket(entries, flags);
    }

    /** Handle on the server: call the appropriate ListNBTHandler method **/
    public static void handle(SaveListsPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            if (pkt.isIdString) {
                ListNBTHandler.saveStringToHeldItem(player, pkt.idString);
            } else {
                List<Boolean> flags = new ArrayList<>(pkt.entries.size());
                for (int i = 0; i < pkt.entries.size(); i++) {
                    flags.add((pkt.friendMask[i >> 3] & (1 << (i & 7))) != 0);
                }
                ListNBTHandler.saveToHeldItem(player, pkt.entries, flags);
            }

            player.inventoryMenu.broadcastChanges();
        });

        ctx.setPacketHandled(true);
    }
}
