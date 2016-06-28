package bps.goods;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;


import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;

public class Subject extends Goods {
	private Logger logger = Logger.getLogger(Subject.class);

	/**
	 * 录入
	 */
	@Override
	public Map<String, Object> enter(Long applicationId, Long borrowerId, Long goodsType, String bizGoodsNo, String goodsName, String goodsDescription, String goodsParams, String showUrl, String extendInfo) throws BizException {
		logger.info("==========================Subject.enter start==============================");
		logger.info("参数applicationId=" + applicationId + ",borrowerId=" + borrowerId + ",goodsType=" + goodsType + ",bizGoodsNo=" + bizGoodsNo + ",goodsName=" + goodsName + ",goodsDescription=" + goodsDescription + ",goodsParams=" + goodsParams + ",showUrl=" + showUrl + ",extendInfo=" + extendInfo);
		
		try{
			//检查
				//检查商品类型
			if(!Constant.GOODS_TYPE_SUBJECT.equals(goodsType)){
				throw new BizException(ErrorCode.OTHER_ERROR, "商品类型错误。");
			}
			
				//检查借款人
			Map<String, Object> borrowerEntity = DynamicEntityService.getEntity(borrowerId, "AMS_Member");
			if(borrowerEntity == null || borrowerEntity.isEmpty()){
				throw new BizException(ErrorCode.USER_NOTEXSIT, "借款方不存在。");
			}
			String borrowerName = (String)borrowerEntity.get("name");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				//检查标的参数
			JSONObject subjectParams = new JSONObject(goodsParams);
			Long beginDate = null;
			Long endDate = null;
			Long amount;
			Long totalAmount;
			Double annualYield;
			Long repayPeriodNumber = null;
			Long repayType;
			Long guaranteeType;
			Long investmentHorizon;
			Long investmentHorizonScale;
			Long highestAmount = null;
			JSONArray repayInfo = null;

			if(subjectParams.isNull("amount")){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "amount参数不正确。");
			}else{
				amount = subjectParams.getLong("amount");
				if(amount <= 0)
					throw new BizException(ErrorCode.OTHER_ERROR, "标的金额不能小于等于零。");
			}
			
			if(subjectParams.isNull("totalAmount")){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "totalAmount参数不正确。");
			}else{
				totalAmount = subjectParams.getLong("totalAmount");
				if(totalAmount <= 0)
					throw new BizException(ErrorCode.OTHER_ERROR, "应还款总额不能小于等于零。");
			}
			
			if(totalAmount < amount)
				throw new BizException(ErrorCode.OTHER_ERROR, "应还款总额不能小于标的金额。");
			
