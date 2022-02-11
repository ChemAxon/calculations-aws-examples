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
        if (request.smiles == null || request.smiles.isEmpty()) {
            throw new IllegalArgumentException("No input structure specified");
        }

        final MsDistrResponse ret = new MsDistrResponse();
        ret.pH = request.pH;
        ret.tautomerize = request.tautomerize;
        ret.temperature = request.temperature;
        ret.results = new ArrayList<>();

        for (int i = 0; i < request.smiles.size(); i++) {
            final String smiles = request.smiles.get(i);

            // TODO: add logging - context.getLogger()
            ret.results.add(calculateMsDistr(smiles, request.pH, request.tautomerize, request.temperature));
        }

        return ret;
    }

    /**
     * Calculates the microspecies distribution of the molecule at the specified pH.
     */
    private MsDistrResult calculateMsDistr(String smiles, double pH, boolean tautomerize, double temperature) {
        try {
            MsDistrResult result = new MsDistrResult();
            result.input = smiles;
            result.microspecies = new ArrayList<>();

            pKaPlugin plugin = new pKaPlugin();
            plugin.setpH(pH);
            plugin.setConsiderTautomerization(tautomerize);
            plugin.setTemperature(temperature);
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
