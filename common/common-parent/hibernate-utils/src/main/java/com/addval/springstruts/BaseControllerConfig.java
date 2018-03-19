//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\BaseControllerConfig.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\BaseControllerConfig.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.springstruts;

import org.apache.struts.config.ControllerConfig;

/**
 * Base Controller config Class - For all action mappings. Derive from this
 * instead of ControllerConfig class. This class provides flexibility to set the
 * subsystem property with for the entire struts module in the struts-config.xml
 * using the set-property tag.
 * This can be configured in struts-config.xml as shown below
 * <controller
 * className="com.addval.struts.BaseControllerConfig"><set-property
 * property="subsystem" value="Rules" /></controller>
 * @author AddVal Technology Inc.
 */
public class BaseControllerConfig extends ControllerConfig
{
	protected String _subsystem;
	protected String _viewrole = "";
	protected String _editrole = "";

	/**
	 * set the subsystem associated with this struts module
	 * @param aSubsystem
	 * @roseuid 3DCE038C023D
	 */
	public void setSubsystem(String aSubsystem)
	{
		_subsystem = aSubsystem;
	}

	/**
	 * get the subsystem associated with this struts module
	 * @return String
	 * @roseuid 3DCE038E0055
	 */
	public String getSubsystem()
	{
		return _subsystem;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE331D020F
	 */
	public String getViewrole()
	{
		return _viewrole;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE331D0237
	 */
	public String getEditrole()
	{
		return _editrole;
	}

	/**
	 * @param aViewrole
	 * @roseuid 3EFE331D0269
	 */
	public void setViewrole(String aViewrole)
	{
		_viewrole = aViewrole;
	}

	/**
	 * @param aEditrole
	 * @roseuid 3EFE331D02D7
	 */
	public void setEditrole(String aEditrole)
	{
		_editrole = aEditrole;
	}
}
