
<%@page import="ime.web.util.HtmlUtil"%><%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="java.io.*,ime.security.*,ime.core.*,ime.core.entity.*,ime.document.*,ime.core.services.*"%>
<%
	LoginSession.setHttpSession(session);

	String result = "{status:'OK'}";
	try{
		String node     = request.getParameter("nodeType");
		String pathname = request.getParameter("pathname");
		String content  = request.getParameter("content");
		
		int nodeType = Integer.parseInt(node);
		WorkshopService.saveFile(nodeType, HtmlUtil.filterPath(pathname), content);
	}
	catch(Exception e){
		result = "{status:'error', message:'" + e.getMessage() + "'}";
	}
%>
<%=result%>

