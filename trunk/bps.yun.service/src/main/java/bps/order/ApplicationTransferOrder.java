/**   
* @Title: DaiFuOrder.java 
* @Package bps.order 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2015年9月14日 下午5:56:19 
* @version V1.0   
*/
package bps.order;

import bps.monitor.TransMonitor;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.kinorsoft.ams.ITradeCommand.CommandResult;
import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.services.QueryService;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.member.Member;
import bps.service.AccountService;

/**
 * @author Administrator
 *	平台转账订单
 */
class ApplicationTransferOrder extends Order{
	
	private static Logger logger = Logger.getLogger(ApplicationTransferOrder.class.getName());
	
	@Override
	public Map<String,Object> applyOrder(final Map<String, Object> param) throws Exception {
		logger.info("ApplicationTransferOrder.applyOrder start");
		final Long member_id              = (Long) param.get("memberId");
        Long order_money       	  = (Long) param.get("money");
        String target_bizUserId = (String) param.get("target_bizUserId");
		
		try {
			Map<String,Object> applicationEntity = DynamicEntityService.getEntity((Long)param.get("applicationId"), "AMS_Organization");
			if(applicationEntity == null){
				throw new Exception("对应的应用不存在");
			}
			if(applicationEntity.get("member_id")==null ){
				throw new Exception("所在应用的开发者不存在");
			}
			if(!member_id.equals(applicationEntity.get("member_id")) ){
				throw new Exception("所传的会员ID和应用ID不符");
			}
			Map<String,Object> source_member_entity =  DynamicEntityService.getEntity((Long)applicationEntity.get("member_id"), "AMS_Member");
			final Member source_member = new Member((Long)applicationEntity.get("member_id"));
			if(source_member_entity==null){
				throw new Exception("所在应用的开发者不存在");
			}
			Member target_member = new Member();
			target_member.setApplication_id((Long)param.get("applicationId"));
			target_member.setBizUserId(target_bizUserId);
			if(target_member.getUserId() == null){
				throw new Exception("收款人不存在");
			}
			Map<String,Object> target_member_entity = target_member.getUserInfo();
            //创建订单
            final Map<String, String> entityMap = new HashMap<>();
            
            entityMap.put("member_id",              source_member_entity.get("id").toString());
            entityMap.put("member_name",            source_member_entity.get("name") == null ? "" : source_member_entity.get("name").toString());
            entityMap.put("member_uuid",            source_member_entity.get("userId").toString());
            entityMap.put("memberNo",               (String)source_member_entity.get("memberNo"));

            entityMap.put("target_member_id",       target_member_entity.get("id").toString());
            entityMap.put("target_member_name",     target_member_entity.get("name") == null ? "" : target_member_entity.get("name").toString());
            entityMap.put("target_uuid",            target_member_entity.get("userId").toString());
            entityMap.put("target_memberNo",        (String)target_member_entity.get("memberNo"));
            entityMap.put("order_state",            Constant.ORDER_STATE_WAIT_PAY.toString());

			entityMap.put("order_money", 			order_money.toString());
			Calendar calendar = Calendar.getInstance();
			entityMap.put("create_time", 			String.valueOf(calendar.getTime().getTime()));

			entityMap.put("sub_trade_type", 		Constant.SUB_TRADE_TYPE_APPLICATION_TRANSFER.toString());
			entityMap.put("trade_type", 			Constant.TRADE_TYPE_TRANSFER.toString());
			entityMap.put("order_state", 			Constant.ORDER_STATE_WAIT_PAY.toString());
			entityMap.put("order_type", 			Constant.ORDER_TYPE_APPLICATION_TRANSFER.toString());
			entityMap.put("orgNo", 					param.get("orgNo").toString());
            entityMap.put("order_name", 			(String)param.get("orderName"));
            entityMap.put("member_ip", 				(String)param.get("memberIp"));
            entityMap.put("extend_info", 			(String)param.get("ext"));
            entityMap.put("description", 			(String)param.get("description"));
            entityMap.put("transaction_type", 		(String)param.get("transactionType"));
            entityMap.put("remark", 				(String)param.get("remark"));
            entityMap.put("application_id",         param.get("applicationId").toString());
            entityMap.put("application_label",      param.get("applicationName").toString());
            entityMap.put("bizOrderNo",             param.get("bizOrderNo").toString());
        	
        	if(param.get("accountNoSet")!=null){
        		Map<String, Object> sourceAccountEntity = QueryService.getAccountType((String)param.get("accountNoSet"));
        		if(sourceAccountEntity==null){
        			throw new Exception("所指定的账户集不存在");
        		}
        		entityMap.put("account_type_id", sourceAccountEntity.get("id").toString());
        		entityMap.put("account_type_label", sourceAccountEntity.get("codeNo").toString());
        	}
        	if(param.get("fee")!=null){
        		entityMap.put("fee", String.valueOf(param.get("fee")));
        	}
	        if(param.get("extend_info") != null){
	        	entityMap.put("extend_info", String.valueOf(param.get("extend_info")));
	        }
	        if(param.get("summary") != null){
	        	entityMap.put("summary", String.valueOf(param.get("summary")));
	        }

            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
	        Map<String,Object> ret = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
	        	@Override
				public boolean before(Session session) throws Exception {
					Map<String,Object> orderEntity = getOrder(Long.parseLong(entityMap.get("application_id")),entityMap.get("bizOrderNo"),session);
					if(orderEntity != null){
						throw new Exception("平台转账订单已经存在");
					}
					
					//检查指令
//					checkDaifuOrder(entityMap,session);
					return true;
				}
	        	
	        	
				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
						
					Map<String, Object> orderEntity = DynamicEntityService.createEntity("AMS_Order", entityMap, null);
	                String orderNo = Order.generateOrderNo((Long)orderEntity.get("id"));
	                Query query = session.createQuery("update AMS_Order set orderNo=:orderNo where id=:orderId");
	                query.setParameter("orderNo", orderNo);

	                query.setParameter("orderId", orderEntity.get("id"));

	                query.executeUpdate();
	                orderEntity.put("orderNo", orderNo);
					//生成指令
	                orderEntity.put("targetAccountSetNo", param.get("targetAccountSetNo"));
					generatePayCommands(source_member,orderEntity,session);
					//完成指令
					Map<String, Object> completeParams = applicationTransferCompleteOrder(orderEntity,null,session);

					Map<String,Object> retMap = new HashMap<>();
					retMap.put("orderNo", 	orderNo);
					retMap.put("state", 	completeParams.get("state"));
//					retMap.put("failPayMessage", completeParams.get("failPayMessage") == null ? null : (String)completeParams.get("failPayMessage"));
					return retMap;
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
	
	private Map<String,Object> generatePayCommands(Member member,Map<String, Object> order_entity, Session session)throws Exception{
		Query query = session.createQuery("delete AMS_OrderPayDetail where bizid=:orderNo");
		query.setParameter("orderNo", order_entity.get("orderNo"));
		query.executeUpdate();
		List<Map<String, String>> commandList = generatePayCommandByAccount(order_entity, session);
		
//		Map<String, Object> ret = new HashMap<String, Object>();
//		ret.put("orderNo", order_entity.get("orderNo"));
		return null;
	}
	
	/**
	 *
	 * @Title: generatePayCommandByAccount
	 * @Description: TODO(生成内部账户的指令)
	 * @param order_entity	订单
	 * @param session		session
	 * @throws Exception    设定文件
	 * @return List<Map<String,String>>    返回类型
	 */
	private List<Map<String, String>> generatePayCommandByAccount(Map<String, Object> order_entity, Session session) throws Exception {
		String targetAccountNo = (String)order_entity.get("targetAccountSetNo");
		Map<String, Object> targetAccountType = QueryService.getAccountType(targetAccountNo);
		List<Map<String,String>> commandList = new ArrayList<>();
		int k = 1;
		Long money = (Long)order_entity.get("order_money");
		Member target_member = new Member((Long)order_entity.get("target_member_id"));
		
		Map<String,Object> targetMemberMap = target_member.getUserInfo();
		Map<String,Object> sourceAccountType = null;
		if(order_entity.get("account_type_id") != null ){
			sourceAccountType = DynamicEntityService.getEntity((Long)order_entity.get("account_type_id"),"AMS_AccountType");
		}else{
			AccountService accountService = new AccountServiceImpl();
			sourceAccountType = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
		}
		
		Map<String, String> command = new HashMap<>();
		command.put("target_userId", 		(String)targetMemberMap.get("userId"));
		command.put("target_memberNo", 		(String)targetMemberMap.get("memberNo"));
		command.put("target_member_name", 	(String)targetMemberMap.get("name"));
		command.put("trade_type", 			order_entity.get("trade_type").toString());
		command.put("sub_trade_type", 		order_entity.get("sub_trade_type").toString());
		command.put("account_type_id", 			sourceAccountType.get("id").toString());
		command.put("account_type_label", 		(String)sourceAccountType.get("name"));
		command.put("account_codeNo", 			(String)sourceAccountType.get("codeNo"));
		command.put("target_account_type_id", 			targetAccountType.get("id").toString());
		command.put("target_account_type_label", 		(String)targetAccountType.get("name"));
		command.put("target_account_codeNo", 			(String)targetAccountType.get("codeNo"));	
		command.put("source_userId", 			(String)order_entity.get("member_uuid"));
		command.put("source_memberNo", 			(String)order_entity.get("memberNo"));
		command.put("source_member_name", 		(String)order_entity.get("member_name"));
		
		command.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
		command.put("command_no", 				(String)order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + k);
		command.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
		command.put("seq_no", 					k + "");
		command.put("pay_interfaceNo", 			Constant.PAY_INTERFACE_AMS);
		command.put("pay_channelNo", 			Constant.PAY_CHANNEL_AMS);

		command.put("trade_money", 				money.toString());
		command.put("bizid", 					(String)order_entity.get("orderNo"));
		command.put("orgNo", 					(String)order_entity.get("orgNo"));
		command.put("order_type", 				order_entity.get("order_type").toString());
		command.put("call_type", 				Constant.CALL_TYPE_INTERFACE.toString());
		command.put("isMaster", 				"true");
		command.put("remark",                   (String)order_entity.get("remark"));

        commandList.add(command);
        //直接转账到银行卡
        k++;
        
		for(Map<String, String> command2 : commandList) {
			DynamicEntityService.createEntity("AMS_OrderPayDetail", command2, null);
		}
		
		Query query = session.createQuery("update AMS_Order set is_exsit_command=true where orderNo=:orderNo");
		query.setParameter("orderNo", order_entity.get("orderNo"));
		query.executeUpdate();
		
		return commandList;
	}
	
	Map<String, Object> applicationTransferCompleteOrder(Map<String, Object> order_entity, Map<String, Object> param, Session session)
			throws Exception {
		String orderNo = (String)order_entity.get("orderNo");
		TradeCommandManager manager = TradeCommandManager.instance();
		CommandResult command_state = manager.doCommands(orderNo, null);
		
		//执行状态  
		String ret = "";// 0 失败  1 成功 2 进行中
		if( command_state.equals(CommandResult.Success)){
			//代付成功：不进行操作 等回调的时候操作
			logger.info("---CommandResult:Success---");
			ret = "1";
		}else if(command_state.equals(CommandResult.PendingContinue)){
			//不会出现
			logger.info("---CommandResult:PendingContinue---");
		}else if( command_state.equals(CommandResult.FailStop) ){
			logger.info("---CommandResult:FailStop---");
			
			ret = "0";
		}else if(command_state.equals(CommandResult.PendingStop)){
			//进行中
			logger.info("---CommandResult:PendingStop---");
			//修改订单状态  为进行中
			Order.updateOrderPending(orderNo, session);
			ret = "2";
		}else{
			logger.info("---CommandResult:null---");
		}
		
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("state", ret);
//		retMap.put("payFailMessage", payFailMessage);
		return retMap;
		
//		// TODO Auto-generated method stub
//		//完成支付
//		CommandResult command_state = null;
//		String payFailMessage = null;
//		String orderNo = (String)order_entity.get("orderNo");
//		Map<String, Object> retMap = new HashMap<>();
//		try {
//			//String withdrawType = order_entity.get("withdrawType") == null?"":order_entity.get("withdrawType").toString();
//			String withdrawType = "";
//			if( !withdrawType.equals("")){
//				String orgNo = (String)order_entity.get("orgNo");
//				String orgList = BusinessUtil.getCacheByKey("orgList");
//				JSONArray ja = new JSONArray(orgList);
//				Long withdrawReserveModel = 0L;
//				for(int i=0;i<ja.length();i++) {
//					JSONObject obj = ja.getJSONObject(i);
//					String codeNo = obj.optString("codeNo");
//					if (orgNo.equals(codeNo)){
//						withdrawReserveModel = obj.optLong("withdrawReserveModel");
//						break;
//					}
//				}
//				List<Map<String, Object>> commandList = getCommands(orderNo, session);
//				if(commandList.isEmpty()) {
//					throw new Exception("支付失败");
//				}
//				if( "T1".equals(withdrawType) ) {
//					for (Map<String, Object> command : commandList) {
//						logger.info("command:" + command);
//						//Map<String, Object> command = (Map<String, Object>)commandList.get(0);
//						withdrawType = command.get("withdrawType") == null ? "" : command.get("withdrawType").toString();
//						if ("T1".equals(withdrawType)) {//T+1批量代付-冻结金额、放到队列。
//							WithdrawOrderToBatchDaiFu bdfr = new WithdrawOrderToBatchDaiFu();
//							bdfr.confirmPay(command, Constant.WITHDRAW_RESERVE_MODEL_ACTIVE);
//
//						} else {
//							logger.info("command.get(\"id\"):" + command.get("id") + "---" + orderNo);
//							TradeCommandManager manager = TradeCommandManager.instance();
//							command_state = manager.doCommands(orderNo, (Long) command.get("id"));
//						}
//					}
//				}else if ("T0".equals(withdrawType) && withdrawReserveModel.equals(Constant.WITHDRAW_RESERVE_MODEL_ENTRUST)){
//					for (Map<String, Object> command : commandList) {
//						logger.info("command:"+command);
//						//Map<String, Object> command = (Map<String, Object>)commandList.get(0);
//						withdrawType = command.get("withdrawType") == null?"":command.get("withdrawType").toString();
//						if ("T0".equals(withdrawType) && withdrawReserveModel.equals(Constant.WITHDRAW_RESERVE_MODEL_ENTRUST)) {//T+0批量代付
//							WithdrawOrderToBatchDaiFu bdfr = new WithdrawOrderToBatchDaiFu();
//							bdfr.confirmPay(command, withdrawReserveModel);
//						}else {
//							logger.info("command.get(\"id\"):"+command.get("id")+"---"+orderNo);
//							TradeCommandManager manager = TradeCommandManager.instance();
//							command_state = manager.doCommands(orderNo, (Long)command.get("id"));
//						}
//					}
//				}else{
//					TradeCommandManager manager = TradeCommandManager.instance();
//					command_state = manager.doCommands(orderNo, null);
//				}
//
//			}else{
//				TradeCommandManager manager = TradeCommandManager.instance();
//				command_state = manager.doCommands(orderNo, null);
//			}
//
//
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//			
//			Long bankMoney = (Long)order_entity.get("bank_money");
//			//代付到余额
//			if(null == bankMoney)
//				throw e;
//			//代付到银行卡
//			else{
//				command_state = CommandResult.FailStop;
//	            payFailMessage =  e.getMessage();
//			}
//		}
//		//
//		logger.info(command_state);
//		//执行状态  
//		String ret = "";// 0 失败  1 成功 2 进行中
//		if( command_state.equals(CommandResult.Success)){
//			//代付成功：不进行操作 等回调的时候操作
//			logger.info("---CommandResult:Success---");
//			ret = "1";
//		}else if(command_state.equals(CommandResult.PendingContinue)){
//			//不会出现
//			logger.info("---CommandResult:PendingContinue---");
//		}else if( command_state.equals(CommandResult.FailStop) ){
//			logger.info("---CommandResult:FailStop---");
//			
//			ret = "0";
//		}else if(command_state.equals(CommandResult.PendingStop)){
//			//进行中
//			logger.info("---CommandResult:PendingStop---");
//			//修改订单状态  为进行中
//			Order.updateOrderPending(orderNo, session);
//			ret = "2";
//		}else{
//			logger.info("---CommandResult:null---");
//		}
//		
//		retMap.put("state", ret);
//		retMap.put("payFailMessage", payFailMessage);
//		return retMap;
////		completeReciever(order_entity,session);
////		//修改订单完成
////		super.completeOrder(order_entity, null, session);
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

	/**
	 * 订单完成
	 * @param order_entity  订单实体
	 * @param param         参数
	 * @param session       session
	 * @throws Exception
	 */
	public String completeOrder(Map<String, Object> order_entity, Map<String, Object> param, Session session) throws Exception{
		Query query = session.createQuery("update AMS_Order set order_state=:order_state, confirm_time=:confirm_time where id=:order_id");
		query.setParameter("order_state", Constant.ORDER_STATE_SUCCESS);
		query.setParameter("confirm_time", new Date());
		query.setParameter("order_id", (Long)order_entity.get("id"));
		query.executeUpdate();
		return null;
	}
}
