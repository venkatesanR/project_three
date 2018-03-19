package com.addval.esapiutils.encoder;



public interface Encoder {

	public String encodeForSafeHTML(final String inputStr) throws AppSecurityEncoderException;
	public String encodeForSafeHTMLAttribute(final String inputStr)throws AppSecurityEncoderException;
	public String encodeForSafeJavaScript(final String inputStr)throws AppSecurityEncoderException;
	public String encodeForSafeCSS(final String inputStr)throws AppSecurityEncoderException;
	public String encodeForSafeURL(final String inputStr)throws AppSecurityEncoderException;
	//public String encodeForSafeSQL(final String inputStr)throws AppSecurityEncoderException;
}
