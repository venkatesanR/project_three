//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\LookupForm.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\LookupForm.java

package com.addval.springstruts;

import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;

/**
 * <b>An Action Form based on AddVal Editor Metadata Framework. This form can be
 * used to display a list for picking a row from the list.The metadata associated
 * with the form is read from the database through the EJBSEditorMetaData bean and
 * cached within this form the first time this beans reset method is called. Some
 * of the default properties for this form can be configured using the
 * FormBeanConfig class. A sample form bean declaration is shown below
 * <form-bean className="com.addval.struts.BaseFormBeanConfig"
 * name="lookupForm" type="com.addval.struts.LookupForm">
 * <set-property property="securityManagerType"
 * value="com.addval.cargoresui.rulesui.RulesListSecurityManager" />
 * <set-property property="initialLookup" value="true" />
 * </form-bean>
 * </b>@author
 * AddVal Technology Inc.
 */
public class LookupForm extends ListForm
{
	private String _displayFieldName;
	private String _displayFieldValue;
	private String _selectFieldName;
	private String _selectFieldValue;
	private String _orderbyFieldName;
	private String _selectFormName;
	private String _title;
	private String _displayTitle;
	private int _displayCellNo;
	private int _selectCellNo;
    private boolean isLookupById = false;
    private String dataDelimiter = null;
    private int delimitedValuesMaxCount=0;

    /**
	 * @return String
	 * @roseuid 3DD2E9F90246
	 */
	public String getDisplayFieldName()
	{
		return _displayFieldName;
	}

	/**
	 * @param aFieldName
	 * @roseuid 3DD2EA280063
	 */
	public void setDisplayFieldName(String aFieldName)
	{
		_displayFieldName = aFieldName;
	}

	public String getSelectFormName() {
		return _selectFormName;
	}

	public void setSelectFormName(String selectFormName) {
		_selectFormName = selectFormName;
	}

	/**
	 * @return String
	 * @roseuid 3DD2EA4B01AE
	 */
	public String getSelectFieldName()
	{
		return _selectFieldName;
	}

	/**
	 * @param aFieldName
	 * @roseuid 3DD2EA570287
	 */
	public void setSelectFieldName(String aFieldName)
	{
		_selectFieldName = aFieldName;
	}

	/**
	 * @return String
	 * @roseuid 3DD2EA67012C
	 */
	public String getOrderbyFieldName()
	{
		return _orderbyFieldName;
	}

	/**
	 * @param aFieldName
	 * @roseuid 3DD2EA7600C9
	 */
	public void setOrderbyFieldName(String aFieldName)
	{
		_orderbyFieldName = aFieldName;
	}

	/**
	 * @return String
	 * @roseuid 3DD2EA87018C
	 */
	public String getTitle()
	{
		return _title;
	}

	/**
	 * @param aTitle
	 * @roseuid 3DD2EA9701AD
	 */
	public void setTitle(String aTitle)
	{
		_title = aTitle;
	}

	/**
	 * @return String
	 * @roseuid 3DD2EACA00AC
	 */
	public String getDisplayTitle()
	{
		return _displayTitle;
	}

	/**
	 * @param aTitle
	 * @roseuid 3DD2EAEA0383
	 */
	public void setDisplayTitle(String aTitle)
	{
		_displayTitle = aTitle;
	}

	/**
	 * @return int
	 * @roseuid 3DD2EB0E02E5
	 */
	public int getDisplayCellNo()
	{
		return _displayCellNo;
	}

	/**
	 * @param aNo
	 * @roseuid 3DD2EB230263
	 */
	public void setDisplayCellNo(int aNo)
	{
		_displayCellNo = aNo;
	}

	/**
	 * @return int
	 * @roseuid 3DD2EB48002B
	 */
	public int getSelectCellNo()
	{
		return _selectCellNo;
	}

	/**
	 * @param aNo
	 * @roseuid 3DD2EB59017A
	 */
	public void setSelectCellNo(int aNo)
	{
		_selectCellNo = aNo;
	}

	/**
	 * @param mapping
	 * @param request
	 * @roseuid 3DD2ECCA0329
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{

		super.reset(mapping, request);

		setDisplayFieldName("");
		setDisplayFieldValue("");
		setSelectFieldName("");
		setSelectFieldValue("");
		setSelectFormName("");
		setTitle("");
		setDisplayTitle("");
		setDisplayCellNo(0);
		setSelectCellNo(0);
        setForLookup(true);

	}

	/**
	 * @return String
	 * @roseuid 3DD2F84500F5
	 */
	public String getDisplayFieldValue()
	{
		return _displayFieldValue;
	}

	/**
	 * @param aFieldValue
	 * @roseuid 3DD2F8530091
	 */
	public void setDisplayFieldValue(String aFieldValue)
	{
		_displayFieldValue = aFieldValue;
	}

	/**
	 * @return String
	 * @roseuid 3DD2F8B70379
	 */
	public String getSelectFieldValue()
	{
		return _selectFieldValue;
	}

	/**
	 * @param aFieldValue
	 * @roseuid 3DD2F8C200EA
	 */
	public void setSelectFieldValue(String aFieldValue)
	{
		_selectFieldValue = aFieldValue;
	}
    public boolean isLookupById() {
        return isLookupById;
    }

    public void setLookupById(boolean lookupById) {
        isLookupById = lookupById;
    }

	public String getDataDelimiter() {
		return dataDelimiter;
	}

	public void setDataDelimiter(String dataDelimiter) {
		this.dataDelimiter = dataDelimiter;
	}

	public int getDelimitedValuesMaxCount() {
		return delimitedValuesMaxCount;
	}

	public void setDelimitedValuesMaxCount(int delimitedValuesMaxCount) {
		this.delimitedValuesMaxCount = delimitedValuesMaxCount;
	}

}
