package com.chemaxon.calculations.lambda.majorms;

import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.Molecule;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.Objects;

import com.chemaxon.calculations.lambda.common.Smiles;

/**
 * A very simple {@link RequestHandler} implementation which calculates the major microspecies of the input structure.
 *
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public class MajorMsCalculator implements RequestHandler<MajorMsRequest, MajorMsResponse> {

    @Override
    public MajorMsResponse handleRequest(MajorMsRequest request, Context context) {

        Objects.requireNonNull(request);
        if (request.pH == null || request.pH.isEmpty()) {
            throw new IllegalArgumentException("No pH specified.");
        }
        if (request.smiles == null || request.smiles.isEmpty()) {
            throw new IllegalArgumentException("No input structure specified");
        }

        MajorMsResponse ret = new MajorMsResponse();
        ret.resultSmiles = new ArrayList<>();

        for (int i = 0; i < request.smiles.size(); i++) {
            String smiles = request.smiles.get(i);
            Molecule cxnMol = Smiles.asCxnMolecule(smiles);

            for (int j = 0; j < request.pH.size(); j++) {
                double pH = request.pH.get(j);

                // TODO: add logging - context.getLogger()
                Molecule majorMs = calculateMajorMs(cxnMol, pH);

                ret.resultSmiles.add(Smiles.fromCxnMolecule(majorMs));
            }
        }

        return ret;
    }

    /**
     * Calculates the major microspecies of the molecule at the specified pH.
     *
     * @see Molecule
     * @see MajorMicrospeciesPlugin
     */
    private Molecule calculateMajorMs(Molecule molecule, final double pH) {
        try {
            MajorMicrospeciesPlugin majorMs = new MajorMicrospeciesPlugin();
            majorMs.setpH(pH);
            majorMs.setMolecule(molecule);
            majorMs.run();
            return majorMs.getMajorMicrospecies();
        } catch (PluginException e) {
            throw new IllegalArgumentException(
                    "Calculation failed for molecule: " + Smiles.fromCxnMolecule(molecule) + ", pH: " + pH, e);
        }
    }
}
