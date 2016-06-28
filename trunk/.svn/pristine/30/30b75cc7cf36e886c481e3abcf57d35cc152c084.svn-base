<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="apf.framework.Framework, ime.security.*,ime.core.*,java.io.*,ime.core.entity.*,ime.document.*"%>
<%
	LoginSession loginSession = new LoginSession(request.getSession());
	Domain domain = Environment.instance().getDomain(loginSession.getDomainId().longValue());
	
	String pathname = request.getParameter("pathname");
	String filetype = request.getParameter("filetype");
	
	StringBuilder path = new StringBuilder();
	while( domain != null ){
		path.setLength(0);
		path.append("/")
			.append(Framework.instance().getApplicationName())
		    .append(DocumentStorage.DOC_ROOT)
		    .append(domain.getStorageName())
		    .append("/")
		    .append(HtmlUtil.filterPath(filetype))
		    .append("/")
		    .append(HtmlUtil.filterPath(pathname));
		File file = new File(path.toString());
		if( file.exists() )
			break;
		if( domain.getParent() == null )
			break;
		domain = Environment.instance().getDomain(domain.getParent().getId());
	}
%>

<%@page import="ime.web.util.HtmlUtil"%><jsp:forward page="<%=path.toString()%>" />

