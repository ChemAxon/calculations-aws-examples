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
    public List<Double> pH;

    /**
     * Convenience factory for a single structure - pH pair.
     * 
     * @param smiles Input structure
     * @param pH pH to use
     * @return Created instance
     */
    public static MsDistrRequest ofSingle(String smiles, double pH) {
        final MsDistrRequest ret = new MsDistrRequest();
        ret.pH = ImmutableList.of(pH);
        ret.smiles = ImmutableList.of(smiles);
        return ret;
    }
}
