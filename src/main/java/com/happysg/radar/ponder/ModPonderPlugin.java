package com.happysg.radar.ponder;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.registry.ModPonderIndex;
import com.happysg.radar.registry.ModPonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public class ModPonderPlugin implements PonderPlugin {
	@Override
	public @NotNull String getModId() {
		return CreateRadar.MODID;
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ModPonderIndex.register(helper);
	}

	@Override
	public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
		ModPonderTags.register(helper);
	}


}