//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\metadata\\EJBUIMetaData.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\metadata\\EJBUIMetaData.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.metadata;

import com.addval.metadata.UIMetaData;
import java.util.Hashtable;
import com.addval.metadata.ColumnMetaData;
import javax.rmi.PortableRemoteObject;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.utils.AVConstants;
import javax.naming.InitialContext;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.utils.XRuntime;
import com.addval.metadata.EditorMetaData;

/**
 * A concrete implementaion of com.addval.metadata.UIMetaData that can be used at
 * the client side as a cache for all EditorMetaData objects
 * @author AddVal Technology Inc.
 */
public class EJBUIMetaData extends UIMetaData {
	private static final String _module = "EJBUIMetaData";
	private static final String _SERVER = AVConstants._METADATA_SERVER;

	/**
	 * @roseuid 3B72D12C024B
	 */
	public EJBUIMetaData() {

		super();
	}

	/**
	 * @return com.addval.ejbutils.metadata.EJBUIMetaData
	 * @roseuid 3AF0A64D0282
	 */
	public static synchronized EJBUIMetaData getInstance() {

		if (_instance == null)
			_instance = new EJBUIMetaData();

		return (EJBUIMetaData)_instance;
	}

	/**
	 * @param name
	 * @param type
	 * @return com.addval.metadata.EditorMetaData
	 * @roseuid 3B72E91F0106
	 */
	protected EditorMetaData createEditorMetaData(String name, String type) {

		try {

			EJBSEditorMetaDataHome	 home   = (EJBSEditorMetaDataHome)PortableRemoteObject.narrow( new InitialContext().lookup( AVConstants._ENC_PREFIX + AVConstants._METADATA_SERVER ), EJBSEditorMetaDataHome.class );
			EJBSEditorMetaDataRemote remote = home.create();
			EditorMetaData			 editor = remote.lookup( name, type );

			// Set the editor to the cache
			setEditorMetaData( editor );

			return editor;
		}
		catch (Exception e) {

			throw new XRuntime( _module, e.getMessage() );
		}
	}
}
