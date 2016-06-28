package bps.external.application.trade;

import bps.common.*;
import com.kinorsoft.allinpay.gateway.AppPay;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.kinorsoft.allinpay.gateway.CheckFileDownLoad;

import bps.external.application.Extension;
import bps.external.application.service.trade.GatewayService;

public class GatewayServiceImpl implements GatewayService {
	 private static Logger logger = Logger.getLogger(GatewayServiceImpl.class.getName());
		private static String url = null;
		private static String key = null;
		private static String merchantId = null;

		public static void main(String[] args) throws Exception {
			gatewayRefund1("v1.3", "1604132270233362D1", "10", "20160413145251");
		}

		/**
		 * 网关退款
		 * @param version       版本号
		 * @param orderNo       商户号
		 * @param orderNo       订单号
		 * @param refundAmount  退款金额
		 * @param orderDatetime 订单的创建日期
		 * @return String       调用网关退款接口的response    
		 * @throws BizException
		 */
		public static String gatewayRefund1(String version, String orderNo, String refundAmount, String orderDatetime) throws Exception{
			try{
//				if(merchantId == null){
//					merchantId = Extension.paramMap.get("AlipayConfig.gateway.merchantId");
//				}
//				if(url == null){
//					url = Extension.paramMap.get("AlipayConfig.gateway.refund.serverUrl");
//				}
//				if(key == null){
//					key = Extension.paramMap.get("AlipayConfig.gateway.key");
//				}

				merchantId = "100020091218888";
				url = "http://ceshi.allinpay.com/gateway/index.do";
				key = "1234567890";
				
				//组装body
				StringBuffer sb = new StringBuffer();
				sb.append("version=").append(version);
				sb.append("&signType=").append("0");
				sb.append("&merchantId=").append(merchantId);
				sb.append("&orderNo=").append(orderNo);
				sb.append("&refundAmount=").append(refundAmount);
				sb.append("&orderDatetime=").append(orderDatetime);
				sb.append("&key=").append(key);
				String signMsg = MD5Util.MD5(sb.toString()).toUpperCase();
				sb.append("&signMsg=").append(signMsg);
				
//				logger.info("网关退款request：" + sb.toString());
				System.out.println("request：" + sb.toString());
				
				//发送并取得response
				String response = get(url,sb.toString());
//				logger.info(response);
				System.out.println("response：" + response);
				return response;
			} catch(Exception e) {
				throw new Exception(e.getMessage());
			}
		}

	/**
	 * 网关退款
	 * @param version       版本号
	 * @param orderNo    商户号
	 * @param orderNo       订单号
	 * @param refundAmount  退款金额
	 * @param orderDatetime 订单的创建日期
	 * @return String       调用网关退款接口的response
	 * @throws BizException
	 */
		public String gatewayRefund(String version, String orderNo, String refundAmount, String orderDatetime, String sysid, String payInterfaceNo) throws Exception{
		try{
			logger.info("gatewayRefund params:version=" + version + ",orderNo=" + orderNo + ",refundAmount=" + refundAmount + ",orderDatetime=" + orderDatetime + ",sysid=" + sysid + ",payInterfaceNo=" + payInterfaceNo );

			//获取应用的支付通道配置
			String cacheKey = payInterfaceNo + "_" + sysid;
			JSONObject payInterConf = null;
			try{
				JSONObject payInterConfAll = new JSONObject(JedisUtils.getCacheByKey(Constant.REDIS_KEY_PI_APP_CONF));
				payInterConf = (JSONObject)payInterConfAll.get(cacheKey);

			}catch(Exception e){
				logger.error(e.getMessage(), e);
				throw new Exception("应用的支付通道配置不存在");
			}

			if(url == null){
				url = Extension.paramMap.get("AlipayConfig.gateway.refund.serverUrl");
			}
			//网关
			if(Constant.PAY_INTERFACE_GETWAY_JJ.equals(payInterfaceNo)){
				merchantId = (String)payInterConf.get("merchant_id");
				key = (String) payInterConf.get("cert_key");
			}else{  //移动认证支付
				merchantId = (String)payInterConf.get("merchant_id");
				key = (String) payInterConf.get("mobile_cert_pay_key");
			}

			//			merchantId = "100020091218001";
			//			url = "http://ceshi.allinpay.com/gateway/index.do";
			//			key = "1234567890";

			//组装body
			StringBuffer sb = new StringBuffer();
			sb.append("version=").append(version);
			sb.append("&signType=").append("0");
			sb.append("&merchantId=").append(merchantId);
			sb.append("&orderNo=").append(orderNo);
			sb.append("&refundAmount=").append(refundAmount);
			sb.append("&orderDatetime=").append(orderDatetime);
			sb.append("&key=").append(key);
			String signMsg = MD5Util.MD5(sb.toString()).toUpperCase();
			sb.append("&signMsg=").append(signMsg);

			logger.info("网关退款request：" + sb.toString());

			//发送并取得response
			String response = get(url,sb.toString());
			logger.info("网关退款response：" + response);
			return response;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}
		
		//post
		private static String get(String urlStr, String postBody) throws Exception{
	    	HttpUtil client = new HttpUtil();
	        String clientStr = client.get(urlStr, postBody);
	        return clientStr;
	    }
		
		/**
		 * 查询通联网关对账数据
		 * @param date 日期 yyyy-MM-dd
		 * @return
		 * @throws Exception
		 */
		public String getCheckAccountDate(String date,String piAppConfObjApplicationStr)throws Exception {
			String checkAccountDate = new String();
			try {
				if(StringUtils.isBlank(date))
					throw new BizException(ErrorCode.PARAM_ERROR, "请传入参数date");
				checkAccountDate = CheckFileDownLoad.reconciled(date,piAppConfObjApplicationStr);
			} catch(BizException e){
				logger.error(e.getMessage(),e);
				throw new BizException(e.getErrorCode(), e.getMessage());
			}catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
			}
			return checkAccountDate;
		}

	@Override
	public void getAppPayOrder(String merchantId, String version, String signType, String orderNo,
								 String orderDatetime, String queryDatetime , String mobileCertPayKey) throws Exception {
		if(StringUtils.isBlank(merchantId)){
			throw new Exception("请传入参数merchantId");
		}
		if(StringUtils.isBlank(version)){
			throw new Exception("请传入参数version");
		}
		if(StringUtils.isBlank(signType)){
			throw new Exception("请传入参数signType");
		}
		if(StringUtils.isBlank(orderNo)){
			throw new Exception("请传入参数orderNo");
		}
		if(StringUtils.isBlank(orderDatetime)){
			throw new Exception("请传入参数orderDatetime");
		}
		if(StringUtils.isBlank(queryDatetime)){
			throw new Exception("请传入参数queryDatetime");
		}
		if(StringUtils.isBlank(mobileCertPayKey)){
			throw new Exception("请传入参数mobileCertPayKey");
		}
		AppPay appPay = new AppPay();
		try {
			appPay.getAppPayOrder(merchantId,version,signType,orderNo,orderDatetime,queryDatetime,mobileCertPayKey);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new Exception(e.getMessage());
		}
	}
}
