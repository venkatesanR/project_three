package com.jmodules.threads;

import com.jmodules.threads.SchedulerResponse;

public interface IScheduler {
	public SchedulerResponse kill(Long taskId);

	public SchedulerResponse addTask(Task task);

	public void killAll();
}
