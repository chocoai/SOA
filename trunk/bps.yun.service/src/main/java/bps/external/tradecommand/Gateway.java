package bps.external.tradecommand;

import ime.core.event.Event;
import ime.core.event.EventManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.Constant;
import bps.order.Order;
import bps.order.OrderFactory;
import bps.order.OrderServiceImpl;
import bps.process.Extension;
import bps.process.PayChannelManage;

import com.kinorsoft.ams.ITradeCommand;
import com.kinorsoft.ams.services.TradeService;

public class Gateway implements ITradeCommand{
	private static Logger logger = Logger.getLogger(Gateway.class);
	private final static String payInterfaceNo = Constant.PAY_INTERFACE_GETWAY_JJ;
	private final static String version = "v1.3";
	private final static Long payStateSuccess = com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS;
	private final static Long refundStatusNotDo = Constant.REFUND_STATE_NODO;

	//String转换为map
	//String格式为：a=1&b=2
	public static Map<String, String> convert(String str){
		String[] strSplit = str.split("&");
		Map<String, String> retMap = new HashMap<>();
		for(String temp:strSplit){
			String[] tempSplit = temp.split("=");
			retMap.put(tempSplit[0], tempSplit[1]);
		}
		
		return retMap;
	}

	public Map<String, Object> doCommand(Map<String, Object> arg0)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> doCommands(List<Map<String, Object>> arg0)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPayInterfaceNo() {
		return payInterfaceNo;
	}

	public Map<String, Object> undoCommand(Map<String, Object> commandInfo)
			throws Exception {
		//判断数据
		if(!payStateSuccess.equals((Long)commandInfo.get("pay_state"))){
			logger.error("支付状态不为支付成功。");
			throw new Exception("支付状态不为支付成功。");
		}
		if(!refundStatusNotDo.equals((Long)commandInfo.get("refund_status"))){
			logger.error("退款状态不为未退款。");
			throw new Exception("退款状态不为未退款。");
		}
		if(!payInterfaceNo.equals(commandInfo.get("pay_interfaceNo"))){
			logger.error("支付通道编号错误。");
			throw new Exception("支付通道编号错误。");
		}

		String orderDatetime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
		Map<String, Object> orderEntity = orderServiceImpl.getOrder(commandInfo.get("bizid").toString());
		String sysid = (String) orderEntity.get("orgNo");
		String refundOrderNo = (String) orderEntity.get("refund_orderNo");
		Map<String, Object> refundOrderEntity = orderServiceImpl.getOrder(refundOrderNo);
		orderDatetime = sdf.format((Date)refundOrderEntity.get("create_time"));
		
		//自定义转账  TradeService.customTransfer(trade_param);
		//交易日志
		Map<String, Object> trade_param = new HashMap<String, Object>();

		trade_param.put("source_userId", 		commandInfo.get("target_userId"));
		trade_param.put("source_memberNo", 		commandInfo.get("target_memberNo"));
		trade_param.put("source_member_name", 	commandInfo.get("target_member_name"));
		trade_param.put("account_type_id", 		commandInfo.get("target_account_type_id"));
		trade_param.put("target_userId", 		commandInfo.get("source_userId"));
		trade_param.put("target_memberNo", 		commandInfo.get("source_memberNo"));
		trade_param.put("target_member_name", 	commandInfo.get("source_member_name"));
		trade_param.put("target_account_type_id", 	commandInfo.get("target_account_type_id"));
		trade_param.put("trade_money", 			commandInfo.get("trade_money"));
		trade_param.put("out_trade_id", 		commandInfo.get("out_trade_id"));
		trade_param.put("out_bizno", 			commandInfo.get("accountNo"));
		trade_param.put("bizid", 				commandInfo.get("bizid"));
		trade_param.put("command_no",           commandInfo.get("command_no"));
		trade_param.put("orgNo", 				commandInfo.get("orgNo"));
		trade_param.put("pay_channelNo", 		Constant.PAY_CHANNEL_GETWAY );
		trade_param.put("trade_type", 			Constant.TRADE_TYPE_REFUNDMENT);
		trade_param.put("trade_time", 			new Date());
		trade_param.put("pay_interfaceNo", 		Constant.PAY_INTERFACE_CERT_PAY);
		trade_param.put("isMaster", 		    false);

		final Map<String, Object> param = new HashMap<String, Object>();
		param.put("refundStatus", Constant.REFUND_STATE_DOING);
		param.put("orderNo", (String)commandInfo.get("bizid"));
		param.put("id", (Long)commandInfo.get("id"));
		try{
			TradeService.customTransfer(trade_param);
			EntityManagerUtil.execute(new TransactionWork<String>() {
				@Override
				public String doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_OrderPayDetail set refund_status=:refundStatus where id=:id");
					query.setParameter("refundStatus", (Long)param.get("refundStatus"));
					query.setParameter("id", (Long)param.get("id"));
					
					query.executeUpdate();
					return null;
				}
	        });
		}
		catch(Exception e){
			logger.error("自定义转账时发生异常：" + e.getMessage());
			throw e;
		}
		
