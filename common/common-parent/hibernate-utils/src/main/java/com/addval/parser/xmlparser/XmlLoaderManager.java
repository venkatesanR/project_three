package com.addval.parser.xmlparser;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.addval.parser.InvalidInputException;
import com.addval.parser.Utils;
import com.addval.utils.CnfgFileMgr;

// Loader Class should extend this class and should override the
// processXml(String xmlString) method to process the Xml String.
public class XmlLoaderManager
{
	private String _projectName			= null;
	//	private static final String _module = "com.addval.parser.xmlparser.XmlLoaderManager";
	private int _updateFrequency 		= 1;
	private XmlLoaderDao _dao 			= null;
	private boolean _continueOnError 	= true;
	private boolean _preserveValues = false;
	protected int _rejectedCount = 0;
	protected int _insertAgentCount=0;
	protected int _deleteAgentCount=0;
	protected int _updateAgentCount=0;
	protected List<Integer> _errorIndexArray	= new ArrayList<Integer>();
	private CnfgFileMgr _cnfgFileMgr = null;
	private static transient final org.apache.log4j.Logger _logger = 	com.addval.utils.LogMgr.getLogger(XmlLoaderManager.class);
	private String _validatitonType = "SIMPLE";

	public XmlLoaderManager(){
	}

	public XmlLoaderManager(String projectName){
		this._projectName = projectName;
	}

	// All Loader classes should implement this method to process
	public void processXml(String xmlString) throws InvalidInputException
	{
		throw new InvalidInputException( "Base class method does not have any implementation.\nXmlLoaderManager should be extended for specific implemenation of processXml(String xmlString) " );
	}

	// Method to process Xml file using File type input
	public void processXml(File xmlFile) throws Exception
	{
		_logger.debug("processXml-File");
		processXml( ParserUtils.getString( xmlFile ) );
	}

	// To call getKeyPairs method of XmlLoaderDao
	public Map<String, String> getKeyPairs(String sqlName) throws SQLException
	{
		_logger.debug("processXml-File");
		return getXmlLoaderDao().getKeyPairs( sqlName );
	}

	public void processXml(Object object) throws SQLException, InvalidInputException
	{
		processXml( new Object[]{ object } );
	}

	public void processXml(Object object, boolean dateCheck, boolean autoClose) throws SQLException, InvalidInputException
	{
		processXml( new Object[]{ object }, dateCheck, autoClose );
	}

	public void processXml(Object objects[]) throws SQLException, InvalidInputException
	{
		processXml(objects, false);
	}

	// To process Xml input. It updates / inserts records
	public void processXml(List objects, boolean dateCheck) throws SQLException, InvalidInputException
	{
		processXml( objects.toArray(), dateCheck );
	}

	// To process Xml input. It updates / inserts records
	public void processXml(Object object, boolean dateCheck) throws SQLException, InvalidInputException
	{
		processXml( new Object[]{ object }, dateCheck );
	}

	// To process Xml input. It updates / inserts records
	public void processXml(Object objects[], boolean dateCheck) throws SQLException, InvalidInputException
	{
		processXml(objects, dateCheck, true );
	}

