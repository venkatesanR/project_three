package com.jmodule.threads;

/**
 * <p>
 * As a brief not this is the starting point of All thread based implementation
 * 
 * </p>
 * 
 * @author vrengasamy
 *
 */
public class DriverClass {
	public static void main(String[] args) throws InterruptedException {
		SchedulerService scheduler = new SchedulerService();
		scheduler.addTask(new SampleTask("0"));
		int i=10;
		while(i > 0 ) {
			scheduler.addTask(new SampleTask(String.valueOf(i)));
			Thread.sleep(1000);
			i-=1;
		}
		scheduler.killAll();
	}
}
