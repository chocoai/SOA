package bps.order;

import bps.check.limit.WithdrawOrderLimit;
import bps.common.JedisUtils;
import bps.member.MemberServiceImpl;
import bps.monitor.TransMonitor;
import bps.service.MemberService;

import com.kinorsoft.ams.services.TradeService;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.code.CodeServiceImpl;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.common.BizException;
import bps.member.Member;
import bps.process.Extension;
import bps.process.PayChannelManage;
import bps.service.AccountService;


import com.kinorsoft.ams.services.QueryService;

/**
 * 提现订单
 * @author 吴海涛
 *
 */
public class WithdrawOrder extends Order {
	private static Logger logger = Logger.getLogger(WithdrawOrder.class.getName());

	@Override
	public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
		logger.info("WithdrawOrder.createOrder start");
		try {
			Long member_id = (Long) param.get("memberId");
			Long bank_card_id = (Long) param.get("bankCardId");
			Long orderMoney = (Long) param.get("money");
			String withdrawType = (String) param.get("withdrawType");
			final List accountList = (List)param.get("accountList");
			final List payInterfaceList = (List)param.get("payInterfaceList");
//			final String accountSetNo = (String) param.get("accountSetNo");
			
			if(member_id == null || member_id == 0L)
				throw new Exception("请传入参数member_id");
			if(bank_card_id == null || bank_card_id == 0L)
				throw new Exception("请传入参数bank_card_id");
			if(orderMoney == null || orderMoney == 0L)
				throw new Exception("请传入参数money");
			Calendar calendar = Calendar.getInstance();
			
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			
			final Member member = new Member(member_id);
			if(member.getUserId() == null)
				throw new Exception("会员不存在");
			
			Map<String, Object> bank_card = DynamicEntityService.getEntity(bank_card_id, "AMS_MemberBank");
			if(bank_card == null)
				throw new Exception("会员银行卡不存在");

			if( bank_card.get("card_type").equals(Constant.BANK_CARD_XY) ){
				throw new BizException(ErrorCode.NOT_SUPPORT_XY_CARD, "提现不支持信用卡");
			}
			Long member_type = member.getMember_type();
			Long bankCardPro = bank_card.get("account_prop") == null ? 0L : (Long)bank_card.get("account_prop");
			if(  member_type.equals(Constant.MEMBER_TYPE_ENTERPRISE) && bankCardPro.equals(0L) && !Boolean.TRUE.equals(bank_card.get("is_person_card_checked"))){
				throw new BizException(ErrorCode.ORDER_ERROR, "企业个人银行卡没有审核！");
			}
			String  bank_code = "";
			if(bank_card.get("bank_code") !=null){
				bank_code = (String)bank_card.get("bank_code");
			}
			//检查是否满足代付设置
			WithdrawOrderLimit.checkPayType(Long.parseLong(param.get("applicationId").toString()), withdrawType);
			final String _withdrawType = withdrawType;
			final Long _applicationId = Long.parseLong(param.get("applicationId").toString());
			final Long _bank_card_id = bank_card_id;
			if(Constant.MEMBER_TYPE_PERSON.equals(member.getMember_type())){
				EntityManagerUtil.execute(new QueryWork<Object>() {
					@Override
					public Object doQuery(Session session) throws Exception {
						// TODO Auto-generated method stub
						Order.checkSetSafeCard(_applicationId,_bank_card_id,session);
						return null;
					}
				});
			}
			
			final Map<String, String> entityMap = new HashMap<>();
			
			entityMap.put("bank_code", 				bank_code);
			entityMap.put("account_name", 			(String)bank_card.get("account_name"));
			entityMap.put("accountNo", 				(String)bank_card.get("accountNo"));
			entityMap.put("accountNo_encrypt", 		(String)bank_card.get("accountNo_encrypt"));
			entityMap.put("bank_name", 				(String)bank_card.get("bank_name"));
			entityMap.put("card_type", 				bank_card.get("card_type").toString());
			entityMap.put("account_prop", 			bank_card.get("account_prop").toString());
			
			entityMap.put("create_time", 			String.valueOf(calendar.getTime().getTime()));
			
			entityMap.put("member_id", 				member.getId().toString());
			entityMap.put("member_name", 			member.getName());
			entityMap.put("member_uuid", 			member.getUserId());
			entityMap.put("memberNo", 				member.getMemberNo());
			
			entityMap.put("order_money", 			orderMoney.toString());
			entityMap.put("trade_type", 			Constant.TRADE_TYPE_WITHDRAW.toString());
			entityMap.put("order_state", 			Constant.ORDER_STATE_WAIT_PAY.toString());
			entityMap.put("order_type", 			Constant.ORDER_TYPE_WITHDRAW.toString());
//            entityMap.put("orgNo",                  Constant.ORG_NO_ALLINPAY.toString());
			entityMap.put("orgNo",                  param.get("orgNo").toString());
			entityMap.put("description", 			"提现");
			entityMap.put("source",                param.get("source").toString());
			entityMap.put("application_id",         param.get("applicationId").toString());
			entityMap.put("application_label",      param.get("applicationName").toString());
			entityMap.put("bizOrderNo",             param.get("bizOrderNo").toString());
			entityMap.put("industry_code",          String.valueOf(param.get("industry_code")));
			entityMap.put("industry_name",          String.valueOf(param.get("industry_name")));
			entityMap.put("frontUrl",               String.valueOf(param.get("frontUrl")));
			entityMap.put("backUrl",                String.valueOf(param.get("backUrl")));

			//提现方式
			entityMap.put("withdrawType",           withdrawType);
			String orgList = JedisUtils.getCacheByKey("orgList");
			JSONArray ja = new JSONArray(orgList);
			Long withdrawReserveModel = 0L;
			for(int i=0;i<ja.length();i++) {
				JSONObject obj = ja.getJSONObject(i);
				String codeNo = obj.optString("codeNo");
				if ( entityMap.get("orgNo").equals(codeNo)){
					withdrawReserveModel = obj.optLong("withdrawReserveModel");
					break;
				}
			}
			entityMap.put("withdrawReserveModel",	withdrawReserveModel.toString());

			final Long _withdrawReserveModel = withdrawReserveModel;

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
			//创建订单
			Map<String,Object> ret = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
	    	   
	    	   public boolean before(Session session) throws Exception {
					Map<String,Object> orderEntity = getOrder(Long.parseLong(entityMap.get("application_id")),entityMap.get("bizOrderNo"),session);
					if(orderEntity != null){
						Long orderType = (Long)orderEntity.get("order_type");
						if(!Constant.ORDER_TYPE_WITHDRAW.equals(orderType)){
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

//				   //检查账户集和应用是否匹配
//				   AccountCheck.isAccountTypeBelongApp((String)entityMap.get("member_uuid"), accountSetNo, session);
					
					//检查指令
					Map<String,Object> checkParam = new HashMap<>();
					checkParam.put("orderMoney", Long.parseLong(entityMap.get("order_money")));
					checkParam.put("application_id", Long.parseLong(entityMap.get("application_id")));
					if(entityMap.get("fee") != null){
						checkParam.put("fee", Long.parseLong(entityMap.get("fee")));
					}

				   //如果是实时单笔代付，有手续产生手续费--TODO 手续费（平台向用户收的）不用生成指令，在确认支付时先冻结，然后在通道完成代付时，进行解冻并进行手续费转帐。
//				   if ( _withdrawReserveModel.equals(Constant.WITHDRAW_RESERVE_MODEL_ACTIVE) && _withdrawType.equals("T0") ) {
					   checkPayCommands(checkParam, payInterfaceList);
//				   }
					return true;
				}

				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					logger.info(entityMap);
					Long orderId = null;
					if(entityMap.get("id") == null){
						Map<String, Object>  orderEntity = DynamicEntityService.createEntity("AMS_Order", entityMap, null);
						Long id = (Long)orderEntity.get("id");
						String orderNo = Order.generateOrderNo((Long)orderEntity.get("id"));
						Query query = session.createQuery("update AMS_Order set orderNo=:orderNo where id=:orderId");
						query.setParameter("orderNo", orderNo);
						query.setParameter("orderId", (Long)orderEntity.get("id"));
						query.executeUpdate();
						orderId = id;
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
					return generatePayCommands(member,orderEntity,accountList,payInterfaceList, _withdrawType, _withdrawReserveModel, session);
					
				}
	        });
			
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
	 * @Description: TODO(检查指令中生存的问题)
	 * @param param					参数
	 * @param payInterfaceList		支付列表
	 * @throws Exception    设定文件
	  */
	@SuppressWarnings("unchecked")
	private void checkPayCommands(Map<String,Object> param, List payInterfaceList ) throws Exception{
		Long payMoney = 0L;
		Long orderMoney = (Long)param.get("orderMoney");
		Long fee = (Long)param.get("fee");
		if(payInterfaceList != null&& !payInterfaceList.isEmpty()){
			Map<String, Object> payInterfaceMap = (Map<String, Object>) payInterfaceList.get(0);
			payMoney += (Long)payInterfaceMap.get("tradeMoney");
			//把此金额拆分成手续费金额
			if(fee!=null &&fee>0){
				payInterfaceMap.put("tradeMoney", (Long)payInterfaceMap.get("tradeMoney")-fee);
			}
		}
		if(fee!=null &&fee>0){
			AccountService accountService = new AccountServiceImpl();
			Map<String, Object> source_accountType = accountService.getApplicationCashAccountType((Long)param.get("application_id"));
			if(source_accountType == null){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT,"此应用下现金账户集不存在");
			}
			Map<String,Object> applicationEntity = DynamicEntityService.getEntity((Long)param.get("application_id"), "AMS_Organization");
			if(applicationEntity == null){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
			}
		}
		if(!payMoney.equals(orderMoney)) {
			throw new Exception("支付金额不符");
		}
		
	}
	private Long command_index = 1L;
	/**
	 *	生成指令
	 * @param member					会员实体
	 * @param order_entity				订单实体
	 * @param accountList				内部帐户列表
	 * @param payInterfaceList			外部帐户列表
	 * @param withdrawType				提现类型
	 * @param _withdrawReserveModel		准备金方式
	 * @param session					session
     * @return	Map 指令列表
     * @throws Exception
     */
	private Map<String,Object> generatePayCommands(Member member,Map<String, Object> order_entity,List accountList, List payInterfaceList, String withdrawType, Long _withdrawReserveModel, Session session)throws Exception{
		command_index = 1L;
		List list = getUnPayCommands((String)order_entity.get("orderNo"), session);
		if(list != null && list.size() > 0) {
			Map<String, Object> old_command = (Map<String, Object>) list.get(list.size() - 1);
			command_index = (Long)old_command.get("seq_no") + 1;
		}
		
		Query query = session.createQuery("delete AMS_OrderPayDetail where bizid=:orderNo");
        query.setParameter("orderNo", order_entity.get("orderNo"));
		query.executeUpdate();
		
		List<Map<String, String>> commandList = generatePayCommandsByPayInterface(order_entity,payInterfaceList, withdrawType, _withdrawReserveModel, session);
		generatePayCommandByAccount(order_entity, accountList, commandList, session);
		
		Map<String, Object> ret = new HashMap<>();
		ret.put("orderNo", order_entity.get("orderNo"));

		logger.info("trade_type:"+order_entity.get("trade_type"));
		logger.info("sub_trade_type:"+order_entity.get("sub_trade_type"));

		if(Constant.TRADE_TYPE_WITHDRAW.equals(order_entity.get("trade_type")) && Constant.SUB_TRADE_TYPE_WITHDRAW_WITHOUT_CONFIRM.equals(order_entity.get("sub_trade_type"))){
			//无验证提现 取消短信验证

			logger.info("无验证提现 取消短信验证");
		}else{
			logger.info("短信验证!!!");
			Map<String, Object> temp = new HashMap<>();
			temp.put("phone", member.getPhone());
			temp.put("orderNo", order_entity.get("orderNo"));
			temp.put("verification_code_type", Constant.VERIFICATION_CODE_AUTH_PAY);
			temp.put("applicationId", Constant.APPLICATION_ID_BPS_YUN);
			temp.put("member_id", member.getId());
			CodeServiceImpl codeServiceImpl = new CodeServiceImpl();
			String content =  codeServiceImpl.generatePhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN,member.getPhone(), Constant.VERIFICATION_CODE_AUTH_PAY, temp);
			logger.info("member.getPhone():"+member.getPhone());
			logger.info("content:"+content);
			Extension.otherService.sendSM(member.getPhone(), content);
		}

