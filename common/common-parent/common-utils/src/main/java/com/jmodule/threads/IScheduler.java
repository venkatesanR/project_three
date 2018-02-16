package com.jmodule.threads;

import com.jmodule.threads.SchedulerResponse;

public interface IScheduler {
	public SchedulerResponse kill(Long taskId);

	public SchedulerResponse pauseTask(Long taskId);

	public SchedulerResponse addTask(Task task);

	public SchedulerResponse removeTask(Long taskId);

	public SchedulerResponse killAll();

	public Task getCurrentTask();
}
