package bps.member;

import bps.process.SafetyLevel;
import ime.core.Environment;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;
import ime.security.Password;
import ime.security.util.TripleDES;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import org.hibernate.Query; 
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction; 
import org.hibernate.transform.Transformers;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.common.MD5Util;
import bps.common.Util;
import bps.process.Extension;
import bps.process.RiskUser;
import bps.service.MemberService;

public class MemberServiceImpl implements MemberService{
	
	public static Logger logger = Logger.getLogger("member");
	
	/**
	 * 通过邮箱注册(门户)
	 * @param source	           来源 1PC 2 手机
	 * @param memberType	会员类型,目前支持2开发者
	 * @param email			邮箱
	 * @param loginPassword	登陆密码
	 * @param registerIp		注册IP
	 * @param extParams			保留参数
	 * @return Map<String, Object>	会员实体对象
	 * @throws BizException
	 */
	public Map<String, Object> registerCompanyUserByEmail(String source,
			Long memberType, String email, String loginPassword,
			String registerIp, Map<String, Object> extParams)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("registerCompanyUserByEmail start");
        logger.info("source:"+source+";memberType:"+memberType+";email:"+email+";loginPassword:"+loginPassword+";registerIp:"+registerIp+";extParams:"+extParams);
        try {
        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("source", source);
            param.put("memberType", memberType);
            param.put("email", email);
            param.put("loginPassword",loginPassword );
            param.put("register_ip",registerIp );
            if(memberType != 2L){
            	throw new BizException(ErrorCode.PARAM_ERROR,"会员类型不识别，请传入正确值");
            }
            final Member meb=new Member();
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            Map<String, Object> member_entity= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
    			@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
    				Map<String, Object> member_entity = meb.registerCompanyUserByEmail(param, session); 
    				return member_entity;
    			}
    		});
            logger.info("member_entity:"+member_entity);
    		return member_entity;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: activateCompanyUser</p> 
	* <p>Description: 激活企业用户</p> 
	* @param memberId
	* @throws BizException 
	* @see bps.service.MemberService#activateCompanyUser(java.lang.Long)
	 */
	public void activateCompanyUser(Long memberId) throws BizException {
		// TODO Auto-generated method stub
		logger.info("activateCompanyUser start");
    	logger.info("memberId:"+memberId);
    	if(memberId == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
    	final Map<String, Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("member_id", memberId);
    	try {
    		Member member = new Member(memberId);
		if((member.getUser_state()).equals(Constant.USER_STATE_ACTIVATION)) {
			 throw new BizException(ErrorCode.USER_IS_ACTIVITE,"该用户已激活");
		}
		EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
			@Override
			public Map<String,Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				Query query = session.createQuery("update AMS_Member set  user_state=:user_state where id=:member_id");
	            query.setParameter("user_state", Constant.USER_STATE_ACTIVATION);
	            query.setParameter("member_id", (Long)paramMap.get("member_id"));
	            query.executeUpdate();
	            logger.info("---------ok------");
				return null;
			}
		});
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			   logger.info(e.getMessage(), e);
			   throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: loginByEmail</p> 
	* <p>Description:登录功能，门户网站中有邮箱登录 </p> 
	* @param source
	* @param email
	* @param password
	* @param loginIp
	* @param extParams
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#loginByEmail(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	public Map<String, Object> loginByEmail(String source, String email,
			String password, String loginIp, Map<String, Object> extParams)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("loginByEmail start");
		logger.info("source:"+source+";email:"+email+";password:"+password+";loginIp:"+loginIp+";extParams:"+extParams);
		try {
			final String  _email      = email;
			final String _password    = password;
			if(!RiskUser.checkRiskUserInfo("email", email)) {
	            throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING,"账号异常已锁定，如有任何疑问请联系客服。");
	        }
			String[] ip_list = loginIp.split(",");
		    if(!RiskUser.checkRiskUserInfo("IP", ip_list[0])) {
		        throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING,"账号异常已锁定，如有任何疑问请联系客服。");
		    }
		    Map<String, Object> resMap=
		    EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
		        		@Override
		        		public Map<String, Object> doTransaction(Session session, Transaction tx)
								throws Exception {
		        			Member member = new Member();
		        			member.setEmail(_email);
	        				if(member.getId() != null) {
		        				Map<String, Object> returnMap =  Security.checkLoginPassword(member,_password, session);
	        					returnMap.put("member_entity", member.getUserInfo());
		        				if(((Long)returnMap.get("status")).equals(Constant.CHECK_PWD_OK))	//更新登录时间
		        					member.setLastlogintime(member.getId(), session);
		        				return returnMap;
	        				} else {
	        					throw new BizException(ErrorCode.USER_NOTEXSIT,"该会员不存在");
	        				}
		        		}
		        	});
		        logger.info("resMap:"+resMap);
		        return resMap;
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: setUserInfo</p> 
	* <p>Description:设置用户信息，包括个人的和企业的，企业的通过人工审核 </p> 
	* @param memberId
	* @param params
	* @param entityParam
	* @throws BizException 
	* @see bps.service.MemberService#setUserInfo(java.lang.Long, java.util.Map, java.util.Map)
	 */
	public void setUserInfo(Long memberId, Map<String, Object> params,Map<String,Object> extParam)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("setUserInfo start");
    	logger.info("memberId:"+memberId+",params:"+params);
    	if(memberId == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
    	final Map<String, Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("member_id", memberId);
    	paramMap.putAll(params);
//    	final Map<String,Object> _entityParam = entityParam;
    	try {
    		final Member member = new Member(memberId);
	        String userId = member.getUserId();
	        if(userId == null && !"".equals(userId)){
	        	throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
	        }
	        EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
			@Override
			public Map<String,Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				//更新会员信息
				StringBuffer sb = new StringBuffer();
				sb.setLength(0);
				sb.append("update AMS_Member set  name=:name");
//				if(Util.isParam(paramMap, "user_name")){
//					sb.append(",user_name=:user_name");
//				}
				if(Util.isParam(paramMap, "address")){
					sb.append(",address=:address");
				}
				if(Util.isParam(paramMap, "country")){
					sb.append(",country=:country");
				}
				if(Util.isParam(paramMap, "province")){
					sb.append(",province=:province");
				}
				if(Util.isParam(paramMap, "area")){
					sb.append(",area=:area");
				}
				sb.append(" where id=:member_id");
				Query query = session.createQuery(sb.toString());
				Map<String,Object> member_entity = member.getUserInfo();
				if(member.getIsIdentity_checked()){
					query.setParameter("name",member.getName());
				}else{
					query.setParameter("name",(String)paramMap.get("name"));
				}
//	            if(Util.isParam(paramMap, "user_name")){
//	            	query.setParameter("user_name", (String)paramMap.get("user_name"));
//	            }
	            if(Util.isParam(paramMap, "address")){
	            	query.setParameter("address", (String)paramMap.get("address"));
				}
				if(Util.isParam(paramMap, "country")){
					query.setParameter("country", (String)paramMap.get("country"));
				}
				if(Util.isParam(paramMap, "province")){
					query.setParameter("province", paramMap.get("province"));
				}
				if(Util.isParam(paramMap, "area")){
					query.setParameter("area", (String)paramMap.get("area"));
				}
	            query.setParameter("member_id", (Long)member_entity.get("id"));
	            query.executeUpdate();
	            logger.info("---------ok------");
				return null;
			}
		});
    	}catch (BizException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
		
		
	}

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: setUserExtendInfo</p> 
	* <p>Description: 设置用户的扩展信息，法人省份证等用×××代替</p> 
	* @param memberId
	* @param params
	* @throws BizException 
	* @see bps.service.MemberService#setUserExtendInfo(java.lang.Long, java.util.Map)
	 */
