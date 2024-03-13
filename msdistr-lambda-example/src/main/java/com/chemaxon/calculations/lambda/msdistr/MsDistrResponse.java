package com.chemaxon.calculations.lambda.msdistr;

import java.util.List;

/**
 * Calculation result for all inputs on all values.
 *
 * @author Laszlo Antal
 */
public class MsDistrResponse {

    public double pH;

    public boolean tautomerize;

    public double temperature;

    /**
     * Results for input structures.
     */
    public List<MsDistrResult> results;

}
