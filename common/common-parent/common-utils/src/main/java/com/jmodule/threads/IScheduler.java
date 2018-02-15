package com.jmodule.threads;

public interface IScheduler {
	public void init();

	public void schedule();

	public SchedulerResponse kill(Long taskId);

	public SchedulerResponse pauseTask(Long taskId);

	public SchedulerResponse addTask(Task task);

	public SchedulerResponse removeTask(Long taskId);

	public SchedulerResponse killAll();

	public Task getCurrentTask();
}
