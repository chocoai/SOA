package bps.order;

import bps.application.Appliction;

import bps.common.*;
import bps.order.Command.Command;
import com.kinorsoft.ams.services.FreezeService;
import ime.core.Environment;
import ime.core.Reserved;
import ime.core.event.Event;
import ime.core.event.EventManager;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;
import ime.security.Password;
import ime.security.util.TripleDES;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kinorsoft.ams.ITradeCommand.CommandResult;
import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.services.QueryService;
import com.kinorsoft.ams.services.TradeService;

import apf.util.BusinessException;
import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.account.AccountServiceImpl;
import bps.code.CodeServiceImpl;
import bps.external.tradecommand.ItsManage;
import bps.member.Member;
import bps.rule.TradeRule;
import bps.service.AccountService;
import bps.service.OrderService;
import bps.util.CodeUtil;

// 交易类型为转账，交易子类型（除了转账到卡，用户），部分字段写入订单明细表。


/**
 * 
 * @author Administrator
 *
 */
public class OrderServiceImpl implements OrderService{
	private static Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	
	/****
	 * 
	* @Title applyOrder
	* @Description TODO 订单申请接口
	* @param applicationId  应用ID
	* @param memberId   会员ID
	* @param bizOrderNo  业务订单号
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
	 * 					tradeMoney
	* @return Map     返回类型
	* @throws BizException
	 */
	public Map<String, Object> applyOrder(Long applicationId, Long memberId,String bizOrderNo,
			Long money, Long orderType, Long tradeType, Long source,
			Map<String, Object> extParams, List accountList,
			List payInterfaceList, List couponList) throws BizException {
		// TODO Auto-generated method stub
		logger.info("OrderServiceImpl.createOrder start");
		logger.info("memberId="+memberId+"money="+money+"orderType="+orderType+"tradeType="+tradeType+"source="+source);
		logger.info("extParams="+extParams);
		logger.info("accountList="+accountList);
		logger.info("payInterfaceList="+payInterfaceList);
		logger.info("couponList=" + couponList);
//		String orderNo="";
		Map<String,Object> ret = null;
		Long subTradeType = (Long) extParams.get("subTradeType");
		if(accountList == null){
			accountList = new ArrayList<>();
		}
		
		extParams.put("memberId", memberId);
		extParams.put("money", money);
		extParams.put("orderType", orderType);
		extParams.put("tradeType", tradeType);
		extParams.put("source", source);
		extParams.put("bizOrderNo", bizOrderNo);
		extParams.put("accountList", accountList);
		extParams.put("payInterfaceList", payInterfaceList);
		extParams.put("couponList", couponList);
		try {
			Member member = new Member(memberId);
			if( Constant.USER_STATE_LOCKED.equals(member.getUser_state())) {
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户已被锁定或关闭");
			}
			Map<String,Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
			if(applicationEntity == null){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT,"应用不存在");
			}

			//交易规则
			TradeRule.checkTradeRule(tradeType, subTradeType, payInterfaceList, accountList, applicationEntity);
			
			//交易规则 end
			String applicationName = (String)applicationEntity.get("name");
			extParams.put("applicationId", applicationId);
			extParams.put("applicationName", applicationName);
			extParams.put("applicationName", applicationName);
			extParams.put("orgNo", applicationEntity.get("codeNo"));
			extParams.put("applicationMemberId", (Long)applicationEntity.get("member_id"));
			Order order = OrderFactory.getOrder(tradeType, subTradeType);
			logger.info("aaaa");
			ret = order.applyOrder(extParams);
			logger.info("订单申请返回：" + ret);
			
			//交易监测
//			List<Map<String, Object>> commandList = getCommands((String)ret.get("orderNo"));
//			for(Map<String, Object> command : commandList){
//				TransMonitor.monitor("response" ,command, new HashMap<String, Object>());
//			}
		} catch(BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
		
		
		return ret;
	}
	
