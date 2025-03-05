package com.github.clawsoftsolutions.purrfectlib.prefab;

import com.github.clawsoftsolutions.purrfectlib.prefab.command.PrefabCommand;
import com.github.clawsoftsolutions.purrfectlib.prefab.renderering.PrefabRenderer;
import com.github.clawsoftsolutions.purrfectlib.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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

@Mod("prefab")
public class Prefab {
    public static final Logger LOG = LoggerFactory.getLogger("prefab");


    public Prefab() {
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
            PrefabRenderer.render(event.getPoseStack(), SuperRenderTypeBuffer.getInstance());
        }

    }
}

