package com.springboot.ping.mvc.easyui;

import java.util.List;

import com.springboot.ping.common.enums.Align;
import com.springboot.ping.common.util.StringUtil;
import com.springboot.ping.mvc.ToolBarUrl;
import com.springboot.ping.mvc.annotation.Dic;
import com.springboot.ping.mvc.annotation.GridField;
import com.springboot.ping.mvc.annotation.GridToolBar;
import com.springboot.ping.mvc.annotation.Search;
import com.springboot.ping.mvc.enums.InputType;
import com.springboot.ping.mvc.enums.Open;
import com.springboot.ping.mvc.meta.GridPropertyInfo;
import com.springboot.ping.mvc.meta.PageDetailProperty;
import com.springboot.ping.mvc.meta.PageListModel;
import com.springboot.ping.mvc.meta.PageTemplate;
import com.springboot.ping.mybatis.enums.DbType;
import com.springboot.ping.mybatis.enums.Operator;

public class EasyuiTemplate implements PageTemplate {

	/**查询条件的form框的id后缀*/
	private final String searchform_suffix = "_searchform";
	/**datagrid的table的id后缀*/
	private final String dg_suffix = "_dg";
	/**属性名，数据类型，数据库三者的分隔符*/
	private final String separator = "|";
	public String getFieldHtml(List<GridPropertyInfo> propertyinfos, final PageListModel pageListModel) {
		StringBuffer fields = new StringBuffer();
		if(!pageListModel.isSingleSelect()){//多选加个checkbox列
			fields.append("<th data-options=\"field:'ck',checkbox:true\"></th>\r\n");
		}
		for (GridPropertyInfo pi : propertyinfos) {
			boolean pk = pi.isPk();
			GridField gf = pi.getGridField();
			String field = pi.getPropertyName();
			String th = "<th data-options=\"field:'%s',width:%s,hidden:%s,align:'%s',key:%s,sortable:%s\">%s</th>";
			if(pk&&gf==null){//主键字段，如果未设置注解，必须生成隐藏列
				fields.append(String.format(th, new Object[]{field,100,true,Align.center,pk,false,field})+"\r\n");
				continue;
			}
			if(gf!=null){
				fields.append(String.format(th, new Object[]{field,gf.width(),gf.hide(),gf.align(),pk,pageListModel.isAllSort()?true:gf.sortable(),StringUtil.isEmpty(gf.name(), field)})+"\r\n");
			}
		}
		return fields.toString();
	}

