package bps.order;

import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.allinpay.commons.core.util.StringUtils;
import com.kinorsoft.ams.trade.AdjustAccount;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.service.AccountService;
import bps.service.AdjustAccountService;

public class AdjustAccountImpl implements AdjustAccountService {
	private Logger logger = Logger.getLogger(AdjustAccountImpl.class);

	public void adjustAccount(String bizNo, String sysid, Long sourceMemberId, Long sourceAccountTypeId, Long targetMemberId, Long targetAccountTypeId, Long amount, String remark) throws BizException {
		logger.info("=============================AdjustAccountImpl.adjustAccount start===========================");
		logger.info("参数：sourceMemberId=" + sourceMemberId + ",sourceAccountTypeId=" + sourceAccountTypeId + ",Long targetMemberId=" + targetMemberId + ",targetAccountTypeId=" + targetAccountTypeId + ",amount=" + amount);
		
		System.out.println("start");
		
		try{
			//检查数据
			if(StringUtils.isBlank(bizNo)){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数bizNo为空或null");
			}
			if(StringUtils.isBlank(sysid)){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sysid为空或null");
			}
			if(sourceMemberId == null || sourceMemberId == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sourceMemberId为0或null");
			}
			if(sourceAccountTypeId == null || sourceAccountTypeId == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数sourceAccountTypeId为0或null");
			}
			if(targetMemberId == null || targetMemberId == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数targetMemberId为0或null");
			}
			if(targetAccountTypeId == null || targetAccountTypeId == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数targetAccountTypeId为0或null");
			}
			if(amount == null || amount == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数amount为0或null");
			}
			
			//必要的检测
				//源用户和目标用户不能相同
			if(sourceMemberId.equals(targetMemberId)){
				throw new BizException(ErrorCode.OTHER_ERROR, "调账对象和被调账对象不能相同。");
			}
		
				//源用户是否存在或者被锁定
			Map<String, Object> sourceMemberEntity = checkMember(sourceMemberId);
		
				//目标用户是否存在或者被锁定
			Map<String, Object> targetMemberEntity = checkMember(targetMemberId);
			
				//检查是否是同一个应用下的用户
			Long sourceApplicationId = (Long)sourceMemberEntity.get("application_id");
			Long targetApplicationId = (Long)targetMemberEntity.get("application_id");
		
				//检测账户
			List<Map<String, Object>> memberAccountList = checkAccount(sourceMemberId, sourceAccountTypeId, targetMemberId, targetAccountTypeId, amount, sourceApplicationId, targetApplicationId);
			Map<String, Object> sourceMemberAccountEntity = memberAccountList.get(0);
			Map<String, Object> targeMembertAccountEntity = memberAccountList.get(1);
			
			//组装adjustAccount实体数据
			Map<String, String> adjustAccountEntity = new HashMap<String, String>();  
			adjustAccountEntity.put("source_userId", (String)sourceMemberEntity.get("userId"));
			adjustAccountEntity.put("target_userId", (String)targetMemberEntity.get("userId"));
			adjustAccountEntity.put("remark", remark);
			adjustAccountEntity.put("account_type_id", sourceAccountTypeId.toString());
			//adjustAccountEntity.put("account_type_label", sourceAccountEntity.get("name") != null ? ((Long)sourceAccountEntity.get("name")).toString() : null);
			adjustAccountEntity.put("trade_money", amount.toString());
			adjustAccountEntity.put("source_member_id", sourceMemberId.toString());
			adjustAccountEntity.put("source_member_label", sourceMemberEntity.get("name") != null ? sourceMemberEntity.get("name").toString() : null);
			adjustAccountEntity.put("target_member_id", targetMemberId.toString());
			adjustAccountEntity.put("target_member_label", targetMemberEntity.get("name") != null ? targetMemberEntity.get("name").toString() : null);
			adjustAccountEntity.put("source_name", sourceMemberEntity.get("name") != null ? sourceMemberEntity.get("name").toString() : null);
			adjustAccountEntity.put("target_name", targetMemberEntity.get("name") != null ? targetMemberEntity.get("name").toString() : null);
			adjustAccountEntity.put("source_memberNo", sourceMemberEntity.get("memberNo") != null ? sourceMemberEntity.get("memberNo").toString() : null);
			//adjustAccountEntity.put("auditor", sourceMemberEntity.get(""));
			//adjustAccountEntity.put("auditor_date", sourceMemberEntity.get(""));
			//adjustAccountEntity.put("operator", sourceMemberEntity.get(""));
			adjustAccountEntity.put("target_memberNo", targetMemberEntity.get("memberNo") != null ? targetMemberEntity.get("memberNo").toString() : null);
			adjustAccountEntity.put("bizid", bizNo);
			adjustAccountEntity.put("target_account_type_id", targetAccountTypeId.toString());
			//adjustAccountEntity.put("target_account_type_label", targetAccountEntity.get("name") != null ? ((Long)targetAccountEntity.get("name")).toString() : null);
			adjustAccountEntity.put("WF_State", "4");
			
			//向数据库写入数据，同时触发自定义转账
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			final Map<String, String> _adjustAccountEntity = adjustAccountEntity;
			final String _sysid = sysid;
			EntityManagerUtil.execute(new TransactionWork<String>() {
	        	@Override
				public boolean before(Session session) throws Exception {
					return true;
				}
	        	
				@Override
				public String doTransaction(Session session, Transaction tx)
						throws Exception {
					Map<String, Object> _adjustAccountEntityObj = DynamicEntityService.createEntity("AMS_AdjustAccount", _adjustAccountEntity, null);
					_adjustAccountEntityObj.put("orgNo", _sysid);
					logger.info("orgNo1=" + _sysid);
					AdjustAccount adjustAccount = new AdjustAccount(_adjustAccountEntityObj, session);
		            adjustAccount.run();
		            
		            return null;
				}
	        });
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE; 
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 检测用户是否存在或者被锁定
	 * @param memberId
	 * @return
	 */
	private Map<String, Object> checkMember(Long memberId) throws BizException{
		try{
			System.out.println("memberId=" + memberId);
			Map<String, Object> memberEntity = DynamicEntityService.getEntity(memberId, "AMS_Member");
			System.out.println("memberEntity=" + memberEntity);
			if(memberEntity == null || memberEntity.isEmpty()){
				throw new BizException(ErrorCode.USER_NOTEXSIT, "用户不存在。");
			}
			
			if(!Constant.USER_STATE_ACTIVATION.equals((Long)memberEntity.get("user_state"))){
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "用户已经被锁定。");
			}
			
			return memberEntity;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE; 
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}

	/**
	 * 检查账户集
	 * @param sourceMemberId
	 * @param sourceAccountTypeId
	 * @param targetMemberId
	 * @param targetAccountTypeId
	 * @param amount
	 * @throws BizException
	 */
	private List<Map<String, Object>> checkAccount(Long sourceMemberId, Long sourceAccountTypeId, Long targetMemberId, Long targetAccountTypeId, Long amount, Long sourceApplicationId, Long targetApplicationId) throws BizException{
		try{
			List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
			
			//检查账户是否存在
			AccountServiceImpl accountServiceImpl = new AccountServiceImpl();
			Map<String, Object> sourceAccountEntity = accountServiceImpl.getMemberAccountByType(sourceMemberId, sourceAccountTypeId);
			if(sourceAccountEntity == null || sourceAccountEntity.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT, "账户不存在");
			}
			Map<String, Object> targetAccountEntity = accountServiceImpl.getMemberAccountByType(targetMemberId, targetAccountTypeId);
			if(targetAccountEntity == null || targetAccountEntity.isEmpty()){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_NOTEXSIT, "账户不存在");
			}
			
			ret.add(sourceAccountEntity);
			ret.add(targetAccountEntity);
			
			//检查账户集类型。
			Map<String, Object> sourceAccountTypeEntity = DynamicEntityService.getEntity(sourceAccountTypeId, "AMS_AccountType");
			Map<String, Object> targetAccountTypeEntity = DynamicEntityService.getEntity(targetAccountTypeId, "AMS_AccountType");
			//1、源账户集为中间账户，则目标账户只能为应用自定义的现金账户集。
			if(Constant.ACCOUNT_NO_STANDARD_BALANCE.equals((String)sourceAccountTypeEntity.get("codeNo"))){
				if(!Constant.ACCOUNT_KIND_TG.equals((Long)targetAccountTypeEntity.get("account_kind"))){
					throw new BizException(ErrorCode.OTHER_ERROR, "账户集类型错误。");
				}
			}
			//2、源账户集为应用自定义的账户集，则目标账户要和源账户集类型一致。
			else if(Constant.ACCOUNT_KIND_TG.equals((Long)sourceAccountTypeEntity.get("account_kind"))){
				if(!((String)sourceAccountTypeEntity.get("codeNo")).equals((String)targetAccountTypeEntity.get("codeNo"))){
					throw new BizException(ErrorCode.OTHER_ERROR, "账户集不一致。");
				}
				
				if(!sourceApplicationId.equals(targetApplicationId)){
					throw new BizException(ErrorCode.OTHER_ERROR, "账户集不一致。");
				}
			}
			else{
				throw new BizException(ErrorCode.OTHER_ERROR, "账户集类型错误。");
			}
			
			//检查源账户集金额是否足够
			Long acountAmount = (Long)sourceAccountEntity.get("amount");
			Long freezenAccountAmount = (Long)targetAccountEntity.get("freeze_amount");
			if((acountAmount - freezenAccountAmount) < amount){
				throw new BizException(ErrorCode.OTHER_ERROR, "账户集类型错误。");
			}
			
			logger.info("ret:" + ret);
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE; 
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}
}
