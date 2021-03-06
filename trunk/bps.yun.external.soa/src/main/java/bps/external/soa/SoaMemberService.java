package bps.external.soa; 

import ime.soa.ISOAService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import bps.common.Constant;
import bps.common.ErrorCode;
import apf.util.BusinessException;
import bps.common.BizException;
import bps.external.soa.process.Extension;

public class SoaMemberService implements ISOAService{
	private static Logger logger = Logger.getLogger("member");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public JSONObject doService(String s, JSONArray jsonarray) throws Exception {
		return null;
	}

	public JSONObject doService(String s, JSONObject json) throws Exception {
		logger.info("s=" + s + " json=" + json);
		SoaServiceUtil.jsonTrim(json);
		logger.info( "json.trim=" + json);

		if("createMember".equals(s))
			return createMember(json);                     //创建会员
		else if("sendVerificationCode".equals(s))
			return sendVerificationCode(json);             //发送短信验证码
		else if("checkVerificationCode".equals(s))
			return checkVerificationCode(json);         //验证短信验证码
		else if("setRealName".equals(s))
			return setRealName(json);                      //实名认证
		else if("bindPhone".equals(s))
			return bindPhone(json);                        //绑定手机
		else if("setCompanyInfo".equals(s)) 
			return setCompanyInfo(json);                   //设置企业会员信息
		else if("setMemberInfo".equals(s))
			return setMemberInfo(json);                    //设置个人会员信息
		else if("getMemberInfo".equals(s))
			return getMemberInfo(json);                    //获取会员信息（个人和企业）
		else if("getBankCardBin".equals(s))
			return getBankCardBin(json);                   //查询卡bin
		else if("applyBindBankCard".equals(s))
			return applyBindBankCard(json);                //请求绑定银行卡
		else if("bindBankCard".equals(s))
			return bindBankCard(json);                     //确认银行卡绑定
		else if("threeElementsBindBankCard".equals(s))
			return threeElementsBindBankCard(json);        //三要素银行卡绑定
		else if("setSafeCard".equals(s))					
			return setSafeCard(json);						//设置安全卡
		else if("queryBankCard".equals(s))
			return queryBankCard(json);                    //查询绑定的银行卡
		else if("unbindBankCard".equals(s))
			return unbindBankCard(json);                   //解绑绑定银行卡
		else if("changeBindPhone".equals(s))
			return changeBindPhone(json);                  //更改绑定手机
		else if("lockMember".equals(s))
			return lockMember(json);                       //锁定用户
		else if("unlockMember".equals(s))
			return unlockMember(json);                     //解锁用户
		else
	        throw new BusinessException("SOA.NoSuchMethod", (new StringBuilder()).append("找不到相应的服务:").append(getServiceName()).append(".").append(s).toString());
	}

	public String getServiceName() {
		return "MemberService";
	}

	public boolean isMustLogined() {
		return false;
	}

	public boolean isMustValidateClient() {
		return false;
	}
	