//	public void setUserExtendInfo(Long memberId, Map<String, Object> params)
//			throws BizException {
//		// TODO Auto-generated method stub
//		logger.info("MemberServiceImpl.setExtendInfo start");
//		logger.info("memberId:"+memberId+";params:"+params);
//		try {
//		////
//            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
//            String cardId = (String)params.get("法人身份证号码");
//            StringBuilder sb = new StringBuilder();
//            sb.setLength(0);
//            sb.append(cardId.substring(0,1));
//            for(int i = 0, j = cardId.length() - 2; i < j; i ++) {
//                sb.append("*");
//            }
//            sb.append(cardId.substring(cardId.length() - 1));
//            params.put("法人身份证号码", sb.toString());
//            
//            
//            String person_bank = (String)params.get("个人账户");
//            String company_bank = (String)params.get("公司对公账户");
//            if(!person_bank.equals("")){
//                sb.setLength(0);
//                for(int i = 0, j = person_bank.length() - 4; i < j; i ++) {
//                    sb.append("*");
//                }
//                sb.append(person_bank.substring(person_bank.length() - 4));
//                params.put("个人账户", sb.toString());
//            }
//            
//            if(!company_bank.equals("")){
//                sb.setLength(0);
//                for(int i = 0, j = company_bank.length() - 4; i < j; i ++) {
//                    sb.append("*");
//                }
//                sb.append(company_bank.substring(company_bank.length() - 4));
//                params.put("公司对公账户", sb.toString());
//            }
//            
//            final Long _memberId=memberId;
//            final Map<String, Object> _params=params;
//            EntityManagerUtil.execute(new TransactionWork<Object>() {
//    			@Override
//    			public Object doTransaction(Session session, Transaction tx)
//    					throws Exception {
//                    Query query = session.createQuery("delete AMS_MemberExtendInfo mb_ex where mb_ex.member_id=:member_id");
//                    query.setParameter("member_id", _memberId);
//                    query.executeUpdate();
//                    new Member().setExtendInfo(_memberId, _params, session);
//    				return null;
//    			}
//            });
//			
//		////	
//		} catch (BizException e) {
//			logger.error(e.getMessage(), e);
//			throw e;
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
//		}
//		
//	}
	/***
	 * 设置企业信息
	 */
	@SuppressWarnings({"unchecked","unused"})
	public void setUserAuthInfo(Long memberId, Map<String, Object> params,
			Map<String, Object> bankParam,Map<String,Object> extendParam) throws BizException {
		// TODO Auto-generated method stub
		logger.info("setUserAuthInfo start");
    	logger.info("memberId:"+memberId+",params:"+params);
    	if(memberId == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
    	final Map<String, Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("member_id", memberId);
    	paramMap.putAll(params);
//    	final Map<String,Object> _bankParam = bankParam;
    	try {
    		final Member member = new Member(memberId);
	        String userId = member.getUserId();
	        if(userId == null && !"".equals(userId)){
	        	throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
	        }
	        final Map<String,Object> _member_entity = member.getUserInfo();
	        final Long _memberId = memberId;
	        LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
	        EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
			@Override
			public Map<String,Object> doTransaction(Session session, Transaction tx)
					throws Exception {
					Query query = session.createQuery("update AMS_Member set name=:name where id=:id");
					query.setParameter("name", (String)paramMap.get("name"));
					query.setParameter("id", _memberId);
					query.executeUpdate();
					member.setName((String)paramMap.get("name"));
					
		            query = session.createQuery("from AMS_MemberEnterprise where member_id=:member_id");
		            query.setParameter("member_id", (Long)_member_entity.get("id"));
		            List<Map<String,Object>> list = query.list();
		            //设置企业信息
		            if(list.size()>0){
		            	Map<String,Object> enterprise = list.get(0);
		            	if(Constant.ENTERPRISE_CHECK_STATE_AUDITED_SUCCESS.equals(enterprise.get("check_state"))){
		            		throw new BizException(ErrorCode.ENTERPRISE_HAS_CHECK,"企业审核成功，不需要再上传企业信息！");
		            	}
		            	StringBuffer sb = new StringBuffer();
		            	sb.setLength(0);
		            	sb.append("update AMS_MemberEnterprise set  name=:name, check_state=:check_state");
		            	if(paramMap.get("address")!=null){
		            		sb.append(",address=:address");
		            	}
		            	if(paramMap.get("organization_code")!=null){
		            		sb.append(",organization_code=:organization_code");
		            	}
		            	if(paramMap.get("telephone")!=null){
		            		sb.append(",telephone=:telephone");
		            	}
		            	if(paramMap.get("business_license")!=null){
		            		sb.append(",business_license=:business_license");
		            	}
		            	if(paramMap.get("legal_name")!=null){
		            		sb.append(",legal_name=:legal_name");
		            	}
		            	if(paramMap.get("legal_idstype")!=null){
		            		sb.append(",legal_idstype=:legal_idstype");
		            	}
		            	if(paramMap.get("legal_ids")!=null){
		            		sb.append(",legal_ids=:legal_ids");
		            	}
		            	if(paramMap.get("legalPhone")!=null){
		            		sb.append(",legalPhone=:legalPhone");
		            	}
		            	if(paramMap.get("accountNo") != null){
		            		sb.append(",accountNo=:accountNo");
		            	}
		            	if(paramMap.get("parentBankName") != null){
		            		sb.append(",parentBankName=:parentBankName");
		            	}
		            	if(paramMap.get("bankCityNo") != null){
		            		sb.append(",bankCityNo=:bankCityNo");
		            	}
		            	if(paramMap.get("bankName") != null){
		            		sb.append(",bankName=:bankName");
		            	}
		            	sb.append(" where id=:id");
		            	query = session.createQuery(sb.toString());
			            query.setParameter("name", (String)paramMap.get("name"));
			            query.setParameter("check_state", Constant.ENTERPRISE_CHECK_STATE_AUDITED_WAIT);
			            if(paramMap.get("address")!=null){
			            	query.setParameter("address", (String)paramMap.get("address"));
		            	}
		            	if(paramMap.get("organization_code")!=null){
		            		query.setParameter("organization_code", (String)paramMap.get("organization_code"));
		            	}
		            	if(paramMap.get("telephone")!=null){
		            		query.setParameter("telephone", (String)paramMap.get("telephone"));
		            	}
		            	if(paramMap.get("business_license")!=null){
		            		query.setParameter("business_license", (String)paramMap.get("business_license"));
		            	}
		            	if(paramMap.get("legal_name")!=null){
		            		query.setParameter("legal_name", (String)paramMap.get("legal_name"));
		            	}
		            	if(paramMap.get("legal_idstype")!=null){
		            		query.setParameter("legal_idstype", (Long)paramMap.get("legal_idstype"));
		            	}
		            	if(paramMap.get("legal_ids")!=null){
		            		query.setParameter("legal_ids", (String)paramMap.get("legal_ids"));
		            	}
		            	if(paramMap.get("legalPhone")!=null){
		            		query.setParameter("legalPhone", (String)paramMap.get("legalPhone"));
		            	}
		            	if(paramMap.get("accountNo") != null){
		            		query.setParameter("accountNo", (String)paramMap.get("accountNo"));
		            	}
		            	if(paramMap.get("parentBankName") != null){
		            		query.setParameter("parentBankName", (String)paramMap.get("parentBankName"));
		            	}
		            	if(paramMap.get("bankCityNo") != null){
		            		query.setParameter("bankCityNo", (String)paramMap.get("bankCityNo"));
		            	}
		            	if(paramMap.get("bankName") != null){
		            		query.setParameter("bankName", (String)paramMap.get("bankName"));
		            	}
			            query.setParameter("id", (Long)enterprise.get("id"));
			            query.executeUpdate();
		            }else{
		            	Map<String,String> enterprise = new HashMap<String,String>();
		            	enterprise.put("name", (String)paramMap.get("name"));
		            	enterprise.put("address", (String)paramMap.get("address"));
		            	enterprise.put("organization_code", (String)paramMap.get("organization_code"));
		            	enterprise.put("telephone", (String)paramMap.get("telephone"));
		            	enterprise.put("business_license", (String)paramMap.get("business_license"));
		            	enterprise.put("legal_name", (String)paramMap.get("legal_name"));
		            	enterprise.put("legal_idstype", String.valueOf(paramMap.get("legal_idstype")));
		            	enterprise.put("legal_ids", (String)paramMap.get("legal_ids"));
		            	enterprise.put("legalPhone", (String)paramMap.get("legalPhone"));
		            	enterprise.put("member_id", String.valueOf(_member_entity.get("id")));
		            	enterprise.put("member_label", (String)_member_entity.get("name"));
		            	enterprise.put("accountNo", (String)paramMap.get("accountNo"));
		            	enterprise.put("parentBankName", (String)paramMap.get("parentBankName"));
		            	enterprise.put("bankCityNo", (String)paramMap.get("bankCityNo"));
		            	enterprise.put("bankName", (String)paramMap.get("bankName"));
		            	enterprise.put("check_state", Constant.ENTERPRISE_CHECK_STATE_AUDITED_WAIT.toString());//企业审核状态
		            	DynamicEntityService.createEntity("AMS_MemberEnterprise", enterprise, null);
		            }
		        /*    //绑定银行卡(对公账户）
		            String accountNo = (String)paramMap.get("accountNo");
		            String parentBankName = (String)paramMap.get("parentBankName");
		            String bankCityNo = (String)paramMap.get("bankCityNo");
		            String bankName = (String)paramMap.get("bankName");
		            
		            String bank_code = "";
			        Map<String, Object> CardBinMap = getCardBin(accountNo.substring(0,6));
			        if(CardBinMap != null && CardBinMap.get("bank_code") != null){
			            bank_code = (String) CardBinMap.get("bank_code");
			        }
		            
		            String identity_cardNo_encrypt = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, (String)paramMap.get("legal_ids"));
		            Map<String, String> bankCardMap = new HashMap<String, String>();
			        bankCardMap.put("bank_name",            bankName);//银行名称
			        bankCardMap.put("identity_cardNo",      identity_cardNo_encrypt);//身份ID
			        bankCardMap.put("account_name",         (String)paramMap.get("legal_name"));//开户名
			        bankCardMap.put("bank_code",            bank_code);//银行代码
			        bankCardMap.put("card_type",            Constant.BANK_CARD_CX.toString());//卡类型
			        bankCardMap.put("account_prop",         "1");//
			        
			        bankCardMap.put("phone",                (String)paramMap.get("legalPhone"));//手机号码 法人的
			
			        bankCardMap.put("bind_state",           Constant.MEMBER_BANK_STATUS_SUCCESS.toString());
			        bankCardMap.put("member_id",            member.getId().toString());
			        StringBuilder sb = new StringBuilder();
			        sb.setLength(0);
			        for(int i = 0, j = accountNo.length() - 4; i < j; i ++) {
			            sb.append("*");
			        }
			        sb.append(accountNo.substring(accountNo.length() - 4));
			        bankCardMap.put("accountNo", sb.toString());
			        bankCardMap.put("member_label", member.getName());
			
			        Long bind_time = new Date().getTime();
			        bankCardMap.put("bind_time", bind_time.toString());
			        
			        String en_accountNo = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, accountNo);
			        bankCardMap.put("accountNo_encrypt", en_accountNo);
			        bankCardMap.put("business_card_type", Constant.BUSINESS_TYPE_BANK_CARD.toString());
			        
			        query =  session.createQuery("update AMS_MemberBank set bind_state=:bind_stat where member_id=:id");
	                query.setParameter("bind_stat", Constant.MEMBER_BANK_STATUS_ERROR);
	                query.setParameter("id", member.getId());
	                query.executeUpdate();
	                
	                DynamicEntityService.createEntity("AMS_MemberBank", bankCardMap, null);
	            */
	            logger.info("---------ok------");
				return null;
			}
		});
    	}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			   logger.info(e.getMessage(), e);
			   throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
		
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getUserAuthInfo(Long memberId)
			throws BizException {
		// TODO Auto-generated method stub
		try {
//    		Member member = new Member(memberId);
//	        String userId = member.getUserId();
//		if(userId == null) {
//			 throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
//		}
//		final Map<String,Object> entityMap = member.getUserInfo();
//		final Map<String,Object> ret = new HashMap<String,Object>();
//		EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
//			
//			@Override
//			public Map<String,Object> doTransaction(Session session, Transaction tx)
//					throws Exception {
//				Query query = session.createQuery("from AMS_MemberEnterprise where member_id=:member_id");
//	            query.setParameter("member_id", (Long)entityMap.get("id"));
//	            List<Map<String,Object>> enterpriseList = query.list();
//	            if(!enterpriseList.isEmpty()){
//	            	Map<String,Object> enterMap = enterpriseList.get(0);
//	            	ret.put("business_license", enterMap.get("business_license"));
//	            	ret.put("organization_code", enterMap.get("organization_code"));
//	            	ret.put("legal_name", enterMap.get("legal_name"));
//	            	ret.put("legal_ids", enterMap.get("legal_ids"));
//	            	ret.put("business_license_path", enterMap.get("business_license_path"));
//	            	ret.put("organization_code_path", enterMap.get("organization_code_path"));
//	            	ret.put("tax_certificate_path", enterMap.get("tax_certificate_path"));
//	            	ret.put("bank_settlement_path", enterMap.get("bank_settlement_path"));
//	            	ret.put("legal_certificate_path", enterMap.get("legal_certificate_path"));
//	            }
//	            query = session.createQuery("from AMS_MemberBank where member_id=:member_id and account_prop=:account_prop");
//	            query.setParameter("member_id", (Long)entityMap.get("id"));
//	            query.setParameter("account_prop", Constant.ACCOUNT_PROP_EXTEND);
//	            List<Map<String,Object>> bankList = query.list();
//	            if(!bankList.isEmpty()){
//	            	Map<String,Object> bank = bankList.get(0);
//	            	ret.put("bank_code", bank.get("bank_code"));
//	            	ret.put("bank_name", bank.get("bank_name"));
//	            	ret.put("card_type", bank.get("card_type"));
//	            	ret.put("account_name", bank.get("account_name"));
//	            	ret.put("bind_state", bank.get("bind_state"));
//	            	ret.put("accountNo", bank.get("accountNo"));
//	            	ret.put("accountNo_encrypt", bank.get("accountNo_encrypt"));
//	            	
//	            }
//	            return null;
//			}
//		});
//		return ret;
			
			final Long _memberId = memberId;
			Map<String, Object> ret;
			ret = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {

				@Override
				public Map<String, Object> doTransaction(Session session, Transaction trans) throws Exception {
					Query query = session.createQuery("from AMS_MemberEnterprise where member_id=:member_id");
					query.setParameter("member_id", _memberId);
					List<Map<String, Object>> list = query.list();
					if(!list.isEmpty()){
						return list.get(0);
					}
					
					return new HashMap<String, Object>();
				}
			});
			
			logger.info("ret:" + ret);
			return ret;
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			   logger.info(e.getMessage(), e);
			   throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}
	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: getUserInfo</p> 
	* <p>Description: 获取会员信息</p> 
	* @param memberId
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#getUserInfo(java.lang.Long)
	 */
	public Map<String, Object> getUserInfo(Long memberId) throws BizException {
		// TODO Auto-generated method stub
		logger.info("MemberService.getUserInfo start");
		logger.info("memberId:"+memberId);
		Member member=new Member(memberId);
		try{
			if(member.getId() == null){
				return null;
			}
			return member.getUserInfo();
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: getUserInfo</p> 
	* <p>Description: 获取会员</p> 
	* @param applicationId
	* @param bizUserId
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#getUserInfo(java.lang.Long, java.lang.String)
	 */
	public Map<String,Object> getUserInfo(Long applicationId,String bizUserId )throws BizException{
		logger.info("MemberService.getUserInfo start");
		logger.info("applicationId:"+applicationId+",bizUserId:"+bizUserId);
		Member member=new Member();
		member.setApplication_id(applicationId);
		member.setBizUserId(bizUserId);
		try{
			if(member.getId() == null){
				return null;
			}

			Map<String,Object>memberMap = member.getUserInfo();
			
			String deID_No = member.getIdentity_cardNo_encrypt() == null? null:TripleDES.decrypt(member.getUserId()+ Constant.MEMBER_BANK_ENCODE_KEY, member.getIdentity_cardNo_encrypt());
			memberMap.put("id_No",deID_No);
			
			return memberMap;
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
		
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: getUserExtendInfo</p> 
	* <p>Description: 获取会员的扩展信息</p> 
	* @param memberId
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#getUserExtendInfo(java.lang.Long)
	 */
//	public Map<String, Object> getUserExtendInfo(Long memberId)
//			throws BizException {
//		// TODO Auto-generated method stub
//		logger.info("MemberServiceImpl.getExtendInfo start");
//		logger.info("memberId:"+memberId);
//		try {
//		////	
//			final Long _memberId = memberId;
//			Map<String, Object> info =
//			EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
//				@Override
//				public Map<String, Object> doQuery(Session session) throws Exception {
//					return new Member().getUserExtendInfo(_memberId, session);
//				}
//				
//			});
//			logger.info("info:"+info);
//        	return info;	
//		////	
//		}catch (BizException e) {
//			logger.error(e.getMessage(), e);
//			throw e;
//		}catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
//		}
//	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: getUserInfo</p> 
	* <p>Description: 获取会员的信息</p> 
	* @param userId
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#getUserInfo(java.lang.String)
	 */
	public Map<String, Object> getUserInfo(String userId)throws BizException {
		// TODO Auto-generated method stub
		logger.info("getUserInfo start");
		logger.info("userId:"+userId);
		Member member=new Member();
		member.setUserId(userId);
		try{ 
			if(member.getId() == null){
				return null;
			}
			return member.getUserInfo();
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: setRealNameInfo</p> 
	* <p>Description: 个人用户的实名认证</p> 
	* @param memberId
	* @param idType
	* @param idNo
	* @param userName
	* @throws BizException 
	* @see bps.service.MemberService#setRealNameInfo(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setRealNameInfo(Long memberId, String idType, String idNo,
			String userName) throws BizException {
		// TODO Auto-generated method stub
		logger.info("setRealNameInfo start");
    	logger.info("memberId="+memberId+",idType="+idType+",idNo="+idNo+",userName="+userName);
    	if(memberId == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
    	if(idType == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 idType");
    	if(idNo == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 idNo");
    	if(userName == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 userName");
    	
    	try {
	    	//检查应用准备金是否足够
	    	Member member = new Member(memberId);
	    	Map<String, Object> memberEntity = member.getUserInfo();
	    	Long applicationId =(Long)memberEntity.get("application_id");
	    	Map<String, Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
	    	String orgNo = (String)applicationEntity.get("codeNo");
	    	Long applicationMemberId = (Long)applicationEntity.get("member_id");
	    	Map<String, Object> applicationMemberEntity = DynamicEntityService.getEntity(applicationMemberId, "AMS_Member");
	    	String applicationUserId = (String)applicationMemberEntity.get("userId");
	    	Long memberType = member.getMember_type();
	    	Map<String, Object> orgFeeEntity = Member.checkFee(orgNo, applicationUserId, Constant.FEE_TYPE_SET_REAL_NAME);
	    	
	    	//身份证都转换为大写
	    	String _idNo = idNo.toUpperCase();
	    	final Map<String, Object> paramMap = new HashMap<String, Object>();
	    	paramMap.put("member_id", memberId);
	    	paramMap.put("idType", idType);
	    	paramMap.put("idNo", _idNo);
	    	paramMap.put("userName", userName);
	    	
    	
	        String userId = member.getUserId();
	        final String identity_cardNo_encrypt = TripleDES.encrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, _idNo);
	        final String identity_cardNo_md5 = Password.encode(_idNo, "MD5");
	        paramMap.put("identity_cardNo_encrypt", identity_cardNo_encrypt);
	        paramMap.put("identity_cardNo_md5", identity_cardNo_md5);
	        boolean isIdentity = false;

	        if(Constant.MEMBER_TYPE_PERSON.equals(memberType)){
	        	isIdentity = member.getIsIdentity_checked() == null ? false : member.getIsIdentity_checked();
		    }else if(Constant.MEMBER_TYPE_ENTERPRISE.equals((memberType))){
		    	Map<String,Object> entity = getUserAuthInfo(memberId);
		    	if(entity.isEmpty())
		    		throw new BizException(ErrorCode.UN_SET_COMPSNYINFO, "未设置企业信息");
		    	isIdentity = entity.get("is_enter_person_id_checked") == null ? false : (Boolean)entity.get("is_enter_person_id_checked");
		    }
	        //判断会员类型
	        if(member.getMember_type()!=Constant.MEMBER_TYPE_PERSON.longValue()){
	        	throw new BizException(ErrorCode.PARAM_ERROR,"非个人用户类型");
	        }
			if(isIdentity) {
				 throw new BizException(ErrorCode.USER_IS_REALNAME,"已实名认证");
			}
			String environment = Environment.instance().getSystemProperty("environment");
			if(!"develop".equals(environment) && !"test".equals(environment)){
				Map<String,Object> idMap = Extension.tltIDCheckService.checkIDNo(userName, _idNo);
		    	logger.info("idMap:"+idMap);
		    	if(!"1".equals(idMap.get("command_result").toString())){
		    		throw new BizException(ErrorCode.ID_CHECK_FAIL,"身份证验证失败");
		    	}
			}
			final Map<String, Object> _orgFeeEntity = orgFeeEntity;
			final Map<String, Object> _applicationMemberEntity = applicationMemberEntity;
			StringBuffer sb = new StringBuffer();
			sb.setLength(0);
			for(int i = 0, j = _idNo.length() - 4; i < j; i ++) {
				sb.append("*");
			}
			sb.append(_idNo.substring(_idNo.length() - 4));
			paramMap.put("idNo", sb.toString());
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			
			//获取会员类型
			final Long _memberType = memberType;
			EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query;
					if(Constant.MEMBER_TYPE_PERSON.equals(_memberType)){
						query = session.createQuery("update AMS_Member set  identity_cardNo=:identity_cardNo,identity_cardNo_encrypt=:identity_cardNo_encrypt,identity_cardNo_md5=:identity_cardNo_md5,name=:name,isIdentity_checked=true,real_name_time=:real_name_time where id=:member_id");
			            query.setParameter("identity_cardNo", (String)paramMap.get("idNo"));
			            query.setParameter("identity_cardNo_encrypt", (String)paramMap.get("identity_cardNo_encrypt"));
			            query.setParameter("identity_cardNo_md5", (String)paramMap.get("identity_cardNo_md5"));
			            query.setParameter("name", (String)paramMap.get("userName"));
			            query.setParameter("real_name_time",    new Date());
			            query.setParameter("member_id", (Long)paramMap.get("member_id"));
			            query.executeUpdate();
					}else if(Constant.MEMBER_TYPE_ENTERPRISE.equals((_memberType))){
						StringBuilder sb = new StringBuilder();
						sb.append("update AMS_Member ");
						sb.append("set enter_bind_person_card_name =:enter_bind_person_card_name, " );
						sb.append("enter_bind_person_card_id =:enter_bind_person_card_id,");
						sb.append("enter_bind_person_card_id_md5 =：enter_bind_person_card_id_md5, ");
						sb.append("enter_bind_person_card_id_enc =:enter_bind_person_card_id_enc, ");
						sb.append("is_enter_person_identity_checked =：is_enter_person_identity_checked ");
						sb.append("where member_id =：member_id");
						query = session.createQuery(sb.toString());
						query.setParameter("bind_card_name",    (String)paramMap.get("userName"));
						query.setParameter("bind_card_id",    new Date());
						query.setParameter("bind_card_id_md5",    (String)paramMap.get("identity_cardNo_md5"));
						query.setParameter("bind_card_id_enc",    (String)paramMap.get("identity_cardNo_encrypt"));
						query.setParameter("is_id_checked",    true);
						query.setParameter("member_id",    (Long)paramMap.get("member_id"));
						query.executeUpdate();
					}
					
		            logger.info("---------ok------");
		            
		            //手续费
		            if(_orgFeeEntity != null){
		            	Member.feeOpe(_applicationMemberEntity, _orgFeeEntity, Constant.FEE_TYPE_SET_REAL_NAME, session);
		            }
		            
					return null;
				}
			});
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			   logger.info(e.getMessage(), e);
			   throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: bindPhone</p> 
	* <p>Description: 绑定手机号码</p> 
	* @param memberId
	* @param phone
	* @throws BizException 
	* @see bps.service.MemberService#bindPhone(java.lang.Long, java.lang.String)
	 */
	public void bindPhone(Long memberId, String phone) throws BizException {
		// TODO Auto-generated method stub
		logger.info("bindPhone start");
    	logger.info("memberId:"+memberId+",phone:"+phone);
    	if(memberId == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
    	if(phone == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 phone");
    	final Map<String, Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("member_id", memberId);
    	paramMap.put("phone", phone);
    	
    	try {
    		Member member = new Member(memberId);
	        String userId = member.getUserId();
		if(userId == null || "".equals(userId)) {
			throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
		}
		EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
			@Override
			public Map<String,Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				Query query = session.createQuery("update AMS_Member set  phone=:phone,isPhone_checked=true where id=:member_id");
	            query.setParameter("phone", (String)paramMap.get("phone"));
	            query.setParameter("member_id", (Long)paramMap.get("member_id"));
	            query.executeUpdate();
	            logger.info("---------ok------");
				return null;
			}
		});
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			   logger.info(e.getMessage(), e);
			   throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: setPayPassword</p> 
	* <p>Description: 设置支付密码</p> 
	* @param memberId
	* @param newPayPassword
	* @param oldPayPassword
	* @throws BizException 
	* @see bps.service.MemberService#setPayPassword(java.lang.Long, java.lang.String, java.lang.String)
	 */
	public void setPayPassword(Long memberId, String newPayPassword,
			String oldPayPassword) throws BizException {
		// TODO Auto-generated method stub
		logger.info("MemberServiceImpl.changeLoginPwd start");
		logger.info("memberId:" + memberId + "; newPayPassword:" + newPayPassword);
		
		if(memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 memberId");
		if(newPayPassword == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 newPayPassword");
		Member member = new Member(memberId);
		final Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("memberId", memberId);
		
		try {
			if(Constant.USER_STATE_LOCKED.equals(member.getUser_state())) {
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户已被锁定");
			}
			if(member.getUserId()==null){
				throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
			}
			
			newPayPassword 	= Password.encode(newPayPassword, "SHA1WithRSA");
			paramMap.put("newPayPassword", newPayPassword);
			
			if(member.getPay_password() != null && !"".equals(member.getPay_password())){
				if(oldPayPassword==null || "".equals(oldPayPassword)){
					throw new BizException(ErrorCode.PASSWORD_ERROR, "新老密码不能一致");
				}
				oldPayPassword = Password.encode(oldPayPassword, "SHA1WithRSA");
				if(!oldPayPassword.equals(member.getPay_password())){
					throw new BizException(ErrorCode.PASSWORD_ERROR, "密码不一致");
				}
				if(newPayPassword.equals(member.getPay_password())){
					throw new BizException(ErrorCode.PASSWORD_ERROR, "新老密码不能一致");
				}
			}
        	EntityManagerUtil.execute(new TransactionWork<Object>() {
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_Member set pay_password=:pay_password where id=:member_id");
					query.setParameter("pay_password", (String)paramMap.get("newPayPassword"));
					query.setParameter("member_id", (Long)paramMap.get("memberId"));
					query.executeUpdate();
					return null;
				}
	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: resetPassword</p> 
	* <p>Description: 重置登录密码</p> 
	* @param memberId
	* @param password
	* @throws BizException 
	* @see bps.service.MemberService#resetPassword(java.lang.Long, java.lang.String)
	 */
	public void resetPassword(Long memberId, String password)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("MemberServiceImpl.resetPassword start");
		logger.info("memberId:" + memberId + "; password:" + password);
		final Long _memberId      = memberId;
		final String _newLoginPwd = password;
		if(_memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
		if(_newLoginPwd == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 new_login_pwd");
		Member member = new Member(_memberId);
		
		try {
			if( Constant.USER_STATE_LOCKED.equals(member.getUser_state())) {
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户已被锁定");
			}
			if(member.getUserId() == null)
				throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
			
			final String login_password 	= Password.encode(_newLoginPwd, "SHA1WithRSA");
			if(login_password.equals(member.getLogin_password()))
				throw new BizException(ErrorCode.PASSWORD_ERROR, "新老密码不能一致");
			
	        EntityManagerUtil.execute(new TransactionWork<Object>() {
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_Member set login_password=:login_password where id=:member_id");
					query.setParameter("login_password", login_password);
					query.setParameter("member_id", _memberId);
					query.executeUpdate();
					return null;
				}
	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
		
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: setLoginSendSms</p> 
	* <p>Description: 设置登录短信通知</p> 
	* @param memberId
	* @throws BizException 
	* @see bps.service.MemberService#setLoginSendSms(java.lang.Long)
	 */
	public void setLoginSendSms(Long memberId) throws BizException {
		// TODO Auto-generated method stub
		logger.info("MemberServiceImpl.setLoginSendSms start");
		logger.info("memberId:" + memberId );
		final Long _memberId      = memberId;
		if(_memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
		Member member = new Member(_memberId);
		
		try {
			if( Constant.USER_STATE_LOCKED.equals(member.getUser_state())) {
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户已被锁定");
			}
			if(member.getUserId() == null)
				throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
			boolean isSMSLoginNotice = false;
			if(member.getIsSMS_login_notice() != null){
				isSMSLoginNotice = member.getIsSMS_login_notice();
			}
			if(isSMSLoginNotice)
				throw new BizException(ErrorCode.USER_IS_LOGIN_NOTIC_SMS, "已经设置用户登录短信通知");
	        EntityManagerUtil.execute(new TransactionWork<Object>() {
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_Member set isSMS_login_notice=true where id=:member_id");
					query.setParameter("member_id", _memberId);
					query.executeUpdate();
					return null;
				}
	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: setLoginSendEmail</p> 
	* <p>Description:设置登录邮件通知 </p> 
	* @param memberId
	* @throws BizException 
	* @see bps.service.MemberService#setLoginSendEmail(java.lang.Long)
	 */
	public void setLoginSendEmail(Long memberId) throws BizException {
		// TODO Auto-generated method stub
		logger.info("MemberServiceImpl.setLoginSendEmail start");
		logger.info("memberId:" + memberId );
		final Long _memberId      = memberId;
		if(_memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
		Member member = new Member(_memberId);
		
		try {
			if( Constant.USER_STATE_LOCKED.equals(member.getUser_state())) {
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户已被锁定");
			}
			if(member.getUserId() == null)
				throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
			boolean isEmailLoginNotice = false;
			if(member.getIsEmail_login_notice() != null){
				isEmailLoginNotice = member.getIsEmail_login_notice();
			}
			if(isEmailLoginNotice)
				throw new BizException(ErrorCode.USER_IS_LOGIN_NOTIC_EMAIL, "已经设置用户登录邮箱通知");
	        EntityManagerUtil.execute(new TransactionWork<Object>() {
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_Member set isEmail_login_notice=true where id=:member_id");
					query.setParameter("member_id", _memberId);
					query.executeUpdate();
					return null;
				}
	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	
	
	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: getApplication</p> 
	* <p>Description: 获取引用，考虑是否使用redis缓存</p> 
	* @param applicationNo
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#getApplication(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getApplication(String applicationNo)throws BizException{ 
		// TODO Auto-generated method stub
		logger.info("getApplication start");
        logger.info("applicationNo:"+applicationNo);
        if(applicationNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 applicationNo");
        try {
        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("applicationNo", applicationNo);
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            Map<String, Object> app_entity= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
    				Query query = session.createQuery("from AMS_Organization where sysid=:sysid");
    				query.setParameter("sysid", (String)param.get("applicationNo"));
					List<Map<String,Object>> list = query.list();
    				if(list.size() == 0)
    					return null;
    				else
    					return (Map<String, Object>)list.get(0);
    			}
    		});
            logger.info("app_entity:"+app_entity);
    		return app_entity;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: getApplication</p> 
	* <p>Description: 获取应用</p> 
	* @param memberId
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#getApplication(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getApplication(Long memberId)throws BizException {
		// TODO Auto-generated method stub
		logger.info("getApplication start");
        logger.info("memberId:"+memberId);
        if(memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 memberId");
        try {
        	 final Member meb=new Member(memberId);
             if(meb.getUserId() == null)
             	throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
             
        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("memberId", memberId);
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            List<Map<String, Object>> app_list= 
    		EntityManagerUtil.execute(new TransactionWork<List<Map<String, Object>>>() {
				@Override
    			public List<Map<String, Object>> doTransaction(Session session, Transaction tx)
    					throws Exception {
    				Query query = session.createQuery("from AMS_Organization where member_id=:memberId");
    				query.setParameter("memberId", (Long)param.get("memberId"));
					List<Map<String,Object>> list = query.list();
    				return list;
    			}
    		});
            logger.info("app_entity:"+app_list);
    		return app_list;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: createBizMember</p> 
	* <p>Description:创建委托用户 </p> 
	* @param applicationId
	* @param bizUserId
	* @param memberType
	* @param extendParam
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#createBizMember(java.lang.Long, java.lang.String, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> createBizMember(Long applicationId,
			String bizUserId, Long memberType,
			Map<String, Object> extendParam)throws BizException {
		// TODO Auto-generated method stub
		logger.info("createBizMember start");
        logger.info("applicationId:"+applicationId);
        if(applicationId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 applicationId");
        if(bizUserId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 bizUserId");
        if(memberType == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 memberType");
        if(memberType != 2L && memberType != 3L){
        	throw new BizException(ErrorCode.PARAM_ERROR,"会员类型不识别，请传入正确值");
        }
        try {
        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("applicationId", applicationId);
            param.put("bizUserId", bizUserId);
            param.put("memberType", memberType);
            if(extendParam != null){
            	param.putAll(extendParam);
            }
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            Map<String, Object> member_entity= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
					Map<String,Object> org_entity = DynamicEntityService.getEntity((Long)param.get("applicationId"), "AMS_Organization");
					if(org_entity == null){
						throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"所属应用不存在");
					}
					Query query = session.createQuery("from AMS_Member where application_id=:application_id and biz_user_id=:bizUserId");
    				query.setParameter("application_id", (Long)param.get("applicationId"));
    				query.setParameter("bizUserId", (String)param.get("bizUserId"));
					List<Map<String,Object>> list = query.list();
					if(list.size()>0){
						throw new BizException(ErrorCode.USER_ERROR,"所属应用下已经存在此用户");
					}
					 Map<String, String> memberMap = new HashMap<String, String>();
			         String uuid = UUID.randomUUID().toString();
			         memberMap.put("userId",             uuid);
			         memberMap.put("member_type",        String.valueOf(param.get("memberType")));
			         logger.info("member_type=" + param.get("memberType").toString());
			         if(Constant.MEMBER_TYPE_ENTERPRISE.equals((Long)param.get("memberType"))){
			        	 memberMap.put("user_state",         String.valueOf(Constant.USER_STATE_AUDITED_WAIT));
			         }else{
			        	 memberMap.put("user_state",         String.valueOf(Constant.USER_STATE_ACTIVATION));
			         }
			         memberMap.put("register_time",      String.valueOf(new Date().getTime()));
			         memberMap.put("biz_user_id", (String)param.get("bizUserId"));
			         memberMap.put("application_id", String.valueOf(org_entity.get("id")));
			         memberMap.put("application_label", String.valueOf(org_entity.get("name")));
			         String memberNo =  Member.getCustomerNo((Long)param.get("memberType"),new Date(),session);
			         memberMap.put("memberNo",           memberNo);
			         if(Util.isParam(param, "source")){
			        	 memberMap.put("source", param.get("source").toString());
			         }
			         Map<String,Object> member_entity = DynamicEntityService.createEntity("AMS_Member", memberMap, null);
			         return member_entity;
    			}
    		});
            logger.info("member_entity:"+member_entity);
    		return member_entity;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: lockBizMember</p> 
	* <p>Description: 锁住用户</p> 
	* @param memberId
	* @param extendParam
	* @throws BizException 
	* @see bps.service.MemberService#lockBizMember(java.lang.Long, java.util.Map)
	 */
	public void lockBizMember(Long memberId, Map<String, Object> extendParam)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("MemberServiceImpl.lockBizMember start");
		logger.info("memberId:" + memberId );
		final Long _memberId      = memberId;
		if(_memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 memberId");
		Member member = new Member(_memberId);
		
		try {
			if(member.getUserId() == null)
				throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
			if( Constant.USER_STATE_LOCKED.equals(member.getUser_state())) {
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户已被锁定");
			}
	        EntityManagerUtil.execute(new TransactionWork<Object>() {
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_Member set user_state=:user_state where id=:member_id");
					query.setParameter("user_state", Constant.USER_STATE_LOCKED);
					query.setParameter("member_id", _memberId);
					query.executeUpdate();
					return null;
				}
	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: unLockBizMember</p> 
	* <p>Description:解锁用户 </p> 
	* @param memberId
	* @param extendParam
	* @throws BizException 
	* @see bps.service.MemberService#unLockBizMember(java.lang.Long, java.util.Map)
	 */
	public void unLockBizMember(Long memberId, Map<String, Object> extendParam)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("MemberServiceImpl.unLockBizMember start");
		logger.info("memberId:" + memberId );
		final Long _memberId      = memberId;
		if(_memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 memberId");
		Member member = new Member(_memberId);
		
		try {
			if(member.getUserId() == null)
				throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
			if(!Constant.USER_STATE_LOCKED.equals(member.getUser_state()) ) {
//				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户未被锁定！");
				return;
			}
	        EntityManagerUtil.execute(new TransactionWork<Object>() {
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_Member set user_state=:user_state where id=:member_id");
					query.setParameter("user_state", Constant.USER_STATE_ACTIVATION);
					query.setParameter("member_id", _memberId);
					query.executeUpdate();
					return null;
				}
	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 获取用户绑定卡列表
	 * @param memberId 会员ID
	 * @param cardKind 卡种  1：银行卡， 2:万商卡
	 * @return
	 * @throws BizException
	 */
	public List<Map<String, Object>> getBindBankCardList(Long memberId,Long cardKind)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("MemberService getBankCardList start");
		logger.info("memberId:" + memberId);
		final Member member  = new Member(memberId);
//		final Long _memberId = memberId;
		final Long _cardKind = cardKind;
    	try {
			if( Constant.USER_STATE_LOCKED.equals(member.getUser_state())) {
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户已被锁定");
			}
    		List<Map<String, Object>> resList= EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>() {
    			@Override
    			public List<Map<String, Object>> doQuery(Session session) throws Exception {
    		    	return member.getBankCardList(_cardKind, session);
				}
			});
    		return resList;
    	}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: getBindBankCard</p> 
	* <p>Description:获取绑定的银行卡 </p> 
	* @param memberId
	* @param cardNo
	* @return
	* @throws BizException 
	* @see bps.service.MemberService#getBindBankCard(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getBindBankCard(Long memberId, String cardNo)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("getBindBankCard start");
        logger.info("memberId:"+memberId);
        if(memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 memberId");
        if(cardNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 cardNo");
        try {
        	 final Member meb=new Member(memberId);
             if(meb.getUserId() == null)
             	throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
             
        	final Map<String, Object> param=new HashMap<String, Object>();
        	String en_accountNo = TripleDES.encrypt(meb.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, cardNo);
            param.put("memberId", memberId);
            param.put("en_accountNo", en_accountNo);
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            Map<String, Object> bank_list= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				
				@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
					Query query = session.createQuery("from AMS_MemberBank where member_id=:memberId and accountNo_encrypt=:accountNo_encrypt and bind_state=:bind_state");
    				query.setParameter("memberId", (Long)param.get("memberId"));
    				query.setParameter("accountNo_encrypt", (String)param.get("en_accountNo"));
    				query.setParameter("bind_state",Constant.MEMBER_BANK_STATUS_SUCCESS);
					List<Map<String,Object>> list = query.list();
					if(list.size()>0){
						return list.get(0);
					}
    				return null;
    			}
    		});
            logger.info("bank_list:"+bank_list);
    		return bank_list;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 *
	 * (非 Javadoc)
	 * <p>Title: getBindBankCard</p>
	 * <p>Description:获取绑定的银行卡 </p>
	 * @param accountNo_md5	卡号
	 * @return
	 * @throws BizException
	 * @see bps.service.MemberService#getBindBankCard(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getBindBankCard( String accountNo_md5)
			throws BizException {
		// TODO Auto-generated method stub
		logger.info("getBindBankCard start");

		if(accountNo_md5 == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 accountNo_md5");
		try {

			final Map<String, Object> param=new HashMap<>();

			param.put("accountNo_md5", accountNo_md5);
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			List<Map<String, Object>> bank_list=
					EntityManagerUtil.execute(new TransactionWork<List<Map<String, Object>>>() {

						@Override
						public List<Map<String, Object>> doTransaction(Session session, Transaction tx)
								throws Exception {
							Query query = session.createQuery("from AMS_MemberBank where accountNo_md5=:accountNo_md5 and bind_state=:bind_state");

							query.setParameter("accountNo_md5", param.get("accountNo_md5"));
							query.setParameter("bind_state", Constant.MEMBER_BANK_STATUS_SUCCESS);
							List<Map<String, Object>> list = query.list();
							if(list.size()>0){
								return list;
							}
							return null;
						}
					});
			logger.info("bank_list:"+bank_list);
			return bank_list;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	/**
	 * 解绑
	 * @param _memberId		会员ID
	 * @param _accountId	账号ID
	 * @throws BizException
	 */
	public void unbindBankCard(Long _memberId, final Long _accountId)
			throws BizException {
		logger.info("MemberServiceImpl.unbindBankCard start");
		logger.info("_memberId:"+_memberId+";_accountId:"+_accountId);
		try {
    		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			final Long accountId	= _accountId;	//银行账号
			if(accountId == null)
				 throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 accountId");
			final Long member_id = _memberId;
			if(member_id == null)
				 throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
			
			final Map<String, Object> tempMap=new HashMap<>();
			tempMap.put("query", null);
			
	        EntityManagerUtil.execute(new TransactionWork<Object>() {
	       	@Override
				public boolean before(Session session) throws Exception {
	       			Member member = new Member(member_id);
	       			if(member.getUserId() == null)
						throw new BizException(ErrorCode.USER_NOTEXSIT,"该用户不存在");
					
					Map<String, Object> entity = DynamicEntityService.getEntity(accountId, "AMS_MemberBank");
					if(entity == null)
						throw new BizException(ErrorCode.CARD_BIND_LOG_NOTEXSIT, "不存在绑定记录");
					//String accountNo_encrypt = (String)entity.get("accountNo_encrypt");
					
					//银行卡解密
			        String key  =  member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY;
			        String accountNo = ime.security.util.TripleDES.decrypt(key, (String)entity.get("accountNo_encrypt"));
					Map<String, String> temp = new HashMap<>();
					temp.put("CARD_NO", accountNo);
					//去除解除签约操作，兼容其他系统
					List<Map<String, Object>> bankList = getBindBankCard((String)entity.get("accountNo_md5"));
					logger.info("bankList:"+bankList);
					if( bankList.size() <= 1 ) {
						//目前可以，如果后期增加第三个绑卡通道后，这个要修改
						if (!entity.get("interfaceNo").equals(Constant.PAY_INTERFACE_TLT_ACCOUNT)) {
							Extension.manageService.removeContract(temp);
						}
					}
					
					return true;
				}

				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					
					StringBuilder sb = new StringBuilder();
					sb.setLength(0);
					sb.append("update AMS_MemberBank set bind_state=:bind_state, unbind_time=:unbind_time where id=:id and member_id=:member_id");
					Query query = session.createQuery(sb.toString());
					query.setParameter("bind_state", Constant.MEMBER_BANK_STATUS_ERROR);
					query.setParameter("id", accountId);
					query.setParameter("member_id", member_id);
					query.setParameter("unbind_time", new Date());
					
					int count = query.executeUpdate();
					if(count == 0){
						throw new BizException(ErrorCode.UNBINDBANKCARD_ERROR,"解绑失败");
					}
					//
//					else{
//						//判断卡号是否绑定过
//						query = session.createQuery("from AMS_MemberBank where bind_state=:state and member_id=:memberId");
//						query.setParameter("memberId", 			member_id);
//						query.setParameter("state",             Constant.MEMBER_BANK_STATUS_SUCCESS);
//						
//						if(query.list().isEmpty()) {
//							query = session.createQuery("update AMS_Member set isBindBank=false where id=:memberId");
//							query.setParameter("memberId", 			member_id);
//							query.executeUpdate();
//						}
//					}
					return null;
				}
	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
		
	}

	/**
	 * 绑定卡
	 * @param _member_id	会员ID
	 * @param _card_type	卡种
	 * @param _accountNo	卡号
	 * @param extParams		扩展参数
	 * @return
	 * @throws BizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> bindBankCard(Long _member_id, Long _card_type,
			String _accountNo, Map<String, Object> extParams)
			throws BizException {
		logger.info("MemberServiceImpl.bindCard start");
		logger.info("_member_id:" + _member_id + ";_card_type:" + _card_type + ";_accountNo:" + _accountNo
				+ ";extParams:" + extParams);
		final	String accountNo		= _accountNo;	//银行账号
		final	String bank_code		= (String)extParams.get("bank_code");	//银行代码
		final	String bank_name		= (String)extParams.get("bank_name");	//银行名称
		final	Long card_type			= _card_type;		//卡类型  1 借记卡 2贷记卡 3 准贷记卡 6预付费卡
		final 	Long identity_type		= extParams.get("identity_type") == null?1L:(Long)extParams.get("identity_type");
		final	String identity_cardNo	= (String)extParams.get("identity_cardNo");	//身份证号
		final	String account_name		= (String)extParams.get("account_name");	//姓名
		final 	Long account_prop		= (Long)extParams.get("account_prop");		//个人/企业姓名  0个人  1 企业
		final	String acct_validdate	= (String)extParams.get("acct_validdate");		//有效期
		final	String cvv2 			= (String)extParams.get("cvv2");
		final	String phone 			= (String)extParams.get("phone");
		final 	Long member_id			= _member_id;
		final	String contract_no 		= (String)extParams.get("contract_no");
		final	String interfaceNo		= extParams.get("interfaceNo") == null?Constant.PAY_INTERFACE_QUICK_CARD:(String)extParams.get("interfaceNo");

		boolean _is_safe_card 	= false;
		if( extParams.get("is_safe_card") != null ){
			_is_safe_card = Boolean.parseBoolean(extParams.get("is_safe_card").toString());
		}
		final boolean is_safe_card = _is_safe_card;
//		final 	Long account_type_id 	= (Long)extParams.get("account_type_id");
//		final	String account_type_label = (String)extParams.get("account_type_label");
			
		if(accountNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 accountNo");
		if(_member_id == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
		if(bank_code == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 bank_code");
		if(bank_name == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 bank_name");
		if(card_type == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 card_type");
		if(account_name == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 account_name");
		if(account_prop == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 account_prop");
		if(interfaceNo.equals(Constant.PAY_INTERFACE_QUICK_CARD) && phone == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 phone");
			
		if(card_type == Constant.BANK_CARD_XY) {
			if(acct_validdate == null)
				throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 account_prop");
    		if(cvv2 == null)
    			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 cvv2");
		}
		//用户登陆
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		final Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			Map<String, Object> returnMap = 
				EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				@Override
				public boolean before(Session session) throws Exception {
					logger.info("before");
					Member invalid_member = new Member();
					Member member = new Member(member_id);
					Boolean is_exsit_member = false;
					
					if(member.getUserId() == null) 
						throw new BizException(ErrorCode.USER_NOTEXSIT,"该用户不存在");
					
//					//判断是否未激活会员
//					if(member.getUser_state().equals(Constant.USER_STATE_NOACTIVATED)){
//						Member exsit_member = new Member();
//						exsit_member.setPhone(phone);
//						if(exsit_member.getId() == null){
//							is_exsit_member = false;
//						}else{
//							is_exsit_member = true;
//							invalid_member = member;
//							member = exsit_member;
//						}
//					}
					paramMap.put("invalid_member", invalid_member);
					paramMap.put("member", 			member);
					paramMap.put("is_exsit_member", is_exsit_member);
					
					return true;
				}
	
				@Override
				public Map<String, Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					logger.info("doTransaction");
					try {
						Query query = null;
						StringBuilder sb = new StringBuilder();
						List list = null;
		                
						Member member 			= (Member)paramMap.get("member");
//						Member invalid_member 	= (Member)paramMap.get("invalid_member");
						Boolean is_exsit_member = (Boolean)paramMap.get("is_exsit_member");
						
//						if(member.getIsIdentity_checked() == null || !member.getIsIdentity_checked() ){
							//积分时间取消
//							Map<String, Object> eventaram = new HashMap<String, Object>();
//							eventaram.put("member_id", 	member.getId());
//							eventaram.put("bonus_rule", BonusEvent.BONUS_RULE_REALNAME);
//		                    Event event = new Event(Constant.EVENT_TYPE_BONUS, eventaram, null);
//		                    EventManager.instance().fireEvent(event);
//		                    Map<String, Object> param = new HashMap<String, Object>();
//		                    param.put("memberId", member.getId());
//							param.put("memo", "绑定银行卡");
//							param.put("authentication_level", Constant.AUTHENTICATION_LEVEL_V2);
//		                    SafetyLevel.doAuthentication(param, session);
//						}
						//DES加密
						String identity_cardNo_encrypt = null;
//		                if(member.getIsIdentity_checked() == null || !member.getIsIdentity_checked() || member.getBank_code().equals("03080000")){	//未进行过实名认证，绑定身份证
//						if(member.getIsIdentity_checked() == null || !member.getIsIdentity_checked()){	//未进行过实名认证，绑定身份证
//							if(identity_cardNo == null) 
//								throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 identity_cardNo");
//								//身份证号码进行加密
//							MemberServiceImpl impl = new MemberServiceImpl();
//							if(impl.checkIdentityCardNo(identity_cardNo))
//								throw new BizException(ErrorCode.USER_BIND,"该身份已绑定");
//							
//							//MD5加密
////							String identity_cardNo_md5 = Password.encode(identity_cardNo, "MD5");
//							//DES
//							identity_cardNo_encrypt = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, identity_cardNo);
//							//更新会员表，绑定身份证
//							//已经有实名认证接口，银行的实名就不需要了 2015-12-7 华迪
////							sb.setLength(0);
////							sb.append("update AMS_Member "
////									+ "set name=:name, real_name_time=:real_name_time, bank_code=:bank_code, identity_cardNo=:identity_cardNo, "
////									+ "identity_cardNo_encrypt=:identity_cardNo_encrypt, identity_cardNo_md5=:identity_cardNo_md5, isIdentity_checked=:isIdentity_checked "
////									+ "where id=:memberId");
////							query = session.createQuery(sb.toString());
////							
////							sb.setLength(0);
////							sb.append(identity_cardNo.substring(0, 1));
////							for(int i = 1, j = identity_cardNo.length() - 1; i < j; i ++) {
////								sb.append("*");
////							}
////							sb.append(identity_cardNo.substring(identity_cardNo.length() - 1));
////							query.setParameter("name", 				account_name);
////							query.setParameter("identity_cardNo",	sb.toString());
////							query.setParameter("identity_cardNo_encrypt", identity_cardNo_encrypt);
////							query.setParameter("identity_cardNo_md5", identity_cardNo_md5);
////		                    query.setParameter("bank_code", bank_code);
////							query.setParameter("isIdentity_checked", true);
////							query.setParameter("memberId", 			member.getId());
////		                    query.setParameter("real_name_time",    new Date());
////							query.executeUpdate();
//						}else{
//							identity_cardNo_encrypt = member.getIdentity_cardNo_encrypt();
//						}
						identity_cardNo_encrypt = member.getIdentity_cardNo_encrypt();
		                //不知道干啥用，先注释掉
//						if(is_exsit_member) {
//						    logger.info("会员绑定转移开始");
//						    sb.setLength(0);
//		                    sb.append("from AMS_MemberMechanism where member_id=:old_member_id");
//		                    query = session.createQuery(sb.toString());
//		                    query.setParameter("old_member_id", invalid_member.getId());
//		                    List mmList = query.list();
//		                    Map<String, Object> oldEntity = (Map<String, Object>)mmList.get(0);
//
//						    sb.setLength(0);
//		                    sb.append("from AMS_MemberMechanism where member_id=:member_id and userId=:userId");
//		                    query = session.createQuery(sb.toString());
//		                    query.setParameter("member_id", member.getId());
//		                    query.setParameter("userId", (String)oldEntity.get("userId"));
//		                    mmList = query.list();
//		                    
//		                    if(mmList == null || mmList.isEmpty()) {
//							    sb.setLength(0);
//			                    sb.append("update AMS_MemberMechanism set member_id=:member_id, member_label=:member_label where member_id=:old_member_id");
//			                    query = session.createQuery(sb.toString());
//			                    query.setParameter("member_id", member.getId());
//			                    query.setParameter("member_label", member.getName());
//			                    query.setParameter("old_member_id", invalid_member.getId());
//			                    query.executeUpdate();
//		                    } else {
//		                    	sb.setLength(0);
//			                    sb.append("delete from AMS_MemberMechanism where member_id=:old_member_id");
//			                    query = session.createQuery(sb.toString());
//			                    query.setParameter("old_member_id", invalid_member.getId());
//			                    query.executeUpdate();
//		                    }
//		                    logger.info("会员绑定转移结束");
//						}
						//判断加密卡号是否绑定过，每个应用的加密不同，所以同一张卡不同应用加密后的结果不同
						if(member.getIsIdentity_checked() != null && member.getIsIdentity_checked())
							list = member.checkBankCard(accountNo, session);
						
						if(list==null || list.isEmpty()) {
							String en_accountNo = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, accountNo);
							
							Map<String, String> bankCardMap = new HashMap<>();
							bankCardMap.put("bank_code",			bank_code);
							bankCardMap.put("bank_name", 			bank_name);
							bankCardMap.put("card_type", 			card_type.toString());
							bankCardMap.put("identity_cardNo", 		identity_cardNo_encrypt);
							bankCardMap.put("account_name", 		account_name);
							bankCardMap.put("interfaceNo", 		interfaceNo);
				    		if(card_type == Constant.BANK_CARD_XY.longValue()) {
								bankCardMap.put("acct_validdate", 	acct_validdate);
								bankCardMap.put("cvv2", 			cvv2);
				    		}
				    		if(contract_no != null)
				    		    bankCardMap.put("contract_no", 		contract_no);
				    		
							bankCardMap.put("phone", 				phone);
//							bankCardMap.put("account_type_id", 		account_type_id.toString());
//							bankCardMap.put("account_type_label", 	account_type_label);
		
							bankCardMap.put("account_prop", 		account_prop.toString());
							bankCardMap.put("bind_state", 			Constant.MEMBER_BANK_STATUS_SUCCESS.toString());
							bankCardMap.put("member_id", 			member.getId().toString());
							sb.setLength(0);
							for(int i = 0, j = accountNo.length() - 4; i < j; i ++) {
								sb.append("*");
							}
							sb.append(accountNo.substring(accountNo.length() - 4));
							bankCardMap.put("accountNo", sb.toString());
							bankCardMap.put("member_label", member.getUserId());
		
							Long bind_time = new Date().getTime();
							bankCardMap.put("bind_time", bind_time.toString());
							
//							en_accountNo = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, accountNo);
							bankCardMap.put("accountNo_encrypt", en_accountNo);
							bankCardMap.put("business_card_type", Constant.BUSINESS_TYPE_BANK_CARD.toString());
							//增加MD5数据 - 2016-3-24加
							bankCardMap.put("accountNo_md5", MD5Util.MD5(accountNo));
							//修改会员绑定银行卡状态为绑定
//							if( member.getIsBindBank()== null || !member.getIsBindBank()){
//								sb.setLength(0);
//								sb.append("update AMS_Member set isBindBank=true where id=:memberId");
//								query = session.createQuery(sb.toString());
//								query.setParameter("memberId", 			member.getId());
//								query.executeUpdate();G
//							}
							bankCardMap.put("is_safe_card", String.valueOf(is_safe_card));
							Map<String, Object> memberBank_entity = DynamicEntityService.createEntity("AMS_MemberBank", bankCardMap, null);
							return memberBank_entity;
						}else{
						    if(!is_exsit_member) {
						        throw new BizException(ErrorCode.CARD_BIND,"该银行卡已绑定");
						    }else{
						        return (Map<String, Object>) list.get(0);
						    }
						}
					} catch(Exception e) {
						logger.error(e.getMessage(), e);
						throw e;
					}
				}
	        });
			
			logger.info("returnMap:" + returnMap);
			return returnMap;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	@Override
	public Map<String, Object> threeElementsBindBankCard(String bankCode, String accountName,
														 Long cardType, String accountNo, String bankName,
														 Long memberId, Long id_type, String id_no, Map<String, Object> extParams) throws Exception {
		Member member = new Member(memberId);
		Map<String, String> params = new HashMap<>();
		params.put("ID_NO",id_no);
		params.put("accountName",accountName);
//		params.put("ID_NO_ENCRYPT")
		//验证传入的身份真和姓名是否与实名记录一致
		Member.chickBindBankCardID(params, member);

		String _id_type = Member.getIdType(Constant.PAY_INTERFACE_TLT_ACCOUNT, id_type);
		//调用外部接口（通联通帐户验证接口）
		String _cardType = Member.getCardType(Constant.PAY_INTERFACE_TLT_ACCOUNT, cardType);
		Map<String, Object> ret = Extension.tltAccountVerificationService.bindBankCard(System.currentTimeMillis() + "D1",bankCode, _cardType, accountName, accountNo, 0L, _id_type, id_no, extParams);
		//检查是否成功
		String retCode = (String)ret.get("RET_CODE");
		String REQ_SN = (String)ret.get("REQ_SN");
		//成功
		if( "0000".equals(retCode) ){
			extParams.put("bank_code",		bankCode);
			extParams.put("bank_name",		bankName);
			extParams.put("identity_cardNo",id_no);
			extParams.put("identity_type",	id_type);
			extParams.put("account_name",	accountName);
//			extParams.put("account_prop",	extParams.get("account_prop"));
			extParams.put("interfaceNo",	Constant.PAY_INTERFACE_TLT_ACCOUNT);
			extParams.put("contract_no",	REQ_SN);

			bindBankCard(memberId,cardType,accountNo,extParams);
		}
		return ret;
	}

	/**
	 * 后台绑定卡
	 * @param _member_id	会员ID
	 * @param _card_type	卡种
	 * @param _accountNo	卡号
	 * @param extParams		扩展参数
	 * @return
	 * @throws BizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> bindBankCardOutCheck(Long _member_id, Long _card_type,
			String _accountNo, Map<String, Object> extParams)
			throws BizException {
		logger.info("MemberServiceImpl.bindCard start");
		logger.info("_member_id:" + _member_id + ";_card_type:" + _card_type + ";_accountNo:" + _accountNo
				+ ";extParams:" + extParams);
		final	String accountNo		= _accountNo;	//银行账号
		final	String bank_code		= (String)extParams.get("bank_code");	//银行代码
		final	String bank_name		= (String)extParams.get("bank_name");	//银行名称
		final	Long card_type			= _card_type;		//卡类型  1 借记卡 2贷记卡 3 准贷记卡 6预付费卡
		final	String identity_cardNo	= (String)extParams.get("identity_cardNo");	//身份证号
		final	String account_name		= (String)extParams.get("account_name");	//姓名
		final 	Long account_prop		= (Long)extParams.get("account_prop");		//个人/企业姓名  0个人  1 企业
		final	String acct_validdate	= (String)extParams.get("acct_validdate");		//有效期
		final	String interfaceNo	= (String)extParams.get("interfaceNo");		//有效期
		final	String cvv2 			= (String)extParams.get("cvv2");
		final	String phone 			= (String)extParams.get("phone");
		final 	Long member_id			= _member_id;
		final	String contract_no 		= (String)extParams.get("contract_no");
		final   Long identity_type   = (Long)extParams.get("identity_type");
		boolean _is_safe_card 	= false;
		if( extParams.get("is_safe_card") != null ){
			_is_safe_card = Boolean.parseBoolean(extParams.get("is_safe_card").toString());
		}
		final boolean is_safe_card = _is_safe_card;
			
		if(accountNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 accountNo");
		if(_member_id == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
		if(bank_code == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 bank_code");
		if(bank_name == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 bank_name");
		if(card_type == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 card_type");
		if(account_name == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 account_name");
		if(account_prop == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 account_prop");
		if(phone == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 phone");
			
		if(card_type == Constant.BANK_CARD_XY) {
			if(acct_validdate == null)
				throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 account_prop");
    		if(cvv2 == null)
    			throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 cvv2");
		}
		//用户登陆
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		final Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			Map<String, Object> returnMap = 
				EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
				@Override
				public boolean before(Session session) throws Exception {
					logger.info("before");
					Member invalid_member = new Member();
					Member member = new Member(member_id);
					Boolean is_exsit_member = false;
					if(member.getUserId() == null) 
						throw new BizException(ErrorCode.USER_NOTEXSIT,"该用户不存在");
					paramMap.put("invalid_member", invalid_member);
					paramMap.put("member", 			member);
					paramMap.put("is_exsit_member", is_exsit_member);
					String _identity_cardNo = identity_cardNo.trim();
					String _identity_cardNo_encrypt = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, _identity_cardNo);
					if(!identity_type.equals(member.getCertificate_type()))
						throw new BizException(ErrorCode.OTHER_ERROR,"证件类型与实名证件类型不符");
					if(!_identity_cardNo_encrypt.equals(member.getIdentity_cardNo_encrypt()))
						throw new BizException(ErrorCode.OTHER_ERROR, "证件号与实名证件号不符");
					return true;
				}
	
				@Override
				public Map<String, Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					logger.info("doTransaction");
					try {
						Query query = null;
						StringBuilder sb = new StringBuilder();
						List list = null;
		                
						Member member 			= (Member)paramMap.get("member");
						Boolean is_exsit_member = (Boolean)paramMap.get("is_exsit_member");
						String identity_cardNo_encrypt = null;
						identity_cardNo_encrypt = member.getIdentity_cardNo_encrypt();
						//判断卡号是否绑定过
						if(member.getIsIdentity_checked() != null && member.getIsIdentity_checked())
							list = member.checkBankCard(accountNo, session);
						
						if(list==null || list.isEmpty()) {
							String en_accountNo = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, accountNo);
							
							Map<String, String> bankCardMap = new HashMap<>();
							bankCardMap.put("bank_code",			bank_code);
							bankCardMap.put("bank_name", 			bank_name);
							bankCardMap.put("card_type", 			card_type.toString());
							bankCardMap.put("identity_cardNo", 		identity_cardNo_encrypt);
							bankCardMap.put("account_name", 		account_name);
							bankCardMap.put("interfaceNo", 			interfaceNo);
				    		if(card_type == Constant.BANK_CARD_XY.longValue()) {
								bankCardMap.put("acct_validdate", 	acct_validdate);
								bankCardMap.put("cvv2", 			cvv2);
				    		}
				    		if(contract_no != null)
				    		    bankCardMap.put("contract_no", 		contract_no);	
							bankCardMap.put("phone", 				phone);
							bankCardMap.put("account_prop", 		account_prop.toString());
							bankCardMap.put("bind_state", 			Constant.MEMBER_BANK_STATUS_SUCCESS.toString());
							bankCardMap.put("member_id", 			member.getId().toString());
							sb.setLength(0);
							for(int i = 0, j = accountNo.length() - 4; i < j; i ++) {
								sb.append("*");
							}
							sb.append(accountNo.substring(accountNo.length() - 4));
							bankCardMap.put("accountNo", sb.toString());
							bankCardMap.put("member_label", member.getUserId());
		
							Long bind_time = new Date().getTime();
							bankCardMap.put("bind_time", bind_time.toString());
							bankCardMap.put("accountNo_encrypt", en_accountNo);
							bankCardMap.put("business_card_type", Constant.BUSINESS_TYPE_BANK_CARD.toString());
							//增加MD5数据 - 2016-3-24加
							bankCardMap.put("accountNo_md5", MD5Util.MD5(accountNo));
							bankCardMap.put("is_safe_card", String.valueOf(is_safe_card));
							bankCardMap.put("is_out_check", "true");
							bankCardMap.put("identity_type", String.valueOf(identity_type));
							Map<String, Object> memberBank_entity = DynamicEntityService.createEntity("AMS_MemberBank", bankCardMap, null);
							return memberBank_entity;
						}else{
						    if(!is_exsit_member) {
						        throw new BizException(ErrorCode.CARD_BIND,"该银行卡已绑定");
						    }else{
						        return (Map<String, Object>) list.get(0);
						    }
						}
					} catch(Exception e) {
						logger.error(e.getMessage(), e);
						throw e;
					}
				}
	        });
			
			logger.info("returnMap:" + returnMap);
			return returnMap;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	
	
	
	/**
	 * 获取卡bin实体
	 * @param cardBin 银行卡卡bin
	 * @return 
	 * @throws BizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getCardBin(String cardBin) throws BizException {
		logger.info("MemberServiceImpl.getCardBin start");
		logger.info("cardBin:"+cardBin);
		try {
			final String _cardBin  = cardBin;
			Map<String, Object> ressMap=
			EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {

					Query query = session.createQuery("from AMS_CardBin where card_bin=:card_bin and card_state=:card_state");
					query.setParameter("card_bin", _cardBin);
					query.setParameter("card_state", Constant.STATE_VAILD);
					List list = query.list();
					if(list.isEmpty())
						return null;
					else
						return (Map<String, Object>)list.get(0);
				}
			});
			logger.info("ressMap:"+ressMap);
			return ressMap;
		////	
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	
	/**
	 * 验证身份证号码是否存在
	 * @param identityCardNo 身份证号
	 * @return 
	 * @throws BizException
	 */
	public Boolean checkIdentityCardNo(String identityCardNo)
			throws BizException {
		logger.info("MemberServiceImpl.checkIdentityCardNo start");
		logger.info("identityCardNo:" + identityCardNo);
		if(identityCardNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR, "请传入参数 identityCardNo");
		final String _identityCardNo = identityCardNo;
		try{
			Boolean result =
				EntityManagerUtil.execute(new QueryWork<Boolean>() {
					@Override
					public Boolean doQuery(Session session) throws Exception {
						String identityCardNo=MD5Util.MD5(_identityCardNo);
						Query query = session.createQuery("from AMS_Member where member_type=:member_type and user_state=:user_state and identity_cardNo_md5=:identity_cardNo_md5");
						query.setParameter("member_type", Constant.MEMBER_TYPE_PERSON);
						query.setParameter("user_state", Constant.USER_STATE_ACTIVATION);
						query.setParameter("identity_cardNo_md5", identityCardNo);
						if(!query.list().isEmpty())
							return true;
						
						query = session.createQuery("from AMS_Member where member_type=:member_type and user_state=:user_state and identity_cardNo_md5=:identity_cardNo_md5");
						query.setParameter("member_type", Constant.MEMBER_TYPE_PERSON);
						query.setParameter("user_state", Constant.USER_STATE_LOCKED);
						query.setParameter("identity_cardNo_md5", identityCardNo);
						if(!query.list().isEmpty())
							return true;
						
						return false;
					}
				});
			logger.info("result:" + result);
			return result;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
		
	}
	
	/**
	 * 获取银行信息
	 * @param bankCode 银行代码
	 * @return 
	 * @throws BizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getBankInfo(String bankCode) throws BizException {
		logger.info("MemberServiceImpl.getBankInfo start");
		logger.info("bankCode:"+bankCode);
		try {
			final String _bankCode = bankCode;
			Map<String, Object> ressMap=
			EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					Query query = session.createQuery("from AMS_Bank where bank_code=:bank_code");
					query.setParameter("bank_code", _bankCode);
					List list = query.list();
					if(list.isEmpty())
						return null;
					else
						return (Map<String, Object>)list.get(0);
				}
			});
			logger.info("ressMap"+ressMap);
			return ressMap;
		////	
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	
	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: registerDeveloper</p> 
	* <p>Description:会员成为开发者 </p> 
	* @param memberId  会员ID 必传
	* @param params    开发者信息  必传    name 姓名  identity_cardNo 身份证  phone手机 email 邮件
	* @throws BizException 
	* @see bps.service.MemberService# registerDeveloper(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public void registerDeveloper(Long memberId,Map<String,Object> params) throws BizException {
		// TODO Auto-generated method stub
		logger.info("activateCompanyUser start");
    	logger.info("memberId:"+memberId);
    	if(memberId == null)
    		throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
    	final Map<String, Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("member_id", memberId);
    	paramMap.putAll(params);    	
    	try {
    		Member member = new Member(memberId);
    	boolean isDeveloper = false;
    	if(member.getIsDeveloper()!=null){
    		isDeveloper = member.getIsDeveloper();
    	}
		if(isDeveloper) {
			 throw new BizException(ErrorCode.USER_IS_DEVELOPER,"该用户已成为开发者");
		}
		final Map<String,Object> _memberEntity = member.getUserInfo();
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
			@Override
			public Map<String,Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				Query query = session.createQuery("from AMS_Developer where member_id=:member_id");
				query.setParameter("member_id", (Long)paramMap.get("member_id"));
				List<Map<String,Object>> list = query.list();
				if(list.size()>0){
					throw new BizException(ErrorCode.USER_IS_DEVELOPER,"该用户已成为开发者");
				}else{
					Map<String,String> entity = new HashMap<String,String>();
					entity.put("name", (String)paramMap.get("name"));
					entity.put("identity_cardNo", (String)paramMap.get("identity_cardNo"));
					entity.put("phone", (String)paramMap.get("phone"));
					entity.put("email", (String)paramMap.get("email"));
					entity.put("member_id", String.valueOf(_memberEntity.get("id")));
					entity.put("member_label", (String)_memberEntity.get("name"));
					DynamicEntityService.createEntity("AMS_Developer", entity, null);
				}
				query = session.createQuery("update AMS_Member set  isDeveloper=:isDeveloper where id=:member_id");
	            query.setParameter("isDeveloper", true);
	            query.setParameter("member_id", (Long)paramMap.get("member_id"));
	            query.executeUpdate();
	            logger.info("---------ok------");
				return null;
			}
		});
    	}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			   logger.info(e.getMessage(), e);
			   throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}

	
	/**
     * 查询银行卡通过bankcode
     * @param bank_code 银行卡号
     * @return
     * @throws BizException
     */
    public Map<String, Object> getBankCode(String bank_code)throws BizException{
    	 logger.info("MemberService getBankCardIdByBankCode start");
         logger.info("bank_code:" + bank_code);
         try {
        	final String bankCode =bank_code; 
        	return EntityManagerUtil.execute(new QueryWork<Map<String,Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session)
						throws Exception {
					String hql = "from AMS_BankCode where bank_code =:bank_code";
					Query query = session.createQuery(hql);
					query.setParameter("bank_code", bankCode);
					List list = query.list();
					Map<String, Object> map = null;
					if(list !=null && list.size()>0){
						map = (Map<String, Object>) list.get(0);
					}
					return map;
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("MemberService getBankCardById end");
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
			}
     }

	public Map<String, Object> getDeveloperInfo(Long memberId)
			throws BizException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getDevelopParam(Long applicationId)
			throws BizException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 银行卡解密
	 * @param memberId 会员编号
	 * @param enBankCardNo 银行卡号密文
	 * @return
	 * @throws Exception
	 */
	public String decryptBankCardNo(Long memberId, String enBankCardNo) throws BizException{
		logger.info("decryptBankCardNo start");
		logger.info("memberId:" + memberId + "; enBankCardNo:" + enBankCardNo); 
		if(memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请输入memberId");
		if(enBankCardNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请输入enBankCardNo");
		try{
			Member member = new Member(memberId);
			if(member.getUserId() == null)
				throw new BizException(ErrorCode.USER_NOTEXSIT, "用户尚未注册");
			
			String bankCardNo = TripleDES.decrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, enBankCardNo);
			return bankCardNo;
		}catch(BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}
	
	/**
     * 身份证解密
     * @param memberId 会员编号
     * @param enIdentityCardID 身份证密文
     * @return
     * @throws Exception
     */
	public String decryptIdentityCardID(Long memberId, String enIdentityCardID ) throws BizException{
		logger.info("decryptBankCardNo start");
		logger.info("memberId:" + memberId + "; enIdentityCardID:" + enIdentityCardID); 
		if(memberId == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请输入memberId");
		if(enIdentityCardID == null)
			throw new BizException(ErrorCode.PARAM_ERROR,"请输入enIdentityCardID");
		try{
			Member member = new Member(memberId);
			if(member.getUserId() == null)
				throw new BizException(ErrorCode.USER_NOTEXSIT, "用户尚未注册");
			
			String identity_cardNo = TripleDES.decrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, enIdentityCardID);
			return identity_cardNo;
		}catch(BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}
	
	
	/**
	 * 获取应用和应用下的企业用户
	 * @return
	 * @throws Exception
	 */
    public List<Map<String, Object>> getAppCompany() throws BizException{
    	logger.info("=====================================getAppCompany start======================================");
    	
    	try{
    		return EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>(){
				@Override
				public List<Map<String, Object>> doQuery(Session session)
						throws Exception {
					StringBuilder sb = new StringBuilder();
					sb.append("select C.sysid, C.biz_user_id, C.id as memberId ");
					sb.append("from ");
					sb.append("(");
					sb.append("select A.sysid, B.biz_user_id,B.id ");
					sb.append("from dyna_ams_organization A ");
					sb.append("inner join dyna_ams_member B ");
					sb.append("on A.id!=1 ");
					sb.append("and B.member_type=:memberType ");
					sb.append("and B.application_id!=1 ");
					sb.append("and A.id = B.application_id ");
					sb.append(") C ");
					sb.append("inner join dyna_ams_memberenterprise D ");
					sb.append("on (D.check_state!=:checkState or D.Check_State is null) and C.id = D.member_id");
					
					logger.info("sql:" + sb.toString());
					
					SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
					sqlQuery.setParameter("memberType", Constant.MEMBER_TYPE_ENTERPRISE);
					sqlQuery.setParameter("checkState", Constant.ENTERPRISE_CHECK_STATE_AUDITED_SUCCESS);
					sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
					List<Map<String, Object>> ret = (List<Map<String, Object>>)sqlQuery.list();
					logger.info("getAppCompany返回:" + ret);
					return ret;
				}
    		});
    	}catch(BizException e){
    		logger.error(e.getMessage(), e);
    		throw e;
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    		throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
    	}
    }

    /**
     * 企业图片保存记录
     * @param picInfo
     * @throws BizException
     */
	public void recordCompanyPic(Map<Long, Map<String, String>> picInfo) throws BizException {
		logger.info("recordCompanyPic参数picInfo=" + picInfo);
		
		try{
			final Map<Long, Map<String, String>> _picInfo = picInfo;
			
			EntityManagerUtil.execute(new TransactionWork<String>() {
				@Override
				public String doTransaction(Session session, Transaction arg1)
						throws Exception {
					for(Map.Entry<Long, Map<String, String>> temp : _picInfo.entrySet()){
						try{
							Long memberId = temp.getKey();
							Map<String, String> picMap = temp.getValue();
							
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							
							StringBuilder sb = new StringBuilder();
							sb.append("update AMS_MemberEnterprise set ");
							sb.append("check_state=:checkState,");
							if(picMap.get("business_license") != null){
								sb.append("business_license_path=:businessLicensePath,");
							}
							if(picMap.get("organization_code") != null){
								sb.append("organization_code_path=:organizationCodePath,");
							}
							if(picMap.get("tax_certificate") != null){
								sb.append("tax_certificate_path=:taxCertificatePath,");
							}
							if(picMap.get("bank_settlement") != null){
								sb.append("bank_settlement_path=:bankSettlementPath,");
							}
							if(picMap.get("org_credit_code") != null){
								sb.append("org_credit_code_path=:orgCreditCodePath,");
							}
							if(picMap.get("icp") != null){
								sb.append("icp_path=:icpPath,");
							}
							if(picMap.get("industry_permit") != null){
								sb.append("industry_permit_path=:industryPermitPath,");
							}
							if(picMap.get("legal_cerificate_front") != null){
								sb.append("legal_cerificate_front_path=:legalCerificateFrontPath,");
							}
							if(picMap.get("legal_cerificate_back") != null){
								sb.append("legal_cerificate_back_path=:legalCerificateBackPath,");
							}
							
							sb.deleteCharAt(sb.length() - 1);
							
							sb.append(" where member_id=:memberId");
							
							String queryStr = sb.toString();
							Query query = session.createQuery(queryStr);
							query.setParameter("checkState", Constant.ENTERPRISE_CHECK_STATE_AUDITED_WAIT);
							if(picMap.get("business_license") != null){
								query.setParameter("businessLicensePath", (String)picMap.get("business_license"));
							}
							if(picMap.get("organization_code") != null){
								query.setParameter("organizationCodePath", (String)picMap.get("organization_code"));
							}
							if(picMap.get("tax_certificate") != null){
								query.setParameter("taxCertificatePath", (String)picMap.get("tax_certificate"));
							}
							if(picMap.get("bank_settlement") != null){
								query.setParameter("bankSettlementPath", (String)picMap.get("bank_settlement"));
							}
							if(picMap.get("org_credit_code") != null){
								query.setParameter("orgCreditCodePath", (String)picMap.get("org_credit_code"));
							}
							if(picMap.get("icp") != null){
								query.setParameter("icpPath", (String)picMap.get("icp"));
							}
							if(picMap.get("industry_permit") != null){
								query.setParameter("industryPermitPath", (String)picMap.get("industry_permit"));
							}
							if(picMap.get("legal_cerificate_front") != null){
								query.setParameter("legalCerificateFrontPath", (String)picMap.get("legal_cerificate_front"));
							}
							if(picMap.get("legal_cerificate_back") != null){
								query.setParameter("legalCerificateBackPath", (String)picMap.get("legal_cerificate_back"));
							}
							query.setParameter("memberId", memberId);
							int n = query.executeUpdate();
							logger.info("nnnnnnnn=" + n);
						}catch(Exception e){
							logger.error(e.getMessage(), e);
						}
					}
					
					return null;
				}
			});
			
			
		}catch(BizException e){
    		logger.error(e.getMessage(), e);
    		throw e;
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    		throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
    	}
		
	}
	/**
	 * 设置安全卡
	 * @param _memberId		会员ID
	 * @param _setSafeCard	是否设置为安全卡
	 * @throws BizException
	 */
	public void setSafeCard(Long _memberId, Long _accountId, boolean _setSafeCard)
			throws BizException {
		logger.info("MemberServiceImpl.setSafeCard start");
		logger.info("_memberId:"+_memberId+";_accountId:"+_accountId);
		try {
    		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			final Long accountId	= _accountId;	//银行账号
			if(accountId == null)
				 throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 accountId");
			final Long member_id = _memberId;
			if(member_id == null)
				 throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 member_id");
			
			final Map<String, Object> tempMap=new HashMap<>();
			tempMap.put("query", null);
			
			final boolean setSafeCard = _setSafeCard;
			
	        EntityManagerUtil.execute(new TransactionWork<Object>() {
	       	@Override
				public boolean before(Session session) throws Exception {
	       			Member member = new Member(member_id);
	       			if(member.getUserId() == null)
						throw new BizException(ErrorCode.USER_NOTEXSIT,"该用户不存在");
	       			
					Map<String, Object> entity = DynamicEntityService.getEntity(accountId, "AMS_MemberBank");
					if(entity == null)
						throw new BizException(ErrorCode.CARD_BIND_LOG_NOTEXSIT, "不存在绑定记录");
					return true;
				}
		       
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					
					
					StringBuilder sb = new StringBuilder();
					sb.setLength(0);
					sb.append("update AMS_MemberBank set is_safe_card=:is_safe_card where id=:id and member_id=:member_id");
					Query query = session.createQuery(sb.toString());
					query.setParameter("is_safe_card", setSafeCard);
					query.setParameter("id", accountId);
					query.setParameter("member_id", member_id);
					
					int count = query.executeUpdate();
					if(count == 0){
						throw new BizException(ErrorCode.SET_SOFE_CARD_ERROR,"设置安全卡失败");
					}
					return null;
				}
	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
		
	}
	/**
	 * 开通简单代扣
	 * */
	public Map<String,Object> openDaikouSimple(Long applicationId, String bizUserId,Boolean isAuthed,Boolean isBinded,Long memberId) throws BizException{
		logger.info("openDaikouSimpleImpleStart:applicationId:"+applicationId+";bizUserId:"+bizUserId+";isAuthed:"+isAuthed+";isBinded:"+isBinded);
		try {

			if(applicationId == null)
				throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数 applicationId");
			if (StringUtils.isBlank(bizUserId))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizUserId为空或null");
			if (isAuthed == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数isAuthed为null");
			if (isBinded == null)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数isBinded为null");

			final Long _memberId = memberId;
			final String _bizUserId = bizUserId;
			final Long _applicationId = applicationId;
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
				@Override
				public Map<String,Object> doTransaction(Session session, Transaction tx)
						throws Exception {
					Query query = session.createQuery("update AMS_Member set  is_higher_card=:isAdvAuthed where biz_user_id=:bizUserId and application_id =:applicationId ");
					query.setParameter("isAdvAuthed", Boolean.valueOf(true));
					query.setParameter("bizUserId", _bizUserId);
					query.setParameter("applicationId", _applicationId);
					query.executeUpdate();
					//创建或更新用户等级；
					SafetyLevel safetyLevel = new SafetyLevel();
					safetyLevel.addOrEditSafetyLevel(_memberId,4L,0L,0L,session);
				/*	Query query2 = session.createQuery("update AMS_Member set  is_higher_card=:isAdvAuthed where biz_user_id=:bizUserId and application_id =:applicationId ");
					query.setParameter("isAdvAuthed", Boolean.valueOf(true));
					query.setParameter("bizUserId", _bizUserId);
					query.setParameter("applicationId", _applicationId);
					query.executeUpdate();*/
					logger.info("---------ok------");
					return null;
				}
			});
			Map<String,Object> daiKou_map = new HashMap<String,Object>();
			daiKou_map.put("bizUserId", bizUserId);
			daiKou_map.put("isAuthed", isAuthed);
			daiKou_map.put("isBinded", isBinded);
			daiKou_map.put("isAdvAuthed", Boolean.valueOf(true));
			daiKou_map.put("memberSafeLevel", "4");//暂定默认为4
			daiKou_map.put("isAllowPay", Boolean.valueOf(true));
			return daiKou_map;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}

	}
    /**
     * 企业图片保存记录
     */
//    public void recordCompanyPic(Long applicationId, Long memberId, Map<String, Object> extInfo) throws BizException{
//    	logger.info("recordCompanyPic参数applicationId=" + applicationId + ",memberId=" + memberId + ",extInfo=" + extInfo);
//    	
//    	try{
//    		//参数检查
//    		if(applicationId != null){
//    			throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数applicationId不能为null。");
//    		}
//    		
//    		if(memberId != null){
//    			throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数memberId不能为null。");
//    		}
//    		
//    		if(extInfo != null){
//    			throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数extInfo不能为null。");
//    		}
//    			
//    		//检查应用是否存在
//    		Map<String, Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
//    		if(applicationEntity == null || applicationEntity.isEmpty()){
//    			throw new BizException(ErrorCode.APPLICATION_NOTEXSIT, "应用不存在。");
//    		}
//    			
//    		//检查用户是否存在
//    		Map<String, Object> memberEntity = DynamicEntityService.getEntity(memberId, "AMS_Member");
//    		if(memberEntity == null || memberEntity.isEmpty()){
//    			throw new BizException(ErrorCode.USER_NOTEXSIT, "用户不存在。");
//    		}
//    		
//    		final Long _applicationId = applicationId;
//    		final Long _memberId = memberId;
//    		final Map<String, Object> _extInfo = extInfo;
//    			
//    		//加入数据库
//    		EntityManagerUtil.execute(new TransactionWork<String>(){
//				@Override
//				public String doTransaction(Session session, Transaction trans)
//						throws Exception {
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					
//					StringBuilder sb = new StringBuilder();
//					sb.append("update AMS_MemberEnterprise set ");
//					if(_extInfo.get("businessLicensePath") != null){
//						sb.append("business_license_path=:businessLicensePath,");
//					}
//					if(_extInfo.get("organizationCodePath") != null){
//						sb.append("organization_code_path=:organizationCodePath,");
//					}
//					if(_extInfo.get("taxCertificatePath") != null){
//						sb.append("tax_certificate_path=:taxCertificatePath,");
//					}
//					if(_extInfo.get("bankSettlementPath") != null){
//						sb.append("bank_settlement_path=:bankSettlementPath,");
//					}
//					if(_extInfo.get("legalCertificatePath") != null){
//						sb.append("legal_certificate_path=:legalCertificatePath,");
//					}
//					
//					sb.deleteCharAt(sb.length() - 1);
//					
//					sb.append(" where member_id=:memberId");
//					
//					String queryStr = sb.toString();
//					Query query = session.createQuery(queryStr);
//					query.setParameter("businessLicensePath", (String)_extInfo.get("businessLicensePath"));
//					query.setParameter("organizationCodePath", (String)_extInfo.get("organizationCodePath"));
//					query.setParameter("taxCertificatePath", (String)_extInfo.get("taxCertificatePath"));
//					query.setParameter("bankSettlementPath", (String)_extInfo.get("bankSettlementPath"));
//					query.setParameter("legalCertificatePath", (String)_extInfo.get("legalCertificatePath"));
//					query.setParameter("memberId", _memberId);
//					query.executeUpdate();
//					
//					return null;
//				}
//			});
//    	}catch(BizException e){
//    		logger.error(e.getMessage(), e);
//    		throw e;
//    	}catch(Exception e){
//    		logger.error(e.getMessage(), e);
//    		throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
//    	}
//    }
}
