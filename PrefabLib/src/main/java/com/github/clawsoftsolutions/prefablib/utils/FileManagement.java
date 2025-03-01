package com.github.clawsoftsolutions.prefablib.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.*;

public class FileManagement {
    private static final String PREFAB_FOLDER = "prefabs";

    public static File exportNBTToFile(CompoundTag tag, String filename) throws IOException {
        File exportFolder = new File(PREFAB_FOLDER);
        if (!exportFolder.exists()) {
            exportFolder.mkdirs();
        }
        File file = new File(exportFolder, filename + ".nbt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            DataOutput output = new DataOutputStream(fos);
            NbtIo.write(tag, output);
        }
        return file;
    }

    public static CompoundTag loadPrefab(String name) {
        File file = new File(PREFAB_FOLDER, name + ".nbt");

        if (!file.exists()) {
            throw new RuntimeException("Prefab file not found: " + file.getAbsolutePath());
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            return NbtIo.readCompressed(inputStream); // Read compressed NBT file
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prefab: " + name, e);
        }
    }
}
