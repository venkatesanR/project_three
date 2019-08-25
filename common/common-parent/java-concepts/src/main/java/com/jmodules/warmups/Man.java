package com.jmodules.warmups;

import java.util.Set;

public class Man {
	private Integer id;
	private Set<String> datas;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<String> getDatas() {
		return datas;
	}

	public void setDatas(Set<String> datas) {
		this.datas = datas;
	}

	@Override
	public int hashCode() {
		return 31 * id;
	}

	@Override
	public boolean equals(Object m2) {
		Man m1 = (Man) m2;
		return (m1.id).equals(this.id);
	}
}