	// To process Xml input. It updates / inserts records
	public void processXml(Object objects[], boolean dateCheck, boolean autoClose) throws SQLException, InvalidInputException
	{
		if (Utils.isNullOrEmpty( objects ))
			return;
		_logger.debug(" Enter - processXml-Object[]" );
		int updateCount = 0;
		int insertCount = 0;
		// implementing classes may have actions in the preProcess method
		preProcess();
		//boolean isMap = objects[0] != null && (objects[0] instanceof Map);
		for (int i=0; i<objects.length; i++) {
			try {
				if (objects[i] == null)
					continue;
				// the implementing classes may do actions in the clear() method, the actions to be
				// taken between every xml processing
				clear();
				// if the table has this row already, it is to be an update else it would be an insert
				boolean hasthisRow;
				//		System.out.println("############################# _preserveValues" + _preserveValues);
				if (_preserveValues)
					hasthisRow = hasThisRow( objects[i] );
				else
					hasthisRow = getXmlLoaderDao().hasThisRow( objects[i] );
				//System.out.println("###########  hasthisRow =" + hasthisRow);
				if(dateCheck)
					getXmlLoaderDao().hasMultipleRow( objects[i] );
				if (hasthisRow){
					getXmlLoaderDao().updateRow( objects[i], ++updateCount % _updateFrequency == 0 );
					if (dateCheck)
						getXmlLoaderDao().insertRow( objects[i], ++insertCount % _updateFrequency == 0 );
				}
				else{
					getXmlLoaderDao().insertRow( objects[i], ++insertCount % _updateFrequency == 0 );
				}
			}
			catch (SQLException  e) {
				_rejectedCount++;
				_errorIndexArray.add( i+1 );
				String errorMessage = getXmlLoaderDao().getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(e);
				if (null != objects[i] && null != errorMessage) {
					((Map) objects[i] ).put("error", errorMessage);
				} else {
					((Map) objects[i] ).put("error", e.getMessage());
				}
				if(!getContinueOnError()) {
					if (autoClose)
						close();
					_logger.error( objects[i] + "");
					_logger.error( e );
					throw e;
				}
				_logger.warn( objects[i] + "");
				//	System.out.println(e);
				_logger.warn( e );
			}
		}
		if (autoClose)
			close();
		// implementing classes may have actions in the postProcess method
		postProcess();
		_logger.debug( "Exit - processXml-Object[]" );
	}

	private boolean hasThisRow(Object object) throws SQLException
	{
		Map[] rows = getXmlLoaderDao().selectRow( object );
		return rows != null && rows.length != 0;
	}

	public Map[] executeAndReturn(Object object) throws SQLException
	{
		return getXmlLoaderDao().selectRow( object );
	}

	public Map[] executeAndReturn(Object object, String sqlName) throws SQLException
	{
		return getXmlLoaderDao().selectRow( object, sqlName );
	}

	// To execute a query
	public void executeQuery(Object objects[], String sqlType) throws SQLException
	{
		_logger.debug(" Enter - executeQuery" );
		if (sqlType.equals( "insertQuery" )) {
			for (Object object : objects) getXmlLoaderDao().insertRow(object, true);
		}
		else if(sqlType.equals( "deleteQuery" )) {
			for (Object object : objects) getXmlLoaderDao().deleteRow(object, true);
		}
		_logger.debug( "Exit - executeQuery" );
	}

