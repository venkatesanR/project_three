package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DynamicMenuItem implements Serializable {
	private static final long serialVersionUID = 170822830852142957L;

	private String appType = null;
	private String menuId = null;
	private String parentMenuId = null;
	private Integer menuLevel = null;
	private Integer menuSeq = null;
	private String menuLabel = null;
	private String menuAction = null;
	private String menuPrivilege = null;
	private String quickmenuParams = null;
	private List<String> quickmenuIds = null;
	private String quickmenuJSFn = null;
	private List<DynamicMenuItem> subMenus = null;

	public DynamicMenuItem(String appType, String menuId, String parentMenuId, Integer menuLevel, Integer menuSeq, String menuLabel, String menuAction, String menuPrivilege) {
		this.appType = appType;
		this.menuId = menuId;
		this.parentMenuId = parentMenuId;
		this.menuLevel = menuLevel;
		this.menuSeq = menuSeq;
		this.menuLabel = menuLabel;
		this.menuAction = menuAction;
		this.menuPrivilege = menuPrivilege;
		subMenus = new ArrayList<DynamicMenuItem>();
	}

	public String getAppType() {
		return appType;
	}

	public String getMenuId() {
		return menuId;
	}

	public String getParentMenuId() {
		return parentMenuId;
	}

	public Integer getMenuLevel() {
		return menuLevel;
	}

	public Integer getMenuSeq() {
		return menuSeq;
	}

	public String getMenuLabel() {
		return menuLabel;
	}

	public String getMenuAction() {
		return menuAction;
	}

	public String getMenuPrivilege() {
		return menuPrivilege;
	}
	
	public String getQuickmenuParams() {
		return quickmenuParams;
	}

	public void setQuickmenuParams(String quickmenuParams) {
		this.quickmenuParams = quickmenuParams;
	}

	public List<String> getQuickmenuIds() {
		return quickmenuIds;
	}

	public void setQuickmenuIds(List<String> quickmenuIds) {
		this.quickmenuIds = quickmenuIds;
	}

	public String getQuickmenuJSFn() {
		return quickmenuJSFn;
	}

	public void setQuickmenuJSFn(String quickmenuJSFn) {
		this.quickmenuJSFn = quickmenuJSFn;
	}

	public List<DynamicMenuItem> getSubMenus() {
		return subMenus;
	}

	public boolean hasSubMenus(){
		return (getSubMenus() != null && getSubMenus().size() > 0);
	}

	public void addDynamicMenuItem(DynamicMenuItem menuItem) {
		if (menuItem != null) {
			getSubMenus().add(menuItem);
		}
	}
}
