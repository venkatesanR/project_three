//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\DetailEditForm.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\DetailEditForm.java

package com.addval.struts;

import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import java.util.Vector;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;

/**
 * <b>An Action Form based on AddVal Editor Metadata Framework. This form can be
 * used for master/detail editor. Two Editors from the metadata can be associated
 * with this form. As long as the two editors have common keys this editor should
 * work. Limitation: However there are restrictions on the number of data elements
 * in the detail field (other than the keys) can only be 1. This needs to be fixed
 * in the next release
 * A sample form bean declaration is shown below
 * <form-bean name="commodityGroupEditForm"
 * type="com.addval.struts.DetailEditForm"
 * className="com.addval.struts.BaseFormBeanConfig">
 * <set-property property="securityManagerType"
 * value="com.addval.cargoresui.rulesui.RulesEditSecurityManager" />
 * <set-property property="editorName" value="RS_COMMODITY_GROUP" />
 * <set-property property="detailEditorName"
 * value="RS_COMMODITY_GROUP_COMMODITY" />
 * </form-bean>
 * </b>@author
 * AddVal Technology Inc.
 */
public class DetailEditForm extends EditForm
{
	private Vector _parentKeyColumns;
	private Vector _detailInfoColumns;
	private String _formInterceptorType = "";
	protected FormInterceptor _formInterceptor = null;
	public EditorMetaData _detailMetadata = null;
	public EJBResultSet _detailResultset = null;

	/**
	 * Access method for the _formInterceptorType property.
	 *
	 * @return   the current value of the _formInterceptorType property
	 */
	public String getFormInterceptorType()
	{
		return _formInterceptorType;
	}

	/**
	 * Sets the value of the _formInterceptorType property.
	 *
	 * @param aFormInterceptorType the new value of the _formInterceptorType property
	 */
	public void setFormInterceptorType(String aFormInterceptorType)
	{
		_formInterceptorType = aFormInterceptorType;

		if ((_formInterceptorType != null) && (!_formInterceptorType.equals("")))
		{
			setFormInterceptor(FormInterceptorFactory.getInstance(_formInterceptorType));
		}
	}

	/**
	 * Access method for the _formInterceptor property.
	 *
	 * @return   the current value of the _formInterceptor property
	 */
	public FormInterceptor getFormInterceptor()
	{
		return _formInterceptor;
	}

	/**
	 * Sets the value of the _formInterceptor property.
	 *
	 * @param aFormInterceptor the new value of the _formInterceptor property
	 */
	public void setFormInterceptor(FormInterceptor aFormInterceptor)
	{
		_formInterceptor = aFormInterceptor;
	}

	/**
	 * @return com.addval.metadata.EditorMetaData
	 * @roseuid 3DDD7B09030F
	 */
	public EditorMetaData getDetailMetadata()
	{
		return _detailMetadata;
	}

	/**
	 * @param aMetadata
	 * @roseuid 3DDD7B090323
	 */
	public void setDetailMetadata(EditorMetaData aMetadata)
	{
		_detailMetadata = aMetadata;
	}

	/**
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DDD7B09032D
	 */
	public EJBResultSet getDetailResultset()
	{
		return _detailResultset;
	}

	/**
	 * @param aResultset
	 * @roseuid 3DDD7B090341
	 */
	public void setDetailResultset(EJBResultSet aResultset)
	{
	   _detailResultset = aResultset;
	}

