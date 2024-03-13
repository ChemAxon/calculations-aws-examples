package com.chemaxon.calculations.lambda.common;

import java.io.IOException;

import chemaxon.formats.MolExporter;
import chemaxon.formats.MolFormatException;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;

/**
 * Utility class for conversion between SMILES format and ChemAxon's structure representation.  
 * 
 * @author Laszlo Antal
 */
public final class Smiles {
    
    private static final String FORMAT = "smiles";

    private Smiles() {
        // Hide constructor.
    }
    
    /**
     * Construct a {@link Molecule} object from the specified SMILES record.
     * 
     * @param smiles Structure record in SMILES format.
     * @return The parsed molecule.
     * @throws IllegalArgumentException If the file format is not recognised.
     */
    public static Molecule asCxnMolecule(String smiles) {
        try {
            return MolImporter.importMol(smiles, FORMAT);
        } catch (MolFormatException e) {
            throw new IllegalArgumentException("Invalid file format: " + smiles, e);
        }
    }
    
    /**
     * Converts the {@link Molecule} object to SMILES format.
     * 
     * @param molecule The input molecule.
     * @return The structure in SMILES format.
     * @throws IllegalArgumentException If the export process fail.
     */
    public static String fromCxnMolecule(Molecule molecule) {
        try {
            return MolExporter.exportToFormat(molecule, FORMAT);
        } catch (IOException e) {
            throw new IllegalArgumentException("Molecule export failed.", e);
        }
    }
}
