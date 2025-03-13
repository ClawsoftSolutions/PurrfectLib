package com.github.clawsoftsolutions.purrfectlib.armor;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ISpecialAbility {
    /**
     * Applies the special ability effect to the player when the armor is worn.
     *
     * @param player The player wearing the armor.
     * @param itemStack The item stack representing the armor piece.
     */
    void applyEffect(Player player, ItemStack itemStack);

    /**
     * A default example ability: Grants Regeneration when worn.
     */
    static ISpecialAbility REGENERATION = (player, itemStack) -> {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
    };
}