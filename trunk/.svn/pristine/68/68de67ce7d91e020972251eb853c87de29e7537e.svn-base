package bps.checkaccount;

import apf.work.QueryWork;
import apf.work.TransactionWork;
import bps.common.BizException;
import bps.common.ErrorCode;
import ime.calendar.TrigerHandler;
import ime.core.Environment;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.apache.log4j.Logger;

import apf.util.EntityManagerUtil;
import apf.util.SessionContext;
import bps.common.Constant;
import bps.process.Extension;

import com.kinorsoft.tools.FileUtil;

			 
public class ItsCheckTriger implements TrigerHandler {
	
	private static Logger logger = Logger.getLogger(GatewayCheckTriger.class.getName());
	private static SimpleDateFormat trade_time_df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat dfits = new SimpleDateFormat("yyyyMMdd");
	private static Long checkAccountId = null;
	private static Long $checkAccountId = null;
	/*对账文件相对路径*/
	private static final String CHECK_FILE_PATH = "CheckAccount.allinpay.its.filePath";
	
	private static Date checkDate	= null;
	private static Date downloadDate	= null;
	
	//第二天定时触发
	public void handle() {
		logger.info("ItsCheckTriger begin ");
		try{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-1);
			Date dt = cal.getTime();
			gatewayCheckAccount(dt);
		}catch(Exception e){
			logger.error("对账失败");
			logger.error(e.getMessage(), e);
		}
		
