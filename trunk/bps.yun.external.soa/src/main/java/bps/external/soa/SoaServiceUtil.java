package bps.external.soa;

import ime.security.util.RSAUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.*;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.common.JedisUtils;
import bps.external.soa.process.Extension;

public class SoaServiceUtil {
	private static Logger logger = Logger.getLogger(SoaServiceUtil.class);
	private static String ip = null;
	private static int port = 0;
	
	static{
		try {
			ip 	= Extension.paramMap.get("cache.ip");
			port = Integer.valueOf(Extension.paramMap.get("cache.port"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据sysid获取从缓存应用
	 * @param bizId		应用编号
	 * @return	应用对像
	 * @throws BizException
	 */
	public static Map<String, Object> getApplication(String bizId) throws BizException{
		logger.info("===================SoaServiceUtil.getApplication start=======================");
		logger.info("参数：bizId=" + bizId);
		
		try{
			//通过缓存获取applicationId
			for(Map<String, Object> temp : Extension.keyList){
				if(bizId.equals(temp.get("sysid"))){
					
					logger.info("ret:" + temp);
					logger.info("===================SoaServiceUtil.getApplication end=======================");
					return temp;
				}
			}
			
			throw new BizException(ErrorCode.APPLICATION_NOTEXSIT, "不存在此应用。");
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	public static Long getApplicationId(String bizId) throws BizException{
		logger.info("===================SoaServiceUtil.getApplicationId start=======================");
		logger.info("参数：bizId=" + bizId);
		
		try{
			//通过缓存获取applicationId
			for(Map<String, Object> temp : Extension.keyList){
				if(bizId.equals(temp.get("sysid"))){
					
					logger.info("ret:" + temp.get("id"));
					logger.info("===================SoaServiceUtil.getApplicationId end=======================");
					return (Long)temp.get("id");
				}
			}
			
			throw new BizException(ErrorCode.APPLICATION_NOTEXSIT, "不存在此应用。");
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 获取用户实体
	 * @param applicationId		应用ID
	 * @param bizUserId			用户uid
	 * @return	Map
	 * @throws BizException
	 */
	public static Map<String, Object> getMemberEntity(Long applicationId, String bizUserId) throws BizException{
		logger.info("===================SoaServiceUtil.getMemberEntity start=======================");
		logger.info("参数：applicationId=" + applicationId + ",bizUserId=" + bizUserId);
		
		try{
			Map<String, Object> ret = Extension.memberService.getUserInfo(applicationId, bizUserId);
			if(ret == null || ret.isEmpty()){
				throw new BizException(ErrorCode.USER_NOTEXSIT, "用户不存在。");
			}
			
			logger.info("返回值：" + ret);
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	/**
	 * 获取用户实体
	 * @param applicationId		应用ID
	 * @param bizUserId			用户UID
	 * @param errorMessage		错误信息
	 * @return Map
	 * @throws BizException
	 */
	public static Map<String, Object> getMemberEntity(Long applicationId, String bizUserId, String errorMessage) throws BizException{
		logger.info("===================SoaServiceUtil.getMemberEntity start=======================");
		logger.info("参数：applicationId=" + applicationId + ",bizUserId=" + bizUserId);

		try{
			Map<String, Object> ret = Extension.memberService.getUserInfo(applicationId, bizUserId);
			if(ret == null || ret.isEmpty()){
				throw new BizException(ErrorCode.USER_NOTEXSIT, errorMessage);
			}

			logger.info("返回值：" + ret);
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 获取应用的key
	 * @param bizId 应用编号
	 * @return	应用对像
	 * @throws Exception
	 */
	public static Map<String, Object> getKeyMap(String bizId) throws Exception{
		logger.info("===================SoaServiceUtil.getKeyMap start=======================");
		logger.info("参数：bizId=" + bizId);
		
		try{
			for(Map<String, Object> temp : Extension.keyList){
				String sysIdTemp = (String)temp.get("sysid");
				if(sysIdTemp.equals(bizId)){
					
					logger.info("ret:" + temp);
					logger.info("===================SoaServiceUtil.getKeyMap end=======================");
					return temp;
				}
			}
			
			throw new BizException(ErrorCode.APPLICATION_NOTEXSIT, "不存在应用或此应用的key不存在。");
		}
		catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "不存在应用或此应用的key不存在。");
		}
	}
	
	/**
	 * 获取RSA操作实体对象
	 * @param bizId		应用编号
	 * @return	RSAUtil
	 * @throws BizException
	 */
	public static RSAUtil getRSA(String bizId) throws BizException{
		logger.info("===================SoaServiceUtil.getRSA start=======================");
		logger.info("参数：bizId=" + bizId);
		
    	try {
    		List<Map<String, Object>> keyList = Extension.keyList;
        	for(Map<String, Object>keyMap : keyList){
        		String id = (String)keyMap.get("sysid");
        		if(bizId.equals(id)){
        			RSAPublicKey publicKey = RSAUtil.getPublicKey((String)keyMap.get("publicKey"));
        			RSAPrivateKey privateKey = RSAUtil.getPrivateKey((String)keyMap.get("privateKey"));
        			
        			logger.info("===================SoaServiceUtil.getRSA end=======================");
        			return new RSAUtil(publicKey, privateKey);
        		}
        		
        		//continue;
        	}
        	
        	throw new BizException(ErrorCode.OTHER_ERROR, "缓存中不存在此应用。");
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
    }
	
	/**
	 * RSA加密字符串
	 * @param bizId 应用的业务系统编号
	 * @param str   待解密的字符串
	 * @return	String
	 * @throws BizException
	 */
	public static String rsaEncrypt(String bizId, String str) throws BizException{
		logger.info("======================SoaServiceUtil.rsaEncrypt start==========================");
		logger.info("参数：bizId=" + bizId + ",str=" + str);
		
		try{
			ime.security.util.RSAUtil rsa = getRSA(bizId);
			String ret = rsa.encrypt(str);
			
			logger.info("ret:" + ret);
			logger.info("======================SoaServiceUtil.rsaEncrypt end==========================");
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.ENCRYPT_ERROR, e.getMessage());
		}
	}
	
	/**
	 * RSA解密字符串
	 * @param bizId 应用的业务系统编号
	 * @param str   待解密的字符串
	 * @return	String
	 * @throws BizException
	 */
	public static String rsaDencrypt(String bizId, String str) throws BizException{
		logger.info("======================SoaServiceUtil.rsaDencrypt start==========================");
		logger.info("参数：bizId=" + bizId + ",str=" + str);
		
		try{
			ime.security.util.RSAUtil rsa = getRSA(bizId);
			String ret = rsa.dencrypt(str);
			ret = ret.replaceAll("[ |　]", " ").trim();
			logger.info("ret:" + ret);
			logger.info("======================SoaServiceUtil.rsaDencrypt end==========================");
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.DECRYPT_ERROR, "解密失败");
		}
	}
	
	/**
	 * 验证RSA签名
	 * @param bizId     应用编号
	 * @param reqStr    请求参数字符串
	 * @param timestamp 时间戳
	 * @param sign      请求的签名
	 * @return	Boolean
	 * @throws BizException
	 */
	public static Boolean validateRsaSign(String bizId, String reqStr, String timestamp, String sign) throws BizException{
		logger.info("===================SoaServiceUtil.validateRsaSign start=======================");
		logger.info("参数：bizId=" + bizId + ",reqStr=" + reqStr + ",timestamp=" + timestamp + ",sign=" + sign);
		
		try{
			Map<String, Object> applicationKey = getKeyMap(bizId);
			String signStr = bizId + reqStr + timestamp;
			logger.info("验证签名signStr=" + signStr);
			String public_key = (String)applicationKey.get("publicKey");
			PublicKey publicKey = RSAUtil.getPublicKey(public_key);
			
			Boolean ret = RSAUtil.verify(publicKey, signStr, sign);
			if(!Boolean.TRUE.equals(ret)){
				throw new BizException(ErrorCode.CHECK_SIGN_ERROR, "验证签名失败。");
			}
			logger.info("ret:" + ret);
			logger.info("===================SoaServiceUtil.validateRsaSign end=======================");
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.CHECK_SIGN_ERROR, "验证签名失败。");
		}
	}
	
	/**
	 * RSA签名
	 * @param	bizId	应用ID
	 * @param 	signStr	签名内容
	 * @return String
	 * @throws BizException
	 */
	public static String rsaSign(String bizId, String signStr) throws BizException{
		logger.info("===================SoaServiceUtil.rsaSign start=======================");
		logger.info("参数：bizId=" + bizId + ",signStr=" + signStr);
		
		try{
			Map<String, Object> applicationKey = getKeyMap(bizId);
			String privateKeyStr = (String)applicationKey.get("privateKey");
			PrivateKey privateKey = RSAUtil.getPrivateKey(privateKeyStr);

			String ret = RSAUtil.sign(privateKey, signStr);
			
			logger.info("ret:" + ret);
			logger.info("===================SoaServiceUtil.rsaSign end=======================");
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.SIGN_ERROR, "RSA加密失败。");
		}
	}
	
	/**
	 * 获得绑定银行卡id
	 * @param bizId      应用id
	 * @param memberId   用户id
	 * @param bankCardNo 加密的银行卡号
	 * @return	绑定银行ID
	 * @throws BizException
	 */
	public static Long getBindBankCardId(String bizId, Long memberId, String bankCardNo) throws BizException{
		logger.info("===================SoaServiceUtil.getBindBankCardId start=======================");
		logger.info("参数：bizId=" + bizId + ",memberId=" + memberId + ",bankCardNo=" + bankCardNo);
		
		try{
			String deCardNo = rsaDencrypt(bizId, bankCardNo);
//			deCardNo = deCardNo.trim();
			logger.info("getBindBankCard参数：memberId=" + memberId + ",deCardNo=" + deCardNo);
			Map<String, Object> cardInfo = Extension.memberService.getBindBankCard(memberId, deCardNo);
			logger.info("getBindBankCard返回：" + cardInfo);
			if(cardInfo == null || cardInfo.isEmpty()){
				throw new BizException(ErrorCode.CARD_BIND_LOG_NOTEXSIT, "此银行卡未绑定。");
			}
			
			logger.info("ret:" + cardInfo.get("id"));
			logger.info("===================SoaServiceUtil.getBindBankCardId end=======================");
			return (Long)cardInfo.get("id");
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}

	/**
	 * 获得绑定银行卡实体
	 * @param bizId			应用id
	 * @param memberId		用户id
	 * @param enBankCardNo	加密的银行卡号
	 * @return	绑定银行实体
	 * @throws BizException
     */
	public static Map<String, Object> getBindBankCard(String bizId, Long memberId, String enBankCardNo) throws BizException{
		logger.info("===================SoaServiceUtil.getBindBankCard start=======================");
		logger.info("参数：bizId=" + bizId + ",memberId=" + memberId + ",enBankCardNo=" + enBankCardNo);

		try{
			String deCardNo = rsaDencrypt(bizId, enBankCardNo);
//			deCardNo = deCardNo.trim();
			logger.info("getBindBankCard参数：memberId=" + memberId + ",deCardNo=" + deCardNo);
			Map<String, Object> cardInfo = Extension.memberService.getBindBankCard(memberId, deCardNo);
			logger.info("getBindBankCard返回：" + cardInfo);
			if(cardInfo == null || cardInfo.isEmpty()){
				throw new BizException(ErrorCode.CARD_BIND_LOG_NOTEXSIT, "此银行卡未绑定。");
			}

			logger.info("===================SoaServiceUtil.getBindBankCard end=======================");
			return cardInfo;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 检查是否超过最长过期失效。
	 * @param sdf				时间格式
	 * @param overdueDateStr	时间
	 * @throws BizException
	 */
	public static void checkOverdue(SimpleDateFormat sdf, String overdueDateStr) throws BizException{
		logger.info("===================SoaServiceUtil.checkOrverdue start=======================");
		logger.info("参数：sdf=" + sdf + ",overdueDateStr=" + overdueDateStr);
		
		try{
			Date overdueDate = sdf.parse(overdueDateStr);
			Calendar overdueCalendar = Calendar.getInstance();
			overdueCalendar.setTime(overdueDate);
			
			Calendar maxOverdueCalendar = Calendar.getInstance();
			maxOverdueCalendar.add(Calendar.HOUR, Constant.ORDER_OVERDUE_HOUR.intValue());
			
			if(overdueCalendar.after(maxOverdueCalendar)){
				throw new BizException(ErrorCode.OTHER_ERROR, "订单过期时间超过限定。");
			}
			
			logger.info("===================SoaServiceUtil.checkOrverdue end=======================");
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 检查来源
	 * @param source 来源
	 * @throws BizException
	 */
	public static void checkSource(Long source) throws BizException{
		logger.info("===================SoaServiceUtil.checkSource start=======================");
		logger.info("参数：source=" + source);
		
		try{
			if(!(source.equals(Constant.SOURCE_PC) || source.equals(Constant.SOURCE_PHONE))){
				throw new BizException(ErrorCode.SOURCE_NOT_SUPPORT, "不支持的来源类型。");
			}
			
			logger.info("===================checkSource end=======================");
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 验证短信验证码
	 * @param applicationId			应用ID
	 * @param phone					手机
	 * @param verificationCodeType	验证类型
	 * @param verificationCode		验证码
	 */
	public static void checkVerificationCode(Long applicationId, String phone, Long verificationCodeType, String verificationCode) throws BizException{
		logger.info("===================SoaServiceUtil.checkVerificationCode start=======================");
		logger.info("参数：applicationId=" + applicationId + ",phone=" + phone + ",verificationCodeType=" + verificationCodeType + ",verificationCode=" + verificationCode);
		
		try{
			logger.info("checkPhoneVerificationCode参数：applicationId=" + applicationId + ",phone=" + phone + ",verificationCodeType=" + verificationCodeType + ",verificationCode=" + verificationCode);
			Long verificationCodeId = Extension.codeService.checkPhoneVerificationCode(applicationId, phone, verificationCodeType, verificationCode);
			logger.info("checkPhoneVerificationCode返回：" + verificationCodeId);
			if(verificationCodeId > 0){
				//设置验证码失效
				Extension.codeService.setPhoneVerificationCode(verificationCodeId);
			}else{
				throw new BizException(ErrorCode.VERIFICATIONCODE_ERROR, "验证验证码失败。");
			}
			
			logger.info("===================SoaServiceUtil.checkVerificationCode end=======================");
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 获取现金账户集的编号。
	 * @param applicationId 应用id
	 * @return	账户集的编号
	 * @throws BizException
	 */
	public static String getCaseAccountSetNo(Long applicationId) throws BizException{
		logger.info("=======================SoaServiceUtil.getAccountSetNo start=======================");
		logger.info("参数：applicationId=" + applicationId);
		
		try{
			Map<String, Object> CaseAccountSet = Extension.accountService.getApplicationCashAccountType(applicationId);
			if(CaseAccountSet == null || CaseAccountSet.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "不存在现金账户集。");
			}
			
			String ret = (String)CaseAccountSet.get("codeNo");
			logger.info("ret:" + ret);
			logger.info("=======================SoaServiceUtil.getAccountSetNo end=======================");
			
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 根据银行code获取银行name
	 * @param bankCode	银行卡号
	 * @return	银行编码
	 * @throws BizException
	 */
	public static String getBankName(String bankCode) throws BizException{
		logger.info("===========================SoaServiceUtil.getBankName start============================");
		logger.info("参数:bankCode=" + bankCode);
		
		try{
			logger.info("getBankCode参数：bankCode=" + bankCode);
			Map<String, Object> bankCodeInfo = Extension.memberService.getBankCode(bankCode);
			logger.info("getBankCode返回：" + bankCodeInfo);
			if(bankCodeInfo == null || bankCodeInfo.isEmpty()){
				throw new BizException(ErrorCode.BANK_CODE_ERROR, "银行代码不正确。");
			}
			String bankName = (String)bankCodeInfo.get("bank_label");
			
			logger.info("ret:" + bankName);
			logger.info("===========================SoaServiceUtil.getBankName end============================");
			return bankName;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 把支付方式转换为相应的银行卡类型
	 * @param payType	支付类型
	 * @return	银行卡类型
	 * @throws BizException
	 */
	public static Long payTypeToCardType(Long payType) throws BizException{
		logger.info("===========================SoaServiceUtil.payTypeToCardType start============================");
		logger.info("参数：payType=" + payType);
		
		try{
			Long ret = null;
			switch(payType.intValue()){
				case 1:
					ret = 1L;
					break;
				case 4:
					ret = 1L;
					break;
				case 11:
					ret = 2L;
					break;
			}
			
			if(ret == null){
				throw new BizException(ErrorCode.OTHER_ERROR, "网关支付类型不存在。");
			}
			
			logger.info("ret:" + ret);
			logger.info("===========================SoaServiceUtil.payTypeToCardType end============================");
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 获取指定的通道商户配置信息
	 * @param payInterfaceNo	通道商户
	 * @param sysid				系统号
	 * @return	商户配置信息
	 */
	public static Map<String, Object> getPiAppConf(String payInterfaceNo, String sysid) throws BizException{
		logger.info("===========================SoaServiceUtil.getPiAppConf start============================");
		logger.info("参数：payInterfaceNo=" + payInterfaceNo + ",sysid=" + sysid);
		
		try{
			Map<String, Object> applicationEntity = getApplication(sysid);
			String orgNo = (String)applicationEntity.get("codeNo");
			String key = payInterfaceNo + "_" + sysid;
			
			JedisPool pool = null;
			Jedis jedis = null;
			try{
				String ip = Extension.paramMap.get("cache.ip");
				int port = Integer.valueOf(Extension.paramMap.get("cache.port"));
				
				pool = JedisUtils.getPool(ip, port);
				jedis = pool.getResource();
				String payInterAppConf = jedis.get(Constant.REDIS_KEY_PI_APP_CONF);
				JSONObject payInterAppConfObj = new JSONObject(payInterAppConf);
				Map<String, Object> ret = ((JSONObject)payInterAppConfObj.get(key)).getMap();
				
				
				logger.info("返回ret：" + ret);
				logger.info("===========================SoaServiceUtil.getPiAppConf end============================");
				return ret;
			}catch(Exception e){
				// 释放redis对象
				if (pool != null)
					pool.returnBrokenResource(jedis);
				logger.error(e.getMessage(), e);
				throw e;
			}finally{
				// 返还到连接池
				JedisUtils.returnResource(pool, jedis);
			}
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 通过key获取缓存的value
	 * @param key	缓存key
	 * @return	缓存内容
	 * @throws BizException
	 */
	public static String getCacheByKey(String key) throws BizException{
		logger.info("getCacheByKey参数:" + key);
		
		JedisPool pool = null;
		Jedis jedis = null;
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();
			
			String cacheValue = jedis.get(key);
			logger.info("getCacheByKey返回=" + cacheValue);
			return cacheValue;
		}catch(Exception e){
			// 释放redis对象
			if( pool != null )
				pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}
	
	/**
	 * 判断应用是否可以使用某个接口
	 * @param sysid		应用ID
	 * @param soaName	接口名称
	 * @param version	版本
	 * @throws BizException 
	 */
	public static void judgeSoaAvailable(String sysid, String soaName, String version) throws BizException{
//		logger.info("参数：sysid=" + sysid + ",soaNAme=" + soaName + ",version=" + version);
//		
//		String redisKey = Constant.REDIS_KEY_APP_SOA_API_CONF;
//		String key = sysid;
//		
//		try{
//			JedisPool pool = null;
//			Jedis jedis = null;
//			
//			try{
//				String ip = Extension.paramMap.get("cache.ip");
//				int port = Integer.valueOf(Extension.paramMap.get("cache.port"));
//				
//				pool = JedisUtils.getPool(ip, port);
//				jedis = pool.getResource();
//				String soaConf = jedis.get(redisKey);
//				JSONObject soaConfObj = new JSONObject(soaConf);
//				JSONArray soaAppConfArray =  (JSONArray)soaConfObj.get(key);
//				
//				if(soaAppConfArray == null || soaAppConfArray.length() == 0){
//					throw new BizException(ErrorCode.APPLICATION_SOA_NOT_AVAILABLE, "应用没有权限使用此接口");
//				}
//				
//				boolean isAvailable = false;
//				for(int i = 0; i < soaAppConfArray.length(); i++){
//					JSONObject temp = (JSONObject)soaAppConfArray.get(i);
//					String soaNameTemp = (String)temp.get("soaNameEn");
//					String versionTemp = (String)temp.get("soaVersion");
//					Boolean isOpen = (Boolean)temp.get("isOpen");
//					
//					if(soaName.equals(soaNameTemp) && version.equals(versionTemp) && Boolean.TRUE.equals(isOpen)){
//						isAvailable = true;
//						break;
//					}
//				}
//				
//				if(isAvailable == false){
//					throw new BizException(ErrorCode.APPLICATION_SOA_NOT_AVAILABLE, "应用没有权限使用此接口");
//				}
//				
//				logger.info("===========================SoaServiceUtil.judgeSoaAvailable end============================");
//			}catch(BizException bizE){
//				logger.error(bizE.getMessage(), bizE);
//				pool.returnBrokenResource(jedis);
//				throw bizE;
//			}catch(Exception e){
//				logger.error(e.getMessage(), e);
//				pool.returnBrokenResource(jedis);
//				throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
//			}finally{
//				// 返还到连接池
//				JedisUtils.returnResource(pool, jedis);
//			}
//		}catch(Exception e){
//			throw new BizException(ErrorCode.APPLICATION_SOA_NOT_AVAILABLE, "应用没有权限使用此接口");
//		}
	}

	/**
	 * 检验投标金额大于总金额
	 * @param applicationId		应用ID
	 * @param goodsType			商品类型
	 * @param goodsNo			商品号
	 * @param tradeCode			业务类型
	 * @param amount			本次金额
     * @throws Exception
     */
	public static void chickAgentCollectAmount(Long applicationId,Long goodsType, String goodsNo, String tradeCode, Long amount)throws Exception{

		Map<String, Object> goodsEntity = Extension.goodsService.queryGoods( applicationId, goodsType, goodsNo );
		List<Map<String, Object>> orderList = Extension.orderService.getOrderListByGoods(Constant.ORDER_TYPE_DAISHOU, tradeCode, goodsType, goodsNo, Constant.ORDER_STATE_SUCCESS);
		if( goodsEntity == null ){
			throw new BizException(ErrorCode.GOODS_NOTEXSIT,"标的商品不存在！");
		}
		Long isOutAmount = goodsEntity.get("highest_amount") == null ? (Long)goodsEntity.get("amount") : (Long)goodsEntity.get("highest_amount");

		Long oldAmount = 0L;
		for (Map<String, Object> order : orderList){
			Long orderAmount = (Long)order.get("order_money");
			oldAmount += orderAmount;
		}
		logger.info("chickAgentCollectAmount---------------------");
		logger.info("goodsEntity:"+goodsEntity);
		logger.info("orderList:"+orderList);
		logger.info("isOutAmount:"+isOutAmount);
		logger.info("oldAmount:"+oldAmount);
		logger.info("amount:"+amount);

		if (isOutAmount < (oldAmount+amount)){
			throw new BizException(ErrorCode.ORDER_THE_EXCESS,"标的超额");
		}
	}
	public static void checkAgentCollectBizUserId(Long applicationId,Long goodsType, String goodsNo, String receiveBizId)throws Exception{
		logger.info("applicationId:"+applicationId+"goodsType:"+goodsType+"goodsNo:"+goodsNo+"receiveBizId:"+receiveBizId);
		Map<String, Object> goodsEntity = Extension.goodsService.queryGoods( applicationId, goodsType, goodsNo );
		Long memberId = (Long)goodsEntity.get("borrower_id");
		if( goodsEntity == null ){
			throw new BizException(ErrorCode.GOODS_NOTEXSIT,"标的商品不存在！");
		}
		Map<String, Object> memberEntity = Extension.memberService.getUserInfo(memberId);
		if( memberEntity == null ){
			throw new BizException(ErrorCode.GOODS_NOTEXSIT,"标的商品借款人不存在！");
		}
		String goodsBizUserId = (String)memberEntity.get("biz_user_id");
		logger.info("receiveBizId:"+receiveBizId);
		logger.info("goodsBizUserId:"+goodsBizUserId);
		if(!receiveBizId.equals(goodsBizUserId)){
			throw new BizException(ErrorCode.GOODS_NOTEXSIT,"商品录入时的bizUserId和收款人不一致！");
		}
	}
	/**
	 * 查询剩余可投资金额（订单完成时调用）
	 * @param applicationId		应用ID
	 * @param goodsType			商品类型
	 * @param goodsNo			商品号
	 * @param tradeCode			业务类型
	 * @return 剩余可投资金额
     * @throws Exception
     */
	public static Long getRemainAmount(Long applicationId,Long goodsType, String goodsNo, String tradeCode, Long amount)throws Exception{
		Map<String, Object> goodsEntity = Extension.goodsService.queryGoods( applicationId, goodsType, goodsNo );
		List<Map<String, Object>> orderList = Extension.orderService.getOrderListByGoods( Constant.ORDER_TYPE_DAISHOU, tradeCode, goodsType, goodsNo, Constant.ORDER_STATE_SUCCESS);

		if( goodsEntity == null ){
			throw new BizException(ErrorCode.GOODS_NOTEXSIT,"标的商品不存在！");
		}

		Long isOutAmount = goodsEntity.get("highest_amount") == null ? (Long)goodsEntity.get("amount") : (Long)goodsEntity.get("highest_amount");

		Long oldAmount = 0L;
		for (Map<String, Object> order : orderList){
			Long orderAmount = (Long)order.get("order_money");
			oldAmount += orderAmount;
		}
		logger.info("isOutAmount:"+isOutAmount);
		logger.info("oldAmount:"+oldAmount);
		logger.info("amount:"+amount);
		return (isOutAmount- oldAmount - amount);
	}

	/**
	 * 检查订单支付方式
	 * @param payMethod		支付方式
	 * @param orderType		订单类型
	 *		支付方式：1、快捷	2、网关	3、余额	4、POS 5、代扣 6、代金 99、其他
	 *
	 */
	public static void chickPayMethod(JSONObject payMethod, Long orderType)throws Exception{
		logger.info("-----------------chickPayMethod begin-----------------");
		logger.info("payMethod:"+ payMethod);
		logger.info("orderType:"+ orderType);

		JSONArray balance 			= payMethod.optJSONArray("BALANCE");
		JSONObject itsPayInfo 		= payMethod.optJSONObject("QUICKPAY");
		JSONObject gatewayPayInfo 	= payMethod.optJSONObject("GATEWAY");
		JSONObject posPay 			= payMethod.optJSONObject("POSPAY");
		JSONObject daikou 			= payMethod.optJSONObject("DAIKOU");
		JSONObject coupon 			= payMethod.optJSONObject("COUPON");

		if( balance == null && itsPayInfo == null && gatewayPayInfo == null && posPay == null && coupon == null && daikou == null){
			throw new BizException(ErrorCode.NO_PAY_METHOD, "请传入支付方式！");
		}
		if (Constant.ORDER_TYPE_DEPOSIT.equals(orderType) || Constant.ORDER_TYPE_DAISHOU.equals(orderType) || Constant.ORDER_TYPE_SHOPPING.equals(orderType)){

			if( posPay != null && (itsPayInfo != null || gatewayPayInfo != null ||  balance != null || coupon != null) ){
				throw new BizException(ErrorCode.NO_PAY_METHOD, "POS不可同时和其他支付一起使用。");
			}


			if((itsPayInfo != null && gatewayPayInfo != null) || (itsPayInfo != null && daikou != null) || (gatewayPayInfo != null && daikou != null)){
				throw new BizException(ErrorCode.NO_PAY_METHOD, "快捷、网关和代扣不可两两同时使用。");
			}

			if( gatewayPayInfo != null ){
				Long payType = gatewayPayInfo.optLong("payType");
				if( payType.equals(27L) && (itsPayInfo != null ||  balance != null || coupon != null || posPay != null || daikou != null)){
					throw new BizException(ErrorCode.NO_PAY_METHOD, "移动认证支付不可同时和其他支付一起使用。");
				}else if(payType.equals(28L)){
					if (gatewayPayInfo.get("bankCardNo") == null )
						throw new BizException(ErrorCode.NO_PAY_METHOD, "当网关支付是Web认证支付（payType= 28）时，bankCardNo必填,RSA加密");
				}
			}

//			if( itsPayInfo != null && gatewayPayInfo != null && posPay != null && balance != null) {
//				throw new BizException(ErrorCode.NO_PAY_METHOD, "快捷、网关、余额和POS不可同时使用。");
//			}else if(itsPayInfo != null && gatewayPayInfo != null){
//				throw new BizException(ErrorCode.NO_PAY_METHOD, "快捷和网关不可同时使用。");
//			}else if ( gatewayPayInfo != null && posPay != null ){
//				throw new BizException(ErrorCode.NO_PAY_METHOD, "网关和POS不可同时使用。");
//			}else if ( itsPayInfo != null && posPay != null ){
//				throw new BizException(ErrorCode.NO_PAY_METHOD, "快捷和POS不可同时使用。");
//			}else if(  posPay != null && balance != null){
//				throw new BizException(ErrorCode.NO_PAY_METHOD, "余额和POS不可同时使用。");
//			}
		}
		if( Constant.ORDER_TYPE_DEPOSIT.equals(orderType) && posPay != null ){
			throw new BizException(ErrorCode.NO_PAY_METHOD, "充值不能使用POS进行支付。");
		}
		logger.info("-----------------chickPayMethod end--------------------");
	}

	/**
	 * JSON遍历，如果是字符串去空格
	 * @param json	json
     */
	public static void jsonTrim(JSONObject json){
		try {
			Iterator it = json.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
//				Object value = json.opt(key);
				try {
					Object value = json.get(key);
					if (value != null ) {
						if (value instanceof String) {
//							String testStr = "　　西式灯饰受欢迎 尽情演绎奢华味道";
//							value = value.toString().replaceAll("[ |　]", " ").trim();
							value = value.toString().trim();
						}else if (value instanceof JSONArray ){
							JSONArray ja = json.getJSONArray(key);
							for (int i = 0; i < ja.length(); i++) {
								JSONObject jo = ja.getJSONObject(i);
								jsonTrim(jo);
							}
						}else if (value instanceof JSONObject){
							JSONObject  jo = json.getJSONObject(key);
							jsonTrim(jo);
						}
					}
					json.put(key,value);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}catch (Exception e){
			System.out.println(e);
		}
	}
}