	public Map[] getDeleteMap(Map[] map, Map newMap, String newStartDateName,  String newEndDateName , String startDateFieldname,  String endDateFieldname)
	{
		_logger.debug(" Enter - getDeleteMap" );
		GregorianCalendar newStartDate	= new GregorianCalendar();
		GregorianCalendar newEndDate	= new GregorianCalendar();
		GregorianCalendar startDate		= new GregorianCalendar();
		GregorianCalendar endDate		= new GregorianCalendar();
		Object 	newStartDateobj 		= newMap.get(newStartDateName);
		String newStartDatestring		= newStartDateobj.toString();
		Object newEndDateobj 			= newMap.get(newEndDateName);
		String newEndDateString			= newEndDateobj.toString();
		SimpleDateFormat inFormater 	= new SimpleDateFormat("yyyy-MM-dd");

		Map deleteMap[]			= null;
		int startIndex 				= 0;
		int endIndex				= 0;
		boolean startIndexChanged	= false;
		boolean endIndexChanged		= false;

		try{
			newStartDate.setTime( new Date( inFormater.parse(newStartDatestring).getTime()) );
			for(int i=0;i<map.length;i++){
				String startStr	= map[i].get( startDateFieldname ).toString();
				String endStr	= map[i].get( endDateFieldname ).toString();
				startDate.setTime( new Date(inFormater.parse( startStr ).getTime()) );
				endDate.setTime( new Date(inFormater.parse( endStr ).getTime()) );
				if(((newStartDate.after( startDate ))||(newStartDate.equals( startDate )))&&((newStartDate.before(endDate)))||(newStartDate.equals(endDate))){
					startIndex = i;
					startIndexChanged = true;
					break;
				}
			}
			newEndDate.setTime(new Date( inFormater.parse(newEndDateString).getTime()) );
			for(int i=startIndex;i<map.length;i++){
				String startStr	= map[i].get(startDateFieldname).toString();
				String endStr	= map[i].get(endDateFieldname).toString();
				startDate.setTime( new Date(inFormater.parse(startStr).getTime()) );
				endDate.setTime( new Date(inFormater.parse(endStr).getTime()) );
				if (((newEndDate.after(startDate))||(newEndDate.equals(startDate)))&&((newEndDate.before(endDate)))||(newEndDate.equals(endDate))) {
					endIndex=i;
					endIndexChanged = true;
					break;
				}
			}
			if (endIndexChanged || startIndexChanged) {
				int size = endIndex - startIndex + 1;
				deleteMap = new HashMap[size];
				for (int i = 0; i < size; i++) {
					deleteMap[i] = new HashMap();
				}
				for (int i = 0; i < size; i++) {
					deleteMap[i] = map[startIndex];
					startIndex++;
				}
			}
			else {
				String startStr = map[0].get(startDateFieldname).toString();
				String endStr = map[map.length - 1].get(endDateFieldname).toString();
				endDate.setTime(new Date(inFormater.parse(endStr).getTime()));
				startDate.setTime(new Date(inFormater.parse(startStr).getTime()));
				if (newStartDate.before(startDate) && (newEndDate.after(endDate))) {
					deleteMap = new HashMap[map.length];
					for (int i = 0; i < map.length; i++) {
						deleteMap[i] = new HashMap();
					}
					System.arraycopy(map, 0, deleteMap, 0, map.length);
				}
				return deleteMap;
			}
		}
		catch(Exception e) {
			_logger.warn( e );
		}
		_logger.debug( "Exit - getDeleteMap" );
		return deleteMap;
	}

