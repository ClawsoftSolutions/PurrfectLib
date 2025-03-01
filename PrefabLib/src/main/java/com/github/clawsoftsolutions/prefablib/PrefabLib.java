package com.github.clawsoftsolutions.prefablib;

import com.github.clawsoftsolutions.prefablib.command.PrefabCommand;
import com.github.clawsoftsolutions.prefablib.renderering.PrefabGhostRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("prefablib")
public class PrefabLib {
    public static final Logger LOG = LoggerFactory.getLogger("prefablib");


    public PrefabLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        PrefabCommand.register(event.getDispatcher());
    }
    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class PrefabRenderingHandler {
        @SubscribeEvent
        public static void onRenderWorldLast(RenderLevelStageEvent event) {
            PoseStack stack = event.getPoseStack();
            PrefabGhostRenderer.render(stack);
        }
    }
}

