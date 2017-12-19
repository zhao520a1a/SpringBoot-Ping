package com.springboot.ping.common.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel数据模型
 * <br>title:标题
 * <br>headers:表头信息,list集合中的数组长度必须是一样的,会自动根据行和列相同值进行合并,例如[{"小三","小四小五","小四小五",,"小六"},{"小三","小五","小五",,"小六"}]
 * <br>datas:数据信息，Map的key是字段名称，value是字段值
 * <br>cols:字段列信息，包含列索引，列名称(数据key)，列数据位置，列数据类型
 * <br>colspans:表头字段对应的数据需要合并的列索引信息,索引从0开始,合并此列中数据一样的单元格
 * <br>@author 刘江平
 *
 */
public class ExcelModel {
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 表头信息,list集合中的数组长度必须是一样的
	 */
	private List<String[]> headers;
	/**
	 * 数据信息，Map的key是字段名称，value是字段值
	 */
	private List<Map<String,Object>> datas;
	/**
	 * 字段列信息，包含列索引，列名称(数据key)，列数据位置，列数据类型
	 */
	private List<ExcelColumn> cols;
	/**
	 * 表头字段对应的数据需要合并的列信息，合并此列中数据一样的单元格
	 */
	private Integer[] colspan;
	/**
	 * 自定义合并某列的某几行，
	 * Map中的Key包括
	 * col：需要合并行的列索引，
	 * row:合并行的开始索引,从表头第一行开始算起，表头第一行索引为0，
	 * rowspan：合并行数,
	 * value:合并之后要赋予的值
	 */
	private List<Map<String,Object>> customRowSpan = new ArrayList<Map<String,Object>>();
	/**需要合并行的列索引**/
	public static final String COL="col";
	/**合并行的开始索引,从表头第一行开始算起，表头第一行索引为0**/
	public static final String ROW="row";
	/**合并行数**/
	public static final String ROWSPAN="rowspan";
	/**合并之后要赋予的值**/
	public static final String VALUE="value";
	/**依据字段的值进行高亮显示，格式为"字段名称-字段值(多个值逗号隔开)",
	 * 参考多个字段的，以"|"隔开，即"字段名称-字段值(多个值逗号隔开)|字段名称-字段值(多个值逗号隔开)",
	 * 比如，"dimname-总计，线上总计，线下总计","dimname-总计，线上总计，线下总计|period-总计"
	 */
	public String hightLightRows;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String[]> getHeaders() {
		return headers;
	}
	public void setHeaders(List<String[]> headers) {
		this.headers = headers;
	}
	public List<Map<String, Object>> getDatas() {
		return datas;
	}
	public void setDatas(List<Map<String, Object>> datas) {
		this.datas = datas;
	}
	public Integer[] getColspan() {
		return colspan;
	}
	public void setColspan(Integer[] colspan) {
		this.colspan = colspan;
	}
	public List<Map<String, Object>> getCustomRowSpan() {
		return customRowSpan;
	}
	public List<ExcelColumn> getCols() {
		return cols;
	}
	public void setCols(List<ExcelColumn> cols) {
		this.cols = cols;
	}
	/**
	 * 添加自定义合并行
	 * @param col 需要合并行的列索引
	 * @param row 合并行的开始索引,从表头第一行开始算起，表头第一行索引为0
	 * @param rowspan  合并行数
	 * @param value  合并之后要赋予的值
	 * @return
	 */
	public ExcelModel addCustomRowSpan(int col,int row,int rowspan,Object value){
		Map<String,Object> m = new HashMap<String,Object>();
		m.put(ExcelModel.COL, col);
		m.put(ExcelModel.ROW, row);
		m.put(ExcelModel.ROWSPAN, rowspan);
		m.put(ExcelModel.VALUE, value);
		this.customRowSpan.add(m);
		return this;
	}
	public String getHightLightRows() {
		return hightLightRows;
	}
	public void setHightLightRows(String hightLightRows) {
		this.hightLightRows = hightLightRows;
	}
	
}
