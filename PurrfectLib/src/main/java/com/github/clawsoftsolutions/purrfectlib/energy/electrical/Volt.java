package com.github.clawsoftsolutions.purrfectlib.energy.electrical;

public record Volt(double volt) {
    public static Volt calculateFromOhmLaw(Ampere current, Ohm resistance) {
        return new Volt(current.amp() * resistance.ohm());
    }
}
