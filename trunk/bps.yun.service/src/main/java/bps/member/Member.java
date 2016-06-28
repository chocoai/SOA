package bps.member;

import bps.common.*;
import bps.order.OrderServiceImpl;
import bps.process.Extension;
import bps.service.OrderService;
import ime.core.Environment;
import ime.core.services.DynamicEntityService;
import ime.security.Password;
import ime.security.util.RSAUtil;
import ime.security.util.TripleDES;

import java.net.URLEncoder;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.kinorsoft.ams.services.QueryService;
import com.kinorsoft.ams.services.TradeService;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import bps.account.AccountServiceImpl;
import bps.code.VerificationCode;
import bps.process.RiskUser;
import bps.util.CodeUtil;
import org.json.JSONObject;

/**
 * 会员类
 * @author Wuht
 *
 */
public class Member{
	/** 会员类型 */
	private Long id;
	private Long domainId;
	private Long FM_CreatePrinId;
	private String FM_CreatePrinName;
	private Long FM_UpdatePrinId;
	private String FM_UpdatePrinName;
	private Date FM_CreateTime;
	private Date FM_UpdateTime;
	private String name;
	private String phone;
	private String address;
	private Long user_state;
	private Long member_type;
	private String userId;
	private String login_password;
	private String pay_password;
	private String email;
	private String country;
	private String province;
	private String area;
	private String identity_cardNo;
	private String user_name;
	private Date last_login_time;
	private Boolean isPhone_checked;
	private Boolean isEmail_checked;
	private Date register_time;
	private Long login_fail_amount;
	private Long pay_fail_amount;
	private Date last_login_fail_time;
	private Date last_pay_fail_time;
	private Boolean isSecurityIssue;
	private Boolean isSetExtendInfo;
//	private String loginId;
//	private String wxUesrId;
	private String memberNo;
//	private String belong_orgNo;
//	private String bind_orgNo;
	private Boolean isIdentity_checked;
	private String identity_cardNo_encrypt;
	private String identity_cardNo_md5;
	private String register_ip;
//	private Long company_id;
//	private String company_name;
	private Date login_time;
//	private String bank_code;
	private String remark;
	private Date real_name_time;
	private Boolean isRiskUesr;
//	private String phone_pay_password;
	private Long phone_pay_fail_amount;
	private Date last_phone_pay_fail_time;
	private Long source;
//	private Boolean isFree;
//	private Long free_limit;
	private Boolean isLoad=false;
//	private Boolean isBindBank;
	private Long application_id;
	private String application_label;
	private Boolean isDeveloper;
	private Boolean isSMS_login_notice;
	private Boolean isEmail_login_notice;
	private String biz_user_id;
	private Boolean is_higher_card;
	private Long certificate_type;
//	public Boolean getIsBindBank() throws Exception {
//		return isBindBank;
//	}
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public Boolean getIs_higher_card() throws Exception {
		checkParam(is_higher_card);
		return is_higher_card;
	}

	public void setIs_higher_card(Boolean is_higher_card) {
		this.is_higher_card = is_higher_card;
	}

	public Boolean getIsSMS_login_notice()throws Exception{
		checkParam(isSMS_login_notice);
		return isSMS_login_notice;
	}
	public Boolean getIsEmail_login_notice()throws Exception{
		checkParam(isEmail_login_notice);
		return isEmail_login_notice;
	}
	public Long getId() throws Exception {
		checkParam(id);
		return id;
	}

	public Long getDomainId()  throws Exception{
		checkParam(domainId);
		return domainId;
	}

	public Long getFM_CreatePrinId() throws Exception {
		checkParam(FM_CreatePrinId);
		return FM_CreatePrinId;
	}

	public String getFM_CreatePrinName()  throws Exception{
		checkParam(FM_CreatePrinName);
		return FM_CreatePrinName;
	}

	public Long getFM_UpdatePrinId() throws Exception {
		checkParam(FM_UpdatePrinId);
		return FM_UpdatePrinId;
	}

	public String getFM_UpdatePrinName() throws Exception {
		checkParam(FM_UpdatePrinName);
		return FM_UpdatePrinName;
	}

	public Date getFM_CreateTime()  throws Exception{
		checkParam(FM_CreateTime);
		return FM_CreateTime;
	}

	public Date getFM_UpdateTime()  throws Exception{
		checkParam(FM_UpdateTime);
		return FM_UpdateTime;
	}

	public String getName() throws Exception {
		checkParam(name);
		return name;
	}

	public String getPhone() throws Exception {
		checkParam(phone);
		return phone;
	}

	public String getAddress() throws Exception {
		checkParam(address);
		return address;
	}

	public Long getUser_state()  throws Exception{
		checkParam(user_state);
		return user_state;
	}

	public Long getMember_type() throws Exception {
		checkParam(member_type);
		return member_type;
	}

	public String getUserId()  throws Exception{
		checkParam(userId);
		return userId;
	}

	public String getLogin_password() throws Exception {
		checkParam(login_password);
		return login_password;
	}

	public String getPay_password() throws Exception {
		checkParam(pay_password);
		return pay_password;
	}

	public String getEmail()  throws Exception{
		checkParam(email);
		return email;
	}

	public String getCountry()  throws Exception{
		checkParam(country);
		return country;
	}

	public String getProvince() throws Exception {
		checkParam(province);
		return province;
	}

	public String getArea() throws Exception {
		checkParam(area);
		return area;
	}

	public String getIdentity_cardNo() throws Exception {
		checkParam(identity_cardNo);
		return identity_cardNo;
	}

	public String getUser_name() throws Exception {
		checkParam(user_name);
		return user_name;
	}

	public Date getLast_login_time() throws Exception {
		checkParam(last_login_time);
		return last_login_time;
	}

	public Boolean getIsPhone_checked() throws Exception {
		checkParam(isPhone_checked);
		return isPhone_checked;
	}

	public Boolean getIsEmail_checked()  throws Exception{
		checkParam(isEmail_checked);
		return isEmail_checked;
	}

	public Date getRegister_time() throws Exception {
		checkParam(register_time);
		return register_time;
	}

	public Long getlogin_fail_amount()  throws Exception{
		checkParam(login_fail_amount);
		return login_fail_amount;
	}

	public Long getPay_fail_amount() throws Exception {
		checkParam(pay_fail_amount);
		return pay_fail_amount;
	}

	public Date getLast_login_fail_time() throws Exception {
		checkParam(last_login_fail_time);
		return last_login_fail_time;
	}

	public Date getLast_pay_fail_time() throws Exception {
		checkParam(last_pay_fail_time);
		return last_pay_fail_time;
	}

	public Boolean getIsSecurityIssue() throws Exception {
		checkParam(isSecurityIssue);
		return isSecurityIssue;
	}

	public Boolean getIsSetExtendInfo() throws Exception {
		checkParam(isSetExtendInfo);
		return isSetExtendInfo;
	}

//	public String getLoginId()  throws Exception{
//		checkParam(loginId);
//		return loginId;
//	}
//
//	public String getWxUesrId()  throws Exception{
//		checkParam(wxUesrId);
//		return wxUesrId;
//	}

	public String getMemberNo()  throws Exception{
		checkParam(memberNo);
		return memberNo;
	}

//	public String getBelong_orgNo() throws Exception {
//		checkParam(belong_orgNo);
//		return belong_orgNo;
//	}
//
//	public String getBind_orgNo() throws Exception {
//		checkParam(bind_orgNo);
//		return bind_orgNo;
//	}

	public Boolean getIsIdentity_checked() throws Exception {
		checkParam(isIdentity_checked);
		return isIdentity_checked;
	}

	public String getIdentity_cardNo_encrypt()  throws Exception{
		checkParam(identity_cardNo_encrypt);
		return identity_cardNo_encrypt;
	}

	public String getIdentity_cardNo_md5() throws Exception {
		checkParam(identity_cardNo_md5);
		return identity_cardNo_md5;
	}

	public String getRegister_ip() throws Exception {
		checkParam(register_ip);
		return register_ip;
	}

//	public Long getCompany_id()  throws Exception{
//		checkParam(company_id);
//		return company_id;
//	}

//	public String getCompany_name()  throws Exception{
//		checkParam(company_name);
//		return company_name;
//	}

	public Date getLogin_time()  throws Exception{
		checkParam(login_time);
		return login_time;
	}

//	public String getBank_code() throws Exception {
//		checkParam(bank_code);
//		return bank_code;
//	}

	public String getRemark()  throws Exception{
		checkParam(remark);
		return remark;
	}

	public Date getReal_name_time()  throws Exception{
		checkParam(real_name_time);
		return real_name_time;
	}

	public Boolean getIsRiskUesr() throws Exception {
		checkParam(isRiskUesr);
		return isRiskUesr;
	}

//	public String getPhone_pay_password() throws Exception {
//		checkParam(phone_pay_password);
//		return phone_pay_password;
//	}

	public Long getPhone_pay_fail_amount() throws Exception {
		checkParam(phone_pay_fail_amount);
		return phone_pay_fail_amount;
	}

	public Date getLast_phone_pay_fail_time()  throws Exception{
		checkParam(last_phone_pay_fail_time);
		return last_phone_pay_fail_time;
	}

