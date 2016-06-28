package bps.service;

import java.util.List;
import java.util.Map;
import bps.common.BizException;

/***
 * 
* 会员类服务接口
* @author huadi
* @date 2015年9月16日 上午10:06:31
 */
public interface MemberService {
	
	
	/**
	 * 通过邮箱注册(门户)
	 * @param source		1 PC  2 手机    必填 
	 * @param memberType	会员类型,目前支持2 开发者  必填 
	 * @param email			邮箱 (String 长度 100) 必填      
	 * @param loginPassword	登陆密码(String 长度 100)  必填 
	 * @param registerIp		注册IP(String 长度 100)  必填 
	 * @param extParams			保留参数 
	 * @return Map<String, Object>	会员实体对象
	 * @throws BizException
	 */
	public Map<String, Object> registerCompanyUserByEmail(
			String source, 
			Long memberType, 
			String email, 
			String loginPassword, 
			String registerIp,
			Map<String,Object> extParams
			) throws BizException; 
	
	/***
	 * 激活注册帐号
	 * @param memberId 会员ID 必填
	 * @throws BizException
	 */
	public void activateCompanyUser(Long memberId)throws BizException;
	
	/**
	 * 
	* 注册成为开发者
	* @param memberId 会员ID  -必填
	* @param params  必填
	* 			name   姓名 	(String 长度 100)	-必填  
	* 			identity_cardNo  身份证 (String 长度 100)  -必填 
	* 			phone			手机	(String 长度 100)	-必填  
	* 			email			邮箱	(String 长度 100)	-必填
	* @throws BizException
	 */
	public void registerDeveloper(Long memberId,Map<String,Object> params)throws BizException;
	
	
	/**
	 * 通过邮箱登陆
	 * @param email			邮件(String 长度 100) -必填
	 * @param password		密码(String 长度 100)  -必填
	 * @param loginIp		登录IP(String 长度 100) -必填
	 * @param extParams		扩展参数 -目前可不填
	 * @return Map<String, Object> 
	 * 	status           : 0:成功；1：帐号不存在；2：密码错误；3：登录帐号已锁定
	 *  member_entity    : Map<String, Object> 会员对象
	 *  login_fail_amount：Long登陆失败次数
	 *  remain_amount	：剩余次数
	 * @throws BizException
	 */
	public Map<String, Object> loginByEmail(
			String source, 
			String email, 
			String password, 
			String loginIp, 
			Map<String, Object> extParams
			) throws BizException;
	
	/**
	 * 
	* 设置会员信息
	* @param memberId  会员ID -必填
	* @param params    会员信息数据
	* 			name  姓名 -必填 String
	* 			user_name 昵称  -必填  String
	* 			address  地址  -非必填 String
	* 			country  国家  -非必填 String 
	* 			province 省份 -非必填 String 
	* 			area 市 -非必填 String
	* @param extendParam  扩展信息 目前不填
	* @throws BizException
	 */
	public void setUserInfo(Long memberId, Map<String, Object> params,Map<String,Object> extendParam) throws BizException;
	
	/***
	 * 
	* 设置会员企业资质信息和银行信息
	* @param memberId  会员ID
	* @param params  会员企业资质信息  
	* @param extendParam
	* @throws BizException
	 */
	/**
	 * 
	* 设置会员企业资质信息和银行信息
	* @param memberId 会员ID -必填
	* @param params   会员企业资质信息  -必填
	* 		 business_license   营业执照注册号(String 长度100)  -必填
	*        organization_code  组织机构代码(String 长度 100)       -必填
	*        legal_name         法人姓名  (String 长度 100)               -必填
	*        legal_ids          法人证件号码 (String 长度 100)      -必填
	*        business_license_path   营业执照图片路径 (String 长度 500)   -非必填
	*        organization_code_path  组织机构代码证路径(String 长度 500) -非必填
	*        tax_certificate_path    税务登记证路径  (String 长度 500)  -非必填
	*        bank_settlement_path    银行结算账户开户许可证路径 (String 长度 500) -非必填
	*        legal_certificate_path  法人证件扫描件路径(String 长度 500) -非必填
	* @param bankParam
	* 			accountNo  		 帐号 (String 长度 100)    -必填
	* 			account_name     开户名 (String 长度 100) -必填
	* 			bank_code        银行代码 (String 长度 100) -必填
	* 			bank_name        银行名称 (String 长度 100) -必填
	* 			phone            预留手机号 (String 长度 100)  -必填
	* @param extendParam
	* @throws BizException
	 */
	public void setUserAuthInfo(Long memberId, Map<String, Object> params,Map<String,Object> bankParam,Map<String,Object> extendParam) throws BizException;
//	/****
//	 * 设置会员扩展信息
//	 * @param memberId
//	 * @param params
//	 * @throws BizException
//	 */
//	public void setUserExtendInfo(Long memberId, Map<String, Object> params) throws BizException;
	
	
	
	
	/***
	 * 获取会员基本信息 
	 * @param memberId 会员ID -必填
	 * @return Map<String,Object> 会员实体对象
	 * @throws BizException
	 */
	public Map<String,Object> getUserInfo(Long memberId) throws BizException;
	
