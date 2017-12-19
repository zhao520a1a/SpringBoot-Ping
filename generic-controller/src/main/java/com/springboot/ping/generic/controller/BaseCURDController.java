package com.springboot.ping.generic.controller;

import com.springboot.ping.common.util.StringUtil;
import com.springboot.ping.generic.vo.Response;
import com.springboot.ping.mybatis.enums.Operator;
import com.springboot.ping.mybatis.extend.entity.BaseModel;
import com.springboot.ping.mybatis.meta.BaseMeta;
import com.springboot.ping.mybatis.meta.PropertyInfo;
import com.springboot.ping.mybatis.vo.Condition;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 支持curd操作的controller基类,继承了BaseQueryController的所有操作
 * <br>url必须RESTful风格,继承此类的controller必须在类上声明RequestMapping的value或者path值,例如@RequestMapping("/api/user")
 * <br>支持单选删除(表必须是单一主键)、多选删除
 * <br>单选删除(表必须是单一主键):例如/api/user/delete/1，其中1就是主键
 * <br>多选删除:例如/api/user/deleteBatch?pks=[{id:1},{id:2}],请求中必须要有参数pks,参数的值的格式必须是一个json数组,数组里的对象的属性就是{主键字段:值},可联合主键
 * @author 刘江平 2017年1月13日下午10:46:21
 *
 * @param <T>
 * @see BaseQueryController
 */
public class BaseCURDController<T extends BaseModel> extends BaseQueryController<T> {
	/**
	 * 单选删除,支持是单一主键的表,例如/api/user/delete/1，其中1就是主键
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "单一主键删除",notes = "根据唯一键删除记录,url示例:/api/user/delete/1 ",produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "id",value = "主键",required = true,dataType = "string")
	})
	@DeleteMapping(value = "/delete/{id}")
	public Response delete(@PathVariable String id){
		List<PropertyInfo> pkPropertyInfos = BaseMeta.getBeanInfo(entityClass).getPkPropertyInfos();
		if(pkPropertyInfos.size()==0){
			throw new RuntimeException("这个实体类"+this.entityClass+"没有主键!");
		}
		if(pkPropertyInfos.size()>1){
			throw new RuntimeException("这个实体类"+this.entityClass+"是联合主键!");
		}
		PropertyInfo pk = pkPropertyInfos.get(0);
		this.genericService.deleteByPk(entityClass, new Condition(pk.getDbColumnName(), pk.getDbType(), Operator.EQ, id));
		return this.getSuccessResponse("删除成功!");
	}
	/**
	 * 多选删除,请求中必须要有参数pks,参数的值的格式必须是一个json数组,数组里的对象的属性就是{主键字段:值},可联合主键
	 * <br>例如/api/user/deleteBatch?pks=[{id:1},{id:2}]
	 * @param pks
	 * @return
	 */
	@ApiOperation(value = "多选删除",notes = "根据唯一键删除记录,url示例:/api/user/deleteBatch?pks=[{id:1},{id:2}] ",produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "pks",value = "多选的主键信息,参数的值的格式必须是一个json数组,数组里的对象的属性就是{主键字段:值},支持联合主键",required = true,dataType = "string",example = "[{id:1},{id:2}]")
	})
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/deleteBatch")
	public Response deleteBatch(String pks){
		if(!StringUtil.isNotEmpty(pks)){
			throw new RuntimeException("多选删除没有传主键参数[pks],必须是一个json数组,数组里的对象的属性就是{主键字段:值},可联合主键");
		}
		List<Map> pkmap = this.getMultiSelectParams(pks);
		List<PropertyInfo> pkPropertyInfos = BaseMeta.getBeanInfo(entityClass).getPkPropertyInfos();
		if(pkPropertyInfos.size()==0){
			throw new RuntimeException("这个实体类"+this.entityClass+"没有主键!");
		}
		boolean rightPks = true;
		StringBuilder sb = new StringBuilder("");
		for (PropertyInfo pk : pkPropertyInfos) {
			if(!pkmap.get(0).containsKey(pk.getDbColumnName())){
				rightPks = false;
				sb.append(",").append(pk.getDbColumnName());
			}
		}
		if(!rightPks){
			throw new RuntimeException("主键参数pks中缺少主键字段"+sb.toString().replaceFirst(",", ""));
		}
		List<List<Condition>> pkConditions = new ArrayList<>();
		List<Condition> conditions = null;
		for (Map map : pkmap) {
			conditions = new ArrayList<>();
			for (PropertyInfo pk : pkPropertyInfos) {
				conditions.add(new Condition(pk.getDbColumnName(), pk.getDbType(), Operator.EQ, map.get(pk.getDbColumnName())));
			}
			pkConditions.add(conditions);
		}
		this.genericService.deleteBatchByPks(this.entityClass, pkConditions);
		return this.getSuccessResponse("批量删除成功!");
	}
}
