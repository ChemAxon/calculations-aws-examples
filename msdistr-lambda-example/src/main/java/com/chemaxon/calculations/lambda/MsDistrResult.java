package com.chemaxon.calculations.lambda;

import java.util.List;

public class MsDistrResult {

    public String input;
    public double pH;
    public List<Ms> microspecies;

    public static class Ms {
        public String ms;
        public double distribution;

        public static Ms of(String ms, double distribution) {
            Ms ret = new Ms();
            ret.ms = ms;
            ret.distribution = distribution;
            return ret;
        }

        @Override
        public String toString() {
            return ms + " [" + String.format("%.2f", distribution) + "]";
        }
    }
}
