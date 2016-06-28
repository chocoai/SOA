package bps.eventhandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.Constant;
import bps.member.Member;
import bps.member.MemberServiceImpl;
import ime.core.event.DynamicEntityEvent;
import ime.core.event.IEntityEventHandler;

public class MemberBankHandle implements IEntityEventHandler {
	/**
     * 客户自定义事件处理
     * @param entityName 事件所在的动态实体类型名称
     * @param eventName  事件名称
     * @param eventParam 事件参数
     * @param session Hibernate Session对象
     * @return 事件处理结果
     */
    private static Logger logger = Logger.getLogger(MemberEntity.class.getName());

	public Map<String, Object> customerEvent(String entityName, String eventName, Map<String, Object> eventParam, Session session) throws Exception {
//		if(eventName.equals("getBankCardNo")){
//			logger.info("getBankCardNo询银行卡号明码:memberId="+eventParam.get("memberId")+"accountNo="+eventParam.get("accountNo"));
//			Long memberId = Long.valueOf(eventParam.get("memberId").toString());
//			String accountNo = eventParam.get("accountNo").toString();
//			MemberServiceImpl memberServiceImpl = new MemberServiceImpl();
//	      	String BankCandNo = memberServiceImpl.decryptBankCardNo(memberId, accountNo);
//	      	Map<String, Object> res = new HashMap<String, Object>();
//	      	res.put("BankCandNo", BankCandNo);
//	        logger.info("res"+res);
//	        return res;
//	    } 
		try{
			if(eventName.equals("bankCardAudit")){
				logger.info("------bankCardAudit start------");
				logger.info("eventParam:"+eventParam);
				final Long id = Long.valueOf(eventParam.get("id").toString());
				Long member_id = Long.valueOf(eventParam.get("member_id").toString());
				Long account_prop = Long.valueOf(eventParam.get("account_prop").toString());
				Member member = new Member(member_id);
				Long member_type = member.getMember_type();
				if(Constant.MEMBER_TYPE_PERSON.equals(member_type))
					throw new Exception("个人会员无法进行该操作");
				if(Constant.MEMBER_TYPE_ENTERPRISE.equals(member_type)){
					if(Constant.BANK_ACCOUNT_PROP_C.equals(account_prop)){
						throw new Exception("企业会员对公账户无法进行该操作");
					}else if(Constant.BANK_ACCOUNT_PROP_P.equals(account_prop)){
						String is_checked = (String)eventParam.get("is_person_card_checked");
						if("1".equals(is_checked))
							throw new Exception("该企业个人银行卡已授权");
						EntityManagerUtil.execute(new TransactionWork<Object>() {
	
							@Override
							public Object doTransaction(Session session, Transaction tx)
									throws Exception {
								// TODO Auto-generated method stub
								Query query = session.createQuery("update AMS_MemberBank set is_person_card_checked =:is_person_card_checked,person_card_checked_time =:person_card_checked_time where id =:id ");
								query.setParameter("is_person_card_checked", true);
								query.setParameter("person_card_checked_time", new Date());
								query.setParameter("id", id);
								query.executeUpdate();
								return null;
							}
						});
					}
				}	
				return null;
			}else if(eventName.equals("bindBankCardOutCheck")){
				logger.info("-----------bindBankCardOutCheck start----------------");
				Long member_id = Long.valueOf(eventParam.get("member_id").toString());
				String accountNo = String.valueOf(eventParam.get("accountNo"));
				String bank_code = String.valueOf(eventParam.get("bank_code"));
				String bank_name = String.valueOf(eventParam.get("bank_name").toString());
				Long card_type = Long.valueOf(eventParam.get("card_type").toString());
				String identity_cardNo = String.valueOf(eventParam.get("identity_cardNo"));
				Long account_prop = Long.valueOf(eventParam.get("account_prop").toString());
			//	String acct_validdate = String.valueOf(eventParam.get("acct_validdate"));
			//	String is_safe_card = String.valueOf(eventParam.get("is_safe_card"));
			//	String cvv2 = String.valueOf(eventParam.get("cvv2"));
				String phone = String.valueOf(eventParam.get("phone"));
				Long identity_type = Long.valueOf(eventParam.get("identity_type").toString());
				if(identity_type < 1 || identity_type > 7)
					throw new Exception("非法证件类型");
				Member member = new Member(member_id);
				if(member == null)
					throw new Exception("会员不存在");
				Boolean isIdentity_checked = member.getIsIdentity_checked();
				if(!Boolean.TRUE.equals(isIdentity_checked))
					throw new Exception("该会员未实名认证");
				String account_name = member.getName();
				Map<String,Object>extParams = new HashMap<String,Object>();
				extParams.put("bank_code",bank_code);
				extParams.put("bank_name",bank_name);
				extParams.put("identity_cardNo",identity_cardNo);
				extParams.put("account_name",account_name);
				extParams.put("account_prop",account_prop);
				extParams.put("interfaceNo",Constant.PAY_INTERFACE_CARD);
			//	extParams.put("cvv2",cvv2);
				extParams.put("phone",phone);
			//	extParams.put("is_safe_card",is_safe_card);
				extParams.put("identity_type", identity_type);
				MemberServiceImpl memberS = new MemberServiceImpl();
				memberS.bindBankCardOutCheck(member_id, card_type, accountNo, extParams);
			}
			return null;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}
		//return null;
		//return eventParam;
	}

	public void postCreate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void postRemove(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void postUpdate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void preCreate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void preRemove(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void preUpdate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
