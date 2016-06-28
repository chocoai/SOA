package bps.service;

import java.util.List;
import java.util.Map;

import apf.util.BusinessException;
import bps.common.BizException;

/**
 *
* 订单类服务接口
* @author huadi
* @date 2015年9月16日 上午10:06:51
 */
public interface OrderService {


	/****
	 *
	* @Title: applyOrder
	* @Description: TODO 订单申请接口
	* @param applicationId  应用ID
	* @param memberId   会员ID
	* @param bizOrderNo	(String 长度 100)  业务订单号
	* @param money       金额
	* @param orderType   订单类型
	* @param tradeType   交易类型
	* @param source		 来源
	* @param extParams
	* 	 		bankCardId 银行卡编号
	 *			accountCodeNo	账户类型编号
	 *			phone	充值的手机号
	 *			subTradeType	子交易类型
	 *			transactionType 支付类型
	 *			merchantOrderNo 外部交易号
	 *			orderName 订单名称
	 *			remark 备注
	 *			memberIp 用户ip
	 *			orgUserId 外部会员唯一标识
	 *			orgNo 机构号
	 *			extendInfo 扩展信息
	 *			qrCode 二维码
	 *			outSerialNumber 外部流水号
	 *			accountNo 银行卡号
	 *			enAccountNo 加密后的银行卡号
	 *			bankCode 银行代码
	 *			accountName 开户名
	 *			bankName 银行名称
	 * @param accountList  内部账号
	 * 成员对象属性Map
	 * 				accountCodeNo 	String 	账户类型编号
	 * 				tradeMoney		Long 	交易金额
	 * 				seqNo			Long 	顺序号
	 * @param payInterfaceList  支付通道
	 * 					 成员当对象Map
	 * 					seqNo			Long 	顺序号
	 * 					cardList 卡对象列表	多条传入列表中
	 * @throws BizException    设定文件
	*  @return Map<String,Object>    返回类型
	 */
	Map<String, Object> applyOrder(
			Long applicationId,
			Long memberId,
			String bizOrderNo,
			Long money,
			Long orderType,
			Long tradeType,
			Long source,
			Map<String, Object> extParams,
			List accountList,
			List payInterfaceList,
			List couponList
			) throws BizException;
	/***
	 *
	* @Title: confirmPay
	*  TODO确认支付接口
	* @param memberId		会员ID
	* @param orderNo		订单号
	* @param oriTraceNum	交易号
	* @param user_ip		用户IP
	* @param phone			手机
	* @param phoneCode		手机验证码
	* @throws BizException    设定文件
	* @return Map<String,Object>    返回类型
	 */
	Map<String, Object> confirmPay(Long memberId, String orderNo, String oriTraceNum, String user_ip, String phone, String phoneCode) throws BizException;

	/**
	 * 平台转账
	 * @param sourceAccountSetNo 源账户集编号
	 * @param targetMemberId     目标用户id
	 * @param targetAccountSetNo 目标账户集编号
	 * @param amount             金额
	 * @param remark             备注
	 * @param extendInfo         扩展信息
	 * @return
	 * @throws BizException
	 */
	Map<String, Object> applicationTransfer(String bizTransferNo,
						Long applicationId,
						String sourceAccountSetNo,
                        Long targetMemberId,
                        String targetAccountSetNo,
                        Long amount,
                        String remark,
                        String extendInfo) throws BizException;


	/***
	 *
	* @Title: getOrder
	*  TODO获取订单接口
	* @param applicationId		应用ID
	* @param bizOrderNo			业务系统订单号
	* @throws BizException    设定文件
	* @return Map<String,Object>    返回类型
	 */
	Map<String, Object> getOrder(Long applicationId,String bizOrderNo) throws BizException;

	/**
	 *
	* @Title: getOrder
	*  TODO获取订单
	* @param _orderNo		订单号
	* @throws BizException    设定文件
	* @return Map<String,Object>    返回类型
	 */
	Map<String, Object> getOrder(String _orderNo) throws BizException;




	/**
     * 获取指令集
     * @param orderNo		订单号
     * @throws BizException
     */
    List getCommands(String orderNo)throws BizException;


