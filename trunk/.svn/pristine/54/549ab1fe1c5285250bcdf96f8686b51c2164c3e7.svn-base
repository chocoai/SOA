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
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.allinpay.ets.client.SecurityUtil"%>
<%@page import="redis.clients.jedis.Jedis"%>
<%@page import="redis.clients.jedis.JedisPool"%>
<%@page import="bps.common.JedisUtils"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<script type="text/javascript">
<%
try{
	logger.info("================================payFront.jsp start=================================");
	
	//获取参数
	bizId  = (String)request.getParameter("sysid");
	String sign = request.getParameter("sign");
	String timestamp = request.getParameter("timestamp");
	String v = request.getParameter("v");
	String req = request.getParameter("req");
    JSONObject reqObject = new JSONObject(req);
    JSONObject param = (JSONObject)reqObject.get("param");
    String bizUserId = (String)param.get("bizUserId");
    String bizOrderNo = (String)param.get("bizOrderNo"); 
    String verificationCode = (String)param.get("verificationCode");
    String consumerIp = (String)param.get("consumerIp");

    logger.info("bizUserId=" + bizUserId);
    logger.info("参数bizId=" + bizId + ",sign=" + sign + ",timestamp=" + timestamp + ",v=" + v + ",req=" + req);
	
	//验证参数
	if(StringUtils.isBlank(bizId))
		throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizId为空或null");
	if(StringUtils.isBlank(sign))
		throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sign为空或null");
	if(StringUtils.isBlank(timestamp))
		throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数timestamp为空或null");
	if(req == null)
		throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数req为null");
	if(StringUtils.isBlank(bizUserId))
		throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
	if(StringUtils.isBlank(bizOrderNo))
		throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null");
	if(StringUtils.isBlank(consumerIp))
		throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数consumerIp为空或null");
	
	//检查订单是否存在
	Long applicationId = SoaServiceUtil.getApplicationId(bizId);
	logger.info("getOrder参数：applicationId=" + applicationId + ",bizOrderNo=" + bizOrderNo);
	Map<String, Object> orderEntity = Extension.orderService.getOrder(applicationId, bizOrderNo);
	logger.info("getOrder返回：" + orderEntity);
	if(orderEntity == null || orderEntity.isEmpty()){
		throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
	}
	String orderNo = (String)orderEntity.get("orderNo");
	Long orderMoney = (Long)orderEntity.get("order_money");
	payCompleteFrontUrl = (String)orderEntity.get("frontUrl");
	logger.info("payCompleteFrontUrl=" + payCompleteFrontUrl);
	
	//签名验证
	SoaServiceUtil.validateRsaSign(bizId, req, timestamp, sign);
	
	//获取会员实体并检查用户是否存在
	logger.info("getUserInfo参数：applicationId=" + applicationId + ",bizUserId=" + bizUserId);
	Map<String, Object> memberEntity = Extension.memberService.getUserInfo(applicationId, bizUserId);
	logger.info("getUserInfo返回：" + memberEntity);
	if(memberEntity == null || memberEntity.isEmpty()){
		throw new BizException(ErrorCode.USER_NOTEXSIT, "用户不存在。");
	}
	Long memberId = (Long)memberEntity.get("id");
	String phone = (String)memberEntity.get("phone");
	
	//检查订单是否过期
	Date ordErexpireDatetime = (Date)orderEntity.get("ordErexpireDatetime");
	if(ordErexpireDatetime.before(new Date())){
		throw new BizException(ErrorCode.ORDER_PASE_DUE, "订单过期。");
	}
	
	//判断订单状态是否是未支付
	Long orderState = (Long)orderEntity.get("order_state");
	if(!orderState.equals(Constant.ORDER_STATE_WAIT_PAY)){
		throw new BizException(ErrorCode.ORDER_NOT_UNPAY, "订单不是未支付状态。");
	}
	
	//获取网关支付信息，并进行判断
	List<Map<String, Object>> commandsList = Extension.orderService.getCommands(orderNo);
	if(commandsList == null || commandsList.isEmpty()){
		throw new BizException(ErrorCode.PAY_ERROR, "没有网关支付指令。"); 
	}
	
	Map<String, Object> gatewayPayInfo = null;
	Long lastCommandSequence = 0L;  //最后一条支付指令号的顺序号
	String gatewayTargetAccountTypeCodeNo = null; //网关充值的目标账户集
	Long gatewayAmount = 0L;  //网关支付的金额
	for(Map<String, Object> temp : commandsList){
		if("1".equals(temp.get("seq_no").toString()) && Constant.PAY_INTERFACE_GETWAY_JJ.equals(temp.get("pay_interfaceNo").toString())){
			gatewayTargetAccountTypeCodeNo = (String)temp.get("target_account_codeNo");
			gatewayAmount = (Long)temp.get("trade_money");
			gatewayPayInfo = temp;
			payType = ((Long)temp.get("pay_type")).toString();
			accountNo = (String)temp.get("accountNo");
			
			logger.info("accountNo1:" + accountNo);
		}
		
		Long tempSeqNo = (Long)temp.get("seq_no");
		lastCommandSequence = tempSeqNo > lastCommandSequence ? tempSeqNo : lastCommandSequence;
	}

	if(gatewayPayInfo == null){
		throw new BizException(ErrorCode.OTHER_ERROR, "支付方式中不存在网关。");
	}
	commandNo = (String)gatewayPayInfo.get("command_no");
	Long commandMoney = (Long)gatewayPayInfo.get("trade_money");
	Long cardType = (Long)gatewayPayInfo.get("card_type");
	
	boolean isAccountPay = false;  //是否有账户余额支付
	
	//判断余额是否足够
	for(Map<String, Object> temp : commandsList){
		//排除网关支付指令,并获取网关支付的目标账户的账户集类型
		if("1".equals(temp.get("seq_no").toString())){
			continue;
		}
		//排除手续费
		if(Constant.TRADE_TYPE_TRANSFER.equals((Long)temp.get("trade_type")) && Constant.SUB_TRADE_TYPE_FEE.equals((Long)temp.get("sub_trade_type"))){
			continue;
		}
		
		//logger.info("gatewayAmount=" + gatewayAmount);
		Long accountSetId = (Long)temp.get("account_type_id");
		//logger.info("accountSetId=" + accountSetId);
		Long accountPayAmount = (Long)temp.get("trade_money");
		//logger.info("accountPayAmount=" + accountPayAmount);
		//如果是网关支付的目标账户集，则需要减去网关支付的金额
		if(gatewayTargetAccountTypeCodeNo.equals((String)temp.get("account_codeNo"))){
			accountPayAmount -= gatewayAmount;
		}
		
		//logger.info("accountPayAmount=" + accountPayAmount);
		
		Map<String, Object> accountSetEntity = Extension.accountService.getMemberAccountByType(memberId, accountSetId);
		if(accountSetEntity == null || accountSetEntity.isEmpty()){
			throw new BizException(ErrorCode.OTHER_ERROR, "账户余额不足。");
		}
		
		Long accountAmount = (Long)accountSetEntity.get("amount");
		Long freezeAmount = (Long)accountSetEntity.get("freeze_amount");
		//logger.info("accountAmount=" + accountAmount);
		if(accountPayAmount > (accountAmount - freezeAmount)){
			throw new BizException(ErrorCode.OTHER_ERROR, "账户余额不足。");
		}
		
		if(accountPayAmount > 0){
			isAccountPay = true;
		}
	}
	
	//判断准备金是否充足
	//logger.info("checkAgencyFees参数：orderNo=" + orderNo);
	Extension.orderService.checkAgencyFees(orderNo);
	
	//如有验证码，则检测
	if(isAccountPay == true){
		if(verificationCode != null){
			//logger.info("checkVerificationCode参数：applicationId=" + 1 + ",phone=" + phone + ",verificationCodeType=" + Constant.VERIFICATION_CODE_AUTH_PAY + ",verificationCode=" + verificationCode);
			SoaServiceUtil.checkVerificationCode(1L, phone, Constant.VERIFICATION_CODE_AUTH_PAY, verificationCode);
		}
		else
			throw new BizException(ErrorCode.VERIFICATIONCODE_ERROR, "请输入验证码。");
	}
	
	Map<String, Object> cacheValue = SoaServiceUtil.getPiAppConf(Constant.PAY_INTERFACE_GETWAY_JJ, bizId);
	serverUrl = (String)Extension.paramMap.get("AlipayConfig.gateway.pay.serverUrl"); //通联支付 提交地址 
	String key = (String)cacheValue.get("cert_key"); 		 //通联支付 安全校验码 
	//字符集 默认填1，固定选择值：1、2、3；1代表UTF-8；2代表GBK；3代表GB2312；
	inputCharset= (String)cacheValue.get("input_charset");
	//前台通知页面
	pickupUrl = (String)Extension.paramMap.get("AlipayConfig.gateway.pickupUrl");
	//后台回调页面
	receiveUrl = (String)Extension.paramMap.get("AlipayConfig.gateway.receiveUrl");
	//接口版本  固定选择值：v1.0、v2.0；注意为小写字母，无特殊需求请一律填写v1.0该字段决定payType、issuerId是否参与签名
	version = (String)cacheValue.get("version");
	//语言种类 固定值：1；1代表简体中文、2代表繁体中文、3代表英文
	language= (String)cacheValue.get("use_language");
	//签名类型  默认填1，固定选择值：0、1；0表示客户用MD5验签，1表示客户用证书验签
	signType = (String)cacheValue.get("sign_type");
	//通联支付 商户号  
	merchantId = (String)cacheValue.get("merchant_id");
	payerName = "";
	payerEmail = "";
	payerTelephone = "";
	payerIDCard = "";
	pid = "";
	orderAmount = commandMoney.toString();
	//订单金额币种类型  默认填0-人民币0和156代表人民币、840代表美元、344代表港币等
	orderCurrency = (String)Extension.paramMap.get("AlipayConfig.gateway.orderCurrency");
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	orderDatetime = df.format((Date)orderEntity.get("create_time"));	
	orderExpireDatetime = df.format((Date)orderEntity.get("ordErexpireDatetime"));
	productName = "";
	productPrice = "";
	productNum = "";
	productId = "";
	productDesc = "";
	ext1 = bizId;
	ext2 = (String)orderEntity.get("frontUrl");
	String extTL = "";
	issuerId = (String)gatewayPayInfo.get("bank_code");
	pan = "";
	if("28".equals(payType)){
		logger.info("1");
		String certPath = (String)cacheValue.get("tltcerPath");
		logger.info("certPath:" + certPath);
		pan = SecurityUtil.encryptByPublicKey(certPath, accountNo);
		logger.info("accountNo1:" + accountNo);
		logger.info("2");
	}
	
	logger.info("pan=" + pan);
	tradeNature = "";
	
	//构造订单请求对象，生成signMsg。
	com.allinpay.ets.client.RequestOrder requestOrder = new com.allinpay.ets.client.RequestOrder();
	if(null!=inputCharset&&!"".equals(inputCharset)){
		requestOrder.setInputCharset(Integer.parseInt(inputCharset));
	}
	requestOrder.setPickupUrl(pickupUrl);
	requestOrder.setReceiveUrl(receiveUrl);
	requestOrder.setVersion(version);
	if(null!=language&&!"".equals(language)){
		requestOrder.setLanguage(Integer.parseInt(language));
	}
	requestOrder.setSignType(Integer.parseInt(signType));
	requestOrder.setPayType(Integer.parseInt(payType));
	requestOrder.setIssuerId(issuerId);
	requestOrder.setMerchantId(merchantId);
	requestOrder.setPayerName(payerName);
	requestOrder.setPayerEmail(payerEmail);
	requestOrder.setPayerTelephone(payerTelephone);
	requestOrder.setPayerIDCard(payerIDCard);
	requestOrder.setPid(pid);
	requestOrder.setOrderNo(commandNo);
	requestOrder.setOrderAmount(Long.parseLong(orderAmount));
	requestOrder.setOrderCurrency(orderCurrency);
	requestOrder.setOrderDatetime(orderDatetime);
	requestOrder.setOrderExpireDatetime(orderExpireDatetime);
	requestOrder.setProductName(productName);
	if(null!=productPrice&&!"".equals(productPrice)){
		requestOrder.setProductPrice(Long.parseLong(productPrice));
	}
	if(null!=productNum&&!"".equals(productNum)){
		requestOrder.setProductNum(Integer.parseInt(productNum));
	}	
	requestOrder.setProductId(productId);
	requestOrder.setProductDesc(productDesc);
	requestOrder.setExt1(ext1);
	requestOrder.setExt2(ext2);
	requestOrder.setExtTL(extTL);//通联商户拓展业务字段，在v2.2.0版本之后才使用到的，用于开通分账等业务
	requestOrder.setPan(pan);
	requestOrder.setTradeNature(tradeNature);
	requestOrder.setKey(key); //key为MD5密钥，密钥是在通联支付网关会员服务网站上设置。

	String strSrcMsg = requestOrder.getSrc(); // 此方法用于debug，测试通过后可注释。
	logger.info("加密前的参数："+strSrcMsg);
	strSignMsg = requestOrder.doSign(); // 签名，设为signMsg字段值。

	logger.info("参数：serverUrl=" + serverUrl + ",inputCharset=" + inputCharset + ",pickupUrl=" + pickupUrl + ",receiveUrl=" + receiveUrl + ",version="+ version + ",language"
	+ language + ",signType=" + signType + ",merchantId=" + merchantId + ",payerName=" + payerName + ",payerEmail=" + payerEmail + ",payerTelephone=" + payerTelephone
	+ ",payerIDCard=" + payerIDCard + ",pid=" + pid + ",_orderNo=" + commandNo + ",orderAmount=" + orderAmount + ",orderCurrency=" + orderCurrency + ",orderDatetime=" +
	orderDatetime + ",orderExpireDatetime=" + orderExpireDatetime + ",productName=" + productName + ",productId=" + productId + ",productDesc=" + productDesc +
	",ext1=" + ext1 + ",ext2=" + ext2 + ",payType=" + payType + ",issuerId=" + issuerId + ",pan=" + pan + ",tradeNature=" + tradeNature + ",strSignMsg=" + strSignMsg);
	logger.info("================================payFront.jsp end=================================");
}catch(BizException bizE){
	logger.error(bizE.getMessage(), bizE);
	
	isError = true;
}catch(Exception e){
	logger.error(e.getMessage(), e);
	
	isError = true;
}
%>


