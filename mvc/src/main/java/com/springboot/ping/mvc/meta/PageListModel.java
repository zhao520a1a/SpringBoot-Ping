package com.springboot.ping.mvc.meta;

/**
 * list模板页面的参数对象
 * <br>module:实体所在的模块
 * <br>entity:实体类简称
 * <br>pageId:主要用于设定dom里的id唯一值
 * <br>singleSelect:是否单选,true或者false
 * <br>remoteSort:是否支持数据库排序,true或者false
 * <br>multiSort:是否支持多字段排序排序,true或者false
 * <br>allSort:是否所有字段排序都排序,true或者false
 * <br>searchHtml:查询条件的html代码
 * <br>toolbarHtml:操作button的html代码
 * <br>title:table的标题
 * <br>fieldHtml:table的表头的html代码
 * @author 刘江平 2016年12月16日下午1:56:27
 *
 */
public class PageListModel {
	private String module;
	private String entity;
	private String pageId;
	private boolean singleSelect;
	private boolean remoteSort;
	private boolean multiSort;
	private boolean allSort;
	private String searchHtml;
	private String toolbarHtml;
	private String fieldHtml;
	private String title;
	public String getModule() {
		return module;
	}
	void setModule(String module) {
		this.module = module;
	}
	public String getEntity() {
		return entity;
	}
	void setEntity(String entity) {
		this.entity = entity;
	}
	public String getPageId() {
		return pageId;
	}
	void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public boolean isSingleSelect() {
		return singleSelect;
	}
	void setSingleSelect(boolean singleSelect) {
		this.singleSelect = singleSelect;
	}
	public boolean isRemoteSort() {
		return remoteSort;
	}
	void setRemoteSort(boolean remoteSort) {
		this.remoteSort = remoteSort;
	}
	public boolean isMultiSort() {
		return multiSort;
	}
	void setMultiSort(boolean multiSort) {
		this.multiSort = multiSort;
	}
	public boolean isAllSort() {
		return allSort;
	}
	void setAllSort(boolean allSort) {
		this.allSort = allSort;
	}
	public String getSearchHtml() {
		return searchHtml;
	}
	void setSearchHtml(String searchHtml) {
		this.searchHtml = searchHtml;
	}
	public String getToolbarHtml() {
		return toolbarHtml;
	}
	void setToolbarHtml(String toolbarHtml) {
		this.toolbarHtml = toolbarHtml;
	}
	public String getFieldHtml() {
		return fieldHtml;
	}
	void setFieldHtml(String fieldHtml) {
		this.fieldHtml = fieldHtml;
	}
	public String getTitle() {
		return title;
	}
	void setTitle(String title) {
		this.title = title;
	}
	
}
