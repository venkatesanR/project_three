package com.designprinciples.designs;

public interface Scheduler {
	public String schedule(Proposal propsal);

	public String reSchedule(Proposal propsal);

	public String cancel(Proposal propsal, String reason);
}
