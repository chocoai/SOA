package bps.external.application.service.trade;

import bps.common.BizException;

public interface GatewayService {
	/**
	 * 网关退款
	 * @param version       版本号
	 * @param orderNo       订单号
	 * @param refundAmount  退款金额
	 * @param orderDatetime 订单的创建日期
	 * @return String       调用网关退款接口的response    
	 * @throws BizException
	 */
	public String gatewayRefund(String version, String orderNo, String refundAmount, String orderDatetime, String sysid, String payInterfaceNo) throws Exception;

	/**
	 * 查询通联网关对账数据
	 * @param date 日期 yyyy-MM-dd
	 * @return
	 * @throws Exception
	 */
	public String getCheckAccountDate(String date,String piAppConfObjApplicationStr)throws Exception;

	/**
	 * 通联移动支付 单笔订单查询接口
	 * 测试环境：http://ceshi.allinpay.com/gateway/index.do
	 * 生产环境：https://service.allinpay.com/gateway/index.do
	 * @param merchantId		商户号				必填
	 * @param version			网关查询接口版本	必填
	 * @param signType		签名类型			必填
	 * @param orderNo			商户订单号			必填
	 * @param orderDatetime	商户订单提交时间	必填
	 * @param queryDatetime	商户提交查询的时间	必填
	 * @return
	 */
	public void getAppPayOrder(String merchantId , String version, String signType ,
								 String orderNo , String orderDatetime , String queryDatetime , String mobileCertPayKey) throws Exception;
}
