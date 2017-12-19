package com.springboot.ping.mvc.meta;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.springboot.ping.mvc.annotation.Grid;
import com.springboot.ping.mybatis.meta.BeanInfo;

public class GridPageModel {
	
	/**
	 * 初始化list页面的参数对象信息
	 * @param pageTemplate
	 * @param request
	 */
	public static PageListModel getPageListModel(BeanInfo beanInfo,String module,PageTemplate pageTemplate,HttpServletRequest request){
		PageListModel pageListModel = new PageListModel();
		pageListModel.setModule(module);
		pageListModel.setEntity(beanInfo.getEntityName());
		long time = System.currentTimeMillis();
		pageListModel.setPageId(beanInfo.getEntityName()+"_"+time);
		Grid grid = beanInfo.getClazz().getAnnotation(Grid.class);
		pageListModel.setSingleSelect(grid.singleSelect());
		pageListModel.setRemoteSort(grid.remoteSort());
		pageListModel.setMultiSort(grid.multiSort());
		pageListModel.setTitle(grid.title());
		
		List<GridPropertyInfo> properties = GridModelMeta.getProperties(beanInfo.getClazz());
		//设置默认查询参数的值
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (GridPropertyInfo gpi : properties) {
			String propertyName = gpi.getPropertyName();
			if(parameterMap.containsKey(propertyName)){
				String[] values = parameterMap.get(propertyName);
				if(values!=null&&values.length>0){
					gpi.setDefaultSearchValue(values[0]);
				}
				if(values!=null&&values.length>1){
					gpi.setDefaultSearchValue2(values[1]);
				}
			}else{
				gpi.setDefaultSearchValue("");
				gpi.setDefaultSearchValue2("");
			}
		}
		pageListModel.setFieldHtml(pageTemplate.getFieldHtml(properties, pageListModel));
		pageListModel.setSearchHtml(pageTemplate.getSearchHtml(properties, pageListModel));
		
//		GridToolBar[] oper = grid.oper();
//		List<GridToolBar> toolbars = new ArrayList<GridToolBar>();
//		for (GridToolBar tb : oper) {
//			String name = tb.name();
//			String url = tb.url().trim();
//			//TODO 权限检查
////			if(!AuthCheckUtil.hasAuth(request, url)){
////				continue;
////			}
//			String open = tb.open();
//			if(url.equals(ToolBarUrl.URL_DELETE)
//					||url.equals(ToolBarUrl.URL_DELETE_BATCH)
//					||url.equals(ToolBarUrl.URL_EXPORT_EXCEL)
//					||url.equals(ToolBarUrl.URL_EXPORT_EXCEL_NOFORMAT)){
//				url = "/"+module+"/"+beanInfo.getEntityName()+"/"+url;
//			}else if(url.equals(ToolBarUrl.URL_DETAIL)){
//				url = "/"+module+"/"+beanInfo.getEntityName()+"/"+url+"?open="+open.toString();
//			}
//			String icon = tb.icon();
//			toolbars.add(new GridToolBar(url, name, icon, open));
//		}
		pageListModel.setToolbarHtml(pageTemplate.getToolbarHtml(grid.oper(), pageListModel));
		return pageListModel;
	}
}
