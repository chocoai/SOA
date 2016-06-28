package bps.web.monitor;

import javax.servlet.http.HttpUtils;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.dubbo.common.utils.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import bps.common.Constant;
import bps.common.HttpUtil;
import bps.common.JedisUtils;
import bps.web.process.Extension;
//import bps.external.soa.Extension;

public class HealthMonitor {
	private static Logger logger = Logger.getLogger(HealthMonitor.class);
	
	private static String web2Url = null;
	private static String externalWeb1Url = null;
	private static String externalWeb2Url = null;
	private static String soa1Url= null;
	private static String soa2Url = null;
	private static String service1Url = null;
	private static String service2Url = null;
	private static String service3Url = null;
	private static String redis1Url = null;
	
	private static int web2Port;
	private static int externalWeb1Port;
	private static int externalWeb2Port;
	private static int soa1Port;
	private static int soa2Port;
	private static int service1Port;
	private static int service2Port;
	private static int service3Port;
	private static int redis1Port;
	
	static{
		try{
			web2Url = null;
			externalWeb1Url = null;
			externalWeb2Url = null;
			soa1Url= "http://10.55.3.236";
			soa2Url = null;
			service1Url = "http://10.55.3.237";
			service2Url = null;
			service3Url = null;
			redis1Url = "http://10.55.3.237";
			
			web2Port = 0;
			externalWeb1Port = 0;
			externalWeb2Port = 0;
			soa1Port = 6003;
			soa2Port = 0;
			service1Port = 9000;
			service2Port = 0;
			service3Port = 0;
			redis1Port = 6379;
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	public String monitor(){
		logger.info("============================HealthMonitor.monitor start=========================");

		JSONArray monitorResultDbArray = new JSONArray();
		JSONObject monitorResultDb;
		StringBuilder sb = new StringBuilder();
		HttpUtil httpUtil = new HttpUtil();
		
		//监测服务器是否正常运行
		logger.info("1");
		if(httpUtil.get(soa1Url + ":" + soa1Port, "a=1") == "ok"){
			logger.info("2");
			monitorResultDbArray.put(buildeJSONObject("soa", soa1Url, soa1Port, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, "ok"));
			logger.info("3");
		}else{
			logger.info("4");
			monitorResultDbArray.put(buildeJSONObject("soa", soa1Url, soa1Port, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, "error"));
			logger.info("5");
		}
		
		if(httpUtil.get(service1Url + ":" + service1Port, "b=1") == "ok"){
			logger.info("6");
			monitorResultDbArray.put(buildeJSONObject("service", service1Url, service1Port, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, "ok"));
			logger.info("7");
		}else{
			logger.info("8");
			monitorResultDbArray.put(buildeJSONObject("service", service1Url, service1Port, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, "error"));
			logger.info("9");
		}
		
		//检查redis
		if(monitorRedis(redis1Url, redis1Port)){
			logger.info("10");
			monitorResultDbArray.put(buildeJSONObject("redis", redis1Url, redis1Port, Constant.HEALTH_MONITOR_TYPE_REDIS, "ok"));
			logger.info("11");
		}else{
			logger.info("12");
			monitorResultDbArray.put(buildeJSONObject("redis", redis1Url, redis1Port, Constant.HEALTH_MONITOR_TYPE_REDIS, "error"));
			logger.info("13");
		}
		
		//把监测记录写入到数据库
		try{
			logger.info("14");
			//Extension.monitorService.addHealthMonitorRecord(monitorResultDbArray.toString());
			logger.info("15");
		}catch(Exception e){
			logger.error(e.getMessage());
			
			logger.info("16");
		}
		
		//返回给监测中心
		logger.info("返回:" + sb.toString());
		logger.info("============================HealthMonitor.monitor end=========================");
		return sb.toString();
	}
	
	/**
	 * 组装
	 * @param serviceType
	 * @param ip
	 * @param port
	 * @param monitorType
	 * @param result
	 * @return
	 */
	private JSONObject buildeJSONObject(String serviceType, String ip, int port, int monitorType, String result){
		JSONObject ret = new JSONObject();
		
		try{
			ret = new JSONObject();
			ret.put("serviceType", "soa");
			ret.put("ip", soa1Url);
			ret.put("port", soa1Port);
			ret.put("monitorType", Constant.HEALTH_MONITOR_TYPE_COMMUNICATE);
			ret.put("result", "ok");
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		
		return ret;
	}
	
	/**
	 * redis监测
	 * @param ip
	 * @param port
	 * @return
	 */
	private boolean monitorRedis(String ip, int port){
		JedisPool pool = null;
		Jedis jedis = null;
		boolean ret = true;
		
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();
			String content = jedis.get(Constant.REDIS_KEY_PI_APP_CONF);
			if(StringUtils.isBlank(content)){
				ret = false;
			}
		}catch(Exception e){
			// 释放redis对象
			pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);

			ret = false;
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
		
		return false;
	}
}
