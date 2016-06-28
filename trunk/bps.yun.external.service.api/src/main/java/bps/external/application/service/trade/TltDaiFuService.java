package bps.external.application.service.trade;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import bps.common.BizException;

public interface TltDaiFuService {
	/**
	 * 通联通单笔代付
	 * @param  tradeNo        交易流水号
	 * @param  accountName    开户人
	 * @param  accountNo      卡号
	 * @param  tradeMoney     交易金额
	 * @param  bankCode       银行代码
	 * @param  cardType       卡类型
	 * @param  accountProp    账户属性
	 * @param  extParams		扩展参数
	 * @return Map<String, Object> 结果信息
	 * @throws BizException
	 */
	public Map<String, Object> singleDaiFu(String tradeNo, String accountName, String accountNo, Long tradeMoney, 
		String bankCode, Long cardType, Long accountProp, Map<String, Object> extParams) throws Exception;
		
	/**
	 * 通联通批量代付
	 * @param  tradeNo        交易流水号
	 * @param  tradeMoney     交易金额
	 * @param  detailList     交易详细信息
	 * 				accountName    卡号
	 * 				bankCode       银行代码
	 * 				cardType       卡类型
	 * 				accountProp    账户属性
	 * 				merchantId     商户号
	 * 				subTradeNo     子交易流水号
	 * 				subTradeMoney  子交易金额
	 * 				merchantId     商户号
	 * @param  extParams		扩展参数
	 * @return Map<String, Object> 结果信息
	 * @throws BizException
	 */
	public Map<String, Object> batchDaiFu(String tradeNo, Long tradeMoney, List<Map<String, Object>> detailList, Map<String, Object> extParams) throws Exception;
	
	/**
	 * 通联通交易查询
	 * @param  tradeNo        交易流水号
	 * @param  queryNo     	    需要查询的流水号
	 * @param  extParams		扩展参数
	 * @return Map<String, Object> 结果信息
	 * @throws BizException
	 */
	public Map<String, Object> queryTradeResult(String tradeNo, String queryNo, Map<String, Object> extParams) throws Exception;
	
	/**
	 * 查询通联网关对账数据
	 * @param date 日期 yyyy-MM-dd
	 * @return
	 * @throws Exception
	 */
	public String getCheckAccountDate(String date,String piAppConfObjApplicationStr)throws Exception ;
	
}
