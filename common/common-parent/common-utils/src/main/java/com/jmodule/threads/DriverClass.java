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
	public static void main(String[] args) {
		SchedulerService scheduler = new SchedulerService();
		scheduler.addTask(new SampleTask());
	}
}
