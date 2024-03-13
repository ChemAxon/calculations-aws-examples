package com.chemaxon.calculations.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import com.chemaxon.calculations.lambda.common.MoleculeFormats;
import com.chemaxon.calculations.lambda.nmr.NmrCalculator;
import com.chemaxon.calculations.lambda.nmr.NmrRequest;
import com.chemaxon.calculations.lambda.nmr.NmrResult;

/**
 * Command line entry point.
 *
 * @author Gabor Imre
 */
public class NmrCli {

    public static void main(String[] args) throws IOException {
        NmrCliParameters p = new NmrCliParameters();

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
                "NMR CLI launched.",
                "",
                "    input location: " + p.in,
                "    output:         " + p.out,
                "    SDF output:     " + p.sdfOut,
                "    JSONL output:   " + p.jsonlOut,
                "    maxCount:       " + (p.maxCount == null ? "read all input" : p.maxCount),
                "    writeTimes:     " + p.writeTimes
        );

        Util.verbose("Command line arguments: ", "    " + Arrays.toString(args));

        NmrCalculator calc = new NmrCalculator();
        long globalStartTime = System.currentTimeMillis();
        try (
                BufferedReader br = Util.openBufferedReader(p.in);
                PrintStream out = p.out != null ? new PrintStream(p.out) : null;
                PrintStream sdfOutOrNull = p.sdfOut != null ? new PrintStream(p.sdfOut) : null;
                PrintStream jsonlOutOrNull = p.jsonlOut != null ? new PrintStream(p.jsonlOut) : null
        ) {
            long readCount = 0;
            String nextInLine;
            while ((nextInLine = br.readLine()) != null && (p.maxCount == null || readCount < p.maxCount)) {
                readCount++;

                if (p.verbose) {
                    Util.verbose("Structure # " + readCount + ": " + nextInLine);
                }

                NmrRequest req = NmrRequest.ofSingle(nextInLine, "smiles");

                long startTime = System.currentTimeMillis();
                NmrResult res = calc.handleRequest(req, null).results.get(0);
                long time = System.currentTimeMillis() - startTime;

                if (sdfOutOrNull != null) {
                    sdfOutOrNull.println(res.toSdf(Map.of("nmr-calc-time", Long.toString(time))));
                }

                if (jsonlOutOrNull != null) {
                    jsonlOutOrNull.println(new Gson().toJson(calc.handleRequest(req, null)));
                }

                StringBuilder outLine = new StringBuilder();
                outLine
                        .append(readCount)
                        .append("\t")
                        .append(res.cnmrResult.size())
                        .append("\t")
                        .append(res.hnmrResult.size());

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
