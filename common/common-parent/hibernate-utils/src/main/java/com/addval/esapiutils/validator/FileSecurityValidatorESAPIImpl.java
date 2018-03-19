package com.addval.esapiutils.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import com.addval.utils.LogMgr;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;

public class FileSecurityValidatorESAPIImpl implements FileSecurityValidator{

	private static final boolean ALLOW_NULL=true;
	private static transient final Logger logger = LogMgr.getLogger(FileSecurityValidatorESAPIImpl.class);
	
	public byte[] getValidFileContent(String ctx, byte[] fileInput) throws AppSecurityValidatorException {
		
		byte[] validFileContent=null;
		
		try{
			validFileContent=ESAPI.validator().getValidFileContent(ctx, fileInput, SecurityValidatorConstants.FILE_CONTAINTENT_SIZE, ALLOW_NULL);
			logger.info("validFileContent   "+validFileContent);
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
		return validFileContent;
	}

	
	public String getValidFileName(String ctx, String inputStr ) throws AppSecurityValidatorException {
		String valideFileName=null;
		List<String> allowedExtns=new ArrayList<String>();
		
		allowedExtns.add("doc");
		allowedExtns.add("zip");
		allowedExtns.add("txt");
		allowedExtns.add("xml");
		allowedExtns.add("csv");
		allowedExtns.add("log");
		allowedExtns.add("xls");
		
		try{
			valideFileName=ESAPI.validator().getValidFileName(ctx, inputStr, allowedExtns, ALLOW_NULL);
			logger.info("valideFileName"+valideFileName);
		}
		catch(ValidationException e)
		{
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}

		catch(IntrusionException e)
		{
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		
		return valideFileName;
	}

	
	public String getValidDiectoryPath(String ctx, String inputStr, File parent)
			throws AppSecurityValidatorException {
		
		String valideDiectoryPath=null;
		try{
			valideDiectoryPath=ESAPI.validator().getValidDirectoryPath(ctx, inputStr, parent,ALLOW_NULL);
			logger.info("valideDiectoryPath"+valideDiectoryPath);
		}
		catch(ValidationException e)
		{
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}

		catch(IntrusionException e)
		{
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		return valideDiectoryPath;
	}
	
	public String safeReadLine(InputStream inputStream ) throws AppSecurityValidatorException{
		String inputReadLine=null;
		try{			
			
			inputReadLine=ESAPI.validator().safeReadLine(inputStream, SecurityValidatorConstants.MAX_READ_LINE_LENGTH);
			logger.info("inputReadLine"+inputReadLine);
		   }
		catch(ValidationException e)
		{
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		catch(Exception e){
			logger.error("error",e);
			throw new AppSecurityValidatorException(e);
		}
		return inputReadLine;
	}

}
