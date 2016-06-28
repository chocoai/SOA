<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" errorPage="" %>
<%@page import="bps.common.Constant"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="bps.external.soa.process.Extension" %>

<%
	Logger logger = Logger.getLogger("certPayBack.jsp");

	try{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("orderNo", "1606060570430850D1");
		param.put("orderMoney", Long.valueOf(1));
		param.put("outTradeId", "201606061542141180");
		param.put("payChannelNo", Constant.PAY_CHANNEL_CERT_PAY);
		param.put("payInterfaceNo", Constant.PAY_INTERFACE_CERT_PAY);
		param.put("tradeTime", new Date());

		Extension.orderService.payChannelDeposit(param);

		param.put("orderNo", "1606060570430850D1");
		param.put("orderMoney", Long.valueOf(1));
		param.put("outTradeId", "201606061412406203");
		param.put("payChannelNo", Constant.PAY_CHANNEL_CERT_PAY);
		param.put("payInterfaceNo", Constant.PAY_INTERFACE_CERT_PAY);
		param.put("tradeTime", new Date());

		Extension.orderService.payChannelDeposit(param);
	}catch(Exception e){
		logger.error(e.getMessage(), e);
	}
%>

	