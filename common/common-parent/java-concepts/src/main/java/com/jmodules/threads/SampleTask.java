package com.jmodules.threads;

import java.util.ArrayList;
import java.util.List;

public class SampleTask implements Task, Comparable<SampleTask> {
	private Integer input;

	public SampleTask(String input) {
		this.input = Integer.valueOf(input);
	}

	@Override
	public List<String> read() {
		System.out.println(input);
		List<String> response = new ArrayList<>();
		response.add("read");
		return response;
	}

	@Override
	public List write(List response) {
		response.add("write");
		return response;
	}

	@Override
	public void process(List response) {
		response.add("process");
		List<String> fReponse = response;
		for (String sq : fReponse) {
			System.out.println(sq);
		}
	}

	@Override
	public int compareTo(SampleTask o) {
		if (this.input > o.input) {
			return -1;
		} else if (this.input < o.input) {
			return 1;
		}
		return 0;
	}
}
