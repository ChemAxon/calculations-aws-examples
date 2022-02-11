package com.chemaxon.calculations.cli;

import com.chemaxon.calculations.common.ProgressObserver;
import com.chemaxon.calculations.lambda.MsDistrCalculator;
import com.chemaxon.calculations.lambda.MsDistrRequest;
import com.chemaxon.calculations.lambda.MsDistrResponse;
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
public class MsDistrCli {

    private static void launch(MsDistrCliParameters p, CliInvocationEnv env) throws Exception {
        env.verboseSection(
                "MsDistr CLI launched.",
                "",
                "    input location: " + p.in,
                "    output:         " + p.out,
                "    JSONL output:   " + p.jsonlOut,
                "    pH:             " + p.ph.toString(),
                "    tautomerize:    " + p.tautomerize,
                "    maxCount:       " + (p.maxCount == null ? "read all input" : p.maxCount),
                "    writeTimes:     " + p.writeTimes);

        env.verbose(
                "Command line arguments:",
                "    " + env.getOriginalCliArguments().toString());

        final MsDistrCalculator calc = new MsDistrCalculator();

        try (
                final ProgressObserver po = env.progressObserver("Importing from " + p.in);
                final CloseableLineIterator inputLines = CmdlineUtils.lineIteratorFromLocation(p.in, true);
                final PrintStream out = CmdlineUtils.printStreamFromLocation(p.out);
                final PrintStream jsonlOutOrNull = CmdlineUtils.printStreamFromNullableLocation(p.jsonlOut).orNull();) {
            long readCount = 0;
            while (inputLines.hasNext() && (p.maxCount == null || readCount < p.maxCount)) {
                final String nextInLine = inputLines.next();
                readCount++;

                if (p.verbose) {
                    env.verbose("Structure # " + readCount + ": " + nextInLine);
                }

                for (double pH : p.ph) {
                    final MsDistrRequest req = MsDistrRequest.ofSingle(nextInLine, pH, p.tautomerize);

                    final long startTime = System.currentTimeMillis();
                    final MsDistrResponse res = calc.handleRequest(req, null);
                    final long time = System.currentTimeMillis() - startTime;

                    if (jsonlOutOrNull != null) {
                        jsonlOutOrNull.println(
                                new Gson().toJson(res));
                    }

                    final StringBuilder outLine = new StringBuilder();
                    outLine
                            .append(nextInLine)
                            .append("\t")
                            .append(pH)
                            .append("\t")
                            .append(res.results.get(0).microspecies);

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
                "Total execution time: " + env.getRunningTimeInHumanReadable());

    }

    public static void main(String[] args) {
        CliInvocation
                .parseParameters(args, MsDistrCliParameters.class)
                .usage(MsDistrCliParameters::getCliHelp)
                .launch(MsDistrCli::launch);

    }
}
