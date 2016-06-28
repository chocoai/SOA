<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.Logger" %>

<% 
	Logger logger = Logger.getLogger("aa");
		
	logger.info("================支付后台回调开始======================");
	out.println("================支付后台回调开始======================");

	String sysid = request.getParameter("sysid");
	logger.info("sysid=" + sysid);
	out.println("sysid:" + sysid);
	
	out.println("================支付后台回调结束======================");
	logger.info("================支付后台回调结束======================");
%>