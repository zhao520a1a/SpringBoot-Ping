package com.springboot.ping.mybatis.extend.dao;

import com.springboot.ping.mybatis.extend.entity.BaseModel;
import com.springboot.ping.mybatis.vo.Column;
import com.springboot.ping.mybatis.vo.Condition;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 所有要支持curd功能的dao都必须继承此类，所有数据库实体对应的dao的名称格式必须实体名称+Dao,
 * 包路径是实体包路径+".dao"
 * Created by 刘江平 on 2016-10-14.
 */
public interface BaseCURDDao<T extends BaseModel> extends BaseQueryDao<T>{

    /**
     * 新增一条记录
     * @param entity
     */
    public void insert(T entity);
    /**
     * 根据主键修改一条记录,为null的属性不修改
     * @param entity
     */
    public void updateByPk(T entity);
    /**
     * 按条件批量修改某个字段的值
     * @param columns 要修改的字段信息
     * @param conditions 条件
     * @return
     */
    public void updateBatchColumns(@Param(value = "columns") List<Column> columns, @Param(value = "conditions") List<Condition> conditions);
    /**
     * 根据主键删除唯一一条记录
     * @param entity
     */
    public void deleteByPk(T entity);
    /**
     * 按条件批量删除
     * @param conditions
     */
    public void deleteBatch(@Param(value = "conditions") List<Condition> conditions);
 
}
