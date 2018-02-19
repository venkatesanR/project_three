package com.jmodule.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SchedulerService implements IScheduler {
	private static final int DEFAULT_THREAD_COUNT = 1;
	private static final int MAX_THREAD_COUNT = 1;
	private Runnable DEAMON_THREAD = null;
	private final Queue<Task> TASK_QUEUE = new LinkedList<>();
	private int threadCount;
	private int maxThread;
	private long timeout = -1;// wait for a while

	public SchedulerService() {
		this.threadCount = DEFAULT_THREAD_COUNT;
		this.maxThread = MAX_THREAD_COUNT;
		start();
	}

	public SchedulerService(final int threadCount, int maxThread) {
		this.threadCount = threadCount;
		this.maxThread = maxThread;
	}

	public SchedulerService(final int threadCount, int maxThread, long timeout) {
		this.threadCount = threadCount;
		this.maxThread = maxThread;
		this.timeout = timeout;
	}

	private void start() {
		DEAMON_THREAD = new Runnable() {
			public void run() {
				try {
					synchronized (TASK_QUEUE) {
						Task taskE = null;
						while (!TASK_QUEUE.isEmpty()) {
							taskE = TASK_QUEUE.poll();
							if (taskE != null)
								onExecute(taskE);
						}
						while (TASK_QUEUE.isEmpty()) {
							try {
								wait();
							} catch (InterruptedException e) {
								System.out.println("Some other Thread trying to interupt");
							}
						}
					}
				} catch (ProcessException pex) {
					pex.printStackTrace();
					
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		};
		new Thread(DEAMON_THREAD).start();
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
		synchronized (TASK_QUEUE) {
			TASK_QUEUE.add(task);
			notifyAll();
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
		synchronized (task) {
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
}
