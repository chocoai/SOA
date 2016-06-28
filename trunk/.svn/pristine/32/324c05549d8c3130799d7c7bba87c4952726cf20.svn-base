/**   
* @Title: BatchDaiFuOrder.java 
* @Package bps.order 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年10月20日 上午9:19:55 
* @version V1.0   
*/
package bps.order;

import bps.monitor.TransMonitor;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.ITradeCommand.CommandResult;
import com.kinorsoft.ams.services.QueryService;
import com.kinorsoft.ams.services.TradeService;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.member.Member;
import bps.member.MemberServiceImpl;
import bps.process.PayChannelManage;
import bps.service.AccountService;
import bps.service.MemberService;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年10月20日 上午9:19:55  
 */
public class BatchDaiFuOrder extends Order{
	private static Logger logger = Logger.getLogger(BatchDaiFuOrder.class.getName());

	/**
	 * ori_order_List  原订单列表  orderNo ,money
	 * agent_pay_list  支付列表     userId,money,accountNo
	 */
	@Override
	public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
		logger.info("BatchDaiFuOrder.applyOrder start");
		final Long member_id              = (Long) param.get("memberId");
		String bizBatchNo = (String)param.get("bizBatchNo");
		if(bizBatchNo == null){
			throw new Exception("批次号必传");
		}
//		final Long goodsType = (Long)param.get("goodsType");
//		final String goodsNo = (String)param.get("goodsNo");
//        final String tradeCode = (String)param.get("tradeCode");
//        final String industryCode = (String)param.get("industryCode");
//        final String industryName = (String)param.get("industryName");
        String batchPayList = (String)param.get("batchPayList");
		if(batchPayList == null){
        	throw new BizException(ErrorCode.PARAM_ERROR,"批量代付列表不能为空");
        }
		JSONArray batchPayArr = null;
		try{
			batchPayArr = new JSONArray(batchPayList);
		}catch(Exception e){
			throw new Exception("批量代付列表格式错误");
		}
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
//			final List<Map<String,String>> orderList = new ArrayList<Map<String,String>>();
			Map<String,Object> returnMap = new HashMap<>();
			for(int i=0;i<batchPayArr.length();i++){
				JSONObject agentPay = batchPayArr.getJSONObject(i);
				Long order_money = agentPay.getLong("amount");
				String extendInfo = (String)agentPay.get("extendInfo");
				String summary = (String)agentPay.get("summary");
				String bizOrderNo = agentPay.getString("bizOrderNo");
				JSONArray collectPayList = agentPay.getJSONArray("collectPayList");
				String target_bizUserId = agentPay.getString("bizUserId");
				String accountSetNo = (String)agentPay.get("accountSetNo");
				Member target_member = new Member();
				target_member.setApplication_id((Long)param.get("applicationId"));
				target_member.setBizUserId(target_bizUserId);
				if(target_member.getUserId() == null){
					throw new Exception("收款人不存在");
				}
				Map<String,Object> target_member_entity = target_member.getUserInfo();
				final Map<String,String> orderMap = new HashMap<>();
				orderMap.put("member_id",              source_member_entity.get("id").toString());
				orderMap.put("member_name",            source_member_entity.get("name") == null ? "" : source_member_entity.get("name").toString());
				orderMap.put("member_uuid",            source_member_entity.get("userId").toString());
				orderMap.put("memberNo",               (String)source_member_entity.get("memberNo"));
				
				orderMap.put("target_member_id",       target_member_entity.get("id").toString());
				orderMap.put("target_member_name",     target_member_entity.get("name") == null ? "" : target_member_entity.get("name").toString());
				orderMap.put("target_uuid",            target_member_entity.get("userId").toString());
				orderMap.put("target_memberNo",        (String)target_member_entity.get("memberNo"));
	            
				orderMap.put("order_state",            Constant.ORDER_STATE_WAIT_PAY.toString());
	
				orderMap.put("order_money", 			order_money.toString());
				Calendar calendar = Calendar.getInstance();
				orderMap.put("create_time", 			String.valueOf(calendar.getTime().getTime()));
	
				orderMap.put("sub_trade_type", 		Constant.SUB_TRADE_TYPE_BATCH_DAIFU.toString());
				orderMap.put("trade_type", 			Constant.TRADE_TYPE_TRANSFER.toString());
				orderMap.put("order_state", 			Constant.ORDER_STATE_WAIT_PAY.toString());
				orderMap.put("order_type", 			Constant.ORDER_TYPE_BATCH_DAIFU.toString());
				orderMap.put("orgNo", 				    param.get("orgNo").toString());
				orderMap.put("member_ip",             (String)param.get("memberIp"));
				orderMap.put("extend_info", 							extendInfo);
				orderMap.put("application_id",         param.get("applicationId").toString());
				orderMap.put("application_label",      param.get("applicationName").toString());
				orderMap.put("goodsType",             param.get("goodsType").toString());
				orderMap.put("goodsNo",               (String)param.get("goodsNo"));
				orderMap.put("goodsName",               (String)param.get("goodsName"));
				orderMap.put("biz_trade_code",             (String)param.get("biz_trade_code"));
				orderMap.put("bizOrderNo",             bizOrderNo);
				orderMap.put("collectPayListClob",    collectPayList.toString());
				orderMap.put("bizBatchNo",    bizBatchNo);
				if(accountSetNo!=null && !"".equals(accountSetNo)){
	        		Map<String, Object> accountEntity = QueryService.getAccountType(accountSetNo);
	        		if(accountEntity==null){
	        			throw new Exception("所指定的账户集不存在");
	        		}
	        		if(!accountEntity.get("application_id").equals(param.get("applicationId"))){
	        			throw new Exception("所指定的账户集不存在");
	        		}
	        		orderMap.put("account_type_id", accountEntity.get("id").toString());
	        		orderMap.put("account_type_label", accountEntity.get("codeNo").toString());
	        	}
				if(agentPay.get("fee")!=null){
					orderMap.put("fee", String.valueOf(agentPay.get("fee")));
	        	}
	        	if(agentPay.get("splitRuleList")!=null){
	        		orderMap.put("splitRule", String.valueOf(agentPay.get("splitRuleList")));
	        	}
		        if(extendInfo != null){
		        	orderMap.put("extend_info", extendInfo);
		        }
		        if(summary != null){
		        	orderMap.put("summary", summary);
		        }
		        if(agentPay.get("backUrl") != null){
		        	orderMap.put("backUrl",                String.valueOf(agentPay.get("backUrl")));
		        }
				String withdrawType = "";
		        if(agentPay.get("bankCardId") != null){
		        	Map<String, Object> bank_card = DynamicEntityService.getEntity(agentPay.getLong("bankCardId"), "AMS_MemberBank");
					if(bank_card == null){
						throw new Exception("会员银行卡不存在");
					}
					Long bank_money = agentPay.getLong("bank_money");
					if(bank_money == null){
						throw new Exception("转账到银行卡的金额不能为空");
					}
					orderMap.put("bank_code", 				(String)bank_card.get("bank_code"));
					orderMap.put("account_name", 			(String)bank_card.get("account_name"));
					orderMap.put("accountNo", 				(String)bank_card.get("accountNo"));
					orderMap.put("accountNo_encrypt", 		(String)bank_card.get("accountNo_encrypt"));
					orderMap.put("bank_name", 				(String)bank_card.get("bank_name"));
					orderMap.put("card_type", 				bank_card.get("card_type").toString());
					orderMap.put("account_prop", 			bank_card.get("account_prop").toString());
					orderMap.put("bank_money", 			    bank_money.toString());
					withdrawType = agentPay.get("withdrawType") == null?"":(String)agentPay.get("withdrawType");
		        }
		        //检验业务码
		        checkTradeCode(orderMap);
//		        orderList.add(orderMap);
				final String _withdrawType = withdrawType;
		        try{
			        LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			        Map<String,Object> ret = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
			        	@Override
						public boolean before(Session session) throws Exception {
		        			Map<String,Object> orderEntity = getOrder(Long.parseLong(orderMap.get("application_id")),orderMap.get("bizOrderNo"),session);
							if(orderEntity != null){
								throw new Exception("代付订单已经存在");
							}
							//检查指令
							checkDaifuOrder(orderMap,session);
							return true;
						}

						@Override
						public Map<String,Object> doTransaction(Session session, Transaction tx)
								throws Exception {
							Map<String, Object> orderEntity = DynamicEntityService.createEntity("AMS_Order", orderMap, null);
			                String orderNo = Order.generateOrderNo((Long)orderEntity.get("id"));
			                Query query = session.createQuery("update AMS_Order set orderNo=:orderNo where id=:orderId");
			                query.setParameter("orderNo", orderNo);
			                query.setParameter("orderId", (Long)orderEntity.get("id"));
			                query.executeUpdate();
			                orderEntity.put("orderNo", orderNo);


							if( !_withdrawType.equals("") ){
								orderEntity.put("withdrawType",_withdrawType);
							}
							//创建指令
							generatePayCommands(source_member,orderEntity,session);
							//完成指令
							Map<String, Object> completeParams = daiFuCompleteOrder(orderEntity,null,session);
							Map<String,Object> ret = new HashMap<>();
							ret.put("orderNo", orderNo);
							ret.put("state", completeParams.get("state"));
							ret.put("failPayMessage", completeParams.get("failPayMessage") == null ? null : (String)completeParams.get("failPayMessage"));
							return ret;
						}
			        });
			        returnMap.put(orderMap.get("bizOrderNo"), ret);
		        }catch(Exception e){
		        	logger.error(e.getMessage(), e);
		        	Map<String,Object> ret = new HashMap<>();
		        	ret.put("state", "0");
		        	ret.put("failPayMessage", e.getMessage());
		        	returnMap.put(orderMap.get("bizOrderNo"), ret);
		        	
//		        	if(agentPay.get("bankCardId") == null){
//		        		throw new BizException(ErrorCode.ORDER_MONEY_PAY_MONEY_NOT_EQUAL, e.getMessage());
//		        	}
		        }
			}
			
			logger.info("批量代付返回：" + returnMap);
			return returnMap;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	
	//检测代收订单的金额情况
		private void checkDaifuOrder(Map<String,String> orderMap,Session session)throws Exception{
			Long order_money = Long.parseLong(orderMap.get("order_money"));
			String collectPayList = (String)orderMap.get("collectPayListClob");
			String target_userId = orderMap.get("target_uuid");
			JSONArray jsonArray = null;
			try{
				jsonArray = new JSONArray(collectPayList);
			}catch(Exception e){
				throw new Exception("代收订单列表格式错误");
			}
			Long allPayMoney = 0L;
			for(int i=0;i<jsonArray.length();i++){
				JSONObject o = jsonArray.getJSONObject(i);
				String bizOrderNo = o.getString("bizOrderNo");
				Long collect_amount = o.getLong("amount");
				
				 Map<String,Object> agentOrderMap = Order.getOrder(Long.parseLong(orderMap.get("application_id")), bizOrderNo, session);
				 if(agentOrderMap == null){
					throw new Exception("代收订单"+bizOrderNo+"不存在");
				 }
				 if(!Constant.ORDER_STATE_SUCCESS.equals((Long)agentOrderMap.get("order_state"))){
					throw new Exception("代收订单"+bizOrderNo+"交易未完成");
				 }
				 
				 long sx_amount = 0L;
				 Map<String, Object> agentDetailMap = null;
				 
				//检测代收订单中的收款人列表是否已经写入数据库
				 int j = 0;
				 boolean flag = false;
				 for(j = 0; j < 20; j++){
					 List<Map<String,Object>> agentDetailList = Order.getAgentDetailList((Long)agentOrderMap.get("id"),session);
					 for(Map<String,Object> agentDetail : agentDetailList){
						 if(agentDetail.get("userId").equals(target_userId)){
							 agentDetailMap = agentDetail;
							 
							 flag = true;
							 //说明代收订单中的收款人已经写入到数据库中
							 break;
						 }
					 }
					 
					 if(flag){
						 break;
					 }
					
					 //等待代收订单中的收款人写入数据库
					 Thread.sleep(5000);
				 }
				 
				 if(j == 20){
//					 throw new Exception("代收订单" + bizOrderNo + "中的" + target_userId + "收款人还未写入数据库。");
					 throw new Exception("收款人不存在。");
				 }
				 
				 Long sx = (Long)agentDetailMap.get("sk_amount") - (Long)agentDetailMap.get("ys_amount") -(Long)agentDetailMap.get("tk_amount");
				 sx_amount += sx;
				 logger.info("sx_amount:"+sx_amount+"collect_amount:"+collect_amount);
				 
				 if(sx_amount<collect_amount.longValue()){
					 throw new Exception("代收订单"+bizOrderNo+"中金额不足");
				 }
				
				allPayMoney+=collect_amount;
			}
			if(allPayMoney.longValue() != order_money){
				throw new Exception("收款人金额列表和订单金额列表不符");
			}
			//检测收款人的金额不能小于分账出去和手续费的金额
			Long fee = 0L;
			if(orderMap.get("fee") != null && !"".equals(orderMap.get("fee"))){
				fee = Long.parseLong(orderMap.get("fee"));
			}
			JSONArray splitRule = null;
			if(orderMap.get("splitRule") != null && !"".equals(orderMap.get("splitRule"))){
				try{
					splitRule = new JSONArray(orderMap.get("splitRule"));
				}catch(Exception e){
					throw new Exception("分账格式错误");
				}
			}
			checkFeeAndSplitRule(order_money,fee,splitRule);
			
			if(orderMap.get("bank_money")!=null&&!"".equals(orderMap.get("bank_money"))){
				Long bank_money = Long.parseLong(orderMap.get("bank_money"));
				Long to_bank_meony = order_money - fee;
				if(splitRule != null){
					 for(int i=0;i<splitRule.length();i++){
						 JSONObject split = splitRule.getJSONObject(i);
						 Long amount = split.getLong("amount");
						 to_bank_meony = to_bank_meony - amount;
					 }
				 }
				if(!bank_money.equals(to_bank_meony)){
					throw new  Exception("转账到银行卡的金额与到帐金额不符");
				}
			}
		}
		 private void checkFeeAndSplitRule(Long outAmount,Long fee,JSONArray splitRuleList)throws Exception{
			 Long inAmount = 0L;
			 if(fee != null){
				 inAmount += fee;
			 }
			 if(splitRuleList != null){
				 for(int i=0;i<splitRuleList.length();i++){
					 JSONObject splitRule = splitRuleList.getJSONObject(i);
					 Long amount = splitRule.getLong("amount");
					 inAmount+=amount;
					 Long in_fee = 0L;
					 if(splitRule.get("fee")!=null){
						 in_fee = splitRule.getLong("fee");
					 }
					 checkFeeAndSplitRule(amount,in_fee,(JSONArray)splitRule.get("splitRuleList"));
				 }
			 }
			 
			 if(inAmount.longValue()>outAmount){
				 throw new Exception("分账金额和手续费金额不能大于收款金额");
			 }
		 }
		
		private Map<String,Object> generatePayCommands(Member member,Map<String, Object> order_entity, Session session)throws Exception{
			Query query = session.createQuery("delete AMS_OrderPayDetail where bizid=:orderNo");
			query.setParameter("orderNo", order_entity.get("orderNo"));
			query.executeUpdate();
			List<Map<String, String>> commandList = generatePayCommandByAccount(order_entity, session);
			
//			Map<String, Object> ret = new HashMap<String, Object>();
//			ret.put("orderNo", order_entity.get("orderNo"));
			return null;
			
			
			
		}
		
		/***
		 * 
		 * @Title: generatePayCommandByAccount
		 * @Description: TODO(生成内部账户的指令)
		 * @param order_entity
		 * @param session
		 * @throws Exception    设定文件
		 * @return List<Map<String,String>>    返回类型
		 */
		private List<Map<String, String>> generatePayCommandByAccount(Map<String, Object> order_entity, Session session) throws Exception {
			String source_accountNo = getAccountNoByTradeNo((String)order_entity.get("biz_trade_code"));
			Map<String, Object> source_accountEntity = QueryService.getAccountType(source_accountNo);
			List<Map<String,String>> commandList = new ArrayList<Map<String,String>>();
			int k = 1;
			Long money = (Long)order_entity.get("order_money");
			Member target_member = new Member((Long)order_entity.get("target_member_id"));
			
			Map<String,Object> targetMemberMap = target_member.getUserInfo();
			Map<String,Object> targetAccountType = null;
			if(order_entity.get("account_type_id") != null ){
				targetAccountType = DynamicEntityService.getEntity((Long)order_entity.get("account_type_id"),"AMS_AccountType");
			}else{
				AccountService accountService = new AccountServiceImpl();
				targetAccountType = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
			}
			
			Map<String, String> command = new HashMap<>();
			command.put("target_userId", 		(String)targetMemberMap.get("userId"));
			command.put("target_memberNo", 		(String)targetMemberMap.get("memberNo"));
			command.put("target_member_name", 	(String)targetMemberMap.get("name"));
			command.put("trade_type", 			order_entity.get("trade_type").toString());
			command.put("sub_trade_type", 		order_entity.get("sub_trade_type").toString());
			command.put("account_type_id", 			source_accountEntity.get("id").toString());
			command.put("account_type_label", 		(String)source_accountEntity.get("name"));
			command.put("account_codeNo", 			(String)source_accountEntity.get("codeNo"));
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
			
	        commandList.add(command);
	        
	        //直接转账到银行卡
	        k++;
	        if(order_entity.get("bank_money")!=null&&(Long)order_entity.get("bank_money")>0){
	        	
	        	Map<String, String> command_tltdf = new HashMap<String, String>();
//	    		JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfo(Constant.PAY_INTERFACE_TLT_DF);
//	    		Member member = new Member(payInterface_entity.get("system_uuid").toString());
	        	
	        	JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfo(Constant.PAY_INTERFACE_TLT_DF, (String)order_entity.get("orgNo"));
	    		Member member = new Member(payInterface_entity.get("system_uuid").toString());
	        	
	    		Map<String, Object> cash_accountEntity = QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
	    		
	    		command_tltdf.put("target_userId", 			(String)member.getUserId());
	    		command_tltdf.put("target_memberNo", 			(String)member.getMemberNo());
	    		command_tltdf.put("target_member_name", 		(String)member.getName());
	    		
	    		command_tltdf.put("source_userId", 			(String)targetMemberMap.get("userId"));
	    		command_tltdf.put("source_memberNo", 			(String)targetMemberMap.get("memberNo"));
	    		command_tltdf.put("source_member_name", 		(String)targetMemberMap.get("name"));
	    		
	    		command_tltdf.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
	    		command_tltdf.put("command_no", 				(String)order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + k);
	    		command_tltdf.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
	    		command_tltdf.put("seq_no", 					k + "");
	    		command_tltdf.put("trade_type", 				Constant.TRADE_TYPE_TRANSFER.toString());
	    		command_tltdf.put("sub_trade_type", 			Constant.SUB_TRADE_TYPE_2BANK.toString());
	    		command_tltdf.put("pay_interfaceNo", 			Constant.PAY_INTERFACE_TLT_DF);

	    		command_tltdf.put("trade_money", 				order_entity.get("bank_money").toString());
	    		command_tltdf.put("bizid", 					(String)order_entity.get("orderNo"));
	    		command_tltdf.put("orgNo", 					(String)order_entity.get("orgNo"));
	    		command_tltdf.put("order_type", 				order_entity.get("order_type").toString());
	    		command_tltdf.put("isMaster", 				"true");
	    		
	    		command_tltdf.put("pay_channelNo", 			(String)payInterface_entity.get("pay_channelNo"));
	    		
	    		command_tltdf.put("account_type_id", 			targetAccountType.get("id").toString());
	    		command_tltdf.put("account_type_label", 		(String)targetAccountType.get("name"));
	    		command_tltdf.put("account_codeNo", 			(String)targetAccountType.get("codeNo"));
	    		
	    		command_tltdf.put("target_account_type_id", 			cash_accountEntity.get("id").toString());
	    		command_tltdf.put("target_account_type_label", 		(String)cash_accountEntity.get("name"));
	    		command_tltdf.put("target_account_codeNo", 			(String)cash_accountEntity.get("codeNo"));
	    		
	    		command_tltdf.put("call_type", 				Constant.CALL_TYPE_INTERFACE.toString());
	    		command_tltdf.put("bank_code",         		(String)order_entity.get("bank_code"));
	    		command_tltdf.put("accountNo_encrypt",        (String)order_entity.get("accountNo_encrypt"));
	    		command_tltdf.put("accountNo",         		(String)order_entity.get("accountNo"));
	    		command_tltdf.put("account_name",         	(String)order_entity.get("account_name"));
	    		command_tltdf.put("card_type",         		order_entity.get("card_type").toString());
	    		command_tltdf.put("account_prop",         	order_entity.get("account_prop").toString());
	    		
	            commandList.add(command_tltdf);
	        }
	        
			for(Map<String, String> command2 : commandList) {
				DynamicEntityService.createEntity("AMS_OrderPayDetail", command2, null);
			}
			Query query = session.createQuery("update AMS_Order set is_exsit_command=true where orderNo=:orderNo");
			query.setParameter("orderNo", order_entity.get("orderNo"));
			query.executeUpdate();
			
			return commandList;
		}
		
		//回调事件
		public void completePay(Map<String, Object> order_entity, Session session) throws Exception {
			// TODO Auto-generated method stub
			//一次性操作完成，不进行实际操作
			String state = "";
			Boolean isFail = (Boolean)order_entity.get("isFail");
			if(Boolean.TRUE.equals(isFail)){
				logger.info("代付失败");
				//去减去原来在收款人列表中增加的金额
				reduceAgentDetail(order_entity,session);
				//然后发送失败消息通知

				//交易监测
				List<Map<String, Object>> commandList = getCommands((String)order_entity.get("orderNo"),session);
				for(Map<String, Object> command : commandList){
					TransMonitor.monitor("response" ,command, new HashMap<String, Object>());
				}
				state = "error";
			}else{
				logger.info("代付成功");
				//进行分账和手续费操作
				completeReciever(order_entity,session);
				super.completeOrder(order_entity, null, session);
				//然后去发送成功消息通知
				state = "OK";
			}
			
//			if(order_entity.get("bank_money")!=null&&(Long)order_entity.get("bank_money")>0){
//				sendProducerMessageWithKafka((String)order_entity.get("orderNo"),state);
//			}
			
			sendProducerMessageWithKafka((String)order_entity.get("orderNo"),state);
		}
		
