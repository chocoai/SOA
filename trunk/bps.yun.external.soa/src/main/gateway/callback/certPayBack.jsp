<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" errorPage="" %>
<%@page import="bps.common.Constant"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="bps.external.soa.process.Extension" %>
<%@page import="org.json.JSONObject"%>
<%@page import="bps.external.soa.SoaServiceUtil"%>
<%@page import="bps.common.MD5Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>

<%
	Logger logger = Logger.getLogger("certPayBack.jsp");
	logger.info("================================移动认证支付SDK后台异步通知开始============================");

	Map requestNames = new HashMap<String, String>();

	//获取接收参数
	String merchantId = request.getParameter("merchantId");
	String version = request.getParameter("version");
	String language = request.getParameter("language");
	String signType = request.getParameter("signType");
	String payType = request.getParameter("payType");
	String issuerId = request.getParameter("issuerId");
	String paymentOrderId = request.getParameter("paymentOrderId");
	String commandNo = request.getParameter("orderNo");
	String orderDatetime = request.getParameter("orderDatetime");
	String orderAmount = request.getParameter("orderAmount");
	String payDatetime = request.getParameter("payDatetime");
	String payAmount = request.getParameter("payAmount");
	String ext1 = request.getParameter("ext1");
	String ext2 = request.getParameter("ext2");
	String payResult = request.getParameter("payResult");
	String errorCode = request.getParameter("errorCode");
	String returnDatetime = request.getParameter("returnDatetime");
	String signMsg = request.getParameter("signMsg");

	logger.info("认证支付后台通知参数：merchantId=" + merchantId + ",version=" + version + ",language=" + language + ",signType=" + signType + ",payType=" + payType
			+ ",issuerId=" + issuerId + ",paymentOrderId=" + paymentOrderId + ",orderNo=" + commandNo + ",orderDatetime=" + orderDatetime + ",orderAmount=" + orderAmount
			+ ",payDatetime=" + payDatetime + ",payAmount=" + payAmount + ",ext1=" + ext1 + ",ext2=" + ext2 + ",payResult=" + payResult + ",errorCode=" + errorCode
			+ ",returnDatetime=" + returnDatetime + ",signMsg=" + signMsg);

	//验签
	StringBuilder sb = new StringBuilder();
	if(!StringUtils.isBlank(merchantId))
		sb.append("merchantId=").append(merchantId).append("&");
	if(!StringUtils.isBlank(version))
		sb.append("version=").append(version).append("&");
	if(!StringUtils.isBlank(language))
		sb.append("language=").append(language).append("&");
	if(!StringUtils.isBlank(signType))
		sb.append("signType=").append(signType).append("&");
	if(!StringUtils.isBlank(payType))
		sb.append("payType=").append(payType).append("&");
	if(!StringUtils.isBlank(issuerId))
		sb.append("issuerId=").append(issuerId).append("&");
	if(!StringUtils.isBlank(paymentOrderId))
		sb.append("paymentOrderId=").append(paymentOrderId).append("&");
	if(!StringUtils.isBlank(commandNo))
		sb.append("orderNo=").append(commandNo).append("&");
	if(!StringUtils.isBlank(orderDatetime))
		sb.append("orderDatetime=").append(orderDatetime).append("&");
	if(!StringUtils.isBlank(orderAmount))
		sb.append("orderAmount=").append(orderAmount).append("&");
	if(!StringUtils.isBlank(payDatetime))
		sb.append("payDatetime=").append(payDatetime).append("&");
	if(!StringUtils.isBlank(payAmount))
		sb.append("payAmount=").append(payAmount).append("&");
	if(!StringUtils.isBlank(ext1))
		sb.append("ext1=").append(ext1).append("&");
	if(!StringUtils.isBlank(ext2))
		sb.append("ext2=").append(ext2).append("&");
	if(!StringUtils.isBlank(payResult))
		sb.append("payResult=").append(payResult).append("&");
	if(!StringUtils.isBlank(errorCode))
		sb.append("errorCode=").append(errorCode).append("&");
	if(!StringUtils.isBlank(returnDatetime))
		sb.append("returnDatetime=").append(returnDatetime).append("&");

	try{
		//获取key
		int index = commandNo.indexOf(Constant.COMMAND_SPLIT_SIGN);
		String orderNo = commandNo.substring(0, index);
		Map<String, Object> orderEntity = Extension.orderService.getOrder(orderNo);
		if(orderEntity == null || orderEntity.isEmpty()){
			logger.error("订单不存在。");
			throw new Exception("订单不存在。");
		}
		String orgNo = (String)orderEntity.get("orgNo");
		JSONObject payInterfaceAppConf = new JSONObject(SoaServiceUtil.getCacheByKey(Constant.REDIS_KEY_PI_APP_CONF));
		String cacheKey = Constant.PAY_INTERFACE_CERT_PAY + "_" + orgNo;
		logger.info("cacheKey=" + cacheKey);

		JSONObject certPayCacheValue = (JSONObject)payInterfaceAppConf.get(cacheKey);
		if(certPayCacheValue == null || certPayCacheValue.length() == 0){
			logger.error("此用户不支持移动认证支付。");
			throw new Exception("此用户不支持移动认证支付。");
		}

		//检查订单生成时间是否一致
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String orderDatetimeYun = sdf.format((Date)orderEntity.get("create_time"));
		if(!orderDatetimeYun.equals(orderDatetime)){
			logger.error("订单生成时间不一致。");
			throw new Exception("订单生成时间不一致。");
		}

		String key = (String)certPayCacheValue.get("mobile_cert_pay_key");
		logger.info("key=" + key);
		if(StringUtils.isBlank(key)){
			logger.error("密钥为空。");
			throw new Exception("密钥为空。");
		}

		sb.append("key=").append(key);
		logger.info("sb=" + sb.toString());
		String calculateSign = MD5Util.MD5(sb.toString());
		logger.info("calculateSign=" + calculateSign);
		if(!calculateSign.equals(signMsg)){
			logger.error("认证支付验签失败。");
			throw new Exception("认证支付验签失败。");
		}

		//调用外部支付成功接口
		if(!"1".equals(payResult)){
			logger.info("认证支付未成功。");
			throw new Exception("认证支付未成功。");
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("orderNo", commandNo);
		param.put("orderMoney", Long.valueOf(payAmount));
		param.put("outTradeId", paymentOrderId);
		param.put("payChannelNo", Constant.PAY_CHANNEL_CERT_PAY);
		param.put("payInterfaceNo", Constant.PAY_INTERFACE_CERT_PAY);
		param.put("tradeTime", new Date());

		/*
		//查询指令
		String orderNo = "1602020040802780";
		logger.info("getAllCommands参数orderNo=" + orderNo);
		List<Map<String, Object>> commands = Extension.orderService.getAllCommands(orderNo);
		logger.info("getAllCommands返回：" + commands);

		String commandNo = (String)commands.get(0).get("command_no");

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("orderNo", commandNo);
		param.put("orderMoney", 1L);
		param.put("outTradeId", "aaaaa");
		param.put("payChannelNo", Constant.PAY_CHANNEL_CERT_PAY);
		param.put("payInterfaceNo", Constant.PAY_INTERFACE_CERT_PAY);
		param.put("tradeTime", new Date());
		*/

		Extension.orderService.payChannelDeposit(param);
	}catch(Exception e){
		logger.error(e.getMessage(), e);
	}

	logger.info("================================cert pay back end============================");
%>

	