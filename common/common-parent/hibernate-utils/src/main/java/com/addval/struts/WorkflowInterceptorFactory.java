//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\WorkflowInterceptorFactory.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.struts;


public class WorkflowInterceptorFactory
{

	/**
	 * @param interceptorClass
	 * @return com.addval.struts.WorkflowInterceptor
	 * @roseuid 3FCD14D70194
	 */
	static WorkflowInterceptor getInstance(String interceptorClass)
	{
		// Construct a new instance of the specified class
		Class clazz = null;

		try {

			if (clazz == null) {

				Thread t 		= 	Thread.currentThread();
				ClassLoader cl 	=	t.getContextClassLoader();
				clazz			= 	cl.loadClass(interceptorClass);

			}

			WorkflowInterceptor interceptor = (WorkflowInterceptor)clazz.newInstance();
			return (interceptor);

		}
		catch (Throwable t) {

			System.out.println("WorkflowInterceptorFactory.getInstance()");
			t.printStackTrace(System.out);
			return (null);

        }
	}
}
