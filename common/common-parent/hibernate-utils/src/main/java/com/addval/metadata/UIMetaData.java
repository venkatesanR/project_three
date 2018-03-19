//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\metadata\\UIMetaData.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.metadata;

import java.util.Hashtable;

public abstract class UIMetaData {
	protected static UIMetaData _instance = null;
	protected Hashtable _editorsMetaData = null;

	/**
	 * @roseuid 3B72D10B0257
	 */
	public UIMetaData() {

		_editorsMetaData = new Hashtable();
	}

	/**
	 * @param name
	 * @return EditorMetaData
	 * @roseuid 3AF99C540316
	 */
	public EditorMetaData getEditorMetaData(String name) {

		return getEditorMetaData( name, null );
	}

	/**
	 * @param name
	 * @param type
	 * @return EditorMetaData
	 * @roseuid 3AE62A2102ED
	 */
	public EditorMetaData getEditorMetaData(String name, String type) {

		EditorMetaData editorMetaData = (EditorMetaData)_editorsMetaData.get( name + type );

		synchronized (this) {

			if (editorMetaData == null)
				editorMetaData = createEditorMetaData( name, type );
		}

		return editorMetaData;
	}

	/**
	 * @param name
	 * @param type
	 * @return EditorMetaData
	 * @roseuid 3AE62A2C022B
	 */
	protected abstract EditorMetaData createEditorMetaData(String name, String type);

	/**
	 * @param editorMetaData
	 * @roseuid 3AEDB8240078
	 */
	protected void setEditorMetaData(EditorMetaData editorMetaData) {

		_editorsMetaData.put( editorMetaData.getName() + ( editorMetaData.getType() == null ? "" : editorMetaData.getType() ), editorMetaData );
	}
}
