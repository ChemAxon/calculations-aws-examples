package com.chemaxon.calculations.cli;

import com.chemaxon.calculations.common.ProgressObserver;
import com.chemaxon.calculations.lambda.MajorMsCalculator;
import com.chemaxon.calculations.lambda.MajorMsRequest;
import com.chemaxon.calculations.lambda.MajorMsResponse;
import com.chemaxon.calculations.util.CloseableLineIterator;
import com.chemaxon.calculations.util.CmdlineUtils;
import com.chemaxon.overlap.cli.invocation.CliInvocation;
import com.chemaxon.overlap.cli.invocation.CliInvocationEnv;
import com.google.gson.Gson;
import java.io.PrintStream;

/**
 * Command line entry point.
 *
 * @author Gabor Imre
 */
public class MajorMsCli {
    
    private static void launch(MajorMsCliParameters p, CliInvocationEnv env) throws Exception {
        env.verboseSection(
                "MajorMS CLI launched.",
                "",
                "    input location: " + p.in,
                "    output:         " + p.out,
                "    JSONL output:   " + p.jsonlOut,
                "    pH:             " + p.ph.toString(),
                "    maxCount:       " + (p.maxCount == null ? "read all input" : p.maxCount),
                "    writeTimes:     " + p.writeTimes
        );

        env.verbose(
                "Command line arguments:",
                "    " + env.getOriginalCliArguments().toString()
        );

        
        final MajorMsCalculator calc = new MajorMsCalculator();

        try (
            final ProgressObserver po = env.progressObserver("Importing from " + p.in);
            final CloseableLineIterator inputLines = CmdlineUtils.lineIteratorFromLocation(p.in, true);
            final PrintStream out = CmdlineUtils.printStreamFromLocation(p.out);
            final PrintStream jsonlOutOrNull = CmdlineUtils.printStreamFromNullableLocation(p.jsonlOut).orNull();
        ) { 
            long readCount = 0;
            while (inputLines.hasNext() && (p.maxCount == null || readCount < p.maxCount)) {
                final String nextInLine = inputLines.next();
                readCount ++;
                
                if (p.verbose) {
                    env.verbose("Structure # " + readCount + ": " + nextInLine);
                }
                
                for (double pH : p.ph) {
                    final MajorMsRequest req = MajorMsRequest.ofSingle(nextInLine, pH);
                
                    final long startTime = System.currentTimeMillis();
                    final MajorMsResponse res = calc.handleRequest(req, null);
                    final long time = System.currentTimeMillis() - startTime;
                    
                    
                    if (jsonlOutOrNull != null) {
                        jsonlOutOrNull.println(
                            new Gson().toJson(res)
                        );
                    }
                    
                    final StringBuilder outLine = new StringBuilder();
                    outLine
                            .append(nextInLine)
                            .append("\t")
                            .append(res.resultSmiles.get(0))
                            .append("\t")
                            .append(pH);
                    
                    if (p.writeTimes) {
                        outLine.append("\t").append(time);
                    }
                    out.println(outLine.toString());
                            
                    
                    po.worked(1);
                }
                
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
                .parseParameters(args, MajorMsCliParameters.class)
                .usage(MajorMsCliParameters::getCliHelp)
                .launch(MajorMsCli::launch);
                
    }
}