//		@Override
//		public String completeOrder(Map<String, Object> order_entity, Map<String, Object> param, Session session)
//				throws Exception {
//			// TODO Auto-generated method stub
//			//完成支付
//			addAgentDetail(order_entity,session);
//			CommandResult command_state = null;
//			String orderNo = (String)order_entity.get("orderNo");
//			try {
//				orderNo = (String)order_entity.get("orderNo");
//	            TradeCommandManager manager = TradeCommandManager.instance();
//	            command_state = manager.doCommands(orderNo,null);
//			} catch(Exception e) {
//				logger.error(e.getMessage(), e);
//				throw e;
//			}
//			//
//			logger.info(command_state);
//			//执行状态  
//			String ret = "";// 0 失败  1 成功 2 进行中 
//			if( command_state.equals(CommandResult.Success) ){
//				//代付成功：不进行操作 等回调的时候操作
//				logger.info("---CommandResult:Success---");
//				ret = "1";
//			}else if(command_state.equals(CommandResult.PendingContinue)){
//				//不会出现
//				logger.info("---CommandResult:PendingContinue---");
//			}else if( command_state.equals(CommandResult.FailStop) ){
//				logger.info("---CommandResult:FailStop---");
//				ret = "0";
//			}else if(command_state.equals(CommandResult.PendingStop)){
//				//进行中
//				logger.info("---CommandResult:PendingStop---");
//				//修改订单状态  为进行中
//				Order.updateOrderPending(orderNo, session);
//				ret = "2";
//			}else{
//				logger.info("---CommandResult:null---");
//			}
//			return ret;
////			completeReciever(order_entity,session);
////			//修改订单完成
////			return super.completeOrder(order_entity, null, session);
//		}
		
		public Map<String, Object> daiFuCompleteOrder(Map<String, Object> order_entity, Map<String, Object> param, Session session)
				throws Exception {
			// TODO Auto-generated method stub
			//完成支付
			addAgentDetail(order_entity,session);
			CommandResult command_state = null;
			String payFailMessage = null;
			String orderNo = (String)order_entity.get("orderNo");
			Map<String, Object> retMap = new HashMap<>();
			try {
				TradeCommandManager manager = TradeCommandManager.instance();
				command_state = manager.doCommands(orderNo, null);

			} catch(Exception e) {
				logger.error(e.getMessage(), e);
				
				Long bankMoney = (Long)order_entity.get("bank_money");
				//代付到余额
				if(null == bankMoney)
					throw e;
				//代付到银行卡
				else{
					command_state = CommandResult.FailStop;
		            payFailMessage =  e.getMessage();
				}
			}
			//
			logger.info(command_state);
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
			
			retMap.put("state", ret);
			retMap.put("payFailMessage", payFailMessage);
			return retMap;
//			completeReciever(order_entity,session);
//			//修改订单完成
//			super.completeOrder(order_entity, null, session);
		}
		
		private void addAgentDetail(Map<String,Object> order_entity,Session session)throws Exception{
			String target_userid = (String)order_entity.get("target_uuid");
			String collectPayList = (String)order_entity.get("collectPayListClob");
			JSONArray collect_list = new JSONArray(collectPayList);
			for(int k=0;k<collect_list.length();k++){
				JSONObject collect = collect_list.getJSONObject(k);
				String bizOrderNo = collect.getString("bizOrderNo");
				Long amount = collect.getLong("amount");
				Map<String,Object> agentOrderEntity = Order.getOrder((Long)order_entity.get("application_id"), bizOrderNo, session);
				List<Map<String,Object>> agentDetailList = Order.getAgentDetailList((Long)agentOrderEntity.get("id"),session);
				for(Map<String,Object> detail:agentDetailList){
					if(detail.get("userId").equals(target_userid)){
						Query query = session.createQuery("update AMS_AgentDetail set ys_amount=:ys_amount where id=:id");
						Long sy = (Long)detail.get("sk_amount")-(Long)detail.get("ys_amount")-(Long)detail.get("tk_amount");
						if(sy.longValue()<amount){
							query.setParameter("ys_amount", (Long)detail.get("ys_amount") + sy);
							amount = amount - sy;
						}else{
							query.setParameter("ys_amount", (Long)detail.get("ys_amount") + amount);
							amount = amount - amount;
						}
						query.setParameter("id", (Long)detail.get("id"));
						int rt = query.executeUpdate();
						if(rt!=1){
							throw new Exception("修改代收明细出错");
						}
					}
				}
				if(amount.longValue()>0){
					throw new Exception("代收订单金额不足！");
				}
			}
		}
		//订单失败的时候 在取消掉代收明细的金额
		private void reduceAgentDetail(Map<String,Object> order_entity,Session session)throws Exception{
			String target_userid = (String)order_entity.get("target_uuid");
			String collectPayList = (String)order_entity.get("collectPayListClob");
			JSONArray collect_list = new JSONArray(collectPayList);
			for(int k=0;k<collect_list.length();k++){
				JSONObject collect = collect_list.getJSONObject(k);
				String bizOrderNo = collect.getString("bizOrderNo");
				Long amount = collect.getLong("amount");
				Map<String,Object> agentOrderEntity = Order.getOrder((Long)order_entity.get("application_id"), bizOrderNo, session);
				List<Map<String,Object>> agentDetailList = Order.getAgentDetailList((Long)agentOrderEntity.get("id"),session);
				for(Map<String,Object> detail:agentDetailList){
					if(detail.get("userId").equals(target_userid)){
						Query query = session.createQuery("update AMS_AgentDetail set ys_amount=:ys_amount where id=:id");
						query.setParameter("ys_amount", (Long)detail.get("ys_amount") - amount);
						query.setParameter("id", (Long)detail.get("id"));
						int rt = query.executeUpdate();
						if(rt!=1){
							throw new Exception("修改代收明细出错");
						}
					}
				}
			}
		}
		//操作收款人列表中的数据
		public void completeReciever(Map<String,Object> order_entity,Session session)throws Exception{
			String target_userid = (String)order_entity.get("target_uuid");
//			String collectPayList = (String)order_entity.get("collectPayList");
//			JSONArray collect_list = new JSONArray(collectPayList);
//			for(int k=0;k<collect_list.length();k++){
//				JSONObject collect = collect_list.getJSONObject(k);
//				String bizOrderNo = collect.getString("bizOrderNo");
//				Long amount = collect.getLong("amount");
//				Map<String,Object> agentOrderEntity = Order.getOrder((Long)order_entity.get("application_id"), bizOrderNo, session);
//				List<Map<String,Object>> agentDetailList = Order.getAgentDetailList((Long)agentOrderEntity.get("id"),session);
//				for(Map<String,Object> detail:agentDetailList){
//					if(detail.get("userId").equals(target_userid)){
//						Query query = session.createQuery("update AMS_AgentDetail set ys_amount=:ys_amount where id=:id");
//						Long sy = (Long)detail.get("sk_amount")-(Long)detail.get("ys_amount")-(Long)detail.get("tk_amount");
//						if(sy.longValue()<amount){
//							query.setParameter("ys_amount", (Long)detail.get("ys_amount") + sy);
//							amount = amount - sy;
//						}else{
//							query.setParameter("ys_amount", (Long)detail.get("ys_amount") + amount);
//							amount = amount - amount;
//						}
//						query.setParameter("id", (Long)detail.get("id"));
//						int rt = query.executeUpdate();
//						if(rt!=1){
//							throw new Exception("修改代收明细出错");
//						}
//					}
//				}
//				if(amount.longValue()>0){
//					throw new Exception("代收订单金额不足！");
//				}
//			}
			
			//去分收款人的手续费
			if(order_entity.get("fee")!=null &&((Long)order_entity.get("fee")>0L)){
				operateFee(order_entity,target_userid,(Long)order_entity.get("fee"),(Long)order_entity.get("account_type_id"), "代付手续费", session);
			}

			//分账
			if(order_entity.get("splitRule")!=null && !"".equals(order_entity.get("splitRule"))){
				operateSpitRule(order_entity,target_userid,new JSONArray((String)order_entity.get("splitRule")),(Long)order_entity.get("account_type_id"),session);
			}
		}
		
		//操作分账
