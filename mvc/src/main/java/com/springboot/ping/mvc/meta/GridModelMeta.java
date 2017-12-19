package com.springboot.ping.mvc.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springboot.ping.common.enums.Align;
import com.springboot.ping.common.enums.Format;
import com.springboot.ping.common.util.StringUtil;
import com.springboot.ping.mvc.annotation.Dic;
import com.springboot.ping.mvc.annotation.GridField;
import com.springboot.ping.mvc.annotation.Search;
import com.springboot.ping.mvc.enums.InputType;
import com.springboot.ping.mybatis.extend.entity.BaseModel;
import com.springboot.ping.mybatis.extend.entity.BaseTimeModel;
import com.springboot.ping.mybatis.meta.BaseMeta;
import com.springboot.ping.mybatis.meta.PropertyInfo;

public class GridModelMeta {
	private static Logger log = LoggerFactory.getLogger(GridModelMeta.class);
	private static Map<Class<?>,List<GridPropertyInfo>> properties = new HashMap<Class<?>, List<GridPropertyInfo>>();
	/**
	 * 获取某个类的属性
	 * @param clazz
	 * @return
	 */
	public static List<GridPropertyInfo> getProperties(Class<?> clazz){
		if(!BaseModel.class.isAssignableFrom(clazz)){
			throw new IllegalArgumentException(clazz.getName()+ "不是一个数据库实体类型");
		}
		List<GridPropertyInfo> gridPropertyInfos = properties.get(clazz);
		if(gridPropertyInfos==null){
			Map<String, PropertyInfo> properties2 = BaseMeta.getBeanInfo(clazz).getPropertyInfosMap();
			if(properties2!=null){
				gridPropertyInfos = new ArrayList<GridPropertyInfo>();
				for (Iterator<Map.Entry<String, PropertyInfo>> it = properties2.entrySet().iterator(); it
						.hasNext();) {
					Map.Entry<String, PropertyInfo> entry = it.next();
					PropertyInfo propertyInfo = entry.getValue();
					String dbColumnName = propertyInfo.getDbColumnName();
					Field field = propertyInfo.getField();
					GridPropertyInfo gpi = new GridPropertyInfo();
					try {
						BeanUtils.copyProperties(gpi, propertyInfo);
						GridField gf_ann = field.getAnnotation(GridField.class);
						if(gf_ann!=null){
//							com.springboot.ping.mvc.vo.GridField gridField = new com.springboot.ping.mvc.vo.GridField();
//							gridField.setDetail(gf_ann.detail());
//							gridField.setDic(new Dic(gf_ann.dic().group()));
//							gridField.setFormat(gf_ann.format().toString());
//							gridField.setHide(gf_ann.hide());
//							gridField.setName(gf_ann.name());
//							gridField.setSearch(new Search(gf_ann.search().search(), gf_ann.search().inputType(), gf_ann.search().between(),gf_ann.search().singleSelect(),gf_ann.search().searchName()));
//							gridField.setWidth(gf_ann.width());
//							gridField.setAlign(gf_ann.align().toString());
//							gridField.setDicFomart(gf_ann.dicFomart());
//							gridField.setSortable(gf_ann.sortable());
							gpi.setGridField(gf_ann);
						}
						if(BaseTimeModel.class.isAssignableFrom(clazz)){
							if(dbColumnName.equals(BaseTimeModel.CREATE_TIME_COLUMNNAME)
							 ||dbColumnName.equals(BaseTimeModel.UPDATE_TIME_COLUMNNAME)){
								if(gf_ann==null){
//									com.springboot.ping.mvc.vo.GridField gridField = new com.springboot.ping.mvc.vo.GridField();
//									gridField.setFormat(Format.DATETIME.toString());
//									gridField.setName(dbColumnName.equals(BaseTimeModel.CREATE_TIME_COLUMNNAME)?"创建时间":"修改时间");
//									gridField.setSearch(new Search(true, InputType.DATEBOX, false,false,null));
									gpi.setGridField(new GridField() {
										@Override
										public Class<? extends Annotation> annotationType() {
											return GridField.class;
										}
										@Override
										public int width() {
											return 100;
										}
										@Override
										public boolean sortable() {
											return true;
										}
										@Override
										public Search search() {
											return new Search() {
												@Override
												public Class<? extends Annotation> annotationType() {
													return Search.class;
												}
												@Override
												public boolean singleSelect() {
													return false;
												}
												@Override
												public String searchName() {
													return dbColumnName.equals(BaseTimeModel.CREATE_TIME_COLUMNNAME)?"创建时间":"修改时间";
												}
												@Override
												public boolean search() {
													return true;
												}
												@Override
												public InputType inputType() {
													return InputType.DATEBOX;
												}
												@Override
												public boolean between() {
													return false;
												}
											};
										}
										@Override
										public String name() {
											return dbColumnName.equals(BaseTimeModel.CREATE_TIME_COLUMNNAME)?"创建时间":"修改时间";
										}
										@Override
										public boolean hide() {
											return false;
										}
										@Override
										public Format format() {
											return Format.DATETIME;
										}
										@Override
										public boolean dicFomart() {
											return false;
										}
										@Override
										public Dic dic() {
											return null;
										}
										@Override
										public boolean detail() {
											return true;
										}
										@Override
										public Align align() {
											return Align.center;
										}
									});
								}
							}
						}
					} catch (IllegalAccessException e) {
						log.error(StringUtil.getTrace(e));
					} catch (InvocationTargetException e) {
						log.error(StringUtil.getTrace(e));
					}
					gridPropertyInfos.add(gpi);
				}
				properties.put(clazz, gridPropertyInfos);
			}
		}
		return Collections.unmodifiableList(gridPropertyInfos);
	}
	
}
