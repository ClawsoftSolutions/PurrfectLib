package com.github.clawsoftsolutions.purrfectlib.energy.electrical;

public record Ampere(double amp) {
    public static Ampere calculateFromOhmLaw(Volt voltage, Ohm resistance) {
        return new Ampere(voltage.volt() / resistance.ohm());
    }
}
