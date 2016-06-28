package bps.order;

import bps.check.limit.AccountCheck;
import bps.monitor.TransMonitor;
import ime.core.Environment;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.kinorsoft.ams.services.QueryService;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.code.CodeServiceImpl;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.common.BizException;
import bps.external.tradecommand.ItsManage;
import bps.member.Member;
import bps.process.PayChannelManage;
import bps.service.AccountService;

import org.json.JSONObject;

/**
 * 充值订单
 * @author 吴海涛
 *
 */
public class DepositOrder extends Order {
	private static Logger logger = Logger.getLogger(DepositOrder.class.getName());
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
		logger.info("DepositOrder.applyOrder 参数=" + param);

		Long memberId = (Long) param.get("memberId");
		Long money = (Long) param.get("money");
		final Long bankCardId=(Long) param.get("bankCardId");
		final List accountList = (List)param.get("accountList");
		final List payInterfaceList = (List)param.get("payInterfaceList");
//		final String accountSetNo = (String) param.get("accountSetNo");
		String posCode = "";
		if(memberId == null || memberId == 0L)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数memberId");
		if(money == null || money == 0L)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数money");
		if(param.get("applicationId")== null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数applicationId");
		try {
			final Member member = new Member(memberId);
			Map<String, Object> user = member.getUserInfo();
			final Map<String, String> entity = new HashMap<>();
			entity.put("create_time", String.valueOf(new Date().getTime()));
			entity.put("member_id",             memberId.toString());
			entity.put("member_name",           user.get("name") == null ? "" : user.get("name").toString());
			entity.put("member_uuid",           user.get("userId").toString());
			entity.put("memberNo",  			user.get("memberNo").toString());
			entity.put("order_money", 			money.toString());
			entity.put("order_state", 			Constant.ORDER_STATE_WAIT_PAY.toString());//未支付
			entity.put("order_type",  			Constant.ORDER_TYPE_DEPOSIT.toString());//充值
			entity.put("trade_type",  			Constant.TRADE_TYPE_DEPOSIT.toString());
			entity.put("sub_trade_type",  		param.get("subTradeType") == null ? null : param.get("subTradeType").toString());
			entity.put("orgNo",                 param.get("orgNo").toString());
            entity.put("source",                param.get("source").toString());
            entity.put("application_id",         param.get("applicationId").toString());
            entity.put("application_label",      param.get("applicationName").toString());
            entity.put("bizOrderNo",             param.get("bizOrderNo").toString());
            entity.put("industry_code",          String.valueOf(param.get("industry_code")));
            entity.put("industry_name",          String.valueOf(param.get("industry_name")));
            entity.put("frontUrl",               String.valueOf(param.get("frontUrl")));
            entity.put("backUrl",                String.valueOf(param.get("backUrl")));
            entity.put("description",           "充值");
			if(param.get("subTradeType")!=null){
				 entity.put("sub_trade_type",String.valueOf(param.get("subTradeType")));
			}
            if(param.get("fee") != null){
            	 entity.put("fee", String.valueOf(param.get("fee")));
            }
            if(param.get("ordErexpireDatetime") != null){
            	entity.put("ordErexpireDatetime", String.valueOf(((Date)param.get("ordErexpireDatetime")).getTime()));
            }
            if(param.get("extend_info") != null){
            	entity.put("extend_info", String.valueOf(param.get("extend_info")));
            }
            if(param.get("summary") != null){
            	entity.put("summary", String.valueOf(param.get("summary")));
            }
            if(param.get("bank_code")!= null &&!"".equals(param.get("bank_code").toString().trim())){
            	entity.put("bank_code", String.valueOf(param.get("bank_code")));
            }
            if(param.get("bank_name")!= null &&!"".equals(param.get("bank_name").toString().trim())){
            	entity.put("bank_name", String.valueOf(param.get("bank_name")));
            }
            
            if(bankCardId != null){
				Map<String, Object> bank_card = DynamicEntityService.getEntity(bankCardId, "AMS_MemberBank");
				if(bank_card == null)
					throw new BizException(ErrorCode.CARD_BIND_LOG_NOTEXSIT,"会员银行卡不存在");
				if(Constant.BANK_CARD_XY.equals(bank_card.get("card_type"))){
					throw new BizException(ErrorCode.NOT_SUPPORT_XY_CARD,"信用卡不能进行充值");
				}
				entity.put("bank_code", 				bank_card.get("bank_code").toString());
				entity.put("account_name", 				bank_card.get("account_name").toString());
				entity.put("accountNo", 				bank_card.get("accountNo").toString());
				entity.put("accountNo_encrypt", 		bank_card.get("accountNo_encrypt").toString());
				entity.put("bank_name", 				bank_card.get("bank_name").toString());
				entity.put("card_type", 				bank_card.get("card_type").toString());
				entity.put("account_prop", 				bank_card.get("account_prop").toString());
            }
			//产生pos支付码
            if(payInterfaceList != null && payInterfaceList.size() > 0){
				Map<String, Object> payEntity = (Map<String, Object>)payInterfaceList.get(0);
				logger.info("Constant.PAY_INTERFACE_POS:"+Constant.PAY_INTERFACE_POS);
				if( Constant.PAY_INTERFACE_POS.equals(payEntity.get("payInterFaceNo"))){
					posCode = getPosCode();
					entity.put("pos_pay_code",posCode);
				}
            }
			
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            
            Map<String,Object> ret = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
				@Override
				public boolean before(Session session) throws Exception {
					Map<String,Object> orderEntity = getOrder(Long.parseLong(entity.get("application_id")),entity.get("bizOrderNo"), session);
					if(orderEntity != null){
						Long orderType = (Long)orderEntity.get("order_type");
						if(!Constant.ORDER_TYPE_DEPOSIT.equals(orderType)){
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
						if(!orderEntity.get("member_id").toString().equals( entity.get("member_id"))){
							throw new BizException(ErrorCode.ORDER_ERROR,"源订单用户和目前订单不符");
						}
						entity.put("id", 				orderEntity.get("id").toString());
					}

//					//检查账户集和应用是否匹配
//					AccountCheck.isAccountTypeBelongApp(entity.get("member_uuid"), accountSetNo, session);

					//检查指令
					Map<String,Object> checkParam = new HashMap<>();
					checkParam.put("orderMoney", Long.parseLong(entity.get("order_money")));
					checkParam.put("application_id", Long.parseLong(entity.get("application_id")));
					if(entity.get("fee") != null){
						checkParam.put("fee", Long.parseLong(entity.get("fee")));
					}
					checkPayCommands(checkParam,accountList,payInterfaceList,session);
					return true;
				}

				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					logger.info(entity);
					Long orderId = null;
					if(entity.get("id") == null){
						Map<String, Object> temp = DynamicEntityService.createEntity("AMS_Order", entity, null);
						Long id = (Long)temp.get("id");
						String orderNo = Order.generateOrderNo(id);
						temp.put("orderNo", orderNo);
						Query query = session.createQuery("update AMS_Order set orderNo=:orderNo where id=:orderId");
						query.setParameter("orderNo", orderNo);
						query.setParameter("orderId", id);
						query.executeUpdate();
						orderId = id;
					}else{
						//重复提交订单
						DynamicEntityService.modifyEntity("AMS_Order", entity, null);
						orderId = Long.parseLong(entity.get("id"));
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
				ret.put("paycode", posCode);
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
	
	 
	
	 /****
	  * 
	 * @Title: checkPayCommands
	 * @Description: TODO(检查指令中生存的问题)
	 * @param @param param
	 * @param @param accountList
	 * @param @param payInterfaceList
	 * @param @param session
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws
	  */
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		Long fee = (Long)param.get("fee");
		if(fee!=null &&fee>0){
//			payMoney =+ fee;
			//计算手续费，直接面向用户收取，用户充值100，先到帐100，在扣取2快手续费
			AccountService accountService = new AccountServiceImpl();
			Map<String, Object> source_accountType = accountService.getApplicationCashAccountType((Long)param.get("application_id"));;
			if(source_accountType == null){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT,"此应用下现金账户集不存在");
			}
			Map<String, Object> target_accountEntity = QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
			Map<String,Object> applicationEntity = DynamicEntityService.getEntity((Long)param.get("application_id"), "AMS_Organization");
			if(applicationEntity == null){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
			}
			Long application_member_uuid = (Long)applicationEntity.get("member_id");
			Map<String, Object> accountMap = new HashMap<String, Object>();
			Member fee_member = new Member(application_member_uuid);
			
			accountMap.put("tradeMoney", fee);
			accountMap.put("target_userId", fee_member.getUserId());
			accountMap.put("target_memberNo", fee_member.getMemberNo());
			accountMap.put("target_member_name", fee_member.getName());
			accountMap.put("trade_type", Constant.TRADE_TYPE_TRANSFER);
			accountMap.put("sub_trade_type", Constant.SUB_TRADE_TYPE_FEE);
			accountMap.put("account_type_id", source_accountType.get("id"));
			accountMap.put("account_type_label", source_accountType.get("name"));
			accountMap.put("accountNo", source_accountType.get("codeNo"));
			accountMap.put("call_type", Constant.CALL_TYPE_INTERFACE);
			accountMap.put("pay_interfaceNo", Constant.PAY_INTERFACE_AMS);
			accountMap.put("pay_channelNo", Constant.PAY_CHANNEL_AMS);
			accountMap.put("target_account_type_id", target_accountEntity.get("id"));
			accountMap.put("target_account_type_label", target_accountEntity.get("name"));
			accountMap.put("isMaster", false);
			accountMap.put("handling_mode", Constant.FEE_CHARGE_TYPE_NETTING);
			
			accountList.add(accountMap);
		}
		if(!payMoney.equals(orderMoney)) {
			logger.error("支付金额不符。payMoney=" + payMoney + ",orderMoney=" + orderMoney);
			throw new Exception("支付金额不符");
		}
		
	}
	
	/***
	 * 
	* @Title: generatePayCommands 
	* @Description: TODO(生成指令) 
	* @param @param order_entity
	* @param @param accountList
	* @param @param payInterfaceList
	* @param @param session
	* @param @throws Exception    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	private Long command_index = 1L;
	@SuppressWarnings("rawtypes")
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
//			temp.put("CNL_MCHT_NAME",(String)order_entity.get("industry_name"));
//			temp.put("CNL_MCHT_TYPE",(String)order_entity.get("industry_code"));
////			temp.put("CNL_MCHT_ID",(String)order_entity.get("orgNo"));
//			temp.put("CNL_MCHT_ID",Constant.ORG_NO_ALLINPAY);
			temp.put("CNL_MCHT_ID",Constant.ORG_NO_ALLINPAY);
			temp.put("CNL_MCHT_NAME","通联钱包-账户充值");
			if(command.get("bank_code").equals("01030000")) {//农行
				temp.put("CNL_MCHT_TYPE","1117");
			}if(command.get("bank_code").equals("03080000")){//招行
				temp.put("CNL_MCHT_TYPE","1116");
			}else{
				temp.put("CNL_MCHT_TYPE","2513");
			}
			
//			temp.put("isSendSM",isSendSM.toString());
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
					Map<String, String> result_its2 = ItsManage.payMessageSend(temp);
					if( "000000".equals(result_its2.get("RET_CODE")) || "359037".equals(result_its2.get("RET_CODE"))){
						ret.put("oriTraceNum", result_its.get("TRACE_NUM"));
						ret.put("oriTransDate", result_its.get("TRANS_DATE"));
					}else{
						if(result_its2.get("ERROR_MSG") != null && !"".equals(result_its2.get("ERROR_MSG").toString())){
							throw new Exception(result_its2.get("ERROR_MSG"));
						}else{
							throw new Exception(result_its2.get("RET_MSG"));
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
			if(Constant.TRADE_TYPE_DEPOSIT.equals(order_entity.get("trade_type")) && Constant.SUB_TRADE_TYPE_DEPOSIT_WITHOUT_CONFIRM.equals(order_entity.get("sub_trade_type"))) {
				logger.info("无验证充值");
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
				codeServiceImpl.generatePhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN,member.getPhone(), Constant.VERIFICATION_CODE_AUTH_PAY, temp);
			}
		}
		return ret;
		
	}
	 
	/***
	 * 
	* @Title: generatePayCommandByAccount 
	* @Description: TODO(生成内部账户的指令) 
	* @param @param order_entity
	* @param @param accountList
	* @param @param commandList
	* @param @param session
	* @param @return
	* @param @throws Exception    设定文件 
	* @return List<Map<String,String>>    返回类型 
	* @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Map<String, String>> generatePayCommandByAccount(Map<String, Object> order_entity,
			List accountList, List<Map<String, String>> commandList, Session session) throws Exception {
		
		//int k = commandList.size() + 1;
		for(Object obj : accountList) {
			Map<String, Object> accountMap = (Map<String, Object>) obj;
			Map<String, String> command = new HashMap<String, String>();
			
			Long tradeType = (Long) accountMap.get("trade_type");
			Long subTradeType = (Long) accountMap.get("sub_trade_type");
			if(tradeType != null && tradeType.equals(Constant.TRADE_TYPE_TRANSFER) && subTradeType.equals(Constant.SUB_TRADE_TYPE_FEE)) {
				command.put("target_userId", 		(String)accountMap.get("target_userId"));
				command.put("target_memberNo", 		(String)accountMap.get("target_memberNo"));
				command.put("target_member_name", 	(String)accountMap.get("target_member_name"));
				command.put("trade_type", 			tradeType.toString());
				command.put("sub_trade_type", 		subTradeType.toString());
				command.put("target_account_type_id", 			accountMap.get("target_account_type_id").toString());
				command.put("target_account_type_label", 		(String)accountMap.get("target_account_type_label"));
				command.put("target_account_codeNo", 			(String)accountMap.get("target_account_codeNo"));
				command.put("account_type_id", 			accountMap.get("account_type_id").toString());
				command.put("account_type_label", 		(String)accountMap.get("account_type_label"));
				command.put("account_codeNo", 			(String)accountMap.get("account_codeNo"));
				command.put("remark",                "充值手续费");
			} else {
				//判断支付权限
				Map<String, Object> payInfo = new HashMap<String, Object>();
				payInfo.put("orgNo", (String)order_entity.get("orgNo"));
				payInfo.put("payInterfaceNo", Constant.PAY_INTERFACE_AMS);
				payInfo.put("account", Boolean.TRUE);
				Order.judgeAppicationPayPermission(payInfo, session);

				command.put("target_userId", 		(String)order_entity.get("target_uuid"));
				command.put("target_memberNo", 		(String)order_entity.get("target_memberNo"));
				command.put("target_member_name", 	(String)order_entity.get("target_member_name"));
				command.put("trade_type", 			order_entity.get("trade_type").toString());
				command.put("sub_trade_type", 		order_entity.get("sub_trade_type").toString());
				
				Map<String, Object> cashAccountEntity = QueryService.getAccountType((String)accountMap.get("accountNo"));
				command.put("account_type_id", 			cashAccountEntity.get("id").toString());
				command.put("account_type_label", 		(String)cashAccountEntity.get("name"));
				command.put("account_codeNo", 			(String)cashAccountEntity.get("codeNo"));
				command.put("target_account_type_id", 			cashAccountEntity.get("id").toString());
				command.put("target_account_type_label", 		(String)cashAccountEntity.get("name"));
				command.put("target_account_codeNo", 			(String)cashAccountEntity.get("codeNo"));
			}
			command.put("source_userId", 			(String)order_entity.get("member_uuid"));
			command.put("source_memberNo", 			(String)order_entity.get("memberNo"));
			command.put("source_member_name", 		(String)order_entity.get("member_name"));
			
			command.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
			command.put("command_no", 				(String)order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + command_index);
			command.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
			command.put("seq_no", 					command_index + "");
			command.put("trade_type", 				tradeType.toString());
			command.put("sub_trade_type", 			subTradeType.toString());
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

			if(command.get("remark") == null){
				if(accountMap.get("remark") != null){
					command.put("remark",         		accountMap.get("remark").toString());
				}else{
					if(order_entity.get("summary") != null){
						command.put("remark",         		order_entity.get("summary").toString());
					}
				}
			}

            if(accountMap.get("extend_info") != null)
            	command.put("extend_info",       	accountMap.get("extend_info").toString());
            commandList.add(command);
            command_index++;
		}
		
		for(Map<String, String> command : commandList) {
			command.put("pay_serialNo", 		command.get("command_no"));
			DynamicEntityService.createEntity("AMS_OrderPayDetail", command, null);
		}

		Query query = session.createQuery("update AMS_Order set is_exsit_command=true where orderNo=:orderNo");
		query.setParameter("orderNo", order_entity.get("orderNo"));
		query.executeUpdate();
		
		return commandList;
	}
	
	/****
	 * 
	* @Title: generatePayCommandsByPayInterface 
	* @Description: TODO(生成通道的指令) 
	* @param order_entity
	* @param @param payInterfaceList
	* @param @param session
	* @param @return
	* @param @throws Exception    设定文件 
	* @return List<Map<String,String>>    返回类型 
	* @throws
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map<String, String>> generatePayCommandsByPayInterface(Map<String, Object> order_entity, List payInterfaceList, Map<String, Object> ret, Session session)throws Exception{
	    	List<Map<String, String>> commandList = new ArrayList<Map<String, String>>();
			Map<String, String> command = null;
			Map<String, Object> source_accountEntity = QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
	    	for(int i = 0, j = payInterfaceList.size(); i < j; i ++) {
				Map<String, Object> payInterfaceMap = (Map<String, Object>) payInterfaceList.get(i);
				//此现金账户暂时有外部传入,
	    		String accountSetNo = (String)payInterfaceMap.get("accountSetNo");
	    		Map<String, Object> target_accountEntity = QueryService.getAccountType(accountSetNo);
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

				//通联通代付才会走
				if(payInterfaceMap.get("bank_code") != null) {
					command.put("bank_code",         	payInterfaceMap.get("bank_code").toString());
	            }
	            if(payInterfaceMap.get("bank_name") != null)
	            	command.put("bank_name",       		payInterfaceMap.get("bank_name").toString());
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
	            if(payInterfaceMap.get("phone") != null)
	            	command.put("phone",       			payInterfaceMap.get("phone").toString());
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
	            if(payInterfaceMap.get("pay_type") != null){
	            	command.put("pay_type",       	payInterfaceMap.get("pay_type").toString());
	            	
	            	logger.info("pay_type:" + (Long)payInterfaceMap.get("pay_type"));
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
				Map<String, Object> payInfo = new HashMap<String, Object>();
				payInfo.put("orgNo", (String)order_entity.get("orgNo"));
				payInfo.put("payInterfaceNo", (String)payInterfaceMap.get("payInterFaceNo"));
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
//		shoppingOrderSplitMoney((Long)order_entity.get("id"), session);
		Long subTradeType= (Long) order_entity.get("sub_trade_type");
		String memberUuid= (String) order_entity.get("member_uuid");
		logger.info("会员uuid:"+memberUuid);
		Long trade_type= (Long) order_entity.get("trade_type");
		if (Constant.SUB_TRADE_TYPE_HIGHER_CARD.equals(subTradeType) && Constant.TRADE_TYPE_DEPOSIT.equals(trade_type)){
			Query query=session.createQuery("update AMS_Member set is_higher_card=:is_higher_card where userId=:userId");
			query.setParameter("userId",memberUuid);
			query.setParameter("is_higher_card",true);
			logger.info("已认证成功is_higher_card变为true");
			query.executeUpdate();
		}

		super.completeOrder(order_entity, null, session);
//		Long oid =(Long)order_entity.get("id");
//		final Map<String,Object> order_detail = getOrderDetailByOrderNo(oid, session);
		sendProducerMessageWithKafka((String)order_entity.get("orderNo"),"OK");
		return null;
	
	}
}
