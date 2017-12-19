package com.springboot.ping.mvc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.springboot.ping.common.util.CollectionUtil;
import com.springboot.ping.mvc.SpringContext;
import com.springboot.ping.mybatis.extend.dao.BaseCURDDao;
import com.springboot.ping.mybatis.vo.Condition;
import com.springboot.ping.mybatis.vo.Pagination;

@Service
public class GenericService{
	/**
	 * 获得某个实体类对应的dao
	 * @param simpleClassName 实体类的简称
	 * @return
	 */
	public BaseCURDDao<?> getEntityDao(String simpleClassName){
		simpleClassName = simpleClassName.substring(0, 1).toLowerCase()+simpleClassName.substring(1);
		return SpringContext.getBean(simpleClassName+"Dao", BaseCURDDao.class);
	}
	
	/**
	 * 按多个条件批量删除
	 * @param simpleClassName 实体类的简称
	 * @param conditions
	 */
	public void deleteBatch(String simpleClassName, List<Condition> conditions){
		this.getEntityDao(simpleClassName).deleteBatch(conditions);
	}
	/**
	 * 根据主键查询唯一记录
	 * @param simpleClassName 实体类的简称
	 * @param conditions
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
	public List<?> findByConditions(String simpleClassName, Pagination pagination, List<Condition> conditions,String orderby) {
		return this.getEntityDao(simpleClassName).find(pagination, conditions,orderby);
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
	 * 查找所有的记录
	 * @param simpleClassName 实体类的简称
	 * @param orderby 排序字符串，比如" order by id desc",传空，则按类字段注解上的OrderBy排序
	 * @return
	 */
	public List<?> findAll(String simpleClassName,String orderby) {
		return this.findByConditions(simpleClassName, null, null,orderby);
	}
	/**
	 * 按条件获取总记录数
	 * @param simpleClassName
	 * @param conditions
	 * @return
	 */
	public long count(String simpleClassName, List<Condition> conditions){
		return this.getEntityDao(simpleClassName).count(conditions);
	}
}
