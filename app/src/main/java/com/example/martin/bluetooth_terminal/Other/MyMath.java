package com.example.martin.bluetooth_terminal.Other;

/**
 * Created by martinberan on 19.12.16.
 */

public class MyMath {

    public static double expo(double zakldad, int exponent){
        double result = zakldad;
        for (int i = 1; i < exponent; i++){
            result = result * zakldad;
        }
        return result;
    }
}
