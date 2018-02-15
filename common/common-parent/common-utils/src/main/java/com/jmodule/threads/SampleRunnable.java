package com.jmodule.threads;

public class SampleRunnable implements Runnable {

	private String name;

	public SampleRunnable(final String name) {
		this.name = name;
	}

	@Override
	public void run() {
		ThreadDetail details = new ThreadDetail();
		details.printThreadDetails(name);
	}

}
