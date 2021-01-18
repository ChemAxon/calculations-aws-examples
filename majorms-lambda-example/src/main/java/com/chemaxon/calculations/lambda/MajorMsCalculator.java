package com.chemaxon.calculations.lambda;

import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.Molecule;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.common.base.Preconditions;
import java.util.ArrayList;

/**
 * A very simple {@link RequestHandler} implementation which calculates the major microspecies of the input structure.
 * 
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public class MajorMsCalculator implements RequestHandler<MajorMsRequest, MajorMsResponse> {

    @Override
    public MajorMsResponse handleRequest(MajorMsRequest request, Context context) {

        Preconditions.checkNotNull(request);
        if (request.pH == null || request.pH.isEmpty()) {
            throw new IllegalArgumentException("No pH specified.");
        }
        if (request.smiles == null || request.smiles.isEmpty()) {
            throw new IllegalArgumentException("No input structure specified");
        }
        
        final MajorMsResponse ret = new MajorMsResponse();
        ret.resultSmiles = new ArrayList<>();
        
        for (int i = 0; i < request.smiles.size(); i++) {
            final String smiles = request.smiles.get(i);
            final Molecule cxnMol = Smiles.asCxnMolecule(smiles);

            for (int j = 0; j < request.pH.size(); j++) {
                final double pH = request.pH.get(j);
                
                // TODO: add logging - context.getLogger()
                final Molecule majorMs = calculateMajorMs(cxnMol, pH);
            
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
