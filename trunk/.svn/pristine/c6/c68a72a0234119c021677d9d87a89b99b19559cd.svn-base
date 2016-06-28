package bps.external.application.service.trade;

import java.util.Map;

public interface ItsService {
	/**
	 * its签约申请
	 * @param traceNum 交易流水号
	 * @param bankCode 银行代码
	 * @param accountName 开户名
	 * @param accountCat 卡类型
	 * @param accountNo 卡号
	 * @param idNo 身份证号
	 * @param phone 手机号
	 * @param extParams		扩展参数
	 *		  	accountValiddate 卡号有效期
	 *		  	cvv2 CVV2
	 *
	 * @return Map<String, Object> 
	 * @throws Exception
	 */
	public String signApply(String traceNum, String bankCode, String accountName,
			String accountCat, String accountNo, String idNo, String phone, 
			Map<String, String> extParams) throws Exception;
	
	/**
	 * its签约短信验证码发送
	 * @param traceNum 交易流水号
	 * @param oriTraceNum 原交易流水号
	 * @param oriTransDate 原交易时间
	 * @param phone 手机号
	 * @param extParams		扩展参数
	 * @return Map<String, Object> 
	 * @throws Exception
	 */
	public String signMessageSend(String traceNum, String oriTraceNum, String oriTransDate,
			String phone, Map<String, String> extParams) throws Exception;
	
	/**
	 * its签约确认
	 * @param traceNum 交易流水号
	 * @param oriTraceNum 原交易流水号
	 * @param oriTransDate 原交易时间
	 * @param phone 手机号
	 * @param extParams		扩展参数
	 * 			verifyCode 短信验证码
	 * @return Map<String, Object> 
	 * @throws Exception
	 */
	public String signACK(String traceNum, String oriTraceNum, String oriTransDate,
			String phone, Map<String, String> extParams) throws Exception;
	
	/**
	 * its支付申请
	 * @param traceNum 交易流水号
	 * @param tradeMoney 交易金额
	 * @param contractNo 协议号
	 * @param bankCode 银行代码
	 * @param accountName 开户名
	 * @param accountCat 卡类型
	 * @param accountNo 卡号
	 * @param idNo 身份证号
	 * @param phone 手机号
	 * @param extParams		扩展参数
	 *			accountValiddate 卡号有效期
	 *			cvv2 CVV2
	 * @return Map<String, Object> 
	 * @throws Exception
	 */
	public String payApply(String traceNum, Long tradeMoney, String contractNo, String bankCode, String accountName,
			String accountCat, String accountNo, String idNo, String phone, 
			Map<String, String> extParams) throws Exception;
	
	/**
	 * its支付短信验证码发送
	 * @param traceNum 交易流水号
	 * @param oriTraceNum 原交易流水号
	 * @param oriTransDate 原交易时间
	 * @param phone 手机号
	 * @param extParams		扩展参数
	 * @return Map<String, Object> 
	 * @throws Exception
	 */
	public String payMessageSend(String traceNum, String oriTraceNum, String oriTransDate,
			String phone, Map<String, String> extParams) throws Exception;
	
	/**
	 * its支付确认
	 * @param traceNum 交易流水号
	 * @param oriTraceNum 原交易流水号
	 * @param oriTransDate 原交易时间
	 * @param phone 手机号
	 * @param extParams		扩展参数
	 * 			verifyCode 短信验证码
	 * @return Map<String, Object> 
	 * @throws Exception
	 */
	public String payACK(String traceNum, String oriTraceNum, String oriTransDate,
			String phone, Map<String, String> extParams) throws Exception;
	
	/**
	 * 调用its退款接口
	 * @param traceNum     交易订单号
	 * @param transAmount  退款金额
	 * @param currency     币种
	 * @param oriTraceNum  原交易流水号
	 * @param oriTransDate 源交易日期
	 * @param extParams
	 * 		String remark  备注		
	 * @return
	 * @throws Exception
	 */
	public String itsRefund(String traceNum, String transAmount, String currency, String oriTraceNum, String oriTransDate, Map<String, String> extParams) throws Exception;
	
	/**
     *  从FTP服务器读取文件
     * @param fileName			要下载的文件名
     * @return
     */
    public  String readFile( String fileName) throws Exception;
}