			if(subjectParams.isNull("annualYield")){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "annualYield参数不正确。");
			}else{
				annualYield = (Double)subjectParams.get("annualYield");
			}
			
			if(!subjectParams.isNull("repayPeriodNumber")){
				repayPeriodNumber = subjectParams.getLong("repayPeriodNumber");
			}
			
			if(subjectParams.isNull("repayType")){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "repayType参数不正确。");
			}else{
				repayType = subjectParams.getLong("repayType");
				if(!Constant.REPAY_TYPE_DQHB.equals(repayType) && !Constant.REPAY_TYPE_MYFX.equals(repayType) && !Constant.REPAY_TYPE_DEBJ.equals(repayType) && !Constant.REPAY_TYPE_DEBX.equals(repayType) && !Constant.REPAY_TYPE_OTHER.equals(repayType)){
					throw new BizException(ErrorCode.OTHER_ERROR, "还款类型不正确。");
				}
			}
			if ( !subjectParams.isNull("highestAmount") ){
				highestAmount = subjectParams.optLong("highestAmount");
			}
			
			if(subjectParams.isNull("guaranteeType")){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "guaranteeType参数不正确。");
			}else{
				guaranteeType = subjectParams.getLong("guaranteeType");
				if(!Constant.GUARANTEE_TYPE_APPLICATION.equals(guaranteeType) && !Constant.GUARANTEE_TYPE_INSURANCE.equals(guaranteeType) && !Constant.GUARANTEE_TYPE_BANK.equals(guaranteeType) && !Constant.GUARANTEE_TYPE_COMPANY.equals(guaranteeType) && !Constant.GUARANTEE_TYPE_NONE.equals(guaranteeType) && !Constant.GUARANTEE_TYPE_OTHER.equals(guaranteeType)){
					throw new BizException(ErrorCode.OTHER_ERROR, "担保类型不正确。");
				}
			}
			
			if(!subjectParams.isNull("repayInfo")){
				repayInfo = (JSONArray)subjectParams.get("repayInfo");
			}
			
			if(subjectParams.isNull("investmentHorizon")){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "investmentHorizon参数不正确。");
			}else{
				investmentHorizon = subjectParams.optLong("investmentHorizon");
				if(investmentHorizon <= 0)
					throw new BizException(ErrorCode.OTHER_ERROR, "投资期限不能小于等于零");
			}
			
			if(subjectParams.isNull("investmentHorizonScale")){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "investmentHorizonScale参数不正确。");
			}else{
				investmentHorizonScale = subjectParams.optLong("investmentHorizonScale");
				if(investmentHorizonScale != 1 && investmentHorizonScale != 2)
					throw new BizException(ErrorCode.OTHER_ERROR, "投资期限单位类型不正确。");
			}
			
			final Long _applicationId = applicationId;
			final String _bizGoodsNo = bizGoodsNo;
			//标的信息
			final Map<String, String> _entity = new HashMap<>();
			_entity.put("amount", amount.toString());
			_entity.put("biz_subject_no", bizGoodsNo);
			_entity.put("application_id", applicationId.toString());
			_entity.put("borrower_id", borrowerId.toString());
			_entity.put("borrower_label", borrowerName);
			_entity.put("name", goodsName);
			_entity.put("total_amount", totalAmount.toString());
			_entity.put("annual_yield", annualYield.toString());
			if(StringUtils.isNotBlank(subjectParams.optString("beginDate"))){
				beginDate = sdf.parse(subjectParams.optString("beginDate")).getTime();
				_entity.put("begin_date", String.valueOf(beginDate));
				logger.info("begin_date:"+String.valueOf(beginDate));
			}
			if(StringUtils.isNotBlank(subjectParams.optString("endDate"))){
				endDate = sdf.parse(subjectParams.optString("endDate")).getTime();
				_entity.put("end_date", String.valueOf(endDate));
				logger.info("end_date:"+String.valueOf(endDate));
			}
			_entity.put("repay_period_number", String.valueOf(repayPeriodNumber));
			_entity.put("repay_type", String.valueOf(repayType));
			_entity.put("guarantee_type", String.valueOf(guaranteeType));
			_entity.put("url", showUrl);
			_entity.put("description", goodsDescription);
			_entity.put("create_time", String.valueOf(new Date().getTime()));
			logger.info("create_time:"+String.valueOf(new Date().getTime()));
			_entity.put("extend_info", extendInfo);
			_entity.put("investment_horizon", investmentHorizon.toString());
			_entity.put("investment_horizon_scale", investmentHorizonScale.toString());
			if( highestAmount != null ){
				_entity.put("highest_amount", highestAmount.toString());
			}
			final JSONArray _repayInfo = repayInfo;
			
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			
			//开始数据库操作
			Map<String, Object> ret = EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>(){
				@Override
				public boolean before(Session session) throws Exception{
					Map<String, Object> subjectEntity = getGoods(session, _applicationId, _bizGoodsNo);
					if(subjectEntity == null || subjectEntity.isEmpty()){
						return true;
					}else{
						throw new Exception("商户标的号已经存在");
					}
				}
				
				@Override
				public Map<String, Object> doTransaction(Session session,
						Transaction arg1) throws Exception {
					//标的录入
					Map<String, Object> temp = DynamicEntityService.createEntity("AMS_Subject", _entity, null);
					Long id = (Long)temp.get("id");
					String subjectNo = generateGoodsNo(id);
					String queryStr = "update AMS_Subject set subject_no = :subjectNo where id = :id";
					Query query = session.createQuery(queryStr);
					query.setParameter("subjectNo", subjectNo);
					query.setParameter("id", id);
					if(query.executeUpdate() != 1){
						throw new Exception("录入标的时出错。");
					}
					
					//录入待还款信息
					if(_repayInfo != null){
						for(int i = 0; i < _repayInfo.length(); i++){
							Map<String, String> repayInfoMap = new HashMap<>();
							
							JSONObject repayInfoObj = (JSONObject)_repayInfo.get(i);
							Long period, amount, interest, principal, overplusPrincipal;
							if(repayInfoObj.isNull("period")){
								throw new BizException(ErrorCode.PARAM_ERROR, "参数repayInfo出错。");
							}else{
								period = repayInfoObj.optLong("period");
							}
							if(repayInfoObj.isNull("amount")){
								throw new BizException(ErrorCode.PARAM_ERROR, "参数repayInfo出错。");
							}else{
								amount = repayInfoObj.optLong("amount");
							}
							if(repayInfoObj.isNull("interest")){
								throw new BizException(ErrorCode.PARAM_ERROR, "参数repayInfo出错。");
							}else{
								interest = repayInfoObj.optLong("interest");
							}
							if(repayInfoObj.isNull("principal")){
								throw new BizException(ErrorCode.PARAM_ERROR, "参数repayInfo出错。");
							}else{
								principal = repayInfoObj.optLong("principal");
							}
							if(repayInfoObj.isNull("overplusPrincipal")){
								throw new BizException(ErrorCode.PARAM_ERROR, "参数repayInfo出错。");
							}else{
								overplusPrincipal = repayInfoObj.optLong("overplusPrincipal");
							}
							
							repayInfoMap.put("goods_type", Constant.GOODS_TYPE_SUBJECT.toString());
							repayInfoMap.put("goods_id", id.toString());
							repayInfoMap.put("period", period.toString());
							repayInfoMap.put("amount", amount.toString());
							repayInfoMap.put("interest", interest.toString());
							repayInfoMap.put("principal", principal.toString());
							repayInfoMap.put("overplusPrincipal", overplusPrincipal.toString());
							
							Map<String, Object> repayInfoEntity = DynamicEntityService.createEntity("AMS_RepayInfo", repayInfoMap, null);
							if(repayInfoEntity == null || repayInfoEntity.isEmpty()){
								throw new BizException(ErrorCode.OTHER_ERROR, "录入待还款信息时出错。");
							}
						}
					}
					
					Map<String, Object> subjectInfo = new HashMap<>();
					subjectInfo.put("goodsNo", subjectNo);
					subjectInfo.put("bizGoodsNo", _bizGoodsNo);
					return subjectInfo;
				}
			});
			
			logger.info("返回ret=" + ret);
			logger.info("==========================Subject.enter end==============================");
			return ret;
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * 获取标的
	 * @param session			session
	 * @param applicationId		应用ID
	 * @param bizSubjectNo		业务系统标的编号
	 * @return	标的信息
	 * @throws BizException
	 */
	public Map<String, Object> getGoods(Session session, Long applicationId, String bizSubjectNo) throws BizException{
		logger.info("=====================Subject.getGoods start=============================");
		logger.info("参数applicationId=" + applicationId + ",bizSubjectNo=" + bizSubjectNo);
		
		try{
			String queryStr = "from AMS_Subject where biz_subject_no=:bizSubjectNo and application_id=:applicationId";
			Query query = session.createQuery(queryStr);
			query.setParameter("bizSubjectNo", bizSubjectNo);
			query.setParameter("applicationId", applicationId);
			List goodsList = query.list();
			if(goodsList.size() == 0){
				return null;
			}
			
			Map<String, Object> ret = (Map<String, Object>)goodsList.get(0);
			
			logger.info("返回ret=" + ret);
			logger.info("=====================Subject.getGoods end=============================");
			return ret;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
    
	/**
	 * 修改标的
	 */
	public void modifyGoods(Session session,Map<String,Object>param)throws Exception{
		logger.info("=====================Subject.modifyGoods start=============================");
		logger.info("参数param=" + param );
		Long applicationId = (Long)param.get("applicationId");
		String bizGoodsNo = (String)param.get("bizGoodsNo"); 
		Date beginDate = (Date)param.get("beginDate");
		Date endDate = (Date)param.get("endDate");
		if(beginDate == null && endDate == null)
			throw new BizException(ErrorCode.PARAM_ERROR_NULL, "修改标的起息日和到期日不能同时为空");
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("update AMS_Subject ");
			if(beginDate != null){
				sb.append("set begin_date = :begin_date ");
				if(endDate != null)
					sb.append(",end_date = :end_date ");
			}else{
				if(endDate != null){
					sb.append("set end_date = :end_date ");
				}
			}
			sb.append("where application_id = :application_id and biz_subject_no = :biz_subject_no ");
			Query query = session.createQuery(sb.toString());
			query.setParameter("application_id", applicationId);
			query.setParameter("biz_subject_no", bizGoodsNo);
			if(beginDate != null)
				query.setParameter("begin_date", beginDate);
			if(endDate != null)
				query.setParameter("end_date", endDate);
			query.executeUpdate();
			logger.info("=====================Subject.modifyGoods end=============================");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}		
	}
}
