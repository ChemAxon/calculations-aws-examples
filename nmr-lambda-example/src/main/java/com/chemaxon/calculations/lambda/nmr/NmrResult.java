package com.chemaxon.calculations.lambda.nmr;

import chemaxon.struc.Molecule;

import java.util.List;
import java.util.Map;

import com.chemaxon.calculations.lambda.common.MoleculeFormats;

/**
 * Calculation result for a single input structure.
 *
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public class NmrResult {

    /**
     * Result molecule.
     * <p>
     * Format is described in {@link #format}, defaults to {@code sdf} currently.
     */
    public String molecule;

    /**
     * Result molecule format.
     * <p>
     * Format of structure in {@link #molecule}, defaults to {@code sdf} currently.
     */

    public String format;

    /**
     * 13C NMR shifts.
     * <p>
     * Atom indices refer to the structure in {@link #molecule}.
     */
    public List<CnmrShift> cnmrResult;

    /**
     * 1H NMR shifts.
     */
    public List<HnmrShift> hnmrResult;

    public static class CnmrShift {
        public int atomIndex;
        public double shift;
        public double shiftError;
        public int bondCount;
        public int hCount;
    }

    public static class HnmrShift {
        public int atomIndex;
        public double shift;
        public double shiftError;
        public int attachedAtomIndex;
        public int attachedAtomNumber;
    }

    /**
     * Create an SDF representation.
     * <p>
     * Note that the contents of the resulting SDF representation is subject to change. Consider the current
     * implementation only an example.
     *
     * @param additionalPropsOrNull Additional SDF properties to store
     * @return SDF representation
     */
    public String toSdf(Map<String, String> additionalPropsOrNull) {
        final Molecule m = MoleculeFormats.asCxnMolecule(this.molecule, this.format);

        if (additionalPropsOrNull != null) {
            additionalPropsOrNull.entrySet().forEach(e -> m.setProperty(e.getKey(), e.getValue()));
        }
        if (this.cnmrResult != null) {
            for (int i = 0; i < this.cnmrResult.size(); i++) {
                final CnmrShift shift = this.cnmrResult.get(i);
                m.setProperty(
                        "cnmr-shift-" + i,
                        "atomIndex: " + shift.atomIndex + ", shift: " + shift.shift + ", shiftError: "
                                + shift.shiftError + ", bondCount: " + shift.bondCount + ", hCount: " + shift.hCount
                );
            }
        }

        if (this.hnmrResult != null) {
            for (int i = 0; i < this.hnmrResult.size(); i++) {
                final HnmrShift shift = this.hnmrResult.get(i);
                m.setProperty(
                        "hnmr-shift-" + i,
                        "atomIndex: " + shift.atomIndex + ", shift: " + shift.shift + ", shiftError: "
                                + shift.shiftError + ", attachedAtomIndex: " + shift.attachedAtomIndex
                                + ", attachedAtomNumber: " + shift.attachedAtomNumber
                );
            }
        }

        return MoleculeFormats.convertToSdf(m);
    }
}
