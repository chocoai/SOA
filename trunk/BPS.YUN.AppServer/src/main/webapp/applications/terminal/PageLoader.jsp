<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="apf.framework.*,ime.core.*,ime.core.entity.*,ime.security.*,ime.security.services.*"%>
<%
	String domainName = request.getParameter("domain");
	String password   = request.getParameter("password");
	String loginId    = request.getParameter("loginId");
	
	LoginSession loginSession = new LoginSession(session);

	boolean isLogined = false;
	try{
		if( LoginSession.isLogined() )
			isLogined = true;
	}
	catch(Exception e){
	}
	if( !isLogined )
		loginSession.login(loginId, password, domainName);

	String url = request.getParameter("url");
	if( url.indexOf("http://") == -1 )
		url = Framework.instance().getApplicationName() + url;
	
	response.addHeader("Pragma", "No-cache");
	response.addHeader("Cache-Control", "no-cache");
	response.addDateHeader("Expires", 1);

	response.sendRedirect(url);
%>