	public String getSearchHtml(List<GridPropertyInfo> propertyinfos, final PageListModel pageListModel) {
		StringBuffer searchs = new StringBuffer();
		/*查询条件数量*/
		int search_count=0;
		/*查询条件table的宽度*/
		int tablewidth = 1150;
		/*查询条件table的列数量/2,即每一行的字段数量*/
		int tds = 4;
		/*查询条件table的输入框列,宽度*/
		int td_input_width = 190;
		/*查询条件table的名称列,宽度*/
		int td_name_width = 100;
		for (GridPropertyInfo pi : propertyinfos) {
			GridField gf = pi.getGridField();
			String field = pi.getPropertyName();
			if(gf!=null){
				DbType dbType = pi.getDbType();
				String name = gf.name();
				boolean hide = gf.hide();
				Search search = gf.search();
				boolean isSearch = search.search();
				if(!hide&&isSearch){
					String defaultSearchValue = pi.getDefaultSearchValue();
					String defaultSearchValue2 = pi.getDefaultSearchValue2();
					search_count++;
					if(search_count==1){
						searchs.append("<table class=\"search-table\" style=\"width: "+tablewidth+"px;\">\r\n<tr>\r\n");
					}
					InputType inputType = search.inputType();
					Dic dic = gf.dic();
					if(dic!=null&&StringUtil.isNotEmpty(dic.group())){
						inputType = InputType.SELECT;
					}
					if(dbType.equals(DbType.DATE)&&(inputType.equals(InputType.TEXT)||inputType.equals(InputType.SELECT))){
						inputType = InputType.DATEBOX;
					}
					boolean between = search.between();
					String input = "";
					if(inputType.equals(InputType.TEXT)){
						/**占位符从左至右:1.field;2.dbType;3.operator*/
						String format = "<input name=\"%s"+this.separator+"%s"+this.separator+"%s\" style=\"width:%spx\" value=\"%s\"/>";
						if(dbType.equals(DbType.INT)){//整数类型
							format = "<input name=\"%s"+this.separator+"%s"+this.separator+"%s\"  class=\"easyui-numberbox\" placeholder=\"只能输入数字!\" style=\"width:%spx\" value=\"%s\"/>";
						}
						if(between){
							input = String.format(format, new Object[]{field,dbType,Operator.GTEQ,90,defaultSearchValue})
									+	"~"+String.format(format, new Object[]{field,dbType,Operator.LTEQ,90,defaultSearchValue2});
						}else{
							input = String.format(format, new Object[]{field,dbType,Operator.LIKE,td_input_width,defaultSearchValue});
						}
					}else if(inputType.equals(InputType.DATEBOX)){
						/**占位符从左至右:1.id,2.field;3.dbType;4.operator;5.onSelect;6.width*/
						String format = "<input id=\"%s\" name=\"%s"+this.separator+"%s"+this.separator+"%s\" data-options=\"editable:false,buttons:buttons%s\" class=\"easyui-datebox\" style=\"width:%spx;\"  value=\"%s\"/>";
						if(between){
							String inputstartid = pageListModel.getPageId()+this.separator+field+this.separator+dbType+this.separator+Operator.GTEQ;
							String inputendid = pageListModel.getPageId()+this.separator+field+this.separator+dbType+this.separator+Operator.LTEQ;
							String onSelect = ",onSelect:function(date){calendarValidatorAfter('"+inputendid+"',date);}";
							input = String.format(format, new Object[]{inputstartid,field,dbType,Operator.GTEQ,onSelect,90,defaultSearchValue})
									+ "~"+String.format(format, new Object[]{inputendid,field,dbType,Operator.LTEQ,"",90,defaultSearchValue2});
						}else{
							input = String.format(format, new Object[]{"",field,dbType,dbType.equals(DbType.DATE)?Operator.EQ:Operator.LIKE,"",td_input_width,defaultSearchValue});
						}
					}else if(inputType.equals(InputType.MONTHBOX)){
						if(between){
							String inputstartid = pageListModel.getPageId()+this.separator+field+this.separator+dbType+this.separator+Operator.GTEQ;
							String inputendid = pageListModel.getPageId()+this.separator+field+this.separator+dbType+this.separator+Operator.LTEQ;
							/**占位符从左至右:1.field;2.dbType;3.operator*/
							String format = "<input id=\"%s\" name=\"%s"+this.separator+"%s"+this.separator+"%s\"  value=\"%s\""
									+ "onclick=\"showMy97Between(this,'%s','%s','yyyy-MM');\" "
									+ "class=\"Wdate\" readonly=\"readonly\" type=\"text\" style=\"width:%spx\"/>";
							input = String.format(format, new Object[]{inputstartid,field,dbType,Operator.GTEQ,inputstartid,inputendid,90,defaultSearchValue})
									+ "~"+String.format(format, new Object[]{inputendid,field,dbType,Operator.LTEQ,inputstartid,inputendid,90,defaultSearchValue2});
						}else{
							/**占位符从左至右:1.field;2.dbType;3.operator*/
							String format = "<input id=\"%s\" name=\"%s"+this.separator+"%s"+this.separator+"%s\"  value=\"%s\""
									+ "onclick=\"WdatePicker({skin:'whyGreen',dateFmt:'yyyyMM'})\" "
									+ "class=\"Wdate\" readonly=\"readonly\" type=\"text\" style=\"width:%spx\"/>";
							input = String.format(format, new Object[]{"",field,dbType,dbType.equals(DbType.DATE)?Operator.EQ:Operator.LIKE,td_input_width,defaultSearchValue});
						}
					}else if(inputType.equals(InputType.YEARBOX)){

						if(between){
							String inputstartid = pageListModel.getPageId()+this.separator+field+this.separator+dbType+this.separator+Operator.GTEQ;
							String inputendid = pageListModel.getPageId()+this.separator+field+this.separator+dbType+this.separator+Operator.LTEQ;
							/**占位符从左至右:1.field;2.dbType;3.operator*/
							String format = "<input id=\"%s\" name=\"%s"+this.separator+"%s"+this.separator+"%s\"  value=\"%s\""
									+ "onclick=\"showMy97Between(this,'%s','%s','yyyy');\" "
									+ "class=\"Wdate\" readonly=\"readonly\" type=\"text\" style=\"width:%spx\"/>";
							input = String.format(format, new Object[]{inputstartid,field,dbType,Operator.GTEQ,inputstartid,inputendid,90,defaultSearchValue})
									+ "~"+String.format(format, new Object[]{inputendid,field,dbType,Operator.LTEQ,inputstartid,inputendid,90,defaultSearchValue2});
						}else{
							/**占位符从左至右:1.field;2.dbType;3.operator*/
							String format = "<input id=\"%s\" name=\"%s"+this.separator+"%s"+this.separator+"%s\"  value=\"%s\""
									+ "onclick=\"WdatePicker({skin:'whyGreen',dateFmt:'yyyy'})\" "
									+ "class=\"Wdate\" readonly=\"readonly\" type=\"text\" style=\"width:%spx\"/>";
							input = String.format(format, new Object[]{"",field,dbType,dbType.equals(DbType.DATE)?Operator.EQ:Operator.LIKE,td_input_width,defaultSearchValue});
						}
					}else if(inputType.equals(InputType.SELECT)){
						boolean singleSelect = search.singleSelect();
						input = "<input name=\""+field+this.separator+dbType+this.separator+((singleSelect)?Operator.EQ:Operator.IN)+"\"  value=\""+defaultSearchValue+"\""
								+ "class=\"easyui-combobox\" style=\"width:"+((singleSelect)?td_input_width:(td_input_width-32))+"px;\" "
								+ "data-options=\"editable:false,"+((singleSelect)?"":"multiple:true,")+"valueField:'dicCode',textField:'dicValue',onLoadSuccess:function(){$(this).combobox('setValues','"+defaultSearchValue+"');},"
								+ "url:'generic/queryDics.action?group="+dic.group()+"',onSelect:function(record){var val=record.dicCode;if(val==''){$(this).combobox('clear');$(this).combobox('setValue',val);}else{$(this).combobox('unselect','');}},"
								+ "onUnselect:function(record){var vals = $(this).combobox('getValues');if(vals==null||vals.length==0){$(this).combobox('setValue',record.dicCode);}}\"/>"
								+ ((singleSelect)?"":"(多选)");
					}
					searchs.append("<td style=\"width: "+td_name_width+"px;\" align=\"right\">"+StringUtil.isEmpty(search.searchName(), StringUtil.isEmpty(name, field))+":</td><td style=\"width: "+td_input_width+"px;\" align=\"left\">"+input+"</td>\r\n");
					if(search_count%tds==0){
						searchs.append("</tr><tr>\r\n");
					}
				}
			}
		}
		String search_str = searchs.toString();
		if(search_count>0){
			String search_button = "<td style=\"width: "+td_name_width+"px;\"><label><a href=\"#\" class=\"easyui-linkbutton\" "
					+ "data-options=\"plain:true,iconCls:'icon-search'\""
					+ " onclick=\"doSearch('"+pageListModel.getPageId()+this.dg_suffix+"','"+pageListModel.getPageId()+this.searchform_suffix+"')\">查询</a>"
					+ "</label></td>";
			if(search_str.endsWith("</tr><tr>\r\n")){
				search_str = search_str.substring(0, search_str.lastIndexOf("</tr><tr>\r\n"))+search_button;
			}else{
				search_str = search_str+search_button;
			}
			if(search_count<tds){
				search_str = search_str + "<td style=\"width: "+td_input_width+"px;\" align=\"right\">&nbsp;&nbsp;</td>";
				for(int i=1;i<tds-search_count;i++){
					search_str = search_str + "<td style=\"width: "+td_name_width+"px;\" align=\"right\">&nbsp;&nbsp;</td><td style=\"width: "+td_input_width+"px;\" align=\"left\">&nbsp;&nbsp;</td>\r\n";
				}
			}
			search_str = search_str+"</tr>\r\n</table>";
		}
		return search_str;
	}

