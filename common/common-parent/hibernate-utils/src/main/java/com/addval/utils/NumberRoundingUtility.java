/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */
package com.addval.utils;

// class to do good control over the way a double value is rounded.
public class NumberRoundingUtility
{
    private static String _UP = "UP";
    private static String _DOWN = "DOWN";
    private static String _STANDARD = "STANDARD";

    // friendly method to invoke the right way to round based on rule name
    public static double round(double value, String rule, double precision)
    {
        if(StrUtl.equals(rule, _UP))
            return roundUp(value, precision);

        if(StrUtl.equals(rule, _DOWN))
            return roundDown(value, precision);
        else if(StrUtl.equals(rule, _STANDARD))
            return roundStandard(value, precision);

        return round(value);
    }

    // friendly method to do the normal rounding with two decimal places
    public static double round(double value)
    {
        return roundStandard(value, 0.01d);
    }

    public static double roundStandard(double value, double precision)
    {
        double half = precision / 2.0d;
        double remainder = value % precision;
        double roundval = value - remainder;
        if(MathUtl.isZero(half - remainder) || (remainder > half))
			roundval  += precision;
		return MathUtl.round(roundval, 3);
    }

    public static double roundUp(double value, double precision)
    {
        double remainder = value % precision;
        double roundval = 0.0d;
        if(!MathUtl.isZero(remainder))
        {
            roundval = value - remainder;
            roundval += precision;
        }
        else
            roundval = value;

        return MathUtl.round(roundval, 3);
    }

    public static double roundDown(double value, double precision)
    {
        long rndval = (long) (value / precision);
        value = rndval * precision;
        return MathUtl.round(value, 3);
    }

    public static final void main(String[] args)
    {
        double precisionValue1 = 0.001d;
        double precisionValue2 = 0.005d;
        double precisionValue3 = 1d;
        double precisionValue4 = 5d;
        double stanInputValue1 = 104.9995d;
        double stanInputValue2 = 105.0025d;
        double stanInputValue3 = 104.5d;
        double stanInputValue4 = 102.5d;
        double upInputValue1 = 105.0010d;
        double upInputValue2 = 105.0001d;
        double upInputValue3 = 104.1d;
        double upInputValue4 = 108d;
        double downInputValue1 = 105.0014d;
        double downInputValue2 = 105.0047d;
        double downInputValue3 = 104.8d;
        double downInputValue4 = 102d;
        System.out.println("\n\n");
        System.out.println("================== UP ======================= ");
        System.out.println("\n\n");
        System.out.println("precisionValue1 ============ " + precisionValue1);
        System.out.println("upInputValue1 ============ " + upInputValue1);
        System.out.println("Output ============ " + round(upInputValue1, _UP, precisionValue1));
        System.out.println("\n\n");
        System.out.println("precisionValue2 ============ " + precisionValue2);
        System.out.println("upInputValue2 ============ " + upInputValue2);
        System.out.println("Output ============ " + round(upInputValue2, _UP, precisionValue2));
        System.out.println("\n\n");
        System.out.println("precisionValue3 ============ " + precisionValue3);
        System.out.println("upInputValue3 ============ " + upInputValue3);
        System.out.println("Output ============ " + round(upInputValue3, _UP, precisionValue3));
        System.out.println("\n\n");
        System.out.println("precisionValue4 ============ " + precisionValue4);
        System.out.println("upInputValue4 ============ " + upInputValue4);
        System.out.println("Output ============ " + round(upInputValue4, _UP, precisionValue4));
        System.out.println("\n\n");
        System.out.println("================== DOWN ======================= ");
        System.out.println("\n\n");
        System.out.println("precisionValue1 ============ " + precisionValue1);
        System.out.println("downInputValue1 ============ " + downInputValue1);
        System.out.println("Output ============ " + round(downInputValue1, _DOWN, precisionValue1));
        System.out.println("\n\n");
        System.out.println("precisionValue2 ============ " + precisionValue2);
        System.out.println("downInputValue2 ============ " + downInputValue2);
        System.out.println("Output ============ " + round(downInputValue2, _DOWN, precisionValue2));
        System.out.println("\n\n");
        System.out.println("precisionValue3 ============ " + precisionValue3);
        System.out.println("downInputValue3 ============ " + downInputValue3);
        System.out.println("Output ============ " + round(downInputValue3, _DOWN, precisionValue3));
        System.out.println("\n\n");
        System.out.println("precisionValue4 ============ " + precisionValue4);
        System.out.println("downInputValue4 ============ " + downInputValue4);
        System.out.println("Output ============ " + round(downInputValue4, _DOWN, precisionValue4));
        System.out.println("\n\n");
        System.out.println("================== STANDARD ======================= ");
        System.out.println("\n\n");
        System.out.println("precisionValue1 ============ " + precisionValue1);
        System.out.println("stanInputValue1 ============ " + stanInputValue1);
        System.out.println("Output ============ " + round(stanInputValue1, _STANDARD, precisionValue1));
        System.out.println("\n\n");
        System.out.println("precisionValue2 ============ " + precisionValue2);
        System.out.println("stanInputValue2 ============ " + stanInputValue2);
        System.out.println("Output ============ " + round(stanInputValue2, _STANDARD, precisionValue2));
        System.out.println("\n\n");
        System.out.println("precisionValue1 ============ " + precisionValue3);
        System.out.println("stanInputValue1 ============ " + stanInputValue3);
        System.out.println("Output ============ " + round(stanInputValue3, _STANDARD, precisionValue3));
        System.out.println("\n\n");
        System.out.println("precisionValue1 ============ " + precisionValue4);
        System.out.println("stanInputValue1 ============ " + stanInputValue4);
        System.out.println("Output ============ " + round(stanInputValue4, _STANDARD, precisionValue4));
    }
}
