package com.springboot.ping.generic.service;

import com.springboot.ping.common.util.CollectionUtil;
import com.springboot.ping.mybatis.extend.dao.BaseCURDDao;
import com.springboot.ping.mybatis.extend.dao.BaseQueryDao;
import com.springboot.ping.mybatis.meta.BaseMeta;
import com.springboot.ping.mybatis.meta.PropertyInfo;
import com.springboot.ping.mybatis.vo.Condition;
import com.springboot.ping.mybatis.vo.Page;
import com.springboot.ping.mybatis.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的service服务
 * @author 刘江平 2017年1月13日下午10:41:11
 *
 */
public class GenericService{
	private static final Logger logger = LoggerFactory.getLogger(GenericService.class);
	
	/**
	 * 获得某个实体类对应的dao,此dao必须是支持query操作
	 * @param simpleClassName 实体类的简称
	 * @return
	 */
	private BaseQueryDao<?> getBaseQueryDao(String simpleClassName){
		simpleClassName = simpleClassName.substring(0, 1).toLowerCase()+simpleClassName.substring(1);
		return SpringContext.getBean(simpleClassName+"Dao", BaseQueryDao.class);
	}
	
	/**
	 * 获得某个实体类对应的dao,此dao必须是可支持crud操作
	 * @param simpleClassName 实体类的简称
	 * @return
	 */
	private BaseCURDDao<?> getBaseCRUDDao(String simpleClassName){
		simpleClassName = simpleClassName.substring(0, 1).toLowerCase()+simpleClassName.substring(1);
		return SpringContext.getBean(simpleClassName+"Dao", BaseCURDDao.class);
	}
	
	/**
	 * 根据主键删除表记录,只支持单一主键
	 * @param simpleClassName 实体类简称
	 * @param pkCondition 主键条件
	 */
	@Transactional
	public void deleteByPk(String simpleClassName, Condition pkCondition){
		List<Condition> cons = new ArrayList<Condition>();
		cons.add(pkCondition);
		this.deleteByPk(simpleClassName, cons);
	}
	
	/**
	 * 根据主键删除表记录,只支持单一主键
	 * @param clazz 实体类
	 * @param pkCondition 主键条件
	 */
	@Transactional
	public void deleteByPk(Class<?> clazz, Condition pkCondition){
		this.deleteByPk(clazz.getSimpleName(), pkCondition);
	}
	
	/**
	 * 根据主键删除表记录,可联合主键
	 * @param clazz 实体类
	 * @param pkCondition 主键条件
	 */
	@Transactional
	public void deleteByPk(Class<?> clazz, List<Condition> pkCondition){
		this.deleteByPk(clazz.getSimpleName(), pkCondition);
	}
	/**
	 * 根据主键删除表记录,可联合主键
	 * @param simpleClassName 实体类
	 * @param pkCondition 主键条件
	 */
	@Transactional
	public void deleteByPk(String simpleClassName, List<Condition> pkCondition){
		List<PropertyInfo> pkPropertyInfos = BaseMeta.getBeanInfo(simpleClassName).getPkPropertyInfos();
		int pkSize = pkPropertyInfos.size();
		if(pkSize==0){
			throw new RuntimeException("这个实体类"+simpleClassName+"没有主键!");
		}
		int conSize = pkCondition.size();
		if(pkSize!=conSize){
			throw new RuntimeException("这个实体类"+simpleClassName+"有["+pkSize+"]主键,而参数条件数量为[]"+conSize);
		}
		boolean rightCon = true;
		StringBuilder sb = new StringBuilder("");
		for (PropertyInfo pk : pkPropertyInfos) {
			boolean conpk = false;
			for (Condition con : pkCondition) {
				if(pk.getDbColumnName().equals(con.getColumn())){
					conpk = true;
				}
			}
			if(!conpk){
				sb.append(",").append(pk.getDbColumnName());
				rightCon = false;
			}
		}
		if(!rightCon){
			throw new RuntimeException("主键参数条件:"+pkCondition+",与实体类"+simpleClassName+"主键不匹配,缺少主键"+sb.toString().replaceFirst(",", ""));
		}
		logger.info("根据主键删除实体类{}对应表的数据,条件:{}",simpleClassName,pkCondition);
		this.getBaseCRUDDao(simpleClassName).deleteBatch(pkCondition);
	}
	/**
	 * 按多个条件批量删除
	 * @param simpleClassName 实体类的简称
	 * @param conditions
	 */
	@Transactional
	public void deleteBatch(String simpleClassName, List<Condition> conditions){
		logger.info("批量删除实体类{}对应表的数据,条件:{}",simpleClassName,conditions);
		this.getBaseCRUDDao(simpleClassName).deleteBatch(conditions);
	}
	
	/**
	 * 按多个条件批量删除
	 * @param clazz 实体类的class
	 * @param conditions 条件
	 */
	@Transactional
	public void deleteBatch(Class<?> clazz, List<Condition> conditions){
		this.deleteBatch(clazz.getSimpleName(),conditions);
	}
	
	/**
	 * 按多个主键条件批量删除
	 * @param simpleClassName 实体类的简称
	 * @param pkConditions 主键条件
	 */
	@Transactional
	public void deleteBatchByPks(String simpleClassName, List<List<Condition>> pkConditions){
		logger.info("批量根据主键删除实体类{}对应表的数据,条件:{}",simpleClassName,pkConditions);
		for (List<Condition> pks : pkConditions) {
			this.deleteByPk(simpleClassName, pks);		
		}
	}
	
