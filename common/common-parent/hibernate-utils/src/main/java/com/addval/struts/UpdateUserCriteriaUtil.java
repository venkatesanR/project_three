
package com.addval.struts;

import java.sql.ResultSet;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.server.EJBSCargoresOverwriterHome;
import com.addval.ejbutils.server.EJBSCargoresOverwriterRemote;
import com.addval.metadata.*;
import com.addval.utils.XRuntime;
import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;
import java.util.*;
import java.rmi.RemoteException;

import org.apache.struts.util.LabelValueBean;

import javax.naming.NamingException;

public class UpdateUserCriteriaUtil
{
	private String subsystem = null;

	public UpdateUserCriteriaUtil(String subsystem)	{
		setSubsystem(subsystem);
	}

	public String getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	public void setDirectoryNames(UpdateUserCriteriaForm form) throws XRuntime
	{
		ArrayList directories = new ArrayList();
		try {
			DirectoryMgr manager = new DirectoryMgr(getSubsystem());
			Hashtable param = new Hashtable();
			param.put("EDITOR_NAME",form.getEditorName());
			EJBResultSet ejbRS = manager.lookup( param );

			if(ejbRS != null) {

				ResultSet rs = ( ResultSet ) ejbRS;
				String directoryName = null;
				String directoryDesc = null;

				while(rs.next()) {

					directoryName = rs.getString("DIRECTORY_NAME");
					directoryDesc = rs.getString("DIRECTORY_DESC");

					if(directoryName != null && directoryDesc != null) {
						directories.add( new LabelValueBean( directoryName, directoryDesc ) );
					}
				}

			}
		}
		catch(Exception ex) {
			throw new XRuntime(getSubsystem(),ex.getMessage());
		}
        form.setDirectoryNames(directories);
	}

	public void setCriteriaNames(UpdateUserCriteriaForm form) throws XRuntime
	{
		ArrayList criterias = new ArrayList();
		try {
			UpdateUserCriteriaMgr manager = new UpdateUserCriteriaMgr(getSubsystem());
			Hashtable param = new Hashtable();
			param.put("EDITOR_NAME",form.getEditorName());
			param.put("DIRECTORY_NAME",form.getDirectoryName());
			EJBResultSet ejbRS = manager.lookup( param );

			if(ejbRS != null) {

				ResultSet rs = ( ResultSet ) ejbRS;
				String criteriaName = null;
				String criteriaDesc = null;

				while(rs.next()) {

					criteriaName = rs.getString("CRITERIA_NAME");
					criteriaDesc = rs.getString("CRITERIA_DESC");

					if(criteriaName != null && criteriaDesc != null) {

						criterias.add( new LabelValueBean( criteriaName, criteriaName ) );


						if(criteriaName.equals( form.getCriteriaName() )) {

							form.setCriteria(new UpdateUserCriteria(form.getEditorName(),
																	form.getDirectoryName(),
																	criteriaName,
																	criteriaDesc,
																	rs.getString("FILTER"),
																	rs.getString("DEFAULT_FILTER"),
																	rs.getString("UPDATE_VALUE")
																	)
											);
						}
					}
				}

			}
		}
		catch(Exception ex) {

			throw new XRuntime( getSubsystem(),ex.getMessage() );

		}
        form.setCriteriaNames( criterias );
	}

	public void populateData(UpdateUserCriteriaForm form) throws Exception{
		UpdateUserCriteria criteria = form.getCriteria();
		if(criteria != null ) {

			StringTokenizer updateOTokens  = null;
			StringTokenizer updateITokens  = null;
			String updateSet = null;
			String columnName= null;
			String columnOperator= null;
			String columnValue= null;
			UpdateColumn updateColumn = null;

			Vector updateColumns = new Vector(5);
			updateOTokens  = new StringTokenizer(criteria.getUpdateValue(),",",false);

			while(updateOTokens.hasMoreTokens()) {

				updateSet = updateOTokens.nextToken();
				updateITokens =  new StringTokenizer(updateSet,"|",false);

				columnName = updateITokens.nextToken();
				columnOperator = updateITokens.nextToken();
				columnValue = updateITokens.nextToken();

				updateColumn = new UpdateColumn();
				updateColumn.setColumnName( columnName );
				updateColumn.setColumnOperator( columnOperator );
				updateColumn.setColumnValue( columnValue );
				updateColumns.add( updateColumn );

			}

			form.setUpdateColumns(updateColumns);
			form.setSearchCriteria(criteria.getFilter());
			form.setCriteriaDesc(criteria.getCriteriaDesc());
		}
		else {
			form.setUpdateColumns( new Vector(5));
			form.setSearchCriteria(null);
			form.setCriteriaDesc(null);
		}
	}

