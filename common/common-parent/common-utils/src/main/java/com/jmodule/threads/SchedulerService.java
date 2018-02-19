package com.jmodule.threads;

import java.util.PriorityQueue;

public class SchedulerService implements IScheduler {
	private static final int MAX_THREAD_COUNT = 5;
	private Thread DEAMON_THREAD = null;
	private SchedulerHelper helper;
	private final PriorityQueue<Task> TASK_QUEUE = new PriorityQueue<>();

	public SchedulerService() {
		helper = new SchedulerHelper(MAX_THREAD_COUNT);
		Runnable service = new Runnable() {
			public void run() {
				while (true) {
					try {
						synchronized (TASK_QUEUE) {
							while (TASK_QUEUE.size() == 0)
								TASK_QUEUE.wait();
							helper.execute(TASK_QUEUE.poll());
						}
					} catch (ProcessException pex) {
						pex.printStackTrace();
					} catch (InterruptedException ex) {
						System.out.println(ex);
					} catch (Exception ex) {
						System.out.println(ex);
					}
				}
			}
		};
		DEAMON_THREAD = new Thread(service);
		DEAMON_THREAD.setDaemon(true);
		DEAMON_THREAD.start();
	}

	@Override
	public final SchedulerResponse kill(final Long taskId) {
		return null;
	}

	@Override
	public final SchedulerResponse addTask(final Task task) {
		synchronized (TASK_QUEUE) {
			TASK_QUEUE.add(task);
			TASK_QUEUE.notifyAll();
		}
		return new SchedulerResponse(JobStatusEnum.SCHEDULED.getMessage());
	}

	@Override
	public final void killAll() {
		System.out.println("Forcibly stopping all Tasks");
		Runtime.getRuntime().exit(0);
	}

}
