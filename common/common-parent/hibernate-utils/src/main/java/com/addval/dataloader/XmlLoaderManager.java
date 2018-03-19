package com.addval.dataloader;

import com.addval.parser.Utils;
import com.addval.parser.InvalidInputException;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.text.SimpleDateFormat;

// Loader Class should extend this class and should implement the
// processXml(String xmlString) method to process the Xml String.
public class XmlLoaderManager
{
	private String _projectName			= null;
	private int _updateFrequency 		= 1;
	private XmlLoaderDao _dao 			= null;
	private boolean _continueOnError 	= true;
    private boolean _preserveValues = false;
	protected int _rejectedCount = 0;
	protected ArrayList _errorIndexArray	= null;
   	private static transient final org.apache.log4j.Logger _logger = 	com.addval.utils.LogMgr.getLogger(XmlLoaderManager.class);


	public void setProjectName(String aName) {
		_projectName = aName;
	}

	public String getProjectName() {
		return _projectName;
	}

	public void setContinueOnError(boolean aBool) {
		_continueOnError = aBool;
	}

	public boolean getContinueOnError() {
		return _continueOnError;
	}

	public void setPreserveValue(boolean aBool) {
		_preserveValues = aBool;
	}

	public boolean getPreserveValues() {
		return _preserveValues;
	}

	public void setXmlLoaderDao(XmlLoaderDao dao) {
		_dao = dao;
	}

	public XmlLoaderDao getXmlLoaderDao() {
		return _dao;
	}

	public void setUpdateFrequency(int freq) {
		_updateFrequency = freq;
	}

	public int getUpdateFrequency() {
		return _updateFrequency;
	}

	public XmlLoaderManager() throws SQLException
	{
        //_projectName 	= projectName;
		// executeBatch will execute after the specified frequency is reached
		_errorIndexArray = new ArrayList();
		//_dao 			 = new XmlLoaderDao( projectName );
		_dao			 = new XmlLoaderDao();
		//setLookupSqlName( Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "selectQuery", "selectQuery" ) );
		//setInsertSqlName( Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "insertQuery", "insertQuery" ) );
		//setUpdateSqlName( Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "updateQuery", "updateQuery" ) );
		//setDeleteSqlName( Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "deleteQuery", "deleteQuery" ) );
	}

    // All Loader classes should implement this method to process
    public void processXml(String xmlString) throws InvalidInputException
	{
		throw new InvalidInputException( "Base class method doe snot have any implementation.\nXmlLoaderManager should be extended for specific implemenation of processXml(String xmlString) " );
	}

	// Method to process Xml file using File type input
	public void processXml(File xmlFile) throws Exception
    {
        _logger.debug("processXml-File");
		processXml( ParserUtils.getString( xmlFile ) );
    }

	// To call getKeyPairs method of XmlLoaderDao
	public Map getKeyPairs(String sqlName) throws SQLException
	{
		_logger.debug("getKeyPairs");
		return _dao.getKeyPairs( sqlName );

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
    	processXml( objects.toArray() );
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
		_logger.debug("processXml-Object[]" );
		int updateCount = 0;
		int insertCount = 0;
		// implementing classes may have actions in the preProcess method
		preProcess();
		//boolean isMap = objects[0] != null && (objects[0] instanceof Map);
		boolean isMap = false;
		for(int i=0;i<objects.length;i++) {
			if(objects[i] != null && (objects[i] instanceof Map)) {
				isMap = true;
				break;
			}
		}
		for (int i=0; i<objects.length; i++) {
			try {
				if (objects[i] == null)
					continue;
				// the implementing classes may do actions in the clear() method, the actions to be
				// taken between every xml processing
				clear();
				// if the table has this row already, it is to be an update else it would be an insert
                boolean hasthisRow = false;
		//		System.out.println("############################# _preserveValues" + _preserveValues);
                if (_preserveValues)
                    hasthisRow = hasThisRow( objects[i], isMap );
                else
                    hasthisRow = _dao.hasThisRow( objects[i], isMap );
             //System.out.println("###########  hasthisRow =" + hasthisRow);
                if (hasthisRow){
                    _dao.updateRow( objects[i], isMap, ++updateCount % _updateFrequency == 0 );
                    if (dateCheck)
                        _dao.insertRow( objects[i], isMap, ++insertCount % _updateFrequency == 0 );
                }
                else{
                    _dao.insertRow( objects[i], isMap, ++insertCount % _updateFrequency == 0 );
                }
			}
			catch (SQLException e) {
				_rejectedCount++;
				_errorIndexArray.add( new Integer(i+1) );
				_logger.error( objects[i] + "");
			//	System.out.println(e);
				_logger.error( e );
				if(!_continueOnError)
					throw e;
			}
		}
		if (autoClose)
        	close();
		// implementing classes may have actions in the postProcess method
		postProcess();
		_logger.debug("exit processXml-Object[]" );
	}

