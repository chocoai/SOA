package bps.external.application;

import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import bps.common.JedisUtils;
import bps.external.monitor.TransMonitorRabbitCousumer;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bps.common.ReadXML;
import bps.external.application.notice.RabbitCousumer;

public class Extension implements ServletContextListener {
	public static Map<String, String> paramMap;
	public Logger logger = Logger.getLogger(Exception.class);
//	public Cousumer instantCousumer = null;
	public void contextDestroyed(ServletContextEvent arg0) {
//		instantCousumer.shutdown();

		logger.info("销毁Extension");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("开始加载dubbo");
		try {
		//读配置信息
		paramMap=ReadXML.getParamMap();
		//启动dubbo 
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-provider.xml"});    
		context.start();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("加载dubbo完成");
		
		//加载消费者
		logger.info("加载消费者开始");
		RabbitCousumer cousume = new RabbitCousumer();
		cousume.start();
		//交易监测
		TransMonitorRabbitCousumer transMonitorRabbitCousumer = new TransMonitorRabbitCousumer();
		transMonitorRabbitCousumer.start();

		String ip 	= Extension.paramMap.get("cache.ip");
		int port = Integer.valueOf(Extension.paramMap.get("cache.port"));

		logger.info("加载消费者完成");
		//设置redis联接参数
		JedisUtils.setParam(ip,port);
//		CopyOfTranxServiceImplaaaa.main(null);
	}
}
