package com.addval.springutils;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationContext;
import com.addval.ejbutils.utils.TableManagerInterceptor;

import java.io.Serializable;

public class SpringAwareInterceptorLoader implements ApplicationListener
{
   private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(SpringAwareInterceptorLoader.class);

   Map     _interceptorMap = null;

   public SpringAwareInterceptorLoader() {

   }

   public Map getInterceptorMap() {
	   return _interceptorMap;
   }

   public void setInterceptorMap(Map aMap) {
	   _interceptorMap = aMap;
   }

   public void onApplicationEvent(ApplicationEvent event) {

	   if (event instanceof ContextRefreshedEvent) {

			ApplicationContext ctx = ((ContextRefreshedEvent) event).getApplicationContext();

			// get all the Interceptors
			Map map = ctx.getBeansOfType(com.addval.ejbutils.utils.TableManagerInterceptor.class);

			_interceptorMap.putAll(map);
	   }
   }

}