	/***
	 * 
	* (非 Javadoc) 
	* <p>Title: confirmPay</p> 支付确认
	* <p>Description: </p> 
	* @param memberId		会员ID
	* @param orderNo		订单号
	* @param oriTraceNum	原交易号
	* @param user_ip		用户IP
	* @param phone			手机号
	* @param phoneCode		短信验证码
	* @return Map	支付结果
	* @throws BizException 
	* @see bps.service.OrderService#confirmPay(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
//	public Map<String, Object> confirmPay(Long memberId, String orderNo, String oriTraceNum, String user_ip, String phone, Long isSendSm, String phoneCode, Long codeType) throws BizException{
	public Map<String, Object> confirmPay(Long memberId, String orderNo, String oriTraceNum, String user_ip, String phone, String phoneCode) throws BizException{
		logger.info("OrderServiceImpl.confirmPay start");
		logger.info("phone="+phone+"memberId="+memberId+"orderNo="+orderNo+"oriTraceNum="+oriTraceNum
				+"phoneCode="+phoneCode+"user_ip="+user_ip);
		Map<String, Object> jsonobject = new HashMap<> ();
        try{
			jsonobject.put("status", "fail");
            if(StringUtils.isBlank(phone)){
            	throw new BizException(ErrorCode.PARAM_ERROR, "手机号不能为空");
            }
            Member member = new Member(memberId);
            if(member.getUserId() == null){
            	throw new BizException(ErrorCode.PARAM_ERROR, "该用户不存在");
            }
			if(Constant.USER_STATE_LOCKED.equals(member.getUser_state())) {
				throw new BizException(ErrorCode.ACCOUNT_EX_LOCKING, "该用户已被锁定或关闭");
			}
            if(StringUtils.isBlank(orderNo)){
            	throw new BizException(ErrorCode.PARAM_ERROR, "订单号不能为空");
            }
			Map<String, Object> order_entity = getOrder(orderNo);
            if(order_entity == null){
            	throw new BizException(ErrorCode.PARAM_ERROR, "该订单不存在");
            }
			if( !member.getUserId().equals(order_entity.get("member_uuid"))){
				throw new BizException(ErrorCode.ORDER_ERROR, "该订单会员和支付确认传入的会员不同。");
			}
			List commandList = getCommands(orderNo);
			if(commandList.isEmpty()) {
			    throw new Exception("支付失败");
			}
			//验证机构准备金账户是否充足
			if( member.getApplicationId().equals(Constant.YUN_APPLICATION_ID) ){
				checkYUNAgencyFees(orderNo);
			}else{
				checkAgencyFees(orderNo);
			}

			//end
			Map<String, Object> command = (Map<String, Object>)commandList.get(0);

			String pay_interfaceNo 		= (String)command.get("pay_interfaceNo");
			String withdrawType 		= (String)command.get("withdrawType");
			Long withdrawReserveModel 	= (Long)command.get("withdrawReserveModel");

//			if ( pay_interfaceNo.equals(Constant.PAY_INTERFACE_TLT_DF) || pay_interfaceNo.equals(Constant.PAY_INTERFACE_TLT_BACH_DF) ){
//				Long fee = (Long)order_entity.get("fee");
//				if( fee > 0L ){
//					logger.info("调取多账户冻结金额接口");
//					final Map<String, Object> param = new HashMap<>();
//
//					String bizid          	= (String)command.get("bizid");
//					String source_userId	= (String)command.get("source_userId");
//					Long account_type_id	= (Long)command.get("account_type_id");
//
//					param.put("userId", 			source_userId);
//					param.put("account_type_id", 	account_type_id);
//					param.put("bizid", 				bizid);
//					param.put("freeze_money", 		fee);
//					param.put("remark", 			"代付手续费,冻结金额");
//
//					LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
//					EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
//						@Override
//						public Map<String, Object> doTransaction(Session session, Transaction tx)
//								throws Exception {
//							FreezeService.freezeMoney(param);
//							return null;
//						}
//					});
//				}
//			}

			if( "T1".equals(withdrawType) ) {//T+1批量代付-冻结金额、放到队列。
				WithdrawOrderToBatchDaiFu bdfr = new WithdrawOrderToBatchDaiFu();
				bdfr.confirmPay(command, Constant.WITHDRAW_RESERVE_MODEL_ACTIVE);

				final String _orderNo = orderNo;
				EntityManagerUtil.execute(new TransactionWork<Object>() {

					@Override
					public Object doTransaction(Session session, Transaction tx)
							throws Exception {
						Order.updateOrderPending(_orderNo, session );
						return null;
					}
				});
				jsonobject.put("orderNo", orderNo);
				jsonobject.put("status", "pending");

			}else if ( "T0".equals(withdrawType) && withdrawReserveModel.equals(Constant.WITHDRAW_RESERVE_MODEL_ENTRUST) ){//T+0批量代付
				WithdrawOrderToBatchDaiFu bdfr = new WithdrawOrderToBatchDaiFu();
				bdfr.confirmPay(command, withdrawReserveModel);

				final String _orderNo = orderNo;
				EntityManagerUtil.execute(new TransactionWork<Object>() {

					@Override
					public Object doTransaction(Session session, Transaction tx)
							throws Exception {
						Order.updateOrderPending(_orderNo, session );
						return null;
					}
				});
				jsonobject.put("orderNo", orderNo);
				jsonobject.put("status", "pending");

			}else if(pay_interfaceNo.equals(Constant.PAY_INTERFACE_QUICK)) {//its支付
				if(StringUtils.isBlank(oriTraceNum)){
	            	throw new BizException(ErrorCode.PARAM_ERROR, "源交易码不能为空");
	            }
//				if(StringUtils.isBlank(phoneCode) && isSendSm.equals(1L)){
				if(StringUtils.isBlank(phoneCode)){
	            	throw new BizException(ErrorCode.PARAM_ERROR, "验证码不能为空");
	            }
				
				Map<String, String> temp = new HashMap<>();
				temp.put("ORI_TRACE_NUM", oriTraceNum);
				temp.put("VERIFY_CODE", phoneCode);
				temp.put("isSendSm", "1");
				temp.put("userId", member.getUserId());
				
				Map<String, String> result_its = ItsManage.payACK(temp);
				if( "000000".equals(result_its.get("RET_CODE")) ){
					updateOrderIP(orderNo, user_ip);
					jsonobject.put("status", "success");
				}else{
					if(result_its.get("ERROR_MSG") != null && !"".equals(result_its.get("ERROR_MSG"))){
						jsonobject.put("payFailMessage", result_its.get("ERROR_MSG"));
					}else{
						jsonobject.put("payFailMessage", result_its.get("RET_MSG"));
					}
				}
			} else {
//				if(isSendSm == null || isSendSm.equals(1L)) {
				if((Constant.TRADE_TYPE_WITHDRAW.equals(order_entity.get("trade_type")) && Constant.SUB_TRADE_TYPE_WITHDRAW_WITHOUT_CONFIRM.equals(order_entity.get("sub_trade_type")))
						|| (Constant.TRADE_TYPE_DEPOSIT.equals(order_entity.get("trade_type")) && Constant.SUB_TRADE_TYPE_DEPOSIT_WITHOUT_CONFIRM.equals(order_entity.get("sub_trade_type")))
						|| (Constant.TRADE_TYPE_TRANSFER.equals(order_entity.get("trade_type")) && Constant.SUB_TRADE_TYPE_SHOPPING_WITHOUT_CONFIRM.equals(order_entity.get("sub_trade_type")))){
					//无验证提现 取消短信验证
					logger.info("confirmPay:取消短信");
				}else {
					//验证短信验证码
					CodeServiceImpl codeServiceImpl = new CodeServiceImpl();
					Long code_id = codeServiceImpl.checkPhoneVerificationCode(Constant.APPLICATION_ID_BPS_YUN, phone, Constant.VERIFICATION_CODE_AUTH_PAY, phoneCode);
					//设置短信验证码已验证
					codeServiceImpl.setPhoneVerificationCode(code_id);
				}
//				}
				Map<String, Object> retMap = new HashMap<>();
		        TradeCommandManager tradeCommandManager = TradeCommandManager.instance();
		        try {
		            CommandResult command_state = tradeCommandManager.doCommands(orderNo, null);
		            retMap.put("command_result", command_state);
		        } catch(Exception e) {
		            logger.error(e.getMessage(), e);
		            retMap.put("command_result", CommandResult.FailStop);
		            retMap.put("err_msg1", e.getMessage());
		        }
				//Map<String, Object> retMap =  OrderService.doCommand(orderNo);
				logger.info("retMap------------------:"+retMap);
				CommandResult command_state = (CommandResult)retMap.get("command_result");
				logger.info("ret_code1------------------:"+command_state);
				Map<String, Object> setParam = new HashMap<>();
				//{command_result=PendingStop, ret_code2=null, ret_code1=1000, err_msg1=序号为0 的交易中账号:6214835741131658 没有从协议库中找到对应的协议号, err_msg2=null}
				if( command_state.equals(CommandResult.Success) ){
					jsonobject.put("orderNo", orderNo);
					jsonobject.put("status", "success");
					/*
					if(((Long)order_entity.get("sub_trade_type")).equals(Constant.SUB_TRADE_TYPE_PHONE) && ((String)order_entity.get("orgNo")).equals(Constant.ORG_NO_ALLINPAY)) {
						OrderService.phoneDepositOrderComplatePay((Long)order_entity.get("id"));
					}
					*/
				}else if(command_state.equals(CommandResult.PendingContinue)){
					jsonobject.put("orderNo", orderNo);
					jsonobject.put("status", "pending");
				}else if(command_state.equals(CommandResult.PendingStop)){
					jsonobject.put("orderNo", orderNo);
					jsonobject.put("status", "pending");
				}else if( command_state.equals(CommandResult.FailStop) ){
					jsonobject.put("status", "fail");
					
					if( retMap.get("err_msg1") != null )
						jsonobject.put("payFailMessage", retMap.get("err_msg1"));
					else
						jsonobject.put("payFailMessage", retMap.get("err_msg2"));
					
					setParam.put("order_state", Constant.ORDER_STATE_CLOSE);
				}else{
					jsonobject.put("status", "fail");
					
					if( retMap.get("err_msg1") != null )
						jsonobject.put("payFailMessage", retMap.get("err_msg1"));
					else
						jsonobject.put("payFailMessage", retMap.get("err_msg2"));
				}
			}
			//返回剩余可投资金额 朱成-2016-3-22
