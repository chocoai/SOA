package bps.external.application.notice;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import bps.common.Constant;
import bps.common.JedisUtils;
import bps.external.application.Extension;
import org.apache.log4j.Logger;
import org.json.JSONObject;



public class BackNotice extends Thread{
	public static Logger logger = Logger.getLogger(BackNotice.class);
	public JSONObject _param =null;


	public BackNotice(JSONObject _param) {
		this._param = _param;
	}
	public void run(){
		 	try {
		 		String url = (String)_param.get("backUrl");
	            String param = (String)_param.get("param");
				String orderNo = (String)_param.get("orderNo");
	        	backNotice(URLDecoder.decode(url) ,param,orderNo);
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage(), e);
			}
	}

	/**
	 * 后台通知URL
	 * @param url		发送地址
	 * @param sb		发送的内容
	 * @param orderNo	订单号（记录发送次数）
	 * @return	空
     * @throws Exception
     */
	public String backNotice(String url,String sb, String orderNo) throws Exception{
		StringBuffer result = new StringBuffer();
		try{
			URL url1 = new URL(url);
		    HttpURLConnection httpurlconnection = (HttpURLConnection) url1.openConnection();
		    httpurlconnection.setDoOutput(true);
		    httpurlconnection.setUseCaches(false);
		    httpurlconnection.setRequestMethod("POST");

	    	byte[] bypes = sb.toString().getBytes();
		    httpurlconnection.getOutputStream().write(bypes);
		    
		    int state = httpurlconnection.getResponseCode(); 
		    logger.info("state------:"+state);
		    if(state == 200) { 
		    	logger.info("URL连接成功！");

				String redisKey = Constant.BACKGROUND_NOTICE_FAIL_AMOUNT;
				JedisUtils.hdelCache(redisKey,orderNo);
//			    BufferedReader br = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream(), "utf-8"));
//			    httpurlconnection.connect();
//
//			    String line = br.readLine();
//
//			    while(line != null){
//			    	result.append(line);
//			        line = br.readLine();
//			    }
//			    br.close();
			
		    }else{
		    	logger.error("URL连接失败！");
		    	sendDate();
		    }
		}catch(Exception e){
			logger.error("发送异常！", e);
			sendDate();
			throw e;
		}
		return  result.toString();
	}

	/**
	 * 通知信息发送到取队列
	 * 通知频率：5s,2m,10m,15m,1h,2h,6h,15h
	 * @throws Exception
     */
	public void sendDate() throws Exception{
		logger.info("-------sendDate--------\n param:"+_param);
		String orderNo = _param.optString("orderNo");
		try {
			//使用延时队列：n分钟一次，每次n+1.最多5次。
			String redisKey = Constant.BACKGROUND_NOTICE_FAIL_AMOUNT;
			logger.info("redisKey:" + redisKey);
			String amount = JedisUtils.hgetCacheByField(redisKey, orderNo);

			if (amount == null || "".equals(amount)) {
				amount = "0";
			}

			logger.info("amount:" + amount);
			Long _amount = Long.parseLong(amount) + 1;

			Long waitTime = Constant.BACKGROUND_NOTICE_TIME_LIST.get(_amount.toString());
			if (waitTime == null) {
				throw new Exception("这个订单没有通知成功，订单号：" + orderNo);
			}

			JedisUtils.hsetCache(redisKey, orderNo, _amount.toString());

			logger.info("waitTime:" + waitTime);

			String queue_name = Extension.paramMap.get("rabbitmq.queue_name");
			logger.info("queue_name:" + queue_name);

			RabbitProducer.getInstance().send(queue_name, queue_name, queue_name, _param.toString(), waitTime);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
//		sleep(60L*1000L*10L);//10分钟
//		RabbitProducer.getInstance().send(URLEncoder.encode(_param.toString(), "UTF-8"));
	}
}
