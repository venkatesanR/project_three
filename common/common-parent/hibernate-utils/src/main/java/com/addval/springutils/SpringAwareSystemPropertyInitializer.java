package com.addval.springutils;

import org.springframework.beans.factory.InitializingBean;
import java.util.Map;
import java.util.Iterator;

/**
 * Bean for automatically initializing System
 * properties from within a Spring context.
 */
public class SpringAwareSystemPropertyInitializer implements InitializingBean {

        /** Properties to be set */
        private Map systemProperties;

        /** Sets the system properties */
        public void afterPropertiesSet()
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

                        System.setProperty(key, value);
                }
        }

        public void setSystemProperties(Map systemProperties) {
                this.systemProperties = systemProperties;
        }
}
