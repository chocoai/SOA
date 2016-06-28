package bps.web.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bps.external.soa.process.Extension;
import org.apache.log4j.Logger;

import com.alibaba.dubbo.common.utils.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import bps.common.Constant;
import bps.common.HttpUtil;
import bps.common.JedisUtils;

import static bps.external.soa.process.Extension.paramMap;
@WebServlet(name = "HealthMonitorServlet", urlPatterns = {"/HealthMonitorServlet.do"})
public class HealthMonitorServlet extends HttpServlet implements Servlet {
	private static Logger logger = Logger.getLogger(HealthMonitorServlet.class);
	
	private static String externalWeb1Url = null;
	private static String externalWeb2Url = null;
	private static String soa1Url= null;
	private static String soa2Url = null;
	private static String service1Url = null;
	private static String service2Url = null;
	private static String service3Url = null;
	private static String redis1Url = null;

	private static int redis1Port;

	private static String alarmMessageStarts 	= "[AAfAAABBQEBAQEBAQJSlNUVVZXWF Alarm Message Starts|";
	private static String alarmMessageEnd		= "|Alarm Message Ends AAfAAABBQEBAQEBAQJSlNUVVZXWF]";
	
	public void init(ServletConfig config)  throws ServletException {
		try{
//			web2Url = null;
			externalWeb1Url = paramMap.get("healthMonitor.externalWeb1.url");
			externalWeb2Url = paramMap.get("healthMonitor.externalWeb2.url");
			soa1Url			= paramMap.get("healthMonitor.soa1.url");//"http://10.55.3.236";
			soa2Url 		= paramMap.get("healthMonitor.soa2.url");
			service1Url 	= paramMap.get("healthMonitor.service1.url");//"http://10.55.3.237";
			service2Url 	= paramMap.get("healthMonitor.service2.url");
			service3Url 	= paramMap.get("healthMonitor.service3.url");
			redis1Url 		= paramMap.get("cache.ip");//"http://10.55.3.237";

			redis1Port 			= Integer.parseInt(paramMap.get("cache.port"));//6379;
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		monitor(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		monitor(request, response);
	}

	public static void monitor( HttpServletRequest request, HttpServletResponse response )throws ServletException, IOException{
		logger.info("============================HealthMonitor.monitor start=========================");

		List<Map<String, Object>> monitorResultDbList = new ArrayList<>();
//		Map<String, Object> monitorResultDb;
		
		String responseCode = Constant.HEALTH_MONITOR_RESULT_RES_SUC;

		String errorObject = "";
		
		HttpUtil httpUtil = new HttpUtil();
		
		//监测服务器是否正常运行--ext1
		String ext1CommunicateResult = httpUtil.get(externalWeb1Url,"");
		String ext1CommunicateResultLast = ext1CommunicateResult == null ? null : ext1CommunicateResult.trim();
		if("ok".equals(ext1CommunicateResultLast)){
			monitorResultDbList.add(buildMonitorResult("soa", externalWeb1Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
		}else{
			monitorResultDbList.add(buildMonitorResult("soa", externalWeb1Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));
			
			responseCode += "$$EXT1-"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
			errorObject += "$$"+externalWeb1Url;
			
//			webCommunicateWrongNum++;
		}
		//ext2
		String ext2CommunicateResult = httpUtil.get(externalWeb2Url, "");
		String ext2CommunicateResultLast = ext2CommunicateResult == null ? null : ext2CommunicateResult.trim();
		if("ok".equals(ext2CommunicateResultLast)){
			monitorResultDbList.add(buildMonitorResult("soa", externalWeb2Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
		}else{
			monitorResultDbList.add(buildMonitorResult("soa", externalWeb2Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));

			responseCode += "$$EXT2-"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
			errorObject = "$$"+externalWeb2Url;

//			webCommunicateWrongNum++;
		}
		//soa
		String soa1CommunicateResult = httpUtil.get(soa1Url, "");
		String soa1CommunicateResultLast = soa1CommunicateResult == null ? null : soa1CommunicateResult.trim();
		if("ok".equals(soa1CommunicateResultLast)){
			monitorResultDbList.add(buildMonitorResult("soa", soa1Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
		}else{
			monitorResultDbList.add(buildMonitorResult("soa", soa1Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));

			responseCode += "$$SOA1-"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
			errorObject += "$$"+soa1Url;

//			soaCommunicateWrongNum++;
		}
		//soa2
		String soa2CommunicateResult = httpUtil.get(soa2Url, "");
		String soa2CommunicateResultLast = soa2CommunicateResult == null ? null : soa2CommunicateResult.trim();
		if("ok".equals(soa2CommunicateResultLast)){
			monitorResultDbList.add(buildMonitorResult("soa", soa2Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
		}else{
			monitorResultDbList.add(buildMonitorResult("soa", soa2Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));

			responseCode += "$$SOA2-"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
			errorObject += "$$"+soa2Url;

//			soaCommunicateWrongNum++;
		}
		//service1
		String service1CommunicateResult = httpUtil.get(service1Url, "");
		String service1CommunicateResultLast = service1CommunicateResult == null ? null : service1CommunicateResult.trim();
		if("ok".equals(service1CommunicateResultLast)){
			monitorResultDbList.add(buildMonitorResult("service", service1Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
		}else{
			monitorResultDbList.add(buildMonitorResult("service", service1Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));
		
			responseCode += "$$service1-"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
			errorObject += "$$"+service1Url;
			
//			serviceCommunicateWrongNum++;
		}

		//service2
		String service2CommunicateResult = httpUtil.get(service2Url, "");
		String service2CommunicateResultLast = service2CommunicateResult == null ? null : service2CommunicateResult.trim();
		if("ok".equals(service2CommunicateResultLast)){
			monitorResultDbList.add(buildMonitorResult("service", service2Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
		}else{
			monitorResultDbList.add(buildMonitorResult("service", service2Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));

			responseCode += "$$service2-"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
			errorObject += "$$"+service1Url;

//			serviceCommunicateWrongNum++;
		}

		//service3
		String service3CommunicateResult = httpUtil.get(service3Url, "");
		String service3CommunicateResultLast = service3CommunicateResult == null ? null : service3CommunicateResult.trim();
		if("ok".equals(service3CommunicateResultLast)){
			monitorResultDbList.add(buildMonitorResult("service", service3Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
		}else{
			monitorResultDbList.add(buildMonitorResult("service", service3Url, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));

			responseCode += "$$service3-"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
			errorObject += "$$"+service1Url;

//			serviceCommunicateWrongNum++;
		}
		
		//检查redis
		if(monitorRedis(redis1Url, redis1Port)){
			monitorResultDbList.add(buildMonitorResult("redis", redis1Url+":"+redis1Port, Constant.HEALTH_MONITOR_TYPE_REDIS, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
		}else{
			monitorResultDbList.add(buildMonitorResult("redis", redis1Url+":"+redis1Port, Constant.HEALTH_MONITOR_TYPE_REDIS, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));
		
			responseCode += "$$"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_REDIS;
			errorObject += "$$"+redis1Url + ":" + redis1Port;
		}
		
		//把监测记录写入到数据库
		try{
			Extension.monitorService.addHealthMonitorRecord(monitorResultDbList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);

			responseCode += "$$"+Constant.HEALTH_MONITOR_RESULT_RES_ERR_DB;
			errorObject += "$$date";
		}

		String outString = "10000";
		if ( !responseCode.equals(Constant.HEALTH_MONITOR_RESULT_RES_SUC) ){
			logger.info("responseCode:"+responseCode);
			logger.info("errorObject:"+errorObject);
			outString = alarmMessageStarts + responseCode + alarmMessageEnd;
		}
		response.setContentLength(outString.getBytes().length);
		response.setCharacterEncoding("UTF-8");

		response.getWriter().print(outString);
		response.flushBuffer();

		logger.info("============================HealthMonitor.monitor end=========================");
	}
//	public String monitor(){
//		logger.info("============================HealthMonitor.monitor start=========================");
//
//		List<Map<String, Object>> monitorResultDbList = new ArrayList<Map<String, Object>>();
//		Map<String, Object> monitorResultDb;
//
//		String responseCode = Constant.HEALTH_MONITOR_RESULT_RES_SUC;
//		String errorInfo = "";
//		String errorType = "";
//		String errorObject = "";
//		String loggerLevel = "400803";
//
//		int webCommunicateWrongNum = 0;
//		int soaCommunicateWrongNum = 0;
//		int serviceCommunicateWrongNum = 0;
//
//		int webWarnNum = 1;
//		int soaWarnNum = 1;
//		int serviceWarnNum = 1;
//
//		int webErrorNum = 1;
//		int soaErrorNum = 1;
//		int serviceErrorNum = 1;
//
//		int webFatalNum = 1;
//		int soaFatalNum = 1;
//		int serviceFatalNum = 1;
//
//		HttpUtil httpUtil = new HttpUtil();
//
//		//监测服务器是否正常运行
//		String soa1CommunicateResult = httpUtil.get(soa1Url + ":" + soa1Port + "//" + communicateMonitorJsp, "");
//		String soa1CommunicateResultLast = soa1CommunicateResult == null ? null : soa1CommunicateResult.trim();
//		if("ok".equals(soa1CommunicateResultLast)){
//			monitorResultDbList.add(buildMonitorResult("soa", soa1Url, soa1Port, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
//		}else{
//			monitorResultDbList.add(buildMonitorResult("soa", soa1Url, soa1Port, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));
//
//			responseCode = Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
//			errorInfo = "SOA通信异常";
//			errorType = "通信异常";
//			errorObject = soa1Url + ":" + soa1Port;
//
//			webCommunicateWrongNum++;
//		}
//
//		String service1CommunicateResult = httpUtil.get(service1Url + ":" + service1Port + "//" + communicateMonitorJsp, "");
//		String service1CommunicateResultLast = service1CommunicateResult == null ? null : service1CommunicateResult.trim();
//		if("ok".equals(service1CommunicateResultLast)){
//			monitorResultDbList.add(buildMonitorResult("service", service1Url, service1Port, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
//		}else{
//			monitorResultDbList.add(buildMonitorResult("service", service1Url, service1Port, Constant.HEALTH_MONITOR_TYPE_COMMUNICATE, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));
//
//			responseCode = Constant.HEALTH_MONITOR_RESULT_RES_ERR_COM;
//			errorInfo = "应用层通信异常";
//			errorType = "通信异常";
//			errorObject = service1Url + ":" + service1Port;
//
//			serviceCommunicateWrongNum++;
//		}
//
//		//检查redis
//		if(monitorRedis(redis1Url, redis1Port)){
//			monitorResultDbList.add(buildMonitorResult("redis", redis1Url, redis1Port, Constant.HEALTH_MONITOR_TYPE_REDIS, Constant.HEALTH_MONITOR_RESULT_TYPE_SUCCESS));
//		}else{
//			monitorResultDbList.add(buildMonitorResult("redis", redis1Url, redis1Port, Constant.HEALTH_MONITOR_TYPE_REDIS, Constant.HEALTH_MONITOR_RESULT_TYPE_ERROR));
//
//			responseCode = Constant.HEALTH_MONITOR_RESULT_RES_ERR_REDIS;
//			errorInfo = "redis异常";
//			errorType = "redis异常";
//			errorObject = redis1Url + ":" + redis1Port;
//		}
//
//		//把监测记录写入到数据库
//		try{
//			//Extension.monitorService.addHealthMonitorRecord(monitorResultDbList);
//		}catch(Exception e){
//			logger.error(e.getMessage());
//
//			responseCode = Constant.HEALTH_MONITOR_RESULT_RES_ERR_DB;
//			errorInfo = "数据库异常";
//			errorType = "数据库异常";
//			errorObject = "数据库";
//		}
//
//		//返回给监测中心
//		if(webCommunicateWrongNum >= webFatalNum || soaCommunicateWrongNum >= soaFatalNum || serviceCommunicateWrongNum >= serviceFatalNum){
//			loggerLevel = "400800";
//		}else if(webCommunicateWrongNum >= webErrorNum || soaCommunicateWrongNum >= soaErrorNum || serviceCommunicateWrongNum >= serviceErrorNum){
//			loggerLevel = "400801";
//		}else if(webCommunicateWrongNum >= webWarnNum || soaCommunicateWrongNum >= soaWarnNum || serviceCommunicateWrongNum >= serviceWarnNum){
//			loggerLevel = "400802";
//		}
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		String dateStr = sdf.format(new Date());
//
//		StringBuilder sb = new StringBuilder();
//		sb.append("02").append("|");
//		sb.append("100130").append("|");
//		sb.append("云账户").append("|");
//		sb.append(dateStr.substring(0, 8)).append("|");
//		sb.append(dateStr.substring(8,14)).append("|");
//		sb.append(responseCode).append("|");
//		sb.append("").append("|");
//		sb.append(errorInfo).append("|");
//		sb.append(errorType).append("|");
//		sb.append(errorObject).append("|");
//		sb.append(loggerLevel);
//
//		String lengthStr = String.valueOf(sb.toString().length());
//		int lengthStrLength = lengthStr.length();
//		for(int i = lengthStrLength; i < 6; i++){
//			lengthStr = "0" + lengthStr;
//		}
//
//		String ret = lengthStr + sb.toString();
//
//		logger.info("返回:" + ret);
//		logger.info("============================HealthMonitor.monitor end=========================");
//		return ret;
//	}
	
	/**
	 * 组装
	 * @param serviceType	类型
	 * @param url			IP
	 * @param monitorType	检查类型
	 * @param result		结果
	 * @return	实体
	 */
	private static Map<String, Object> buildMonitorResult(String serviceType, String url, int monitorType, int result){
		Map<String, Object> ret = new HashMap<>();
		
		try{
			ret.put("url", url);
			ret.put("serviceType", serviceType);
			ret.put("monitorType", monitorType);
			ret.put("monitorResult", result);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		
		return ret;
	}
	
	/**
	 * redis监测
	 * @param ip		ip
	 * @param port		端口
	 * @return	boolean
	 */
	private static boolean monitorRedis(String ip, int port){
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
		return ret;
	}
}
