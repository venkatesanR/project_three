package com.addval.ejbutils.utils;


/**
 * This class is a Singleton factory that creates an instance of the class
 * implementing the TableManagerInterceptor interface
 */
public class TableManagerInterceptorFactory
{

	/**
	 * @param interceptorClass
	 * @return com.addval.ejbutils.utils.TableManagerInterceptor
	 */
	public static TableManagerInterceptor getInstance(String interceptorClass)
	{
		// Construct a new instance of the specified class
		Class clazz = null;
		try {
			if (clazz == null) {

				Thread t 		= 	Thread.currentThread();
				ClassLoader cl 	=	t.getContextClassLoader();
				clazz			= 	cl.loadClass(interceptorClass);
				// clazz = Class.forName(securityClass);
			}
			TableManagerInterceptor interceptor = (TableManagerInterceptor)clazz.newInstance();
			return (interceptor);
		} catch (Throwable t) {
			System.out.println("TableManagerInterceptorFactory.getInstance");
			t.printStackTrace(System.out);
			return (null);
        }
	}
}
