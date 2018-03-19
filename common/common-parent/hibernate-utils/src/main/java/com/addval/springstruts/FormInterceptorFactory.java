//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\FormInterceptorFactory.java

package com.addval.springstruts;


/**
 * This class is a Singleton factory that creates an instance of the class
 * implementing the FormInterceptor interface
 */
public class FormInterceptorFactory
{

	/**
	 * @param interceptorClass
	 * @return com.addval.struts.FormInterceptor
	 * @roseuid 3F07B6CA01CF
	 */
	public static FormInterceptor getInstance(String interceptorClass)
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
			FormInterceptor interceptor = (FormInterceptor)clazz.newInstance();
			return (interceptor);
		} catch (Throwable t) {
			System.out.println("FormInterceptorFactory.getInstance");
			t.printStackTrace(System.out);
			return (null);
        }
	}
}
