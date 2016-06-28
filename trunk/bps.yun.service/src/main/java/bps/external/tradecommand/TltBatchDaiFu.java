package bps.external.tradecommand;

import apf.util.EntityManagerUtil;
import apf.util.SessionContext;
import apf.work.TransactionWork;
import bps.common.Constant;

import bps.order.Order;
import bps.order.OrderServiceImpl;
import bps.process.PayChannelManage;
import bps.service.OrderService;
import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.services.FreezeService;
import com.kinorsoft.ams.services.TradeService;
import ime.core.Reserved;
import ime.core.event.Event;
import ime.core.event.EventManager;
import ime.security.LoginSession;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import java.util.*;

public class TltBatchDaiFu{
	private static Logger tlt_logger = Logger.getLogger("tlt");

	public String getPayInterfaceNo(){
		return Constant.PAY_INTERFACE_TLT_BACH_DF;
	}
	/**
	 * 执行订单
	 */
	public void doCommand(Map<String, Object> result ) {
		try{
			String ret_code1 	=  (String)result.get("RET_CODE1");
			List<Map<String,String>> DETAIL	= (List<Map<String, String>>) result.get("DETAIL");
			if ("0000".equals(ret_code1)){

				for (Map<String, String> temp : DETAIL){
					final String SN = temp.get("SN");
					String ret_code2 = temp.get("RET_CODE");
					Map<String, Object> command = Order.getCommand(SN);
					if( ret_code2.equals("0000") || ret_code2.equals("4000")){	//成功
						//代付成功
						daifuSucess(command);

						Map<String, Object> param = new HashMap<>();
						param.put("orderNo", command.get("bizid"));
						Event event = new Event(com.kinorsoft.ams.Constant.EVENT_TYPE_ORDERCOMPLETEPAY, param, null);
						EventManager.instance().fireEvent(event);

						tlt_logger.info("触发订单完成支付事件");
					}else{	//失败
						daifuFail(command);
					}
				}
			}
		}catch(Exception e){
			tlt_logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 代付成功
	 * @param command	command
	 * @throws Exception
	 */
	public static void daifuSucess(Map<String, Object> command)throws Exception{
		tlt_logger.info("TltDaiFu daifuSucess start");
		
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		final String bizid			= (String)command.get("bizid");
		final String source_userId	= (String)command.get("source_userId");
		final Long trade_money 		= (Long)command.get("trade_money");
		final Long account_type_id	= (Long)command.get("account_type_id");
		final Map<String, Object> _command = command;
		
		
		
		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				
				//修改2015-11-13 by huad 如果是代付到银行卡的，解冻订单的所有金额
				Long to_freeze_money = trade_money;
				OrderService orderService = new OrderServiceImpl();
				Map<String,Object> order_entity = orderService.getOrder(bizid);
				if(Constant.ORDER_TYPE_DAIFU.equals(order_entity.get("order_type"))||Constant.ORDER_TYPE_BATCH_DAIFU.equals(order_entity.get("order_type"))){
					to_freeze_money = (Long)order_entity.get("order_money");
				}
				//解冻部分金额
				Map<String, Object> unfreeze_param = new HashMap<>();
				unfreeze_param.put("userId", 			source_userId);    
				unfreeze_param.put("bizid", 			bizid);
				unfreeze_param.put("account_type_id", 	account_type_id);
				unfreeze_param.put("unfreeze_money", 	to_freeze_money);
				unfreeze_param.put("remark", 			"代付成功,解冻");
				
				FreezeService.unfreezeMoney(unfreeze_param);
				
				//交易
				Long trade_type	= (Long)_command.get("trade_type");
				Map<String, Object> trade_param = new HashMap<>();
				
				trade_param.put("source_userId", 		_command.get("source_userId"));
				trade_param.put("account_type_id", 		_command.get("account_type_id"));
				trade_param.put("target_account_type_id", 		_command.get("target_account_type_id"));
				trade_param.put("trade_money", 			_command.get("trade_money"));
				trade_param.put("target_userId", 		_command.get("target_userId"));
				trade_param.put("out_trade_id", 		_command.get("out_trade_id"));
				trade_param.put("out_bizno", 			_command.get("accountNo"));
				trade_param.put("bizid", 				bizid);
                trade_param.put("command_no",           _command.get("command_no"));
				trade_param.put("remark", 				_command.get("remark"));
				trade_param.put("source_memberNo", 		_command.get("source_memberNo"));
				trade_param.put("target_memberNo", 		_command.get("target_memberNo"));
				trade_param.put("isMaster", 			_command.get("isMaster"));
				trade_param.put("orgNo", 				_command.get("orgNo"));
				trade_param.put("pay_channelNo", 		_command.get("pay_channelNo"));
				trade_param.put("sub_trade_type", 		_command.get("sub_trade_type"));
				trade_param.put("source_member_name", 	_command.get("source_member_name"));
				trade_param.put("target_member_name", 	_command.get("target_member_name"));	
				trade_param.put("trade_type", 			_command.get("trade_type"));
				trade_param.put("trade_time", 			new Date());

				trade_param.put("pay_interfaceNo", 		_command.get("pay_interfaceNo"));	
				trade_param.put("bank_code", 			_command.get("bank_code"));		
				
				if(trade_type.equals(Constant.TRADE_TYPE_TRANSFER)){	
					TradeService.customTransfer(trade_param);
				}else if(trade_type.equals(Constant.TRADE_TYPE_WITHDRAW)){
					TradeService.withdraw(trade_param);
				}else {
					throw new Exception("不支持该交易类型");
				}
				
				return null;
				
			}
		}); 
		OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
		List list = orderServiceImpl.getCommands(bizid);
        for(Object obj : list) {
            Map<String,Object> command_entity = (Map<String, Object>) obj;
            if((command_entity.get("orgNo")).equals(command.get("orgNo"))) {
                Map<String,Object> temp = new HashMap<>();
                temp.put("id", command_entity.get("id"));
                temp.put("command_state", com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS);
                temp.put("out_ret_code1", "");
                temp.put("out_ret_code2", "");
                temp.put("out_errmsg1", "");
                temp.put("out_errmsg2", "");
                TradeCommandManager.setCommandState(temp);
                break;
            }
        }
	}
	
	/**
	 * 代付失败
	 * @param command	指令
	 * @throws Exception
	 */
	public static void daifuFail(Map<String, Object> command)throws Exception{
		SessionContext ctx = null;
		try {
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			ctx = EntityManagerUtil.currentContext();
			Session session 	= ctx.getHibernateSession();	//获取会话
			Transaction tx 		= session.getTransaction();
			//Boolean isActive	= !tx.isActive();
			
			String bizid			= (String)command.get("bizid");
			String source_userId	= (String)command.get("source_userId");
			Long trade_money 		= (Long)command.get("trade_money");
			Long account_type_id	= (Long)command.get("account_type_id");
			//修改2015-11-13 by huad 如果是代付到银行卡的，冻结订单的所有金额
			Long to_freeze_money = trade_money;
			OrderService orderService = new OrderServiceImpl();
			Map<String,Object> order_entity = orderService.getOrder(bizid);
			if(Constant.ORDER_TYPE_DAIFU.equals(order_entity.get("order_type"))||Constant.ORDER_TYPE_BATCH_DAIFU.equals(order_entity.get("order_type"))){
				to_freeze_money = (Long)order_entity.get("order_money");
			}
			//解冻
			Map<String, Object> unfreeze_param = new HashMap<>();
			unfreeze_param.put("userId", 	source_userId);
			unfreeze_param.put("bizid", 			bizid);
			unfreeze_param.put("account_type_id", 	account_type_id);
			unfreeze_param.put("unfreeze_money", 	to_freeze_money);
			unfreeze_param.put("remark", 			"代付失败,解冻");
			
			FreezeService.unfreezeMoney(unfreeze_param);
			
		}catch (Exception e) {
			tlt_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			EntityManagerUtil.closeSession(ctx);
			tlt_logger.info("TltAllinPay daifuFail end");
		}
	}

	public Map<String, Object> undoCommand(Map<String, Object> command)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> undoCommands(List<Map<String, Object>> commands)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> calculateFees(Map<String, Object> param)
			throws Exception {
		tlt_logger.info("计算手续费");
		tlt_logger.info("param=" + param);
	    String pay_interfaceNo = (String) param.get("pay_interfaceNo");
	    Long money = (Long) param.get("trade_money");
	    
	    
	    Map<String, Object> result = new HashMap<>();
	    Long fee_money = 0L;
		JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfoPure(pay_interfaceNo);
		tlt_logger.info("payInterface_entity=" + payInterface_entity);
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
			tlt_logger.info("handling_each="+handling_each);
			if(handling_each == null) {
				handling_each = 0L;
			}
			tlt_logger.info("handling_each="+handling_each);
			if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_PERCENTAGE)) {
				tlt_logger.info("111111111=");
			    fee_money = Math.round(money * handling_rate);
			} else if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_EACH)) {
				tlt_logger.info("222222222=");
			    fee_money = handling_each;
			}
	        result.put("handling_mode", handling_mode);
		}
	    result.put("fee_money", fee_money);
	    tlt_logger.info("result=" + result);
	    return result;
	}
}