package bps.external.soa;

import java.util.Map;

import org.apache.log4j.Logger;

import bps.common.BizException;
import bps.common.Constant;
import bps.external.soa.process.Extension;

public class DaifuThread extends Thread {
	private static Logger logger = Logger.getLogger(DaifuThread.class);
	private Long applicationId;
	private Long applicationMemberId;
	private String bizOrderNo;
	private Long amount;
	private Map<String, Object> extParams;
	
	public DaifuThread(Long applicationId, Long applicationMemberId, String bizOrderNo, Long amount, Map<String, Object> extParams){
		this.applicationId = applicationId;
		this.applicationMemberId = applicationMemberId;
		this.bizOrderNo = bizOrderNo;
		this.amount = amount;
		this.extParams = extParams;
	}
	
	public void run() {
		try {
			logger.info("applyOrder参数：applicationId=" + applicationId + ",memberId=" + applicationMemberId + ",bizOrderNo=" + bizOrderNo + ",amount=" + amount + ",orderType=" + Constant.ORDER_TYPE_DAIFU + ",tradeType=" + Constant.TRADE_TYPE_TRANSFER + ",source=" + null + ",extParams=" + extParams + ",accountList=" + null + ",payInterfaceList=" + null + ",couponList=" + null);
			Map<String, Object> orderEntity = Extension.orderService.applyOrder(applicationId, applicationMemberId, bizOrderNo, amount, Constant.ORDER_TYPE_DAIFU, Constant.TRADE_TYPE_TRANSFER, null, extParams, null, null, null);
			logger.info("applyOrder返回：" + orderEntity);
			
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
