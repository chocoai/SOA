package bps.external.tradecommand;



import ime.core.Environment;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;
import ime.security.util.TripleDES;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.code.CodeServiceImpl;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.HttpUtil;
import bps.common.MD5Util;
import bps.member.Member;
import bps.member.MemberServiceImpl;
import bps.order.OrderServiceImpl;
import bps.process.Extension;
import bps.process.RiskUser;

import com.kinorsoft.ams.services.TradeService;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ItsManage {
	private static Logger its_logger = Logger.getLogger("its");

	private  static String memberUrl 	= "";
	private  static String KEY 		= "";
	private  static String access_id 	= "";
	
	private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	
	static
	{
		Environment environment = null;
		try {
			environment = Environment.instance();
		} catch (Exception e) {
			its_logger.error(e.getMessage(), e);
		}
		its_logger.info("its信息初始化");
		memberUrl 	= environment.getSystemProperty("its.serverUrl");
		KEY			= environment.getSystemProperty("its.key");
		access_id		= environment.getSystemProperty("its.access_id");
	}
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat format0 = new SimpleDateFormat("HHmmss");
	
	/**
	 * 签约申请
	 * @param params{acct_name:用户名称,acct_cat:账户类型,acct_no:银行卡号,id_no:身份证,phone_no:手机号码}
	 * @return Map
	 * @throws Exception
	 */
	public static Map<String, String> signApply(Map<String, String> params) throws Exception{
		OrderServiceImpl impl = new OrderServiceImpl();
		its_logger.info("ItsManage.signApply start");
		its_logger.info("params:" + params);
		//验证参数
		String BANK_CODE	= params.get("BANK_CODE");	//银行代码
		if(BANK_CODE == null)	
			throw new Exception("请掺入参数 BANK_CODE");
		
		BANK_CODE = impl.getITSBankCode(BANK_CODE);
		
		String ACCT_NAME	= params.get("ACCT_NAME");	//账号姓名
		if(ACCT_NAME == null)
			throw new Exception("请掺入参数 ACCT_NAME");
		String ACCT_CAT		= params.get("ACCT_CAT");	//账户类型
		if(ACCT_CAT == null)
			throw new Exception("请掺入参数 ACCT_CAT");
		
		String ACCT_VALIDDATE = null;
		String CVV2	 = null;
		if(ACCT_CAT.equals(Constant.BANK_CARD_CX.toString())){
			ACCT_CAT = "0" + ACCT_CAT;
			ACCT_VALIDDATE = "";
			CVV2 = "";
		}else if(ACCT_CAT.equals(Constant.BANK_CARD_XY.toString())){
			ACCT_CAT = "0" + ACCT_CAT;
			ACCT_VALIDDATE	= params.get("ACCT_VALIDDATE");
			if(ACCT_VALIDDATE == null)
				throw new Exception("请传入参数：" + ACCT_VALIDDATE);
			CVV2			= params.get("CVV2");
			if(CVV2 == null)
				throw new Exception("请传入参数：" + CVV2);
		}
		
		String ACCT_NO		= params.get("ACCT_NO");	//账号
		if(ACCT_NO == null)
			throw new Exception("请掺入参数 ACCT_NO");

        if(!RiskUser.checkRiskUserInfo("riskBankCardNo", ACCT_NO)) {
            throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
        }
        
//		String account_type_id		= params.get("ACCOUNT_TYPE_ID");	//账号
//		if(account_type_id == null)
//			throw new Exception("请掺入参数 account_type_id");
//		String account_type_label		= params.get("ACCOUNT_TYPE_LABEL");	//账号
//		if(account_type_label == null)
//			throw new Exception("请掺入参数 account_type_label");
		
		
		String ID_NO			= params.get("ID_NO");		//证件号码
		
		
		String ID_NO_ENCRYPT 	= params.get("ID_NO_ENCRYPT");
		String ID_NO_MASK		= params.get("ID_NO_MASK");
		
		String PHONE_NO		= params.get("PHONE_NO");	//手机号码
		if(PHONE_NO == null)
			throw new Exception("请掺入参数 PHONE_NO");
		String bank_name	= params.get("BANK_NAME");
		if(bank_name == null)
			throw new Exception("请传入参数 BANK_NAME");
			
		String userId	= params.get("userId");
		if(userId == null)
			throw new Exception("请传入参数 userId");

		String CNL_MCHT_NAME	= params.get("CNL_MCHT_NAME");
		if(CNL_MCHT_NAME == null)
			CNL_MCHT_NAME = "";

		String CNL_MCHT_TYPE	= params.get("CNL_MCHT_TYPE");
		if(CNL_MCHT_TYPE == null)
			CNL_MCHT_TYPE = "";

		String CNL_MCHT_ID	= params.get("CNL_MCHT_ID");
		if(CNL_MCHT_ID == null)
			CNL_MCHT_ID = "";
		
		StringBuilder sb = new StringBuilder();
		if(ID_NO == null || ID_NO.equals("")){	//解密
			if(ID_NO_MASK==null || ID_NO_ENCRYPT==null)
				throw new Exception("请传入证件号码");
			ID_NO	= TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, ID_NO_ENCRYPT);
		}else{	//加密+掩码
			ID_NO_ENCRYPT = TripleDES.encrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, ID_NO);
			//身份证号码掩码
			sb.setLength(0);
			sb.append(ID_NO.substring(0, 1));
			for(int i = 1, j = ID_NO.length() - 1; i < j; i ++)
				sb.append("*");
			sb.append(ID_NO.substring(ID_NO.length() - 1));
			ID_NO_MASK	= sb.toString();
		}
		if(!RiskUser.checkRiskUserInfo("riskCertificateID", ID_NO)) {
            throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
        }

		//卡号掩码
		sb.setLength(0);
		for(int i = 0, j = ACCT_NO.length() - 4; i < j; i ++) {
			sb.append("*");
		}
		sb.append(ACCT_NO.substring(ACCT_NO.length() - 4));
		String ACCT_NO_MASK = sb.toString();	
		//卡号加密
		String ACCT_NO_ENCRYPT	= TripleDES.encrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, ACCT_NO);
		
		//组装数据
		Map<String, String> signlogMap = new HashMap<String, String>();
		
		Date now  = new Date();
		String trans_date	= format.format(now);
		String trans_time	= format0.format(now);
		
		signlogMap.put("userId", 				userId);
		signlogMap.put("bank_code", 			BANK_CODE);
		signlogMap.put("accountNo", 			ACCT_NO_MASK);
		signlogMap.put("account_name", 			ACCT_NAME);
		signlogMap.put("identity_cardNo", 		ID_NO_MASK);
		signlogMap.put("card_type", 			ACCT_CAT);
		signlogMap.put("accountNo_encrypt", 	ACCT_NO_ENCRYPT);
		signlogMap.put("identity_cardNo_encrypt", ID_NO_ENCRYPT);
		signlogMap.put("phone", 				PHONE_NO);
		signlogMap.put("acct_validdate", 		ACCT_VALIDDATE);
		signlogMap.put("cvv2", 					CVV2);
		signlogMap.put("sign_type", 			"1");
		signlogMap.put("trans_date", 			trans_date);
		signlogMap.put("trans_time", 			trans_time);
		signlogMap.put("acct_cat", 				ACCT_CAT);
