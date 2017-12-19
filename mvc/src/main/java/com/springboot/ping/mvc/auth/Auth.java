package com.springboot.ping.mvc.auth;

/**
 * 权限类
 * <br>创建者： 刘江平
 * 创建时间：2015年10月23日下午3:18:39
 */
public class Auth {
	/**权限ID*/
	private String id;
	/**权限父ID*/
	private String pid;
	/**权限名称*/
	private String name;
	/**权限url*/
	private String url;
	/**前缀图标*/
	private String icon;
	/**权限类型,比如菜单、按钮等*/
	private AuthType type;
	/**父节点的权限类型,比如菜单、按钮等*/
	private AuthType ptype;
	public Auth(){}
	/**
	 * 此构造器默认父节点的权限类型与自己的权限类型一致
	 * @param id 权限ID
	 * @param pid 权限父ID
	 * @param name 权限名称
	 * @param url 权限url
	 * @param icon 前缀图标
	 * @param type 权限类型,比如菜单、按钮等
	 */
	public Auth(String id, String pid, String name, String url, String icon,AuthType type) {
		this.id = id;
		this.pid = pid;
		this.name = name;
		this.url = url;
		this.icon = icon;
		this.type = type;
		this.ptype = type;
	}
	/**
	 * 
	 * @param id 权限ID
	 * @param pid 权限父ID
	 * @param name 权限名称
	 * @param url 权限url
	 * @param icon 前缀图标
	 * @param type 权限类型,比如菜单、按钮等
	 * @param ptype 父节点权限类型,比如菜单、按钮等
	 */
	public Auth(String id, String pid, String name, String url, String icon,AuthType type,AuthType ptype) {
		this.id = id;
		this.pid = pid;
		this.name = name;
		this.url = url;
		this.icon = icon;
		this.type = type;
		this.ptype = ptype;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public AuthType getType() {
		return type;
	}
	public void setType(AuthType type) {
		this.type = type;
	}
	public AuthType getPtype() {
		return ptype;
	}
	public void setPtype(AuthType ptype) {
		this.ptype = ptype;
	}
	
}
