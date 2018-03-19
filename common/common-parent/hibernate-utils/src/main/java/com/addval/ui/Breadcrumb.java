package com.addval.ui;

import java.io.Serializable;

public class Breadcrumb implements Serializable {
	private static final long serialVersionUID = 6613226240595910966L;
	private String url;
	private String title;
	private String menuIndex;

	public Breadcrumb(String url, String title) {
		this.url = url;
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMenuIndex() {
		return menuIndex;
	}

	public void setMenuIndex(String menuIndex) {
		this.menuIndex = menuIndex;
	}
	
	
}
