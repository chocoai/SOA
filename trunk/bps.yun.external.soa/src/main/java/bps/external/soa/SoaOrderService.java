package bps.external.soa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import bps.common.JedisUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import apf.util.BusinessException;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.external.soa.process.Extension;
import ime.soa.ISOAService;

public class SoaOrderService implements ISOAService {
	private static Logger logger = Logger.getLogger(SoaMemberService.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf_yyyy_MM_dd_HHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf_yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	
	public JSONObject doService(String arg0, JSONArray arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject doService(String s, JSONObject json) throws Exception {
		logger.info("s=" + s + " json=" + json);

		SoaServiceUtil.jsonTrim(json);
		logger.info( "json2=" + json);

		if("depositApply".equals(s))
			return depositApply(json);                    //充值    
		else if("withdrawApply".equals(s))
			return withdrawApply(json);                   //提现
		else if("consumeApply".equals(s))
			return consumeApply(json); 		              //消费    
		else if("agentCollectApply".equals(s))
			return agentCollectApply(json); 	              //代收
		else if("signalAgentPay".equals(s))
			return signalAgentPay(json);                  //单笔代付
		else if("batchAgentPay".equals(s))
			return batchAgentPay(json);                  //批量代付
		else if("pay".equals(s))
			return pay(json);                             //后台支付
		else if("entryGoods".equals(s))
			return entryGoods(json);                      //商品录入
		else if("freezeMoney".equals(s))
			return freezeMoney(json);                  //冻结金额
		else if("unfreezeMoney".equals(s))
			return unfreezeMoney(json);                  //解冻余额
		else if("refund".equals(s))
			return refund(json);                  //退款
		else if("failureBidRefund".equals(s))
			return failureBidRefund(json);                  //批量退款
		else if("cancel".equals(s))
			return cancel(json);                  //撤销
		else if("applicationTransfer".equals(s))
			return applicationTransfer(json);     //平台转账
		else if("queryBalance".equals(s))
			return queryBalance(json);                  //查询余额
		else if("getOrderDetail".equals(s))
			return getOrderDetail(json);                  //查询订单状态
//		else if("queryOrderDetail".equals(s))
//			return queryOrderDetail(json);                  //查询订单详情
		else if("queryOrderPayDetail".equals(s))
			return queryOrderPayDetail(json);                  //查询支付详情
		else if("queryInExpDetail".equals(s))
			return queryInExpDetail(json);                  //查询收支明细
		else if("transferCrossApplication".equals(s))
			return transferCrossApplication(json);                  //跨应用转账
		else if("getCertPaySign".equals(s))               //认证支付签名
			return getCertPaySign(json);
		else if("queryModifyGoods".equals(s))            //商品查询修改接口
			return queryModifyGoods(json);
		else if ("higherCardAuthApply".equals(s)){       //强实名认证支付
			return higherCardAuthApply(json);
		}
		else
	        throw new BusinessException("SOA.NoSuchMethod", (new StringBuilder()).append("找不到相应的服务:").append(getServiceName()).append(".").append(s).toString());
	}

	public String getServiceName() {
		// TODO Auto-generated method stub
		return "OrderService";
	}

	public boolean isMustLogined() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isMustValidateClient() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private JSONObject cancel(JSONObject json) throws BizException, JSONException{
		logger.info("===============cancel start==================");
		
		String bizNo = (String)json.get("bizNo");
		String sysid = (String)json.get("sysid");
		Long sourceMemberId = json.optLong("sourceMemberId");
		Long sourceAccountTypeId = json.optLong("sourceAccountTypeId");
		Long targetMemberId = json.optLong("targetMemberId");
		Long targetAccountTypeId = json.optLong("targetAccountTypeId");
		Long amount = json.optLong("amount");
		String remark = (String)json.get("remark");
		
		try {
			
			Extension.adjustAccountService.adjustAccount(bizNo, sysid, sourceMemberId, sourceAccountTypeId, targetMemberId, targetAccountTypeId, amount, remark);
			
			logger.info("===============cancel end==================");
			return new JSONObject();
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 充值申请
	 * @param json
	 * 			bizOrderNo				商户订单号
	 * 			bizUserId				商户系统用户标识
	 * 			accountSetNo			账户集编号			?用户怎么获取该参数
	 * 			amount					订单金额
	 * 			fee						手续费
	 * 			frontUrl				前台通知地址
	 * 			backUrl					后台通知地址
	 * 			ordErexpireDatetime		订单过期时间格式：yyyy-MM-dd HH:mm:ss
	 * 			payMethod	JSONArray
	 * 				QUICKPAY JSONObject
	 * 					bankCardNo		银行卡号，RSA加密。
	 * 					amount			支付金额
	 *				GATEWAY	JSONObject
	 *					bankCode		发卡机构
	 *					payType			网关支付类型
	 *					bankCardNo		当网关支付是Web认证支付（payType= 28）时，必填。RSA加密。
	 *					amount 			支付金额
	 * 			industryCode
	 * 			industryName
	 * 			source
	 * 			summary
	 * 			extendInfo
	 * @return	JSONObject
	 */
	private JSONObject depositApply(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.applyDeposit start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizOrderNo = (String)json.get("bizOrderNo");
			String accountSetNo = (String)json.get("accountSetNo");
			Long amount = json.isNull("amount") ? null : json.optLong("amount");
			Long fee = json.isNull("fee") ? null : json.optLong("fee");
			String frontUrl = (String)json.get("frontUrl");
			String backUrl = (String)json.get("backUrl");
			String ordErexpireDatetime = (String)json.get("ordErexpireDatetime");
			JSONObject payMethod = (JSONObject)json.get("payMethod");
			String industryCode = (String)json.get("industryCode");
			String industryName = (String)json.get("industryName");
			Long source = json.isNull("source") ? null : json.optLong("source");
			String summary = (String)json.get("summary");
			String extendInfo = (String)json.get("extendInfo");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(accountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountSetNo为空或null。");
			if(amount == null || amount <= 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为null或小于0。");
			if(fee == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数fee为null。");
			if(StringUtils.isBlank(backUrl))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数backUrl为空或null。");
			if(payMethod == null || payMethod.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数payMethod为空或null。");
			if(StringUtils.isBlank(industryCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数industryCode为空或null。");
			if(StringUtils.isBlank(industryName))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数industryName为空或null。");
			if(source == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数source为null。");
            
			//判断summary长度
			if(summary != null && summary.length() > 20)
				throw new BizException(ErrorCode.PARAM_ERROR, "summary长度不能大于20");

			JSONObject itsPayInfo = (JSONObject)payMethod.get("QUICKPAY");
			JSONObject gatewayPayInfo = (JSONObject)payMethod.get("GATEWAY");
			JSONObject posPay 			= (JSONObject)payMethod.get("POSPAY");
			JSONObject daikou     = (JSONObject)payMethod.get("DAIKOU");

			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "depositApply", version);
			
			//必要的检查
			//检查订单过期时间是否在规定范围内
			if(ordErexpireDatetime != null){
				SoaServiceUtil.checkOverdue(sdf, ordErexpireDatetime);
			}
			
				//检查终端来源
			SoaServiceUtil.checkSource(source);
			
				//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
				//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			
			Long memberId = (Long)memberEntity.get("id");
			String phone = (String)memberEntity.get("phone");
			
			//检查是否已经绑定手机
			Long memberType = (Long)memberEntity.get("member_type");
			//企业会员无需绑定手机，因为企业用户只能用网银充值。
			if(!Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType)){
				if (memberEntity.get("isPhone_checked") == null || !(Boolean) memberEntity.get("isPhone_checked"))
					throw new BizException(ErrorCode.NOT_BIND_PHONE, "未绑定手机。");
			}
				
			//检查是否有可用的支付方式。			
			SoaServiceUtil.chickPayMethod(payMethod, Constant.ORDER_TYPE_DEPOSIT);

			if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && itsPayInfo != null)
				throw new BizException(ErrorCode.OTHER_ERROR,"企业会员不能使用ITS进行充值");

//			if (posPay != null ){
//				throw new BizException(ErrorCode.NO_PAY_METHOD, "充值不能使用pos支付！");
//			}
			//检查手续费是否超过充值金额
			if(fee > amount){
				throw new BizException(ErrorCode.FEE_OVER_AMOUNT, "手续费大于充值金额。");
			}
					
			//检查是否是现金账户集。（以后已经去除这个检查，因为也可以是其他账户集，如积分）
			logger.info("getAccountTypeByNo参数：accountSetNo=" + accountSetNo);
			Map<String, Object> accountTypeEntity = Extension.accountService.getAccountTypeByNo(accountSetNo);
			logger.info("getAccountTypeByNo返回：" + accountTypeEntity);
			if(accountTypeEntity == null || accountTypeEntity.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT, "账户集不存在");
			}
			if(!Constant.BIZ_TYPE_CASH.equals(accountTypeEntity.get("biz_type"))){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "目前只支持现金账户集。");
			}
			//朱成 2016-06-14。检查帐户集是否在对应的应用下
			if(!applicationId.equals(accountTypeEntity.get("application_id"))){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "应用下没有对应的帐户集。");
			}
			Long payAmount = 0L;
			Long bindBankCardId = null;
			
			//支付信息组装
			List<Map<String, Object>> payInterfaceList = new ArrayList<>();
			Map<String, Object> payInterfaceMap = new HashMap<>();
				//快捷
			if(itsPayInfo != null){
				String bankCardNo = (String)itsPayInfo.get("bankCardNo");
				
				//检查是否实名认证
				if(!(Boolean)memberEntity.get("isIdentity_checked")){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先实名认证。");
				}
				
				//获取绑定银行卡id
				bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, memberId, bankCardNo);
				
				Long itsMoney = itsPayInfo.optLong("amount");
				payAmount += itsMoney;
				
				payInterfaceMap.put("tradeMoney", itsMoney);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_QUICK);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_OTHER);
				payInterfaceMap.put("bankId", bindBankCardId);
				payInterfaceMap.put("accountSetNo", accountSetNo);
				payInterfaceList.add(payInterfaceMap);
			}else if(gatewayPayInfo != null){ //网关
				Long gatewayMoney = gatewayPayInfo.optLong("amount");
				String bankCode = (String)gatewayPayInfo.get("bankCode");
				Long payType = gatewayPayInfo.optLong("payType");
				String bankCardNo = (String)gatewayPayInfo.get("bankCardNo");
				
				if(Constant.GATEWAY_PAY_METHOD_XY.equals(payType)){
					throw new BizException(ErrorCode.NOT_SUPPORT_XY_CARD, "不支持信用卡充值。");
				}
				
//				if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && payType != 4){
//					throw new BizException(ErrorCode.OTHER_ERROR, "企业用户只能使用企业网银。");
//				}

				if(payType.equals(28L)){
					if(StringUtils.isBlank(bankCardNo)){
						throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankCardNo为空。");
					}
				}
				
				payAmount += gatewayMoney;
				
				//移动认证支付
				if(payType.equals(27L)){
					payInterfaceMap.put("tradeMoney", gatewayMoney);
					payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_CERT_PAY);
					payInterfaceMap.put("call_type", Constant.CALL_TYPE_PAGE);
//					payInterfaceMap.put("bank_code", bankCode);
					payInterfaceMap.put("accountNo", bankCardNo == null ? null : SoaServiceUtil.rsaDencrypt(bizId, bankCardNo));
					payInterfaceMap.put("phone", phone);
					//payInterfaceMap.put("bank_name", bankName);
					//payInterfaceMap.put("card_type", bankCardType);
					payInterfaceMap.put("accountSetNo", accountSetNo);
					payInterfaceMap.put("pay_type", payType);
					payInterfaceList.add(payInterfaceMap);
				}else{
					//Long bankCardType = SoaServiceUtil.payTypeToCardType(payType);
					
					//获取银行名称
					//String bankName = SoaServiceUtil.getBankName(bankCode);					
					payInterfaceMap.put("tradeMoney", gatewayMoney);
					payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_GETWAY_JJ);
					payInterfaceMap.put("call_type", Constant.CALL_TYPE_PAGE);
					payInterfaceMap.put("bank_code", bankCode);
					payInterfaceMap.put("accountNo", bankCardNo == null ? null : SoaServiceUtil.rsaDencrypt(bizId, bankCardNo));
					payInterfaceMap.put("phone", phone);
					//payInterfaceMap.put("bank_name", bankName);
					//payInterfaceMap.put("card_type", bankCardType);
					payInterfaceMap.put("accountSetNo", accountSetNo);
					payInterfaceMap.put("pay_type", payType);
					payInterfaceList.add(payInterfaceMap);
				}
			}else if( posPay != null ){		//pos支付
				Long itsMoney = posPay.optLong("amount");
				payAmount += itsMoney;

				payInterfaceMap.put("tradeMoney", itsMoney);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_POS);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_OTHER);
				payInterfaceMap.put("accountSetNo", accountSetNo);
				payInterfaceList.add(payInterfaceMap);
			}else if(daikou != null){    //代扣
				String bankCardNo = (String)daikou.get("bankCardNo");

				//检查是否实名认证
				if(!Boolean.TRUE.equals(memberEntity.get("isIdentity_checked"))){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先实名认证。");
				}

				//获取绑定银行卡id
				bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, memberId, bankCardNo);

				//检查是否已经强实名认证
				if(!Boolean.TRUE.equals(memberEntity.get("is_higher_card"))){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先强实名认证。");
				}

				Long daikouMoney = daikou.optLong("amount");
				payAmount += daikouMoney;

				payInterfaceMap.put("tradeMoney", daikouMoney);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_UNION_DAIKOU);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_INTERFACE);
				payInterfaceMap.put("accountSetNo", accountSetNo);
				payInterfaceMap.put("bankId", bindBankCardId);
				payInterfaceList.add(payInterfaceMap);
			}


			logger.info("Constant.PAY_INTERFACE_POS:"+Constant.PAY_INTERFACE_POS);
			if(!payAmount.equals(amount))
				throw new BizException(ErrorCode.ORDER_MONEY_PAY_MONEY_NOT_EQUAL, "订单金额和支付金额不一致。");
			
			//组装其他信息
			Map<String, Object> extParams = new HashMap<>();
			extParams.put("bizOrderNo", bizOrderNo);
			extParams.put("order_money", amount);
			extParams.put("fee", fee);
			extParams.put("bankCardId", bindBankCardId);
			extParams.put("frontUrl", frontUrl);
			extParams.put("backUrl", backUrl);
			extParams.put("ordErexpireDatetime", ordErexpireDatetime == null ? null :sdf.parse(ordErexpireDatetime));
			extParams.put("industry_code", industryCode);
			extParams.put("industry_name", industryName);
			extParams.put("summary", summary);
			extParams.put("source", source);
