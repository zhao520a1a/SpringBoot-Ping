package com.springboot.ping.generic.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 响应给前端的表格展现的对象
 * @author 刘江平 2017年1月7日下午3:44:40
 *
 */
@SuppressWarnings("rawtypes")
@ApiModel("前端表格对象")
public class DataGrid {
	/**当前页数*/
	@ApiModelProperty("当前页数")
	private int page;
	/**总记录数*/
	@ApiModelProperty("总记录数")
	private int total;
	/**总页数*/
	@ApiModelProperty("总页数")
	private int pagecount;
	/**当前页记录数*/
	@ApiModelProperty("当前页记录")
	private List rows;
	public DataGrid() {
	}
	/**
	 * 不分页展现使用
	 * @param rows 当前页记录数
	 */
	public DataGrid(List rows) {
		this.rows = rows;
	}
	/**
	 * 分页展现使用
	 * @param page 当前页数
	 * @param total 总记录数
	 * @param pagecount 总页数
	 * @param rows 当前页记录数
	 */
	public DataGrid(int page, int total, int pagecount, List rows) {
		this.page = page;
		this.total = total;
		this.pagecount = pagecount;
		this.rows = rows;
	}
	/**当前页数*/
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	/**总记录数*/
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	/**总页数*/
	public int getPagecount() {
		return pagecount;
	}
	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}
	/**当前页记录数*/
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
	
}
