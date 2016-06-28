/**   
* @Title: DaiShouOrder.java 
* @Package bps.order 
* @Description: (用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2015年9月14日 下午5:55:51 
* @version V1.0   
*/
package bps.order;

import bps.monitor.TransMonitor;
import bps.order.component.GenerateAgentDetail;
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
import bps.code.CodeServiceImpl;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.external.tradecommand.ItsManage;
import bps.member.Member;
import bps.member.MemberServiceImpl;
import bps.process.Extension;
import bps.process.PayChannelManage;
import bps.service.AccountService;
import bps.service.MemberService;

/**
 * @author Administrator
 *
 */
public class DaiShouOrder extends Order{
	
	private static Logger logger = Logger.getLogger(ShoppingOrder.class.getName());
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
		logger.info("ShoppingOrder.createOrder start");
		logger.info("param=" + param);
		final Long member_id              = (Long) param.get("memberId");
        Long order_money       	  = (Long) param.get("money");
        final List accountList = (List)param.get("accountList");
		final List payInterfaceList = (List)param.get("payInterfaceList");
		final List couponList = (List)param.get("couponList");

		String posCode = "";
        if(param.get("recieverList") == null){
        	throw new BizException(ErrorCode.PARAM_ERROR,"代收订单列表不能为空");
        }
        
