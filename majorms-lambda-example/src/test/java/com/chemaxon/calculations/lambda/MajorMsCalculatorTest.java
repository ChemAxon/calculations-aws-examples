package com.chemaxon.calculations.lambda;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import org.junit.Test;

public class MajorMsCalculatorTest {

    @Test
    public void test() {
        final MajorMsRequest request = MajorMsRequest.ofSingle(
            "CC(=O)OC1=CC=CC=C1C(O)=O", // aspirin
            7.4
        );

        final MajorMsCalculator handler = new MajorMsCalculator();
        final MajorMsResponse response = handler.handleRequest(request, null);
        
        assertThat(response.resultSmiles, contains("CC(=O)OC1=CC=CC=C1C([O-])=O"));
    }
}