	public Long getSource()  throws Exception{
		checkParam(source);
		return source;
	}
	public Boolean getIsDeveloper()throws Exception{
		checkParam(isDeveloper);
		return isDeveloper;
	}
	public Long getApplicationId()throws Exception{
		checkParam(application_id);
		return application_id;
	}

//	public Boolean getIsFree()  throws Exception{
//		checkParam(isFree);
//		return isFree;
//	}

//	public Long getFree_limit() throws Exception {
//		checkParam(free_limit);
//		return free_limit;
//	}
	public Long getCertificate_type() throws Exception {
		checkParam(certificate_type);
		return certificate_type;
	}
	
	public Map<String, Object> getUserInfo() throws Exception {
		Map<String, Object> user = new HashMap<String, Object>();
		if(!this.isLoad())
			this.load();
		user.put("id", id);
		user.put("domainId", domainId);
		user.put("FM_CreatePrinId", FM_CreatePrinId);
		user.put("user_state", user_state);
		user.put("member_type", member_type);
		user.put("login_fail_amount", login_fail_amount);
		user.put("pay_fail_amount", pay_fail_amount);
//		user.put("company_id", company_id);
		user.put("phone_pay_fail_amount", phone_pay_fail_amount);
		user.put("source", source);
//		user.put("free_limit", free_limit);
		user.put("FM_CreatePrinName", FM_CreatePrinName);
		user.put("FM_UpdatePrinName", FM_UpdatePrinName);
		user.put("name", name);
		user.put("phone", phone);
		user.put("address", address);
		user.put("userId", userId);
		user.put("login_password", login_password);
		user.put("pay_password", pay_password);
		user.put("email", email);
		user.put("country", country);
		user.put("province", province);
		user.put("area", area);
		user.put("identity_cardNo", identity_cardNo);
		user.put("user_name", user_name);
//		user.put("loginId", loginId);
//		user.put("wxUesrId", wxUesrId);
		user.put("memberNo", memberNo);
//		user.put("belong_orgNo", belong_orgNo);
//		user.put("bind_orgNo", bind_orgNo);
		user.put("identity_cardNo_encrypt", identity_cardNo_encrypt);
		user.put("identity_cardNo_md5", identity_cardNo_md5);
		user.put("register_ip", register_ip);
//		user.put("company_name", company_name);
//		user.put("bank_code", bank_code);
		user.put("remark", remark);
//		user.put("phone_pay_password", phone_pay_password);
		user.put("FM_CreateTime", FM_CreateTime);
		user.put("FM_UpdateTime", FM_UpdateTime);
		user.put("last_login_time", last_login_time);
		user.put("register_time", register_time);
		user.put("last_login_fail_time", last_login_fail_time);
		user.put("last_pay_fail_time", last_pay_fail_time);
		user.put("login_time", login_time);
		user.put("real_name_time", real_name_time);
		user.put("last_phone_pay_fail_time", last_phone_pay_fail_time);
		user.put("isPhone_checked", isPhone_checked);
		user.put("isEmail_checked", isEmail_checked);
		user.put("isSecurityIssue", isSecurityIssue);
		user.put("isSetExtendInfo", isSetExtendInfo);
		user.put("isIdentity_checked", isIdentity_checked);
		user.put("isRiskUesr", isRiskUesr);
//		user.put("isFree", isFree);
		user.put("application_id", application_id);
		user.put("application_label", application_label);
		user.put("isDeveloper", isDeveloper);
		user.put("isSMS_login_notice", isSMS_login_notice);
		user.put("isEmail_login_notice", isEmail_login_notice);
		user.put("biz_user_id", biz_user_id);
		user.put("is_higher_card",is_higher_card);
		user.put("certificate_type",certificate_type);
		return user;
	}
	
	public Member() {
		
	}
	public Member(Long id) {
		this.id = id;
	}
	
