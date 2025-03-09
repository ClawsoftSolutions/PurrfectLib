package com.github.clawsoftsolutions.purrfectlib.model;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public interface IModelLoader {
    Model loadModel(ResourceLocation modelPath) throws IOException;
}
