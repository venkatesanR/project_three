package com.techmania.designprinciples.assignments.scheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SchedulerManager implements IScheduler {
    private final Queue<Task> TASK_QUEUE = new LinkedList<>();
    private ScheduledThreadPoolExecutor scheduleExecutor = null;
    private Map<String, SchedulerResponse> TASK_STATUS = new ConcurrentHashMap<>();

    private static final long INIT_DELAY = 5;
    private static final long POLLING_INTERVAL = 5;
    private static final int DEFAULT_POOL_SIZE = 5;
    private static final int CHUNK_POLL_SIZE = 5;
    private volatile boolean canRun = true;

    private final Object LOCK = new Object();

    public SchedulerManager() {
        this(DEFAULT_POOL_SIZE);
    }

    public SchedulerManager(Integer poolSize) {
        if (scheduleExecutor == null) {
            scheduleExecutor = new ScheduledThreadPoolExecutor(poolSize != null ? poolSize :
                    DEFAULT_POOL_SIZE);
        }
        scheduleExecutor.scheduleAtFixedRate(pollerDaemon(), INIT_DELAY, POLLING_INTERVAL, TimeUnit.SECONDS);
    }

    public SchedulerResponse getStatus(final String taskId) {
        return TASK_STATUS.get(taskId);
    }

    @Override
    public SchedulerResponse kill(Long taskId) {
        return TASK_STATUS.get(taskId);
    }

    @Override
    public SchedulerResponse addTask(final Task task) {
        synchronized (LOCK) {
            task.setTaskId(generateTaskId());
            TASK_QUEUE.add(task);
            return acknowledge(task.getTaskId());
        }
    }

    @Override
    public void stopScheduler() {
        canRun = false;
    }

    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    private SchedulerResponse acknowledge(String taskId) {
        SchedulerResponse schedulerResponse = new SchedulerResponse(taskId);
        schedulerResponse.setStatus(TaskStatusEnum.SCHEDULED.getMessage());
        TASK_STATUS.put(taskId, schedulerResponse);
        return schedulerResponse;
    }

    public Runnable pollerDaemon() {
        return () -> {
            if (!canRun) {
                return;
            }
            int pollCounter = 0;
            List<Task> tasks = new ArrayList<>();
            while (pollCounter < CHUNK_POLL_SIZE && !TASK_QUEUE.isEmpty()) {
                tasks.add(TASK_QUEUE.poll());
                pollCounter += 1;
            }
            if (tasks.isEmpty()) {
                System.out.println("Nothing to execute");
            }
            execute(tasks);
        };
    }

    public void execute(List<Task> tasks) {
        tasks.stream().forEach(task -> {
            CompletableFuture.runAsync(() -> {
                try {
                    String response = task.executeTask();
                    TASK_STATUS.get(task.getTaskId()).setStatus(TaskStatusEnum.COMPLETED.name());
                    TASK_STATUS.get(task.getTaskId()).setMessage(response);
                    System.out.println(TASK_STATUS.get(task.getTaskId()));
                } catch (Exception ex) {
                    System.out.println("Fatal Exception occured");
                    TASK_STATUS.get(task.getTaskId()).setStatus(TaskStatusEnum.FAILED.name());
                }
            });
        });
    }
}