	public Map[] getInsertMap(Map [] map, Map newMap, String newStartDateName,  String newEndDateName , String startDateFieldname,  String endDateFieldname, String fieldNames[], String keyNames[])
	{
		_logger.debug(" Enter - getInsertMap" );
		GregorianCalendar newStartDate	= new GregorianCalendar();
		GregorianCalendar newEndDate	= new GregorianCalendar();
		GregorianCalendar startDate		= new GregorianCalendar();
		GregorianCalendar endDate		= new GregorianCalendar();
		Object newStartDateobj 			= newMap.get(newStartDateName);
		String newStartDatestring		= newStartDateobj.toString();
		Object newEndDateobj 			= newMap.get(newEndDateName);
		String newEndDateString			= newEndDateobj.toString();
		SimpleDateFormat inFormater 	= new SimpleDateFormat("yyyy-MM-dd");
		HashMap insertMap[]				= new HashMap[3];
		for (int i=0; i<3; i++)
			insertMap[i] = new HashMap<String, String>();
		try {
			int noOfFieldNames 	= fieldNames.length;
			int noOfKeyNames	= keyNames.length;
			newStartDate.setTime(new Date( inFormater.parse(newStartDatestring).getTime()) );
			newEndDate.setTime(new Date( inFormater.parse(newEndDateString).getTime()) );
			boolean insertNew=false;
			if (map==null){
				insertNew=true;
			}else {
				String startStr	= map[0].get( startDateFieldname ).toString();
				String endStr	= map[map.length-1].get(endDateFieldname).toString();
				endDate.setTime( new Date(inFormater.parse(endStr).getTime()) );
				startDate.setTime( new Date(inFormater.parse( startStr ).getTime()) );
				if((newStartDate.before(startDate))&&(newEndDate.after(endDate))) {
					insertNew=true;
				}
			}
			if (insertNew) {
				insertMap[0].put(startDateFieldname, inFormater.format(newStartDate.getTime()));
				insertMap[0].put(endDateFieldname, inFormater.format(newEndDate.getTime()));
				for(int i=0;i<noOfKeyNames;i++)
					insertMap[0].put(fieldNames[i], newMap.get(keyNames[i]));
				return insertMap;
			}
			String startStr = map[0].get(startDateFieldname).toString();
			String endStr 	= map[0].get(endDateFieldname).toString();
			startDate.setTime( new Date(inFormater.parse(startStr).getTime()) );
			endDate.setTime( new Date(inFormater.parse(endStr).getTime()) );
			int position = 0;
			if((newStartDate.equals(startDate))){
				insertMap[0].put( startDateFieldname, inFormater.format(startDate.getTime()) );
			}
			else{
				insertMap[0].put( startDateFieldname, inFormater.format(startDate.getTime()) );
				newStartDate.add(java.util.Calendar.DAY_OF_MONTH,-1);
				insertMap[0].put( endDateFieldname, inFormater.format(newStartDate.getTime()) );
				for(int i=0;i<noOfFieldNames;i++)
					insertMap[0].put(fieldNames[i], map[0].get(fieldNames[i]));
				newStartDate.add(java.util.Calendar.DAY_OF_MONTH,1);
				insertMap[1].put( startDateFieldname, inFormater.format(newStartDate.getTime()) );
				position = 1;
			}

			startStr 	= map[map.length-1].get(startDateFieldname).toString();
			endStr 		= map[map.length-1].get(endDateFieldname).toString();
			endDate.setTime( new Date(inFormater.parse(endStr).getTime()) );
			if((newEndDate.equals(endDate))){
				insertMap[position].put( endDateFieldname, inFormater.format(endDate.getTime()) );
				for(int i=0;i<noOfKeyNames;i++){
					insertMap[position].put(fieldNames[i], newMap.get(keyNames[i]));
				}//
			}
			else{
				insertMap[position].put( endDateFieldname, inFormater.format(newEndDate.getTime()) );
				for(int i=0;i<noOfKeyNames;i++){
					insertMap[position].put(fieldNames[i], newMap.get(keyNames[i]));
				}
				newEndDate.add(java.util.Calendar.DAY_OF_MONTH,1);
				insertMap[position+1].put( startDateFieldname, inFormater.format(newEndDate.getTime()) );
				insertMap[position+1].put( endDateFieldname, inFormater.format(endDate.getTime()) );
				for(int i=0;i<noOfFieldNames;i++){
					insertMap[position+1].put(fieldNames[i], map[map.length-1].get(fieldNames[i]));
				}
			}
		}
		catch(Exception e){
			_logger.warn( e );
		}
		_logger.debug( "Exit - getInsertMap" );
		return insertMap;
	}


	public int getIntValue( String sqlName) throws SQLException
	{
		return getXmlLoaderDao().getIntValue( sqlName);
	}

	public void deleteOverlapping() throws SQLException
	{
		getXmlLoaderDao().deleteOverlapping();
	}

	// unused method as of now
	public String getStringValue( String sqlName, Object object ) throws SQLException
	{
		return getXmlLoaderDao().getStringValue( sqlName, object );
	}

	public void setLookupSqlName(String lookupSqlName)
	{
		getXmlLoaderDao().setLookupSqlName( lookupSqlName );
	}

	public void setInsertSqlName(String insertSqlName)
	{
		getXmlLoaderDao().setInsertSqlName( insertSqlName );
	}

	public void setUpdateSqlName(String updateSqlName)
	{
		getXmlLoaderDao().setUpdateSqlName( updateSqlName );
	}

	public void setDeleteSqlName(String deleteSqlName)
	{
		getXmlLoaderDao().setDeleteSqlName( deleteSqlName );
	}

