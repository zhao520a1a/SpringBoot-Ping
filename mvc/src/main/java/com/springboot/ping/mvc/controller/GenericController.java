package com.springboot.ping.mvc.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springboot.ping.common.enums.Format;
import com.springboot.ping.common.util.StringUtil;
import com.springboot.ping.mvc.PingMvcProperties;
import com.springboot.ping.mvc.annotation.Grid;
import com.springboot.ping.mvc.annotation.GridField;
import com.springboot.ping.mvc.meta.GridModelMeta;
import com.springboot.ping.mvc.meta.GridPageModel;
import com.springboot.ping.mvc.meta.GridPropertyInfo;
import com.springboot.ping.mvc.meta.PageDetailProperty;
import com.springboot.ping.mvc.meta.PageTemplate;
import com.springboot.ping.mvc.service.GenericService;
import com.springboot.ping.mvc.util.GridExcelUtil;
import com.springboot.ping.mvc.vo.ResponseVO;
import com.springboot.ping.mybatis.enums.DbType;
import com.springboot.ping.mybatis.enums.Operator;
import com.springboot.ping.mybatis.meta.BaseMeta;
import com.springboot.ping.mybatis.meta.BeanInfo;
import com.springboot.ping.mybatis.vo.Condition;
import com.springboot.ping.mybatis.vo.Pagination;

@Controller
@RequestMapping(value="/{module}/{entity}")
public class GenericController extends BaseController{
	private static Logger log = LoggerFactory.getLogger(GenericController.class);
	@Autowired
	private GenericService genericService;
	@Autowired
	private PageTemplate pageTemplate;
	@Autowired
	private PingMvcProperties pingMvcProperties;
	
	/**固定参数变量*/
	/**toolbar按钮打开方式的参数*/
	private final String param_open = "open";
	/**排序方式，asc desc*/
	private final String param_order = "order";
	/**排序字段*/
	private final String param_sort = "sort";
	/**
	 * 转向到list页面
	 * @param entity 实体短名称
	 * @return
	 */
	@RequestMapping(method={RequestMethod.POST,RequestMethod.GET})
	public String list(@PathVariable("module")String module
			,@PathVariable("entity")String entity
			,ModelMap modelMap
			,HttpServletRequest request){
		BeanInfo beanInfo = getBeanInfo(entity);
		modelMap.addAttribute("page", GridPageModel.getPageListModel(beanInfo,module, pageTemplate, request));
		return this.pingMvcProperties.getListTemplatePath();
	}

