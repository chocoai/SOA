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
<%@page import="java.net.URLEncoder"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<%
		Logger logger = Logger.getLogger("aaa.jsp");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		JSONObject param = new JSONObject();
		param.put("bizUserId", "tltl");
		param.put("memberType",3);
		param.put("source", 1);
		param.put("extendParam", new JSONObject());
		
		JSONObject req = new JSONObject();
		req.put("service", "MemberService");
		req.put("method", "createMember");
		req.put("param", param);

		String sysid = "100000000002";
		String timestamp = sdf.format(new Date());
		String alias = "100000000002";
		String path = "/home/bps.yun.test/tomcat6103/webapps/ROOT/service/gateway/100000000002.pfx";
		String password = "473712";
		PrivateKey privateKey = RSAUtil.loadPrivateKey(alias, path, password);
		logger.info("privateKey" + privateKey);
		StringBuilder sb = new StringBuilder();
		
		String reqStr1 = req.toString();
		sb.append(sysid).append(reqStr1).append(timestamp);
		String sign = RSAUtil.sign(privateKey, sb.toString());
		
		logger.info("sign" + sign);
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

		out.println("http://122.227.225.142:23661/service/soa?"+sb.toString());
	%>

</body>
</html>