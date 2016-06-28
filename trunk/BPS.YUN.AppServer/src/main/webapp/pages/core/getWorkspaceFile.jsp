<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="ime.core.*,ime.core.entity.*,ime.document.*"%>
<%
	String fileType  = request.getParameter("fileType");
	String appUID    = request.getParameter("appUID");

	Domain domain = Environment.instance().getDomain(Reserved.MAIN_DOMAIN_ID);
	String path = DocumentStorage.getWebPath(domain.getStorageName(), "applications");

	StringBuilder sb = new StringBuilder();
	if( "Entity".equals(fileType) ){
		String entityUID = request.getParameter("entityUID");
		sb.append(path)
		  .append("/")
		  .append(appUID)
		  .append("/Entity/")
		  .append(entityUID)
		  .append(".xml?")
		  .append("_time_=")
		  .append(System.currentTimeMillis());
	}
	else if( "EntityExt".equals(fileType) ){
		String entityUID = request.getParameter("entityUID");
		sb.append(path)
		  .append("/")
		  .append(appUID)
		  .append("/Entity/")
		  .append(entityUID)
		  .append("_EXT.xml?")
		  .append("_time_=")
		  .append(System.currentTimeMillis());
	}
	else if( "Form".equals(fileType) ){
		String formUID   = request.getParameter("formUID");
		sb.append(path)
		  .append("/")
		  .append(appUID)
		  .append("/Form/")
		  .append(formUID)
		  .append(".xml?")
		  .append("_time_=")
		  .append(System.currentTimeMillis());
	}
	else if( "Query".equals(fileType) ){
		String queryUID  = request.getParameter("queryUID");
		sb.append(path)
		  .append("/")
		  .append(appUID)
		  .append("/Query/")
		  .append(queryUID)
		  .append(".xml?")
		  .append("_time_=")
		  .append(System.currentTimeMillis());
	}
	else if( "Page".equals(fileType) ){
		String pageUID = request.getParameter("pageUID");
		sb.append(path)
		  .append("/")
		  .append(appUID)
		  .append("/Page/")
		  .append(pageUID)
		  .append(".xml?")
		  .append("_time_=")
		  .append(System.currentTimeMillis());
	}
	else if( "Report".equals(fileType) ){
		String pageUID = request.getParameter("reportUID");
		sb.append(path)
		  .append("/")
		  .append(appUID)
		  .append("/Report/")
		  .append(pageUID)
		  .append(".xml?")
		  .append("_time_=")
		  .append(System.currentTimeMillis());
	}
	else if( "Diagram".equals(fileType) ){
		String pageUID = request.getParameter("diagramUID");
		sb.append(path)
		  .append("/")
		  .append(appUID)
		  .append("/Diagram/")
		  .append(pageUID)
		  .append(".xml?")
		  .append("_time_=")
		  .append(System.currentTimeMillis());
	}
	if( sb.length() == 0 )
		throw new Exception("文件无法找到！");
	response.sendRedirect(sb.toString());
	//request.getRequestDispatcher(sb.toString()).forward(request, response);
%>
