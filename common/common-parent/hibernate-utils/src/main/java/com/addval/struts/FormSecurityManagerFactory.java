//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\FormSecurityManagerFactory.java

package com.addval.struts;


/**
 * This class is a Singleton factory that creates an instance of the class
 * implementing the FormSecurityManager interface
 */
public class FormSecurityManagerFactory {

	/**
	 * @param securityClass
	 * @return com.addval.struts.FormSecurityManager
	 * @roseuid 3E205C430050
	 */
	static FormSecurityManager getInstance(String securityClass) {
		// Construct a new instance of the specified class
		Class clazz = null;
		try {
			if (clazz == null) {

				Thread t 		= 	Thread.currentThread();
				ClassLoader cl 	=	t.getContextClassLoader();
				clazz			= 	cl.loadClass(securityClass);
				// clazz = Class.forName(securityClass);
			}
			FormSecurityManager security = (FormSecurityManager)clazz.newInstance();
			return (security);
		} catch (Throwable t) {
			System.out.println("FormSecurityManagerFactory.getInstance");
			t.printStackTrace(System.out);
			return (null);
        }
	}
}