	/**
	 * 创建会员
	 * @param json
	 * 			bizUserId   业务系统用户唯一标识
	 * 			memberType  会员类型， 1：个人。2：企业
	 * 			extendParam 扩展参数
	 * @return
	 * @throws Exception
	 */
	public JSONObject createMember(JSONObject json) throws BusinessException{
		logger.info("=======================createMember start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			Long memberType = json.isNull("memberType") ? null : json.optLong("memberType");
			JSONObject extendParam = (JSONObject)json.get("extendParam");
			
			logger.info("bizId:" + bizId + ",version=" + version);
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(memberType == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数memberType为null");
			
			if(!memberType.equals(Constant.MEMBER_TYPE_ENTERPRISE) && !memberType.equals(Constant.MEMBER_TYPE_PERSON)){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "会员类型不正确");
			}
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "createMember", version);
			
			bizUserId = bizUserId.trim();
			
			//检查bizUserId是否为B2C bizUserId
			if(Constant.B2C_BIZ_USER_ID.equals(bizUserId)){
				throw new BizException(ErrorCode.OTHER_ERROR, bizUserId + "为系统保留用户标识，不能使用。");
			}
			
			//检查并获取application
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			
			logger.info("createBizMember参数：applicationId=" + applicationId + ",bizUserId=" + bizUserId + ",memberType=" + memberType + ",extendParam=" + extendParam);
			//创建会员
			Map<String, Object> extendParamMap = extendParam == null ? null : extendParam.getMap();
			Map<String, Object> resp = Extension.memberService.createBizMember(applicationId, bizUserId, memberType, extendParamMap);
			logger.info("createBizMember返回:" + resp);
			
			JSONObject ret = new JSONObject();
			ret.put("userId", resp.get("userId"));
			ret.put("bizUserId", bizUserId);
			
			logger.info("return:" + ret);
			logger.info("=======================createMember end===========================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" : bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 发送短信验证码
	 * @param json
	 * 			userId               业务系统用户唯一标识
	 * 			phone                手机号码
	 * 			verificationCodeType 验证码类型
	 * @return
	 * @throws Exception
	 */
	public JSONObject sendVerificationCode(JSONObject json) throws BusinessException {
		logger.info("=======================sendVerificationCode start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String phone = (String)json.get("phone");
			Long verificationCodeType = json.isNull("verificationCodeType") ? null : json.optLong("verificationCodeType");
			JSONObject extendParam = (JSONObject)json.get("extendParam");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(phone))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数phone为空或null");
			if(verificationCodeType == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数verificationCodeType为null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "sendVerificationCode", version);
			
			bizUserId = bizUserId.trim();
			
			//检查用户是否存在
			Long sysApplicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String,Object> memberEntity = SoaServiceUtil.getMemberEntity(sysApplicationId, bizUserId);
			
			//Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Long applicationId = 1L; //目前写死
			
			extendParam = extendParam == null ? new JSONObject() : extendParam;
			if(extendParam == null || extendParam.length() == 0)
				extendParam = new JSONObject();
			extendParam.put("member_id", memberEntity.get("id"));
			logger.info("generatePhoneVerificationCode参数:applicationId=" + applicationId + ",phone=" + phone + ",verificationCodeType=" + verificationCodeType + ",extendParam=" + extendParam);
			String content = Extension.codeService.generatePhoneVerificationCode(applicationId, phone, verificationCodeType, extendParam.getMap());
			logger.info("generatePhoneVerificationCode返回：" + content);
			
			logger.info("sendSM参数：phone=" + phone + ",content=" + content);
			Extension.otherService.sendSM(phone, content);
			
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("phone", phone);
			
			logger.info("return:" + ret);
			logger.info("=======================sendVerificationCode end===========================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 验证短信验证码
	 * @param json
	 * 			userId               云账户用户唯一标识
	 * 			phone                手机号码
	 * 			verificationCodeType 验证码类型
	 * 			verificationCode     验证码
	 * @return
	 * @throws Exception
	 */
	public JSONObject checkVerificationCode(JSONObject json) throws BusinessException{
		logger.info("=======================validateVerificationCode start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String phone = (String)json.get("phone");
			Long verificationCodeType = json.isNull("verificationCodeType") ? null : json.optLong("verificationCodeType");
			String verificationCode = (String)json.get("verificationCode");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(phone))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数phone为空或null");
			if(verificationCodeType == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数verificationCodeType为null");
			if(StringUtils.isBlank(verificationCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数verificationCode为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "checkVerificationCode", version);
			
			bizUserId = bizUserId.trim();
			
			//检查用户是否存在
			Long sysApplicationId = SoaServiceUtil.getApplicationId(bizId);
			SoaServiceUtil.getMemberEntity(sysApplicationId, bizUserId);
			
			//Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Long applicationId = 1L; //目前写死
			
			logger.info("checkPhoneVerificationCode参数：applicationId=" + applicationId + ",phone=" + phone + ",verificationCodeType=" + verificationCodeType + ",verificationCode=" + verificationCode);
			Long verificationCodeId = Extension.codeService.checkPhoneVerificationCode(applicationId, phone, verificationCodeType, verificationCode);
			logger.info("checkPhoneVerificationCode返回:" + verificationCodeId);
			if(verificationCodeId > 0){
				//设置验证码失效
				logger.info("setPhoneVerificationCode参数：verificationCodeId=" + verificationCodeId);
				Extension.codeService.setPhoneVerificationCode(verificationCodeId);
				
				logger.info("=======================validateVerificationCode end===========================");
				JSONObject ret = new JSONObject();
				ret.put("bizUserId", bizUserId);
				ret.put("phone", phone);
				return ret;
			}else{
				throw new BizException(ErrorCode.VERIFICATIONCODE_ERROR, "验证验证码失败。");
			}
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 个人实名认证
	 * @param json
	 * 			userId       云账户用户唯一标识
	 * 			name		   姓名
	 * 			identityType 证件类型，目前只支持身份证
	 * 			identityNo   证件号
	 * @return
	 * @throws BizException
	 */
	public JSONObject setRealName(JSONObject json) throws BusinessException{
		logger.info("=======================setRealName start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			Boolean isAuth = json.get("isAuth") == null ? true : json.getBoolean("isAuth");
			String name = (String)json.get("name");
			Long identityType = json.isNull("identityType") ? null : json.optLong("identityType");
			String identityNo = (String)json.get("identityNo");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(name))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数name为空或null");
			if(identityType == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数identityType为null");
			if(StringUtils.isBlank(identityNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数identityNo为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "setRealName", version);
			
			bizUserId = bizUserId.trim();
			
			if(identityType != 1){
				throw new BizException(ErrorCode.CERTIFICATES_ERROR, "不支持证件类型");
			}
		
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			
			//检查是否是个人用户
			Long memberType = (Long)memberEntity.get("member_type");
			if(!Constant.MEMBER_TYPE_PERSON.equals(memberType)){
				throw new BizException(ErrorCode.USER_TYPE_ERROR, "会员类型不正确。");
			}
			
			//身份证解密
			String deIdentityNo = SoaServiceUtil.rsaDencrypt(bizId, identityNo);
			
			Long memberId = (Long)memberEntity.get("id");
			logger.info("setRealNameInfo参数：memberId=" + memberId + ",identityType=" + identityType + ",deIdentityNo=" + deIdentityNo + ",name=" + name);
			Extension.memberService.setRealNameInfo(memberId, identityType.toString(), deIdentityNo, name);
			
			logger.info("=======================setRealName end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("name", name);
			ret.put("identityType", identityType);
			ret.put("identityNo", identityNo);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 绑定手机
	 * @param json
	 * 			userId           云账户用户唯一标识
	 * 			phone            手机
	 * 			verificationCode 验证码
	 * @return
	 */
	public JSONObject bindPhone(JSONObject json) throws BusinessException{
		logger.info("=======================bindPhone start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String phone = (String)json.get("phone");
			String verificationCode = (String)json.get("verificationCode");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(phone))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数phone为空或null");
			if(StringUtils.isBlank(verificationCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数verificationCode为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "bindPhone", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			
			//检测用户是否已经绑定手机
			if(memberEntity.get("isPhone_checked") != null && (Boolean)memberEntity.get("isPhone_checked")){
				throw new BizException(ErrorCode.HAS_BIND_PHONE,"手机已绑定");
			}
			
			//检测验证码
			logger.info("checkPhoneVerificationCode参数:applicationId=" + 1 + ",phone=" + phone + ",verificationCodeType=" + Constant.VERIFICATION_CODE_TYPE_SET_NEW_PHONE + ",verificationCode=" + verificationCode);
			//Long verificationCodeId = Extension.codeService.checkPhoneVerificationCode(applicationId, phone, Constant.VERIFICATION_CODE_TYPE_SET_NEW_PHONE, verificationCode);
			Long verificationCodeId = Extension.codeService.checkPhoneVerificationCode(1L, phone, Constant.VERIFICATION_CODE_TYPE_SET_NEW_PHONE, verificationCode);
            
			String environment = Extension.paramMap.get("environment");
			if(!"develop".equals(environment)){
				logger.info("checkPhoneVerificationCode返回：" + verificationCodeId);
				if(verificationCodeId > 0){
					logger.info("setPhoneVerificationCode参数：verificationCodeId=" + verificationCodeId);
					Extension.codeService.setPhoneVerificationCode(verificationCodeId);
				}else{
					throw new BizException(ErrorCode.VERIFICATIONCODE_ERROR, "验证码错误。");
				}
			}
			//绑定手机
			Long memberId = (Long)memberEntity.get("id");
			logger.info("bindPhone参数:memberId=" + memberId + ",phone=" + phone);
			Extension.memberService.bindPhone(memberId, phone);	
			
			logger.info("=======================bindPhone end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("phone", phone);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 设置企业会员信息
	 * @param json
	 * @return
	 * @throws BizException
	 */
	public JSONObject setCompanyInfo(JSONObject json) throws BusinessException{
		logger.info("=======================setCompanyInfo start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			JSONObject companyBasicInfo =  (JSONObject)json.get("companyBasicInfo");
			JSONObject companyExtendInfo = (JSONObject)json.get("companyExtendInfo");
			JSONObject bankInfo = null;
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(companyBasicInfo == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数companyBasicInfo为空或null");
			if(companyBasicInfo.get("companyName") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数companyName为空或null");
			if(companyBasicInfo.get("businessLicense") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数businessLicense为空或null");
			if(companyBasicInfo.get("legalName") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数legalName为空或null");
			if(companyBasicInfo.get("identityType") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数identityType为空或null");
			if(companyBasicInfo.get("legalIds") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数legalIds为空或null");
			if(companyBasicInfo.get("legalPhone") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数legalPhone为空或null");
			if(companyBasicInfo.get("accountNo") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数accountNo为空或null");
			if(companyBasicInfo.get("parentBankName") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数parentBankName为空或null");
			if(companyBasicInfo.get("bankCityNo") == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankCityNo为空或null");
//			if(companyBasicInfo.get("bankName") == null)
//				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankName为空或null");
			
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "setCompanyInfo", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//检查是否是企业用户
			Long memberType = (Long)memberEntity.get("member_type");
			if(!Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType)){
				throw new BizException(ErrorCode.USER_TYPE_ERROR, "此用户不是企业用户。");
			}
			
			//用户已经认证，则不允许操作
			if(Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_ACTIVITE, "当前企业用户已审核通过，无法设置企业信息。");
			}
			
			//把企业基本信息的参数名改为数据库的参数名
			Map<String, Object> companyBasicInfoDb = companyInfoSoaToDb(bizId, companyBasicInfo.getMap());
			
			Long memberId = (Long)memberEntity.get("id");
			Map<String, Object> companyExtendInfoMap = companyExtendInfo == null ? null : companyExtendInfo.getMap();
			Map<String, Object> bankInfoMap = bankInfo == null ? null : bankInfo.getMap();
			logger.info("setUserAuthInfo参数：memberId=" + memberId + ",bankInfoMap=" + bankInfoMap + ",companyBasicInfo=" + companyBasicInfoDb + ",companyExtendInfoMap=" + companyExtendInfoMap);
			Extension.memberService.setUserAuthInfo(memberId, companyBasicInfoDb, bankInfoMap, companyExtendInfoMap);
			
			logger.info("=======================setCompanyInfo end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 设置个人会员信息
	 * @param json
	 * 			userId   云账户用户唯一标识
	 * 			userInfo 个人会员信息
	 * @return
	 * @throws BizException
	 */
	public JSONObject setMemberInfo(JSONObject json) throws BusinessException{
		logger.info("=======================setMemberInfo start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			JSONObject userInfoSoa = (JSONObject)json.get("userInfo");
			
			bizUserId = bizUserId.trim();
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(userInfoSoa == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数userInfo为null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "setMemberInfo", version);

			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}

			//检查是否是个人用户
			Long memberType = (Long)memberEntity.get("member_type");
			if(!Constant.MEMBER_TYPE_PERSON.equals(memberType)){
				throw new BizException(ErrorCode.USER_TYPE_ERROR, "此用户不是个人用户。");
			}
			
			//个人信息SOA形式转换为db形式
			Map<String, Object> userInfoDb = memberInfoSoaToDb(userInfoSoa.getMap());
			
			//设置个人会员信息
			Long memberId = (Long)memberEntity.get("id");
			logger.info("setUserInfo参数：memberId=" + memberId + ",userInfo=" + userInfoDb);
			Extension.memberService.setUserInfo(memberId, userInfoDb, null);
			
			logger.info("=======================setMemberInfo end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 获取会员信息（个人和企业）
	 * @param json
	 * 			userId 云账户用户唯一标识
	 * @return
	 * @throws BizException
	 */
	public JSONObject getMemberInfo(JSONObject json) throws BusinessException{
		logger.info("=======================getMemberInfo start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "getMemberInfo", version);
			
			bizUserId = bizUserId.trim();

			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			
			
			//获取会员基本信息
			Long memberId = (Long)memberEntity.get("id");
			Map<String, Object> userInfo = null;
			Long memberType = (Long)memberEntity.get("member_type");
			//个人会员基本信息
			if(Constant.MEMBER_TYPE_PERSON.equals(memberType)){
				//用户不可用，则不允许操作
				if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
					throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
				}
				userInfo = getMemberInfoSoa(bizId, memberEntity);
			}else if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType)){  //企业会员基本信息
				userInfo = getCompanyBasicInfoSoa(bizId, memberId);
			}else{
				throw new BizException(ErrorCode.USER_TYPE_ERROR, "会员类型不正确。");
			}
			
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("memberType", (Long)memberEntity.get("member_type"));
			ret.put("memberInfo", new JSONObject(userInfo));
			
			logger.info("return:" + ret);
			logger.info("=======================getMemberInfo end===========================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 获取卡bin信息
	 * @param json
	 * @return
	 * @throws BizException
	 */
	public JSONObject getBankCardBin(JSONObject json) throws BusinessException{
		logger.info("=======================getBankCardBin start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String cardNo = (String)json.get("cardNo");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(cardNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数cardNo为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "getBankCardBin", version);
			
			String cardNoDe = SoaServiceUtil.rsaDencrypt(bizId, cardNo);
			
			logger.info("getCardBin参数：bankCardNo=" + cardNoDe.substring(0, 6));
			Map<String, Object> cardBinDb = Extension.memberService.getCardBin(cardNoDe.substring(0, 6));
			logger.info("getCardBin返回：" + cardBinDb);
			
			Map<String, Object> cardBinSoa = cardBinInfoDbToSoa(cardBinDb);
			JSONObject ret = new JSONObject();
			ret.put("cardBinInfo", cardBinSoa);
			
			logger.info("return:" + ret);
			logger.info("=======================getBankCardBin end===========================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 绑定银行卡申请
	 * @param json
	 * 			userId          云账户用户唯一标识
	 * 			cardNo          银行卡号
	 * 			phone           银行预留手机
	 * 			validityPeriod  有效期
	 * 			vericationValue cvv2
	 * 			isSafeCard		是否安全卡:
	 * 				isSafeCard：true时卡类型必需为借记卡,并且没有绑定过其他的安全卡。
	 * @return JSONObject
	 * @throws BusinessException
	 */
	public JSONObject applyBindBankCard(JSONObject json) throws BusinessException{
		logger.info("=======================applyBindBankCard start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String cardNo = (String)json.get("cardNo");
			String phone = (String)json.get("phone");
			String name = (String)json.get("name");
			Long cardType = json.isNull("cardType") ? null : json.optLong("cardType");
			String bankCode = (String)json.get("bankCode");
			Long identityType = json.isNull("identityType") ? null : json.optLong("identityType");
			String identityNo = (String)json.get("identityNo");
			String validate = (String)json.get("validate");
			String cvv2 = (String)json.get("cvv2");
			Boolean isSafeCard = json.get("isSafeCard") != null && json.optBoolean("isSafeCard");
			//String isSafeCard = "false";
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(cardNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数cardNo为空或null");
			if(StringUtils.isBlank(phone))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数phone为空或null");
			if(StringUtils.isBlank(name))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数name为空或null");
			if(cardType == null || cardType.equals(0L))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数cardType为null");
			if(StringUtils.isBlank(bankCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankCode为空或null");
			if(identityType == null || identityType.equals(0L))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数identityType为null");
			if(StringUtils.isBlank(identityNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数identityNo为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "applyBindBankCard", version);
			
			bizUserId = bizUserId.trim();
			
			//检查证件类型
			if(identityType != 1){
				throw new BizException(ErrorCode.CERTIFICATES_ERROR, "不支持证件类型");
			}
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");
			
			//检查是否已经实名认证，并且验证实名信息
			Long memberType = (Long)memberEntity.get("member_type");
			if(Constant.MEMBER_TYPE_PERSON.equals(memberType)){
				if(memberEntity.get("isIdentity_checked") == null || !(Boolean)memberEntity.get("isIdentity_checked") ){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "未实名认证。");
				}
			}
			
			//判断是否为企业会员，企业会员不能设置安全卡
			if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && isSafeCard)				
				throw new BizException(ErrorCode.USER_TYPE_ERROR, "企业会员不能设置安全卡");

			
			//获取绑定银行卡	
			List<Map<String,Object>> bankList = Extension.memberService.getBindBankCardList(memberId, Constant.BUSINESS_TYPE_BANK_CARD);
			//企业个人银行卡是否已绑定
			if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType)){
				for(Map<String,Object>temp : bankList){
					if(temp.get("account_prop").equals(Constant.BANK_ACCOUNT_PROP_P))
						throw new BizException(ErrorCode.OTHER_ERROR, "企业用户只能绑定一张个人银行卡");
				}
			}
			
			//如为信用卡，则检查validityPeriod，vericationValue是否有值，并检查isSafeCard是否为true
			if(!Constant.CARD_BIN_CARD_TYPE_JJ.equals(cardType)){
				if(StringUtils.isBlank(validate))
					throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数validate为空或null");
				if(StringUtils.isBlank(cvv2))
					throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数cvv2为空或null");
				
				if(isSafeCard){
					throw new BizException(ErrorCode.SAFE_CARD_IS_DEBIT, "安全卡必需为借记卡");
				}
			}
			//如果设置为安全卡，检查已绑定的卡中是否有安全卡
			if(isSafeCard){
				//查询已绑定的银行卡列表
				for( Map<String, Object> bank : bankList){
					Boolean is_safe_card = (Boolean)bank.get("is_safe_card");
					if( is_safe_card ){
						throw new BizException(ErrorCode.SAFE_CARD_EXSIT, "安全卡已存在");
					}
				}
			}
				
			String deIdentityNo = SoaServiceUtil.rsaDencrypt(bizId, identityNo);
			
			//检查bankcode,获取银行名称
			Map<String, Object> bankCodeInfo = Extension.memberService.getBankCode(bankCode);
			if(bankCodeInfo == null || bankCodeInfo.isEmpty()){
				throw new BizException(ErrorCode.BANK_CODE_ERROR, "银行代码不正确。");
			}
			String bankName = (String)bankCodeInfo.get("bank_label");
			
			//解码银行卡
			String deCardNo = SoaServiceUtil.rsaDencrypt(bizId, cardNo);

			//根据银行卡和bankcode判断银行卡是否正确
			Map<String, Object> cardBinDb = Extension.memberService.getCardBin(deCardNo.substring(0, 6));
			if(cardBinDb == null || cardBinDb.isEmpty())
				throw new BizException(ErrorCode.OTHER_ERROR,"不支持该银行卡");
			if(!bankCode.equals(cardBinDb.get("bank_code")))
				throw new BizException(ErrorCode.OTHER_ERROR,"银行代码和银行卡不一致");
			//检查银行卡是否已经绑定
			logger.info("getBindBankCard参数:memberId=" + memberId + ",deCardNo=" + deCardNo);
			Map<String, Object> bindBankMap = Extension.memberService.getBindBankCard(memberId, deCardNo);
			logger.info("getBindBankCard返回:" + bindBankMap);
			if(bindBankMap != null && !bindBankMap.isEmpty()){
				throw new BizException(ErrorCode.CARD_BIND, "此银行卡已经绑定");
			}	
			//绑定银行卡申请
			Map<String,Object> extParams = new HashMap<>();
			
			extParams.put("acctValiddate", 	StringUtils.isBlank(validate) ? null : SoaServiceUtil.rsaDencrypt(bizId, validate));//有效期
			extParams.put("cvv2", 			StringUtils.isBlank(cvv2) ? null : SoaServiceUtil.rsaDencrypt(bizId, cvv2));//CVV2
			extParams.put("identityCardNo", deIdentityNo.toUpperCase());
			extParams.put("isSafeCard", isSafeCard);

			//商户		
			logger.info("itsSignApply参数：bankCode=" + bankCode + ",name=" + name + ",cardType=" + cardType + ",deCardNo=" + deCardNo + ",accountTypeId=" + ",phone=" + phone + ",bankName=" + bankName + ",memberId=" + memberId + ",payType=" + 1 + ",extParams=" + extParams);
			//Map<String, String> signMap = Extension.orderService.itsSignApply(bankCode, name, cardType, deCardNo, accountTypeId, accountTypeName, phone, bankName, memberId, 1L, extParams);
			Map<String, String> signMap = Extension.orderService.itsSignApply(bankCode, name, cardType, deCardNo, phone, bankName, memberId, 1L, extParams);
			
			logger.info("itsSignApply返回:" + signMap);
			
			JSONObject ret = new JSONObject();
			if( "000000".equals(signMap.get("RET_CODE")) || "359037".equals(signMap.get("RET_CODE"))){
				ret.put("tranceNum", signMap.get("TRACE_NUM"));
				ret.put("transDate", signMap.get("TRANS_DATE"));
			}else{
				String errorMsg;				
				if( signMap.get("ERROR_MSG") != null && !"".equals(signMap.get("ERROR_MSG"))){
					errorMsg = signMap.get("ERROR_MSG");
				}else{
					errorMsg = signMap.get("RET_MSG");
				}
				throw new BizException(ErrorCode.UNBINDBANKCARD_ERROR, errorMsg);
			}			
			logger.info("return:" + ret);
			logger.info("=======================applyBindBankCard end===========================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 绑定银行卡确认
	 * @param json
	 * 			userId           云账户用户唯一标识
	 * 			tranceNum        绑定银行卡申请返回的流水号
	 * 			transDate	             绑定银行卡申请返回的时间
	 * 			phone            银行预留手机
	 * 			verificationCode 验证码
	 * @return JSONObject
	 * @throws BusinessException
	 */
	public JSONObject bindBankCard(JSONObject json) throws BusinessException{
		logger.info("=======================bindBankCard start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId  = (String)json.get("bizUserId");
			String tranceNum  = (String)json.get("tranceNum");
			String transDate  = (String)json.get("transDate");
			String phone  = (String)json.get("phone");
			String verificationCode  = (String)json.get("verificationCode");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(tranceNum))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数tranceNum为空或null");
			if(StringUtils.isBlank(transDate))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数transDate为空或null");
			if(StringUtils.isBlank(phone))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数phone为空或null");
			if(StringUtils.isBlank(verificationCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数verificationCode为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "bindBankCard", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			
			//绑定银行卡确认
			Long memberId = (Long)memberEntity.get("id");
			logger.info("itsSignACK参数：tranceNum=" + tranceNum + ",transDate=" + transDate + ",memberId=" + memberId + ",verificationCode=" + verificationCode + ",payType=" + 1);
			
			Extension.orderService.itsSignACK(tranceNum, transDate, memberId, verificationCode, 1L);
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("tranceNum", tranceNum);
			ret.put("transDate", transDate);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	/**
	 * 三要素绑定银行卡确认
	 * @param json
	 * 			userId           云账户用户唯一标识
	 * 			tranceNum        绑定银行卡申请返回的流水号
	 * 			transDate	             绑定银行卡申请返回的时间
	 * 			phone            银行预留手机
	 * 			verificationCode 验证码
	 * @return JSONObject
	 * @throws BusinessException
	 */
	public JSONObject threeElementsBindBankCard(JSONObject json) throws BusinessException{
		logger.info("=======================applyBindBankCard start===========================");
		logger.info("get:" + json);

		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  		= request.getParameter("sysid");
			String version 		= request.getParameter("v");
			String bizUserId 	= (String)json.get("bizUserId");
			String cardNo 		= (String)json.get("cardNo");
			String name 		= (String)json.get("name");
			Long cardType 		= json.isNull("cardType") ? null : json.optLong("cardType");
			String bankCode 	= (String)json.get("bankCode");
			Long identityType 	= json.isNull("identityType") ? null : json.optLong("identityType");
			String identityNo 	= (String)json.get("identityNo");
			Boolean isSafeCard 	= json.get("isSafeCard") != null && json.optBoolean("isSafeCard");
			//String isSafeCard = "false";

			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(cardNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数cardNo为空或null");

			if(StringUtils.isBlank(name))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数name为空或null");
			if(cardType == null || cardType.equals(0L))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数cardType为null");
			if(StringUtils.isBlank(bankCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bankCode为空或null");
			if(identityType == null || identityType.equals(0L))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数identityType为null");
			if(StringUtils.isBlank(identityNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数identityNo为空或null");

			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "applyBindBankCard", version);

//			bizUserId = bizUserId.trim();

			//检查证件类型
			if(identityType != 1){
				throw new BizException(ErrorCode.CERTIFICATES_ERROR, "不支持证件类型");
			}

			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);

			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			Long memberId = (Long)memberEntity.get("id");

			//检查是否已经实名认证，并且验证实名信息
			Long memberType = (Long)memberEntity.get("member_type");
			if(Constant.MEMBER_TYPE_PERSON.equals(memberType)){
				if(memberEntity.get("isIdentity_checked") == null || !(Boolean)memberEntity.get("isIdentity_checked") ){
					throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "未实名认证。");
				}
			}

			//判断是否为企业会员，企业会员不能设置安全卡
			if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && isSafeCard)
				throw new BizException(ErrorCode.USER_TYPE_ERROR, "企业会员不能设置安全卡");


			//获取绑定银行卡
			List<Map<String,Object>> bankList = Extension.memberService.getBindBankCardList(memberId, Constant.BUSINESS_TYPE_BANK_CARD);
			//企业个人银行卡是否已绑定
			if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType)){
				for(Map<String,Object>temp : bankList){
					if(temp.get("account_prop").equals(Constant.BANK_ACCOUNT_PROP_P))
						throw new BizException(ErrorCode.OTHER_ERROR, "企业用户只能绑定一张个人银行卡");
				}
			}

			//如为信用卡，则检查validityPeriod，vericationValue是否有值，并检查isSafeCard是否为true
			if(!Constant.CARD_BIN_CARD_TYPE_JJ.equals(cardType)){
				throw new BizException(ErrorCode.BIND_BANK_CARD_ERROR, "三要素绑卡必需为借记卡");
			}
			//如果设置为安全卡，检查已绑定的卡中是否有安全卡
			if(isSafeCard){
				//查询已绑定的银行卡列表
				for( Map<String, Object> bank : bankList){
					Boolean is_safe_card = (Boolean)bank.get("is_safe_card");
					if( is_safe_card ){
						throw new BizException(ErrorCode.SAFE_CARD_EXSIT, "安全卡已存在");
					}
				}
			}

			String deIdentityNo = SoaServiceUtil.rsaDencrypt(bizId, identityNo);

			//检查bankcode,获取银行名称
			Map<String, Object> bankCodeInfo = Extension.memberService.getBankCode(bankCode);
			if(bankCodeInfo == null || bankCodeInfo.isEmpty()){
				throw new BizException(ErrorCode.BANK_CODE_ERROR, "银行代码不正确。");
			}
			String bankName = (String)bankCodeInfo.get("bank_label");

			//解码银行卡
			String deCardNo = SoaServiceUtil.rsaDencrypt(bizId, cardNo);
//			String deIdentityNo = SoaServiceUtil.rsaDencrypt(bizId, identityNo);

			//根据银行卡和bankcode判断银行卡是否正确
			Map<String, Object> cardBinDb = Extension.memberService.getCardBin(deCardNo.substring(0, 6));
			if(cardBinDb == null || cardBinDb.isEmpty())
				throw new BizException(ErrorCode.OTHER_ERROR,"不支持该银行卡");
			if(!bankCode.equals(cardBinDb.get("bank_code")))
				throw new BizException(ErrorCode.OTHER_ERROR,"银行代码和银行卡不一致");
			//检查银行卡是否已经绑定
			logger.info("getBindBankCard参数:memberId=" + memberId + ",deCardNo=" + deCardNo);
			Map<String, Object> bindBankMap = Extension.memberService.getBindBankCard(memberId, deCardNo);
			logger.info("getBindBankCard返回:" + bindBankMap);
			if(bindBankMap != null && !bindBankMap.isEmpty()){
				throw new BizException(ErrorCode.CARD_BIND, "此银行卡已经绑定");
			}
			//绑定银行卡申请
			Map<String,Object> extParams = new HashMap<>();

			extParams.put("identityCardNo", deIdentityNo.toUpperCase());
			extParams.put("isSafeCard", isSafeCard);
			extParams.put("account_prop", 0L);//默认只能绑定私人银行卡

			//bankCode去地区码（取前4位），除了平安银行：04105840
			if (!bankCode.equals("04105840")) {
				bankCode = bankCode.substring(0, 4);
			}
			logger.info("TLTSignApply参数：bankCode=" + bankCode + ",name=" + name + ",cardType=" + cardType + ",deCardNo=" + deCardNo + ",accountTypeId=" + ",bankName=" + bankName + ",memberId=" + memberId + ",payType=" + 1 + ",extParams=" + extParams);
			Map<String, Object> signMap = Extension.memberService.threeElementsBindBankCard(bankCode, name, cardType, deCardNo, bankName, memberId, identityType, deIdentityNo.toUpperCase(), extParams);

			logger.info("itsSignApply返回:" + signMap);

			JSONObject ret = new JSONObject();
			if( "0000".equals(signMap.get("RET_CODE")) ){
				ret.put("bizUserId", bizUserId);
				Map<String, Object> memberBank = SoaServiceUtil.getBindBankCard(bizId, memberId, cardNo);
				ret.put("transDate", sdf.format((Date)memberBank.get("bind_time")));
			}else{
				String errorMsg;
				if( signMap.get("ERR_MSG") != null && !"".equals(signMap.get("ERR_MSG"))){
					errorMsg = signMap.get("ERR_MSG").toString();
				}else{
					errorMsg = signMap.get("ERR_MSG1").toString();
				}
				throw new BizException(ErrorCode.UNBINDBANKCARD_ERROR, errorMsg);
			}
			logger.info("return:" + ret);
			logger.info("=======================applyBindBankCard end===========================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 查询绑定银行卡
	 * @param json
	 * 			userId 云账户用户唯一标识
	 * 			cardNo 绑定的银行卡号，如为null,则查全部
	 * @return	JSONObject
	 * @throws BusinessException
	 */
	public JSONObject queryBankCard(JSONObject json) throws BusinessException{
		logger.info("=======================queryBankCard start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String cardNo = (String)json.get("cardNo");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "queryBankCard", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			
			Long memberId = (Long)memberEntity.get("id");
			JSONArray bindCardInfoArray = new JSONArray();
			//查询一张卡
			if(!StringUtils.isBlank(cardNo)){
				String deCardNo = SoaServiceUtil.rsaDencrypt(bizId, cardNo);
				logger.info("getBindBankCard参数:memberId=" + memberId + ",deCardNo=" + deCardNo);
				Map<String, Object> cardInfoDb = Extension.memberService.getBindBankCard(memberId, deCardNo);
				logger.info("getBindBankCard返回：" + cardInfoDb);
				if(!(cardInfoDb == null || cardInfoDb.isEmpty())){
					Map<String, Object> cardInfoSoa = bindBankCardInfoDbToSoa(bizId, cardInfoDb);
					cardInfoSoa.put("bankCardNo", cardNo);
					bindCardInfoArray.put(new JSONObject(cardInfoSoa));
				}
			}else{ //查询所有卡
				logger.info("getBindBankCardList参数：memberId=" + memberId + ",cardKind=" + Constant.BUSINESS_TYPE_BANK_CARD);
				List<Map<String, Object>> cardInfoList = Extension.memberService.getBindBankCardList(memberId, Constant.BUSINESS_TYPE_BANK_CARD);
				logger.info("getBindBankCardList返回：" + cardInfoList);
				for(Map<String, Object> cardInfoDb : cardInfoList){
					Map<String, Object> cardInfoSoa = bindBankCardInfoDbToSoa(bizId, cardInfoDb);
					bindCardInfoArray.put(new JSONObject(cardInfoSoa));
				}
			}
			
			JSONObject ret = new JSONObject();
			ret.put("bindCardList", bindCardInfoArray);
			
			logger.info("return:" + ret);
			logger.info("=======================queryBankCard end===========================");
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 解绑绑定银行卡
	 * @param json
	 * 			userId 云账户用户唯一标识
	 * 			cardNo 银行卡号，加密
	 * @return
	 * @throws BizException
	 */
	public JSONObject unbindBankCard(JSONObject json)throws BusinessException{
		logger.info("=======================unbindBankCard start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String cardNo = (String)json.get("cardNo");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(cardNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数cardNo为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "unbindBankCard", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
		
			Long memberId = (Long)memberEntity.get("id");
			//检查银行卡是否为安全卡
			String deCardNo = SoaServiceUtil.rsaDencrypt(bizId, cardNo);
	
			Map<String, Object> bindBankMap = Extension.memberService.getBindBankCard(memberId, deCardNo);
			logger.info("getBindBankCard返回:" + bindBankMap);
			if(bindBankMap != null && !bindBankMap.isEmpty()){			
				Boolean is_safe_card = (Boolean)bindBankMap.get("is_safe_card");
				if(Boolean.TRUE.equals(is_safe_card))
					throw new BizException(ErrorCode.SAFE_CARD_NOT_UNBIND, "安全卡不可解绑");
				//企业个人银行卡不允许解绑
				if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberEntity.get("member_type")) && Constant.BANK_ACCOUNT_PROP_P.equals(bindBankMap.get("account_prop")))
					throw new BizException(ErrorCode.OTHER_ERROR, "企业个人银行卡不允许解绑");
			}
			
			//解绑绑定银行卡

			Long cardBindId = SoaServiceUtil.getBindBankCardId(bizId, memberId, cardNo);
			
			logger.info("unbindBankCard参数：memberId=" + memberId + ",cardBindId=" + cardBindId);
			Extension.memberService.unbindBankCard(memberId, cardBindId);
			
			logger.info("=======================unbindBankCard end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("cardNo", cardNo);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 更改绑定手机
	 * @param json
	 * 			userId              云账户的用户编号
	 * 			oldPhone            原绑定手机
	 * 			oldVerificationCode 原绑定手机验证码
	 * 			newPhone            新绑定手机
	 * 			newVerificationCode 新绑定手机验证码
	 * @return
	 * @throws BizException
	 */
	public JSONObject changeBindPhone(JSONObject json)throws BusinessException{
		logger.info("=======================changeBindPhone start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String oldPhone = (String)json.get("oldPhone");
			String oldVerificationCode = (String)json.get("oldVerificationCode");
			String newPhone = (String)json.get("newPhone");
			String newVerificationCode = (String)json.get("newVerificationCode");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(oldPhone))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数oldPhone为空或null");
			if(StringUtils.isBlank(oldVerificationCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数oldVerificationCode为空或null");
			if(StringUtils.isBlank(newPhone))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数newPhone为空或null");
			if(StringUtils.isBlank(newVerificationCode))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数newVerificationCode为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "changeBindPhone", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			
			//检测验证码
//			Long oldVerificationCodeId = Extension.codeService.checkPhoneVerificationCode(applicationId, oldPhone, Constant.VERIFICATION_CODE_TYPE_CHANGE_PHONE, oldVerificationCode);
//			Extension.codeService.setPhoneVerificationCode(oldVerificationCodeId);
//			Long newVerificationCodeId = Extension.codeService.checkPhoneVerificationCode(applicationId, newPhone, Constant.VERIFICATION_CODE_TYPE_SET_NEW_PHONE, newVerificationCode);
//			Extension.codeService.setPhoneVerificationCode(newVerificationCodeId);
			
			//验证原手机验证码是否验证过
			Map<String, Object> oldVerificationCodeInfo = Extension.codeService.getPhoneVerificationCode(1L, oldPhone,  Constant.VERIFICATION_CODE_TYPE_CHANGE_PHONE, oldVerificationCode);
			if(oldVerificationCodeInfo == null || oldVerificationCodeInfo.isEmpty()){
				throw new BizException(ErrorCode.VERIFICATIONCODE_ERROR, "原手机验证码错误。");
			}
			if(oldVerificationCodeInfo.get("isChecked") == null || !(Boolean)oldVerificationCodeInfo.get("isChecked")){
				logger.info("checkPhoneVerificationCode_old参数：applicationId=" + 1 + ",oldPhone=" + oldPhone + ",verificationCodeType=" +  Constant.VERIFICATION_CODE_TYPE_CHANGE_PHONE + ",oldVerificationCode=" + oldVerificationCode);
				Long oldVerificationCodeId = Extension.codeService.checkPhoneVerificationCode(1L, oldPhone, Constant.VERIFICATION_CODE_TYPE_CHANGE_PHONE, oldVerificationCode);
				logger.info("checkPhoneVerificationCode_old返回：" + oldVerificationCodeId);
				Extension.codeService.setPhoneVerificationCode(oldVerificationCodeId);
			}
			
			logger.info("checkPhoneVerificationCode_new参数：applicationId=" + 1 + ",newPhone=" + newPhone + ",verificationCodeType=" +  Constant.VERIFICATION_CODE_TYPE_SET_NEW_PHONE + ",newVerificationCode=" + newVerificationCode);
			Long newVerificationCodeId = Extension.codeService.checkPhoneVerificationCode(1L, newPhone, Constant.VERIFICATION_CODE_TYPE_SET_NEW_PHONE, newVerificationCode);
			logger.info("checkPhoneVerificationCode_new返回：" + newVerificationCodeId);
			Extension.codeService.setPhoneVerificationCode(newVerificationCodeId);
			
			//修改绑定手机
			Long memberId = (Long)memberEntity.get("id");
			logger.info("bindPhone参数：memberId=" + memberId + ",newPhone=" +newPhone);
			Extension.memberService.bindPhone(memberId, newPhone);
			
			logger.info("=======================changeBindPhone end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("oldPhone", oldPhone);
			ret.put("newPhone", newPhone);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 锁定用户
	 * @param json
	 * 			userId              云账户的用户编号
	 * @return
	 * @throws BizException
	 */
	public JSONObject lockMember(JSONObject json) throws BusinessException{
		logger.info("=======================lockMember start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "lockMember", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
			
			//锁定用户
			Long memberId = (Long)memberEntity.get("id");
			Map<String, Object> extendParam = new HashMap<String, Object>();
			logger.info("lockBizMember参数:memberId=" + memberId + ",extendParam=" + extendParam);
			Extension.memberService.lockBizMember(memberId, extendParam);
			
			logger.info("=======================lockMember end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 解锁用户
	 * @param json
	 * 			userId              云账户的用户编号
	 * @return
	 * @throws BizException
	 */
	public JSONObject unlockMember(JSONObject json) throws BusinessException{
		logger.info("=======================unlockMember start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = (String)request.getParameter("sysid");
			String version = (String)request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "unlockMember", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//解锁用户
			Long memberId = (Long)memberEntity.get("id");
			Map<String, Object> extendParam = new HashMap<String, Object>();
			logger.info("unLockBizMember参数:memberId=" + memberId + ",extendParam=" + extendParam);
			Extension.memberService.unLockBizMember(memberId, extendParam);
			
			logger.info("=======================unlockMember end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}

	/**
	 * 个人信息SOA形式转换为db形式
	 * @param memberInfoSoa SOA形式的个人信息
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> memberInfoSoaToDb(Map<String, Object> memberInfoSoa) throws Exception{
		logger.info("==========================memberInfoSoaToDb start==============================");
		logger.info("参数：" + memberInfoSoa);
		
		try{
			
			logger.info("==========================memberInfoSoaToDb end==============================");
			return memberInfoSoa;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 企业信息SOA形式转换成db形式
	 * @param companyInfoSoa SOA形式的企业信息。
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> companyInfoSoaToDb(String sysid, Map<String, Object> companyInfoSoa) throws Exception{
		logger.info("============================companyInfoSoa start==============================");
		logger.info("参数：" + companyInfoSoa);
		
		try{
			Map<String, Object> companyBasicInfoDb = new HashMap<String, Object>();
			if(companyInfoSoa != null && !companyInfoSoa.isEmpty()){
				companyBasicInfoDb.put("name", companyInfoSoa.get("companyName"));
				companyBasicInfoDb.put("address", companyInfoSoa.get("companyAddress"));
				companyBasicInfoDb.put("business_license", companyInfoSoa.get("businessLicense"));
				companyBasicInfoDb.put("organization_code", companyInfoSoa.get("organizationCode"));
				companyBasicInfoDb.put("telephone", companyInfoSoa.get("telephone"));
				companyBasicInfoDb.put("legal_name", companyInfoSoa.get("legalName"));
				companyBasicInfoDb.put("legal_idstype", Long.valueOf(((Integer)companyInfoSoa.get("identityType")).toString()));
				companyBasicInfoDb.put("legal_ids",  SoaServiceUtil.rsaDencrypt(sysid, (String)companyInfoSoa.get("legalIds")));
				companyBasicInfoDb.put("legalPhone", companyInfoSoa.get("legalPhone"));
				companyBasicInfoDb.put("accountNo", SoaServiceUtil.rsaDencrypt(sysid, (String)companyInfoSoa.get("accountNo")));
				companyBasicInfoDb.put("parentBankName", companyInfoSoa.get("parentBankName"));
				companyBasicInfoDb.put("bankCityNo", companyInfoSoa.get("bankCityNo"));
				companyBasicInfoDb.put("bankName", companyInfoSoa.get("bankName"));
			}
			
			logger.info("companyBasicInfoDb:" + companyBasicInfoDb);
			logger.info("============================companyInfoSoa end==============================");
			return companyBasicInfoDb;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 把从数据库读取的个人信息转换为SOA需要的信息
	 * @param memberEntity 数据库读取的个人信息
	 * @return
	 * @throws BizException
	 */
	private Map<String, Object> getMemberInfoSoa(String bizid, Map<String, Object> memberEntity) throws Exception{
		logger.info("======================getMemberInfo start=======================");
		logger.info("参数:memberId=" + memberEntity);
	
		try{
			Map<String, Object> memberEntitySoa = new HashMap<String, Object>();
			if(memberEntity != null && !memberEntity.isEmpty()){
				memberEntitySoa.put("name", (String)memberEntity.get("name"));
				memberEntitySoa.put("address", (String)memberEntity.get("address"));
				memberEntitySoa.put("userState", (Long)memberEntity.get("user_state"));
				memberEntitySoa.put("memberType", (Long)memberEntity.get("member_type"));
				memberEntitySoa.put("userId", (String)memberEntity.get("userId"));
				memberEntitySoa.put("country", (String)memberEntity.get("country"));
				memberEntitySoa.put("province", (String)memberEntity.get("province"));
				memberEntitySoa.put("area", (String)memberEntity.get("area"));
				memberEntitySoa.put("phone", (String)memberEntity.get("phone"));
				memberEntitySoa.put("identityCardNo", memberEntity.get("id_No") == null ? null : SoaServiceUtil.rsaEncrypt(bizid, (String)memberEntity.get("id_No")));
				memberEntitySoa.put("isPhoneChecked", memberEntity.get("isPhone_checked") == null ? false : (Boolean)memberEntity.get("isPhone_checked"));
				memberEntitySoa.put("registerTime", memberEntity.get("register_time") == null ? null : sdf.format((Date)memberEntity.get("register_time")));
				memberEntitySoa.put("registerIp", (String)memberEntity.get("register_ip"));
				memberEntitySoa.put("payFailAmount", memberEntity.get("pay_fail_amount") == null ? 0L : (Long)memberEntity.get("pay_fail_amount"));
				memberEntitySoa.put("isIdentityChecked", memberEntity.get("isIdentity_checked") == null ? false : (Boolean)memberEntity.get("isIdentity_checked"));
				memberEntitySoa.put("realNameTime", memberEntity.get("real_name_time") == null ? null : sdf.format((Date)memberEntity.get("real_name_time")));
				memberEntitySoa.put("remark", (String)memberEntity.get("remark"));
				memberEntitySoa.put("source", (Long)memberEntity.get("source"));
				memberEntitySoa.put("bizUserId", (String)memberEntity.get("biz_user_id"));
			}
			
			logger.info("memberEntitySoa:" + memberEntitySoa);
			logger.info("======================getMemberInfo end=======================");
			return memberEntitySoa;
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 获取企业基本信息（数据库），并转为SOA可以使用的信息
	 * @param memberId 云账户会员id
	 * @return
	 * @throws BizException
	 */
	private Map<String, Object> getCompanyBasicInfoSoa(String sysid, Long memberId) throws BizException{
		logger.info("======================getCompanyInfoSoa start=======================");
		logger.info("参数：memberId=" + memberId);
		
		try{
			logger.info("getUserAuthInfo参数：memberId=" + memberId);
			Map<String, Object> companyInfo = Extension.memberService.getUserAuthInfo(memberId);
			logger.info("getUserAuthInfo返回：" + companyInfo);
			
			Map<String, Object> companyInfoSoa = new HashMap<String, Object>();
			
			companyInfoSoa.put("companyName", companyInfo.get("name"));
			companyInfoSoa.put("companyAddress", companyInfo.get("address"));
			companyInfoSoa.put("businessLicense", companyInfo.get("business_license"));
			companyInfoSoa.put("organizationCode", companyInfo.get("organization_code"));
			companyInfoSoa.put("telephone", companyInfo.get("telephone"));
			companyInfoSoa.put("legalName", companyInfo.get("legal_name"));
			companyInfoSoa.put("identityType", companyInfo.get("legal_idstype"));
			companyInfoSoa.put("legalIds", SoaServiceUtil.rsaEncrypt(sysid, (String)companyInfo.get("legal_ids")));
			companyInfoSoa.put("legalPhone", companyInfo.get("legalPhone"));
			companyInfoSoa.put("accountNo", SoaServiceUtil.rsaEncrypt(sysid, (String)companyInfo.get("accountNo")));
			companyInfoSoa.put("parentBankName", companyInfo.get("parentBankName"));
			companyInfoSoa.put("bankCityNo", companyInfo.get("bankCityNo"));
			companyInfoSoa.put("bankName", companyInfo.get("bankName"));
			companyInfoSoa.put("status", companyInfo.get("check_state"));
			companyInfoSoa.put("checkTime", companyInfo.get("check_time") == null ? null : sdf.format(companyInfo.get("check_time")));
			companyInfoSoa.put("remark", companyInfo.get("remark"));
			companyInfoSoa.put("failReason", companyInfo.get("fail_reason"));

			logger.info("companyInfoSoa:" + companyInfoSoa);
			logger.info("======================getCompanyInfoSoa end=======================");
			return companyInfoSoa;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}

	/**
	 * 绑定银行卡信息，数据库转为SOA的形式
	 * @param bankCardInfoDb 绑定银行卡信息的数据库形式
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	private Map<String, Object> bindBankCardInfoDbToSoa(String bizId, Map<String, Object> bankCardInfoDb) throws Exception{
		logger.info("==================bankCardInfoDbToSoa start=======================");
		logger.info("参数:" + bankCardInfoDb);
		
		try{
			Map<String, Object> bankCardInfoSoa = new HashMap<>();
			if(bankCardInfoDb != null && !bankCardInfoDb.isEmpty()){
				bankCardInfoSoa.put("bankCardNo", bankCardInfoDb.get("bankCardNo")          == null ? "" : SoaServiceUtil.rsaEncrypt(bizId, (String)bankCardInfoDb.get("bankCardNo")));
				//bankCardInfoSoa.put("bankCode", bankCardInfoDb.get("bank_code")             == null ? "" : (String)bankCardInfoDb.get("bank_code"));
				bankCardInfoSoa.put("bankName", bankCardInfoDb.get("bank_name")             == null ? "" : (String)bankCardInfoDb.get("bank_name"));
				bankCardInfoSoa.put("bindTime", bankCardInfoDb.get("bind_time")             == null ? "" : sdf.format((Date)bankCardInfoDb.get("bind_time")));
//				bankCardInfoSoa.put("unbindTime", bankCardInfoDb.get("unbind_time")         == null ? "" : sdf.format((Date)bankCardInfoDb.get("unbind_time")));
				bankCardInfoSoa.put("cardType", bankCardInfoDb.get("card_type")             == null ? null : (Long)bankCardInfoDb.get("card_type"));
				bankCardInfoSoa.put("bindState", bankCardInfoDb.get("bind_state")           == null ? null : (Long)bankCardInfoDb.get("bind_state"));
				bankCardInfoSoa.put("phone", bankCardInfoDb.get("phone")                    == null ? "" : (String)bankCardInfoDb.get("phone"));
				bankCardInfoSoa.put("is_safe_card", bankCardInfoDb.get("is_safe_card")      == null ? false : (Boolean)bankCardInfoDb.get("is_safe_card"));
//				bankCardInfoSoa.put("validate", bankCardInfoDb.get("acct_validdate")        == null ? "" : SoaServiceUtil.rsaEncrypt(bizId, (String)bankCardInfoDb.get("acct_validdate")));
//				bankCardInfoSoa.put("cvv2", bankCardInfoDb.get("cvv2")                      == null ? "" : (String)bankCardInfoDb.get("cvv2"));
			}
			
			logger.info("bankCardInfoSoa:" + bankCardInfoSoa);
			logger.info("==================bankCardInfoDbToSoa end=======================");
			return bankCardInfoSoa;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 卡bin信息数据库转换为SOA
	 * @param cardBinInfoDb
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> cardBinInfoDbToSoa(Map<String, Object> cardBinInfoDb) throws Exception{
		logger.info("==================cardBinInfoDbToSoa start=======================");
		logger.info("参数:" + cardBinInfoDb);
		
		try{
			Map<String, Object> cardBinInfoSoa = new HashMap<String, Object>();
			if(cardBinInfoDb != null && !cardBinInfoDb.isEmpty()){
				cardBinInfoSoa.put("cardBin", (String)cardBinInfoDb.get("card_bin"));
				cardBinInfoSoa.put("cardType", (Long)cardBinInfoDb.get("card_type"));
				cardBinInfoSoa.put("bankCode", (String)cardBinInfoDb.get("bank_code"));
				cardBinInfoSoa.put("bankName", (String)cardBinInfoDb.get("bank_name"));
				cardBinInfoSoa.put("cardName", (String)cardBinInfoDb.get("card_name"));
				cardBinInfoSoa.put("cardLenth", (Long)cardBinInfoDb.get("card_length"));
				cardBinInfoSoa.put("cardState", (Long)cardBinInfoDb.get("card_state"));
				cardBinInfoSoa.put("cardTypeLabel", (String)cardBinInfoDb.get("card_type_label"));
			}
			
			logger.info("==================cardBinInfoDbToSoa end=======================");
			logger.info("ret:" + cardBinInfoSoa);
			return cardBinInfoSoa;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * 设置安全卡
	 * @param json	json
	 * @return js
	 * @throws BusinessException
	 */
	private JSONObject setSafeCard(JSONObject json) throws BusinessException{
		logger.info("=======================setSafeCard start===========================");
		logger.info("get:" + json);
		
		try{
			HttpServletRequest request = (HttpServletRequest)json.get("$request");
			String bizId  = request.getParameter("sysid");
			String version = request.getParameter("v");
			String bizUserId = (String)json.get("bizUserId");
			String cardNo = (String)json.get("cardNo");
			Boolean setSafeCard = json.isNull("setSafeCard") || json.optBoolean("setSafeCard");

			logger.info("setSafeCard:"+setSafeCard);
			if(StringUtils.isBlank(bizId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			if(StringUtils.isBlank(version))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数v为空或null");
			if(StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if(StringUtils.isBlank(cardNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数cardNo为空或null");
			//boolean _setSafeCard = true;
			if( setSafeCard.equals(false)){
				throw new BizException(ErrorCode.SAFE_CARD_NOT_UNBIND, "目前接口不支持解绑安全卡");
			}
			//检查应用使用接口权限
			SoaServiceUtil.judgeSoaAvailable(bizId, "setSafeCard", version);
			
			bizUserId = bizUserId.trim();
			
			//获取会员实体并检查用户是否存在
			Long applicationId = SoaServiceUtil.getApplicationId(bizId);
			Map<String, Object> memberEntity = SoaServiceUtil.getMemberEntity(applicationId, bizUserId);
			
			//用户不可用，则不允许操作
			if(!Constant.USER_STATE_ACTIVATION.equals(memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.USER_IS_NOT_USEFUL, "当前用户不可用。");
			}
		    
			Long memberType = (Long)memberEntity.get("member_type");
			//判断是否为企业会员，企业会员不能设置安全卡
			if(Constant.MEMBER_TYPE_ENTERPRISE.equals(memberType) && setSafeCard)				
				throw new BizException(ErrorCode.USER_TYPE_ERROR, "企业会员不能设置安全卡");
			
			Long memberId = (Long)memberEntity.get("id");
			//检查银行卡是否为安全卡
			List<Map<String,Object>> bankList = Extension.memberService.getBindBankCardList(memberId, 1L);
			//查询已绑定的银行卡列表
			for( Map<String, Object> bank : bankList){
				Boolean is_safe_card = bank.get("is_safe_card") == null ? false:(Boolean)bank.get("is_safe_card");
				if( is_safe_card ){
					throw new BizException(ErrorCode.SAFE_CARD_EXSIT, "安全卡已存在");
				}
			}


			//设置安全卡
			Map<String, Object> bankEntity = SoaServiceUtil.getBindBankCard(bizId, memberId, cardNo);
			Long cardBindId = (Long)bankEntity.get("id");

			Long card_type = (Long)bankEntity.get("card_type");

			if(!Constant.CARD_BIN_CARD_TYPE_JJ.equals(card_type)){
				throw new BizException(ErrorCode.SAFE_CARD_IS_DEBIT, "安全卡必需为借记卡");
			}

			logger.info("unbindBankCard参数：memberId=" + memberId + ",cardBindId=" + cardBindId);
			Extension.memberService.setSafeCard(memberId, cardBindId,setSafeCard);
			
			logger.info("=======================setSafeCard end===========================");
			JSONObject ret = new JSONObject();
			ret.put("bizUserId", bizUserId);
			ret.put("cardNo", cardNo);
			return ret;
		}catch(BizException bizE){
			String message = bizE.getMessage() == null ? "其他错误" :bizE.getMessage();
			logger.error(message, bizE);
			throw new BusinessException(bizE.getErrorCode(), message);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
}
