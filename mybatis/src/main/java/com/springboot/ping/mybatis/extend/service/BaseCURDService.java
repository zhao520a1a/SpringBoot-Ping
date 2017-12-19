package com.springboot.ping.mybatis.extend.service;

import com.springboot.ping.common.util.CollectionUtil;
import com.springboot.ping.mybatis.extend.dao.BaseCURDDao;
import com.springboot.ping.mybatis.extend.entity.BaseModel;
import com.springboot.ping.mybatis.extend.entity.BaseTimeModel;
import com.springboot.ping.mybatis.vo.*;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 支持crud功能的service基类
 * Created by 刘江平 on 2016-10-14.
 */
public abstract class BaseCURDService<T extends BaseModel, K extends BaseCURDDao<T>> extends BaseQueryService<T, K>{

    /**
     * 根据主键删除唯一记录
     *
     * @param entity
     */
    @Transactional
    public void deleteByPk(T entity) {
        baseDao.deleteByPk(entity);
    }

    /**
     * 批量删除
     *
     * @param entitys
     */
    @Transactional
    public void deleteBatchByPks(List<T> entitys) {
        for (T entity : entitys) {
            this.deleteByPk(entity);
        }
    }

    /**
     * 删除所有
     */
    @Transactional
    public void deleteAll() {
        baseDao.deleteBatch(emptyConditions);
    }

    /**
     * 按单个条件批量删除
     *
     * @param condition
     */
    @Transactional
    public void deleteBatch(Condition condition) {
        List<Condition> conditions = null;
        if (condition != null) {
            conditions = new ArrayList<Condition>();
            conditions.add(condition);
        }
        baseDao.deleteBatch(conditions);
    }

    /**
     * 按多个条件批量删除
     *
     * @param conditions
     */
    @Transactional
    public void deleteBatch(List<Condition> conditions) {
        baseDao.deleteBatch(conditions);
    }

    
    /**
     * 新增一条记录,
     * 如果实体继承了类BaseTimeModel,则会默认注入create_time和update_time的值为当前时间
     *
     * @param entity
     */
    @Transactional
    public void save(T entity) {
        if (this.isBaseTimeModel) {
            BaseTimeModel e = (BaseTimeModel) entity;
            e.setCreate_time(new Date());
            e.setUpdate_time(new Date());
        }
        baseDao.insert(entity);
    }

    /**
     * 批量保存所有实体
     *
     * @param entities
     */
    @Transactional
    public void saveBatch(List<T> entities) {
        if (!CollectionUtil.isEmpty(entities)) {
            for (T entity : entities) {
                save(entity);
            }
        }
    }

    /**
     * 根据主键修改一条记录,
     * 如果实体继承了类BaseTimeModel,则会默认注入update_time的值为当前时间
     *
     * @param entity
     */
    @Transactional
    public void update(T entity) {
        if (this.isBaseTimeModel) {
            BaseTimeModel e = (BaseTimeModel) entity;
            e.setUpdate_time(new Date());
        }
        baseDao.updateByPk(entity);
    }

    /**
     * 按多个条件批量修改多个字段的值,
     *如果实体继承了类BaseTimeModel,则会默认注入update_time的值为当前时间
     * @param columns    要修改的字段
     * @param conditions 条件
     * @return
     */
    @Transactional
    public void updateBatchColumns(List<Column> columns, List<Condition> conditions) {
        if(this.isBaseTimeModel){
            boolean noUpdateTime = true;
            for (Column c : columns) {
                if(c.getColumn().equalsIgnoreCase(BaseTimeModel.UPDATE_TIME_COLUMNNAME)){
                    noUpdateTime = false;
                }
            }
            if(noUpdateTime){
                columns.add(new Column(BaseTimeModel.UPDATE_TIME_COLUMNNAME,new Date()));
            }
        }
        this.baseDao.updateBatchColumns(columns, conditions);
    }

    /**
     * 按单个条件批量修改多个字段的值
     *
     * @param columns   要修改的字段
     * @param condition 条件
     * @return
     */
    @Transactional
    public void updateBatchColumns(List<Column> columns, Condition condition) {
        List<Condition> conditions = null;
        if (condition != null) {
            conditions = new ArrayList<Condition>();
            conditions.add(condition);
        }
        this.updateBatchColumns(columns, conditions);
    }

    /**
     * 按多个条件批量修改某个字段的值
     *
     * @param column     字段名称
     * @param value      要修改的值
     * @param conditions 条件
     * @return
     */
    @Transactional
    public void updateBatchColumn(String column, Object value, List<Condition> conditions) {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column(column, value));
        this.updateBatchColumns(columns, conditions);

    }

    /**
     * 按单个条件批量修改某个字段的值
     *
     * @param column    字段名称
     * @param value     要修改的值
     * @param condition 条件
     * @return
     */
    @Transactional
    public void updateBatchColumn(String column, Object value, Condition condition) {
        List<Condition> conditions = null;
        if (condition != null) {
            conditions = new ArrayList<Condition>();
            conditions.add(condition);
        }
        List<Column> columns = new ArrayList<>();
        columns.add(new Column(column, value));
        this.updateBatchColumns(columns, conditions);
    }

    /**
     * 更新list中所有的实体。
     *
     * @param entities
     */
    @Transactional
    public void updateBatch(List<T> entities) {
        if (!CollectionUtil.isEmpty(entities)) {
            for (T entity : entities) {
                update(entity);
            }
        }
    }

}
