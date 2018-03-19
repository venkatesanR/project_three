package com.addval.springutils;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.addval.ui.SetupPageMetadata;

public class SpringAwarePageMetadataLoader implements ApplicationListener {

	public void onApplicationEvent(ApplicationEvent event) {
		if (!(event instanceof ContextRefreshedEvent)) {
			return;
		}
		System.out.println("SpringAwarePageMetadataLoader:onApplicationEvent");
		ApplicationContext ctx = ((ContextRefreshedEvent) event).getApplicationContext();
		Map map = ctx.getBeansOfType(SetupPageMetadata.class);

		Object[] values = map.values().toArray();
		for (int i = 0; i < values.length; ++i) {
			SetupPageMetadata loader = (SetupPageMetadata) values[i];
			loader.loadPageMetadata();
		}
	}

}
