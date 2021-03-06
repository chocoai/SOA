package bps.external.soa;

import java.util.Map;

import org.apache.log4j.Logger;

import bps.common.BizException;
import bps.common.Constant;
import bps.external.soa.process.Extension;

public class BatchDaifuThread extends Thread {
	private static Logger logger = Logger.getLogger(BatchDaifuThread.class);
	private Long applicationId;
	private Long applicationMemberId;
	private Map<String, Object> extParams;
	
	public BatchDaifuThread(Long applicationId, Long applicationMemberId, Map<String, Object> extParams){
		this.applicationId = applicationId;
		this.applicationMemberId = applicationMemberId;
		this.extParams = extParams;
	}
	
	public void run() {
		try {
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + applicationMemberId + ",bizOrderNo=" + null + ",amount=" + null + ",orderType=" + Constant.ORDER_TYPE_BATCH_DAIFU + ",tradeType=" + Constant.TRADE_TYPE_TRANSFER + ",source=" + null + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + null + ",couponList=" + null);

			Map<String, Object> orderEntity = orderEntity = Extension.orderService.applyOrder(applicationId, applicationMemberId, null, null, Constant.ORDER_TYPE_BATCH_DAIFU, Constant.TRADE_TYPE_TRANSFER, null, extParams, null, null, null);
			
			logger.info("applyOrder返回：" + orderEntity);
			
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
