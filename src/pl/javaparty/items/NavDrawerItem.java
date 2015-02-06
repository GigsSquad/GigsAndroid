package pl.javaparty.items;

import java.util.ArrayList;

public class NavDrawerItem {

	private String title;
	private ArrayList<String> submenu; //if null then no submenu
	private int icon;
	private String count;
	private boolean isCounterVisible = false;

	public NavDrawerItem(String title, int icon) {
		this.title = title;
		this.icon = icon;
		submenu = null;
	}

	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count) {
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
		submenu = null;
	}
	
	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count, ArrayList<String> submenu)
	{
		this(title, icon, isCounterVisible, count);
		this.submenu = submenu;
	}
	
	public NavDrawerItem(String title, int icon, ArrayList<String> submenu)
	{
		this(title, icon);
		this.submenu = submenu;
	}

	public String getTitle() {
		return this.title;
	}

	public int getIcon() {
		return this.icon;
	}

	public String getCount() {
		return this.count;
	}

	public boolean getCounterVisibility() {
		return this.isCounterVisible;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public void setCounterVisibility(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}
	/**
	 * @return Submenu ArrayList<String>, or null if no submenu.
	 */
	public ArrayList<String> getSubmenu()
	{
		return submenu;
	}
}