package com.github.clawsoftsolutions.purrfectlib.impl.content.utils;

public enum WrenchMode {
    ROTATE, DEBUG, DEFAULT;

    public static WrenchMode cycleForward(WrenchMode current) {
        WrenchMode[] values = values();
        return values[(current.ordinal() + 1) % values.length];
    }

    public static WrenchMode cycleBackward(WrenchMode current) {
        WrenchMode[] values = values();
        return values[(current.ordinal() - 1 + values.length) % values.length];
    }
}