	public UpdateUserCriteria getUpdateUserCriteria(UpdateUserCriteriaForm form) {
		UpdateUserCriteria criteria = new UpdateUserCriteria(form.getEditorName(),
												 form.getDirectoryName(),
												 getCriteriaName(form),
												 getCriteriaDesc(form),
												 form.getSearchCriteria(),
												 getDefaultFilter(form),
												 getUpdateValue(form)
											);
		return criteria;
	}

	public String getCriteriaName(UpdateUserCriteriaForm form) {
		String criteriaName = null;
		if(form.getNewCriteriaName() != null && form.getNewCriteriaName().trim().length() > 0)
			criteriaName = form.getNewCriteriaName().trim();
		else if(form.getCriteriaName() != null)
			criteriaName = form.getCriteriaName();
		return criteriaName;
	}

	public String getCriteriaDesc(UpdateUserCriteriaForm form) {
		String criteriaDesc = null;
		if(form.getNewCriteriaName() != null && form.getNewCriteriaName().length() > 0 &&
		   form.getNewCriteriaDesc() != null && form.getNewCriteriaDesc().length() > 0 )
			criteriaDesc = form.getNewCriteriaDesc().trim();
		else if(form.getCriteriaDesc() != null)
			criteriaDesc = form.getCriteriaDesc();
		return criteriaDesc;
	}

	public String getDefaultFilter(UpdateUserCriteriaForm form) {
		String defaultFilter = null;
		try{
			DirectoryMgr manager = new DirectoryMgr(getSubsystem());
			Hashtable param = new Hashtable();
			param.put("EDITOR_NAME",form.getEditorName());
			param.put("DIRECTORY_NAME",form.getDirectoryName());
			EJBResultSet ejbRS = manager.lookup( param );
			if(ejbRS != null) {
				ResultSet rs = ( ResultSet ) ejbRS;
				if(rs.next())
					defaultFilter = rs.getString("DIRECTORY_CONDITION");
			}
		}
		catch(Exception ex) {
			throw new XRuntime( getSubsystem(),ex.getMessage() );
		}
		return defaultFilter;
	}

	public String getUpdateValue(UpdateUserCriteriaForm form) {
		StringBuffer updateValue = new StringBuffer();

		if(form.getUpdateColumns() == null)
			return updateValue.toString().length() > 0 ? updateValue.toString() : null;

		UpdateColumn updateColumn = null;

		for(Iterator iterator = form.getUpdateColumns().iterator();iterator.hasNext(); ) {

			updateColumn = (UpdateColumn)iterator.next();

			if( ( updateColumn.getColumnName() != null && !updateColumn.getColumnName().trim().equals("")  ) &&
				( updateColumn.getColumnValue() != null && !updateColumn.getColumnValue().trim().equals("") ) ) {

				updateValue.append(",")
							.append( updateColumn.getColumnName() ).append("|")
							.append( updateColumn.getColumnOperator() ).append("|")
							.append( updateColumn.getColumnValue() );
			}
		}
		return updateValue.toString().length() > 0 ? updateValue.toString().substring(1) : null;
	}

	public void validateUpdateSql(EditorMetaData metadata, UpdateUserCriteria criteria) throws XRuntime {
		try {
			String SUBSYSTEM = "usercriteria";
			getCargoresOverwriterRemote(SUBSYSTEM).validate(metadata, criteria);
		}
		catch(Exception ex) {
			throw new XRuntime(getSubsystem(),"Invalid Update Criteria. Please check the Criteria.");
		}
	}

    private EJBSCargoresOverwriterRemote getCargoresOverwriterRemote(String subsystem) throws NamingException, RemoteException, Exception {
        String homeName = Environment.getInstance(subsystem).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSCargoresOverwriterBeanName", "CargoresOverwriterBean");
        EJBSCargoresOverwriterHome home = ( EJBSCargoresOverwriterHome ) EJBEnvironment.lookupEJBInterface( subsystem, homeName , EJBSCargoresOverwriterHome.class );
        EJBSCargoresOverwriterRemote remote = home.create();
        return remote;
	}
}

