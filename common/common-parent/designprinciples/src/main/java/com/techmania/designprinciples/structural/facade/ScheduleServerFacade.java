package com.techmania.designprinciples.structural.facade;

/**
 * Example of Facade Pattern
 * 
 * @author vrengasamy
 *
 */
public class ScheduleServerFacade {
	private final ScheduleServer scheduleServer;

	public ScheduleServerFacade(ScheduleServer scheduleServer) {
		this.scheduleServer = scheduleServer;
	}

	public void startServer() {
		scheduleServer.startBooting();
		scheduleServer.readSystemConfigFile();
		scheduleServer.init();
		scheduleServer.initializeContext();
		scheduleServer.initializeListeners();
		scheduleServer.createSystemObjects();
	}

	public void stopServer() {
		scheduleServer.releaseProcesses();
		scheduleServer.destory();
		scheduleServer.destroySystemObjects();
		scheduleServer.destoryListeners();
		scheduleServer.destoryContext();
		scheduleServer.shutdown();
	}

	public static void main(String[] args) {
		ScheduleServer scheduler = new ScheduleServer();
		ScheduleServerFacade facade = new ScheduleServerFacade(scheduler);
		facade.startServer();
		facade.stopServer();
	}
}
