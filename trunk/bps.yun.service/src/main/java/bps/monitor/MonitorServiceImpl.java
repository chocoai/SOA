package bps.monitor;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.service.MonitorService;

public class MonitorServiceImpl implements MonitorService {
	private static Logger logger = Logger.getLogger(MonitorServiceImpl.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void addHealthMonitorRecord(List<Map<String, Object>> monitorResultList) throws Exception {
		logger.info("==========================MonitorServiceImpl.addHealthMonitorRecord start=========================");
		logger.info("参数monitorResultList=" + monitorResultList);
		
		//写入到数据库中
		try{
			if(!LoginSession.isLogined())
			    LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			
			final List<Map<String, Object>> _monitorResultList = monitorResultList;
			
			EntityManagerUtil.execute(new TransactionWork<String>() {
				@Override
				public String doTransaction(Session session, Transaction transaction)
						throws Exception {
					
					for(Map<String, Object> monitorResult : _monitorResultList){
						Map<String, String> healthMonitorEntity = new HashMap<String, String>();
						healthMonitorEntity.put("monitor_time", sdf.format(new Date()));
						healthMonitorEntity.put("url", (String)monitorResult.get("url"));
//						healthMonitorEntity.put("port", String.valueOf((Integer)monitorResult.get("port")));
						healthMonitorEntity.put("service_type", (String)monitorResult.get("serviceType"));
						healthMonitorEntity.put("monitor_type", String.valueOf((Integer)monitorResult.get("monitorType")));
						healthMonitorEntity.put("monitor_result", String.valueOf((Integer)monitorResult.get("monitorResult")));
						
						DynamicEntityService.createEntity("AMS_HealthMonitor", healthMonitorEntity, null);
					}
					
					return null;
				}
			});
		
			logger.info("==========================MonitorServiceImpl.addHealthMonitorRecord end=========================");
		}catch(Exception e){
			logger.error(e.getMessage());
			
			throw e;
		}
	}

}
