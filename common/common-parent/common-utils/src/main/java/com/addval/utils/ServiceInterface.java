/* Copyright AddVal Technology Inc. */

package com.addval.utils;


/**
 * @author
 * @version
 */
public interface ServiceInterface
{

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FB60D0120
	 */
	public void startUp();

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FB61C01B8
	 */
	public void shutDown();

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FB61C02BC
	 */
	public void activate();

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FB61C03A2
	 */
	public void deactivate();

	/**
	 * @return boolean
	 * @exception
	 * @roseuid 3B2FB62D0389
	 */
	public boolean isActive();
}
