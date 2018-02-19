package com.jmodule.threads;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SchedulerHelper {
	private int defaultPoolSize;
	private ScheduledThreadPoolExecutor scheduleExecutor = null;

	public SchedulerHelper(int defaultPoolSize) {
		if (scheduleExecutor == null) {
			this.defaultPoolSize = defaultPoolSize;
			scheduleExecutor = new ScheduledThreadPoolExecutor(this.defaultPoolSize);
		}
	}

	public void execute(final Task task) {
		scheduleExecutor.execute(buildRunnable(task));
	}

	private Runnable buildRunnable(Task task) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				synchronized (task) {
					List fromRead;
					List processed;
					try {
						fromRead = task.read();
					} catch (Throwable t) {
						throw new ProcessException("Problem occured while executing read", t);
					}

					try {
						processed = task.write(fromRead);
					} catch (Throwable t) {
						throw new ProcessException("Problem occured while executing write", t);
					}

					try {
						task.process(processed);
					} catch (Throwable t) {
						throw new ProcessException("Problem occured while executing write", t);
					}
				}
			}
		};
		return runnable;
	}

}