	/**
	 * 按多个主键条件批量删除
	 * @param clazz 实体类的class
	 * @param pkConditions 主键条件
	 */
	@Transactional
	public void deleteBatchByPks(Class<?> clazz, List<List<Condition>> pkConditions){
		this.deleteBatchByPks(clazz.getSimpleName(),pkConditions);
	}
	/**
	 * 根据主键查询唯一记录,只支持单一主键
	 * @param clazz 实体类的class
	 * @param condition 主键条件
	 * @return
	 */
	public <T>T findByPk(Class<T> clazz, Condition condition) {
		List<Condition> cons = new ArrayList<>();
		cons.add(condition);
		return this.findByPk(clazz, cons);
	}
	/**
	 * 根据主键查询唯一记录,只支持单一主键
	 * @param simpleClassName 实体类简称
	 * @param condition 主键条件
	 * @return
	 */
	public Object findByPk(String simpleClassName, Condition condition) {
		List<Condition> cons = new ArrayList<>();
		cons.add(condition);
		return this.findByPk(simpleClassName, cons);
	}
	/**
	 * 根据主键查询唯一记录,支持联合主键
	 * @param clazz 实体类的class
	 * @param conditions 主键条件
	 * @return
	 */
	public <T>T findByPk(Class<T> clazz, List<Condition> conditions) {
		return (T)this.findByPk(clazz.getSimpleName(), conditions);
	}
	
	/**
	 * 根据主键查询唯一记录,支持联合主键
	 * @param simpleClassName 实体类的简称
	 * @param conditions 主键条件
	 * @return
	 */
	public Object findByPk(String simpleClassName, List<Condition> conditions) {
		List<?> list = this.findByConditions(simpleClassName, null, conditions,null);
		if(CollectionUtil.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
	/**
	 * 根据分页和条件进行查询。多条件查询
	 * @param simpleClassName 实体类的简称
	 * @param pagination
	 * @param conditions
	 * @param orderby 排序字符串，比如" order by id desc",传空，则按类字段注解上的OrderBy排序
	 * @return
	 */
	private List<?> findByConditions(String simpleClassName, Pagination pagination, 
			List<Condition> conditions,String orderby) {
		return this.getBaseQueryDao(simpleClassName).find(pagination, conditions,orderby);
	}
	/**
	 * 分页查询
	 * @param clazz 实体类class
	 * @param pagination 分页对象
	 * @param conditions 条件
	 * @param orderby 排序字符串，比如" order by id desc",传空，则按类字段注解上的OrderBy排序
	 * @return
	 */
	public <T>Page<T> findPagination(Class<T> clazz, Pagination pagination,
			List<Condition> conditions,String orderby){
		return new Page<T>((List<T>)this.findByConditions(clazz.getSimpleName(), pagination, conditions, orderby), pagination);
	}
	/**
	 * 分页查询
	 * @param simpleClassName 实体类的简称
	 * @param pagination 分页对象
	 * @param conditions 条件
	 * @param orderby 排序字符串，比如" order by id desc",传空，则按类字段注解上的OrderBy排序
	 * @return
	 */
	public Page<?> findPagination(String simpleClassName, Pagination pagination, 
			List<Condition> conditions,String orderby){
		return new Page<>(this.findByConditions(simpleClassName, pagination, conditions, orderby), pagination);
	}
	/**
	 * 按条件查找所有的记录
	 * @param simpleClassName 实体类的简称
	 * @param conditions 条件
	 * @param orderby 排序字符串，比如" order by id desc",传空，则按类字段注解上的OrderBy排序
	 * @return
	 */
	public List<?> findAll(String simpleClassName, List<Condition> conditions,String orderby) {
		return this.findByConditions(simpleClassName, null, conditions,orderby);
	}
	
	/**
	 * 按条件查找所有的记录
	 * @param clazz 实体类class
	 * @param conditions 条件
	 * @param orderby 排序字符串，比如" order by id desc",传空，则按类字段注解上的OrderBy排序
	 * @return
	 */
	public <T>List<T> findAll(Class<T> clazz, List<Condition> conditions,String orderby) {
		return (List<T>)this.findByConditions(clazz.getSimpleName(), null, conditions,orderby);
	}
	
	/**
	 * 查找所有的记录
	 * @param simpleClassName 实体类的简称
	 * @param orderby 排序字符串，比如" order by id desc",传空，则按类字段注解上的OrderBy排序
	 * @return
	 */
	public List<?> findAll(String simpleClassName,String orderby) {
		return this.findByConditions(simpleClassName, null, null,orderby);
	}
	
	/**
	 * 查找所有的记录
	 * @param clazz 实体类class
	 * @param orderby 排序字符串，比如" order by id desc",传空，则按类字段注解上的OrderBy排序
	 * @return
	 */
	public <T>List<T> findAll(Class<T> clazz,String orderby) {
		return (List<T>)this.findByConditions(clazz.getSimpleName(), null, null,orderby);
	}
	/**
	 * 按条件获取总记录数
	 * @param simpleClassName
	 * @param conditions
	 * @return
	 */
	public long count(String simpleClassName, List<Condition> conditions){
		return this.getBaseQueryDao(simpleClassName).count(conditions);
	}
	/**
	 * 按条件获取总记录数
	 * @param clazz 实体类class
	 * @param conditions
	 * @return
	 */
	public long count(Class<?> clazz, List<Condition> conditions){
		return this.count(clazz.getSimpleName(), conditions);
	}
}
