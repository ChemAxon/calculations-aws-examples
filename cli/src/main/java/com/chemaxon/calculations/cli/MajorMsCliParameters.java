package com.chemaxon.calculations.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;


/**
 * Parameters for {@link MajorMsCli}.
 * 
 * @author Gabor Imre
 */
public class MajorMsCliParameters {

    @Parameter(
            names = {"-h", "-help", "--help"}, 
            help = true, 
            description = "Print basic help on usage then exit."
    )
    public boolean help;
    
    @Parameter(
            names = {"-ph" },
            variableArity = true,
            description = "pH value(s) to use for all input structures. Multiple values can be specified."
    )
    public List<Double> ph = new ArrayList<>();
    
    @Parameter(
            names = {"-in"},
            required = true,
            description = "Input SMILES location. Gzipped input is recognized."
    )
    public String in;
    
    
    @Parameter(
            names = {"-out"},
            description = "Output location"
    )
    public String out = "-";
    
    @Parameter(
            names = {"-jsonl-out"},
            description = "Optipnal location for JSONL (one JSON line per structure) output."
    )
    public String jsonlOut = null;
    
    
    @Parameter(
           names = {"-max-count"},
           description = "Max structure count to read"
    )
    public Long maxCount;
    
    @Parameter(
           names = {"-write-times"},
           arity = 1,
           description = "Write calculation times (in ms)"
    )
    public boolean writeTimes = true;
    
    @Parameter(
            names = {"-v"},
            description = "Be more verbose"
    )
    public boolean verbose = false;
    
    
    public MajorMsCliParameters() {
        this.ph.add(7.4);
    }

    public static String getCliHelp() {
        final JCommander jc = new JCommander(new MajorMsCliParameters());
        jc.setProgramName("majorMsCli");
        final StringBuilder b = new StringBuilder();
        jc.usage(b);
        return b.toString();
    }
    
}