		return ret;
		
		
	}

	/**
	 * 生成支付指令
	 * @Title: generatePayCommandsByPayInterface
	 * @Description TODO(生成通道的指令)
	 * @param  order_entity		订单信息
	 * @param  payInterfaceList	支付信息
	 * @param  session				session
	 * @return List		返回
	 * @throws Exception
	 */
	private List<Map<String, String>> generatePayCommandsByPayInterface(Map<String, Object> order_entity, List payInterfaceList,String withdrawType, Long _withdrawReserveModel, Session session)throws Exception{
		List<Map<String, String>> commandList = new ArrayList<>();
		
//		int k = commandList.size() + 1;
		
		Map<String, String> command = new HashMap<>();
		Map<String, Object> targart_accountEntity = QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
		String accountSetNo = (String)((Map<String,Object>)payInterfaceList.get(0)).get("accountSetNo");
		String payInterFaceNo = (String)((Map<String,Object>)payInterfaceList.get(0)).get("payInterFaceNo");
		Map<String, Object> source_accountEntity = QueryService.getAccountType(accountSetNo);
		
//		JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfo(Constant.PAY_INTERFACE_TLT_DF);
//		Member member = new Member(payInterface_entity.get("system_uuid").toString());

		//判断支付权限
		Map<String, Object> payInfo = new HashMap<>();
		payInfo.put("orgNo", (String)order_entity.get("orgNo"));
		payInfo.put("payInterfaceNo", payInterFaceNo);
		payInfo.put("bankCardType", order_entity.get("card_type"));
		Order.judgeAppicationPayPermission(payInfo, session);
		
		JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfo(payInterFaceNo, (String)order_entity.get("orgNo"));
		Member member = new Member(payInterface_entity.get("system_uuid").toString());
		
		command.put("source_userId", 			(String)order_entity.get("member_uuid"));
		command.put("source_memberNo", 			(String)order_entity.get("memberNo"));
		command.put("source_member_name", 		(String)order_entity.get("member_name"));
		command.put("account_type_id", 			source_accountEntity.get("id").toString());
		command.put("account_type_label", 		(String)source_accountEntity.get("name"));
		command.put("account_codeNo", 			(String)source_accountEntity.get("codeNo"));
		
		command.put("target_userId", 			member.getUserId());
		command.put("target_memberNo", 			member.getMemberNo());
		command.put("target_member_name", 		member.getName());
		command.put("target_account_type_id", 	targart_accountEntity.get("id").toString());
		command.put("target_account_type_label",(String)targart_accountEntity.get("name"));
		command.put("target_account_codeNo", 	(String)targart_accountEntity.get("codeNo"));
		
		
		command.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
		command.put("command_no", 				order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + command_index);
		command.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
		command.put("seq_no", 					command_index  + "");
		command.put("trade_type", 				Constant.TRADE_TYPE_WITHDRAW.toString());
		command.put("pay_interfaceNo", 			payInterFaceNo);
		Long order_money = (Long)order_entity.get("order_money");
		Long fee = (Long)order_entity.get("fee");
		if(fee != null && fee>0){
			order_money = order_money - fee;
		}
		command.put("trade_money", 				order_money.toString());
		command.put("bizid", 					(String)order_entity.get("orderNo"));
		command.put("orgNo", 					(String)order_entity.get("orgNo"));
		command.put("order_type", 				order_entity.get("order_type").toString());
		command.put("isMaster", 				"true");
		
		command.put("pay_channelNo", 			(String)payInterface_entity.get("pay_channelNo"));
		
		
		
		command.put("call_type", 				Constant.CALL_TYPE_INTERFACE.toString());
		command.put("bank_code",         		(String)order_entity.get("bank_code"));
		command.put("accountNo_encrypt",        (String)order_entity.get("accountNo_encrypt"));
		command.put("accountNo",         		(String)order_entity.get("accountNo"));
		command.put("account_name",         	(String)order_entity.get("account_name"));
		command.put("card_type",         		order_entity.get("card_type").toString());
		command.put("account_prop",         	order_entity.get("account_prop").toString());
		command.put("account_name",         	(String)order_entity.get("account_name"));
		command.put("account_name",         	(String)order_entity.get("account_name"));
		command.put("withdrawType",				withdrawType);
		command.put("withdrawReserveModel",		_withdrawReserveModel.toString());

		if(order_entity.get("summary") != null){
    		command.put("remark",         		order_entity.get("summary").toString());
    	}
		
        commandList.add(command);
		command_index++;
        return commandList;
	}
	
	/**
	 * @Title: generatePayCommandByAccount
	 * @Description: 生成内部账户的指令
	 * @param order_entity		订单
	 * @param accountList		内部帐户列表
	 * @param commandList		指令
	 * @param session			session
	 * @throws Exception
	 * @return List    内部指令列表
	 */
	private List<Map<String, String>> generatePayCommandByAccount(Map<String, Object> order_entity,
			List accountList, List<Map<String, String>> commandList, Session session) throws Exception {
		
		//int k = commandList.size() + 1;
		for(Object obj : accountList) {
			Map<String, Object> accountMap = (Map<String, Object>) obj;
			Map<String, String> command = new HashMap<>();
			
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
				command.put("remark",                "提现手续费");
			} else {
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
		
		
		for(Map<String, String> command2 : commandList) {
			DynamicEntityService.createEntity("AMS_OrderPayDetail", command2, null);
		}

		Query query = session.createQuery("update AMS_Order set is_exsit_command=true where orderNo=:orderNo");
		query.setParameter("orderNo", order_entity.get("orderNo"));
		query.executeUpdate();
		
		return commandList;
	}
	
	@Override
	public void completePay(Map<String, Object> order_entity, Session session) throws Exception {
		// TODO Auto-generated method stub

		completeOrder(order_entity, null, session);
	}
	@Override
	public String completeOrder(Map<String, Object> order_entity, Map<String, Object> param, Session session)
			throws Exception {
		String state = null;
		logger.info("订单状态：" + (Long)order_entity.get("order_state"));
		
//		if(Constant.ORDER_STATE_CLOSE.equals((Long)order_entity.get("order_state"))){
//			logger.info("提现失败。");
//			
//			state = "error";
//		}else{
//			logger.info("提现成功。");
//			super.completeOrder(order_entity, null, session);
//			state = "OK";
//		}
		
		Boolean isFail = (Boolean)order_entity.get("isFail");
		
		if(Boolean.TRUE.equals(isFail)){
			logger.info("提现失败。");
			state = "error";
		}else{
			logger.info("提现成功。");

			if( order_entity.get("fee")!=null &&((Long)order_entity.get("fee")>0L) ){

				List commandList = getCommands((String) order_entity.get("orderNo"), session);
////
				Map<String, Object> command = (Map<String, Object>)commandList.get(0);
////
//				String bizid          	= (String)command.get("bizid");
				String source_userId	= (String)command.get("source_userId");
//				Long account_type_id	= (Long)command.get("account_type_id");
////
//				Map<String, Object> unfreeze_param = new HashMap<>();
//				unfreeze_param.put("userId", 			source_userId);
//				unfreeze_param.put("bizid", 			bizid);
//				unfreeze_param.put("account_type_id", 	account_type_id);
//				unfreeze_param.put("unfreeze_money", 	order_entity.get("fee"));
//				unfreeze_param.put("remark", 			"代付成功,手续费解冻");
//				//解冻手续费金额
//				FreezeService.unfreezeMoney(unfreeze_param);

				operateFee(order_entity,source_userId,(Long)order_entity.get("fee"),(Long)order_entity.get("account_type_id"), "提现手续费", session);
			}
			super.completeOrder(order_entity, null, session);
			state = "OK";
		}

		//交易监测
		List<Map<String, Object>> commandList = getCommands((String)order_entity.get("orderNo"),session);
		for(Map<String, Object> command : commandList){
			TransMonitor.monitor("response" ,command, new HashMap<String, Object>());
		}

		sendProducerMessageWithKafka((String)order_entity.get("orderNo"), state);
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
}
