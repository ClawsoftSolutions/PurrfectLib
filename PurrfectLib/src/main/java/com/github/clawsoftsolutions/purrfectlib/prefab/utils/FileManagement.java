package com.github.clawsoftsolutions.purrfectlib.prefab.utils;

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
            System.err.println("Error: Prefab file not found: " + file.getAbsolutePath());
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            // Try reading as GZIP first
            try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                return NbtIo.readCompressed(dis); // GZIP format
            }
        } catch (IOException gzipException) {
            // If GZIP fails, try reading as raw uncompressed NBT
            try (FileInputStream fis = new FileInputStream(file);
                 DataInputStream dis = new DataInputStream(new BufferedInputStream(fis))) {
                return NbtIo.read(dis); // Uncompressed format
            } catch (IOException rawException) {
                System.err.println("Error loading NBT file: " + rawException.getMessage());
                return null;
            }
        }
    }
}
