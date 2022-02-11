package com.chemaxon.calculations.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Parameters for {@link MsDistrCli}.
 * 
 * @author Laszlo Antal
 */
public class MsDistrCliParameters {

    @Parameter(
            names = { "-h", "-help", "--help" },
            help = true,
            description = "Print basic help on usage then exit.")
    public boolean help;

    @Parameter(
            names = { "-ph" },
            variableArity = true,
            description = "pH value to use for all input structures.")
    public double ph = 7.4;

    @Parameter(
            names = { "-tautomerize" },
            description = "Consoder tautomerization and resonance.")
    public boolean tautomerize = false;

    @Parameter(
            names = { "-temperature" },
            description = "Temperature in Kelvin.")
    public double temperature = 298.0;

    @Parameter(
            names = { "-in" },
            required = true,
            description = "Input SMILES location. Gzipped input is recognized.")
    public String in;

    @Parameter(
            names = { "-out" },
            description = "Output location")
    public String out = "-";

    @Parameter(
            names = { "-jsonl-out" },
            description = "Optipnal location for JSONL (one JSON line per structure) output.")
    public String jsonlOut = null;

    @Parameter(
            names = { "-max-count" },
            description = "Max structure count to read")
    public Long maxCount;

    @Parameter(
            names = { "-write-times" },
            arity = 1,
            description = "Write calculation times (in ms)")
    public boolean writeTimes = true;

    @Parameter(
            names = { "-v" },
            description = "Be more verbose")
    public boolean verbose = false;

    public static String getCliHelp() {
        final JCommander jc = new JCommander(new MsDistrCliParameters());
        jc.setProgramName("msDistrCli");
        final StringBuilder b = new StringBuilder();
        jc.usage(b);
        return b.toString();
    }

}