	public String getToolbarHtml(GridToolBar[] gridToolBars, final PageListModel pageListModel) {
		StringBuffer tools = new StringBuffer();
		//参数从左至右的意义:1.按钮前图标;2.按钮点击方式(与easyui.js中的方法名一致);3.dg_id;4.url;5.按钮名称;6.按钮名称
		String format = "<a href=\"#\" class=\"easyui-linkbutton\" data-options=\"plain:true,iconCls:'%s'\""
				+ " onclick=\"%s('%s','%s','%s')\">%s</a>\r\n";
		//excel导出的方法参数特殊,主要是onclick时间函数的参数不一样
		//参数从左至右的意义:1.按钮前图标;2.按钮点击方式(与easyui.js中的方法名一致);3.url;4.查询条件form的id;5.按钮名称
		String btnExel = "<a href=\"#\" class=\"easyui-linkbutton\" data-options=\"plain:true,iconCls:'%s'\""
				+ " onclick=\"%s('%s','%s')\">%s</a>\r\n";
		String module = pageListModel.getModule();
		String entity = pageListModel.getEntity();
		for (GridToolBar toolbar : gridToolBars) {
			String name = toolbar.name();
			String openTitle = toolbar.openTitle();
			String open = toolbar.open();
			String icon = toolbar.icon();
			String url = toolbar.url();
			if(url.equals(ToolBarUrl.URL_DELETE)
					||url.equals(ToolBarUrl.URL_DELETE_BATCH)
					||url.equals(ToolBarUrl.URL_EXPORT_EXCEL)
					||url.equals(ToolBarUrl.URL_EXPORT_EXCEL_NOFORMAT)){
				url = "/"+module+"/"+entity+"/"+url;
			}else if(url.equals(ToolBarUrl.URL_DETAIL)){
				url = "/"+module+"/"+entity+"/"+url+"?open="+open;
			}
			
			if(open.equals(Open.BTNEXCEL)){
				tools.append(String.format(btnExel, new Object[]{icon,open,url,pageListModel.getPageId()+this.searchform_suffix,name}));
			}else{
				tools.append(String.format(format, new Object[]{icon,open,pageListModel.getPageId()+this.dg_suffix,url,StringUtil.isEmpty(openTitle, name),name}));
			}
		}
		return tools.toString();
	}

