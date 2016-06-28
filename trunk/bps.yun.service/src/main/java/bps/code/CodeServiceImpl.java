package bps.code;

import apf.work.QueryWork;
import ime.core.Environment;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;







import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.ErrorCode;
import bps.common.Util;
import bps.common.BizException;
import bps.service.CodeService;

public class CodeServiceImpl implements CodeService {
	
	public static Logger logger = Logger.getLogger(CodeServiceImpl.class.getName());

	/**
	 * 添加短信模板
	 * @param applicationId  应用ID -必填
	 * @param verificationCodeType  验证类型内容为101到109  -必填
	 * @param template  模板内容 (String 500) -必填
	 * @return
	 * @throws BizException
     */
	@SuppressWarnings("unchecked")
	public  Map<String,Object> addSMSTemplate(Long applicationId,Long verificationCodeType,String template)throws BizException{
		
		logger.info("getSMSTemplate start");
        logger.info("memberId:"+applicationId+",verificationCodeType:"+verificationCodeType);

		try {
        	if(applicationId == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数applicationId不能为空");
			if(verificationCodeType == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数verificationCodeType不能为空");
			if(template == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数template不能为空");
			if(!(verificationCodeType<110&&verificationCodeType>100))
				throw new BizException(ErrorCode.VERIFACTIONCODETYPE_ERROR,"短信模本类型错误");

        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("applicationId", applicationId);
            param.put("verificationCodeType", verificationCodeType);
            param.put("template", template);
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            Map<String, Object> sms_entity= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
					Map<String,Object> app_entity = DynamicEntityService.getEntity((Long)param.get("applicationId"), "AMS_Organization");
					if(app_entity == null){
						throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
					}
    				Query query = session.createQuery("from AMS_SMTemplet where application_id=:application_id and verification_code_type=:verification_code_type");
    				query.setParameter("application_id", (Long)param.get("applicationId"));
    				query.setParameter("verification_code_type", (Long)param.get("verificationCodeType"));
					List<Map<String,Object>> list = query.list();
					if(list.size()>0){
						 Map<String,Object> entity = list.get(0);
						 query = session.createQuery("update AMS_SMTemplet set contents=:contents where id=:id");
						 query.setParameter("contents", (String)param.get("template"));
		    			 query.setParameter("id", (Long)entity.get("id"));
		    			 query.executeUpdate();
		    			 entity.put("contents", (String)param.get("template"));
		    			 return entity;
						 
					}else{
						Map<String,String> entityParam = new HashMap<String,String>();
						entityParam.put("verification_code_type", String.valueOf(param.get("verificationCodeType")));
						entityParam.put("contents", (String)param.get("template"));
//						entityParam.put("codeNo", value);
						entityParam.put("application_id", String.valueOf(app_entity.get("id")));
						entityParam.put("application_label", (String)app_entity.get("name"));
						return DynamicEntityService.createEntity("AMS_SMTemplet", entityParam, null);
					
					}
    			}
    		});
            logger.info("bank_list:"+sms_entity);
    		return sms_entity;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
		
		
		
		
	}
	
	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: getSMSTemplate</p> 
	* <p>Description: 获取短信模板实现 </p> 
	* @param applicationId  应用ID
	* @param verificationCodeType  短信模板类型ID
	* @return
	* @throws BizException 
	* @see bps.service.CodeService#getSMSTemplate(java.lang.Long, java.lang.Long)
	 */
	public Map<String,Object> getSMSTemplate(Long applicationId,Long verificationCodeType)throws BizException{
		logger.info("getSMSTemplate start");
        logger.info("memberId:"+applicationId+",verificationCodeType:"+verificationCodeType);

		try {
			if(applicationId == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数applicationId不能为空");
			if(verificationCodeType == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数verificationCodeType不能为空");

        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("applicationId", applicationId);
            param.put("verificationCodeType", verificationCodeType);
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            Map<String, Object> sms_entity= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
    				Query query = session.createQuery("from AMS_SMTemplet where application_id=:application_id and verification_code_type=:verification_code_type");
    				query.setParameter("application_id", (Long)param.get("applicationId"));
    				query.setParameter("verification_code_type", (Long)param.get("verificationCodeType"));
					List<Map<String,Object>> list = query.list();
					if(list.size()>0){
						return list.get(0);
					}
    				return null;
    			}
    		});
            logger.info("bank_list:"+sms_entity);
    		return sms_entity;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
		
		
	}
	
	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: generatePhoneVerificationCode</p> 
	* <p>Description: 自动生成短信内容，模板已经匹配生成</p> 
	* @param applicationId 应用ID
	* @param phone 手机
	* @param verificationCodeType 模板类型Id
	* @param extParams
	* @return
	* @throws BizException 
	* @see bps.service.CodeService#generatePhoneVerificationCode(java.lang.Long, java.lang.String, java.lang.Long, java.util.Map)
	 */
	public String generatePhoneVerificationCode(Long applicationId,String phone,
			Long verificationCodeType, Map<String, Object> extParams)
			throws BizException {
		logger.info("generatePhoneVerificationCode start");
		try {
			final Map<String, Object> param=new HashMap<String, Object>();
			if(extParams!= null && !extParams.isEmpty()){
				param.putAll(extParams);
			}
			param.put("applicationId", applicationId);
			param.put("phone", phone);
	        param.put("verification_code_type", verificationCodeType);
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			if(applicationId == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数applicationId不能为空");
			if(phone == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phone不能为空");
			if(!Util.isNumeric(phone))
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phone"+ phone + "不正确");
			if(phone.length() != 11)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phone" + phone + "不正确");
			if(verificationCodeType == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数verificationCodeType不能为空");

			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			return EntityManagerUtil.execute(new TransactionWork<String>() {
	    			@Override
   			public String doTransaction(Session session, Transaction tx)
   					throws Exception {
					String html = VerificationCode.generatePhoneVerificationCode(param, false, session);
					return html;
				}
			}); 
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR,e.getMessage());
		}

	}
	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: checkPhoneVerificationCode</p> 
	* <p>Description: 验证验证发是否正确，有返回值的表示成功</p> 
	* @param applicationId 应用ID
	* @param phone 电话号码
	* @param verificationCodeType 短信类型
	* @param phoneCode  验证码
	* @return
	* @throws BizException 
	* @see bps.service.CodeService#checkPhoneVerificationCode(java.lang.Long, java.lang.String, java.lang.Long, java.lang.String)
	 */
	public Long checkPhoneVerificationCode(Long applicationId,String phone,
			 Long verificationCodeType,   String phoneCode) throws BizException {
		logger.info("CodeService.checkPhoneVerificationCode start");
		Long ret = null;
		try {		
			String environment = Environment.instance().getSystemProperty("environment");
			if(!"develop".equals(environment)){
				final String _phone=phone;
				final Long _verificationCodeType=verificationCodeType;
				final String _phoneCode=phoneCode;
				final Long _applicationId = applicationId;
				if(_phone == null)
					throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phone不能为空");
				if(!Util.isNumeric(_phone))
					throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phone"+ phone + "不正确");
				if(phone.length() != 11)
					throw new BizException(ErrorCode.OTHER_ERROR,"内部参数" + phone + "不正确");
				if(_phoneCode == null || _phoneCode.equals(""))
					throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phoneCode不能为空");
				if(_verificationCodeType == null || _verificationCodeType == 0L)
					throw new BizException(ErrorCode.OTHER_ERROR,"内部参数verificationCodeType不能为空");
				if(_applicationId == null || _applicationId == 0L)
					throw new BizException(ErrorCode.OTHER_ERROR,"内部参数applicationId不能为空");
//				ret= EntityManagerUtil.execute(new TransactionWork<Long>() {
//						@Override
//						public Long doTransaction(Session session, Transaction tx)
//								throws Exception {
//							Map<String, Object> mapV=VerificationCode.checkPhoneVerificationCode(_applicationId,_phone, _verificationCodeType, _phoneCode, session);
//							logger.info("###"+mapV+"###");
//							if(mapV != null && mapV.size() != 0){
//
//								return (Long)mapV.get("id");
//							} else {
//								throw new BizException(ErrorCode.VERIFICATIONCODE_ERROR,"验证码错误");
//							}
//						}
//				});

				ret= EntityManagerUtil.execute(new QueryWork<Long>() {


					@Override
					public Long doQuery(Session session) throws Exception {
						Map<String, Object> mapV=VerificationCode.checkPhoneVerificationCode(_applicationId,_phone, _verificationCodeType, _phoneCode, session);
						logger.info("###"+mapV+"###");
						if(mapV != null && mapV.size() != 0){

							return (Long)mapV.get("id");
						} else {
							throw new BizException(ErrorCode.VERIFICATIONCODE_ERROR,"验证码错误");
						}
					}
				});
			}
			return ret;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR,e.getMessage());
		}
	}
	
	
	public  Map<String, Object> getPhoneVerificationCode(Long applicationId,String phone,
			 Long verificationCodeType,   String phoneCode) throws BizException{
		logger.info("CodeService.checkPhoneVerificationCode start");
		Map<String,Object> ret = null;
		try {	
			final String _phone=phone;
			final Long _verificationCodeType=verificationCodeType;
			final String _phoneCode=phoneCode;
			final Long _applicationId = applicationId;
			if(_phone == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phone不能为空");
			if(!Util.isNumeric(_phone))
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phone"+ phone + "不正确");
			if(phone.length() != 11)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phone"+ phone + "不正确");
			if(_phoneCode == null || _phoneCode.equals(""))
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数phoneCode不能为空");
			if(_verificationCodeType == null || _verificationCodeType == 0L)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数verificationCodeType不能为空");
			if(_applicationId == null || _applicationId == 0L)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数applicationId不能为空");
//			ret= EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
//					@Override
//					public Map<String,Object> doTransaction(Session session, Transaction tx)
//							throws Exception {
//							Map<String, Object> mapV=VerificationCode.getPhoneVerificationCode(_applicationId,_phone, _verificationCodeType, _phoneCode, session);
//							logger.info("###"+mapV+"###");
//							return mapV;
//					}
//			});

			ret= EntityManagerUtil.execute(new QueryWork<Map<String,Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					Map<String, Object> mapV=VerificationCode.getPhoneVerificationCode(_applicationId,_phone, _verificationCodeType, _phoneCode, session);
					logger.info("###"+mapV+"###");
					return mapV;
				}
			});
			return ret;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: setPhoneVerificationCode</p> 
	* <p>Description:验证码设置已经使用 </p> 
	* @param verificationCodeId 验证码类型
	* @throws BizException 
	* @see bps.service.CodeService#setPhoneVerificationCode(java.lang.Long)
	 */
	public void setPhoneVerificationCode(Long verificationCodeId)
			throws BizException {
		logger.info("setPhoneVerificationCode start");
		try {
			String environment = Environment.instance().getSystemProperty("environment");
			if(!"develop".equals(environment)){
				final Long _verificationCodeId=verificationCodeId;
				if(_verificationCodeId == null || _verificationCodeId == 0L) {
					throw new BizException(ErrorCode.OTHER_ERROR,"内部参数verificationCodeType不能为空");
				}
				EntityManagerUtil.execute(new TransactionWork<Object>() {
		    			@Override
	    			public Object doTransaction(Session session, Transaction tx)
	    					throws Exception {
						VerificationCode.setVerificationCodeChecked(_verificationCodeId, session);
						return null;
					}
				}); 
			}
		} catch (BizException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: generateEmailVerificationCode</p> 
	* <p>Description: 生成发送邮件的内容</p> 
	* @param applicationId 
	* @param email
	* @param verificationCodeType
	* @param memberId
	* @param extParams
	* @return
	* @throws BizException 
	* @see bps.service.CodeService#generateEmailVerificationCode(java.lang.Long, java.lang.String, java.lang.Long, java.lang.Long, java.util.Map)
	 */
	public Map<String, Object> generateEmailVerificationCode(Long applicationId,String email,
			Long verificationCodeType, Long memberId,
			Map<String, Object> extParams) throws BizException {
		logger.info("generateEmailVerificationCode start");
		final Map<String, Object> param=new HashMap<String, Object>();
		if(extParams!= null && !extParams.isEmpty()){
			param.putAll(extParams);
		}
		param.put("email", email);
		param.put("verification_code_type", verificationCodeType);
		param.put("member_id", memberId);
		param.put("applicationId", applicationId);
		try {
			if(email == null)
                throw new BizException(ErrorCode.PARAM_ERROR,"请传入邮箱");
            if(verificationCodeType == null)
                throw new BizException(ErrorCode.PARAM_ERROR,"请传入验证码类型 ");
            if(memberId == null)
                throw new BizException(ErrorCode.PARAM_ERROR,"请传入member_id ");
            if(applicationId == null)
                throw new BizException(ErrorCode.PARAM_ERROR,"请传入applicationId ");
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			return EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
	    		@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
                    Map<String, Object> result = VerificationCode.generateEmailVerificationCode(param, false, session);
	                return result;
				}
			}); 
 
		} catch (BizException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: checkEmailVerificationCode 验证邮件的验证吗是否正确</p> 
	* <p>Description: </p> 
	* @param applicationId
	* @param email
	* @param verificationCodeType
	* @param verificationCode
	* @return
	* @throws BizException 
	* @see bps.service.CodeService#checkEmailVerificationCode(java.lang.Long, java.lang.String, java.lang.Long, java.lang.String)
	 */
	public Long checkEmailVerificationCode(Long applicationId,String email,
			Long verificationCodeType, String verificationCode)
			throws BizException {
		logger.info("CodeService.checkEmailVerificationCode start");
		final String _email=email;
		final Long _verificationCodeType=verificationCodeType;
		final String _verificationCode=verificationCode;
		final Long _applicationId = applicationId;
//		final Map<String, Object> param=new HashMap<String, Object>();
//		//final Map<String, Object> mapV =new HashMap<String, Object>();
//		param.put("email", email);
//		param.put("verification_code_type", verificationCodeType);
//		param.put("verification_code", verificationCode);
		
		try {	
			if(_email == null)
	            throw new BizException(ErrorCode.PARAM_ERROR,"请传入邮箱");
	        if(_verificationCode == null || _verificationCode.equals(""))
	            throw new BizException(ErrorCode.PARAM_ERROR,"请传入验证码");
	        if(_verificationCodeType == null || _verificationCodeType == 0L)
	            throw new BizException(ErrorCode.PARAM_ERROR,"请传入验证码类型");
	        if(_applicationId == null || _applicationId == 0L)
	            throw new BizException(ErrorCode.PARAM_ERROR,"请传入应用");
	        
	
	        Long codeId = EntityManagerUtil.execute(new TransactionWork<Long>() {
				@Override
				public Long doTransaction(Session session, Transaction tx)
						throws Exception {
					Map<String, Object> mapV = VerificationCode.checkEmailVerificationCode(_applicationId,_email, _verificationCodeType, _verificationCode, session);
		            if(mapV != null && mapV.size() != 0){
		                return (Long)mapV.get("id");
		            } else {
		            	throw new BizException(ErrorCode.VERIFICATIONCODE_ERROR,"验证码错误");
		            }
				}
			});
	        return codeId;
		} catch (BizException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: setEmailVerificationCode</p> 
	* <p>Description: 设置邮件验证码已经使用</p> 
	* @param verificationCodeId
	* @throws BizException 
	* @see bps.service.CodeService#setEmailVerificationCode(java.lang.Long)
	 */
	public void setEmailVerificationCode(Long verificationCodeId)
			throws BizException {
		logger.info("setEmailVerificationCode start");
		try {
			final Long _verificationCodeId=verificationCodeId;
			if(_verificationCodeId == null || _verificationCodeId == 0L) {
				throw new BizException(ErrorCode.PARAM_ERROR,"请传入验证码id");
			}
			EntityManagerUtil.execute(new TransactionWork<Object>() {
	    			@Override
    			public Object doTransaction(Session session, Transaction tx)
    					throws Exception {
	    			VerificationCode.setVerificationCodeChecked(_verificationCodeId, session);
	    			return null;
					
				}
			}); 
		} catch (BizException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	
	



}
