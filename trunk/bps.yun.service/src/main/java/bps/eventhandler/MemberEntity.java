/*
 * @(#)MemberEntity.java 2015-4-2 下午08:12:41
 * Copyright 2015 刘志坚, Inc. All rights reserved. 8637.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package bps.eventhandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.Constant;
import bps.member.Member;
import bps.member.MemberServiceImpl;
import bps.process.PayChannelManage;
import ime.core.event.DynamicEntityEvent;
import ime.core.event.IEntityEventHandler;
import ime.core.services.DynamicEntityService;
import ime.security.Password;
import ime.security.util.TripleDES;

/**
 * <p>File：MemberEntity.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015 2015-4-2 下午08:12:41</p>
 * <p>Company: allinpay.com</p>
 * @author 陈育秀
 * @version 1.0
 */
public class MemberEntity implements IEntityEventHandler
{
    private static Logger logger = Logger.getLogger(MemberEntity.class.getName());

    public void preCreate (DynamicEntityEvent event, Session session)throws Exception{
        Map<String, Object> member_entity = event.getEntity();
        String userId = (String)member_entity.get("userId");
        if(userId == null) {
            userId = UUID.randomUUID().toString();
            member_entity.put("userId", userId);
        }
    }
    
    public void postCreate(DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    public void preUpdate (DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    public void postUpdate(DynamicEntityEvent event, Session session)throws Exception{
    	
    }
    
    public void preRemove (DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    public void postRemove(DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    /**
     * 客户自定义事件处理
     * @param entityName 事件所在的动态实体类型名称
     * @param eventName  事件名称
     * @param eventParam 事件参数
     * @param session Hibernate Session对象
     * @return 事件处理结果
     */
    public Map<String, Object> customerEvent(String entityName, String eventName, Map<String, Object> eventParam, Session session)throws Exception{

    	if(eventName.equals("updatePayChannelManage")){
            PayChannelManage.loadPayInterfaceInfo();
    	}else if(eventName.equals("getBankCardNo")){
	        MemberServiceImpl memberServiceImpl = new MemberServiceImpl();
			Long memberId = Long.valueOf(eventParam.get("memberId").toString());
			String accountNo = eventParam.get("accountNo").toString();
	        String BankCandNo = memberServiceImpl.decryptBankCardNo(memberId, accountNo);
	        Map<String, Object> res = new HashMap<String, Object>();
	        res.put("BankCandNo", BankCandNo);
	        return res;
    	}else if(eventName.equals("getIdCardNo")){
	        MemberServiceImpl memberServiceImpl = new MemberServiceImpl();
			Long memberId = Long.valueOf(eventParam.get("memberId").toString());
			String IDNo = eventParam.get("accountNo").toString();
			String identity_cardNo = memberServiceImpl.decryptIdentityCardID(memberId, IDNo);
			Map<String, Object> res = new HashMap<String, Object>();
			res.put("identity_cardNo", identity_cardNo);
			return res;
    	}else if(eventName.equals("memberEnAudited")){//企业审核
    		logger.info("eventParam=" + eventParam);
    		
    		eventParam.put("memberEnterprise_id", Long.valueOf(eventParam.get("memberEnterprise_id").toString()));
    		eventParam.put("member_id", Long.valueOf(eventParam.get("member_id").toString()));
    		eventParam.put("state", Long.valueOf(eventParam.get("state").toString()));
    		
	        Map<String, Object> old_member_entity = DynamicEntityService.getEntity((Long)eventParam.get("member_id"), "AMS_Member");
	        if(((Long)old_member_entity.get("user_state")).equals(Constant.USER_STATE_AUDITED_WAIT)) {
    	        final Member member = new Member();
    	        final Long checkState = (Long)eventParam.get("state");
    	        final Map<String,Object> _eventParam = eventParam;
    	        EntityManagerUtil.execute(new TransactionWork<Object>() {
					@Override
					public Object doTransaction(Session session,
							Transaction tx) throws Exception {
						// TODO Auto-generated method stub
						if(checkState == null){
		    	        	throw new Exception("审核状态不正确。");
		    	        }else if(Constant.ENTERPRISE_CHECK_STATE_AUDITED_FAIL.longValue() == checkState){
		    	        		member.userAuditedFail(_eventParam, session);
		    	        }else if(Constant.ENTERPRISE_CHECK_STATE_AUDITED_SUCCESS.longValue() == checkState){
		    	        		member.userAuditedSuccess(_eventParam, session);
		    	        }else{
		    	        	throw new Exception("审核状态不正确。");
		    	        }
						return null;
					}
				});
    	    }else{
    	    	throw new Exception("用户不是待审核状态。");
    	    }
	     }else if("setRealName_back".equals(eventName)){
	    	 try{
	    		 final Long memberId = Long.valueOf(eventParam.get("member_id").toString());
		    	 final String name = String.valueOf(eventParam.get("name"));
		    	 final Long certificate_type = Long.valueOf(eventParam.get("certificate_type").toString());
		    	 String identity_cardNo = String.valueOf(eventParam.get("identity_cardNo")).trim();
		    	 Member member = new Member(memberId);
		    	 final String identity_cardNo_encrypt = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, identity_cardNo);
			     final String identity_cardNo_md5 = Password.encode(identity_cardNo, "MD5");
		    	 
			     if(certificate_type < 0 || certificate_type > 5)
			    	 throw new Exception("非法证件类型");
			     if(Boolean.TRUE.equals(member.getIsIdentity_checked()))
			    	 throw new Exception("该会员已实名");
			     StringBuffer sb = new StringBuffer();
				 sb.setLength(0);
				 for(int i = 0, j = identity_cardNo.length() - 4; i < j; i ++) {
					 sb.append("*");
				 }
					 sb.append(identity_cardNo.substring(identity_cardNo.length() - 4));
			     final String idCardNo = sb.toString();
			     
		    	 EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
					@Override
					public Map<String, Object> doTransaction(Session session,
							Transaction tx) throws Exception {
						// TODO Auto-generated method stub
						
						Query query = session.createQuery("update AMS_Member set  identity_cardNo=:identity_cardNo,identity_cardNo_encrypt=:identity_cardNo_encrypt,identity_cardNo_md5=:identity_cardNo_md5,name=:name,isIdentity_checked=true,real_name_time=:real_name_time, certificate_type =:certificate_type where id=:member_id");
			            query.setParameter("identity_cardNo", idCardNo);
			            query.setParameter("identity_cardNo_encrypt", identity_cardNo_encrypt);
			            query.setParameter("identity_cardNo_md5", identity_cardNo_md5);
			            query.setParameter("name", name);
			            query.setParameter("certificate_type", certificate_type);
			            query.setParameter("real_name_time",    new Date());
			            query.setParameter("member_id", memberId);
			            query.executeUpdate();
						return null;
					}	 
		    	 });
	    	 }catch(Exception e){
	    		 logger.error(e.getMessage(),e);
	    		 throw e;
	    	 }    	 
	     }
    	 return null;
    		//更新黑名单列表
//          if(eventName.equals("updateRiskUserList")){
//              RiskUser.loadRiskUserInfo();
  //更新渠道
//      	}
    		//插入黑名单
//       	} else if(eventName.equals("insertblcaklist")){
//            	Member member = new Member(Long.valueOf(eventParam.get("id").toString()));
//    	    	Map<String, Object> res = new HashMap<String, Object>();
//    	        res.put("isinsertblack", member.isinsertblacklist(member.getUserId(),session));
//    	        return res;
    //银行卡解码
//        	} else  
//			else if(eventName.equals("getUserdetail")){
//        	Member member = new Member(Long.valueOf(eventParam.get("id").toString()));
//          Map<String, Object> detail = member.getUserdetail(session);
//            return detail;
//    		else if(eventName.equals("withdrawOrderSuccess")){
//            logger.info("eventParam=" + eventParam);
//            Order.afterArtificial((String)eventParam.get("orderNo"), session);
//        }else if(eventName.equals("getBankCardNoOnly")){
//        	logger.info("eventParam=" + eventParam);
//        	String accountNo_encrypt = eventParam.get("accountNo_encrypt").toString();
//        	String userId = eventParam.get("userId").toString();
//        	String accountNo = TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, accountNo_encrypt);
//        	Map<String, Object> res = new HashMap<String, Object>();
//            res.put("BankCandNo", accountNo);
//            logger.info("res"+res);
//            return res;
//        }else if(eventName.equals("bankCardinBlcak")){
//        	logger.info("eventParam=" + eventParam);
//        	Map<String, Object> res = new HashMap<String, Object>();
//        	Member member = new Member();
//	        res.put("bankCardinBlcak", member.bankCardinBlcak(eventParam.get("bankcard").toString(),session));
//	        return res;
//        }
        
    }
}
