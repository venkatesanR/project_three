package com.techmania.hibernateutils;

import java.util.List;

public interface TransactionPhases {
	public <Q, T> List<T> getList(Q queryParam, Class clazz);

	public <Q, T> T get(Q queryParam, Class clazz);

	public <T> void save(T input);

	public <T> boolean update(T input);
}