//			Long order_type = order_entity.get("order_type") == null ? null : (Long)order_entity.get("order_type");
//			String biz_trade_code =  order_entity.get("biz_trade_code") == null ? "" : (String)order_entity.get("biz_trade_code");
//			Long goodsType = order_entity.get("goodsType") == null ? null : (Long)order_entity.get("goodsType");
//			String goodsNo =  order_entity.get("goodsNo") == null ? "" : (String)order_entity.get("goodsNo");
//
//			if (  Constant.ORDER_TYPE_DAISHOU.equals(order_type) && "1001".equals( biz_trade_code ) ){
//				List<Map<String, Object>> orderList = getOrderListByGoods(order_type,biz_trade_code,goodsType,goodsNo);
//				Long sumOldAmount = 0L;
//				for (Map<String, Object> temp : orderList){
//					Long tempAmount = temp.get("order_money") == null ? 0L : (Long)temp.get("order_money");
//					sumOldAmount += tempAmount;
//				}
//
//				GoodsServiceImpl gsi = new GoodsServiceImpl();
//				Map<String,Object> goods = gsi.queryGoods((Long)order_entity.get("application_id"),goodsType,goodsNo);
//				Long highest_amount = goods.get("highest_amount") == null ? (Long)goods.get("total_amount") : (Long)goods.get("highest_amount");
//
//				logger.info("highest_amount:"+highest_amount);
//				logger.info("sumOldAmount:"+sumOldAmount);
//
//				Long remainAmount = highest_amount - sumOldAmount;
//				jsonobject.put("remainAmount",remainAmount);
//			}
        }catch(BizException e){
			logger.error(e.getMessage(), e);
            throw e;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
        }
        logger.info("组合支付结束");
        return jsonobject;
	}
	
	/**
     * 获取指令集
     * @param orderNo		订单号
     * @throws Exception
     */
    public List getCommands(String orderNo)throws BizException{
		logger.info("OrderServiceImpl.getCommand start");
		try {
			final String _orderNo = orderNo;
			List<Map<String, Object>> res=
			EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> doQuery(Session session) throws Exception {
					Query query = session.createQuery("from AMS_OrderPayDetail where bizid=:orderNo and pay_state=:pay_state order by seq_no asc");
		            query.setParameter("orderNo", _orderNo);
		            query.setParameter("pay_state", com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY);
		            
		            return query.list();
				}
				
			});
			return res;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
    }

	public void closeCommandOrder(String commandNo, Map<String, Object> params) throws BizException{
		logger.info("OrderServiceImpl.closeCommandOrder=" + commandNo + ",params=" + params);

		final String _commandNo = commandNo;
		final Map<String, Object> _params = params;

		try {
			EntityManagerUtil.execute(new TransactionWork<Object>() {

				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					String orderNo = _commandNo.split(Constant.COMMAND_SPLIT_SIGN)[0];

					Command.closeCommandByCommandNo(_commandNo, _params, session);

					Order.closeOrder(orderNo, _params, session);
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

	/****
	 * 
	* @Title: closeOrder 
	* @Description: TODO(关闭订单
	* @param @param orderNo
	* @param @param extParams
	* @param @throws BizException    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public void closeOrder(String orderNo, Map<String, Object> extParams)
			throws BizException {
		logger.info("OrderServiceImpl.closeOrder start");
		logger.info("extParams="+extParams);
		final String _orderNo=orderNo;
		final Map<String, Object> param=extParams;
		try {
	        EntityManagerUtil.execute(new TransactionWork<Object>() {

				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Order.closeOrder(_orderNo, param, session);
					return null;
				}

	        });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
//	public Map<String, String> itsSignApply(String bankCode,
//			String accountName, Long cardType, String accountNo,
//			Long accountTypeId, String accountTypeName, String phone,
//			String bankName, Long memberId, Long payType, Map<String, Object> extParams)

	/**
	 * 绑定银行卡申请
	 * @param bankCode 		银行代码
	 * @param accountName 	开户名
	 * @param cardType 		卡类型
	 * @param accountNo 	卡号
	 * @param phone 		手机
	 * @param bankName 		银行名称
	 * @param memberId 		会员编号
	 * @param payType 		1正常，3免验证码
	 * @param extParams 	扩展参数
	 * 			acctValiddate   	有效期  信用卡属性
	 * 			cvv2				cvv2  信用卡属性
	 * 			identityCardNo		身份证
	 * 			identityCardNoEncrypt  身份证加密码
	 * 			identityCardNoMask  身份证掩码
	 * 			cnlMchtName			商户名称
	 * 			cnlMchtType			商户类型
     * 			cnlMchtId			id 应用ID
     * @return Map<String,String>
     * @throws BizException
     */
	public Map<String, String> itsSignApply(String bankCode,
			String accountName, Long cardType, String accountNo,String phone,
			String bankName, Long memberId, Long payType, Map<String, Object> extParams)
			throws BizException {
		logger.info("OrderServiceImpl.itsSignApply start");
		try {
			Member member = new Member(memberId);
			if(member.getUserId() == null) {
				throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
			}
			//组装参数
			Map<String, String> params = new HashMap<>();
			params.put("BANK_CODE", bankCode);
			params.put("ACCT_NAME", accountName);
			params.put("ACCT_CAT", cardType.toString());
		    params.put("member_id", memberId.toString());
			if(extParams.get("acctValiddate") != null)
				params.put("ACCT_VALIDDATE", extParams.get("acctValiddate").toString());
			if(extParams.get("cvv2") != null)
				params.put("CVV2", extParams.get("cvv2").toString());
			params.put("ACCT_NO", accountNo);
//			params.put("ACCOUNT_TYPE_ID", accountTypeId.toString());
//			params.put("ACCOUNT_TYPE_LABEL", accountTypeName);
			if(extParams.get("identityCardNo") != null)
				params.put("ID_NO", extParams.get("identityCardNo").toString());
			if(extParams.get("identityCardNoEncrypt") != null)
				params.put("ID_NO_ENCRYPT", extParams.get("identityCardNoEncrypt").toString());
			if(extParams.get("identityCardNoMask") != null)
				params.put("ID_NO_MASK", extParams.get("identityCardNoMask").toString());
			
			if(extParams.get("isSafeCard") != null)
				params.put("isSafeCard", String.valueOf(extParams.get("isSafeCard")));
			
			params.put("PHONE_NO", phone);
			params.put("BANK_NAME", bankName);
			params.put("userId", member.getUserId());
			params.put("payType", payType.toString());
			if(extParams.get("cnlMchtName") != null)
				params.put("CNL_MCHT_NAME", extParams.get("cnlMchtName").toString());
			if(extParams.get("cnlMchtType") != null)
				params.put("CNL_MCHT_TYPE", extParams.get("cnlMchtType").toString());
			if(extParams.get("cnlMchtId") != null)
				params.put("CNL_MCHT_ID", extParams.get("cnlMchtId").toString());
			
			//验证传入的身份真和姓名是否与实名记录一致
			params.put("accountName",accountName);
			Member.chickBindBankCardID(params, member);
//			if(Constant.MEMBER_TYPE_PERSON.equals(member.getMember_type())){
//				if(!accountName.equals(member.getName()))
//					throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
//				//身份证最后一位是X时验证：把x转成大小写来验证，二者过一个就可以。
//				if(params.get("ID_NO")!=null){
//					String id_no = params.get("ID_NO");
//					String identity_cardNo_md5 = Password.encode(id_no, "MD5");
//
//					String id_no_x = "";
//					if(id_no.contains("x")){
//						id_no_x = id_no.toUpperCase();
//					}else if(id_no.contains("X")){
//						id_no_x = id_no.toLowerCase();
//					}
//					if( !id_no_x.equals("")){
//						id_no_x = Password.encode(id_no_x, "MD5");
//						if(!identity_cardNo_md5.equals(member.getIdentity_cardNo_md5())){
//							if(!id_no_x.equals(member.getIdentity_cardNo_md5())){
//								throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
//							}
//						}
//					}else{
//						if(!identity_cardNo_md5.equals(member.getIdentity_cardNo_md5())){
//							throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
//						}
//					}
//				}else if(params.get("ID_NO_ENCRYPT")!=null){
//					String id_no	= TripleDES.decrypt(member.getUserId() + Constant.MEMBER_BANK_ENCODE_KEY, params.get("ID_NO_ENCRYPT"));
//					String identity_cardNo_md5 = Password.encode(id_no, "MD5");
//
//					String id_no_x = "";
//					if(id_no.contains("x")){
//						id_no_x = id_no.toUpperCase();
//					}else if(id_no.contains("X")){
//						id_no_x = id_no.toLowerCase();
//					}
//					if( !id_no_x.equals("")){
//						id_no_x = Password.encode(id_no_x, "MD5");
//						if(!identity_cardNo_md5.equals(member.getIdentity_cardNo_md5())){
//							if(!id_no_x.equals(member.getIdentity_cardNo_md5())){
//								throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
//							}
//						}
//					}else{
//						if(!identity_cardNo_md5.equals(member.getIdentity_cardNo_md5())){
//							throw new BizException(ErrorCode.VERIFY_REAL_NAME_FAIL,"银行卡用户信息不一致");
//						}
//					}
//				}
//			}
			
			//调用its申请绑卡接口
			Map<String, String> result_its = ItsManage.signApply(params);
			logger.info("result_its:"+result_its);
			if( "000000".equals(result_its.get("RET_CODE")) || "359037".equals(result_its.get("RET_CODE"))){
				if(result_its.get("SEND_SMS") ==null || "2".equals(result_its.get("SEND_SMS")) ){
					params = new HashMap<String, String>();
					params.put("ORI_TRACE_NUM", result_its.get("TRACE_NUM"));
					params.put("ORI_TRANS_DATE", result_its.get("TRANS_DATE"));
					params.put("PHONE_NO", phone);
					params.put("userId", member.getUserId());
					params.put("member_id", memberId.toString());
					Map<String, String> result_its2 = ItsManage.signMessageSend(params);
					if("000000".equals(result_its2.get("RET_CODE")) || "359037".equals(result_its2.get("RET_CODE"))){
						return result_its;
					}else{
						if(result_its2.get("ERROR_MSG") != null && !"".equals(result_its2.get("ERROR_MSG").toString())){
							throw new Exception(result_its2.get("ERROR_MSG"));
						}else{
							throw new Exception(result_its2.get("RET_MSG"));
						}
					}
				}
			}
			return result_its;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
    
	/**
	 * @Title:itsSignACK
	 * @Description: 绑定银行卡确认接口
	 * @param oriTraceNum:流水号,请求绑定银行卡接口返回
	 * @param oriTransDate:申请时间,请求绑定银行卡接口返回
	 * @param memberId:会员ID
	 * param verifyCode:手机验证码
	 */
	public void itsSignACK(String oriTraceNum,
			String oriTransDate, Long memberId, String verifyCode, Long payType)
			throws BizException {
		logger.info("OrderServiceImpl.itsSignACK start");
		try {
			Member member = new Member(memberId);
			if(member.getUserId() == null) {
				throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
			}
			
			//检查应用准备金是否足够
	    	Map<String, Object> memberEntity = member.getUserInfo();
	    	Long applicationId =(Long)memberEntity.get("application_id");
	    	Map<String, Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
	    	String orgNo = (String)applicationEntity.get("codeNo");
	    	Long applicationMemberId = (Long)applicationEntity.get("member_id");
	    	Map<String, Object> applicationMemberEntity = DynamicEntityService.getEntity(applicationMemberId, "AMS_Member");
	    	String applicationUserId = (String)applicationMemberEntity.get("userId");
	    	
	    	Map<String, Object> orgFeeEntity = Member.checkFee(orgNo, applicationUserId, Constant.FEE_TYPE_BIND_BANK_CARD);
			
	    	//绑卡
			Map<String, String> params = new HashMap<String, String>();
			params.put("ORI_TRACE_NUM", oriTraceNum);
			params.put("ORI_TRANS_DATE", oriTransDate);
			params.put("VERIFY_CODE", verifyCode);
			params.put("userId", member.getUserId());
			params.put("payType", payType.toString());
			
			Map<String, String> ret = ItsManage.signACK(params);
			
			//检查是否成功
			String retCode = ret.get("RET_CODE");
				//成功
			if("000000".equals(retCode) || "359037".equals(retCode)){
					//手续费
				if(orgFeeEntity != null){
					final Map<String, Object> _orgFeeEntity = orgFeeEntity;
					final Map<String, Object> _applicationMemberEntity = applicationMemberEntity;
					EntityManagerUtil.execute(new TransactionWork<String>() {
						@Override
						public String doTransaction(Session session, Transaction tx)
								throws Exception {
				            Member.feeOpe(_applicationMemberEntity, _orgFeeEntity, Constant.FEE_TYPE_BIND_BANK_CARD, session);
							
							return null;
						}
					});
				}
			}
				//失败
			else{
//				throw new BusinessException(ErrorCode.BIND_BANK_CARD_ERROR, "绑卡失败。");

				if(ret.get("ERROR_MSG") != null && !"".equals(ret.get("ERROR_MSG").toString())){
					throw new BusinessException(ErrorCode.BIND_BANK_CARD_ERROR, ret.get("ERROR_MSG"));
				}else{
					throw new BusinessException(ErrorCode.BIND_BANK_CARD_ERROR, ret.get("RET_MSG"));
				}
			}
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	public void completeOrder(String orderNo, Map<String, Object> param) throws BizException{
		logger.info("OrderServiceImpl.completeOrder start");
		logger.info("orderNo="+orderNo+"param="+param);
		final Map<String, Object> order_entity = getOrder(orderNo);
		final Long tradeType = (Long) order_entity.get("trade_type");
		final Long subTradeType = (Long) order_entity.get("sub_trade_type");
		final Map<String, Object> _param = param;
		try{
			EntityManagerUtil.execute(new TransactionWork<Object>(){
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Order order = OrderFactory.getOrder(tradeType, subTradeType);
					order.completeOrder(order_entity, _param, session);
					return null;
				}
			});
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	public Map<String, Object> getOrder(String _orderNo) throws BizException {
		logger.info("OrderServiceImpl.getOrder start");
		try {
			final String orderNo=_orderNo;
			Map<String, Object> res=
			EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					return Order.getOrder(orderNo, session);
				}
			});
			return res;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	public Map<String, Object> getOrder(Long applicationId,String bizOrderNo) throws BizException{
		logger.info("OrderServiceImpl.getOrder start");
		try {
			final Long _applicationId=applicationId;
			final String _bizOrderNo = bizOrderNo;
			Map<String, Object> res=
			EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					return Order.getOrder(_applicationId,_bizOrderNo, session);
				}
			});
			return res;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
		
		
	}
	
	
	public void updateOrderIP(String _orderNo, String _memberIp)throws BizException {
		logger.info("OrderServiceImpl.updateOrderIP start");
		try {
			final String orderNo=_orderNo;
			final String member_ip=_memberIp;
		    EntityManagerUtil.execute(new TransactionWork<Object>() {
		
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					 Order.updateOrderIP(orderNo, member_ip, session);
					return null;
				}
		
		    });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	
	}
	
	public Map<String, Object> getOrg(String _orgNo) throws BizException {
		logger.info("OrderServiceImpl.getOrg start");
		try {
			final String orgNo=_orgNo;
			 Map<String, Object>  res=
			EntityManagerUtil.execute(new QueryWork< Map<String, Object> >() {
				@Override
				public  Map<String, Object>  doQuery(Session session) throws Exception {
					return Order.getOrgByOrgNo(orgNo, session);
				}
				
			});
			return res;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	/***
	 * 
	* @Title: getOrgList 获取应用
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @return
	* @param @throws BizException    设定文件 
	* @return List    返回类型 
	* @throws
	 */
    public List getOrgList()throws BizException{
		logger.info("OrderServiceImpl.getCommand start");
		try {
			List<Map<String, Object>> res=
			EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> doQuery(Session session) throws Exception {
					Query query = session.createQuery("from AMS_Organization where state=1");
		            return query.list();
				}
				
			});
			return res;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
    }
    
	public String getITSBankCode(String bankCode) throws BizException {
		logger.info("OrderServiceImpl.getITSBankCode start");
		try {
			final String bank_code=bankCode;
			String  res=
			EntityManagerUtil.execute(new QueryWork<String >() {
				@Override
				public String  doQuery(Session session) throws Exception {
				  return Order.getITSBankCode(bank_code, session);
				}
			});
			return res;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	public void modifyITSBankSMSSender(Boolean isSmsVerify, String bankCode)
	throws BizException {
		logger.info("OrderServiceImpl.modifyITSBankSMSSender start");
		try {
			final String bank_code=bankCode;
			final boolean is_sms_verify=isSmsVerify;
		    EntityManagerUtil.execute(new TransactionWork<Object>() {
		
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					session.beginTransaction();
					  Query query = session.createQuery("update AMS_PayInterfaceBank set is_sms_verify=:is_sms_verify where pay_interfaceNo=:pay_interfaceNo and bank_code=:bank_code");
		                query.setParameter("is_sms_verify", is_sms_verify);
		                query.setParameter("pay_interfaceNo", Constant.PAY_INTERFACE_QUICK);
		                query.setParameter("bank_code", bank_code);
		                query.executeUpdate();
					return null;
				}
		
		    });
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 外部支付渠道充值
	 * @param param
	 * @throws Exception
	 */
	public void  payChannelDeposit(Map<String, Object> param)throws BizException{
		logger.info("TradeService payChannelDeposit start");
		logger.info("param:" + param);
		final String orderNo 		= (String)param.get("orderNo");			//订单号
		final Long order_money 	= (Long)param.get("orderMoney");		//订单金额
		final String out_trade_id = (String)param.get("outTradeId");	//外部流水号
		final String out_bizno	= (String)param.get("out_bizNo");
		String pay_channelNo	= (String)param.get("payChannelNo");
		final String pay_interfaceNo	= (String)param.get("payInterfaceNo");
		final String bank_code	= (String)param.get("bank_code");
		final String extend_info	= (String)param.get("extend_info");
		final Date trade_time	= (Date)param.get("tradeTime");
		final String remark	= (String)param.get("remark");
		final Long card_type   = (Long)param.get("card_type");
		final String biz_orderNo   = (String)param.get("biz_orderNo");
		final String pay_serialNo = (String)param.get("pay_serialNo");
		
		final int index = orderNo.indexOf(Constant.COMMAND_SPLIT_SIGN);	//判断是否多指令集	
		final Map<String, Object> _param = new HashMap<String, Object>();
		Boolean isOK = false;
		
		if(orderNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR, "请传入参数  orderNo");
		if(order_money == null)
			throw new BizException(ErrorCode.PARAM_ERROR, "请传入参数  order_money");
		if(out_trade_id == null)
			throw new BizException(ErrorCode.PARAM_ERROR, "请传入参数  out_trade_id");
		if(pay_channelNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR, "请传入参数  pay_channelNo");
		if(pay_interfaceNo == null)
			throw new BizException(ErrorCode.PARAM_ERROR, "请传入参数  pay_interfaceNo");
		if(trade_time == null)
			throw new BizException(ErrorCode.PARAM_ERROR, "请传入参数  trade_time");
		
		try {
			logger.info("测试++++++"+LoginSession.isLogined());
			if(!LoginSession.isLogined())
			    LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);

            String order_No = null;
            Long seq_no   = null;
            if(index >= 0) {
                order_No = orderNo.substring(0, index);
                seq_no = Long.parseLong(orderNo.substring(index+1));
            } else {
                order_No = orderNo;
                seq_no = 1L;
            }
            _param.put("order_No", order_No);
            _param.put("seq_no", seq_no);
            _param.put("isOK", isOK);
            
			EntityManagerUtil.execute(new TransactionWork<String>() {
				@Override
				public boolean before(Session session) throws Exception {
					Map<String, Object> depositParam = new HashMap<String, Object>();
					//查询指令
					StringBuilder sb = new StringBuilder();
					sb.setLength(0);
					sb.append("from  AMS_OrderPayDetail where bizid=:bizid and seq_no=:seq_no and pay_state=:pay_state");
					Query query = session.createQuery(sb.toString());
					query.setParameter("bizid", 	_param.get("order_No"));
					query.setParameter("seq_no", 	_param.get("seq_no"));
		            query.setParameter("pay_state", com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY);
					
					List list = query.list();
					if(list == null || list.isEmpty())
						throw new Exception("无此订单号");
					
					Map<String, Object> orderPayDetail_entity = (Map<String, Object>)list.get(0);		
					
					Long commandId	= (Long)orderPayDetail_entity.get("id");
					//查询订单
					Map<String, Object> order_entity = TradeService.getOrder((String)_param.get("order_No"), session);
					if(order_entity == null)
						throw new Exception("无此订单号");
					Long orderId	= (Long)order_entity.get("id");
					Long trade_money	= (Long)orderPayDetail_entity.get("trade_money");
					if(!order_money.equals(trade_money))
						throw new Exception("交易金额不一致");
					Long pay_state = (Long)orderPayDetail_entity.get("pay_state");
					if(!pay_state.equals(com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY))	//判断订单状态
						throw new Exception("订单非未支付状态");
					
					depositParam.put("source_userId", 		orderPayDetail_entity.get("source_userId"));
					depositParam.put("account_type_id", 	orderPayDetail_entity.get("account_type_id"));
					depositParam.put("target_account_type_id", 	orderPayDetail_entity.get("target_account_type_id"));
					depositParam.put("trade_money", 		trade_money);
					depositParam.put("target_userId", 		orderPayDetail_entity.get("target_userId"));
					depositParam.put("out_trade_id", 		out_trade_id);
					depositParam.put("out_bizno", 			out_bizno);
					depositParam.put("bizid", 				_param.get("order_No"));
		            depositParam.put("command_no",          orderNo);
					depositParam.put("source_memberNo", 	orderPayDetail_entity.get("source_memberNo"));
					depositParam.put("target_memberNo", 	orderPayDetail_entity.get("target_memberNo"));
					depositParam.put("isMaster", 			orderPayDetail_entity.get("isMaster"));
					depositParam.put("orgNo", 				orderPayDetail_entity.get("orgNo"));
					depositParam.put("pay_channelNo", 		orderPayDetail_entity.get("pay_channelNo"));
					depositParam.put("sub_trade_type", 		orderPayDetail_entity.get("sub_trade_type"));
					depositParam.put("source_member_name", 	orderPayDetail_entity.get("source_member_name"));
					depositParam.put("target_member_name", 	orderPayDetail_entity.get("target_member_name"));
					depositParam.put("pay_interfaceNo", 	pay_interfaceNo);
					depositParam.put("bank_code", 			bank_code);
					depositParam.put("extend_info", 		extend_info);
					depositParam.put("trade_time", 			trade_time);
					depositParam.put("remark", 				remark);
					depositParam.put("card_type", 			card_type == null ? order_entity.get("card_type") : card_type);
					depositParam.put("biz_orderNo",         biz_orderNo);
					depositParam.put("pay_serialNo",         pay_serialNo);
					
					_param.put("depositParam", depositParam);
					_param.put("commandId", commandId);
					return true;
				}

				@Override
				public String doTransaction(Session session, Transaction tx)
						throws Exception {
					TradeService.deposit((Map<String, Object>)_param.get("depositParam"));
					if(index > 0){	//更新交易指令结果
						Query query = session.createQuery("update AMS_OrderPayDetail set pay_state=:pay_state,out_trade_id=:outTradeId where id=:commandId");
						query.setParameter("pay_state", com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS);
						query.setParameter("outTradeId", out_trade_id);
						query.setParameter("commandId", (Long)_param.get("commandId"));
						query.executeUpdate();
					}
		            _param.put("isOK", true);
					return orderNo;
				}
	        });
		} catch(BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e){
			logger.info(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
		logger.info("isOK="+isOK);
		logger.info("index="+index);
		try {
			if((Boolean)_param.get("isOK")){
				if(index > 0)	//继续下一条指令
					TradeCommandManager.instance().doCommands(orderNo, (Long)_param.get("commandId"));
				else{	//单指令，触发订单支付完成事件
					Event event = new Event(com.kinorsoft.ams.Constant.EVENT_TYPE_ORDERCOMPLETEPAY, param, null);
					EventManager.instance().fireEvent(event);
				}
			}
		} catch(BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e){
			logger.info(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
	}

	/**
	 * 检查准备金账户手续费
	 * @param orderNo	要执行的订单号
	 * @throws BizException
     */
	public void checkAgencyFees(String orderNo) throws BizException {
		// TODO Auto-generated method stub
		final String _orderNo = orderNo;
		try{
			EntityManagerUtil.execute(new QueryWork<Boolean>() {
				@Override
				public Boolean doQuery(Session session) throws Exception {
					Map<String,Object> order_entity = Order.getOrder(_orderNo, session);
					Long fee = Order.getAgencyFees(order_entity, session);
					logger.info("检测机构手续费fee:"+fee);
					if(fee > 0){
						AccountService accountService = new AccountServiceImpl();
						Map<String,Object> application = DynamicEntityService.getEntity((Long)order_entity.get("application_id"), "AMS_Organization");
						Map<String,Object> accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_READY);
						Map<String,Object> accountMap = accountService.getMemberAccountByType((Long)application.get("member_id"), (Long)accountType.get("id"));
						if(accountMap == null){
							throw new BizException(ErrorCode.READ_ACCOUNT_NO_ENOUGH,"准备金账户手续费不足！");
						}else{
							Long amount = (Long)accountMap.get("amount");
							if(amount==null ||amount< fee.longValue()){
								throw new BizException(ErrorCode.READ_ACCOUNT_NO_ENOUGH,"准备金账户手续费不足！");
							}
						}
					}
					return true;
				}
				
			});
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
		//end
	}
	/**
	 * 检查云平台准备金账户手续费
	 * @param orderNo	要执行的订单号
	 * @throws BizException
	 */
	public void checkYUNAgencyFees(String orderNo) throws BizException {
		// TODO Auto-generated method stub
		final String _orderNo = orderNo;
		try{
			EntityManagerUtil.execute(new QueryWork<Boolean>() {
				@Override
				public Boolean doQuery(Session session) throws Exception {
					Map<String,Object> order_entity = Order.getOrder(_orderNo, session);
					Long fee = Order.getYUNAgencyFees(order_entity, session);
					logger.info("检测机构手续费fee:"+fee);
					if(fee > 0){
						AccountService accountService = new AccountServiceImpl();
						Map<String,Object> application = DynamicEntityService.getEntity((Long)order_entity.get("application_id"), "AMS_Organization");
						Map<String,Object> accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_READY);
						Map<String,Object> accountMap = accountService.getMemberAccountByType((Long)application.get("member_id"), (Long)accountType.get("id"));
						if(accountMap == null){
							throw new BizException(ErrorCode.READ_ACCOUNT_NO_ENOUGH,"准备金账户手续费不足！");
						}else{
							Long amount = (Long)accountMap.get("amount");
							if(amount==null ||amount< fee.longValue()){
								throw new BizException(ErrorCode.READ_ACCOUNT_NO_ENOUGH,"准备金账户手续费不足！");
							}
						}
					}
					return true;
				}

			});
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
		//end
	}
	
	public static void updateBankCardContractNo(Long bankCardId, String contractNo) throws Exception {
		logger.info("OrderServiceImpl.updateBankCardContractNo start");
		try {
			final Long _bankCardId = bankCardId;
			final String _contractNo = contractNo;
			EntityManagerUtil.execute(new TransactionWork<Object>(){
				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {
					Order.updateBankCardContractNo(_bankCardId, _contractNo, session);
					return null;
				}
			});
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAccountChgDetailList(Long memberId,
			Map<String, Object> param, Long start, Long end)
			throws BizException {
		// TODO Auto-generated method stub
		try{
			logger.info("getAccountChgDetailList start : memberId:"+memberId+",param:"+param+",start:"+start+",end:"+end);
			Member member = new Member(memberId);
			final String userId = member.getUserId();
			if(userId == null ||"".equals(userId)){
				throw new BizException(ErrorCode.PARAM_ERROR, "该用户不存在");
			}
			final Map<String,Object> _param = param;
			final Long _start = start;
			final Long _end = end;
			List<Map<String,Object>> list = EntityManagerUtil.execute(new QueryWork<List<Map<String,Object>>>() {
				@Override
				public List<Map<String,Object>> doQuery(Session session) throws Exception {
					StringBuffer hql = new StringBuffer();
					hql.append("from AMS_AccountChgLog where userId=:userId");
					if(_param.get("account_type_id")!=null){
						hql.append(" and account_type_id=:account_type_id");
					}
					if(_param.get("chg_time_start")!=null){
						hql.append(" and chg_time>=:chg_time_start");
					}
					if(_param.get("chg_time_end")!=null){
						hql.append(" and chg_time<=:chg_time_end");
					}
					hql.append(" order by chg_time desc");
					Query query = session.createQuery(hql.toString());
					query.setParameter("userId", userId);
					if(_param.get("account_type_id")!=null){
						query.setParameter("account_type_id", (Long)_param.get("account_type_id"));
					}
					if(_param.get("chg_time_start")!=null){
						query.setParameter("chg_time_start", (Date)_param.get("chg_time_start"));
					}
					if(_param.get("chg_time_end")!=null){
						query.setParameter("chg_time_end", (Date)_param.get("chg_time_end"));
					}
					query.setFirstResult(_start.intValue());
					query.setMaxResults(_end.intValue());
					return query.list();
				}
				
			});
			return list;
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	public Long getAccountChgDetailCount(Long memberId,
			Map<String, Object> param) throws BizException {
		// TODO Auto-generated method stub
		try{
			logger.info("getAccountChgDetailCount start : memberId:"+memberId+",param:"+param);
			Member member = new Member(memberId);
			final String userId = member.getUserId();
			if(userId == null ||"".equals(userId)){
				throw new BizException(ErrorCode.PARAM_ERROR, "该用户不存在");
			}
			final Map<String,Object> _param = param;
			Long ret = EntityManagerUtil.execute(new QueryWork<Long>() {
				@Override
				public Long doQuery(Session session) throws Exception {
					StringBuffer hql = new StringBuffer();
					hql.append("select count(*) from AMS_AccountChgLog where userId=:userId");
					if(_param.get("account_type_id")!=null){
						hql.append(" and account_type_id=:account_type_id");
					}
					if(_param.get("chg_time_start")!=null){
						hql.append(" and chg_time>=:chg_time_start");
					}
					if(_param.get("chg_time_end")!=null){
						hql.append(" and chg_time<=:chg_time_end");
					}
					Query query = session.createQuery(hql.toString());
					query.setParameter("userId", userId);
					if(_param.get("account_type_id")!=null){
						query.setParameter("account_type_id", (Long)_param.get("account_type_id"));
					}
					if(_param.get("chg_time_start")!=null){
						query.setParameter("chg_time_start", (Date)_param.get("chg_time_start"));
					}
					if(_param.get("chg_time_end")!=null){
						query.setParameter("chg_time_end", (Date)_param.get("chg_time_end"));
					}
					return (Long) query.uniqueResult();
				}
			});
			return ret;
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPayOrderList(Long applicationId,
			Map<String, Object> param, Long start, Long end)
			throws BizException {
		// TODO Auto-generated method stub
		try{
			logger.info("getPayOrderList start:applicationId:"+applicationId+",param:"+param+",start:"+start+",end:"+end);
			final Long _applicationId = applicationId;
			final Map<String,Object> _param = param;
			final Long _start = start;
			final Long _end = end;
			List<Map<String,Object>> list = EntityManagerUtil.execute(new QueryWork<List<Map<String,Object>>>() {
				
				@Override
				public List<Map<String,Object>> doQuery(Session session) throws Exception {
					StringBuffer hql = new StringBuffer();
					hql.append("from AMS_Order where application_id=:application_id");
					if(_param.get("member_id")!=null){
						hql.append(" and member_id=:member_id");
					}
					if(_param.get("order_type")!=null){
						hql.append(" and order_type=:order_type");
					}
					if(_param.get("order_state")!=null){
						hql.append(" and order_state=:order_state");
					}
					if(_param.get("orderNo")!=null){
						hql.append(" and orderNo=:orderNo");
					}
					if(_param.get("create_time_start")!=null){
						hql.append(" and create_time>=:create_time_start");
					}
					if(_param.get("create_time_end")!=null){
						hql.append(" and create_time<=:create_time_end");
					}
					hql.append(" order by create_time desc");
					Query query = session.createQuery(hql.toString());
					query.setParameter("application_id", _applicationId);
					if(_param.get("member_id")!=null){
						query.setParameter("member_id", (Long)_param.get("member_id"));
					}
					if(_param.get("order_type")!=null){
						query.setParameter("order_type", (Long)_param.get("order_type"));
					}
					if(_param.get("order_state")!=null){
						query.setParameter("order_state", (Long)_param.get("order_state"));
					}
					if(_param.get("orderNo")!=null){
						query.setParameter("orderNo", (String)_param.get("orderNo"));
					}
					if(_param.get("create_time_start")!=null){
						query.setParameter("create_time_start", (Date)_param.get("create_time_start"));
					}
					if(_param.get("create_time_end")!=null){
						query.setParameter("create_time_end", (Date)_param.get("create_time_end"));
					}
					query.setFirstResult(_start.intValue());
					query.setMaxResults(_end.intValue());
					return query.list();
				}
			});
			return list;
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	public Long getPayOrderCount(Long applicationId, Map<String, Object> param)
			throws BizException {
		// TODO Auto-generated method stub
		try{
			logger.info("getPayOrderCount start:applicationId:"+applicationId+",param:"+param);
			final Long _applicationId = applicationId;
			final Map<String,Object> _param = param;
			Long ret = EntityManagerUtil.execute(new QueryWork<Long>() {
				@Override
				public Long doQuery(Session session) throws Exception {
					StringBuffer hql = new StringBuffer();
					hql.append("select count(*) from AMS_Order where application_id=:application_id");
					if(_param.get("member_id")!=null){
						hql.append(" and member_id=:member_id");
					}
					if(_param.get("order_type")!=null){
						hql.append(" and order_type=:order_type");
					}
					if(_param.get("order_state")!=null){
						hql.append(" and order_state=:order_state");
					}
					if(_param.get("orderNo")!=null){
						hql.append(" and orderNo=:orderNo");
					}
					if(_param.get("create_time_start")!=null){
						hql.append(" and create_time>=:create_time_start");
					}
					if(_param.get("create_time_end")!=null){
						hql.append(" and create_time<=:create_time_end");
					}
					Query query = session.createQuery(hql.toString());
					query.setParameter("application_id", _applicationId);
					if(_param.get("member_id")!=null){
						query.setParameter("member_id", (Long)_param.get("member_id"));
					}
					if(_param.get("order_type")!=null){
						query.setParameter("order_type", (Long)_param.get("order_type"));
					}
					if(_param.get("order_state")!=null){
						query.setParameter("order_state", (Long)_param.get("order_state"));
					}
					if(_param.get("orderNo")!=null){
						query.setParameter("orderNo", (String)_param.get("orderNo"));
					}
					if(_param.get("create_time_start")!=null){
						query.setParameter("create_time_start", (Date)_param.get("create_time_start"));
					}
					if(_param.get("create_time_end")!=null){
						query.setParameter("create_time_end", (Date)_param.get("create_time_end"));
					}
					return (Long) query.uniqueResult();
				}
				
			});
			return ret;
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPayDetailList(Long orderId)
			throws BizException {
		// TODO Auto-generated method stub
		try{
			logger.info("getPayDetailList orderId:"+orderId);
			final Long _orderId = orderId;
			List<Map<String,Object>> list = EntityManagerUtil.execute(new QueryWork<List<Map<String,Object>>>() {
				@Override
				public List<Map<String,Object>> doQuery(Session session) throws Exception {
					StringBuffer hql = new StringBuffer();
					hql.append("from AMS_PayDetail where pay_order_id=:orderId");
					Query query = session.createQuery(hql.toString());
					query.setParameter("orderId", _orderId);
					return query.list();
				}
				
			});
			return list;
		}catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}





	/**
	 * MD5签名
	 * @param param
	 * @return
	 * @throws Exception
	 * @xul
	 */
	public String getCertPaySign(Map<String,Object> param)throws BizException{
		logger.info("getCertPaySign参数：" + param);
		
		String inputCharset = (String)param.get("inputCharset");
		String receiveUrl = (String)param.get("receiveUrl");
		String version = (String)param.get("version");
		String signType = (String)param.get("signType");
		String merchantId = (String)param.get("merchantId");
		String orderNo = (String)param.get("orderNo");
		Long orderAmount = (Long)param.get("orderAmount");
		String orderCurrency = (String)param.get("orderCurrency");
		String orderDatetime = (String)param.get("orderDatetime");
		String productName = (String)param.get("productName");
		String payType = (String)param.get("payType");
		String cardNo = (String)param.get("cardNo");
		String tradeNature = (String)param.get("tradeNature");
		Long applicationId = (Long)param.get("applicationId");
		Long memberId = (Long)param.get("memberId");
		
		try{
			//验证参数
			Environment environment = Environment.instance();
			
			if(!environment.getSystemProperty("certPay.receiveUrl").equals(receiveUrl))
				throw new BizException(ErrorCode.PARAM_ERROR, "后台回调地址错误");
			if(!"0".equals(signType))
				throw new BizException(ErrorCode.PARAM_ERROR, "非法签名类型"); 
			if(!("0".equals(orderCurrency) || "156".equals(orderCurrency)))
				throw new BizException(ErrorCode.PARAM_ERROR, "币种只支持人民币"); 
			
			Map<String, Object> orderEntity = getOrder(orderNo);
			logger.info("getOrder返回：" + orderEntity);
			if(orderEntity == null || orderEntity.isEmpty()){
				throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
			}
			
			//检查订单是否过期
			if(orderEntity.get("ordErexpireDatetime") != null){
				Date ordErexpireDatetime = (Date)orderEntity.get("ordErexpireDatetime");
				if(ordErexpireDatetime.before(new Date())){
					throw new BizException(ErrorCode.ORDER_PASE_DUE, "订单过期。");
				}
			}
			//检查订单是否处于未支付状态
			Long orderState = (Long)orderEntity.get("order_state");
			if(!orderState.equals(Constant.ORDER_STATE_WAIT_PAY)){
				throw new BizException(ErrorCode.ORDER_NOT_UNPAY, "订单不是未支付状态。");
			}
	        Long orderType = (Long)orderEntity.get("order_type");
	        if(!(Constant.ORDER_TYPE_DAISHOU.equals(orderType) || Constant.ORDER_TYPE_DEPOSIT.equals(orderType) || Constant.ORDER_TYPE_SHOPPING.equals(orderType)))
	        	throw new BizException(ErrorCode.ORDER_ERROR, "订单类型错误。");
			Long orderMoney = (Long)orderEntity.get("order_money");
				//验证金额
			if(!orderMoney.equals(orderAmount))
				throw new BizException(ErrorCode.ORDER_NOT_UNPAY, "订单金额错误。");
			OrderService orderService = new OrderServiceImpl();
			Map<String, Object> command = orderService.getAllCommands(orderNo).get(0);
				//验证支付方式
			Long orderPayType = (Long)command.get("pay_type");
			if(!(orderPayType == 27)){
				throw new BizException(ErrorCode.OTHER_ERROR, "订单支付方式不是移动认证支付。");
			}
			
				//验证账号
			if(!StringUtils.isBlank(cardNo)){
				String accountNo = (String)command.get("accountNo");
				if(accountNo == null || !accountNo.equals(cardNo)){
					throw new BizException(ErrorCode.OTHER_ERROR, "银行卡号和创建订单时不一致。");
				}
			}
			
			//获取key
			Map<String, Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
			if(applicationEntity == null || applicationEntity.isEmpty()){
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT, "应用不存在。");
			}
			String orgNo = (String)applicationEntity.get("codeNo");
			JSONObject payInterfaceAppConf = new JSONObject(JedisUtils.getCacheByKey(Constant.REDIS_KEY_PI_APP_CONF));
			String cacheKey = Constant.PAY_INTERFACE_CERT_PAY + "_" + orgNo;
			logger.info("cacheKey=" + cacheKey);
			
			JSONObject certPayCacheValue = (JSONObject)payInterfaceAppConf.get(cacheKey);
			if(certPayCacheValue == null || certPayCacheValue.length() == 0){
				logger.error("此用户不支持移动认证支付。");
				throw new Exception("此用户不支持移动认证支付。");
			}
			String key = (String)certPayCacheValue.get("mobile_cert_pay_key");
			logger.info("key=" + key);
			if(StringUtils.isBlank(key)){
				logger.error("密钥为空。");
				throw new Exception("密钥为空。");
			}
			
			//组装数据
			StringBuilder sb = new StringBuilder();
			String signMsg = "";
			sb.append("inputCharset="+inputCharset+"&");
			sb.append("receiveUrl="+receiveUrl+"&");
			sb.append("version="+version+"&");
			sb.append("signType="+signType+"&");
			sb.append("merchantId="+merchantId+"&");
			sb.append("orderNo="+orderNo+"&");
			sb.append("orderAmount="+orderAmount+"&");
			sb.append("orderCurrency="+orderCurrency+"&");
			sb.append("orderDatetime="+orderDatetime+"&");
			sb.append("productName="+productName+"&");
			sb.append("payType="+payType+"&");
			if(StringUtils.isBlank(tradeNature))
				sb.append("tradeNature="+tradeNature+"&");
			if(!StringUtils.isBlank(cardNo))
				sb.append("cardNo="+cardNo+"&");
			
			logger.info("=====signMsgStr:"+sb.toString());
			return (MD5Util.MD5(sb.toString())).toUpperCase();
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
	
	/**
	 * 获取所有指令
	 * @param orderNo
	 * @return
	 * @throws BizException
	 */
	public List<Map<String, Object>> getAllCommands(String orderNo) throws BizException{
		logger.info("getAllCommands参数orderNo=" + orderNo);
		
		final String _orderNo = orderNo;
		try{
			return EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>(){

				@Override
				public List<Map<String, Object>> doQuery(Session session)
						throws Exception {
					return Order.getCommands(_orderNo, session);
				}
			});
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}

	@Override
	public Map<String, Object> getOrderByPos(String posCode)  throws Exception {
		logger.info("--------getOrderByPos begin");
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR)-24);
			Date now = calendar.getTime();

			final String _now = sdf.format(now);
			final String _posCode = posCode;
			logger.info("posCode:"+posCode);
			logger.info("now:"+_now);
			Map<String,Object> ret = EntityManagerUtil.execute(new QueryWork<Map<String,Object>>() {
				@Override
				public Map<String, Object> doQuery(Session session) throws Exception {
					StringBuffer hql = new StringBuffer();
					hql.append("select  d.trade_money,o.member_name,o.application_id from AMS_Order o, AMS_OrderPayDetail d " +
							" where o.orderNo = d.bizid and o.pos_pay_code=:posCode and order_state=:order_state and o.create_time >=to_date('"+_now+"', 'yyyy-MM-dd HH24:mi:ss')");
					logger.info("sql:"+hql.toString());

					Query query = session.createQuery(hql.toString());
					query.setParameter("posCode", _posCode);
					query.setParameter("order_state", Constant.ORDER_STATE_WAIT_PAY);
					List<Object[]> list = query.list();
					if (list.isEmpty() || list.size() <= 0)
						return null;
					Object[] temp = list.get(0);

					//String create_time = (String)temp[0];
					Long trade_money = (Long)temp[0];
					String member_name = (String)temp[1];
					Long application_id = (Long)temp[2];
					Double _trade_money = trade_money / 100.00;

					java.text.DecimalFormat   df   =new   java.text.DecimalFormat("0.00");

					Map<String, Object> shop = Appliction.getApplicationMemberEntity(application_id);

					Map<String, Object> order = new HashMap<>();
					order.put("paycode" ,_posCode);
					order.put("amt", df.format( _trade_money ) );
					order.put("name", member_name);
					order.put("merchantname", shop.get("name"));

					logger.info("========返回的参数:"+ order+"=========");
					return order;
				}
			});
			return ret;
		} catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}

	@Override
	public void setPosOrder(Map<String, Object> posOrder) throws Exception {
		logger.info("OrderServiceImpl.setPosOrder start");
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR)-24);
			Date now = calendar.getTime();

			final String _now = sdf.format(now);
			final String paycode = (String)posOrder.get("paycode");
			final String terminal_id = (String)posOrder.get("terminal_id");
			final String mcht_cd = (String)posOrder.get("mcht_cd");

			EntityManagerUtil.execute(new TransactionWork<Object>() {

				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {

					StringBuffer hql = new StringBuffer();
					hql.append("select d.id from AMS_Order o, AMS_OrderPayDetail d " +
							" where o.orderNo = d.bizid and o.pos_pay_code=:posCode and order_state=:order_state and o.create_time >=to_date('"+_now+"', 'yyyy-MM-dd HH24:mi:ss')");
					logger.info("sql:"+hql.toString());

					Query query = session.createQuery(hql.toString());
					query.setParameter("posCode", paycode);
					query.setParameter("order_state", Constant.ORDER_STATE_WAIT_PAY);
					List<Object[]> list = query.list();
					if (list.isEmpty() || list.size() <= 0) {
						throw new Exception("订单过期或没有支付码对应的订单。");
					}
					Object detail_id = list.get(0);

					query = session.createQuery("update AMS_OrderPayDetail " +
							"set pos_pay_code=:pos_pay_code, terminal_id=:terminal_id, mcht_cd=:mcht_cd \n" +
							" where id=:id");

					query.setParameter("pos_pay_code", paycode);
					query.setParameter("terminal_id", terminal_id);
					query.setParameter("mcht_cd", mcht_cd);
					query.setParameter("id", detail_id);

					query.executeUpdate();
					return null;
				}

			});
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}

	}

	/**
	 * pos支付成功回调
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
	public void setPosOrderAndOver(Map<String, Object> posOrder) throws Exception {
		logger.info("OrderServiceImpl.setPosOrderAndOver start");
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR)-24);
			Date now = calendar.getTime();

			final String _now = sdf.format(now);

			final String paycode 		= (String)posOrder.get("paycode");
			final String pay_type 		= (String)posOrder.get("pay_type");
			final String trace_no 		= (String)posOrder.get("trace_no");

			final String refer_no 		= (String)posOrder.get("refer_no");
			String amt 					= (String)posOrder.get("amt");
			final String bank_card_no 	= (String)posOrder.get("bank_card_no");
			final String bank_code 		= (String)posOrder.get("bank_code");
			final String terminal_id 	= (String)posOrder.get("terminal_id");
			final String mcht_cd 		= (String)posOrder.get("mcht_cd");

			logger.info("paycode:"+paycode+"--pay_type:"+pay_type+"--trace_no:"+trace_no+"--refer_no:"+refer_no+"--amt:"+amt+"" +
					"--bank_card_no:"+bank_card_no+"--bank_code:"+bank_code+"--terminal_id:"+terminal_id+"--mcht_cd:"+mcht_cd);
			Double D_amt = Double.parseDouble(amt);
			D_amt = D_amt * 100;
			final Long _amt  = D_amt.longValue();

			EntityManagerUtil.execute(new TransactionWork<Object>() {

				@Override
				public Object doTransaction(Session session, Transaction tx)
						throws Exception {

					StringBuffer hql = new StringBuffer();
					hql.append("select d.id, d.terminal_id, d.mcht_cd,d.command_no from AMS_Order o, AMS_OrderPayDetail d " +
							" where o.orderNo = d.bizid and o.pos_pay_code=:posCode and order_state=:order_state and o.create_time >=to_date('"+_now+"', 'yyyy-MM-dd HH24:mi:ss')");
					logger.info("sql:"+hql.toString());

					Query query = session.createQuery(hql.toString());
					query.setParameter("posCode", paycode);
					query.setParameter("order_state", Constant.ORDER_STATE_WAIT_PAY);
					List<Object[]> list = query.list();
					if (list.isEmpty() || list.size() <= 0){
						throw new Exception("订单过期或没有支付码对应的订单。");
					}
					Object[] detail = list.get(0);
					if( !terminal_id.equals(detail[1]) || !mcht_cd.equals(detail[2])){
						logger.error("申请的terminal_id："+detail[1]+"------mcht_cd:"+detail[2]);
						throw new Exception("申请和确认支付时的终端号或商户号不一样！");
					}
					hql.setLength(0);
					hql.append(" update AMS_OrderPayDetail set ");

					hql.append(" pos_pay_type=:pos_pay_type,");
					hql.append(" trace_no=:trace_no,");
					hql.append(" refer_no=:refer_no,");
					//hql.append(" trade_money=:trade_money,");
					hql.append(" accountNo=:bank_card_no,");

					hql.append(" bank_code=:bank_code ");
					hql.append(" where id=:id ");

					query = session.createQuery(hql.toString());

					query.setParameter("pos_pay_type", Long.parseLong(pay_type));
					query.setParameter("trace_no", trace_no);
					query.setParameter("refer_no", refer_no);

//					query.setParameter("trade_money", _amt);
					query.setParameter("bank_card_no", bank_card_no);
					query.setParameter("bank_code", bank_code);

					query.setParameter("id", detail[0]);

					query.executeUpdate();

					Map<String, Object> param = new HashMap<>();
					param.put("orderNo", detail[3]);
					param.put("orderMoney", _amt);
					param.put("outTradeId", refer_no);
					param.put("payChannelNo", Constant.PAY_CHANNEL_POS);
					param.put("payInterfaceNo", Constant.PAY_INTERFACE_POS);
					param.put("tradeTime", new Date());
					logger.info("payChannelDeposit参数：param=" + param);

					payChannelDeposit(param);
					return null;
				}

			});
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}

	/**
	 * 查询标得订单
	 * @param order_type		订单类型
	 * @param biz_trade_code	交易码
	 * @param goodsType			商品类型
	 * @param goodsNo			商品编码
	 * @return 标得订单列表
     * @throws Exception
     */
	public List<Map<String, Object>> getOrderListByGoods(Long order_type, String biz_trade_code, Long goodsType, String goodsNo, Long orderState) throws Exception {
		logger.info("getOrderListByGoods参数order_type=" + order_type);

		final Long _order_type = order_type;
		final String _biz_trade_code = biz_trade_code;
		final Long _goodsType = goodsType;
		final String _goodsNo = goodsNo;
		final Long _orderState = orderState;
		try {
			return EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> doQuery(Session session) throws Exception {
					return Order.getOrderListByGoods(_order_type, _biz_trade_code, _goodsType, _goodsNo, _orderState, session);
				}
			});
		} catch (BizException bizE) {
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}

	@Override
	public Map<String, Object> statistics(Long application) throws BizException {
		return null;
	}

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
	public Map<String, Object> applicationTransfer(String bizTransferNo, Long applicationId, String sourceAccountSetNo, Long targetMemberId, String targetAccountSetNo, Long amount, String remark, String extendInfo) throws BizException {
		logger.info("transfer参数applicationId=" + applicationId + ",sourceAccountSetNo=" + sourceAccountSetNo + ",targetMemberId=" + targetMemberId + ",targetAccountSetNo=" + targetAccountSetNo + ",amount=" + amount + ",remark=" + remark + ",extendInfo=" + extendInfo);
		
		try{
			//检查参数
			if(applicationId == null || applicationId == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "applicationId不能为空。");
			if(StringUtils.isBlank(sourceAccountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "sourceAccountSetNo不能为空。");
			if(targetMemberId == null || targetMemberId == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "targetMemberId不能为空。");
			if(StringUtils.isBlank(targetAccountSetNo))
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "targetAccountSetNo不能为空。");
			if(amount == null || amount == 0)
				throw new BizException(ErrorCode.PARAM_ERROR_NULL, "amount不能为空或者0。");
			
			//目前只支持从平台的保证金账户或者营销专用账户转出
			if(!Constant.ACCOUNT_NO_STANDARD_BOND.equals(sourceAccountSetNo) && !Constant.ACCOUNT_NO_COUPON.equals(sourceAccountSetNo)){
				throw new BizException(ErrorCode.ACCOUNT_TYPE_ERROR, "目前只支持从保证金账户或者营销专用账户转出。");
			}
			
			Map<String, Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
			if(applicationEntity == null || applicationEntity.isEmpty())
				throw new BizException(ErrorCode.APPLICATION_NOTEXSIT, "应用不存在。");
			Map<String, Object> sourceMemberEntity = DynamicEntityService.getEntity((Long)applicationEntity.get("member_id"), "AMS_Member");
			if(sourceMemberEntity == null || sourceMemberEntity.isEmpty())
				throw new BizException(ErrorCode.USER_NOTEXSIT, "应用用户不存在。");
			
			Map<String, Object> targetMemberEntity = DynamicEntityService.getEntity(targetMemberId, "AMS_Member");
			if(targetMemberEntity == null || targetMemberEntity.isEmpty())
				throw new BizException(ErrorCode.USER_NOTEXSIT, "目标用户不存在。");
			
			//监测商户系统转账编号是否唯一
			final String _sourceUserId = (String)sourceMemberEntity.get("userId");
			final String _outBizno = bizTransferNo;
			List tradeLoglist = EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>(){
				@Override
				public List<Map<String, Object>> doQuery(Session session)
						throws Exception {
					return (List<Map<String, Object>>)Order.getTradeLog(Constant.TRADE_TYPE_TRANSFER,  Constant.SUB_TRADE_TYPE_APPLICATION, _sourceUserId, _outBizno, session);
				}
			});
			if(!(tradeLoglist == null || tradeLoglist.isEmpty())){
				throw new BizException(ErrorCode.OTHER_ERROR, "商户系统转账编号已经存在。");
			}
			
			AccountService accountService = new AccountServiceImpl();
			Map<String, Object> sourceAccountTypeEntity = accountService.getAccountTypeByNo(sourceAccountSetNo);
			Map<String, Object> targetAccountTypeEntity = accountService.getAccountTypeByNo(targetAccountSetNo);
			
			//获取云账户平台转账编号
			String bizid = CodeUtil.getCode(Constant.CODE_PREFIX_APPLICATION_TRANSFER, Constant.CODE_TYPE_APPLICATION_TRANSFER, Constant.CODE_LENGTH_APPLICATION_TRANSFER);
			
			//组装信息
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("source_userId",      (String)sourceMemberEntity.get("userId"));
	    	param.put("source_memberNo",    (String)sourceMemberEntity.get("memberNo"));
	    	param.put("source_member_name", (String)sourceMemberEntity.get("name"));
	        param.put("target_userId",      (String)targetMemberEntity.get("userId"));
	        param.put("target_memberNo",    (String)targetMemberEntity.get("memberNo"));
	        param.put("target_member_name", (String)targetMemberEntity.get("name"));
	        param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
	        param.put("sub_trade_type",     Constant.SUB_TRADE_TYPE_APPLICATION);
	        param.put("trade_money",        amount);
	        param.put("bizid",              bizid);
	        param.put("out_bizno",          bizTransferNo);
	        param.put("remark",             remark);
	        param.put("account_type_id",    (Long)sourceAccountTypeEntity.get("id"));
	        param.put("target_account_type_id",    (Long)targetAccountTypeEntity.get("id"));
	        param.put("isMaster",           true);
	        param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
	        param.put("isMaster",           true);
	        param.put("orgNo",    			(String)applicationEntity.get("codeNo"));
			
			//调用自定义转账
	        TradeService.customTransfer(param);
	        
	        Map<String, Object> ret = new HashMap<String, Object>();
	        ret.put("transferNo", bizid);
	        return ret;
		}catch(BizException e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}

}
