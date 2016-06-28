<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*,apf.framework.*,org.dom4j.io.*,org.dom4j.*,java.io.*,org.json.*,ime.security.services.*"%>
<%@page import="ime.core.entity.*,ime.core.*,ime.security.*,ime.security.entity.*"%>
<%@page import="apf.util.*,org.hibernate.*"%>

<%!
	private boolean isPermission(String permission, List perList, Group root){
		if(permission == null || "".equals(permission) || perList == null || perList.isEmpty() || root == null)
			return false;
		Object all = perList.get(0);
		if(all instanceof String && "$all permissions$".equals(all.toString()))
			return true;
		String[] spilts = permission.split("\\.");
		if(spilts.length <= 1)
			return false;
		String appName = spilts[0];
		Group appGroup = root.findFromName(appName);
		if(appGroup == null)
			return false;
		Group permRoot = appGroup.findFromName("PermissionRoot");
		if(permRoot == null)
			return false;
		spilts = permission.substring(permission.indexOf(appName) + appName.length() + 1).split("\\.");
		Group permGroup = findPermGroup(spilts, root);
		if(permGroup == null)
			return false;
		Iterator it = perList.iterator();
		while(it.hasNext()){
			PagePermission entry = (PagePermission)it.next();
			if(entry.getGroup().getId() == permGroup.getId()){
				String _perm = entry.getPermissions();
				if(_perm.indexOf(spilts[spilts.length - 1]) != -1)
					return true;
			}
		}
		return false;
	}

	private Group findPermGroup(String[] spilts, Group group){
		Group child = group;
		for(int i = 0; i < spilts.length - 1; i++){
			String perm = spilts[i];
			child = findPermGroup(perm, child);
		}
		return child;
	}
	
	private Group findPermGroup(String perm, Group group){
		for( Iterator<Group> iter = group.getChildren().iterator(); iter.hasNext(); ){
			Group child = iter.next();
			if(perm.equals(child.getName()))
				return child;
			else{
				child = findPermGroup(perm, child);
				if(child != null)
					return child;
			}
		}
		return null;
	}
