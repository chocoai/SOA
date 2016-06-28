package bps.external.tradecommand;

import ime.core.Reserved;
import ime.security.LoginSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import apf.util.BusinessException;
import apf.util.EntityManagerUtil;
import apf.util.SessionContext;
import apf.work.TransactionWork;
import bps.common.Constant;
import bps.member.MemberServiceImpl;
import bps.order.OrderServiceImpl;
import bps.process.Extension;
import bps.process.PayChannelManage;
import bps.process.RiskUser;
import bps.service.OrderService;

import com.kinorsoft.ams.ErrorCode;
import com.kinorsoft.ams.ITradeCommand;
import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.services.FreezeService;
import com.kinorsoft.ams.services.QueryService;
import com.kinorsoft.ams.services.TradeService;

import ime.core.Environment;

public class TltDaiFu implements ITradeCommand{
	private static Logger tlt_logger = Logger.getLogger("tlt");
		
		
	public String getPayInterfaceNo(){
		return Constant.PAY_INTERFACE_TLT_DF;
	}
	
	/**
	 * 单笔代付
	 * @param command 指令
	 * @return	执行结果
	 * @throws Exception
	 */
	public Map<String, Object> doCommand(Map<String, Object> command)throws Exception{
		tlt_logger.info("通联通代付开始:");
		tlt_logger.info("command:" + command);
	
		Environment environment = Environment.instance();
		
		if(command.get("bizid") == null)
			throw new Exception("请传入参数 bizid");
        String command_no                = (String)command.get("command_no");
        String bizid                     = (String)command.get("bizid");
		if(command.get("source_userId") == null)
			throw new Exception("请传入参数 source_userId");
		String source_userId		= (String)command.get("source_userId");
		if(command.get("trade_money") == null)
			throw new Exception("请传入参数 trade_money");
		Long trade_money = (Long)command.get("trade_money");
		Long account_type_id	= (Long)command.get("account_type_id");
		if(account_type_id == null)
			throw new Exception("请传入参数 account_type_id");
		
		String bank_code = (String)command.get("bank_code");
		if(bank_code == null)
			throw new Exception("请传入参数 bank_code");
		String accountNo_encrypt	= (String)command.get("accountNo_encrypt");
		if(accountNo_encrypt == null)
			throw new Exception("请传入参数 accountNo_encrypt");
		String account_name			= (String)command.get("account_name");
		if(account_name == null)
			throw new Exception("请传入参数 account_name");
		Long card_type				= (Long)command.get("card_type");
		if(card_type == null)
			throw new Exception("请传入参数 card_type");
		Long account_prop			= (Long)command.get("account_prop");
		if(account_prop == null)
			throw new Exception("请传入参数 account_prop");
		String orgNo = (String)command.get("orgNo");
		if(orgNo == null)
			throw new Exception("请传入参数orgNo");
		
		//查询会员账户
		Map<String, Object> member_account = QueryService.queryAccount(source_userId, account_type_id);
		if(member_account == null)
			throw new BusinessException(ErrorCode.ACCOUNT_NO_MONEY, "账户余额不足");
		Long amount = (Long)member_account.get("amount");
		Long freeze_amount	= (Long)member_account.get("freeze_amount");
		if(freeze_amount == null)
			freeze_amount = 0L;
		//修改2015-11-13 by huad 如果是代付到银行卡的，冻结订单的所有金额
//		Long to_freeze_money = trade_money;
		OrderService orderService = new OrderServiceImpl();
		Map<String,Object> order_entity = orderService.getOrder(bizid);
//		if(Constant.ORDER_TYPE_DAIFU.equals(order_entity.get("order_type"))||Constant.ORDER_TYPE_BATCH_DAIFU.equals(order_entity.get("order_type"))){
		Long to_freeze_money = (Long)order_entity.get("order_money");
//		}
		//判断账户余额
		if(amount - freeze_amount - to_freeze_money < 0 )
			throw new BusinessException(ErrorCode.ACCOUNT_NO_MONEY, "账户余额不足");
		
		tlt_logger.info("调取多账户冻结金额接口");
		final Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", 			source_userId);
		param.put("account_type_id", 	account_type_id);
		param.put("bizid", 				bizid);
		param.put("freeze_money", 		to_freeze_money);
		param.put("remark", 			"代付,冻结金额");

		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				Map<String, Object> signlog_entity = null;
				FreezeService.freezeMoney(param);
				return signlog_entity;
			}
		}); 
		
		//银行卡解密
		String key 	=  source_userId + Constant.MEMBER_BANK_ENCODE_KEY;
		String accountNo = ime.security.util.TripleDES.decrypt(key, accountNo_encrypt);
		tlt_logger.info("银行卡解密后查看是否进入黑名单筛选");
        if(!RiskUser.checkRiskUserInfo("riskBankCardNo", accountNo)) {
            throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
        }
        Map<String,Object> bank_card = new HashMap<String, Object>();
        JSONArray support_bank_list_temp = PayChannelManage.getPayInterfaceBankInfo(Constant.PAY_INTERFACE_TLT_DF); 
        if(support_bank_list_temp != null && support_bank_list_temp.length() > 0){
            for(int i = 0, j = support_bank_list_temp.length(); i < j; i ++){
            	JSONObject bank = support_bank_list_temp.getJSONObject(i);
                if(((String)bank.get("bank_code")).indexOf(bank_code) >= 0){
                    bank_card = bank.getMap();
                    break;
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        MemberServiceImpl memberServiceImpl = new MemberServiceImpl();
        Map<String, Object> member_map = memberServiceImpl.getUserInfo(source_userId);
        /*
        if(member_map != null && member_map.get("member_type") != null && member_map.get("member_type").equals(Constant.MEMBER_TYPE_ENTERPRISE)){//企业用户，判断走单笔还是批量
            if( bank_card != null && bank_card.get("single")!= null && (Boolean)bank_card.get("single")){//单笔
            	result = Extension.tltDaiFuService.singleDaiFu(command_no, account_name, accountNo, trade_money, bank_code, card_type, account_prop, null);
            } else if(bank_card != null && bank_card.get("batch")!= null && (Boolean)bank_card.get("batch")){//批量
                List<Map<String, Object>> detail_list = new ArrayList<Map<String, Object>>();
                Map<String, Object> detail = new HashMap<String,Object>();
                detail.put("ACCOUNT_NAME",  account_name);
                detail.put("ACCOUNT_NO",     accountNo);
    
                detail.put("AMOUNT",           String.valueOf(trade_money));
                detail.put("BANK_CODE",      bank_code);
                detail.put("ACCOUNT_PROP",   account_prop.toString());
                detail.put("ACCOUNT_TYPE",   "0" + card_type);
                detail.put("SN",             command_no );
    
                detail_list.add(detail);
                
            	result = Extension.tltDaiFuService.batchDaiFu(command_no, trade_money, detail_list, null);
                
              //开启线程，查询交易结果
                List<Map<String, Object>> commandList = new ArrayList<Map<String, Object>>();
                commandList.add(command);
                
                TltQueryTradeResult queryResult = new TltQueryTradeResult(commandList);
                queryResult.start();
                
                Map<String, Object> retuenValue = new HashMap<String, Object>();
                retuenValue.put("command_result",   CommandResult.PendingStop);
                return retuenValue;
            }
        }else{//个人用户走单笔
        */
		try{
			Map<String, Object> extParams = new HashMap<String, Object>();
			extParams.put("orgNo", orgNo);
			result = Extension.tltDaiFuService.singleDaiFu(command_no, account_name, accountNo, trade_money, bank_code, card_type, account_prop, extParams);
			tlt_logger.info("singleDaiFu返回:" + result);
		}catch(Exception e){
			tlt_logger.error(e.getMessage(), e);

			//开启线程，查询交易结果
			List<Map<String, Object>> commandList = new ArrayList<Map<String, Object>>();
			commandList.add(command);

			TltQueryTradeResult queryResult = new TltQueryTradeResult(commandList);
			queryResult.start();

			Map<String, Object> retuenValue = new HashMap<String, Object>();
			retuenValue.put("command_result", 	CommandResult.PendingStop);

			return retuenValue;
		}

        //}
		String ret_code1 	=  (String)result.get("RET_CODE1");
		String err_msg1		= (String)result.get("ERR_MSG1");
		String ret_code2 	=  (String)result.get("RET_CODE");
		String err_msg2		= (String)result.get("ERR_MSG");
		String ret_details	= (String)result.get("RET_DETAILS");
		//ret_code1 = "2000";
		//tlt_logger.info("----------------测试中间状态，ret_code1："+ret_code1);
		command.put("out_trade_id", command_no);
		if(ret_code1.equals("0000")){	//交易已处理
			command.put("ret_code1", ret_code1);
			command.put("err_msg1",	 err_msg1);
			command.put("ret_code2", ret_code2);
			command.put("err_msg2", err_msg2);
			
			if(ret_details != null || ret_code2.equals("0000") || ret_code2.equals("4000")){	//成功
				
				//代付成功
				daifuSucess(command);
                tlt_logger.info("daifuSucess---retuenValue=");
				
				Map<String, Object> retuenValue = new HashMap<String, Object>();
				retuenValue.put("command_result", 	CommandResult.Success);
				tlt_logger.info("retuenValue="+retuenValue);
				return retuenValue;
			}else{	//失败
				daifuFail(command);
				
				Map<String, Object> retuenValue = new HashMap<String, Object>();
				retuenValue.put("command_result", 	CommandResult.FailStop);
				retuenValue.put("ret_code1", ret_code1);
				retuenValue.put("err_msg1",	 err_msg1);
				retuenValue.put("ret_code2", ret_code2);
				retuenValue.put("err_msg2", err_msg2);
				return retuenValue;
			}
		}else if(ret_code1.equals("2000")|| ret_code1.equals("2001")||ret_code1.equals("2003")||
				ret_code1.equals("2005")|| ret_code1.equals("2007")||ret_code1.equals("2008")||
				ret_code1.equals("0003") || ret_code1.equals("0014")){	//交易处理中
			//开启线程，查询交易结果
			List<Map<String, Object>> commandList = new ArrayList<Map<String, Object>>();
			commandList.add(command);

			TltQueryTradeResult queryResult = new TltQueryTradeResult(commandList);
			queryResult.start();
			
			Map<String, Object> retuenValue = new HashMap<String, Object>();
			retuenValue.put("command_result", 	CommandResult.PendingStop);
	
			return retuenValue;
		}else{	//交易失败
			command.put("ret_code1", ret_code1);
			command.put("err_msg1",	 err_msg1);
			command.put("ret_code2", ret_code2);
			command.put("err_msg2", err_msg2);
			daifuFail(command);
			
			Map<String, Object> retuenValue = new HashMap<String, Object>();
			retuenValue.put("command_result", 	CommandResult.FailStop);
			retuenValue.put("ret_code1", ret_code1);
			retuenValue.put("err_msg1",	 err_msg1);
			retuenValue.put("ret_code2", ret_code2);
			retuenValue.put("err_msg2", err_msg2);
			return retuenValue;
		}
	}
	
	public Map<String, Object> doCommands(List<Map<String, Object>> commands)throws Exception{
		return null;
		/*
		logger.info("通联通代付开始");
		SessionContext ctx = null;
		try {
			LoginSession loginSession = new LoginSession();
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			
			ctx = EntityManagerUtil.currentContext();
			Session session 	= ctx.getHibernateSession();	//获取会话
			
			Long total_trade_money = 0L;
			String source_userId = null;
			String bizid = null;
			String remark = "";
			Long account_type_id = null;
			
			Environment environment = Environment.instance();
			String merchantId = environment.getSystemProperty("AlipayConfig.interfaces.merchantId");
			if(merchantId == null)
				throw new Exception("未设置通联商户号");
			
			for(int i=0; i<commands.size(); i++){
				Map<String, Object> command_entity = commands.get(i);
					
				if(command_entity.get("bizid") == null)
					throw new Exception("请传入参数 bizid");
				bizid				= (String)command_entity.get("bizid");
				if(command_entity.get("source_userId") == null)
					throw new Exception("请传入参数 source_userId");
				source_userId		= (String)command_entity.get("source_userId");
				if(command_entity.get("trade_money") == null)
					throw new Exception("请传入参数 trade_money");
				total_trade_money  	+= (Long)command_entity.get("trade_money");
				account_type_id	= (Long)command_entity.get("account_type_id");
				remark			= (String)command_entity.get("remark");				
				
			}
			
			//查询会员账户
			Map<String, Object> member_account = QueryService.queryAccount(source_userId, account_type_id);
			
			if(member_account == null)
				throw new BusinessException(ErrorCode.ACCOUNT_NO_MONEY, "账户余额不足");
			Long amount = (Long)member_account.get("amount");
			Long freeze_amount	= (Long)member_account.get("freeze_amount");
			if(freeze_amount == null)
				freeze_amount = 0L;
			//判断账户余额
			if(amount - freeze_amount - total_trade_money < 0 )
				throw new BusinessException(ErrorCode.ACCOUNT_NO_MONEY, "账户余额不足");
			
			Map<String, Object> order_entity = Order.getOrder(bizid, session);
			//提现金额
			Long order_id		= (Long)order_entity.get("id");
			
			logger.info("调取多账户冻结金额接口");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("userId", 			source_userId);
			param.put("account_type_id", 	account_type_id);
			param.put("bizid", 				bizid);
			param.put("freeze_money", 		total_trade_money);
			param.put("remark", 			"代付,冻结金额");
			
			Transaction tx 		= session.getTransaction();
			Boolean isActive	= !tx.isActive();
			try {
				if(isActive)	
					tx.begin();
				//冻结
				FreezeService.freezeMoney(param);
				//设置订单状态进行中
				Query query = session.createQuery("update AMS_Order set pay_channelNo=:pay_channelNo, order_state=:order_state where id=:id");
				
				query.setParameter("pay_channelNo", 	"");
				query.setParameter("id", 				order_id);
				query.setParameter("order_state", 		Constant.ORDER_STATE_PROCESSING);
				query.executeUpdate();
				
				//更新交易指令
				if(commands.size() > 1){
					for(int i=0; i<commands.size(); i++){
						Map<String, Object> command = (Map<String, Object>)commands.get(i);
						query = session.createQuery("update AMS_OrderPayDetail set trade_seq_no=:trade_seq_no, pay_state=:pay_state where id=:id");
						query.setParameter("trade_seq_no", 	command.get("trade_seq_no"));
						query.setParameter("id", 			command.get("id"));
						query.setParameter("pay_state", 	com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY);
						
						query.executeUpdate();
					}
				}
				if (isActive)
					tx.commit();
			}catch (Exception e) {
				if (isActive)
					tx.rollback();
				throw e;
			}
			
			logger.info("开始调用批量代付");
			
			
			//线程中查询交易结果
	//		TltBatchDaiFuThread tltDaifu = new TltBatchDaiFuThread(commands);
	//		tltDaifu.start();
			
			return CommandResult.PendingStop;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			EntityManagerUtil.closeSession(ctx);
			logger.info("TltAllinPay doCommands end");
		}
		*/
	}
	
	/**
	 * 代付成功
	 * @param command
	 * @throws Exception
	 */
	public static void daifuSucess(Map<String, Object> command)throws Exception{
		tlt_logger.info("TltDaiFu daifuSucess start");
		
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		final String bizid			= (String)command.get("bizid");
		final String source_userId	= (String)command.get("source_userId");
		final Long trade_money 		= (Long)command.get("trade_money");
		final Long account_type_id	= (Long)command.get("account_type_id");
		final Map<String, Object> _command = command;
		
		
		
		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				
				//修改2015-11-13 by huad 如果是代付到银行卡的，解冻订单的所有金额
//				Long to_freeze_money = trade_money;
				OrderService orderService = new OrderServiceImpl();
				Map<String,Object> order_entity = orderService.getOrder(bizid);
//				if(Constant.ORDER_TYPE_DAIFU.equals(order_entity.get("order_type"))||Constant.ORDER_TYPE_BATCH_DAIFU.equals(order_entity.get("order_type"))){
				Long to_freeze_money = (Long)order_entity.get("order_money");
//				}
				//解冻部分金额
				Map<String, Object> unfreeze_param = new HashMap<String, Object>();
				unfreeze_param.put("userId", 			source_userId);    
				unfreeze_param.put("bizid", 			bizid);
				unfreeze_param.put("account_type_id", 	account_type_id);
				unfreeze_param.put("unfreeze_money", 	to_freeze_money);
				unfreeze_param.put("remark", 			"代付成功,解冻");
				
				FreezeService.unfreezeMoney(unfreeze_param);
				
				//交易
				Long trade_type	= (Long)_command.get("trade_type");
				Map<String, Object> trade_param = new HashMap<String, Object>();
				
				trade_param.put("source_userId", 		_command.get("source_userId"));
				trade_param.put("account_type_id", 		_command.get("account_type_id"));
				trade_param.put("target_account_type_id", 		_command.get("target_account_type_id"));
				trade_param.put("trade_money", 			_command.get("trade_money"));
				trade_param.put("target_userId", 		_command.get("target_userId"));
				trade_param.put("out_trade_id", 		_command.get("out_trade_id"));
				trade_param.put("out_bizno", 			_command.get("accountNo"));
				trade_param.put("bizid", 				bizid);
                trade_param.put("command_no",           _command.get("command_no"));
				trade_param.put("remark", 				_command.get("remark"));
				trade_param.put("source_memberNo", 		_command.get("source_memberNo"));
				trade_param.put("target_memberNo", 		_command.get("target_memberNo"));
				trade_param.put("isMaster", 			_command.get("isMaster"));
				trade_param.put("orgNo", 				_command.get("orgNo"));
				trade_param.put("pay_channelNo", 		_command.get("pay_channelNo"));
				trade_param.put("sub_trade_type", 		_command.get("sub_trade_type"));
				trade_param.put("source_member_name", 	_command.get("source_member_name"));
				trade_param.put("target_member_name", 	_command.get("target_member_name"));	
				trade_param.put("trade_type", 			_command.get("trade_type"));
				trade_param.put("trade_time", 			new Date());

				trade_param.put("pay_interfaceNo", 		_command.get("pay_interfaceNo"));	
				trade_param.put("bank_code", 			_command.get("bank_code"));		
				
				if(trade_type.equals(Constant.TRADE_TYPE_TRANSFER)){	
					TradeService.customTransfer(trade_param);
				}else if(trade_type.equals(Constant.TRADE_TYPE_WITHDRAW)){
					TradeService.withdraw(trade_param);
				}else {
					throw new Exception("不支持该交易类型");
				}
				
				return null;
				
			}
		}); 
		OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
		List list = orderServiceImpl.getCommands(bizid);
        for(Object obj : list) {
            Map<String,Object> command_entity = (Map<String, Object>) obj;
            if(((String)command_entity.get("orgNo")).equals(command.get("orgNo"))) {
                Map<String,Object> temp = new HashMap<String, Object>();
                temp.put("id", command_entity.get("id"));
                temp.put("command_state", com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS);
                temp.put("out_ret_code1", "");
                temp.put("out_ret_code2", "");
                temp.put("out_errmsg1", "");
                temp.put("out_errmsg2", "");
                TradeCommandManager.setCommandState(temp);
                break;
            }
        }
	}
	
	/**
	 * 代付失败
	 * @param command
	 * @throws Exception
	 */
	public static void daifuFail(Map<String, Object> command)throws Exception{
		SessionContext ctx = null;
		try {
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			ctx = EntityManagerUtil.currentContext();
			Session session 	= ctx.getHibernateSession();	//获取会话
			Transaction tx 		= session.getTransaction();
			Boolean isActive	= !tx.isActive();
			
			String bizid			= (String)command.get("bizid");
			String source_userId	= (String)command.get("source_userId");
			Long trade_money 		= (Long)command.get("trade_money");
			Long account_type_id	= (Long)command.get("account_type_id");
			//修改2015-11-13 by huad 如果是代付到银行卡的，冻结订单的所有金额
//			Long to_freeze_money = trade_money;
			OrderService orderService = new OrderServiceImpl();
			Map<String,Object> order_entity = orderService.getOrder(bizid);
//			if(Constant.ORDER_TYPE_DAIFU.equals(order_entity.get("order_type"))||Constant.ORDER_TYPE_BATCH_DAIFU.equals(order_entity.get("order_type"))){
//				to_freeze_money = (Long)order_entity.get("order_money");
//			}
			Long to_freeze_money = (Long)order_entity.get("order_money");
			//解冻
			Map<String, Object> unfreeze_param = new HashMap<String, Object>();
			unfreeze_param.put("userId", 	source_userId);
			unfreeze_param.put("bizid", 			bizid);
			unfreeze_param.put("account_type_id", 	account_type_id);
			unfreeze_param.put("unfreeze_money", 	to_freeze_money);
			unfreeze_param.put("remark", 			"代付失败,解冻");
			
			FreezeService.unfreezeMoney(unfreeze_param);
			
		}catch (Exception e) {
			tlt_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			EntityManagerUtil.closeSession(ctx);
			tlt_logger.info("TltAllinPay daifuFail end");
		}
	}

	public Map<String, Object> undoCommand(Map<String, Object> command)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> undoCommands(List<Map<String, Object>> commands)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> calculateFees(Map<String, Object> param)
			throws Exception {
		tlt_logger.info("计算手续费");
		tlt_logger.info("param=" + param);
	    String pay_interfaceNo = (String) param.get("pay_interfaceNo");
	    Long money = (Long) param.get("trade_money");
	    
	    
	    Map<String, Object> result = new HashMap<String, Object>();
	    Long fee_money = 0L;
		JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfoPure(pay_interfaceNo);
		tlt_logger.info("payInterface_entity=" + payInterface_entity);
		if(payInterface_entity != null) {
			Long handling_type = null;
			if(payInterface_entity.get("handling_type") != null)
				handling_type = payInterface_entity.optLong("handling_type");
			Long handling_mode = null;
			if(payInterface_entity.get("handling_mode") != null)
		        handling_mode = payInterface_entity.optLong("handling_mode");
			Double handling_rate = null;
			if(payInterface_entity.get("handling_rate") != null)
				handling_rate = payInterface_entity.optDouble("handling_rate");
			if(handling_rate == null) {
				handling_rate = 0D;
			}
			Long handling_each = null;
			if(payInterface_entity.get("handling_each") != null)
				handling_each = payInterface_entity.optLong("handling_each");
			tlt_logger.info("handling_each="+handling_each);
			if(handling_each == null) {
				handling_each = 0L;
			}
			tlt_logger.info("handling_each="+handling_each);
			if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_PERCENTAGE)) {
				tlt_logger.info("111111111=");
			    fee_money = Math.round(money * handling_rate);
			} else if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_EACH)) {
				tlt_logger.info("222222222=");
			    fee_money = handling_each;
			}
	        result.put("handling_mode", handling_mode);
		}
	    result.put("fee_money", fee_money);
	    tlt_logger.info("result=" + result);
	    return result;
	}
}