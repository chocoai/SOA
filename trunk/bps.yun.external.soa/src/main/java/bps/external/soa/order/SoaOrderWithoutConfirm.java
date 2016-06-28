package bps.external.soa.order;

import apf.util.BusinessException;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.external.soa.SoaServiceUtil;
import bps.external.soa.process.Extension;
import ime.soa.ISOAService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/6.
 */
public class SoaOrderWithoutConfirm implements ISOAService {


    private static Logger logger = Logger.getLogger(SoaOrderWithoutConfirm.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public JSONObject doService(String arg0, JSONArray arg1) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public JSONObject doService(String s, JSONObject json) throws Exception {

        logger.info("s=" + s + " json=" + json);
        if("depositWithoutConfirm".equals(s))
            return depositApply(json);                    //充值
        else if("withdrawWithoutConfirm".equals(s))
            return withdrawApply(json);                 //提现
        else if("consumeWithoutConfirm".equals(s))
            return consumeApply(json);                 //消费
        else
            throw new BusinessException("SOA.NoSuchMethod", (new StringBuilder()).append("找不到相应的服务:").append(getServiceName()).append(".").append(s).toString());

}

    public String getServiceName() {
        // TODO Auto-generated method stub
        return "OrderServiceWithoutConfirm";
    }

    public boolean isMustLogined() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isMustValidateClient() {
        // TODO Auto-generated method stub
        return false;
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
            JSONObject posPay 			= payMethod.optJSONObject("POSPAY");
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

                if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && payType != 4){
                    throw new BizException(ErrorCode.OTHER_ERROR, "企业用户只能使用企业网银。");
                }

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
                //检查是否已经强实名认证
                if(!Boolean.TRUE.equals(memberEntity.get("is_higher_card"))){
                    throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先强实名认证。");
                }

                //获取绑定银行卡id
                bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, memberId, bankCardNo);

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
            extParams.put("subTradeType",Constant.SUB_TRADE_TYPE_DEPOSIT_WITHOUT_CONFIRM);
//			extParams.put("accountSetNo", accountSetNo);
            extParams.put("extend_info", extendInfo);

            logger.info("无验证充值applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_DEPOSIT + ",tradeType=" + Constant.TRADE_TYPE_DEPOSIT + ",source=" + source + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + payInterfaceList + ",couponList=" + null);
            Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.ORDER_TYPE_DEPOSIT, Constant.TRADE_TYPE_DEPOSIT, source, extParams, null, payInterfaceList, null);
            logger.info("无验证充值applyOrder返回：" + orderEntity);

            JSONObject ret = new JSONObject();
            ret.put("orderNo", orderEntity.get("orderNo"));
            ret.put("bizOrderNo", bizOrderNo);
            ret.put("amount", amount);
            ret.put("extendInfo", extendInfo);
            /*ret.put("receiveUrl", orderEntity.get("receiveUrl"));
            ret.put("certPayOrderNo", orderEntity.get("commandNo"));
            ret.put("orderDatetime", orderEntity.get("orderDatetime"));
            ret.put("paycode", orderEntity.get("paycode"));*/

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
     * 无验证提现申请
     * @param json	json
     * @return JSONObject
     * @throws BusinessException
     */
    private JSONObject withdrawApply(JSONObject json) throws BusinessException{
        logger.info("==============================SoaOrderWithoutConfirm.applyWithdraw start==============================");

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
                logger.info("bizid:"+bizId);
                logger.info("org:"+org);
                Long withdrawReserveModel = org.get("withdrawReserveModel") == null ? 0L : (Long)org.get("withdrawReserveModel");
                logger.info("withdrawReserveModel:"+withdrawReserveModel);
                logger.info("compare:"+withdrawReserveModel.equals(0L));
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
            extParams.put("subTradeType",Constant.SUB_TRADE_TYPE_WITHDRAW_WITHOUT_CONFIRM);
            extParams.put("withdrawType", withdrawType);
//			extParams.put("accountSetNo", accountSetNo);
            extParams.put("extend_info", extendInfo);

            //调用应用层
            logger.info("无验证提现申请 applyOrder参数：applicationId=" + applicationId + ",memberId=" + memberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_WITHDRAW + ",tradeType=" + Constant.TRADE_TYPE_WITHDRAW + ",source=" + source + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + payInterfaceList + ",couponList=" + null);
            //Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.ORDER_TYPE_WITHDRAW, Constant.TRADE_TYPE_WITHDRAW, source, extParams, null, payInterfaceList, null);
            Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.ORDER_TYPE_WITHDRAW, Constant.TRADE_TYPE_WITHDRAW, source, extParams, null, payInterfaceList, null);

            logger.info("无验证提现申请applyOrder返回：" + orderEntity);

            //返回
            JSONObject ret = new JSONObject();
            ret.put("orderNo", orderEntity.get("orderNo"));
            ret.put("bizOrderNo", bizOrderNo);
            ret.put("extendInfo", extendInfo);
            ret.put("amount",amount);

            logger.info("ret:" + ret);
            logger.info("==============================无验证提现申请applyWithdraw end==============================");
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

                if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && payType != 4){
                    throw new BizException(ErrorCode.OTHER_ERROR, "企业用户只能使用企业网银。");
                }

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
                //检查是否已经强实名认证
                if(!Boolean.TRUE.equals(buyerMemberEntity.get("is_higher_card"))){
                    throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "请先强实名认证。");
                }

                //获取绑定银行卡id
               Long bindBankCardId = SoaServiceUtil.getBindBankCardId(bizId, buyerMemberId, bankCardNo);

                Long daikouMoney = daikou.optLong("amount");
                payMoney += daikouMoney;

                payInterfaceMap.put("tradeMoney", daikouMoney);
                payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_UNION_DAIKOU);
                payInterfaceMap.put("call_type", Constant.CALL_TYPE_INTERFACE);
                //payInterfaceMap.put("accountSetNo", accountSetNo);
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
            extParams.put("subTradeType", Constant.SUB_TRADE_TYPE_SHOPPING_WITHOUT_CONFIRM);
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
            ret.put("bizOrderNo", bizOrderNo);
            ret.put("extendInfo", extendInfo);
            ret.put("amount",amount);
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
}
