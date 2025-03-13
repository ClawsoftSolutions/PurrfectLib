package com.github.clawsoftsolutions.purrfectlib;

import com.github.clawsoftsolutions.purrfectlib.impl.content.command.BaseCommands;
import com.github.clawsoftsolutions.purrfectlib.impl.content.debug.DebugDataRegistry;
import com.github.clawsoftsolutions.purrfectlib.impl.content.event.WrenchEvents;
import com.github.clawsoftsolutions.purrfectlib.impl.content.item.DebugWrench;
import com.github.clawsoftsolutions.purrfectlib.impl.content.overlay.DebugOverlay;
import com.github.clawsoftsolutions.purrfectlib.impl.network.NetworkHandler;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


@Mod("purrfectlib")
public class PurrfectLib {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "purrfectlib");

    public static final RegistryObject<Item> DEBUG_HAMMER = ITEMS.register("debug_wrench",
            () -> new DebugWrench());

    public PurrfectLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        NetworkHandler.registerPackets();

        MinecraftForge.EVENT_BUS.register(DebugDataRegistry.class);
        MinecraftForge.EVENT_BUS.register(DebugOverlay.class);
        MinecraftForge.EVENT_BUS.register(WrenchEvents.class);
        MinecraftForge.EVENT_BUS.register(this);
        ITEMS.register(modEventBus);
    }




    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BaseCommands.register(event.getDispatcher());
    }

}
