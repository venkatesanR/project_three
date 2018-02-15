package com.jmodule.threads;

public class SampleThread extends Thread {
	private String name;

	public SampleThread(final String name) {
		this.name = name;
	}

	@Override
	public void run() {
		ThreadDetail details = new ThreadDetail();
		details.printThreadDetails(name);
	}
}
