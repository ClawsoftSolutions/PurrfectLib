package com.github.clawsoftsolutions.testing;

import com.github.clawsoftsolutions.purrfectlib.api.debug.DebugDataProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class SomeOtherModDebugProvider implements DebugDataProvider {
    @Override
    public void renderDebugData(Minecraft mc, Font font, PoseStack poseStack, int x, int y) {
        font.draw(poseStack, "Some other mod debug data", x, y, 0xFFFFFF);
    }

    @Override
    public String getModId() {
        return "testing";
    }
}
