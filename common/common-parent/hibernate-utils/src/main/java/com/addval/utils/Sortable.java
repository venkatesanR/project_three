// Sortable.java

package com.addval.utils;

public interface Sortable
{
	public	void	setSortBy( String sortByMethodName );
	public	String	getSortBy();
	public	void	setSortOrder( String order );
	public	String	getSortOrder();
	public	void	performSort( java.util.Vector vectorTobeSorted ) throws Exception;
}
