package com.chemaxon.calculations.lambda.msdistr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MsDistrCalculatorTest {

    @Test
    void test() {
        MsDistrRequest request = MsDistrRequest.ofSingle(
                "CC(=O)OC1=CC=CC=C1C(O)=O", // aspirin
                7.4,
                false,
                298.0
        );

        MsDistrCalculator handler = new MsDistrCalculator();
        MsDistrResponse response = handler.handleRequest(request, null);

        assertEquals(1, response.results.size());

        var result = response.results.get(0);
        assertNotNull(result.input);
        assertNotEquals("", result.input);
        assertFalse(result.microspecies.isEmpty());
    }

}
