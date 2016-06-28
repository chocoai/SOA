<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*,java.net.*,java.text.*,java.io.*,org.json.*,apf.util.*,org.hibernate.*,ime.core.services.*" %>
<%!
	public static String getValue(Map<String, Object> map, String name, String defValue){
		if( !map.containsKey(name) )
			return defValue;
		Object val = map.get(name);
		if( val == null || "".equals(val.toString()) || "null".equals(val.toString()))
			return defValue;
		try{
			if( val instanceof org.hibernate.lob.SerializableClob){
				org.hibernate.lob.SerializableClob clog = (org.hibernate.lob.SerializableClob)val;
				return clog.getSubString(1L, Long.valueOf(clog.length()).intValue());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return val.toString();
	}
%>
<%
	String content_id = request.getParameter("id");
	if(content_id == null || "".equals(content_id) || "null".equals(content_id)){
		response.getWriter().print("");
		return;
	}
	
	Map<String,Object> contentEntity = DynamicEntityService.getEntity(Long.valueOf(content_id), "CMSArticle");
	if(contentEntity == null){
		response.getWriter().print("");
		return;
	}
		
	response.getWriter().print(getValue(contentEntity,"contents",""));
%>