	/**
	 * 订单完成
	 * @param orderNo	订单号
	 * @param param		其他参数
	 * @throws BizException
	 */
	void completeOrder(String orderNo, Map<String, Object> param) throws BizException;



	/**
	 * 获取机构信息
	 * @param orgNo	机构号
	 * @return 机构信息
	 * @throws BizException
	 */
	Map<String, Object> getOrg(String orgNo) throws BizException;

	/**
	 * 获取应用列表
	 * @Title: getOrgList
	 * TODO获取应用
	 * @throws BizException    设定文件
	 * @return List    返回类型
	 */
	List getOrgList()throws BizException;
	/**
	 * its签约申请
	 * @param bankCode 银行代码
	 * @param accountName 开户名
	 * @param cardType 卡类型
	 * @param accountNo 卡号
	 * @param phone 手机
	 * @param bankName 银行名称
	 * @param memberId 会员编号
	 * @param payType 1正常，3免验证码
	 * @param extParams 扩展参数
	 * 			acctValiddate   有效期  信用卡属性
	 * 			cvv2			cvv2  信用卡属性
	 * 			identityCardNo	身份证
	 * 			identityCardNoEncrypt  身份证加密码
	 * 			identityCardNoMask  身份证掩码
	 * 			cnlMchtName			商户名称
	 * 			cnlMchtType			商户类型
	 * 			cnlMchtId			id 应用ID
	 * @return Map<String, String>
	 * 			TRANS_DATE			交易日期
	 * 			TRACE_NUM			交易流水号
	 * 			RET_MSG				返回信息
	 * 			SEND_SMS			消息发送
	 * 			RET_CODE			返回编码
	 * 			ITS_TRANS_DATE
	 * 			ITS_TRANS_TIME
	 * 			ITS_TRACE_NUM
	 * 			CNL_ID
	 * @throws BizException
	 */
	Map<String, String> itsSignApply(String bankCode, String accountName, Long cardType,
			String accountNo,  String phone,
			String bankName, Long memberId, Long payType, Map<String, Object> extParams) throws BizException;

	/**
	 * its签约确认
	 * @param oriTraceNum 原交易号
	 * @param oriTransDate 原交易日期
	 * @param memberId 会员编号
	 * @param verifyCode 手机验证码
	 * @param payType 1正常，，3免验证码，
	 *  返回Map<String, String>
	 * 			RET_MSG
	 * 			RET_CODE
	 * 			ITS_TRANS_DATE
	 * 			ITS_TRANS_TIME
	 * 			ITS_TRACE_NUM
	 * 			CONTRACT_NO
	 * 			BANKCARD_ID
	 * 			CNL_ID
	 * @throws BizException
	 */
	 void itsSignACK(String oriTraceNum, String oriTransDate, Long memberId, String verifyCode, Long payType) throws BizException;


	/**
	 * 修改订单操作ip
	 * @param orderNo 订单号
	 * @param memberIp 用户IP
	 * @throws BizException
	 */
	void updateOrderIP(String orderNo, String memberIp) throws BizException;

	/**
	 * 获取its银行代码
	 * @param bankCode 银行代码
	 * @return String its银行代码
	 * @throws BizException
	 */
	String getITSBankCode(String bankCode) throws BizException;


	/**
	 * 设置its银行短信来源
	 * @param isSmsVerify 是否系统发送短信
	 * @param bankCode 银行代码
	 * @throws BizException
	 */
	void modifyITSBankSMSSender(Boolean isSmsVerify, String bankCode) throws BizException;


	/**
	 *  外部支付完成
	 * @param param
	 * 				orderNo 订单号
	 * 				orderMoney 订单金额
	 * 				outTradeId 外部交易流水号
	 * 				payChannelNo 支付渠道号
	 * 				payInterfaceNo 支付通道号
	 * 				tradeTime 交易时间
	 * @throws BizException
	 */
	void payChannelDeposit(Map<String, Object> param)throws BizException;



	/**
	 *
	* 检测机构手续费金额
	* @param orderNo	订单
	* @throws BizException
	 */
	void checkAgencyFees(String orderNo)throws BizException;

