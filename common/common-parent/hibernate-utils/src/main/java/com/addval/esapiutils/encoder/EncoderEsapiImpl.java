package com.addval.esapiutils.encoder;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import com.addval.esapiutils.encoder.AppSecurityEncoderException;

public class EncoderEsapiImpl implements Encoder {
private static transient final org.apache.log4j.Logger logger = Logger.getLogger("EncoderEsapiImpl.java");
	
	public String encodeForSafeHTML(String inputStr) throws AppSecurityEncoderException{
		
		String safeHtmlString=null;
		try{
			safeHtmlString= ESAPI.encoder().encodeForHTML( inputStr );
			logger.info("safeHtmlString "+safeHtmlString);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityEncoderException(e);
		}
		
		return safeHtmlString;
	}
	
	
	public String encodeForSafeHTMLAttribute(String inputStr) throws AppSecurityEncoderException{
		
		String safeHtmlAttrString=null;
		try{
			safeHtmlAttrString= ESAPI.encoder().encodeForHTMLAttribute( inputStr );
			logger.info("safeHtmlAttrString "+safeHtmlAttrString);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityEncoderException(e);
		}
		
		return safeHtmlAttrString;
	}

	
	public String encodeForSafeJavaScript(String inputStr) throws AppSecurityEncoderException{
		
		String safeJSString=null;
		try{
			safeJSString= ESAPI.encoder().encodeForJavaScript( inputStr );
			logger.info("safeHtmlAttrString "+safeJSString);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityEncoderException(e);
		}
		
		return safeJSString;
	}

	
	public String encodeForSafeCSS(String inputStr) throws AppSecurityEncoderException{
		
		String safeCssString=null;
		try{
			safeCssString= ESAPI.encoder().encodeForCSS( inputStr );
			logger.info("safeCssString "+safeCssString);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityEncoderException(e);
		}		
		return safeCssString;
	}

	
	public String encodeForSafeURL(String inputStr) throws AppSecurityEncoderException{
		
		String safeURLString=null;
		try{
			safeURLString= ESAPI.encoder().encodeForURL(inputStr );
			logger.info("safeURLString "+safeURLString);
		}
		catch(EncodingException e){
			logger.error("error",e);
			throw new AppSecurityEncoderException(e);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityEncoderException(e);
		}		
		return safeURLString;
	}

}