//			extParams.put("accountSetNo", accountSetNo);
			extParams.put("extend_info", extendInfo);
			
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_DEPOSIT + ",tradeType=" + Constant.TRADE_TYPE_DEPOSIT + ",source=" + source + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + payInterfaceList + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.ORDER_TYPE_DEPOSIT, Constant.TRADE_TYPE_DEPOSIT, source, extParams, null, payInterfaceList, null);
			logger.info("applyOrder返回：" + orderEntity);
			
			JSONObject ret = new JSONObject();
			ret.put("orderNo", orderEntity.get("orderNo"));
			ret.put("bizOrderNo", bizOrderNo);
			ret.put("tradeNo", orderEntity.get("oriTraceNum"));
			ret.put("extendInfo", extendInfo);
			ret.put("receiveUrl", orderEntity.get("receiveUrl"));
			ret.put("certPayOrderNo", orderEntity.get("commandNo"));
			ret.put("orderDatetime", orderEntity.get("orderDatetime"));
			ret.put("paycode", orderEntity.get("paycode"));
			
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.applyDeposit end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}

	/**
	 * 提现申请
	 * @param json	json
	 * @return JSONObject
	 * @throws BusinessException
	 */
	private JSONObject withdrawApply(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.applyWithdraw start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizOrderNo = (String)json.get("bizOrderNo");
			String accountSetNo = (String)json.get("accountSetNo");
			Long amount = json.isNull("amount") ? null : json.optLong("amount");
			Long fee =  json.isNull("fee") ? null : json.optLong("fee");
			String backUrl = (String)json.get("backUrl");
			String ordErexpireDatetime = (String)json.get("ordErexpireDatetime");
			String bankCardNo = (String)json.get("bankCardNo");
			String industryCode = (String)json.get("industryCode");
			String industryName = (String)json.get("industryName");
			Long source = json.isNull("source") ? null : json.optLong("source");
			String summary = (String)json.get("summary");
			String extendInfo = (String)json.get("extendInfo");
			Long bankCardPro = json.isNull("bankCardPro") ? 0L : json.optLong("bankCardPro");
			String withdrawType = json.optString("withdrawType");
			if( withdrawType.equals("") ){
				withdrawType = "T0";
			}
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(accountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountSetNo为空或null。");
			if(amount == null || amount <= 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为null或小于0。");
			if(fee == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数fee为null。");
			if(StringUtils.isBlank(backUrl))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数backUrl为空或null。");
			if(StringUtils.isBlank(bankCardNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankCardNo为空或null。");
			if(StringUtils.isBlank(industryCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数industryCode为空或null。");
			if(StringUtils.isBlank(industryName))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数industryName为空或null。");
			if(source == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数source为null。");
			
			//判断summary长度
			if(summary != null && summary.length() > 20)
				throw new BizException(ErrorCode.PARAM_ERROR, "summary长度不能大于20");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "withdrawApply", version);
			
			//一些必要的检查
			//检查订单过期时间是否在规定范围内
			if(ordErexpireDatetime != null){
				SoaServiceUtil.checkOverdue(sdf, ordErexpireDatetime);
			}
			
			//检查手续费是否超过充值金额
			if( fee > amount ){
				throw new BizException(ErrorCode.FEE_OVER_AMOUNT, "手续费大于提现金额。");
			}
			
			//检查来源
			SoaServiceUtil.checkSource(source);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			
			Long memberId = (Long)memberEntity.get("id");
			
			//检查是否已经绑定手机
			if(memberEntity.get("isPhone_checked") == null  || !(Boolean)memberEntity.get("isPhone_checked")){
				throw new BizException(ErrorCode.NOT_BIND_PHONE, "未绑定手机。");
			}
			
			//检查是否实名认证
			if(Constant.MEMBER_TYPE_PERSON.equals(memberEntity.get("member_type"))){
				if(!(Boolean)memberEntity.get("isIdentity_checked")){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先实名认证。");
				}
			}		

			Map<String, Object> cardInfo = SoaServiceUtil.getBindBankCard(bizId,memberId,bankCardNo);
			logger.info("memberId:"+memberId);
			logger.info("bankCardNo:"+bankCardNo);
			logger.info("cardInfo:"+cardInfo);
			if( cardInfo.get("card_type").equals(Constant.BANK_CARD_XY) ){
				throw new BizException(ErrorCode.NOT_SUPPORT_XY_CARD, "提现不支持信用卡");
			}
			//验证传入的银行卡属性和绑定时对比。
			Long _bankCardPro = cardInfo.get("account_prop") == null ? 0L : (Long)cardInfo.get("account_prop");
			logger.info("^^_bankCardPro:"+_bankCardPro);
			logger.info("^^bankCardPro:"+bankCardPro);
			if( !bankCardPro.equals(_bankCardPro)){
				throw new BizException(ErrorCode.ORDER_ERROR, "传入的银行卡账号属性和绑定时不一至。");
			}
			if( memberEntity.get("member_type").equals(Constant.MEMBER_TYPE_ENTERPRISE)) {
				if (Constant.BANK_ACCOUNT_PROP_P.equals(bankCardPro) && !Boolean.TRUE.equals(cardInfo.get("is_person_card_checked"))) {
					throw new BizException(ErrorCode.ORDER_ERROR, "企业个人银行卡没有审核！");
				}
			}
			//检查提现银行卡是否绑定，获取提现银行卡的绑定号
			Long bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, memberId, bankCardNo);
			//检查是否是现金账户集。
			logger.info("getAccountTypeByNo参数：accountSetNo=" + accountSetNo);
			Map<String, Object> accountTypeEntity = Extension.accountService.getAccountTypeByNo(accountSetNo);
			logger.info("getAccountTypeByNo返回：" + accountTypeEntity);
			if(accountTypeEntity == null || accountTypeEntity.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT, "账户集不存在");
			} 
			if(!Constant.BIZ_TYPE_CASH.equals(accountTypeEntity.get("biz_type"))){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "只支持现金账户集。");
			}
			
			//组装数据
			//支付信息
			List<Map<String, Object>> payInterfaceList = new ArrayList<>();
			Map<String, Object> payInterfaceMap = new HashMap<>();
			payInterfaceMap.put("tradeMoney", amount);

			payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_TLT_DF);
			if (withdrawType.equals("T1")){
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_TLT_BACH_DF);
			}else{
				Map<String, Object> org = SoaServiceUtil.getApplication(bizId);
				Long withdrawReserveModel = org.get("withdrawReserveModel") == null ? 0L : (Long)org.get("withdrawReserveModel");
				logger.info("bizId:"+bizId);
				logger.info("withdrawReserveModel:"+withdrawReserveModel);
				if (withdrawReserveModel.equals(0L)){
					throw new BizException( ErrorCode.WITHDRAW_PAY_TYPE_NOT_SET, "没有设置提现准备金方式！" );
				}else if (withdrawReserveModel.equals( Constant.WITHDRAW_RESERVE_MODEL_ENTRUST) ){
					payInterfaceMap.put( "payInterFaceNo", Constant.PAY_INTERFACE_TLT_BACH_DF );
				}
			}

			payInterfaceMap.put("call_type", Constant.CALL_TYPE_INTERFACE);
			payInterfaceMap.put("bankId", bindBankCardId);
			payInterfaceMap.put("accountSetNo", accountSetNo);
			payInterfaceList.add(payInterfaceMap);
			
				//其他信息
			Map<String, Object> extParams = new HashMap<>();
			extParams.put("bizOrderNo", bizOrderNo);
			extParams.put("bankCardId", bindBankCardId);
			extParams.put("fee", fee);
			extParams.put("backUrl", backUrl);
			extParams.put("ordErexpireDatetime", ordErexpireDatetime == null ? null :sdf.parse(ordErexpireDatetime));
			extParams.put("industry_code", industryCode);
			extParams.put("industry_name", industryName);
			extParams.put("source", source);
			extParams.put("summary", summary);
			extParams.put("withdrawType", withdrawType);
//			extParams.put("accountSetNo", accountSetNo);
			extParams.put("extend_info", extendInfo);
			
			//调用应用层
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_WITHDRAW + ",tradeType=" + Constant.TRADE_TYPE_WITHDRAW + ",source=" + source + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + payInterfaceList + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.ORDER_TYPE_WITHDRAW, Constant.TRADE_TYPE_WITHDRAW, source, extParams, null, payInterfaceList, null);
			logger.info("applyOrder返回：" + orderEntity);
			
			//返回
			JSONObject ret = new JSONObject();
			ret.put("orderNo", orderEntity.get("orderNo"));
			ret.put("bizOrderNo", bizOrderNo);
			ret.put("extendInfo", extendInfo);
			
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.applyWithdraw end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 消费申请
	 * @param json 	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject consumeApply(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.applyConsume start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String payerId = (String)json.get("payerId");
			String recieverId = (String)json.get("recieverId");
			String bizOrderNo = (String)json.get("bizOrderNo");
			Long amount = json.isNull("amount") ? null : json.optLong("amount");
			Long fee = json.isNull("fee") ? null : json.optLong("fee");
			JSONArray splitRule = (JSONArray)json.get("splitRule");
			String frontUrl = (String)json.get("frontUrl");
			String backUrl = (String)json.get("backUrl");
			String showUrl = (String)json.get("showUrl");
			String ordErexpireDatetime = (String)json.get("ordErexpireDatetime");
			JSONObject payMethod = (JSONObject)json.get("payMethod");
			String goodsName = (String)json.get("goodsName");
			String goodsDesc = (String)json.get("goodsDesc");
			String industryCode = (String)json.get("industryCode");
			String industryName = (String)json.get("industryName");
			Long source = json.isNull("source") ? null : json.optLong("source");
			String summary = (String)json.get("summary");
			String extendInfo = (String)json.get("extendInfo");

			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(payerId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数payerId为空或null。");
			if(StringUtils.isBlank(recieverId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数recieverId为空或null。");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(amount == null || amount <= 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为null或小于0。");
			if(fee == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数fee为null。");
			if(StringUtils.isBlank(backUrl))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数backUrl为空或null。");
			if(payMethod == null || payMethod.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数payMethod为空或null。");
			if(StringUtils.isBlank(industryCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数industryCode为空或null。");
			if(StringUtils.isBlank(industryName))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数industryName为空或null。");
			if(source == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数source为null。");

			//判断summary长度
			if(summary != null && summary.length() > 20)
				throw new BizException(ErrorCode.PARAM_ERROR, "summary长度不能大于20");
			
			JSONArray accountPayInfo = (JSONArray)payMethod.get("BALANCE");
			JSONObject itsPayInfo = (JSONObject)payMethod.get("QUICKPAY");
			JSONObject gatewayPayInfo = (JSONObject)payMethod.get("GATEWAY");
			JSONObject posPay 			= payMethod.optJSONObject("POSPAY");
			JSONObject daikou     = (JSONObject)payMethod.get("DAIKOU");

			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "consumeApply", version);

			//检查支付方式
			SoaServiceUtil.chickPayMethod(payMethod, Constant.ORDER_TYPE_SHOPPING);

			//一些必要的检查
				//检查订单过期时间是否在规定范围内
			logger.info("ordErexpireDatetime=" + ordErexpireDatetime);
			if(ordErexpireDatetime != null){
				SoaServiceUtil.checkOverdue(sdf, ordErexpireDatetime);
			}

				//检查来源
			SoaServiceUtil.checkSource(source);

				//获取会员实体并检查用户是否存在
			Map<String, Object> applicationEntity = SoaServiceUtil.getApplication(bizId);
			Long applicationId = (Long)applicationEntity.get("id");
			//获取会员实体并检查用户是否存在
			Map<String, Object> buyerMemberEntity = SoaServiceUtil.getMemberEntity(applicationId, payerId,"付款方用户不存在。");

			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(buyerMemberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "付款方用户不可用。");
			}

			Long buyerMemberId = (Long)buyerMemberEntity.get("id");
			//判断是否是B2C
			Long sellerMemberId;
			if(Constant.B2C_BIZ_USER_ID.equals(recieverId)){
				sellerMemberId = (Long)applicationEntity.get("member_id");
				if (fee > 0 ){
					throw new BizException(ErrorCode.ORDER_ERROR, "B2C订单类型不能设置手续费！");
				}
			}else{
				//获取会员实体并检查用户是否存在
				Map<String, Object> sellerMemberEntity = SoaServiceUtil.getMemberEntity(applicationId, recieverId,"收款方用户不存在。");

				//用户不可用，则不允许操作
				if(!Constant.USER_STATE_ACTIVATION.equals(sellerMemberEntity.get("user_state"))){
					throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "收款方用户不可用。");
				}
				sellerMemberId = (Long)sellerMemberEntity.get("id");
			}
            			
			//检查是否已经绑定手机
			Long memberType = (Long)buyerMemberEntity.get("member_type");
			if(!(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && (accountPayInfo == null || accountPayInfo.length() == 0))){
				if (buyerMemberEntity.get("isPhone_checked") == null || !(Boolean) buyerMemberEntity.get("isPhone_checked"))
					throw new BizException(ErrorCode.NOT_BIND_PHONE, "未绑定手机。");
			}
			
			//组装支付信息
			Long payMoney = 0L;
			List<Map<String, Object>> accountList = new ArrayList<>();
			List<Map<String, Object>> payInterfaceList = new ArrayList<>();

			//检查是否有可用的支付方式。
			SoaServiceUtil.chickPayMethod(payMethod,Constant.ORDER_TYPE_SHOPPING);

			if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && itsPayInfo != null)
				throw new BizException(ErrorCode.OTHER_ERROR,"企业会员不能使用ITS支付方式");

			String accountSetCodeNo;

				//账户余额
			if(accountPayInfo != null && accountPayInfo.length() != 0){
				for(int i = 0; i < accountPayInfo.length(); i++){
					JSONObject temp = (JSONObject)accountPayInfo.get(i);
					Long tempAmount = temp.optLong("amount");
					String tempAccountSetNo = (String)temp.get("accountSetNo");
					payMoney += tempAmount;
					temp.put("tradeMoney", tempAmount);
					temp.put("call_type", Constant.CALL_TYPE_INTERFACE);
					temp.put("accoutSetNo", tempAccountSetNo);
					temp.put("isMaster", true);
					//temp.put("remark", "");
					//temp.put("extend_info", "");
					//temp.put("handling_mode", "");
					accountList.add(temp.getMap());
				}
			}
				//快捷
			if(itsPayInfo != null && itsPayInfo.length() != 0){
				Map<String, Object> payInterfaceMap = new HashMap<>();
				Long itsAmount = itsPayInfo.optLong("amount");
				String bankCardNo = (String)itsPayInfo.get("bankCardNo");
				//检查是否实名认证
				if(!(Boolean)buyerMemberEntity.get("isIdentity_checked")){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先实名认证。");
				}
				Long bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, buyerMemberId, bankCardNo);

				payMoney += itsAmount;
				payInterfaceMap.put("tradeMoney", itsAmount);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_QUICK);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_OTHER);
				payInterfaceMap.put("bankId", bindBankCardId);
				payInterfaceList.add(payInterfaceMap);
			}
				//网关
			if(gatewayPayInfo != null && gatewayPayInfo.length() != 0){
				//获取账户集编号

				accountSetCodeNo = SoaServiceUtil.getCaseAccountSetNo(applicationId);

				Long gatewayAmount = gatewayPayInfo.optLong("amount");
				String bankCode = (String)gatewayPayInfo.get("bankCode");
				Long payType = gatewayPayInfo.optLong("payType");
				String bankCardNo = (String)gatewayPayInfo.get("bankCardNo");
				//Long bankCardType = SoaServiceUtil.payTypeToCardType(payType);
				//String bankName = SoaServiceUtil.getBankName(bankCode);

				payMoney += gatewayAmount;
				Map<String, Object> payInterfaceMap = new HashMap<>();

//				if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && payType != 4){
//					throw new BizException(ErrorCode.OTHER_ERROR, "企业用户只能使用企业网银。");
//				}

				if(payType.equals(28L)){
					if(StringUtils.isBlank(bankCardNo)){
						throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankCardNo为空。");
					}
				}

				//移动认证支付
				if(payType == 27){
					payInterfaceMap.put("tradeMoney", gatewayAmount);
					payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_CERT_PAY);
					payInterfaceMap.put("call_type", Constant.CALL_TYPE_PAGE);
//					payInterfaceMap.put("bank_code", bankCode);
					payInterfaceMap.put("accountNo", bankCardNo == null ? null : SoaServiceUtil.rsaDencrypt(bizId, bankCardNo));
					//payInterfaceMap.put("bank_name", bankName);
					//payInterfaceMap.put("card_type", bankCardType);
					payInterfaceMap.put("accountTypeCodeNo", accountSetCodeNo);
					payInterfaceMap.put("pay_type", payType);

					payInterfaceList.add(payInterfaceMap);
				}else{
					payInterfaceMap.put("tradeMoney", gatewayAmount);
					payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_GETWAY_JJ);
					payInterfaceMap.put("call_type", Constant.CALL_TYPE_PAGE);
					payInterfaceMap.put("bank_code", bankCode);
					payInterfaceMap.put("accountNo", bankCardNo == null ? null : SoaServiceUtil.rsaDencrypt(bizId, bankCardNo));
					//payInterfaceMap.put("bank_name", bankName);
					//payInterfaceMap.put("card_type", bankCardType);
					payInterfaceMap.put("accountTypeCodeNo", accountSetCodeNo);
					payInterfaceMap.put("pay_type", payType);

					payInterfaceList.add(payInterfaceMap);
				}
			}else if( posPay != null ){		//pos支付
				Map<String, Object> payInterfaceMap = new HashMap<>();
				Long posAmount = posPay.optLong("amount");
				payMoney += posAmount;

				payInterfaceMap.put("tradeMoney", payMoney);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_POS);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_OTHER);
				payInterfaceList.add(payInterfaceMap);
			}else if(daikou != null){    //代扣
				Map<String, Object> payInterfaceMap = new HashMap<>();
				String bankCardNo = (String)daikou.get("bankCardNo");

				//检查是否实名认证
				if(!Boolean.TRUE.equals(buyerMemberEntity.get("isIdentity_checked"))){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先实名认证。");
				}

				//获取绑定银行卡id
				Long bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, buyerMemberId, bankCardNo);

				//检查是否已经强实名认证
				if(!Boolean.TRUE.equals(buyerMemberEntity.get("is_higher_card"))){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先强实名认证。");
				}

				Long daikouMoney = daikou.optLong("amount");
				payMoney += daikouMoney;

				payInterfaceMap.put("tradeMoney", daikouMoney);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_UNION_DAIKOU);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_INTERFACE);
