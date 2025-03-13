package com.github.clawsoftsolutions.purrfectlib.impl.content.debug;

import com.github.clawsoftsolutions.purrfectlib.api.debug.DebugDataProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;

public class DebugDataRegistry {
    private static final List<DebugDataProvider> debugDataProviders = new ArrayList<>();

    public static void registerDebugDataProvider(DebugDataProvider provider) {
        debugDataProviders.add(provider);
    }

    public static List<DebugDataProvider> getDebugDataProviders() {
        return debugDataProviders;
    }

    @SubscribeEvent
    public static void onDebugDataRegistration(DebugDataRegistryEvent event) {
        System.out.println("DebugDataRegistry received " + event.getDebugDataProviders().size() + " provider(s).");
        debugDataProviders.addAll(event.getDebugDataProviders());
    }
}
