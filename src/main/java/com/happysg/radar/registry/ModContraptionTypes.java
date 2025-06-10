package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.block.radar.bearing.RadarContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import rbasamoyai.createbigcannons.CreateBigCannons;

public class ModContraptionTypes {
    public static final Holder.Reference<ContraptionType> RADAR_BEARING =
            register("radar_bearing", RadarContraption::new);

    private static Holder.Reference<ContraptionType> register(String name, Supplier<? extends Contraption> factory) {
        ContraptionType type = new ContraptionType(factory);

        return Registry.registerForHolder(CreateBuiltInRegistries.CONTRAPTION_TYPE, CreateBigCannons.resource(name), type);
    }
}
