package com.chemaxon.calculations.lambda;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Stores input data for the {@link NmrCalculator}.
 *
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public class NmrRequest {

    /**
     * Input structures.
     * 
     * Format must match to the value of {@link #format}.
     */
    public List<String> structureSources;

    /**
     * File format of input structures. 
     * 
     * Only {@code smiles}, {@code mol} and {@code sdf} are supported.
     */
    public String format;

    /**
     * Constructs a request from the specified source.
     *
     * @param structureSource  Input structure source
     * @param format Input structure format, currently {@code smiles}, {@code sdf} and {@code mol} are supported.
     * 
     * @return Created instance
     */
    public static NmrRequest ofSingle(String structureSource, String format) {
        if (structureSource == null || structureSource.isEmpty()) {
            throw new IllegalArgumentException("No structure source specified.");
        }
        MoleculeFormats.ensureSupportedFormat(format);
        
        final NmrRequest ret = new NmrRequest();
        ret.structureSources = ImmutableList.of(structureSource);
        ret.format = format;
        return ret;
    }
}
