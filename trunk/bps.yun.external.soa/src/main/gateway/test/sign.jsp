<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="org.json.JSONObject"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="bps.common.BizException"%>
<%@page import="bps.common.ErrorCode"%>
<%@page import="bps.common.Constant"%>
<%@page import="bps.external.soa.SoaServiceUtil"%>
<%@page import="bps.external.soa.process.Extension"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ime.security.util.RSAUtil"%>
<%@page import="java.security.PrivateKey"%>
<%@page import="java.security.PublicKey"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="bps.external.soa.SoaServiceUtil"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<%
		Logger logger = Logger.getLogger("aaa.jsp");
	
		String sysid = "100000000001";
		String alias = "100000000001";
		String path = "/home/bps.yun.test/tomcat6103/webapps/ROOT/gateway/100000000001.pfx";
		String password = "502175";
			
		String timestamp = "2015-12-21 14:18:15";
		String reqStr = "{\"status\":\"OK\",\"returnValue\":{\"buyerBizUserId\":\"zxccy\",\"amount\":1,\"bizOrderNo\":\"1450678521834xf\",\"payDatetime\":\"2015-12-21 14:18:15\",\"orderNo\":\"1512210255293929\"},\"service\":\"OrderService\",\"method\":\"pay\"}";
		String signStr = sysid + reqStr + timestamp;
		String initSign = "x5XknQf/ecHpXNX8RhQv8YQN9ttYRyt4Ix/WbyCXVgKMasTBpGz+nyVsSUq2DHSJ1C9ERcufeb3eUzUaDCNJDHbQ199QElAIR89asuKtTke5Azb3l0+a6i3j886N5FLjSH/mEVaefvEhZVgUP1+jeP80PvO4GctnbGNpokvMudFSzDC0iM+CJWw4163Mq/NWloB9E9OMQ2utzuybc2LxHz6THmdevt7elSvrQn7eKDFiVlzqFFeoAzjFy5LTuxUwv9bDlwaXXA1j54mmFpjjhbhEByxaZ9PFBF24bmG36iNSn0YcBQgpJ52UZmSHk/CN8UKcOhUnWBRF4E3l0GQZwg==";	
		
		
		
		PublicKey publicKey = RSAUtil.loadPublicKey(alias, path, password);
		Boolean ret = RSAUtil.verify(publicKey, signStr, initSign);
		
		
		//Boolean ret = bps.external.soa.SoaServiceUtil.validateRsaSign(sysid, reqStr, timestamp, initSign);
		
		out.println(ret);
		
	%>

</body>
</html>