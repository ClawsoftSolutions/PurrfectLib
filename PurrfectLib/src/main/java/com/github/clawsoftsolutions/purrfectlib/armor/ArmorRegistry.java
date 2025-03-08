package com.github.clawsoftsolutions.purrfectlib.armor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArmorRegistry {

    /**
     * Registers an armor item for the specified name and equipment slot.
     * This method creates a new armor material with default values and registers it in the given Deferred Register.
     *
     * @param name The name of the armor set. This will be used to create the armor item's registry name.
     * @param slot The equipment slot where the armor will be equipped (e.g., head, chest, legs, or feet).
     * @param ITEMS The DeferredRegister for items where the armor item will be registered.
     * @param mod_id The mod ID that is used to properly register the armor item under the mod's namespace.
     */
    public static void registerArmor(String name, EquipmentSlot slot, DeferredRegister<Item> ITEMS, String mod_id, ISpecialAbility specialAbility) {
        IModArmorMaterial material = new ModArmorMaterial(
                name, 30, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_IRON,
                2.0f, 0.1f, () -> Ingredient.of(Items.IRON_INGOT)
        );

        // If you want it to return the registered armor, add the return property in front of ITEMS.register and remove the void parameter
        ITEMS.register(name + "_" + slot.getName(),
                () -> new ModArmorItem(mod_id, material, slot, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), specialAbility));
    }


    /**
     * Loads armor sets from JSON files and registers them in the provided Deferred Register.
     * This method reads JSON files from the given directory, parses each set, and registers the armor using the
     * {@link #registerArmorFromJson(JsonObject, DeferredRegister, String)} method.
     *
     * @param ITEMS The DeferredRegister for items where the armor sets will be registered.
     * @param mod_id The mod ID that is used to properly register the armor items under the mod's namespace.
     * @param configPath The path to the directory containing the JSON configuration files for armor sets.
     */
    public static void loadAndRegisterArmorSets(DeferredRegister<Item> ITEMS, String mod_id, Path configPath) {
        List<JsonObject> armorSets = loadArmorSetsFromJson(configPath);

        for (JsonObject data : armorSets) {
            registerArmorFromJson(data, ITEMS, mod_id);
        }
    }

    private static List<JsonObject> loadArmorSetsFromJson(Path configPath) {
        List<JsonObject> armorSets = new ArrayList<>();
        try {
            Files.createDirectories(configPath);

            Files.walk(configPath.resolve("armor_sets"))
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(file -> {
                        try (FileReader reader = new FileReader(file.toFile())) {
                            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
                            armorSets.add(jsonObject);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return armorSets;
    }

    private static void registerArmorFromJson(JsonObject data, DeferredRegister<Item> ITEMS, String mod_id) {
        String name = data.get("name").getAsString();
        int durability = data.get("durability").getAsInt();
        int[] protectionValues = new Gson().fromJson(data.get("protection_values"), int[].class);
        int enchantability = data.get("enchantability").getAsInt();
        String equipSound = data.get("equip_sound").getAsString();
        float toughness = data.get("toughness").getAsFloat();
        float knockbackResistance = data.get("knockback_resistance").getAsFloat();
        String repairItem = data.get("repair_item").getAsString();
        String specialAbility = data.has("special_ability") ? data.get("special_ability").getAsString() : null;

        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(equipSound));
        Item repairItemInstance = ForgeRegistries.ITEMS.getValue(new ResourceLocation(repairItem));

        IModArmorMaterial material = new ModArmorMaterial(
                name, durability, protectionValues, enchantability,
                soundEvent != null ? soundEvent : SoundEvents.ARMOR_EQUIP_IRON,
                toughness, knockbackResistance,
                () -> Ingredient.of(repairItemInstance != null ? repairItemInstance : Items.IRON_INGOT)
        );

        ISpecialAbility ability = null;
        if ("regen".equals(specialAbility)) {
            ability = ISpecialAbility.REGENERATION;
        }

        registerArmor(name, EquipmentSlot.HEAD, ITEMS, mod_id, ability);
        registerArmor(name, EquipmentSlot.CHEST, ITEMS, mod_id, ability);
        registerArmor(name, EquipmentSlot.LEGS, ITEMS, mod_id, ability);
        registerArmor(name, EquipmentSlot.FEET, ITEMS, mod_id, ability);
    }
}
