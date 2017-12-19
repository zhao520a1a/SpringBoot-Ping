package com.springboot.ping.generic.controller;

import com.alibaba.fastjson.JSONArray;
import com.springboot.ping.common.util.StringUtil;
import com.springboot.ping.generic.vo.Response;
import com.springboot.ping.mybatis.enums.DbType;
import com.springboot.ping.mybatis.enums.Operator;
import com.springboot.ping.mybatis.vo.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class BaseController {
	private static Logger log = LoggerFactory.getLogger(BaseController.class);
	/**
	 * 操作成功响应前端的提示对象!
	 * @param msg 提示信息
	 * @return
	 */
	protected Response getSuccessResponse(String msg){
		return new Response(true, msg);
	}

	/**
	 * 操作成功响应前端的提示对象!
	 * @param msg 提示信息
	 * @param data 其他数据
     * @return
     */
	protected Response getSuccessResponse(String msg,Map<String,Object> data){
		return new Response(true, msg, data);
	}

	/**
	 * 操作成功响应前端的提示对象!
	 * @param data 数据
	 * @return
	 */
	protected Response getSuccessResponse(Map<String,Object> data){
		return new Response(true, data);
	}

	/**
	 * 操作失败响应前端的提示的对象!
	 * @param msg 提示信息
	 * @return
	 */
	protected Response getFailedResponse(String msg){
		return new Response(false, msg);
	}
	/**
	 * 操作失败响应前端的提示的对象!
	 * @param errors 提示信息
	 * @return
	 */
	protected Response getFailedResponse(Map<String,Object> errors){
		return new Response(false, errors);
	}

	/**
	 * 操作失败响应前端的提示的对象!
	 * @param msg 提示信息
	 * @param data 其他数据
	 * @return
	 */
	protected Response getFailedResponse(String msg,Map<String,Object> data){
		return new Response(false, data);
	}
    /**
     * 多选情况下传过来的参数，转换之后，一个map就是条记录传过来的参数，key字段属性名称
     * @param jsonArrayParams 多选的参数json数组字符串,比如[{"id":1},{"id":2}]
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected List<Map> getMultiSelectParams(String jsonArrayParams){
    	try {
			List<Map> parseArray = JSONArray.parseArray(jsonArrayParams, Map.class);
			return parseArray;
		} catch (Exception e) {
			log.info("多选参数{}的格式不是一个json数组!",jsonArrayParams);
			return new ArrayList<>();
		}
    }
    
    /**
	 * 解析request请求中的查询条件参数，转化成Condition类型
	 * @param request
	 * @return
	 */
	protected List<Condition> parseParamsToCondition(Class<?> entityClass,HttpServletRequest request) {
		List<Condition> conditions = new ArrayList<Condition>();
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (Iterator<Entry<String, String[]>> it = parameterMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String[]> entry = it.next();
			String key = entry.getKey();
			if(!Pattern.matches(Condition.CONDITION_PATTERN, key)){
				log.debug("传入的参数值[{}]格式不正确!正确格式为:字段名称(只能数字、字母或者下划线)、数据库类型{}和sql操作符{},以'{}'分隔!比如id|int|eq",key,StringUtil.parseArray(DbType.values()),StringUtil.parseArray(Operator.values()),Condition.CONDITION_SEPARATOR);
				continue;
			}
			String[] vals = entry.getValue();
			String tempval = (StringUtil.isNotEmpty(vals[0]))?vals[0].trim():null;
			if(tempval!=null){
				String tempkey = key.toLowerCase();
				int dateindex = tempkey.indexOf(Condition.CONDITION_PATTERN+"date"+Condition.CONDITION_PATTERN+Operator.EQ.toString());
				if(dateindex>=0){
					int index = key.lastIndexOf(Condition.CONDITION_PATTERN+Operator.EQ.toString());
					String dateGt = key.substring(0, index)+Condition.CONDITION_PATTERN+Operator.GTEQ.toString();
					String dateLt = key.substring(0, index)+Condition.CONDITION_PATTERN+Operator.LTEQ.toString();
					conditions.add(Condition.getCondition(dateGt, tempval,entityClass));
					conditions.add(Condition.getCondition(dateLt, tempval,entityClass));
				}else{
					conditions.add(Condition.getCondition(key, tempval,entityClass));
				}
			}
		}
		return conditions;
	}
}
