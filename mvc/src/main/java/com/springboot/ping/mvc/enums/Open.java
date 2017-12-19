package com.springboot.ping.mvc.enums;

/**
 * datagrid的toolbar按钮点击的方式,即按钮点击的方法名
 * 创建者： 刘江平
 * 创建时间：2015年7月14日下午6:24:01
 */
public class Open {
	/**
	 * 选中一行打开一个600*300的dialog框,比如明细、修改
	 */
	public static final String OPENDIALOGROW = "openDialogRow";
	/**
	 * 不选中行打开一个600*300的dialog框,比如新增
	 */
	public static final String OPENDIALOG = "openDialog";
	/**
	 * 选中一行打开一个600*500的dialog框,比如明细、修改
	 */
	public static final String OPENLONGDIALOGROW = "openLongDialogRow";
	/**
	 * 不选中行打开一个600*500的dialog框,比如新增
	 */
	public static final String OPENLONGDIALOG = "openLongDialog";
	/**
	 * 选中一行打开一个1000*400的dialog框,比如明细、修改
	 */
	public static final String OPENBIGDIALOGROW = "openBigDialogRow";
	/**
	 * 不选中行打开一个1000*400的dialog框,比如新增
	 */
	public static final String OPENBIGDIALOG = "openBigDialog";
	/**
	 * 选中一行打开一个tab页,比如明细、修改
	 */
	public static final String OPENTABROW = "openTabRow";
	/**
	 * 不选中行打开一个tab页,比如新增
	 */
	public static final String OPENTAB = "openTab";
	/**
	 * 选中一行做ajax请求操作,比如删除
	 */
	public static final String BTNROW = "btnRow";
	/**
	 * 选中多行做ajax请求操作,比如删除多个
	 */
	public static final String BTNROWS = "btnRows";
	/**
	 * 不选中行做ajax请求操作
	 */
	public static final String BTNAJAX = "btnAjax";
	/**
	 * excel导出
	 */
	public static final String BTNEXCEL = "btnExcel";
}
