package com.chemaxon.calculations.lambda.common;

import org.junit.jupiter.api.Test;

import chemaxon.struc.MolAtom;
import chemaxon.struc.Molecule;
import chemaxon.struc.PeriodicSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Exercise {@link Smiles}.
 *
 * @author Gabor Imre
 */
class SmilesTest {

    @Test
    void simpleFromSmiles() {
        Molecule mol = Smiles.asCxnMolecule("CN");
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());

        assertEquals("C", mol.getAtom(0).getSymbol());
        assertEquals("N", mol.getAtom(1).getSymbol());
    }

    @Test
    void simpleFromMolecule() {
        Molecule mol = new Molecule();
        mol.add(new MolAtom(PeriodicSystem.Ag));
        mol.add(new MolAtom(PeriodicSystem.Ba));
        String smi = Smiles.fromCxnMolecule(mol);

        assertEquals("[Ag].[Ba]", smi);
    }

}
