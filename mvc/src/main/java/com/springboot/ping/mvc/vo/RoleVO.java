package com.springboot.ping.mvc.vo;

public class RoleVO {
	private String roleid;
	private String name;
	private String checked;
	public RoleVO(){}
	public RoleVO(String roleid, String name, String checked) {
		super();
		this.roleid = roleid;
		this.name = name;
		this.checked = checked;
	}
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
}
