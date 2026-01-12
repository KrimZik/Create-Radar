package com.happysg.radar.networking.networkhandlers;

import net.minecraft.nbt.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ListNBTHandler {

    private static final String ENTRIES_KEY = "EntriesList";
    private static final String FRIEND_OR_FOE_KEY = "FriendOrFoeList";
    private static final String SINGLE_KEY = "IDSTRING";

    // Save entries and friend/foe flags
    public static void saveToHeldItem(Player player, List<String> entries, List<Boolean> friendOrFoe) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return;

        CompoundTag tag = stack.getOrCreateTag();

        // Save string entries
        ListTag entriesTag = new ListTag();
        for (String s : entries) {
            entriesTag.add(StringTag.valueOf(s));
        }
        tag.put(ENTRIES_KEY, entriesTag);

        // Save friend/foe booleans as packed bits
        boolean[] flags = new boolean[friendOrFoe.size()];
        for (int i = 0; i < friendOrFoe.size(); i++) {
            flags[i] = friendOrFoe.get(i);
        }
        BoolNBThelper.saveBooleansAsBits(stack, flags, FRIEND_OR_FOE_KEY);
    }

    // Load entries and friend/foe flags
    public static LoadedLists loadFromHeldItem(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !stack.hasTag()) return new LoadedLists();

        CompoundTag tag = stack.getTag();

        ListTag entriesTag = tag.getList(ENTRIES_KEY, Tag.TAG_STRING);
        LoadedLists loaded = new LoadedLists(entriesTag.size());

        // Load string entries
        for (int i = 0; i < entriesTag.size(); i++) {
            loaded.entries.add(entriesTag.getString(i));
        }

        // Load friend/foe booleans
        boolean[] flags = BoolNBThelper.loadBooleansFromBits(stack, FRIEND_OR_FOE_KEY, entriesTag.size());
        for (boolean b : flags) loaded.friendOrFoe.add(b);

        return loaded;
    }

    // Save a single string to the held item
    public static void saveStringToHeldItem(Player player, String value) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return;
        stack.getOrCreateTag().putString(SINGLE_KEY, value);
    }

    // Load a single string from the held item
    public static String loadStringFromHeldItem(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return "";
        CompoundTag tag = stack.getTag();
        if (tag == null) return "";
        return tag.getString(SINGLE_KEY);
    }

    // Loaded ID container
    public static class LoadedId {
        public static String storedID = "";
    }

    // Loaded lists container with pre-sized ArrayLists
    public static class LoadedLists {
        public final List<String> entries;
        public final List<Boolean> friendOrFoe;

        public LoadedLists(int size) {
            this.entries = new ArrayList<>(size);
            this.friendOrFoe = new ArrayList<>(size);
        }

        public LoadedLists() {
            this(0);
        }
    }
}
