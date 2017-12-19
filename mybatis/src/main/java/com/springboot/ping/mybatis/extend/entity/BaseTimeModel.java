package com.springboot.ping.mybatis.extend.entity;

import java.util.Date;

/**
 * 此类含有两个时间属性，create_time:创建时间，update_time:修改时间
 * 实体继承此类,执行新增和修改操作是会默认注入这两个属性值
 * Created by 刘江平 on 2016-10-13.
 */
public class BaseTimeModel extends BaseModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6474730067948150406L;
	/**
     * 创建时间数据库字段名称
     */
    public static final String CREATE_TIME_COLUMNNAME = "create_time";
    /**
     * 修改时间数据库字段名称
     */
    public static final String UPDATE_TIME_COLUMNNAME = "update_time";
    /**
     * 创建时间
     */
    protected Date create_time;
    /**
     * 修改时间
     */
    protected Date update_time;

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
