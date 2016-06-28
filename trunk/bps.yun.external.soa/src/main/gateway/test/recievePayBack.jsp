<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.Enumeration" %>

<% 
	Logger logger = Logger.getLogger("aa");
		
	logger.info("================支付后台回调开始======================");
	
	Enumeration enu = request.getParameterNames();
	while(enu.hasMoreElements()){
		String paramName = (String)enu.nextElement();
		logger.info("参数" + paramName + "=" + request.getParameter(paramName));
	}
	
	String sysid = request.getParameter("sysid");
	String sign = request.getParameter("sign");
	
	logger.info("sign=" + sign);
	
	String timestamp = request.getParameter("timestamp");
	String v = request.getParameter("v");
	String rps = request.getParameter("rps");
	
	logger.info("sysid=" + sysid + ",,sign" + sign + ",,timestamp=" + timestamp + ",,v=" + v + ",,rps=" +rps);
	
	//JSONObject rpsObject = new JSONObject(rps);
	
	Boolean ret = bps.external.soa.SoaServiceUtil.validateRsaSign(sysid, rps, timestamp, sign);
	
	logger.info("签名验证结果：" + ret);
	
	logger.info("================支付后台回调结束======================");
%>