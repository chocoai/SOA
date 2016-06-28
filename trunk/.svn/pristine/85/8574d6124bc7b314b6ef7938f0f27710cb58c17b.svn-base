package bps.triger;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ime.calendar.TrigerHandler;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import bps.common.Constant;
import apf.util.EntityManagerUtil;
import apf.util.SessionContext;

/**
 * 统计账户余额
 * @author Administrator
 *
 */
public class AccountBalanceTriger implements TrigerHandler {
	private static Logger logger = Logger.getLogger(AccountBalanceTriger.class.getName());
	public void handle(){
//		logger.info("CloseOrderTriger start");
//		
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_MONTH, -1);
//		
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.MINUTE, 0);
//		
//		Date beginDt = cal.getTime();
//		
//		cal.set(Calendar.HOUR_OF_DAY, 23);
//		cal.set(Calendar.MINUTE, 59);
//		cal.set(Calendar.MINUTE, 59);
//		Date endDt = cal.getTime();
//		
//		SessionContext ctx = null;
//		try {
//			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
//			
//			ctx = EntityManagerUtil.currentContext();
//			Session session 		= ctx.getHibernateSession();	//获取会话
//			Transaction tx 			= session.getTransaction();
//			Boolean isActive		= !tx.isActive();
//			
//			//统计个人、企业账户余额
//			StringBuilder sb = new StringBuilder();
//			sb.setLength(0);
//			sb.append("select a0.account_type_id, sum(a0.amount) ");
//			sb.append("from AMS_MemberAccount a0, AMS_Member a1 ");
//			sb.append("where a0.userId=a1.userId and a1.member_type>1 ");
//			sb.append("group by a0.account_type_id ");
//			Query query = session.createQuery(sb.toString());
//			
//			List yue_list = query.list();
//			
//			//统计出金
//			sb.setLength(0);
//			sb.append("select a0.account_type_id, sum(a0.chg_money) ");
//			sb.append("from AMS_AccountChgLog a0, AMS_Member a1 ");
//			sb.append("where a0.userId=a1.userId and a1.member_type=1 and a0.chg_money>0 and a0.chg_time>=:beginDt and a0.chg_time<=:endDt ");
//			sb.append("group by a0.account_type_id ");
//			
//			query = session.createQuery(sb.toString());
//			query.setParameter("beginDt", beginDt);
//			query.setParameter("endDt", endDt);
//			
//			List out_list = query.list();
//			
//			//统计入金
//			sb.setLength(0);
//			sb.append("select a0.account_type_id, sum(a0.chg_money) ");
//			sb.append("from AMS_AccountChgLog a0, AMS_Member a1 ");
//			sb.append("where a0.userId=a1.userId and a1.member_type=1 and a0.chg_money<0 and a0.chg_time>=:beginDt and a0.chg_time<=:endDt ");
//			sb.append("group by a0.account_type_id ");
//			
//			query = session.createQuery(sb.toString());
//			query.setParameter("beginDt", beginDt);
//			query.setParameter("endDt", endDt);
//			
//			List in_list = query.list();
//			
//			//统计调帐出金
//            sb.setLength(0);
//            sb.append("select a0.account_type_id, sum(a0.trade_money) ");
//            sb.append("from AMS_AdjustAccount a0, AMS_Member a1 ");
//            sb.append("where a0.source_userId=a1.userId and a1.member_type=1 and a0.FM_CreateTime>=:beginDt and a0.FM_CreateTime<=:endDt ");
//            sb.append("group by a0.account_type_id ");
//            
//            query = session.createQuery(sb.toString());
//            query.setParameter("beginDt", beginDt);
//            query.setParameter("endDt", endDt);
//            
//            List out_mix_list = query.list();
//
//            //统计调帐入金
//            sb.setLength(0);
//            sb.append("select a0.account_type_id, sum(a0.trade_money) ");
//            sb.append("from AMS_AdjustAccount a0, AMS_Member a1 ");
//            sb.append("where a0.target_userId=a1.userId and a1.member_type=1 and a0.FM_CreateTime>=:beginDt and a0.FM_CreateTime<=:endDt ");
//            sb.append("group by a0.account_type_id ");
//            
//            query = session.createQuery(sb.toString());
//            query.setParameter("beginDt", beginDt);
//            query.setParameter("endDt", endDt);
//            
//            List in_mix_list = query.list();
//            
//            //统计个人、企业账户余额
//            sb.setLength(0);
//            sb.append("select balance ");
//            sb.append("from AMS_AccountBalance ");
//            sb.append("where balance_type=:balance_type ");
//            sb.append("order by FM_CreateTime desc ");
//            query = session.createQuery(sb.toString());
//            query.setParameter("balance_type", Constant.BALANCE_TYPE_NETTING);
//            query.setFirstResult(0);
//            query.setMaxResults(1);
//            List prev_net_list = query.list();
//            
//            //统计轧差金额
//            sb.setLength(0);
//            sb.append("select sum(handling_charge) ");
//            sb.append("from AMS_TradeLog ");
//            sb.append("where handling_mode=:handling_mode and trade_time>=:beginDt and trade_time<=:endDt ");
//            
//            query = session.createQuery(sb.toString());
//            query.setParameter("beginDt", beginDt);
//            query.setParameter("endDt", endDt);
//            query.setParameter("handling_mode", Constant.FEE_CHARGE_TYPE_NETTING);
//            
//            List net_list = query.list();
//			try{
//				if(isActive)
//					tx.begin();
//				
//				for(int i=0; i<yue_list.size(); i++){
//					Object[] record = (Object[])yue_list.get(i);
//					Long account_type_id	= (Long)record[0];
//					Long balance			= (Long)record[1];
//		            
//		            //统计个人、企业账户余额
//		            sb.setLength(0);
//		            sb.append("select balance ");
//		            sb.append("from AMS_AccountBalance ");
//		            sb.append("where balance_type=:balance_type and account_type_id=:account_type_id ");
//		            sb.append("order by FM_CreateTime desc ");
//		            query = session.createQuery(sb.toString());
//                    query.setParameter("account_type_id", account_type_id);
//		            query.setParameter("balance_type", Constant.BALANCE_TYPE_BALANCE);
//		            query.setFirstResult(0);
//		            query.setMaxResults(1);
//		            List prev_yue_list = query.list();
//					logger.info("prev_yue_list="+prev_yue_list+";account_type_id="+account_type_id);
//					Map<String, Object> account_type_entity = DynamicEntityService.getEntity(account_type_id, "AMS_AccountType");
//					
//					Map<String, String> accountBalanceMap = new HashMap<String, String>();
//					Long now_time = new Date().getTime();
//					accountBalanceMap.put("balance_date", 		now_time.toString());
//					accountBalanceMap.put("account_type_id", 	account_type_id.toString());
//					accountBalanceMap.put("account_type_label", account_type_entity.get("name").toString());
//					accountBalanceMap.put("balance", 			balance.toString());
//					for(Object obj : out_list) {
//						Object[] out_record = (Object[])obj;
//						if(account_type_id.equals((Long)out_record[0])) {
//							Long out_money			= Math.abs((Long)out_record[1]);
//							accountBalanceMap.put("withdraw_aount", 	out_money.toString());						
//						}
//					}
//					for(Object obj : in_list) {
//						Object[] in_record = (Object[])obj;
//						if(account_type_id.equals((Long)in_record[0])) {
//							Long in_money			= Math.abs((Long)in_record[1]);
//							accountBalanceMap.put("deposit_amount", 	in_money.toString());
//						}
//					}
//					if(!prev_yue_list.isEmpty()) {
//						accountBalanceMap.put("prev_balance", 	prev_yue_list.get(0).toString());
//					}
//					Long mix_amount = 0L;
//                    for(Object obj : in_mix_list) {
//                        Object[] in_mix_record = (Object[])obj;
//                        if(account_type_id.equals((Long)in_mix_record[0])) {
//                            mix_amount += Math.abs((Long)in_mix_record[1]);
//                        }
//                    }
//                    for(Object obj : out_mix_list) {
//                        Object[] out_mix_record = (Object[])obj;
//                        if(account_type_id.equals((Long)out_mix_record[0])) {
//                            mix_amount -= Math.abs((Long)out_mix_record[1]);
//                        }
//                    }
//                    accountBalanceMap.put("balance_type", Constant.BALANCE_TYPE_BALANCE.toString());
//                    accountBalanceMap.put("mix_amount",     mix_amount.toString());
//					DynamicEntityService.createEntity("AMS_AccountBalance", accountBalanceMap, null);
//				}
//				
//				Map<String, String> accountBalanceMap = new HashMap<String, String>();
//                Long now_time = new Date().getTime();
//                accountBalanceMap.put("balance_date",       now_time.toString());
//                Long prev_balance = 0L;
//                if(!prev_net_list.isEmpty()) {
//                    prev_balance = (Long)prev_net_list.get(0);
//                }
//                accountBalanceMap.put("prev_balance",   prev_balance.toString());
//                if(net_list.isEmpty() || net_list.get(0) == null) {
//                    accountBalanceMap.put("deposit_amount", "0");
//                    accountBalanceMap.put("balance",        prev_balance.toString());
//                } else {
//                    accountBalanceMap.put("deposit_amount",  net_list.get(0).toString());
//                    accountBalanceMap.put("balance",         (prev_balance + (Long)net_list.get(0)) + "");
//                }
//                accountBalanceMap.put("balance_type", Constant.BALANCE_TYPE_NETTING.toString());
//                DynamicEntityService.createEntity("AMS_AccountBalance", accountBalanceMap, null);
//				if (isActive)
//					tx.commit();
//			}catch (Exception e) {
//				if (isActive)
//					tx.rollback();
//				throw e;
//			}
//			
//		}catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		} finally {
//			EntityManagerUtil.closeSession(ctx);
//			logger.info("CloseOrderTriger end");
//		}
			
	}
}