	public Member(String userId){
		this.userId=userId;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public void setBizUserId(String bizUserId) {
		this.biz_user_id = bizUserId;
	}

	public void setMemberNo(String memberNo) {
		this.memberNo = memberNo;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setIdentity_cardNo_md5(String identity_cardNo_md5) {
		this.identity_cardNo_md5 = identity_cardNo_md5;
	}

	public void setApplication_id(Long application_id) {
		this.application_id = application_id;
	}
	
	public void setName(String name){
		this.name = name;
	}

	/**
	 * 通过id获取联系人
	 * @param contactId 	联系人id
	 * @return Map<String, Object>
	 * @throws Exception 
	 */
	static Map<String, Object> getContact(Long contactId,Session session) throws Exception{
	    Query query = session.createQuery("from AMS_LinkMan where id=:id");
		query.setParameter("id", contactId);
		List list = query.list();
		
		if(list.size() != 0) {
		    Map<String, Object> map = (Map<String, Object>)list.get(0);
			return map;
		}
        return null;
	}

	public Boolean isLoad(){
		return isLoad;
	}
	
	/**
	 * 加载
	 * @throws Exception
	 */
	public void load() throws Exception{
		try {
			Map<String, Object> user = EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					   StringBuilder sb = new StringBuilder();
			            sb.setLength(0);
			            Map<String, Object> tempMap=new HashMap<String, Object>();

						if(userId != null){
							tempMap=getUserInfoByUuid(userId, session);				
			            } else if(phone!=null) {
			            	 tempMap=getUserInfoByPhone(phone, null, session);			            			 			      
			            } else if(email!=null) {
			            	 tempMap=getUserInfoByEmail(email, session);//根据邮箱获取用户帐号，一般
			            } else if(memberNo!=null){
			            	 tempMap=getUserInfoByMemberNo(memberNo, null, session);
			            } else if(id!=null) {
			            	 tempMap=getUserInfoByMemberId(id, session);			            	 			      	 
			            } else if(identity_cardNo_md5!=null) {
			            	 tempMap=getUserInfoByIDCode(identity_cardNo_md5, session);			            	 			      	 
			            }else if( userId!=null){
			            	tempMap=getUserInfoByUserId(userId,session);
			            }else if(application_id!=null && biz_user_id != null){
			            	tempMap=getUserInfoByBizUserId(application_id,biz_user_id,session);
			            }else{
			            	throw new Exception("参数不足无法加载用户");
			            }
						return tempMap;
			}
			});
			if(user != null) {
				this.id = (Long)user.get("id");
				this.domainId=(Long)user.get("domainId");
				this.FM_CreatePrinId=(Long)user.get("FM_CreatePrinId");
				this.FM_UpdatePrinId=(Long)user.get("FM_UpdatePrinId");
				this.user_state=(Long)user.get("user_state");
				this.member_type=(Long)user.get("member_type");
				this.login_fail_amount=(Long)user.get("login_fail_amount");
				this.pay_fail_amount=(Long)user.get("pay_fail_amount");
//				this.company_id=(Long)user.get("company_id");
				this.phone_pay_fail_amount=(Long)user.get("phone_pay_fail_amount");
				this.source=(Long)user.get("source");
//				this.free_limit=(Long)user.get("free_limit");
				this.FM_CreatePrinName=(String)user.get("FM_CreatePrinName");
				this.FM_UpdatePrinName=(String)user.get("FM_UpdatePrinName");
				this.name=(String)user.get("name");
				this.phone=(String)user.get("phone");
				this.address=(String)user.get("address");
				this.userId=(String)user.get("userId");
				this.login_password=(String)user.get("login_password");
				this.pay_password=(String)user.get("pay_password");
				this.email=(String)user.get("email");
				this.country=(String)user.get("country");
				this.province=(String)user.get("province");
				this.area=(String)user.get("area");
				this.identity_cardNo=(String)user.get("identity_cardNo");
				this.user_name=(String)user.get("user_name");
//				this.loginId=(String)user.get("loginId");
//				this.wxUesrId=(String)user.get("wxUesrId");
				this.memberNo=(String)user.get("memberNo");
//				this.belong_orgNo=(String)user.get("belong_orgNo");
//				this.bind_orgNo=(String)user.get("bind_orgNo");
				this.identity_cardNo_encrypt=(String)user.get("identity_cardNo_encrypt");
				this.identity_cardNo_md5=(String)user.get("identity_cardNo_md5");
				this.register_ip=(String)user.get("register_ip");
//				this.company_name=(String)user.get("company_name");
//				this.bank_code=(String)user.get("bank_code");
				this.remark=(String)user.get("remark");
//				this.phone_pay_password=(String)user.get("phone_pay_password");
				this.FM_CreateTime=(Date)user.get("FM_CreateTime");
				this.FM_UpdateTime=(Date)user.get("FM_UpdateTime");
				this.last_login_time=(Date)user.get("last_login_time");
				this.register_time=(Date)user.get("register_time");
				this.last_login_fail_time=(Date)user.get("last_login_fail_time");
				this.last_pay_fail_time=(Date)user.get("last_pay_fail_time");
				this.login_time=(Date)user.get("login_time");
				this.real_name_time=(Date)user.get("real_name_time");
				this.last_phone_pay_fail_time=(Date)user.get("last_phone_pay_fail_time");
				this.isPhone_checked=(Boolean)user.get("isPhone_checked");
				this.isEmail_checked=(Boolean)user.get("isEmail_checked");
				this.isSecurityIssue=(Boolean)user.get("isSecurityIssue");
				this.isSetExtendInfo=(Boolean)user.get("isSetExtendInfo");
				this.isIdentity_checked=(Boolean)user.get("isIdentity_checked");
				this.isRiskUesr=(Boolean)user.get("isRiskUesr");
//				this.isFree=(Boolean)user.get("isFree");
				this.application_id = (Long)user.get("application_id");
				this.application_label = (String)user.get("application_label");
				this.isDeveloper = (Boolean)user.get("isDeveloper");
				this.isSMS_login_notice = (Boolean)user.get("isSMS_login_notice");
				this.isEmail_login_notice = (Boolean)user.get("isEmail_login_notice");
				this.biz_user_id = (String)user.get("biz_user_id");
				this.is_higher_card = (Boolean)user.get("is_higher_card");
				this.certificate_type = (Long)user.get("certificate_type");
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	private void checkParam(Object param) throws Exception{
		if(param==null && !isLoad()){
			load();
		}
	}
	
	private static Logger logger = Logger.getLogger("member");
	
	
	
	/**
	 * 通过邮箱获取用户信息
	 * @param email
	 * @param session
	 * @return
	 */
	private Map<String, Object> getUserInfoByEmail(String email, Session session)throws Exception{
		Query query = session.createQuery("from AMS_Member where email=:email  and application_id=:application_id");
		query.setParameter("email", email);
		query.setParameter("application_id", Constant.APPLICATION_ID_BPS_YUN);
		List list = query.list();
		
		if(list.size() != 0) {
		    for(Object obj : list) {
		        Map<String, Object> temp = (Map<String, Object>)obj;
		        return temp;
	        }
		}
        return null;
			
	}
	
	/**
	 * 通过UUD查询用户信息
	 * @param uuid		uuid
	 * @param session	session
	 * @return 用户信息
	 * @throws Exception
	 */
	private  Map<String, Object>getUserInfoByUuid(String uuid, Session session)throws Exception{
		Query query = session.createQuery("from AMS_Member where userId=:userId");
		query.setParameter("userId", uuid);
		List list = query.list();
		
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
	/**
	 * 通过id查询用户信息
	 * @param id
	 * @param session
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object>getUserInfoByMemberId(Long id,Session session) throws Exception{
		Query query = session.createQuery("from AMS_Member where id=:id");
		query.setParameter("id", id);
		List list = query.list();
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
	/**
	 * 通过身份证查询用户信息
	 * @param identity_cardNo_md5	md5卡号
	 * @param session				session
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object>getUserInfoByIDCode(String identity_cardNo_md5,Session session) throws Exception{
		Query query = session.createQuery("from AMS_Member where identity_cardNo_md5=:identity_cardNo_md5");
		query.setParameter("identity_cardNo_md5", identity_cardNo_md5);
		List list = query.list();
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
	
	private Map<String, Object>getUserInfoByUserId(String userId,Session session) throws Exception{
		Query query = session.createQuery("from AMS_Member where userId=:userId");
		query.setParameter("userId", userId);
		List list = query.list();
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
	
	private Map<String,Object> getUserInfoByBizUserId(Long applicationId,String bizUserId,Session session)throws Exception{
		Query query = session.createQuery("from AMS_Member where application_id=:application_id and biz_user_id=:biz_user_id");
		query.setParameter("application_id", applicationId);
		query.setParameter("biz_user_id", bizUserId);
		List list = query.list();
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
    /**
     * 通过机构号查询用户信息
     * @param orgNo			应用号
     * @param session		session
     * @return	机构信息
     * @throws Exception
     */
    public  Map<String, Object> getUserInfoByOrgNo(String orgNo, Session session)throws Exception{
        Query query = session.createQuery("select m from AMS_Member m, AMS_Organization o where o.codeNo=:orgNo and o.member_id=m.id");
        query.setParameter("orgNo", orgNo);
        List list = query.list();
        
        if(list.size() == 0)
            return null;
        else
            return (Map<String, Object>)list.get(0);
    }
	
	/**
	 * 通过UUD查询用户信息
	 * @param loginId	loginId
	 * @param session	session
	 * @return	用户信息
	 * @throws Exception
	 */
	private  Map<String, Object>getUserInfoByLoginId(String loginId, Session session)throws Exception{
		Query query = session.createQuery("from AMS_Member where loginId=:loginId");
		query.setParameter("loginId", loginId);
		List list = query.list();
		
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
	
	
	/**
	 * 通过手机号码获取用户信息
	 * @param phone			手机号
	 * @param belong_orgNo	无用
	 * @param session		session
	 * @return	用户信息
	 */
	private   Map<String, Object> getUserInfoByPhone(String phone, String belong_orgNo, Session session) throws Exception{
		logger.info("########### "+phone+" ###########");
		Query query = session.createQuery("from AMS_Member where phone=:phone");
		query.setParameter("phone", phone);
		List list = query.list();
		
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
	/**
	 *获取会员根据会员号
	 * @param memberNo		会员号
	 * @param belong_OrgNo	无用
	 * @return	会员信息
	 */
	private  Map<String, Object> getUserInfoByMemberNo(String memberNo,String belong_OrgNo,Session session){
		Query query = session.createQuery("from AMS_Member where memberNo=:memberNo");
		query.setParameter("memberNo", memberNo);
		List list = query.list();
		
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
		
	}
	
	/**
	 * 设置最后登录时间
	 * @param member_id		会员ID
	 * @param session		session
	 * @throws Exception
	 */
	public  void setLastlogintime(Long member_id, Session session) throws Exception{
		logger.info("Member.setLastlogintime start");
		Query query = session.createQuery("from AMS_Member where id=:id");
		query.setParameter("id", member_id);
		List list = query.list();
		Map<String, Object> user = (Map<String, Object>)list.get(0);
		
		query = session.createQuery("update AMS_Member set last_login_time=:last_login_time, login_time=:login_time where id=:id");
		query.setParameter("last_login_time", user.get("login_time") == null ? new Date() : (Date)user.get("login_time"));
		query.setParameter("login_time", new Date());
		query.setParameter("id", member_id);
		query.executeUpdate();
	}

	
	public  Map<String, Object> registerUserByPhone(Map<String, Object> param, Session session) throws Exception{
		String phone = (String)param.get("phone");
		if(phone == null)
			throw new Exception("请传入参数 phone");
		String login_password = (String)param.get("login_password");
		if(login_password == null)
			throw new Exception("请传入参数 login_password");
		String pay_password	= (String)param.get("pay_password");
		if(pay_password == null)
			throw new Exception("请传入参数  pay_password");
		String belong_orgNo 	= (String)param.get("belong_orgNo");
		if(belong_orgNo == null || belong_orgNo.equals(""))
			throw new Exception("请传入参数belong_orgNo");
		String register_ip 		= (String)param.get("register_ip");
		if(register_ip == null || register_ip.equals(""))
			throw new Exception("请传入参数register_ip");
		String area 			= (String)param.get("area");
		if(area == null)
			area = "";
		
		if(getUserInfoByPhone(phone, belong_orgNo, session) != null)
			throw new Exception("手机已注册");
		if(!RiskUser.checkRiskUserInfo("mobile", phone)) {
		    throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
		}
		String[] ip_list = register_ip.split(",");
        if(!RiskUser.checkRiskUserInfo("IP", ip_list[0])) {
            throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
        }
		
		Query query = session.createQuery("select b from APA_BranchCompanyArea a, APA_BranchCompany b where b.id=a.company_id and area=:area");
		query.setParameter("area", area);
		List list = query.list();
		logger.info("注册明文："+login_password);
		login_password 	= Password.encode(login_password, "SHA1WithRSA");
		
		logger.info("注册加密："+login_password);
		logger.info("注册加密支付："+pay_password);
		pay_password	= Password.encode(pay_password, "SHA1WithRSA");
		logger.info("注册加密支付："+login_password);
		Date date = new Date();
		Map<String, String> memberMap = new HashMap<String, String>();
		String uuid = UUID.randomUUID().toString();
		memberMap.put("userId", 			uuid);
		memberMap.put("login_password", 	login_password);
		memberMap.put("pay_password",		pay_password);
		memberMap.put("phone", 				phone);
		memberMap.put("user_state", 		"1");
		memberMap.put("member_type", 		String.valueOf(Constant.MEMBER_TYPE_PERSON));
		memberMap.put("register_time", 		String.valueOf(date.getTime()));
		memberMap.put("belong_orgNo", 		belong_orgNo);
		memberMap.put("register_ip", 		register_ip);
		memberMap.put("area", 				area);

		if(list.isEmpty()) {
			memberMap.put("company_name", 	Constant.COMPANY_NAME);
		} else {
			Map<String, Object> entity = (Map<String, Object>)list.get(0);
			memberMap.put("company_id", 	entity.get("id").toString());
			memberMap.put("company_name", 	entity.get("name").toString());
		}
		logger.info(memberMap);
		Map<String, Object> member_entity = DynamicEntityService.createEntity("AMS_Member", memberMap, null);
		
		query = session.createQuery("update AMS_Member set memberNo=:memberNo where id=:member_id");
		query.setParameter("memberNo", getCustomerNo(Constant.MEMBER_TYPE_PERSON,date,session));
		query.setParameter("member_id", (Long)member_entity.get("id"));
		query.executeUpdate();
		
		return member_entity;
	}
	
	
	   public  void registerUser_batch(List<Map<String, Object>> list_param, String belong_orgNo,Session session) throws Exception{
	           
	       for(Map<String, Object> param : list_param){
	       
	        String phone = (String)param.get("phone");
	        if(phone == null)
	            throw new Exception("请传入参数 phone");
	        String login_password = (String)param.get("login_password");
	        if(login_password == null)
	            throw new Exception("请传入参数 login_password");
	        String pay_password = (String)param.get("pay_password");
	        String area = "";
	        
	        if(getUserInfoByPhone(phone, belong_orgNo, session) != null)
	            throw new Exception("手机已注册");
	    
	        Query query = session.createQuery("select b from APA_BranchCompanyArea a, APA_BranchCompany b where b.id=a.company_id and area=:area");
	        query.setParameter("area", area);
	        List list = query.list();
	        
	        login_password  = Password.encode(login_password, "SHA1WithRSA");
	        pay_password    = Password.encode(pay_password, "SHA1WithRSA");
	        Date date = new Date();
	        Map<String, String> memberMap = new HashMap<String, String>();
	        String uuid = UUID.randomUUID().toString();
	        memberMap.put("userId",             uuid);
	        memberMap.put("login_password",     login_password);
	        memberMap.put("pay_password",       pay_password);
	        memberMap.put("phone",              phone);
	        memberMap.put("user_state",         "1");
	        memberMap.put("member_type",        String.valueOf(Constant.MEMBER_TYPE_PERSON));
	        memberMap.put("register_time",      String.valueOf(date.getTime()));
	        memberMap.put("belong_orgNo",       belong_orgNo);
	        memberMap.put("area",               area);

	        if(list.isEmpty()) {
	            memberMap.put("company_name",   Constant.COMPANY_NAME);
	        } else {
	            Map<String, Object> entity = (Map<String, Object>)list.get(0);
	            memberMap.put("company_id",     entity.get("id").toString());
	            memberMap.put("company_name",   entity.get("name").toString());
	        }
	        
	        Map<String, Object> member_entity = DynamicEntityService.createEntity("AMS_Member", memberMap, null);
	        
	        query = session.createQuery("update AMS_Member set memberNo=:memberNo where id=:member_id");
	        query.setParameter("memberNo", getCustomerNo(Constant.MEMBER_TYPE_PERSON,date,session));
	        query.setParameter("member_id", (Long)member_entity.get("id"));
	        query.executeUpdate();
	       }
	    }
	
	
	  /**
     * 企业用户注册
     * @param param		注册信息
     * @param session	session
     * @throws Exception
     */
	   public  Map<String, Object> registerCompanyUserByEmail(Map<String, Object> param, Session session) throws Exception{
	       String email = (String)param.get("email");
	       if(email == null || email.equals(""))
	            throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数email");
	        Long memberType    = (Long)param.get("memberType");
	        if(memberType == null )
	            throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数memberType");
	        String register_ip      = (String)param.get("register_ip");
	        if(register_ip == null || register_ip.equals(""))
	            throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数register_ip");
	        String loginPassword    = (String)param.get("loginPassword");
	        if(loginPassword == null)
	        	throw new  BizException(ErrorCode.PARAM_ERROR,"请传入参数loginPassword");
	        loginPassword = Password.encode(loginPassword, "SHA1WithRSA");
	        String source = (String)param.get("source");
	        Member member = new Member();
	        member.setEmail(email);
	        member.setApplication_id(Constant.APPLICATION_ID_BPS_YUN);
	        //获取用户信息
	        Map<String, Object> member_list=member.getUserInfo();
	        if(member_list.get("id")!=null){
	        	if(Constant.USER_STATE_ACTIVATION.equals(member.getUser_state())){
	        		throw new BizException(ErrorCode.EMAIL_HAS_REGIST,"此邮箱已被注册");
	        	}else if(Constant.USER_STATE_AUDITED_WAIT.equals(member.getUser_state())){
	        		throw new BizException(ErrorCode.EMAIL_HAS_REGIST,"此邮箱正在审核中");
	        	}else if(Constant.USER_STATE_LOCKED.equals(member.getUser_state())){
	        		throw new BizException(ErrorCode.EMAIL_HAS_REGIST,"此邮箱已被注册");
	        	}else{
	        		throw new BizException(ErrorCode.EMAIL_HAS_REGIST,"此邮箱已被注册");
	        	}
	        }
	        String memberNo =  getCustomerNo(Constant.MEMBER_TYPE_ENTERPRISE,new Date(),session);
	        Map<String, Object> member_entity = null;
	        Date date = new Date();
            Map<String, String> memberMap = new HashMap<String, String>();
            String uuid = UUID.randomUUID().toString();
            memberMap.put("userId",             uuid);
            memberMap.put("source",             source);
            memberMap.put("user_state",         String.valueOf(Constant.USER_STATE_AUDITED_WAIT));
            memberMap.put("member_type",        String.valueOf(Constant.MEMBER_TYPE_ENTERPRISE));
            memberMap.put("register_time",      String.valueOf(date.getTime()));
            memberMap.put("register_ip",        register_ip);
//          memberMap.put("area",               area);
            memberMap.put("email",              email);
            memberMap.put("login_password", loginPassword);
            memberMap.put("biz_user_id", email);//企业注册的biz_user_id为邮箱号
            memberMap.put("application_id", Constant.APPLICATION_ID_BPS_YUN.toString());//企业注册的biz_user_id为邮箱号
            memberMap.put("application_label", Constant.APPLICATION_LABEL_BPS_YUN);//企业注册的biz_user_id为邮箱号
            memberMap.put("memberNo",           memberNo);
                
                
            member_entity = DynamicEntityService.createEntity("AMS_Member", memberMap, null);
	        return member_entity;
	    }
	

	   /**
	     * 企业用户注册第二步
	     * @param param		注册信息
	     * @param session	session
	     * @throws Exception
	     */
	       public  Long registerUserByEmailStep2(Map<String, Object> param, Session session) throws Exception{
	           Long memberId = (Long)param.get("memberId");
	           if(memberId == null || memberId.equals(""))
	                throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数memberId");
	            String pwd    = (String)param.get("pwd");
	            if(pwd == null || pwd.equals(""))
	                throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数pwd");
	            String pay_pwd      = (String)param.get("pay_pwd");
	            if(pay_pwd == null || pay_pwd.equals(""))
	                throw new BizException(ErrorCode.PARAM_ERROR,"请传入参数pay_pwd");

	            pwd  = Password.encode(pwd, "SHA1WithRSA");
	            pay_pwd = Password.encode(pay_pwd, "SHA1WithRSA");
	            
	            Query query = session.createQuery("update AMS_Member set login_password=:pwd, pay_password=:pay_pwd where id=:memberId");
	            query.setParameter("pwd",  pwd);
	            query.setParameter("pay_pwd", pay_pwd);
	            query.setParameter("memberId", memberId);
	            query.executeUpdate();
	            return memberId;
	        }
	       
	/**
	 * 查询安全问题
	 * @param session	session
	 * @throws Exception
	 */
	public  List findSafetyProblem( Session session) throws Exception{
		Query query = session.createQuery("from AMS_SecurityIssue where member_id=:member_id order by id");
		query.setParameter("member_id", this.getId());
		return query.list();
	}
	
	/**
     * 查询银行卡id通过bankcode
     * @param bank_code		bank_code
     * @throws Exception
     */
    public  Map<String, Object>  getBankCardIdByBankCode(String bank_code, Session session) throws Exception{
        Query query = session.createQuery("from AMS_BankCode where bank_code =:bank_code");
        query.setParameter("bank_code", bank_code);
        List list = query.list();
        Map<String, Object> map = null;
        if(list !=null && list.size()>0){
            map = (Map<String, Object>) list.get(0);
        }
        return map;
    }
    

	/**
	 * 查询银行卡
	 * @param cardKind		cardKind
	 * @throws Exception
	 */
	public  List<Map<String, Object>> getBankCardList(Long cardKind, Session session) throws Exception{
		logger.info("Member.getBankCardList start");
		logger.info("id="+this.getId());
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if(cardKind.equals(Constant.BUSINESS_TYPE_BANK_CARD)){
			Query query = session.createQuery("from AMS_MemberBank mb, AMS_Bank b where mb.member_id=:member_id and mb.bind_state=:bind_state and mb.bank_code=b.bank_code and mb.business_card_type=:business_card_type order by mb.id desc");
			query.setParameter("member_id", this.getId());
			query.setParameter("bind_state",Constant.MEMBER_BANK_STATUS_SUCCESS);
			query.setParameter("business_card_type",cardKind);
			List<Map<String, Object>> list = query.list();
			if(!list.isEmpty()) {
			    for(Object obj : list) {
			        Object[] objs = (Object[])obj;
			        Map<String, Object> member_bank = (Map<String, Object>) objs[0];
			        String enBankCardNo = (String)member_bank.get("accountNo_encrypt");
			        String bankCardNo = TripleDES.decrypt(this.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, enBankCardNo);
			        member_bank.put("bankCardNo", bankCardNo);
	                Map<String, Object> bank = (Map<String, Object>) objs[1];
	                member_bank.put("logo", bank.get("logo"));
			        result.add(member_bank);
			    }
			}
		}else if(cardKind.equals(Constant.BUSINESS_TYPE_ALLINPAY_CARD)){
			Query query = session.createQuery("from AMS_MemberBank  where member_id=:member_id and bind_state=:bind_state and business_card_type=:business_card_type order by id desc");
			query.setParameter("member_id", this.getId());
			query.setParameter("bind_state",Constant.MEMBER_BANK_STATUS_SUCCESS);
			query.setParameter("business_card_type",cardKind);
			List<Map<String, Object>> list = query.list();
			if(!list.isEmpty()) {
				result = list;
			}
		}
		return result;
	}
	
	/**
	 * 获取已解绑卡列表
	 * @return List<Map<String, Object>>
	 * @throws BizException
	 */
	public List<Map<String, Object>> getUnbindedBankCardList(
			Long cardKind, Session session) throws BizException {
		try{
			Query query = session.createQuery("from AMS_MemberBank mb, AMS_Bank b where mb.member_id=:member_id and mb.bind_state=:bind_state and mb.bank_code=b.bank_code and mb.business_card_type=:business_card_type order by mb.id desc");
			query.setParameter("member_id", this.getId());
			query.setParameter("bind_state",Constant.MEMBER_BANK_STATUS_ERROR);
			query.setParameter("business_card_type",cardKind);
			List<Map<String, Object>> list = query.list();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			if(!list.isEmpty()) {
			    for(Object obj : list) {
			        Object[] objs = (Object[])obj;
			        Map<String, Object> member_bank = (Map<String, Object>) objs[0];
	                Map<String, Object> bank = (Map<String, Object>) objs[1];
	                member_bank.put("logo", bank.get("logo"));
			        result.add(member_bank);
			    }
			}
			
			return result;
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 查询银行卡是否绑定
	 * @param accountNo		卡号
	 * @param session		session
	 * @throws Exception
	 */
	public List checkBankCard(String accountNo, Session session) throws Exception{
		String en_accountNo = TripleDES.encrypt(this.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, accountNo);
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		sb.append("from AMS_MemberBank where accountNo_encrypt=:accountNo_encrypt and bind_state=:bind_state");
		Query query = session.createQuery(sb.toString());
		query.setParameter("accountNo_encrypt", en_accountNo);
		query.setParameter("bind_state", 		Constant.MEMBER_BANK_STATUS_SUCCESS);
		return query.list();
	}
	
	/**
     * 设置扩展信息
     * @param member_id		会员ID
     * @param extend_info	扩展信息
     * @param session		session
     */
    public  void setExtendInfo(Long member_id, Map<String, Object>extend_info, Session session) throws Exception{
        logger.info("Member setExtend_info start");
        logger.info("member_id:" + member_id + " extend_info:" + extend_info);
        
        Map<String, Object> old_ExtendInfo = getUserExtendInfo(member_id, session);
        Query query = session.createQuery("update AMS_MemberExtendInfo set property_value=:property_value " +
                "where member_id=:member_id and property_name=:property_name");
        
        Map<String, Object>member_entity = DynamicEntityService.getEntity(member_id, "AMS_Member");
        for(Map.Entry<String, Object> entry : extend_info.entrySet()){
            String name     = entry.getKey();
            String value    = (String)entry.getValue();
            
            if(old_ExtendInfo.get(name) != null){   //更新
                query.setParameter("member_id", member_id);
                query.setParameter("property_name", name);
                query.setParameter( "property_value", value);
                
                query.executeUpdate();
            }else{  //创建
                Map<String, String> newMap = new HashMap<String, String>();
                newMap.put("member_id", member_id.toString());
                newMap.put("member_label", member_entity.get("userId").toString());
                newMap.put("property_name", name);
                newMap.put("property_value", value);
                
                DynamicEntityService.createEntity("AMS_MemberExtendInfo", newMap, null);
            }
        }
    }

    /**
     * 获取扩展信息
     * @param member_id
     * @param session
     * @return
     */
    public  Map<String, Object> getUserExtendInfo(Long member_id, Session session){
        logger.info("Member getUserExtendInfo start");
        logger.info("member_id:" + member_id);
        Query query = session.createQuery("from AMS_MemberExtendInfo where member_id=:member_id");
        query.setParameter("member_id", member_id);
        
        List list = query.list();
        
        Map<String, Object> extendInfoMap = new HashMap<String, Object>();
        for(int i=0; i<list.size(); i++){
            Map<String, Object> map = (Map<String, Object>)list.get(i);
            
            extendInfoMap.put((String)map.get("property_name"), map.get("property_value"));
        }
        return extendInfoMap;
    }

    /**
     * 审核失败
     * @param params		企业信息
     * @param session		session
     */
    public void userAuditedFail(Map<String, Object> params, Session session) throws Exception{
        logger.info("Member userAuditedFail start");
        logger.info("params:" + params);

        try {
        	 Query query = session.createQuery("update AMS_MemberEnterprise set check_state=:check_state, fail_reason=:fail_reason,remark=:remark,check_time=:check_time where id=:id");
             query.setParameter("check_state", Constant.ENTERPRISE_CHECK_STATE_AUDITED_FAIL);
             query.setParameter("fail_reason", (String)params.get("fail_reason"));
             query.setParameter("remark", (String)params.get("remark"));
             query.setParameter("check_time", new Date());
             query.setParameter("id", (Long)params.get("memberEnterprise_id"));
             query.executeUpdate();
//            Query query = session.createQuery("update AMS_Member set user_state=:user_state, remark=:reason where id=:id");
//            query.setParameter("user_state", (Long)member_entity.get("user_state"));
//            query.setParameter("reason", (String)member_entity.get("reason"));
//            query.setParameter("id", (Long)member_entity.get("id"));
//            query.executeUpdate();
//            Map<String, Object> param = new HashMap<String, Object>();
//            param.put("email", member_entity.get("email"));
//            param.put("verification_code_type", Constant.EMAIL_CODE_TYPE_USER_AUDITED_WAIT);
//            param.put("member_id", member_entity.get("id"));
//            param.put("reason", member_entity.get("remark"));
//            
//            Map<String, String> email_resolve;
//            email_resolve = VerificationCode.textResolve(param);
//            String html = email_resolve.get("html");
//            String subject = email_resolve.get("theme");
            //SendMail sm  = new SendMail((String)member_entity.get("email"), subject, html);
            //sm.start();
			Member member = new Member((Long)params.get("member_id"));
			String callback_url = params.get("callback_url") == null?"":(String)params.get("callback_url");
			if (!"".equals(callback_url.trim())) {
				params.put("check_time", sdf.format(new Date()));
				params.put("callback_url", callback_url);
				params.put("en_accountNo", biz_user_id);
				params.put("appid",member.getApplicationId());
				params.put("check_state",Constant.ENTERPRISE_CHECK_STATE_AUDITED_FAIL);

				sendMessage(false, params);
			}
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        
        logger.info("Member userAuditedFail end");
    }

    /**
     * 企业审核成功
     * @param params		企业信息
     * @param session		session
     */
    public void userAuditedSuccess(Map<String, Object> params, Session session) throws Exception{
        logger.info("Member userAuditedSuccess start");
        logger.info("params:" + params);
        try {      	
        	Query query = session.createQuery("update AMS_MemberEnterprise set check_state=:check_state, remark=:remark,check_time=:check_time where id=:id");
            query.setParameter("check_state", Constant.ENTERPRISE_CHECK_STATE_AUDITED_SUCCESS);
            query.setParameter("remark", params.get("remark"));
            query.setParameter("check_time", new Date());
            query.setParameter("id", (Long)params.get("memberEnterprise_id"));
            query.executeUpdate();
        	
            query = session.createQuery("update AMS_Member set user_state=:user_state where id=:id ");
            query.setParameter("user_state", Constant.USER_STATE_ACTIVATION);
//            query.setParameter("isIdentityChecked", true);
            query.setParameter("id", (Long)params.get("member_id"));
            query.executeUpdate();
            
            //绑定银行卡(对公账户）
            Member member = new Member((Long)params.get("member_id"));
            String accountNo = (String)params.get("accountNo");
            String bankName = (String)params.get("bankName");
            
            String bank_code = (String)params.get("bankCode");
//            Map<String, Object> CardBinMap = new MemberServiceImpl().getCardBin(accountNo.substring(0,6));
//            if(CardBinMap != null && CardBinMap.get("bank_code") != null){
//                bank_code = (String) CardBinMap.get("bank_code");
//            }
            
            String identity_cardNo_encrypt = TripleDES.encrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, (String)params.get("legal_ids"));
            Map<String, String> bankCardMap = new HashMap<>();
            bankCardMap.put("bank_name",            bankName);//银行名称
            bankCardMap.put("identity_cardNo",      identity_cardNo_encrypt);//身份ID
            bankCardMap.put("account_name",         (String)params.get("name"));//开户名
            bankCardMap.put("bank_code",            bank_code);//银行代码
            bankCardMap.put("card_type",            Constant.BANK_CARD_CX.toString());//卡类型
            bankCardMap.put("account_prop",         "1");//           
            bankCardMap.put("phone",                (String)params.get("legalPhone"));//手机号码 法人的
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
            
            DynamicEntityService.createEntity("AMS_MemberBank", bankCardMap, null);
            
//            Map<String, Object> param = new HashMap<String, Object>();
//            param.put("email", member_entity.get("email"));
//            param.put("verification_code_type", Constant.EMAIL_CODE_TYPE_USER_AUDITED_SUCCESS);
//            param.put("member_id", member_entity.get("id"));
//            
//            Map<String, String> email_resolve;
//            email_resolve = VerificationCode.textResolve(param);
//            String html = email_resolve.get("html");
//            String subject = email_resolve.get("theme");
            //SendMail sm  = new SendMail((String)member_entity.get("email"), subject, html);
            //sm.start();
			String callback_url = params.get("callback_url") == null?"":(String)params.get("callback_url");
			if (!"".equals(callback_url.trim())) {
				params.put("check_time", sdf.format(new Date()));
				params.put("callback_url", callback_url);
				params.put("en_accountNo", biz_user_id);
				params.put("appid",member.getApplicationId());
				params.put("check_state",Constant.ENTERPRISE_CHECK_STATE_AUDITED_SUCCESS);
				sendMessage(true, params);
			}
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        
        logger.info("Member userAuditedSuccess end");
    }
    
    public  List getMemberBankCardList(Long member_id, String interfaceNo, Session session) throws Exception{
		Query query = session.createQuery("from AMS_MemberBank mb, AMS_Bank b,AMS_PayInterfaceBank c where mb.member_id=:member_id and mb.bind_state=:bind_state and mb.bank_code=b.bank_code and b.id=c.bank_id and c.pay_interfaceNo=:pay_interfaceNo order by mb.id desc");
		query.setParameter("member_id", member_id);
		query.setParameter("bind_state",Constant.MEMBER_BANK_STATUS_SUCCESS);
		query.setParameter("pay_interfaceNo", interfaceNo);
		List list = query.list();
		List result = new ArrayList();
		if(!list.isEmpty()) {
		    for(Object obj : list) {
		        Object[] objs = (Object[])obj;
		        Map<String, Object> member_bank = (Map<String, Object>) objs[0];
                Map<String, Object> bank = (Map<String, Object>) objs[1];
                Map<String, Object> interfaceBank = (Map<String, Object>) objs[2];
                member_bank.put("logo", bank.get("logo"));
                member_bank.put("is_sms_verify", interfaceBank.get("is_sms_verify"));
		        result.add(member_bank);
		    }
		}
		return result;
	}
    /**
     * 查询登陆日志
     * @param memberId	会员ID
     * @param pageNo	页码
     * @param pageSize	每页数量
     * @return
     */
    public List<Map<String, Object>> getLoginLog(Long memberId, Long pageNo, Long pageSize, Session session) throws Exception {
        Long first = (pageNo - 1) * pageSize;
        
        Query query = session.createQuery("select ll from AMS_LoginLog ll, AMS_Member m where m.id=ll.member_id and m.id=:memberId order by ll.login_date desc");
        query.setParameter("memberId", memberId);
        query.setFirstResult(first.intValue());
        query.setMaxResults(pageSize.intValue());
        return query.list();
    }

    /**
     * 查询登陆日志总数
     * @param memberId	会员ID
     * @return	总记录数
     */
    public Long getLoginLogCount(Long memberId, Session session) throws Exception {
        
        Query query = session.createQuery("select count(ll.id) from AMS_LoginLog ll, AMS_Member m where m.id=ll.member_id and m.id=:memberId order by ll.login_date desc");
        query.setParameter("memberId", memberId);
        List list = query.list();
        return (Long) list.get(0);
    }

	/**
	 * 查询通联卡
	 * @throws Exception
	 */
	public List getAllinpayCardList(Session session) throws Exception{
		Query query = session.createQuery("from AMS_MemberBank where member_id=:member_id and bind_state=:bind_state and business_card_type=:business_card_type order by id desc");
		query.setParameter("member_id", this.getId());
		query.setParameter("bind_state",Constant.MEMBER_BANK_STATUS_SUCCESS);
		query.setParameter("business_card_type",Constant.BUSINESS_TYPE_ALLINPAY_CARD);
		List list = query.list();
		return list;
	}
	
	/**
     * 加入黑名单
     */
    public void joinBlackList(Session session) throws Exception{
		logger.info("Member.isinsertblackList start");
		Boolean	flag = false;
		try{
			Member member = new Member(id);
			logger.info("member_bank"+member);
			String userId = member.getUserId();
			String phone = member.getPhone();
			String identity_cardNo_encrypt = member.getIdentity_cardNo_encrypt();
			String identity_cardNo = TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, identity_cardNo_encrypt);
			Map<String,Object> map = getUserdetail(session);
			List<String> banklist =(List<String>)map.get("banklist");
			if(!isinsertblacklist(userId,session)){
				logger.info("创建黑名单实体");
				for(String bankNo :banklist ){
					Map<String,String> entity = new HashMap<String,String> ();
				    entity.put("userId", userId);
				    entity.put("mobile", phone);
				    entity.put("certificateID", identity_cardNo);
				    entity.put("bankCardNo", bankNo);
				    DynamicEntityService.createEntity("AMS_RiskUserList", entity, null);
				}
				setisRiskUser(this.getUserId(),session,true);
			}
			    
		}catch(Exception e){
		    logger.info(e.getMessage(),e);
		    throw e;
		}finally{
		    logger.info("Member.isinsertblackList end");
		}
    }
    
    /**
	  * 只对方银行卡加入黑名单
     * @throws Exception 
	  */
    public Boolean bankCardinBlcak(String bankcard,Session session) throws Exception{
		 logger.info("Member.bankcardinblcak start");
	     Boolean	flag = false;
	     try{
		     if(!isbankcardinblcak(bankcard,session)){
		    	 logger.info("创建黑名单实体");
		    	 Map<String,String> entity = new HashMap<String,String> ();
		    	 entity.put("bankCardNo", bankcard);
	             Map<String, Object> temp = DynamicEntityService.createEntity("AMS_RiskUserList", entity, null);
	             flag = true;
		     }
	     }catch(Exception e){
	            logger.info(e.getMessage(),e);
	            throw e;
	     }finally{
            logger.info("Member.bankcardinblcak end");
	     }
	     return flag;
    	
    }
    
    /**
     * 获取会员详细（省份证明码和用户绑定的银行卡）加入黑名单
     * @throws Exception
     */
    public Map<String,Object> getUserdetail(Session session) throws Exception{
        logger.info("Member.getUserdetail start");
        try{
            Map<String,Object> deatil = new HashMap<String, Object>();
            String userId = this.getUserId();
            String identity_cardNo_encrypt = this.getIdentity_cardNo_encrypt();
            String identity_cardNo = TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, identity_cardNo_encrypt);
            deatil.put("identity_cardNo", identity_cardNo);
            List<Map<String,Object>> banklistold = this.getBankCardList(Constant.BUSINESS_TYPE_BANK_CARD, session);
            List banklist = new ArrayList();
            logger.info("banklistold"+banklistold);
            if(!banklistold.isEmpty()){
            	for (Map<String,Object> bank_mp : banklistold){
                	String accountNo_encrypt = (String)bank_mp.get("accountNo_encrypt");
                    String accountNo = TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, accountNo_encrypt);
                    banklist.add(accountNo);
                }
            }
            deatil.put("banklist", banklist);
            logger.info("deatil:"+deatil);
            return deatil;
        }catch(Exception e){
            logger.info(e.getMessage(),e);
            throw e;
        }finally{
            logger.info("Member.getUserdetail end");
        }
    }
    
    
	/**
     * 查看是否加入过黑名单
     * @param userId
	 * @throws Exception 
     */
	public Boolean isinsertblacklist(String userId,Session session) throws Exception{
	
		Boolean flag =true;
		logger.info("MemberService.isinsertblacklist start");
		try{
		
			Query query = session.createQuery("from AMS_RiskUserList where userId=:userId");
			query.setParameter("userId", userId);
			List<Map<String, Object>> list = query.list();
			logger.info("黑名单长度"+list.size());
			if(list.size() == 0)
				return false;
		   else
	    	 return flag; 
		}catch(Exception e){
			
            logger.info(e.getMessage(),e);
            throw e;
    	}finally{
    		
            logger.info("MemberService.isinsertblacklist end");
        }
	}
	/**
	 * 银行卡是否加入黑名单
	 */
	public Boolean isbankcardinblcak(String bankCardNo,Session session) throws Exception{
		
		Boolean flag =true;
		logger.info("MemberService.isbankcardinblcak start");
		try{
		
			Query query = session.createQuery("from AMS_RiskUserList where bankCardNo=:bankCardNo");
			query.setParameter("bankCardNo", bankCardNo);
			List<Map<String, Object>> list = query.list();
			logger.info("黑名单长度"+list.size());
			if(list.size() == 0)
				return false;
		   else
	    	 return flag; 
		}catch(Exception e){
			
            logger.info(e.getMessage(),e);
            throw e;
    	}finally{
    		
            logger.info("MemberService.isbankcardinblcak end");
        }
	}
	
	 /**
	  * 修改会员表是否加入黑名单状态
	  * @param userId		会员ID
	  * @param isRiskUser	isRiskUser
      * @throws Exception
      */
	public static void setisRiskUser(String userId, Session session, Boolean isRiskUser) throws Exception{
		try{
		logger.info("MemberService.setisRiskUser start");
		if(userId == null || userId.equals("")) {
			throw new Exception("请传入用户id"+userId);
		}
			Query query = session.createQuery("update AMS_Member set isRiskUser=:isRiskUser where userId=:userId");
			query.setParameter("userId", userId);
			query.setParameter("isRiskUser", isRiskUser);
			query.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}finally{
            logger.info("MemberService.setisRiskUser end");
        }
	}
	
	/**
     * 生成客户号
     * @param id
     * @return
     * 第1位：会员类型
     * 第2-7位：注册日期，YYMMDD
     * 第8-14位：顺序号，当日支持最多注册生一千万用户
     * 第15 位：校验码，自动随机生成
     */
    public static String getCustomerNo(Long id, Date date, Session session) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String customerNo = "";
        
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        DecimalFormat dft = new DecimalFormat("0000000");
        String random  = Util.getRandom();
        Long CUSTOMERNO = 0L;
        Map<String, Object> map = getNewMember(sdf.format(date), session);
        logger.info("map:"+map);
        if(map == null || map.isEmpty()){
            customerNo += id + df.format(date) + dft.format(CUSTOMERNO) + random;
        }else{
            String memberNo = (String) map.get("memberNo");
            memberNo = memberNo.substring(7, 13);
            logger.info("memberNo" + memberNo);
            CUSTOMERNO = Long.valueOf(memberNo) + 1;
            customerNo += id + df.format(date) + dft.format(CUSTOMERNO) + random;
        }
        return customerNo;
    }
    
    /**
	 * 根据时间查询最新注册的会员
	 * @param date		注册时间
	 * @param session	session
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> getNewMember(String date, Session session)throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Query query = session.createQuery("from AMS_Member where register_time>=:startDate and register_time<=:endDate and memberNo>0 order by register_time desc");
		Date startDate = sdf.parse(date+" 00:00:00");
		Date endDate = sdf.parse(date+" 23:59:59");
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setMaxResults(1);
		List list = query.list();
		
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
	
	/**
	 * 手续费操作。（银行卡绑卡或者实名认证）
	 * @param memberEntity		会员实体
	 * @param orgFeeEntity		手续费
	 * @param feeType			手续类型
	 * @param session			session
	 * @throws Exception
	 */
	public static void feeOpe(Map<String, Object> memberEntity, Map<String, Object> orgFeeEntity, Long feeType, Session session) throws Exception{
		logger.info("=======================Member.feeOpe start===========================");
		logger.info("参数memberEntity=" + memberEntity + ",feeType=" + feeType);
		
		Long fee = (Long)orgFeeEntity.get("handling_each");
		Long handlingMode = (Long)orgFeeEntity.get("handling_mode");
		
		if(fee == 0){
			return;
		}
		
		try{
			//写入AMS_HandlingLog表
			Map<String,Object> yunApplicationEntity = DynamicEntityService.getEntity(Constant.APPLICATION_ID_BPS_YUN, "AMS_Organization");
            			
			Map<String, String> entity = new HashMap<String, String>();
            entity.put("memberNo",                  (String)memberEntity.get("memberNo"));
            entity.put("member_name",               memberEntity.get("name") == null ? "" : (String)memberEntity.get("name"));
            entity.put("member_userId",             (String)memberEntity.get("userId"));
            entity.put("trade_time",                String.valueOf(new Date().getTime()));
            entity.put("trade_money",               "0");
            entity.put("handling_fee",              fee.toString());
            entity.put("orderNo",                   null);
            entity.put("handling_mode",             String.valueOf(handlingMode));
            entity.put("is_charge",                 "false");
            entity.put("orgNo",                     (String)yunApplicationEntity.get("codeNo"));
            Map<String,Object> handlingLogEntity = DynamicEntityService.createEntity("AMS_HandlingLog", entity, null);
			
			//自定义转账
            Map<String,Object> targetMemberEntity = DynamicEntityService.getEntity((Long)yunApplicationEntity.get("member_id"), "AMS_Member");
            Map<String,Object> source_accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_READY);
            Map<String,Object> target_accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
            Map<String, Object> setRnOrBindBcParam = getSetRnOrBindBcParam(feeType);
            logger.info("setRnOrBindBcParam=" + setRnOrBindBcParam);
            String bizid = CodeUtil.getCode((String)setRnOrBindBcParam.get("prefix"), (Long)setRnOrBindBcParam.get("codeType"), Integer.parseInt((String)setRnOrBindBcParam.get("codeLength")));
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("source_userId",      (String)memberEntity.get("userId"));
            param.put("source_memberNo",    (String)memberEntity.get("memberNo"));
            param.put("source_member_name", (String)memberEntity.get("name"));
            param.put("target_userId",      (String)targetMemberEntity.get("userId"));
            param.put("target_memberNo",    (String)targetMemberEntity.get("memberNo"));
            param.put("target_member_name", (String)targetMemberEntity.get("name"));
            param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
            param.put("sub_trade_type",     Constant.SUB_TRADE_TYPE_ORG_FEE);
            param.put("trade_money",        fee);
            param.put("bizid",              bizid);
            param.put("remark",             setRnOrBindBcParam.get("remark"));
            param.put("account_type_id",    (Long)source_accountType.get("id"));
            param.put("target_account_type_id",    (Long)target_accountType.get("id"));
            param.put("isMaster",           true);
            param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
            param.put("orgNo",    			(String)yunApplicationEntity.get("codeNo"));
            String tradeId = TradeService.customTransfer(param);
            
            //更新AMS_HandlingLog表
            Query query = session.createQuery("update AMS_HandlingLog set is_charge=true,trade_id=:trade_id where id=:id");
            query.setParameter("trade_id", tradeId);
            query.setParameter("id", handlingLogEntity.get("id"));
            query.executeUpdate();
            
            logger.info("=======================Member.feeOpe end===========================");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 检查准备金账户是否足够扣除手续费。（实名认证或者绑定银行卡）
	 * @param orgNo
	 * @param userId
	 * @param feeType
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> checkFee(String orgNo, String userId, Long feeType ) throws Exception{
		logger.info("=======================Member.checkFee start===========================");
		logger.info("参数orgNo=" + orgNo + ",useId=" + userId + ",feeType=" + feeType);
			
		final String _orgNo = orgNo;
		final String _userId = userId;
		final Long _feeType = feeType;
		
		try{
			Map<String, Object> ret = EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					String queryStr = "from AMS_OrgFee where orgNo = :orgNo and fee_type = :feeType";
					Query query = session.createQuery(queryStr);
					query.setParameter("orgNo", _orgNo);
					query.setParameter("feeType", _feeType);
					List<Map<String, Object>> orgFeeEntityList = query.list();
					logger.info("orgFeeEntityList=" + orgFeeEntityList);
					//不存在配置
					if(orgFeeEntityList.isEmpty()){
						return null;
					}
					
					Map<String, Object> orgFeeEntity = (Map<String, Object>)orgFeeEntityList.get(0);
					
					if(orgFeeEntity == null || orgFeeEntity.isEmpty()){
						throw new Exception("不存在相应的手续费配置信息。");
					}
					Long fee = (Long)orgFeeEntity.get("handling_each");
					
					if(fee == 0){
						return orgFeeEntity;
					}
					
					//判断账户的金额是否足够
					AccountServiceImpl accountServiceImpl = new AccountServiceImpl();
					Map<String, Object> accountTypeEntity = accountServiceImpl.getAccountTypeByNo(Constant.ACCOUNT_NO_STANDARD_READY);
					Long accountTypeId = (Long)accountTypeEntity.get("id");
					
					String queryMemberAccountStr = "from AMS_MemberAccount where userId = :userId and account_type_id = :accountTypeId and account_state = :accountState";
					Query queryMemberAccount = session.createQuery(queryMemberAccountStr);
					queryMemberAccount.setParameter("userId", _userId.toString());
					queryMemberAccount.setParameter("accountTypeId", accountTypeId);
					queryMemberAccount.setParameter("accountState", Constant.ACCOUNT_STATE_VAILD);
					
					logger.info("userId = " + _userId.toString() + ",accountTypeId=" + accountTypeId + ",accountState=" + Constant.ACCOUNT_STATE_VAILD);
					
					logger.info("queryMemberAccount=" + queryMemberAccount.getQueryString());
					
					List<Map<String, Object>> memberAccountEntityList = queryMemberAccount.list();
					if(memberAccountEntityList == null || memberAccountEntityList.isEmpty()){
						throw new Exception("用户账户不存在。");
					}
					
					Map<String, Object> memberAccountEntity = (Map<String, Object>)memberAccountEntityList.get(0);
					if(memberAccountEntity == null || memberAccountEntity.isEmpty()){
						throw new Exception("用户账户不存在。");
					}
					if((Long)memberAccountEntity.get("account_state") != Constant.ACCOUNT_STATE_VAILD){
						throw new Exception("用户账户不可用。");
					}
					Long amount = (Long)memberAccountEntity.get("amount");
					Long freezeAmount = (Long)memberAccountEntity.get("freeze_amount");
					Long avaliableAmount = amount - freezeAmount;
					
					if(avaliableAmount < fee){
						throw new Exception("准备金金额不足。");
					}
					
					return orgFeeEntity;
				}
			});
			
			logger.info("=======================Member.checkFee end===========================");
			logger.info("返回ret=" + ret);
			return ret;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 获取实名认证或者绑定银行卡的参数
	 * @param feeType
	 * @return
	 */
	private static Map<String, Object> getSetRnOrBindBcParam(Long feeType){
		Map<String, Object> ret = new HashMap<String, Object>();
		
		//实名认证
		if(Constant.FEE_TYPE_SET_REAL_NAME.equals(feeType)){
			ret.put("prefix", "SM");
			ret.put("codeType", Constant.CODE_TYPE_SET_REAL_NAME);
			ret.put("codeLength", String.valueOf((Constant.CODE_LENGTH_SET_REAL_NAME)));
			ret.put("remark", "实名认证手续费");
		}
		//绑定银行卡
		else{
			ret.put("prefix", "BY");
			ret.put("codeType", Constant.CODE_TYPE_BIND_BANK_CARD);
			ret.put("codeLength", String.valueOf(Constant.CODE_LENGTH_BIND_BAND_CARD));
			ret.put("remark", "绑定银行卡手续费");
		}
		
		return ret;
	}

	/**
	 * 转成云帐户系统证件类型
	 * @param interfaceNo	通道编码
	 * @param _id_type		卡类型
     * @return Long 云帐户系统证件类型
	 * 				1	身份证
	 *				2	军官证
	 *				3	护照
	 *				4	回乡证
	 *				5	台胞证
	 *				6	警官证
	 *				7	士兵证
	 *				99	其它证件
     */
	public static Long getYunIdType(String interfaceNo, String _id_type){
		//0：身份证,1: 户口簿，2：护照,3.军官证,4.士兵证，5. 港澳居民来往内地通行证,6. 台湾同胞来往内地通行证,7. 临时身份证,8. 外国人居留证,9. 警官证, X.其他证件
//		01	身份证
//		02	军官证
//		03	护照
//		04	回乡证
//		05	台胞证
//		06	警官证
//		07	士兵证
//		99	其它证件
		if (interfaceNo.equals(Constant.PAY_INTERFACE_QUICK_CARD)){
			switch (_id_type){
				case "01":
					return 1L;
				case "02":
					return 3L;
				case "03":
					return 2L;
				case "04":
					return 4L;
				case "05":
					return 5L;
				case "06":
					return 6L;
				case "07":
					return 7L;
				default:
					return 99L;
			}
		}else if(interfaceNo.equals(Constant.PAY_INTERFACE_TLT_ACCOUNT)){
			switch (_id_type){
				case "0":
					return 1L;
				case "2":
					return 3L;
				case "3":
					return 2L;
				case "4":
					return 7L;
				case "5":
					return 4L;
				case "6":
					return 5L;
				case "9":
					return 6L;
				default:
					return 99L;
			}
		}
		return 0L;
	}
	/**
	 * 转成云帐户系统证件类型
	 * @param interfaceNo	通道编码
	 * @param _id_type		卡类型
	 * @return Long 云帐户系统证件类型
	 * 				1	身份证
	 *				2	军官证
	 *				3	护照
	 *				4	回乡证
	 *				5	台胞证Long
	 *				6	警官证
	 *				7	士兵证
	 *				99	其它证件
	 */
	public static String getIdType(String interfaceNo, Long _id_type){
		//0：身份证,1: 户口簿，2：护照,3.军官证,4.士兵证，5. 港澳居民来往内地通行证,6. 台湾同胞来往内地通行证,7. 临时身份证,8. 外国人居留证,9. 警官证, X.其他证件
//		01	身份证
//		02	军官证
//		03	护照
//		04	回乡证
//		05	台胞证
//		06	警官证
//		07	士兵证
//		99	其它证件
		if (interfaceNo.equals(Constant.PAY_INTERFACE_QUICK_CARD)){
			if (_id_type.equals(1L)) {
				return "01";
			} else if (_id_type.equals(2L)) {
				return "03";
			} else if (_id_type.equals(3L)) {
				return "02";
			} else if (_id_type.equals(4L)) {
				return "04";
			} else if (_id_type.equals(5L)) {
				return "05";
			} else if (_id_type.equals(6L)) {
				return "06";
			} else if (_id_type.equals(7L)) {
				return "07";
			} else {
				return "99";
			}
		}else if(interfaceNo.equals(Constant.PAY_INTERFACE_TLT_ACCOUNT)){
			if (_id_type.equals(1L)) {
				return "0";
			} else if (_id_type.equals(3L)) {
				return "2";
			} else if (_id_type.equals(2L)) {
				return "3";
			} else if (_id_type.equals(7L)) {
				return "4";
			} else if (_id_type.equals(4L)) {
				return "5";
			} else if (_id_type.equals(5L)) {
				return "6";
			} else if (_id_type.equals(6L)) {
				return "9";
			} else {
				return "X";
			}
		}
		return "";
	}

	/**
	 * 通过云帐户卡类型传成对应通道的卡类型（目前支持通联通）
	 * @param interfaceNo		通道号
	 * @param card_type			卡类型
	 * @return	通道卡类型
	 * @throws Exception
     */
	public static String getCardType(String interfaceNo, Long card_type)throws Exception{
		if(interfaceNo.equals(Constant.PAY_INTERFACE_TLT_ACCOUNT)){
			if(card_type.equals(1L)){
				return "00";
			}else if (card_type.equals(2L)){
				return "02";
			}
		}
		throw new Exception("不支持的卡类型！");
	}
	public static void chickBindBankCardID(Map<String, String> params, Member member)throws Exception{
		//验证传入的身份真和姓名是否与实名记录一致
		if(Constant.MEMBER_TYPE_PERSON.equals(member.getMember_type())){
			if(!params.get("accountName").equals(member.getName()))
				throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
			//身份证最后一位是X时验证：把x转成大小写来验证，二者过一个就可以。
			if(params.get("ID_NO")!=null){
				String id_no = params.get("ID_NO");
				String identity_cardNo_md5 = Password.encode(id_no, "MD5");

				String id_no_x = "";
				if(id_no.contains("x")){
					id_no_x = id_no.toUpperCase();
				}else if(id_no.contains("X")){
					id_no_x = id_no.toLowerCase();
				}
				if( !id_no_x.equals("")){
					id_no_x = Password.encode(id_no_x, "MD5");
					if(!identity_cardNo_md5.equals(member.getIdentity_cardNo_md5())){
						if(!id_no_x.equals(member.getIdentity_cardNo_md5())){
							throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
						}
					}
				}else{
					if(!identity_cardNo_md5.equals(member.getIdentity_cardNo_md5())){
						throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
					}
				}
			}else if(params.get("ID_NO_ENCRYPT")!=null){
				String id_no	= TripleDES.decrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, params.get("ID_NO_ENCRYPT"));
				String identity_cardNo_md5 = Password.encode(id_no, "MD5");

				String id_no_x = "";
				if(id_no.contains("x")){
					id_no_x = id_no.toUpperCase();
				}else if(id_no.contains("X")){
					id_no_x = id_no.toLowerCase();
				}
				if( !id_no_x.equals("")){
					id_no_x = Password.encode(id_no_x, "MD5");
					if(!identity_cardNo_md5.equals(member.getIdentity_cardNo_md5())){
						if(!id_no_x.equals(member.getIdentity_cardNo_md5())){
							throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
						}
					}
				}else{
					if(!identity_cardNo_md5.equals(member.getIdentity_cardNo_md5())){
						throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
					}
				}
			}
		}
	}
	public  void sendMessage(Boolean state, Map<String, Object> params)throws Exception{
		String version = Environment.instance().getSystemProperty("version");

		JSONObject returnValue = new JSONObject();
//		sdf.format((Date)member.getFM_CreateTime())
//		params.get("callback_url")
//		params.get("en_accountNo")
		//query.setParameter("check_state", Constant.ENTERPRISE_CHECK_STATE_AUDITED_SUCCESS);
		Long check_state = (Long)params.get("check_state");
		returnValue.put("status", check_state);
		returnValue.put("checkTime", params.get("checkTime"));
		returnValue.put("bizUserId", biz_user_id);
		returnValue.put("remark", params.get("remark"));
		//fail_reason
		if ( check_state.equals( Constant.ENTERPRISE_CHECK_STATE_AUDITED_FAIL))
			returnValue.put("failReason", params.get("fail_reason"));

		Long appid = (Long)params.get("appid");//member.getApplicationId();

		Map<String, Object> app = DynamicEntityService.getEntity(appid, "AMS_Organization");
		String orgNo = (String)app.get("codeNo");
		JSONObject rps = new JSONObject();

		rps.put("returnValue", returnValue);
		rps.put("status", "ok");

		if(!state){
			rps.put("errorCode", ErrorCode.USER_ERROR);
			rps.put("message", "审核失败。");
			rps.put("status", "fail");
		}
		String timestamp = sdf.format(new Date());
		String signStr = app.get("codeNo") + rps.toString() + timestamp;

		OrderService orderService = new OrderServiceImpl();
		Map<String, Object> org = orderService.getOrg(orgNo);
		PrivateKey privateKey = RSAUtil.getPrivateKey((String)org.get("private_key"));
		String sign = RSAUtil.sign(privateKey, signStr);
		logger.info("signStr:"+signStr);
		logger.info("sign:"+sign);
		StringBuilder sb = new StringBuilder();
		sb.append("sysid=").append(orgNo);
		sb.append("&rps=").append(rps.toString());
		sb.append("&timestamp=").append(timestamp);
		sb.append("&v=").append(version);
		sb.append("&sign=").append(sign);
		JSONObject sendContent = new JSONObject();
		sendContent.put("backUrl", params.get("callback_url"));
		sendContent.put("param", sb.toString());
		sendContent.put("orderNo", params.get("en_accountNo"));
		RabbitProducerManager rabbitProducerManager = RabbitProducerManager.getInstance();
		logger.info("-----发送开始----");
		try {

//    				    		JSONObject json = new JSONObject(_order_entity);
			logger.info("-----start" + sb.toString());
//    				    		json.put("time", new Date().getTime());
			rabbitProducerManager.send(URLEncoder.encode(sendContent.toString(), "UTF-8"));
			logger.info("-----end");
		} catch (Exception e1) {
			logger.info("--error---");
			logger.error(e1.getMessage(), e1);
		}
		logger.info("-----发送完毕----");
	}
}

