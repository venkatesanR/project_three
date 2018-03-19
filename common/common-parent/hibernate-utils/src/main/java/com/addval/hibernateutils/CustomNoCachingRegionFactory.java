package com.addval.hibernateutils;

import java.util.Properties;

import org.hibernate.cache.impl.NoCachingRegionFactory;

public class CustomNoCachingRegionFactory extends NoCachingRegionFactory {
	public CustomNoCachingRegionFactory(Properties properties) {
		super(properties);
	}
	
	public long nextTimestamp() {
		//org.hibernate.impl.SessionFactoryImpl.openSession(Interceptor sessionLocalInterceptor)
		//long timestamp = settings.getRegionFactory().nextTimestamp();
		//<prop key="hibernate.cache.region.factory_class">com.addval.hibernateutils.CustomNoCachingRegionFactory</prop>
		//return System.currentTimeMillis() / 100;
		return System.currentTimeMillis();
	}	
}
