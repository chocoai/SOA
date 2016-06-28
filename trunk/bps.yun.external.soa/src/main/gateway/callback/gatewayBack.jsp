<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" errorPage="" %>
<%@page import="bps.common.Constant"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="bps.external.soa.process.Extension" %>
<%@page import="org.json.JSONObject"%>
<%@page import="bps.external.soa.SoaServiceUtil"%>
<%@page import="bps.common.ErrorCode"%>
<%@page import="bps.common.BizException"%>

<%
	Logger logger = Logger.getLogger("gatewayBack.jsp");
	logger.info("================================gatewayBack begin============================");

	SimpleDateFormat sdfOut = new SimpleDateFormat("yyyyMMDDhhmmss");
	SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//接收Server返回的支付结果
	String merchantId=request.getParameter("merchantId");
	String version=request.getParameter("version");
	String language=request.getParameter("language");
	String signType=request.getParameter("signType");
	String payType=request.getParameter("payType");
	String issuerId=request.getParameter("issuerId");
	String paymentOrderId=request.getParameter("paymentOrderId");
	String commandNo=request.getParameter("orderNo");
	String orderDatetime=request.getParameter("orderDatetime");
	String gatwayAmount=request.getParameter("orderAmount");
	String payDatetime=request.getParameter("payDatetime");
	String payAmount=request.getParameter("payAmount");
	String ext1=request.getParameter("ext1");
	String ext2=request.getParameter("ext2");
	String payResult=request.getParameter("payResult");
	String errorCode=request.getParameter("errorCode");
	String returnDatetime=request.getParameter("returnDatetime");
	String signMsg=request.getParameter("signMsg");
	boolean paySuccess = false;
	logger.info("----------------回调返回参数-------------------");
	logger.info("merchantId:"+merchantId+",version:"+version+",language:"+
			language+",signType:"+signType+",payType:"+payType+",issuerId:"+issuerId+",paymentOrderId:"+
			paymentOrderId+",orderNo:"+commandNo+",orderDatetime:"+orderDatetime+",gatwayAmount:"+
			gatwayAmount+",payDatetime:"+payDatetime+",payAmount:"+payAmount+",ext1:"+ext1+",ext2:"+
			ext2+",payResult:"+payResult+",errorCode:"+errorCode+",returnDatetime:"+returnDatetime);
	logger.info("signMsg:"+signMsg);
	logger.info("-----------------------------------");
	try {
		//验签是商户为了验证接收到的报文数据确实是支付网关发送的。
		//构造订单结果对象，验证签名。
		com.allinpay.ets.client.PaymentResult paymentResult = new com.allinpay.ets.client.PaymentResult();
		paymentResult.setMerchantId(merchantId);
		paymentResult.setVersion(version);
		paymentResult.setLanguage(language);
		paymentResult.setSignType(signType);
		paymentResult.setPayType(payType);
		paymentResult.setIssuerId(issuerId);
		paymentResult.setPaymentOrderId(paymentOrderId);
		paymentResult.setOrderNo(commandNo);
		paymentResult.setOrderDatetime(orderDatetime);
		paymentResult.setOrderAmount(gatwayAmount);
		paymentResult.setPayDatetime(payDatetime);
		paymentResult.setPayAmount(payAmount);
		paymentResult.setExt1(ext1);
		paymentResult.setExt2(ext2);
		paymentResult.setPayResult(payResult);
		paymentResult.setErrorCode(errorCode);
		paymentResult.setReturnDatetime(returnDatetime);
		//signMsg为服务器端返回的签名值。
		paymentResult.setSignMsg(signMsg);

		//signType为"1"时，必须设置证书路径。
		String sysid = ext1;
		Map<String, Object> piAppConf = SoaServiceUtil.getPiAppConf(Constant.PAY_INTERFACE_GETWAY_JJ, sysid);
		String TLCertUrl = (String)piAppConf.get("tltcerPath");
		paymentResult.setCertPath(TLCertUrl);

		//验证签名：返回true代表验签成功；否则验签失败。
		boolean verifyResult = paymentResult.verify();
		logger.info("verifyResult==============="+verifyResult);
		if(verifyResult == false){
			throw new Exception("验签失败。");
		}

		//验签成功，还需要判断订单状态，为"1"表示支付成功。
		String userId = "";
		JSONObject ret = new JSONObject();
		Map<String, Object> orderEntity = null;

		int index = commandNo.indexOf(Constant.COMMAND_SPLIT_SIGN);
		String orderNo = commandNo.substring(0, index);
		if(index >= 0) {
			orderEntity = Extension.orderService.getOrder(orderNo);

			if(orderEntity == null || orderEntity.isEmpty()){
				throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
			}
		}else{
			logger.error("支付指令号不正确。");
			throw new BizException(ErrorCode.OTHER_ERROR, "支付指令号不正确");
		}

		//支付成功
		if(payResult.equals("1")){
			List<Map<String, Object>> commandsList = Extension.orderService.getCommands(orderNo);
			if(commandsList == null || commandsList.isEmpty()){
				throw new BizException(ErrorCode.PAY_ERROR, "没有网关支付指令。");
			}
//			Map<String, Object> commandEntity = commandsList.get(0);
//			for(Map<String, Object> temp : commandsList){
//				if("1".equals(temp.get("seq_no").toString())){
//					commandEntity = temp;
//
//					break;
//				}
//			}
//			if(commandEntity == null || commandEntity.isEmpty()){
//				throw new BizException(ErrorCode.OTHER_ERROR, "支付指令不存在。");
//			}

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
		}else{ //支付失败
			throw new BizException(ErrorCode.PAY_ERROR, "支付失败。");
		}

	}catch(Exception e){
		logger.error(e.getMessage(), e);
	}

	logger.info("================================gatewayBack end============================");
%>