	private BeanInfo getBeanInfo(String entity){
		BeanInfo beanInfo = BaseMeta.getBeanInfo(entity);
		if(beanInfo==null){
			throw new RuntimeException("entity:"+entity+"不存在！");
		}
		return beanInfo;
	}
	/**
	 * 分页查询数据
	 * @param entity 实体短名称
	 * @return
	 */
	@RequestMapping(value="/queryPage")
	@ResponseBody
	public Object queryPage(@PathVariable("module")String module
			,@PathVariable("entity")String entity
			,HttpServletRequest request){
		BeanInfo beanInfo = this.getBeanInfo(entity);
		ParamsVO params = this.parseParams(beanInfo.getClazz(),request);
		List<Condition> conditions = params.getConditions();
		List<Map<String,Object>> rows = this.formatData(beanInfo.getClazz(),this.genericService.findByConditions(beanInfo.getEntityName(), new Pagination(), conditions,params.getOrderby()));
		return this.pageTemplate.getDataGrid(rows);

	}
	/**
	 * 转换成list map，并格式化数据
	 * @param clazz java实体bean
	 * @param rows
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List<Map<String, Object>> formatData(Class<?> clazz,List rows) {
		List<GridPropertyInfo> propertyinfos = GridModelMeta.getProperties(clazz);
		List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();
		for (Object obj : rows) {
			if(obj==null){
				continue;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			try {
				for (GridPropertyInfo pi : propertyinfos) {
					String propertyName = pi.getPropertyName();
					Method readMethod = pi.getReadMethod();
					GridField gf = pi.getGridField();
					Object value = readMethod.invoke(obj);
					if(gf!=null){
						value = formatValue(value,pi);
					}
					data.put(propertyName, value);
				}
			} catch (Exception e) {
				log.error(StringUtil.getTrace(e));
			}
			datas.add(data);
		}
		return datas;
	}
	/**
	 * 格式单个字段的值
	 * @param value
	 * @param pi
	 * @return
	 */
	private Object formatValue(Object value, GridPropertyInfo pi) {
		if(value==null){
			return "";
		}
		GridField gf = pi.getGridField();
		Format format = gf.format();
		DbType dbType = pi.getDbType();
		if(dbType.toString().equals(DbType.DATE.toString())
				&&!format.equals(Format.DATE.toString())
				&&!format.equals(Format.DATETIME.toString())
				&&!format.equals(Format.TIME.toString())){
			format = Format.DATETIME;;
		}
		com.springboot.ping.mvc.annotation.Dic dic = gf.dic();
		String group = dic.group();
		value = StringUtil.formatData(value, format);
		if(StringUtil.isNotEmpty(group)&&gf.dicFomart()){
			//			value = value+" "+DicUtil.getDicValue(group, String.valueOf(value));
		}
		return value;
	}
	/**
	 * 明细页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/detail")
	public String detail(@PathVariable("module")String module
			,@PathVariable("entity")String entity
			,ModelMap modelMap
			,HttpServletRequest request){
		Class<?> clazz = this.getBeanInfo(entity).getClazz();
		List<Condition> conditions = this.parsePkParams(clazz ,request);
		Object obj = this.genericService.findByPk(entity, conditions);
		List<PageDetailProperty> pageDetailPropertys = new ArrayList<>();
		String[] openValues = request.getParameterValues(this.param_open);
		String open="";
		if(openValues!=null&&openValues.length>0){
			open=openValues[0];
		}
		if(obj!=null){
			List<GridPropertyInfo> propertyinfos = GridModelMeta.getProperties(clazz);
			try {
				for (GridPropertyInfo pi : propertyinfos) {
					Method readMethod = pi.getReadMethod();
					Object value = readMethod.invoke(obj);
					GridField gf = pi.getGridField();
					if(gf==null){
						continue;
					}
					boolean detail = gf.detail();
					if(detail){
						value = this.formatValue(value, pi);
						pageDetailPropertys.add(new PageDetailProperty(pi.getPropertyName(), gf.name(), value.toString()));
					}
				}
			} catch (Exception e) {
				log.error(StringUtil.getTrace(e));
			}
		}
		modelMap.addAttribute("deail", this.pageTemplate.getDetailHtml(pageDetailPropertys, open));
		return this.pingMvcProperties.getDetailTemplatePath();
	}
	/**
	 * 获取主键的条件信息
	 * @param clazz java实体bean
	 * @param request
	 * @return
	 */
	private List<Condition> parsePkParams(Class<?> clazz, HttpServletRequest request) {
		List<Condition> conditions = new ArrayList<Condition>();
		Map<String, String[]> parameterMap = request.getParameterMap();
		List<GridPropertyInfo> propertyinfos = GridModelMeta.getProperties(clazz);
		for (Iterator<Entry<String, String[]>> it = parameterMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String[]> entry = it.next();
			String key = entry.getKey();
			boolean isPk = false;
			String dbType = null;
			for (GridPropertyInfo pi : propertyinfos) {
				String propertyName = pi.getPropertyName();
				if(key.equals(propertyName)){
					dbType = pi.getDbType().toString();
					isPk = true;
				}
			}
			if(!isPk){
				continue;
			}
			String[] vals = entry.getValue();
			String tempval = (StringUtil.isNotEmpty(vals[0]))?vals[0].trim():null;
			if(tempval!=null){
				String field_dbtype_operator = key+this.separator+dbType+this.separator+Operator.EQ.toString();
				conditions.add(Condition.getCondition(field_dbtype_operator, tempval,clazz));
			}
		}
		return conditions;
	}
	/**
	 * 删除一条记录
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/delete")
	@ResponseBody
	public ResponseVO delete(@PathVariable("module")String module
			,@PathVariable("entity")String entity
			,HttpServletRequest request){
		BeanInfo beanInfo = this.getBeanInfo(entity);
		List<Condition> conditions = this.parsePkParams(beanInfo.getClazz(),request);
		this.genericService.deleteBatch(entity, conditions);
		return new ResponseVO("删除成功!");
	}
	/**
	 * 批量删除
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/deleteBatch")
	@ResponseBody
	public ResponseVO deleteBatch(@PathVariable("module")String module
			,@PathVariable("entity")String entity
			,String keys
			,HttpServletRequest request){
		BeanInfo beanInfo = this.getBeanInfo(entity);
		List<GridPropertyInfo> propertyinfos = GridModelMeta.getProperties(beanInfo.getClazz());
		List<Map<String, String>> keyObjs = this.getKeyObjs(keys);
		for (Map<String, String> map : keyObjs) {
			List<Condition> conditions = new ArrayList<Condition>();
			for (Iterator<Map.Entry<String, String>> it = map.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
				String key = entry.getKey();
				boolean isPk = false;
				String dbType = null;
				for (GridPropertyInfo pi : propertyinfos) {
					String propertyName = pi.getPropertyName();
					if(key.equals(propertyName)){
						dbType = pi.getDbType().toString();
						isPk = true;
					}
				}
				if(!isPk){
					continue;
				}
				String value = entry.getValue();
				if(value!=null){
					String field_dbtype_operator = key+this.separator+dbType+this.separator+Operator.EQ.toString();
					conditions.add(Condition.getCondition(field_dbtype_operator, value,beanInfo.getClazz()));
				}
			}
			this.genericService.deleteBatch(entity, conditions);
		}
		return new ResponseVO("批量删除成功!");
	}
	/**
	 * 将查询条件参数解析成Condition类型，并且获取order by的字段想字段信息
	 * @param request
	 * @return 
	 */
	private ParamsVO parseParams(Class<?> entityClass,HttpServletRequest request) {
		ParamsVO pv = new ParamsVO();
		pv.setConditions(this.parseParamsToCondition(entityClass ,request));

		String[] order = request.getParameterValues(param_order);
		String[] sort = request.getParameterValues(param_sort);
		if(sort!=null&&sort.length>0&&StringUtil.isNotEmpty(sort[0].toString())){
			String[] sorts = sort[0].toString().split(",");
			String[] orders = order[0].toString().split(",");
			int len = sorts.length;
			String orderby = "";
			for (int i = 0; i < len; i++) {
				orderby = orderby+","+sorts[i]+" "+orders[i];
			}
			orderby=orderby.replaceFirst(",", "");
			if(StringUtil.isNotEmpty(orderby)){
				pv.setOrderby(" order by "+orderby);
			}
		}
		return pv;
	}
	class ParamsVO{
		/**查询条件*/
		private List<Condition> conditions;
		/**排序字符串，比如 order by id desc*/
		private String orderby;
		public List<Condition> getConditions() {
			return conditions;
		}
		public void setConditions(List<Condition> conditions) {
			this.conditions = conditions;
		}
		public String getOrderby() {
			return orderby;
		}
		public void setOrderby(String orderby) {
			this.orderby = orderby;
		}

	}
	/**
	 * excel导出，会对导出的数据根据注解格式化和转义字典数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/excel")
	@ResponseBody
	public ResponseVO excel(@PathVariable("module")String module
			,@PathVariable("entity")String entity
			,HttpServletRequest request
			,HttpServletResponse response){
		return exportExcel(entity, request, response, true);
	}
	private ResponseVO exportExcel(String entity,HttpServletRequest request
			,HttpServletResponse response,boolean isFormat){
		BeanInfo beanInfo = this.getBeanInfo(entity);
		Class<?> clazz = beanInfo.getClazz();
		ParamsVO params = this.parseParams(clazz,request);
		List<Condition> conditions = params.getConditions();
		Grid grid = clazz.getAnnotation(Grid.class);
		String excelName = StringUtil.isEmpty(grid.title(), entity);
		long count = this.genericService.count(entity, conditions);
		long maxExcelNumber = this.pingMvcProperties.getMaxExcelNumber();
		if(count>maxExcelNumber){
			return new ResponseVO("此次导出的记录数已超过["+maxExcelNumber+"],请分批导出!");
		}
		List<?> datas = this.genericService.findByConditions(entity, null, conditions,params.getOrderby());
		if(isFormat){
			GridExcelUtil.writeGridFieldOneClass(clazz, datas, excelName, response);
		}else{
			GridExcelUtil.writeGridFieldOneClassNoFormat(clazz, datas, excelName, response);
		}
		return new ResponseVO("导出成功!");
	}
	/**
	 * excel导出，不会对导出的数据根据注解格式化和转义字典数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/excelNoFormat")
	@ResponseBody
	public ResponseVO excelNoFormat(@PathVariable("module")String module
			,@PathVariable("entity")String entity
			,HttpServletRequest request
			,HttpServletResponse response){
		return this.exportExcel(entity, request, response, false);
	}
	//	/**
	//	 * 查询某个字典组的数据
	//	 * @param group
	//	 * @return
	//	 */
	//	@RequestMapping("/queryDics")
	//	@ResponseBody
	//	public List<Dic> queryDics(String group){
	//		Dic dic = new Dic(group,"","全部");
	//		List<Dic> dics = DicUtil.getListDics(group);
	//		dics.add(0, dic);
	//		return dics;
	//	}

}
