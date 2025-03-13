package com.github.clawsoftsolutions.testing;

import com.github.clawsoftsolutions.purrfectlib.api.debug.DebugDataProvider;
import com.github.clawsoftsolutions.purrfectlib.api.debug.DebugDataRegistrar;
import com.github.clawsoftsolutions.purrfectlib.prefab.command.PrefabCommand;
import com.github.clawsoftsolutions.purrfectlib.prefab.renderering.PrefabRenderer;
import com.github.clawsoftsolutions.purrfectlib.render.SuperRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod("prefab")
public class Testing {
    private boolean registrationFired = false;
    public static final Logger LOG = LoggerFactory.getLogger("prefab");


    public Testing() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!registrationFired && event.phase == TickEvent.Phase.START) {
            registrationFired = true;
            DebugDataRegistrar.fireDebugDataRegistration(List.of(new SomeOtherModDebugProvider()));
        }
    }


    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        PrefabCommand.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class PrefabRenderingHandler {
        @SubscribeEvent
        public static void onRenderWorldLast(RenderLevelStageEvent event) {
            PrefabRenderer.render(event.getPoseStack(), SuperRenderTypeBuffer.getInstance());
        }

    }
}

