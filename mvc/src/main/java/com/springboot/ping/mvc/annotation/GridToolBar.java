package com.springboot.ping.mvc.annotation;

import com.springboot.ping.mvc.enums.Open;




/**
 * datagrid的toolbar(操作按钮)信息
 * <br>url:必填,url链接,常量类ToolBarUrl中有部分通用的url，比如明细、删除、导出等
 * <br>name:必填,按钮名称
 * <br>icon:选填,按钮前的图标,默认是一个明细按钮图标
 * <br>open:选填,按钮点击的方式,默认是openDialogRow,即选中一行打开一个dialog,值域范围请参考注解Open
 * <br>openTitle:选填,打开页面后的页面标题，如tab页、dialog框、window框等,默认属性name一样
 * <br>创建者： 刘江平
 * 创建时间：2015年7月14日下午6:18:13
 */
public @interface GridToolBar {
	/**
	 * url链接
	 * @return
	 */
	public abstract String url();
	/**
	 * 按钮名称
	 * @return
	 */
	public abstract String name();
	/**
	 * 按钮前的图标,默认是一个明细按钮图标
	 * @return
	 */
	public abstract String icon() default "icon-detail"; 
	/**
	 * 按钮点击的方式即方法名，默认是openDialogRow,即选中一行打开一个dialog,值域范围请参考常量Open,
	 * <br>也可自定义toolbar方法名，参数必须是(dg_id,url,name),比如openBigDialog(dg_id,url,name),在main.jsp引入此方法所在的js即可
	 * <br>dg_id就是datagrid的tableid,url和name就是注解配置的
	 * @see Open
	 * @return
	 */
	public abstract String open() default Open.OPENDIALOGROW;
	
	/**
	 * 打开页面后的页面标题，如tab页、dialog框、window框等
	 * @return
	 */
	public abstract String openTitle() default ""; 
}
