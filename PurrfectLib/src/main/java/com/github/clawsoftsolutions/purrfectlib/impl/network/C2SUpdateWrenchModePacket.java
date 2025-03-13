package com.github.clawsoftsolutions.purrfectlib.impl.network;

import com.github.clawsoftsolutions.purrfectlib.impl.content.item.DebugWrench;
import com.github.clawsoftsolutions.purrfectlib.impl.content.utils.WrenchMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class C2SUpdateWrenchModePacket {
    private final String mode;

    public C2SUpdateWrenchModePacket(String mode) {
        this.mode = mode;
    }

    public static void encode(C2SUpdateWrenchModePacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.mode);
    }

    public static C2SUpdateWrenchModePacket decode(FriendlyByteBuf buf) {
        return new C2SUpdateWrenchModePacket(buf.readUtf(32767));
    }

    public static void handle(C2SUpdateWrenchModePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof DebugWrench) {
                    DebugWrench.setMode(stack, WrenchMode.valueOf(packet.mode));
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
