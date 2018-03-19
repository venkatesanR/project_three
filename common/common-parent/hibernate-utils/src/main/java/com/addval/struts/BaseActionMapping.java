//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\BaseActionMapping.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\BaseActionMapping.java

package com.addval.struts;

import org.apache.struts.action.ActionMapping;

/**
 * Base Action Mapping Class - For all action mappings. Derive from this instead 
 * of ActionMapping class. This class provides flexibility to set the subsystem 
 * property with each action tag in the struts-config.xml using the set-property 
 * tag. 
 * There are two methods to configuring this. In method 1, the BaseActionMapping 
 * is configured in the action-mappings tag. This means that this will be the 
 * default ActionMapping instance passed to all the actions within this tag unless 
 * it is overriden as shown in method 2.
 * METHOD 1:
 * <action-mappings type="com.addval.struts.BaseActionMapping">
 * <action path="/lookDownXml" type="com.addval.struts.LookupXmlAction" 
 * name="exactLookupForm" scope="request">
 * <set-property property="subsystem" value="Rules" />
 * </action>
 * </action-mapping>
 * </action-mappings>
 * METHOD 2:
 * <action-mappings>
 * <action path="/lookDownXml" 
 * className="com.addval.struts.BaseActionMapping" 
 * type="com.addval.struts.LookupXmlAction" name="exactLookupForm" scope="request">
 * <set-property property="subsystem" value="Rules" />
 * </action>
 * </action-mapping>
 * </action-mappings>
 * @author AddVal Technology Inc.
 */
public class BaseActionMapping extends ActionMapping 
{
	protected String _subsystem;
	
	/**
	 * set the subsystem associated with this action mapping
	 * @param aSubsystem
	 * @roseuid 3DCC765F01BB
	 */
	public void setSubsystem(String aSubsystem) 
	{
		_subsystem = aSubsystem;		
	}
	
	/**
	 * get the subsystem associated with this action mapping
	 * @return String
	 * @roseuid 3DCC76800050
	 */
	public String getSubsystem() 
	{
		return this._subsystem;		
	}
}
