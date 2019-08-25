package com.techmania.designprinciples.assignments.scheduler;

import com.techmania.designprinciples.models.ServiceMarker;

@ServiceMarker(serviceName = "schedulerService")
public interface IScheduler {
    public SchedulerResponse kill(Long taskId);

    public SchedulerResponse addTask(Task task);

    public void stopScheduler();
}