	/**
	 * @param mapping
	 * @param request
	 * @roseuid 3DDD81D300AC
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{

	   super.reset(mapping, request);

	   BaseActionMapping baseMapping = (BaseActionMapping) mapping;
	   //org.apache.struts.config.ApplicationConfig appConfig = (org.apache.struts.config.ApplicationConfig) request.getAttribute(org.apache.struts.action.Action.APPLICATION_KEY);

       org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);


	   BaseFormBeanConfig formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());

	   // Initialize the data elements from the formConfig
	   String editorName = formConfig.getDetailEditorName();
	   String editorType = formConfig.getDetailEditorType();
	   String subsystem = null;


	   if ((formConfig.getSubsystem() == null) || (formConfig.getSubsystem().equals("")))
	   {
			BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();
			subsystem = baseConfig.getSubsystem();
		} else
		{
			subsystem = formConfig.getSubsystem();
		}


		// If detail editorName is not configured for this mapping. Check if it is available in the
		// request as an Attribute or as a Parameter
		if ((editorName == null) || (editorName.equals("")))
		{
			editorName = (String) request.getAttribute("DETAIL_EDITOR_NAME");
		}

		if ((editorName == null) || (editorName.equals("")))
		{
			editorName = (String) request.getParameter("DETAIL_EDITOR_NAME");
		}

		if (!formConfig.getFormInterceptorType().equals(""))
		{
			setFormInterceptorType(formConfig.getFormInterceptorType());
		}


	   if (formConfig.getDetailEditorName().equals(""))
	   {
		   _detailMetadata  = null;
		   _parentKeyColumns = null;
		   _detailInfoColumns = null;
	   }

	   _detailResultset = null;

	   if (_detailMetadata == null)
	   {
		       try {
					 String homeName = Environment.getInstance(subsystem).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName", "EJBSTableManager");

				     EJBSEditorMetaDataHome   metaDataHome   = ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( subsystem, homeName , EJBSEditorMetaDataHome.class );
				     EJBSEditorMetaDataRemote metaDataRemote = metaDataHome.create( );

				     _detailMetadata = metaDataRemote.lookup( editorName, editorType );

				     // Now populate parentKeyColumns and detailInfoColumns
					_parentKeyColumns = new java.util.Vector();

				     for (int i = 1; i <= getMetadata().getColumnCount(); ++i)
				     {
						 	ColumnMetaData columnMetaData = getMetadata().getColumnMetaData(i);

						    // System.out.println("Picking Column: " + columnMetaData.getName());
						    if (columnMetaData.isKey() || columnMetaData.isEditKey())
						    {

							    // System.out.println("Picking Column is a key: " + columnMetaData.getName());

								// now check if it is part of detailmetadata
								for (int j=1;j<=_detailMetadata.getColumnCount(); ++j)
								{


									ColumnMetaData dtlcolumnMetadata = _detailMetadata.getColumnMetaData(j);

								    // System.out.println("Detail Column: " + dtlcolumnMetadata.getName());

									if (columnMetaData.getName().equals(dtlcolumnMetadata.getName()))
									{
										_parentKeyColumns.add(dtlcolumnMetadata);

										// System.out.println("Adding Column to key: " + dtlcolumnMetadata.getName());
									}

								}

							}
					 }




					_detailInfoColumns = new java.util.Vector();


					for (int i=1;i<=_detailMetadata.getColumnCount(); ++i)
					{
						ColumnMetaData dtlcolumnMetadata = _detailMetadata.getColumnMetaData(i);


						boolean found = false;

						for (int j=0;j<_parentKeyColumns.size();++j)
						{
							ColumnMetaData columnMetaData = (ColumnMetaData) _parentKeyColumns.elementAt(j);

							if (columnMetaData.getName().equals(dtlcolumnMetadata.getName()) == true)
							{
								found = true;
							}

						}

							if (found == false)
							{
								_detailInfoColumns.add(dtlcolumnMetadata);
								// System.out.println("Adding Detail Info: " + dtlcolumnMetadata.getName());
							}



					}





		   	    }
				catch (java.lang.Exception ex)
				{
					_detailMetadata = null;

				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error reading metadata for " + editorName);
				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);
	        	}


		}
	}

	/**
	 * Access method for the _parentKeyColumns property.
	 * @return   the current value of the _parentKeyColumns property
	 * @roseuid 3E205C2502E2
	 */
	public Vector getParentKeyColumns()
	{
		return _parentKeyColumns;
	}

	/**
	 * Sets the value of the _parentKeyColumns property.
	 * @param aParentKeyColumns the new value of the _parentKeyColumns property
	 * @roseuid 3E205C25035A
	 */
	public void setParentKeyColumns(Vector aParentKeyColumns)
	{
		_parentKeyColumns = aParentKeyColumns;
	}

	/**
	 * Access method for the _detailInfoColumns property.
	 * @return   the current value of the _detailInfoColumns property
	 * @roseuid 3E205C260030
	 */
	public Vector getDetailInfoColumns()
	{
		return _detailInfoColumns;
	}

	/**
	 * Sets the value of the _detailInfoColumns property.
	 * @param aDetailInfoColumns the new value of the _detailInfoColumns property
	 * @roseuid 3E205C2600B3
	 */
	public void setDetailInfoColumns(Vector aDetailInfoColumns)
	{
		_detailInfoColumns = aDetailInfoColumns;
	}
}
