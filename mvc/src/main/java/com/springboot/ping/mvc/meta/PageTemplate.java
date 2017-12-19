package com.springboot.ping.mvc.meta;

import java.util.List;

import com.springboot.ping.mvc.annotation.GridToolBar;

/**
 * 页面模板接口
 * @author 刘江平 2016年12月17日下午1:17:33
 *
 */
public interface PageTemplate {
	/**
	 * 获取table的表头的字段信息的html代码
	 * @param propertyinfos 字段属性集合
	 * @param pageListModel list页面模板参数对象
	 * @return
	 */
	public String getFieldHtml(List<GridPropertyInfo> propertyinfos, final PageListModel pageListModel);
	/**
	 * 获取list页面的查询条件的html代码
	 * @param propertyinfos 字段属性集合
	 * @param pageListModel list页面模板参数对象
	 * @return
	 */
	public String getSearchHtml(List<GridPropertyInfo> propertyinfos, final PageListModel pageListModel);
	/**
	 * 获取list页面的操作功能button的html代码
	 * @param gridToolBars button信息
	 * @param pageListModel list页面模板参数对象
	 * @return
	 */
	public String getToolbarHtml(GridToolBar[] gridToolBars, final PageListModel pageListModel);
	/**
	 * 获取明细模板页面的html代码
	 * @param pageDetailPropertys
	 * @param open 明细页面的打开窗口,tab或者window或者dialog,可用来布局明细页列数量
	 * @return
	 */
	public String getDetailHtml(List<PageDetailProperty> pageDetailPropertys,String open);
	/**
	 * 获取响应到前端的table对象,比如数据、总页数、总记录数等
	 * @param <T>
	 * @param datas 数据
	 * @return
	 */
	public <T> Object getDataGrid(List<T> datas);
}