	@Override
	public <T> Object getDataGrid(List<T> datas) {
		return new EasyuiDataGrid(datas);
	}

	@Override
	public String getDetailHtml(List<PageDetailProperty> pageDetailPropertys,String open) {
		StringBuffer details = new StringBuffer();
		int count=0;
		/**明细table的列数量/2*/
		int tds = 1;
		//判断明细页面是否打开的tab页
		if(open.equals(Open.OPENTABROW)){
			tds = 2;
		}
		String td = "<td width=\""+(20/tds)+"%%\" class=\"even\">%s:</td><td width=\""+(80/tds)+"%%\" class=\"odd\">%s</td>";
		for (PageDetailProperty pageDetailProperty : pageDetailPropertys) {
			count++;
			String value = pageDetailProperty.getValue();
			String name = pageDetailProperty.getPropertyDesc();
			if(count==1){
				details.append("<table class=\"detail-table\" style=\"width: 100%\">\r\n<tr class=\"even\">\r\n");
			}
			int length = value.getBytes().length;
			if(length>70){
				int rows = length/70+1;
				rows = (rows>20)?20:rows;
				value = "<textarea readonly style='border: 0px solid #94BBE2;width:98%;' rows="+rows+" >"+value+"</textarea>";
			}else{
				value = "<input style='border: 0px;width:98%;' readonly value='"+value+"'/>";
			}
			details.append(String.format(td, new Object[]{name,value}));
			if(count%tds==0){
				details.append("</tr><tr>\r\n");
			}
		}

		if(count>0){
			details.append("</tr>\r\n</table>");
		}
		return null;
	}

}