    private boolean hasThisRow(Object object, boolean isMap) throws SQLException
    {
        if (!isMap)
            throw new IllegalAccessError( "Object must be implementation of Map. " + object.getClass().getName() );
		return _dao.selectRow( (HashMap)object ) != null;
    }

	public HashMap[] executeAndReturn(Object object) throws SQLException
	{
		boolean isMap = object != null && (object instanceof Map);
		return _dao.selectRow( object,isMap );
	}

	public HashMap[] executeAndReturn(Object object, String sqlName) throws SQLException
	{
		boolean isMap = object != null && (object instanceof Map);
		return _dao.selectRow( object,isMap, sqlName );
	}

	// To execute a query
	public void executeQuery(Object objects[], String sqlType) throws SQLException
	{
		_logger.debug( "enter executeQuery" );
		boolean isMap = objects[0] != null && (objects[0] instanceof Map);
		if(sqlType=="insertQuery"){
		for (int i=0; i<objects.length; i++)
			_dao.insertRow( objects[i],isMap,true);
		}
		else if(sqlType=="deleteQuery"){
			for (int i=0; i<objects.length; i++)
				_dao.deleteRow( objects[i],isMap,true );
		}
		_logger.debug( "exit executeQuery" );
	}

	public HashMap []  getDeleteMap(HashMap [] map, Map newMap, String newStartDateName,  String newEndDateName , String startDateFieldname,  String endDateFieldname)
	{
            _logger.trace( "enter getDeleteMap" );
			GregorianCalendar newStartDate	= new GregorianCalendar();
			GregorianCalendar newEndDate	= new GregorianCalendar();
			GregorianCalendar startDate		= new GregorianCalendar();
			GregorianCalendar endDate		= new GregorianCalendar();
			Object 	newStartDateobj 		= newMap.get(newStartDateName);
			String newStartDatestring		= newStartDateobj.toString();
			Object newEndDateobj 			= newMap.get(newEndDateName);
			String newEndDateString			= newEndDateobj.toString();
			SimpleDateFormat inFormater 	= new SimpleDateFormat("yyyy-MM-dd");

			HashMap deleteMap[]			= null;
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
					if(((newStartDate.after( startDate )==true)||(newStartDate.equals( startDate )==true))&&((newStartDate.before(endDate)==true))||(newStartDate.equals(endDate)==true)){
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
					if(((newEndDate.after(startDate)==true)||(newEndDate.equals(startDate)==true))&&((newEndDate.before(endDate)==true))||(newEndDate.equals(endDate)==true)){
						endIndex=i;
						endIndexChanged = true;
						break;
					}
				}
				if((startIndexChanged==false)&&(endIndexChanged==false)){
					String startStr	= map[0].get( startDateFieldname ).toString();
					String endStr	= map[map.length-1].get(endDateFieldname).toString();
					endDate.setTime( new Date(inFormater.parse(endStr).getTime()) );
					startDate.setTime( new Date(inFormater.parse( startStr ).getTime()) );
					if(newStartDate.before(startDate)&&(newEndDate.after(endDate))){
						deleteMap	= new HashMap[map.length];
						for(int i=0;i<map.length;i++){
							deleteMap[i] = new HashMap();
						}
						for(int i=0;i<map.length;i++){
							deleteMap[i]=map[i];
						}
					}
					return deleteMap;
				}
				int size	= endIndex-startIndex+1;
				deleteMap	= new HashMap[size];
				for(int i=0;i<size;i++){
					deleteMap[i] = new HashMap();
				}
				for(int i=0;i<size;i++){
					deleteMap[i]=map[startIndex];
					startIndex++;
				}
			}catch(Exception e){e.getMessage();}
		_logger.debug( "exit getDeleteMap" );
		return deleteMap;
	}

	public HashMap []  getInsertMap(HashMap [] map, Map newMap, String newStartDateName,  String newEndDateName , String startDateFieldname,  String endDateFieldname, String fieldNames[], String keyNames[] )
	{
			_logger.debug( "enter getInsertMap" );
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
			for(int i=0;i<3;i++){
				insertMap[i] = new HashMap();
			}
			try{

            int noOfFieldNames 	= fieldNames.length;
			int noOfKeyNames	= keyNames.length;
			newStartDate.setTime(new Date( inFormater.parse(newStartDatestring).getTime()) );
			newEndDate.setTime(new Date( inFormater.parse(newEndDateString).getTime()) );
			boolean insertNew=false;
			if(map==null){
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
			if(insertNew==true){
				insertMap[0].put(startDateFieldname, inFormater.format(newStartDate.getTime()));
				insertMap[0].put(endDateFieldname, inFormater.format(newEndDate.getTime()));
				for(int i=0;i<noOfKeyNames;i++){
						insertMap[0].put(fieldNames[i], newMap.get(keyNames[i]));
					}
				return insertMap;
			}
			String startStr = map[0].get(startDateFieldname).toString();
			String endStr 	= map[0].get(endDateFieldname).toString();
			startDate.setTime( new Date(inFormater.parse(startStr).getTime()) );
			endDate.setTime( new Date(inFormater.parse(endStr).getTime()) );
			int position = 0;
			if((newStartDate.equals(startDate)==true)){
				insertMap[0].put( startDateFieldname, inFormater.format(startDate.getTime()) );
			}else{
					insertMap[0].put( startDateFieldname, inFormater.format(startDate.getTime()) );
					newStartDate.add(java.util.Calendar.DAY_OF_MONTH,-1);
					insertMap[0].put( endDateFieldname, inFormater.format(newStartDate.getTime()) );
                	for(int i=0;i<noOfFieldNames;i++){
						insertMap[0].put(fieldNames[i], map[0].get(fieldNames[i]));
					}
					newStartDate.add(java.util.Calendar.DAY_OF_MONTH,1);
					insertMap[1].put( startDateFieldname, inFormater.format(newStartDate.getTime()) );
					position = 1;
				}

				startStr 	= map[map.length-1].get(startDateFieldname).toString();
				endStr 		= map[map.length-1].get(endDateFieldname).toString();
				endDate.setTime( new Date(inFormater.parse(endStr).getTime()) );
				if((newEndDate.equals(endDate)==true)){
					insertMap[position].put( endDateFieldname, inFormater.format(endDate.getTime()) );
					for(int i=0;i<noOfKeyNames;i++){
						insertMap[position].put(fieldNames[i], newMap.get(keyNames[i]));
					}//
				}else{
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
			}catch(Exception e){
				e.getMessage();
			}
			_logger.debug( "exit getInsertMap" );
	      return insertMap;
	}

	public void deleteOverlapping() throws SQLException {
		_dao.deleteOverlapping();
	}
	// unused method as of now
	public int getIntValue( String sqlName, Object object ) throws SQLException
	{
		return _dao.getIntValue( sqlName, object );
	}

	public int getIntValue( String sqlName) throws SQLException
	{
		return _dao.getIntValue( sqlName);
	}

	// unused method as of now
	public String getStringValue( String sqlName, Object object ) throws SQLException
	{
		return _dao.getStringValue( sqlName, object );
	}

	public void setLookupSqlName(String lookupSqlName) throws SQLException
	{
		_dao.setLookupSqlName( lookupSqlName );
	}

	public void setInsertSqlName(String insertSqlName) throws SQLException
	{
		_dao.setInsertSqlName( insertSqlName );
	}

	public void setUpdateSqlName(String updateSqlName) throws SQLException
	{
		_dao.setUpdateSqlName( updateSqlName );
	}

	public void setDeleteSqlName(String deleteSqlName) throws SQLException
	{
		_dao.setDeleteSqlName( deleteSqlName );
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

	public void  setErrorIndexArray(ArrayList errorList)
	{
       _errorIndexArray = errorList ;
	}

	public ArrayList getErrorIndexArray()
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

			String details = getprintDetails().toString();
			_logger.info( details );
			System.out.println( details );


			/*System.out.println("Database Operations:");
			System.out.println( "No of rows rejected   " + _rejectedCount );
			_log.logInfo("Database Operations:");
			_log.logInfo( "No of rows rejected   " + _rejectedCount );
			 if(_errorIndexArray.size()>0) {
				System.out.println( "Index of rejected records " + _errorIndexArray );
				_log.logInfo( "Index of rejected records " + _errorIndexArray );
			}    */
		}
		catch(Exception e) {
			// don;t do anything
		}
	}

	public String getprintDetails()
	{
		StringBuffer details = new StringBuffer();
		details.append("\nDatabase Operations:" ).append("\n");
		details.append(_dao.getprintDetails()).append("\n");
		details.append("No of rows rejected   " + _rejectedCount).append("\n");
		 if(_errorIndexArray != null && _errorIndexArray.size()>0)
				details.append("Index of rejected records " + _errorIndexArray ).append("\n");
		return details.toString() ;
	}
}
