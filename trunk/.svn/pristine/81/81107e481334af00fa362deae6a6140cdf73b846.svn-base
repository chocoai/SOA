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
	//调用外部支付完成接口
	Map<String, Object> param = new HashMap<String, Object>();
	param.put("orderNo", commandNo);
	param.put("orderMoney", Long.valueOf(gatwayAmount));
	param.put("outTradeId", paymentOrderId);
	param.put("payChannelNo", Constant.PAY_CHANNEL_GETWAY);
	param.put("payInterfaceNo", Constant.PAY_INTERFACE_GETWAY_JJ);
	param.put("tradeTime", new Date());
	logger.info("payChannelDeposit参数：param=" + param);
	Extension.orderService.payChannelDeposit(param);
	%>

</body>
</html>