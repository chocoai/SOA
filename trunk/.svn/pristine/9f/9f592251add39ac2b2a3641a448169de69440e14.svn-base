package bps.external.tradecommand;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import bps.order.OrderServiceImpl;
import bps.process.Extension;

import com.kinorsoft.ams.TradeCommandManager;
import com.kinorsoft.ams.services.TradeService;

import ime.core.event.Event;
import ime.core.event.EventManager;

public class TltQueryTradeResult extends Thread{
	private static Logger tlt_logger = Logger.getLogger("tlt");
	/**转账等待时间*/
	
	/**等待时间*/
//	private static int WAIT_TIME = 1000 * 60*5;	//5分钟
	private static int WAIT_TIME = 1000 * 60 * 1;	//5分钟
	
	private SimpleDateFormat trade_time_df = new SimpleDateFormat("yyyyMMddHHmmss");
	private boolean stoped = false;
	private List<Map<String, Object>> _command_list = null;
	private String merchantId = "";
	
	public TltQueryTradeResult(List<Map<String, Object>> command_list){
		_command_list = command_list;
	}
	
	public void run() {
		int z = 12*3;	//查询次数
		int k = 0;
		while( !stoped ){
			k ++;
			tlt_logger.info("thread:" + k);
			if(k > z)
				stoped = true;
			try {
				if(stoped)
					break;
				
				synchronized(this){
					sleep(WAIT_TIME);
				}
				
				//查询交易结果
				Date dt = new Date();
				String requestId = "Q" + dt.getTime();
				
				String command_no = (String)_command_list.get(0).get("command_no");
				String bizid = (String)_command_list.get(0).get("bizid");
				String orgNo = (String)_command_list.get(0).get("orgNo");
				//设置流水号
//				Map<String, String> req_param = new HashMap<String, String>();
//				req_param.put("REQ_SN", 		merchantId + requestId);
//				req_param.put("QUERY_SN", 		merchantId + command_no);
				Map<String, Object> extParams = new HashMap<String, Object>();
	        	extParams.put("orgNo", orgNo);
				
				tlt_logger.info("通联查询交易结果开始：REQ_SN=" + (merchantId + requestId) + ",QUERY_SN=" +command_no+ ",extParams=" + extParams);
				Map<String, Object> result = Extension.tltDaiFuService.queryTradeResult(merchantId + requestId, command_no, extParams);
				tlt_logger.info("通联通查询结果返回:" + result);
				
				if(result == null)
					continue;
				//报文结果
				String ret_code1	= (String)result.get("RET_CODE1");
				String err_msg1		=(String)result.get("ERR_MSG1");
				
				//报文明细
				List qtDetailList = (List)result.get("DETAIL");
				//判断是否处理成功
				String ret_code2 	= (String)result.get("RET_CODE");
				String err_msg2	= (String)result.get("ERR_MSG");
				OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
				if(ret_code1!=null && ret_code1.equals("0000")){	//交易已处理
					stoped = true;	//可停止循环		
					if(qtDetailList == null){	//单笔
						
						Map<String, Object> command = _command_list.get(0);
						command.put("ret_code1", ret_code1);
						command.put("err_msg1",	 err_msg1);
						command.put("ret_code2", ret_code2);
						command.put("err_msg2", err_msg2);
						String trx_code	= (String)result.get("TRX_CODE");
						if(ret_code2.equals("0000") || ret_code2.equals("4000")){	//代付成功
							if("100014".equals(trx_code)){		//代付
								TltDaiFu.daifuSucess(command);
							}else if("100011".equals(trx_code)){	//代扣
								//充值
								Map<String, Object> param = new HashMap<String, Object>();
								param.put("source_userId", 		command.get("source_userId"));
								param.put("account_type_id", 	command.get("account_type_id"));
								param.put("target_account_type_id", 	command.get("target_account_type_id"));
								param.put("trade_money", 		command.get("trade_money"));
								param.put("target_userId", 		command.get("target_userId"));
								param.put("out_trade_id", 		merchantId + command_no);
								param.put("out_bizno", 			command.get("accountNo"));
								param.put("command_no", 		command_no);
								param.put("bizid", 				bizid);
								param.put("remark", 			command.get("remark"));
								param.put("source_memberNo", 	command.get("source_memberNo"));
								param.put("target_memberNo", 	command.get("target_memberNo"));
								param.put("isMaster", 			command.get("isMaster"));
								param.put("orgNo", 				command.get("orgNo"));
								param.put("pay_channelNo", 		command.get("pay_channelNo"));
								param.put("pay_interfaceNo", 	command.get("pay_interfaceNo"));
								param.put("bank_code", 			command.get("bank_code"));
								param.put("sub_trade_type", 	command.get("sub_trade_type"));
								param.put("source_member_name", command.get("source_member_name"));
								param.put("target_member_name", command.get("target_member_name"));
								
								
								TradeService.deposit(param);
							}

							//设置指令状态
							command.put("command_state", 	com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS);
							if(command.get("id") != null)
								TradeCommandManager.setCommandState(command);
							//继续执行指令
							if(command.get("id") != null)
								TradeCommandManager.instance().doCommands(bizid, (Long)command.get("id"));
							else{	
								// 触发订单已完成支付事件
								Map<String, Object> param = new HashMap<String, Object>();
								param.put("orderNo", bizid);
								Event event = new Event(com.kinorsoft.ams.Constant.EVENT_TYPE_ORDERCOMPLETEPAY, param, null);
								EventManager.instance().fireEvent(event);
							}
						}else{	//失败
							command.put("command_state", 	com.kinorsoft.ams.Constant.COMMAND_STATE_FAIL);
							if("100014".equals(trx_code))		//代付
								TltDaiFu.daifuFail(command);
							
							if(command.get("id") != null)		//指令情况下
								TradeCommandManager.setCommandState(command);
							//设置订单状态
							TradeCommandManager.instance().undoCommands(bizid);
							
//							Map<String, Object> param = new HashMap<String, Object>();
//							param.put("ret_code1", ret_code1);
//							param.put("err_msg1",	 err_msg1);
//							param.put("ret_code2", ret_code2);
//							param.put("err_msg2", err_msg2);
//							orderServiceImpl.closeOrder(bizid, param);
						}
					}else{	//批量
                        Map<String, Object> command = _command_list.get(0);
                        command.put("ret_code1", ret_code1);
                        command.put("err_msg1",  err_msg1);
                        command.put("ret_code2", ret_code2);
                        command.put("err_msg2", err_msg2);
					    //for(int i = 0, j = qtDetailList.size(); i < j; i ++) {
                        if(qtDetailList.size() != 0){
					        Map<String, String> detail = (Map<String, String>)qtDetailList.get(0);
	                        ret_code2       = (String)detail.get("RET_CODE");
	                        err_msg2        = (String)detail.get("ERR_MSG");
	                        
	                        command.put("ret_code2", ret_code2);
	                        command.put("err_msg2", err_msg2);
	                        //ret_code2 = "0000";
	                        String trx_code    = (String)result.get("TRX_CODE");
	                            
	                        if(ret_code2.equals("0000") || ret_code2.equals("4000")){   //取现成功
	                            if("100014".equals(trx_code) || "200004".equals(trx_code)){     //代付
	                                TltDaiFu.daifuSucess(command);
	                            }else if("100011".equals(trx_code)){    //代扣
	                                //充值
	                                Map<String, Object> param = new HashMap<String, Object>();
	                                param.put("source_userId",      command.get("source_userId"));
	                                param.put("account_type_id",    command.get("account_type_id"));
	                                param.put("target_account_type_id",    command.get("target_account_type_id"));
	                                param.put("trade_money",        command.get("trade_money"));
	                                param.put("target_userId",      command.get("target_userId"));
	                                param.put("out_trade_id",       merchantId + command_no);
	                                param.put("out_bizno",          command.get("accountNo"));
									param.put("command_no", 		command_no);
	                                param.put("bizid",              bizid);
	                                param.put("remark",             command.get("remark"));
	                                param.put("source_memberNo",    command.get("source_memberNo"));
	                                param.put("target_memberNo",    command.get("target_memberNo"));
	                                param.put("isMaster",           command.get("isMaster"));
	                                param.put("orgNo",              command.get("orgNo"));
	                                param.put("pay_channelNo",      command.get("pay_channelNo"));
	                                param.put("pay_interfaceNo",    command.get("pay_interfaceNo"));
	                                param.put("bank_code",          command.get("bank_code"));
	                                param.put("sub_trade_type",     command.get("sub_trade_type"));
	                                param.put("source_member_name", command.get("source_member_name"));
	                                param.put("target_member_name", command.get("target_member_name"));
	                                
	                                
	                                TradeService.deposit(param);
	                            }
	                            //设置指令状态
	                            command.put("command_state",    com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS);
	                            if(command.get("id") != null)
	                                TradeCommandManager.setCommandState(command);
	                            //继续执行指令
	                            if(command.get("id") != null)
	                                TradeCommandManager.instance().doCommands(bizid, (Long)command.get("id"));
	                            else{   
	                                // 触发订单已完成支付事件
	                                Map<String, Object> param = new HashMap<String, Object>();
	                                param.put("orderNo", bizid);
	                                Event event = new Event(com.kinorsoft.ams.Constant.EVENT_TYPE_ORDERCOMPLETEPAY, param, null);
	                                EventManager.instance().fireEvent(event);
	                            }
	                        } else {
	                            command.put("command_state",   com.kinorsoft.ams.Constant.COMMAND_STATE_FAIL);
	                            if("100014".equals(trx_code))       //代付
	                                TltDaiFu.daifuFail(command);
	                            
	                            if(command.get("id") != null)       //指令情况下
	                                TradeCommandManager.setCommandState(command);
	                            
	                            //如果订单失败 走undoCommands
	                            TradeCommandManager.instance().undoCommands(bizid);
	                            //设置订单状态
//	                            Map<String, Object> param = new HashMap<String, Object>();
//	                            param.put("ret_code1", ret_code1);
//	                            param.put("err_msg1",    err_msg1);
//	                            param.put("ret_code2", ret_code2);
//	                            param.put("err_msg2", err_msg2);
	                            
//	                            orderServiceImpl.closeOrder(bizid, param);
	                        }
					    }
					}
				}else if(ret_code1!=null && (ret_code1.equals("2000")||ret_code1.equals("2001")||ret_code1.equals("2003")||
						ret_code1.equals("2005")||ret_code1.equals("2007")||ret_code1.equals("2008"))){
					//继续等待
					stoped = false;
				}else{
					stoped = true;
					if(qtDetailList == null){	//单笔
						Map<String, Object> command = _command_list.get(0);
						command.put("ret_code1", ret_code1);
						command.put("err_msg1",	 err_msg1);
						command.put("ret_code2", ret_code2);
						command.put("err_msg2", err_msg2);
						String trx_code	= (String)result.get("TRX_CODE");
						
						command.put("command_state", 	com.kinorsoft.ams.Constant.COMMAND_STATE_FAIL);
						if("100014".equals(trx_code))		//代付
							TltDaiFu.daifuFail(command);
						
						if(command.get("id") != null)		//指令情况下
							TradeCommandManager.setCommandState(command);
						
						//如果订单失败 走undoCommands
                        TradeCommandManager.instance().undoCommands(bizid);
						
						//设置订单状态
//						Map<String, Object> param = new HashMap<String, Object>();
//						param.put("ret_code1", ret_code1);
//						param.put("err_msg1",	 err_msg1);
//						param.put("ret_code2", ret_code2);
//						param.put("err_msg2", err_msg2);
//						
//						orderServiceImpl.closeOrder(bizid, param);
						
					}else{	//批量
					    if(qtDetailList.size() != 0){
					        Map<String, Object> command = _command_list.get(0);
	                        command.put("ret_code1", ret_code1);
	                        command.put("err_msg1",  err_msg1);
	                        command.put("ret_code2", ret_code2);
	                        command.put("err_msg2", err_msg2);
	                        String trx_code = (String)result.get("TRX_CODE");
	                        
	                        command.put("command_state",    com.kinorsoft.ams.Constant.COMMAND_STATE_FAIL);
	                        if("100014".equals(trx_code))       //代付
	                            TltDaiFu.daifuFail(command);
	                        
	                        if(command.get("id") != null)       //指令情况下
	                            TradeCommandManager.setCommandState(command);
	                        
	                        //如果订单失败 走undoCommands
                            TradeCommandManager.instance().undoCommands(bizid);
	                        
//	                        //设置订单状态
//	                        Map<String, Object> param = new HashMap<String, Object>();
//	                        param.put("ret_code1", ret_code1);
//	                        param.put("err_msg1",    err_msg1);
//	                        param.put("ret_code2", ret_code2);
//	                        param.put("err_msg2", err_msg2);
//	                        
//	                        orderServiceImpl.closeOrder(bizid, param);
					    }
					}
				}
			}catch(Exception e){
				tlt_logger.error(e.getMessage(), e);
			}
		}
	}
	/*
	public void run() {
		Long total_trade_money = 0L;
		String source_userId = null;
		String bizid = null;
		Long account_type_id = null;
		String merchantId = "";
		//设置代付参数
		List<Map<String, String>> trans_detailLList = new ArrayList<Map<String, String>>();
		Map<String, Object>commandsMap 	= new HashMap<String, Object>();
		try{
			Environment environment = Environment.instance();
			merchantId = environment.getSystemProperty("AlipayConfig.interfaces.merchantId");
			
			
			for(int i=0; i<_command_list.size(); i++){
				Map<String, Object> command_entity = _command_list.get(i);
					
				if(command_entity.get("bizid") == null)
					throw new Exception("请传入参数 bizid");
				bizid				= (String)command_entity.get("bizid");
				if(command_entity.get("source_userId") == null)
					throw new Exception("请传入参数 source_userId");
				source_userId		= (String)command_entity.get("source_userId");
				if(command_entity.get("trade_money") == null)
					throw new Exception("请传入参数 trade_money");
				total_trade_money  	+= (Long)command_entity.get("trade_money");
	//			remark				= (String)command_entity.get("remark");
				if(command_entity.get("account_type_id") == null)
					throw new Exception("请传入参数 account_type_id");
				account_type_id		= (Long)command_entity.get("account_type_id");
				
				Map<String, String>detailparam = new HashMap<String, String>();
				if(command_entity.get("account_name") == null)
					throw new Exception("请传入参数 account_name");
				detailparam.put("ACCOUNT_NAME", 	(String)command_entity.get("account_name"));				//户名
				if(command_entity.get("accountNo") == null)
					throw new Exception("请传入参数 accountNo");
				String en_accountNo	= (String)command_entity.get("accountNo_encrypt");
				
				//银行卡解密
				String key = source_userId + Constant.MEMBER_BANK_ENCODE_KEY;
				String accountNo = ime.security.util.TripleDES.decrypt(key, en_accountNo);
				
				detailparam.put("ACCOUNT_NO", 		accountNo);				//银行帐号
				if(command_entity.get("trade_money") == null)
					throw new Exception("请传入参数 trade_money");
				detailparam.put("AMOUNT", 			command_entity.get("trade_money").toString());	//金额
				if(command_entity.get("bank_code") == null)
					throw new Exception("请传入参数 bank_code");
				detailparam.put("BANK_CODE", 		command_entity.get("bank_code").toString());					//银行代码
				if(command_entity.get("ACCOUNT_PROP") == null)
					detailparam.put("ACCOUNT_PROP", 	"0");		//默认为个人
				
				int seq = i+1;
				String sn = "";
				for(int j = 0; j<(4-String.valueOf(seq).length()); j++){
					sn += "0";
				}
				sn += i;
				detailparam.put("SN", sn);
				
				//设置交易指令中交易顺序号
				command_entity.put("trade_seq_no", sn);
				
				trans_detailLList.add(detailparam);
				
				Map<String, Object> command 	= (Map<String, Object>)_command_list.get(i);
				commandsMap.put((String)command.get("trade_seq_no"), command);
			}
		}catch(Exception e){
			logger.info(e.getMessage(), e);
			return;
		}
		
		
		Map<String, Object> reqParam = new HashMap<String, Object>();
		reqParam.put("DETAIL", 			trans_detailLList);
		reqParam.put("REQ_SN", 			merchantId + bizid);
		reqParam.put("BUSINESS_CODE", 	"09900");
		reqParam.put("TOTAL_SUM", 		total_trade_money);
		reqParam.put("TOTAL_ITEM", 		_command_list.size());
		
		
		Map<String, Object> result_daifu = null;	//通联支付返回结果
		try{	//批量代付
			result_daifu = TranxServiceImpl.batchDaiFushi(reqParam, false);	
		}catch(Exception e){
			pay_logger.info("代付失败");
			logger.error(e.getMessage(), e);
		}
			
		logger.info("通联代付返回值:" + result_daifu);

		int z = 12*6;	//查询次数
		int k = 0;
		
		
		while( !stoped ){
			k ++;
			pay_logger.info("thread:" + k);
			if(k > z)
				stoped = true;
			try {
				if(stoped)
					break;
				
				synchronized(this){
					sleep(WAIT_TIME);
				}
				
				//查询交易结果
				SessionContext ctx = null;
				try{
					LoginSession loginSession = new LoginSession();
					LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
					
					ctx = EntityManagerUtil.currentContext();
					Session session 	= ctx.getHibernateSession();	//获取会话
					Transaction tx 		= session.getTransaction();
					Boolean isActive	= !tx.isActive();
					
					Date dt = new Date();
					String requestId = "Q" + dt.getTime();
					
					//设置流水号
					Map<String, String> req_param = new HashMap<String, String>();
					req_param.put("REQ_SN", 		merchantId + requestId);
					req_param.put("QUERY_SN", 		merchantId + bizid);
					
					logger.info("通联查询交易结果开始：" + req_param);
					
					Map<String, Object> result = null;
					try{
						result = TranxServiceImpl.queryTradeResult(req_param, false);
						/*
						result = new HashMap<String, Object>();
						result.put("RET_CODE1", "0000");
						List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
						resultList.add(new HashMap<String, String>());
						result.put("DETAIL", resultList);
						*/
	/*				}catch(Exception e){
						logger.error(e.getMessage(), e);
						result = null;
					}
					logger.info("通联查询结果返回:" + result);
					
					if(result == null)
						continue;
					//报文结果
					String ret_code1	= (String)result.get("RET_CODE1");
					String err_msg1		=(String)result.get("ERR_MSG1");
					//报文明细
					List qtDetailList = (List)result.get("DETAIL");
					
					//判断是否处理成功
					String err_msg2	= "";
					String ret_code2 = "";
					if(ret_code1!=null && ret_code1.equals("0000")){
						stoped = true;	//可停止循环		
						
						for(int i=0; i<qtDetailList.size(); i++){	//判断明细
							Map<String, String> detail = (Map<String, String>)qtDetailList.get(0);
							ret_code2 	= (String)detail.get("RET_CODE");
							err_msg2	= (String)detail.get("ERR_MSG");
							String sn	= detail.get("SN");
							
							//获取对应交易指令
							Map<String, Object> detial_command = (Map<String, Object>)commandsMap.get(sn);
							if(ret_code2.equals("0000") || ret_code2.equals("4000")){	//取现成功
								session 	= ctx.getHibernateSession();	//获取会话
								tx 			= session.getTransaction();
								isActive	= !tx.isActive();
								try{	
									if(isActive)
										tx.begin();
										//解冻部分金额
										Map<String, Object> unfreeze_param = new HashMap<String, Object>();
										unfreeze_param.put("userId", 	detial_command.get("source_userId"));
										unfreeze_param.put("bizid", 			bizid);
										unfreeze_param.put("account_type_id", 	detial_command.get("account_type_id"));
										unfreeze_param.put("unfreeze_money", 	detial_command.get("trade_money"));
										unfreeze_param.put("remark", 			"代付成功,解冻");
										
										FreezeService.unfreezeMoney(unfreeze_param);
										
										//交易
										Long trade_type	= (Long)detial_command.get("trade_type");
										Map<String, Object> trade_param = new HashMap<String, Object>();
										
										trade_param.put("source_userId", 		detial_command.get("source_userId"));
										trade_param.put("account_type_id", 		detial_command.get("account_type_id"));
										trade_param.put("trade_money", 			detial_command.get("trade_money"));
										trade_param.put("target_userId", 		detial_command.get("target_userId"));
										trade_param.put("out_trade_id", 		"");
										trade_param.put("out_bizno", 			"");
										trade_param.put("bizid", 				bizid);
										trade_param.put("source_memberNo", 		detial_command.get("source_memberNo"));
										trade_param.put("target_memberNo", 		detial_command.get("target_memberNo"));
										trade_param.put("isMaster", 			detial_command.get("isMaster"));
										trade_param.put("orgNo", 				detial_command.get("orgNo"));
										trade_param.put("pay_channelNo", 		detial_command.get("pay_channelNo"));
										trade_param.put("sub_trade_type", 		detial_command.get("sub_trade_type"));
										trade_param.put("source_member_name", 	detial_command.get("source_member_name"));
										trade_param.put("target_member_name", 	detial_command.get("target_member_name"));	
										trade_param.put("trade_type", 			detial_command.get("trade_type"));
										trade_param.put("trade_seq_no", 		sn);
										
										if(trade_type.equals(Constant.TRADE_TYPE_TRANSFER)){	
											TradeService.customTransfer(trade_param);
										}else{
											TradeService.withdraw(trade_param);
										}
										
										//更新交易指令
										Map<String, Object> command_result = new HashMap<String, Object>();
										command_result.put("ret_code1", 	ret_code1);
										command_result.put("ret_code2", 	ret_code2);
										command_result.put("err_msg2", 		err_msg2);
										command_result.put("err_msg1", 		err_msg1);
										command_result.put("command_state", com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS);
										
										if(detial_command.get("id") == null){
											command_result.put("command_state", 	Constant.ORDER_STATE_SUCCESS);
											setOrderState(bizid, command_result, session);
										}else{
											command_result.put("command_state", 	com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS);
											setCommandState((Long)detial_command.get("id"), command_result, session);
										}
									if (isActive)
										tx.commit();
								}catch (Exception e) {
									pay_logger.error("通联取现使用冻结金额失败");
									pay_logger.error(e.getMessage(), e);
									logger.error(e.getMessage(), e);
									pay_logger.error("userId:" + detial_command.get("source_userId") + " trade_money:" 
											+ detial_command.get("trade_money") + " bizid:" + bizid + sn);
								
									if (isActive)
										tx.rollback();
								}
							}else{	//失败
								
								session 	= ctx.getHibernateSession();	//获取会话
								tx 			= session.getTransaction();
								isActive	= !tx.isActive();
								try{	
									if(isActive)
										tx.begin();
									//解冻部分金额
									Map<String, Object> unfreeze_param = new HashMap<String, Object>();
									unfreeze_param.put("userId", 	detial_command.get("source_userId"));
									unfreeze_param.put("bizid", 			bizid);
									unfreeze_param.put("account_type_id", 	detial_command.get("account_type_id"));
									unfreeze_param.put("unfreeze_money", 	detial_command.get("trade_money"));
									unfreeze_param.put("remark", 			"代付失败,解冻");
									
									FreezeService.unfreezeMoney(unfreeze_param);
									
									//更新交易指令
									Map<String, Object> command_result = new HashMap<String, Object>();
									command_result.put("ret_code1", 	ret_code1);
									command_result.put("ret_code2", 	ret_code2);
									command_result.put("err_msg2", 		err_msg2);
									command_result.put("err_msg1", 		err_msg1);
									
									if(detial_command.get("id") == null){
										command_result.put("command_state", 	Constant.ORDER_STATE_FAIL);
										setOrderState(bizid, command_result, 	session);
									}else{
										command_result.put("command_state", com.kinorsoft.ams.Constant.COMMAND_STATE_FAIL);
										setCommandState((Long)detial_command.get("id"), command_result, session);
									}
									if(isActive)
									tx.commit();
								}catch (Exception e) {
									pay_logger.error("通联代付解冻金额失败");
									logger.error(e.getMessage(), e);
									pay_logger.error(e.getMessage(), e);
									pay_logger.error("userId:" + detial_command.get("source_userId") + " trade_money:" 
											+ detial_command.get("trade_money") + " bizid:" + bizid + " sn:" + sn);
									if (isActive)
										tx.rollback();
								}
							}
						}
					}else if(ret_code1!=null && (ret_code1.equals("2000")||ret_code1.equals("2001")||ret_code1.equals("2003")||
							ret_code1.equals("2005")||ret_code1.equals("2007")||ret_code1.equals("2008"))){
						//继续等待
						stoped = false;
					}else{
						stoped = true;
						session 	= ctx.getHibernateSession();	//获取会话
						tx 			= session.getTransaction();
						isActive	= !tx.isActive();
						try{
							if(isActive)
								tx.begin();
							//解冻
							Map<String, Object> unfreeze_param = new HashMap<String, Object>();
							unfreeze_param.put("userId", 	source_userId);
							unfreeze_param.put("bizid", 			bizid);
							unfreeze_param.put("account_type_id", 	account_type_id);
							unfreeze_param.put("unfreeze_money", 	total_trade_money);
							unfreeze_param.put("remark", 			"代付失败,解冻");
							
							FreezeService.unfreezeMoney(unfreeze_param);
							//更新订单交易结果
							Map<String, Object> command_result = new HashMap<String, Object>();
							command_result.put("ret_code1", 	ret_code1);
							command_result.put("ret_code2", 	ret_code2);
							command_result.put("err_msg2", 		err_msg2);
							command_result.put("err_msg1", 		err_msg1);
							command_result.put("command_state", Constant.ORDER_STATE_FAIL);
							
							setOrderState(bizid, command_result, session);
							
							//更新指令交易结果
							for(int h=0; h<_command_list.size(); h++){
								command_result.put("command_state", com.kinorsoft.ams.Constant.COMMAND_STATE_FAIL);
								if(_command_list.get(h).get("id") != null)
									setCommandState((Long)_command_list.get(h).get("id"), command_result, session);
							}
							if (isActive)
								tx.commit();
						}catch (Exception e) {
							pay_logger.error("通联代付解冻金额失败");
							logger.error(e.getMessage(), e);
							pay_logger.error(e.getMessage(), e);
							pay_logger.error("userId:" + source_userId + " trade_money:" 
									+ total_trade_money + " bizid:" + bizid );
							if (isActive)
								tx.rollback();
						}
					}
				}catch(Exception e){
					logger.error(e.getMessage(), e);
					pay_logger.error(e.getMessage(), e);
				}finally {
					EntityManagerUtil.closeSession(ctx);
				}
			}catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	*/
	
	
	
	
}
