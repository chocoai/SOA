<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="org.json.JSONObject"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ime.security.util.RSAUtil"%>
<%@page import="java.security.PrivateKey"%>
<%@page import="java.net.URLEncoder"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache"> 
<meta http-equiv="cache-control" content="no-cache"> 
<meta http-equiv="expires" content="0">   
</head>
<body>
	<%
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");

		Logger logger = Logger.getLogger("aaa.jsp");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String bizUserId = (String)request.getParameter("bizUserId");
		String bizOrderNo = (String)request.getParameter("bizOrderNo");
		String consumerIp = (String)request.getParameter("consumerIp");
		String verificationCode = (String)request.getParameter("verificationCode");
		String sysid = (String)request.getParameter("sysid") == null ? "100000000002" : (String)request.getParameter("sysid");

		JSONObject param = new JSONObject();
		param.put("bizUserId", bizUserId);
		param.put("bizOrderNo", bizOrderNo); 
		param.put("consumerIp", consumerIp);
		param.put("verificationCode", verificationCode);
		
		JSONObject req = new JSONObject();
		req.put("service", "OrderService");
		req.put("method", "pay");
		req.put("param", param);

		String timestamp = sdf.format(new Date());
		String alias = "100000000002";
		String path = "/home/bps.yun.test/tomcat6103/webapps/ROOT/gateway/100000000002.pfx";
		String password = "697057";


		if( "100000000010".equals(sysid)) {
			alias = "100000000010";
			path = "/home/bps.yun.test/tomcat6103/webapps/ROOT/gateway/100000000010.pfx";
			password = "886047";
		}else if("100000000001".equals(sysid)){
			alias = "100000000001";
			path = "/home/bps.yun.test/tomcat6103/webapps/ROOT/gateway/100000000001.pfx";
			password = "502175";
		}else if ("100000000054".equals(sysid)){
			alias = "100000000054";
			path = "/home/bps.yun.test/tomcat6103/webapps/ROOT/gateway/100000000054.pfx";
			password = "557789";
		}


		PrivateKey privateKey = RSAUtil.loadPrivateKey(alias, path, password);
		logger.info("privateKey" + privateKey);
		StringBuilder sb = new StringBuilder();
		
		String reqStr1 = req.toString();
		sb.append(sysid).append(reqStr1).append(timestamp);
		String sign = RSAUtil.sign(privateKey, sb.toString());
		
		logger.info("sign1" + sign);
		Map map = new HashMap();
		map.put("sysid", sysid);
		map.put("sign", sign);
		map.put("timestamp", timestamp);
		map.put("v", "1.0");
		map.put("req", req.toString());
		sb.setLength(0);
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (java.util.Map.Entry) iterator.next();
			sb.append((String) entry.getKey()).append("=").append(URLEncoder.encode((String) entry.getValue(),"UTF-8")).append("&");
		}
		
		out.println(map.toString());

//		String href = "http://122.227.225.142:23661/gateway/payFront_test.jsp?"+sb.toString();
		String href = "http://122.227.225.142:23661/service/gateway/frontTrans.do?"+sb.toString();
//		String href = "https://yun.allinpay.com/yungateway/frontTrans.do?" + sb.toString();

		out.println("</ br>");
		
		out.println(href);
	%>

	<a href="<%=href%>" style="margin-left: 50%; font-size: 36px">前台支付</a>
	<br />

</body>
</html>