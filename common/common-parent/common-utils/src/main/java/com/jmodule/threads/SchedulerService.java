package com.jmodule.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SchedulerService implements IScheduler {
	private static final int MAX_THREAD_COUNT = 5;
	private Runnable DEAMON_THREAD = null;
	private SchedulerHelper helper;
	private final Queue<Task> TASK_QUEUE = new LinkedList<>();
	private boolean killAll;
	public SchedulerService() {
		helper=new SchedulerHelper(MAX_THREAD_COUNT);
		DEAMON_THREAD = new Runnable() {
			public void run() {
				while(!killAll) {
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
			TASK_QUEUE.notifyAll();
		}
		return new SchedulerResponse(JobStatusEnum.SCHEDULED.getMessage());
	}

	@Override
	public final SchedulerResponse removeTask(final Long taskId) {
		return null;
	}

	@Override
	public final SchedulerResponse killAll() {
		Runtime.getRuntime().exit(0);
		return new SchedulerResponse(JobStatusEnum.STOPPED.getMessage());
	}

	@Override
	public final Task getCurrentTask() {
		return null;
	}
}
