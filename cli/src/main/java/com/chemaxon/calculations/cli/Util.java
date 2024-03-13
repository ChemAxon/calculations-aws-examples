package com.chemaxon.calculations.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

class Util {

    /**
     * Returns a new input stream for the given path. Gzipped input is recognized and handled.
     */
    static BufferedReader openBufferedReader(String path) throws IOException {
        InputStream in = Files.newInputStream(Paths.get(path));
        return new BufferedReader(new InputStreamReader(path.endsWith(".gz") ? new GZIPInputStream(in) : in));
    }

    /**
     * Prints verbose information.
     */
    static void verbose(String... lines) {
        for (String line : lines) {
            System.err.println(line);
        }
    }

    /**
     * Prints verbose information.
     */
    static void verboseSection(String... lines) {
        verbose("", "", "");
        verbose("*********************************************************************************");
        verbose("*");
        for (String line : lines) {
            verbose("* " + line);
        }
        verbose("*");
        verbose("*********************************************************************************");
        verbose("", "");
    }

    /**
     * Converts a duration given in milliseconds to a human-readable string.
     */
    static String timeToHumanReadable(long t) {
        if (t == 0) {
            return "0";
        } else if (t < 1000) {
            return t + " ms";
        } else if (t < 60e3) {
            return String.format(Locale.US, "%.2f s", (double) t / 1e3);
        } else {
            long s = t / 1000;

            long h = s / 3600;
            s -= h * 3600;

            long m = s / 60;
            s -= m * 60;

            return h > 0
                    ? h + " h " + m + " m " + s + " s"
                    : m + " m " + s + " s";
        }
    }

}
