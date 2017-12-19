package com.springboot.ping.common.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springboot.ping.common.enums.Align;
import com.springboot.ping.common.enums.Format;
import com.springboot.ping.common.util.StringUtil;


/**
 * excel导入，导出
 * @author 刘江平
 *
 */
public class ExcelUtil {
	private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);
	/**
	 * 不在服务器上生成文件,生成单个sheet
	 * @param ems excel数据模型
	 * @param sheetName
	 * @param excelName 文件名称
	 * @param response
	 */
	public static void writeExcelOneSheet(List<ExcelModel> ems,String sheetName,String excelName,HttpServletResponse response){
		XSSFWorkbook wb = createWBOneSheet(ems,sheetName);
		writeXSSFWorkbook(wb,response,excelName);
	}
	/**
	 * 把XSSFWorkbook写入输出流，响应出去
	 * @param wb XSSFWorkbook对象
	 * @param response 
	 * @param filename 文件名称
	 */
	protected static void writeXSSFWorkbook(XSSFWorkbook wb,
			HttpServletResponse response, String filename) {
		try
		{
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("GBK"),"ISO-8859-1") + ".xlsx");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setDateHeader("Expires", 0L);

			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
		}
		catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(),e);
		}
		catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}
	/**
	 * 不在服务器上生成文件,生成多个sheet,一个excelmodel对应一个sheet
	 * @param ems excel数据模型
	 * @param excelName 文件名称
	 * @param response
	 */
	public static void writeExcelSheets(List<ExcelModel> ems,String excelName,HttpServletResponse response){
		XSSFWorkbook wb = createWBSheets(ems);
		writeXSSFWorkbook(wb, response, excelName);
	}
	/**
	 * 服务器上生成文件,生成多个sheet,一个excelmodel对应一个sheet
	 * @param ems
	 * @param out
	 */
	public static void writeExcelSheets(List<ExcelModel> ems,OutputStream out){
		XSSFWorkbook wb = createWBSheets(ems);
		try {
			wb.write(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

	/**
	 * 会在服务器上生成文件，生成单个sheet
	 * @param ems excel数据模型
	 * @param sheetName
	 * @param excelName 文件名称
	 * @param path 文件放置的目录全路径，不要以"\"结尾
	 * @return 返回生成文件的全路径
	 */
	public static String writeExcelOneSheet(List<ExcelModel> ems,String sheetName,String excelName,String path){
		//建立新XSSFWorkbook对象
		XSSFWorkbook wb = createWBOneSheet(ems,sheetName);
		String pathname = path+File.separatorChar+excelName+".xlsx";
		File baseFile = new File(path);
		if(!baseFile.exists()){
			baseFile.mkdirs();
		}
		try {
			OutputStream out =  new FileOutputStream(pathname);
			wb.write(out);
			out.flush();
			out.close();
			return pathname;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 生成单个sheet，可以自定义设置列数据的类型和位置，excelmodel需要有List<Column> cols 属性值
	 * @param ems
	 * @param sheetName
	 * @return
	 */
	protected static XSSFWorkbook createWBOneSheet(List<ExcelModel> ems, String sheetName) {
		//建立新XSSFWorkbook对象
		XSSFWorkbook wb = new XSSFWorkbook();
		//标题单元格样式
		XSSFCellStyle titleCellStyle = getTitleCellStyle(wb);
		//表头单元格样式
		XSSFCellStyle headerCellStyle = getHeaderCellStyle(wb);
		/**存储各数据列的样式*/
		Map<String,XSSFCellStyle> dataCellStyles = getDataCellStyles(wb);
		//建立新的sheet对象
		XSSFSheet sheet = wb.createSheet(sheetName);
		setSheetStyle(sheet);
		//标题行索引
		int titlerow=0;
		//表头开始行索引
		int headerrowstart=1;
		//表头结束行索引
		int headerrowend=1;
		//数据开始行索引
		int datarowstart=2;
		//数据结束行索引
		int datarowend=2;
		//画excel
		if(ems==null||ems.size()==0){
			log.warn("没有excel模型数据！");
			return null;
		}
		for (int i = 0; i < ems.size(); i++) {
			ExcelModel em = ems.get(i);
			parseExcelModelToExcel(wb,titleCellStyle,headerCellStyle,dataCellStyles,sheet,titlerow,headerrowstart,headerrowend,datarowstart,datarowend,em);
			//标题行索引
			titlerow=sheet.getLastRowNum()+3;
			//表头开始行索引
			headerrowstart=titlerow+1;
			//表头结束行索引
			headerrowend=headerrowstart;
			//数据开始行索引
			datarowstart=headerrowend+1;
			//数据结束行索引
			datarowend=datarowstart;

		}
		return wb;
	}
	/**
	 * 解析excelmodel，写入excel
	 * @param wb XSSFWorkbook对象
	 * @param dataCellStyles 数据列单元个样式  map
	 * @param headerCellStyle  表头单元格样式
	 * @param titleCellStyle  标题单元格样式
	 * @param sheet XSSFSheet对象
	 * @param titlerow 标题行索引
	 * @param headerrowstart 表头开始行索引
	 * @param headerrowend 表头结束行索引
	 * @param datarowstart 数据开始行索引
	 * @param datarowend 数据结束行索引
	 * @param em
	 */
	protected static void parseExcelModelToExcel(XSSFWorkbook wb,XSSFCellStyle titleCellStyle, 
			XSSFCellStyle headerCellStyle, Map<String, XSSFCellStyle> dataCellStyles, 
			XSSFSheet sheet, int titlerow, int headerrowstart,int headerrowend,
			int datarowstart, int datarowend, ExcelModel em) {
		String title = em.getTitle();
		List<String[]> headers = em.getHeaders();
		/**建立标题行开始**/
		XSSFRow row = sheet.createRow(titlerow);
		XSSFCell c_title = row.createCell((short) 0);
		c_title.setCellStyle(titleCellStyle);
		c_title.setCellValue(new XSSFRichTextString(title));
		/**建立标题行结束**/
		if(headers==null||headers.size()==0){
			log.info("无表头信息");
			return ;
		}
		/**建立表头行开始**/
		//表头字段列数量
		int column = 0;
		//表头行数量
		int headerrows = headers.size();
		//建立表头行数据
		for (int j = 0; j < headerrows; j++) {
			String[] header = headers.get(j);
			column = header.length;
			row = sheet.createRow(headerrowstart+j);//建立标题行
			for (int k = 0; k < column; k++) {
				XSSFCell c_header = row.createCell((short) k);
				c_header.setCellStyle(headerCellStyle);
				c_header.setCellValue(new XSSFRichTextString(header[k].replace("<br>", "\r\n")));
			}
		}	
		/**建立表头行结束**/
		/**合并标题行开始**/
		if(column>1){
			sheet.addMergedRegion(new CellRangeAddress(titlerow,  titlerow,0,(column-1)));
		}
		/**合并标题行结束**/

		/**合并表头开始**/
		/**合并表头行开始**/
		headerrowend = sheet.getLastRowNum();
		mergeRow(sheet, column, headerrowstart, headerrowend);
		/**合并表头行结束**/

		/**合并表头列开始**/
		mergerCol(sheet, column, headerrowstart, headerrowend);
		/**合并表头列结束**/
		/**合并表头结束**/
		/**列字段信息**/
		//Map<Integer, String> columns = em.getColumns();
		/**列字段信息**/
		List<ExcelColumn> cols = em.getCols();
		/**合并数据一样的行的列信息**/
		Integer[] colspan = em.getColspan();
		/**字段对应的数据库数据信息**/
		List<Map<String, Object>> datas = em.getDatas();
		String hightLightRows = em.getHightLightRows();
		if(datas!=null&&datas.size()>0){
			/**写数据开始**/
			datarowstart = sheet.getLastRowNum()+1;
			for (int j = 0; j < datas.size(); j++) {
				row=sheet.createRow(datarowstart+j);
				Map<String, Object> data = datas.get(j);
				boolean isHightLight = false;
				if(StringUtil.isNotEmpty(hightLightRows)){
					String[] hlrs = hightLightRows.split("\\|");
					for (String hlr : hlrs) {
						if(StringUtil.isNotEmpty(hlr)){
							String[] fvs = hlr.split("-");
							String field = fvs[0];
							String fieldval = null;
							if(StringUtil.isNotEmpty(field)&&data.containsKey(field) && data.get(field) != null){
								fieldval = (String)data.get(field);
							}
							if(StringUtil.isNotEmpty(fieldval)){
								String vals = fvs[1];
								String[] values = vals.split(",");
								Arrays.sort(values);
								int index = Arrays.binarySearch(values, fieldval);
								if(index>=0){
									isHightLight = true;
									break;
								}
							}
						}
					}
				}
				for (ExcelColumn col : cols) {
					int colindex = col.getColindex();
					String colfield = col.getField();
					String colvalue = "";
					if(data.containsKey(colfield)){
						Object obj = data.get(colfield);
						if(obj==null){
							colvalue="";	
						}else{
							colvalue = obj.toString();
						}
					}
					XSSFCell c_data = row.createCell((short) colindex);
					setDataCellProperties(c_data,wb,colvalue,dataCellStyles,col,isHightLight);
				}
			}
			/**写数据结束**/

			/**合并指定数据列的相同值的行开始**/
			if(colspan!=null&&colspan.length>0){
				datarowend = sheet.getLastRowNum();
				mergeRow(sheet, colspan, datarowstart, datarowend);

			}
			/**合并指定数据列的相同值的行结束**/
			/**合并指定数据列的指定行索引的行开始**/
			List<Map<String, Object>> crs = em.getCustomRowSpan();
			if(crs!=null&&crs.size()>0){
				for (Map<String, Object> cr : crs) {
					int c_col = (Integer) cr.get(ExcelModel.COL);
					int firstRow = (Integer) cr.get(ExcelModel.ROW)+headerrowstart;
					int lastRow = firstRow+(Integer) cr.get(ExcelModel.ROWSPAN)-1;
					Object c_val = cr.get(ExcelModel.VALUE);
					sheet.addMergedRegion(new CellRangeAddress(firstRow,lastRow,c_col,c_col));
					XSSFRow c_row = sheet.getRow(firstRow);
					XSSFCell c_cell = c_row.getCell(c_col);
					ExcelColumn tempcol = null;
					for(ExcelColumn col:cols){
						int colindex = col.getColindex();
						if(colindex==c_col){
							tempcol = col;
							break;
						}
					}
					if(tempcol!=null){
						setDataCellProperties(c_cell,  wb, c_val.toString(),dataCellStyles,tempcol,false);
					}
				}
			}
			/**合并指定数据列的指定行索引的行结束**/
		}
	}
	/**
	 * 给存储数据的单元格赋值数据和设置样式
	 * @param c_data  单元格对象
	 * @param wb XSSFWorkbook对象
	 * @param colvalue 数据值
	 * @param dataCellStyles 存储数据列的样式map
	 * @param col 数据列类型
	 * @param isHightLight 是否要高亮，true代表是，false代表不是
	 */
	protected static void setDataCellProperties(XSSFCell c_data, XSSFWorkbook wb,
			String colvalue, Map<String, XSSFCellStyle> dataCellStyles, ExcelColumn col, boolean isHightLight) {
		if(colvalue==null){
			colvalue = "";
		}
		boolean existZero = col.isExistZero();
		Format format = col.getFormat();
		Align align = col.getAlign();
		String key = format.toString()+align.toString();
		String hl = (isHightLight)?"HL":"";
		if(!existZero){
			String tempval = replaceAllSpecialCharacter(colvalue);
			try {
				double pd = Double.parseDouble((tempval.equals("—")||tempval.equals(""))?"0":tempval);
				if(pd==0){
					c_data.setCellType(CellType.STRING);
					c_data.setCellStyle(dataCellStyles.get(hl+Format.TEXT+align));
					c_data.setCellValue(new XSSFRichTextString("—"));
					return;
				}
			} catch (NumberFormatException e) {
				log.error(StringUtil.getTrace(e));
			}
		}
		
		switch (format) {
		case NUMBER:
			c_data.setCellType(CellType.NUMERIC);
			key = getNumberKey(key, colvalue);
			c_data.setCellValue(getDoubleValue(colvalue));
			break;
		case AMT:
			c_data.setCellType(CellType.NUMERIC);
			key = getNumberKey(key, colvalue);
			c_data.setCellValue(getDoubleValue(colvalue));
			break;
		case RATE:
			c_data.setCellType(CellType.STRING);
			colvalue = replaceAllSpecialCharacter(colvalue);
			colvalue = ((StringUtil.isNotEmpty(colvalue))?colvalue:"0")+ "%";
			key=Format.TEXT.toString()+align;
			c_data.setCellValue(new XSSFRichTextString(colvalue));
			break;
		default:
			c_data.setCellType(CellType.STRING);
			c_data.setCellValue(new XSSFRichTextString(colvalue));
			break;
		}
		c_data.setCellStyle(dataCellStyles.get(hl+key));
	}
	private static String replaceAllSpecialCharacter(String colvalue){
		return colvalue.replaceAll("￥", "").replaceAll(",", "").replaceAll("\\$", "").replaceAll("¥", "");
	}
	
	private static double getDoubleValue(String colvalue){
		colvalue = replaceAllSpecialCharacter(colvalue);
		return new BigDecimal((StringUtil.isNotEmpty(colvalue))?colvalue:"0").doubleValue();
	}
	private static String getNumberKey(String key, String colvalue) {
		colvalue = replaceAllSpecialCharacter(colvalue);
		int pointindex = colvalue.indexOf(".");
		key = key+"0";
		if(pointindex>=0&&!colvalue.endsWith(".")){
			String temp = colvalue.substring(pointindex+1);
			if(null!=temp&&!temp.trim().equals("")){
				if(Double.parseDouble(temp)!=0){
					key = key.substring(0, key.length()-1)+"10";
				}
			}
		}
		return key;
	}
	/**
	 * 获取常用的样式
	 * key对应的样式：
	 * text2:居中不格式化；number210:居中格式化数字类，千分位，保留10位小数；amt210:居中格式化金额类，千分位，保留10位小数；
	 * number20:居中格式化数字类，千分位，不保留小数；amt20:居中格式化金额类，千分位，不保留小数；
	 * text3:靠右不格式化；number310:靠右格式化数字类，千分位，保留10位小数；amt310:靠右格式化金额类，千分位，保留10位小数；
	 * number30:靠右格式化数字类，千分位，不保留小数；amt30:靠右格式化金额类，千分位，不保留小数；
	 * text1:靠左不格式化；number110:靠左格式化数字类，千分位，保留10位小数；amt110:靠左格式化金额类，千分位，保留10位小数；
	 * number10:靠左格式化数字类，千分位，不保留小数；amt10:靠左格式化金额类，千分位，不保留小数； 
	 * @param wb
	 * @return
	 */
	protected static Map<String, XSSFCellStyle> getDataCellStyles(XSSFWorkbook wb) {
		Map<String, XSSFCellStyle> styles = new HashMap<String, XSSFCellStyle>();
		//居中不格式化
		XSSFCellStyle s1 = getDataCellStyle(wb,false);
		s1.setAlignment(HorizontalAlignment.CENTER);
		styles.put(Format.TEXT.toString()+Align.center, s1);
		//居中格式化数字类，千分位，保留10位小数
		XSSFCellStyle s2 = getDataCellStyle(wb,false);
		s2.setAlignment(HorizontalAlignment.CENTER);
		s2.setDataFormat(wb.createDataFormat().getFormat("#,##0.###########"));
		styles.put(Format.NUMBER.toString()+Align.center+"10", s2);
		//居中格式化金额类，千分位，保留10位小数
		XSSFCellStyle s3 = getDataCellStyle(wb,false);
		s3.setAlignment(HorizontalAlignment.CENTER);
		s3.setDataFormat(wb.createDataFormat().getFormat("¥#,##0.###########"));
		styles.put(Format.AMT.toString()+Align.center+"10", s3);
		//居中格式化数字类，千分位，不保留小数
		XSSFCellStyle s4 = getDataCellStyle(wb,false);
		s4.setAlignment(HorizontalAlignment.CENTER);
		s4.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
		styles.put(Format.NUMBER.toString()+Align.center+"0", s4);
		//居中格式化金额类，千分位，不保留小数
		XSSFCellStyle s5 = getDataCellStyle(wb,false);
		s5.setAlignment(HorizontalAlignment.CENTER);
		s5.setDataFormat(wb.createDataFormat().getFormat("¥#,##0"));
		styles.put(Format.AMT.toString()+Align.center+"0", s5);
		//靠右不格式化
		XSSFCellStyle s6 = getDataCellStyle(wb,false);
		s6.setAlignment(HorizontalAlignment.RIGHT);
		styles.put(Format.TEXT.toString()+Align.right, s6);
		//靠右格式化数字类，千分位，保留10位小数
		XSSFCellStyle s7 = getDataCellStyle(wb,false);
		s7.setAlignment(HorizontalAlignment.RIGHT);
		s7.setDataFormat(wb.createDataFormat().getFormat("#,##0.###########"));
		styles.put(Format.NUMBER.toString()+Align.right+"10", s7);
		//靠右格式化金额类，千分位，保留10位小数
		XSSFCellStyle s8 = getDataCellStyle(wb,false);
		s8.setAlignment(HorizontalAlignment.RIGHT);
		s8.setDataFormat(wb.createDataFormat().getFormat("¥#,##0.###########"));
		styles.put(Format.AMT.toString()+Align.right+"10", s8);
		//靠右格式化数字类，千分位，不保留小数
		XSSFCellStyle s9 = getDataCellStyle(wb,false);
		s9.setAlignment(HorizontalAlignment.RIGHT);
		s9.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
		styles.put(Format.NUMBER.toString()+Align.right+"0", s9);
		//靠右格式化金额类，千分位，不保留小数
		XSSFCellStyle s10 = getDataCellStyle(wb,false);
		s10.setAlignment(HorizontalAlignment.RIGHT);
		s10.setDataFormat(wb.createDataFormat().getFormat("¥#,##0"));
		styles.put(Format.AMT.toString()+Align.right+"0", s10);
		//靠左不格式化
		XSSFCellStyle s11 = getDataCellStyle(wb,false);
		s11.setAlignment(HorizontalAlignment.LEFT);
		styles.put(Format.TEXT.toString()+Align.left, s11);
		//靠左格式化数字类，千分位，保留10位小数
		XSSFCellStyle s12 = getDataCellStyle(wb,false);
		s12.setAlignment(HorizontalAlignment.LEFT);
		s12.setDataFormat(wb.createDataFormat().getFormat("#,##0.###########"));
		styles.put(Format.NUMBER.toString()+Align.left+"10", s12);
		//靠左格式化金额类，千分位，保留10位小数
		XSSFCellStyle s13 = getDataCellStyle(wb,false);
		s13.setAlignment(HorizontalAlignment.LEFT);
		s13.setDataFormat(wb.createDataFormat().getFormat("¥#,##0.###########"));
		styles.put(Format.AMT.toString()+Align.left+"10", s13);
		//靠左格式化数字类，千分位，不保留小数
		XSSFCellStyle s14 = getDataCellStyle(wb,false);
		s14.setAlignment(HorizontalAlignment.LEFT);
		s14.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
		styles.put(Format.NUMBER.toString()+Align.left+"0", s14);
		//靠左格式化金额类，千分位，不保留小数
		XSSFCellStyle s15 = getDataCellStyle(wb,false);
		s15.setAlignment(HorizontalAlignment.LEFT);
		s15.setDataFormat(wb.createDataFormat().getFormat("¥#,##0"));
		styles.put(Format.AMT.toString()+Align.left+"0", s15);

		//居中不格式化
		XSSFCellStyle s16 = getDataCellStyle(wb,true);
		s16.setAlignment(HorizontalAlignment.CENTER);
		styles.put("HL"+Format.TEXT+Align.center, s16);
		//居中格式化数字类，千分位，保留10位小数
		XSSFCellStyle s17 = getDataCellStyle(wb,true);
		s17.setAlignment(HorizontalAlignment.CENTER);
		s17.setDataFormat(wb.createDataFormat().getFormat("#,##0.###########"));
		styles.put("HL"+Format.NUMBER+Align.center+"10", s17);
		//居中格式化金额类，千分位，保留10位小数
		XSSFCellStyle s18 = getDataCellStyle(wb,true);
		s18.setAlignment(HorizontalAlignment.CENTER);
		s18.setDataFormat(wb.createDataFormat().getFormat("¥#,##0.###########"));
		styles.put("HL"+Format.AMT+Align.center+"10", s18);
		//居中格式化数字类，千分位，不保留小数
		XSSFCellStyle s19 = getDataCellStyle(wb,true);
		s19.setAlignment(HorizontalAlignment.CENTER);
		s19.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
		styles.put("HL"+Format.NUMBER+Align.center+"0", s19);
		//居中格式化金额类，千分位，不保留小数
		XSSFCellStyle s20 = getDataCellStyle(wb,true);
		s20.setAlignment(HorizontalAlignment.CENTER);
		s20.setDataFormat(wb.createDataFormat().getFormat("¥#,##0"));
		styles.put("HL"+Format.AMT+Align.center+"0", s20);
		//靠右不格式化
		XSSFCellStyle s21 = getDataCellStyle(wb,true);
		s21.setAlignment(HorizontalAlignment.RIGHT);
		styles.put("HL"+Format.TEXT+Align.right, s21);
		//靠右格式化数字类，千分位，保留10位小数
		XSSFCellStyle s22 = getDataCellStyle(wb,true);
		s22.setAlignment(HorizontalAlignment.RIGHT);
		s22.setDataFormat(wb.createDataFormat().getFormat("#,##0.###########"));
		styles.put("HL"+Format.NUMBER+Align.right+"10", s22);
		//靠右格式化金额类，千分位，保留10位小数
		XSSFCellStyle s23 = getDataCellStyle(wb,true);
		s23.setAlignment(HorizontalAlignment.RIGHT);
		s23.setDataFormat(wb.createDataFormat().getFormat("¥#,##0.###########"));
		styles.put("HL"+Format.AMT+Align.right+"10", s23);
		//靠右格式化数字类，千分位，不保留小数
		XSSFCellStyle s24 = getDataCellStyle(wb,true);
		s24.setAlignment(HorizontalAlignment.RIGHT);
		s24.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
		styles.put("HL"+Format.NUMBER+Align.right+"0", s24);
		//靠右格式化金额类，千分位，不保留小数
		XSSFCellStyle s25 = getDataCellStyle(wb,true);
		s25.setAlignment(HorizontalAlignment.RIGHT);
		s25.setDataFormat(wb.createDataFormat().getFormat("¥#,##0"));
		styles.put("HL"+Format.AMT+Align.right+"0", s25);
		//靠左不格式化
		XSSFCellStyle s26 = getDataCellStyle(wb,true);
		s26.setAlignment(HorizontalAlignment.LEFT);
		styles.put("HL"+Format.TEXT+Align.left, s26);
		//靠左格式化数字类，千分位，保留10位小数
		XSSFCellStyle s27 = getDataCellStyle(wb,true);
		s27.setAlignment(HorizontalAlignment.LEFT);
		s27.setDataFormat(wb.createDataFormat().getFormat("#,##0.###########"));
		styles.put("HL"+Format.NUMBER+Align.left+"10", s27);
		//靠左格式化金额类，千分位，保留10位小数
		XSSFCellStyle s28 = getDataCellStyle(wb,true);
		s28.setAlignment(HorizontalAlignment.LEFT);
		s28.setDataFormat(wb.createDataFormat().getFormat("¥#,##0.###########"));
		styles.put("HL"+Format.AMT+Align.left+"10", s28);
		//靠左格式化数字类，千分位，不保留小数
		XSSFCellStyle s29 = getDataCellStyle(wb,true);
		s29.setAlignment(HorizontalAlignment.LEFT);
		s29.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
		styles.put("HL"+Format.NUMBER+Align.left+"0", s29);
		//靠左格式化金额类，千分位，不保留小数
		XSSFCellStyle s30 = getDataCellStyle(wb,true);
		s30.setAlignment(HorizontalAlignment.LEFT);
		s30.setDataFormat(wb.createDataFormat().getFormat("¥#,##0"));
		styles.put("HL"+Format.AMT+Align.left+"0", s30);
		return styles;
	}
	/**
	 * 获取存储表头的单元格的样式
	 * @param wb
	 * @return
	 */
	protected static XSSFCellStyle getHeaderCellStyle(XSSFWorkbook wb) {
		XSSFCellStyle style_header = wb.createCellStyle();
		//设置样式
		style_header.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style_header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style_header.setBorderBottom(BorderStyle.THIN);
		style_header.setBorderLeft(BorderStyle.THIN);
		style_header.setBorderRight(BorderStyle.THIN);
		style_header.setBorderTop(BorderStyle.THIN);
		style_header.setAlignment(HorizontalAlignment.CENTER);
		style_header.setVerticalAlignment(VerticalAlignment.CENTER);
		//生成一个字体
		XSSFFont font = wb.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		//把字体应用到当前的样式
		style_header.setFont(font);
		return style_header;
	}
	/**
	 * 获取存储标题的单元格的样式
	 * @param wb
	 * @return
	 */
	protected static XSSFCellStyle getTitleCellStyle(XSSFWorkbook wb) {
		XSSFCellStyle style_title= wb.createCellStyle();
		//设置样式
		style_title.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		style_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style_title.setBorderBottom(BorderStyle.THIN);
		style_title.setBorderLeft(BorderStyle.THIN);
		style_title.setBorderRight(BorderStyle.THIN);
		style_title.setBorderTop(BorderStyle.THIN);
		style_title.setAlignment(HorizontalAlignment.CENTER);
		style_title.setVerticalAlignment(VerticalAlignment.CENTER);
		//生成一个字体
		XSSFFont font = wb.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		//把字体应用到当前的样式
		style_title.setFont(font);
		return style_title;
	}
	/**
	 * 获取存储数据的单元格的样式
	 * @param wb
	 * @param isHightLight  是否要高亮，true代表高亮，false代表不高亮
	 * @return
	 */
	protected static XSSFCellStyle getDataCellStyle(XSSFWorkbook wb,boolean isHightLight) {
		XSSFCellStyle style_data = wb.createCellStyle();
		style_data.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style_data.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style_data.setBorderBottom(BorderStyle.THIN);
		style_data.setBorderLeft(BorderStyle.THIN);
		style_data.setBorderRight(BorderStyle.THIN);
		style_data.setBorderTop(BorderStyle.THIN);
		style_data.setAlignment(HorizontalAlignment.CENTER);
		style_data.setVerticalAlignment(VerticalAlignment.CENTER);
		//生成另一个字体
		XSSFFont font = wb.createFont();
		font.setBold(false);
		if(isHightLight){
			font.setColor(HSSFColor.RED.index);
			font.setBold(true);
		}
		//把字体应用到当前的样式
		style_data.setFont(font);
		return style_data;
	}
	/**
	 * 生成多个sheet
	 * @param ems
	 * @return
	 */
	protected static XSSFWorkbook createWBSheets(List<ExcelModel> ems) {
		if(ems==null||ems.size()==0){
			log.error("没有excel模型数据！");
			return null;
		}
		//建立新XSSFWorkbook对象
		XSSFWorkbook wb = new XSSFWorkbook();
		//标题单元格样式
		XSSFCellStyle titleCellStyle = getTitleCellStyle(wb);
		//表头单元格样式
		XSSFCellStyle headerCellStyle = getHeaderCellStyle(wb);
		/**存储各数据列的样式*/
		Map<String,XSSFCellStyle> dataCellStyles = getDataCellStyles(wb);
		for (ExcelModel em : ems) {
			String title = em.getTitle();
			//建立新的sheet对象
			XSSFSheet sheet = wb.createSheet(title);
			setSheetStyle(sheet);
			parseExcelModelToExcel(wb,titleCellStyle,headerCellStyle,dataCellStyles,sheet,0,1,1,2,2,em);
		}

		return wb;
	}
	protected static void setSheetStyle(XSSFSheet sheet) {
		//设置表格默认列宽度为30个字节
		sheet.setDefaultColumnWidth((short)15);
		sheet.setDefaultRowHeight((short)300);
	}
	/**
	 * 合并行区间中的列数据
	 * @param sheet
	 * @param column 总列数
	 * @param rowstart 起始行
	 * @param rowend 结束行
	 */
	protected static void mergerCol(XSSFSheet sheet,int column,int rowstart,int rowend){
		//记录比较行单元格中的上一个单元格的数据
		String tmp_v = null;
		//合并列开始索引
		int firstCol = 0;
		//合并列结束索引
		int lastCol = 0;
		for(int l=rowstart;l<=rowend;l++){
			for (int m = 0; m < column; m++) {
				XSSFRow row = sheet.getRow(l);
				XSSFCell cell = row.getCell(m);
				String value = getValue(cell);
				//开始比较参考
				if(tmp_v==null){
					tmp_v=value;
					firstCol=m;
					lastCol=m;
					continue;
				}
				if(tmp_v.equals(value)){
					lastCol++;
				}else{
					if(firstCol!=lastCol){
						//Region 参数，开始行索引，开始列索引，结束行索引，结束列索引
						//合并单元格
						sheet.addMergedRegion(new CellRangeAddress(l,l,firstCol,lastCol));
						firstCol=m;
						lastCol=m;
					}
					firstCol=m;
					lastCol=m;
					tmp_v=value;
				}
				if(m==(column-1)){//最后一列
					if(firstCol!=lastCol){
						//Region 参数，开始行索引，开始列索引，结束行索引，结束列索引
						//合并单元格
						sheet.addMergedRegion(new CellRangeAddress(l,l,firstCol,lastCol));
					}
					tmp_v=null;
				}
			}
		}
	}
	/**
	 * 合并指定列的行数据
	 * @param sheet
	 * @param colspan 指定的列索引
	 * @param rowstart 起始行
	 * @param rowend 结束行
	 */
	protected static void mergeRow(XSSFSheet sheet,Integer[] colspan,int rowstart,int rowend){
		//记录比较行单元格中的上一个单元格的数据
		String tmp_v = null;
		//合并行开始索引
		int firstRow = 0;
		//合并行结束索引
		int lastRow = 0;
		for (int m = 0; m < colspan.length; m++) {
			for(int l=rowstart;l<=rowend;l++){
				XSSFRow row = sheet.getRow(l);
				int firstCol = colspan[m];
				XSSFCell cell = row.getCell(firstCol);
				String value = getValue(cell);
				//开始比较参考
				if(tmp_v==null){
					tmp_v=value;
					firstRow=l;
					lastRow=l;
					continue;
				}
				if(tmp_v.equals(value)&&!tmp_v.trim().equals("")){
					lastRow++;
				}else{
					if(firstRow!=lastRow){
						//Region 参数，开始行索引，开始列索引，结束行索引，结束列索引
						//合并单元格
						sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, firstCol));
						firstRow=l;
						lastRow=l;
					}
					firstRow=l;
					lastRow=l;
					tmp_v=value;
				}
				if(l==rowend){//最后一行
					if(firstRow!=lastRow){
						//Region 参数，开始行索引，开始列索引，结束行索引，结束列索引
						//合并单元格
						sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, firstCol));
					}
					tmp_v=null;
				}
			}
		}
	}
	/**
	 * 合并所有列的行数据
	 * @param sheet
	 * @param colsum  总列数
	 * @param rowstart 起始行
	 * @param rowend 结束行
	 */
	protected static void mergeRow(XSSFSheet sheet,int colsum,int rowstart,int rowend){
		//记录比较行单元格中的上一个单元格的数据
		String tmp_v = null;
		//合并行开始索引
		int firstRow = 0;
		//合并行结束索引
		int lastRow = 0;
		for (int m = 0; m < colsum; m++) {
			for(int l=rowstart;l<=rowend;l++){
				XSSFRow row = sheet.getRow(l);
				XSSFCell cell = row.getCell(m);
				String value = getValue(cell);
				//开始比较参考
				if(tmp_v==null){
					tmp_v=value;
					firstRow=l;
					lastRow=l;
					continue;
				}
				if(tmp_v.equals(value)){
					lastRow++;
				}else{
					if(firstRow!=lastRow){
						//Region 参数，开始行索引，开始列索引，结束行索引，结束列索引
						//合并单元格
						sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, m, m));
						firstRow=l;
						lastRow=l;
					}
					firstRow=l;
					lastRow=l;
					tmp_v=value;
				}
				if(l==rowend){//最后一行
					if(firstRow!=lastRow){
						//Region 参数，开始行索引，开始列索引，结束行索引，结束列索引
						//合并单元格
						sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, m, m));
					}
					tmp_v=null;
				}
			}
		}
	}
	/**
	 * 描述:得到一个单元格的值
	 * @param cell
	 * @return
	 */
	protected static String getValue(XSSFCell cell ){
		if(null!=cell){
			//取值之前都设置为文本格式，就不用去考虑不同类型的值
			String value = "";	
			@SuppressWarnings("deprecation")
			CellType type = cell.getCellTypeEnum();
			switch (type) {
			case BLANK ://空值
				value = "";
				break;
			case BOOLEAN://布尔型 
				value = String.valueOf(cell.getBooleanCellValue());
				break;
			case ERROR://错误
				value = "";
				break;
			case FORMULA://公式型
				value = cell.getNumericCellValue()+"";
				break;
			case NUMERIC://数值型 
				if(HSSFDateUtil.isCellDateFormatted(cell)){
					value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
				}else{
					value = cell.getNumericCellValue()+"";
					BigDecimal bd = new BigDecimal(value);
					value = bd.toPlainString().trim();
					if(value.endsWith(".0")){
						value = value.substring(0,value.length()-2);
					}
				}
				break;
			case STRING://字符串型 
				value = cell.getRichStringCellValue().getString();
				break;
			default:
				break;
			}
			//cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			if(null==value){
				value="";
			}else{
				//把值转换为String 并且去掉前后空格    
				value=String.valueOf(value).trim();
			}
			return  value;  
		}else{
			return "";
		}
	}

	/**
	 * 利用Excel注解和反射方式，生成excel
	 * @param clazz 目标类型
	 * @param datas 目标类型的数据集合
	 * @param sheetName 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 */
	protected static XSSFWorkbook createWBOneClass(Class<?> clazz,List<?> datas,String sheetName) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, ClassNotFoundException {
		//建立新XSSFWorkbook对象
		XSSFWorkbook wb = new XSSFWorkbook();
		//表头样式
		XSSFCellStyle style_header = getHeaderCellStyle(wb);
		/**存储各数据列的样式*/
		Map<String,XSSFCellStyle> dataCellStyles = getDataCellStyles(wb);
		//建立新的sheet对象
		XSSFSheet sheet = wb.createSheet(sheetName);
		parseExcelAnnotationToExcel(wb,sheet,style_header,dataCellStyles,clazz,datas);
		return wb;
	}
	/**
	 * 利用Excel注解和反射方式，解析生成excel
	 * @see Excel
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
	protected static void parseExcelAnnotationToExcel(XSSFWorkbook wb,XSSFSheet sheet,
			XSSFCellStyle headerCellStyle,Map<String, XSSFCellStyle> dataCellStyles, Class<?> clazz,
			List<?> datas) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		//创建绘图对象   
		XSSFDrawing p = sheet.createDrawingPatriarch(); 
		//设置表格默认列宽度为30个字节
		sheet.setDefaultColumnWidth((short) 30);
		sheet.setDefaultRowHeight((short)500);
		XSSFRow row = sheet.createRow(0);//建立第一行，表头
		Field[] fields = clazz.getDeclaredFields();
		short colindex = 0;
		for (Field field : fields) {
			Excel ann = field.getAnnotation(Excel.class);
			if(ann!=null){
				//列名称
				String name = ann.name();
				//列注解
				String postil = ann.postil();
				XSSFCell cell = row.createCell(colindex);
				cell.setCellStyle(headerCellStyle);
				cell.setCellValue(new XSSFRichTextString(name));
				if(!postil.trim().equals("")){
					//获取批注对象   
					//(int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2, int row2)   
					//前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.   
					XSSFComment comment=p.createCellComment(new XSSFClientAnchor(0,0,0,0,(short)3,3,(short)4,8)); 
					comment.setAuthor("system");
					comment.setString(new XSSFRichTextString(postil));
					cell.setCellComment(comment);
				}
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
				short colindex2 = 0;
				for (Field field : fields) {
					Excel ann = field.getAnnotation(Excel.class);
					if(ann!=null){
						Align align = ann.align();
//						short excelalign = Align.center;
//						switch (align) {
//						case center:
//							excelalign = Align.center;
//							break;
//						case left:
//							excelalign = Align.left;
//							break;
//						case right:
//							excelalign = Align.right;
//							break;
//						default:
//							break;
//						}
//						
						Format format = ann.format();
//						String exceltype = Format.TEXT;
//						switch (format) {
//						case AMT:
//							exceltype = Format.AMT;
//							break;
//						case NUMBER:
//							exceltype = Format.NUMBER;
//							break;
//						case RATE:
//							exceltype = ExcelColumn.TYPE_RATE;
//							break;
//						default:
//							break;
//						}
						String propertyName = field.getName();
						String methodName = "get"+propertyName.substring(0, 1).toUpperCase()+propertyName.substring(1);
						Method method = clazz.getMethod(methodName);
						Object val = method.invoke(obj);
						String[] escapes = ann.escape();
						String value = "";
						if(val!=null){
							value = val.toString();
							if(Date.class==val.getClass()){
								switch (format) {
								case DATE:
									value = new SimpleDateFormat("yyyy-MM-dd").format(val);
									break;
								case TIME:
									value = new SimpleDateFormat("HH:mm:ss").format(val);
									break;
								default:
									value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(val);
									break;
								}
							}
							if(escapes.length>0){
								for (String escape : escapes) {
									if(escape.startsWith(value+":")){
										value = escape.replaceFirst(value+":", "");
										break;
									}
								}
							}
						}

						XSSFCell cell = row.createCell(colindex2);
						setDataCellProperties(cell, wb, value,dataCellStyles,new ExcelColumn(colindex2, propertyName, format, align),false);
						colindex2++;
					}
				}
			}	
		}else{
			log.info("传入的数据集为null或者为空！");
		}
	}
	/**
	 * 利用Excel注解和反射方式，创建excel，生成多个sheet
	 * @param data Map数据集只能包含3条数据  
	 * Map中的数据格式必须为：
	 * 1.key：clazz,value:目标类型Class;
	 * 2.key:datas,value:目标类型数据集合;
	 * 3.key:sheetName,value:excel的sheet名称
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("rawtypes")
	protected static XSSFWorkbook createWBClasses(List<Map<String,Object>> data) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, ClassNotFoundException {
		//建立新XSSFWorkbook对象
		XSSFWorkbook wb = new XSSFWorkbook();
		//表头样式
		XSSFCellStyle style_header = getHeaderCellStyle(wb);
		/**存储各数据列的样式*/
		Map<String,XSSFCellStyle> dataCellStyles = getDataCellStyles(wb);
		for (Map<String, Object> map : data) {
			Class<?> clazz = (Class) map.get("clazz");
			List<?> datas = (List<?>) map.get("datas");
			String sheetName = (String) map.get("sheetName");
			//建立新的sheet对象
			XSSFSheet sheet = wb.createSheet(sheetName);
			parseExcelAnnotationToExcel(wb, sheet, style_header, dataCellStyles, clazz, datas);
		}
		return wb;
	}
	/**
	 * 利用Excel注解和反射方式,在服务器上生成文件,生成单个sheet
	 * @param clazz 数据实体类型
	 * @param datas excel数据集
	 * @param excelName 文件名称
	 * @param path 文件放置的目录全路径，不要以"\"结尾
	 */
	public static String writeExcelOneClass(Class<?> clazz,List<?> datas,String excelName,String path) {
		String pathname = path+File.separatorChar+excelName+".xls";
		File baseFile = new File(path);
		if(!baseFile.exists()){
			baseFile.mkdirs();
		}
		try{
			XSSFWorkbook wb = createWBOneClass(clazz,datas,excelName);
			OutputStream out = new FileOutputStream(pathname);
			wb.write(out);
			out.flush();
			out.close();
			return pathname;
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}

	/**
	 * 利用Excel注解和反射方式,不在服务器上生成文件,导出单个实体类的数据，生成单个sheet
	 * @see Excel
	 * @param clazz 数据实体类型
	 * @param datas excel数据集
	 * @param excelName 文件名称
	 * @param response
	 */
	public static void writeExcelOneClass(Class<?> clazz,List<?> datas,String excelName,HttpServletResponse response) {
		try{
			XSSFWorkbook wb = createWBOneClass(clazz,datas,excelName);
			writeXSSFWorkbook(wb, response, excelName);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	/**
	 * 不在服务器上生成文件,导出多个实体类的数据，生成多个sheet,一个实体类对应一个sheet
	 * @param data Map数据集只能包含3条数据  
	 * Map中的数据格式必须为，key：clazz,value:对象类型Class;key:datas,value:list集合;key:sheetName,value:excel的sheet名称
	 * @param excelName excel文件名称
	 * @param response
	 */
	public static void writeExcelClasses(List<Map<String,Object>> data,String excelName,HttpServletResponse response) {
		try{
			XSSFWorkbook wb = createWBClasses(data);
			writeXSSFWorkbook(wb, response, excelName);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	/**
	 * 根据注解Excel解析excel文件
	 * @see Excel
	 * @param clazz  此类的字段中需包含Excel注解
	 * @param input
	 * @param checkClass 检查excel内容的类 可传null,则不检查,检查类的检查方法格式如下:1.static静态;2.返回类型String;3.方法名称必须是check+导入的实体类名称+属性字段名称(首字母大写);4.方法的参数(String,int)(检查的值,行号)
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws InvocationTargetException
	 */
	public static List<Object> readExcel(Class<?> clazz,InputStream input,Class<?> checkClass) throws InstantiationException, IllegalAccessException, IOException, InvocationTargetException{
		return readExcelByAnnotation(clazz, input,getExcelFields(clazz),getExcelNormName(clazz),checkClass);
	}

	/**
	 * 根据实体类的注解解析excel文件
	 * @param clazz  解析生成的实体类型
	 * @param input  excel文件输入流
	 * @param fields 标准的列集合
	 * @param normNames 标准的列名称 key：字段java名称，value：字段中文名称
	 * @param checkClass 检查excel内容的类 可传null,则不检查
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 * @throws InvocationTargetException 
	 */
	protected static List<Object> readExcelByAnnotation(Class<?> clazz,InputStream input, List<Field> fields,Map<String,String> normNames,Class<?> checkClass) throws InstantiationException, IllegalAccessException, IOException, InvocationTargetException{
		List<Object> objs=new ArrayList<Object>();	
		String colnameMessage = "";//记录excel列名称错误信息
		String contentMessage = "";//记录excel内容错误信息
		if(null!=input){
			XSSFWorkbook wb = new XSSFWorkbook(input);
			//得到第一个Sheet
			XSSFSheet st = wb.getSheetAt(0);
			//总共有多少行
			int rows= st.getPhysicalNumberOfRows();
			//第一行
			XSSFRow rootRow = st.getRow(0);
			if(rootRow==null){
				wb.close();
				throw new RuntimeException("文件内容布局必须从第一行开始！");
			}
			//第一列
			XSSFCell rootCell = rootRow.getCell(0);
			if(rootCell==null){
				wb.close();
				throw new RuntimeException("文件内容布局必须从第一列开始！");
			}
			//总共有多少列
			int cells = rootRow.getPhysicalNumberOfCells();
			int size = fields.size();//excel文件应该有多少列
			//判断导入的excel列数量是否正确
			if(cells>size){
				wb.close();
				throw new RuntimeException("列数量不是标准的【"+size+"】列,多了【"+(cells-size)+"】列;");
			}
			if(cells<size){
				wb.close();
				throw new RuntimeException("列数量不是标准的【"+size+"】列,少了【"+(size-cells)+"】列;");
			}
			//检查列名称是否正确
			for (int i = 0; i < size; i++) {
				Field field = fields.get(i);
				XSSFCell cell = rootRow.getCell(i);
				String excelValue = getValue(cell);//导入的excel文件的列名称
				String normName = normNames.get(field.getName());//标准列名称
				if(!normName.equals(excelValue)){
					colnameMessage = colnameMessage + "第"+(i+1)+"列名称【"+excelValue+"】不是标准名称【"+normName+"】;<br>";
				}
			}
			if(!colnameMessage.equals("")){
				wb.close();
				throw new RuntimeException(colnameMessage);
			}
			if(rows==1){
				wb.close();
				throw new RuntimeException("excel文件无实际数据！");
			}
			//循环得到Excel里的数据（从第二行数据库开始，第一行是表头）
			for(int i=1;i<rows;i++){
				Object obj = clazz.newInstance();
				XSSFRow row = st.getRow(i);
				if(row==null){
					continue;
				}
				for (int j = 0; j < size; j++) {
					Field field = fields.get(j);
					XSSFCell cell = row.getCell(j);
					contentMessage = setValue(field,cell,obj,contentMessage,i,checkClass);
				}

				objs.add(obj);
			}
			wb.close();
		}
		contentMessage = contentMessage.trim();
		if(contentMessage.equals("")){
			return objs;
		}else{
			throw new RuntimeException(contentMessage);
		}
	}
	/**
	 * 获取列的标准名称
	 * @param clazz
	 * @return
	 */
	protected static Map<String,String> getExcelNormName(Class<?> clazz) {
		Map<String,String> names = new HashMap<String, String>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Excel obj = field.getAnnotation(Excel.class);
			if(obj!=null){
				names.put(field.getName(), obj.name());
			}
		}
		return names;
	}
	/**
	 * 给对象属性赋值
	 * @param field 目标属性
	 * @param cell excel单元格对象
	 * @param obj 实体对象
	 * @param i 行索引
	 * @param checkClass 检查excel内容的类 可传null,则不检查
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	protected static String setValue(Field field, XSSFCell cell, Object obj, String message, int i,Class<?> checkClass) throws IllegalAccessException, InvocationTargetException {
		Class<?> type = field.getType();
		String propertyName = field.getName();
		if(type==Date.class){						
			Date date = cell.getDateCellValue();
			BeanUtils.copyProperty(obj, propertyName, date);
		}
		String value = getValue(cell);
		Class<? extends Object> clazz1 = obj.getClass();
		String simpleName = clazz1.getSimpleName();
		if(checkClass!=null){
			try {
				String checkMethodName = "check"+simpleName+propertyName.substring(0, 1).toUpperCase()+propertyName.substring(1);
				Method m = checkClass.getMethod(checkMethodName,String.class,int.class);
				String invoke = (String) m.invoke(null, value,i+1);
				message+=invoke;
			} catch (Exception e) {
			}
		}
		if(type==int.class||type==Integer.class){
			int intValue = Integer.parseInt(value);
			BeanUtils.copyProperty(obj, propertyName, intValue);
		}else if(type==double.class||type==Double.class){
			double doubleValue = Double.parseDouble(value);
			BeanUtils.copyProperty(obj, propertyName, doubleValue);
		}else if(type==float.class||type==Float.class){
			float floatValue = Float.parseFloat(value);
			BeanUtils.copyProperty(obj, propertyName, floatValue);
		}else if(type==long.class||type==Long.class){
			long longValue = Long.parseLong(value);
			BeanUtils.copyProperty(obj, propertyName, longValue);
		}else if(type==String.class){
			BeanUtils.copyProperty(obj, propertyName, value);
		}else if(type==boolean.class||type==Boolean.class){
			boolean booleanValue = Boolean.parseBoolean(value.toLowerCase());
			BeanUtils.copyProperty(obj, propertyName, booleanValue);
		}
		return message;
	}
	/**
	 * 获得excel应有的列字段
	 * @param clazz
	 * @param annotationname 注解名称
	 * @return
	 */
	protected static List<Field> getExcelFields(Class<?> clazz) {
		List<Field> fs = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Excel obj = field.getAnnotation(Excel.class);
			if(obj!=null){
				fs.add(field);
			}
		}
		return fs;
	}
	public static void main(String[] args) throws Exception {
		ExcelModel em = new ExcelModel();
		em.setTitle("测试excel");
		List<String[]> headers = new ArrayList<>();
		headers.add(new String[]{"小三","小四小五","小四小五","小六"});
		headers.add(new String[]{"小三","小四","小五","小六"});
		em.setHeaders(headers);
		List<ExcelColumn> cols = new ArrayList<>();
		cols.add(new ExcelColumn(0, "san"));
		cols.add(new ExcelColumn(1, "si",Format.NUMBER,Align.left));
		cols.add(new ExcelColumn(2, "wu",Format.AMT,Align.right));
		cols.add(new ExcelColumn(3, "liu",Format.RATE,Align.left));
		em.setCols(cols );
		List<Map<String, Object>> datas = new ArrayList<>();
		Map<String,Object> d1 = new HashMap<>();
		d1.put("san", "50");
		d1.put("si", "$50￥0,00¥000");
		d1.put("wu", "￥500000");
		d1.put("liu", "50");
		datas.add(d1);
		em.setDatas(datas );
		List<ExcelModel> ems = new ArrayList<>();
		ems.add(em);
		writeExcelOneSheet(ems , "xiaoping", "xiaopng", "E:\\xiaoping");
	}
}
