package bps.external.application.trade.its;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import bps.common.ErrorCode;
import bps.common.HttpUtil;
import bps.common.MD5Util;
import bps.common.BizException;
import bps.external.application.Extension;
import bps.external.application.service.trade.ItsService;

public class ItsServiceImpl implements ItsService {
	private  static String memberUrl 	= "";
	private  static String KEY 		= "";
	private  static String access_id 	= "";
	private static Logger logger = Logger.getLogger(ItsServiceImpl.class.getName());
	private static Logger its_logger = Logger.getLogger(ItsServiceImpl.class.getName());
	
	static
	{
		its_logger.info("its信息初始化");
		memberUrl 	= Extension.paramMap.get("its.serverUrl");
		KEY			= Extension.paramMap.get("its.key");
		access_id		= Extension.paramMap.get("its.access_id");
	}

	public String signApply(String traceNum, String bankCode,String accountName, String accountCat, String accountNo,String idNo, String phone, Map<String, String> extParams) throws Exception{
		logger.info("signApplyextParams"+extParams);
		if(traceNum==null){
			throw new Exception("traceNum参数不能为空！");
		}
		if(bankCode==null){
			throw new Exception("bankCode参数不能为空！");
		}
		if(accountName==null){
			throw new Exception("accountName参数不能为空！");
		}
		if(accountCat==null){
			throw new Exception("accountCat参数不能为空！");
		}
		if(accountNo==null){
			throw new Exception("accountNo参数不能为空！");
		}
		if(idNo==null){
			throw new Exception("idNo参数不能为空！");
		}
		if(phone==null){
			throw new Exception("phone参数不能为空！");
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder envelope = new StringBuilder();
		String strResult;
		try {
			envelope.append("<ENVELOPE>")
				.append("<HEAD>")
					.append("<VERSION>v1.0</VERSION>")
					.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
					.append("<PAY_TYPE>05</PAY_TYPE>")
					.append("<TRANS_CODE>1001</TRANS_CODE>")
					.append("<ACCESS_ID>").append(access_id).append("</ACCESS_ID>")
					.append("<TRACE_NUM>").append(traceNum).append("</TRACE_NUM>")
					.append("<TRANS_DATE>").append(extParams.get("transDate")).append("</TRANS_DATE>")
					.append("<TRANS_TIME>").append(extParams.get("transTime")).append("</TRANS_TIME>")
				.append("</HEAD>")
				.append("<TX_INFO>")
					.append("<BANK_CODE>").append(bankCode).append("</BANK_CODE>")
					.append("<ACCT_NAME>").append(accountName).append("</ACCT_NAME>")
					.append("<ACCT_CAT>").append(accountCat).append("</ACCT_CAT>")
					.append("<ACCT_NO>").append(accountNo).append("</ACCT_NO>")
					.append("<ACCT_VALIDDATE>").append(extParams.get("acctValidDate")).append("</ACCT_VALIDDATE>")
					.append("<CVV2>").append(extParams.get("cvv2")).append("</CVV2>")
					.append("<ID_TYPE>01</ID_TYPE>")
					.append("<ID_NO>").append(idNo).append("</ID_NO>")
					.append("<PHONE_NO>").append(phone).append("</PHONE_NO>")
					.append("<EXTEND_INFO>")
						.append("<SUB_MCHT>")
							.append("<SUB_MCHT_ID>").append(access_id).append("</SUB_MCHT_ID>")
							.append("<CNL_MCHT_ID>").append(extParams.get("cnlMchtId")).append("</CNL_MCHT_ID>")
							.append("<CNL_MCHT_NAME>").append(extParams.get("cnlMchtName")).append("</CNL_MCHT_NAME>")
							.append("<CNL_MCHT_TYPE>").append(extParams.get("cnlMchtType")).append("</CNL_MCHT_TYPE>")	
						.append("</SUB_MCHT>")
					.append("</EXTEND_INFO>")
				.append("</TX_INFO>")
			.append("</ENVELOPE>");
			
			sb.setLength(0);
			sb.append("<REQUEST>")
				.append(envelope)
				.append("<SIGNATURE>")
					.append("<SIGN_TYPE>0</SIGN_TYPE>")
					.append("<SIGN_MSG>")
					.append(MD5Util.MD5(envelope.append("<KEY>").append(KEY).append("</KEY>").toString()))
					.append("</SIGN_MSG>")
				.append("</SIGNATURE>")
			.append("</REQUEST>");
			
			its_logger.info("发送报文:" + sb.toString());
			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
			NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1") };
			String str = new HttpUtil().post(memberUrl, datas);
			BASE64Decoder decoder = new BASE64Decoder();

			byte[] b = decoder.decodeBuffer(str);
			strResult = new String(b, "UTF-8");
			its_logger.info("返回报文:" + strResult);
			
			strResult = "<?xml version=\"1.1\" encoding=\"utf-8\"?>\r\n" + strResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		return strResult;
	}
	 
	public String signMessageSend(String traceNum,
			String oriTraceNum, String oriTransDate, String phone,
			Map<String, String> extParams) throws Exception {
		if(traceNum==null){
			throw new Exception("traceNum参数不能为空！");
		}if(oriTraceNum==null){
			throw new Exception("oriTraceNum参数不能为空！");
		}if(oriTransDate==null){
			throw new Exception("oriTransDate参数不能为空！");
		}if(phone==null){
			throw new Exception("phone参数不能为空！");
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder envelope = new StringBuilder();
		String strResult;
		try {
			  
			envelope.append("<ENVELOPE>")
				.append("<HEAD>")
					.append("<VERSION>v1.0</VERSION>")
					.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
					.append("<PAY_TYPE>05</PAY_TYPE>")
					.append("<TRANS_CODE>1002</TRANS_CODE>")
					.append("<ACCESS_ID>").append(access_id).append("</ACCESS_ID>")
					.append("<TRACE_NUM>").append(traceNum).append("</TRACE_NUM>")
					.append("<TRANS_DATE>").append(extParams.get("transDate")).append("</TRANS_DATE>")
					.append("<TRANS_TIME>").append(extParams.get("transTime")).append("</TRANS_TIME>")
				.append("</HEAD>")
				.append("<TX_INFO>")
					.append("<ORI_TRACE_NUM>").append(oriTraceNum).append("</ORI_TRACE_NUM>")
					.append("<ORI_TRANS_DATE>").append(oriTransDate).append("</ORI_TRANS_DATE>")
					.append("<PHONE_NO>").append(phone).append("</PHONE_NO>")
				.append("</TX_INFO>")
			.append("</ENVELOPE>");
				
			sb.setLength(0);
			sb.append("<REQUEST>")
				.append(envelope)
				.append("<SIGNATURE>")
					.append("<SIGN_TYPE>0</SIGN_TYPE>")
					.append("<SIGN_MSG>")
					.append(MD5Util.MD5(envelope.append("<KEY>").append(KEY).append("</KEY>").toString()))
					.append("</SIGN_MSG>")
				.append("</SIGNATURE>")
			.append("</REQUEST>");
			
			its_logger.info("发送报文:" + sb.toString());
			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
			NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1") };
			String str = new HttpUtil().post(memberUrl, datas);
			BASE64Decoder decoder = new BASE64Decoder();

			byte[] b = decoder.decodeBuffer(str);
			strResult = new String(b, "UTF-8");
			its_logger.info("返回报文:" + strResult);
			
			strResult = "<?xml version=\"1.1\" encoding=\"utf-8\"?>\r\n" + strResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		     throw new Exception(e.getMessage());
		
		}
		return strResult;
	}

	public String signACK(String traceNum, String oriTraceNum,
			String oriTransDate, String phone, Map<String, String> extParams)
			throws Exception {
		if(traceNum==null){
			throw new Exception("traceNum参数不能为空！");
		}if(oriTraceNum==null){
			throw new Exception("oriTraceNum参数不能为空！");
		}if(oriTransDate==null){
			throw new Exception("oriTransDate参数不能为空！");
		}if(phone==null){
			throw new Exception("phone参数不能为空！");
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder envelope = new StringBuilder();
		String strResult;
		try {
			  
			envelope.append("<ENVELOPE>")
				.append("<HEAD>")
					.append("<VERSION>v1.0</VERSION>")
					.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
					.append("<PAY_TYPE>05</PAY_TYPE>")
					.append("<TRANS_CODE>1003</TRANS_CODE>")
					.append("<ACCESS_ID>").append(access_id).append("</ACCESS_ID>")
					.append("<TRACE_NUM>").append(traceNum).append("</TRACE_NUM>")
					.append("<TRANS_DATE>").append(extParams.get("transDate")).append("</TRANS_DATE>")
					.append("<TRANS_TIME>").append(extParams.get("transTime")).append("</TRANS_TIME>")
				.append("</HEAD>")
				.append("<TX_INFO>")
					.append("<ORI_TRACE_NUM>").append(oriTraceNum).append("</ORI_TRACE_NUM>")
					.append("<ORI_TRANS_DATE>").append(oriTransDate).append("</ORI_TRANS_DATE>")
					.append("<VERIFY_CODE>").append(extParams.get("verifyCode")).append("</VERIFY_CODE>")
				.append("</TX_INFO>")
			.append("</ENVELOPE>");
				
			sb.setLength(0);
			sb.append("<REQUEST>")
				.append(envelope)
				.append("<SIGNATURE>")
					.append("<SIGN_TYPE>0</SIGN_TYPE>")
					.append("<SIGN_MSG>")
					.append(MD5Util.MD5(envelope.append("<KEY>").append(KEY).append("</KEY>").toString()))
					.append("</SIGN_MSG>")
				.append("</SIGNATURE>")
			.append("</REQUEST>");
			
			its_logger.info("发送报文:" + sb.toString());
			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
			NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1") };
			String str = new HttpUtil().post(memberUrl, datas);
			BASE64Decoder decoder = new BASE64Decoder();

			byte[] b = decoder.decodeBuffer(str);
			strResult = new String(b, "UTF-8");
			its_logger.info("返回报文:" + strResult);
			
			strResult = "<?xml version=\"1.1\" encoding=\"utf-8\"?>\r\n" + strResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		     throw new Exception(e.getMessage());
		}
		return strResult;
	}

	public String payApply(String traceNum, Long tradeMoney,
			String contractNo, String bankCode, String accountName,
			String accountCat, String accountNo, String idNo, String phone,
			Map<String, String> extParams) throws Exception {
		
		logger.info("payApplyextParams"+extParams);
		if(traceNum==null){
			throw new Exception("traceNum参数不能为空！");
		}if(tradeMoney==null){
			throw new Exception("tradeMoney参数不能为空！");
		}if(contractNo==null){
			throw new Exception("contractNo参数不能为空！");
		}if(bankCode==null){
			throw new Exception("bankCode参数不能为空！");
		}if(accountName==null){
			throw new Exception("accountName参数不能为空！");
		}if(accountCat==null){
			throw new Exception("accountCat参数不能为空！");
		}if(accountNo==null){
			throw new Exception("accountNo参数不能为空！");
		}if(idNo==null){
			throw new Exception("idNo参数不能为空！");
		}if(phone==null){
			throw new Exception("phone参数不能为空！");
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder envelope = new StringBuilder();
		String strResult;
		try {
			envelope.append("<ENVELOPE>")
				.append("<HEAD>")
					.append("<VERSION>v1.0</VERSION>")
					.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
					.append("<PAY_TYPE>05</PAY_TYPE>")
					.append("<TRANS_CODE>2011</TRANS_CODE>")
					.append("<ACCESS_ID>").append(access_id).append("</ACCESS_ID>")
					.append("<TRACE_NUM>").append(traceNum).append("</TRACE_NUM>")
					.append("<TRANS_DATE>").append(extParams.get("transDate")).append("</TRANS_DATE>")
					.append("<TRANS_TIME>").append(extParams.get("transTime")).append("</TRANS_TIME>")
				.append("</HEAD>")
				.append("<TX_INFO>")
					.append("<TRANS_AMOUNT>").append(tradeMoney.toString()).append("</TRANS_AMOUNT>")
					.append("<CURRENCY>156</CURRENCY>")
					.append("<BANK_CODE>").append(bankCode).append("</BANK_CODE>")
					.append("<ACCT_NAME>").append(accountName).append("</ACCT_NAME>")
					.append("<ACCT_CAT>").append(accountCat).append("</ACCT_CAT>")
					.append("<ACCT_NO>").append(accountNo).append("</ACCT_NO>")
					.append("<ACCT_VALIDDATE>").append(extParams.get("acctValidDate")).append("</ACCT_VALIDDATE>")
					.append("<CVV2>").append(extParams.get("cvv2")).append("</CVV2>")
					.append("<ID_TYPE>01</ID_TYPE>")
					.append("<ID_NO>").append(idNo).append("</ID_NO>")
					.append("<PHONE_NO>").append(phone).append("</PHONE_NO>")
					.append("<CONTRACT_NO>").append(contractNo).append("</CONTRACT_NO>")
					.append("<EXTEND_INFO>")
						.append("<ORDER_INFO></ORDER_INFO>")
						.append("<SUB_MCHT>")
							.append("<SUB_MCHT_ID>").append(access_id).append("</SUB_MCHT_ID>")
							.append("<CNL_MCHT_ID>").append(extParams.get("cnlMchtId")).append("</CNL_MCHT_ID>")
							.append("<CNL_MCHT_NAME>").append(extParams.get("cnlMchtName")).append("</CNL_MCHT_NAME>")
							.append("<CNL_MCHT_TYPE>").append(extParams.get("cnlMchtType")).append("</CNL_MCHT_TYPE>")	
						.append("</SUB_MCHT>")
					.append("</EXTEND_INFO>")
					.append("<REMARK></REMARK>")
				.append("</TX_INFO>")
			.append("</ENVELOPE>");
				
			sb.setLength(0);
			sb.append("<REQUEST>")
				.append(envelope)
				.append("<SIGNATURE>")
					.append("<SIGN_TYPE>0</SIGN_TYPE>")
					.append("<SIGN_MSG>")
					.append(MD5Util.MD5(envelope.append("<KEY>").append(KEY).append("</KEY>").toString()))
					.append("</SIGN_MSG>")
				.append("</SIGNATURE>")
			.append("</REQUEST>");
			
			its_logger.info("发送报文:" + sb.toString());
			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
			NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1") };
			String str = new HttpUtil().post(memberUrl, datas);
			BASE64Decoder decoder = new BASE64Decoder();

			byte[] b = decoder.decodeBuffer(str);
			strResult = new String(b, "UTF-8");
			its_logger.info("返回报文:" + strResult);
			
			strResult = "<?xml version=\"1.1\" encoding=\"utf-8\"?>\r\n" + strResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		     throw new Exception(e.getMessage());
		
		}
		return strResult;
	}

	public String payMessageSend(String traceNum,
			String oriTraceNum, String oriTransDate, String phone,
			Map<String, String> extParams) throws Exception {
		if(traceNum==null){
			throw new Exception("traceNum参数不能为空！");
		}if(oriTraceNum==null){
			throw new Exception("oriTraceNum参数不能为空！");
		}if(oriTransDate==null){
			throw new Exception("oriTransDate参数不能为空！");
		}if(phone==null){
			throw new Exception("phone参数不能为空！");
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder envelope = new StringBuilder();
		String strResult;
		try {
			  
			envelope.append("<ENVELOPE>")
				.append("<HEAD>")
					.append("<VERSION>v1.0</VERSION>")
					.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
					.append("<PAY_TYPE>05</PAY_TYPE>")
					.append("<TRANS_CODE>2012</TRANS_CODE>")
					.append("<ACCESS_ID>").append(access_id).append("</ACCESS_ID>")
					.append("<TRACE_NUM>").append(traceNum).append("</TRACE_NUM>")
					.append("<TRANS_DATE>").append(extParams.get("transDate")).append("</TRANS_DATE>")
					.append("<TRANS_TIME>").append(extParams.get("transTime")).append("</TRANS_TIME>")
				.append("</HEAD>")
				.append("<TX_INFO>")
					.append("<ORI_TRACE_NUM>").append(oriTraceNum).append("</ORI_TRACE_NUM>")
					.append("<ORI_TRANS_DATE>").append(oriTransDate).append("</ORI_TRANS_DATE>")
					.append("<PHONE_NO>").append(phone).append("</PHONE_NO>")
				.append("</TX_INFO>")
			.append("</ENVELOPE>");
				
			sb.setLength(0);
			sb.append("<REQUEST>")
				.append(envelope)
				.append("<SIGNATURE>")
					.append("<SIGN_TYPE>0</SIGN_TYPE>")
					.append("<SIGN_MSG>")
					.append(MD5Util.MD5(envelope.append("<KEY>").append(KEY).append("</KEY>").toString()))
					.append("</SIGN_MSG>")
				.append("</SIGNATURE>")
			.append("</REQUEST>");
			
			its_logger.info("发送报文:" + sb.toString());
			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
			NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1") };
			String str = new HttpUtil().post(memberUrl, datas);
			BASE64Decoder decoder = new BASE64Decoder();

			byte[] b = decoder.decodeBuffer(str);
			strResult = new String(b, "UTF-8");
			its_logger.info("返回报文:" + strResult);
			
			strResult = "<?xml version=\"1.1\" encoding=\"utf-8\"?>\r\n" + strResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		     throw new Exception(e.getMessage());
		
		}
		return strResult;
	}

	public String payACK(String traceNum, String oriTraceNum,
			String oriTransDate, String phone, Map<String, String> extParams)
			throws Exception {
			if(traceNum==null){
				throw new Exception("traceNum参数不能为空！");
			}if(oriTraceNum==null){
				throw new Exception("oriTraceNum参数不能为空！");
			}if(oriTransDate==null){
				throw new Exception("oriTransDate参数不能为空！");
			}if(phone==null){
				throw new Exception("phone参数不能为空！");
			}
			StringBuilder sb = new StringBuilder();
			StringBuilder envelope = new StringBuilder();
			String strResult;
			try {
				  
				envelope.append("<ENVELOPE>")
				.append("<HEAD>")
					.append("<VERSION>v1.0</VERSION>")
					.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
					.append("<PAY_TYPE>05</PAY_TYPE>")
					.append("<TRANS_CODE>2013</TRANS_CODE>")
					.append("<ACCESS_ID>").append(access_id).append("</ACCESS_ID>")
					.append("<TRACE_NUM>").append(traceNum).append("</TRACE_NUM>")
					.append("<TRANS_DATE>").append(extParams.get("transDate")).append("</TRANS_DATE>")
					.append("<TRANS_TIME>").append(extParams.get("transTime")).append("</TRANS_TIME>")
				.append("</HEAD>")
				.append("<TX_INFO>")
					.append("<ORI_TRACE_NUM>").append(oriTraceNum).append("</ORI_TRACE_NUM>")
					.append("<ORI_TRANS_DATE>").append(oriTransDate).append("</ORI_TRANS_DATE>")
					.append("<VERIFY_CODE>").append(extParams.get("verifyCode")).append("</VERIFY_CODE>")
				.append("</TX_INFO>")
			.append("</ENVELOPE>");
					
					sb.setLength(0);
					sb.append("<REQUEST>")
						.append(envelope)
						.append("<SIGNATURE>")
							.append("<SIGN_TYPE>0</SIGN_TYPE>")
							.append("<SIGN_MSG>")
							.append(MD5Util.MD5(envelope.append("<KEY>").append(KEY).append("</KEY>").toString()))
							.append("</SIGN_MSG>")
						.append("</SIGNATURE>")
					.append("</REQUEST>");
					
					its_logger.info("发送报文:" + sb.toString());
					BASE64Encoder encoder = new BASE64Encoder();
					String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
					NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1") };
					String str = new HttpUtil().post(memberUrl, datas);
					BASE64Decoder decoder = new BASE64Decoder();

						byte[] b = decoder.decodeBuffer(str);
						strResult = new String(b, "UTF-8");
						its_logger.info("返回报文:" + strResult);
						
						Map<String, String> resultMap = new HashMap<String, String>();
						strResult = "<?xml version=\"1.1\" encoding=\"utf-8\"?>\r\n" + strResult;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			    throw new Exception(e.getMessage());
			}
			return strResult;
	}
	

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
	 * @throws BizException
	 */
	public String itsRefund(String traceNum, String transAmount, String currency, String oriTraceNum, String oriTransDate, Map<String, String> extParams) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		String transDate	= sdf.format(now);
		sdf = new SimpleDateFormat("HHmmss");
		String transTime	= sdf.format(now);

		StringBuilder envelope = new StringBuilder();
		envelope.append("<ENVELOPE>")
			.append("<HEAD>")
				.append("<VERSION>v1.0</VERSION>")
				.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
				.append("<PAY_TYPE>01</PAY_TYPE>")
				.append("<TRANS_CODE>6001</TRANS_CODE>")
				.append("<ACCESS_ID>").append(access_id).append("</ACCESS_ID>")
				.append("<TRACE_NUM>").append(traceNum).append("</TRACE_NUM>")
				.append("<TRANS_DATE>").append(transDate).append("</TRANS_DATE>")
				.append("<TRANS_TIME>").append(transTime).append("</TRANS_TIME>")
			.append("</HEAD>")
			.append("<TX_INFO>")
				.append("<TRANS_AMOUNT>").append(transAmount).append("</TRANS_AMOUNT>")
				.append("<CURRENCY>").append(currency).append("</CURRENCY>")
				.append("<ORI_TRACE_NUM>").append(oriTraceNum).append("</ORI_TRACE_NUM>")
				.append("<ORI_TRANS_DATE>").append(oriTransDate).append("</ORI_TRANS_DATE>")
				.append("<REMARK>").append(extParams.get("remark")).append("</REMARK>")
			.append("</TX_INFO>")
		.append("</ENVELOPE>");
		
		try { 
			StringBuilder sb = new StringBuilder();
			sb.setLength(0);
			sb.append("<REQUEST>")
				.append(envelope)
				.append("<SIGNATURE>")
					.append("<SIGN_TYPE>0</SIGN_TYPE>")
					.append("<SIGN_MSG>")
					.append(MD5Util.MD5(envelope.append("<KEY>").append(KEY).append("</KEY>").toString()).toUpperCase())
					.append("</SIGN_MSG>")
				.append("</SIGNATURE>")
			.append("</REQUEST>");
			System.out.println(sb.toString());
			logger.info("发送报文:" + sb.toString());
			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
			NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1")};
			String str = new HttpUtil().post(memberUrl, datas);
			BASE64Decoder decoder = new BASE64Decoder();
		
			byte[] b = decoder.decodeBuffer(str);
			String strResult = new String(b, "UTF-8");
			logger.info("返回报文:" + strResult);
			
			Map<String, String> resultMap = new HashMap<String, String>();
			strResult = "<?xml version=\"1.1\" encoding=\"utf-8\"?>\r\n" + strResult;
			
			return strResult;
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	public String readFile(String fileNameAfter) throws Exception {
		String encode = Extension.paramMap.get("its.checkAccount.encode");
		String url = Extension.paramMap.get("its.checkAccount.url");
		String port = Extension.paramMap.get("its.checkAccount.port");
		String username = Extension.paramMap.get("its.checkAccount.username");
		String password = Extension.paramMap.get("its.checkAccount.password");
		String localPath = Extension.paramMap.get("its.checkAccount.localPath");
		String fileName_pre = Extension.paramMap.get("its.checkAccount.fileName");
		String fileName = fileName_pre + fileNameAfter +"_1.csv";
		logger.info("encode"+encode+"url"+url+"port"+port+"username"+username+"password"+password+"localPath"+localPath+"fileName"+fileName);
		// TODO Auto-generated method stub
		String str ="";
		try{
			FtpUtils ftp = new FtpUtils(encode,url,Integer.valueOf(port),username,password);
			ftp.setPath(localPath);
			ftp.setFileName(localPath +"/"+fileName);
			str = ftp.readFile();
			ftp.closeServer();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		return str;
	}
}
