package com.happysg.radar.block.radar.behavior;

import com.happysg.radar.block.datalink.DataController;
import com.happysg.radar.block.datalink.DataLinkBlockEntity;
import com.happysg.radar.block.datalink.DataLinkContext;
import com.happysg.radar.block.datalink.DataPeripheral;
import com.happysg.radar.block.datalink.screens.AbstractDataLinkScreen;
import com.happysg.radar.block.datalink.screens.RadarFilterScreen;
import com.happysg.radar.block.monitor.MonitorBlockEntity;
import com.happysg.radar.block.monitor.MonitorFilter;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class RadarScannerLinkBehavior extends NumericSingleLineDisplaySource {

    public void transferData(@NotNull DataLinkContext context, @NotNull DataController activeTarget) {
        if (context.level().isClientSide())
            return;
        if (context.getSourceBlockEntity() instanceof SmartBlockEntity smartBlockEntity) {
            RadarScanningBlockBehavior behavior = smartBlockEntity.getBehaviour(RadarScanningBlockBehavior.TYPE);
            if (behavior != null && context.getMonitorBlockEntity() != null) {
                MonitorBlockEntity monitorBlockEntity = context.getMonitorBlockEntity();
                    monitorBlockEntity.getController().setRadarPos(context.getSourcePos());
                    if (context.sourceConfig().contains("filter")) {
                        monitorBlockEntity.setFilter(MonitorFilter.fromTag(context.sourceConfig().getCompound("filter")));
                    }
                    monitorBlockEntity.getController().updateCache();
            }
        }
    }

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        return Component.literal("rizz");
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
