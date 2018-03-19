package com.addval.ejbutils.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import com.addval.ejbutils.dbutils.EJBColumn;
import com.addval.ejbutils.dbutils.EJBRecord;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.environment.Environment;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.RecordStatus;
import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;

/**
 * This TableManagerInterceptor is used to save the file which is in the
 * EJBResultSet as object in the column UPLOAD_FILE and also delete the file
 * when a schedule is deleted
 */

public class UploadTableManagerInterceptor extends DefaultTableManagerInterceptor {
	private String _uploadDir = null;
	private String _logDir = null;
	private SimpleDateFormat _dateFormatter = null;
	private String logFileName = null;
	private Object fileObject = null;
	private String fileName = null;
	private String _fileExtType = null;
	private static String _UPLOAD_FILE_COLUMN = "UPLOAD_FILE";
	private static String _PARM_FILE_NAME = "PARM_FILENAME";
	private static String _PARM_VALUE_1 = "PARM_VALUE_1";
	private static String _UPLOAD_FILE_NAME_COLUMN = "UPLOAD_FILE_NAME";
	private static String _ORIGINAL_FILE_NAME_COLUMN = "ORIGINAL_FILE_NAME";
	private static String _LOG_FILE_NAME_COLUMN = "LOG_FILE_NAME";
	private static String _IS_FILE_UNIQUE_COLUMN = "IS_FILE_UNIQUE";
	private static String _LOG_FILE_PREFIX = "msgadapter_";
	protected static final String _KEY_COLUMN_NAME = "JOB_STATUS_KEY";
	public String fileSeparator = System.getProperty("file.separator");
	public String userHome = System.getProperty("user.home");

	public UploadTableManagerInterceptor() {
	}

	public void setSubSystem(String subSystem) {
		super.setSubSystem(subSystem);
		initialise();
	}

	/**
	 * This method will be initiate the default value from properties file
	 */
	private void initialise() {
		_dateFormatter = new SimpleDateFormat("ddMMyyyHHmmss");
		_dateFormatter.setTimeZone(AVConstants._GMT_TIMEZONE);
		_uploadDir = Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("data.dir", userHome);
		_logDir = Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("logs.dir", userHome);
		_logDir += System.getProperty("file.separator") + "batch";
		_fileExtType = Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("loader.logfile.extension", ".log");
	}

