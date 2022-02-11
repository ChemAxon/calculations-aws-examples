package com.chemaxon.calculations.lambda;

import java.util.ArrayList;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.chemaxon.calculations.lambda.MsDistrResult.Ms;
import com.google.common.base.Preconditions;

import chemaxon.marvin.calculations.pKaPlugin;
import chemaxon.marvin.plugin.PluginException;

/**
 * A very simple {@link RequestHandler} implementation which calculates the microspecies distribution of the input
 * structure.
 * 
 * @author Laszlo Antal
 */
public class MsDistrCalculator implements RequestHandler<MsDistrRequest, MsDistrResponse> {

    @Override
    public MsDistrResponse handleRequest(MsDistrRequest request, Context context) {

        Preconditions.checkNotNull(request);
        if (request.pH == null || request.pH.isEmpty()) {
            throw new IllegalArgumentException("No pH specified.");
        }
        if (request.smiles == null || request.smiles.isEmpty()) {
            throw new IllegalArgumentException("No input structure specified");
        }

        final MsDistrResponse ret = new MsDistrResponse();
        ret.results = new ArrayList<>();

        for (int i = 0; i < request.smiles.size(); i++) {
            final String smiles = request.smiles.get(i);

            for (int j = 0; j < request.pH.size(); j++) {
                final double pH = request.pH.get(j);

                // TODO: add logging - context.getLogger()
                ret.results.add(calculateMsDistr(smiles, pH, request.tautomerize));
            }
        }

        return ret;
    }

    /**
     * Calculates the microspecies distribution of the molecule at the specified pH.
     */
    private MsDistrResult calculateMsDistr(String smiles, final double pH, final boolean tautomerize) {
        try {
            MsDistrResult result = new MsDistrResult();
            result.input = smiles;
            result.pH = pH;
            result.tautomerize = tautomerize;
            result.microspecies = new ArrayList<>();

            pKaPlugin plugin = new pKaPlugin();
            plugin.setpH(pH);
            plugin.setConsiderTautomerization(tautomerize);
            plugin.setMolecule(Smiles.asCxnMolecule(smiles));
            plugin.run();

            for (int i = 0; i < plugin.getMsCount(); i++) {
                double msDistr = plugin.getMsDistribution(i)[0];
                if (msDistr > 0.01) {
                    result.microspecies.add(Ms.of(Smiles.fromCxnMolecule(plugin.getMsMolecule(i)), msDistr));
                }
            }

            return result;
        } catch (PluginException e) {
            throw new IllegalArgumentException(
                    "Calculation failed for molecule: " + smiles + ", pH: " + pH, e);
        }
    }
}