	/**
	 * 
	* 获取企业资质信息
	* @param memberId
	* @return
	* @throws BizException
	 */
	/****
	 * 
	* 获取企业资质信息
	* @param memberId  会员ID -必填
	* @return Map<String,Object>   
	* 				business_license  		营业执照注册号(String)
	* 				organization_code		组织机构代码(String)
	* 				legal_name				法人姓名(String)
	* 				legal_ids				法人证件号码 (String)
	* 				business_license_path   营业执照图片路径(String)
	* 				organization_code_path	组织机构代码证路径  (String)
	* 				tax_certificate_path	税务登记证路径  (String)
	* 				bank_settlement_path	银行结算账户开户许可证路径(String)
	* 				legal_certificate_path	法人证件扫描件路径(String)
	* 				bank_code				银行代码 (String)
	* 				bank_name				银行名称(String)
	* 				card_type				卡类型(Long)
	* 				account_name			开户名(String)
	* 				bind_state				绑定状态(Long)
	* 				accountNo				帐号(String)
	* 				accountNo_encrypt		帐号(加密)(String)
	* 				
	* @throws BizException
	 */
	public Map<String,Object> getUserAuthInfo(Long memberId)throws BizException;
	
//	/*****
//	 * 获取会员扩展信息
//	 * @param memberId
//	 * @return
//	 * @throws BizException
//	 */
//	public Map<String,Object> getUserExtendInfo(Long memberId) throws BizException;
	
	
	/****
	 * 获取会员基本信息 
	 * @param userId 云账户会员UUID -必填
	 * @return Map<String,Object> 会员实体对象
	 */
	public Map<String,Object> getUserInfo(String userId)throws BizException ;
	
	/***
	 * 
	*根据业务系统ID和业务系统账号获取会员
	* @param applicationId  业务系统ID -必填
	* @param bizUserId  业务系统帐号  -必填
	* @return Map<String,Object> 会员实体对象
	* @throws BizException
	 */
	public Map<String,Object> getUserInfo(Long applicationId,String bizUserId )throws BizException;
	
	
	/**
	 * 
	* 设置个人实名信息（个人，非企业类型）
	* @param memberId  会员id -必填
	* @param idType    身份类型 目前1 只支持身份证  -必填
	* @param idNo      身份证 (String 长度 100)  -必填
	* @param userName  姓名 (String 长度 100)   -必填
	* @throws BizException
	 */
	public void setRealNameInfo(Long memberId, String idType,String idNo,String userName) throws BizException;
	
	
	
	
	/**
	 * 绑定手机号码，验证操作有前台通过调用CodeService 自己去验证
	 * @param memberId  会员ID -必填
	 * @param phone  绑定手机号码 (String 长度 100)  -必填
	 * @throws BizException
	 */
	public void bindPhone(Long memberId,String phone)throws BizException;
	
	
	
	
	/***
	 * 设置支付密码和修改支付密码
	 * @param memberId  会员ID -必填
	 * @param newPayPassword  密码 (String 长度 100)  -必填
	 * @param oldPayPassword   老密码 (String 长度 100)   如果第一次设置不填，修改密码必填
	 * @throws BizException
	 */
	public void setPayPassword(Long memberId,String newPayPassword,String oldPayPassword)throws BizException;
	
	/***
	 *  登录密码重置，验证在前台验证，验证通过调用此接口
	 * @param memberId  会员ID -必填
	 * @param password  密码 (String 长度 100)  -必填
	 * @throws BizException
	 */
	public void resetPassword(Long memberId,String password)throws BizException;
	
	/***
	 * 设置登录短信通知  
	 * @param memberId 会员ID -必填
	 * @throws BizException
	 */
	public void setLoginSendSms(Long memberId)throws BizException;
	
	/***
	 * 设置登录邮件通知
	 * @param memberId -必填
	 * @throws BizException
	 */
	public void setLoginSendEmail(Long memberId)throws BizException;
	
	
	
