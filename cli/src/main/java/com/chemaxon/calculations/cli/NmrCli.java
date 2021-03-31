package com.chemaxon.calculations.cli;

import com.chemaxon.calculations.common.ProgressObserver;
import com.chemaxon.calculations.io.Segmenter;
import com.chemaxon.calculations.lambda.MoleculeFormats;
import com.chemaxon.calculations.lambda.NmrCalculator;
import com.chemaxon.calculations.lambda.NmrRequest;
import com.chemaxon.calculations.lambda.NmrResponse;
import com.chemaxon.calculations.lambda.NmrResult;
import com.chemaxon.calculations.util.CloseableLineIterator;
import com.chemaxon.calculations.util.CmdlineUtils;
import com.chemaxon.overlap.cli.invocation.CliInvocation;
import com.chemaxon.overlap.cli.invocation.CliInvocationEnv;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.io.PrintStream;
import java.util.Iterator;

/**
 * Command line entry point.
 *
 * @author Gabor Imre
 */
public class NmrCli {
    
    private static void launch(NmrCliParameters p, CliInvocationEnv env) throws Exception {
        env.verboseSection(
                "NMR CLI launched.",
                "",
                "    input location: " + p.in,
                "    input format:   " + p.inFormat,
                "    output:         " + p.out,
                "    SDF output:     " + p.sdfOut,
                "    JSONL output:   " + p.jsonlOut,
                "    maxCount:       " + (p.maxCount == null ? "read all input" : p.maxCount),
                "    writeTimes:     " + p.writeTimes
        );

        env.verbose(
                "Command line arguments:",
                "    " + env.getOriginalCliArguments().toString()
        );

        
        MoleculeFormats.ensureSupportedFormat(p.inFormat);
        
        final Segmenter inputSegmenter = MoleculeFormats.segmenterForFormat(p.inFormat);
        final NmrCalculator calc = new NmrCalculator();

        try (
            final ProgressObserver po = env.progressObserver("Importing from " + p.in);
            final CloseableLineIterator inputLines = CmdlineUtils.lineIteratorFromLocation(p.in, true);
            final PrintStream out = CmdlineUtils.printStreamFromLocation(p.out);
            final PrintStream sdfOutOrNull = CmdlineUtils.printStreamFromNullableLocation(p.sdfOut).orNull();
            final PrintStream jsonlOutOrNull = CmdlineUtils.printStreamFromNullableLocation(p.jsonlOut).orNull();
        ) { 
            final Iterator<String> inputStructureSources = inputSegmenter.plainStringSegments(inputLines);
            
            long readCount = 0;
            while (inputStructureSources.hasNext() && (p.maxCount == null || readCount < p.maxCount)) {
                final String nextInputStructureSource = inputStructureSources.next();
                readCount ++;
                
                if (p.verbose) {
                    env.verbose("Structure # " + readCount + ": " + nextInputStructureSource);
                }
                
                final NmrRequest req = NmrRequest.ofSingle(nextInputStructureSource, p.inFormat);
                final long startTime = System.currentTimeMillis();
                final NmrResponse resp = calc.handleRequest(req, null);
                final long time = System.currentTimeMillis() - startTime;
                final NmrResult res = resp.results.get(0);
                
                if (sdfOutOrNull != null) {
                    sdfOutOrNull.println(res.toSdf(ImmutableMap.of(
                        "nmr-calc-time", Long.toString(time)
                    )));
                }
                
                if (jsonlOutOrNull != null) {
                    jsonlOutOrNull.println(
                        new Gson().toJson(resp)
                    );
                }
                    
                final StringBuilder outLine = new StringBuilder();
                outLine
                        .append(readCount)
                        .append("\t")
                        .append(res.cnmrResult.size())
                        .append("\t")
                        .append(res.hnmrResult.size())
                        ;                    
                
                if (p.writeTimes) {
                    outLine.append("\t").append(time);
                }
                out.println(outLine.toString());
                            
                    
                po.worked(1);
                
                
            }
        } 

        
        
        env.verboseSection(
                "All done.",
                "",
                "Total execution time: " + env.getRunningTimeInHumanReadable()
        );
       
    }
    
    
    
    public static void main(String [] args) {
        CliInvocation
                .parseParameters(args, NmrCliParameters.class)
                .usage(NmrCliParameters::getCliHelp)
                .launch(NmrCli::launch);
                
    }
}