	public void beforeUpdate(EJBResultSet ejbRS) throws Exception {
		ejbRS.beforeFirst();
		if (!ejbRS.next())
			return;
		EditorMetaData metaData = ejbRS.getEJBResultSetMetaData().getEditorMetaData();
		String editorName = metaData.getName();
		ColumnMetaData columnMetaData = metaData.getColumnMetaData(_UPLOAD_FILE_COLUMN);
		if (columnMetaData != null) {
			columnMetaData.setUpdatable(false);
			fileObject = ejbRS.getObject(_UPLOAD_FILE_COLUMN);
		}

		if (fileObject == null) {
			return;
		}
		if (!(fileObject instanceof byte[])) {
			return;
		}
		try {
			// need to update the data for column _IS_FILE_UNIQUE_COLUMN and 	_LOG_FILE_NAME_COLUMN with their value
			/**
			 * the byte array from resultSet will be set in the FileObject and
			 * the Upload FileName and Log FileName will be set in the ejb
			 * ResultSet
			 */
			String fileNamePrefix = ejbRS.getString(_UPLOAD_FILE_NAME_COLUMN);
			String fileUnique = ejbRS.getString(_IS_FILE_UNIQUE_COLUMN);
			String fileName = ejbRS.getString(_ORIGINAL_FILE_NAME_COLUMN);

			String fileType = fileName != null ? (fileName.substring(fileName.lastIndexOf(".") + 1)).toLowerCase()
					: null;
			boolean isFileUnique = StrUtl.isEmptyTrimmed(fileUnique) || fileUnique.equals("true") ? true : false;
			setFileName(editorName, fileNamePrefix, isFileUnique, fileType);

			columnMetaData = metaData.getColumnMetaData(_IS_FILE_UNIQUE_COLUMN);
			if (columnMetaData != null) {
				columnMetaData.setUpdatable(true);
			}

			columnMetaData = metaData.getColumnMetaData(_LOG_FILE_NAME_COLUMN);
			if (columnMetaData != null) {
				columnMetaData.setUpdatable(true);
				ejbRS.updateString(_LOG_FILE_NAME_COLUMN, getLogFileName());
			}

			columnMetaData = metaData.getColumnMetaData(_PARM_FILE_NAME);
			if (columnMetaData != null) {
				columnMetaData.setUpdatable(true);
				ejbRS.updateString(_PARM_FILE_NAME, getFileName());
			}

			columnMetaData = metaData.getColumnMetaData(_UPLOAD_FILE_COLUMN);
			if (columnMetaData != null) {
				ejbRS.updateString(_UPLOAD_FILE_COLUMN, getFileName());
			}

			columnMetaData = metaData.getColumnMetaData(_PARM_VALUE_1);
			if (columnMetaData != null) {
				columnMetaData.setUpdatable(true);
			}

			columnMetaData = metaData.getColumnMetaData(_ORIGINAL_FILE_NAME_COLUMN);
			if (columnMetaData != null) {
				columnMetaData.setUpdatable(true);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * getFileName will return the File to be saved file name will be
	 * (EDITOR_NAME)_(DATE_TIME).(fileExtension) or the File Name from the
	 * Client side depends on flag isFileUnique
	 */
	public String getFileName() {
		return fileName;
	}

	protected void setFileName(String editorName, String fileNamePrefix, boolean isFileUnique, String fileType) {
		if (fileType == null)	{
			fileType = Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue(editorName + ".fileType", "csv");
		}
		String dateStr = _dateFormatter.format(Calendar.getInstance().getTime());

		if (isFileUnique) {
			fileName = fileNamePrefix + "_" + dateStr + "." + fileType;
			logFileName = fileNamePrefix + "_" + dateStr;
		}
		else {
			fileName = fileNamePrefix;
			fileNamePrefix = fileNamePrefix.lastIndexOf(".") == -1 ? fileNamePrefix : fileNamePrefix.substring(0, fileNamePrefix.lastIndexOf("."));
			logFileName = fileNamePrefix + "_" + dateStr;
		}
	}

	protected void setFileName(String editorName, String fileNamePrefix, boolean isFileUnique) {
		setFileName(editorName, fileNamePrefix, isFileUnique, null);
	}

	public void afterUpdate(EJBResultSet ejbRS) throws Exception {
		if (ejbRS == null)
			return;

		Vector records = ejbRS.getRecords();
		if (records == null)
			return;

		EJBRecord ejbRecord = null;
		Vector columns = null;
		boolean retVal = false;

		for (int i = 0; i < records.size(); ++i) {

			ejbRecord = (EJBRecord) records.elementAt(i);
			columns = ejbRecord.getColumns();

			if (columns != null) {
				switch (ejbRecord.getStatus()) {
				case RecordStatus._RMS_DELETED: {
					try {

						/**
						 * During delete in addition of task from job_status
						 * table the file will be deleted from data_dir only if
						 * the isFileUnique set true and log file be deleted
						 * from batch_log dir
						 */
						int size = columns.size();
						boolean isFileUnique = false;
						String jobStatusKey = null;
						for (int index = 0; index < size; index++) {
							EJBColumn column = (EJBColumn) columns.get(index);
							String colName = column.getName();
							if (colName.equals(_KEY_COLUMN_NAME)) {
								jobStatusKey = column.getStrValue();
							}
							if (colName.equals(_IS_FILE_UNIQUE_COLUMN)) {
								String fileUnique = column.getStrValue();
								isFileUnique = (!StrUtl.isEmptyTrimmed(fileUnique)) && fileUnique.equalsIgnoreCase("true");
								//													break;
							}
							else if (colName.equals(_LOG_FILE_NAME_COLUMN)) {
								logFileName = column.getStrValue();
								File file = new File(_logDir + fileSeparator + _LOG_FILE_PREFIX + logFileName + _fileExtType);
								file.delete();
							}
						}
						if (isFileUnique) {
							for (int index = 0; index < size; index++) {
								EJBColumn column = (EJBColumn) columns.get(index);
								String colName = column.getName();
								if (colName.equals(_UPLOAD_FILE_COLUMN)) {
									String fileName = column.getStrValue();
									deleteUploadedFile(jobStatusKey, fileName);
									break;
								}
							}
						}
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					break;
				}

				case RecordStatus._RMS_INSERTED: {
					try {
						int size = columns.size();
						for (int index = 0; index < size; index++) {
							EJBColumn column = (EJBColumn) columns.get(index);
							String colName = column.getName();
							if (colName.equals(_KEY_COLUMN_NAME)) {
								String jobKey = column.getStrValue();
								saveFile(jobKey);
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
					break;
				}
				}
			}
		}
	}

	/**
	 * This method will delete uploaded File in dataDirectory
	 */
	protected void deleteUploadedFile(String jobStatusKey, String fileName) throws Exception {
		File file = new File(_uploadDir + fileSeparator + fileName);
		file.delete();
	}

	/**
	 * This method will save uploaded File into dataDirectory from the in byte[]
	 */
	protected void saveFile(String key) throws Exception {
		FileOutputStream outputStream = null;
		try {
			String fileName = getFileName();
			String dirPath = _uploadDir;

			if (fileName != null) {
				outputStream = new FileOutputStream(new File(dirPath + fileSeparator + fileName));
				byte[] byteArray = (byte[]) getFileObject();
				outputStream.write(byteArray);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			}
			catch (Exception e) {
			}
		}
	}

	public Object getFileObject() {
		return fileObject;
	}

	public void setFileObject(Object fileObject) {
		this.fileObject = fileObject;
	}

	public String getLogDir() {
		return _logDir;
	}

	public void setLogDir(String _logDir) {
		this._logDir = _logDir;
	}

	public String getUploadDir() {
		return _uploadDir;
	}

	public void setUploadDir(String _uploadDir) {
		this._uploadDir = _uploadDir;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
}