//				payInterfaceMap.put("accountSetNo", accountSetNo);
				payInterfaceMap.put("bankId", bindBankCardId);
				payInterfaceList.add(payInterfaceMap);
			}


			if(!payMoney.equals(amount))
				throw new BizException(ErrorCode.ORDER_MONEY_PAY_MONEY_NOT_EQUAL, "订单金额错误。");

			//把分账数据的类型改为dubbo可以识别的类型（基本类型）
//			List<Map<String, Object>> splitRuleList = null;
//			if(splitRule != null){
//				splitRuleList = new ArrayList<Map<String, Object>>();
//
//				for(int i = 0; i < splitRule.length(); i++){
//					JSONObject temp = (JSONObject)splitRule.get(i);
//					splitRuleList.add(temp.getMap());
//				}
//			}

			//组装其他信息
			Map<String, Object> extParams = new HashMap<>();
			extParams.put("subTradeType", Constant.SUB_TRADE_TYPE_SHOPPING);
			extParams.put("fee", fee);
			extParams.put("targetMemberId", sellerMemberId);
			extParams.put("source", source);
			extParams.put("bizOrderNo", bizOrderNo);
			extParams.put("industry_code", industryCode);
			extParams.put("industry_name", industryName);
			extParams.put("frontUrl", frontUrl);
			extParams.put("backUrl", backUrl);
			extParams.put("showUrl", showUrl);
			extParams.put("ordErexpireDatetime", ordErexpireDatetime == null ? null :sdf.parse(ordErexpireDatetime));
			extParams.put("description", goodsDesc);
			extParams.put("goodsName", goodsName);
			extParams.put("splitRule", splitRule == null ? null : splitRule.toString());
			extParams.put("summary", summary);

			//payInterfaceList = payInterfaceList.isEmpty() ? null : payInterfaceList;
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + buyerMemberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_SHOPPING + ",tradeType=" + Constant.TRADE_TYPE_TRANSFER + ",source=" + source + ",extParams=" + extParams + ",accountList=" + accountList + ",payInterfaceList=" + payInterfaceList + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, buyerMemberId, bizOrderNo, amount, Constant.ORDER_TYPE_SHOPPING, Constant.TRADE_TYPE_TRANSFER, source, extParams, accountList, payInterfaceList, null);
			JSONObject ret = new JSONObject();
			ret.put("orderNo",  orderEntity.get("orderNo"));
			ret.put("tradeNo",  orderEntity.get("oriTraceNum"));
			ret.put("bizOrderNo", bizOrderNo);
			ret.put("extendInfo", extendInfo);
			ret.put("receiveUrl", orderEntity.get("receiveUrl"));
			ret.put("certPayOrderNo", orderEntity.get("commandNo"));
			ret.put("orderDatetime", orderEntity.get("orderDatetime"));
			ret.put("payCode", orderEntity.get("payCode"));
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.applyConsume end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 代收申请
	 * @param json		json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject agentCollectApply(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.applyHostCollect start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizOrderNo = (String)json.get("bizOrderNo");
			String payerId = (String)json.get("payerId");
			JSONArray recieverList = (JSONArray)json.get("recieverList");
			Long goodsType = json.isNull("goodsType") ? 0L : json.optLong("goodsType");
			String goodsNo = (String)json.get("goodsNo");
			String tradeCode = (String)json.get("tradeCode");
			Long amount = json.isNull("amount") ? null : json.optLong("amount");
			Long fee = json.optLong("fee");
			String frontUrl = (String)json.get("frontUrl");
			String backUrl = (String)json.get("backUrl");
			String showUrl = (String)json.get("showUrl");
			String ordErexpireDatetime = (String)json.get("ordErexpireDatetime");
			JSONObject payMethod = (JSONObject)json.get("payMethod");
			String goodsName = (String)json.get("goodsName");
			String goodsDesc = (String)json.get("goodsDesc");
			String industryCode = (String)json.get("industryCode");
			String industryName = (String)json.get("industryName");
			Long source = json.isNull("source") ? null : json.optLong("source");
			String summary = (String)json.get("summary");
			String extendInfo = (String)json.get("extendInfo");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(payerId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数payerId为空或null。");
			if(recieverList == null || recieverList.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数recieverList为空或null。");
			if(goodsType != 0 && StringUtils.isBlank(goodsNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数goodsNo为空或null。");
			if(StringUtils.isBlank(tradeCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数tradeCode为空或null。");
			if(amount == null || amount <= 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为空或小于0。");
			if(StringUtils.isBlank(backUrl))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数backUrl为空或null。");
			if(payMethod == null || payMethod.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数payMethod为空或null。");
			if(StringUtils.isBlank(industryCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数industryCode为空或null。");
			if(StringUtils.isBlank(industryName))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数industryName为空或null。");
			if(source == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数source为空或null。");
			
			//判断summary长度
			if(summary != null && summary.length() > 20)
				throw new BizException(ErrorCode.PARAM_ERROR, "summary长度不能大于20");
			
			//收款人列表最多2000个
			if(recieverList.length() > 2000){
				throw new BizException(ErrorCode.PARAM_ERROR, "收款人列表不能超过2000个");
			}
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "agentCollectApply", version);
			
			//一些必要的检查
				//检查订单过期时间是否在规定范围内
			if(ordErexpireDatetime != null){
				SoaServiceUtil.checkOverdue(sdf, ordErexpireDatetime);
			}
			
				//检查来源
			SoaServiceUtil.checkSource(source);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, payerId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			JSONArray accountPayInfo = (JSONArray)payMethod.get("BALANCE");
			JSONObject itsPayInfo = (JSONObject)payMethod.get("QUICKPAY");
			JSONObject gatewayPayInfo = (JSONObject)payMethod.get("GATEWAY");
			JSONObject couponPayInfo = (JSONObject)payMethod.get("COUPON");
			JSONObject posPay 			= payMethod.optJSONObject("POSPAY");
			JSONObject daikou     = (JSONObject)payMethod.get("DAIKOU");

			//检查是否已经绑定手机
			Long memberType = (Long)memberEntity.get("member_type");
			if(!(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && (accountPayInfo == null || accountPayInfo.length() == 0))){
				if (memberEntity.get("isPhone_checked") == null || !(Boolean) memberEntity.get("isPhone_checked"))
					throw new BizException(ErrorCode.NOT_BIND_PHONE, "未绑定手机。");
			}
			
			//检查是否有可用的支付方式。			
			SoaServiceUtil.chickPayMethod(payMethod, Constant.ORDER_TYPE_DAISHOU);

			if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && itsPayInfo != null)
				throw new BizException(ErrorCode.OTHER_ERROR,"企业会员不能使用ITS支付方式");

			//检查标的是否超额
			Long remainAmount = null;
			if( "1001".equals(tradeCode) && goodsType.equals(1L)) {
//				SoaServiceUtil.chickAgentCollectAmount(applicationId, goodsType, goodsNo, tradeCode, amount);
				//校验商品录入时的bizUserId和代收申请时收款人bizUserId是否一致
				JSONObject receiveObject = (JSONObject)recieverList.get(0);
				String receiveBizId = receiveObject.getString("bizUserId");
				SoaServiceUtil.checkAgentCollectBizUserId(applicationId,goodsType,goodsNo,receiveBizId);
				remainAmount = SoaServiceUtil.getRemainAmount(applicationId, goodsType, goodsNo, tradeCode, amount);
			}

			String accountSetCodeNo;
			
			//组装支付信息
			Long payAmount = 0L;
			List<Map<String, Object>> accountList = new ArrayList<>();
			List<Map<String, Object>> payInterfaceList = new ArrayList<>();
			List<Map<String, Object>> couponList = new ArrayList<>();
			
				//账户余额
			if(accountPayInfo != null && accountPayInfo.length() != 0){
				for(int i = 0; i < accountPayInfo.length(); i++){
					JSONObject temp = (JSONObject)accountPayInfo.get(i);
					Long tempAmount = temp.optLong("amount");
					String accountSetNo = (String)temp.get("accountSetNo");
					
					payAmount += tempAmount;
					
					temp.put("tradeMoney", tempAmount);
					temp.put("accountSetNo", accountSetNo);
					temp.put("isMaster", true);
					temp.put("call_type", Constant.CALL_TYPE_INTERFACE);
					accountList.add(temp.getMap());
				}
			}
				//快捷
			if(itsPayInfo != null){
				Map<String, Object> payInterfaceMap = new HashMap<>();
				Long itsAmount = itsPayInfo.optLong("amount");
				String bankCardNo = (String)itsPayInfo.get("bankCardNo");
				//检查是否实名认证
				if(!(Boolean)memberEntity.get("isIdentity_checked")){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先实名认证。");
				}
				Long bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, memberId, bankCardNo);
				
				payAmount += itsAmount;
				payInterfaceMap.put("tradeMoney", itsAmount);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_QUICK);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_OTHER);
				payInterfaceMap.put("bankId", bindBankCardId);
				payInterfaceList.add(payInterfaceMap);
			}
				//网关
			if(gatewayPayInfo != null){
				//获取账户集编号

				accountSetCodeNo = SoaServiceUtil.getCaseAccountSetNo(applicationId);

				
				Long gatewayAmount = gatewayPayInfo.optLong("amount");
				String bankCode = (String)gatewayPayInfo.get("bankCode");
				Long payType = gatewayPayInfo.optLong("payType");
				String bankCardNo = (String)gatewayPayInfo.get("bankCardNo");
				//Long bankCardType = SoaServiceUtil.payTypeToCardType(payType);
				//String bankName = SoaServiceUtil.getBankName(bankCode);
				
//				if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && payType != 4){
//					throw new BizException(ErrorCode.OTHER_ERROR, "企业用户只能使用企业网银。");
//				}

				if(payType.equals(28L)){
					if(StringUtils.isBlank(bankCardNo)){
						throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankCardNo为空。");
					}
				}
				
				payAmount += gatewayAmount;
				
				Map<String, Object> payInterfaceMap = new HashMap<>();
				//移动认证支付
				if(payType == 27){
					payInterfaceMap.put("tradeMoney", gatewayAmount);
					payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_CERT_PAY);
					payInterfaceMap.put("call_type", Constant.CALL_TYPE_PAGE);
					payInterfaceMap.put("accountNo", bankCardNo == null ? null : SoaServiceUtil.rsaDencrypt(bizId, bankCardNo));
					//payInterfaceMap.put("bank_name", bankName);
					//payInterfaceMap.put("card_type", bankCardType);
					payInterfaceMap.put("accountTypeCodeNo", accountSetCodeNo);
					payInterfaceMap.put("pay_type", payType);
				}else{
					payInterfaceMap.put("tradeMoney", gatewayAmount);
					payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_GETWAY_JJ);
					payInterfaceMap.put("call_type", Constant.CALL_TYPE_PAGE);
					payInterfaceMap.put("bank_code", bankCode);
					payInterfaceMap.put("accountNo", bankCardNo == null ? null : SoaServiceUtil.rsaDencrypt(bizId, bankCardNo));
					//payInterfaceMap.put("bank_name", bankName);
					//payInterfaceMap.put("card_type", bankCardType);
					payInterfaceMap.put("accountTypeCodeNo", accountSetCodeNo);
					payInterfaceMap.put("pay_type", payType);
				}
				
				payInterfaceList.add(payInterfaceMap);
			}else if( posPay != null ){		//pos支付
				Map<String, Object> payInterfaceMap = new HashMap<>();
				Long posAmount = posPay.optLong("amount");
				payAmount += posAmount;

				payInterfaceMap.put("tradeMoney", payAmount);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_POS);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_OTHER);
				payInterfaceList.add(payInterfaceMap);
			}else if(daikou != null){    //代扣
				Map<String, Object> payInterfaceMap = new HashMap<>();
				String bankCardNo = (String)daikou.get("bankCardNo");

				//检查是否实名认证
				if(!Boolean.TRUE.equals(memberEntity.get("isIdentity_checked"))){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先实名认证。");
				}

				//获取绑定银行卡id
				Long bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, memberId, bankCardNo);

				//检查是否已经强实名认证
				if(!Boolean.TRUE.equals(memberEntity.get("is_higher_card"))){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先强实名认证。");
				}

				Long daikouMoney = daikou.optLong("amount");
				payAmount += daikouMoney;

				payInterfaceMap.put("tradeMoney", daikouMoney);
				payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_UNION_DAIKOU);
				payInterfaceMap.put("call_type", Constant.CALL_TYPE_INTERFACE);
//				payInterfaceMap.put("accountSetNo", accountSetNo);
				payInterfaceMap.put("bankId", bindBankCardId);
				payInterfaceList.add(payInterfaceMap);
			}


			//代金券
			if(couponPayInfo != null){
				Map<String, Object> couponMap = new HashMap<>();
				Long couponAmount = couponPayInfo.optLong("amount");
				couponMap.put("tradeMoney", couponAmount);
				
				payAmount += couponAmount;
				
				couponList.add(couponMap);
			}
			
			if(!payAmount.equals(amount))
				throw new BizException(ErrorCode.ORDER_MONEY_PAY_MONEY_NOT_EQUAL, "订单金额和支付金额不一致。");
			
			//组装其他信息
			Map<String, Object> extParams = new HashMap<>();
			extParams.put("subTradeType", Constant.SUB_TRADE_TYPE_DAISHOU);
			extParams.put("recieverList", recieverList.toString());
			extParams.put("fee", fee);
			extParams.put("source", source);
			extParams.put("bizOrderNo", bizOrderNo);
			extParams.put("industry_code", industryCode);
			extParams.put("industry_name", industryName);
			extParams.put("frontUrl", frontUrl);
			extParams.put("backUrl", backUrl);
			extParams.put("showUrl", showUrl);
			extParams.put("ordErexpireDatetime", ordErexpireDatetime == null ? null :sdf.parse(ordErexpireDatetime));
			extParams.put("description", goodsDesc);
			extParams.put("goodsName", goodsName);
			extParams.put("biz_trade_code", tradeCode);
			extParams.put("goodsNo", goodsNo);
			extParams.put("goodsType", goodsType);
			extParams.put("summary", summary);
			
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_DAISHOU + ",tradeType=" + Constant.TRADE_TYPE_TRANSFER + ",source=" + source + ",extParams=" + extParams + ",accountList=" + accountList + ",payInterfaceList=" + payInterfaceList + ",couponList=" + couponList);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.ORDER_TYPE_DAISHOU, Constant.TRADE_TYPE_TRANSFER, source, extParams, accountList, payInterfaceList, couponList);
			logger.info("applyOrder返回：" + orderEntity);
			
			JSONObject ret = new JSONObject();
			ret.put("orderNo", orderEntity.get("orderNo"));
			ret.put("bizOrderNo", bizOrderNo);
			ret.put("tradeNo",  orderEntity.get("oriTraceNum"));
			ret.put("extendInfo", extendInfo);
			ret.put("receiveUrl", orderEntity.get("receiveUrl"));
			ret.put("certPayOrderNo", orderEntity.get("commandNo"));
			ret.put("orderDatetime", orderEntity.get("orderDatetime"));
			ret.put("payCode", orderEntity.get("payCode"));
			if ( remainAmount != null ){
				ret.put("remainAmount", remainAmount);
			}
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.applyHostCollect start==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}


	/**
	 * 单笔代付
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject signalAgentPay(JSONObject json) throws BusinessException{
		logger.info("========================SoaOrderService.signalAgentPay start=============================");
		
		try{
			//参数获取
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizOrderNo = (String)json.get("bizOrderNo");
			String bizUserId = (String)json.get("bizUserId");
			JSONArray collectPayList = (JSONArray)json.get("collectPayList");
			String accountSetNo = (String)json.get("accountSetNo");
			JSONObject payToBankCardInfo = json.get("payToBankCardInfo") == null ? null : (JSONObject)json.get("payToBankCardInfo");
			Long amount = json.isNull("amount") ? null : json.optLong("amount");
			Long fee = json.isNull("fee") ? null : json.optLong("fee");
			JSONArray splitRuleList = (JSONArray)json.get("splitRuleList");
			Long goodsType = json.isNull("goodsType")? 0L : json.optLong("goodsType");
			String goodsNo = (String)json.get("goodsNo");
			String tradeCode = (String)json.get("tradeCode");
			String summary = (String)json.get("summary");
			String extendInfo = (String)json.get("extendInfo");
			//增加T+提现选择
			String withdrawType = "";

			
			//参数验证
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(collectPayList == null || collectPayList.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数collectPayList为空或null。");
			if(StringUtils.isBlank(accountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountSetNo为空或null。");
			if(amount == null || amount == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为0或null。");
			if(fee == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数fee为null。");
			if( goodsType != 0 && StringUtils.isBlank(goodsNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数goodsNo为空或null。");
			if(StringUtils.isBlank(tradeCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数tradeCode为空或null。");
			
			//判断summary长度
			if(summary != null && summary.length() > 20)
				throw new BizException(ErrorCode.PARAM_ERROR, "summary长度不能大于20");
			
			//collectPayList长度不能超过100个
//			if(collectPayList.length() > 100){
//				throw new BizException(ErrorCode.PARAM_ERROR, "collectPayList不能大于100个");
//			}
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "signalAgentPay", version);
			
			//如果代付到银行卡，则不支持分账
//			if(payToBankCardInfo != null && payToBankCardInfo.length() > 0 && splitRuleList != null && splitRuleList.length() > 0){
//				throw new BizException(ErrorCode.OTHER_ERROR, "代付到银行卡时，不支持分账。");
//			}
			
			//获取应用的用户id
			logger.info("getApplication参数:bizId=" + bizId);
			Map<String, Object> applicationEntity = Extension.memberService.getApplication(bizId);
			logger.info("getApplication返回：" + applicationEntity);
			if(applicationEntity == null || applicationEntity.isEmpty()){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT, "应用不存在。");
			}
			Long applicationId = (Long)applicationEntity.get("id");
			//这里是付款的用户id，此处付款用户为应用
			Long applicationMemberId = (Long)applicationEntity.get("member_id");
			
			//获取收款方用户id
			//获取会员实体并检查用户是否存在
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//如果是代付到银行卡
			Long bindBankCardId = null;
			Long bankAmount = null;
			String backUrl = null;
			if(payToBankCardInfo != null && payToBankCardInfo.length() > 0){
				bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, memberId, (String)payToBankCardInfo.get("bankCardNo"));
				bankAmount = payToBankCardInfo.optLong("amount");
				backUrl = (String)payToBankCardInfo.get("backUrl");
				withdrawType = payToBankCardInfo.optString("withdrawType");
			}
			if( withdrawType.equals("") ){
				withdrawType = "T0";
			}
			
			//组装其他信息
			Map<String, Object> extParams = new HashMap<>();
			extParams.put("target_bizUserId", bizUserId);
			extParams.put("subTradeType", Constant.SUB_TRADE_TYPE_DAIFU);
			extParams.put("collectPayList", collectPayList.toString());
			extParams.put("goodsType", goodsType);
			extParams.put("goodsNo", goodsNo);
			extParams.put("biz_trade_code", tradeCode);
			extParams.put("accountNoSet", accountSetNo);
			extParams.put("fee", fee);
			extParams.put("splitRuleList", splitRuleList == null ? null : splitRuleList.toString());
			extParams.put("extend_info", extendInfo);
			extParams.put("summary", summary);
				//代付到银行卡则传
			extParams.put("bankCardId", bindBankCardId);
			extParams.put("bank_money", bankAmount);
			extParams.put("backUrl", backUrl);
//			extParams.put("accountSetNo", accountSetNo);
			extParams.put("withdrawType", withdrawType);
			
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + applicationMemberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_DAIFU + ",tradeType=" + Constant.TRADE_TYPE_TRANSFER + ",source=" + null + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + null + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, applicationMemberId, bizOrderNo, amount, Constant.ORDER_TYPE_DAIFU, Constant.TRADE_TYPE_TRANSFER, null, extParams, null, null, null);
			logger.info("applyOrder返回：" + orderEntity);
			
//			DaifuThread daifuThread = new DaifuThread(applicationId, applicationMemberId, bizOrderNo,amount, extParams);
//			daifuThread.run();
			
			String status = (String)orderEntity.get("state");
			String payStatus;
			//失败
			if("0".equals(status)){
				payStatus = "fail";
			}else if("1".equals(status)){
				payStatus = "success";
			}else{
				payStatus = "pending";
			}
			
			
			JSONObject ret = new JSONObject();
			ret.put("payStatus", payStatus);
			ret.put("payFailMessage", orderEntity.get("failPayMessage") == null ? null : (String)orderEntity.get("failPayMessage"));
			ret.put("orderNo", orderEntity.get("orderNo"));
			ret.put("bizOrderNo", bizOrderNo);
			ret.put("extendInfo", extendInfo);
			
			logger.info("========================SoaOrderService.signalAgentPay end=============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 批量代付
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject batchAgentPay(JSONObject json) throws BusinessException{
		logger.info("========================SoaOrderService.batchAgentPay start=============================");
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizBatchNo = (String)json.get("bizBatchNo");
			JSONArray batchPayList = (JSONArray)json.get("batchPayList");
			Long goodsType = json.isNull("goodsType") ? 0L : json.optLong("goodsType");
			String goodsNo = (String)json.get("goodsNo");
			String tradeCode = (String)json.get("tradeCode");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizBatchNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizBatchNo为空或null。");
			if(batchPayList == null || batchPayList.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数batchPayList为空或null。");
			if( !goodsType.equals(0L) && StringUtils.isBlank(goodsNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数goodsNo为空或null。");
			if(StringUtils.isBlank(tradeCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数tradeCode为空或null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "batchAgentPay", version);
			
			//获取应用的用户id
			logger.info("getApplication参数:bizId=" + bizId);
			Map<String, Object> applicationEntity = Extension.memberService.getApplication(bizId);
			logger.info("getApplication返回：" + applicationEntity);
			if(applicationEntity == null || applicationEntity.isEmpty()){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT, "应用不存在。");
			}
			Long applicationId = (Long)applicationEntity.get("id");
			//这里是付款的用户id，此处付款用户为应用
			Long applicationMemberId = (Long)applicationEntity.get("member_id");
			
			int agentPayType = 0; //用来检测要么全部代付到用户余额，要么全部代付到银行卡
			//把batchPayList改为应用层可以使用的结构
			for(int i = 0; i < batchPayList.length(); i++){
				JSONObject payInfo = (JSONObject)batchPayList.get(i);
				JSONArray collectPayList = (JSONArray)payInfo.get("collectPayList");
				JSONObject payToBankCardInfo = payInfo.optJSONObject("payToBankCardInfo");
				JSONArray splitRuleList = payInfo.optJSONArray("splitRuleList");
				String summary = (String)json.get("summary");
            	//判断summary长度
    			if(summary != null && summary.length() > 20)
    				throw new BizException(ErrorCode.PARAM_ERROR, "summary长度不能大于20");
    			
    			//collectPayList长度不能超过2000个
    			if(collectPayList.length() > 2000){
    				throw new BizException(ErrorCode.PARAM_ERROR, "collectPayList不能大于2000个");
    			}
    			
				//代付到银行卡
				if(payToBankCardInfo != null && payToBankCardInfo.length() > 0){
					//如果代付到银行卡，则不支持分账
					/**
					if( splitRuleList != null || splitRuleList.length() > 0){
						throw new BizException(ErrorCode.OTHER_ERROR, "代付到银行卡时，不支持分账。");
					}
					 **/
					
					String bizUserId = (String)payInfo.get("bizUserId");
					//获取收款方用户id
					//获取会员实体并检查用户是否存在
					Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
					
					//用户不可用，则不允许操作
					if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
						throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
					}
					Long memberId = (Long)memberEntity.get("id");
					
					//检查提现银行卡是否绑定，获取提现银行卡的绑定号
					Long bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, memberId, (String)payToBankCardInfo.get("bankCardNo"));
					payInfo.put("bankCardId", bindBankCardId);
					payInfo.put("bank_money", payToBankCardInfo.optLong("amount"));
					payInfo.put("backUrl", payToBankCardInfo.get("backUrl"));
					String withdrawType = payToBankCardInfo.get("withdrawType") == null?"":payToBankCardInfo.get("withdrawType").toString();

					if( withdrawType.equals("") ){
						withdrawType = "T0";
					}
					payInfo.put("withdrawType", withdrawType);
					if(agentPayType == 0 || agentPayType == 1){
						agentPayType = 1;
					}else{
						throw new BizException(ErrorCode.OTHER_ERROR, "批量代付不支持混合代付。");
					}
					
					continue;
				}
				
				if(agentPayType == 0 || agentPayType == 2){
					agentPayType = 2;
				}else{
					throw new BizException(ErrorCode.OTHER_ERROR, "批量代付不支持混合代付。");
				}
			}
			
			logger.info("6");
			//组装其他信息
			Map<String, Object> extParams = new HashMap<String, Object>();
			extParams.put("subTradeType", Constant.SUB_TRADE_TYPE_BATCH_DAIFU);
			extParams.put("bizBatchNo", bizBatchNo);
			extParams.put("batchPayList", batchPayList.toString());
			extParams.put("goodsType", goodsType);
			extParams.put("goodsNo", goodsNo);
			extParams.put("biz_trade_code", tradeCode);
			
			BatchDaifuThread batchDaifuThread = new BatchDaifuThread(applicationId, applicationMemberId, extParams);
			batchDaifuThread.start();
			
