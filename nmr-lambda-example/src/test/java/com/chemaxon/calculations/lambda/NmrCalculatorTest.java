package com.chemaxon.calculations.lambda;

import chemaxon.struc.Molecule;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import org.junit.Test;

public class NmrCalculatorTest {

    static final String ASPIRIN_SMILES = "CC(=O)OC1=CC=CC=C1C(O)=O";

    @Test
    public void testSizeOfNmrResults() {
        NmrRequest request = NmrRequest.ofSingle(ASPIRIN_SMILES, "smiles");

        NmrCalculator handler = new NmrCalculator();
        final NmrResponse response = handler.handleRequest(request, null);
        final NmrResult res = response.results.get(0);
                

        Molecule resultMol = MoleculeFormats.asCxnMolecule(res.molecule, res.format);
        assertThat(res.cnmrResult, hasSize(resultMol.getAtomCount(6)));
        assertThat(res.hnmrResult, hasSize(resultMol.getAtomCount(1)));
    }
}
