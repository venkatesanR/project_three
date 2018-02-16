package com.jmodule.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class AbstractSchedulerService implements IScheduler {
	private final Queue<Task> TASK_QUEUE = new LinkedList<>();
	private int threadCount;
	private int maxThread;
	private long timeout = -1;// wait for a while

	public AbstractSchedulerService(final int threadCount, int maxThread) {
		this.threadCount = threadCount;
		this.maxThread = maxThread;
	}

	public AbstractSchedulerService(final int threadCount, int maxThread, long timeout) {
		this.threadCount = threadCount;
		this.maxThread = maxThread;
		this.timeout = timeout;
	}

	public final void schedule() {
		while (!TASK_QUEUE.isEmpty()) {
			Runnable service = new Runnable() {
				public void run() {
					try {
						synchronized (TASK_QUEUE) {
							onExecute(TASK_QUEUE.poll());	
						}
					} catch (ProcessException pex) {
						System.out.println(pex.getMessage());
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			};
			new Thread(service).start();
		}
	}

	@Override
	public final SchedulerResponse kill(final Long taskId) {
		return null;
	}

	@Override
	public final SchedulerResponse pauseTask(final Long taskId) {
		return null;
	}

	@Override
	public final SchedulerResponse addTask(final Task task) {
		synchronized (task) {
			TASK_QUEUE.add(task);
		}
		return new SchedulerResponse(JobStatusEnum.SCHEDULED.getMessage());
	}

	@Override
	public final SchedulerResponse removeTask(final Long taskId) {
		return null;
	}

	@Override
	public final SchedulerResponse killAll() {
		return null;
	}

	@Override
	public final Task getCurrentTask() {
		return null;
	}

	private <T> void onExecute(Task task) {
		List<T> fromRead;
		List<T> processed;
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
