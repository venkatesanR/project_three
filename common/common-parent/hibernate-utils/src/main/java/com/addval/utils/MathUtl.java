/*
 * MathUtl.java
 *
 * Created on June 25, 2003, 5:07 PM
 */

package com.addval.utils;

/**
 *
 * @author  ravi
 */
public class MathUtl
{
	public static final double _FUDGE_FACTOR = 1.0E-8;

	/** Creates a new instance of MathUtl */
	public MathUtl()
	{
	}

	public static boolean isZero(double val)
	{
		return (Double.isNaN(val) || Math.abs(val) < _FUDGE_FACTOR);
	}

	public static boolean isZero(Double val)
	{
		if(val == null)return true;
		return MathUtl.isZero(val.doubleValue());
	}

	public static boolean equals(double d1, double d2)
	{
		if(Double.isNaN(d1) || Double.isNaN(d2))return false;

		return (Math.abs(d1 - d2) < _FUDGE_FACTOR);
	}

	public static boolean equals(Double d1, Double d2)
	{
		if(d1 == null && d2 == null)return true;
		if((d1 == null && d2 != null) || (d1 != null && d2 == null))return false;

		return MathUtl.equals(d1.doubleValue(), d2.doubleValue());
	}

	public static boolean equals(double d1, double d2, int precision)
	{
		if(Double.isNaN(d1) || Double.isNaN(d2))return false;

		return (Math.abs(d1 - d2) < Math.pow(0.1, precision));
	}

	public static boolean equals(Double d1, Double d2, int precision)
	{
		if(d1 == null && d2 == null)return true;
		if((d1 == null && d2 != null) || (d1 != null && d2 == null))return false;

		return MathUtl.equals(d1.doubleValue(), d2.doubleValue(), precision);
	}

 /**
    * Compares two double numbers
    * Behavior same as Double.compareTo method
    * @param d1 first double value
    * @param d2 second double value
    * @return int a negative integer, zero, or a positive integer as d1 is less than, equal to, or greater than d2.
    */
   public static int compare(Double d1, Double d2)
   {
	   if(d1 == null && d2 == null)return 0;
	   if(d1 == null && d2 != null)return -1;
	   if(d1 != null && d2 == null)return +1;
	   return compare( d1.doubleValue(), d2.doubleValue() );
   }

   /**
    * Compares two double numbers
    * Behavior same as Double.compareTo method
    * @param d1 first double value
    * @param d2 second double value
    * @return int a negative integer, zero, or a positive integer as d1 is less than, equal to, or greater than d2.
    */
   public static int compare(double d1, double d2)
   {
      if (d1 < d2) return -1;		 // Neither val is NaN, d1 is smaller
      if (d1 > d2) return 1;		 // Neither val is NaN, d1 is larger

      long d1Bits = Double.doubleToLongBits(d1);
      long d2Bits = Double.doubleToLongBits(d2);

        return (d1Bits == d2Bits ?  0 : // Values are equal
                (d1Bits < d2Bits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                 1));                   // (0.0, -0.0) or (NaN, !NaN)
   }

	/**
	* Compares two double numbers
	* Behavior similar to Double.compareTo method but the numbers are deemed equal
	* if isZero() returns true for the difference in numbers
	* @param d1 first double value
	* @param d2 second double value
	* @return int a negative integer, zero, or a positive integer as d1 is less than, equal to, or greater than d2.
	*/
	public static int compareFudge(double d1, double d2)
	{
		if(MathUtl.isZero(d1 - d2))return 0;
		return MathUtl.compare(d1, d2);
	}

	/**
	* Rounds the parameter value to the precision and returns the same.
	* Maximum precision is 9.
	* Ex. round(0.234, 2) returns 0.23 and round(0.235, 2) returns 0.24.
	*/
	public static double round(double value, int precision)
	{
		if(precision <= 0){
			return java.lang.Math.rint(value);
		}
		if(precision > 9){
			return value;
		}
		double multiple = java.lang.Math.pow(10.0, precision);

		double val = value * multiple;
		if(Double.isInfinite(val) || Double.isNaN(val))return value;

		return (java.lang.Math.rint(val) / multiple);
	}

	public static int compare(long l1, long l2)
	{
		return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
	}

	public static int compare(int i1, int i2)
	{
		return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
	}

	public static double nvl( Double d ) {

		return d == null ? 0d : d.doubleValue();
	}

	public static int nvl( Integer i ) {

		return i == null ? 0 : i.intValue();
	}

	public static double nvl( Double d, double dValue ) {

		return d == null ? dValue : d.doubleValue();
	}

	public static int nvl( Integer i, int intValue ) {

		return i == null ? intValue : i.intValue();
	}
    
    /** returns sign of a number as +-1, 0 is returned as zero */
    public static int sign(long val)
    {
        return val > 0 ? 1 : (val < 0 ? -1 : 0);
    }
    
    /** returns sign of a number as +-1, 0 is returned as zero */
    public static int sign(int val)
    {
        return val > 0 ? 1 : (val < 0 ? -1 : 0);
    }    
    
    /** returns sign of a number as +-1, 0 is returned as zero */
    public static int sign(double val)
    {
        return val > 0 ? 1 : (val < 0 ? -1 : 0);
    }    
    
    /** returns sign of a number as +-1, 0 is returned as zero */
    public static int sign(float val)
    {
        return val > 0 ? 1 : (val < 0 ? -1 : 0);
    }    
}
