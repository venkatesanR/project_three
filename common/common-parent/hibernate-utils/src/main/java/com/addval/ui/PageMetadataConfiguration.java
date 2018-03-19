package com.addval.ui;

import java.util.ArrayList;
import java.util.Iterator; 
import java.util.Hashtable;
import java.util.List;

import com.addval.dbutils.DAOSQLStatement;

public class PageMetadataConfiguration {
	private Hashtable _pageDefinitions = null;
	
	public PageMetadataConfiguration() {
		
	}
	
	/**
	 * @param sqlStatement
	 * @roseuid 3EA06B1F03C2
	 */
	public void addPageMetadata(PageMetadata page) {

		if (_pageDefinitions == null)
			_pageDefinitions = new Hashtable();

		_pageDefinitions.put( page.getName(), page );
	}


	public Hashtable getPageDefinitions() {
		return _pageDefinitions;
	}


	public void setPageDefinitions(Hashtable pageDefinitions) {
		_pageDefinitions = pageDefinitions;
	}

	
	public List<UIComponentMetadata> getComponentMetadata() {
		List<UIComponentMetadata> compMetadata = new ArrayList<UIComponentMetadata>();
		UIComponentMetadata uiCmd = null;

		if (getPageDefinitions() != null) {

			Iterator 		iterator = getPageDefinitions().entrySet().iterator();
			DAOSQLStatement sql 	 = null;

			while (iterator.hasNext()) {

				PageMetadata pg = (PageMetadata) iterator.next();
				
				compMetadata.addAll(pg.getComponentMetadata());				
			}
		}
		
		return compMetadata;		
	}
}
