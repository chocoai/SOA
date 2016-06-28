package bps.external.tradecommand;

import ime.core.Environment;
import ime.core.event.Event;
import ime.core.event.EventManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.Constant;
import bps.order.Order;
import bps.order.OrderFactory;
import bps.process.Extension;
import bps.process.PayChannelManage;

import com.kinorsoft.ams.ITradeCommand;
import com.kinorsoft.ams.services.TradeService;

public class ItsTradeCommand implements ITradeCommand {
	private static Logger logger    = Logger.getLogger(ItsTradeCommand.class);
	private static String accessId  = null;
	private static String memberUrl = null;
	private static String KEY 		= null;
	private final static String payInterfaceNo = Constant.PAY_INTERFACE_QUICK;
	private final static Long payStateSuccess = com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS;
	private final static Long refundStatusNotDo = Constant.REFUND_STATE_NODO;
	
	static
	{
		Environment environment = null;
		try {
			environment = Environment.instance();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		accessId	= environment.getSystemProperty("its.access_id");
		KEY			= environment.getSystemProperty("its.key");
		memberUrl 	= environment.getSystemProperty("its.serverUrl");
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
		// TODO Auto-generated method stub
		return payInterfaceNo;
	}

	public Map<String, Object> undoCommand(Map<String, Object> orderInfo)
			throws Exception {
		logger.info("-------------------ItsTradeCommand.undoCommand start--------------------");
		
		//检查支付状态是否为成功
		if(!payStateSuccess.equals((Long)orderInfo.get("pay_state"))){
			logger.error("支付状态不为支付成功。");
			throw new Exception("支付状态不为支付成功。");
		}
		//检查退款状态是否为未退款
		if(!refundStatusNotDo.equals((Long)orderInfo.get("refund_status"))){
			logger.error("退款状态不为未退款。");
			throw new Exception("退款状态不为未退款。");
		}
		//检查支付通道是否为its
		if(!payInterfaceNo.equals(orderInfo.get("pay_interfaceNo"))){
			logger.error("支付通道编号错误。");
			throw new Exception("支付通道编号错误。");
		}
		
		//获取its原始签约记录
		Map<String, Object> itsApplyPayLog = ItsManage.getAuthPayLog2((String)orderInfo.get("command_no"));
		
		//创建新的its签约记录
		Date now  = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat format0 = new SimpleDateFormat("HHmmss");
		String trans_date	= format.format(now);
		String trans_time	= format0.format(now);
		Map<String, String> signlogMap = new HashMap<String, String>();
		signlogMap.put("userId", 				(String)itsApplyPayLog.get("userId"));
		signlogMap.put("ori_trace_num", 		(String)itsApplyPayLog.get("trace_num"));
		signlogMap.put("ori_trans_date", 		(String)itsApplyPayLog.get("trans_date"));
		signlogMap.put("sign_type", 			Constant.SIGN_TYPE_REFUND.toString());
		signlogMap.put("trans_date", 			trans_date);
		signlogMap.put("trans_time", 			trans_time);
		signlogMap.put("trans_amount", 		((Long)orderInfo.get("trade_money")).toString());
		Map<String, Object> signlog_entity = ItsManage.createSignLog(signlogMap);
		
		//获取数据并检查
		String traceNum	= (String)signlog_entity.get("trace_num");
		String transAmount = ((Long)orderInfo.get("trade_money")).toString();
		String currency = Constant.CURRENCY_TYPE_RBM.toString();
		String oriTraceNum = signlogMap.get("ori_trace_num");
		String oriTransDate = signlogMap.get("ori_trans_date");
		Map<String, String> extParams = new HashMap<String, String>();
		
		if(StringUtils.isBlank(traceNum)){
			throw new Exception("参数traceNum为空。");
		}
		if(StringUtils.isBlank(transAmount)){
			throw new Exception("参数transAmount为空。");
		}
		if(StringUtils.isBlank(currency)){
			throw new Exception("参数currency为空。");
		}
		if(StringUtils.isBlank(oriTraceNum)){
			throw new Exception("参数oriTraceNum为空。");
		}
		if(StringUtils.isBlank(oriTransDate)){
			throw new Exception("参数oriTransDate为空。");
		}
		
		logger.info("traceNum:" + traceNum + ",transAmount=" + transAmount + ",currency=" + currency + ",oriTraceNum=" + oriTraceNum + ",oriTransDate" + oriTransDate);
		
		//自定义转账
		Map<String, Object> trade_param = new HashMap<String, Object>();
		trade_param.put("source_userId", 		orderInfo.get("target_userId"));
		if(orderInfo.get("target_account_type_id") != null ){
			trade_param.put("account_type_id", 		orderInfo.get("target_account_type_id"));
		}else{
			trade_param.put("account_type_id", 		orderInfo.get("account_type_id"));
		}
		trade_param.put("target_account_type_id", 		orderInfo.get("account_type_id"));
		trade_param.put("trade_money", 			orderInfo.get("trade_money"));
		trade_param.put("target_userId", 		orderInfo.get("source_userId"));
		trade_param.put("out_trade_id", 		orderInfo.get("out_trade_id"));
		trade_param.put("out_bizno", 			orderInfo.get("accountNo"));
		trade_param.put("bizid", 				orderInfo.get("bizid"));
		trade_param.put("bank_code", 			orderInfo.get("bank_code"));
		trade_param.put("card_type", 			orderInfo.get("card_type"));
        trade_param.put("command_no",           orderInfo.get("command_no"));
		trade_param.put("source_memberNo", 		orderInfo.get("target_memberNo"));
		trade_param.put("target_memberNo", 		orderInfo.get("source_memberNo"));
		trade_param.put("orgNo", 				orderInfo.get("orgNo"));
		trade_param.put("pay_channelNo", 		Constant.PAY_CHANNEL_QUICK);
		trade_param.put("source_member_name", 	orderInfo.get("target_member_name"));
		trade_param.put("target_member_name", 	orderInfo.get("source_member_name"));	
		trade_param.put("trade_type", 			Constant.TRADE_TYPE_REFUNDMENT);
		trade_param.put("trade_time", 			new Date());
		trade_param.put("pay_interfaceNo", 		Constant.PAY_INTERFACE_QUICK);	
		trade_param.put("isMaster", 		false);
		final Long order_id = (Long)orderInfo.get("id");
		final String orderNo = (String)orderInfo.get("bizid");
		
		try{
			TradeService.customTransfer(trade_param);
			EntityManagerUtil.execute(new TransactionWork<String>() {
				@Override
				public String doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_OrderPayDetail set refund_status=:refundStatus where id=:id");
					query.setParameter("refundStatus", Constant.REFUND_STATE_DOING);
					query.setParameter("id", order_id);
					
					query.executeUpdate();
					return null;
				}
	        });
		}
		catch(Exception e){
			logger.error("自定义转账时发生异常：" + e.getMessage());
			throw e;
		}
		
		//调用接口进行退款
		String response = null;
		try{
			response = Extension.itsService.itsRefund(traceNum, transAmount, currency, oriTraceNum, oriTransDate, extParams);
			logger.info(response);
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}

		//根据response进行处理
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		Document document = DocumentHelper.parseText(response);
		Element rootEl = document.getRootElement();
		
		Long refundStatus;
		
		Element errorElement = (Element)rootEl.selectSingleNode("/ERROR");
		//系统错误
		if(errorElement != null){
			Element errorCodeElement = (Element)rootEl.selectSingleNode("/ERROR/ERROR_CODE");
			if(errorCodeElement != null){
				responseMap.put("errorCode", errorCodeElement.getText());
			}
			
			Element errorMsgElement = (Element)rootEl.selectSingleNode("/ERROR/ERROR_MSG");
			if(errorMsgElement != null){
				responseMap.put("errorMsg", errorMsgElement.getText());
			}
			
			refundStatus = Constant.REFUND_STATE_FAIL;
			retMap.put("command_result", CommandResult.FailStop);
			retMap.put("ret_code1", responseMap.get("errorCode"));
			retMap.put("err_msg1",	responseMap.get("errorMsg"));
		}
		//交易错误
		else{
			//中间状态
			//头响应码
			Element headRetCodeElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_CODE");
			if(headRetCodeElement != null){
				responseMap.put("retCode", headRetCodeElement.getText());
			}
			
			//头响应信息
			Element headRetMsgElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_MSG");
			if(headRetMsgElement != null){
				responseMap.put("retMsg", headRetMsgElement.getText());
			}
			
			//最终状态
			//退款金额
			Element transAmountElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/TRANS_AMOUNT");
			if(transAmountElement != null){
				responseMap.put("transAmount", transAmountElement.getText());
			}
			//币种
			Element currencyElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CURRENCY");
			if(currencyElement != null){
				responseMap.put("currency", currencyElement.getText());
			}
			//响应吗
			Element retCodeElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_CODE");
			if(retCodeElement != null){
				responseMap.put("retCode", retCodeElement.getText());
			}
			//相应信息
			Element retMsgElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_MSG");
			if(retMsgElement != null){
				responseMap.put("retMsg", retMsgElement.getText());
			}
			//its交易流水号
			Element itsTraceNumElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_NUM");
			if(itsTraceNumElement != null){
				responseMap.put("itsTraceNum", itsTraceNumElement.getText());
			}
			//its交易日期
			Element itsTraceDateElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_DATE");
			if(itsTraceDateElement != null){
				responseMap.put("itsTraceDate", itsTraceDateElement.getText());
			}
			//its交易时间
			Element itsTraceTimeElement = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_TIME");
			if(itsTraceTimeElement != null){
				responseMap.put("itsTraceTime", itsTraceTimeElement.getText());
			}
			
			String retCode = (String)responseMap.get("retCode");
			String isSucCode = retCode.substring(0, 1);
			//TODO 朱成 首位 0：成功，1、2：中间状态，3：失败。这边处理有问题
			refundStatus = ("0".endsWith(isSucCode) || "1".endsWith(isSucCode) || "2".endsWith(isSucCode)) ? Constant.REFUND_STATE_SUCCESS : Constant.REFUND_STATE_FAIL;
			if(refundStatus.equals(Constant.REFUND_STATE_SUCCESS)){
				logger.info("its退款接口调用：成功或已受理。");
				retMap.put("command_result", CommandResult.Success);
			}
			else{
				logger.error("its退款接口调用：交易失败或者无此交易.");
				retMap.put("command_result", CommandResult.Success);
				retMap.put("ret_code1", retCode);
				retMap.put("err_msg1",	 (String)responseMap.get("retMsg"));
			}
		}
		final Long _refundStatus = refundStatus;
		EntityManagerUtil.execute(new TransactionWork<String>() {
			@Override
			public String doTransaction(Session session, Transaction tx)
					throws Exception {
				Query query = session.createQuery("update AMS_OrderPayDetail set refund_status=:refundStatus where id=:id");
				query.setParameter("refundStatus", _refundStatus);
				query.setParameter("id", order_id);
				
				query.executeUpdate();
				List list = Order.getSuccessReturnCommands(orderNo, session);
				List list2 = Order.getSuccessCommands(orderNo, session);
				
				if(list != null && list.size() == list2.size()) {
					Map<String, Object> order_entity = Order.getOrder(orderNo, session);
					Order order = OrderFactory.getOrder((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"));
					if(Constant.TRADE_TYPE_REFUNDMENT.equals(order_entity.get("trade_type"))) {
						logger.info("触发订单支付完成事件");
    					//触发订单支付完成事件
    					Map<String, Object> param2 = new HashMap<String, Object>();
    					param2.put("orderNo", orderNo);
    					Event event = new Event(com.kinorsoft.ams.Constant.EVENT_TYPE_ORDERCOMPLETEPAY, param2, null);
    					EventManager.instance().fireEvent(event);
    					
    					logger.info("触发订单完成支付事件");
					} else {
						order.closeOrder(orderNo, null, session);
					}
				}
				return null;
			}
        });
		return retMap;
	}

	public Map<String, Object> undoCommands(List<Map<String, Object>> arg0)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> calculateFees(Map<String, Object> param)
			throws Exception {
		logger.info("计算手续费-its");
		logger.info("param="+param);

		String pay_interfaceNo = (String) param.get("pay_interfaceNo");
		String bank_code = (String) param.get("bank_code");
	    Long card_type = (Long) param.get("card_type");
	    Long money = (Long) param.get("trade_money");
	    Map<String, Object> result = new HashMap<String, Object>();
	    Long fee_money = 0L;
		JSONArray payInterface_bank_list = PayChannelManage.getPayInterfaceBankInfo(pay_interfaceNo);
		if(payInterface_bank_list != null && payInterface_bank_list.length() > 0) {
			for(int i = 0, j = payInterface_bank_list.length(); i < j; i ++) {
				JSONObject payInterface_bank_entity = payInterface_bank_list.getJSONObject(i);
				if(((String)payInterface_bank_entity.get("bank_code")).indexOf(bank_code) >= 0) {
					Long handling_type = null;
					Double handling_rate = null;
					Long handling_each = null;
                    Long handling_mode = null;
					if(card_type.equals(Constant.BANK_CARD_CX)) {
						if(payInterface_bank_entity.get("debit_handling_type") != null) {
							handling_type = payInterface_bank_entity.optLong("debit_handling_type");
						}
						if(payInterface_bank_entity.get("debit_handling_rate") != null) {
							handling_rate = payInterface_bank_entity.optDouble("debit_handling_rate");
						}
						if(payInterface_bank_entity.get("debit_handling_each") != null) {
							handling_each = payInterface_bank_entity.optLong("debit_handling_each");
						}
						if(payInterface_bank_entity.get("debit_handling_mode") != null) {
							handling_mode = payInterface_bank_entity.optLong("debit_handling_mode");
						}
					} else if(card_type.equals(Constant.BANK_CARD_XY)) {
						if(payInterface_bank_entity.get("credit_handling_type") != null) {
							handling_type = payInterface_bank_entity.optLong("credit_handling_type");
						}
						if(payInterface_bank_entity.get("credit_handling_rate") != null) {
							handling_rate = payInterface_bank_entity.optDouble("credit_handling_rate");
						}
						if(payInterface_bank_entity.get("credit_handling_each") != null) {
							handling_each = payInterface_bank_entity.optLong("credit_handling_each");
						}
						if(payInterface_bank_entity.get("credit_handling_mode") != null) {
							handling_mode = payInterface_bank_entity.optLong("credit_handling_mode");
						}
					}
					if(handling_each == null) {
						handling_each = 0L;
					}
					if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_PERCENTAGE)) {
					    fee_money = Math.round(money * handling_rate);
					} else if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_EACH)) {
					    fee_money = handling_each;
					}
			        result.put("handling_mode", handling_mode);
				}
			}
		}
		result.put("fee_money", fee_money);
        return result;
	}

}
