//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\server\\EJBEEditorMetaDataBeanPK.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\ejbutils\\server\\EJBEEditorMetaDataBeanPK.java

package com.addval.ejbutils.server;

import java.io.Serializable;

/**
 * The primary key object for the entity bean EJBEEditorMetaData. The editor name,
 * editor type and the userId (if the editor is customized) together form the
 * primary key for the bean
 */
public class EJBEEditorMetaDataBeanPK implements Serializable {
	private String _name = null;
	private String _type = null;
	private String _userId = null;
	private String _default= "DEFAULT";

	/**
	 * @roseuid 3E2073980276
	 */
	public EJBEEditorMetaDataBeanPK() {

		_name 	= "";
		_type 	= "";
		_userId = "";
	}

	/**
	 * @param name
	 * @param type
	 * @param userId
	 * @roseuid 3B6F4F1502BA
	 */
	public EJBEEditorMetaDataBeanPK(String name, String type, String userId) {

		_name 	= name;
		_type 	= type;
		_userId = userId;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B6F501F0140
	 */
	public String getName() {
		return (_name != null && _name.trim().length() > 0 ) ? _name : _default;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B6F501902B4
	 */
	public String getType() {
		return (_type != null && _type.trim().length() > 0 ) ? _type : _default;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3D4EC5FD0295
	 */
	public String getUserId() {
		return (_userId != null && _userId.trim().length() > 0 ) ? _userId : _default;
	}

	/**
	 * @return int
	 * @roseuid 3B6F50260028
	 */
	public int hashCode() {

		String code = getName() + getType() + getUserId();

		return code.hashCode();
	}

	/**
	 * @param obj
	 * @return boolean
	 * @roseuid 3B6F502F00CB
	 */
	public boolean equals(Object obj) {

		boolean rv = false;

		if (obj == null || !(obj instanceof EJBEEditorMetaDataBeanPK)) {
			rv = false;
		}
		else {

			String name   = ((EJBEEditorMetaDataBeanPK)obj).getName();
			String type   = ((EJBEEditorMetaDataBeanPK)obj).getType();
			String userId = ((EJBEEditorMetaDataBeanPK)obj).getUserId();

			if (name.equals( getName() ) && type.equals( getType() ) && userId.equals( getUserId() ) )
				rv = true;
			else
				rv = false;
		}

		return rv;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B6F5042019A
	 */
	public String toString() {

		return "EditorName =" + getName() + " : EditorType =" + getType() + " : User Id =" + getUserId();
	}
}
