package bps.order;

import bps.check.limit.AccountCheck;
import bps.monitor.TransMonitor;
import ime.core.Environment;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kinorsoft.ams.services.QueryService;
import com.kinorsoft.ams.services.TradeService;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.application.Appliction;
import bps.code.CodeServiceImpl;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.common.BizException;
import bps.external.tradecommand.ItsManage;
import bps.member.Member;
import bps.member.MemberServiceImpl;
import bps.process.Extension;
import bps.process.PayChannelManage;
import bps.service.AccountService;
import bps.service.MemberService;

/**
 * 购物订单
 * @author 吴海涛
 * @see 2016-2-17日增加分帐检查:下级分帐用户中不能保含自己,下级列表金额和不能大于上级金额
 */
public class ShoppingOrder extends Order {
	private static Logger logger = Logger.getLogger(ShoppingOrder.class.getName());
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
		logger.info("ShoppingOrder.createOrder start");
		final Long member_id              = (Long) param.get("memberId");
        final Long target_member_id       = (Long) param.get("targetMemberId");
        Long order_money       	  = (Long) param.get("money");
		Long fee				  = param.get("fee") == null ? 0L:(Long)param.get("fee");

		String posCode = "";
		if (fee > order_money ){
			throw new BizException(ErrorCode.FEE_OVER_AMOUNT,"手续费大于金额");
		}
        if(member_id.equals(target_member_id)) 
            throw new BizException(ErrorCode.OTHER_ERROR,"不能卖给自己");
        final Map<String, Object> member_entity = DynamicEntityService.getEntity(member_id, "AMS_Member");
        final Map<String, Object> target_member_entity = DynamicEntityService.getEntity(target_member_id, "AMS_Member");
        
        if(member_entity == null)
            throw new BizException(ErrorCode.USER_NOTEXSIT,"付款人不存在");
        if(target_member_entity == null)
        	throw new BizException(ErrorCode.USER_NOTEXSIT,"收款人不存在");
        
