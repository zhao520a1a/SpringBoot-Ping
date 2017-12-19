package com.springboot.ping.mvc.vo;

import com.springboot.ping.common.enums.Align;
import com.springboot.ping.common.enums.Format;


/**
 * list页面或者是excel的字段信息,与注解GridField对应
 * <br>name:必填,字段中文名称
 * <br>search:选填,是否要设置成查询条件,默认为不设置成查询条件
 * <br>dicData:选填,此列的字典转义
 * <br>width:选填,前端展现或者excel导出是列的宽度,默认为100px
 * <br>hide:选填,前端展现是否要隐藏此列,默认为false不隐藏
 * <br>detail:选填,明细页面是否要展现此列，默认为true要展现
 * <br>format:选填,格式化数据，默认为文本
 * <br>align:选填,,字段的数据显示位置，默认居中显示
 * <br>dicFomart:选填,list页面中字典列的值是否要翻译成字典值,默认为true,会翻译，但必须依赖上面注解dic()配置了,false表示不翻译
 * <br>sortable:选填,list页面是否要支持此字段排序，默认false不支持排序，true代表支持排序
 * <br>创建者： 刘江平
 * 创建时间：2015年7月13日下午3:33:42
 * @see org.ping.mvc.annotation.GridField
 */
public class GridField {
	/**
	 * 字段中文名称
	 * 
	 */
	private String name;
	/**
	 * 设置成查询条件后，输入框为下拉列表框，此属性才有效
	 * 从字典表里查询数据
	 */
	private Dic dic = new Dic();
	/**
	 * 查询条件信息
	 */
	private Search search = new Search();
	/**
	 * 前端展现或者excel导出是列的宽度,默认为100px
	 * 
	 */
	private int width = 100;
	/**
	 * 前端展现是否要隐藏此列,默认为false不隐藏
	 * 
	 */
	private boolean hide = false;
	/**
	 * 明细页面是否要展现此列，默认为true要展现
	 * 
	 */
	private boolean detail = true;
	/**
	 * 格式化数据
	 * 
	 */
	private Format format = Format.TEXT;
	/**
	 * 字段数据显示位置
	 */
	private Align align = Align.center;
	/**
	 * list页面中字典列的值是否要翻译成字典值,默认为true,会翻译，但必须依赖上面注解dic()配置了,false表示不翻译
	 */
	private boolean dicFomart = true;
	
	/**
	 * list页面是否要支持此字段排序，默认false不支持排序，true代表支持排序
	 * @return
	 */
	private boolean sortable = true;
	
	/**
	 * 字段中文名称
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 字段中文名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 设置成查询条件后，输入框为下拉列表框，此属性才有效
	 * 从字典表里查询数据
	 * @return
	 */
	public Dic getDic() {
		return dic;
	}
	/**
	 * 设置成查询条件后，输入框为下拉列表框，此属性才有效
	 * 从字典表里查询数据
	 * @param dic
	 */
	public void setDic(Dic dic) {
		this.dic = dic;
	}
	/**
	 * 前端展现或者excel导出是列的宽度,默认为100px
	 * @return
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * 前端展现或者excel导出是列的宽度,默认为100px
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * 前端展现是否要隐藏此列,默认为false不隐藏
	 * @return
	 */
	public boolean isHide() {
		return hide;
	}
	/**
	 * 前端展现是否要隐藏此列,默认为false不隐藏
	 * @param hide true隐藏，false不隐藏
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	/**
	 * 明细页面是否要展现此列，默认为true要展现
	 * @return
	 */
	public boolean isDetail() {
		return detail;
	}
	/**
	 * 明细页面是否要展现此列，默认为true要展现
	 * @param detail true展现,false不展线
	 */
	public void setDetail(boolean detail) {
		this.detail = detail;
	}
	/**
	 * 格式化数据
	 * @return
	 */
	public Format getFormat() {
		return format;
	}
	/**
	 * 格式化数据,值域范围参考枚举Format
	 * @param format
	 * @see Format
	 */
	public void setFormat(Format format) {
		this.format = format;
	}
	/**
	 * 字段数据显示位置
	 * @return
	 */
	public Align getAlign() {
		return align;
	}
	/**
	 * 字段数据显示位置,值域范围参考枚举Align
	 * @param align
	 * @see Align
	 */
	public void setAlign(Align align) {
		this.align = align;
	}
	/**
	 * 查询条件信息
	 * @return
	 */
	public Search getSearch() {
		return search;
	}
	/**
	 * 查询条件信息
	 * @param search
	 */
	public void setSearch(Search search) {
		this.search = search;
	}
	/**
	 * list页面中字典列的值是否要翻译成字典值,默认为true,会翻译，但必须依赖上面注解dic()配置了,false表示不翻译
	 * @return
	 */
	public boolean isDicFomart() {
		return dicFomart;
	}
	public void setDicFomart(boolean dicFomart) {
		this.dicFomart = dicFomart;
	}
	public boolean isSortable() {
		return sortable;
	}
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
}
