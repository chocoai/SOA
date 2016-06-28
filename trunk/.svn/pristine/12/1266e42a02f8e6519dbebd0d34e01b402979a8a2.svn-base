package bps.service;

import java.util.Date;
import java.util.Map;

import bps.common.BizException;

/**
 * 商品
 * @author Administrator
 *
 */
public interface GoodsService {
	Map<String, Object> enter(Long applicationId, Long borrowerId, Long goodsType, String bizGoodsNo, String goodsName, String goodsDescription, String goodsParams, String showUrl, String extendInfo) throws BizException;
	
	Map<String,Object> queryModifyGoods(Long applicationId,Long memberId,String bizGoodsNo,Long goodsType,Date beginDate,Date endDate) throws Exception;

	/**
	 * 查询商户品
	 * @param bizGoodsNo		商户编号
	 * @param applicationId		应用类型
	 * @param goodsType			商品类型
	 * @return	商户信息
	 * @throws BizException
     */
	Map<String, Object> queryGoods( Long applicationId,Long goodsType, String bizGoodsNo )throws Exception;
}
