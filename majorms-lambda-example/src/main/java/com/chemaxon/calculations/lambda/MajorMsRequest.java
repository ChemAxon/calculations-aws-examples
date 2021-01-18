package com.chemaxon.calculations.lambda;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Stores input data for the {@link MajorMsCalculator}.
 * 
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public class MajorMsRequest {

    /**
     * Input structures in SMILES format.
     */
    public List<String> smiles;

    /**
     * Calculates the major microspecies at ths pH.
     */
    public List<Double> pH;

    
    /**
     * Convenience factory for a single structure - pH pair.
     * 
     * @param smiles Input structure
     * @param pH pH to use
     * @return Created instance
     */
    public static MajorMsRequest ofSingle(String smiles, double pH) {
        final MajorMsRequest ret = new MajorMsRequest();
        ret.pH = ImmutableList.of(pH);
        ret.smiles = ImmutableList.of(smiles);
        return ret;
    }
}
