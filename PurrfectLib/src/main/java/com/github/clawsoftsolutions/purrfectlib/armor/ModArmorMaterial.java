package com.github.clawsoftsolutions.purrfectlib.armor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public record ModArmorMaterial(String name, int durability, int[] protectionValues,
                               int enchantability, SoundEvent equipSound,
                               float toughness, float knockbackResistance,
                               Supplier<Ingredient> repairMaterial) implements IModArmorMaterial {

    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};


    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getIndex()] * durability;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return protectionValues[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getTextureName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}

