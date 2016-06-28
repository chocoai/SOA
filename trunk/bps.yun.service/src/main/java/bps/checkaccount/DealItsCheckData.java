package bps.checkaccount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import apf.util.EntityManagerUtil;
import apf.util.SessionContext;
import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.common.Constant;

public class DealItsCheckData {
	private static Logger logger = Logger.getLogger(DealItsCheckData.class.getName());
	private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 短款处理
	 * @param bankDataMap
	 * @param settDay
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Map<String,String>> shortRecord(Map<String, Map<String, String>> bankDataMap,String settDay) throws Exception{
		List<Map<String, Object>> longlist = null;
		List<Map<String, Object>> deallonglist =  new ArrayList<Map<String,Object>>();
		SessionContext ctx = null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(df.parse(settDay));
		cal.add(Calendar.DAY_OF_MONTH, -7); 
		final Date beginDt = cal.getTime();
		final Date endDt = df.parse(settDay);
		try {
		
			longlist =	EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> doQuery(Session session) throws Exception {												//
					 Query query = session.createQuery("from AMS_ErrorTradeLog where pay_channelNo=:pay_channelNo and trade_type=:trade_type and error_type=:error_type  and idDealwith<>true and trade_time>=:beginDt and trade_time<=:endDt");
					 	query.setParameter("beginDt", beginDt);
						query.setParameter("endDt", endDt);
						
						query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_QUICK);
						query.setParameter("trade_type", Constant.TRADE_TYPE_DEPOSIT);
						query.setParameter("error_type", 1L);
	                    List<Map<String,Object>> list = query.list();
	    				return list;
				}
			});
			for(String key : bankDataMap.keySet()){
				Map<String,String> shortmap = (Map<String,String>)bankDataMap.get(key);
				String shortMoney = shortmap.get("trade_money");
				for(int j=0; j<longlist.size(); j++){
					Map<String,Object> longMap = longlist.get(j);
					String ams_order_no	 	= String.valueOf(longMap.get("bizid"));
					String longMoney = longMap.get("e_trade_money").toString();
					if(bankDataMap.containsKey(ams_order_no) && shortMoney.equals(longMoney)){
						deallonglist.add(longMap);
						bankDataMap.remove(key);
					}
				}	
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		try{
			//处理对上的长款
			
			if(deallonglist.size()>0){
				for(int k=0; k< deallonglist.size() ;k++){
					final Map<String,Object> deallongmap = deallonglist.get(k);
					 EntityManagerUtil.execute(new TransactionWork<Object>() {
				  			@Override
				  			public Object doTransaction(Session session, Transaction tx)
				  					throws Exception {
				                  Query query = session.createQuery("update AMS_ErrorTradeLog set idDealwith =true where bizid=:bizid");
				                  query.setParameter("bizid", deallongmap.get("bizid").toString());
				                  query.executeUpdate();
				  				return null;
				  			}
				      });
				}
			}
			logger.info("deallonglist---"+deallonglist);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} 
		return bankDataMap;
	}
	/**
	 * 长款处理
	 * @param longlist
	 * @param settDay
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String,Object>> longRecord(List<Map<String,Object>> longlist,String settDay) throws Exception{
		List<Map<String, Object>> shortlist = null;
		List<Map<String, Object>> dealshortlist =  new ArrayList<Map<String,Object>>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(df.parse(settDay));
		cal.add(Calendar.DAY_OF_MONTH, -7); 
		final Date beginDt = cal.getTime();
		final Date endDate =  df.parse(settDay);
		try {
			shortlist =	EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> doQuery(Session session) throws Exception {// 
					 Query query = session.createQuery("from AMS_ErrorTradeLog where pay_channelNo=:pay_channelNo and trade_type=:trade_type and error_type=:error_type and idDealwith<>true and trade_time>=:beginDt and trade_time<=:endDt");
						query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_QUICK);
						query.setParameter("trade_type", Constant.TRADE_TYPE_DEPOSIT);
						query.setParameter("error_type", 2L);
						query.setParameter("beginDt", beginDt);
						query.setParameter("endDt", endDate);
	                    List<Map<String,Object>> list = query.list();
	    				return list;
				}
			});
			logger.info("longlist"+longlist);
			logger.info("shortlist"+shortlist);
			for(int i=0;i<longlist.size();i++){
				Map<String,Object> longMap = longlist.get(i);
				String long_order_no	 	= String.valueOf(longMap.get("command_no"));
				Long longMoney = (Long)longMap.get("trade_money");
				for(int j=0; j<shortlist.size(); j++){
					Map<String,Object> shortMap = shortlist.get(j);
					String 	 short_order_no	= String.valueOf(shortMap.get("bizid"));
					Long 	 shortMoney	= (Long)shortMap.get("out_trade_money");
					if(long_order_no.equals(short_order_no) && longMoney.equals(shortMoney)){
						dealshortlist.add(shortMap);
						longlist.remove(i);					//删除有老短款款数据对上的长款
						i--;
					}
				}	
			}
			
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} 
		try{
			StringBuffer sb = new StringBuffer();
			//处理对上的短款
			logger.info("dealshortlist------------"+dealshortlist);
			if(dealshortlist.size()>0){
				for(int k=0; k< dealshortlist.size() ;k++){
					final Map<String,Object> dealshortmap = dealshortlist.get(k);
					 EntityManagerUtil.execute(new TransactionWork<Object>() {
				  			@Override
				  			public Object doTransaction(Session session, Transaction tx)
				  					throws Exception {
				                  Query query = session.createQuery("update AMS_ErrorTradeLog set idDealwith =true where bizid=:bizid");
				                  query.setParameter("bizid", dealshortmap.get("bizid").toString());
				                  query.executeUpdate();
				  				return null;
				  			}
				      });
				}
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return longlist;
	}
}
