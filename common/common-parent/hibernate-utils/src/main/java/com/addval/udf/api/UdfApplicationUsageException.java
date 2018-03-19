package com.addval.udf.api;

/**
 * UdfRuntimeException
 *
 */
public class UdfApplicationUsageException extends RuntimeException
{
	public UdfApplicationUsageException( String msg ) {
		super(msg);
	}

	public UdfApplicationUsageException( String msg, Exception ex ) {
		super(msg, ex);
	}

	//@@@TODO  - other construtors?

}
