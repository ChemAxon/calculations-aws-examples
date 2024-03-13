package com.chemaxon.calculations.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import com.chemaxon.calculations.lambda.majorms.MajorMsCalculator;
import com.chemaxon.calculations.lambda.majorms.MajorMsRequest;
import com.chemaxon.calculations.lambda.majorms.MajorMsResponse;

/**
 * Command line entry point.
 *
 * @author Gabor Imre
 */
public class MajorMsCli {

    public static void main(String[] args) throws IOException {
        MajorMsCliParameters p = new MajorMsCliParameters();

        JCommander jc = new JCommander(p);
        try {
            jc.parse(args);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            jc.usage();
            return;
        }
        if (p.help) {
            jc.usage();
            return;
        }

        Util.verboseSection(
                "MajorMS CLI launched.",
                "",
                "    input location: " + p.in,
                "    output:         " + p.out,
                "    JSONL output:   " + p.jsonlOut,
                "    pH:             " + p.ph.toString(),
                "    maxCount:       " + (p.maxCount == null ? "read all input" : p.maxCount),
                "    writeTimes:     " + p.writeTimes
        );

        Util.verbose("Command line arguments: ", "    " + Arrays.toString(args));

        MajorMsCalculator calc = new MajorMsCalculator();
        long globalStartTime = System.currentTimeMillis();
        try (
                BufferedReader br = Util.openBufferedReader(p.in);
                PrintStream out = p.out != null ? new PrintStream(p.out) : null;
                PrintStream jsonlOutOrNull = p.jsonlOut != null ? new PrintStream(p.jsonlOut) : null
        ) {
            int readCount = 0;
            String nextInLine;
            while ((nextInLine = br.readLine()) != null && (p.maxCount == null || readCount < p.maxCount)) {
                readCount++;

                if (p.verbose) {
                    Util.verbose("Structure # " + readCount + ": " + nextInLine);
                }

                for (double pH : p.ph) {
                    MajorMsRequest req = MajorMsRequest.ofSingle(nextInLine, pH);

                    long startTime = System.currentTimeMillis();
                    MajorMsResponse res = calc.handleRequest(req, null);
                    long time = System.currentTimeMillis() - startTime;

                    if (jsonlOutOrNull != null) {
                        jsonlOutOrNull.println(new Gson().toJson(res));
                    }

                    StringBuilder outLine = new StringBuilder();
                    outLine
                            .append(nextInLine)
                            .append("\t")
                            .append(res.resultSmiles.get(0))
                            .append("\t")
                            .append(pH);

                    if (p.writeTimes) {
                        outLine.append("\t").append(time);
                    }
                    if (out != null) {
                        out.println(outLine);
                    } else {
                        System.out.println(outLine);
                    }
                }
            }
        }

        Util.verboseSection(
                "All done.",
                "",
                "Total execution time: " + Util.timeToHumanReadable(System.currentTimeMillis() - globalStartTime)
        );
    }

}
