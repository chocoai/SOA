<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" errorPage="" %>
<%@page import="bps.web.monitor.HealthMonitor"%>
<%@page import="org.apache.log4j.Logger"%>

<%
	Logger logger = Logger.getLogger("healthMonitor.jsp");
	logger.info("================================healthMonitor begin============================");
	
	HealthMonitor healthMonitor = new HealthMonitor();
	String ret = healthMonitor.monitor();
	
	logger.info("健康监测返回数据：" + ret);
	
	out.println(ret);
	
	
	logger.info("================================healthMonitor end============================");
%>

	