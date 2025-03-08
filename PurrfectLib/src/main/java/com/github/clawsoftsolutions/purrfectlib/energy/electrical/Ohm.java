package com.github.clawsoftsolutions.purrfectlib.energy.electrical;

public record Ohm(double ohm) {
    public static Ohm calculateFromOhmLaw(Volt voltage, Ampere current) {
        return new Ohm(voltage.volt() / current.amp());
    }
}
