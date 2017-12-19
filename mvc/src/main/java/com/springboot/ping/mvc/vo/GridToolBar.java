package com.springboot.ping.mvc.vo;

public class GridToolBar {
	/**
	 * url链接
	 */
	private String url;
	/**
	 * 按钮名称
	 */
	private String name;
	/**
	 * 按钮前的图标
	 */
	private String icon;
	/**
	 * 按钮点击的方式，默认是openDialogRow,即选中一行打开一个dialog,值域范围请参考注解Open
	 */
	private String open;
	public GridToolBar(){}
	/**
	 * @see Open
	 * @param url url链接
	 * @param name 按钮名称
	 * @param icon 按钮前的图标
	 * @param open 按钮点击的方式，默认是openDialogRow,即选中一行打开一个dialog,值域范围请参考常量@see Open
	 */
	public GridToolBar(String url, String name, String icon, String open) {
		this.url = url;
		this.name = name;
		this.icon = icon;
		this.open = open;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	
}
