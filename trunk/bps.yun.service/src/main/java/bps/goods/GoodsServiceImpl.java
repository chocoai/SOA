package bps.goods;

import ime.core.services.DynamicEntityService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.util.Logger;





import org.hibernate.Session;
import org.hibernate.Transaction;

import com.allinpay.commons.core.util.StringUtils;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.common.BizException;
import bps.common.ErrorCode;
import bps.service.GoodsService;


public class GoodsServiceImpl implements GoodsService {
	private Logger logger = Logger.getLogger(GoodsServiceImpl.class);

	public Map<String, Object> enter(Long applicationId, Long borrowerId, Long goodsType, String bizGoodsNo, String goodsName, String goodsDescription, String goodsParams, String showUrl, String extendInfo) throws BizException {
		logger.info("==========================GoodsServiceImpl.enter start==============================");
		logger.info("参数applicationId=" + applicationId + ",borrowerId=" + borrowerId + ",goodsType=" + goodsType + ",bizGoodsNo=" + bizGoodsNo + ",goodsName=" + goodsName + ",goodsDescription=" + goodsDescription + ",goodsParams=" + goodsParams + ",showUrl=" + showUrl + ",extendInfo=" + extendInfo);
		
		try{
			//检查参数
			if(applicationId == null || applicationId == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "applicationId参数错误。");
			}
			if(borrowerId == null || borrowerId == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "borrowerId参数错误。");
			}
			if(goodsType == null || goodsType == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "goodsType参数错误。");
			}
			if(bizGoodsNo == null || "".equals(bizGoodsNo)){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "bizGoodsNo参数错误。");
			}
			if(goodsName == null || "".equals(goodsName)){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "goodsName参数错误。");
			}
			if(goodsParams == null || "".equals(goodsParams)){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "goodsParams参数错误。");
			}
			
			//检查applicationId
			Map<String, Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
			if(applicationEntity == null || applicationEntity.isEmpty()){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
			}
			
			Goods goods = GoodsFactory.GetGoods(goodsType);
			Map<String, Object> ret = goods.enter(applicationId, borrowerId, goodsType, bizGoodsNo, goodsName, goodsDescription, goodsParams, showUrl, extendInfo);
			
			logger.info("返回ret=" + ret);
			logger.info("==========================GoodsServiceImpl.enter end==============================");
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
	 * 查询修改商品
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> queryModifyGoods(Long applicationId,Long memberId,String bizGoodsNo,Long goodsType,Date beginDate,Date endDate)throws Exception{
		logger.info("==========================GoodsServiceImpl.queryModifyGoods start==============================");
		logger.info("参数applicationId=" + applicationId + ",memberId=" + memberId + ",bizGoodsNo=" + bizGoodsNo + ",goodsType=" + goodsType + ",beginDate=" + beginDate + ",endDate=" + endDate);
		
		try{
			//检查参数
			if(applicationId == null || applicationId == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数错误。");
			}
			if(memberId == null || memberId == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数错误。");
			}
			if(goodsType == null || goodsType == 0){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数错误。");
			}
			if(bizGoodsNo == null || "".equals(bizGoodsNo)){
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "参数错误。");
			}
			
			//检查applicationId
			Map<String, Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
			if(applicationEntity == null || applicationEntity.isEmpty()){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
			}
			
			final Long _applicationId = applicationId;
			final String _bizGoodsNo = bizGoodsNo;
			final Date _beginDate = beginDate;
			final Date _endDate = endDate;
			final Map<String,Object> param = new HashMap<String,Object>();
			param.put("applicationId", _applicationId);
			param.put("bizGoodsNo", _bizGoodsNo);
			param.put("beginDate", _beginDate);
			param.put("endDate", _endDate);
			final Goods goods = GoodsFactory.GetGoods(goodsType);
			
			Map<String,Object> goodsEntity  = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
				@Override
				public Map<String,Object> doTransaction(Session session,
						Transaction tx) throws Exception {
					if(_beginDate != null || _endDate != null){
						goods.modifyGoods(session,param);
						
						session.flush();
						session.clear();
					}					
					
					return goods.getGoods(session, _applicationId, _bizGoodsNo);
				}
			});
			return goodsEntity;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}

	/**
	 * 查询商户信息
	 * @param bizGoodsNo	商户编号
	 * @param goodsType		商户类型
	 * @return	商品信息
	 * @throws BizException
     */
	public Map<String, Object> queryGoods( Long applicationId,Long goodsType, String bizGoodsNo ) throws Exception {
		logger.info("==========================GoodsServiceImpl.queryGoods start==============================");
		logger.info("参数bizGoodsNo=" + bizGoodsNo + ",applicationId=" + applicationId+",goodsType:"+goodsType);

		final Goods goods = GoodsFactory.GetGoods(goodsType);
		final Long _applicationId = applicationId;
		final String _bizGoodsNo = bizGoodsNo;

		Map<String,Object> goodsEntity  = EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
			@Override
			public Map<String,Object> doTransaction(Session session,
													Transaction tx) throws Exception {


				return goods.getGoods(session, _applicationId, _bizGoodsNo);
			}
		});
		return goodsEntity;
	}
}
