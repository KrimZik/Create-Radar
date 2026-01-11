package com.happysg.radar.networking.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoolListPacket {

    private static final int EXPECTED_FLAG_COUNT = 6;

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
        buf.writeVarInt(pkt.flags.length);
        for (boolean b : pkt.flags) {
            buf.writeBoolean(b);
        }
    }

    public static BoolListPacket decode(FriendlyByteBuf buf) {
        boolean mainHand = buf.readBoolean();
        String key = buf.readUtf(32767);

        int len = buf.readVarInt();
        boolean[] flags = new boolean[len];
        for (int i = 0; i < len; i++) {
            flags[i] = buf.readBoolean();
        }

        return new BoolListPacket(mainHand, flags, key);
    }

    /* ------------------ HANDLER ------------------ */

    public static void handle(BoolListPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {

            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            // Validate packet
            if (pkt.flags.length != EXPECTED_FLAG_COUNT) return;
            if (pkt.nbtKey == null || pkt.nbtKey.isEmpty()) return;

            InteractionHand hand = pkt.mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) return;

            // if (stack.getItem() != ModItems.RADAR_ITEM.get()) return;

            byte[] data = new byte[EXPECTED_FLAG_COUNT];
            for (int i = 0; i < EXPECTED_FLAG_COUNT; i++) {
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
