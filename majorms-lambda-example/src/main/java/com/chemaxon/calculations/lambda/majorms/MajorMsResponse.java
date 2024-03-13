package com.chemaxon.calculations.lambda.majorms;

import java.util.List;

/**
 * Calculation result for all inputs on all values.
 *
 * @author Gabor Imre
 */
public class MajorMsResponse {

    /**
     * Result SMILES.
     * <p>
     * Result for {@code i}-th input structure and {@code j}-th pH value stored at
     * index {@code j + PH_COUNT * j} (0-based indices): first {@code PH_COUNT} items
     * are for the first input structure.
     */
    public List<String> resultSmiles;

}
