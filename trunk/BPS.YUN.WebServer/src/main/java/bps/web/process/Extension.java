package bps.web.process;

import ime.soa.SOAServiceManager;
import ime.soa.SystemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import apf.framework.Framework;
import bps.common.JedisUtils;
import bps.common.ReadXML;
//import bps.external.application.service.increment.OtherService;
//import bps.external.soa.SoaMemberService;
//import bps.external.soa.SoaOrderService;
//import bps.service.AccountService;
//import bps.service.AdjustAccountService;
//import bps.service.CodeService;
//import bps.service.GoodsService;
//import bps.service.MemberService;
//import bps.service.OrderService;
import bps.service.MonitorService;

public class Extension implements ServletContextListener {
	public static Map<String, String> paramMap;
	public Logger logger = Logger.getLogger(Exception.class);
	private static final String ENCRYPTION_TYPE = "SHA1WithRSA";
	public static List<Map<String, Object>> keyList = new ArrayList<Map<String, Object>>();
	public static MonitorService monitorService;
	
	private static String cacheIp = null;
	private static int cachePort = 0;
	
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("销毁Extension");
	}

	public void contextInitialized(ServletContextEvent sce) {
		logger.info("开始加载dubbo");
		try {
			ime.dwr.TransactionRemoter.enabled = true;
			logger.info("创建dubbo开始");
			paramMap = 					ReadXML.getParamMap();
			Framework.instance().setResourcePath(this.getClass().getResource("/").getPath());
	        ClassPathXmlApplicationContext CTX=new ClassPathXmlApplicationContext(	new String[] { "dubbo-comsumer.xml" });
	        
	        monitorService = (MonitorService)CTX.getBean("monitor");
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.info("加载dubbo完成");
	}
}
