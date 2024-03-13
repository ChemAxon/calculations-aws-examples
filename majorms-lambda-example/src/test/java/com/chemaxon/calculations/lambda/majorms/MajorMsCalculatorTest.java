package com.chemaxon.calculations.lambda.majorms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MajorMsCalculatorTest {

    @Test
    void test() {
        MajorMsRequest request = MajorMsRequest.ofSingle(
                "CC(=O)OC1=CC=CC=C1C(O)=O", // aspirin
                7.4
        );

        MajorMsCalculator handler = new MajorMsCalculator();
        MajorMsResponse response = handler.handleRequest(request, null);

        assertTrue(response.resultSmiles.contains("CC(=O)OC1=CC=CC=C1C([O-])=O"));
    }

}
