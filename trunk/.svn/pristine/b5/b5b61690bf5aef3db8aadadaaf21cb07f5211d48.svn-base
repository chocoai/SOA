package bps.triger;

import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import bps.common.Constant;
import apf.util.EntityManagerUtil;
import apf.util.SessionContext;


import ime.calendar.TrigerHandler;
import ime.core.Reserved;
import ime.security.LoginSession;

public class ConfirmReceiptTriger implements TrigerHandler {
private static Logger logger = Logger.getLogger(ConfirmReceiptTriger.class.getName());
	

	public void handle(){
		logger.info("ConfirmReceiptTriger start");
//		SessionContext ctx = null;
//		
//		try {
//			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
//			
//			ctx = EntityManagerUtil.currentContext();
//			Session session 		= ctx.getHibernateSession();	//获取会话
//			Transaction tx 			= session.getTransaction();
//			Boolean isActive		= !tx.isActive();
//			
//			Query query = session.createQuery("from AMS_Order o, AMS_OrderDetail od where o.id=od.owner_order_id and o.order_state=:order_state and o.receiving_delay_date<=:receiving_delay_date");
//			query.setParameter("order_state", Constant.ORDER_STATE_WAIT_Receiving);
//			query.setParameter("receiving_delay_date", new Date()); 
//			List list = query.list();
//			Map<Long, Map<String, Object>> order_list = new HashMap<Long, Map<String, Object>>();
//			List<Long> order_id_list = new ArrayList();
//			
//			for(int i=0; i<list.size(); i++){
//				Object[] objs = (Object[])list.get(i);
//				Map<String, Object> order_entity = (Map<String, Object>)objs[0];
//				Map<String, Object> orderDetail = (Map<String, Object>)objs[1];
//				Long orderId = (Long)order_entity.get("id");
//				if(order_list.get(orderId) == null) {
//					List<Map<String, Object>> orderDetail_list = new ArrayList();
//					orderDetail_list.add(orderDetail);
//					order_entity.put("orderDetail_list", orderDetail_list);
//					order_list.put(orderId, order_entity);
//					order_id_list.add(orderId);
//				} else {
//					order_entity = order_list.get("orderId");
//					List<Map<String, Object>> orderDetail_list = (List<Map<String, Object>>)order_entity.get("orderDetail_list");
//					orderDetail_list.add(orderDetail);
//				}
//			}
//			
//			try{
//				if(isActive)
//					tx.begin();
//				
//				for(Long orderId : order_id_list) {
//					Map<String, Object> order_entity = order_list.get("orderId");
//					List<Map<String, Object>> orderDetail_list = (List<Map<String, Object>>)order_entity.get("orderDetail_list");
//					boolean flag = true;
//					for(Map<String, Object> orderDetail : orderDetail_list) {
////						if(orderDetail.get("return_goods_label") != null 
////								&& orderDetail.get("return_goods_label").toString().equals(Constant.ORDER_RETURN_STATU_GOODS_EXAMINE.toString())
////								&& orderDetail.get("return_goods_label").toString().equals(Constant.ORDER_RETURN_STATU_GOODS_WAIT_RECEIPT.toString())
////								&& orderDetail.get("return_goods_label").toString().equals(Constant.ORDER_RETURN_STATU_MONEY_EXAMINE.toString())) {
//							flag = false;
//							break;
//						}
//					}
////					if(flag) {
//	//					Order.confirmReceipt((Long)order_entity.get("id"), session);
////					}
//	//			}
//				if (isActive)
//					tx.commit();
//				
//			}catch (Exception e) {
//				if (isActive)
//					tx.rollback();
//				throw e;
//			}
//			
//			/*通知商城
//			logger.info("通知商城");
//			for(int i=0; i<list.size(); i++){
//				Map<String, Object> order_entity = (Map<String, Object>)list.get(i);
//				String orderNo	= (String)order_entity.get("orderNo");
//				Order.noticeMSorder(orderNo, Constant.ORDER_STATE_SUCCESS);
//			}*/
//		}catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		} finally {
//			EntityManagerUtil.closeSession(ctx);
//			logger.info("ConfirmReceiptTriger end");
//		}
		
		
	}
	
}