        //判断是否是B2C订单
        if(isB2c((Long)param.get("applicationId"),target_member_id)){
        	List<Map<String,Object>> list = Appliction.getAppConif((Long)param.get("applicationId"), Constant.APPLICTION_CONFIG_B2C);
        	if(list == null ||list.isEmpty()){
        		throw new BizException(ErrorCode.APPLICATION_NO_B2C,"B2C模式未开启");
        	}
        	Map<String,Object> appConfi = list.get(0);
        	if(!(Boolean)appConfi.get("isOpen")){
        		throw new BizException(ErrorCode.APPLICATION_NO_B2C,"B2C模式未开启");
        	}
        	if(appConfi.get("accountNo")==null||"".equals(appConfi.get("accountNo"))){
        		throw new BizException(ErrorCode.APPLICATION_NO_B2C,"B2C模式未开启");
        	}
        }

        
		try {
			final Member member = new Member(member_id);
            //创建订单
            final Map<String, String> entityMap = new HashMap<>();
            
            entityMap.put("member_id",              member_entity.get("id").toString());
            entityMap.put("member_name",            member_entity.get("name") == null ? "" : member_entity.get("name").toString());
            entityMap.put("member_uuid",            member_entity.get("userId").toString());
            entityMap.put("memberNo",               (String)member_entity.get("memberNo"));

            entityMap.put("target_member_id",       target_member_entity.get("id").toString());
            entityMap.put("target_member_name",     target_member_entity.get("name") == null ? "" : target_member_entity.get("name").toString());
            entityMap.put("target_uuid",            target_member_entity.get("userId").toString());
            entityMap.put("target_memberNo",        (String)target_member_entity.get("memberNo"));
            entityMap.put("order_state",            Constant.ORDER_STATE_WAIT_PAY.toString());
            entityMap.put("source",                 param.get("source").toString());

			entityMap.put("order_money", 			order_money.toString());
			Calendar calendar = Calendar.getInstance();
			entityMap.put("create_time", 			String.valueOf(calendar.getTime().getTime()));

			entityMap.put("sub_trade_type", 		Constant.SUB_TRADE_TYPE_SHOPPING.toString());
			entityMap.put("trade_type", 			Constant.TRADE_TYPE_TRANSFER.toString());
			entityMap.put("order_state", 			Constant.ORDER_STATE_WAIT_PAY.toString());
			entityMap.put("order_type", 			Constant.ORDER_TYPE_SHOPPING.toString());
			entityMap.put("orgNo", 					param.get("orgNo").toString());
				
            entityMap.put("order_name", (String)param.get("orderName"));
            entityMap.put("member_ip", (String)param.get("memberIp"));
            entityMap.put("extend_info", (String)param.get("ext"));
            entityMap.put("description", "消费");
            entityMap.put("remark", (String)param.get("remark"));
            entityMap.put("showUrl", (String)param.get("showUrl"));
            entityMap.put("application_id",         param.get("applicationId").toString());
            entityMap.put("application_label",      param.get("applicationName").toString());
            entityMap.put("bizOrderNo",             param.get("bizOrderNo").toString());
            entityMap.put("industry_code",          String.valueOf(param.get("industry_code")));
			entityMap.put("industry_name",          String.valueOf(param.get("industry_name")));
			entityMap.put("frontUrl",               String.valueOf(param.get("frontUrl")));
			entityMap.put("backUrl",                String.valueOf(param.get("backUrl")));
			entityMap.put("goodsType",             	String.valueOf(param.get("goodsType")));
            entityMap.put("goodsNo",             	String.valueOf(param.get("goodsNo")));
            entityMap.put("goodsName",             	String.valueOf(param.get("goodsName")));
            entityMap.put("description",            String.valueOf(param.get("description")));
			if(param.get("subTradeType")!=null){
				entityMap.put("sub_trade_type",String.valueOf(param.get("subTradeType")));
			}
			if(param.get("fee") != null){
				entityMap.put("fee", String.valueOf(param.get("fee")));
			}
			if(param.get("ordErexpireDatetime") != null){
				entityMap.put("ordErexpireDatetime", String.valueOf(((Date)param.get("ordErexpireDatetime")).getTime()));
	        }
	        if(param.get("extend_info") != null){
	        	entityMap.put("extend_info", String.valueOf(param.get("extend_info")));
	        }
	        if(param.get("summary") != null){
	        	entityMap.put("summary", String.valueOf(param.get("summary")));
	        }
	        if(param.get("bank_code")!= null &&!"".equals(param.get("bank_code").toString().trim())){
	        	entityMap.put("bank_code", String.valueOf(param.get("bank_code")));
            }
	        if(param.get("bank_name")!= null &&!"".equals(param.get("bank_name").toString().trim())){
	        	entityMap.put("bank_name", String.valueOf(param.get("bank_name")));
            }
            final List accountList = (List)param.get("accountList");
    		final List payInterfaceList = (List)param.get("payInterfaceList");
            if(param.get("splitRule")!=null){
            	String splitRule = (String)param.get("splitRule");
            	JSONArray spliRuteList = new JSONArray(splitRule);
            	//检查分帐
				Long checkRuteMoney = order_money - fee;

            	checkSplitRule(spliRuteList, checkRuteMoney, target_member_entity.get("userId").toString());
            	//end
            	entityMap.put("splitRule", splitRule);
            }

			//产生pos支付码
            if(payInterfaceList != null && payInterfaceList.size() > 0){
				Map<String, Object> payEntity = (Map<String, Object>)payInterfaceList.get(0);
				if( Constant.PAY_INTERFACE_POS.equals(payEntity.get("payInterFaceNo"))){
					posCode = getPosCode();
					entityMap.put("pos_pay_code",posCode);
				}
            }

            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
	        Map<String,Object> ret = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
	        	@Override
				public boolean before(Session session) throws Exception {
					Map<String,Object> orderEntity = getOrder(Long.parseLong(entityMap.get("application_id")),entityMap.get("bizOrderNo"),session);
					if(orderEntity != null){
						Long orderType = (Long)orderEntity.get("order_type");
						if(!Constant.ORDER_TYPE_SHOPPING.equals(orderType)){
							throw new BizException(ErrorCode.ORDER_ERROR, "订单号已经被其他订单类型使用。");
						}
						
						Long order_state = (Long)orderEntity.get("order_state");
						if(Constant.ORDER_STATE_WAIT_PAY.longValue() != order_state){
							throw new BizException(ErrorCode.ORDER_ERROR,"订单已支付或者已关闭");
						}
						Date ordErexpireDatetime = (Date)orderEntity.get("ordErexpireDatetime");
						if(ordErexpireDatetime!=null && new Date().getTime()>ordErexpireDatetime.getTime()){
							throw new BizException(ErrorCode.ORDER_ERROR,"订单已过期");
							
						}
						if(!orderEntity.get("member_id").toString().equals( entityMap.get("member_id"))){
							throw new BizException(ErrorCode.ORDER_ERROR,"源订单用户和目前订单不符");
						}
						entityMap.put("id", 				orderEntity.get("id").toString());
					}
					//检查指令
					Map<String,Object> checkParam = new HashMap<>();
					checkParam.put("orderMoney", Long.parseLong(entityMap.get("order_money")));
					checkParam.put("application_id", Long.parseLong(entityMap.get("application_id")));
					if(entityMap.get("fee") != null){
						checkParam.put("fee", Long.parseLong(entityMap.get("fee")));
					}
					checkPayCommands(checkParam,accountList,payInterfaceList,session);
					return true;
				}
	        	
				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					Long orderId;
					if(entityMap.get("id") == null){
						Map<String, Object> orderEntity;
						session.beginTransaction();
		                orderEntity = DynamicEntityService.createEntity("AMS_Order", entityMap, null);
		                String orderNo = Order.generateOrderNo((Long)orderEntity.get("id"));
		                Query query = session.createQuery("update AMS_Order set orderNo=:orderNo where id=:orderId");
		                query.setParameter("orderNo", orderNo);
		                query.setParameter("orderId", orderEntity.get("id"));
		                query.executeUpdate();
		                orderId = (Long)orderEntity.get("id");
					}else{
						DynamicEntityService.modifyEntity("AMS_Order", entityMap, null);

						orderId = Long.parseLong(entityMap.get("id"));
						
					}
					//生成指令
					session.flush();//新推到数据库中，避免hibernate从缓存中取，取不到数据
					session.clear();
					Query query = session.createQuery("from AMS_Order where id=:id");
					query.setParameter("id", orderId);
					Map<String,Object> orderEntity = (Map<String,Object>)query.list().get(0);
					return generatePayCommands(member,orderEntity,accountList,payInterfaceList,session);
				}
	        });
			if( !posCode.equals("")) {
				ret.put("payCode", posCode);
			}
			return ret;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	
	 /**
	  * @Title: checkPayCommands
	  * @Description: 检查指令中生存的问题
	  * @param param				其他参数
	  * @param accountList		内部帐户列表
	  * @param payInterfaceList	外部帐户列表
	  * @param session			session
	  * @throws Exception    设定文件
	  */
	@SuppressWarnings("unchecked")
	private void checkPayCommands(Map<String,Object> param,List accountList, List payInterfaceList, Session session) throws Exception{
		Long payMoney = 0L;
		Long orderMoney = (Long)param.get("orderMoney");
		if(payInterfaceList != null){
			for(Object obj : payInterfaceList) {
				Map<String, Object> payInterfaceMap = (Map<String, Object>) obj;
				payMoney += (Long)payInterfaceMap.get("tradeMoney");
			}
		}
		if(accountList != null){
			for(Object obj : accountList) {
				Map<String, Object> accountMap = (Map<String, Object>) obj;
				payMoney += (Long)accountMap.get("tradeMoney");
			}
		}
		if(!payMoney.equals(orderMoney)) {
			throw new Exception("支付金额不符");
		}
		
	}
	
	private Long command_index = 1L;
	private Map<String,Object> generatePayCommands(Member member,Map<String, Object> order_entity,List accountList, List payInterfaceList, Session session)throws Exception{
		
		command_index = 1L;
		List list = getUnPayCommands((String)order_entity.get("orderNo"), session);
		if(list != null && list.size() > 0) {
			Map<String, Object> old_command = (Map<String, Object>) list.get(list.size() - 1);
			command_index = (Long)old_command.get("seq_no") + 1;
		}
		
		Query query = session.createQuery("delete AMS_OrderPayDetail where bizid=:orderNo");
        query.setParameter("orderNo", order_entity.get("orderNo"));
		query.executeUpdate();
		
		Map<String, Object> ret = new HashMap<String, Object>();

		List<Map<String, String>> commandList = generatePayCommandsByPayInterface(order_entity, payInterfaceList, ret, session);
		commandList = generatePayCommandByAccount(order_entity,accountList,commandList,session);
		
		ret.put("orderNo", order_entity.get("orderNo"));
		Map<String, String> command = commandList.get(0);
		if(Constant.PAY_INTERFACE_QUICK.equals((String)command.get("pay_interfaceNo"))) {
			Map<String, String> temp = new HashMap<String, String>();
			temp.put("TRANS_AMOUNT", command.get("trade_money"));
			temp.put("BANK_CODE", command.get("bank_code"));
			temp.put("ACCT_NAME", command.get("account_name"));
			temp.put("ACCT_CAT", command.get("card_type"));
			if(Long.valueOf(command.get("card_type")).equals(Constant.BANK_CARD_XY)){
				temp.put("ACCT_VALIDDATE", command.get("acct_validdate"));
				temp.put("CVV2", command.get("cvv2"));
			}
			temp.put("PHONE_NO", command.get("phone"));
			temp.put("CONTRACT_NO", command.get("contract_no"));
			temp.put("ACCT_NO_ENCRYPT", command.get("accountNo_encrypt"));
			temp.put("ID_NO_ENCRYPT", member.getIdentity_cardNo_encrypt());
			temp.put("TRACE_NUM", command.get("command_no"));
			temp.put("BANK_NAME", command.get("bank_name"));
			temp.put("userId", member.getUserId());
//			Map<String,Object> orgInfo = DynamicEntityService.getEntity((Long)order_entity.get("application_id"), "AMS_Organization");
			temp.put("CNL_MCHT_ID",Constant.ORG_NO_ALLINPAY);
			temp.put("CNL_MCHT_NAME","通联钱包-账户充值");
			if(command.get("bank_code").equals("01030000")) {
				temp.put("CNL_MCHT_TYPE","1117");
			}if(command.get("bank_code").equals("03080000")){//招行
				temp.put("CNL_MCHT_TYPE","1116");
			}else{
				temp.put("CNL_MCHT_TYPE","2513");
			}
			temp.put("isSendSM","1");
			temp.put("member_id", member.getId().toString());
//			if(!RiskUser.checkRiskUserInfo("mobile", command.get("phone"))) {
//			    throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
//			}
			
			Map<String, String> result_its = ItsManage.payApply(temp);
			if( "000000".equals(result_its.get("RET_CODE")) ){
//				if( "2".equals(result_its.get("SEND_SMS")) && isSendSM.equals(1L)){
				if( "2".equals(result_its.get("SEND_SMS"))){
					temp = new HashMap<String, String>();
					temp.put("ORI_TRACE_NUM", result_its.get("TRACE_NUM"));
					temp.put("ORI_TRANS_DATE", result_its.get("TRANS_DATE"));
					temp.put("PHONE_NO", command.get("phone"));
					temp.put("userId", command.get("target_userId"));
					result_its = ItsManage.payMessageSend(temp);
					if( "000000".equals(result_its.get("RET_CODE")) ){
						ret.put("oriTraceNum", result_its.get("TRACE_NUM"));
						ret.put("oriTransDate", result_its.get("TRANS_DATE"));
					}else{
						if(result_its.get("ERROR_MSG") != null && !"".equals(result_its.get("ERROR_MSG").toString())){
							throw new Exception(result_its.get("ERROR_MSG"));
						}else{
							throw new Exception(result_its.get("RET_MSG"));
						}
					}
				}else{
					ret.put("oriTraceNum", result_its.get("TRACE_NUM"));
					ret.put("oriTransDate", result_its.get("TRANS_DATE"));
				}
			}else{
				if(result_its.get("ERROR_MSG") != null && !"".equals(result_its.get("ERROR_MSG").toString())){
					throw new Exception(result_its.get("ERROR_MSG"));
				}else{
					throw new Exception(result_its.get("RET_MSG"));
				}
			}
		} else {
			//非its的操作，如果是纯网关的，不需要发送短信验证码,有余额支付的 发送短信
			boolean isSend  = true;
			logger.info("getway message:"+command.get("pay_interfaceNo")+"-"+order_entity.get("order_money")+"|"+command.get("trade_money"));
			if(Constant.PAY_INTERFACE_GETWAY_JJ.equals((String)command.get("pay_interfaceNo"))&&((Long)order_entity.get("order_money")).equals(Long.parseLong(command.get("trade_money")))){
				logger.info("getway message:false");
				isSend = false;
			}
			if(Constant.TRADE_TYPE_TRANSFER.equals(order_entity.get("trade_type")) && Constant.SUB_TRADE_TYPE_SHOPPING_WITHOUT_CONFIRM.equals(order_entity.get("sub_trade_type"))) {
				logger.info("无验证消费");
				isSend = false;
			}
			if(isSend) {
				Map<String, Object> temp = new HashMap<String, Object>();
				temp.put("phone", member.getPhone());
				temp.put("orderNo", order_entity.get("orderNo"));
				temp.put("verification_code_type", Constant.VERIFICATION_CODE_AUTH_PAY);
				temp.put("applicationId", Constant.APPLICATION_ID_BPS_YUN);
				temp.put("member_id", member.getId());
				CodeServiceImpl codeServiceImpl = new CodeServiceImpl();
				String content = codeServiceImpl.generatePhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN,member.getPhone(), Constant.VERIFICATION_CODE_AUTH_PAY, temp);
				Extension.otherService.sendSM(member.getPhone(), content);
			}
		}
		return ret;
		
		
		
	}
	/**
	 * @Title: generatePayCommandByAccount
	 * @Description: 生成内部账户的指令
	 * @param order_entity		订单
	 * @param accountList		内部帐户列表
	 * @param commandList		指令
	 * @param session			session
	 * @throws Exception    设定文件
	 * @return List    内部指令列表
	 */
	private List<Map<String, String>> generatePayCommandByAccount(Map<String, Object> order_entity,
			List accountList, List<Map<String, String>> commandList, Session session) throws Exception {

		logger.info("order_entity=" + order_entity + ",accountList=" + accountList + ",commandList=" + commandList);
		
		AccountService accountService = new AccountServiceImpl();
		Map<String, Object> cashAccountEntity = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
		if(cashAccountEntity == null){
			throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT,"此应用下现金账户集不存在");
		}

		//int k = commandList.size() + 1;
		Long accountMoney = 0L;
		for(Object obj : accountList) {
			Map<String, Object> accountMap = (Map<String, Object>) obj;
			Map<String, Object> accountEntity = QueryService.getAccountType((String)accountMap.get("accountSetNo"));
			if(accountEntity.get("codeNo").equals(cashAccountEntity.get("codeNo"))){
				continue;//如果是现金账户的 走下面
			}
			
			Map<String, String> command = new HashMap<String, String>();
//			Long tradeType = (Long) accountMap.get("trade_type");
//			Long subTradeType = (Long) accountMap.get("sub_trade_type");
//			if(tradeType != null && tradeType.equals(Constant.TRADE_TYPE_TRANSFER) && subTradeType.equals(Constant.SUB_TRADE_TYPE_FEE)) {
//				command.put("target_userId", 		(String)accountMap.get("target_userId"));
//				command.put("target_memberNo", 		(String)accountMap.get("target_memberNo"));
//				command.put("target_member_name", 	(String)accountMap.get("target_member_name"));
//				command.put("trade_type", 			tradeType.toString());
//				command.put("sub_trade_type", 		subTradeType.toString());
//				command.put("target_account_type_id", 			accountMap.get("target_account_type_id").toString());
//				command.put("target_account_type_label", 		(String)accountMap.get("target_account_type_label"));
//				command.put("target_account_codeNo", 			(String)accountMap.get("target_account_codeNo"));
//				command.put("account_type_id", 			accountMap.get("account_type_id").toString());
//				command.put("account_type_label", 		(String)accountMap.get("account_type_label"));
//				command.put("account_codeNo", 			(String)accountMap.get("account_codeNo"));
//			} else {
//				command.put("target_userId", 		(String)order_entity.get("target_uuid"));
//				command.put("target_memberNo", 		(String)order_entity.get("target_memberNo"));
//				command.put("target_member_name", 	(String)order_entity.get("target_member_name"));
//				command.put("trade_type", 			order_entity.get("trade_type").toString());
//				command.put("sub_trade_type", 		order_entity.get("sub_trade_type").toString());
//				
//				
//				command.put("account_type_id", 			accountEntity.get("id").toString());
//				command.put("account_type_label", 		(String)accountEntity.get("name"));
//				command.put("account_codeNo", 			(String)accountEntity.get("codeNo"));
//				command.put("target_account_type_id", 			accountEntity.get("id").toString());
//				command.put("target_account_type_label", 		(String)accountEntity.get("name"));
//				command.put("target_account_codeNo", 			(String)accountEntity.get("codeNo"));
//			}
			command.put("target_userId", 		(String)order_entity.get("target_uuid"));
			command.put("target_memberNo", 		(String)order_entity.get("target_memberNo"));
			command.put("target_member_name", 	(String)order_entity.get("target_member_name"));
			command.put("trade_type", 			order_entity.get("trade_type").toString());
			command.put("sub_trade_type", 		order_entity.get("sub_trade_type").toString());
			
			
			command.put("account_type_id", 			accountEntity.get("id").toString());
			command.put("account_type_label", 		(String)accountEntity.get("name"));
			command.put("account_codeNo", 			(String)accountEntity.get("codeNo"));
			command.put("target_account_type_id", 			accountEntity.get("id").toString());
			command.put("target_account_type_label", 		(String)accountEntity.get("name"));
			command.put("target_account_codeNo", 			(String)accountEntity.get("codeNo"));
			
			
			command.put("source_userId", 			(String)order_entity.get("member_uuid"));
			command.put("source_memberNo", 			(String)order_entity.get("memberNo"));
			command.put("source_member_name", 		(String)order_entity.get("member_name"));
			
			command.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
			command.put("command_no", 				(String)order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + command_index);
			command.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
			command.put("seq_no", 					command_index + "");
			command.put("pay_interfaceNo", 			Constant.PAY_INTERFACE_AMS);
			command.put("pay_channelNo", 			Constant.PAY_CHANNEL_AMS);

			command.put("trade_money", 				accountMap.get("tradeMoney").toString());
			command.put("bizid", 					(String)order_entity.get("orderNo"));
			command.put("orgNo", 					(String)order_entity.get("orgNo"));
			command.put("order_type", 				order_entity.get("order_type").toString());
			command.put("isMaster", 				accountMap.get("isMaster").toString());
			command.put("call_type", 				accountMap.get("call_type").toString());
			
            if(accountMap.get("handling_mode") != null)
            	command.put("handling_mode",     	accountMap.get("handling_mode").toString());
            if(accountMap.get("remark") != null){
            	command.put("remark",         		accountMap.get("remark").toString());
            }else{
            	if(order_entity.get("summary") != null){
            		command.put("remark",         		order_entity.get("summary").toString());
            	}
            }
            if(accountMap.get("extend_info") != null)
            	command.put("extend_info",       	accountMap.get("extend_info").toString());
            
            commandList.add(command);
            command_index++;
            accountMoney += (Long)accountMap.get("tradeMoney");
		}
		if(((Long)order_entity.get("order_money") - accountMoney) > 0) {
			//判断支付权限
			Map<String, Object> payInfo = new HashMap<>();
			payInfo.put("orgNo", (String)order_entity.get("orgNo"));
			payInfo.put("payInterfaceNo", Constant.PAY_INTERFACE_AMS);
			payInfo.put("account", Boolean.TRUE);
			logger.info("payInfo=" + payInfo);
			Order.judgeAppicationPayPermission(payInfo, session);

			Map<String,Object> targetAccountType = cashAccountEntity;
			//判断是否是B2C订单,如果B2C的结算账户余额走配置的
			boolean isB2C = isB2c((Long)order_entity.get("application_id"),(Long)order_entity.get("target_member_id"));
			if(isB2C){
				List<Map<String,Object>> list = Appliction.getAppConif((Long)order_entity.get("application_id"), Constant.APPLICTION_CONFIG_B2C);
				String accountNo = (String)list.get(0).get("accountNo");
				targetAccountType = QueryService.getAccountType(accountNo);
			}
			
			Map<String, String> command = new HashMap<>();
			
			command.put("source_userId", 			(String)order_entity.get("member_uuid"));
			command.put("source_memberNo", 			(String)order_entity.get("memberNo"));
			command.put("source_member_name", 		(String)order_entity.get("member_name"));
			command.put("target_userId", 			(String)order_entity.get("target_uuid"));
			command.put("target_memberNo", 			(String)order_entity.get("target_memberNo"));
			command.put("target_member_name", 		(String)order_entity.get("target_member_name"));
			
			command.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
			command.put("command_no", 				(String)order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + command_index);
			command.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
			command.put("seq_no", 					command_index + "");
			command.put("trade_type", 				Constant.TRADE_TYPE_TRANSFER.toString());
			command.put("sub_trade_type", 			Constant.SUB_TRADE_TYPE_SHOPPING.toString());
			command.put("pay_interfaceNo", 			Constant.PAY_INTERFACE_AMS);
	
			command.put("trade_money", 				((Long)order_entity.get("order_money") - accountMoney) + "");
			command.put("bizid", 					(String)order_entity.get("orderNo"));
			command.put("orgNo", 					(String)order_entity.get("orgNo"));
			command.put("order_type", 				order_entity.get("order_type").toString());
			command.put("isMaster", 				"true");
			
			command.put("pay_channelNo", 			Constant.PAY_CHANNEL_AMS);
			
			command.put("account_type_id", 			cashAccountEntity.get("id").toString());
			command.put("account_type_label", 		(String)cashAccountEntity.get("name"));
			command.put("account_codeNo", 			(String)cashAccountEntity.get("codeNo"));
//			command.put("target_account_type_id", 			cashAccountEntity.get("id").toString());
//			command.put("target_account_type_label", 		(String)cashAccountEntity.get("name"));
//			command.put("target_account_codeNo", 			(String)cashAccountEntity.get("codeNo"));
			command.put("target_account_type_id", 			targetAccountType.get("id").toString());
			command.put("target_account_type_label", 		(String)targetAccountType.get("name"));
			command.put("target_account_codeNo", 			(String)targetAccountType.get("codeNo"));
			
			command.put("call_type", 				Constant.CALL_TYPE_INTERFACE.toString());
			
	        if(order_entity.get("remark") != null)
	        	command.put("remark",         		order_entity.get("remark").toString());
	        if(order_entity.get("extend_info") != null)
	        	command.put("extend_info",       	order_entity.get("extend_info").toString());
			
	        commandList.add(command);
		}
		
		for(Map<String, String> command2 : commandList) {
			DynamicEntityService.createEntity("AMS_OrderPayDetail", command2, null);
		}
		Query query = session.createQuery("update AMS_Order set is_exsit_command=true where orderNo=:orderNo");
		query.setParameter("orderNo", order_entity.get("orderNo"));
		query.executeUpdate();
		
		return commandList;
	}
	/**
	* @Title: generatePayCommandsByPayInterface 
	* @Description: 生成通道的指令
	* @param order_entity		订单实体
	* @param payInterfaceList	支付列表
	* @param session			session
	* @throws Exception    设定文件
	* @return List<Map<String,String>>    返回类型
	 */
	private List<Map<String, String>> generatePayCommandsByPayInterface(Map<String, Object> order_entity, List payInterfaceList, Map<String, Object> ret, Session session)throws Exception{
	    	List<Map<String, String>> commandList = new ArrayList<Map<String, String>>();
			Map<String, String> command = null;
//			Long k = 1L;
			
			AccountService accountService = new AccountServiceImpl();
			Map<String, Object> target_accountEntity = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
			if(target_accountEntity == null){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT,"此应用下现金账户集不存在");
			}
			Map<String, Object> source_accountEntity = QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
	    	for(int i = 0, j = payInterfaceList.size(); i < j; i ++) {
	    		Map<String, Object> payInterfaceMap = (Map<String, Object>) payInterfaceList.get(i);
//	    		String acountTypeCodeNo = (String)payInterfaceMap.get("accountTypeCodeNo");
//	    		Map<String, Object> target_accountEntity = QueryService.getAccountType(acountTypeCodeNo);
	    		command = new HashMap<String, String>();
				logger.info("创建command:payInterfaceMap="+payInterfaceMap);
				
//				JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfo((String)payInterfaceMap.get("payInterFaceNo"));
//				Member member = new Member();
//				member.setUserId(payInterface_entity.get("system_uuid").toString());
				
				JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfo((String)payInterfaceMap.get("payInterFaceNo"), (String)order_entity.get("orgNo"));
				Member member = new Member();
				member.setUserId(payInterface_entity.get("system_uuid").toString());
				String commandNo = (String)order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + command_index;
				
				command.put("source_userId", 			member.getUserId());
				command.put("source_memberNo", 			member.getMemberNo());
				command.put("source_member_name", 		member.getName());
				command.put("account_type_id", 			source_accountEntity.get("id").toString());
				command.put("account_type_label", 		(String)source_accountEntity.get("name"));
				command.put("account_codeNo", 			(String)source_accountEntity.get("codeNo"));
				command.put("target_userId", 			(String)order_entity.get("member_uuid"));
				command.put("target_memberNo", 			(String)order_entity.get("memberNo"));
				command.put("target_member_name", 		(String)order_entity.get("member_name"));
				command.put("target_account_type_id", 	 target_accountEntity.get("id").toString()		);
				command.put("target_account_type_label", (String)target_accountEntity.get("name")		);
				command.put("target_account_codeNo", 	 (String)target_accountEntity.get("codeNo")		);
				command.put("trade_type", 				Constant.TRADE_TYPE_DEPOSIT.toString());
				command.put("order_type", 				order_entity.get("order_type").toString());
				command.put("bizid", 					(String)order_entity.get("orderNo"));
				command.put("trade_money", 				payInterfaceMap.get("tradeMoney").toString());
				command.put("orgNo", 					(String)order_entity.get("orgNo"));
				command.put("isMaster", 				"false");
				command.put("seq_no", 					 command_index + "");
				command.put("pay_interfaceNo", 			(String)payInterfaceMap.get("payInterFaceNo"));
				command.put("pay_channelNo", 			(String)payInterface_entity.get("pay_channelNo"));
				command.put("command_no", 				commandNo);
				
				command.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
				command.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
				command.put("call_type", 				payInterfaceMap.get("call_type").toString());

				Long bankCardType = null;
				if(payInterfaceMap.get("bankId") != null) {
//					MemberServiceImpl memberServiceImpl = new MemberServiceImpl();
//					Map<String, Object> bank_card = memberServiceImpl.getBankCardById((Long)payInterfaceMap.get("bankId"));
					Map<String,Object> bank_card = DynamicEntityService.getEntity((Long)payInterfaceMap.get("bankId"), "AMS_MemberBank");
					bankCardType = (Long)bank_card.get("card_type");
	                if(bank_card == null)
	                    throw new Exception("会员银行卡不存在");
	                command.put("bank_code",         	(String)bank_card.get("bank_code"));
	                command.put("phone", 				(String)bank_card.get("phone"));
	                command.put("bank_name", 			(String)bank_card.get("bank_name"));
	                command.put("accountNo",         	(String)bank_card.get("accountNo"));
	                command.put("account_name",      	(String)bank_card.get("account_name"));
	                command.put("contract_no",      	(String)bank_card.get("contract_no"));
	                command.put("card_type",         	bank_card.get("card_type").toString());
	                if(Constant.BANK_CARD_XY.equals((Long)bank_card.get("card_type"))){
	                	 command.put("acct_validdate",  bank_card.get("acct_validdate").toString());
	                	 command.put("cvv2",         	bank_card.get("cvv2").toString());
	                }
	                command.put("account_prop",      	bank_card.get("account_prop") != null ? bank_card.get("account_prop").toString() : "");
	                command.put("accountNo_encrypt",    (String)bank_card.get("accountNo_encrypt"));
	            }

				if(payInterfaceMap.get("bank_code") != null) {
					command.put("bank_code",         	payInterfaceMap.get("bank_code").toString());
	            }
				if(payInterfaceMap.get("accountNo_encrypt") != null)
					command.put("accountNo_encrypt",	payInterfaceMap.get("accountNo_encrypt").toString());
	            if(payInterfaceMap.get("accountNo") != null)
	            	command.put("accountNo",     		payInterfaceMap.get("accountNo").toString());
	            if(payInterfaceMap.get("account_name") != null)
	            	command.put("account_name",      	payInterfaceMap.get("account_name").toString());
	            if(payInterfaceMap.get("card_type") != null)
	            	command.put("card_type",         	payInterfaceMap.get("card_type").toString());
	            if(payInterfaceMap.get("account_prop") != null)
	            	command.put("account_prop",     	payInterfaceMap.get("account_prop").toString());
	            if(payInterfaceMap.get("handling_mode") != null)
	            	command.put("handling_mode",     	payInterfaceMap.get("handling_mode").toString());
	            if(payInterfaceMap.get("remark") != null){
	            	command.put("remark",         		payInterfaceMap.get("remark").toString());
	            }else{
	            	if(order_entity.get("summary") != null){
	            		command.put("remark",         		order_entity.get("summary").toString());
	            	}
	            }
	            if(payInterfaceMap.get("extend_info") != null)
	            	command.put("extend_info",       	payInterfaceMap.get("extend_info").toString());
	            if(payInterfaceMap.get("phone") != null)
	            	command.put("phone",       			payInterfaceMap.get("phone").toString());
	            if(payInterfaceMap.get("bank_name") != null)
	            	command.put("bank_name",       		payInterfaceMap.get("bank_name").toString());
	            if(payInterfaceMap.get("pay_type") != null){
	            	command.put("pay_type",       	payInterfaceMap.get("pay_type").toString());
	            	
	            	if((Long)payInterfaceMap.get("pay_type") == 27){
	            		ret.put("receiveUrl", Environment.instance().getSystemProperty("certPay.receiveUrl"));
	            		ret.put("commandNo", commandNo);
						ret.put("orderDatetime", sdf.format((Date)order_entity.get("create_time")));
	            	}

					//用于支付权限
					if((Long)payInterfaceMap.get("pay_type") == 1){
						bankCardType = Constant.CARD_BIN_CARD_TYPE_JJ;
					}
					if((Long)payInterfaceMap.get("pay_type") == 11){
						bankCardType = Constant.CARD_BIN_CARD_TYPE_DJ;
					}
	            }

				//判断支付权限
				logger.info("payType=" + payInterfaceMap.get("pay_type"));
				logger.info("bankCardType=" + bankCardType);
				Map<String, Object> payInfo = new HashMap<>();
				payInfo.put("orgNo", order_entity.get("orgNo"));
				payInfo.put("payInterfaceNo", payInterfaceMap.get("payInterFaceNo"));
				if(bankCardType != null){
					payInfo.put("bankCardType", bankCardType);
				}
				Order.judgeAppicationPayPermission(payInfo, session);

				commandList.add(command);
				command_index++;
	    	}
	    	return commandList;
	    }
	
	@Override
	public void completePay(Map<String, Object> order_entity, Session session) throws Exception {
		// TODO Auto-generated method stub

		Boolean isFail = (Boolean)order_entity.get("isFail");
		if(Boolean.TRUE.equals(isFail)){

		}else{
			completeOrder(order_entity, null, session);
		}
		//交易监测
		List<Map<String, Object>> commandList = getCommands((String)order_entity.get("orderNo"),session);
		for(Map<String, Object> command : commandList){
			TransMonitor.monitor("response" ,command, new HashMap<String, Object>());
		}
	}
	
	@Override
	public String completeOrder(Map<String, Object> order_entity, Map<String, Object> param, Session session)
			throws Exception {
		// TODO Auto-generated method stub
		//向用户收取手续费
//		receiveFees(order_entity,session);
//		orderSplitMoney((Long)order_entity.get("id"), session);
		
		//去分收款人的手续费
		if(order_entity.get("fee")!=null &&((Long)order_entity.get("fee")>0L)){
			String target_userid = (String)order_entity.get("target_uuid");
			operateFee(order_entity,target_userid,(Long)order_entity.get("fee"),(Long)order_entity.get("account_type_id"), "消费手续费", session);
		}
		//分账
		if(order_entity.get("splitRule")!=null && !"".equals(order_entity.get("splitRule"))){
			logger.info("splitRule:"+order_entity.get("splitRule"));
			String target_userid = (String)order_entity.get("target_uuid");
			operateSpitRule(order_entity,target_userid,new JSONArray((String)order_entity.get("splitRule")),(Long)order_entity.get("account_type_id"),session);
		}
		
		super.completeOrder(order_entity, null, session);
		sendProducerMessageWithKafka((String)order_entity.get("orderNo"),"OK");
		return null;
	}
	
	//操作手续费
	private void operateFee(Map<String,Object> orderEntity,String source_userId,Long fee,Long account_type_id, String remark, Session session)throws Exception{
		 Map<String,Object> source_accountType = null;
		 if(account_type_id !=null){
			 source_accountType =  DynamicEntityService.getEntity(account_type_id, "AMS_AccountType");
		 }else{
			 AccountService accountService  = new AccountServiceImpl();
			 source_accountType = accountService.getApplicationCashAccountType((Long)orderEntity.get("application_id"));
		 }
		 Map<String,Object> target_accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
		 MemberService memberService = new MemberServiceImpl();
		 Map<String,Object> source_member = memberService.getUserInfo(source_userId);
		 Map<String, Object> target_member = new Member().getUserInfoByOrgNo((String)orderEntity.get("orgNo"), session);
		 Map<String, Object> param = new HashMap<>();
         param.put("source_userId",      (String)source_member.get("userId"));
         param.put("source_memberNo",    (String)source_member.get("memberNo"));
         param.put("source_member_name", (String)source_member.get("name"));
         param.put("target_userId",      (String)target_member.get("userId"));
         param.put("target_memberNo",    (String)target_member.get("memberNo"));
         param.put("target_member_name", (String)target_member.get("name"));
         param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
         param.put("sub_trade_type",         Constant.SUB_TRADE_TYPE_FEE);
         param.put("trade_money",        fee);
         param.put("bizid",              (String)orderEntity.get("orderNo"));
         param.put("remark",             remark);
         param.put("account_type_id",    (Long)source_accountType.get("id"));
         param.put("target_account_type_id",    (Long)target_accountType.get("id"));
         param.put("isMaster",           true);
         param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
         param.put("orgNo",    			(String)orderEntity.get("orgNo"));
	     TradeService.customTransfer(param);
	}
	
	//操作分账
