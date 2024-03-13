package com.chemaxon.calculations.lambda.nmr;

import java.util.List;

import com.chemaxon.calculations.lambda.common.MoleculeFormats;

/**
 * Stores input data for the {@link NmrCalculator}.
 *
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public class NmrRequest {

    /**
     * Input structures.
     * <p>
     * Format must match to the value of {@link #format}.
     */
    public List<String> structureSources;

    /**
     * File format of input structures.
     * <p>
     * Only {@code smiles}, {@code mol} and {@code sdf} are supported.
     */
    public String format;

    /**
     * Constructs a request from the specified source.
     *
     * @param structureSource Input structure source
     * @param format Input structure format, currently {@code smiles}, {@code sdf} and {@code mol} are
     *         supported.
     * @return Created instance
     */
    public static NmrRequest ofSingle(String structureSource, String format) {
        if (structureSource == null || structureSource.isEmpty()) {
            throw new IllegalArgumentException("No structure source specified.");
        }
        MoleculeFormats.ensureSupportedFormat(format);

        NmrRequest ret = new NmrRequest();
        ret.structureSources = List.of(structureSource);
        ret.format = format;
        return ret;
    }

}
