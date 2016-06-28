<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" errorPage="" %>
<%@page import="bps.common.Constant"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="bps.external.soa.process.Extension" %>

<%
	Logger logger = Logger.getLogger("gatewayBack.jsp");
	logger.info("================================gatewayShCL begin============================");

	SimpleDateFormat sdfOut = new SimpleDateFormat("yyyyMMDDhhmmss");
	SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	try {
			//调用外部支付完成接口
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("orderNo", commandNo);
			param.put("orderMoney", Long.valueOf(gatwayAmount));
			param.put("outTradeId", paymentOrderId);
			param.put("payChannelNo", Constant.PAY_CHANNEL_GETWAY);
			param.put("payInterfaceNo", Constant.PAY_INTERFACE_GETWAY_JJ);
			param.put("tradeTime", new Date());
			param.put("paySerialNo", commandNo);
			logger.info("payChannelDeposit参数：param=" + param);
			Extension.orderService.payChannelDeposit(param);
	}catch(Exception e){
		logger.error(e.getMessage(), e);
	}

	logger.info("================================gatewayBack end============================");
%>

