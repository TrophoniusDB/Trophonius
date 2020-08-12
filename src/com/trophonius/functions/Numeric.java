package com.trophonius.functions;

public class Numeric {

    // Rounding doubles with decimals, like round() in sql
    public static double round(double n, int dec) {
        return Math.round(n * Math.pow(10, dec)) / (double) Math.pow(10, dec);
    }

    // Rounding floats with decimals, like round() in sql
    public static float round(float n, int dec) {
        return Math.round(n * Math.pow(10, dec)) / (float) Math.pow(10, dec);
    }

}
