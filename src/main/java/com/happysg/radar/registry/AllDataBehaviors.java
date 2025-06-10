package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.block.controller.pitch.PitchLinkBehavior;
import com.happysg.radar.block.controller.track.TrackLinkBehavior;
import com.happysg.radar.block.controller.yaw.YawLinkBehavior;
import com.happysg.radar.block.datalink.DataController;
import com.happysg.radar.block.datalink.DataLinkBehavior;
import com.happysg.radar.block.datalink.DataPeripheral;
import com.happysg.radar.block.monitor.MonitorRadarBehavior;
import com.happysg.radar.block.radar.behavior.RadarScannerLinkBehavior;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AllDataBehaviors {
    // public static final RegistryEntry<RadarScannerLinkBehavior> RADAR = simple("radar", RadarScannerLinkBehavior::new);
    // public static final RegistryEntry<PitchLinkBehavior> PITCH = simple("radar", PitchLinkBehavior::new);
    // public static final RegistryEntry<YawLinkBehavior> YAW = simple("radar", YawLinkBehavior::new);
    // public static final RegistryEntry<TrackLinkBehavior> TRACK = simple("radar", TrackLinkBehavior::new);
    public static final RegistryEntry<RadarScannerLinkBehavior> PLANE_RADAR = simple("radar", RadarScannerLinkBehavior::new);
    public static void registerDefaults() {
        assignBlockEntity(register(CreateRadar.asResource("monitor"), new MonitorRadarBehavior()), ModBlockEntityTypes.MONITOR.get());
        assignBlockEntity(register(CreateRadar.asResource("radar"), new RadarScannerLinkBehavior()), ModBlockEntityTypes.RADAR_BEARING.get());
        assignBlockEntity(register(CreateRadar.asResource("pitch"), new PitchLinkBehavior()), ModBlockEntityTypes.AUTO_PITCH_CONTROLLER.get());
        assignBlockEntity(register(CreateRadar.asResource("yaw"), new YawLinkBehavior()), ModBlockEntityTypes.AUTO_YAW_CONTROLLER.get());
        assignBlockEntity(register(CreateRadar.asResource("track"), new TrackLinkBehavior()), ModBlockEntityTypes.TRACK_CONTROLLER.get());
        assignBlockEntity(register(CreateRadar.asResource("plane_radar"), new RadarScannerLinkBehavior()), ModBlockEntityTypes.PLANE_RADAR.get());
    }

    private static <T extends DisplaySource> RegistryEntry<T> simple(String name, Supplier<T> supplier) {
		return CreateRadar.REGISTRATE.displaySource(name, supplier).register();
	}
}