	/****
	 * 获取应用
	 * @param applicationNo (String 长度 100)  应用号 -必填
	 * @return Map<String,Object> 应用实体
	 */
	public Map<String,Object> getApplication(String applicationNo)throws BizException;
	
	/****
	 * 获取应用
	 * @param memberId  会员ID -必填
	 * @return Map<String,Object> 应用实体
	 */
	public List<Map<String,Object>> getApplication(Long memberId)throws BizException;
	
	/**
	 * 
	* 创建托管会员 
	* @param applicationId  应用ID -必填
	* @param bizUserId      业务系统ID -必填
	* @param memberType     会员类型   2 应用的企业会员  3 应用的 个人会员 必填
	* @param extendParam    扩展信息  -目前不填
	* @return Map<String,Object> 会员实体
	* @throws BizException
	 */
	public Map<String,Object> createBizMember(Long applicationId,String bizUserId,Long memberType,Map<String,Object> extendParam)throws BizException;
	
	/**
	 * 
	*锁住会员
	* @param memberId  会员ID -必填
	* @param extendParam  扩展参数  
	* @throws BizException
	 */
	public void lockBizMember(Long memberId,Map<String,Object> extendParam)throws BizException;
	
	/****
	 * 解锁用户
	 * @param memberId  会员ID -必填
	 * @param extendParam 扩展参数   -目前不填
	 * @throws BizException
	 */
	public void unLockBizMember(Long memberId,Map<String,Object> extendParam)throws BizException;
	/**
	 * 
	* 获取银行卡列表
	* @param memberId  会员ID -必填
	* @param cardKind  卡业务类型  1：银行卡， 2:万商卡
	* @return List<Map<String, Object>> 绑定银行卡实体
	* @throws BizException
	 */
	public List<Map<String, Object>> getBindBankCardList(Long memberId,Long cardKind) throws BizException;
	
	/****
	 * 获取绑定的银行卡
	 * @param memberId  会员ID -必填
	 * @param cardNo	卡号 (String 长度 100)  -必填
	 * @return Map<String,Object> 银行卡实体
	 * @throws BizException
	 */
	public Map<String,Object> getBindBankCard(Long memberId,String cardNo) throws BizException;
	
	
	/***
	 * 
	* 解邦银行卡
	* @param _memberId   会员ID
	* @param _accountId  绑定银行卡ID
	* @throws BizException
	 */
	public void unbindBankCard(Long _memberId, Long _accountId) throws BizException;
	/**
	 * 设置安全卡
	 * @param _memberId			会员ID
	 * @param _accountId		设置安全卡的银行卡ID
	 * @param _setSafeCard		是否设置为安全卡
	 * @throws BizException
	 */
	public void setSafeCard(Long _memberId, Long _accountId, boolean _setSafeCard) throws BizException;
	
	/***
	 * 
	* 绑定银行卡
	* @param memberId   会员ID -必填
	* @param cardType   卡类型  1 借记卡 2贷记卡 3 准贷记卡6预付费卡  -必填
	* @param cardNo      卡号   (String 长度 100) 			-必填
	* @param extParams   银行卡信息   		-必填
	* 		 bank_code   银行代码 (String 长度 100) 		-必填
	* 		 bank_name	  银行名称 (String 长度 100) 		-必填
	* 		 identity_cardNo  身份证	 (String 长度 100) 	-必填
	* 		 account_name   开户名称 (String 长度 100) 		-必填
	*        account_prop   账户属性(long)  0个人 1企业		-必填
	*        acct_validdate 有效期 (String 长度 100) 	信用卡类型必传，-其他不传
	*        cvv2        	cvv2 (String 长度 100) 	信用卡类型必传，-其他不传
	*        phone          手机预留号 (String 长度 100) 		-必填
	*        contract_no    协议号 (String 长度 100) 	 绑定签约申请有协议号 -必填
	* @return	Map<String, Object>  返回银行卡实体
	* @throws BizException
	 */
	public Map<String, Object> bindBankCard(Long memberId, Long cardType,
			String cardNo, Map<String, Object> extParams)
			throws BizException ;

