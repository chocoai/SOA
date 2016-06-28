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
<%@page import="java.security.PublicKey"%>
<%@page import="java.net.URLEncoder"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<%
		Logger logger = Logger.getLogger("aaa.jsp");
		
		String sysid = request.getParameter("sysid");
		String sign = request.getParameter("sign");
		
		logger.info("sign=" + sign);
		
		String timestamp = request.getParameter("timestamp");
		String v = request.getParameter("v");
		String rps = request.getParameter("rps");
		out.println(rps);
		JSONObject rpsObject = new JSONObject(rps);
		
		out.println("sysid=" + sysid + ",,sign" + sign + ",,timestamp=" + timestamp + ",,v=" + v + ",,rps=" +rps);

		String _sysid = "100000000002";
		String _alias = "100000000002";
		String _path = "/home/bps.yun.test/tomcat6103/webapps/ROOT/gateway/100000000002.pfx";
		String _password = "697057";
		if("100000000001".equals(sysid)){

			_alias = "111111111111";
			_path = "/home/bps.yun.test/tomcat6103/webapps/ROOT/gateway/111111111111.pfx";
			_password = "160692";
		}
		PublicKey publicKey = RSAUtil.loadPublicKey(_alias, _path, _password);
		boolean isVerify = RSAUtil.verify(publicKey, sysid + rps + timestamp, sign);
		
		
		//Boolean ret = bps.external.soa.SoaServiceUtil.validateRsaSign(sysid, rps, timestamp, sign);
		
		out.println("签名验证结果：" + isVerify);
	%>

</body>
</html>