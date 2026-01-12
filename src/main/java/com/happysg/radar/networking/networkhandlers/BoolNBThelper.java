package com.happysg.radar.networking.networkhandlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.BitSet;

public final class BoolNBThelper {
    private BoolNBThelper() {}

    /** Save flags as byte[] */
    public static void saveBooleansAsBytes(ItemStack stack, boolean[] flags, String key) {
        if (stack == null || flags == null || key == null) return;

        CompoundTag tag = stack.getOrCreateTag();
        byte[] arr = new byte[flags.length];
        for (int i = 0; i < flags.length; i++) arr[i] = (byte) (flags[i] ? 1 : 0);
        tag.putByteArray(key, arr);
    }

    /** Load flags from byte[] */
    public static boolean[] loadBooleansFromBytes(ItemStack stack, String key, int expectedLength) {
        boolean[] res = new boolean[Math.max(0, expectedLength)];
        if (stack == null || key == null || expectedLength <= 0) return res;

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(key)) return res;

        try {
            byte[] arr = tag.getByteArray(key);
            int len = Math.min(arr.length, res.length);
            for (int i = 0; i < len; i++) res[i] = arr[i] != 0;
        } catch (Throwable t) {
            // defensive: return defaults
        }
        return res;
    }
    

    /** Save flags as packed bits (BitSet) */
    public static void saveBooleansAsBits(ItemStack stack, boolean[] flags, String key) {
        if (stack == null || flags == null || key == null) return;

        CompoundTag tag = stack.getOrCreateTag();
        BitSet bitSet = new BitSet(flags.length);
        for (int i = 0; i < flags.length; i++) {
            if (flags[i]) bitSet.set(i);
        }
        tag.putByteArray(key, bitSet.toByteArray());
    }

    /** Load flags as packed bits (BitSet) */
    public static boolean[] loadBooleansFromBits(ItemStack stack, String key, int expectedLength) {
        boolean[] res = new boolean[Math.max(0, expectedLength)];
        if (stack == null || key == null || expectedLength <= 0) return res;

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(key)) return res;

        try {
            byte[] arr = tag.getByteArray(key);
            BitSet bitSet = BitSet.valueOf(arr);
            for (int i = 0; i < res.length; i++) {
                res[i] = bitSet.get(i);
            }
        } catch (Throwable t) {
            // return defaults
        }
        return res;
    }

    /**
     * Loads booleans flexibly:
     * - If the stored byte array is short (< expectedLength / 2), assume legacy byte-per-boolean format
     * - Otherwise, assume packed BitSet
     */
    public static boolean[] loadBooleansFlexible(ItemStack stack, String key, int expectedLength) {
        if (stack == null || key == null || expectedLength <= 0) return new boolean[0];
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(key)) return new boolean[expectedLength];

        byte[] arr = tag.getByteArray(key);
        if (arr.length >= expectedLength / 2) {
            boolean[] res = new boolean[expectedLength];
            int len = Math.min(arr.length, res.length);
            for (int i = 0; i < len; i++) res[i] = arr[i] != 0;
            return res;
        } else {
            BitSet bitSet = BitSet.valueOf(arr);
            boolean[] res = new boolean[expectedLength];
            for (int i = 0; i < expectedLength; i++) res[i] = bitSet.get(i);
            return res;
        }
    }

    /** Load into existing dest array (legacy or packed) */
    public static void loadBooleansInto(ItemStack stack, String key, boolean[] dest) {
        if (dest == null || stack == null || key == null) return;
        boolean[] tmp = loadBooleansFlexible(stack, key, dest.length);
        System.arraycopy(tmp, 0, dest, 0, Math.min(tmp.length, dest.length));
    }
}
