package com.chemaxon.calculations.lambda;

import chemaxon.formats.MolExporter;
import chemaxon.formats.MolFormatException;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import com.chemaxon.calculations.io.Segmenter;
import com.chemaxon.calculations.io.Segmenters;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * Utility class for conversion between MOL/SMILES format and ChemAxon's structure representation.
 *
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public final class MoleculeFormats {

    private MoleculeFormats() {
        // Hide constructor.
    }

    public static String getSupportedFormatsListAsHumanReadable() {
        return "\"smiles\", \"sdf\" or \"mol\"";
    }
    
    public static void ensureSupportedFormat(String format) {
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("No format specified, use " + getSupportedFormatsListAsHumanReadable());
        }
                
    }
    
    public static Segmenter segmenterForFormat(String format) {
        ensureSupportedFormat(format);
        return Segmenters.ofFormat(format);
    }
    
    
    /**
     * Construct a {@link Molecule} object from the specified MOL record.
     *
     * @param source Structure record in {@code smiles}, {@code sdf} or {@code mol} format.
     * @param format Structure record format
     * @param molDesc Structure description when importing failed
     * @return The parsed molecule.
     * @throws IllegalArgumentException If the file format is not recognised.
     */
    public static Molecule asCxnMolecule(String source, String format, Supplier<String> molDesc) {
        ensureSupportedFormat(format);
        if (source == null) {
            throw new IllegalArgumentException(molDesc.get() + " is null");
        }
        if (source.isEmpty()) {
            throw new IllegalArgumentException(molDesc.get() + " is empty");
        }

        try {
            return MolImporter.importMol(source, format);
        } catch (MolFormatException e) {
            throw new IllegalArgumentException("Error importing " + molDesc.get(), e);
        }
    }
    
    /**
     * Construct a {@link Molecule} object from the specified MOL record.
     *
     * @param source Structure record in {@code smiles}, {@code sdf} or {@code mol} format.
     * @param format Structure record format
     * @return The parsed molecule.
     * @throws IllegalArgumentException If the file format is not recognised.
     */
    public static Molecule asCxnMolecule(String source, String format) {
        return asCxnMolecule(source, format, () -> "structure");
    }

    /**
     * Converts the {@link Molecule} object to MOL format.
     *
     * @param molecule The input molecule.
     * @return The structure in sdf format.
     * @throws IllegalArgumentException If the export process fail.
     */
    public static String convertToSdf(Molecule molecule) {
        try {
            return MolExporter.exportToFormat(molecule, "sdf");
        } catch (IOException e) {
            throw new IllegalArgumentException("Molecule export to sdf failed.", e);
        }
    }
}
