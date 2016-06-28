package bps.service;

import java.util.List;
import java.util.Map;

import apf.util.BusinessException;
import bps.common.BizException;
import scala.util.parsing.combinator.testing.Str;

/**
* 账户类接口文档
* @author huadi
* @date 2015年9月16日 上午9:22:26 
*/
public interface AccountService {
	/****
	 * 创建账户集  现金类
	 * @param memberId  会员ID  -必填
	 * @param applicationId 应用ID -必填
	 * @param name   账户集名称 (String 100) -必填
	 * @return Map<String,Object>    账户集对象
	 * @throws BizException
	 */
	public Map<String,Object> createBizCashAccountType(Long memberId,
			Long applicationId,
			String name,
			String description)throws BizException;
	/****
	 * 创建账户集  积分类
	 * @param memberId  会员ID  -必填
	 * @param applicationId 应用ID  -必填
	 * @param name  账户集名称 (String 100) -必填
	 * @param unit  单位 (String 100) -必填
	 * @param rmbRate 和人名币比例  必须整数  -必填
	 * @return Map<String,Object>    账户集对象
	 * @throws BizException
	 */
	public Map<String,Object> createBizBonusAccountType(Long memberId,
			Long applicationId,
			String name,
			String unit,
			Long rmbRate,
			String description)throws BizException;
	/***
	 * 创建账户集 货基类
	 * @param memberId  会员ID
	 * @param applicationId 应用ID
	 * @param name   账户集，名称(String 长度 100)
	 * @param fundId 货基类ID
	 * @return Map<String,Object>    返回类型 
	 * @throws BizException
	 */
	public Map<String,Object> createBizFundAccountType(Long memberId,
			Long applicationId,
			String name,
			Long fundId,
			String description)throws BizException;
	
	
	/**
	 * 
	* 获取用户下的现金账户集
	* @param applicationId 应用ID
	* @param @throws BizException    异常
	* @return Map<String,Object>    返回类型 
	* @throws BizException
	 */
	public Map<String,Object> getApplicationCashAccountType(Long applicationId)throws BizException;


	/**
	 * 
	* 获取用户账户
	* @param memberId   会员ID
	* @param accountTypeId	账户类型ID
	* @return	Map<String,Object> 获取会员账户
	* @throws BizException
	 */
	public Map<String,Object> getMemberAccountByType(Long memberId,Long accountTypeId)throws BizException;



	/**
	 * 
	* 冻结账户金额
	* @param bizFreezenNo
	* @param memberId
	* @param accountTypeId
	* @param freezeMoney
	* @throws BizException
	 */
	public void freezeMoney(String bizFreezenNo,Long memberId, Long accountTypeId,Long freezeMoney) throws BizException;
	
	/**
	 * 
	* 解冻账户金额
	* @param bizFreezenNo
	* @param memberId
	* @param accountTypeId
	* @param unFreezeMoney
	* @throws BizException
	 */
	public  void unFreezeMoney(String bizFreezenNo,Long memberId,Long accountTypeId,Long unFreezeMoney)throws BizException;

	
	public Map<String,Object> getAccountTypeByNo(String accountNo)throws BizException;


}
