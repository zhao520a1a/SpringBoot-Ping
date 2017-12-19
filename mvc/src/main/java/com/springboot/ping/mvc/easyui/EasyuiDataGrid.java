package com.springboot.ping.mvc.easyui;

import java.util.List;

/**
 * easyui的datagrid的数据信息类
 * 创建者： 刘江平
 * 创建时间：2015年7月10日下午5:38:47
 */
@SuppressWarnings("rawtypes")
public class EasyuiDataGrid {
	private int page;
	private int total;
	private int pagecount;
	private List rows;
	private List footer;
	public EasyuiDataGrid(){}
	public EasyuiDataGrid(List rows){
		this.rows = rows;
//		this.page = PageModelContext.getPagination().getCurrentPage();
//		this.pagecount = PageModelContext.getPagination().getTotalPages();
//		this.total = PageModelContext.getPagination().getTotalCount();
	}
	public EasyuiDataGrid(List rows,List footer){
		this.rows = rows;
		this.footer = footer;
//		this.page = PageModelContext.getPagination().getCurrentPage();
//		this.pagecount = PageModelContext.getPagination().getTotalPages();
//		this.total = PageModelContext.getPagination().getTotalCount();
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPagecount() {
		return pagecount;
	}
	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
 
	public List getFooter() {
		return footer;
	}
	public void setFooter(List footer) {
		this.footer = footer;
	}
}
