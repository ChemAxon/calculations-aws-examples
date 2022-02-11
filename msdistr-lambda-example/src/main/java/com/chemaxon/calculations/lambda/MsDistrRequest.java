package com.chemaxon.calculations.lambda;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Stores input data for the {@link MsDistrCalculator}.
 * 
 * @author Laszlo Antal
 */
public class MsDistrRequest {

    /**
     * Input structures in SMILES format.
     */
    public List<String> smiles;

    /**
     * Calculates the microspecies distribution at this pH.
     */
    public double pH = 7.4;

    /**
     * Consider tautomerization and resonance.
     */
    public boolean tautomerize = false;

    /**
     * Temperature in Kelvin.
     */
    public double temperature = 298.0;

    /**
     * Convenience factory for a single structure - pH pair.
     * 
     * @param smiles Input structure
     * @param pH pH to use
     * @param tautomerize consider tautomerization/resonance
     * @param temperature temperature in Kelvin
     * @return Created instance
     */
    public static MsDistrRequest ofSingle(String smiles, double pH, boolean tautomerize, double temperature) {
        final MsDistrRequest ret = new MsDistrRequest();
        ret.pH = pH;
        ret.tautomerize = tautomerize;
        ret.temperature = temperature;
        ret.smiles = ImmutableList.of(smiles);
        return ret;
    }
}
