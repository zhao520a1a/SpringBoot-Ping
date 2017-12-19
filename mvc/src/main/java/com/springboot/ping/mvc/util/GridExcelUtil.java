package com.springboot.ping.mvc.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springboot.ping.common.enums.Align;
import com.springboot.ping.common.enums.Format;
import com.springboot.ping.common.excel.ExcelColumn;
import com.springboot.ping.common.excel.ExcelUtil;
import com.springboot.ping.common.util.DateUtil;
import com.springboot.ping.common.util.StringUtil;
import com.springboot.ping.mvc.annotation.Dic;
import com.springboot.ping.mvc.annotation.GridField;
import com.springboot.ping.mvc.meta.GridModelMeta;
import com.springboot.ping.mvc.meta.GridPropertyInfo;

public class GridExcelUtil extends ExcelUtil {
	private static Logger log = LoggerFactory.getLogger(GridExcelUtil.class);
	/**
	 * 利用GridField注解和反射方式,不在服务器上生成文件,导出单个实体类的数据，生成单个sheet
	 * @see GridField
	 * @param clazz
	 * @param datas
	 * @param excelName
	 * @param response
	 */
	public static void writeGridFieldOneClass(Class<?> clazz,List<?> datas,String excelName,HttpServletResponse response) {
		try{
			XSSFWorkbook wb = createWBOneClass(clazz, datas, excelName,true);
			writeXSSFWorkbook(wb, response, excelName);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	private static XSSFWorkbook createWBOneClass(Class<?> clazz, List<?> datas,
			String sheetName, boolean isFormat) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		//建立新XSSFWorkbook对象
		XSSFWorkbook wb = new XSSFWorkbook();
		//表头样式
		XSSFCellStyle style_header = getHeaderCellStyle(wb);
		/**存储各数据列的样式*/
		Map<String,XSSFCellStyle> dataCellStyles = getDataCellStyles(wb);
		//建立新的sheet对象
		XSSFSheet sheet = wb.createSheet(sheetName);
		setSheetStyle(sheet);
		parseGridFieldAnnotationToExcel(wb, sheet, style_header, dataCellStyles, clazz, datas, isFormat);
		return wb;
	}
	/**
	 * 利用GridField注解和反射方式，解析生成excel
	 * @see GridField
	 * @param wb
	 * @param sheet
	 * @param headerCellStyle  表头单元格样式
	 * @param dataCellStyles 数据列单元格样式
	 * @param clazz  目标类型
	 * @param datas 目标类型数据集合
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static void parseGridFieldAnnotationToExcel(XSSFWorkbook wb,XSSFSheet sheet,
			XSSFCellStyle headerCellStyle,Map<String, XSSFCellStyle> dataCellStyles, Class<?> clazz,
			List<?> datas,boolean isFormat) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		XSSFRow row = sheet.createRow(0);//建立第一行，表头
		List<GridPropertyInfo> properties = GridModelMeta.getProperties(clazz);
		int colindex = 0;
		for (GridPropertyInfo gridPropertyInfo : properties) {
			GridField gridField = gridPropertyInfo.getGridField();
			if(gridField!=null){
				//列名称
				String name = gridField.name();
				XSSFCell cell = row.createCell(colindex);
				cell.setCellStyle(headerCellStyle);
				cell.setCellValue(new XSSFRichTextString(name));
				colindex++;
			}
		}
		//冻结第一行
		sheet.createFreezePane(0, 1,0,1);
		if(datas!=null&&datas.size()>=0){
			//将数据源数据写入excel中
			for (int i = 0; i < datas.size(); i++) {
				row = sheet.createRow(i+1);//建立新行
				Object obj = datas.get(i);
				int colindex2 = 0;
				for (GridPropertyInfo gridPropertyInfo : properties) {
					GridField gridField = gridPropertyInfo.getGridField();
					if(gridField!=null){
						Align align = gridField.align();
						Format format = gridField.format();
						Method method = gridPropertyInfo.getReadMethod();
						Object val = method.invoke(obj);
						String value = "";
						if(val!=null){
							
							if(Date.class==val.getClass()){
								switch (format) {
								case DATE:
									value = DateUtil.formatDate((Date) val, "yyyy-MM-dd");
									break;
								case TIME:
									value = DateUtil.formatDate((Date) val, "HH:mm:ss");
									break;
								default:
									value = DateUtil.formatDate((Date) val, "yyyy-MM-dd HH:mm:ss");
									break;
								}
							}
							value = val.toString();
							if(isFormat){
								Dic dic = gridField.dic();
								String group = dic.group();
								if(StringUtil.isNotEmpty(group)&&gridField.dicFomart()){
									//TODO 字典格式化
//									value = value+" "+DicUtil.getDicValue(group, value);
								}
							}
						}

						XSSFCell cell = row.createCell(colindex2);
						setDataCellProperties(cell, wb, value,dataCellStyles,new ExcelColumn(colindex2, gridPropertyInfo.getPropertyName(), format, align),false);
						colindex2++;
					}
				}
			}	
		}else{
			log.info("传入的数据集为null或者为空！");
		}
	}
	/**
	 * 利用GridField注解和反射方式,不在服务器上生成文件,导出单个实体类的数据，生成单个sheet，不做数据格式化
	 * @param clazz
	 * @param datas
	 * @param excelName
	 * @param response
	 */
	public static void writeGridFieldOneClassNoFormat(Class<?> clazz,List<?> datas,String excelName,HttpServletResponse response) {
		try{
			XSSFWorkbook wb = createWBOneClass(clazz,datas,excelName,false);
			writeXSSFWorkbook(wb, response, excelName);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	/**
	 * 根据注解GridField解析excel文件
	 * @see GridField
	 * @param clazz 此类的字段中需包含GridField注解
	 * @param input
	 * @param checkClass 检查excel内容的类 可传null,则不检查,检查类的检查方法格式如下:1.static静态;2.返回类型String;3.方法名称必须是check+导入的实体类名称+属性字段名称(首字母大写);4.方法的参数(String,int)(检查的值,行号)
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws InvocationTargetException
	 */
	public static List<Object> readGridField(Class<?> clazz,InputStream input,Class<?> checkClass) throws InstantiationException, IllegalAccessException, IOException, InvocationTargetException{
		return readExcelByAnnotation(clazz, input,getGridFields(clazz),getGridNormName(clazz),checkClass);
	}
	private static Map<String, String> getGridNormName(Class<?> clazz) {
		Map<String,String> names = new HashMap<String, String>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			GridField obj = field.getAnnotation(GridField.class);
			if(obj!=null){
				names.put(field.getName(), obj.name());
			}
		}
		return names;
	}
	private static List<Field> getGridFields(Class<?> clazz) {
		List<Field> fs = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			GridField obj = field.getAnnotation(GridField.class);
			if(obj!=null){
				fs.add(field);
			}
		}
		return fs;
	}
}
