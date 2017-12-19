package com.springboot.ping.common.excel;

import java.util.regex.Pattern;

/**
 * 导入excel的内容校验，方法名称格式为：check+导入的实体类名称+属性字段名称(首字母大写)
 * @author 刘江平
 *
 */
public class ExcelCheckUtils {
	/**
	 * 检查Indexinfo指标表字段：指标代码
	 * @param value 检查的值
	 * @param rowIndex  行号  
	 * @return
	 */
	public static String checkIndexinfoIndexno(String value,int rowIndex){

		if(value==null||value.equals("")){
			return "第"+rowIndex+"行【指标代码】未填值;<br/>";
		}
		String regex = "[a-zA-Z0-9]{10}";
		if(!Pattern.matches(regex, value)){
			return "第"+rowIndex+"行【指标代码】的值不是10位数字或字母组成的代码;<br/>";
		}
		return "";
	}

}
