package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuItem implements Serializable {
	private static final long serialVersionUID = -8416509509252956709L;

	private String pageName = null;
	private String url = null;
	private String menuId = null;
	private int depth;

	private List<MenuItem> subMenus = null;

	public MenuItem(String pageName, String url) {
		this.pageName = pageName;
		this.url = url;
	}

	public String getPageName() {
		return pageName;
	}

	public String getUrl() {
		return url;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getJSOnclick(){
		return (getSubMenus().size() > 0)? "" : "showSubMenu('"+ getMenuId() +"')";
	}

	public void addMenuItem(MenuItem subMenu) {
		if (subMenu != null) {
			getSubMenus().add(subMenu);
		}
	}

	public List<MenuItem> getSubMenus() {
		if (subMenus == null) {
			subMenus = new ArrayList<MenuItem>();
		}
		return subMenus;
	}

	public void setSubMenus(List<MenuItem> subMenus) {
		this.subMenus = subMenus;
	}

	public static void setMenuItemProperties(List<MenuItem> menuItems, String menuId) {
		int i = 0;
		for (MenuItem menuItem : menuItems) {
			setMenuItemProperties(menuId, menuItem, 0, Integer.toString(i));
			i++;
		}
	}

	private static void setMenuItemProperties(String menuId, MenuItem menuItem,int depth,String menuItemId) {
		menuItem.setDepth(depth);
		menuItem.setMenuId(menuId + "_" + menuItemId);
		int i = 0;
		for (MenuItem subMenu: menuItem.getSubMenus()) {
			setMenuItemProperties(menuId, subMenu,depth + 1,menuItemId + "-" + i);
			i++;
		}
	}

	public static MenuItem findParentMenuItem(List<MenuItem> menus,MenuItem parentMenu, String url) {
		if(url == null){
			return null;
		}
		MenuItem menuItemFound = null;
		for (MenuItem menuItem : menus) {
			if (menuItem.getUrl() != null && menuItem.getUrl().startsWith( url )) {
				menuItemFound = parentMenu;
				break;
			}
			else {
				menuItemFound = findParentMenuItem(menuItem.getSubMenus(),menuItem, url);
				if(menuItemFound != null){
					break;
				}
			}
		}
		return menuItemFound;
	}

	
}
