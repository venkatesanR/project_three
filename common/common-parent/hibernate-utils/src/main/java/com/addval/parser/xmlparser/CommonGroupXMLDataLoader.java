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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.addval.environment.Environment;
import com.addval.parser.InvalidInputException;
/**
 * This generic class loads any parent - child loader xml into data base.
 * @author priya.a.sekar
 *
 */
public class CommonGroupXMLDataLoader extends XmlLoaderManager {


    /** _logger */
    private static org.apache.log4j.Logger logger = com.addval.utils.LogMgr.getLogger(CommonGroupXMLDataLoader.class);
    /** _recordSize. */
    private int recordSize;
    /** _errorIndex. */
    private List<?> errorIndex = new ArrayList<Object>();
    /** _env.*/
    private Environment env;
    /** _rejectedIndex. */
    private List<?> rejectedIndex = new ArrayList<Object>();
    /** loader. */
    private LoaderHelper loaderHelper;
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

    /** CommonXMLDataLoader default constructor.
     *
     */
    public CommonGroupXMLDataLoader() {
        super();
    }
    /**
     * This method checks for the action type and further instructs the other methods to store the data into Data base.
     *
     * @param xml
     *         the xml
     * @throws InvalidInputException
     *
     */
    public void processXml(String xml) throws InvalidInputException {

        logger.info("XmlLoader started processing");
        long startTime = System.currentTimeMillis();
        try {
            Map<?,?>[] map = getLoaderHelper().getLoaderMap(xml, getLoaderName(), getValidationType());

            findActionAndContinueProcess(map);

            calculateStatisticsAndTime(startTime);
        } catch (SQLException e) {
            if(!getContinueOnError()) {
                throw new InvalidInputException(e.getMessage());
            }
            logger.error("Error while processing xml data" + e);
        } catch (Exception e) {
            if(!getContinueOnError()) {
                throw new InvalidInputException(e.getMessage());
            }
            logger.error("Error while processing xml data" + e);
        }
    }
    /**
     * This method checks for the type of action to be performed and proceed further based on it
     * @param map
     * @throws InvalidInputException
     * @throws SQLException
     * @throws ParseException
     */
    private void findActionAndContinueProcess(Map<?, ?>[] map)
            throws InvalidInputException, SQLException, ParseException {
        recordSize = map.length;
        for (int i = 0; i < recordSize; i++) {
            if (!map[i].isEmpty() && null != map[i].get(action) && null == map[i].get(error)) {
                if (updateAction.equalsIgnoreCase(map[i].get(action).toString())) {
                    executeInsertOrUpdate(map, i);
                } else if (deleteAction.equalsIgnoreCase(map[i].get(action).toString())) {
                    executeDelete(map, i);
                }
            } else if (map[i].containsKey(error) && null != map[i].get(error)) {
                getLoaderHelper().populateError(map[i].get(objectValue) , map[i].get(error).toString(), loaderName);
                _rejectedCount++;
            }
        }
    }

    /**
     * This method prepares map object for parent and executes the insert or update function.
     * And if there is no error with parent execution then it prepares map object for child and executes insert or update function.
     *
     * @param map
     * @param i
     * @throws SQLException
     * @throws ParseException
     */
    private void executeInsertOrUpdate(Map<?, ?>[] map, int i) throws InvalidInputException, SQLException, ParseException {
        getLoaderHelper().initializeParentSQLStmts(getLoaderName());
        Map<?,?> parentObj = getLoaderHelper().fetchParentLoaderMap(map[i].get(objectValue), getLoaderName());
        getXmlLoaderDao().initialize();
        processXml(parentObj, false, false);
        if (parentObj.containsKey(error) && null != parentObj.get(error)) {
            getLoaderHelper().populateError(map[i].get(objectValue) , parentObj.get(error).toString(), loaderName);
        } else {
            getLoaderHelper().initializeChildSQLStmts(getLoaderName());
            getXmlLoaderDao().initialize();
            getLoaderHelper().childProcessMap(loaderName, map[i].get(objectValue),  parentObj);
        }
    }

    /**
     * This method executes the delete function.
     *
     * @param map
     * @param i
     * @throws SQLException
     */
    private void executeDelete(Map<?, ?>[] map, int i) throws SQLException {
        try {
            getLoaderHelper().initializeParentSQLStmts(getLoaderName());
            getXmlLoaderDao().initialize();
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
            if(!getContinueOnError()) {
                throw e;
            }
        }
    }

    /**
     * This method calculates the statistics and the performance timings.
     *
     * @param startTime
     *         the startTime.
     *
     */
    private void calculateStatisticsAndTime(long startTime) {
    	logger.info(getDataDetails());
		logger.info(printDataDetails());
        double loadingTimeInMinutes = (System.currentTimeMillis() - startTime) / 1000d / 60d;
        DecimalFormat formatter = new DecimalFormat(".##");
        loadingTimeInMinutes = Double.parseDouble(formatter.format(loadingTimeInMinutes));
        logger.info("Total Time taken = " + loadingTimeInMinutes + " minutes");
        logger.info("XmlLoader process completed successfully");
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
     * This method prints the number of records inserted ,updated, added while loading to data base.
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
            buffer.append("No errors in records.");
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
        if (null != val) {
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
        if (null != val) {
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
        if (null != val) {
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
    /** get Recod size. */
    public int getRecordSize() {
		return recordSize;
	}

	/** set Recod size. */
	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}
}
