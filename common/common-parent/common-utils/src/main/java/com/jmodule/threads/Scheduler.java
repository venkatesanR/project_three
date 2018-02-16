package com.jmodule.threads;

public class Scheduler extends AbstractSchedulerService {
	private static int DEFAULT_THREAD_COUNT = 1;
	private static int MAX_THREAD_COUNT = 1;
	private Runnable DEAMON_THREAD = null;

	public Scheduler() {
		super(DEFAULT_THREAD_COUNT, MAX_THREAD_COUNT);
		DEAMON_THREAD = new Runnable() {
			public void run() {
				while (true) {
					schedule();
				}
			}
		};
		new Thread(DEAMON_THREAD).start();
	}

	public Scheduler(final int threadCount, int maxThread) {
		super(threadCount, maxThread);
		schedule();
	}

	public Scheduler(final int threadCount, int maxThread, long timeout) {
		super(threadCount, maxThread, timeout);
		schedule();
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		scheduler.addTask(new SampleTask());
		while(true) {
		}
	}
}