//	private void operateSpitRule(Map<String,Object> orderEntity,String source_userId,JSONArray spliRuteList,Long account_type_id,Session session)throws Exception{
//		for(int i=0;i<spliRuteList.length();i++){
//			JSONObject spliRute = spliRuteList.getJSONObject(i);
//			String target_bizUserId = spliRute.getString("bizUserId");
//			Long amount = spliRute.getLong("amount");
//			MemberService memberService = new MemberServiceImpl();
//			Map<String,Object> source_member = memberService.getUserInfo(source_userId);
//			Map<String,Object> target_member = memberService.getUserInfo((Long)orderEntity.get("application_id"), target_bizUserId);
//			if(amount.longValue()>0){
//				Map<String,Object> accountType = null;
//				if(account_type_id !=null){
//					accountType =  DynamicEntityService.getEntity(account_type_id, "AMS_AccountType");
//				}else{
//					AccountService accountService  = new AccountServiceImpl();
//					accountType = accountService.getApplicationCashAccountType((Long)orderEntity.get("application_id"));
//				}
//				Map<String, Object> param = new HashMap<String, Object>();
//				param.put("source_userId",      (String)source_member.get("userId"));
//	        	param.put("source_memberNo",    (String)source_member.get("memberNo"));
//	        	param.put("source_member_name", (String)source_member.get("name"));
//	            param.put("target_userId",      (String)target_member.get("userId"));
//	            param.put("target_memberNo",    (String)target_member.get("memberNo"));
//	            param.put("target_member_name", (String)target_member.get("name"));
//	            param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
//	            param.put("sub_trade_type",         Constant.SUB_TRADE_TYPE_SPLIT);
//	            param.put("trade_money",        amount);
//	            param.put("bizid",              (String)orderEntity.get("orderNo"));
//	            param.put("remark",             (String)orderEntity.get("remark"));
//	            param.put("account_type_id",    (Long)accountType.get("id"));
//	            param.put("target_account_type_id",    (Long)accountType.get("id"));
//	            param.put("isMaster",           true);
//	           
//	            param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
//	            param.put("orgNo",    			(String)orderEntity.get("orgNo"));
//	            
//	            TradeService.customTransfer(param);
//			}
//			
//			if(spliRute.get("fee")!=null &&(spliRute.getLong("fee")>0L)){
//				operateFee(orderEntity,(String)target_member.get("userId"),spliRute.getLong("fee"),account_type_id,session);
//			}
//			if(spliRute.get("splitRuleList") != null){
//				operateSpitRule(orderEntity,(String)target_member.get("userId"),spliRute.getJSONArray("splitRuleList"),account_type_id,session);
//			}
//			
//		}
//	}
	
	//操作分账
	private void operateSpitRule(Map<String,Object> orderEntity,String source_userId,JSONArray splitRuteList, Long sourceAccountTypeId, Session session)throws Exception{
		for(int i=0;i<splitRuteList.length();i++){
			JSONObject splitRute = splitRuteList.getJSONObject(i);
			String target_bizUserId = splitRute.getString("bizUserId");
			String targetAccountSetNo = (String)splitRute.get("accountSetNo");
			Long amount = splitRute.getLong("amount");
			String splitRemark = (String) splitRute.get("remark");
			MemberService memberService = new MemberServiceImpl();
			Map<String,Object> source_member = memberService.getUserInfo(source_userId);
			
			//获取分账目标用户的实体
			Map<String,Object> target_member;
			if(Constant.APPLICATION_BIZ_USER_ID.equals(target_bizUserId)){ //目标用户为平台
				Map<String, Object> applicationEntity = DynamicEntityService.getEntity((Long)orderEntity.get("application_id"), "AMS_Organization");
				target_member = memberService.getUserInfo((Long)applicationEntity.get("member_id"));
			}else{ //目标用户为会员
				target_member = memberService.getUserInfo((Long)orderEntity.get("application_id"), target_bizUserId);
			}

//			//检测账户集和应用是否匹配
//			if(!StringUtils.isBlank(targetAccountSetNo)){
//				AccountCheck.isAccountTypeBelongApp((String) target_member.get("userId"), targetAccountSetNo, session);
//			}
			
			//获取源账户集类型
			AccountService accountService  = new AccountServiceImpl();
			
			Map<String,Object> sourceAccountType = null;
			if(sourceAccountTypeId != null){
				sourceAccountType = DynamicEntityService.getEntity(sourceAccountTypeId, "AMS_AccountType");
			}else{
				sourceAccountType = accountService.getApplicationCashAccountType((Long)orderEntity.get("application_id"));
			}
			
			//获取目标账户集类型
			Map<String,Object> targetAccountType = null;
			if(targetAccountSetNo !=null){
				targetAccountType = accountService.getAccountTypeByNo(targetAccountSetNo);
			}else{
				targetAccountType = accountService.getApplicationCashAccountType((Long)orderEntity.get("application_id"));
			}
					
			if(amount.longValue()>0){
				Map<String, Object> param = new HashMap<>();
				param.put("source_userId",      (String)source_member.get("userId"));
	        	param.put("source_memberNo",    (String)source_member.get("memberNo"));
	        	param.put("source_member_name", (String)source_member.get("name"));
	            param.put("target_userId",      (String)target_member.get("userId"));
	            param.put("target_memberNo",    (String)target_member.get("memberNo"));
	            param.put("target_member_name", (String)target_member.get("name"));
	            param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
	            param.put("sub_trade_type",         Constant.SUB_TRADE_TYPE_SPLIT);
	            param.put("trade_money",        amount);
	            param.put("bizid",              (String)orderEntity.get("orderNo"));
	            param.put("remark",             splitRemark);
	            param.put("account_type_id",    (Long)sourceAccountType.get("id"));
	            param.put("target_account_type_id",    (Long)targetAccountType.get("id"));
	            param.put("isMaster",           true);
	           
	            param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
	            param.put("orgNo",    			(String)orderEntity.get("orgNo"));
	            
	            TradeService.customTransfer(param);
			}
			
			if(splitRute.get("fee")!=null &&(splitRute.getLong("fee")>0L)){
				operateFee(orderEntity,(String)target_member.get("userId"), splitRute.getLong("fee"), (Long)targetAccountType.get("id"), "消费分账手续费", session);
			}
			if(splitRute.get("splitRuleList") != null){
				operateSpitRule(orderEntity,(String)target_member.get("userId"),splitRute.getJSONArray("splitRuleList"),(Long)targetAccountType.get("id"),session);
			}
		}
	}
	
	
	//是否是b2c订单
	public  boolean isB2c(Long applicationId,Long targetMemberId)throws Exception{
  		Map<String,Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
  		Long applictionMemberId = (Long)applicationEntity.get("member_id");
  		if(applictionMemberId.longValue()==targetMemberId.longValue()){
  			return true;
  		}
  		return false;
  	}
	
	
}
