package bps.account;

import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.allinpay.commons.core.util.StringUtils;
import com.kinorsoft.ams.services.FreezeService;
import com.kinorsoft.ams.services.QueryService;
import com.kinorsoft.ams.trade.Account;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.member.Member;
import bps.service.AccountService;
import bps.util.CodeUtil;

public class AccountServiceImpl implements AccountService{
	
	public static Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: createBizCashAccountType</p> 
	* <p>Description: 创建现金类托管账户集 </p> 
	* @param memberId
	* @param applicationId
	* @param name
	* @param description
	* @return
	* @throws BizException 
	* @see bps.service.AccountService#createBizCashAccountType(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	public Map<String,Object> createBizCashAccountType(Long memberId, Long applicationId,
			String name, String description)throws BizException {
		
		// TODO Auto-generated method stub
		logger.info("crateApplication start");
        logger.info("memberId:"+memberId+";applicationId:"+applicationId+";name:"+name+";description:"+description);
		try {
			if(memberId == null)
				throw new BizException(ErrorCode.OTHER_ERROR, "内部参数memberId不能为空");
			if(applicationId == null)
				throw new BizException(ErrorCode.OTHER_ERROR, "内部参数applicationId不能为空");
			if(name == null)
				throw new BizException(ErrorCode.OTHER_ERROR, "内部参数name不能为空");

        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("memberId", memberId);
            param.put("applicationId", applicationId);
            param.put("name", name);
            param.put("description",description );
            final Member meb=new Member(memberId);
           
            if(meb.getUserId() == null)
            	throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
            boolean isDeveloper = false;
            if(meb.getIsDeveloper() != null){
            	isDeveloper = meb.getIsDeveloper();
            }
            if(!isDeveloper)
            	throw new BizException(ErrorCode.USER_IS_NO_DEVELOPER, "用户未成为开发者，请先成为开发者");
            
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            final Map<String,Object> org_entity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
            if(org_entity == null){
            	throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
            }
            if(((Long)org_entity.get("member_id"))!= memberId.longValue()){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
			}
            Map<String, Object> accountType_entity= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
    			
    			public boolean before(Session session) throws Exception {
    				Map<String,Object> accountType = getApplicationCashAccountType((Long)org_entity.get("id"));
    				if(accountType != null){
    					throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR,"此应用下已经存在现金账户集，不允许在创建");
    				}
					return true;
				}
    			
    			@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
    				Map<String, String> accountTypeMap = new HashMap<String, String>();
    				accountTypeMap.put("valid_type", String.valueOf(Constant.VALIDITYTYPE_FOREVER));
    				accountTypeMap.put("valid_merge", String.valueOf(Constant.VALID_MERGE_NEVER));
    				accountTypeMap.put("name", (String)param.get("name"));
    				accountTypeMap.put("biz_type", String.valueOf(Constant.BIZ_TYPE_CASH));
    				accountTypeMap.put("account_prop", String.valueOf(Constant.ACCOUNT_PROP_DEFAULT));
    				accountTypeMap.put("rbm_rate", String.valueOf(Constant.RMB_RATE_DEFAULT));
    				accountTypeMap.put("application_id", String.valueOf(org_entity.get("id")));
    				accountTypeMap.put("application_label", String.valueOf(org_entity.get("name")));
    				accountTypeMap.put("description", (String)param.get("description"));
    				accountTypeMap.put("member_id", String.valueOf(meb.getId()));
    				accountTypeMap.put("member_label", String.valueOf(meb.getName()));
    				accountTypeMap.put("account_kind", String.valueOf(Constant.ACCOUNT_KIND_TG));
    				accountTypeMap.put("applicationNo",(String)org_entity.get("sysid"));
    				accountTypeMap.put("unit",Constant.ACCOUNT_UNIT);
    				accountTypeMap.put("codeNo",CodeUtil.getCode("2", 1L, 6));
    				Map<String,Object> entity = DynamicEntityService.createEntity("AMS_AccountType", accountTypeMap, null);
    				return entity;
    			}
    		});
            logger.info("accountType_entity:"+accountType_entity);
    		return accountType_entity;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR,e.getMessage());
		}
	}

	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: createBizBonusAccountType</p> 
	* <p>Description: 创建积分类托管账户集合</p> 
	* @param memberId
	* @param applicationId
	* @param name
	* @param unit
	* @param rmbRate
	* @param description
	* @return
	* @throws BizException
	 */
	public Map<String,Object> createBizBonusAccountType(Long memberId, Long applicationId,
			String name, String unit, Long rmbRate, String description)throws BizException {
		// TODO Auto-generated method stub
		logger.info("crateApplication start");
        logger.info("memberId:"+memberId+";applicationId:"+applicationId+";name:"+name+";description:"+description);

		try {
			if(memberId == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数memberId不能为空");
			if(applicationId == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数applicationId不能为空");
			if(name == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数name不能为空");
			if(unit == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数unit不能为空");
			if(rmbRate == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数rmbRate不能为空");

        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("memberId", memberId);
            param.put("applicationId", applicationId);
            param.put("name", name);
            param.put("description",description );
            param.put("rmbRate",rmbRate );
            param.put("unit",unit );
            final Member meb=new Member(memberId);
           
            if(meb.getUserId() == null)
            	throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
            boolean isDeveloper = false;
            if(meb.getIsDeveloper() != null){
            	isDeveloper = meb.getIsDeveloper();
            }
            if(!isDeveloper)
            	throw new BizException(ErrorCode.USER_IS_NO_DEVELOPER, "用户未成为开发者，请先成为开发者");
            
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            final Map<String,Object> org_entity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
            if(org_entity == null)
            	throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
            if(((Long)org_entity.get("member_id"))!= memberId.longValue()){
            	throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
            }
            Map<String, Object> accountType_entity= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
    			@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
    				Map<String, String> accountTypeMap = new HashMap<String, String>();
    				accountTypeMap.put("valid_type", String.valueOf(Constant.VALIDITYTYPE_FOREVER));
    				accountTypeMap.put("valid_merge", String.valueOf(Constant.VALID_MERGE_NEVER));
    				accountTypeMap.put("name", (String)param.get("name"));
    				accountTypeMap.put("biz_type", String.valueOf(Constant.BIZ_TYPE_INTEGRAL));
    				accountTypeMap.put("account_prop", String.valueOf(Constant.ACCOUNT_PROP_DEFAULT));
    				accountTypeMap.put("rbm_rate", String.valueOf(param.get("rmbRate")));
    				accountTypeMap.put("application_id", String.valueOf(org_entity.get("id")));
    				accountTypeMap.put("application_label", String.valueOf(org_entity.get("name")));
    				accountTypeMap.put("description", (String)param.get("description"));
    				accountTypeMap.put("member_id", String.valueOf(meb.getId()));
    				accountTypeMap.put("member_label", String.valueOf(meb.getName()));
    				accountTypeMap.put("account_kind", String.valueOf(Constant.ACCOUNT_KIND_TG));
    				accountTypeMap.put("applicationNo",(String)org_entity.get("sysid"));
    				accountTypeMap.put("unit",(String)param.get("unit"));
    				accountTypeMap.put("codeNo",CodeUtil.getCode("3", 2L, 6));
    				Map<String,Object> entity = DynamicEntityService.createEntity("AMS_AccountType", accountTypeMap, null);
    				return entity;
    			}
    		});
            logger.info("accountType_entity:"+accountType_entity);
    		return accountType_entity;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR,e.getMessage());
		}
	}


	public Map<String, Object> getMemberAccountByType(Long memberId,
			Long accountTypeId) throws BizException {
		// TODO Auto-generated method stub
		try{
			if(memberId == null)
				throw new BizException(ErrorCode.OTHER_ERROR, "内部参数memberId不能为空");
			if(accountTypeId == null)
				throw new BizException(ErrorCode.OTHER_ERROR, "内部参数accountTypeId不能为空");

			final Long _accountTypeId = accountTypeId;
			final Member member = new Member(memberId);
			final String userId = member.getUserId();
			if(userId == null && !"".equals(userId)){
	        	throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
	        }
			Map<String, Object> memberAccount = EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					Account account = new Account();
					return account.getMemberAccount(userId, _accountTypeId, session);
			}
			});
			return memberAccount;
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}


	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: createBizFundAccountType</p> 
	* <p>Description: 创建托管货基类账户集</p> BIZ_TYPE_CARGO
	* @param memberId
	* @param applicationId
	* @param name
	* @param fundId
	* @param description
	* @return
	* @throws BizException 
	* @see bps.service.AccountService#createBizFundAccountType(java.lang.Long, java.lang.Long, java.lang.String, java.lang.Long, java.lang.String)
	 */
	public Map<String,Object> createBizFundAccountType(Long memberId, Long applicationId,
			String name, Long fundId, String description)throws BizException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取第一个现金账户集（非托管专用账户）
	 */
	public Map<String, Object> getApplicationCashAccountType(Long applicationId)
			throws BizException {
		// TODO Auto-generated method stub
		try{
			if(applicationId == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数applicationId不能为空");

			final Long _applicationId = applicationId;

			Map<String, Object> accountType = EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					String sqlStr = "from AMS_AccountType where application_id=:application_id and biz_type=:biz_type and account_kind=:account_kind and (is_marketing_account is null or is_marketing_account=:isMarketingAccount) order by codeNo";
					Query query = session.createQuery(sqlStr);
					query.setParameter("application_id", _applicationId);
					query.setParameter("biz_type", Constant.BIZ_TYPE_CASH);
					query.setParameter("account_kind", Constant.ACCOUNT_KIND_TG);
					query.setParameter("isMarketingAccount", false);
					List list = query.list();
					if(list.size() == 0){
						return null;
					}
					
					return (Map<String, Object>)list.get(0);
				}
			});
			return accountType;
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 根据codeNo获取账户集
	 * @param codeNo
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getAccountTypeByCodeNo(String codeNo) throws Exception{
		try{
			if(StringUtils.isBlank(codeNo)){
				throw new Exception("内部参数codeNo不能为空");
			}

			final String _codeNo = codeNo;
			
			return EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {

				@Override
				public Map<String, Object> doQuery(Session session)
						throws Exception {
					String queryStr = "from AMS_AccountType where codeNo=:codeNo";
					Query query = session.createQuery(queryStr);
					query.setParameter("codeNo", _codeNo);
					List list = query.list();
					if(list.size() == 0){
						return null;
					}
					
					return (Map<String, Object>)list.get(0);
				}
			});
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 *
	* 冻结账户金额
	* @param bizFreezenNo  冻结订单号
	* @param memberId      冻结会员
	* @param freezeMoney   冻结金额
	* @throws BizException
	 */
	public void freezeMoney(String bizFreezenNo,Long memberId, Long accountTypeId,Long freezeMoney) throws BizException {
		logger.info("freezeMoney start");
		try {
			final String _bizid =bizFreezenNo;
			final Long _accountTypeId=accountTypeId;
			final Long _freezeMoney=freezeMoney;
			final Member member = new Member(memberId);
			if(member.getUserId() == null || "".equals(member.getUserId())){
				throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
			}
			
	        EntityManagerUtil.execute(new TransactionWork<Object>() {

				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Map<String, Object> param = new HashMap<String, Object>();
                    param.put("userId",             member.getUserId());
                    param.put("account_type_id",    _accountTypeId);
                    param.put("bizid",              _bizid);
                    param.put("freeze_money",       _freezeMoney );
                    param.put("remark",             "冻结金额");
                    FreezeService.freezeMoney(param);
					return null;
				}
	        });
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
	* 解冻账户金额
	* @param bizFreezenNo  冻结订单号
	* @param memberId      解冻会员
	* @param accountTypeId 解冻账户类型
	* @throws BizException
	 */
    public  void unFreezeMoney(String bizFreezenNo,Long memberId,Long accountTypeId,Long unFreezeMoney)throws BizException{
    	
    	logger.info("freezeMoney start");
		try {
			final String _bizid =bizFreezenNo;
			final Long _accountTypeId=accountTypeId;
			final Long _unFreezeMoney=unFreezeMoney;
			final Member member = new Member(memberId);
			if(member.getUserId() == null || "".equals(member.getUserId())){
				throw new BizException(ErrorCode.USER_NOTEXSIT,"用户不存在");
			}
	        EntityManagerUtil.execute(new TransactionWork<Object>() {
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					 Map<String, Object> unfreeze_param = new HashMap<String, Object>();
				     unfreeze_param.put("userId",            member.getUserId());    
				     unfreeze_param.put("bizid",             _bizid);
				     unfreeze_param.put("account_type_id",    _accountTypeId);
				     unfreeze_param.put("unfreeze_money",     _unFreezeMoney);
				     unfreeze_param.put("remark",            "解冻");
				     FreezeService.unfreezeMoney(unfreeze_param);
					 return null;
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

	public Map<String, Object> getAccountTypeByNo(String accountNo) 
			throws BizException {
		// TODO Auto-generated method stub
		try{
			return QueryService.getAccountType(accountNo);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}





}
