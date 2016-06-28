package bps.service;

import java.util.Map;

import bps.common.BizException;


/**
 * 
* 发送短信和邮件，以及验证code接口
* @author huadi
* @date 2015年9月16日 上午9:59:55
 */
public interface CodeService {
	
	/**
	 * 
	* 添加短信模板
	* @param applicationId  应用ID -必填
	* @param verificationCodeType  验证类型内容为101到109  -必填 
	* @param template  模板内容 (String 500) -必填
	* @return Map<String,Object> 短信模板实体
	* @throws BizException
	 */
	public Map<String,Object> addSMSTemplate(Long applicationId,Long verificationCodeType,String template)throws BizException;
	
	/**
	 * 
	* 获取短信模板
	* @param applicationId  应用ID -必填
	* @param verificationCodeType  验证类型 -必填 
	* @return  Map<String,Object> 短信模板实体
	* @throws BizException
	 */
	public Map<String,Object> getSMSTemplate(Long applicationId,Long verificationCodeType)throws BizException;
	
	
	

	/**
	 * 
	* 生成短信验证码
	* @param applicationId 应用ID -必填
	* @param phone  手机号码(String 100) -必填
	* @param verificationCodeType  验证类型 -必填
	* @param extParams  扩展参数                               -非必填
	* 					codeNo 模板编号	   -非必填
	* 					member_id  会员ID	-非必填
	* 					orderNo		订单编号   -非必填
	* @return  String 短信文本
	* @throws BizException
	 */
	public String generatePhoneVerificationCode(
			Long applicationId,
			String phone, 
			Long verificationCodeType, 
			Map<String, Object> extParams
			) throws BizException;
	
	
	/**
	*  验证短信验证码
	* @param applicationId	 应用ID -必填
	* @param phone	手机号码				-必填
	* @param verificationCodeType	验证码类型		-必填
	* @param phoneCode	验证码						-必填
	* @return	Long 验证码记录编号  >0表示成功
	* @throws BizException
	 */
	public Long checkPhoneVerificationCode(
			Long applicationId,
			String phone, 
			Long verificationCodeType, 
			String phoneCode
			) throws BizException;
	
	/**
	 * 设置短信验证码无效
	 * @param verificationCodeId	验证码记录编号
	 * @return 
	 * @throws BizException
	 */
	public void setPhoneVerificationCode(
			Long verificationCodeId
			) throws BizException;
	
	
	/**
	 * 生成邮箱验证码
	 * @param bizId	业务ID
	 * @param identityId 云账户ID
	 * @param email	邮箱
	 * @param verificationCodeType	验证码类型
	 * @param memberId	会员编号
	 * @param extParams	保留参数
	 * @return 
	 * @throws BizException
	 */
	/**
	 * 
	* (生成邮箱验证码
	* @param applicationId	 应用ID -必填
	* @param email	邮箱(String 100)				-必填
	* @param verificationCodeType
	* @param memberId
	* @param extParams  扩展参数                               -非必填
	* 					codeNo 模板编号	   -非必填
	* 					member_id  会员ID	-非必填
	* 					orderNo		订单编号   -非必填
	* @return
	* @throws BizException
	 */
	public Map<String, Object> generateEmailVerificationCode(
			Long applicationId,
			String email, 
			Long verificationCodeType, 
			Long memberId, 
			Map<String, Object> extParams
			) throws BizException;
	
	
	
	/**
	*   验证邮箱验证码
	* @param applicationId	 应用ID -必填
	* @param emial	邮箱			-必填
	* @param verificationCodeType	验证码类型		-必填
	* @param phoneCode	验证码						-必填
	* @return	Long 验证码记录编号  >0表示成功
	* @throws BizException
	 */
	public Long checkEmailVerificationCode(
			Long applicationId,
			String emial, 
			Long verificationCodeType, 
			String verificationCode
			) throws BizException;
	
	/**
	 * 设置邮箱验证码无效
	 * @param verificationCodeId	验证码记录编号
	 * @return 
	 * @throws BizException
	 */
	public void setEmailVerificationCode(
			Long verificationCodeId
			) throws BizException;
	
	
	/**
	 * 
	* 获取短信验证实体
	* @param applicationId  应用
	* @param phone	手机号码
	* @param verificationCodeType  类型
	* @param phoneCode	验证码
	* @return	Map<String,Object>  短信验证实体
	* @throws BizException
	 */
	public  Map<String, Object> getPhoneVerificationCode(Long applicationId,String phone,
			 Long verificationCodeType,   String phoneCode) throws BizException;
}
