package com.chemaxon.calculations.cli;

import com.beust.jcommander.Parameter;

/**
 * Parameters for {@link NmrCli}.
 *
 * @author Gabor Imre
 */
public class NmrCliParameters {

    @Parameter(
            names = { "-h", "-help", "--help" },
            help = true,
            description = "Print basic help on usage then exit."
    )
    public boolean help;

    @Parameter(
            names = "-in",
            required = true,
            description = "Input SMILES location. Gzipped input is recognized."
    )
    public String in;

    @Parameter(
            names = "-out",
            description = "Output location for statistics."
    )
    public String out = null;

    @Parameter(
            names = "-sdf-out",
            description = "Optional location for SDF output."
    )
    public String sdfOut = null;

    @Parameter(
            names = "-jsonl-out",
            description = "Optional location for JSONL (one JSON line per structure) output."
    )
    public String jsonlOut = null;

    @Parameter(
            names = "-max-count",
            description = "Max structure count to read"
    )
    public Long maxCount;

    @Parameter(
            names = "-write-times",
            arity = 1,
            description = "Write calculation times (in ms)"
    )
    public boolean writeTimes = true;

    @Parameter(
            names = "-v",
            description = "Be more verbose"
    )
    public boolean verbose = false;

}
