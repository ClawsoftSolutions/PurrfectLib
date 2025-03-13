package com.github.clawsoftsolutions.purrfectlib.impl.network;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("purrfectlib", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void registerPackets() {
        INSTANCE.registerMessage(packetId++,
                C2SUpdateWrenchModePacket.class,
                C2SUpdateWrenchModePacket::encode,
                C2SUpdateWrenchModePacket::decode,
                C2SUpdateWrenchModePacket::handle);
    }
}
