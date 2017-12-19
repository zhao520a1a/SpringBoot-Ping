package com.springboot.ping.mvc.vo;

import com.springboot.ping.mvc.enums.InputType;


/**
 * datagrid查询条件辅助类
 * <br>search:选填,是否要设置成查询条件,默认为false不设置成查询条件
 * <br>inputType:选填,设置成查询条件后，查询条件的输入框类型，默认为text文本框
 * <br>between:选填,设置成查询条件后,是否设置成一个查询区间的输入,默认false为不按区间查询
 * <br>创建者： 刘江平
 * 创建时间：2015年7月17日下午5:23:28
 */
public class Search {
	/**
	 * 是否要设置成查询条件,默认为false不设置成查询条件
	 * 
	 */
	private boolean search = false;
	/**
	 * 设置成查询条件后，查询条件的输入框类型，默认为text文本框
	 * 
	 */
	private String inputType = InputType.TEXT.toString();
	/**
	 * 设置成查询条件后,是否设置成一个查询区间的输入,默认false为不按区间查询
	 */
	private boolean between = false;
	/**
	 * 下拉款是否单选，默认为true单选,false代表多选,此属性只针对下拉款查询有效
	 */
	private boolean singleSelect = true;
	/**
	 * 此查询条件的显示名称,默认与字段中文名称一样
	 */
	private String searchName = "";
	public Search(){}
	
	public Search(boolean search, InputType inputType, boolean between,boolean singleSelect,String searchName) {
		this.search = search;
		this.inputType = inputType.toString();
		this.between = between;
		this.singleSelect = singleSelect;
		this.searchName = searchName;
	}
	
	public Search(boolean search, InputType inputType) {
		super();
		this.search = search;
		this.inputType = inputType.toString();
	}

	public Search(boolean search) {
		super();
		this.search = search;
	}

	public boolean isSearch() {
		return search;
	}
	public void setSearch(boolean search) {
		this.search = search;
	}
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public boolean isBetween() {
		return between;
	}
	public void setBetween(boolean between) {
		this.between = between;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public boolean isSingleSelect() {
		return singleSelect;
	}

	public void setSingleSelect(boolean singleSelect) {
		this.singleSelect = singleSelect;
	}
}
