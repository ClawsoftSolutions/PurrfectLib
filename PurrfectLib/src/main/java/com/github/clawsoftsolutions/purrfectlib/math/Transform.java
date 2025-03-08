package com.github.clawsoftsolutions.purrfectlib.math;

import com.mojang.math.Vector3f;

public class Transform {
    /**
     * Transforms a vector by a 4x4 matrix.
     * Assumes the matrix is in column-major order and the vector is a position (w = 1).
     */
    public static Vector3f transformVector4x4(float[] m, Vector3f v) {
        float x = v.x();
        float y = v.y();
        float z = v.z();
        float tx = m[0] * x + m[4] * y + m[8] * z + m[12];
        float ty = m[1] * x + m[5] * y + m[9] * z + m[13];
        float tz = m[2] * x + m[6] * y + m[10] * z + m[14];
        return new Vector3f(tx, ty, tz);
    }

    /**
     * Transforms a vector by a 3x3 matrix.
     * Assumes the matrix is in column-major order and the vector is a direction (w = 0).
     */
    public static Vector3f transformVector3x3(float[] m, Vector3f v) {
        float x = v.x();
        float y = v.y();
        float z = v.z();
        float tx = m[0] * x + m[3] * y + m[6] * z;
        float ty = m[1] * x + m[4] * y + m[7] * z;
        float tz = m[2] * x + m[5] * y + m[8] * z;
        return new Vector3f(tx, ty, tz);
    }
}