//			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + applicationMemberId + ",bizOrderNo=" + null + ",amount=" + null + ",orderType=" + Constant.ORDER_TYPE_BATCH_DAIFU + ",tradeType=" + Constant.TRADE_TYPE_TRANSFER + ",source=" + null + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + null + ",couponList=" + null);
//			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, applicationMemberId, null, null, Constant.ORDER_TYPE_BATCH_DAIFU, Constant.TRADE_TYPE_TRANSFER, null, extParams, null, null, null);
//			logger.info("applyOrder返回：" + orderEntity);
			
//			JSONArray orderNoList = new JSONArray();
//			for(Map.Entry<String, Object> temp : orderEntity.entrySet()){
//				JSONObject bizOrderInfo = new JSONObject();
//				bizOrderInfo.put("bizOrderNo",  temp.getKey());
//				Map<String, Object> orderInfo = (Map<String, Object>)temp.getValue();
//				bizOrderInfo.put("orderNo", orderInfo.get("orderNo"));
//				
//				String status = (String)orderInfo.get("state");
//				
//				String payStatus;
//				//失败
//				if("0".equals(status)){
//					payStatus = "fail";
//				}else if("1".equals(status)){
//					payStatus = "success";
//				}else{
//					payStatus = "pending";
//				}
//				
//				bizOrderInfo.put("payStatus", payStatus);
//				ret.put("payFailMessage", orderEntity.get("failPayMessage") == null ? null : (String)orderEntity.get("failPayMessage"));
//				
//				orderNoList.put(bizOrderInfo);
//			}
			
			JSONObject ret = new JSONObject();
			ret.put("bizBatchNo", bizBatchNo);
			logger.info("========================SoaOrderService.batchAgentPay end=============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 后台支付
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject pay(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.pay start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizOrderNo = (String)json.get("bizOrderNo");
			String tradeNo = (String)json.get("tradeNo");
			String verificationCode = (String)json.get("verificationCode");
			String consumerIp = (String)json.get("consumerIp");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(verificationCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数verificationCode为空或null。");
			if(StringUtils.isBlank(consumerIp))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数consumerIp为空或null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "pay", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			String phone = (String)memberEntity.get("phone");
			
			//检查订单知否存在
			logger.info("getOrder参数：applicationId=" + applicationId + ",bizOrderNo=" + bizOrderNo);
			Map<String, Object> orderEntity = Extension.orderService.getOrder(applicationId, bizOrderNo);
			logger.info("getOrder返回：" + orderEntity);
			if(orderEntity == null || orderEntity.isEmpty()){
				throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
			}
			String orderNo = (String)orderEntity.get("orderNo");
			
			//检查订单和用户是否匹配
			Long orderMemberId = (Long)orderEntity.get("member_id");
			if(!memberId.equals(orderMemberId)){
				throw new BizException(ErrorCode.ORDER_MEMBER_ERROR, "订单和用户不匹配。");
			}
			
			//检查订单是否过期
			if(orderEntity.get("ordErexpireDatetime") != null){
				Date ordErexpireDatetime = (Date)orderEntity.get("ordErexpireDatetime");
				if(ordErexpireDatetime.before(new Date())){
					throw new BizException(ErrorCode.ORDER_PASE_DUE, "订单过期。");
				}
			}
			
			
			//检查订单是否处于未支付状态
			Long orderState = (Long)orderEntity.get("order_state");
			if(!orderState.equals(Constant.ORDER_STATE_WAIT_PAY)){
				throw new BizException(ErrorCode.ORDER_NOT_UNPAY, "订单不是未支付状态。");
			}
			
			//如果有快捷支付，则去获取银行卡预留手机
			List<Map<String, Object>> commandsList = Extension.orderService.getCommands(orderNo);
			if(!(commandsList == null || commandsList.isEmpty())){
				for (Map<String, Object> temp : commandsList){
					String pay_interfaceNo = temp.get("pay_interfaceNo").toString();
					if( Constant.PAY_INTERFACE_CERT_PAY.equals(pay_interfaceNo) ){
						throw new BizException(ErrorCode.ORDER_ERROR, "移动认证支付不支持后台支付。");
					}
					if( Constant.PAY_INTERFACE_POS.equals(pay_interfaceNo) ){
						throw new BizException(ErrorCode.ORDER_ERROR, "POS不支持后台支付。");
					}
					if(Constant.PAY_INTERFACE_GETWAY_JJ.equals(pay_interfaceNo)){
						throw new BizException(ErrorCode.ORDER_ERROR, "网关不支持后台支付。");
					}
				}
				for(Map<String, Object> temp : commandsList){
					if("1".equals(temp.get("seq_no").toString()) && Constant.PAY_INTERFACE_QUICK.equals(temp.get("pay_interfaceNo").toString())){
						phone = (String)temp.get("phone");
						
						break;
					}
				}
			}
			
			logger.info("confirmPay参数：memberId=" + memberId + ",orderNo=" + orderNo + ",tradeNo=" + tradeNo + ",consumerIp=" + consumerIp + ",phone=" + phone + ",verificationCode=" + verificationCode);
			Map<String, Object> payResponseMap = Extension.orderService.confirmPay(memberId, orderNo, tradeNo, consumerIp, phone, verificationCode);
			logger.info("confirmPay返回：" + payResponseMap);
			
			if(payResponseMap == null || payResponseMap.isEmpty()){
				throw new BizException(ErrorCode.PAY_ERROR, "支付失败。");	
			}

			JSONObject ret = new JSONObject();
			ret.put("payStatus", payResponseMap.get("status"));
			ret.put("payFailMessage", payResponseMap.get("payFailMessage") == null ? null : (String)payResponseMap.get("payFailMessage"));
			ret.put("bizUserId", bizUserId);
			ret.put("bizOrderNo", bizOrderNo);
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.pay end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 商品录入
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject entryGoods(JSONObject json) throws BusinessException{
		logger.info("============================SoaOrderService.entryGoods start===========================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			Long goodsType = json.isNull("goodsType") ? null : json.getLong("goodsType");
			String bizGoodsNo = (String)json.get("bizGoodsNo");
			String goodsName = (String)json.get("goodsName");
			String goodsDetail = (String)json.get("goodsDetail");
			JSONObject goodsParams = (JSONObject)json.get("goodsParams");
			String showUrl = (String)json.get("showUrl");
			String extendInfo = (String)json.get("extendInfo");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。"); 
			if(goodsType == null || goodsType == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数goodsType为0或null。"); 
			if(StringUtils.isBlank(bizGoodsNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizGoodsNo为空或null。"); 
			if(StringUtils.isBlank(goodsName))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数goodsName为空或null。"); 
			if(goodsParams == null || goodsParams.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizGoodsNo为空或null。"); 
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "entryGoods", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//调用接口
			String goodsParamsStr = goodsParams.toString();
			logger.info("enter参数:applicationId=" + applicationId + ",memberId=" + memberId + ",goodsType=" + goodsType + ",bizGoodsNo=" + bizGoodsNo + ",goodsName=" + goodsName + ",goodsDetail=" + goodsDetail + ",goodsParamsStr=" + goodsParamsStr + ",showUrl=" + showUrl + ",extendInfo=" + extendInfo);
			Map<String, Object> goodsEntity = Extension.goodsService.enter(applicationId, memberId, goodsType, bizGoodsNo, goodsName, goodsDetail, goodsParamsStr, showUrl, extendInfo);
			logger.info("enter返回:" + goodsEntity);
			
			if(goodsEntity == null || goodsEntity.isEmpty()){
				throw new BizException(ErrorCode.ENTRY_GOODS_FAIL, "录入错误。");
			}
			
			JSONObject ret = new JSONObject();
			ret.put("goodsNo", goodsEntity.get("goodsNo"));
			ret.put("bizGoodsNo", goodsEntity.get("bizGoodsNo"));
			
			logger.info("返回ret:" + ret);
			logger.info("============================SoaOrderService.entryGoods end===========================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 冻结金额
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject freezeMoney(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.freezeMoney start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizFreezenNo = (String)json.get("bizFreezenNo");
			String accountSetNo = (String)json.get("accountSetNo");
			Long amount = json.isNull("amount") ? null : json.optLong("amount");
			
			//检查参数
			//检测参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(bizFreezenNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizFreezenNo为空或null。");
			if(StringUtils.isBlank(accountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountSetNo为空或null。");
			if(amount == null || amount == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为0或null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "freezeMoney", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//获取账户的账户集类型编号
			logger.info("getAccountTypeByNo参数：accountSetNo=" + accountSetNo);
			Map<String, Object> accountTypeEntity = Extension.accountService.getAccountTypeByNo(accountSetNo);
			logger.info("getAccountTypeByNo返回：" + accountTypeEntity);
			if(accountTypeEntity == null || accountTypeEntity.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "账户集编号错误。");
			}
			Long accountTypeId = (Long)accountTypeEntity.get("id");
			
			//调用接口,冻结金额
			logger.info("freezeMoney参数：bizFreezenNo=" + bizFreezenNo + ",memberId=" + memberId + ",accountTypeId=" + accountTypeId + ",amount=" + amount);
			Extension.accountService.freezeMoney(bizFreezenNo, memberId, accountTypeId, amount);
		
			JSONObject ret = new JSONObject();
			ret.put("bizFreezenNo", bizFreezenNo);
			ret.put("amount", amount);
			
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.freezeMoney start==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	

	/**
	 * 解冻金额
	 * @param json json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject unfreezeMoney(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.unfreezeMoney start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizFreezenNo = (String)json.get("bizFreezenNo");
			String accountSetNo = (String)json.get("accountSetNo");
			Long amount = json.isNull("amount") ? null : json.optLong("amount");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(bizFreezenNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizFreezenNo为空或null。");
			if(StringUtils.isBlank(accountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountSetNo为空或null。");
			if(amount == null || amount == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为0或null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "unfreezeMoney", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//获取账户的账户集类型编号
			logger.info("getAccountTypeByNo参数：accountSetNo=" + accountSetNo);
			Map<String, Object> accountTypeEntity = Extension.accountService.getAccountTypeByNo(accountSetNo);
			logger.info("getAccountTypeByNo返回：" + accountTypeEntity);
			if(accountTypeEntity == null || accountTypeEntity.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "账户集编号错误。");
			}
			Long accountTypeId = (Long)accountTypeEntity.get("id");
			
			//调用接口
			logger.info("freezeMoney参数：unFreezeMoney=" + bizFreezenNo + ",memberId=" + memberId + ",accountTypeId=" + accountTypeId + ",amount=" + amount);
			Extension.accountService.unFreezeMoney(bizFreezenNo, memberId, accountTypeId, amount);
		
			JSONObject ret = new JSONObject();
			ret.put("bizFreezenNo", bizFreezenNo);
			ret.put("amount", amount);
			
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.unfreezeMoney end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 标准退款
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject refund(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.refund start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizOrderNo = (String)json.get("bizOrderNo");
			String oriBizOrderNo = (String)json.get("oriBizOrderNo");
			String bizUserId = (String)json.get("bizUserId");
			JSONArray refundList = (JSONArray)json.get("refundList");
			Long amount = json.isNull("amount") ? null : json.optLong("amount");
			Long couponAmount = json.isNull("couponAmount") ? null : json.optLong("couponAmount");
			Long feeAmount = json.isNull("feeAmount") ? null : json.optLong("feeAmount");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(oriBizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数oriBizOrderNo为空或null。");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(amount == null || amount == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为0或null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "refund", version);
			
			//必要的检查
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
				//检查源订单号是否存在
			logger.info("getOrder参数:applicationId=" + applicationId + ",oriBizOrderNo=" + oriBizOrderNo);
			Map<String, Object> oriOrderEntity = Extension.orderService.getOrder(applicationId, oriBizOrderNo);
			logger.info("getOrder返回：" + oriOrderEntity);
			if(oriOrderEntity == null || oriOrderEntity.isEmpty()){
				throw new BizException(ErrorCode.ORDER_NOTEXSIT, "源订单不存在。");
			}
			String oriOrderNo = (String)oriOrderEntity.get("orderNo");
			
			//检查原订单和用户是否匹配
			Long orderMemberId = (Long)oriOrderEntity.get("member_id");
			if(!memberId.equals(orderMemberId)){
				throw new BizException(ErrorCode.ORDER_MEMBER_ERROR, "订单和用户不匹配。");
			}
			
				//如果是代收，则refundList不能为空
			if(Constant.ORDER_TYPE_DAISHOU.equals(oriOrderEntity.get("order_type"))){
				if(refundList == null || refundList.length() == 0){
					throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数refundList为空或null。");
				}
			}
			
			//组装其他信息
			Map<String, Object> extParams = new HashMap<String, Object>();
			extParams.put("oriOrderNo", oriOrderNo);
			extParams.put("refundList", refundList == null ? null : refundList.toString());
			extParams.put("couponAmount", couponAmount);
			extParams.put("feeAmount", feeAmount);
			
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_REFUNDMENT + ",tradeType=" + Constant.TRADE_TYPE_REFUNDMENT + ",source=" + null + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + null + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.ORDER_TYPE_REFUNDMENT, Constant.TRADE_TYPE_REFUNDMENT, null, extParams, null, null, null);
			logger.info("applyOrder返回：" + orderEntity);
			
			JSONObject ret = new JSONObject();
			ret.put("orderNo", orderEntity.get("orderNo"));
			ret.put("bizOrderNo", bizOrderNo);
			ret.put("amount", amount);
			ret.put("couponAmount", couponAmount);
			ret.put("feeAmount", feeAmount);
			
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.refund end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 流标退款
	 * @param json json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject failureBidRefund(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.refund start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizBatchNo = (String)json.get("bizBatchNo");
			Long goodsType = json.isNull("goodsType") ? null : json.optLong("goodsType");
			String goodsNo = (String)json.get("goodsNo");
			JSONArray batchRefundList = (JSONArray)json.get("batchRefundList");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizBatchNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizBatchNo为空或null。");
			if(goodsType == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数goodsType为null。");
			if(StringUtils.isBlank(goodsNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数goodsNo为空或null。");
			if(batchRefundList == null || batchRefundList.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数batchRefundList为空或null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "failureBidRefund", version);
			
			//必要的检查
				//获取应用实体
			Map<String, Object> applicationEntity = SoaServiceUtil.getApplication(bizId);
			Long applicationId = (Long)applicationEntity.get("id");
			Long memberId = (Long)applicationEntity.get("member_id");
			
			//组装其他信息
			Map<String, Object> extParams = new HashMap<String, Object>();
			extParams.put("subTradeType", Constant.SUB_TRADE_TYPE_BATCH_REFUND);
			extParams.put("bizBatchNo", bizBatchNo);
			extParams.put("goodsType", goodsType);
			extParams.put("goodsNo", goodsNo);
			extParams.put("refundOriList", batchRefundList.toString());
			
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + null + ",amount=" + null + ",orderType=" + Constant.ORDER_TYPE_BATCH_REFUNDMENT + ",tradeType=" + Constant.TRADE_TYPE_REFUNDMENT + ",source=" + null + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + null + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, null, null, Constant.ORDER_TYPE_BATCH_REFUNDMENT, Constant.TRADE_TYPE_REFUNDMENT, null, extParams, null, null, null);
			logger.info("applyOrder返回：" + orderEntity);
			
			JSONObject ret = new JSONObject();
			JSONArray orderNoList = new JSONArray();
			for(Map.Entry<String, Object> temp : orderEntity.entrySet()){
				JSONObject bizOrderInfo = new JSONObject();
				bizOrderInfo.put("bizOrderNo",  temp.getKey());
				Map<String, Object> orderInfo = (Map<String, Object>)temp.getValue();
				bizOrderInfo.put("orderNo", orderInfo.get("orderNo"));
				
				orderNoList.put(bizOrderInfo);
			}
			
			ret.put("orderNoList", orderNoList);
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.refund end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 平台转账
	 * @param json 	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject applicationTransfer(JSONObject json) throws BusinessException{
		logger.info("=========================SoaOrderService.transfer==========================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizTransferNo = json.getString("bizTransferNo");
			String sourceAccountSetNo = (String)json.get("sourceAccountSetNo");
			String targetBizUserId = (String)json.get("targetBizUserId");
			String targetAccountSetNo = (String)json.get("targetAccountSetNo");
			Long amount = json.isNull("amount")?null:json.optLong("amount");
			String remark = (String)json.get("remark");
			String extendInfo = (String)json.get("extendInfo");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空。");
			if(StringUtils.isBlank(bizTransferNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizTransferNo为空。");
			if(StringUtils.isBlank(sourceAccountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sourceAccountSetNo为空。");
			if(StringUtils.isBlank(targetBizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数targetBizUserId为空。");
			if(StringUtils.isBlank(targetAccountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数targetAccountSetNo为空。");
			if(amount == null || amount == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为空。");
			
			//必要的检查

			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "signalAgentPay", version);

				//获取应用实体
			Map<String, Object> applicationEntity = SoaServiceUtil.getApplication(bizId);
			Long applicationId = (Long)applicationEntity.get("id");
			//这里是付款的用户id，此处付款用户为应用
			Long applicationMemberId = (Long)applicationEntity.get("member_id");
			
			//获取目标会员id
			Long targetMemberId;
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, targetBizUserId);
			if(memberEntity == null || memberEntity.isEmpty())
				throw new BizException(ErrorCode.USER_NOTEXSIT, "目标会员不存在。");
			targetMemberId = (Long)memberEntity.get("id");

			//目前只支持从平台的保证金账户或者营销专用账户转出
			if(!Constant.ACCOUNT_NO_STANDARD_BOND.equals(sourceAccountSetNo) && !Constant.ACCOUNT_NO_COUPON.equals(sourceAccountSetNo)){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "目前只支持从保证金账户或者营销专用账户转出。");
			}

			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			//调用接口
//			logger.info("applicationTransfer请求参数：bizTransferNo=" + bizTransferNo + ",applicationId=" + applicationId + ",sourceAccountSetNo=" + sourceAccountSetNo + ",targetMemberId=" + targetMemberId + ",targetAccountSetNo=" + targetAccountSetNo + ",amount=" + amount + ",remark=" + remark + ",extendInfo=" + extendInfo);
//			Map<String, Object> applicationTransferRet = Extension.orderService.applicationTransfer(bizTransferNo, applicationId, sourceAccountSetNo, targetMemberId, targetAccountSetNo, amount, remark, extendInfo);
//			logger.info("applicationTransfer返回：" + applicationTransferRet);

			//组装其他信息
			Map<String, Object> extParams = new HashMap<>();
			extParams.put("target_bizUserId", targetBizUserId);
			extParams.put("subTradeType", Constant.SUB_TRADE_TYPE_APPLICATION_TRANSFER);
			extParams.put("accountNoSet", sourceAccountSetNo);
			extParams.put("targetAccountSetNo", targetAccountSetNo);
			extParams.put("extend_info", extendInfo);
			extParams.put("remark", remark);


			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + applicationMemberId + ",bizOrderNo=" + bizTransferNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_DAIFU + ",tradeType=" + Constant.TRADE_TYPE_TRANSFER + ",source=" + null + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + null + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, applicationMemberId, bizTransferNo, amount, Constant.ORDER_TYPE_APPLICATION_TRANSFER, Constant.TRADE_TYPE_TRANSFER, null, extParams, null, null, null);
			logger.info("applyOrder返回：" + orderEntity);
			
			//返回参数
			JSONObject ret = new JSONObject();
		
			String status = (String)orderEntity.get("state");
			String payStatus;
			//失败
			if("0".equals(status)){
				throw new BizException(ErrorCode.OTHER_ERROR, "平台转账失败。");
			}else if("1".equals(status)){
				ret.put("transferNo", orderEntity.get("orderNo"));
				ret.put("bizTransferNo", bizTransferNo);
				ret.put("amount", amount);
				ret.put("extendInfo", extendInfo);
			}else{
				throw new BizException(ErrorCode.OTHER_ERROR, "平台转账失败。");
			}
		
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	
	/**
	 * 查询余额
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject queryBalance(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.queryBalance start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String accountSetNo = (String)json.get("accountSetNo");
			
			//检查参数
			//检测参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(accountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountSetNo为空或null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "queryBalance", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//获取账户的账户集类型编号
			logger.info("getAccountTypeByNo参数：accountSetNo=" + accountSetNo);
			Map<String, Object> accountTypeEntity = Extension.accountService.getAccountTypeByNo(accountSetNo);
			logger.info("getAccountTypeByNo返回：" + accountTypeEntity);
			if(accountTypeEntity == null || accountTypeEntity.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "账户集编号错误。");
			}
			Long accountTypeId = (Long)accountTypeEntity.get("id");
			
			//调用接口
			logger.info("getMemberAccountByType参数：memberId=" + memberId + ",accountTypeId=" + accountTypeId);
			Map<String, Object> memberAccountEntity = Extension.accountService.getMemberAccountByType(memberId, accountTypeId);
			logger.info("getMemberAccountByType返回：" + memberAccountEntity);
			JSONObject ret = new JSONObject();
			if(memberAccountEntity == null || memberAccountEntity.isEmpty()){
				ret.put("allAmount", 0L);
				ret.put("freezenAmount", 0L);
			}else{
				ret.put("allAmount", memberAccountEntity.get("amount"));
				ret.put("freezenAmount", memberAccountEntity.get("freeze_amount"));
			}
		
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.queryBalance end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 查询订单状态
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject getOrderDetail(JSONObject json) throws BusinessException {
		logger.info("==============================SoaOrderService.getOrderDetail start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizOrderNo = (String)json.get("bizOrderNo");
			String signType = "0";

			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(signType))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数signType为空或null。");


			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "getOrderDetail", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			//Long memberId = (Long)memberEntity.get("id");

			logger.info("getOrder参数:applicationId=" + applicationId + ",bizOrderNo=" + bizOrderNo);
			Map<String, Object> orderEntity = Extension.orderService.getOrder(applicationId, bizOrderNo);
			logger.info("getOrder返回=" + orderEntity);
			if(orderEntity == null || orderEntity.isEmpty()){
				throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
			}
			//移动认证支付开始
			//查询订单明细   判断是否是移动订单
			if(!orderEntity.containsKey("orderNo")){
				throw new BizException(ErrorCode.ORDER_ERROR, "订单错误，订单号不存在。");
			}
			Long order_state = 0L;
			if(orderEntity.containsKey("order_state")){
				order_state = Long.valueOf(String.valueOf(orderEntity.get("order_state")));
			}
            logger.info("order_state");
			//未支付的订单才会走移动认证支付流程
			if(order_state.equals(Constant.ORDER_STATE_WAIT_PAY)){
				String orderNo = String.valueOf(orderEntity.get("orderNo"));
				List<Map<String,Object>> orderPayDetails = Extension.orderService.getAllCommands(orderNo);
                logger.info("orderPayDetails");
				if(orderPayDetails!=null){
                    logger.info("orderPayDetails length="+orderPayDetails.size());
					for(Map<String,Object> orderPayDetail : orderPayDetails){
						//移动认证支付指标
                        String payInterfaceNo = String.valueOf(orderPayDetail.get("pay_interfaceNo"));
                        logger.info("payInterfaceNo="+payInterfaceNo);
                        if(Constant.PAY_INTERFACE_CERT_PAY.equals(payInterfaceNo)){
                            //调用认证支付接口
							String commandNo = (String) orderPayDetail.get("command_no");
							String appVersion = Extension.paramMap.get("AlipayConfig.apppay.version");
							String orderDatetime = sdf_yyyyMMddHHmmss.format((Date) orderEntity.get("create_time"));
							String queryDateTime = sdf_yyyyMMddHHmmss.format(new Date());
                            String cacheKey = Constant.PAY_INTERFACE_CERT_PAY+"_"+bizId;
                            JSONObject payInterfaceAppConfig = new JSONObject(JedisUtils.getCacheByKey("payInterfaceAppConfig"));
                            String result = payInterfaceAppConfig.getString(cacheKey);
                            JSONObject resultObj = new JSONObject(result);
                            String mobileCertPayKey = resultObj.getString("mobile_cert_pay_key");
                            String merchantId = resultObj.getString("merchant_id");
                            logger.info("bps.process.Extension.gatewayService.getAppPayOrder start");
                            Extension.gatewayService.getAppPayOrder(
                                    merchantId,appVersion,signType,commandNo,orderDatetime,queryDateTime,mobileCertPayKey
							);
                            logger.info("bps.process.Extension.gatewayService.getAppPayOrder end");
                        }
					}
				}
			}
			//移动认证支付结束
			JSONObject ret = new JSONObject();
			ret.put("orderStatus", orderEntity.get("order_state"));
			ret.put("orderNo", orderEntity.get("orderNo"));
			ret.put("bizOrderNo", bizOrderNo);
			logger.info("ret:" + ret);
			logger.info("==============================SoaOrderService.getOrderDetail end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	
	/**
	 * 查询订单详情
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject queryOrderDetail(JSONObject json) throws BusinessException {
		logger.info("==============================SoaOrderService.queryOrderDetail start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizOrderNo = (String)json.get("bizOrderNo");
			Long orderType = json.isNull("orderType") ? null : json.optLong("orderType");
			Long orderStatus = json.isNull("orderStatus") ? null : json.optLong("orderStatus");
			String dateStart = (String)json.get("dateStart");
			String dateEnd = (String)json.get("dateEnd");
			Long numberStart = json.isNull("numberStart") ? null : json.optLong("numberStart");
			Long numberEnd = json.isNull("numberEnd") ? null : json.optLong("numberEnd");
			
			//检测参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(numberStart == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数numberStart为null。");
			if(numberEnd == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数numberEnd为null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "queryOrderDetail", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//获取云账户订单编号
			String orderNo = null;
			if(!StringUtils.isBlank(bizOrderNo)){
				logger.info("getOrder参数：applicationId=" + applicationId + ",bizOrderNo=" + bizOrderNo);
				Map<String, Object> orderEntity = Extension.orderService.getOrder(applicationId, bizOrderNo);
				if(orderEntity == null || orderEntity.isEmpty()){
					throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
				}
				orderNo = (String)orderEntity.get("orderNo");
				
				Long orderMemberId = (Long)orderEntity.get("member_id");
				if(!memberId.equals(orderMemberId)){
					throw new BizException(ErrorCode.ORDER_MEMBER_ERROR, "订单和用户不匹配。");
				}
			}
			
			//组装参数
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("member_id", memberId);
			param.put("order_type", orderType);
			param.put("order_state", orderStatus);
			param.put("orderNo", orderNo);
			param.put("create_time_start", dateStart == null ? null : sdf.parse(dateStart));
			param.put("create_time_end", dateEnd == null ? null : sdf.parse(dateEnd));
			
			//查询订单详情
			logger.info("getPayOrderList参数：applicationId=" + applicationId + ",param=" + param + ",numberStart=" + numberStart + ",numberEnd=" + numberEnd);
			List<Map<String,Object>> orderList = Extension.orderService.getPayOrderList(applicationId, param, numberStart, numberEnd);
			logger.info("getPayOrderList返回：" + orderList);
			
			JSONArray orderDetail = new JSONArray();
			for(Map<String,Object> temp : orderList){
				JSONObject orderSoa = orderDetailDbToSoa(temp);
				orderDetail.put(orderSoa);
			}
			
			//查询总数
			logger.info("getPayOrderCount参数：applicationId=" + applicationId + ",param=" + param);
			Long totalNumber = Extension.orderService.getPayOrderCount(applicationId, param);
			logger.info("getPayOrderCount返回：" + totalNumber);
			
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("totalNum", totalNumber);
			ret.put("orderDetail", orderDetail);
			
			logger.info("返回ret:" + ret);
			logger.info("==============================SoaOrderService.queryOrderDetail end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 查询支付详情
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	public JSONObject queryOrderPayDetail(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.queryOrderPayDetail start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizOrderNo = (String)json.get("bizOrderNo");
			
			//检测参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "queryOrderPayDetail", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			//Long memberId = (Long)memberEntity.get("id");
			
			//获取云账户订单编号
			logger.info("getOrder参数：applicationId=" + applicationId + ",bizOrderNo=" + bizOrderNo);
			Map<String, Object> orderEntity = Extension.orderService.getOrder(applicationId, bizOrderNo);
			if(orderEntity == null || orderEntity.isEmpty()){
				throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
			}
			Long orderId = (Long)orderEntity.get("id");
			
			Long memberId = (Long)memberEntity.get("id");
			Long orderMemberId = (Long)orderEntity.get("member_id");
			if(!memberId.equals(orderMemberId)){
				throw new BizException(ErrorCode.ORDER_MEMBER_ERROR, "订单和用户不匹配。");
			}
			
			//只支持充值、消费、代收
			Long orderType = (Long)orderEntity.get("order_type");
			if(!(Constant.ORDER_TYPE_DEPOSIT.equals(orderType) || Constant.ORDER_TYPE_SHOPPING.equals(orderType) || Constant.ORDER_TYPE_DAISHOU.equals(orderType))){
				throw new BizException(ErrorCode.OTHER_ERROR, "订单类型不支持。");
			}
			
			//调用获取支付详情接口
			logger.info("getPayDetailList参数：" + orderId);
			List<Map<String, Object>> payDetailList = Extension.orderService.getPayDetailList(orderId);
			logger.info("getPayDetailList返回：" + payDetailList);
			
			JSONArray payDetailListSoa = new JSONArray();
			for(Map<String, Object> temp : payDetailList){
				JSONObject payDetail = payDetailDbToSoa(bizId, temp);
				payDetailListSoa.put(payDetail);
			}
			
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("bizOrderNo", bizOrderNo);
			ret.put("payDetail", payDetailListSoa);
			 
			logger.info("返回ret:" + ret);
			logger.info("==============================SoaOrderService.queryOrderPayDetail end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 查询账户收支明细
	 * @param json	json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	public JSONObject queryInExpDetail(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.queryInExpDetail start==============================");
		
		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String accountSetNo = (String)json.get("accountSetNo");
			String dateStart = (String)json.get("dateStart");
			String dateEnd = (String)json.get("dateEnd");
			Long startPosition = json.isNull("startPosition") ? null : (json.optLong("startPosition")-1);
			Long queryNum = json.isNull("queryNum") ? null : json.optLong("queryNum");
			
			//检测参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(startPosition == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数numberStart为null。");
			if(queryNum == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数numberEnd为null。");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "queryInExpDetail", version);
					
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//获取云账户账户集id
			Long accountSetId = null;
			if(!StringUtils.isBlank(accountSetNo)){
				logger.info("getAccountTypeByNo参数：accountSetNo=" + accountSetNo);
				Map<String, Object> accountSetEntity = Extension.accountService.getAccountTypeByNo(accountSetNo);
				logger.info("getAccountTypeByNo返回：" + accountSetEntity);
				
				if(accountSetEntity == null || accountSetEntity.isEmpty()){
					throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT, "账户集不存在。");
				}
				
				accountSetId = (Long)accountSetEntity.get("id");
			}
			
			//调用获取账户交易明细接口
				//组装参数
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("account_type_id", accountSetId);
			param.put("chg_time_start", dateStart == null ? null : sdf.parse(dateStart + " 00:00:00"));
			param.put("chg_time_end", dateEnd == null ? null : sdf.parse(dateEnd + " 23:59:59"));
			
			logger.info("getAccountChgDetailList参数:memberId=" + memberId + ",param=" + param + ",numberStart=" + startPosition + ",numberEnd=" + queryNum);
			List<Map<String, Object>> inExpDetailDb = Extension.orderService.getAccountChgDetailList(memberId, param, startPosition, queryNum);
			logger.info("getAccountChgDetailList返回:" + inExpDetailDb);
			
			JSONArray inExpDtailList = new JSONArray();
			for(Map<String, Object> temp : inExpDetailDb){
				JSONObject inExpDatailSoa = inExpDetailDbToSoa(temp);
				
				inExpDtailList.put(inExpDatailSoa);
			}
			
			//获取交易明细总条数
			logger.info("getAccountChgDetailCount参数：memberId=" + memberId + ",param=" + param);
			Long totalNumber = Extension.orderService.getAccountChgDetailCount(memberId, param);
			logger.info("getAccountChgDetailCount返回：" + totalNumber);
			
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("totalNum", totalNumber);
			ret.put("inExpDetail", inExpDtailList);
			 
			logger.info("返回ret:" + ret);
			logger.info("==============================SoaOrderService.queryInExpDetail end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}

	/**
	 * 订单详情（从数据库形式转成SOA形式）
	 * @param orderDetailDb	orderDetailDb
	 * @return	 JSONObject
	 * @throws Exception
	 */
	private JSONObject orderDetailDbToSoa(Map<String, Object> orderDetailDb) throws Exception{
		JSONObject ret = new JSONObject();

		try{
			ret.put("bizOrderNo", orderDetailDb.get("bizOrderNo"));
			ret.put("orderNo", orderDetailDb.get("orderNo"));
			ret.put("orderType", orderDetailDb.get("order_type"));
			ret.put("bizBatchNo", orderDetailDb.get("bizBatchNo"));
			ret.put("createTime", sdf.format((Date)orderDetailDb.get("create_time")));
			ret.put("confirmTime", orderDetailDb.get("confirm_time") == null ? null : sdf.format((Date)orderDetailDb.get("confirm_time")));
			ret.put("ordErexpireDatetime", orderDetailDb.get("ordErexpireDatetime") == null ? null : sdf.format((Date)orderDetailDb.get("ordErexpireDatetime")));
			ret.put("amount", orderDetailDb.get("order_money"));
			ret.put("fee", orderDetailDb.get("fee"));
			ret.put("splitRule", orderDetailDb.get("splitRule"));
			ret.put("orderStatus", orderDetailDb.get("order_state"));
			ret.put("source", orderDetailDb.get("source"));
			ret.put("summary", orderDetailDb.get("summary"));
			ret.put("extendInfo", orderDetailDb.get("extend_info"));

			return ret;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 跨应用转账
	 * @param json json
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	private JSONObject transferCrossApplication(JSONObject json) throws BusinessException{
		logger.info("==============================SoaOrderService.transferCrossApplication start====================================");

		try{
			//获取参数
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizOrderNo  = (String)json.get("bizOrderNo");
			String bizUserId  = (String)json.get("bizUserId");
			String accountSetNo  = (String)json.get("accountSetNo");
			String opponentSysid  = (String)json.get("opponentSysid");
			String opponentBizUserId  = (String)json.get("opponentBizUserId");
			String opponentAccountSetNo  = (String)json.get("opponentAccountSetNo");
			Long amount  = json.get("amount") == null  ? null : json.optLong("amount");
			Long source  = json.get("source") == null  ? null : json.optLong("source");
			String summary  = (String)json.get("summary");
			String extendInfo  = (String)json.get("extendInfo");

			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(accountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountSetNo为空或null。");
			if(StringUtils.isBlank(opponentSysid))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数opponentSysid为空或null。");
			if(StringUtils.isBlank(opponentBizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数opponentBizUserId为空或null。");
			if(StringUtils.isBlank(opponentAccountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数opponentAccountSetNo为空或null。");
			if(amount == null || amount == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为0或null。");
			if(source == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数source为null。");

			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "transferCrossApplication", version);

			//检查终端来源
			SoaServiceUtil.checkSource(source);

			//检查是否为不同应用
			if(bizId.equals(opponentSysid)){
				throw new BizException(ErrorCode.OTHER_ERROR, "同一应用中不能进行跨应用转账。");
			}

			//获取应用id
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Long opponentApplicationId = SoaServiceUtil.getApplicationId(opponentSysid);

			//获取收款方和付款方的会员实体，并检查是否通过实名认证
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			Map<String, Object> opponentMemberEntity = SoaServiceUtil.getMemberEntity(opponentApplicationId, opponentBizUserId);
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "收款方不可用。");
			}
			if(!Constant.USER_STATE_ACTIVATION.equals(opponentMemberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "付款方不可用。");
			}
			if(!Boolean.TRUE.equals(memberEntity.get("isIdentity_checked"))){
				throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "收款方未实名认证。");
			}
			if(!Boolean.TRUE.equals(opponentMemberEntity.get("isIdentity_checked"))){
				throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "付款方未实名认证。");
			}
			if(!Constant.MEMBER_TYPE_PERSON.equals(memberEntity.get("member_type")) || !Constant.MEMBER_TYPE_PERSON.equals(opponentMemberEntity.get("member_type"))){
				throw new BizException(ErrorCode.USER_TYPE_ERROR, "目前只支持个人会员跨应用转账。");
			}

			Long memberId = (Long)memberEntity.get("id");
			Long opponentMemberId = (Long)opponentMemberEntity.get("id");

			//组装参数
			Map<String, Object> extParams = new HashMap<String, Object>();
			extParams.put("subTradeType", Constant.SUB_TRADE_TYPE_CROSS_APP);
			extParams.put("sourceMemberId", opponentMemberId);
			extParams.put("sourceApplicationId", opponentApplicationId);
			extParams.put("accountNoSet", accountSetNo);
			extParams.put("sourceAccountNoSet", opponentAccountSetNo);
			extParams.put("source", source);
			extParams.put("summary", summary);
			extParams.put("extend_info", extendInfo);

			//调用接口
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.SUB_TRADE_TYPE_CROSS_APP + ",tradeType=" + Constant.TRADE_TYPE_TRANSFER + ",source=" + source + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + null + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.SUB_TRADE_TYPE_CROSS_APP, Constant.TRADE_TYPE_TRANSFER, source, extParams, null, null, null);
			logger.info("applyOrder返回：" + orderEntity);

			//返回值
			JSONObject ret = new JSONObject();
			ret.put("orderNo", orderEntity.get("orderNo"));
			ret.put("bizOrderNo", bizOrderNo);
			ret.put("extendInfo", extendInfo);

			logger.info("transferCrossApplication返回:" + ret);
			logger.info("==============================SoaOrderService.transferCrossApplication end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}

	/**
	 * 支付详情（数据库形式转换为SOA形式）
	 * @param  bizId 		bizId
	 * @param payDetailDb	payDetailDb
	 * @return	JSONObject
	 * @throws Exception
	 */
	private JSONObject payDetailDbToSoa(String bizId, Map<String, Object> payDetailDb) throws Exception{
		JSONObject ret = new JSONObject();
		
		try{
			ret.put("amount", payDetailDb.get("trade_money"));
			ret.put("accountSetName", payDetailDb.get("account_type_label"));
			ret.put("bankName", payDetailDb.get("bank_name"));
			ret.put("bankCardNo", payDetailDb.get("bank_code") == null ? null : SoaServiceUtil.rsaEncrypt(bizId, (String)payDetailDb.get("bank_code")));
			int payMethod = 99;
			String payChannelNo = (String)payDetailDb.get("pay_channelNo");
			if(Constant.PAY_CHANNEL_AMS.equals(payChannelNo)){
				payMethod = 3;
			}else if(Constant.PAY_CHANNEL_QUICK.equals(payChannelNo)){
				payMethod = 1;
			}else if(Constant.PAY_CHANNEL_GETWAY.equals(payChannelNo)){
				payMethod = 2;
			}else if(Constant.PAY_CHANNEL_CERT_PAY.equals(payChannelNo)){
				payMethod = 4;
			}else if(Constant.PAY_CHANNEL_POS.equals(payChannelNo)){
				payMethod = 5;
			}
			ret.put("payChannel", payMethod);
			
			return ret;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 账户收支明细详情(数据库形式转换为SOA形式)
	 * @param inExpDetailDb	inExpDetailDb
	 * @return	JSONObject
	 * @throws Exception
	 */
	private JSONObject inExpDetailDbToSoa(Map<String, Object> inExpDetailDb) throws Exception{
		JSONObject ret = new JSONObject();
		
		try{
			ret.put("tradeNo", inExpDetailDb.get("trade_id"));
			ret.put("accountSetName", inExpDetailDb.get("account_type_label"));
			ret.put("changeTime", sdf.format(inExpDetailDb.get("chg_time")));
			ret.put("curAmount", inExpDetailDb.get("cur_money"));
			ret.put("oriAmount", inExpDetailDb.get("old_money"));
			ret.put("chgAmount", inExpDetailDb.get("chg_money"));
			ret.put("curFreezenAmount", inExpDetailDb.get("cur_freeze_money"));
			ret.put("remark", inExpDetailDb.get("remark"));

			return ret;
		}catch(Exception e){
				logger.error(e.getMessage(), e);
				throw e;
		}
	}
	
	/**
	 * 认证支付签名
	 * @param json json
	 * @return JSONObject
	 * @throws Exception
	 * @author xul
	 */
	private JSONObject getCertPaySign(JSONObject json) throws Exception{
		logger.info("============getCertPaySign start============");
		//获取参数
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			JSONObject payData = (JSONObject)json.get("payData");
			String inputCharset = (String)payData.get("inputCharset");
			String receiveUrl = (String)payData.get("receiveUrl");
			String certPayVersion = (String)payData.get("version");
			String signType = (String)payData.get("signType");
			String merchantId = (String)payData.get("merchantId");
			String orderNo = (String)payData.get("orderNo");
			Long orderAmount = payData.isNull("orderAmount")?null:payData.optLong("orderAmount");
			String orderCurrency = (String)payData.get("orderCurrency");
			String orderDatetime = (String)payData.get("orderDatetime");
			String productName = (String)payData.get("productName");
			String payType = (String)payData.get("payType");
			String tradeNature = (String)payData.get("tradeNature");
			String cardNo = (String)payData.get("cardNo");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "getCertPaySign", version);
			
			Map<String, Object>signMap = new HashMap<String, Object>();
			//检测数据
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null。");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(payData == null || payData.length() == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数payData为空或null。");
			if(StringUtils.isBlank(inputCharset))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数inputCharset为空或null。");
			signMap.put("inputCharset", inputCharset);
			if(StringUtils.isBlank(receiveUrl))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数receiveUrl为空或null。");
			signMap.put("receiveUrl", receiveUrl);
			if(StringUtils.isBlank(certPayVersion))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数version为空或null。");
			signMap.put("version", certPayVersion);
			if(StringUtils.isBlank(signType))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数signType为空或null。");
			signMap.put("signType", signType);
			if(StringUtils.isBlank(merchantId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数merchantId为空或null。");
			signMap.put("merchantId", merchantId);
			if(StringUtils.isBlank(orderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数orderNo为空或null。");
			signMap.put("orderNo", orderNo);
			if(orderAmount == null || orderAmount <= 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数orderAmount为null或小于等于0。");
			signMap.put("orderAmount", orderAmount);
			if(StringUtils.isBlank(orderCurrency))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数orderCurrency为空或null。");
			signMap.put("orderCurrency",orderCurrency);
			if(StringUtils.isBlank(orderDatetime))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数orderDatetime为空或null。");
			signMap.put("orderDatetime", orderDatetime);
			if(StringUtils.isBlank(productName))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数productName为空或null。");
			signMap.put("productName", productName);
			if(StringUtils.isBlank(payType))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数payType为空或null。");
			signMap.put("payType", payType);
			signMap.put("tradeNature", tradeNature);
			signMap.put("cardNo", cardNo);
				
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			signMap.put("applicationId", applicationId);
			signMap.put("memberId", memberEntity.get("id"));
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
	
			//签名
			String signMsg = Extension.orderService.getCertPaySign(signMap);
			JSONObject ret = new JSONObject();
			ret.put("orderNo", orderNo);
			ret.put("signMsg", signMsg);
			
			logger.info("ret:" + ret);
			logger.info("=====================SoaOrderService.getCertPaySign end=======================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 商品查询修改
	 * @param  json	json
	 * @return JSONObject
	 * @throws Exception
	 */
	private JSONObject queryModifyGoods(JSONObject json)throws Exception{
		logger.info("==============================SoaOrderService.queryModifyGoods start==============================");
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String bizGoodsNo = (String)json.get("bizGoodsNo");
			Long goodsType = json.isNull("goodsType") ? null : json.getLong("goodsType");
			String beginDate = (String)json.get("beginDate");
			String endDate = (String)json.get("endDate");
			
			//检查参数
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null。");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(bizGoodsNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizGoodsNo为空或null");
			if(goodsType == null || goodsType == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数goodsType为0或null。");
			
			Date $beginDate = null;
			if(!StringUtils.isBlank(beginDate)){
				try{
					$beginDate = sdfDate.parse(beginDate);
				}catch(Exception e){
					throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数beginDate格式错误");
				}
			}
			Date $endDate = null;
			if(!StringUtils.isBlank(endDate)){
				try{
					$endDate = sdfDate.parse(endDate);
				}catch(Exception e){
					throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数beginDate格式错误");
				}
			}
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "getCertPaySign", version);
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//调用接口
			Map<String,Object> goodsParams = Extension.goodsService.queryModifyGoods(applicationId,memberId,bizGoodsNo,goodsType,$beginDate,$endDate);
			JSONObject goodsJSON = new JSONObject();
			goodsJSON.put("amount", goodsParams.get("amount"));
			goodsJSON.put("totalAmount", goodsParams.get("totalAmount"));
			goodsJSON.put("annualYield", goodsParams.get("annual_yield"));
			goodsJSON.put("investmentHorizon", goodsParams.get("investment_horizon"));
			goodsJSON.put("investmentHorizonScale", goodsParams.get("investment_horizon_scale"));

			Date begin_date = goodsParams.get("begin_date")==null? null:(Date)goodsParams.get("begin_date");
			Date end_date = goodsParams.get("end_date")==null? null:(Date)goodsParams.get("end_date");
			if(begin_date != null)
				goodsJSON.put("beginDate", sdfDate.format(begin_date));
			if(end_date != null)
				goodsJSON.put("endDate", sdfDate.format(end_date));
			goodsJSON.put("repayType", goodsParams.get("repay_type"));
			goodsJSON.put("guaranteeType", goodsParams.get("guarantee_type"));
			goodsJSON.put("repayPeriodNumber", goodsParams.get("repay_period_number"));
			
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("bizGoodsNo",bizGoodsNo);
			ret.put("goodsType",goodsType);
			ret.put("goodsName",goodsParams.get("name"));
			ret.put("goodsDetail",goodsParams.get("description"));
			ret.put("showUrl",goodsParams.get("url"));
			ret.put("extendInfo",goodsParams.get("extend_info"));
			ret.put("goodsParams",goodsJSON);
			logger.info("==============================SoaOrderService.queryModifyGoods end==============================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}		
	}
	/**
	 * 强实名认证支付
	 * @param
	 * @return
	 * @throws BusinessException
	 */
	private JSONObject higherCardAuthApply(JSONObject json) throws BusinessException {
		logger.info("========higherCardAuthApply start==========");
		try {
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId = request.getParameter("sysid");
			String v = request.getParameter("v");
			String bizOrderNo = (String)json.get("bizOrderNo");
			String bizUserId = (String)json.get("bizUserId");
			String accountSetNo = (String)json.get("accountSetNo");
			String bankCardNo = (String)json.get("bankCardNo");
			Long payType = json.optLong("payType");
			String bankCode= (String) json.get("bankCode");
			String ordErexpireDatetime = (String)json.get("ordErexpireDatetime");
			String frontUrl = (String)json.get("frontUrl");
			String backUrl = (String)json.get("backUrl");
			String summary = json.isNull("summary") ? null : json.optString("summary");
			String extendInfo = (String)json.get("extendInfo");

			if (StringUtils.isBlank(bizId)){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL,"参数sysid为空或null");
			}
			if (StringUtils.isBlank(v)){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL,"参数v为空活null");
			}
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null。");
			if(StringUtils.isBlank(bizOrderNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizOrderNo为空或null。");
			if(StringUtils.isBlank(bankCardNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankCardNo为空或null。");
			if(StringUtils.isBlank(accountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountSetNo为空或null。");
			if(StringUtils.isBlank(backUrl))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数backUrl为空或null。");
			
			//必要的检查
				//检查接口使用全限
			SoaServiceUtil.judgeSoaAvailable(bizId,"higherCardAuthApply",v);
			
				//判断summary长度
			if(summary != null && summary.length() > 20)
				throw new BizException(ErrorCode.PARAM_ERROR, "summary长度不能大于20");
			
				//检查订单过期时间是否在规定范围内
			if(ordErexpireDatetime != null){
				SoaServiceUtil.checkOverdue(sdf, ordErexpireDatetime);
			}
			
				//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			Long memberId= (Long) memberEntity.get("id");
			String phone= (String) memberEntity.get("phone");
			Boolean isIdentityChecked= (Boolean) memberEntity.get("isIdentity_checked");
				//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
				//企业用户不允许使用强实名认证 
			if (Constant.MEMBER_TYPE_ENTERPRISE.equals(memberEntity.get("member_type"))) {
					throw new BizException(ErrorCode.USER_IS_NOT_USEFUL,"企业用户不允许使用强实名认证");
			}

				//检查银行卡是否是借记卡，并且已经绑定  
			Map<String,Object> bankEntity=SoaServiceUtil.getBindBankCard(bizId,memberId,bankCardNo);
			if (!Constant.BANK_CARD_CX.equals(bankEntity.get("card_type"))){
				throw new BizException(ErrorCode.NOT_SUPPORT_XY_CARD,"只能使用借记卡");
			}

				//检查是否绑定手机
			if (memberEntity.get("isPhone_checked") == null || !(Boolean) memberEntity.get("isPhone_checked")) {
				throw new BizException(ErrorCode.NOT_BIND_PHONE, "未绑定手机。");
			}

				//检查是否是现金账户集。（以后已经去除这个检查，因为也可以是其他账户集，如积分）
			logger.info("getAccountTypeByNo参数：accountSetNo=" + accountSetNo);
			Map<String, Object> accountTypeEntity = Extension.accountService.getAccountTypeByNo(accountSetNo);
			logger.info("getAccountTypeByNo返回：" + accountTypeEntity);
			if(accountTypeEntity == null || accountTypeEntity.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT, "账户集不存在");
			}
			if(!Constant.BIZ_TYPE_CASH.equals(accountTypeEntity.get("biz_type"))){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "目前只支持现金账户集。");
			}
			Long money = Long.valueOf(new Random().nextInt(100) + 1);
			Long fee=0l;
			
			//是否已经强实名认证 
			Boolean is_higher_card= (Boolean) memberEntity.get("is_higher_card");
			if (Boolean.TRUE.equals(is_higher_card)){
				logger.info("is_higher_card值为"+is_higher_card);
				throw  new BizException(ErrorCode.MEMBER_HIGHER_ERROR,"当前用户已经强实名认证过了");
			}

			Long source=9L;   //表示手机和电脑端都可以
			List<Map<String,Object>> payInterfaceList=new ArrayList<>();
			Map<String,Object> payInterfaceMap=new HashMap<>();
			if (payType.equals(27L)){
				payInterfaceMap.put("tradeMoney",money);
				payInterfaceMap.put("payInterFaceNo",Constant.PAY_INTERFACE_CERT_PAY);
				payInterfaceMap.put("call_type",Constant.CALL_TYPE_PAGE);
				payInterfaceMap.put("accountNo",SoaServiceUtil.rsaDencrypt(bizId,bankCardNo));
				payInterfaceMap.put("phone",phone);
				payInterfaceMap.put("accountSetNo",accountSetNo);
				payInterfaceMap.put("pay_type",payType);
				payInterfaceList.add(payInterfaceMap);
			}else if (payType.equals(28L)){
				if (StringUtils.isBlank(bankCode)){
					throw new BizException(ErrorCode.PARAM_ERROR_NULL,"payType==28时,bankCode必传");
				}
				payInterfaceMap.put("tradeMoney",money);
				payInterfaceMap.put("payInterFaceNo",Constant.PAY_INTERFACE_GETWAY_JJ);
				payInterfaceMap.put("call_type",Constant.CALL_TYPE_PAGE);
				payInterfaceMap.put("accountNo",SoaServiceUtil.rsaDencrypt(bizId,bankCardNo));
				payInterfaceMap.put("bank_code",bankCode);
				payInterfaceMap.put("phone",phone);
				payInterfaceMap.put("accountSetNo",accountSetNo);
				payInterfaceMap.put("pay_type",payType);
				payInterfaceList.add(payInterfaceMap);
			}else {
                throw new BizException(ErrorCode.PARAM_ERROR, "参数payType只能为27或28");
			}
			
			Map<String,Object> extParams=new HashMap<String, Object>();
			Long bankCardId=SoaServiceUtil.getBindBankCardId(bizId,memberId,bankCardNo);
			extParams.put("subTradeType",Constant.SUB_TRADE_TYPE_HIGHER_CARD);
			extParams.put("bizOrderNo",bizOrderNo);
			extParams.put("order_money",money);
			extParams.put("fee",fee);
			extParams.put("bankCardId",bankCardId);
			extParams.put("frontUrl",frontUrl);
			extParams.put("backUrl",backUrl);
			extParams.put("summary",summary);
			extParams.put("extend_info",extendInfo);
			extParams.put("source",source);
			extParams.put("industry_code", "1910");
			extParams.put("industry_name", "其他");

			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + money + ",orderType=" + Constant.ORDER_TYPE_DEPOSIT + ",tradeType=" + Constant.TRADE_TYPE_DEPOSIT + ",source=" + source + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + payInterfaceList + ",couponList=" + null);
			Map<String,Object> orderEntity=Extension.orderService.applyOrder(applicationId,memberId,bizOrderNo,money,Constant.ORDER_TYPE_DEPOSIT,Constant.TRADE_TYPE_DEPOSIT,source,extParams,null,payInterfaceList,null);
			logger.info("applyOrder返回：" + orderEntity);
			
			JSONObject ret=new JSONObject();
			ret.put("orderNo",orderEntity.get("orderNo"));
			ret.put("bizOrderNO",bizOrderNo);
			ret.put("amount",money);
			ret.put("receiveUrl",orderEntity.get("receiveUrl"));
			ret.put("certPayOrderNo", orderEntity.get("commandNo"));
			ret.put("orderDatetime", orderEntity.get("orderDatetime"));
			ret.put("extendInfo",extendInfo);
			

			return ret;
		} catch (BizException bizE) {
			logger.error(bizE.getMessage(), bizE);
			
			String message=bizE.getErrorCode()==null ? "其他错误" : bizE.getMessage();
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
}