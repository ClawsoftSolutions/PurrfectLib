package com.github.clawsoftsolutions.purrfectlib.api.debug;

import com.github.clawsoftsolutions.purrfectlib.impl.content.debug.DebugDataRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;

public class DebugDataRegistrar {
    public static void fireDebugDataRegistration(List<DebugDataProvider> providers) {
        MinecraftForge.EVENT_BUS.post(new DebugDataRegistryEvent(providers));
    }
}
