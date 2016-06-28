package bps.external.application.service.increment;

public interface OtherService {
	/**
	 * 发送短信
	 * @param mobiles
	 * @param content
	 * @throws Exception
	 */
	public void sendSM(String mobiles, String content)throws Exception;
}