	/**
	 *
	* 查询账户变更日志列表
	* @param memberId  会员id(Long)  必填
	* @param param     account_type_id(Long)  账户类型                非必填
	* 				   chg_time_start(Date) 账户变更开始时间  非必填
	* 				   chg_time_end(Date)   账户变更结束时间   非必填
	* @param start  开始查询条数  从0开始
	* @param end    结束查询条数
	* @return  账户变更日志
	* @throws BizException
	 */
	List<Map<String,Object>> getAccountChgDetailList(Long memberId,Map<String,Object> param,Long start,Long end)throws BizException;
	/**
	 *
	* 查询账户变更日子总记录数
	* @param memberId	会员ID
	* @param param		参数
	* @return 记录数
	* @throws BizException
	 */
	Long getAccountChgDetailCount(Long memberId,Map<String,Object> param)throws BizException;

	/**
	 *
	* 查询支付订单列表（交易记录）
	* @param applicationId  应用Id 必传
	* @param param          member_id 会员ID 非必传
	* 						order_type 订单类型  非必传
	* 						order_state 订单状态  非必传
	* 						orderNo 订单编号    非必传
	* 						create_time_start(Date) 订单创建开始时间
	* 						create_time_end(Date)
	* @param start	开始查询条数  从0开始
	* @param end	结束查询条数
	* @return 支付订单列表
	* @throws BizException
	 */
	List<Map<String,Object>> getPayOrderList(Long applicationId,Map<String,Object> param,Long start,Long end)throws BizException;
	/**
	 *
	* 查询支付订单总条数（交易记录）
	* @param applicationId		应用ID
	* @param param				参数
	* @return 支付订单总条数
	* @throws BizException
	 */
	Long getPayOrderCount(Long applicationId,Map<String,Object> param)throws BizException;
	/**
	 *
	* 查询支付订单明细（交易详情）
	* @param orderId	订单ID
	* @return	支付明细
	* @throws BizException
	 */
	List<Map<String,Object>> getPayDetailList(Long orderId)throws BizException;




	/**
	 * MD5签名
	 * @param param	参数
	 * @return 签名后内容
	 * @throws BizException
	 * @author xul
	 */
	String getCertPaySign(Map<String,Object> param)throws BizException;

	/**
	 * 获取所有指令
	 * @param orderNo	订单号
	 * @return	指令列表
	 * @throws BizException
	 */
	List<Map<String, Object>> getAllCommands(String orderNo) throws BizException;

	/**
	 * 查询标得订单
	 * @param order_type		订单类型
	 * @param biz_trade_code	交易码
	 * @param goodsType			商品类型
	 * @param goodsNo			商品编码
	 * @return 标得订单列表
	 * @throws Exception
	 */
	List<Map<String, Object>> getOrderListByGoods(Long order_type, String biz_trade_code, Long goodsType, String goodsNo, Long orderState)throws Exception;

	Map<String,Object>  statistics(Long application) throws BizException ;

	/**
	 * 查询pos订单
	 * @param posCode	pos订单号
	 * @return	订单实体
     */
	Map<String, Object> getOrderByPos(String posCode) throws Exception;

	/**
	 * 设置pos订单信息(查询)
	 * @param posOrder	更新信息
	 *                  paycode			支付码
	 *                  terminal_id		终端号
	 *                  mcht_cd			商户号
	 * @throws Exception
     */
	void setPosOrder(Map<String, Object> posOrder)throws Exception;

	/**
	 * 设置pos订单信息（支付完成）
	 * @param posOrder	更新信息
	 *                  paycode			支付码
	 *                  pay_type		支付方式
	 *                  trace_no		凭证号
	 *                  refer_no		银行流水号（对账用的）
	 *                  amt				交易金额
	 *                  bank_card_no	银行卡号
	 *                  bank_code		银行代码
	 *                  terminal_id		终端号
	 *                  mcht_cd			商户号
	 * @throws Exception
     */
	void setPosOrderAndOver(Map<String, Object> posOrder) throws Exception;

	/**
	 * 关闭指令和相应的订单
	 * @param commandNo
	 * @param params
	 * @throws BizException
     */
	public void closeCommandOrder(String commandNo, Map<String, Object> params) throws BizException;
}