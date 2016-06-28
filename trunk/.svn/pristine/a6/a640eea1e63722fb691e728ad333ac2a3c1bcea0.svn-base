/**   
* @Title: AppApiManage.java 
* @Package bps.process 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年12月25日 下午5:06:26 
* @version V1.0   
*/
package bps.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ime.core.Environment;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import apf.util.EntityManagerUtil;
import apf.util.SessionContext;
import bps.common.Constant;
import bps.common.JedisUtils;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年12月25日 下午5:06:26  
 */
public class AppSoaApiManage {
	private static Logger logger = Logger.getLogger(AppSoaApiManage.class);
	private static String ip = null;
	private static int port = 0;
	
//	static{
//		if(ip == null || port == 0){
//			try {
//				ip 	= Environment.instance().getSystemProperty("cache.ip");
//				port	= Integer.valueOf(Environment.instance().getSystemProperty("cache.port"));
//			} catch (Exception e) {
//				logger.error("读取redis的ip和port出错。");
//			}
//		}
//	}
	
	
	/**
	 * 加载支付渠道
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void loadSoaApiInfo(){
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
		SessionContext ctx 	= null;
		try{
			ctx           	= EntityManagerUtil.currentContext();
			Session session = ctx.getHibernateSession();
			
			Query query = session.createQuery("from AMS_AppApiConifg");
			List<Map<String,Object>> list = query.list();
			//组装数据
			Map<String,List> soaMap = new HashMap<String,List>();
			for(Map<String, Object> temp : list){
				String appNo = (String)temp.get("appNo");
				List soaList = soaMap.get(appNo);
				if(soaList==null){
					soaList = new ArrayList();
					soaList.add(temp);
					soaMap.put(appNo, soaList);
				}else{
					soaList.add(temp);
				}
			}
			JSONObject payInterfaceJObj = new JSONObject(soaMap);
			JedisPool pool = null;
			Jedis jedis = null;
			try {
				pool = JedisUtils.getPool(ip, port);
				jedis = pool.getResource();
				jedis.set(Constant.REDIS_KEY_APP_SOA_API_CONF, payInterfaceJObj.toString());
			} catch (Exception e) {
				// 释放redis对象
				pool.returnBrokenResource(jedis);
				logger.error(e.getMessage(), e);
				throw e;
			} finally {
				// 返还到连接池
				JedisUtils.returnResource(pool, jedis);
			}
			
		}catch(Exception e){
			logger.info(e.getMessage(), e); 
		}finally{
			logger.info("SOA加载成功");
			EntityManagerUtil.closeSession(ctx);
		}
	}	
}
