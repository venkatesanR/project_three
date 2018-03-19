package com.addval.udf.api;

/**
 * This is the interface that must be implemented by application classes that
 * need to support user-defined fields.  Normally these will be "cfc" type
 * classes, such as Booking, Customer, Segment, ULD, or PieceDims.
 *
 */
public interface UdfEnabled
{
	/**
	 * Sets the UdfHolder.
	 */
	public void setUdfHolder(UdfHolder udfHolder);

	/**
	 * Returns the UdfHolder.
	 */
	public UdfHolder getUdfHolder();

}
