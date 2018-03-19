package com.addval.springutils;

import org.springframework.beans.factory.InitializingBean;
import java.util.Map;
import java.util.Iterator;


/**
 * Configuring log4j from Spring using LogMgr
 *
 * If this bean is configured in applicationContext and the following two properties are set in the applicationContext
 *
 * logs.dir
 * log4j.configFile
 *
 * Then log4j subsystem will be reconfigured with these two variables.
 *
 * This allows for having custome log4j configuration files depending on the environment
 * This allows for using variables such as ${logs.dir} in the log4j configuration file and have it injected through spring
 *
 */
public class SpringLog4jInitializingBean implements InitializingBean
{
        /** Properties to be set */
        protected Map systemProperties;

        public void setSystemProperties(Map systemProperties) {
                this.systemProperties = systemProperties;
        }

        /** Sets the system properties */
        private void initSystemProperties()
               throws Exception {

                if (systemProperties == null ||
                       systemProperties.isEmpty()) {
                        // No properties to initialize
                        return;
                }

                Iterator i = systemProperties.keySet().iterator();
                while (i.hasNext()) {
                        String key = (String) i.next();
                        String value = (String) systemProperties.get(key);

						// set the property only if nothing is configured in commandline
						if (System.getProperty(key) == null) {

							//set the property only if the property is not null
							if (value.trim().length() > 0) {
								if (!value.toLowerCase().equals("default"))
                        			System.setProperty(key, value);
							}
						}
                }
        }


        /** Sets the system properties */
        public void afterPropertiesSet()
               throws Exception {
				initSystemProperties();
//				LogMgr.doConfigure();
		}
}

