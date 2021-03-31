package com.chemaxon.calculations.lambda;

import chemaxon.struc.MolAtom;
import chemaxon.struc.Molecule;
import chemaxon.struc.PeriodicSystem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

/**
 * Exercise {@link Smiles}.
 * 
 * @author Gabor Imre
 */
public class SmilesTest {
    
    @Test
    public void simple_from_smiles() {
        final Molecule mol = Smiles.asCxnMolecule("CN");
        assertThat(mol.getAtomCount(), is(2));
        assertThat(mol.getBondCount(), is(1));
        
        assertThat(mol.getAtom(0).getSymbol(), is("C"));
        assertThat(mol.getAtom(1).getSymbol(), is("N"));
    }
    
    @Test
    public void simple_from_mol() {
        final Molecule mol = new Molecule();
        mol.add(new MolAtom(PeriodicSystem.Ag));
        mol.add(new MolAtom(PeriodicSystem.Ba));
        final String smi = Smiles.fromCxnMolecule(mol);
        
        assertThat(smi, is("[Ag].[Ba]"));
    }
    
}
