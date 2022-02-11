package com.chemaxon.calculations.lambda;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

public class MsDistrCalculatorTest {

    @Test
    public void test() {
        final MsDistrRequest request = MsDistrRequest.ofSingle(
            "CC(=O)OC1=CC=CC=C1C(O)=O", // aspirin
            7.4
        );

        final MsDistrCalculator handler = new MsDistrCalculator();
        final MsDistrResponse response = handler.handleRequest(request, null);

        assertThat(response.results, hasSize(1));
        assertThat(response.results.get(0).input, not(isEmptyOrNullString()));
        assertThat(response.results.get(0).microspecies, not(empty()));
    }
}
