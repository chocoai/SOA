<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bps.external.tradecommand.BarchDaiFuRedisTriger" %>
<%@ page import="bps.external.tradecommand.BarchDaiFuT0RedisTriger" %>
<%
	String action = request.getParameter("action");
	String day = request.getParameter("day");
	String endDay = request.getParameter("endDay");
	
	//业务处理
	BarchDaiFuRedisTriger bdfrt = new BarchDaiFuRedisTriger();
	if( action.equals("auto")){
		bdfrt.go(day);
	}else {
		BarchDaiFuT0RedisTriger bdfrt2 = new BarchDaiFuT0RedisTriger();
		bdfrt2.handle();
	}
%>