package com.springboot.ping.mvc.meta;

import com.springboot.ping.mvc.annotation.GridField;
import com.springboot.ping.mybatis.meta.PropertyInfo;


/**
 * 实体的一个属性元信息
 * 创建者： 刘江平
 * 创建时间：2015年7月6日下午2:18:49
 */
public class GridPropertyInfo extends PropertyInfo{
	/**
	 * list页面或者是excel的字段信息
	 */
	private GridField gridField;
	/**
	 * 默认的查询条件值
	 */
	private String defaultSearchValue="";
	/**
	 * 如果是between的查询，第二个条件值
	 */
	private String defaultSearchValue2="";
	/**
	 * list页面或者是excel的字段信息
	 * @return
	 */
	public GridField getGridField() {
		return gridField;
	}
	/**
	 * list页面或者是excel的字段信息
	 * @param gridField
	 */
	public void setGridField(GridField gridField) {
		this.gridField = gridField;
	}
	/**
	 * 默认的查询条件值
	 * @return
	 */
	public String getDefaultSearchValue() {
		return defaultSearchValue;
	}
	/**
	 * 默认的查询条件值
	 * @param defaultSearchValue
	 */
	public void setDefaultSearchValue(String defaultSearchValue) {
		this.defaultSearchValue = defaultSearchValue;
	}
	public String getDefaultSearchValue2() {
		return defaultSearchValue2;
	}
	public void setDefaultSearchValue2(String defaultSearchValue2) {
		this.defaultSearchValue2 = defaultSearchValue2;
	}
}
