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
package com.addval.parser.xmlparser;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlException;

import com.addval.environment.Environment;
import com.addval.parser.InvalidInputException;
/**
 * This generic class loads any single loader xml into data base.
 * @author priya.a.sekar
 *
 */
public class CommonXMLDataLoader extends XmlLoaderManager {

    /** _logger */
    private static org.apache.log4j.Logger logger = com.addval.utils.LogMgr.getLogger(CommonXMLDataLoader.class);

    /** startTime. */
    private long startTime;

    /** _recordSize. */
    private int recordSize;

    /** _invalidIndex. */
    private List<?> invalidIndex = new ArrayList<Object>();

    /** _errorIndex. */
    private List<?> errorIndex = new ArrayList<Object>();

    /** _rejectedIndex. */
    private List<?> rejectedIndex = new ArrayList<Object>();

    /** loader. */
    private LoaderHelper loaderHelper;

    /** _env.*/
    private Environment env;

    /** loaderName.*/
    private String loaderName;

    /** xmlFileName.*/
    private String xmlFileName;

    /** validationType.*/
    private String validationType;

    //constant variables

    /** ACTION.*/
    private String action = "ACTION";

    /** ERROR.*/
    private String error = "error";

    /** DELETE_QUERY.*/
    private String deleteQuery = "deleteQuery";

    /** OBJECT_VALUE.*/
    private String objectValue = "objectValue";

    /** UPDATE_ACTION.*/
    private String updateAction = "U";

    /** DELETE_ACTION.*/
    private String deleteAction = "D";
    
    /**
     * CommonXMLDataLoader constructor.
     */
    public CommonXMLDataLoader() {
        super();
    }

    /**
     * This method stores the data into Data base.
     *
     * @param xml
     *         the xml
     * @throws InvalidInputException 
     * @throws SQLException 
     *
     */
    public void processXml(String xml) throws InvalidInputException {
        logger.info("XmlLoader started processing");
        try {
            Map<?,?>[] map = getLoaderHelper().getLoaderMap(xml, this.getLoaderName(), this.getValidationType());
            if (null != map) {
                processMap(map, getLoaderName());
            } 
        } catch(SQLException se) {
            if (!getContinueOnError()) {
                throw new InvalidInputException(se.getMessage());
            }
        } catch (XmlException e) {
            if (!getContinueOnError()) {
                throw new InvalidInputException(e.getMessage());
            }
        }
        // method call to log the data statistics and its performance time.
        logger.info(getDataDetails());
		logger.info(printDataDetails());
        double loadingTimeInMinutes = (System.currentTimeMillis() - startTime) / 1000d / 60d;
        DecimalFormat formatter = new DecimalFormat(".##");
        loadingTimeInMinutes = Double.parseDouble(formatter.format(loadingTimeInMinutes));
        logger.info("Total Time taken = " + loadingTimeInMinutes + " minutes");
        logger.info("XmlLoader process completed successfully");
    }

    /**
     * This method process the map and execute the query to load the data into data base
     * and also populates error if there occurs an SQL exception.
     *
     * @param map
     *         the map
     * @param loaderName
     *         the loaderName
     * @throws InvalidInputException 
     * @throws SQLException 
     *
     */
    private void processMap(Map<?,?>[] map, String loaderName) throws SQLException, InvalidInputException {
        recordSize = map.length;
        for (int i = 0; i < recordSize; i++) {
            if (!map[i].isEmpty() && null != map[i].get(action) && null == map[i].get(error)) {
                if (updateAction.equalsIgnoreCase(map[i].get(action).toString())) {
                    executeInsertOrUpdate(map, loaderName, i);
                } else if (deleteAction.equalsIgnoreCase(map[i].get(action).toString())) {
                    executeDelete(map, loaderName, i);
                }
            } else if (map[i].containsKey(error) && null != map[i].get(error)) {
                getLoaderHelper().populateError(map[i].get(objectValue) , map[i].get(error).toString(), loaderName);
                _rejectedCount++;
            }
        }
    }
    /**
     * This method executes the insert or update function.
     * @param map
     * @param loaderName
     * @param i
     * @throws InvalidInputException 
     * @throws SQLException 
     */
    private void executeInsertOrUpdate(Map<?, ?>[] map, String loaderName, int i) throws SQLException, InvalidInputException {
        processXml(map[i], false, false);
        if (map[i].containsKey(error) && null != map[i].get(error)) {
            getLoaderHelper().populateError(map[i].get(objectValue) , map[i].get(error).toString(), loaderName);
        }
    }

