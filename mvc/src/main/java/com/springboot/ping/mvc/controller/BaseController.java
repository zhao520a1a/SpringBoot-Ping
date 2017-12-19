package com.springboot.ping.mvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.springboot.ping.common.util.StringUtil;
import com.springboot.ping.mybatis.enums.DbType;
import com.springboot.ping.mybatis.enums.Operator;
import com.springboot.ping.mybatis.vo.Condition;

@Component
public class BaseController{
	private static Logger log = LoggerFactory.getLogger(BaseController.class);
	/**分隔符*/
	protected final String separator = "|";
	
    /**
     * 多选情况下传过来的参数，转换之后，一个map就是条记录传过来的参数，key字段属性名称
     * @param keys
     * @return
     */
    public List<Map<String,String>> getKeyObjs(String keys){
    	List<Map<String,String>> keyObjs = new ArrayList<Map<String,String>>();
    	if(StringUtil.isNotEmpty(keys)){
    		String[] objs = keys.split("\\},\\{");
    		for (String obj : objs) {
    			obj = obj.replaceAll("\\}", "").replaceAll("\\{", "");
    			String[] sps = obj.split(",");
    			Map<String,String> keyObj = new HashMap<String,String>();
    			for (String s : sps) {
    				String[] ks = s.split(":");
    				keyObj.put(ks[0], ks[1]);
    			}
    			keyObjs.add(keyObj);
    		}
    	}
		return keyObjs;
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
			Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) it.next();
			String key = entry.getKey();
			String reg1 = "^\\w+\\"+this.separator+"[a-zA-Z]+\\"+this.separator+"[a-zA-Z]+$";//userid|int|eq
			if(inKeys(key)){
				continue;
			}
			if(!Pattern.matches(reg1, key)){
				log.debug("传入的参数值["+key+"]格式不正确!正确格式为:字段名称(只能数字、字母或者下划线)、数据库类型"+StringUtil.parseArray(DbType.values())+"和sql操作符"+StringUtil.parseArray(Operator.values())+"，以'"+separator+"'分隔!");
				continue;
			}
			String[] vals = entry.getValue();
			String tempval = (StringUtil.isNotEmpty(vals[0]))?vals[0].trim():null;
			if(tempval!=null){
				String tempkey = key.toLowerCase();
				int dateindex = tempkey.indexOf(this.separator+"date"+this.separator+Operator.EQ.toString());
				if(dateindex>=0){
					int index = key.lastIndexOf(this.separator+Operator.EQ.toString());
					String dateGt = key.substring(0, index)+this.separator+Operator.GTEQ.toString();
					String dateLt = key.substring(0, index)+this.separator+Operator.LTEQ.toString();
					conditions.add(Condition.getCondition(dateGt, tempval,entityClass));
					conditions.add(Condition.getCondition(dateLt, tempval,entityClass));
				}else{
					conditions.add(Condition.getCondition(key, tempval,entityClass));
				}
			}
		}
		return conditions;
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean inKeys(String key) {
		if(!StringUtil.isNotEmpty(key)){
			return true;
		}
		String[] keys = {"entity","rows","page"};
		for (String str : keys) {
			if(str.equals(key)){
				return true;
			}
		}
		return false;
	}
}
