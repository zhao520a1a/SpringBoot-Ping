package com.springboot.ping.generic.controller;

import com.springboot.ping.generic.service.GenericService;
import com.springboot.ping.generic.vo.DataGrid;
import com.springboot.ping.mybatis.enums.DbType;
import com.springboot.ping.mybatis.enums.Operator;
import com.springboot.ping.mybatis.extend.entity.BaseModel;
import com.springboot.ping.mybatis.meta.BaseMeta;
import com.springboot.ping.mybatis.meta.PropertyInfo;
import com.springboot.ping.mybatis.vo.Condition;
import com.springboot.ping.mybatis.vo.Page;
import com.springboot.ping.mybatis.vo.Pagination;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * url必须RESTful风格,继承此类的controller必须在类上声明RequestMapping的value或者path值,例如@RequestMapping("/api/user")
 * <br>支持分页查询、查询所有、单一主键查询
 * <br>分页查询1,url后两层为/当前页数/每页大小,例如/api/user/1/10?username|string|like=小三&create_time|date|eq=2016-01-01,request请求中参数的名称必须符合condition格式:"字段名称|类型|比较符",例如id|int|eq
 * <br>分页查询2,例如/api/user/list?rows=10&page=1&username|string|like=小三&create_time|date|eq=2016-01-01,request请求中参数的名称必须符合condition格式:"字段名称|类型|比较符",例如id|int|eq
 * <br>查询所有,例如/api/user?username|string|like=小三&create_time|date|eq=2016-01-01,request请求中参数的名称必须符合condition格式:"字段名称|类型|比较符",例如id|int|eq
 * <br>单一主键查询,例如/api/user/1   ，其中1就是主键
 * @author 刘江平 2017年1月7日下午4:32:21
 *
 * @param <T>
 */
public class BaseQueryController<T extends BaseModel> extends BaseController{

	protected final static String apidesc = "查询条件参数名称必须符合condition格式:\"数据库字段名称|类型|比较符\",例如id|int|eq,类型包括:[string,date,int,float],比较符包括:[gt,lt,gteq,lteq,like,notlike,eq,nq,in,notin]";
	@Autowired
	protected GenericService genericService;
	 /**
     * 实体类型
     */
    protected Class<T> entityClass;
    
    @SuppressWarnings("unchecked")
	@PostConstruct
    public void init(){
    	Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class<T>) params[0];
    }
    /**
     * 分页查询,url后两层为/当前页数/每页大小,例如/api/user/1/10?username|string|like=小三&create_time|date|eq=2016-01-01
     * <br>request请求中参数的名称必须符合condition格式:"字段名称|类型|比较符",例如id|int|eq
     * @param page 当前页数
     * @param pageSize 每页大小
     * @param request 
     * @see Condition
     * @see DbType
     * @see Operator
     * @return
     */
	@ApiOperation(value = "分页查询",notes = apidesc+"<br>url示例:/api/user/1/10?username|string|like=小三&create_time|date|eq=2016-01-01",produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "page",value = "当前页数",required = true,dataType = "int"),
			@ApiImplicitParam(name = "pageSize",value = "每页大小",required = true,dataType = "int")
	})
	@GetMapping(value = "/{page}/{pageSize}")
	public DataGrid queryPage(@PathVariable int page,@PathVariable int pageSize,HttpServletRequest request){
		Page<T> dg = this.genericService.findPagination(entityClass, new Pagination(pageSize, page), this.parseParamsToCondition(entityClass, request), "");
		return new DataGrid(page, dg.getTotalCount(), dg.getTotalPages(), dg.getRows());
	}
	
	/**
     * 分页查询,例如/api/user/list?rows=10&page=1&username|string|like=小三&create_time|date|eq=2016-01-01
     * <br>request请求中参数的名称必须符合condition格式:"字段名称|类型|比较符",例如id|int|eq
     * @param page 当前页数
     * @param rows 每页大小
     * @param request 
     * @see Condition
     * @see DbType
     * @see Operator
     * @return
     */
	@ApiOperation(value = "分页查询",notes = apidesc+"<br>url示例:/api/user/list?rows=10&page=1&username|string|like=小三&create_time|date|eq=2016-01-01",produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "page",value = "当前页数",required = true,dataType = "int"),
			@ApiImplicitParam(name = "rows",value = "每页大小",required = true,dataType = "int")
	})
	@GetMapping(value = "/list")
	public DataGrid queryPage2(int page,int rows,HttpServletRequest request){
		Page<T> dg = this.genericService.findPagination(entityClass, new Pagination(rows, page), this.parseParamsToCondition(entityClass, request), "");
		return new DataGrid(page, dg.getTotalCount(), dg.getTotalPages(), dg.getRows());
	}
	
	/**
	 * 查询所有,例如/api/user?username|string|like=小三&create_time|date|eq=2016-01-01
	 * <br>request请求中参数的名称必须符合condition格式:"字段名称|类型|比较符",例如id|int|eq
	 * @param request
	 * @see Condition
	 * @return
	 */
	@ApiOperation(value = "查询列表",notes = apidesc+"<br>url示例:/api/user?username|string|like=小三&create_time|date|eq=2016-01-01",produces = MediaType.APPLICATION_JSON_VALUE)
	@GetMapping(value = "")
	public List<T> queryAll(HttpServletRequest request){
		return this.genericService.findAll(entityClass,this.parseParamsToCondition(entityClass, request),"");
	}
	/**
	 * 根据单一主键查询一条记录,例如/api/user/1 ，其中1就是主键
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "单一主键查询",notes = "根据唯一键查询记录,url示例:/api/user/1 ",produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "id",value = "主键",required = true,dataType = "string")
	})
	@GetMapping(value = "/{id}")
	public T query(@PathVariable String id){
		List<PropertyInfo> pkPropertyInfos = BaseMeta.getBeanInfo(entityClass).getPkPropertyInfos();
		if(pkPropertyInfos.size()==0){
			throw new RuntimeException("这个实体类"+this.entityClass+"没有主键!");
		}
		if(pkPropertyInfos.size()>1){
			throw new RuntimeException("这个实体类"+this.entityClass+"是联合主键!");
		}
		PropertyInfo pk = pkPropertyInfos.get(0);
		return this.genericService.findByPk(entityClass, new Condition(pk.getDbColumnName(), pk.getDbType(), Operator.EQ, id));
	}
	
}
