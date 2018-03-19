package com.addval.esapiutils.validator;

import java.io.InputStream;

import com.addval.esapiutils.encoder.AppSecurityEncoderException;

public interface FileSecurityValidator {
	public byte[] getValidFileContent(final String ctx, final byte[] fileInput ) throws AppSecurityValidatorException;
	public String getValidFileName(final String ctx, final String inputStr ) throws AppSecurityValidatorException;
	//public String getValidDiectoryPath(final String ctx, final String inputStr, File parent ) throws AppSecurityValidatorException;
	public String safeReadLine(InputStream inputStream) throws AppSecurityValidatorException;
                              
}


