package com.addval.utils.trees;

public class AFLSCacheException extends com.addval.utils.XRuntime {	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param source
	 * @param desc
	 */
	public AFLSCacheException(String source, String desc) {
		super(source, desc);		
	}	

	/**
	 * @param desc
	 */
	public AFLSCacheException(String desc) {
		super(desc);		
	}

}
