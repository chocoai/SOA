package bps.order;

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

import com.allinpay.commons.core.util.StringUtils;
import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.services.TradeService;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.service.AccountService;

public class RefundOrderNew extends Order {
	private static Logger logger = Logger.getLogger(RefundOrderNew.class.getClass());
	
	private Long amount_g;
	private Long couponAmount_g;
	private Long feeAmount_g;
	private String oriOrderNo_g;
	private String refundList_g;
	private String orgNo_g;
	private String bizOrderNo_g;
	private Long applicationId_g;
	private String applicationName_g;
	private Long applicationMemberId_g;
	private Map<String, Object> sourceMemberEntity_g;
	private Map<String, Object> targetMemberEntity_g;
	private Long oriOrderTargetAccountTypeId_g;
	
	
	@Override
	public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
		logger.info("param:" + param);
		
		try{
			//获取参数
			loadParams(param);
			
			//初步检测
			preCheck();
			
			final List<Map<String, Object>> oriCommandSortedList = new ArrayList();
			final Map<String, Object> extParams = new HashMap<>();
			final Map<String, Object> _refundOrderInfo = EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				@Override
	        	public boolean before(Session session) throws Exception {
					Map<String, Object> oriOrderEntity = getOrder(oriOrderNo_g, session);
					logger.info("oriOrderEntity:" + oriOrderEntity);
					extParams.put("oriOrderEntity", oriOrderEntity);
					
					//创建订单map
					sourceMemberEntity_g = DynamicEntityService.getEntity((Long)oriOrderEntity.get("member_id"), "AMS_Member");
					targetMemberEntity_g = DynamicEntityService.getEntity((Long)oriOrderEntity.get("target_member_id"), "AMS_Member");
					Map<String, String> orderMap = generateOrderMap();
					extParams.put("orderMap", orderMap);
					
					//获取已经退款的金额
					Long refundedAmount = 0L;
					Long couponRefundedAmount = 0L;
					Long feeRefundedAmount = 0L;
					String sqlStr = "from AMS_Order where refund_orderNo=:oriOrderNo and order_state=:orderState";
					Query query = session.createQuery(sqlStr);
					query.setParameter("oriOrderNo", oriOrderNo_g);
					query.setParameter("orderState", Constant.ORDER_STATE_SUCCESS);
					List<Map<String, Object>> refundedOrderList = query.list();
					for(Map<String, Object> refundedOrder : refundedOrderList){
						if(refundedOrder.get("order_money") != null){
							refundedAmount += (Long)refundedOrder.get("order_money");
						}
						if(refundedOrder.get("coupon_refund_amount") != null){
							couponRefundedAmount += (Long)refundedOrder.get("coupon_refund_amount");
						}
						if(refundedOrder.get("fee_refund_amount") != null){
							feeRefundedAmount += (Long)refundedOrder.get("fee_refund_amount");
						}
					}
					
					extParams.put("refundedAmount", refundedAmount);
					extParams.put("couponRefundedAmount", couponRefundedAmount);
					extParams.put("feeRefundedAmount", feeRefundedAmount);
					
					//网关，its，余额的指令排序，并获取代金券指令
					List<Map<String, Object>> oriCommandList = Order.getSuccessCommands(oriOrderNo_g, session);
					extParams.put("oriCommandList", oriCommandList);
					if(oriCommandList != null){
						for(Map<String, Object> oriCommand : oriCommandList){
							//把网关或者its的指令放在最前面
							if(Constant.PAY_INTERFACE_GETWAY_JJ.equals((String)oriCommand.get("pay_interfaceNo")) || Constant.PAY_INTERFACE_QUICK.equals((String)oriCommand.get("pay_interfaceNo"))) {
								oriCommandSortedList.add(oriCommand);
							}
						}
						
						for(Map<String, Object> oriCommand : oriCommandList){
							if(Constant.PAY_INTERFACE_AMS.equals((String)oriCommand.get("pay_interfaceNo")) ) {
		                		//代金券
								if(Constant.TRADE_TYPE_DEPOSIT.equals(oriCommand.get("trade_type")) && Constant.SUB_TRADE_TYPE_MARKETING_ACTIVITY.equals(oriCommand.get("sub_trade_type"))){
									extParams.put("oriCouponCommand", oriCommand);
								}else{  //余额
									oriCommandSortedList.add(oriCommand);
									
									oriOrderTargetAccountTypeId_g = (Long)oriCommand.get("target_account_type_id");
								}
		                	}
						}
					}
					
					extParams.put("oriCommandSortedList", oriCommandSortedList);
					
					//检测
					check(oriOrderEntity, amount_g, refundedAmount, extParams, session);
					
					return true;
				}
				
				@Override
				public Map<String, Object> doTransaction(Session session,
						Transaction trans) throws Exception {
					if(!LoginSession.isLogined())
					    LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
					
					//创建订单
					Map<String, String> orderMap = (Map<String, String>)extParams.get("orderMap");
					Map<String, Object> orderEntity = DynamicEntityService.createEntity("AMS_Order", orderMap, null);
					extParams.put("orderEntity", orderEntity);
					String orderNo = Order.generateOrderNo((Long)orderEntity.get("id"));
	                Query query = session.createQuery("update AMS_Order set orderNo=:orderNo where id=:orderId");
	                query.setParameter("orderNo", orderNo);
	                query.setParameter("orderId", (Long)orderEntity.get("id"));
	                query.executeUpdate();
	                orderEntity.put("orderNo", orderNo);
	                
	                extParams.put("orderNo", orderNo);
					
					//创建退款指令
	                Map<String, Object> oriOrderEntity = (Map<String, Object>)extParams.get("oriOrderEntity");
	                List<Map<String, Object>> oriCommandList = (List<Map<String, Object>>)extParams.get("oriCommandList");
	                Long refundedAmount = (Long)extParams.get("refundedAmount");
	                createCommands(oriOrderEntity, oriCommandList, amount_g, refundedAmount, extParams, session);

					Map<String, Object> refundOrderInfo = new HashMap();
					refundOrderInfo.put("orderEntity", orderEntity);
					refundOrderInfo.put("orderNo", orderNo);

					return refundOrderInfo;
				}
			});

			//触发指令执行，订单完成

			return EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doTransaction(Session session, Transaction transaction) throws Exception {
					Map<String, Object> orderEntity = (Map<String, Object>)_refundOrderInfo.get("orderEntity");
					String orderNo = (String)_refundOrderInfo.get("orderNo");

					//完成订单
					completeOrder(orderEntity, null, session);
					Map<String,Object> ret = new HashMap<>();
					ret.put("orderNo", orderNo);
					return ret;
				}
			});
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 加载参数
	 * @param params
	 * 		money				金额
	 * 		couponAmount		代金
	 * 		feeAmount			手续费
	 * 		oriOrderNo
	 * 		refundList
	 * 		orgNo				应用号
	 * 		bizOrderNo			业务订单号
	 * 		applicationId		应用ID
	 * 		applicationName		应用名称
	 * 		applicationMemberId	应用会员
	 */
	private void loadParams(Map<String, Object> params){
		amount_g = (Long)params.get("money");
		couponAmount_g = params.get("couponAmount") == null ? 0L : (Long)params.get("couponAmount");
		feeAmount_g = params.get("feeAmount") == null ? 0L : (Long)params.get("feeAmount");
		oriOrderNo_g = (String)params.get("oriOrderNo");
		refundList_g = (String)params.get("refundList");
		orgNo_g = (String)params.get("orgNo");
		bizOrderNo_g = (String)params.get("bizOrderNo");
		applicationId_g = (Long)params.get("applicationId");
		applicationName_g = (String)params.get("applicationName");
		applicationMemberId_g = (Long)params.get("applicationMemberId");
	}
	
	/**
	 * 初步检测
	 * @throws BizException
	 */
	private void preCheck() throws BizException{
		try{
			if(amount_g <= 0){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "退款总金额错误。");
			}
			if(couponAmount_g < 0){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "代金券退款金额错误。");
			}
			if(feeAmount_g < 0){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "手续费退款金额错误。");
			}
			if(StringUtils.isBlank(oriOrderNo_g)){
				throw new BizException(ErrorCode.OTHER_ERROR, "商户原订单号不存在。");
			}
			if(StringUtils.isBlank(orgNo_g)){
				throw new BizException(ErrorCode.OTHER_ERROR, "商户应用号不存在。");
			}
			if(StringUtils.isBlank(bizOrderNo_g)){
				throw new BizException(ErrorCode.OTHER_ERROR, "商户订单号为空。");
			}
			if(applicationId_g == null){
				throw new BizException(ErrorCode.OTHER_ERROR, "商户应用不存在。");
			}
			if(applicationMemberId_g == null){
				throw new BizException(ErrorCode.OTHER_ERROR, "商户应用不存在。");
			}
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 检测
	 * @param oriOrderEntity   	原订单实体
	 * @param amount           	本次退款总金额
	 * @param refundedAmount   	已退款金额
	 * @param extParams			其他参数
	 *                   feeRefundedAmount
	 *                   feeRefundedAmount
	 * @param session			session
	 * @throws BizException
	 */
	private void check(Map<String, Object> oriOrderEntity, Long amount, Long refundedAmount, Map<String, Object> extParams, Session session) throws BizException{
		try{
			//检查退款订单号是否存在
			Map<String,Object> orderEntity = getOrder(applicationId_g, bizOrderNo_g, session);
			if(orderEntity != null){
				throw new BizException(ErrorCode.ORDER_EXSIT, "退款订单已经存在。");
			}
			
			//检查源订单号是否存在
			if(oriOrderEntity == null){
				throw new BizException(ErrorCode.ORDER_NOTEXSIT, "原订单不存在。");
			}
			
			//只有消费订单和代收订单才允许退款
			Long oriOrderType = (Long)oriOrderEntity.get("order_type");
			if(!Constant.ORDER_TYPE_SHOPPING.equals(oriOrderType) && !Constant.ORDER_TYPE_DAISHOU.equals(oriOrderType)){
				throw new BizException(ErrorCode.ORDER_TYPE_ERROR, "原订单不支持退款。");
			}

			//检查源订单状态
			Long oriOrderState = (Long)oriOrderEntity.get("order_state");
			if(!Constant.ORDER_STATE_SUCCESS.equals(oriOrderState)){
				throw new BizException(ErrorCode.ORDER_STATE_ERROR, "订单状态错误。");
			}
			
			//检测本次退款的代金券金额和手续费金额是否大于总退款金额
			if(couponAmount_g > amount || feeAmount_g > amount){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "金额错误。");
			}
			
			//检测退款金额是否大于源订单金额
			Long oriAmount = (Long)oriOrderEntity.get("order_money");
			if(amount + refundedAmount > oriAmount){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "已退款金额大于原订单支付金额。");
			}
			
			//检测代金券金额
			Long couponRefundedAmount = (Long)extParams.get("couponRefundedAmount");
			Map<String, Object> oriCouponCommand = (Map<String, Object>)extParams.get("oriCouponCommand");
			Long oriCouponAmount = oriCouponCommand == null ? 0L : (Long)oriCouponCommand.get("trade_money");
				//代金券退款金额超出可退金额。
			if(couponAmount_g + couponRefundedAmount > oriCouponAmount){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "代金券退款金额超出可退金额。");
			}
				//经过本次退款后，未退代金券金额大于未退总金额
			if((oriCouponAmount - couponRefundedAmount - couponAmount_g) > (oriAmount - refundedAmount - amount)){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "代金券退款金额错误。");
			}
		 
			//检测手续费金额
			Long oriFee = (Long)oriOrderEntity.get("fee");
			Long feeRefundedAmount = (Long)extParams.get("feeRefundedAmount");
				//手续费退款金额超出可退金额
			if(feeAmount_g + feeRefundedAmount > oriFee){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "手续费退款金额超出可退金额。");
			}
			
				//经过本次退款后，未退手续费金额大于未退总金额
			if((oriFee - feeRefundedAmount - feeAmount_g) > (oriAmount - refundedAmount - amount)){
				throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "手续费退款金额错误。");
			}
			
			//如果是代收订单，检测退款列表
			if(Constant.ORDER_TYPE_DAISHOU.equals(oriOrderEntity.get("order_type"))){
            	JSONArray refundArr = null;
            	try{
            		refundArr = new JSONArray(refundList_g);
            	}catch(Exception e){
            		throw new Exception("退款人列表格式出错");
            	}
            	List<Map<String,Object>> agentDetailList = Order.getAgentDetailList((Long)oriOrderEntity.get("id"),session);
            	long all_refund_amount = 0L;
            	for(int i=0;i<refundArr.length();i++){
            		JSONObject refund = refundArr.getJSONObject(i);
            		String bizUserId = refund.getString("bizUserId");
            		Long refund_amount = refund.getLong("amount");
            		long sx_amount = 0L;
            		for(Map<String,Object> agentDetail : agentDetailList){
           				 if(agentDetail.get("bizUserId").equals(bizUserId)){
           					 Long sx = (Long)agentDetail.get("sk_amount") - (Long)agentDetail.get("ys_amount") -(Long)agentDetail.get("tk_amount");
           					 sx_amount += sx;
           				 }
           			}
            		if(sx_amount < refund_amount){
           				 throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "代收订单"+oriOrderEntity.get("bizOrderNo")+"中退款金额不足");
           			}
            		all_refund_amount += refund_amount;
            	}
            	if(all_refund_amount + feeAmount_g != amount){
            		throw new BizException(ErrorCode.ORDER_AMOUNT_ERROR, "退款金额与付款人列表中的退款金额不符。");
            	}
            }
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 产生订单map
	 * @return	订单实体
	 * @throws Exception
	 */
	private Map<String, String> generateOrderMap() throws Exception{
		Map<String, String> orderMap = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		
		orderMap.put("member_id",              sourceMemberEntity_g.get("id").toString());
		orderMap.put("member_name",            sourceMemberEntity_g.get("name") == null ? "" : sourceMemberEntity_g.get("name").toString());
		orderMap.put("member_uuid",            sourceMemberEntity_g.get("userId").toString());
		orderMap.put("memberNo",               (String)sourceMemberEntity_g.get("memberNo"));
        
		orderMap.put("target_member_id",       targetMemberEntity_g.get("id").toString());
		orderMap.put("target_member_name",     targetMemberEntity_g.get("name") == null ? "" : targetMemberEntity_g.get("name").toString());
		orderMap.put("target_uuid",            targetMemberEntity_g.get("userId").toString());
		orderMap.put("target_memberNo",        (String)targetMemberEntity_g.get("memberNo"));
		
		orderMap.put("order_money", 			amount_g.toString());
		orderMap.put("coupon_refund_amount", 	couponAmount_g.toString());
		orderMap.put("fee_refund_amount", 		feeAmount_g.toString());
		
		orderMap.put("order_state",             Constant.ORDER_STATE_WAIT_PAY.toString());
		orderMap.put("create_time", 			String.valueOf(calendar.getTime().getTime()));
		orderMap.put("trade_type", 			    Constant.TRADE_TYPE_REFUNDMENT.toString());
		orderMap.put("order_type", 			    Constant.ORDER_TYPE_REFUNDMENT.toString());
		orderMap.put("orgNo", 					orgNo_g);
		orderMap.put("refund_orderNo", 		    oriOrderNo_g);
		orderMap.put("bizOrderNo",              bizOrderNo_g);
		orderMap.put("description", 			"退款");
		orderMap.put("application_id",          applicationId_g.toString());
		orderMap.put("application_label",       applicationName_g);
        if(refundList_g!=null && !"".equals(refundList_g)){
        	orderMap.put("refundList", refundList_g);
        }
        
        return orderMap;
	}
	
	/**
	 * 产生退款指令
	 * @param oriOrderEntity	订单实体
	 * @param oriCommandList	指令实体
	 * @param amount			金额
	 * @param refundedAmount	退款金额
	 * @param extParams			其他参数
	 *                  orderNo
	 *                  couponRefundedAmount
	 *                  oriCouponCommand
	 *					oriCommandSortedList
	 * @param session			session
	 * @throws BizException
	 */
	private void createCommands(Map<String, Object> oriOrderEntity, List<Map<String, Object>> oriCommandList, Long amount, Long refundedAmount, Map<String, Object> extParams, Session session) throws BizException{
		try{
			Long oriAmount = (Long)oriOrderEntity.get("order_money");
			String orderNo = (String)extParams.get("orderNo");
			Map<String, Object> oriCouponCommand = (Map<String, Object>)extParams.get("oriCouponCommand");
			Long couponRefundedAmount = (Long)extParams.get("couponRefundedAmount");
			//Long couponAmount = (Long)extParams.get("couponAmount");
			List<Map<String, Object>> oriCommandSortedList = (List<Map<String, Object>>)extParams.get("oriCommandSortedList");
			
//			Map<Long,Long> itsOrWgAmountMap = new HashMap<Long,Long>();//its或者网关支付的金额
			Map<Long,Long> accountAmountMap = new HashMap<Long,Long>();//内部退款金额
			
			//全额退款
			logger.info("aa");
			logger.info("amount=" + amount);
			logger.info("oriAmount=" + oriAmount);

			if(amount.equals(oriAmount)){
				for(Map<String, Object> oriCommand : oriCommandList){

					//去除手续费指令
					if(oriCommand.get("sub_trade_type") != null && ((Long)oriCommand.get("sub_trade_type")).equals(Constant.SUB_TRADE_TYPE_FEE)) {
						continue;
					}
					
					createCommand(oriCommand, orderNo, (Long)oriCommand.get("trade_money"), "退款");

					logger.info("oriCommand=" + oriCommand);
				}
			}
			//部分退款，先退代金券
			else{
				//已退款金额减去代金券已退款金额
				refundedAmount -= couponRefundedAmount;
				
				//先退代金券金额
				if(couponAmount_g > 0){
					Long accountTypeId = (Long)oriCouponCommand.get("target_account_type_id");
					if(accountAmountMap.get(accountTypeId) == null){
						accountAmountMap.put(accountTypeId, couponAmount_g);
					}else{
						accountAmountMap.put(accountTypeId, couponAmount_g + accountAmountMap.get(accountTypeId));
					}
					
					createCommand(oriCouponCommand, orderNo, couponAmount_g, "退款");
				}
				
				//已退款金额-已退款代金券-本次退款代金券
//				refundedAmount -= couponAmount_g;
				amount -= couponAmount_g;

				//退款金额大于0，继续退款
				if(amount > 0){
					for(Map<String, Object> oriCommandSort : oriCommandSortedList){
//						//its和网关的消费的金额
//						if(oriOrderEntity.get("member_uuid").equals(oriCommandSort.get("target_userId"))) {
//							Long accountTypeId = (Long)oriCommandSort.get("target_account_type_id");
//							if(itsOrWgAmountMap.get(accountTypeId) == null){
//								itsOrWgAmountMap.put(accountTypeId, (Long)oriCommandSort.get("trade_money"));
//							}else{
//								itsOrWgAmountMap.put(accountTypeId, itsOrWgAmountMap.get(accountTypeId) + (Long)oriCommandSort.get("trade_money"));
//							}
//						}
						
						Long commandAmount = (Long)oriCommandSort.get("trade_money");
						
						//已退款金额小于当前指令金额，说明此指令还能继续退款
						if(refundedAmount < commandAmount){
							commandAmount -= refundedAmount;
							
							//当前指令金额大于退款金额，就从此指令中退款
							if(commandAmount >= amount){
								//是its或者网关的金额
								if(!Constant.PAY_INTERFACE_AMS.equals((String)oriCommandSort.get("pay_interfaceNo"))) {
									Long accountTypeId = (Long)oriCommandSort.get("target_account_type_id");
									if(accountAmountMap.get(accountTypeId) == null){
										accountAmountMap.put(accountTypeId, amount);
									}else{
										accountAmountMap.put(accountTypeId, amount + accountAmountMap.get(accountTypeId));
									}
									
									createCommand(oriCommandSort, orderNo, amount, "退款");
								}else{
									Long accountTypeId = (Long)oriCommandSort.get("account_type_id");
									if(accountAmountMap.get(accountTypeId)==null){
										accountAmountMap.put(accountTypeId, amount);
									}else{
										accountAmountMap.put(accountTypeId, amount + accountAmountMap.get(accountTypeId));
									}
								}
								
								break;
							}
							//当前指令金额小于退款金额，就先把此指令全部退款，再去下面的指令中退款
							else{
								//是its或者网关的金额
								if(!Constant.PAY_INTERFACE_AMS.equals((String)oriCommandSort.get("pay_interfaceNo"))) {
									Long accountTypeId = (Long)oriCommandSort.get("target_account_type_id");
									if(accountAmountMap.get(accountTypeId) == null){
										accountAmountMap.put(accountTypeId, commandAmount);
									}else{
										accountAmountMap.put(accountTypeId, commandAmount + accountAmountMap.get(accountTypeId));
									}
									
									createCommand(oriCommandSort, orderNo, commandAmount, "退款");
								}else{
									Long accountTypeId = (Long)oriCommandSort.get("account_type_id");
									if(accountAmountMap.get(accountTypeId)==null){
										accountAmountMap.put(accountTypeId, commandAmount);
									}else{
										accountAmountMap.put(accountTypeId, commandAmount + accountAmountMap.get(accountTypeId));
									}
								}
								
								amount -= commandAmount;
								refundedAmount = 0L;
							}
						}
						//已退款金额大于当前金额，说明此指令已经退款
						else{
							refundedAmount -= commandAmount;
						}
					}
				}
			}
			
			for(Long accountTypeId : accountAmountMap.keySet()){
				Long accountAmount = accountAmountMap.get(accountTypeId);
				
				if(accountAmount > 0){
					for(Map<String, Object> oriCommand : oriCommandList){
						if(Constant.SUB_TRADE_TYPE_FEE.equals((Long)oriCommand.get("sub_trade_type"))){
							continue;
						}
						
						if(Constant.PAY_INTERFACE_AMS.equals((String)oriCommand.get("pay_interfaceNo")) && accountTypeId.equals(oriCommand.get("account_type_id"))) {
							createCommand(oriCommand, orderNo, accountAmount, "退款");
							
							break;
						}
					}
				}
			}
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 保存退款指令
	 * @param commandEntity			指令
	 * @param orderNo				订单号
	 * @param commandRefundAmount	指令退款金额
	 * @param remark				备注
	 * @throws Exception
	 */
	private void createCommand(Map<String, Object> commandEntity, String orderNo, Long commandRefundAmount, String remark) throws Exception{
		try{
			Map<String, String> refundCommandMap = new HashMap<>();
			Set<String> keys = commandEntity.keySet();
			for (String key : keys) {
				if(commandEntity.get(key) == null) {
					continue;
				}
				if(key.equals("bizid")) {
					refundCommandMap.put(key, orderNo);
					continue;
				} else if(key.equals("trade_money")) {
					refundCommandMap.put(key, commandRefundAmount.toString());
					continue;
				} else if(key.equals("$type$") || key.equals("id")) {
					continue;
				} else if(key.equals("remark")) {
					refundCommandMap.put("remark", remark);
					continue;
				}

				refundCommandMap.put(key, String.valueOf(commandEntity.get(key)));
			}

			refundCommandMap.put("is_refund", "true"); //设置为退款指令
            DynamicEntityService.createEntity("AMS_OrderPayDetail", refundCommandMap, null);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
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
			try {
				//如果有手续费退款，则进行自定义转账，从平台余额账户转账到用户现金
				if(feeAmount_g > 0){
					refundFee(order_entity);
				}
				
				//撤销操作
	            TradeCommandManager manager = TradeCommandManager.instance();
	            manager.undoCommands(orderNo);
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			
			Map<String,Object> orderMap = Order.getOrder(orderNo, session);
			String refund_orderNo = (String)orderMap.get("refund_orderNo");
			Map<String,Object> oriOrderMap = Order.getOrder(refund_orderNo, session);
			if(Constant.ORDER_TYPE_DAISHOU.equals(oriOrderMap.get("order_type"))){
				String refundList = (String)orderMap.get("refundList");
				JSONArray jsonArray = new JSONArray(refundList);
				for(int i=0;i<jsonArray.length();i++){
					JSONObject refund = jsonArray.getJSONObject(i);
					String bizUserId = refund.getString("bizUserId");
					Long amount = refund.getLong("amount");
					List<Map<String,Object>> agentDetailList = Order.getAgentDetailList((Long)oriOrderMap.get("id"),session);
					for(Map<String,Object> detail:agentDetailList){
						if(detail.get("bizUserId").equals(bizUserId)){
							Query query = session.createQuery("update AMS_AgentDetail set tk_amount=:tk_amount where id=:id");
							Long sy = (Long)detail.get("sk_amount")-(Long)detail.get("ys_amount")-(Long)detail.get("tk_amount");
							if(sy.longValue()<amount){
								query.setParameter("tk_amount", (Long)detail.get("tk_amount") + sy);
								amount = amount - sy;
							}else{
								query.setParameter("tk_amount", (Long)detail.get("tk_amount") + amount);
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
			
			//修改订单完成
			return super.completeOrder(order_entity, null, session);
		}
		
		/**
		 * 手续费退款
		 * @param orderEntity	订单
		 * @throws Exception
		 */
		private void refundFee(Map<String, Object> orderEntity) throws Exception{
			try{
				Map<String, Object> applicationMemberEntity = DynamicEntityService.getEntity(applicationMemberId_g, "AMS_Member");
				AccountService accountService  = new AccountServiceImpl();
				Map<String, Object> sourceAccountTypeEntity =  accountService.getAccountTypeByNo(Constant.ACCOUNT_NO_STANDARD_BALANCE);
						
				Map<String, Object> param = new HashMap<>();
				param.put("source_userId",      (String)applicationMemberEntity.get("userId"));
	        	param.put("source_memberNo",    (String)applicationMemberEntity.get("memberNo"));
	        	param.put("source_member_name", (String)applicationMemberEntity.get("name"));
	            param.put("target_userId",      (String)targetMemberEntity_g.get("userId"));
	            param.put("target_memberNo",    (String)targetMemberEntity_g.get("memberNo"));
	            param.put("target_member_name", (String)targetMemberEntity_g.get("name"));
	            
	            param.put("account_type_id",    (Long)sourceAccountTypeEntity.get("id"));
	            param.put("target_account_type_id",    oriOrderTargetAccountTypeId_g);
	            
	            param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
	            param.put("sub_trade_type",         Constant.SUB_TRADE_TYPE_FEE_REFUND);
	            param.put("trade_money",        feeAmount_g);
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
