package com.springboot.ping.mvc.easyui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.springboot.ping.common.util.CollectionUtil;
import com.springboot.ping.common.util.StringUtil;
import com.springboot.ping.mvc.auth.Auth;
import com.springboot.ping.mvc.auth.AuthType;


/**
 * easyui的tree树辅助类
 * <br>创建者： 刘江平
 * 创建时间：2015年7月23日下午4:51:22
 */
public class EasyuiTree {
	/**
	 * 绑定节点的标识值
	 */
	private String id;
	/**
	 * 显示的节点文本
	 */
	private String text;
	/**
	 * 显示的节点图标CSS类ID
	 */
	private String iconCls;
	/**
	 * 节点状态，'open' 或 'closed'
	 */
	private String state = "open";
	/**
	 * 该节点是否被选中
	 */
	private boolean checked = false;
	/**
	 * 绑定该节点的自定义属性
	 */
	private Map<String, Object> attributes = new HashMap<String, Object>();
	/**
	 * 子节点集合
	 */
	private List<EasyuiTree> children = new ArrayList<EasyuiTree>();
	/**用户或者角色是否拥有此权限*/
	private boolean auth = false;
	
	public EasyuiTree(){}
	/**
	 * url会添加参数authid
	 * 节点的attributes属性包含权限路径:url;权限父id:pid;是否是按钮:button;父节点权限类型:ptype;是否是继承过来的权限:extend
	 * @param auth  权限
	 * @param ownAuths 用户或者角色拥有的所有权限集合
	 * @param extendAuths 用户或者角色继承的权限
	 */
	public EasyuiTree(Auth auth, List<Auth> ownAuths, List<Auth> extendAuths){
		String authid = auth.getId();
		this.id = authid;
		this.text = auth.getName();
		String type = auth.getType().toString();
		if(!CollectionUtil.isEmpty(ownAuths)){
			String key = authid+"|"+type;
			for (Auth ownAuth : ownAuths) {
				if(key.equals(ownAuth.getId()+"|"+ownAuth.getType())){
					this.checked=true;
					this.auth=true;
					break;
				}
			}
		}
		if(!CollectionUtil.isEmpty(extendAuths)){
			String key = authid+"|"+type;
			for (Auth extendAuth : extendAuths) {
				if(key.equals(extendAuth.getId()+"|"+extendAuth.getType())){
					this.checked=true;
					this.auth=true;
					this.attributes.put("extend", true);
					break;
				}
			}
		}
		String icon = auth.getIcon();
		if(StringUtil.isNotEmpty(icon)){
			this.iconCls = icon;
		}
		String url = auth.getUrl();
		if(StringUtil.isNotEmpty(url)){
			if(url.indexOf("?")==-1){
				url = url +"?authid="+authid;
			}else{
				url = url +"&authid="+authid;
			}
			this.attributes.put("url", url);
		}
		this.attributes.put("pid", auth.getPid());
		this.attributes.put("button", type.equalsIgnoreCase(AuthType.button.toString()));
		this.attributes.put("ptype", auth.getPtype().toString());
	}
	/**
	 * 将所有权限数据生成树状
	 * @param allAuths 所有权限集合
	 * @param rootid 所用权限的根ID
	 * @param roottype 根权限类型，菜单或者按钮等
	 * @param ownAuths 用户或者角色本身拥有的权限，如果传入有值，则生成的权限树种会匹配这部分权限并设置checked=true，选中，权限回显可用，如果传入为空，则不匹配
	 * @param extendAuths 用户或者角色继承的权限
	 * @return
	 */
	public static List<EasyuiTree> buildTree(List<Auth> allAuths,String rootid,String roottype,
			List<Auth> ownAuths,List<Auth> extendAuths){
		Map<String,EasyuiTree> tempTree = new LinkedHashMap<String, EasyuiTree>();
		for (Auth auth : allAuths) {
			tempTree.put(auth.getId()+"|"+auth.getType(), new EasyuiTree(auth,ownAuths,extendAuths));
		}
		for (Iterator<Map.Entry<String,EasyuiTree>> iterator = tempTree.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String,EasyuiTree> entry = iterator.next();
			EasyuiTree tree = entry.getValue();
			String pid = (String) tree.getAttributes().get("pid");
			String ptype = (String) tree.getAttributes().get("ptype");
			String rootkey = rootid+"|"+roottype;
			String treekey = pid+"|"+ptype;
			if(!treekey.equals(rootkey)){
				EasyuiTree parentTree = tempTree.get(treekey);
				if(parentTree!=null){
					parentTree.getChildren().add(tree);
					parentTree.setChecked(false);
					tempTree.put(treekey, parentTree);
				}
			}
		}
		List<EasyuiTree> trees = new ArrayList<EasyuiTree>();
		for (Iterator<Map.Entry<String,EasyuiTree>> iterator = tempTree.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String,EasyuiTree> entry = iterator.next();
			EasyuiTree tree = entry.getValue();
			String pid = (String) tree.getAttributes().get("pid");
			String ptype = (String) tree.getAttributes().get("ptype");
			String rootkey = rootid+"|"+roottype;
			String treekey = pid+"|"+ptype;
			if(treekey.equals(rootkey)){
				trees.add(tree);
			}
		}
		addEmptyButton(trees);
		return trees;
	}
	/**
	 * 给有按钮子节点的菜单下增加一个"无按钮"节点button
	 * @param trees
	 */
	private static void addEmptyButton(List<EasyuiTree> trees) {
		for (EasyuiTree tree : trees) {
			List<EasyuiTree> children = tree.children;
			if(!CollectionUtil.isEmpty(children)){
				if((Boolean)children.get(0).attributes.get("button")){
					boolean emptyButtonChecked = true;
					for (EasyuiTree child : children) {
						if(child.checked){
							emptyButtonChecked = false;
						}
					}
					EasyuiTree e = new EasyuiTree();
					e.id="-1";
					e.text="可看菜单但无子按钮";
					e.checked=emptyButtonChecked?tree.auth:emptyButtonChecked;
					e.attributes.put("pid", tree.id);
					e.attributes.put("button", true);
					e.attributes.put("ptype", AuthType.menu.toString());
					e.iconCls = "icon-no";
					tree.children.add(e);
				}else{
					addEmptyButton(children);
				}
			}
		}
	}
	/**
	 * 生成左菜单的html语言
	 * @param auths 权限列表
	 * @param rootid 根权限ID
	 * @param roottype 根权限类型，菜单或者按钮等
	 * @return
	 */
	public static String buildLeftMenu(List<Auth> auths,String rootid,String roottype){
		List<EasyuiTree> buildTree = buildTree(auths,rootid,roottype,null,null);
		StringBuffer menu = new StringBuffer();
		String rootMenu = "<div title=\"%s\" style=\"padding:10px;\">%s</div>";
		String childrenMenu = "<ul class=\"easyui-tree\" data-options=\"data:%s,onClick:clickLeftMenu\"></ul>";
		for (EasyuiTree tree : buildTree) {
			String text = tree.getText();
			List<EasyuiTree> children = tree.getChildren();
			String treeDatas = JSONArray.toJSONString(children);
			treeDatas = treeDatas.replaceAll("\"", "'");
			menu.append(String.format(rootMenu, new Object[]{text,String.format(childrenMenu, treeDatas)}));
		}
		return menu.toString();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public List<EasyuiTree> getChildren() {
		return children;
	}
	public void setChildren(List<EasyuiTree> children) {
		this.children = children;
	}
}