		logger.info("GatewayCheckTriger end ");
	}
	
	/** 
	 * its对账
	 * @param date 对账日期
	*/
	public static void gatewayCheckAccount(Date date) throws Exception {
		logger.info("gatewayCheckAccount: 	date:" + dfits.format(date));
		try {
			if(!LoginSession.isLogined())
				LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			//创建对账记录
			EntityManagerUtil.execute(new TransactionWork<String>() {
				@Override
				public String doTransaction(Session session, Transaction tx)
						throws Exception {
					Map<String, String> checkAccountMap = new HashMap<String, String>();
					checkAccountMap.put("pay_channelNo", 		Constant.PAY_CHANNEL_QUICK);
					Map<String, Object> resumeMap = DynamicEntityService.createEntity("AMS_CheckAccount", checkAccountMap, null);
					checkAccountId = (Long)resumeMap.get("id");
					Map<String, Object> refundMap = DynamicEntityService.createEntity("AMS_CheckAccount", checkAccountMap, null);
					$checkAccountId = (Long)refundMap.get("id");
					return null;
				}
			});
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
		checkDate = date;
		try{				
			String settday = dfits.format(date);
		
			String checkAccountData =null;
			String filepath = null;
			String checkFilePath = null;
			InputStreamReader ins = null;
			Environment environment = Environment.instance();
			try {
	
				//获取对账文件路径
				String rootfilepath = environment.getSystemProperty(Constant.FILE_ROOT_PATH);
				if(rootfilepath == null)
					throw new Exception("未设置文件根目录");
				checkFilePath = environment.getSystemProperty(CHECK_FILE_PATH);
				if(checkFilePath == null)
					throw new Exception("未设置对账文件目录");
				filepath	= rootfilepath + checkFilePath;
				//判断文件路径
				logger.info("---filepath:"+filepath);
				if(!FileUtil.isExsit(filepath))
					FileUtil.creatIfNotExsit(filepath);
				logger.info("---FileUtil.creatIfNotExsit:");
				//获取ITS对账数据
				//checkAccountData = Extension.itsService.readFile("gbk", "10.55.3.235", 21, "ftproot", "root", localPath,fileName);
				if(Extension.itsService == null){
					logger.info("itsService服务加载失败");
					throw new Exception("itsService服务加载失败");
				}
				checkAccountData = Extension.itsService.readFile(settday);
				logger.info("get checkAccountData:"+checkAccountData);
				downloadDate = new Date();
			} catch(Exception e) {
				try {
					if(!LoginSession.isLogined())
						LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
						EntityManagerUtil.execute(new TransactionWork<String>() {
							@Override
							public String doTransaction(Session session, Transaction tx)
									throws Exception {
								Query query = session.createQuery("update AMS_CheckAccount set download_state=:download_state where id=:id");
								query.setParameter("download_state", 0L);
								query.setParameter("id", checkAccountId);
								query.executeUpdate();

								query.setParameter("download_state", 0L);
								query.setParameter("id", $checkAccountId);
								query.executeUpdate();
								return null;
							}
						});
				}catch (Exception e1) {
					logger.error("更新下载状态失败！");
					throw e1;
				}
				throw e;
			}
			//解析对账数据
	        String[] contfees = checkAccountData.split("\r\n");
			logger.info("contfees数据:"+contfees);
	        //封装对账消费数据
			Map<String,Map<String,String>> itsConsumDataMap = getTlMap(contfees,"2013");

			Iterator<Map.Entry<String, Map<String,String>>> it = itsConsumDataMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Map<String,String>> entry = it.next();
				//区别云账户和钱包,云账户含有D字母
				if(entry.getKey().indexOf(com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN) < 0 )
					it.remove();
			}
			
			StringBuilder $sb = new StringBuilder();
			for(Map<String,String> map :itsConsumDataMap.values()){
				$sb.append(map.get("orderNo"));
				$sb.append("|");
				$sb.append(map.get("tl_orderNo"));
				$sb.append("|");
				$sb.append(map.get("trade_money"));
				$sb.append("|");
				$sb.append(map.get("trade_time"));
				$sb.append("|");
				$sb.append(map.get("tradeType"));
				$sb.append("\n");
			}
			FileUtil.saveToFile($sb.toString(), filepath + File.separatorChar +  "ALLINPAY-ITS-COMSUNP"+settday + ".txt", "UTF-8");
			
			
			//封装对账退款数据
			Map<String,Map<String,String>> itsRefundDataMap = getTlMap(contfees,"6001");
			it = itsRefundDataMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Map<String,String>>entry = it.next();
				if(entry.getKey().indexOf(com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN) < 0 )
					it.remove();
			}
			
			$sb.setLength(0);
			for(Map<String,String> map :itsRefundDataMap.values()){
				$sb.append(map.get("orderNo"));
				$sb.append("|");
				$sb.append(map.get("tl_orderNo"));
				$sb.append("|");
				$sb.append(map.get("trade_money"));
				$sb.append("|");
				$sb.append(map.get("trade_time"));
				$sb.append("|");
				$sb.append(map.get("tradeType"));
				$sb.append("\n");
			}
			FileUtil.saveToFile($sb.toString(), filepath + File.separatorChar +  "ALLINPAY-ITS-REFUND"+settday + ".txt", "UTF-8");


			//查询settday交易日志
			List<Map<String,Object>> amsList = getTradeLog(date);

			List<Map<String,Object>> listcumsunp = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> listrefund = new ArrayList<Map<String,Object>>();
			StringBuilder sb = new StringBuilder();
			StringBuilder sb1 = new StringBuilder();
			sb.append("订单号|通联订单号|交易金额|交易时间|用户ID\n");
			sb1.append("订单号|通联订单号|交易金额|交易时间|用户ID\n");
			for(Map<String, Object> temp : amsList){
				if(temp.get("trade_type").equals(Constant.TRADE_TYPE_REFUNDMENT)){
					sb1.append(temp.get("trace_num")).append("|");		//订单号
					sb1.append(temp.get("out_trade_id")).append("|");	//its订单号
					sb1.append(temp.get("trade_money")).append("|");
					Date trade_time = (Date)temp.get("trade_time");
					String strDate	= trade_time_df.format(trade_time);
					sb1.append(strDate).append("|");
					sb1.append((String)temp.get("target_userId"));
					sb1.append("\n");
					listrefund.add(temp);
				}else{
					sb.append(temp.get("trace_num")).append("|");		//订单号
					sb.append(temp.get("out_trade_id")).append("|");	//its订单号
					sb.append(temp.get("trade_money")).append("|");
					Date trade_time = (Date)temp.get("trade_time");
					String strDate	= trade_time_df.format(trade_time);
					sb.append(strDate).append("|");
					sb.append((String)temp.get("target_userId"));
					sb.append("\n");
					listcumsunp.add(temp);
				}
				
			}
			//生成多帐户对账文件
			FileUtil.saveToFile(sb.toString(), filepath + File.separatorChar + "COMSUNP"+  settday + ".txt", "UTF-8");
			
			//生成多帐户退款对账文件
			FileUtil.saveToFile(sb1.toString(), filepath + File.separatorChar + "REFUND"+ settday + ".txt", "UTF-8");

			List<Map<String,Object>> listcumsunp2 = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> listrefund2 = new ArrayList<Map<String,Object>>();
			//查询长款的差异流水日志，滚动对账2天
			List<Map<String,Object>> errorAmsLongList = getErrorTradeLog(date,1L);
			//查询短款的差异流水日志，滚动对账7天
			List<Map<String,Object>> errorAmsShortList = getErrorTradeLog(date,2L);
			//长款差异流水与its消费和退款数据进行对比,如果存在数据吻合,则将该差异流水字段标为已处理
			compareError(itsConsumDataMap,errorAmsLongList);
			compareError(itsRefundDataMap,errorAmsLongList);
			//短款差异流水与交易日志数据进行对比,如果存在数据吻合,则将该差异流水字段标为已处理
			compareErrorList(amsList,errorAmsShortList);
			for(Map<String, Object> temp : amsList){
				if(temp.get("trade_type").equals(Constant.TRADE_TYPE_REFUNDMENT)){
					listrefund2.add(temp);
				}else{
					listcumsunp2.add(temp);
				}

			}
			//数据进行对比,并生成对账文件  消费
			compare(settday, itsConsumDataMap, listcumsunp2,"COMSUNP");
			//数据进行对比,并生成对账文件  退库
			compare(settday, itsRefundDataMap, listrefund2,"REFUND");
		}catch(Exception e){
			logger.error("生成对账文件失败");
			logger.error(e.getMessage(), e);
		}
		
	}
	
	/**
	 * 封装通联对账数据，以map形式返回，以交易流水号为key.
	 * @param contfees
	 * @return
	 */
	private static Map<String,Map<String,String>> getTlMap(String[] contfees,String type){
		logger.info("contfees:" + contfees.toString());
		Map<String,Map<String,String>> bankList = new HashMap<String, Map<String, String>>();
		Map<String, String> row;
		for( int i = 1; i < contfees.length; i++ ){
			row = new HashMap<String, String>();
			String[] rows = contfees[i].split(",");

			if(rows.length == 0 || !rows[2].equals(type)){
				continue;
			}
			/*row.put("orderNo", rows[3]);
			row.put("tl_orderNo", rows[13]);*/
			row.put("tl_orderNo", rows[3]);//含有D的
			row.put("orderNo", rows[13]);
			row.put("trade_money", rows[6]==null?null:String.valueOf(Double.valueOf(rows[6]).longValue()));
			row.put("trade_time", rows[14]);
			row.put("out_trade_type", rows[2]);
			bankList.put(rows[3], row);
		}
		return bankList;
	}
	
	/**
	 * 查询交易日志
	 * @param SETTDAY
	 * @return
	 * @throws Exception
	 */
	private static List<Map<String, Object>> getTradeLog(Date settDay)throws Exception{
		if(settDay == null)
			throw new Exception("请传入参数 SETTDAY");
		Calendar cal = Calendar.getInstance();
		//起始时间为8天前，结束时间为昨天
		cal.setTime(settDay);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date beginDt = cal.getTime();
//		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-1);


		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date endDt = cal.getTime();

		SessionContext ctx = null;
		List<Map<String, Object>> list = null;
		List<Map<String,Object>> _list = new ArrayList<Map<String,Object>>();
		try {
			
			ctx = EntityManagerUtil.currentContext();
			Session session 	= ctx.getHibernateSession();	//获取会话
			Query query = session.createQuery("select b.trace_num,a.trade_type,a.out_trade_id,a.trade_money,a.trade_time,a.target_userId,a.pay_interfaceNo from AMS_TradeLog a,AMS_AuthPayLog b " +
					"where a.command_no = b.ori_trace_num and a.pay_channelNo=:pay_channelNo and a.trade_time>=:beginDt and a.trade_time<=:endDt");
			query.setParameter("beginDt", beginDt);
			query.setParameter("endDt", endDt);
			query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_QUICK);
			
			list = query.list();
			
			for(Object obj:list){
				Map<String,Object>map = new HashMap<String,Object>();
				Object[] record = (Object[])obj;
				map.put("trace_num", record[0]);
				map.put("trade_type", record[1]);
				map.put("out_trade_id", record[2]);
				map.put("trade_money", record[3]);
				map.put("trade_time", record[4]);
				map.put("target_userId", record[5]);
				map.put("pay_interfaceNo", record[6]);
				_list.add(map);
			}
		}catch (Exception e) {
			throw e;
		} finally {
			EntityManagerUtil.closeSession(ctx);
		}
		return _list;
	}
	
	
	
	/**
	 * 保存对账结果文件
	 * @param SETTDAY
	 * @param bankDataList
	 * @param amsList
	 * @throws Exception
	 */
	private static void compare(String settday, Map<String,Map<String,String>> bankDataMap, List<Map<String,Object>> amsList,String type)throws Exception{
		List<String> amsHaveList = new ArrayList<String>();
		List<Map<String,Object>> amsErrorList = new ArrayList<Map<String,Object>>();
		amsHaveList.add("---------its没有的，系统有的数据---------\n");
		amsHaveList.add("订单号|通联订单号|交易金额|交易时间|用户ID\n");
		
		List<String> bankHaveList = new ArrayList<String>();
		List<Map<String,String>> bankErrorList = new ArrayList<Map<String,String>>();
		bankHaveList.add("---------its有的，系统没有的数据---------\n");
		bankHaveList.add("订单号|通联订单号|交易金额|交易时间\n");
		
		List<String> differList = new ArrayList<String>();
		List<Map<String,Object>> differErrorList = new ArrayList<Map<String,Object>>();
		differList.add("--------its和系统不同的数据---------\n");
		differList.add("商户订单号|通联订单号|交易金额|交易时间  && 订单号|通联订单号|交易金额|交易时间|用户ID\n");
		
		Environment environment = Environment.instance();
		//获取手续费
		String handlingFee = environment.getSystemProperty("AlipayConfig.gateway.handlingFee");
		if(handlingFee==null || handlingFee=="")
			handlingFee = "0";
		logger.info("handlingFee：" + handlingFee);
		
		
		if(amsList != null && bankDataMap!=null){
			for(int i=0; i<amsList.size(); i++){
				Map<String,Object> amsMap = amsList.get(i);
				String ams_order_no	 	= String.valueOf(amsMap.get("trace_num"));
				Date trade_time			= (Date)amsMap.get("trade_time");
				String ams_out_trade_id	= (String)amsMap.get("out_trade_id");
				String ams_trade_time  	= trade_time_df.format(trade_time);
				//原交易金额 + 手续费
				Long ams_trade_money	= (Long)amsMap.get("trade_money");
				String userId			= String.valueOf(amsMap.get("target_userId"));
				
				//查询通联交易数据
				logger.info("ams_order_no"+ams_order_no);
				Map<String,String> bankMap = bankDataMap.get(ams_order_no);
				
				if(bankMap == null)
					continue;
				String orderNo 			= bankMap.get("orderNo");
				String tl_orderNo		= bankMap.get("tl_orderNo");
				String tl_trade_money	= bankMap.get("trade_money");
				String tl_trade_time	= bankMap.get("trade_time");
				logger.info("tl_trade_money:"+tl_trade_money+"ams_trade_money:"+ams_trade_money);
				logger.info("campoareMoney:"+ams_trade_money.equals(Long.valueOf(tl_trade_money)));
				logger.info("campoareTradeId金额不符:"+ams_out_trade_id.equals(orderNo));
				/*if(ams_trade_money != null && ams_trade_money.equals(Long.valueOf(tl_trade_money)) && ams_out_trade_id!=null && ams_out_trade_id.equals(orderNo)){
					//完全匹配
				}*/
				if(ams_trade_money != null && ams_trade_money.equals(Long.valueOf(tl_trade_money))){
					//完全匹配
				}
				else{	//生成差异化数据
					differList.add(orderNo + "|" + tl_orderNo + "|"+ tl_trade_money +" "+ tl_trade_time + " && " 
							+ ams_order_no+ "|" + ams_out_trade_id+ "|"+ ams_trade_money + "|" + ams_trade_time + "|" + userId +"\n");
					
					Map<String,Object> temp = new HashMap<String, Object>();
					temp.put("bizid", amsMap.get("trace_num"));
					temp.put("e_trade_money", amsMap.get("trade_money"));
					temp.put("userId", amsMap.get("target_userId"));
					temp.put("trade_time", amsMap.get("trade_time"));
					temp.put("pay_interfaceNo", amsMap.get("pay_interfaceNo"));
					temp.put("out_trade_id", bankMap.get("orderNo"));
					temp.put("out_trade_money", bankMap.get("trade_money"));
					temp.put("out_trade_type", bankMap.get("out_trade_type"));
					differErrorList.add(temp);
				}
				
				bankDataMap.remove(ams_order_no);	//删除通联集合数据
				amsList.remove(i);					//删除多帐户集合数据
				i--;								//复位
			}
			
		}
		//多帐户集合中是否存在剩余数据
		if( amsList != null){
			for(int i=0; i<amsList.size(); i++){
				Map<String,Object> amsMap = amsList.get(i);
				String ams_order_no	 	= String.valueOf(amsMap.get("trace_num"));
				Date trade_time			= (Date)amsMap.get("trade_time");
				String ams_out_trade_id	= (String)amsMap.get("out_trade_id");
				String ams_trade_time  	= trade_time_df.format(trade_time);
				String ams_trade_money	= String.valueOf(amsMap.get("trade_money"));
				String userId			= String.valueOf(amsMap.get("target_userId"));
				
				amsHaveList.add(ams_order_no+"|"+ams_out_trade_id+ "|"+ ams_trade_money + "|" + ams_trade_time + "|" + userId +"\n");
				amsErrorList.add(amsMap);
			}
		}
		
		//its数据集合中是否剩余数据
		if(bankDataMap != null){
			for(String key : bankDataMap.keySet()){
				Map<String,String> bankMap = bankDataMap.get(key);
				String orderNo 		= bankMap.get("orderNo");
				String tl_orderNo	= bankMap.get("tl_orderNo");
				String tl_trade_money	= bankMap.get("trade_money");
				String tl_trade_time	= bankMap.get("trade_time");

				bankHaveList.add(orderNo+"|"+ tl_orderNo +"|"+ tl_trade_money +"|"+ tl_trade_time +"\n");
				bankErrorList.add(bankMap);
			}
		}
		
//		amsList =  DealItsCheckData.longRecord(amsList, settday);
//		bankDataMap = DealItsCheckData.shortRecord(bankDataMap, settday);
		
		
		
		String strContent = "";
		
		for( int i = 0; i < amsHaveList.size(); i++ ){
			strContent += amsHaveList.get(i);
		}
		for( int i = 0; i < bankHaveList.size(); i++ ){
			strContent += bankHaveList.get(i);
		}
		for( int i = 0; i < differList.size(); i++ ){
			strContent += differList.get(i);
		}
		//文件路径
		String rootfilepath = environment.getSystemProperty(Constant.FILE_ROOT_PATH);
		String checkFilePath = environment.getSystemProperty(CHECK_FILE_PATH);
		
		String filePath = rootfilepath + checkFilePath;
		FileUtil.saveToFile(strContent, filePath + File.separatorChar +"ITS-RESULT-" + type + settday + ".txt", "UTF-8");
		//对账结果
		Long checkResult = Constant.CHECK_RESULT_ERROR;
		if(amsHaveList.size()==2 && bankHaveList.size()==2 && differList.size()==2)
			checkResult = Constant.CHECK_RESULT_OK;
		
		SessionContext ctx = null;
		try {
			
			ctx = EntityManagerUtil.currentContext();
			Session session 	= ctx.getHibernateSession();	//获取会话

			StringBuffer sb = new StringBuffer();
			sb.append("update AMS_CheckAccount set checkDate=:checkDate, checkResult=:checkResult, outSystemFile=:outSystemFile")
				.append(", checkAccountFile=:checkAccountFile, checkResultFile=:checkResultFile, download_state=:download_state")
				.append(", check_state=:check_state, download_time=:download_time, check_time=:check_time where id=:id");
			Query query = session.createQuery(sb.toString());
			query.setParameter("checkDate", checkDate);
			query.setParameter("checkResult", checkResult);
			query.setParameter("outSystemFile", "ALLINPAY-ITS-" + type+settday + ".txt:" + checkFilePath + "/" + "ALLINPAY-ITS-" +type+ settday + ".txt");
			query.setParameter("checkAccountFile", type+settday + ".txt:" + checkFilePath + "/" + type+settday + ".txt");
			query.setParameter("checkResultFile", "ITS-RESULT-" + type+settday + ".txt:" + checkFilePath + "/" + "ITS-RESULT-" +type+ settday + ".txt");
			query.setParameter("download_state", 1L);
			query.setParameter("check_state", 1L);
			query.setParameter("download_time", downloadDate);
			query.setParameter("check_time", new Date());
			if("COMSUNP".equals(type))
				query.setParameter("id", checkAccountId);
			if("REFUND".equals(type))
				query.setParameter("id", $checkAccountId);
			Transaction tx 		= session.getTransaction();
			Boolean isActive	= !tx.isActive();
			
			LoginSession loginSession = new LoginSession();
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			try{
				if(isActive)
					tx.begin();
				query.executeUpdate();

				if(checkResult.equals(Constant.CHECK_RESULT_ERROR)) {
					for( int i = 0; i < amsErrorList.size(); i++ ){
						logger.info("--------------amsErrorList----------------");
						Map<String, Object> amsError = amsErrorList.get(i);
						Map<String, String> errorTradeLogMap = new HashMap<String, String>();
						errorTradeLogMap.put("bizid", 				amsError.get("trace_num").toString());
						errorTradeLogMap.put("out_trade_id", 		amsError.get("out_trade_id").toString());
						errorTradeLogMap.put("e_trade_money", 		amsError.get("trade_money").toString());
						errorTradeLogMap.put("pay_interfaceNo", 	amsError.get("pay_interfaceNo").toString());
						errorTradeLogMap.put("pay_channelNo", 		Constant.PAY_CHANNEL_QUICK);
						errorTradeLogMap.put("trade_type", 			Constant.TRADE_TYPE_DEPOSIT.toString());
						Long trade_time = ((Date)amsError.get("trade_time")).getTime();
						errorTradeLogMap.put("trade_time", 			trade_time.toString());
						//长款
						errorTradeLogMap.put("error_type", 			"1");
						errorTradeLogMap.put("pay_interface_name", 	"ITS");
						errorTradeLogMap.put("idDealwith", Boolean.valueOf(false).toString());
						//errorTradeLogMap.put("FM_CreateTime",String.valueOf(trade_time_df.parse(trade_time_df.format(new Date()))));
						logger.info(errorTradeLogMap);
						DynamicEntityService.createEntity("AMS_ErrorTradeLog", errorTradeLogMap, null);
					}
					for( int i = 0; i < bankErrorList.size(); i++ ){
						logger.info("--------------bankErrorList----------------");
						Map<String, String> bankError = bankErrorList.get(i);
						Map<String, String> errorTradeLogMap = new HashMap<String, String>();
						errorTradeLogMap.put("bizid", 				bankError.get("tl_orderNo"));
						errorTradeLogMap.put("out_trade_id", 		bankError.get("orderNo"));
						String money = (String)bankError.get("trade_money");
						errorTradeLogMap.put("out_trade_money", 	money.toString());
						errorTradeLogMap.put("pay_channelNo", 		Constant.PAY_CHANNEL_QUICK);
						errorTradeLogMap.put("trade_type", 			Constant.TRADE_TYPE_DEPOSIT.toString());
						errorTradeLogMap.put("trade_time", 		String.valueOf(dfits.parse(bankError.get("trade_time")).getTime()));
						//短款
						errorTradeLogMap.put("error_type", 			"2");
						errorTradeLogMap.put("out_trade_type", 		bankError.get("out_trade_type"));
						errorTradeLogMap.put("pay_interfaceNo", 	Constant.PAY_INTERFACE_QUICK);
						errorTradeLogMap.put("pay_interface_name", 	"ITS");
						errorTradeLogMap.put("idDealwith", Boolean.valueOf(false).toString());
						//errorTradeLogMap.put("FM_CreateTime",String.valueOf(trade_time_df.parse(trade_time_df.format(new Date()))));
						logger.info(errorTradeLogMap);
						DynamicEntityService.createEntity("AMS_ErrorTradeLog", errorTradeLogMap, null);
					}
					for( int i = 0; i < differErrorList.size(); i++ ){
						logger.info("--------------differErrorList----------------");
						Map<String, Object> differError = differErrorList.get(i);
						Map<String, String> errorTradeLogMap = new HashMap<String, String>();
						errorTradeLogMap.put("bizid", 				differError.get("bizid").toString());
						errorTradeLogMap.put("e_trade_money", 		differError.get("e_trade_money").toString());
						errorTradeLogMap.put("pay_interfaceNo", 	differError.get("pay_interfaceNo").toString());
						errorTradeLogMap.put("pay_channelNo", 		Constant.PAY_CHANNEL_QUICK);
						errorTradeLogMap.put("trade_type", 			Constant.TRADE_TYPE_DEPOSIT.toString());
						Long trade_time = ((Date)differError.get("trade_time")).getTime();
						errorTradeLogMap.put("trade_time", 			trade_time.toString());
						errorTradeLogMap.put("out_trade_id", 		differError.get("out_trade_id")==null? null:differError.get("out_trade_id").toString());
						String money = (String)differError.get("out_trade_money");
						errorTradeLogMap.put("out_trade_money", 	money);
						//金额不同
						errorTradeLogMap.put("error_type", 			"3");
						errorTradeLogMap.put("out_trade_type", 		differError.get("out_trade_type").toString());
						errorTradeLogMap.put("pay_interface_name", 	"ITS");
						errorTradeLogMap.put("idDealwith", Boolean.valueOf(false).toString());
						//errorTradeLogMap.put("FM_CreateTime",String.valueOf(trade_time_df.parse(trade_time_df.format(new Date()))));
						logger.info(errorTradeLogMap);
						DynamicEntityService.createEntity("AMS_ErrorTradeLog", errorTradeLogMap, null);
					}
				}
				if (isActive)
					tx.commit();
			}catch (Exception e) {
				if (isActive)
					tx.rollback();
				throw e;
			}
		}catch (Exception e) {
			throw e;
		}finally {
			EntityManagerUtil.closeSession(ctx);
			logger.info("compare end");
		}
	}
	/**
	 * 获取差异流水日志
	 * @param settDay 		当天的前一天
	 * @param errorType     差异流水类型
	 * @return
	 * @throws BizException
	 */
	private static List<Map<String, Object>> getErrorTradeLog(Date settDay,Long errorType)throws Exception{
		logger.info("getErrorTradeLog:"+settDay);
		if(settDay == null)
			throw new Exception("请传入参数 SETTDAY");
		Calendar cal = Calendar.getInstance();
		Calendar ca2 = Calendar.getInstance();
		//起始时间为settDay的前7天，结束时间为settDay
		cal.setTime(settDay);
		if(Long.valueOf(1).equals(errorType)){
			cal.add(Calendar.DATE,-2);
		}else{
			cal.add(Calendar.DATE,-7);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Long beginDt =trade_time_df.parse(trade_time_df.format(cal.getTime())).getTime();
		final Long _beginDt = beginDt;

		ca2.setTime(settDay);
		cal.add(Calendar.DATE,-1);
		ca2.set(Calendar.HOUR_OF_DAY, 23);
		ca2.set(Calendar.MINUTE, 59);
		ca2.set(Calendar.SECOND, 59);
		Long endDt =trade_time_df.parse(trade_time_df.format(ca2.getTime())).getTime();
		final Long _endDt = endDt;
		final Long _errorType = errorType;
		//List<Map<String, Object>> list = null;
		List<Map<String,Object>> _list = new ArrayList<Map<String,Object>>();
		logger.info("beginDt:"+_beginDt+"---_endDt:"+_endDt);
		try {
			_list = EntityManagerUtil.execute(new QueryWork<List<Map<String,Object>>>() {
				@Override
				public List<Map<String,Object>> doQuery(Session session) throws Exception {
					Query query = session.createQuery("select a.bizid,a.trade_type,a.out_trade_id,a.e_trade_money,a.trade_time,a.userId,a.pay_interfaceNo,a.out_trade_money " +
							"from AMS_ErrorTradeLog a where a.pay_channelNo=:pay_channelNo and to_number(a.trade_time)>=:beginDt and to_number(a.trade_time)<=:endDt and error_type=:error_type" +
							" and idDealwith=:isDealwith");
					query.setParameter("beginDt", _beginDt);
					query.setParameter("endDt", _endDt);
					query.setParameter("error_type", _errorType);
					query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_QUICK);
					query.setParameter("isDealwith", Boolean.valueOf(false));
					List<Object> list = query.list();
					List<Map<String,Object>> _list = new ArrayList<Map<String,Object>>();
					for(Object obj:list){
						Map<String,Object>map = new HashMap<String,Object>();
						Object[] record = (Object[])obj;
						Long eTradeMoney = (Long)record[3];
						Long outTradeMoney = (Long)record[7];
						Long  tradeMoney =null;
						//存在两种金额,是ams和its订单号相同,金额不同,则过滤.
						if(eTradeMoney != null && outTradeMoney != null){
							continue;
						}
						if(eTradeMoney == null && outTradeMoney != null){
							tradeMoney=outTradeMoney;
						}else if(eTradeMoney != null && outTradeMoney == null){
							tradeMoney=eTradeMoney;
						}
						map.put("trace_num", record[0]);
						map.put("trade_type", record[1]);
						map.put("out_trade_id", record[2]);
						map.put("trade_money", tradeMoney);
						map.put("e_trade_money",record[3]);
						map.put("out_trade_money",record[7]);
						map.put("trade_time", record[4]);
						map.put("target_userId", record[5]);
						map.put("pay_interfaceNo", record[6]);
						_list.add(map);
					}
					return _list;
				}
			});
			return  _list;
		}catch (Exception e) {
			logger.info("获取差错流水日志错误！");
			throw e;
		}
	}
	private static void compareError(Map<String,Map<String,String>> tempDataMap, List<Map<String,Object>> amsErrorList)throws Exception{
		logger.info("compareError:"+"---tempDataMap:"+tempDataMap);
		logger.info("amsErrorList:"+"---amsErrorList:"+amsErrorList);
		if(amsErrorList != null && tempDataMap!=null ) {
			for (int i = 0; i < amsErrorList.size(); i++) {
					Map<String,Object> amsMap = amsErrorList.get(i);
					final String ams_order_no	 	= String.valueOf(amsMap.get("trace_num"));
					final String ams_trade_time		= (String)amsMap.get("trade_time");
					final String ams_out_trade_id	= (String)amsMap.get("out_trade_id");
					//String ams_trade_time  	= trade_time_df.format(trade_time);

					final Long ams_trade_money	= (Long)amsMap.get("trade_money") == null ? 0:(Long)amsMap.get("trade_money");
					final Long e_trade_money	= (Long)amsMap.get("e_trade_money");
					final Long eout_trade_money	= (Long)amsMap.get("out_trade_money");
					final String userId			= String.valueOf(amsMap.get("target_userId"));

					//查询通联交易数据
					logger.info("ams_order_no"+ams_order_no);
					Map<String,String> tempMap = tempDataMap.get(ams_order_no);
					if(tempMap == null )
						continue;
					final String orderNo 		= tempMap.get("orderNo");
					final String tl_orderNo		= tempMap.get("tl_orderNo");
					final String tl_trade_money	= tempMap.get("trade_money");
					final String tl_trade_time	= tempMap.get("trade_time");
					logger.info("ams_out_trade_id:"+ams_out_trade_id+"ams_order_no:"+ams_order_no+"ams_trade_time:"+ams_trade_time+"ams_trade_money:"+ams_trade_money);
					/*if(ams_trade_money.equals(Long.valueOf(tl_trade_money)) && ams_out_trade_id!=null && ams_out_trade_id.equals(orderNo)){*/
					if(ams_trade_money.equals(Long.valueOf(tl_trade_money))){
						//完全匹配 则修改差异流水表中的字段是否处理为已处理
						try{
							if(!LoginSession.isLogined())
								LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
								EntityManagerUtil.execute(new TransactionWork<String>() {
								@Override
								public String doTransaction(Session session, Transaction transaction)
										throws Exception {
									boolean isDealwith = true;
									boolean unDealwith = false;
									StringBuilder sb = new StringBuilder();
									sb.append("update AMS_ErrorTradeLog set idDealwith="+isDealwith+" where  pay_channelNo=:pay_channelNo and idDealwith="+unDealwith+" " +
											"and error_type <>3");
									if(ams_out_trade_id != null){
										sb.append("and out_trade_id=:out_trade_id ");
									}
									if(ams_order_no != null){
										sb.append("and  bizid=:bizid ");
									}
									if(ams_trade_time != null){
										sb.append("and trade_time=:trade_time ");
									}
									if(e_trade_money != null){
										sb.append("and e_trade_money=:e_trade_money ");
									}
									if(eout_trade_money != null){
										sb.append("and out_trade_money=:out_trade_money ");
									}
									logger.info("sql:"+sb.toString());
									Query query = session.createQuery(sb.toString());
									if(ams_out_trade_id != null){
										query.setParameter("out_trade_id", ams_out_trade_id);
									}
									if(ams_order_no != null){
										query.setParameter("bizid", ams_order_no);
									}
									if(ams_trade_time != null){
										query.setParameter("trade_time", ams_trade_time);
									}
									if(e_trade_money != null){
										query.setParameter("e_trade_money", e_trade_money);
									}
									if(eout_trade_money != null){
										query.setParameter("out_trade_money", eout_trade_money);
									}
									query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_QUICK);
									query.executeUpdate();
									return null;
								}
							});
							tempDataMap.remove(ams_order_no);	//删除内存中通联集合数据
							amsErrorList.remove(i);				//删除多帐户集合数据
							i--;								//复位
							logger.info("==========================MonitorServiceImpl.addHealthMonitorRecord end=========================");
						}catch(Exception e){
							logger.error(e.getMessage());
							throw e;
						}
					}else{	//不做任何处理

					}
			}
		}

		}
	private static void compareErrorList(List<Map<String,Object>> amsList, List<Map<String,Object>> amsErrorShortList)throws Exception{
		logger.info("compareErrorList:"+"---amsList:"+amsList+"amsErrorShortList:"+amsErrorShortList);
		if(amsList != null && amsErrorShortList!=null ) {
			for (int i = 0; i < amsErrorShortList.size(); i++) {
				boolean flag = false;
				Map<String,Object> amsErrorMap = amsErrorShortList.get(i);
				final String ams_order_no	 	= String.valueOf(amsErrorMap.get("trace_num"));
				final String ams_trade_time		= (String)amsErrorMap.get("trade_time");
				final String ams_out_trade_id	= (String)amsErrorMap.get("out_trade_id");
				//String ams_trade_time  	= trade_time_df.format(trade_time);

				final Long ams_trade_money	= (Long)amsErrorMap.get("trade_money") == null ? 0:(Long)amsErrorMap.get("trade_money");
				final Long e_trade_money	= (Long)amsErrorMap.get("e_trade_money");
				final Long eout_trade_money	= (Long)amsErrorMap.get("out_trade_money");
				final String userId			= String.valueOf(amsErrorMap.get("target_userId"));

				//查询通联交易数据
				logger.info("ams_order_no"+ams_order_no);
				for(int j = 0; j < amsList.size(); j++){

					String log_orderNo = (String)amsList.get(j).get("trace_num");
					if(ams_order_no != null && ams_order_no.equals(log_orderNo)){
						//final String log_orderNo 		= (String)amsList.get(j).get("trace_num");
						final String orderNo		= (String)amsList.get(j).get("out_trade_id");
						final Long tl_trade_money	= (Long)amsList.get(j).get("trade_money");
						/*if(ams_trade_money.equals(Long.valueOf(tl_trade_money)) && ams_out_trade_id!=null && ams_out_trade_id.equals(orderNo)){*/
						if(ams_trade_money.equals(Long.valueOf(tl_trade_money)) ){
								try{
								if(!LoginSession.isLogined())
									LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
								EntityManagerUtil.execute(new TransactionWork<String>() {
									@Override
									public String doTransaction(Session session, Transaction transaction)
											throws Exception {
										boolean isDealwith = true;
										boolean unDealwith = false;
										StringBuilder sb = new StringBuilder();
										sb.append("update AMS_ErrorTradeLog set idDealwith="+isDealwith+" where  pay_channelNo=:pay_channelNo and idDealwith="+unDealwith+" " +
												"and error_type <>3");
										if(ams_out_trade_id != null){
											sb.append("and out_trade_id=:out_trade_id ");
										}
										if(ams_order_no != null){
											sb.append("and  bizid=:bizid ");
										}
										if(ams_trade_time != null){
											sb.append("and trade_time=:trade_time ");
										}
										if(e_trade_money != null){
											sb.append("and e_trade_money=:e_trade_money ");
										}
										if(eout_trade_money != null){
											sb.append("and out_trade_money=:out_trade_money ");
										}
										logger.info("sql:"+sb.toString());
										Query query = session.createQuery(sb.toString());
										if(ams_out_trade_id != null){
											query.setParameter("out_trade_id", ams_out_trade_id);
										}
										if(ams_order_no != null){
											query.setParameter("bizid", ams_order_no);
										}
										if(ams_trade_time != null){
											query.setParameter("trade_time", ams_trade_time);
										}
										if(e_trade_money != null){
											query.setParameter("e_trade_money", e_trade_money);
										}
										if(eout_trade_money != null){
											query.setParameter("out_trade_money", eout_trade_money);
										}
										query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_QUICK);
										query.executeUpdate();
										return null;
									}
								});
								flag =true;

								logger.info("==========================MonitorServiceImpl.addHealthMonitorRecord end=========================");
							}catch(Exception e){
								logger.error(e.getMessage());
								throw e;
							}
						}
					}
					if(flag){
						amsList.remove(j);							//删除内存中交易日志的数据
						j--;
					}
				}
				if(flag){
					amsErrorShortList.remove(i);				//删除多帐户集合数据
					i--;
				}
			}
		}

	}
}
