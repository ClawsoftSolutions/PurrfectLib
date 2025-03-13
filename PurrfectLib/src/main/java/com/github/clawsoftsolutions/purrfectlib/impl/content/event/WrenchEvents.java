package com.github.clawsoftsolutions.purrfectlib.impl.content.event;

import com.github.clawsoftsolutions.purrfectlib.impl.content.item.DebugWrench;
import com.github.clawsoftsolutions.purrfectlib.impl.content.utils.WrenchMode;
import com.github.clawsoftsolutions.purrfectlib.impl.network.C2SUpdateWrenchModePacket;
import com.github.clawsoftsolutions.purrfectlib.impl.network.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WrenchEvents {

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || !player.isShiftKeyDown()) return;

        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof DebugWrench)) return;

        event.setCanceled(true);

        WrenchMode currentMode = DebugWrench.getMode(heldItem);
        boolean scrollingUp = event.getScrollDelta() > 0;
        WrenchMode newMode = scrollingUp ? WrenchMode.cycleForward(currentMode) : WrenchMode.cycleBackward(currentMode);

        DebugWrench.setMode(heldItem, newMode);
        NetworkHandler.INSTANCE.sendToServer(new C2SUpdateWrenchModePacket(newMode.name()));
        player.displayClientMessage(Component.literal("Wrench Mode: " + newMode).withStyle(ChatFormatting.AQUA), true);
    }
}
