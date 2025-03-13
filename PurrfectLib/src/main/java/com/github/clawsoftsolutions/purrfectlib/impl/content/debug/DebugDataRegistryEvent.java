package com.github.clawsoftsolutions.purrfectlib.impl.content.debug;

import com.github.clawsoftsolutions.purrfectlib.api.debug.DebugDataProvider;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class DebugDataRegistryEvent extends Event {
    private final List<DebugDataProvider> debugDataProviders;

    public DebugDataRegistryEvent(List<DebugDataProvider> debugDataProviders) {
        this.debugDataProviders = debugDataProviders;
    }

    public List<DebugDataProvider> getDebugDataProviders() {
        return debugDataProviders;
    }
}