	/**
	 * 三要素绑定银行卡
	 * @param bankCode		银行代码
	 * @param accountName	帐户名称
	 * @param cardType		卡类型
	 * @param accountNo		帐号
	 * @param bankName		银行名称
	 * @param memberId		会员ID
	 * @param extParams		其他参数
     * @return	map
     * @throws BizException
     */
	Map<String, Object> threeElementsBindBankCard(String bankCode, String accountName, Long cardType, String accountNo,
						  String bankName, Long memberId, Long id_type, String id_no, Map<String, Object> extParams) throws Exception ;
	/***
	 * 
	* 绑定银行卡
	* @param memberId   会员ID -必填
	* @param cardType   卡类型  1 借记卡 2贷记卡 3 准贷记卡6预付费卡  -必填
	* @param cardNo      卡号   (String 长度 100) 			-必填
	* @param extParams   银行卡信息   		-必填
	* 		 bank_code   银行代码 (String 长度 100) 		-必填
	* 		 bank_name	  银行名称 (String 长度 100) 		-必填
	* 		 identity_cardNo  身份证	 (String 长度 100) 	-必填
	* 		 account_name   开户名称 (String 长度 100) 		-必填
	*        account_prop   账户属性(long)  0个人 1企业		-必填
	*        acct_validdate 有效期 (String 长度 100) 	信用卡类型必传，-其他不传
	*        cvv2        	cvv2 (String 长度 100) 	信用卡类型必传，-其他不传
	*        phone          手机预留号 (String 长度 100) 		-必填
	*        contract_no    协议号 (String 长度 100) 	 绑定签约申请有协议号 -必填
	* @return	Map<String, Object>  返回银行卡实体
	* @throws BizException
	 */
	public Map<String, Object> bindBankCardOutCheck(Long memberId, Long cardType,
			String cardNo, Map<String, Object> extParams)
			throws BizException ;
	/***
	 * 获取卡bin
	 * @param cardBin 卡bin (String 长度 100)   -必填
	 * @return Map<String, Object> 卡bin实体
	 * @throws BizException
	 */
	public Map<String, Object> getCardBin(String cardBin) throws BizException;
	
	/***
	 * 验证身份证号码是否存在
	 * @param identityCardNo  身份证号 (String 长度 100)   -必填
	 * @return Boolean
	 * @throws BizException
	 */
	public Boolean checkIdentityCardNo(String identityCardNo)throws BizException ;
	
	/**
	 * 获取银行信息
	 * @param bankCode 银行代码 (String 长度 100)   -必填
	 * @return Map<String, Object> 银行实体
	 * @throws BizException
	 */
	public Map<String, Object> getBankInfo(String bankCode) throws BizException;
	
	/**
	 * 
	* 根据bankCode  获取 银行代码
	* @param bank_code (String 长度 100)   -必填
	* @return  银行代码 实体
	* @throws BizException
	 */
	public Map<String, Object> getBankCode(String bank_code)throws BizException;
	
	
	/***
	 * 
	* 回去开发者信息接口
	* @param memberId  会员ID -必填
	* @return	Map<String,Object> 
	* 			name						姓名
	* 			identity_cardNo				身份证
	* 			phone						手机号码
	* 			email						邮件
	* 			member_id					会员ID
	* 			member_label				会员名称
	* @throws BizException
	 */
	public Map<String,Object> getDeveloperInfo(Long memberId)throws BizException;
	
	
	/***
	 * 
	* 获取开发参数
	* @param applicationId  应用ID	-必填
	* @return	Map<String,Object>
	* 			public_key 公钥
	* 			sysid      系统编号
	* @throws BizException
	 */
	public Map<String,Object> getDevelopParam(Long applicationId)throws BizException;
	
	/**
	 * 获取应用和应用下的企业用户
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getAppCompany() throws BizException;
	
	 /**
     * 企业图片保存记录
     * @param picInfo
     * @throws BizException
     */
	public void recordCompanyPic(Map<Long, Map<String, String>> picInfo) throws BizException;

	/**
	 * 开通简单代扣
	 * @param applicationId(Long)    应用ID	-必填
	 * @param bizUserId(String)      业务系统ID -必填
	 * @param isAuthed(Boolean)      是否已实名认证 -必填
	 * @param isBinded(Boolean)      是否已绑卡
	 * @return	Map<String,Object>
	 * 			bizUserId(String)      业务系统ID
	 * 		    isAuthed(Boolean)      是否已实名认证
	 * 		    isBinded(Boolean)      是否已绑卡
	 * 		    isAdvAuthed(Boolean)   是否已强实名
	 * 		    memberSafeLevel(String)      会员安全等级
	 * 		    isAllowPay(Boolean)	   是否允许支付
	 * @throws BizException
	 */
	public Map<String,Object> openDaikouSimple(Long applicationId, String bizUserId,Boolean isAuthed,Boolean isBinded,Long memberId) throws BizException;
	
//	/**
//	 * 通过userId获取信息
//	 * @param userId 	用户UUID
//	 * @return Map<String, Object> 会员实体对象
//	 * @throws BizException
//	 */
//	public Map<String, Object> getUserInfoByUUID(String userId)throws BizException;
	
	

}