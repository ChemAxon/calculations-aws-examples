package com.chemaxon.calculations.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import com.chemaxon.calculations.lambda.msdistr.MsDistrCalculator;
import com.chemaxon.calculations.lambda.msdistr.MsDistrRequest;
import com.chemaxon.calculations.lambda.msdistr.MsDistrResponse;

/**
 * Command line entry point.
 *
 * @author Laszlo Antal
 * @author Gabor Imre
 */
public class MsDistrCli {

    public static void main(String[] args) throws IOException {
        MsDistrCliParameters p = new MsDistrCliParameters();

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
                "MsDistr CLI launched.",
                "",
                "    input location: " + p.in,
                "    output:         " + p.out,
                "    JSONL output:   " + p.jsonlOut,
                "    pH:             " + p.ph,
                "    tautomerize:    " + p.tautomerize,
                "    temperature:    " + p.temperature,
                "    maxCount:       " + (p.maxCount == null ? "read all input" : p.maxCount),
                "    writeTimes:     " + p.writeTimes
        );

        Util.verbose("Command line arguments: ", "    " + Arrays.toString(args));

        MsDistrCalculator calc = new MsDistrCalculator();
        long globalStartTime = System.currentTimeMillis();
        try (
                BufferedReader br = Util.openBufferedReader(p.in);
                PrintStream out = p.out != null ? new PrintStream(p.out) : null;
                PrintStream jsonlOutOrNull = p.jsonlOut != null ? new PrintStream(p.jsonlOut) : null
        ) {
            long readCount = 0;
            String nextInLine;
            while ((nextInLine = br.readLine()) != null && (p.maxCount == null || readCount < p.maxCount)) {
                readCount++;

                if (p.verbose) {
                    Util.verbose("Structure # " + readCount + ": " + nextInLine);
                }

                MsDistrRequest req = MsDistrRequest.ofSingle(nextInLine, p.ph, p.tautomerize, p.temperature);

                long startTime = System.currentTimeMillis();
                MsDistrResponse res = calc.handleRequest(req, null);
                long time = System.currentTimeMillis() - startTime;

                if (jsonlOutOrNull != null) {
                    jsonlOutOrNull.println(new Gson().toJson(res));
                }

                StringBuilder outLine = new StringBuilder();
                outLine
                        .append(nextInLine)
                        .append("\t")
                        .append(res.results.get(0).microspecies);

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

        Util.verboseSection(
                "All done.",
                "",
                "Total execution time: " + Util.timeToHumanReadable(System.currentTimeMillis() - globalStartTime)
        );

    }

}
