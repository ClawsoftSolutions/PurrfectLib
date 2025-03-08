package com.github.clawsoftsolutions.purrfectlib.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ModArmorItem extends ArmorItem {
    private final IModArmorMaterial material;
    private final String mod_id;
    private final ISpecialAbility specialAbility;

    public ModArmorItem(String mod_id, IModArmorMaterial material, EquipmentSlot slot, Properties properties, ISpecialAbility specialAbility) {
        super(material, slot, properties);
        this.material = material;
        this.mod_id = mod_id;
        this.specialAbility = specialAbility;
    }

    /**
     * Called when the player equips the armor. This will apply the special ability effect if it exists.
     *
     * @param stack The armor item being equipped.
     * @param player The player wearing the armor.
     */

    @Override
    public void onArmorTick(ItemStack stack, net.minecraft.world.level.Level world, Player player) {
        super.onArmorTick(stack, world, player);

        if (specialAbility != null) {
            specialAbility.applyEffect(player, stack);
        }
    }

    /**
     * Getter for the special ability.
     *
     * @return The special ability of the armor item.
     */
    public ISpecialAbility getSpecialAbility() {
        return specialAbility;
    }

    /**
     * Getter for the armour texture.
     *
     * @param stack  ItemStack for the equipped armor
     * @param entity The entity wearing the armor
     * @param slot   The slot the armor is in
     * @param type   The subtype, can be null or "overlay"
     * @return The armor texture of the armor item
     */

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return String.format(mod_id + ":textures/models/armor/%s_layer_%d.png", material.getTextureName(), slot == EquipmentSlot.LEGS ? 2 : 1);
    }
}
