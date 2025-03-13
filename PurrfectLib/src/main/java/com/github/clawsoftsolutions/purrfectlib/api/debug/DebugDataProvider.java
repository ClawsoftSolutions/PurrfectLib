package com.github.clawsoftsolutions.purrfectlib.api.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public interface DebugDataProvider {
    void renderDebugData(Minecraft mc, Font font, PoseStack poseStack, int x, int y);
    String getModId();
}