//		private void operateSpitRule(Map<String,Object> orderEntity,String source_userId,JSONArray spliRuteList,Long account_type_id,Session session)throws Exception{
//			for(int i=0;i<spliRuteList.length();i++){
//				JSONObject spliRute = spliRuteList.getJSONObject(i);
//				String target_bizUserId = spliRute.getString("bizUserId");
//				Long amount = spliRute.getLong("amount");
//				MemberService memberService = new MemberServiceImpl();
//				Map<String,Object> source_member = memberService.getUserInfo(source_userId);
//				Map<String,Object> target_member = memberService.getUserInfo((Long)orderEntity.get("application_id"), target_bizUserId);
//				if(amount.longValue()>0){
//					Map<String,Object> accountType = null;
//					if(account_type_id !=null){
//						accountType =  DynamicEntityService.getEntity(account_type_id, "AMS_AccountType");
//					}else{
//						AccountService accountService  = new AccountServiceImpl();
//						accountType = accountService.getApplicationCashAccountType((Long)orderEntity.get("application_id"));
//					}
//					Map<String, Object> param = new HashMap<String, Object>();
//					param.put("source_userId",      (String)source_member.get("userId"));
//		        	param.put("source_memberNo",    (String)source_member.get("memberNo"));
//		        	param.put("source_member_name", (String)source_member.get("name"));
//		            param.put("target_userId",      (String)target_member.get("userId"));
//		            param.put("target_memberNo",    (String)target_member.get("memberNo"));
//		            param.put("target_member_name", (String)target_member.get("name"));
//		            param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
//		            param.put("sub_trade_type",         Constant.SUB_TRADE_TYPE_SPLIT);
//		            param.put("trade_money",        amount);
//		            param.put("bizid",              (String)orderEntity.get("orderNo"));
//		            param.put("remark",             (String)orderEntity.get("remark"));
//		            param.put("account_type_id",    (Long)accountType.get("id"));
//		            param.put("target_account_type_id",    (Long)accountType.get("id"));
//		            param.put("isMaster",           true);
//		           
//		            param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
//		            param.put("orgNo",    			(String)orderEntity.get("orgNo"));
//		            
//		            TradeService.customTransfer(param);
//				}
//				
//				if(spliRute.get("fee")!=null &&(spliRute.getLong("fee")>0L)){
//					operateFee(orderEntity,(String)target_member.get("userId"),spliRute.getLong("fee"),account_type_id,session);
//				}
//				if(spliRute.get("splitRuleList") != null){
//					operateSpitRule(orderEntity,(String)target_member.get("userId"),spliRute.getJSONArray("splitRuleList"),account_type_id,session);
//					operateSpitRule(orderEntity,(String)target_member.get("userId"),splitRute.getJSONArray("splitRuleList"),(Long)targetAccountType.get("id"),session);
//				}
//				
//			}
//		}
		
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
				Map<String,Object> target_member = new HashMap<String, Object>();
				if(Constant.APPLICATION_BIZ_USER_ID.equals(target_bizUserId)){ //目标用户为平台
					Map<String, Object> applicationEntity = DynamicEntityService.getEntity((Long)orderEntity.get("application_id"), "AMS_Organization");
					target_member = memberService.getUserInfo((Long)applicationEntity.get("member_id"));
				}else{ //目标用户为平台会员
					target_member = memberService.getUserInfo((Long)orderEntity.get("application_id"), target_bizUserId);
				}

				if(target_member == null || target_member.isEmpty()){
					throw new BizException(ErrorCode.USER_NOTEXSIT, "分账收款方不存在。");
				}

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
					operateFee(orderEntity,(String)target_member.get("userId"), splitRute.getLong("fee"), (Long)targetAccountType.get("id"), "代付分账手续费", session);
				}
				if(splitRute.get("splitRuleList") != null){
					operateSpitRule(orderEntity,(String)target_member.get("userId"),splitRute.getJSONArray("splitRuleList"),(Long)targetAccountType.get("id"),session);
				}
			}
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
			 Map<String, Object> target_member = (Map<String, Object>)new Member().getUserInfoByOrgNo((String)orderEntity.get("orgNo"), session);
			 Map<String, Object> param = new HashMap<String, Object>();
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
	         param.put("remark",             remark + "（" + orderEntity.get("biz_trade_code") + "）");
	         param.put("account_type_id",    (Long)source_accountType.get("id"));
	         param.put("target_account_type_id",    (Long)target_accountType.get("id"));
	         param.put("isMaster",           true);
	         param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
	         param.put("orgNo",    			(String)orderEntity.get("orgNo"));
		     TradeService.customTransfer(param);
			
		}
}