		//调用接口
		String refundResponse;
		try{
			refundResponse = Extension.gatewayService.gatewayRefund(version, commandInfo.get("command_no").toString(), commandInfo.get("trade_money").toString(), orderDatetime, "", Constant.PAY_INTERFACE_GETWAY_JJ);
		}
		catch(Exception e){
			logger.error("调用网关退款时发生异常：" + e.getMessage());
			throw e;
		}
		
		logger.info("网关退款response：" + refundResponse);
		
		Map<String, String> responseMap = convert(refundResponse);

		logger.info("responseMap=" + responseMap);

		//检错
		String errorCode = null;
		Long refundStatus = Constant.REFUND_STATE_SUCCESS;
		
		String refundResultCode = responseMap.get("refundResult");
		if(!"20".equals(refundResultCode)){
			refundStatus = Constant.REFUND_STATE_FAIL;
			errorCode = responseMap.get("errorCode").toString();
		}
		param.put("refundStatus", refundStatus);
		
		EntityManagerUtil.execute(new TransactionWork<String>() {

			@Override
			public String doTransaction(Session session, Transaction tx)
					throws Exception {
				Query query = session.createQuery("update AMS_OrderPayDetail set refund_status=:refundStatus where id=:id");
				query.setParameter("refundStatus", (Long)param.get("refundStatus"));
				query.setParameter("id", (Long)param.get("id"));
				
				query.executeUpdate();
				
				List list = Order.getSuccessReturnCommands((String)param.get("orderNo"), session);
				List list2 = Order.getSuccessCommands((String)param.get("orderNo"), session);
				
				if(list != null && list.size() == list2.size()) {
					Map<String, Object> order_entity = Order.getOrder((String)param.get("orderNo"), session);
					Order order = OrderFactory.getOrder((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"));
					if(Constant.TRADE_TYPE_REFUNDMENT.equals(order_entity.get("trade_type"))) {
						logger.info("触发订单支付完成事件");
    					//触发订单支付完成事件
    					Map<String, Object> param2 = new HashMap<String, Object>();
    					param2.put("orderNo", (String)param.get("orderNo"));
    					Event event = new Event(com.kinorsoft.ams.Constant.EVENT_TYPE_ORDERCOMPLETEPAY, param2, null);
    					EventManager.instance().fireEvent(event);
    					
    					logger.info("触发订单完成支付事件");
					} else {
						order.closeOrder((String)param.get("orderNo"), null, session);
					}
				}
				return null;
			}
        });
		
		//返回 retuenValue.put("command_result", 	CommandResult.FailStop);
		Map<String, Object> retuenValue = new HashMap<String, Object>();
		if(refundStatus.equals(Constant.REFUND_STATE_SUCCESS)){
            logger.info("网关退款成功");
			retuenValue.put("command_result", CommandResult.Success);
		}
		else{
			logger.info("网关退款失败code：" + refundResultCode);
			retuenValue.put("command_result", 	CommandResult.FailStop);
		}
		retuenValue.put("retCode", errorCode);
		retuenValue.put("refundResult", refundResultCode);
		
		return retuenValue;
	}

	public Map<String, Object> undoCommands(List<Map<String, Object>> arg0)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> calculateFees(Map<String, Object> param) throws Exception {
		logger.info("计算手续费");
	    logger.info("param=" + param);
	    String pay_interfaceNo = (String) param.get("pay_interfaceNo");
	    Long money = (Long) param.get("trade_money");
	    
	    
	    Map<String, Object> result = new HashMap<String, Object>();
	    Long fee_money = 0L;
		JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfoPure(pay_interfaceNo);
	    logger.info("payInterface_entity=" + payInterface_entity);
		if(payInterface_entity != null) {
			Long handling_type = null;
			if(payInterface_entity.get("handling_type") != null)
				handling_type = payInterface_entity.optLong("handling_type");
			Long handling_mode = null;
			if(payInterface_entity.get("handling_mode") != null)
		        handling_mode = payInterface_entity.optLong("handling_mode");
			Double handling_rate = null;
			if(payInterface_entity.get("handling_rate") != null)
				handling_rate = payInterface_entity.optDouble("handling_rate");
			if(handling_rate == null) {
				handling_rate = 0D;
			}
			Long handling_each = null;
			if(payInterface_entity.get("handling_each") != null)
				handling_each = payInterface_entity.optLong("handling_each");
			logger.info("handling_each="+handling_each);
			if(handling_each == null) {
				handling_each = 0L;
			}
	        logger.info("handling_each="+handling_each);
			if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_PERCENTAGE)) {
		        logger.info("111111111=");
			    fee_money = Math.round(money * handling_rate);
			} else if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_EACH)) {
		        logger.info("222222222=");
			    fee_money = handling_each;
			}
	        result.put("handling_mode", handling_mode);
		}
	    result.put("fee_money", fee_money);
	    logger.info("result=" + result);
	    return result;
	}
}
