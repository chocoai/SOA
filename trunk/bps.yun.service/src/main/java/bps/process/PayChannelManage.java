package bps.process;
import ime.core.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.common.JedisUtils;
import bps.order.Order;
import apf.util.EntityManagerUtil;
import apf.util.SessionContext;

public class PayChannelManage{
	private static Logger logger = Logger.getLogger(PayChannelManage.class.getName());
	private static String ip = null;
	private static int port = 0;
	
	static{
		if(ip == null || port == 0){
			try {
				ip 	= Environment.instance().getSystemProperty("cache.ip");
				port	= Integer.valueOf(Environment.instance().getSystemProperty("cache.port"));
			} catch (Exception e) {
				logger.error("读取redis的ip和port出错。");
			}
		}
	}
	
	public static JSONObject getPayInterfaceInfoPure(String pay_interfaceNo)throws Exception{
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();
			String payInterfaceStr = jedis.get("payInterfaceMap");
			JSONObject payInterfaceJObj = new JSONObject(payInterfaceStr);
			Map<String, Object> payInterfaceMap = payInterfaceJObj.getMap();
			return (JSONObject)payInterfaceMap.get(pay_interfaceNo);
		} catch (Exception e) {
			// 释放redis对象
			pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}
	
	/**
	 * 获取支付通道信息
	 * @param payInterfaceNo
	 * @param sysid
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getPayInterfaceInfo(String payInterfaceNo, String orgNo) throws Exception{
		logger.info("=====================PayChannelManage.getPayInterfaceMember start===============================");
		logger.info("参数payInterfaceNo=" + payInterfaceNo + ",orgNo=" + orgNo);
		
		JedisPool pool = null;
		Jedis jedis = null;
		try{
			//获取支付通道
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();
			String payInterfaceStr = jedis.get("payInterfaceMap");
			JSONObject payInterfaceJObj = new JSONObject(payInterfaceStr);
			JSONObject payInterfaceMatchJObj = (JSONObject)payInterfaceJObj.get(payInterfaceNo);
			
			//所有应用共用一个支付通道系统账户
			if(!payInterfaceMatchJObj.isNull("system_uuid")){
				
				logger.info("=====================PayChannelManage.getPayInterfaceMember end===============================");
				return payInterfaceMatchJObj;
			}
			//各应用使用自己的支付通道系统账户
			else{
				String key = payInterfaceNo + "_" + orgNo;
				
				String piAppConf = jedis.get(Constant.REDIS_KEY_PI_APP_CONF);
				JSONObject piAppConfJObj = new JSONObject(piAppConf);
				JSONObject piAppConfMatchJObj = (JSONObject)piAppConfJObj.get(key);
				piAppConfMatchJObj.put("pay_channelNo", payInterfaceMatchJObj.get("pay_channelNo"));
				
				logger.info("=====================PayChannelManage.getPayInterfaceMember end===============================");
				return piAppConfMatchJObj;
			}
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw e;
		}finally {
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}

	public static JSONArray getPayInterfaceBankInfo(String pay_interfaceNo)throws Exception{
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();
			String payInterfaceBankStr = jedis.get("payInterfaceBankMap");
			JSONObject payInterfaceBankJObj = new JSONObject(payInterfaceBankStr);
			Map<String, List<Map<String, Object>>> payInterfaceBankMap = payInterfaceBankJObj.getMap();
			
			if(payInterfaceBankMap.get(pay_interfaceNo) == null) {
		        return null;
		    }
			return (JSONArray)payInterfaceBankMap.get(pay_interfaceNo);
		} catch (Exception e) {
			// 释放redis对象
			pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
		
	}
	/**
	 * 加载支付渠道
	 */
	public static void loadPayInterfaceInfo(){
		if(ip == null) {
			Environment environment = null;
			try {
				environment = Environment.instance();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			ip 	= environment.getSystemProperty("cache.ip");
			port	= Integer.valueOf(environment.getSystemProperty("cache.port"));
		}
		Map<String, Object> payInterfaceMap = null;
		Map<String, List<Map<String, Object>>> payInterfaceBankMap = null;
		SessionContext ctx 	= null;
		try{
			ctx           	= EntityManagerUtil.currentContext();
			Session session = ctx.getHibernateSession();
			
			Query query = session.createQuery("from AMS_PayInterface pi, AMS_PayChannel pc where pc.id=pi.pay_channel_id");
			List list = query.list();
			payInterfaceMap = new HashMap<String, Object>();
			
			for(int i=0; i<list.size(); i++){
				Object[] objs = (Object[])list.get(i);
				Map<String, Object> payInterface = (Map<String, Object>)objs[0];
				Map<String, Object> payChannel = (Map<String, Object>)objs[1];
				payInterface.put("pay_channelNo", payChannel.get("codeNo"));
				payInterfaceMap.put((String)payInterface.get("codeNo"), payInterface);
			}
			
			logger.info("payInterfaceMap:" + payInterfaceMap);
			JedisPool pool = null;
			Jedis jedis = null;
			
			query = session.createQuery("from AMS_PayInterfaceBank pib, AMS_Bank b, AMS_BankCode bc where pib.bank_code=b.bank_code and bc.bank_id=b.id");
			list = query.list();
			logger.info("payInterfaceBankMap======"+list);
			payInterfaceBankMap = new HashMap<String, List<Map<String, Object>>>();
			
			Map<Long, Map<String, Object>> bankMap = new HashMap<Long, Map<String,Object>>();
			for(int i=0; i<list.size(); i++){
                Object[] objs = (Object[])list.get(i);
                Map<String, Object> payInterfaceBank = (Map<String, Object>)objs[0];
                Map<String, Object> bank = (Map<String, Object>)objs[1];
                Map<String, Object> bankCode = (Map<String, Object>)objs[2];
                payInterfaceBank.put("logo", bank.get("logo"));
                Long payInterfaceBank_id = (Long)payInterfaceBank.get("id");
                if((((Long)bankCode.get("cart_type")).equals(Constant.BANK_CARD_CX) && payInterfaceBank.get("debit") != null && (Boolean)payInterfaceBank.get("debit")) || (((Long)bankCode.get("cart_type")).equals(Constant.BANK_CARD_XY) && payInterfaceBank.get("credit") != null && (Boolean)payInterfaceBank.get("credit"))) {
                    Map<String, Object> bank_entity = null;
                    if(bankMap.containsKey(payInterfaceBank_id)) {
                        bank_entity = bankMap.get(payInterfaceBank_id);
                        bank_entity.put("bank_code", (String)bank_entity.get("bank_code") + "," + (String)bankCode.get("bank_code"));
                    } else {
                        bank_entity = new HashMap<String, Object>();
                        bank_entity.putAll(payInterfaceBank);
                        bank_entity.put("bank_code", (String)bankCode.get("bank_code"));
                        bankMap.put(payInterfaceBank_id, bank_entity);
                    }
                }
            }
			
			Set<Long> bank_set =  bankMap.keySet();
			for(Long key : bank_set){
				Map<String, Object> payInterfaceBank = (Map<String, Object>)bankMap.get(key);
				String pay_interfaceNo = (String)payInterfaceBank.get("pay_interfaceNo");
				List<Map<String, Object>> bank_list = null;
				if(payInterfaceBankMap.containsKey(pay_interfaceNo)) {
					bank_list = payInterfaceBankMap.get(pay_interfaceNo);
					bank_list.add(payInterfaceBank);
				} else {
					bank_list = new ArrayList<Map<String, Object>>();
					bank_list.add(payInterfaceBank);
					payInterfaceBankMap.put(pay_interfaceNo, bank_list);
				}
			}

			JSONObject payInterfaceJObj = new JSONObject(payInterfaceMap);
			JSONObject payInterfaceBankJObj = new JSONObject(payInterfaceBankMap);
			try {
				pool = JedisUtils.getPool(ip, port);
				jedis = pool.getResource();
				jedis.set("payInterfaceMap", payInterfaceJObj.toString());
				jedis.set("payInterfaceBankMap", payInterfaceBankJObj.toString());
			} catch (Exception e) {
				// 释放redis对象
				pool.returnBrokenResource(jedis);
				logger.error(e.getMessage(), e);
				throw e;
			} finally {
				// 返还到连接池
				JedisUtils.returnResource(pool, jedis);
			}
			
			logger.info("payInterfaceBankMap:" + payInterfaceBankMap);
		}catch(Exception e){
			logger.info(e.getMessage(), e); 
		}finally{
			logger.info("支付通道加载成功");
			EntityManagerUtil.closeSession(ctx);
		}
	}	
	
	/**
	 * 计算手续费
	 * @param pay_interfaceNo
	 * @param money
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public static Map<String, Object> calculateFees(String pay_interfaceNo, Long money, String sysid) throws Exception{
	    logger.info("计算手续费");
	    logger.info("pay_interfaceNo=" + pay_interfaceNo + "money=" + money);
        Map<String, Object> result = new HashMap<String, Object>();
        Long fee_money = 0L;
		JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfoPure(pay_interfaceNo);
        logger.info("payInterface_entity=" + payInterface_entity);
		if(payInterface_entity != null) {
    		Long handling_type = (Long)payInterface_entity.get("handling_type");
            Long handling_mode = (Long)payInterface_entity.get("handling_mode");
    		Double handling_rate = (Double)payInterface_entity.get("handling_rate");
    		if(handling_rate == null) {
    			handling_rate = 0D;
    		}
    		Long handling_each = (Long)payInterface_entity.get("handling_each");
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
	
	/**
	 * 计算手续费-its
	 * @param pay_interfaceNo
	 * @param bank_code
     * @param card_type
     * @param money
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public static Map<String, Object> calculateFeesITS(String pay_interfaceNo, String bank_code, Long card_type, Long money) throws Exception{
        logger.info("计算手续费-its");
        logger.info("pay_interfaceNo=" + pay_interfaceNo + "bank_code=" + bank_code + "card_type=" + card_type + "money=" + money);
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
    
    /**
     * 计算手续费-万商卡
     * @param pay_interfaceNo
     * @param bizid
     * @param trade_money
     * @param session
     * @return Map<String, Object>
     * @throws Exception
     */
//    public static Map<String, Object> calculateFeesWSK(String pay_interfaceNo, String bizid, Long trade_money, Session session) throws Exception{
//        logger.info("计算手续费-万商卡");
//        logger.info("pay_interfaceNo=" + pay_interfaceNo + "bizid=" + bizid + "trade_money=" + trade_money);
//        Map<String, Object> order_entity = Order.getOrder(bizid, session);
//        Map<String, Object> result = new HashMap<String, Object>();
//        Map<String, Object>  fee_entity = Order.calculateAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), pay_interfaceNo, null, (String)order_entity.get("orgNo"), trade_money, null, session);
//        result.put("fee_money", fee_entity.get("fee"));
//        result.put("handling_mode", fee_entity.get("handling_mode"));
//        return result;
//    }
}