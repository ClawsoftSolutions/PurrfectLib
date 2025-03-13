package com.github.clawsoftsolutions.purrfectlib.impl.content.overlay;

import com.github.clawsoftsolutions.purrfectlib.api.debug.DebugDataProvider;
import com.github.clawsoftsolutions.purrfectlib.impl.content.debug.DebugDataRegistry;
import com.github.clawsoftsolutions.purrfectlib.impl.content.item.DebugWrench;
import com.github.clawsoftsolutions.purrfectlib.impl.content.utils.WrenchMode;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;


public class DebugOverlay {

    private static final Random random = new Random();
    private static final Map<DebugDataProvider, Integer> providerColors = new HashMap<>();

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.screen != null) return;

        PoseStack poseStack = event.getPoseStack();
        Font font = mc.font;

        boolean isDebugActive = false;
        ItemStack wrenchStack = ItemStack.EMPTY;
        if (player.getOffhandItem().getItem() instanceof DebugWrench &&
                DebugWrench.getMode(player.getOffhandItem()) == WrenchMode.DEBUG) {
            wrenchStack = player.getOffhandItem();
            isDebugActive = true;
        }
        else if (player.getMainHandItem().getItem() instanceof DebugWrench &&
                DebugWrench.getMode(player.getMainHandItem()) == WrenchMode.DEBUG) {
            wrenchStack = player.getMainHandItem();
            isDebugActive = true;
        }

        if (!isDebugActive) return;

        int x = 10, y = 10;
        font.draw(poseStack, "Debug Info:", x, y, 0xFFFFFF);
        font.draw(poseStack, "Player Pos: " + player.blockPosition(), x, y + 10, 0xFFFFFF);

        String handInfo = (player.getOffhandItem().getItem() instanceof DebugWrench) ? "Offhand" : "Main Hand";
        font.draw(poseStack, "Wrench in: " + handInfo, x, y + 20, 0xFFFFFF);

        int blockSectionEndY = y + 30;

        if (mc.hitResult != null) {
            if (mc.hitResult.getType() == BlockHitResult.Type.BLOCK) {
                BlockHitResult hitResult = (BlockHitResult) mc.hitResult;
                BlockPos blockPos = hitResult.getBlockPos();
                BlockState blockState = mc.level.getBlockState(blockPos);
                Block block = blockState.getBlock();

                font.draw(poseStack, "Block Type: " + block.getName(), x, blockSectionEndY, 0xFFFFFF);
                font.draw(poseStack, "Block Pos: " + blockPos, x, blockSectionEndY + 10, 0xFFFFFF);

                int propY = blockSectionEndY + 20;
                for (Map.Entry<Property<?>, Comparable<?>> entry : blockState.getValues().entrySet()) {
                    String propLine = entry.getKey().getName() + ": " + entry.getValue();
                    font.draw(poseStack, propLine, x, propY, 0xFFFFFF);
                    propY += font.lineHeight;
                }

                BlockEntity blockEntity = mc.level.getBlockEntity(blockPos);
                if (blockEntity != null) {
                    CompoundTag blockNBT = blockEntity.getPersistentData();
                    font.draw(poseStack, "Block NBT: " + blockNBT, x, propY, 0xFFFFFF);
                    propY += font.lineHeight;
                }

                blockSectionEndY = propY + 10;
            }

            if (mc.hitResult.getType() == BlockHitResult.Type.ENTITY) {
                Entity entity = ((net.minecraft.world.phys.EntityHitResult) mc.hitResult).getEntity();
                if (!entity.isRemoved()) {
                    CompoundTag entityTag = new CompoundTag();
                    entity.saveWithoutId(entityTag);

                    font.draw(poseStack, "Looking at Entity: " + entity.getName().getString(), x, blockSectionEndY, 0xFFFFFF);
                    font.draw(poseStack, "Entity Type: " + entity.getType().toString(), x, blockSectionEndY + 10, 0xFFFFFF);
                    font.draw(poseStack, "Entity Pos: " + entity.blockPosition(), x, blockSectionEndY + 20, 0xFFFFFF);

                    if (entity instanceof ItemEntity itemEntity) {
                        font.draw(poseStack, "Item: " + itemEntity.getItem().getHoverName().getString(), x, blockSectionEndY + 30, 0xFFFFFF);
                    }

                    if (entity.hasCustomName()) {
                        font.draw(poseStack, "Custom Name: " + entity.getCustomName().getString(), x, blockSectionEndY + 40, 0xFFFFFF);
                    }

                    int offsetY = blockSectionEndY + 50;

                    font.draw(poseStack, "NBT Data:", x, offsetY, 0xFFFFFF);
                    List<String> formattedLines = formatNBTCompact(entityTag, 4);
                    int offsetY10 = offsetY + 10;
                    for (String line : formattedLines) {
                        if (line.isEmpty()) continue;
                        int textWidth = font.width(line);
                        int lineX = x;

                        font.draw(poseStack, line, lineX, offsetY10, 0xFFFFFF);
                        offsetY10 += font.lineHeight;
                    }
                }
            }
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        ItemStack mainHand = player.getMainHandItem();
        int rightX = screenWidth - 10;
        int rightY = 10;
        int maxWidth = 600;

        font.draw(poseStack, "Main Hand Item:", rightX - font.width("Main Hand Item:"), rightY, 0xFFFFFF);
        font.draw(poseStack, mainHand.getHoverName().getString(), rightX - font.width(mainHand.getHoverName().getString()), rightY + 10, 0xFFFFFF);

        if (mainHand.hasTag()) {
            CompoundTag tag = mainHand.getTag();
            String formattedNBT = formatNBT(tag, 0);

            int offsetY = rightY + 20;

            for (FormattedCharSequence line : font.split(Component.literal(formattedNBT), maxWidth)) {
                int textWidth = font.width(line);
                int lineX = rightX - textWidth;

                font.draw(poseStack, line, lineX, offsetY, 0xFFFFFF);
                offsetY += font.lineHeight;
            }
        } else {
            font.draw(poseStack, "No NBT data.", rightX - font.width("No NBT data."), rightY + 20, 0xFFFFFF);
        }

        int nextY = 400;
        for (DebugDataProvider provider : DebugDataRegistry.getDebugDataProviders()) {
            String modId = provider.getModId();
            int color = providerColors.computeIfAbsent(provider, p -> getRandomColor());
            font.draw(poseStack, String.format("[{%s}]", modId), x, nextY, color);
            nextY += font.lineHeight + 2;
            provider.renderDebugData(mc, font, poseStack, x, nextY);
            nextY += font.lineHeight + 2;
        }
    }


    private static int getRandomColor() {
        return (random.nextInt(256) << 16) | (random.nextInt(256) << 8) | random.nextInt(256);
    }

    private static List<String> formatNBTCompact(CompoundTag tag, int propertiesPerLine) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder("{ ");
        int count = 0;

        for (String key : tag.getAllKeys()) {
            net.minecraft.nbt.Tag nbtTag = tag.get(key);

            if (nbtTag instanceof CompoundTag) {
                String value = ((CompoundTag) nbtTag).getAsString();
                currentLine.append(key).append(": ").append(value).append(", ");
                count++;
            } else if (nbtTag instanceof net.minecraft.nbt.ListTag && key.equals("Attributes")) {
                List<String> attributeLines = formatAttributes((net.minecraft.nbt.ListTag) nbtTag);
                lines.addAll(attributeLines);
            } else {
                currentLine.append(key).append(": ").append(nbtTag.getAsString()).append(", ");
                count++;
            }
            if (count >= propertiesPerLine) {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
                count = 0;
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString().trim() + " }");
        }

        return lines;
    }

    private static List<String> formatAttributes(net.minecraft.nbt.ListTag attributesTag) {
        List<String> attributeLines = new ArrayList<>();
        int count = 0;
        StringBuilder currentLine = new StringBuilder("{ ");

        for (net.minecraft.nbt.Tag attributeTag : attributesTag) {
            if (attributeTag instanceof CompoundTag) {
                CompoundTag attribute = (CompoundTag) attributeTag;
                String attributeName = attribute.getString("Name");
                String attributeValue = attribute.getString("Base");
                currentLine.append(attributeName).append(": ").append(attributeValue).append(", ");
                count++;

                if (count >= 2) {
                    attributeLines.add(currentLine.toString().trim() + "}");
                    currentLine = new StringBuilder();
                    count = 0;
                }
            }
        }
        if (count > 0) {
            attributeLines.add(currentLine.toString().trim() + "}");
        }

        return attributeLines;
    }


    private static String formatNBT(CompoundTag tag, int depth) {
        StringBuilder sb = new StringBuilder();
        String indent = " ".repeat(depth * 2);

        sb.append("{\n");
        for (String key : tag.getAllKeys()) {
            sb.append(indent).append("  ").append(key).append(": ");
            if (tag.get(key) instanceof CompoundTag nested) {
                sb.append(formatNBT(nested, depth + 1));
            } else {
                sb.append(tag.get(key)).append("\n");
            }
        }
        sb.append(indent).append("}\n");

        return sb.toString();
    }

}
