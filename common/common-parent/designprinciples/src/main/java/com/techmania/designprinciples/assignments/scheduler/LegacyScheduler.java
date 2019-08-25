package com.techmania.designprinciples.assignments.scheduler;

import com.techmania.designprinciples.models.ServiceMarker;

import java.util.concurrent.SynchronousQueue;

@ServiceMarker(serviceName = "legacySchedulerService")
public class LegacyScheduler extends SchedulerManager {
    public LegacyScheduler() {
        super(5);
    }
}