    /**
     * This method executes the delete function.
     * @param map
     * @param loaderName
     * @param i
     * @throws SQLException 
     */
    private void executeDelete(Map<?, ?>[] map, String loaderName, int i) throws SQLException {
        try {
            boolean isRecordAvailable = getXmlLoaderDao().hasThisRow(map[i]);
            if (isRecordAvailable) {
                Map<?,?>[] confirmDelete = new HashMap[1];
                confirmDelete[0] = map[i];
                executeQuery(confirmDelete, deleteQuery);
            } else {
                ((Map) map[i] ).put("error", "No Such Record is present");
                getLoaderHelper().populateError(map[i].get(objectValue) , map[i].get(error).toString(), loaderName);
                _rejectedCount++;
            }
        } catch(SQLException e) {
            _rejectedCount++;
            String errorMessage = getXmlLoaderDao().getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(e);
            if (null != map[i] && null != errorMessage) {
                ((Map) map[i] ).put("error", errorMessage);
            } else {
                ((Map) map[i] ).put("error", e.getMessage());
            }
            getLoaderHelper().populateError(map[i].get(objectValue) , map[i].get(error).toString(), loaderName);
            logger.error("Error while deleting xml data" + e);
            if (!getContinueOnError()) {
                throw e;
            }
        }
    }

    /**
     * Prints the data operation details.
     *
     * @return the prints the data details
     */
    public String printDataDetails() {
    	String statistics = getDetails();
    	StringBuffer details = new StringBuffer();
    	details.append(statistics);
		details.append("\nDatabase Operations:" ).append("\n");
		details.append(getXmlLoaderDao().getprintDetails()).append("\n");
		details.append("No of rows rejected   " ).append( _rejectedCount).append("\n");
		return details.toString() ;
    }
    /**
     * This method prints the number of records inserted ,updated, added and rejected while loading to data base.
     *
     * @return String
     *
     */
    private String getDetails() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\nTotal Number of records : ").append(recordSize).append("\n");
        if (errorIndex.size() > 0) {
            buffer.append("Format failed records     : ").append(errorIndex.size()).append("\n");
            buffer.append("Index of failed records   : ").append(errorIndex).append("\n");
        } else {
            buffer.append("No errors in format of records.\n");
        }
        if (invalidIndex.size() > 0) {
            buffer.append("Invalid records                : ").append(invalidIndex.size()).append("\n");
            buffer.append("Index of Invalid records       : ").append(invalidIndex).append("\n");
        }
        return buffer.toString();
    }

    /**
     * This method prints the number of rejected records while loading to data base.
     *
     * @return String
     *
     */
    public String getDataDetails() {
        StringBuffer buffer = new StringBuffer();

        if (rejectedIndex.size() != 0) {
            buffer.append("No of records rejected (for Action D) : ").append(rejectedIndex.size()).append("\n");
            buffer.append("Index of rejected records: ").append(rejectedIndex).append("\n");
        }

        return buffer.toString();
    }

    /**
     * This method fetches the Validation type from the arguments we pass through the configuration.
     * 
     */
    public void setValidationType(String val) {
        if (val != null) {
            validationType = val;
        } else {
            logger.info("Validation type : " + validationType);
        }
    }

    /**
     * This method fetches the Loader Name from the arguments we pass through the configuration.
     * 
     */
    public void setLoaderName(String val) {
        if (val != null) {
            loaderName = val;
        } else {
            logger.info("Loader Name : " + loaderName);
        }
    }

    /**
     * This method fetches the XML filename from the arguments we pass through the configuration.
     * 
     */
    public void setXmlFileName(String val) {
        if (val != null) {
            xmlFileName = val;
        } else {
            logger.info("XML file Name : " + xmlFileName);
        }
    }

    /** initQuery.
     *
     * @throws SQLException
     *
     */
    public void initQuery() throws SQLException {
        super.afterPropertiesSet();
    }

    /** getLoaderHelper. */
    public LoaderHelper getLoaderHelper() {
        return loaderHelper;
    }

    /** setLoaderHelper. */
    public void setLoaderHelper(LoaderHelper loaderHelper) {
        this.loaderHelper = loaderHelper;
    }

    /** getEnvironment. */
    public Environment getEnvironment() {
        return env;
    }

    /** setEnvironment. */
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    /** getLoaderName. */
    public String getLoaderName() {
        return loaderName;
    }

    /** getValidationType. */
    public String getValidationType() {
        return validationType;
    }

    /** getXmlFileName. */
    public String getXmlFileName() {
        return xmlFileName;
    }

}
