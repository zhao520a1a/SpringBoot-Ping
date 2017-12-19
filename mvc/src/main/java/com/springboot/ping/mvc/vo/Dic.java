package com.springboot.ping.mvc.vo;

/**
 * 字典对象
 * <br>创建者： 刘江平
 * 创建时间：2016年4月25日下午7:30:56
 */
public class Dic {
	/**字典组代码*/
	private String group;
	/**字典代码*/
	private String dicCode;
	/**字典值*/
	private String dicValue;
	public Dic(){}
	public Dic(String group){
		this.group = group;
	}
	/**
	 * 
	 * @param group 字典组代码
	 * @param diccode 字典代码
	 * @param dicValue 字典值
	 */
	public Dic(String group, String dicCode, String dicValue) {
		this.group = group;
		this.dicCode = dicCode;
		this.dicValue = dicValue;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getDicCode() {
		return dicCode;
	}
	public void setDicCode(String dicCode) {
		this.dicCode = dicCode;
	}
	public String getDicValue() {
		return dicValue;
	}
	public void setDicValue(String dicValue) {
		this.dicValue = dicValue;
	}
	
}
