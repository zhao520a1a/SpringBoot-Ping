package com.springboot.ping.mybatis.core;

import com.springboot.ping.mybatis.meta.PropertyInfo;

import java.util.List;

/**
 * 生成mapper文件的crud操作的sql
 * Created by 刘江平 on 2016-10-13.
 */
public interface MapperCRUDBuilder {

    public String findByPk(String tableName, List<PropertyInfo> propertyInfos);
    public String find(String tableName, List<PropertyInfo> propertyInfos);
    public String insert(String tableName, List<PropertyInfo> propertyInfos);
    public String updateByPk(String tableName, List<PropertyInfo> propertyInfos);
    public String updateBatchColumns(String tableName, List<PropertyInfo> propertyInfos);
    public String deleteByPk(String tableName, List<PropertyInfo> propertyInfos);
    public String deleteBatch(String tableName, List<PropertyInfo> propertyInfos);
    public String count(String tableName, List<PropertyInfo> propertyInfos);
    public String countDistinctColumns(String tableName, List<PropertyInfo> propertyInfos);
    public String sumColumn(String tableName, List<PropertyInfo> propertyInfos);
    public String maxColumn(String tableName, List<PropertyInfo> propertyInfos);
    public String minColumn(String tableName, List<PropertyInfo> propertyInfos);
    public String avgColumn(String tableName, List<PropertyInfo> propertyInfos);
}
