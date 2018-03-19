package com.addval.esapiutils.validator;

import java.io.File;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.apache.log4j.Logger;

import com.addval.utils.LogMgr;

public class HTMLSecurityValidatorESAPIImpl implements HTMLSecurityValidator {
	private static transient final Logger logger = LogMgr.getLogger(HTMLSecurityValidatorESAPIImpl.class);


	public String getValidSafeHTML(String context, String inputStr) throws AppSecurityValidatorException {
		
		String safeHTMLStr= null;			
		
		try{
			safeHTMLStr= ESAPI.validator().getValidSafeHTML(context, inputStr, SecurityValidatorConstants.SAFE_HTML_MAX_LENGTH, SecurityValidatorConstants.ALLOW_NULL);
			logger.info("safeHTMLStr"+safeHTMLStr);
		}
		catch (ValidationException e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch(IntrusionException e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		
		return safeHTMLStr;
	}


	public String getValidInput(String context, String inputStr)
			throws AppSecurityValidatorException {
		String vaildInput= null;
		try{
			File f = new File(inputStr);
			if(f.exists() && !f.isDirectory()) { 
				vaildInput=inputStr;
			}
			else
			{
			vaildInput=ESAPI.validator().getValidInput(context, inputStr, SecurityValidatorConstants.INPUT_TYPE_PROPERTY, SecurityValidatorConstants.SAFE_INPUT_MAX_LENGTH, SecurityValidatorConstants.ALLOW_NULL);
			}
			
			logger.info("vaildInput"+vaildInput);
		}
		catch (ValidationException e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch (IntrusionException e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		return vaildInput;
	}


	public String getValidRedirectLocation(String context, String inputURL)
			throws AppSecurityValidatorException {
		
		String redirectlocation=null;
		try{
			redirectlocation=ESAPI.validator().getValidRedirectLocation(context, inputURL, SecurityValidatorConstants.ALLOW_NULL);
			logger.info("redirectlocation"+redirectlocation);
		}
		catch (ValidationException e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch (IntrusionException e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		
		
		return redirectlocation;
	}

}
