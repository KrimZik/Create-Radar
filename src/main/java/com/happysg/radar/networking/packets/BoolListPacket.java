package com.happysg.radar.networking.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.BitSet;
import java.util.function.Supplier;

public class BoolListPacket {


    private static final int MAX_FLAG_COUNT = 256;

    private final boolean mainHand;
    private final boolean[] flags;
    private final String nbtKey;

    public BoolListPacket(boolean mainHand, boolean[] flags, String nbtKey) {
        this.mainHand = mainHand;
        this.flags = flags;
        this.nbtKey = nbtKey;
    }

    /* ------------------ ENCODE / DECODE ------------------ */

    public static void encode(BoolListPacket pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.mainHand);
        buf.writeUtf(pkt.nbtKey);

        final int len = pkt.flags == null ? 0 : pkt.flags.length;
        buf.writeVarInt(len);
        
        if (len == 0) {
            buf.writeVarInt(0);
            return;
        }
        BitSet bits = new BitSet(len);
        for (int i = 0; i < len; i++) {
            if (pkt.flags[i]) bits.set(i);
        }
        byte[] packed = bits.toByteArray();
        buf.writeVarInt(packed.length);
        buf.writeBytes(packed);
    }

    public static BoolListPacket decode(FriendlyByteBuf buf) {
        boolean mainHand = buf.readBoolean();
        String key = buf.readUtf(32767);

        int len = buf.readVarInt();
        if (len < 0) len = 0;
        if (len > MAX_FLAG_COUNT) len = MAX_FLAG_COUNT;

        int packedLen = buf.readVarInt();
        if (packedLen < 0) packedLen = 0;
       
        int maxPackedLen = (len + 7) >>> 3;
        if (packedLen > maxPackedLen) packedLen = maxPackedLen;

        byte[] packed = new byte[packedLen];
        buf.readBytes(packed);
        BitSet bits = BitSet.valueOf(packed);

        boolean[] flags = new boolean[len];
        for (int i = 0; i < len; i++) {
            flags[i] = bits.get(i);
        }

        return new BoolListPacket(mainHand, flags, key);
    }
    
    public static void handle(BoolListPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {

            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            // Validate packet
            if (pkt.flags == null) return;
            if (pkt.flags.length <= 0 || pkt.flags.length > MAX_FLAG_COUNT) return;
            if (pkt.nbtKey == null || pkt.nbtKey.isEmpty()) return;

            InteractionHand hand = pkt.mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) return;

            // Server-side storage stays as byte[]
            final int n = pkt.flags.length;
            byte[] data = new byte[n];
            for (int i = 0; i < n; i++) {
                data[i] = (byte) (pkt.flags[i] ? 1 : 0);
            }

            CompoundTag tag = stack.getOrCreateTag();
            tag.putByteArray(pkt.nbtKey, data);

            player.setItemInHand(hand, stack);
            player.inventoryMenu.broadcastChanges();
        });

        ctx.setPacketHandled(true);
    }
}
