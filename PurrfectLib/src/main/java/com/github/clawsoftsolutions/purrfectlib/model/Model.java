package com.github.clawsoftsolutions.purrfectlib.model;


import com.github.clawsoftsolutions.purrfectlib.math.Vector3f;

import java.util.List;

public class Model {
    public List<Bone> bones;
    private List<Cube> cubes;

    public static class Bone {
        public String name;
        public List<Float> pivot;
        public List<Float> rotation;
        public List<Cube> cubes;
    }

    public static class Cube {
        public Vector3f position;
        public Vector3f size;
        public String texture;
    }

    public List<Cube> getCubes() {
        return cubes;
    }
}
