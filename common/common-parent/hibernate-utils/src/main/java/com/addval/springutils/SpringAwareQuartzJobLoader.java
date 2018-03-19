package com.addval.springutils;

import com.addval.utils.CacheMgr;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;
import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.BeanFactoryUtils;

public class SpringAwareQuartzJobLoader implements ApplicationListener
{
	static transient Logger _logger = com.addval.utils.LogMgr.getLogger(SpringAwareQuartzJobLoader.class);

	private AVQuartzPropertiesConfig _config;
	private org.quartz.Scheduler _scheduler;

	public AVQuartzPropertiesConfig getConfig() {
		return _config;
	}

	public void setConfig(AVQuartzPropertiesConfig config) {
		_config = config;
	}

	public Scheduler getScheduler() {
		return _scheduler;
	}

	public void setScheduler(Scheduler bean) {
		_scheduler = bean;
	}

    public void onApplicationEvent(ApplicationEvent event) {

	   if (event instanceof ContextRefreshedEvent) {
			ApplicationContext ctx = ((ContextRefreshedEvent) event).getApplicationContext();

			_logger.debug(" Context Refreshed Event is triggered in SpringAwareQuartzJobLoader");

			//logBeanNames(ctx);

			// get all the cronTriggerBeans
			Map mapcron = ctx.getBeansOfType(org.springframework.scheduling.quartz.CronTriggerBean.class);

			// get all the simpleTriggerBeans
			Map mapsimple = ctx.getBeansOfType(org.springframework.scheduling.quartz.SimpleTriggerBean.class);

			Object[] colcron = null;
			Object[] colsimple = null;


			colcron 	= (Object[]) mapcron.values().toArray();;
			colsimple 	= (Object[]) mapsimple.values().toArray();

			for (int i=0; i<colcron.length; ++i) {
				CronTriggerBean bean = (CronTriggerBean) colcron[i];
				try {
					_logger.debug("Cron Trigger Found");
					if (bean.getJobDetail().getJobDataMap().get("execute_in_serverType") != null) {
						String str = (String) bean.getJobDetail().getJobDataMap().get("execute_in_serverType");
						if (str.indexOf(getConfig().getServerType()) != -1) {
							getScheduler().addJob(bean.getJobDetail(), true);
							getScheduler().scheduleJob(bean);
						}
					} else {
						getScheduler().addJob(bean.getJobDetail(), true);
						getScheduler().scheduleJob(bean);
					}
				} catch (Exception e) {
					_logger.error(e);
				}
			}

			for (int i=0; i<colsimple.length; ++i) {
				try {
					SimpleTriggerBean bean = (SimpleTriggerBean) colsimple[i];
					_logger.debug("Simple Trigger Found");
					if (bean.getJobDetail().getJobDataMap().get("execute_in_serverType") != null) {
						String str = (String) bean.getJobDetail().getJobDataMap().get("execute_in_serverType");
						if (str.indexOf(getConfig().getServerType()) != -1) {
							getScheduler().addJob(bean.getJobDetail(), true);
							getScheduler().scheduleJob(bean);
						}
					} else {
						getScheduler().addJob(bean.getJobDetail(), true);
						getScheduler().scheduleJob(bean);
					}
				} catch (Exception e) {
					_logger.error(e);
				}
			}


	   }

  }

   public void logBeanNames(ApplicationContext ctx) {

		_logger.debug("Application Context " + ctx.getDisplayName() + " Bean Count: " + ctx.getBeanDefinitionCount());


		String names[] = ctx.getBeanDefinitionNames();

		for (int i=0;i<names.length;++i) {
			_logger.debug("" + i + " ---> " + names[i]);
		}

		_logger.debug("------------------: " + ctx.getBeanDefinitionCount());

   }


}