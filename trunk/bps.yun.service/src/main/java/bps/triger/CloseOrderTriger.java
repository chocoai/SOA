package bps.triger;

import java.util.Calendar;
import java.util.Date;

import ime.calendar.TrigerHandler;
import ime.core.Environment;
import ime.core.Reserved;
import ime.security.LoginSession;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import bps.common.Constant;
import apf.util.EntityManagerUtil;
import apf.util.SessionContext;


public class CloseOrderTriger  implements TrigerHandler {
	private static Logger logger = Logger.getLogger(CloseOrderTriger.class.getName());
	
	public void handle(){
		logger.info("CloseOrderTriger start");
		SessionContext ctx = null;
		try {
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			
			ctx = EntityManagerUtil.currentContext();
			Session session 		= ctx.getHibernateSession();	//获取会话
			Transaction tx 			= session.getTransaction();
			Boolean isActive		= !tx.isActive();
			
			Environment environment = Environment.instance();
			String str_day 		= environment.getSystemProperty("order.close.day");
			Long day = Long.valueOf(str_day);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - (int)day.longValue());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			Date dt = cal.getTime();
			
			StringBuilder sb = new StringBuilder();
			sb.append("update AMS_Order set order_state=:colse_order_state where create_time<:dt and order_state=:order_state");
			
			try{
				if(isActive)
					tx.begin();
				
				Query query = session.createQuery(sb.toString());
				query.setParameter("dt", dt);
				query.setParameter("order_state", Constant.ORDER_STATE_WAIT_PAY);
				query.setParameter("colse_order_state", Constant.ORDER_STATE_CLOSE);
				query.executeUpdate();
				
				if (isActive)
					tx.commit();
			}catch (Exception e) {
				if (isActive)
					tx.rollback();
				throw e;
			}
			
			//批量通知接口
			//Order.noticeMSorder(orderNo, order_state);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			EntityManagerUtil.closeSession(ctx);
			logger.info("CloseOrderTriger end");
		}
		
		//通知商城
	}
}