	public void setRejectedCount(int rejectedCount)
	{
		_rejectedCount = rejectedCount ;
	}

	public int getRejectedCount()
	{
		return _rejectedCount ;
	}

	public void addRejectedCount(int count)
	{
		_rejectedCount =  _rejectedCount + count;
	}

	public void  setErrorIndexArray(List<Integer> errorList)
	{
		_errorIndexArray = errorList ;
	}

	public List<Integer> getErrorIndexArray()
	{
		return _errorIndexArray ;
	}

	// implementing classes may override this method to do actions
	// that may be required before processing a set of XMLs
	protected void preProcess()
	{

	}

	// implementing classes may override this method to do actions
	// that may be required after processing a set of XMLs
	// and it is a must to call this super method
	protected void postProcess()
	{

	}

	// implementing classes may override this method to do actions
	// that may be required after processing individual Xml of a set of XMLs
	protected void clear()
	{

	}

	public void close()
	{
		try {
			if (_dao != null){
				_dao.close();
			}
			String details = getPrintDetails();
			_logger.info( details );
			System.out.println( details );
		}
		catch(Exception e) {
			// don;t do anything
		}
	}

	public String getPrintDetails()
	{
		StringBuffer details = new StringBuffer();
		details.append("\nDatabase Operations:" ).append("\n");
		details.append(_dao.getprintDetails()).append("\n");
		details.append("No of rows rejected   " ).append( _rejectedCount).append("\n");
		if (_errorIndexArray != null && _errorIndexArray.size()>0)
			details.append("Index of rejected records " ).append( _errorIndexArray ).append("\n");
		return details.toString() ;
	}

	public void afterPropertiesSet() throws SQLException
	{
		if (getCnfgFileMgr() == null) {
			_logger.warn("CnfgFileMgr set as Null");
			return;
		}
		_preserveValues = getCnfgFileMgr().getBoolValue( "preserveValues", true );

		//_dao 			 = new XmlLoaderDao( projectName );
		//_dao 			 = new XmlLoaderDao();
		setLookupSqlName( getCnfgFileMgr().getPropertyValue( "selectQuery", "selectQuery" ) );
		setInsertSqlName( getCnfgFileMgr().getPropertyValue( "insertQuery", "insertQuery" ) );
		setUpdateSqlName( getCnfgFileMgr().getPropertyValue( "updateQuery", "updateQuery" ) );
		setDeleteSqlName( getCnfgFileMgr().getPropertyValue( "deleteQuery", "deleteQuery" ) );
		getXmlLoaderDao().initialize();
	}

	public void updateTaskStatus(int count) throws SQLException
	{
		_dao.updateTaskStatus(count);
	}


	public void setProjectName(String aName) {
		_projectName = aName;
	}

	public String getProjectName() {
		return _projectName;
	}

	public void setCnfgFileMgr(CnfgFileMgr cnfgFileMgr){
		_cnfgFileMgr = cnfgFileMgr;
	}
	public CnfgFileMgr getCnfgFileMgr(){
		return _cnfgFileMgr;
	}

	public void setXmlLoaderDao(XmlLoaderDao xmlLoaderDao){
		_dao = xmlLoaderDao;
	}
	public XmlLoaderDao getXmlLoaderDao(){
		return _dao;
	}

	public void setContinueOnError(boolean continueOnError){
		_continueOnError = continueOnError;
	}
	public boolean getContinueOnError(){
		return _continueOnError;
	}

	public void setUpdateFrequency(int updateFrequency){
		_updateFrequency = updateFrequency;
	}
	public int getUpdateFrequency(){
		return _updateFrequency;
	}

	public void setValidatitonType(String validatitonType){
		_validatitonType = validatitonType;
	}
	public String getValidatitonType(){
		return _validatitonType;
	}

	public void setPreserveValue(boolean aBool) {
		_preserveValues = aBool;
	}

	public boolean getPreserveValues() {
		return _preserveValues;
	}

}