package bps.external.application.increment.other;

import org.apache.log4j.Logger;
import bps.external.application.service.increment.OtherService;

public class OtherServiceImpl implements OtherService{
	 private static Logger logger = Logger.getLogger(OtherServiceImpl.class.getName());

	/**
	 * 发送短信
	 * @param mobiles
	 * @param content
	 * @throws Exception
	 */
	public void sendSM(String mobiles, String content)throws Exception{
		if(mobiles==null){
			throw new Exception("mobiles不能为空！");
		}if(content==null){
			throw new Exception("content不能为空！");
		}
		SendSM sendSM =new SendSM(mobiles, content);
		try {
			sendSM.start();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw e;
		}
	}
}
