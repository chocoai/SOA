
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%><%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	Integer start = Integer.valueOf(request.getParameter("start"));
	Integer limit = Integer.valueOf(request.getParameter("limit"));
	
	JSONArray array = new JSONArray();
	for(int i = start; i < ((start / limit + 1) *limit); i++){
		JSONObject o = new JSONObject();
		o.put("company", "company" + i);
		o.put("alink", "测试" + i);
		o.put("id", i + 1);
		array.put(o);
	}
	
	JSONObject value = new JSONObject();
	value.put("data", array);
	value.put("totol", 101);
	
	out.println(value.toString());
%>