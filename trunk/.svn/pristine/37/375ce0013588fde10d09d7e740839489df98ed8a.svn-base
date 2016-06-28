package bps.eventhandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import bps.order.OrderServiceImpl;
import ime.core.event.DynamicEntityEvent;
import ime.core.event.IEntityEventHandler;

public class ArtOrderCompleteHandler implements IEntityEventHandler{
	private static Logger logger = Logger.getLogger(ArtOrderCompleteHandler.class.getName());
	@Override
	public Map<String, Object> customerEvent(String arg0, String arg1,
			Map<String, Object> arg2, Session arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postCreate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postRemove(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postUpdate(DynamicEntityEvent event, Session session)
			throws Exception {		
		//触发事件
		Map<String,Object> entity = event.getEntity();
		Integer WF_State = (Integer)entity.get("WF_State");
		if(WF_State == 4){ //审核成功
			//调用外部支付完成接口
			Map<String, Object> param = new HashMap<String, Object>();
			String commandNo = (String)entity.get("command_no");
			Long order_money = (Long)entity.get("order_money");
			String out_trade_id = (String)entity.get("out_trade_id");
			String payChannelNo = (String)entity.get("payChannelNo");
			String payInterfaceNo = (String)entity.get("payInterfaceNo");
			param.put("orderNo", commandNo);
			param.put("orderMoney", order_money);
			param.put("outTradeId", out_trade_id);
			param.put("payChannelNo", payChannelNo);
			param.put("payInterfaceNo", payInterfaceNo);
			param.put("tradeTime", new Date());
			logger.info("payChannelDeposit参数：param=" + param);
			OrderServiceImpl orderService = new OrderServiceImpl();
			orderService.payChannelDeposit(param);		
		}
		
	}

	@Override
	public void preCreate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preRemove(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preUpdate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
