package com.chemaxon.calculations.lambda;

import chemaxon.calculations.nmr.NMRCalculator;
import chemaxon.calculations.nmr.NMRSpectrum;
import chemaxon.calculations.nmr.NMRSpectrum.Nucleus;
import chemaxon.calculations.nmr.Shift;
import chemaxon.struc.MolAtom;
import chemaxon.struc.Molecule;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.chemaxon.calculations.lambda.NmrResult.CnmrShift;
import com.chemaxon.calculations.lambda.NmrResult.HnmrShift;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;

/**
 * A very simple {@link RequestHandler} implementation which calculates the NMR shifts of the input structure.
 *
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public class NmrCalculator implements RequestHandler<NmrRequest, NmrResponse> {

    @Override
    public NmrResponse handleRequest(NmrRequest request, Context context) {

        Preconditions.checkNotNull(request);
        
        MoleculeFormats.ensureSupportedFormat(request.format);
        
        if (request.structureSources == null || request.structureSources.isEmpty()) {
            throw new IllegalArgumentException("No input structure specified");
        }

        // convert spectrum
        final NmrResponse ret = new NmrResponse();
        ret.results = new ArrayList<>();
        
        for (int i = 0; i < request.structureSources.size(); i++) {
            final int idx = i;
            final String structureSource = request.structureSources.get(i);
            final Molecule molecule = MoleculeFormats.asCxnMolecule(
                    structureSource, 
                    request.format,
                    () -> "structure " + idx + " (0-based)"
            );
            
            // Expand and ungroup all S-groups
            // The calculation will invoke SDF export where the presence of
            // S-groups cause a call into libraries excluded during the cherry
            // picking process
            molecule.ungroupSgroups();
            
            // calculate C and H NMR spectra
            // Note that H NMR calculation uses its own hydrogenization
            NMRSpectrum hnmr = calculateNmrSpectrum(
                molecule,
                Nucleus.H1
            );
            NMRSpectrum cnmr = calculateNmrSpectrum(
                    hnmr.getMolecule(), 
                    Nucleus.C13
            );

            final String resultSdf = MoleculeFormats.convertToSdf(hnmr.getMolecule());
            final NmrResult resi = new NmrResult();
            resi.molecule = resultSdf;
            resi.format = "sdf";
            resi.cnmrResult = cnmrResult(cnmr);
            resi.hnmrResult = hnmrResult(hnmr);
            
            ret.results.add(resi);
        }


        return ret;
    }

    /**
     * 13C NMR result.
     */
    private List<CnmrShift> cnmrResult(NMRSpectrum spectrum) {
        List<CnmrShift> result = new ArrayList<>();

        Molecule nmrMolecule = spectrum.getMolecule();

        for (Shift shift : spectrum.getShifts()) {
            CnmrShift cs = new CnmrShift();
            cs.atomIndex = shift.getAtomIndex();
            cs.shift = shift.getShift();
            cs.shiftError = shift.getShiftError();
            MolAtom atom = nmrMolecule.getAtom(shift.getAtomIndex());
            cs.bondCount = atom.getBondCount() + atom.getImplicitHcount();
            cs.hCount = atom.getExplicitHcount() + atom.getImplicitHcount();

            result.add(cs);
        }

        return result;
    }

    /**
     * 1H NMR result.
     */
    private List<HnmrShift> hnmrResult(NMRSpectrum spectrum) {
        List<HnmrShift> result = new ArrayList<>();

        Molecule nmrMolecule = spectrum.getMolecule();

        for (Shift shift : spectrum.getShifts()) {
            HnmrShift hs = new HnmrShift();
            hs.atomIndex = shift.getAtomIndex();
            hs.shift = shift.getShift();
            hs.shiftError = shift.getShiftError();
            MolAtom attachedAtom = nmrMolecule.getAtom(shift.getAtomIndex()).getLigand(0);
            hs.attachedAtomIndex = nmrMolecule.indexOf(attachedAtom);
            hs.attachedAtomNumber = attachedAtom.getAtno();

            result.add(hs);
        }

        return result;
    }

    /**
     * Calculates NMR spectrum of the specified molecule.
     */
    private NMRSpectrum calculateNmrSpectrum(Molecule mol, Nucleus n) {
        NMRCalculator nmr = new NMRCalculator.Builder().setNucleus(n).build();
        return nmr.calculate(mol);
    }

}
