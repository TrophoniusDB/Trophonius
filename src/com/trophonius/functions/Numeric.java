package com.trophonius.functions;

/**
 * Class for numeric functions
 */

public class Numeric {

    /**
     * Rounding doubles with decimals, like round() in sql
     * @param n double to be rounded
     * @param dec no of decimal to round to
     * @return rounded double
     */
    public static double round(double n, int dec) {
        return Math.round(n * Math.pow(10, dec)) / Math.pow(10, dec);
    }

    /**
     * Rounding floats with decimals, like round() in sql
     * @param n float to be rounded
     * @param dec no of decimal to round to
     * @return rounded float
     */
    public static float round(float n, int dec) {
        return Math.round(n * Math.pow(10, dec)) / (float) Math.pow(10, dec);
    }

}