		try {
			Map<String,Object> applicationEntity = DynamicEntityService.getEntity((Long)param.get("applicationId"), "AMS_Organization");
			if(applicationEntity == null){
				throw new Exception("对应的应用不存在");
			}
			if(applicationEntity.get("member_id")==null ){
				throw new Exception("所在应用的开发者不存在");
			}
			Map<String,Object> target_member =  DynamicEntityService.getEntity((Long)applicationEntity.get("member_id"), "AMS_Member");
			if(target_member==null){
				throw new Exception("所在应用的开发者不存在");
			}
			
			final Member member = new Member(member_id);
			Map<String,Object> member_entity = member.getUserInfo();
            //创建订单
            final Map<String, String> entityMap = new HashMap<>();
            
            entityMap.put("member_id",              member_entity.get("id").toString());
            entityMap.put("member_name",            member_entity.get("name") == null ? "" : member_entity.get("name").toString());
            entityMap.put("member_uuid",            member_entity.get("userId").toString());
            entityMap.put("memberNo",               (String)member_entity.get("memberNo"));

            entityMap.put("target_member_id",       target_member.get("id").toString());
            entityMap.put("target_member_name",     target_member.get("name") == null ? "" : target_member.get("name").toString());
            entityMap.put("target_uuid",            target_member.get("userId").toString());
            entityMap.put("target_memberNo",        (String)target_member.get("memberNo"));
            entityMap.put("order_state",            Constant.ORDER_STATE_WAIT_PAY.toString());
            entityMap.put("source",                 param.get("source").toString());

			entityMap.put("order_money", 			order_money.toString());
			Calendar calendar = Calendar.getInstance();
			entityMap.put("create_time", 			String.valueOf(calendar.getTime().getTime()));

			entityMap.put("sub_trade_type", 		Constant.SUB_TRADE_TYPE_DAISHOU.toString());
			entityMap.put("trade_type", 			Constant.TRADE_TYPE_TRANSFER.toString());
			entityMap.put("order_state", 			Constant.ORDER_STATE_WAIT_PAY.toString());
			entityMap.put("order_type", 			Constant.ORDER_TYPE_DAISHOU.toString());
			entityMap.put("orgNo", 				param.get("orgNo").toString());
            entityMap.put("order_name", (String)param.get("orderName"));
            entityMap.put("member_ip", (String)param.get("memberIp"));
            entityMap.put("extend_info", (String)param.get("ext"));
            entityMap.put("description", (String)param.get("description"));
            
            entityMap.put("remark", (String)param.get("remark"));
            entityMap.put("application_id",         param.get("applicationId").toString());
            entityMap.put("application_label",      param.get("applicationName").toString());
            if(param.get("splitRule")!=null){
            	entityMap.put("splitRule", (String)param.get("splitRule"));
            }
            entityMap.put("goodsType",             	String.valueOf(param.get("goodsType")));
            entityMap.put("goodsNo",             	String.valueOf(param.get("goodsNo")));
            entityMap.put("goodsName",             	String.valueOf(param.get("goodsName")));
            entityMap.put("biz_trade_code", 		(String)param.get("biz_trade_code"));
            entityMap.put("industry_code", 			(String)param.get("industry_code"));
            entityMap.put("industry_name", 			(String)param.get("industry_name"));
            entityMap.put("frontUrl", 				(String)param.get("frontUrl"));
            entityMap.put("backUrl", 				(String)param.get("backUrl"));
            entityMap.put("bizOrderNo",             param.get("bizOrderNo").toString());
            entityMap.put("recieverListClob",       param.get("recieverList").toString());
            if(param.get("fee")!=null){
            	entityMap.put("fee",       param.get("fee").toString());
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
	        //检验业务码
	        checkTradeCode(entityMap);

			//产生pos支付码
	        if(payInterfaceList != null && payInterfaceList.size() > 0){
				Map<String, Object> payEntity = (Map<String, Object>)payInterfaceList.get(0);
				if( Constant.PAY_INTERFACE_POS.equals(payEntity.get("payInterFaceNo"))){
					posCode = getPosCode();
					entityMap.put("pos_pay_code",posCode);
				}
	        }


	        final Map<String, Object> _param = param;
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
	        Map<String,Object> ret = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
	        	@Override
				public boolean before(Session session) throws Exception {
					Map<String,Object> orderEntity = getOrder(Long.parseLong(entityMap.get("application_id")),entityMap.get("bizOrderNo"),session);
					if(orderEntity != null){
						Long orderType = (Long)orderEntity.get("order_type");
						if(!Constant.ORDER_TYPE_DAISHOU.equals(orderType)){
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
					checkPayCommands(entityMap,accountList,payInterfaceList,couponList,session);
					return true;
				}
	        	
	        	
				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					Long orderId = null;
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
					Map<String, Object> commandsExtParam = new HashMap<String, Object>();
					commandsExtParam.put("applicationMemberId", (Long)_param.get("applicationMemberId"));
					return generatePayCommands(member,orderEntity,accountList,couponList,payInterfaceList,commandsExtParam,session);
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
	
	
	/****
	  * 
	 * @Title: checkPayCommands 
	 * @Description:(检查指令中生存的问题) 
	 * @param @param param
	 * @param @param accountList
	 * @param @param payInterfaceList
	 * @param @param session
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws
	  */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkPayCommands(Map<String,String> orderEntity,List accountList, List payInterfaceList, List couponList, Session session) throws Exception{
		//检查支付金额
		Long payMoney = 0L;
		Long orderMoney = Long.parseLong(orderEntity.get("order_money"));
		Long applicationId = Long.valueOf(orderEntity.get("application_id"));
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
		if(couponList != null){
			for(Object obj : couponList){
				Map<String, Object> couponMap = (Map<String, Object>)obj;
				payMoney += (Long)couponMap.get("tradeMoney");
			}
		}
		if(!payMoney.equals(orderMoney)) {
			logger.info("payMoney=" + payMoney);
			logger.info("orderMoney=" + orderMoney);
			throw new Exception("支付金额不符");
		}
		//检查收款人列表金额
		String recieverList = (String)orderEntity.get("recieverListClob");
		JSONArray jsonArray = null;
        try{
        	jsonArray = new JSONArray(recieverList);
        }catch(Exception e){
        	throw new Exception("代收订单列表格式错误");
        }
        Long allMoney = 0L;

		//检查会员是否存在，并且可用
        MemberService memberService = new MemberServiceImpl();
		Map<String, String> bizUserIdMap = new HashMap<String, String>();
		//用于检测用户合法性，因为性能问题，现不进行检测。
//		StringBuilder queryMemberSql = new StringBuilder();
//		queryMemberSql.append("from AMS_Member where application_id=:applicationId and biz_user_id in(");

    	for(int i=0;i<jsonArray.length();i++){
			JSONObject receiver = jsonArray.getJSONObject(i);

			if(bizUserIdMap.containsKey((String)receiver.getString("bizUserId"))){
				throw new BizException(ErrorCode.USER_ERROR, "收款人" + (String)receiver.getString("bizUserId") + "有重复。");
			}
			bizUserIdMap.put((String)receiver.getString("bizUserId"), "");

//    		Map<String, Object> ret = memberService.getUserInfo(applicationId, (String)receiver.getString("bizUserId"));
//			if(ret == null || ret.isEmpty() || !Constant.USER_STATE_ACTIVATION.equals((Long)ret.get("user_state"))){
//				throw new BizException(ErrorCode.USER_ERROR, "会员" + (String)receiver.getString("bizUserId") + "不存在或者已经被锁定");
//			}

//			if(i > 0){
//				queryMemberSql.append(",");
//			}
//			queryMemberSql.append("'").append((String)receiver.getString("bizUserId")).append("'");
			
    		//性能问题暂时先注释
//    		Map<String, Object> ret = memberService.getUserInfo(applicationId, (String)receiver.getString("bizUserId"));
//    		if(ret == null || ret.isEmpty() || !Constant.USER_STATE_ACTIVATION.equals((Long)ret.get("user_state"))){
//    			throw new BizException(ErrorCode.USER_ERROR, "会员" + (String)receiver.getString("bizUserId") + "不存在或者已经被锁定");
//    		}
    		
    		allMoney += receiver.getLong("amount");
    	}

//		queryMemberSql.append(")");
//		Query query = session.createQuery(queryMemberSql.toString());
//		query.setParameter("applicationId", applicationId);
//		if(jsonArray.length() != query.list().size()){
//			throw new BizException(ErrorCode.USER_ERROR, "收款人列表错误。");
//		}

    	if(orderEntity.get("fee")!=null){
    		allMoney += Long.parseLong(orderEntity.get("fee"));
    	}
    	if(allMoney.longValue() != orderMoney){
    		throw new Exception("收款人金额列表和订单金额列表不符");
    	}	
	}
	
	private void generateAgentDetail(Map<String,Object> orderMap,Session session)throws Exception{
		Query query = session.createQuery("delete AMS_AgentDetail where agentOrder_id=:agentOrder_id");
		query.setParameter("agentOrder_id", orderMap.get("application_id"));
		query.executeUpdate();
		String recieverList = (String)orderMap.get("recieverListClob");
		JSONArray jsonArray = new JSONArray(recieverList);
		Map<String,Object> checkAgent = new HashMap<String,Object>();
		for(int i=0;i<jsonArray.length();i++){
    		JSONObject o = jsonArray.getJSONObject(i);
    		Member member = new Member();
    		member.setApplication_id((Long)orderMap.get("application_id"));
    		member.setBizUserId(o.getString("bizUserId"));
    		Map<String,String> agentDetail = new HashMap<String,String>();
    		agentDetail.put("agentOrder_id", String.valueOf(orderMap.get("id")));
    		agentDetail.put("agentOrder_label", (String)orderMap.get("orderNo"));
    		agentDetail.put("bizUserId", o.getString("bizUserId"));
    		agentDetail.put("userId", member.getUserId());
    		agentDetail.put("sk_amount", String.valueOf(o.get("amount")));
    		agentDetail.put("ys_amount", "0");
    		agentDetail.put("tk_amount", "0");
    		if(checkAgent.get(member.getUserId())==null){
    			checkAgent.put(member.getUserId(), true);
    		}else{
    			throw new Exception("收款人员列表中有相同的人员记录");
    		}
    		DynamicEntityService.createEntity("AMS_AgentDetail", agentDetail, null);
    	}
	}
	private Long command_index = 1L;
	@SuppressWarnings("rawtypes")
	private Map<String,Object> generatePayCommands(Member member,Map<String, Object> order_entity,List accountList,List couponList, List payInterfaceList, Map<String, Object> extParam, Session session)throws Exception{
		//生成代收明细
//		generateAgentDetail(order_entity,session);
		GenerateAgentDetail generateAgentDetail =  new GenerateAgentDetail(order_entity);
		generateAgentDetail.start();
		
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
		Map<String, Object> couponExtParam = new HashMap<String, Object>();
		couponExtParam.put("applicationMemberId", (Long)extParam.get("applicationMemberId"));
		
		//产生指令
		List<Map<String, String>> commandList = generatePayCommandsByPayInterface(order_entity, payInterfaceList, ret, session);
		commandList = generatePayCommandsByCoupon(order_entity, couponList, commandList, couponExtParam, session);
		commandList = generatePayCommandByAccount(order_entity,accountList,commandList,session);
		
		ret.put("orderNo", order_entity.get("orderNo"));
		Map<String, String> command = commandList.get(0);
		if(Constant.PAY_INTERFACE_QUICK.equals((String)command.get("pay_interfaceNo"))) {
			logger.info("command:"+command);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map<String, String>> generatePayCommandByAccount(Map<String, Object> order_entity,
			 List accountList, List<Map<String, String>> commandList, Session session) throws Exception {


		AccountService accountService = new AccountServiceImpl();
		Map<String, Object> cashAccountEntity = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
		if(cashAccountEntity == null){
			throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT,"此应用下现金账户集不存在");
		}
//		Map<String, Object> target_accountEntity = QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_REPLACE_COLLECT);
		String target_accountNo = getAccountNoByTradeNo((String)order_entity.get("biz_trade_code"));
		Map<String, Object> target_accountEntity = QueryService.getAccountType(target_accountNo);
		//int k = commandList.size() + 1;
		Long accountMoney = 0L;
		for(Object obj : accountList) {
			Map<String, Object> accountMap = (Map<String, Object>) obj;
			Map<String, Object> accountEntity = QueryService.getAccountType((String)accountMap.get("accountSetNo"));
			
			if(accountEntity.get("codeNo").equals(cashAccountEntity.get("codeNo"))){
				continue;
			}
			Map<String, String> command = new HashMap<String, String>();
			command.put("target_userId", 		(String)order_entity.get("target_uuid"));
			command.put("target_memberNo", 		(String)order_entity.get("target_memberNo"));
			command.put("target_member_name", 	(String)order_entity.get("target_member_name"));
			command.put("trade_type", 			order_entity.get("trade_type").toString());
			command.put("sub_trade_type", 		order_entity.get("sub_trade_type").toString());
			
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
			
			
			command.put("account_type_id", 			accountEntity.get("id").toString());
			command.put("account_type_label", 		(String)accountEntity.get("name"));
			command.put("account_codeNo", 			(String)accountEntity.get("codeNo"));
			
			command.put("target_account_type_id", 			target_accountEntity.get("id").toString());
			command.put("target_account_type_label", 		(String)target_accountEntity.get("name"));
			command.put("target_account_codeNo", 			(String)target_accountEntity.get("codeNo"));
			command.put("call_type", 				accountMap.get("call_type").toString());
			if(accountMap.get("bank_code") != null) {
				command.put("bank_code",         	accountMap.get("bank_code").toString());
            }
			
			if(accountMap.get("accountNo_encrypt") != null)
				command.put("accountNo_encrypt",	accountMap.get("accountNo_encrypt").toString());
            if(accountMap.get("accountNo") != null)
            	command.put("accountNo",     		accountMap.get("accountNo").toString());
            if(accountMap.get("account_name") != null)
            	command.put("account_name",      	accountMap.get("account_name").toString());
            if(accountMap.get("card_type") != null)
            	command.put("card_type",         	accountMap.get("card_type").toString());
            if(accountMap.get("account_prop") != null)
            	command.put("account_prop",     	accountMap.get("account_prop").toString());
            if(accountMap.get("handling_mode") != null)
            	command.put("handling_mode",     	accountMap.get("handling_mode").toString());
            if(accountMap.get("remark") != null)
            	command.put("remark",         		accountMap.get("remark").toString());
            if(accountMap.get("extend_info") != null)
            	command.put("extend_info",       	accountMap.get("extend_info").toString());
            
            commandList.add(command);
            command_index++;
            accountMoney += (Long)accountMap.get("tradeMoney");
		}

		//使用外部交易的金额去支付
		if(((Long)order_entity.get("order_money") - accountMoney) > 0) {
			//判断支付权限
			Map<String, Object> payInfo = new HashMap<String, Object>();
			payInfo.put("orgNo", (String)order_entity.get("orgNo"));
			payInfo.put("payInterfaceNo", Constant.PAY_INTERFACE_AMS);
			payInfo.put("account", Boolean.TRUE);
			Order.judgeAppicationPayPermission(payInfo, session);

			Map<String, String> command = new HashMap<String, String>();
			
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
			command.put("sub_trade_type", 			Constant.SUB_TRADE_TYPE_DAISHOU.toString());
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
			command.put("target_account_type_id", 	 target_accountEntity.get("id").toString());
			command.put("target_account_type_label", (String)target_accountEntity.get("name"));
			command.put("target_account_codeNo", 	(String)target_accountEntity.get("codeNo"));
			
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
	 * 产生指令--代金券
	 * @param order_entity
	 * @param couponList
	 * @param session
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, String>> generatePayCommandsByCoupon(Map<String, Object> order_entity, List couponList, List<Map<String, String>> commandList, Map<String, Object> extParam, Session session) throws Exception{
		Long applicationId = (Long)order_entity.get("application_id");
		
		//获取最终支付的现金账户集
		AccountServiceImpl accountService = new AccountServiceImpl();
		Map<String, Object> cashAccountEntity = accountService.getApplicationCashAccountType(applicationId);
		if(cashAccountEntity == null){
			throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT,"此应用下现金账户集不存在");
		}
		
		//获取此应用的营销专用账户集
		Map<String, Object> couponAccountEntity = accountService.getAccountTypeByCodeNo(Constant.ACCOUNT_NO_COUPON);
		
		//获取所属应用的memberEntity
		Long applicationMemberId = (Long)extParam.get("applicationMemberId");
		Map<String, Object> applicationMemberEntity = DynamicEntityService.getEntity(applicationMemberId, "AMS_Member");
		
		Long accountMoney = 0L;
		//产生指令
		for(Object coupon : couponList) {
			Map<String, Object> couponMap = (Map<String, Object>)coupon;

			//判断支付权限
			Map<String, Object> payInfo = new HashMap<String, Object>();
			payInfo.put("orgNo", (String)order_entity.get("orgNo"));
			payInfo.put("payInterfaceNo", Constant.PAY_INTERFACE_AMS);
			payInfo.put("account", Boolean.TRUE);
			Order.judgeAppicationPayPermission(payInfo, session);
			
			//创建指令map
			Map<String, String> command = new HashMap<String, String>();
			command.put("source_userId", 			(String)applicationMemberEntity.get("userId"));
			command.put("source_memberNo", 			(String)applicationMemberEntity.get("memberNo"));
			command.put("source_member_name", 		(String)applicationMemberEntity.get("name"));
			command.put("account_type_id", 			couponAccountEntity.get("id").toString());
			command.put("account_type_label", 		(String)couponAccountEntity.get("name"));
			command.put("account_codeNo", 			(String)couponAccountEntity.get("codeNo"));
			
			command.put("target_userId", 			(String)order_entity.get("member_uuid"));
			command.put("target_memberNo", 			(String)order_entity.get("memberNo"));
			command.put("target_member_name", 		(String)order_entity.get("member_name"));
			command.put("target_account_type_id", 			cashAccountEntity.get("id").toString());
			command.put("target_account_type_label", 		(String)cashAccountEntity.get("name"));
			command.put("target_account_codeNo", 			(String)cashAccountEntity.get("codeNo"));
			
			command.put("trade_type", 			    Constant.TRADE_TYPE_DEPOSIT.toString());
			command.put("sub_trade_type", 			Constant.SUB_TRADE_TYPE_MARKETING_ACTIVITY.toString());
			command.put("pay_state", 				com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY + "");
			command.put("command_no", 				(String)order_entity.get("orderNo") + com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN + command_index);
			command.put("refund_status", 			Constant.REFUND_STATE_NODO.toString());
			command.put("seq_no", 					command_index + "");
			command.put("pay_interfaceNo", 			Constant.PAY_INTERFACE_AMS);
			command.put("pay_channelNo", 			Constant.PAY_CHANNEL_AMS);
			command.put("trade_money", 				couponMap.get("tradeMoney").toString());
			command.put("bizid", 					(String)order_entity.get("orderNo"));
			command.put("orgNo", 					(String)order_entity.get("orgNo"));
			command.put("order_type", 				order_entity.get("order_type").toString());
			command.put("isMaster", 				"false");
			command.put("call_type", 				Constant.CALL_TYPE_INTERFACE.toString());
			      
            commandList.add(command);
            command_index++;
		}
		
		return commandList;
	}
	
	
	
	/****
	* @Title: generatePayCommandsByPayInterface 
	* @Description: TODO(生成通道的指令) 
	* @param @param order_entity
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
			
			//获取现金账户集
			Map<String, Object> source_accountEntity = QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
			AccountService accountService = new AccountServiceImpl();
			Map<String, Object> target_accountEntity = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
			if(target_accountEntity == null){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT,"此应用下现金账户集不存在");
			}
			
	    	for(int i = 0, j = payInterfaceList.size(); i < j; i ++) {
	    		Map<String, Object> payInterfaceMap = (Map<String, Object>)payInterfaceList.get(i);
//	    		String acountTypeCodeNo = (String)payInterfaceMap.get("accountTypeCodeNo");
//	    		Map<String, Object> target_accountEntity = QueryService.getAccountType(acountTypeCodeNo);
	    		command = new HashMap<String, String>();
				logger.info("创建command:payInterfaceMap="+payInterfaceMap);
				
//				JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfo((String)payInterfaceMap.get("payInterFaceNo"));
//				Member member = new Member();
//				member.setUserId(payInterface_entity.get("system_uuid").toString());
//				logger.info("systemMember:"+member);
				
				JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfo((String)payInterfaceMap.get("payInterFaceNo"), (String)order_entity.get("orgNo"));
				Member member = new Member();
				member.setUserId(payInterface_entity.get("system_uuid").toString());
				logger.info("systemMember:"+member);
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
				command.put("target_account_type_id", 	 target_accountEntity.get("id").toString());
				command.put("target_account_type_label", (String)target_accountEntity.get("name"));
				command.put("target_account_codeNo", 	 (String)target_accountEntity.get("codeNo"));
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
					logger.info("bank_card:"+bank_card);
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
	            if(payInterfaceMap.get("remark") != null)
	            	command.put("remark",         		payInterfaceMap.get("remark").toString());
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
	
	//操作手续费
	private void operateFee(Map<String,Object> orderEntity,String source_userId,Long fee,Long account_type_id,Session session)throws Exception{
		
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
         param.put("remark",             "代收手续费（" + orderEntity.get("biz_trade_code") + "）");
         param.put("account_type_id",    (Long)source_accountType.get("id"));
         param.put("target_account_type_id",    (Long)target_accountType.get("id"));
         param.put("isMaster",           true);
         param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
         param.put("orgNo",    			(String)orderEntity.get("orgNo"));
	     TradeService.customTransfer(param);
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
		//从代收中间账户中收取手续费
		Long fee = (Long)order_entity.get("fee");
		if(fee != null && fee > 0){
			Map<String, Object> applicationEntity = (Map<String, Object>)new Member().getUserInfoByOrgNo((String)order_entity.get("orgNo"), session);
			String middleAccountNo = getAccountNoByTradeNo((String)order_entity.get("biz_trade_code"));
			Map<String, Object> sourceAccountTypeEntity = QueryService.getAccountType(middleAccountNo);
			operateFee(order_entity, (String)applicationEntity.get("userId") , fee, (Long)sourceAccountTypeEntity.get("id"), session);
		}
		
		super.completeOrder(order_entity, null, session);

		sendProducerMessageWithKafka((String)order_entity.get("orderNo"),"OK");
		return null;
	}

}
