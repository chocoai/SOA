/**   
* @Title: AppCrossOrder.java 
* @Package bps.order 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年12月23日 上午10:26:19 
* @version V1.0   
*/
package bps.order;

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

import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.ITradeCommand.CommandResult;
import com.kinorsoft.ams.services.QueryService;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.application.Appliction;
import bps.code.CodeServiceImpl;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.external.tradecommand.ItsManage;
import bps.member.Member;
import bps.process.Extension;
import bps.process.PayChannelManage;
import bps.service.AccountService;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年12月23日 上午10:26:19  
 */
public class AppCrossOrder extends Order{

private static Logger logger = Logger.getLogger(AppCrossOrder.class.getName());
	
	@Override
	public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
		logger.info("AppCrossOrder.createOrder start");
		Long member_id              = (Long) param.get("memberId");
        Long source_member_id       = (Long) param.get("sourceMemberId");
        Long applicationId          = (Long) param.get("applicationId");
        Long sourceApplicationId	= (Long) param.get("sourceApplicationId");
        Long order_money       	  = (Long) param.get("money");
        String accountNoSet = (String) param.get("accountNoSet");
        String sourceAccountNoSet = (String) param.get("sourceAccountNoSet");
        if(applicationId.longValue() == sourceApplicationId.longValue()){
        	throw new BizException(ErrorCode.OTHER_ERROR,"参数非法");
        }
        Map<String,Object> source_application = DynamicEntityService.getEntity(sourceApplicationId, "AMS_Organization");
        if(source_application == null){
        	throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
        }
        
        Member member = new Member(member_id);
        Member source_member = new Member(source_member_id);
        if(member.getUserId() == null || "".equals(member.getUserId())){
        	throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
        }
        if(source_member.getUserId() == null || "".equals(source_member.getUserId())){
        	throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
        }
        if(applicationId.longValue()!=(member.getApplicationId())){
        	throw new BizException(ErrorCode.OTHER_ERROR,"参数非法");
        }
        if(sourceApplicationId.longValue()!=((Long)source_member.getApplicationId())){
        	throw new BizException(ErrorCode.OTHER_ERROR,"参数非法");
        }
        if(!isAppConif(applicationId,sourceApplicationId,accountNoSet,sourceAccountNoSet)){
        	throw new BizException(ErrorCode.APPLICATION_NO_CROSS_APP,"跨账户转账未开通");
        }
        if(!member.getIsIdentity_checked()){
        	throw new BizException(ErrorCode.USER_IS_NO_REALNAME,"用户不存在");
        }
        if(!source_member.getIsIdentity_checked()){
        	throw new BizException(ErrorCode.USER_IS_NO_REALNAME,"用户不存在");
        }
        if(!member.getName().equals(source_member.getName())){
        	throw new BizException(ErrorCode.OTHER_ERROR,"实名用户不一致");
        }
        if(!member.getIdentity_cardNo_md5().equals(source_member.getIdentity_cardNo_md5())){
        	throw new BizException(ErrorCode.OTHER_ERROR,"实名用户不一致");
        }
        Map<String,Object> sourceAccount = QueryService.queryAccount(source_member.getUserId(), sourceAccountNoSet);
        if(sourceAccount == null){
        	throw new BizException(ErrorCode.ACCOUNT_NO_ENOUGH,"账户余额不足");
        }else{
        	Long amount = (Long)sourceAccount.get("amount");
        	if(amount.longValue() < order_money.longValue()){
        		throw new BizException(ErrorCode.ACCOUNT_NO_ENOUGH,"账户余额不足");
        	}
        }
        Map<String,Object> source_accountType =  QueryService.getAccountType(sourceAccountNoSet);
        Map<String,Object> target_accountType =  QueryService.getAccountType(accountNoSet);
        //组装支付信息
        final Map<String,Object> accountMap = new HashMap<String,Object>();
        accountMap.put("source_account_type_id", source_accountType.get("id"));
        accountMap.put("source_account_type_label", source_accountType.get("name"));
        accountMap.put("source_accountSetNo", source_accountType.get("codeNo"));
        accountMap.put("target_account_type_id", target_accountType.get("id"));
        accountMap.put("target_account_type_label", target_accountType.get("name"));
        accountMap.put("target_accountSetNo", target_accountType.get("codeNo"));
        accountMap.put("tradeMoney", order_money);
        
