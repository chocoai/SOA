package bps.eventhandler;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import bps.common.Constant;
import bps.order.Order;
import bps.order.OrderFactory;
import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import ime.core.Reserved;
import ime.core.event.Event;
import ime.core.event.IEventHandler;
import ime.security.LoginSession;

public class OrderComplatePayEventHandler implements IEventHandler{
	private static Logger logger = Logger.getLogger(OrderComplatePayEventHandler.class.getName());
	private static Logger pay_logger = Logger.getLogger("pay");
	
	public void handleEvent(Event event) throws Exception {
		logger.info("OrderComplatePayEventHandler begin");
		logger.info("event.getTarget:" + event.getTarget());
		
		Map<String, Object> param = (Map<String, Object>)event.getTarget();
		String command_no 		= (String)param.get("orderNo");			//订单号
		String orderNo = command_no;
		if(orderNo == null)
			throw new Exception("请传入参数 orderNo ");
		int index = orderNo.indexOf(Constant.COMMAND_SPLIT_SIGN);
		if(index > 0){
		    orderNo = orderNo.substring(0, index);
		}
		final String _orderNo = orderNo;
		
		if(!LoginSession.isLogined())
		    LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);

		
		final Map<String, Object> _param = param;
		EntityManagerUtil.execute(new TransactionWork<Object>() {
			@Override
			public Object doTransaction(Session session, Transaction tx)
					throws Exception {
				session.flush();
				session.clear();
				Map<String, Object> order_entity = (Map<String, Object>)Order.getOrder(_orderNo, session);
				order_entity.putAll(_param);
				Order order = OrderFactory.getOrder((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"));
				order.completePay(order_entity, session);
				session.flush();
				session.clear();
				order_entity = (Map<String, Object>)Order.getOrder(_orderNo, session);
				if(Constant.ORDER_STATE_SUCCESS.equals(order_entity.get("order_state"))) {
					logger.info("收取机构数续费");
					if(Constant.YUN_APPLICATION_ID.equals(order_entity.get("application_id"))){
						Order.receivePortalAgencyFees(order_entity, session);//收取机构订单手续费
						Order.createPayDetail(order_entity, session);//创建支付明细
					}else{
						Order.receiveAgencyFees(order_entity, session);//收取机构订单手续费
						Order.createPayDetail(order_entity, session);//创建支付明细
					}
				}
				
    			return null;
			}
		}); 
	}
}