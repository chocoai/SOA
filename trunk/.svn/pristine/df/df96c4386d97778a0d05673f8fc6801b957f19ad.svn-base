package bps.process;

import ime.core.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.common.JedisUtils;
import apf.util.EntityManagerUtil;
import apf.work.QueryWork;

/**
 * 把支付通道中应用配置的信息写入到缓存中
 * @author Administrator
 *
 */
public class PayInterfaceAppConfigCache {
	private static Logger logger = Logger.getLogger(PayInterfaceAppConfigCache.class);
	private static String ip = null;
	private static int port = 0;
	
	/**
	 * 写入缓存
	 */
	public static void loadToCache() throws BizException{
		logger.info("=======================PayInterfaceAppConfigCache.loadToCache start=============================");
		
		try{
			//获取各支付通道中各应用配置信息
			Map<String, Object> payInterfaceAppConfig = getPayInterfaceAppConfig();
			JSONObject payInterfaceAppConfigObj = new JSONObject(payInterfaceAppConfig);
			
			//写入redis
			if(ip == null || port == 0){
				ip 	= Environment.instance().getSystemProperty("cache.ip");
				port	= Integer.valueOf(Environment.instance().getSystemProperty("cache.port"));
			}
			
			JedisPool pool = null;
			Jedis jedis = null;
			try{
				pool = JedisUtils.getPool(ip, port);
				jedis = pool.getResource();
				jedis.set(Constant.REDIS_KEY_PI_APP_CONF, payInterfaceAppConfigObj.toString());
			}catch(Exception e){
				// 释放redis对象
				pool.returnBrokenResource(jedis);
				logger.error(e.getMessage(), e);
				throw e;
			}finally{
				// 返还到连接池
				JedisUtils.returnResource(pool, jedis);
			}
			
			logger.info("=======================PayInterfaceAppConfigCache.loadToCache end=============================");
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
}
	
	/**
	 * 获取各支付通道中各应用配置信息
	 */
	public static Map<String, Object> getPayInterfaceAppConfig() throws BizException{
		logger.info("==============PayInterfaceAppConfigCache.getPayInterfaceAppConfig start============================");
		
		Map<String, Object> ret = new HashMap<String, Object>();
		
		try{
			//从数据库中获取数据
			List<Map<String, Object>> payInterfaceAppConfig = EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>(){

				@Override
				public List<Map<String, Object>> doQuery(Session session) throws Exception {
					String queryStr = "from AMS_AppPiConfig";
					Query query = session.createQuery(queryStr);
					List<Map<String, Object>> payInterfaceUserInfo = query.list();
					
					logger.info("AMS_PayInterfaceAppConfig内容：" + payInterfaceUserInfo);
					return payInterfaceUserInfo;
				}
			});
			
			//组装数据
			for(Map<String, Object> temp : payInterfaceAppConfig){
				JSONObject value = new JSONObject();
				
				for(Map.Entry<String, Object> entry : temp.entrySet()){
					if("pay_interface_label".equals(entry.getKey()) || "org_no".equals(entry.getKey())){
						continue;
					}
					
					value.put(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
				}
				
				String key = (String)temp.get("pay_interface_label") + "_" + (String)temp.get("org_no");
				
				ret.put(key, value);
			}
			
			logger.info("返回ret:" + ret);
			logger.info("==============PayInterfaceAppConfigCache.getPayInterfaceAppConfig end============================");
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
}
