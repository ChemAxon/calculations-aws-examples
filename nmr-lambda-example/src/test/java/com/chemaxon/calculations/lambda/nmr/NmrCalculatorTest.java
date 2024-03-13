package com.chemaxon.calculations.lambda.nmr;

import org.junit.jupiter.api.Test;

import com.chemaxon.calculations.lambda.common.MoleculeFormats;

import chemaxon.core.ChemConst;
import chemaxon.struc.Molecule;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NmrCalculatorTest {

    private static final String ASPIRIN_SMILES = "CC(=O)OC1=CC=CC=C1C(O)=O";

    @Test
    void testSizeOfNmrResults() {
        NmrRequest request = NmrRequest.ofSingle(ASPIRIN_SMILES, "smiles");

        NmrCalculator handler = new NmrCalculator();
        NmrResponse response = handler.handleRequest(request, null);
        NmrResult res = response.results.get(0);

        Molecule resultMol = MoleculeFormats.asCxnMolecule(res.molecule, res.format);
        assertEquals(resultMol.getAtomCount(ChemConst.C), res.cnmrResult.size());
        assertEquals(resultMol.getAtomCount(ChemConst.H), res.hnmrResult.size());
    }

}
