package com.github.clawsoftsolutions.purrfectlib.energy.electrical.capabilities;

import com.github.clawsoftsolutions.purrfectlib.energy.electrical.Ampere;
import com.github.clawsoftsolutions.purrfectlib.energy.electrical.Ohm;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IElectricalEnergyTransmitter {
    Ampere getAmpere();
    void setAmpere(Ampere ampere);

    Ohm getOhm();
    void setOhm(Ohm ohm);
}
