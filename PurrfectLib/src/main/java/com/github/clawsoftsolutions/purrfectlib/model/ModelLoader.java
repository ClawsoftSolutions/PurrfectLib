package com.github.clawsoftsolutions.purrfectlib.model;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ModelLoader implements IModelLoader {
    private static final Gson GSON = new Gson();

    @Override
    public Model loadModel(ResourceLocation modelPath) throws IOException {
        InputStream stream = Minecraft.getInstance().getResourceManager().getResource(modelPath).get().open();
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        return GSON.fromJson(reader, Model.class);
    }
}
