package com.jmodule.threads;

import com.jmodule.threads.SchedulerResponse;

public interface IScheduler {
	public SchedulerResponse kill(Long taskId);

	public SchedulerResponse addTask(Task task);

	public void killAll();
}