//		signlogMap.put("account_type_id", 		account_type_id);
//		signlogMap.put("account_type_label", 	account_type_label);
		signlogMap.put("bank_name", 			bank_name);
		
		if(params.get("isSafeCard") != null ){
			signlogMap.put("is_safe_cards", 			params.get("isSafeCard"));
		}
		//创建签约记录
		Map<String, Object> signlog_entity = ItsManage.createSignLog(signlogMap);
		String trace_num	= (String)signlog_entity.get("trace_num");
		
		Map<String, String> extParams = new HashMap<String, String>();
		extParams.put("transDate", trans_date);
		extParams.put("transTime", trans_time);
		extParams.put("acctValidDate", ACCT_VALIDDATE);
		extParams.put("cvv2", CVV2);
		extParams.put("cnlMchtId", CNL_MCHT_ID);
		extParams.put("cnlMchtName", CNL_MCHT_NAME);
		extParams.put("cnlMchtType", CNL_MCHT_TYPE);
		String strResult = Extension.itsService.signApply(trace_num, BANK_CODE, ACCT_NAME, ACCT_CAT, ACCT_NO, ID_NO, PHONE_NO, extParams);
		//获取外部返回信息，组装信息
		try { 
			Map<String, String> resultMap = new HashMap<String, String>();
			Document document = DocumentHelper.parseText(strResult);
			Element rootEl = document.getRootElement();
			
			
			//修改的签约记录信息
			Map<String, String> modifySignlogMap = new HashMap<String, String>();
			modifySignlogMap.put("id", 		signlog_entity.get("id").toString());
			
			//头响应码
			Element e_HEAD_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_CODE");
			if(e_HEAD_RET_CODE != null){
				resultMap.put("RET_CODE", 		e_HEAD_RET_CODE.getText());
				modifySignlogMap.put("ret_code", 		e_HEAD_RET_CODE.getText());
			}
			
			resultMap.put("TRACE_NUM", trace_num);
			resultMap.put("TRANS_DATE", trans_date);
			
			//头响应信息
			Element e_HEAD_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_MSG");
			if(e_HEAD_RET_MSG != null){
				resultMap.put("RET_MSG", 		e_HEAD_RET_MSG.getText());
				modifySignlogMap.put("ret_msg", 		e_HEAD_RET_MSG.getText());
			}
			//交易流水号
			Element e_ITS_TRACE_NUM = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_NUM");
			if(e_ITS_TRACE_NUM != null){
				resultMap.put("ITS_TRACE_NUM", 			e_ITS_TRACE_NUM.getText());
				modifySignlogMap.put("its_trace_num", 	e_ITS_TRACE_NUM.getText());
			}
			//签约协议号
			Element e_CONTRACT_NO = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CONTRACT_NO");
			if(e_CONTRACT_NO != null){
				resultMap.put("CONTRACT_NO", 	e_CONTRACT_NO.getText());
				modifySignlogMap.put("bank_contract_no", 	e_CONTRACT_NO.getText());
			}
			//响应码
			Element e_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_CODE");
			if(e_RET_CODE != null){
				resultMap.put("RET_CODE", 		e_RET_CODE.getText());
				modifySignlogMap.put("ret_code", 	e_RET_CODE.getText());
			}
			//响应信息
			Element e_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_MSG");
			if(e_RET_MSG != null){
				resultMap.put("RET_MSG", 		e_RET_MSG.getText());
				modifySignlogMap.put("ret_msg", 		e_RET_MSG.getText());
			}
			//its交易日期
			Element e_ITS_TRANS_DATE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_DATE");
			if(e_ITS_TRANS_DATE != null){
				resultMap.put("ITS_TRANS_DATE", 		e_ITS_TRANS_DATE.getText());
				modifySignlogMap.put("its_trans_date", 		e_ITS_TRANS_DATE.getText());
			}
			//its交易时间
			Element e_ITS_TRANS_TIME = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_TIME");
			if(e_ITS_TRANS_TIME != null){
				resultMap.put("ITS_TRANS_TIME", 			e_ITS_TRANS_TIME.getText());
				modifySignlogMap.put("its_trans_time", 		e_ITS_TRANS_TIME.getText());
			}
			//签约渠道ID
			Element e_CNL_ID	= (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CNL_ID");
			if(e_CNL_ID != null){
				resultMap.put("CNL_ID", e_CNL_ID.getText());
				modifySignlogMap.put("cnl_id", 		e_CNL_ID.getText());
			}
			//发送短信状态
			Element e_SEND_SMS	= (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/SEND_SMS");
			if(e_SEND_SMS != null){
				resultMap.put("SEND_SMS", 				e_SEND_SMS.getText());
				modifySignlogMap.put("send_sms", 		e_SEND_SMS.getText());
				Boolean is_sms_verify = true;
				if(e_SEND_SMS.getText().equals("3")){	//发送短信
					String payType = params.get("payType");
					if(payType == null || payType.equals("1")|| payType.equals("4")) {//3免验证码
						Map<String, Object> temp = new HashMap<String, Object>();
						temp.put("phone", PHONE_NO);
						temp.put("bank_name", bank_name);
						temp.put("accountNo", ACCT_NO_MASK);
						temp.put("account_name", ACCT_NAME);
						temp.put("verification_code_type", Constant.VERIFICATION_CODE_AUTH_SIGN);
						temp.put("member_id", Long.valueOf(params.get("member_id")));
						CodeServiceImpl codeServiceImpl = new CodeServiceImpl();
						String content = codeServiceImpl.generatePhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN,PHONE_NO, Constant.VERIFICATION_CODE_AUTH_SIGN, temp);
						Extension.otherService.sendSM(PHONE_NO, content);
						is_sms_verify = false;
					}
				}
				impl.modifyITSBankSMSSender(is_sms_verify, BANK_CODE);
			}
			
			//错误信息
			Element e_ERROR_CODE = (Element)rootEl.selectSingleNode("/ERROR/ERROR_CODE");
			if(e_ERROR_CODE != null){
				resultMap.put("ERROR_CODE", 		e_ERROR_CODE.getText());
				modifySignlogMap.put("error_code", 	e_ERROR_CODE.getText());
			}
			Element e_ERROR_MSG = (Element)rootEl.selectSingleNode("/ERROR/ERROR_MSG");
			if(e_ERROR_CODE != null){
				resultMap.put("ERROR_MSG", 			e_ERROR_MSG.getText());
				modifySignlogMap.put("error_msg", 	e_ERROR_MSG.getText());
			}
			//修改签约记录
			ItsManage.modifySignLog(modifySignlogMap);
			its_logger.info("返回信息:" + resultMap);			
			
			return resultMap;
		} catch (BizException e) {
			its_logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}catch (Exception e){ 
			its_logger.info(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * 签约短信验证码发送
	 * @param params{ori_trace_num:交易（签约申请）的交易流水号,ori_trans_date:原交易（签约申请）的交易日期,phone_no:电话号码}
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> signMessageSend(Map<String, String> params) throws Exception{
		its_logger.info("ItsManage.signMessageSend start");
		CodeServiceImpl codeServiceImpl = new CodeServiceImpl();
		its_logger.info("params:" + params);
		String ORI_TRACE_NUM	= params.get("ORI_TRACE_NUM");
		if(ORI_TRACE_NUM == null)
			throw new Exception("请传入参数 ORI_TRACE_NUM");
		String ORI_TRANS_DATE	= params.get("ORI_TRANS_DATE");
		if(ORI_TRANS_DATE == null)
			throw new Exception("请传入参数 ORI_TRANS_DATE");
		String PHONE_NO			= params.get("PHONE_NO");
		if(PHONE_NO == null)
			throw new Exception("请传入参数 PHONE_NO");

		Map<String, String> signlogMap = new HashMap<String, String>();
		String userId	= params.get("userId");
		if(userId == null)
			throw new Exception("请传入参数 userId");

		//查询签约申请记录
		Map<String, Object> ori_entity = ItsManage.getAuthPayLog(ORI_TRACE_NUM);
		its_logger.info("ori_entity="+ori_entity);
		if(ori_entity == null)
			throw new Exception("请传入有效的 ORI_TRACE_NUM");
		if("359037".equals(ori_entity.get("ret_code"))) {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("phone", PHONE_NO);
			temp.put("bank_name", (String)ori_entity.get("bank_name"));
			temp.put("accountNo", (String)ori_entity.get("accountNo"));
			temp.put("account_name", (String)ori_entity.get("account_name"));
			temp.put("verification_code_type", Constant.VERIFICATION_CODE_AUTH_SIGN);
			temp.put("member_id", Long.valueOf((String)params.get("member_id")));

			String content = codeServiceImpl.generatePhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN,PHONE_NO, Constant.VERIFICATION_CODE_AUTH_SIGN, temp);
			Extension.otherService.sendSM(PHONE_NO, content);

			Map<String, String> resultMap = new HashMap<String, String>();
			resultMap.put("RET_CODE", 		"359037");
			its_logger.info("返回信息:" + resultMap);
			return resultMap;
		} else {
			Date now  = new Date();
			String trans_date	= format.format(now);
			String trans_time	= format0.format(now);
			
			signlogMap.put("userId", 				userId);
			signlogMap.put("ori_trace_num", 		ORI_TRACE_NUM);
			signlogMap.put("ori_trans_date", 		ORI_TRANS_DATE);
			signlogMap.put("phone", 				PHONE_NO);
			signlogMap.put("sign_type", 			"2");
			signlogMap.put("trans_date", 			trans_date);
			signlogMap.put("trans_time", 			trans_time);
			
			//创建签约记录
			Map<String, Object> signlog_entity = ItsManage.createSignLog(signlogMap);
			String trace_num	= (String)signlog_entity.get("trace_num");
					
			Map<String, String> extParams = new HashMap<String, String>();
			extParams.put("transDate", trans_date);
			extParams.put("transTime", trans_time);
			String strResult = Extension.itsService.signMessageSend(trace_num, ORI_TRACE_NUM, ORI_TRANS_DATE, PHONE_NO, extParams);
			
			try { 
				Map<String, String> resultMap = new HashMap<String, String>();
				Document document = DocumentHelper.parseText(strResult);
				Element rootEl = document.getRootElement();
				
				//修改的签约记录信息
				Map<String, String> modifySignlogMap = new HashMap<String, String>();
				modifySignlogMap.put("id", 		signlog_entity.get("id").toString());
				//头响应吗
				Element e_HEAD_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_CODE");
				if(e_HEAD_RET_CODE != null){
					resultMap.put("RET_CODE", 		e_HEAD_RET_CODE.getText());
					modifySignlogMap.put("ret_code", 		e_HEAD_RET_CODE.getText());
				}
				//头响应信息
				Element e_HEAD_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_MSG");
				if(e_HEAD_RET_MSG != null){
					resultMap.put("RET_MSG", 		e_HEAD_RET_MSG.getText());
					modifySignlogMap.put("ret_msg", 		e_HEAD_RET_MSG.getText());
				}
				//交易流水号
				Element e_ITS_TRACE_NUM = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_NUM");
				if(e_ITS_TRACE_NUM != null){
					resultMap.put("TRACE_NUM", 			e_ITS_TRACE_NUM.getText());
					modifySignlogMap.put("its_trace_num", 	e_ITS_TRACE_NUM.getText());
				}
				//响应码
				Element e_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_CODE");
				if(e_RET_CODE != null){
					resultMap.put("RET_CODE", 		e_RET_CODE.getText());
					its_logger.info("e_RET_CODE.getText()");
					if("359037".equals(e_RET_CODE.getText())) {
						Map<String, Object> temp = new HashMap<String, Object>();
						temp.put("phone", PHONE_NO);
						temp.put("bank_name", (String)ori_entity.get("bank_name"));
						temp.put("accountNo", (String)ori_entity.get("accountNo"));
						temp.put("account_name", (String)ori_entity.get("account_name"));
						temp.put("verification_code_type", Constant.VERIFICATION_CODE_AUTH_SIGN);
						temp.put("member_id", Long.valueOf(params.get("member_id")));
						
						String content = codeServiceImpl.generatePhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN,PHONE_NO, Constant.VERIFICATION_CODE_AUTH_SIGN, temp);
						Extension.otherService.sendSM(PHONE_NO, content);
					}
					modifySignlogMap.put("ret_code", 	e_RET_CODE.getText());
				}
				//响应信息
				Element e_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_MSG");
				if(e_RET_MSG != null){
					resultMap.put("RET_MSG", 		e_RET_MSG.getText());
					modifySignlogMap.put("ret_msg", 		e_RET_MSG.getText());
				}
				//its交易日期
				Element e_ITS_TRANS_DATE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_DATE");
				if(e_ITS_TRANS_DATE != null){
					resultMap.put("TRANS_DATE", 		e_ITS_TRANS_DATE.getText());
					modifySignlogMap.put("its_trans_date", 		e_ITS_TRANS_DATE.getText());
				}
				//its交易时间
				Element e_ITS_TRANS_TIME = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_TIME");
				if(e_ITS_TRANS_TIME != null){
					resultMap.put("TRANS_TIME", 			e_ITS_TRANS_TIME.getText());
					modifySignlogMap.put("its_trans_time", 		e_ITS_TRANS_TIME.getText());
				}
				
				//错误信息
				Element e_ERROR_CODE = (Element)rootEl.selectSingleNode("/ERROR/ERROR_CODE");
				if(e_ERROR_CODE != null){
					resultMap.put("ERROR_CODE", 		e_ERROR_CODE.getText());
					modifySignlogMap.put("error_code", 	e_ERROR_CODE.getText());
				}
				Element e_ERROR_MSG = (Element)rootEl.selectSingleNode("/ERROR/ERROR_MSG");
				if(e_ERROR_CODE != null){
					resultMap.put("ERROR_MSG", 			e_ERROR_MSG.getText());
					modifySignlogMap.put("error_msg", 	e_ERROR_MSG.getText());
				}
				//修改签约记录
				ItsManage.modifySignLog(modifySignlogMap);
				its_logger.info("返回信息:" + resultMap);
				return resultMap;
			}catch (Exception e){ 
				its_logger.info(e.getMessage(), e);
				throw e;
			}
		}
	}
	
	/**
	 * 签约确认
	 * @param params{ori_trace_num:交易（签约申请）的交易流水号,ori_trans_date:原交易（签约申请）的交易日期}
	 * @return 签约结果
	 * @throws Exception
	 */
	public static Map<String, String> signACK(Map<String, String> params) throws Exception{
		its_logger.info("ItsManage.signACK start");
		CodeServiceImpl codeServiceImpl = new CodeServiceImpl();
		its_logger.info("params:" + params);
		String ORI_TRACE_NUM	= params.get("ORI_TRACE_NUM");
		if(ORI_TRACE_NUM == null)
			throw new Exception("请传入参数 ORI_TRACE_NUM");
		String ORI_TRANS_DATE	= params.get("ORI_TRANS_DATE");
		if(ORI_TRANS_DATE == null)
			throw new Exception("请传入参数 ORI_TRANS_DATE");
		String VERIFY_CODE		= params.get("VERIFY_CODE");
		if("2".equals(params.get("payType")) || "3".equals(params.get("payType"))){
			VERIFY_CODE = "";
		}
		if(VERIFY_CODE == null)
			throw new Exception("请传入参数 VERIFY_CODE");
		
		Map<String, String> signlogMap = new HashMap<>();
		String userId	= params.get("userId");
		if(userId == null)
			throw new Exception("请传入参数 userId");
		
		//查询签约申请记录
		Map<String, Object>ori_entity = ItsManage.getAuthPayLog(ORI_TRACE_NUM);//签约的
		Map<String, Object>ori_entity2 = ItsManage.getAuthPayLog2(ORI_TRACE_NUM);//its发送短信
		its_logger.info("ori_entity:" + ori_entity);
		
		if(ori_entity == null)
			throw new Exception("请传入有效的 ori_entity");
		String send_sms = (String)ori_entity.get("send_sms");
		String phone	= (String)ori_entity.get("phone");
		Long code_id = null;
		if((send_sms == null || send_sms.equals("3")) && ("1".equals(params.get("payType")) || "4".equals(params.get("payType")))) {
			//验证短信验证码
			code_id = codeServiceImpl.checkPhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN,phone, Constant.VERIFICATION_CODE_AUTH_SIGN, VERIFY_CODE);
		}
		try{
			String ret_code = "", CONTRACT_NO = "", cnl_id	= "";
			String sub_ret_code = "";
			Map<String, String> resultMap = new HashMap<>();
			if((ori_entity2 != null && !"359037".equals(ori_entity2.get("ret_code"))) || (ori_entity2 == null && !"359037".equals(ori_entity.get("ret_code")))) {
				Date now  = new Date();
				String trans_date	= format.format(now);
				String trans_time	= format0.format(now);
				
				signlogMap.put("userId", 				userId);
				signlogMap.put("ori_trace_num", 		ORI_TRACE_NUM);
				signlogMap.put("ori_trans_date", 		ORI_TRANS_DATE);
				signlogMap.put("verification_code", 	VERIFY_CODE);
				signlogMap.put("sign_type", 			"3");
				signlogMap.put("trans_date", 			trans_date);
				signlogMap.put("trans_time", 			trans_time);
				
				//创建签约记录
				Map<String, Object> signlog_entity = ItsManage.createSignLog(signlogMap);
				String trace_num	= (String)signlog_entity.get("trace_num");
				
				Map<String, String> extParams = new HashMap<>();
				extParams.put("transDate", trans_date);
				extParams.put("transTime", trans_time);
				extParams.put("verifyCode", VERIFY_CODE);
				String strResult = Extension.itsService.signACK(trace_num, ORI_TRACE_NUM, ORI_TRANS_DATE, phone, extParams);
				
				Document document = DocumentHelper.parseText(strResult);
				Element rootEl = document.getRootElement();
				
				//修改的签约记录信息
				Map<String, String> modifySignlogMap = new HashMap<>();
				modifySignlogMap.put("id", 		signlog_entity.get("id").toString());
				
				//头响应吗
				Element e_HEAD_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_CODE");
				if(e_HEAD_RET_CODE != null){
					resultMap.put("RET_CODE", 		e_HEAD_RET_CODE.getText());
					modifySignlogMap.put("ret_code", 		e_HEAD_RET_CODE.getText());
				}
				//头响应信息
				Element e_HEAD_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_MSG");
				if(e_HEAD_RET_MSG != null){
					resultMap.put("RET_MSG", 		e_HEAD_RET_MSG.getText());
					modifySignlogMap.put("ret_msg", 		e_HEAD_RET_MSG.getText());
				}
				//交易流水号
				Element e_ITS_TRACE_NUM = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_NUM");
				if(e_ITS_TRACE_NUM != null){
					resultMap.put("ITS_TRACE_NUM", 			e_ITS_TRACE_NUM.getText());
					modifySignlogMap.put("its_trace_num", 	e_ITS_TRACE_NUM.getText());
				}
				//签约协议号
				Element e_CONTRACT_NO = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CONTRACT_NO");
				if(e_CONTRACT_NO != null){
					resultMap.put("CONTRACT_NO", 	e_CONTRACT_NO.getText());
					modifySignlogMap.put("bank_contract_no", 	e_CONTRACT_NO.getText());
					CONTRACT_NO = e_CONTRACT_NO.getText();
				}
				//响应码
				Element e_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_CODE");
				if(e_RET_CODE != null){
					resultMap.put("RET_CODE", 		e_RET_CODE.getText());
					modifySignlogMap.put("ret_code", 	e_RET_CODE.getText());
					ret_code	= e_RET_CODE.getText();
				}
				//响应信息
				Element e_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_MSG");
				if(e_RET_MSG != null){
					resultMap.put("RET_MSG", 		e_RET_MSG.getText());
					modifySignlogMap.put("ret_msg", 		e_RET_MSG.getText());
				}
				//its交易日期
				Element e_ITS_TRANS_DATE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_DATE");
				if(e_ITS_TRANS_DATE != null){
					resultMap.put("ITS_TRANS_DATE", 		e_ITS_TRANS_DATE.getText());
					modifySignlogMap.put("its_trans_date", 		e_ITS_TRANS_DATE.getText());
				}
				//its交易时间
				Element e_ITS_TRANS_TIME = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_TIME");
				if(e_ITS_TRANS_TIME != null){
					resultMap.put("ITS_TRANS_TIME", 			e_ITS_TRANS_TIME.getText());
					modifySignlogMap.put("its_trans_time", 		e_ITS_TRANS_TIME.getText());
				}
				//签约渠道ID
				Element e_CNL_ID	= (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CNL_ID");
				if(e_CNL_ID != null){
					resultMap.put("CNL_ID", e_CNL_ID.getText());
					modifySignlogMap.put("cnl_id", 		e_CNL_ID.getText());
					
					cnl_id = e_CNL_ID.getText();
				}
				//发送短信状态
				Element e_SEND_SMS	= (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/SEND_SMS");
				if(e_SEND_SMS != null){
					resultMap.put("SEND_SMS", 				e_SEND_SMS.getText());
					modifySignlogMap.put("send_sms", 		e_SEND_SMS.getText());
				}
				
				//错误信息
				Element e_ERROR_CODE = (Element)rootEl.selectSingleNode("/ERROR/ERROR_CODE");
				if(e_ERROR_CODE != null){
					resultMap.put("ERROR_CODE", 		e_ERROR_CODE.getText());
					modifySignlogMap.put("error_code", 	e_ERROR_CODE.getText());
				}
				Element e_ERROR_MSG = (Element)rootEl.selectSingleNode("/ERROR/ERROR_MSG");
				if(e_ERROR_CODE != null){
					resultMap.put("ERROR_MSG", 			e_ERROR_MSG.getText());
					modifySignlogMap.put("error_msg", 	e_ERROR_MSG.getText());
				}
				//修改签约记录
				ItsManage.modifySignLog(modifySignlogMap);
			} else {
				resultMap.put("RET_CODE", 		"359037");
				ret_code = "359037";
			}
			if(ret_code != null && ret_code.length() > 1) {
			    sub_ret_code = ret_code.substring(1);
			}
			
			if(sub_ret_code.equals("00000") || sub_ret_code.equals("59037")){	//交易成功活已签约
				if((send_sms == null || send_sms.equals("3")) && ("1".equals(params.get("payType")) || "4".equals(params.get("payType")))) {
					//设置短信验证码已验证
					codeServiceImpl.setPhoneVerificationCode(code_id);
				}
				//绑定银行卡
				Map<String, Object> bankCardMap = new HashMap<>();
				String accountNo_encrypt	= (String)ori_entity.get("accountNo_encrypt");
				//卡号解密
				String accountNo = TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, accountNo_encrypt);
				if(accountNo ==null ||"".equals(accountNo)){
					throw new Exception("银行卡操作失败");
				}
				bankCardMap.put("accountNo", 		accountNo);
				bankCardMap.put("bank_code", 		ori_entity.get("bank_code"));
				bankCardMap.put("bank_name", 		ori_entity.get("bank_name"));
				bankCardMap.put("card_type", 		ori_entity.get("card_type"));
				
				//判断是否实名
				Member member = new Member();
				member.setUserId(userId);
				Map<String, Object> member_entity = member.getUserInfo();
				Boolean isIdentity_checked = (Boolean)member_entity.get("isIdentity_checked");
                String member_bank_code = (String)member_entity.get("bank_code");
				
					String identity_cardNo_encrypt	= (String)ori_entity.get("identity_cardNo_encrypt");
					String identity_cardNo = TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, identity_cardNo_encrypt);
				if(isIdentity_checked==null || !isIdentity_checked  || member_bank_code == null || member_bank_code.equals("03080000")){	//非实名
					bankCardMap.put("identity_cardNo", 	identity_cardNo);
				}
				bankCardMap.put("account_name", 		ori_entity.get("account_name"));
				bankCardMap.put("account_prop", 		0L);			
				bankCardMap.put("acct_validdate", 		ori_entity.get("acct_validdate"));
				bankCardMap.put("cvv2", 				ori_entity.get("cvv2"));
				bankCardMap.put("account_type_id", 		ori_entity.get("account_type_id"));
				bankCardMap.put("account_type_label", 	ori_entity.get("account_type_label"));
				bankCardMap.put("member_id", 			(Long)member_entity.get("id"));
				bankCardMap.put("phone", 				ori_entity.get("phone"));
				bankCardMap.put("interfaceNo", 			Constant.PAY_INTERFACE_QUICK_CARD);
				if( ori_entity.get("is_safe_cards") != null ){
					bankCardMap.put("is_safe_card", 				ori_entity.get("is_safe_cards"));
				}
                bankCardMap.put("contract_no",          CONTRACT_NO);
				
                MemberServiceImpl memberServiceImpl = new MemberServiceImpl();
				Map<String, Object> bindcard_entity = memberServiceImpl.bindBankCard((Long)member_entity.get("id"), (Long)ori_entity.get("card_type"), accountNo, bankCardMap);
				if(!((Long)member_entity.get("id")).equals((Long)bindcard_entity.get("member_id"))) {
				    resultMap.put("AMS_STATUS",    "login");
				}
				resultMap.put("BANKCARD_ID", 	bindcard_entity.get("id").toString());
				
				Map<String, Object> bank_entity = memberServiceImpl.getBankInfo((String)ori_entity.get("bank_code"));
				String short_name = (String)bank_entity.get("short_name");
				String its_bank_code	= (String)bank_entity.get("its_bank_code");
			
				if(sub_ret_code.equals("00000")){
					//签约成功 调用协议保存接口		
					Map<String, String> param = new HashMap<>();
					param.put("ORG_ID", 		its_bank_code);					
					//param.put("ORG_ID", 		cnl_id);
					param.put("ORG_NAME", 		(String)ori_entity.get("bank_name"));
					param.put("CARD_NO", 		accountNo);
					param.put("ORG_CODE", 		(String)ori_entity.get("bank_code"));
					if(Constant.BANK_CARD_XY.equals(ori_entity.get("card_type"))) {
						param.put("ACCOUNT_TYPE", 	"00");
					} else if(Constant.BANK_CARD_CX.equals(ori_entity.get("card_type"))) {
						param.put("ACCOUNT_TYPE", 	"01");
					}
					
					param.put("ORG_DESC", 		short_name);
					
					param.put("USER_NAME", 		(String)ori_entity.get("account_name"));
					param.put("MOBILE_NO", 		(String)ori_entity.get("phone"));
					param.put("ID_NO", 			identity_cardNo);
					param.put("ITS_CONN_ID", 	cnl_id);
					param.put("CONTRACT_NO", 	CONTRACT_NO);
					
					its_logger.info("调用协议保存接口");
					Map result = Extension.manageService.saveContract(param);
					/*
					its_logger.info("result="+result);
					List list = (List) result.get("CXJG");
					Map map = (Map) list.get(0);
					CONTRACT_NO = (String) map.get("CONTRACT_NO");
					its_logger.info("CONTRACT_NO="+CONTRACT_NO);
					*/
				}else{
					//已签约调用重复签约接口
					Map<String, String> param = new HashMap<>();
					param.put("ORG_ID", 		its_bank_code);		
//					param.put("ORG_ID", 		cnl_id);
					param.put("ORG_NAME", 		(String)ori_entity.get("account_name"));
					param.put("CARD_NO", 		accountNo);
					param.put("ORG_CODE", 		(String)ori_entity.get("bank_code"));
					if(Constant.BANK_CARD_XY.equals(ori_entity.get("card_type"))) {
						param.put("ACCOUNT_TYPE", 	"00");
					} else if(Constant.BANK_CARD_CX.equals(ori_entity.get("card_type"))) {
						param.put("ACCOUNT_TYPE", 	"01");
					}
					param.put("USER_NAME", 		(String)ori_entity.get("account_name"));
					param.put("MOBILE_NO", 		(String)ori_entity.get("phone"));
					param.put("ID_NO", 			identity_cardNo);
					param.put("ORG_DESC", 		short_name);
					param.put("ITS_CONN_ID", 	cnl_id);
					param.put("CONTRACT_NO", 	CONTRACT_NO);
					
					its_logger.info("调用重复签约接口");
					Map result = Extension.manageService.repeatContract(param);
					its_logger.info("result="+result);
					List list = (List) result.get("CXJG");
					if(list == null) {
						throw new Exception(result.get("JYMS").toString());
					}
					Map map = (Map) list.get(0);
					CONTRACT_NO = (String) map.get("CONTRACT_NO");
					its_logger.info("CONTRACT_NO="+CONTRACT_NO);
					OrderServiceImpl.updateBankCardContractNo((Long)bindcard_entity.get("id"), CONTRACT_NO);
				}
			}
			its_logger.info("返回信息:" + resultMap);
			return resultMap;
		} catch (Exception e){
			its_logger.info(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 签约关系查询
	 * @param params{acct_name:用户名称,acct_cat:账户类型,acct_no:银行卡号,id_no:身份证,phone_no:手机号码}
	 * @return
	 * @throws Exception
	 */
	public static String querySign(Map<String, String> params) throws Exception{
		SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmmss");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat format0 = new SimpleDateFormat("HHmmss");
		Date now  = new Date();
		StringBuilder envelope = new StringBuilder();
		envelope.append("<ENVELOPE>")
			.append("<HEAD>")
				.append("<VERSION>v1.0</VERSION>")
				.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
				.append("<PAY_TYPE>05</PAY_TYPE>")
				.append("<TRANS_CODE>1006</TRANS_CODE>")
				.append("<ACCESS_ID>").append(access_id).append("</ACCESS_ID>")
                .append("<TRACE_NUM>").append(format1.format(now)).append("</TRACE_NUM>")
                .append("<TRANS_DATE>").append(format.format(now)).append("</TRANS_DATE>")
                .append("<TRANS_TIME>").append(format0.format(now)).append("</TRANS_TIME>")
			.append("</HEAD>")
			.append("<TX_INFO>")
				.append("<BANK_CODE>").append(params.get("back_code")).append("</BANK_CODE>")
				.append("<ACCT_NAME>").append(params.get("acct_name")).append("</ACCT_NAME>")
				.append("<ACCT_CAT>").append(params.get("acct_cat")).append("</ACCT_CAT>")
				.append("<ACCT_NO>").append(params.get("acct_no")).append("</ACCT_NO>")
				.append("<ID_TYPE>01</ID_TYPE>")
				.append("<ID_NO>").append(params.get("id_no")).append("</ID_NO>")
				.append("<PHONE_NO>").append(params.get("phone_no")).append("</PHONE_NO>")
			.append("</TX_INFO>")
		.append("</ENVELOPE>");
		
		StringBuilder sb = new StringBuilder();
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
		BASE64Encoder encoder = new BASE64Encoder();
		String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
		NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1") };
		String str = new HttpUtil().post(memberUrl, datas);
		BASE64Decoder decoder = new BASE64Decoder();
		try { 
			byte[] b = decoder.decodeBuffer(str);
			return new String(b, "UTF-8");
		} catch (Exception e){ 
			throw e;
		}
	}
	
	/**
	 * 支付申请
	 * @param params{trans_amount:交易金额,acct_name:用户名称,acct_cat:账户类型,acct_no:银行卡号,id_no:身份证,phone_no:手机号码}
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> payApply(Map<String, String> params) throws Exception{
		its_logger.info("ItsManage.payApply start");
		OrderServiceImpl impl = new OrderServiceImpl();
		its_logger.info("params:" + params);
		
		String TRANS_AMOUNT	= params.get("TRANS_AMOUNT");
		if(TRANS_AMOUNT == null)
			throw new Exception("请传入参数 TRANS_AMOUNT");
		String BANK_CODE	= params.get("BANK_CODE");
		if(BANK_CODE == null)
			throw new Exception("请传入参数 BANK_CODE");
		
		BANK_CODE = impl.getITSBankCode(BANK_CODE);
		
		String ACCT_NAME	= params.get("ACCT_NAME");
		if(ACCT_NAME == null)
			throw new Exception("请传入参数 ACCT_NAME");
		String ACCT_CAT	= params.get("ACCT_CAT");
		if(ACCT_CAT == null)
			throw new Exception("请传入参数 ACCT_CAT");
		String ACCT_VALIDDATE = null;
		String CVV2	 = null;
		if(ACCT_CAT.equals(Constant.BANK_CARD_CX.toString())){
			ACCT_CAT = "0" + ACCT_CAT;
			ACCT_VALIDDATE = "";
			CVV2 = "";
		}else if(ACCT_CAT.equals(Constant.BANK_CARD_XY.toString())){
			ACCT_CAT = "0" + ACCT_CAT;
			ACCT_VALIDDATE	= params.get("ACCT_VALIDDATE");
			if(ACCT_VALIDDATE == null)
				throw new Exception("请传入参数：" + ACCT_VALIDDATE);
			CVV2			= params.get("CVV2");
			if(CVV2 == null)
				throw new Exception("请传入参数：" + CVV2);
		}
		
		String PHONE_NO	= params.get("PHONE_NO");
		if(PHONE_NO == null)
			throw new Exception("请传入参数 PHONE_NO");
		String CONTRACT_NO	= params.get("CONTRACT_NO");
		if(CONTRACT_NO == null)
		    CONTRACT_NO = "";
		String ACCT_NO_ENCRYPT	= params.get("ACCT_NO_ENCRYPT");
		if(ACCT_NO_ENCRYPT == null)
			throw new Exception("请传入参数 ACCT_NO_ENCRYPT");
		
		String ID_NO_ENCRYPT		= params.get("ID_NO_ENCRYPT");
		if(ID_NO_ENCRYPT == null)
			throw new Exception("请传入参数 ID_NO_ENCRYPT");
		String trace_num	= params.get("TRACE_NUM");
		if(trace_num == null)
			throw new Exception("请传入参数 TRACE_NUM");
		String bank_name	= params.get("BANK_NAME");
		if(bank_name == null)
			throw new Exception("请传入参数 BANK_NAME");
		
		String userId	= params.get("userId");
		if(userId == null)
			throw new Exception("请传入参数 userId");

		String ACCT_NO	= TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, ACCT_NO_ENCRYPT);
		String ID_NO	= TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, ID_NO_ENCRYPT);
		if(ACCT_NO == null){
			 throw new Exception("银行卡解析失败");
		}
		
		
//		if(!RiskUser.checkRiskUserInfo("riskBankCardNo", ACCT_NO)) {
//            throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
//        }
//		if(!RiskUser.checkRiskUserInfo("riskCertificateID", ID_NO)) {
//            throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
//        }

		String CNL_MCHT_NAME	= params.get("CNL_MCHT_NAME");
		if(CNL_MCHT_NAME == null)
			CNL_MCHT_NAME = "";

		String CNL_MCHT_TYPE	= params.get("CNL_MCHT_TYPE");
		if(CNL_MCHT_TYPE == null)
			CNL_MCHT_TYPE = "";

		String CNL_MCHT_ID	= params.get("CNL_MCHT_ID");
		if(CNL_MCHT_ID == null)
			CNL_MCHT_ID = "";
		
		Date now  = new Date();
		String trans_date	= format.format(now);
		String trans_time	= format0.format(now);
		StringBuffer sb = new StringBuffer();
		
		//证件号码解密
		String ID_NO_M	= TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, ID_NO_ENCRYPT);
        //身份证号码掩码
        sb.setLength(0);
        sb.append(ID_NO.substring(0, 1));
        for(int i = 1, j = ID_NO.length() - 1; i < j; i ++)
            sb.append("*");
        sb.append(ID_NO.substring(ID_NO.length() - 1));
        String ID_NO_MASK  = sb.toString();
        
		//卡号解密
		String ACCT_NO_M	= TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, ACCT_NO_ENCRYPT);
        //卡号掩码
        sb.setLength(0);
        for(int i = 0, j = ACCT_NO.length() - 4; i < j; i ++) {
            sb.append("*");
        }
        sb.append(ACCT_NO.substring(ACCT_NO.length() - 4));
        String ACCT_NO_MASK = sb.toString();    
		
		Map<String, String> signlogMap = new HashMap<String, String>();
		signlogMap.put("userId", 				userId);
		signlogMap.put("bank_code", 			BANK_CODE);
		signlogMap.put("accountNo", 			ACCT_NO_MASK);
		signlogMap.put("account_name", 			ACCT_NAME);
		signlogMap.put("identity_cardNo", 		ID_NO_MASK);
		signlogMap.put("card_type", 			ACCT_CAT);
		signlogMap.put("accountNo_encrypt", 	ACCT_NO_ENCRYPT);
		signlogMap.put("identity_cardNo_encrypt", ID_NO_ENCRYPT);
		signlogMap.put("phone", 				PHONE_NO);
		signlogMap.put("acct_validdate", 		ACCT_VALIDDATE);
		signlogMap.put("cvv2", 					CVV2);
		signlogMap.put("sign_type", 			"4");
		signlogMap.put("trace_num", 			trace_num);
		signlogMap.put("trans_date", 			trans_date);
		signlogMap.put("trans_time", 			trans_time);
		signlogMap.put("bank_name", 			bank_name);
		signlogMap.put("trans_amount", 			TRANS_AMOUNT);
		
		//创建签约记录
		Map<String, Object> signlog_entity = ItsManage.createSignLog(signlogMap);
		
		Map<String, String> extParams = new HashMap<String, String>();
		extParams.put("transDate", trans_date);
		extParams.put("transTime", trans_time);
		extParams.put("acctValidDate", ACCT_VALIDDATE);
		extParams.put("cvv2", CVV2);
		extParams.put("cnlMchtId", CNL_MCHT_ID);
		extParams.put("cnlMchtName", CNL_MCHT_NAME);
		extParams.put("cnlMchtType", CNL_MCHT_TYPE);
		String strResult = Extension.itsService.payApply(trace_num, Long.valueOf(TRANS_AMOUNT), CONTRACT_NO, BANK_CODE, ACCT_NAME, 
				ACCT_CAT, ACCT_NO_M, ID_NO_M, PHONE_NO, extParams);
		its_logger.info("返回信息strResult:" + strResult);
		try { 
			Map<String, String> resultMap = new HashMap<String, String>();
			Document document = DocumentHelper.parseText(strResult);
			Element rootEl = document.getRootElement();
			
			//修改的签约记录信息
			Map<String, String> modifySignlogMap = new HashMap<String, String>();
			modifySignlogMap.put("id", 		signlog_entity.get("id").toString());
			
			resultMap.put("TRACE_NUM", trace_num);
			resultMap.put("TRANS_DATE", trans_date);
			
			//头响应吗
			Element e_HEAD_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_CODE");
			if(e_HEAD_RET_CODE != null){
				resultMap.put("RET_CODE", 		e_HEAD_RET_CODE.getText());
				modifySignlogMap.put("ret_code", 		e_HEAD_RET_CODE.getText());
			}
			//头响应信息
			Element e_HEAD_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_MSG");
			if(e_HEAD_RET_MSG != null){
				resultMap.put("RET_MSG", 		e_HEAD_RET_MSG.getText());
				modifySignlogMap.put("ret_msg", 		e_HEAD_RET_MSG.getText());
			}
			//交易流水号
			Element e_ITS_TRACE_NUM = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_NUM");
			if(e_ITS_TRACE_NUM != null){
				resultMap.put("ITS_TRACE_NUM", 			e_ITS_TRACE_NUM.getText());
				modifySignlogMap.put("its_trace_num", 	e_ITS_TRACE_NUM.getText());
			}
			//签约协议号
			Element e_CONTRACT_NO = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CONTRACT_NO");
			if(e_CONTRACT_NO != null){
				CONTRACT_NO = e_CONTRACT_NO.getText();
				resultMap.put("CONTRACT_NO", 	CONTRACT_NO);
				modifySignlogMap.put("bank_contract_no", 	e_CONTRACT_NO.getText());
			}
			//响应码
			Element e_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_CODE");
			if(e_RET_CODE != null){
				resultMap.put("RET_CODE", 		e_RET_CODE.getText());
				modifySignlogMap.put("ret_code", 	e_RET_CODE.getText());
			}
			//响应信息
			Element e_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_MSG");
			if(e_RET_MSG != null){
				resultMap.put("RET_MSG", 		e_RET_MSG.getText());
				modifySignlogMap.put("ret_msg", 		e_RET_MSG.getText());
			}
			//its交易日期
			Element e_ITS_TRANS_DATE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_DATE");
			if(e_ITS_TRANS_DATE != null){
				resultMap.put("ITS_TRANS_DATE", 		e_ITS_TRANS_DATE.getText());
				modifySignlogMap.put("its_trans_date", 		e_ITS_TRANS_DATE.getText());
			}
			//its交易时间
			Element e_ITS_TRANS_TIME = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_TIME");
			if(e_ITS_TRANS_TIME != null){
				resultMap.put("ITS_TRANS_TIME", 			e_ITS_TRANS_TIME.getText());
				modifySignlogMap.put("its_trans_time", 		e_ITS_TRANS_TIME.getText());
			}
			//签约渠道ID
			Element e_CNL_ID	= (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CNL_ID");
			if(e_CNL_ID != null){
				resultMap.put("CNL_ID", e_CNL_ID.getText());
				modifySignlogMap.put("cnl_id", 		e_CNL_ID.getText());
			}
	        sb.setLength(0);
	        for(int i = 0, j = ACCT_NO_M.length() - 4; i < j; i ++) {
	            sb.append("*");
	        }
	        sb.append(ACCT_NO_M.substring(ACCT_NO_M.length() - 4));
	        ACCT_NO_M = sb.toString();  
	        
			//发送短信状态
			Element e_SEND_SMS	= (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/SEND_SMS");
			if(e_SEND_SMS != null){
				resultMap.put("SEND_SMS", 				e_SEND_SMS.getText());
				modifySignlogMap.put("send_sms", 		e_SEND_SMS.getText());
				if(e_SEND_SMS.getText().equals("3")){	//发送短信
					String isSendSM = params.get("isSendSM");
					if(isSendSM.equals("1")) {
						Map<String, Object> temp = new HashMap<String, Object>();
						temp.put("phone", PHONE_NO);
						temp.put("bank_name", bank_name);
						temp.put("accountNo", ACCT_NO_M);
	                    temp.put("account_name", ACCT_NAME);
						temp.put("orderNo", trace_num);
						temp.put("verification_code_type", Constant.VERIFICATION_CODE_AUTH_PAY);
						temp.put("member_id", Long.valueOf(params.get("member_id")));
						its_logger.info("+++++temp="+temp);
						CodeServiceImpl codeServiceImpl = new CodeServiceImpl();
						String content = codeServiceImpl.generatePhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN,PHONE_NO, Constant.VERIFICATION_CODE_AUTH_PAY, temp);
						Extension.otherService.sendSM(PHONE_NO, content);
					}
				}
			}
			
			//错误信息
			Element e_ERROR_CODE = (Element)rootEl.selectSingleNode("/ERROR/ERROR_CODE");
			if(e_ERROR_CODE != null){
				resultMap.put("ERROR_CODE", 		e_ERROR_CODE.getText());
				modifySignlogMap.put("error_code", 	e_ERROR_CODE.getText());
			}
			Element e_ERROR_MSG = (Element)rootEl.selectSingleNode("/ERROR/ERROR_MSG");
			if(e_ERROR_CODE != null){
				resultMap.put("ERROR_MSG", 			e_ERROR_MSG.getText());
				modifySignlogMap.put("error_msg", 	e_ERROR_MSG.getText());
			}
			//修改签约记录
			ItsManage.modifySignLog(modifySignlogMap);
			
			its_logger.info("返回信息:" + resultMap);
			return resultMap;
		} catch (Exception e){
			its_logger.info(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * 支付短信验证码发送
	 * @param params{ori_trace_num:原交易（支付申请）的交易流水号,ori_trans_date:原交易（支付申请）的交易日期,phone_no:电话号码}
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> payMessageSend(Map<String, String> params) throws Exception{
		its_logger.info("ItsManage.payMessageSend start");
		its_logger.info("params:" + params);
		
		String ORI_TRACE_NUM	= params.get("ORI_TRACE_NUM");
		if(ORI_TRACE_NUM == null)
			throw new Exception("请传入参数 ORI_TRACE_NUM");
		String ORI_TRANS_DATE	= params.get("ORI_TRANS_DATE");
		if(ORI_TRANS_DATE == null)
			throw new Exception("请传入参数 ORI_TRANS_DATE");
		String PHONE_NO	= params.get("PHONE_NO");
		if(PHONE_NO == null)
			throw new Exception("请传入参数 PHONE_NO");
		
		Date now  = new Date();
		String trans_date	= format.format(now);
		String trans_time	= format0.format(now);
		
		Map<String, String> signlogMap = new HashMap<String, String>();
		String userId	= params.get("userId");
		if(userId == null)
			throw new Exception("请传入参数 userId");

		signlogMap.put("userId", 				userId);
		signlogMap.put("ori_trace_num", 		ORI_TRACE_NUM);
		signlogMap.put("ori_trans_date", 		ORI_TRANS_DATE);
		signlogMap.put("phone", 				PHONE_NO);
		signlogMap.put("sign_type", 			"5");
		signlogMap.put("trans_date", 			trans_date);
		signlogMap.put("trans_time", 			trans_time);
		
		//创建签约记录
		Map<String, Object> signlog_entity = ItsManage.createSignLog(signlogMap);
		String trace_num	= (String)signlog_entity.get("trace_num");
		
		Map<String, String> extParams = new HashMap<String, String>();
		extParams.put("transDate", trans_date);
		extParams.put("transTime", trans_time);
		String strResult = Extension.itsService.payMessageSend(trace_num, ORI_TRACE_NUM, ORI_TRANS_DATE, PHONE_NO, extParams);
		
		try { 
			Map<String, String> resultMap = new HashMap<String, String>();
			Document document = DocumentHelper.parseText(strResult);
			Element rootEl = document.getRootElement();
			
			//修改的签约记录信息
			Map<String, String> modifySignlogMap = new HashMap<String, String>();
			modifySignlogMap.put("id", 		signlog_entity.get("id").toString());
			//头响应吗
			Element e_HEAD_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_CODE");
			if(e_HEAD_RET_CODE != null){
				resultMap.put("RET_CODE", 		e_HEAD_RET_CODE.getText());
				modifySignlogMap.put("ret_code", 		e_HEAD_RET_CODE.getText());
			}
			//头响应信息
			Element e_HEAD_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_MSG");
			if(e_HEAD_RET_MSG != null){
				resultMap.put("RET_MSG", 		e_HEAD_RET_MSG.getText());
				modifySignlogMap.put("ret_msg", 		e_HEAD_RET_MSG.getText());
			}
			//交易流水号
			Element e_ITS_TRACE_NUM = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_NUM");
			if(e_ITS_TRACE_NUM != null){
				resultMap.put("ITS_TRACE_NUM", 			e_ITS_TRACE_NUM.getText());
				modifySignlogMap.put("its_trace_num", 	e_ITS_TRACE_NUM.getText());
			}
			//响应码
			Element e_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_CODE");
			if(e_RET_CODE != null){
				resultMap.put("RET_CODE", 		e_RET_CODE.getText());
				modifySignlogMap.put("ret_code", 	e_RET_CODE.getText());
			}
			//响应信息
			Element e_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_MSG");
			if(e_RET_MSG != null){
				resultMap.put("RET_MSG", 		e_RET_MSG.getText());
				modifySignlogMap.put("ret_msg", 		e_RET_MSG.getText());
			}
			//its交易日期
			Element e_ITS_TRANS_DATE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_DATE");
			if(e_ITS_TRANS_DATE != null){
				resultMap.put("ITS_TRANS_DATE", 		e_ITS_TRANS_DATE.getText());
				modifySignlogMap.put("its_trans_date", 		e_ITS_TRANS_DATE.getText());
			}
			//its交易时间
			Element e_ITS_TRANS_TIME = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_TIME");
			if(e_ITS_TRANS_TIME != null){
				resultMap.put("ITS_TRANS_TIME", 			e_ITS_TRANS_TIME.getText());
				modifySignlogMap.put("its_trans_time", 		e_ITS_TRANS_TIME.getText());
			}
			
			//错误信息
			Element e_ERROR_CODE = (Element)rootEl.selectSingleNode("/ERROR/ERROR_CODE");
			if(e_ERROR_CODE != null){
				resultMap.put("ERROR_CODE", 		e_ERROR_CODE.getText());
				modifySignlogMap.put("error_code", 	e_ERROR_CODE.getText());
			}
			Element e_ERROR_MSG = (Element)rootEl.selectSingleNode("/ERROR/ERROR_MSG");
			if(e_ERROR_CODE != null){
				resultMap.put("ERROR_MSG", 			e_ERROR_MSG.getText());
				modifySignlogMap.put("error_msg", 	e_ERROR_MSG.getText());
			}
			//修改签约记录
			ItsManage.modifySignLog(modifySignlogMap);
			
			its_logger.info("返回信息:" + resultMap);
			return resultMap;
		}catch (Exception e){ 
			its_logger.info(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * 支付确认
	 * @param params{ori_trace_num:原交易（支付申请）的交易流水号,ori_trans_date:原交易（支付申请）的交易日期}
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> payACK(Map<String, String> params) throws Exception{
		its_logger.info("ItsManage.payACK start");
		its_logger.info("params:" + params);
		String ORI_TRACE_NUM	= params.get("ORI_TRACE_NUM");
		if(ORI_TRACE_NUM == null)
			throw new Exception("请传入参数 ORI_TRACE_NUM");
		String VERIFY_CODE			= params.get("VERIFY_CODE");
		if("2".equals((String)params.get("payType")) || "3".equals((String)params.get("payType"))){
			VERIFY_CODE = "";
		}
		if(VERIFY_CODE == null)
			throw new Exception("请传入参数 VERIFY_CODE");
		String userId	= params.get("userId");
		if(userId == null)
			throw new Exception("请传入参数 userId");
		
		//查询签约申请记录
		Map<String, Object> ori_entity = ItsManage.getAuthPayLog(ORI_TRACE_NUM);
		String ORI_TRANS_DATE	= (String) ori_entity.get("its_trans_date");
		String send_sms = (String)ori_entity.get("send_sms");
		String phone	= (String)ori_entity.get("phone");
		if(send_sms.equals("3") && "1".equals((String)params.get("isSendSm"))) {
			//验证短信验证码
			CodeServiceImpl codeServiceImpl = new CodeServiceImpl();
			Long code_id = codeServiceImpl.checkPhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN, phone, Constant.VERIFICATION_CODE_AUTH_PAY, VERIFY_CODE);
			//设置短信验证码已验证
			codeServiceImpl.setPhoneVerificationCode(code_id);
		}
		
		Date now  = new Date();
		String trans_date	= format.format(now);
		String trans_time	= format0.format(now);
		
		Map<String, String> signlogMap = new HashMap<String, String>();
		signlogMap.put("userId", 				userId);
		signlogMap.put("ori_trace_num", 		ORI_TRACE_NUM);
		signlogMap.put("ori_trans_date", 		ORI_TRANS_DATE);
		signlogMap.put("verification_code", 	VERIFY_CODE);
		signlogMap.put("sign_type", 			"6");
		signlogMap.put("trans_date", 			trans_date);
		signlogMap.put("trans_time", 			trans_time);
		//创建签约记录
		Map<String, Object> signlog_entity = ItsManage.createSignLog(signlogMap);
		String trace_num	= (String)signlog_entity.get("trace_num");
		
		Map<String, String> extParams = new HashMap<String, String>();
		extParams.put("transDate", trans_date);
		extParams.put("transTime", trans_time);
		extParams.put("verifyCode", VERIFY_CODE);
		String strResult = Extension.itsService.payACK(trace_num, ORI_TRACE_NUM, ORI_TRANS_DATE, phone, extParams);
		
		try { 
			Document document = DocumentHelper.parseText(strResult);
			Element rootEl = document.getRootElement();
	
			Map<String, String> resultMap = new HashMap<String, String>();
			//修改的签约记录信息
			Map<String, String> modifySignlogMap = new HashMap<String, String>();
			modifySignlogMap.put("id", 		signlog_entity.get("id").toString());
			
	
			//头响应吗
			Element e_HEAD_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_CODE");
			if(e_HEAD_RET_CODE != null){
				resultMap.put("RET_CODE", 		e_HEAD_RET_CODE.getText());
				modifySignlogMap.put("ret_code", 		e_HEAD_RET_CODE.getText());
			}
			//头响应信息
			Element e_HEAD_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/HEAD/RET_MSG");
			if(e_HEAD_RET_MSG != null){
				resultMap.put("RET_MSG", 		e_HEAD_RET_MSG.getText());
				modifySignlogMap.put("ret_msg", 		e_HEAD_RET_MSG.getText());
			}
			//交易流水号
			String its_trace_num = "";
			Element e_ITS_TRACE_NUM = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRACE_NUM");
			if(e_ITS_TRACE_NUM != null){
				resultMap.put("ITS_TRACE_NUM", 			e_ITS_TRACE_NUM.getText());
				modifySignlogMap.put("its_trace_num", 	e_ITS_TRACE_NUM.getText());
				its_trace_num	= e_ITS_TRACE_NUM.getText();
			}
			//签约协议号
			Element e_CONTRACT_NO = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CONTRACT_NO");
			if(e_CONTRACT_NO != null){
				resultMap.put("CONTRACT_NO", 	e_CONTRACT_NO.getText());
				modifySignlogMap.put("bank_contract_no", 	e_CONTRACT_NO.getText());
			}
			//响应码
			Element e_RET_CODE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_CODE");
			String ret_code	= "";
			if(e_RET_CODE != null){
				resultMap.put("RET_CODE", 		e_RET_CODE.getText());
				modifySignlogMap.put("ret_code", 	e_RET_CODE.getText());
				ret_code = e_RET_CODE.getText();
			}
			//响应信息
			Element e_RET_MSG = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/RET_MSG");
			if(e_RET_MSG != null){
				resultMap.put("RET_MSG", 		e_RET_MSG.getText());
				modifySignlogMap.put("ret_msg", 		e_RET_MSG.getText());
			}
			//its交易日期
			Element e_ITS_TRANS_DATE = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_DATE");
			if(e_ITS_TRANS_DATE != null){
				resultMap.put("ITS_TRANS_DATE", 		e_ITS_TRANS_DATE.getText());
				modifySignlogMap.put("its_trans_date", 		e_ITS_TRANS_DATE.getText());
			}
			//its交易时间
			Element e_ITS_TRANS_TIME = (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/ITS_TRANS_TIME");
			if(e_ITS_TRANS_TIME != null){
				resultMap.put("ITS_TRANS_TIME", 			e_ITS_TRANS_TIME.getText());
				modifySignlogMap.put("its_trans_time", 		e_ITS_TRANS_TIME.getText());
			}
			//签约渠道ID
			Element e_CNL_ID	= (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/CNL_ID");
			if(e_CNL_ID != null){
				resultMap.put("CNL_ID", e_CNL_ID.getText());
				modifySignlogMap.put("cnl_id", 		e_CNL_ID.getText());
			}
			//发送短信状态
			Element e_SEND_SMS	= (Element)rootEl.selectSingleNode("/RESPONSE/ENVELOPE/TX_INFO/SEND_SMS");
			if(e_SEND_SMS != null){
				resultMap.put("SEND_SMS", 				e_SEND_SMS.getText());
				modifySignlogMap.put("send_sms", 		e_SEND_SMS.getText());
			}
			
			//错误信息
			Element e_ERROR_CODE = (Element)rootEl.selectSingleNode("/ERROR/ERROR_CODE");
			if(e_ERROR_CODE != null){
				resultMap.put("ERROR_CODE", 		e_ERROR_CODE.getText());
				modifySignlogMap.put("error_code", 	e_ERROR_CODE.getText());
			}
			Element e_ERROR_MSG = (Element)rootEl.selectSingleNode("/ERROR/ERROR_MSG");
			if(e_ERROR_CODE != null){
				resultMap.put("ERROR_MSG", 			e_ERROR_MSG.getText());
				modifySignlogMap.put("error_msg", 	e_ERROR_MSG.getText());
			}
			

			//修改签约记录
			ItsManage.modifySignLog(modifySignlogMap);
			if(ret_code.equals("000000")){	//充值
				Map<String, Object> log_entity = getAuthPayLog(ORI_TRACE_NUM);
				Map<String, Object> depositMap = new HashMap<String, Object>();	
				depositMap.put("orderNo", 		ORI_TRACE_NUM);
				depositMap.put("order_money", 	Long.valueOf(log_entity.get("trans_amount").toString()));
				depositMap.put("out_trade_id", 	its_trace_num);
				depositMap.put("out_bizNo", 	log_entity.get("accountNo"));
				depositMap.put("pay_channelNo", Constant.PAY_CHANNEL_QUICK);
				depositMap.put("trade_time", 		new Date());
				depositMap.put("bank_code", 		log_entity.get("bank_code"));
				depositMap.put("pay_interfaceNo", 	Constant.PAY_INTERFACE_QUICK);
                depositMap.put("card_type",         log_entity.get("card_type"));
				depositMap.put("pay_serialNo",         ORI_TRACE_NUM);
				
				//外部系统充值
				TradeService.payChannelDeposit(depositMap);
			}
			its_logger.info("返回信息:" + resultMap);
			return resultMap;
		} catch (Exception e){
			its_logger.info(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 单笔交易查询
	 * @param params{ori_trace_num:原接入方交易流水号,ori_trans_date:原接入方交易日期}
	 * @return
	 * @throws Exception
	 */
	public static String querySingleTransaction(Map<String, String> params) throws Exception{
		SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmmss");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat format0 = new SimpleDateFormat("HHmmss");
		Date now  = new Date();
		StringBuilder envelope = new StringBuilder();
		envelope.append("<ENVELOPE>")
			.append("<HEAD>")
				.append("<VERSION>v1.0</VERSION>")
				.append("<BUSINESS_TYPE>0001</BUSINESS_TYPE>")
				.append("<PAY_TYPE>05</PAY_TYPE>")
				.append("<TRANS_CODE>4001</TRANS_CODE>")
				.append("<ACCESS_ID>400SmokTst00001</ACCESS_ID>")
				.append("<TRACE_NUM>").append(format1.format(now)).append("</TRACE_NUM>")
				.append("<TRANS_DATE>").append(format.format(now)).append("</TRANS_DATE>")
				.append("<TRANS_TIME>").append(format0.format(now)).append("</TRANS_TIME>")
			.append("</HEAD>")
			.append("<TX_INFO>")
				.append("<ORI_TRACE_NUM>").append(params.get("ori_trace_num")).append("</ORI_TRACE_NUM>")
				.append("<ORI_TRANS_DATE>").append(params.get("ori_trans_date")).append("</ORI_TRANS_DATE>")
			.append("</TX_INFO>")
		.append("</ENVELOPE>");
		
		StringBuilder sb = new StringBuilder();
		sb.append("<REQUEST>")
			.append(envelope)
			.append("<SIGNATURE>")
				.append("<SIGN_TYPE>0</SIGN_TYPE>")
				.append("<SIGN_MSG>")
				.append(MD5Util.MD5(envelope.append(KEY).toString()))
				.append("</SIGN_MSG>")
			.append("</SIGNATURE>")
		.append("</REQUEST>");
		
		BASE64Encoder encoder = new BASE64Encoder();
		String base64 = encoder.encode(sb.toString().getBytes("UTF-8"));
		NameValuePair[] datas = { new NameValuePair("reqMsg", base64), new NameValuePair("msgType", "1") };
		String str = new HttpUtil().post(memberUrl, datas);
		BASE64Decoder decoder = new BASE64Decoder();
		try { 
			byte[] b = decoder.decodeBuffer(str);
			return new String(b, "UTF-8");
		} catch (Exception e){ 
			throw e;
		}
	}
	
	/**
	 * 创建签约记录
	 * @param signlogMap
	 * @throws Exception
	 */
	public static Map<String, Object> createSignLog(Map<String, String> signlogMap)throws Exception{
		its_logger.info("createSignLog start");
		its_logger.info("signlogMap:" + signlogMap);
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		final Map<String, String> _signlogMap = signlogMap;
		return EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				Map<String, Object> signlog_entity = null;
				signlog_entity = DynamicEntityService.createEntity("AMS_AuthPayLog", _signlogMap, null);
				
				if(signlog_entity.get("trace_num") == null){
					Date dt = new Date();
					String trace_num	=  Constant.COMMAND_SPLIT_SIGN + format.format(dt) + signlog_entity.get("id").toString();
					Query query = session.createQuery("update AMS_AuthPayLog set trace_num=:trace_num where id=:id");
					query.setParameter("trace_num", trace_num);
					query.setParameter("id", 		signlog_entity.get("id"));
					
					query.executeUpdate();
					
					signlog_entity.put("trace_num", trace_num);
				}
				return signlog_entity;
				
			}
		}); 
	}
	
	/**
	 * 更新签约记录
	 * @param signlogMap
	 * @throws Exception
	 */
	private static void modifySignLog(Map<String, String> signlogMap)throws Exception{
		its_logger.info("modifySignLog start");
		its_logger.info("signlogMap:" + signlogMap);
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		final Map<String, String> _signlogMap = signlogMap;
		EntityManagerUtil.execute(new TransactionWork<Object>() {
			@Override
			public Object doTransaction(Session session, Transaction tx)
					throws Exception {
				DynamicEntityService.modifyEntity("AMS_AuthPayLog", _signlogMap, null);
				return null;
				
			}
		}); 
	}
	
	/**
	 * 根据流水查询签约记录
	 * @param trace_num
	 * @return
	 */
	public static Map<String, Object> getAuthPayLog(String trace_num)throws Exception{
		final String _trace_num = trace_num;
		return EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> doQuery(Session session)
					throws Exception {
				Query query = session.createQuery("from AMS_AuthPayLog where trace_num=:trace_num order by id desc");
				query.setParameter("trace_num", _trace_num);
				
				List list = query.list();
				if(list.isEmpty())
					return null;
				else
					return (Map<String, Object>)list.get(0);
			}
		});
	}
	
	/**
	 * 根据原流水查询签约记录
	 * @param ori_trace_num
	 * @return
	 */
	public static Map<String, Object> getAuthPayLog2(String ori_trace_num)throws Exception{
		final String _ori_trace_num = ori_trace_num;
		return EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> doQuery(Session session)
					throws Exception {
				Query query = session.createQuery("from AMS_AuthPayLog where ori_trace_num=:ori_trace_num order by id desc");
				query.setParameter("ori_trace_num", _ori_trace_num);
				
				List list = query.list();
				if(list.isEmpty())
					return null;
				else
					return (Map<String, Object>)list.get(0);
			}
		});
	}
}
