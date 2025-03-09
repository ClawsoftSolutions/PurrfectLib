package com.github.clawsoftsolutions.purrfectlib.armor;

import com.github.clawsoftsolutions.purrfectlib.model.Model;
import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ModArmorItem extends ArmorItem {
    private final IModArmorMaterial material;
    private final String modId;
    private final ISpecialAbility specialAbility;
    private final String modelPath;

    public ModArmorItem(String modId, IModArmorMaterial material, EquipmentSlot slot, Properties properties, ISpecialAbility specialAbility, String modelPath) {
        super(material, slot, properties);
        this.material = material;
        this.modId = modId;
        this.specialAbility = specialAbility;
        this.modelPath = modelPath;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        super.onArmorTick(stack, world, player);

        if (specialAbility != null) {
            specialAbility.applyEffect(player, stack);
        }
    }

    /**
     * Returns the armor texture based on the equipped armor.
     */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return String.format(modId + ":textures/models/armor/%s_layer_%d.png", material.getTextureName(), slot == EquipmentSlot.LEGS ? 2 : 1);
    }

    /**
     * Getter for the armor model path (geo.json or bbmodel).
     *
     * @return The path of the model to be used for the armor.
     */
    public String getArmorModelPath() {
        return modelPath;
    }

    /**
     * This method is used to load the armor model dynamically using the model path.
     * Will return a model renderer based on your custom model files like geo.json or bbmodel.
     */
    public void loadArmorModel(PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
        if (modelPath != null) {
            try {
                Model model = loadCustomModel(new ResourceLocation(modId, modelPath));
                ArmorRenderer.render(poseStack, buffer, model, light, overlay);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Model loadCustomModel(ResourceLocation modelPath) throws IOException {
        InputStream stream = Minecraft.getInstance().getResourceManager().getResource(modelPath).get().open();
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        return new Gson().fromJson(reader, Model.class);
    }
}
