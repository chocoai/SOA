/**   
* @Title: BatchRefundOrder.java 
* @Package bps.order 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年12月24日 下午4:35:58 
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
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.application.Appliction;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.service.AccountService;

import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.services.QueryService;
import com.kinorsoft.ams.services.TradeService;

/** 
 * 流标退款
 * @author huadi
 * @date 2015年12月24日 下午4:35:58  
 */
public class BatchRefundOrder extends Order{
	
private static Logger logger = Logger.getLogger(BatchRefundOrder.class.getName());
	
	@Override
	public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
		// TODO Auto-generated method stub
		logger.info("BatchRefundOrder.createOrder start");
		logger.info("param="+param);
		String bizBatchNo = (String)param.get("bizBatchNo");
		if(bizBatchNo == null){
			throw new Exception("批次号必传");
		}
		Long goodsType = (Long)param.get("goodsType");
		if(goodsType!=1L){
			throw new Exception("商品类型不为标的类型");
		}
		String goodsNo = (String)param.get("goodsNo");
		String goodsName = (String)param.get("goodsName");
		if(goodsNo==null ||"".equals(goodsNo)){
			throw new Exception("商品编号不能为空");
		}
		String refundOriList = (String)param.get("refundOriList");
		if(refundOriList == null){
        	throw new BizException(ErrorCode.PARAM_ERROR,"退款原订单号不能为空");
        }
		JSONArray refundOriArr = null;
		try{
			refundOriArr = new JSONArray(refundOriList);
		}catch(Exception e){
			throw new Exception("退款列表格式错误");
		}
//		final Long money = (Long) param.get("money");
//		final Map<String, Object> oriOrderMap = new HashMap<String, Object>();
//		final String oriOrderNo = (String) param.get("oriOrderNo");
//		final String refundList = (String)param.get("refundList");
//		oriOrderMap.put("oriReturnMoney", 0L);
		if(param.get("applicationId")== null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数applicationId");
		try {
            //创建订单
			final List<Map<String,String>> refundOrderList = new ArrayList<Map<String,String>>();
			for(int i=0;i<refundOriArr.length();i++){
				JSONObject refundObject = refundOriArr.getJSONObject(i);
				String oriBizOrderNo = refundObject.getString("oriBizOrderNo");
				String bizOrderNo = refundObject.getString("bizOrderNo");
				Map<String,String> entityMap = new HashMap<String,String>();
				entityMap.put("order_state",            Constant.ORDER_STATE_WAIT_PAY.toString());
				Calendar calendar = Calendar.getInstance();
				entityMap.put("create_time", 			String.valueOf(calendar.getTime().getTime()));

				entityMap.put("trade_type", 			Constant.TRADE_TYPE_REFUNDMENT.toString());
				entityMap.put("order_state", 			Constant.ORDER_STATE_WAIT_PAY.toString());
				entityMap.put("order_type", 			Constant.ORDER_TYPE_BATCH_REFUNDMENT.toString());
				entityMap.put("sub_trade_type", 		Constant.SUB_TRADE_TYPE_BATCH_REFUND.toString());
				entityMap.put("refund_orderNo", 		oriBizOrderNo);//这里先赋值 原订单的业务系统编号，在下面替换掉
				entityMap.put("bizOrderNo",             bizOrderNo);
	            entityMap.put("description", 			"退款");
				entityMap.put("application_id",         param.get("applicationId").toString());
				entityMap.put("application_label",      param.get("applicationName").toString());
				entityMap.put("orgNo", 					param.get("orgNo").toString());
				entityMap.put("goodsType", 				goodsType.toString());
				entityMap.put("goodsNo", 				goodsNo);
				entityMap.put("goodsName", 				goodsName);
				if(refundObject.get("summary") != null){
					entityMap.put("summary", String.valueOf(refundObject.get("summary")));
				}
				if(refundObject.get("extendInfo") != null){
					entityMap.put("extend_info", String.valueOf(refundObject.get("extendInfo")));
	            }
				refundOrderList.add(entityMap);
				
			}
//            final List<Map<String, Object>> oriCommandlist = new ArrayList<Map<String, Object>>();
            
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            Map<String,Object> retMap =
	        EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
	        	@Override
	        	public boolean before(Session session) throws Exception {
	        		for(Map<String,String> entityMap : refundOrderList){
	        			Map<String,Object> orderEntity = getOrder(Long.parseLong(entityMap.get("application_id")),entityMap.get("bizOrderNo"),session);
						if(orderEntity != null){
							throw new Exception("退款订单号已经存在！");
						}
						String oriBizOrderNo = entityMap.get("refund_orderNo");
						Map<String, Object> oriOrderEntity = getOrder(Long.parseLong(entityMap.get("application_id")),oriBizOrderNo, session);
						if(oriOrderEntity == null)
							throw new Exception("原交易订单不存在");
						if(Constant.ORDER_STATE_CLOSE.equals((Long)oriOrderEntity.get("order_state")) 
								|| Constant.ORDER_STATE_WAIT_PAY.equals((Long)oriOrderEntity.get("order_state")) 
								){
							throw new Exception("原交易订单状态错误，无法退款");
						}
						if(!(Constant.ORDER_TYPE_SHOPPING.equals(oriOrderEntity.get("order_type"))||Constant.ORDER_TYPE_DAISHOU.equals(oriOrderEntity.get("order_type")))){
							throw new Exception("原交易订单不允许退款");
						}
						if(!entityMap.get("goodsNo").equals(oriOrderEntity.get("goodsNo"))){
							throw new Exception("商品编号不匹配");
						}
						String oriOrderNo = oriOrderEntity.get("orderNo").toString();
						entityMap.put("biz_trade_code",         (String)oriOrderEntity.get("biz_trade_code"));
						entityMap.put("refund_orderNo", 		oriOrderNo);//替换上面的订单号
						entityMap.put("order_money", 			oriOrderEntity.get("order_money").toString());
						entityMap.put("member_id",              oriOrderEntity.get("member_id").toString());
			            entityMap.put("member_name",            (String)oriOrderEntity.get("member_name"));
			            entityMap.put("member_uuid",            (String)oriOrderEntity.get("member_uuid"));
			            entityMap.put("memberNo",               (String)oriOrderEntity.get("memberNo"));
			            if(oriOrderEntity.get("target_member_id") != null) {
				            entityMap.put("target_member_id",       oriOrderEntity.get("target_member_id").toString());
				            entityMap.put("target_member_name",     (String)oriOrderEntity.get("target_member_name"));
				            entityMap.put("target_uuid",            (String)oriOrderEntity.get("target_uuid"));
				            entityMap.put("target_memberNo",        (String)oriOrderEntity.get("target_memberNo"));
			            }
//			            oriOrderMap.putAll(oriOrderEntity);
//			            List<Map<String, Object>> list = Order.getSuccessCommands(oriOrderNo, session);
//			            logger.info("list="+list);
//			            if(list != null) {
//			            	//把网关和its放在交易的最前面
//			        		for(Map<String, Object> temp : list) {
//			                	if(Constant.PAY_INTERFACE_GETWAY_JJ.equals((String)temp.get("pay_interfaceNo")) || Constant.PAY_INTERFACE_QUICK.equals((String)temp.get("pay_interfaceNo"))) {
//			                		oriCommandlist.add(temp);
//			                	}
//			        		}
//			        		for(Map<String, Object> temp : list) {
//			                	if(Constant.PAY_INTERFACE_AMS.equals((String)temp.get("pay_interfaceNo")) ) {
//			                		oriCommandlist.add(temp);
//			                	}
//			        		}
//			            }
			          //检测原订单是否进行过代付或者退款操作
//						Long oriReturnMoney = (Long) oriOrderMap.get("oriReturnMoney");
			            Query query = session.createQuery("from AMS_Order where refund_orderNo=:refund_orderNo");
		                query.setParameter("refund_orderNo", oriOrderNo);
		                List<Map<String, Object>> oldReturnOrderList = query.list();
		                if(!oldReturnOrderList.isEmpty()) {
		                	throw new Exception("订单已经进行过退款操作，不能在进行此操作");
		                }
		                //如果是代收订单，校验退款列表
		                if(Constant.ORDER_TYPE_DAISHOU.equals(oriOrderEntity.get("order_type"))){
		                	List<Map<String,Object>> agentDetailList = Order.getAgentDetailList((Long)oriOrderEntity.get("id"),session);
		                	for(Map<String,Object> agentDetail : agentDetailList){
		                		 if((Long)agentDetail.get("ys_amount")>0||(Long)agentDetail.get("tk_amount")>0){
		                			 throw new Exception("订单已经进行过退款或者代付操作，不能在进行此操作");
		                		 }
		           			}
		                }
	        		}
					return true;
	        	}
	        	
				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					Map<String,Object>  returnMap = new HashMap<String,Object>();
					for(Map<String,String> entityMap : refundOrderList){
						Map<String, Object> orderEntity = DynamicEntityService.createEntity("AMS_Order", entityMap, null);
		                String orderNo = Order.generateOrderNo((Long)orderEntity.get("id"));
		                Query query = session.createQuery("update AMS_Order set orderNo=:orderNo where id=:orderId");
		                query.setParameter("orderNo", orderNo);
		                query.setParameter("orderId", (Long)orderEntity.get("id"));
		                query.executeUpdate();
		                orderEntity.put("orderNo", orderNo);
		                createReturnCommands(orderEntity, session);
		                completeOrder(orderEntity, null, session);
		                Map<String,Object> ret = new HashMap<String,Object>();
						ret.put("orderNo", orderNo);
						returnMap.put((String)orderEntity.get("bizOrderNo"), ret);
					}
					return returnMap;
					
				}
	        });
            
			return retMap ;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	/***
	 * 
	* 退款订单
	* @param orderEntity
	* @param session
	* @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void createReturnCommands( Map<String,Object> orderEntity, Session session) throws Exception {
		logger.info("RefundOrder.createReturnCommands start");
		logger.info("orderEntity="+orderEntity);
		String oriOrderNo = (String)orderEntity.get("refund_orderNo");
		String orderNo = (String)orderEntity.get("orderNo");
		List<Map<String, Object>> oriCommandlist = Order.getSuccessCommands(oriOrderNo, session);
		Map<String,String> strMap = null;
		//退款到余额，不关心网关和快捷
		for(Map<String, Object> temp : oriCommandlist) {
			if(Constant.PAY_INTERFACE_GETWAY_JJ.equals((String)temp.get("pay_interfaceNo")) || Constant.PAY_INTERFACE_QUICK.equals((String)temp.get("pay_interfaceNo"))) {
				continue;
			}
        	strMap = new HashMap<String, String>();
			Set<String> keys = temp.keySet();
			for (String key : keys) {
				if(temp.get(key) == null) {
					continue;
				}
				if(key.equals("bizid")) {
					strMap.put(key, orderNo);
					continue;
				} else if(key.equals("$type$") || key.equals("id")) {
					continue;
				} else if(key.equals("remark")) {
					strMap.put("remark", "退款");
					continue;
				} else if(key.equals("trade_money")) {
					strMap.put(key, (Long)temp.get(key) + "");
					continue;
				}
				strMap.put(key, String.valueOf(temp.get(key)));
			}
			logger.info("strMap11111111111="+strMap);
            DynamicEntityService.createEntity("AMS_OrderPayDetail", strMap, null);
		}
	}
	
	//回调事件
	public void completePay(Map<String, Object> refundOrderEntity, Session session)
			throws Exception {
		//一次性操作完成，不进行实际操作
	}
	
	@Override
	public String completeOrder(Map<String, Object> order_entity, Map<String, Object> param, Session session)
			throws Exception {
		logger.info("RefundOrder.completeOrder start:"+order_entity);
		String orderNo = (String)order_entity.get("orderNo");
		String oriOrderNo = (String)order_entity.get("refund_orderNo");
		
		
		try {
			Map<String, Object> oriOrderEntity = Order.getOrder(oriOrderNo, session);
			Long feeAmount = (Long)oriOrderEntity.get("fee");
			logger.info("feeAmount:" + feeAmount);
			
			//手续费退款
			if(feeAmount != null && feeAmount > 0){
				refundFee(order_entity, feeAmount);
			}
			
			//撤销操作
            TradeCommandManager manager = TradeCommandManager.instance();
            manager.undoCommands(orderNo);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			//交易监测
			List<Map<String, Object>> commandList = getCommands((String)order_entity.get("orderNo"),session);
			for(Map<String, Object> command : commandList){
				TransMonitor.monitor("response" ,command, new HashMap<String, Object>());
			}
			throw e;
		}
		Map<String,Object> orderMap = Order.getOrder(orderNo, session);
		String refund_orderNo = (String)orderMap.get("refund_orderNo");
		Map<String,Object> oriOrderMap = Order.getOrder(refund_orderNo, session);
		if(Constant.ORDER_TYPE_DAISHOU.equals(oriOrderMap.get("order_type"))){
				List<Map<String,Object>> agentDetailList = Order.getAgentDetailList((Long)oriOrderMap.get("id"),session);
				for(Map<String,Object> detail:agentDetailList){
						if((Long)detail.get("tk_amount")>0 ||(Long)detail.get("ys_amount")>0){
							throw new Exception("订单已经进行过退款或者代付操作，不能在进行此操作");
						}
						Query query = session.createQuery("update AMS_AgentDetail set tk_amount=:tk_amount where id=:id");
						query.setParameter("tk_amount", (Long)detail.get("sk_amount"));
						query.setParameter("id", (Long)detail.get("id"));
						int rt = query.executeUpdate();
						if(rt!=1){
							throw new Exception("修改代收明细出错");
						}
				}
		}
		//修改订单完成
		return super.completeOrder(order_entity, null, session);
	}
	
	/**
	 * 手续费退款
	 * @param orderEntity
	 * @throws Exception
	 */
	private void refundFee(Map<String, Object> orderEntity, Long fee) throws Exception{
		try{
			Long applicationId = (Long)orderEntity.get("application_id");
			Long targetMemberId = (Long)orderEntity.get("target_member_id");
			Map<String, Object> applicationMemberEntity = Appliction.getApplicationMemberEntity(applicationId);
			Map<String, Object> targetMemberEntity = DynamicEntityService.getEntity(targetMemberId, "AMS_Member");
			
			AccountService accountService  = new AccountServiceImpl();
			Map<String, Object> sourceAccountTypeEntity =  accountService.getAccountTypeByNo(Constant.ACCOUNT_NO_STANDARD_BALANCE);
			String middleAccountNo = getAccountNoByTradeNo((String)orderEntity.get("biz_trade_code"));
			Map<String, Object> targetAccountTypeEntity = QueryService.getAccountType(middleAccountNo);

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("source_userId",      (String)applicationMemberEntity.get("userId"));
        	param.put("source_memberNo",    (String)applicationMemberEntity.get("memberNo"));
        	param.put("source_member_name", (String)applicationMemberEntity.get("name"));
            param.put("target_userId",      (String)targetMemberEntity.get("userId"));
            param.put("target_memberNo",    (String)targetMemberEntity.get("memberNo"));
            param.put("target_member_name", (String)targetMemberEntity.get("name"));
            
            param.put("account_type_id",    (Long)sourceAccountTypeEntity.get("id"));
            param.put("target_account_type_id",    (Long)targetAccountTypeEntity.get("id"));
            
            param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
            param.put("sub_trade_type",         Constant.SUB_TRADE_TYPE_FEE_REFUND);
            param.put("trade_money",        fee);
            param.put("bizid",              (String)orderEntity.get("orderNo"));
            param.put("remark",             (String)orderEntity.get("remark"));
            param.put("isMaster",           true);
           
            param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
            param.put("orgNo",    			(String)orderEntity.get("orgNo"));
            
            TradeService.customTransfer(param);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

}
