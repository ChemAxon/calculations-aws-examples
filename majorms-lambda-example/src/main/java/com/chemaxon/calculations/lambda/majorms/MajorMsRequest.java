package com.chemaxon.calculations.lambda.majorms;

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
        MajorMsRequest ret = new MajorMsRequest();
        ret.pH = List.of(pH);
        ret.smiles = List.of(smiles);
        return ret;
    }

}