%>
<%
	LoginSession.setHttpSession(session);

	try {
		PagePermissionService pagePermissionService = new PagePermissionService();
		List perList = (List)pagePermissionService.getAllPermissions();
		
		Group root = Environment.instance().getGroup(Reserved.WORKSPACE_GROUP_ID);
		
		//默认三列， 按顺序1、2、3、4、5...排列下去
		List<Object> configJson = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		sb.append(Framework.instance().getApplicationPath()).append("portal/xml/portal.xml");

		SAXReader xreader = new SAXReader();
		Document doc = xreader.read(new InputStreamReader(new FileInputStream(sb.toString()), "UTF-8"));
		
		Element portalEl = doc.getRootElement();
		
		List list = portalEl.elements("panel");
		Iterator it = list.iterator();
		
		List<Object> globalList = new ArrayList<Object>();
		while(it.hasNext()){
			Element panelEl = (Element)it.next();
			
			Element typeEl = (Element)panelEl.selectSingleNode("type");
			if(typeEl == null)
				continue;
			
			Map<String, Object> panel = new HashMap<String, Object>();
			panel.put("type", typeEl.attributeValue("value"));
			Element titleEl = (Element)panelEl.selectSingleNode("title");
			if(titleEl != null && !"".equals(titleEl.attributeValue("value")))
				panel.put("title", titleEl.attributeValue("value"));
			Element nameEl = (Element)panelEl.selectSingleNode("name");
			if(nameEl != null && !"".equals(nameEl.attributeValue("value")))
				panel.put("name", nameEl.attributeValue("value"));
			Element permissionIdEl = (Element)panelEl.selectSingleNode("permissionId");
			if(permissionIdEl != null && !"".equals(permissionIdEl.attributeValue("value")) && 
					isPermission(permissionIdEl.attributeValue("value"), perList, root))
				panel.put("permissionId", permissionIdEl.attributeValue("value"));
			else if("".equals(permissionIdEl.attributeValue("value"))){
				panel.put("permissionId", "");
			}else{
				continue;
			}
			
			Element sizeEl = (Element)panelEl.selectSingleNode("size");
			if(sizeEl != null){
				if(sizeEl.attributeValue("width") != null && !"".equals(sizeEl.attributeValue("width")))
					panel.put("width", sizeEl.attributeValue("width"));
				if(sizeEl.attributeValue("height") != null && !"".equals(sizeEl.attributeValue("height")))
					panel.put("height", sizeEl.attributeValue("height"));
			}
			
			if("panel".equals(typeEl.attributeValue("value"))){
				Element htmlUrlEl = (Element)panelEl.selectSingleNode("htmlUrl");
				if(htmlUrlEl != null && !"".equals(htmlUrlEl.attributeValue("value")))
					panel.put("htmlUrl", htmlUrlEl.attributeValue("htmlUrl"));
				Element htmlEl = (Element)panelEl.selectSingleNode("html");
				if(htmlEl != null && !"".equals(htmlEl.getTextTrim()))
					panel.put("html", htmlEl.getTextTrim());
			}else if("grid".equals(typeEl.attributeValue("value"))){
				Element isPageEl = (Element)panelEl.selectSingleNode("isPage");
				if(isPageEl != null && !"".equals(isPageEl.attributeValue("value")))
					panel.put("isPage", isPageEl.attributeValue("value"));
				
				Element pageSizeEl = (Element)panelEl.selectSingleNode("pageSize");
				if(pageSizeEl != null && !"".equals(pageSizeEl.attributeValue("value")))
					panel.put("pageSize", pageSizeEl.attributeValue("value"));
				Element dataUrlEl = (Element)panelEl.selectSingleNode("dataUrl");
				if(dataUrlEl != null && !"".equals(dataUrlEl.attributeValue("value")))
					panel.put("dataUrl", dataUrlEl.attributeValue("value"));
				Element dataEl = (Element)panelEl.selectSingleNode("data");
				if(dataEl != null && !"".equals(dataEl.getTextTrim()))
					panel.put("data", dataEl.getTextTrim());
				
				List<Object> cols = new ArrayList<Object>();
				panel.put("columns", cols);
				List colsList = panelEl.selectNodes("colunms/colunm");
				Iterator _it = colsList.iterator();
				while(_it.hasNext()){
					Element colEl = (Element)_it.next();
					Map<String, Object> _v = new HashMap<String, Object>();
					Element _titleEl = (Element)colEl.selectSingleNode("title");
					if(_titleEl != null && !"".equals(_titleEl.attributeValue("value")))
						_v.put("title", _titleEl.attributeValue("value"));
					Element _nameEl = (Element)colEl.selectSingleNode("name");
					if(_nameEl != null && !"".equals(_nameEl.attributeValue("value")))
						_v.put("name", _nameEl.attributeValue("value"));
					Element alinkEl = (Element)colEl.selectSingleNode("alink");
					if(alinkEl != null && !"".equals(alinkEl.attributeValue("value")))
						_v.put("alink", alinkEl.attributeValue("value"));
					Element _typeEl = (Element)colEl.selectSingleNode("type");
					if(_typeEl != null && !"".equals(_typeEl.attributeValue("value")))
						_v.put("type", _typeEl.attributeValue("value"));
					Element sortEl = (Element)colEl.selectSingleNode("sort");
					if(sortEl != null && !"".equals(sortEl.attributeValue("value")))
						_v.put("sort", sortEl.attributeValue("value"));
					cols.add(_v);
				}
			}else{
				continue;
			}
			
			globalList.add(panel);
		}
		
		out.println(new JSONArray(globalList));
	} catch (Exception e) {
		e.printStackTrace();
	}
%>