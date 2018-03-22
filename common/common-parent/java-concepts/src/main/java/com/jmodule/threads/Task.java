package com.jmodule.threads;

import java.util.List;

public interface Task<T> {
	public List<T> read();

	public List<T> write(List<T> input);

	public void process(List<T> input);
}
