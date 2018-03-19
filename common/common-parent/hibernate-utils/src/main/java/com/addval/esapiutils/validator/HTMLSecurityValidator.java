package com.addval.esapiutils.validator;

public interface HTMLSecurityValidator {
	public String getValidSafeHTML(final String context, final String inputStr) throws AppSecurityValidatorException;	
	public String getValidInput(final String context, final String inputStr) throws AppSecurityValidatorException;	
	public String getValidRedirectLocation( final String context, final String inputURL) throws AppSecurityValidatorException;

 }