		try {
            //创建订单
            final Map<String, String> entityMap = new HashMap<String, String>();
            
            entityMap.put("member_id",              member.getId().toString());
            entityMap.put("member_name",            member.getName() == null ? "" : member.getName());
            entityMap.put("member_uuid",            member.getUserId());
            entityMap.put("memberNo",               member.getMemberNo());

            entityMap.put("target_member_id",       source_member.getId().toString());
            entityMap.put("target_member_name",     source_member.getName() == null ? "" : source_member.getName());
            entityMap.put("target_uuid",            source_member.getUserId());
            entityMap.put("target_memberNo",        source_member.getMemberNo());
            entityMap.put("order_state",            Constant.ORDER_STATE_WAIT_PAY.toString());
            entityMap.put("source",                 param.get("source").toString());

			entityMap.put("order_money", 			order_money.toString());
			Calendar calendar = Calendar.getInstance();
			entityMap.put("create_time", 			String.valueOf(calendar.getTime().getTime()));

			entityMap.put("sub_trade_type", 		Constant.SUB_TRADE_TYPE_CROSS_APP.toString());
			entityMap.put("trade_type", 			Constant.TRADE_TYPE_TRANSFER.toString());
			entityMap.put("order_type", 			Constant.ORDER_TYPE_CROSS_APP.toString());
			entityMap.put("orgNo", 					param.get("orgNo").toString());
				
            entityMap.put("order_name", (String)param.get("orderName"));
            entityMap.put("member_ip", (String)param.get("memberIp"));
            entityMap.put("extend_info", (String)param.get("ext"));
            entityMap.put("description", "跨应用转账");
            entityMap.put("remark", (String)param.get("remark"));
            entityMap.put("showUrl", (String)param.get("showUrl"));
            entityMap.put("application_id",         param.get("applicationId").toString());
            entityMap.put("application_label",      param.get("applicationName").toString());
            entityMap.put("target_app_id",          source_application.get("id").toString());
            entityMap.put("target_app_label",       source_application.get("name").toString());
            
            entityMap.put("bizOrderNo",             param.get("bizOrderNo").toString());
//            entityMap.put("industry_code",          String.valueOf(param.get("industry_code")));
//			entityMap.put("industry_name",          String.valueOf(param.get("industry_name")));
//			entityMap.put("frontUrl",               String.valueOf(param.get("frontUrl")));
//			entityMap.put("backUrl",                String.valueOf(param.get("backUrl")));
//			if(param.get("fee") != null){
//				entityMap.put("fee", String.valueOf(param.get("fee")));
//			}
			if(param.get("ordErexpireDatetime") != null){
				entityMap.put("ordErexpireDatetime", String.valueOf(((Date)param.get("ordErexpireDatetime")).getTime()));
	        }
	        if(param.get("extend_info") != null){
	        	entityMap.put("extend_info", String.valueOf(param.get("extend_info")));
	        }
	        if(param.get("summary") != null){
	        	entityMap.put("summary", String.valueOf(param.get("summary")));
	        }
//	        if(param.get("bank_code")!= null &&!"".equals(param.get("bank_code").toString().trim())){
//	        	entityMap.put("bank_code", String.valueOf(param.get("bank_code")));
//            }
//	        if(param.get("bank_name")!= null &&!"".equals(param.get("bank_name").toString().trim())){
//	        	entityMap.put("bank_name", String.valueOf(param.get("bank_name")));
//            }
//          final List accountList = (List)param.get("accountList");
//    		final List payInterfaceList = (List)param.get("payInterfaceList");
//          if(param.get("splitRule")!=null){
//            	entityMap.put("splitRule", (String)param.get("splitRule"));
//          }
            
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
	        Map<String,Object> ret = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
	        	@Override
				public boolean before(Session session) throws Exception {
					Map<String,Object> orderEntity = getOrder(Long.parseLong(entityMap.get("application_id")),entityMap.get("bizOrderNo"),session);
					if(orderEntity != null){
						throw new BizException(ErrorCode.ORDER_ERROR,"订单已存在");
					}
					//检查指令
//					Map<String,Object> checkParam = new HashMap<String,Object>();
//					checkParam.put("orderMoney", Long.parseLong(entityMap.get("order_money")));
//					checkParam.put("application_id", Long.parseLong(entityMap.get("application_id")));
//					if(entityMap.get("fee") != null){
//						checkParam.put("fee", Long.parseLong(entityMap.get("fee")));
//					}
//					checkPayCommands(checkParam,accountList,payInterfaceList,session);
					return true;
				}
	        	
	        	
				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					Map<String, Object> orderEntity=new HashMap<String, Object>();
					session.beginTransaction();
	                orderEntity = DynamicEntityService.createEntity("AMS_Order", entityMap, null);
	                String orderNo = Order.generateOrderNo((Long)orderEntity.get("id"));
	                Query query = session.createQuery("update AMS_Order set orderNo=:orderNo where id=:orderId");
	                query.setParameter("orderNo", orderNo);
	                query.setParameter("orderId", (Long)orderEntity.get("id"));
	                query.executeUpdate();
	                orderEntity.put("orderNo", orderNo);
					return generatePayCommands(orderEntity,accountMap,session);
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
	
	private Map<String,Object> generatePayCommands(Map<String, Object> order_entity,Map<String,Object> accountMap, Session session)throws Exception{
		Query query = session.createQuery("delete AMS_OrderPayDetail where bizid=:orderNo");
        query.setParameter("orderNo", order_entity.get("orderNo"));
		query.executeUpdate();
		List<Map<String,String>> commandList = new ArrayList<Map<String,String>>();
		commandList = generatePayCommandByAccount(order_entity,accountMap,commandList,session);
		
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("orderNo", order_entity.get("orderNo"));
		String orderNo = (String)order_entity.get("orderNo");
		
		Map<String, Object> retMap = new HashMap<String, Object>();
        TradeCommandManager tradeCommandManager = TradeCommandManager.instance();
        try {
            CommandResult command_state = tradeCommandManager.doCommands(orderNo, null);
            retMap.put("command_result", command_state);
        } catch(Exception e) {
//            logger.error(e.getMessage(), e);
//            retMap.put("command_result", CommandResult.FailStop);
//            retMap.put("err_msg1", e.getMessage());
        	logger.error(e.getMessage(), e);
        	throw new BizException(ErrorCode.ORDER_ERROR,e.getMessage());
        }
		logger.info("retMap------------------:"+retMap);
		CommandResult command_state = (CommandResult)retMap.get("command_result");
		logger.info("ret_code1------------------:"+command_state);
		//{command_result=PendingStop, ret_code2=null, ret_code1=1000, err_msg1=序号为0 的交易中账号:6214835741131658 没有从协议库中找到对应的协议号, err_msg2=null}
		if( !command_state.equals(CommandResult.Success) ){
			throw new BizException(ErrorCode.ORDER_ERROR,"跨应用转账失败");
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
	private List<Map<String, String>> generatePayCommandByAccount(Map<String, Object> order_entity,
			Map<String,Object> accountMap, List<Map<String, String>> commandList, Session session) throws Exception {
		
		AccountService accountService = new AccountServiceImpl();
		Map<String, Object> cashAccountEntity = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
		if(cashAccountEntity == null){
			throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT,"此应用下现金账户集不存在");
		}
		int k = commandList.size() + 1;
		
		Map<String, String> command = new HashMap<String, String>();
		command.put("source_userId", 			(String)order_entity.get("target_uuid"));
		command.put("source_memberNo", 			(String)order_entity.get("target_memberNo"));
		command.put("source_member_name", 		(String)order_entity.get("target_member_name"));
		command.put("target_userId", 		(String)order_entity.get("member_uuid"));
		command.put("target_memberNo", 		(String)order_entity.get("memberNo"));
		command.put("target_member_name", 	(String)order_entity.get("member_name"));
		
		command.put("account_type_id", 			accountMap.get("source_account_type_id").toString());
		command.put("account_type_label", 		(String)accountMap.get("source_account_type_label"));
		command.put("account_codeNo", 			(String)accountMap.get("source_accountSetNo"));
		command.put("target_account_type_id", 	accountMap.get("target_account_type_id").toString());
		command.put("target_account_type_label", (String)accountMap.get("target_account_type_label"));
		command.put("target_account_codeNo", 	 (String)accountMap.get("target_accountSetNo"));
		command.put("trade_type", 			order_entity.get("trade_type").toString());
		command.put("sub_trade_type", 		order_entity.get("sub_trade_type").toString());
		
		command.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
		command.put("command_no", 				(String)order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + k);
		command.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
		command.put("seq_no", 					k + "");
		command.put("pay_interfaceNo", 			Constant.PAY_INTERFACE_AMS);
		command.put("pay_channelNo", 			Constant.PAY_CHANNEL_AMS);

		command.put("trade_money", 				accountMap.get("tradeMoney").toString());
		command.put("bizid", 					(String)order_entity.get("orderNo"));
		command.put("orgNo", 					(String)order_entity.get("orgNo"));
		command.put("order_type", 				order_entity.get("order_type").toString());
		command.put("isMaster", 				"true");
		command.put("call_type", 				Constant.CALL_TYPE_INTERFACE.toString());
		
        if(order_entity.get("summary") != null){
        	command.put("remark",         		order_entity.get("summary").toString());
        }
        
        commandList.add(command);
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
		
		super.completeOrder(order_entity, null, session);
		return null;
		
	
	}
	
	public boolean isAppConif(Long applictionId,Long sourceApplicationId,String accountNo,String sourceAccountNo) throws Exception{
		final Long _applictionId = applictionId;
		final Long _sourceApplicationId = sourceApplicationId;
		final String _accountNo = accountNo;
		final String _sourceAccountNo = sourceAccountNo;
		
		
		List<Map<String,Object>> list =  EntityManagerUtil.execute(new QueryWork<List<Map<String,Object>>>() {
			@Override
			public List<Map<String,Object>> doQuery(Session session) throws Exception {
				Query query = session.createQuery("from AMS_AppConfig where application_id=:application_id and openApp_id=:openApp_id "
						+ "and config_type=:config_type and accountNo=:accountNo and sourceAccountNo=:sourceAccountNo and isOpen=:isOpen");
	            query.setParameter("application_id", _applictionId);
	            query.setParameter("openApp_id", _sourceApplicationId);
	            query.setParameter("accountNo", _accountNo);
	            query.setParameter("sourceAccountNo", _sourceAccountNo);
	            query.setParameter("config_type", Constant.APPLICATION_CONFIG_CROASSAPP);
	            query.setParameter("isOpen", true);
	            List<Map<String,Object>> list =  query.list();
	            return list;
			}
		});
		if(list.size()==1){
			return true;
		}
		return false;
	}
}