<%!
String serverUrl;
String inputCharset;
String pickupUrl;
String receiveUrl;
String version;
String language;
String signType;
String merchantId;
String payerName;
String payerEmail;
String payerTelephone;
String payerIDCard;
String pid;
String commandNo;
String orderAmount;
String orderCurrency;
String orderDatetime;
String orderExpireDatetime;
String productName;
String productPrice;
String productNum;
String productId;
String productDesc;
String ext1;
String ext2;
String payType;
String issuerId;
String pan;
String tradeNature;
String strSignMsg;
String accountNo;

Logger logger = Logger.getLogger("payFront.jsp");
String payCompleteFrontUrl = null;
String payCompleteFrontUrlAll = null;
String bizId = null;
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
boolean isError = false;
String returnUrl = null;

%>
</script>
</head>

<body onload="onSubmit();">
	<form name="form2" id="form2" action="<%= serverUrl%>" method="post">
		<input type="hidden" name="inputCharset" value="<%=inputCharset%>"/>
		<input type="hidden" name="pickupUrl" value="<%=pickupUrl%>"/>
		<input type="hidden" name="receiveUrl" value="<%=receiveUrl%>" />
		<input type="hidden" name="version" value="<%=version %>"/>
		<input type="hidden" name="language" value="<%=language %>" />
		<input type="hidden" name="signType" value="<%=signType%>"/>
		<input type="hidden" name="merchantId" value="<%=merchantId%>" />
		<input type="hidden" name="payerName" value="<%=payerName%>"/>
		<input type="hidden" name="payerEmail" value="<%=payerEmail%>" />
		<input type="hidden" name="payerTelephone" value="<%=payerTelephone %>" />
		<input type="hidden" name="payerIDCard" value="<%=payerIDCard %>" />
		<input type="hidden" name="pid" value="<%=pid%>"/>
		<input type="hidden" name="orderNo" value="<%=commandNo%>" />
		<input type="hidden" name="orderAmount" value="<%=orderAmount %>"/>
		<input type="hidden" name="orderCurrency" value="<%=orderCurrency%>" />
		<input type="hidden" name="orderDatetime" value="<%=orderDatetime%>" />
		<input type="hidden" name="orderExpireDatetime" value="<%=orderExpireDatetime %>"/>
		<input type="hidden" name="productName" value="<%=productName%>" />
		<input type="hidden" name="productPrice" value="<%=productPrice%>" />
		<input type="hidden" name="productNum" value="<%=productNum %>"/>
		<input type="hidden" name="productId" value="<%=productId%>" />
		<input type="hidden" name="productDesc" value="<%=productDesc%>" />
		<input type="hidden" name="ext1" value="<%=ext1%>" />
		<input type="hidden" name="ext2" value="<%=ext2%>" />
		<input type="hidden" name="payType" value="<%=payType%>" />
		<input type="hidden" name="issuerId" value="<%=issuerId%>" />
		<input type="hidden" name="pan" value="<%=pan %>" />
		<input type="hidden" name="tradeNature" value="<%=tradeNature %>" />
		<input type="hidden" name="signMsg" value="<%=strSignMsg %>" />
	 </form>
<script type="text/javascript">
    function onSubmit(){
    	var isError = <%=isError %>;
    	
    	if(isError == true){
    		//alert("a");
    		window.location.href = 'error/500.html';
    		
    	}else{
    		//alert("b");
    		var form2 = document.getElementById('form2');
    	    form2.submit();
    	}
    }
</script>
</body>
</html>