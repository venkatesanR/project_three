package com.jmodule.threads;

import java.util.ArrayList;
import java.util.List;

public class SampleTask implements Task {
	private Integer input;

	@Override
	public List<String> read() {
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

}
