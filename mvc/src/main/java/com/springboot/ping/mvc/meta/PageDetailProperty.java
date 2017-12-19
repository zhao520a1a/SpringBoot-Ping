package com.springboot.ping.mvc.meta;

/**
 * 明细页面属性对象
 * <br>propertyName:属性英文字段名称(java类的属性字段)
 * <br>propertyDesc:属性中文描述
 * <br>value:属性的值
 * @author 刘江平 2016年12月17日上午11:24:50
 *
 */
public class PageDetailProperty {
	/**
	 * 属性英文字段名称
	 */
	private String propertyName;
	/**
	 * 属性中文描述
	 */
	private String propertyDesc;
	/**
	 * 属性的值
	 */
	private String value;
	
	/**
	 * 
	 * @param propertyName 属性英文字段名称
	 * @param propertyDesc 属性中文描述
	 * @param value 属性的值
	 */
	public PageDetailProperty(String propertyName, String propertyDesc,
			String value) {
		this.propertyName = propertyName;
		this.propertyDesc = propertyDesc;
		this.value = value;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public String getPropertyDesc() {
		return propertyDesc;
	}
	public String getValue() {
		return value;
	}
	
	
}
