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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import apf.util.EntityManagerUtil;
import apf.util.SessionContext;
import bps.common.Constant;
import bps.common.JedisUtils;
import bps.process.Extension;

import com.kinorsoft.allinpay.interfaces.TranxServiceImpl;
import com.kinorsoft.tools.FileUtil;


/**
 * 对账文件下载Test
 * 
 * 如果有任何对代码的修改,请按下面的格式注明修改的内容.
 * 
 * 序号 时间 作者 修改内容
 * 
 * 1. 2011-3-9 Robin created this class.
 * 错误代码描述：
 * ERRORCODE:001 ERRORDES:系统繁忙.请稍候............
 * ERRORCODE:002 ERRORDES:请传入有效的商户号,结算日期,signMsg
 * ERRORCODE:003 ERRORDES:结算日期格式错误(格式为yyyy-MM-dd)
 * ERRORCODE:004 ERRORDES:商户号不存在或者MD5key没有设置
 * ERRORCODE:005 ERRORDES:摘要信息验证有误
 * ERRORCODE:006 ERRORDES:没有相应的对账信息
 * 
 * 
 */
public class InterfacesCheckTriger implements TrigerHandler{
	
	private static Logger logger = Logger.getLogger(InterfacesCheckTriger.class.getName());
	private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	private static SimpleDateFormat trade_time_df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat trade_time_tlt = new SimpleDateFormat("yyyyMMddHHmmss");
	/*对账文件相对路径*/
	private static final String CHECK_FILE_PATH = "CheckAccount.allinpay.tlt.filePath";
	
	private static Date checkDate	= null;
	private static Date downloadDate	= null;
	private static Long checkAccountId = null;

	public void handle(){
		logger.info("InterfacesCheckTriger begin ");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-1);
		final Date dt = cal.getTime();

	    JSONArray list = null;
	    JSONObject piAppConfObj = null;
		try{

        	logger.info("orgList:"+JedisUtils.getCacheByKey("orgList"));
        	list = new JSONArray(JedisUtils.getCacheByKey(("orgList")));
			piAppConfObj = new JSONObject(JedisUtils.getCacheByKey((Constant.REDIS_KEY_PI_APP_CONF)));
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
		
	    try{
		    if(null !=list && piAppConfObj != null){
		    	final JSONObject _piAppConfObj = piAppConfObj;
				for(int i=0;i<list.length();i++){
					JSONObject obj = list.getJSONObject(i);
					final String codeNo = obj.optString("codeNo");
					//暂时先对账单笔代付、批量代付?通String[]应用的多个对账json
					final String key = Constant.PAY_INTERFACE_TLT_DF + "_" + codeNo;
					final String key2 = Constant.PAY_INTERFACE_TLT_BACH_DF + "_" + codeNo;
					JSONArray jsonArray = new JSONArray();
					if(!StringUtils.isBlank(codeNo) &&  (!_piAppConfObj.isNull(key) || !_piAppConfObj.isNull(key2))){
						if(!_piAppConfObj.isNull(key)){
							logger.info("存在此应用渠道："+key);
							JSONObject piAppConfObjApplication = (JSONObject)_piAppConfObj.get(key);
							jsonArray.put(piAppConfObjApplication);
						}
						if(!_piAppConfObj.isNull(key2)){
							logger.info("存在此应用渠道："+key2);
							JSONObject piAppConfObjApplication = (JSONObject)_piAppConfObj.get(key2);
							jsonArray.put(piAppConfObjApplication);
						}
						logger.info("jsonArray"+jsonArray+";codeNo:"+codeNo);
						interfaceCheckAccount(dt,jsonArray,codeNo);
					}
				}
	        }
	    }catch(Exception e){
		    logger.error(e.getMessage(),e);
	    }
	}
	
	/**
	 * 通联通对账
	 * @param date 对账日期
	*/
	public static void interfaceCheckAccount(Date date,JSONArray jsonArray,String codeNo) throws Exception {
		logger.info("interfaceCheckAccount: 	date:" + df.format(date));
		try {
			if(!LoginSession.isLogined())
				LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			//创建对账记录
			EntityManagerUtil.execute(new TransactionWork<String>() {
				@Override
				public String doTransaction(Session session, Transaction tx)
						throws Exception {
					Map<String, String> checkAccountMap = new HashMap<String, String>();
					checkAccountMap.put("pay_channelNo", 		Constant.PAY_CHANNEL_TLT);
					Map<String, Object> map = DynamicEntityService.createEntity("AMS_CheckAccount", checkAccountMap, null);
					checkAccountId = (Long)map.get("id");
					return null;
				}
			});

		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
		checkDate = date;
		try{				
			String settday = df.format(date);
			String checkAccountData = null;
			String filepath = null;
			String checkFilePath = null;
			
			Environment environment = Environment.instance();
			
			try {
				
				if(Extension.tltDaiFuService == null){
					logger.info("tltDaiFuService服务加载失败");
					throw new Exception("tltDaiFuService服务加载失败");
				}
				for(int i = 0;i<jsonArray.length();i++){
					JSONObject jsonObject = (JSONObject)jsonArray.get(i);
					String piAppConfObjApplicationStr = jsonObject.toString();
					checkAccountData += Extension.tltDaiFuService.getCheckAccountDate(settday,piAppConfObjApplicationStr)+"\r\n";
				/*	logger.info("checkAccountData+"+i+":"+checkAccountData);*/
				}
				/*String piAppConfObjApplicationStr = piAppConfObjApplication.toString();
				checkAccountData = Extension.tltDaiFuService.getCheckAccountDate(settday,piAppConfObjApplicationStr);*/

				String rootfilepath = environment.getSystemProperty(Constant.FILE_ROOT_PATH);
				if(rootfilepath == null){
					throw new Exception("未设置文件根目录");
				}
				checkFilePath = environment.getSystemProperty(CHECK_FILE_PATH);
				if(checkFilePath == null){
					throw new Exception("未设置对账文件目录");
				}
				filepath	= rootfilepath + checkFilePath;
				//判断文件路径
				logger.info("---filepath:"+filepath);
				if(!FileUtil.isExsit(filepath)){
					FileUtil.creatIfNotExsit(filepath);
				}
				logger.info("---FileUtil.creatIfNotExsit:");
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
	        //封装对账数据
			Map<String,Map<String,String>> tlDataMap = getTlMap(contfees);
			Iterator<Map.Entry<String, Map<String,String>>> it = tlDataMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Map<String,String>> entry = it.next();
				//区别云账户和钱包,云账户含有D字母
				if(entry.getKey().indexOf(com.kinorsoft.ams.Constant.COMMAND_SPLIT_SIGN) < 0 )
					it.remove();
			}
			logger.info("tlDataMap:"+tlDataMap);


			StringBuilder $sb = new StringBuilder();
			for(Map<String,String> map :tlDataMap.values()){
				$sb.append(map.get("REQ_SN"));
				$sb.append("|");
				$sb.append(map.get("SN"));
				$sb.append("|");
				$sb.append(map.get("REQ_TYPE"));
				$sb.append("|");
				$sb.append(map.get("REQ_CODE"));
				$sb.append("|");
				$sb.append(map.get("AMOUNT"));
				$sb.append("|");
				$sb.append(map.get("REQ_DATE"));
				$sb.append("|");
				$sb.append(map.get("SETTDAY"));
				$sb.append("\n");
			}
			//生成通联对账文件
			String file = filepath + File.separatorChar  + "ALLINPAY-INTERFACE-" + codeNo+"-"+settday + ".txt";
			logger.info("file:"+file);
			FileUtil.saveToFile($sb.toString(), file, "UTF-8");

		/*	
	        Map<String,Map<String,String>> tlDataMap = new HashMap<String,Map<String,String>>();
	        Map<String,String> map = new HashMap<String,String>();
	        map.put("REQ_SN", "1602167594162852D1");
	        map.put("SN", "0");
	        map.put("REQ_TYPE", "0");
	        map.put("REQ_CODE", "0000");
	        map.put("AMOUNT", "11000");
	        map.put("CODE_NO", "6222024402056813078");
	        map.put("REQ_DATE", "20160217164017");
	        map.put("SETTDAY", "20160217");
	        tlDataMap.put("1602167594162852D1", map);
			*/



			//查询交易日志
			List<Map<String,Object>> amsList = getTradeLog(date,codeNo);
			StringBuilder sb = new StringBuilder();
			sb.append("订单号|通联订单号|交易金额|交易时间|用户ID\n");
		
			for(Map<String, Object> temp : amsList){
				sb.append(temp.get("command_no")).append("|");		//订单号
				String out_trade_id = (String)temp.get("out_trade_id");
				sb.append(out_trade_id).append("|");	//通联订单号
				sb.append(temp.get("trade_money")).append("|");
				Date trade_time = (Date)temp.get("trade_time");
				String strDate	= trade_time_df.format(trade_time);
				sb.append(strDate).append("|");
				sb.append((String)temp.get("source_userId"));
				sb.append("\n");
			}
			//生成多帐户对账文件
			FileUtil.saveToFile(sb.toString(), filepath + File.separatorChar + codeNo+"-"+settday + ".txt", "UTF-8");

			//查询长款的差异流水日志，滚动对账2天
			List<Map<String,Object>> errorAmsLongList = getErrorTradeLog(date,1L,codeNo);
			//查询短款的差异流水日志，滚动对账7天
			List<Map<String,Object>> errorAmsShortList = getErrorTradeLog(date,2L,codeNo);
			logger.info("errorAmsLongList:"+errorAmsLongList);
			logger.info("errorAmsShortList:"+errorAmsShortList);
			//长款差异流水与网关支付和退款数据进行对比,如果存在数据吻合,则将该差异流水字段标为已处理
			compareError(tlDataMap,errorAmsLongList);
			//compareError(tlRefunDataMap,errorAmsLongList);

			//短款差异流水与交易日志数据进行对比,如果存在数据吻合,则将该差异流水字段标为已处理
			compareErrorList(amsList,errorAmsShortList);


			logger.info("compare begin_settday:"+settday+";codeNo:"+codeNo+";checkAccountId:"+checkAccountId);
			logger.info("tlDataMap:"+tlDataMap);
			logger.info("amsList:"+amsList);
			//数据进行对比,并生成对账文件
			compare(settday, tlDataMap, amsList,codeNo,checkAccountId);

		}catch(Exception e){
			logger.error("生成对账文件失败");
			logger.error(e.getMessage(), e);
		}
		
		
	}
	
	
	/**
	 * 封装通联数据
	 * @param contfees
	 * @return
	 */
	private static Map<String,Map<String,String>> getTlMap(String[] contfees){
		Map<String,Map<String,String>> bankList = new HashMap<String, Map<String, String>>();
		Map<String, String> row;
		for( int i = 1; i < contfees.length; i++ ){
			row = new HashMap<String, String>();
			String[] rows = contfees[i].split(" ");
			//取消抬头
			if("PDSMK".equals(rows[0])){
				continue;
			}
			row.put("REQ_SN", rows[0]);
			row.put("SN", rows[1]);
			row.put("REQ_TYPE", rows[2]);
			row.put("REQ_CODE", rows[3]);
			row.put("AMOUNT", rows[4]);
			//对方账号
			row.put("CODE_NO", rows[5]);
			//交易时间
			row.put("REQ_DATE", rows[6]);
			//结算日期
			row.put("SETTDAY", rows[7]);
			
			bankList.put(rows[0], row);
		}
		return bankList;
	}
	
	/**
	 * 查询交易日志
	 * @param SETTDAY
	 * @return
	 * @throws Exception
	 */
	private static List<Map<String, Object>> getTradeLog(Date settDay,String codeNo)throws Exception{
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
		List<Map<String, Object>> list2 = null;
		logger.info("getTradeLog:beginDt:"+beginDt+";endDt:"+endDt);
		try {
			
			ctx = EntityManagerUtil.currentContext();
			Session session 	= ctx.getHibernateSession();	//获取会话

			Query query = session.createQuery("from AMS_TradeLog where pay_channelNo=:pay_channelNo and orgNo = :orgNo and trade_time>=:beginDt and trade_time<=:endDt");
			query.setParameter("beginDt", beginDt);
			query.setParameter("endDt", endDt);
			query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_TLT);
			query.setParameter("orgNo", codeNo);
			list = query.list();
			
			/*if(!list.isEmpty()) {
				list2 = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> map : list) {
					if(Constant.TRADE_TYPE_WITHDRAW.equals((Long)map.get("trade_type"))) {
						list2.add(map);
					} else if(Constant.TRADE_TYPE_TRANSFER.equals((Long)map.get("trade_type")) && Constant.SUB_TRADE_TYPE_2BANK.equals((Long)map.get("sub_trade_type"))) {
						list2.add(map);
					}
				}
				return list2;
			}
			*/
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		} finally {
			EntityManagerUtil.closeSession(ctx);
		}
		return list;
	}
	/**
	 * 获取差异流水日志
	 * @param settDay 		当天的前一天
	 * @param errorType     差异流水类型
	 * @return
	 * @throws BizException
	 */
	private static List<Map<String, Object>> getErrorTradeLog(Date settDay,Long errorType,String codeNo)throws Exception{
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
		final String orgNo = codeNo;
		//List<Map<String, Object>> list = null;
		List<Map<String,Object>> _list = new ArrayList<Map<String,Object>>();
		logger.info("beginDt:"+_beginDt+"---_endDt:"+_endDt);
		try {
			_list = EntityManagerUtil.execute(new QueryWork<List<Map<String,Object>>>() {
				@Override
				public List<Map<String,Object>> doQuery(Session session) throws Exception {
					Query query = session.createQuery("select a.bizid,a.trade_type,a.out_trade_id,a.e_trade_money,a.trade_time,a.userId,a.pay_interfaceNo,a.out_trade_money " +
							"from AMS_ErrorTradeLog a  where a.pay_channelNo=:pay_channelNo and to_number(a.trade_time)>=:beginDt and to_number(a.trade_time)<=:endDt and error_type=:error_type" +
							" and idDealwith=:isDealwith ");
					query.setParameter("beginDt", _beginDt);
					query.setParameter("endDt", _endDt);
					query.setParameter("error_type", _errorType);
					query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_TLT);
					query.setParameter("isDealwith", Boolean.valueOf(false));
					/*query.setParameter("orgNo", orgNo);*/
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
				final String orderNo 		= tempMap.get("REQ_SN");
				final String tl_trade_money	= tempMap.get("AMOUNT");
				final String tl_trade_time	= tempMap.get("REQ_DATE");
				logger.info("ams_out_trade_id:"+ams_out_trade_id+"ams_order_no:"+ams_order_no+"ams_trade_time:"+ams_trade_time+"ams_trade_money:"+ams_trade_money);
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
								query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_TLT);
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

					String log_orderNo = (String)amsList.get(j).get("command_no");
					if(ams_order_no != null && ams_order_no.equals(log_orderNo)){
						//final String log_orderNo 		= (String)amsList.get(i).get("trace_num");
						final String orderNo		= (String)amsList.get(j).get("out_trade_id");
						final Long tl_trade_money	= (Long)amsList.get(j).get("trade_money");
						if(ams_trade_money.equals(Long.valueOf(tl_trade_money))){
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
										query.setParameter("pay_channelNo", Constant.PAY_CHANNEL_TLT);
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

	private static void compare(String settday, Map<String,Map<String,String>> bankDataMap, List<Map<String,Object>> amsList,String codeNo,Long checkAccountId)throws Exception{
		List<String> amsHaveList = new ArrayList<String>();
		List<Map<String,Object>> amsErrorList = new ArrayList<Map<String,Object>>();
		amsHaveList.add("---------接口没有的，系统有的数据---------\n");
		amsHaveList.add("业务编号 |外部交易流水号 | 交易时间｜交易金额 ｜用户ID\n");
		List<String> bankHaveList = new ArrayList<String>();
		List<Map<String,String>> bankErrorList = new ArrayList<Map<String,String>>();
		bankHaveList.add("---------接口有的，系统没有的数据---------\n");
		bankHaveList.add("交易批次号｜ 交易序号 ｜交易类型 ｜交易状态 ｜交易金额 ｜对方账号 ｜交易时间\n");
		List<String> differList = new ArrayList<String>();
		List<Map<String,Object>> differErrorList = new ArrayList<Map<String,Object>>();
		differList.add("---------接口和系统不同的数据---------\n");
		differList.add("交易批次号｜ 交易序号 ｜交易类型 ｜交易状态 ｜交易金额 ｜对方账号 ｜交易时间  && 业务编号 |外部交易流水号| 交易时间｜交易金额 ｜用户ID \n");
		
		Environment environment = Environment.instance();
		
		if(amsList != null && bankDataMap!=null){
			for(int i=0; i<amsList.size(); i++){
				Map<String,Object> amsMap = amsList.get(i);
				String ams_order_no	 	= String.valueOf(amsMap.get("command_no"));
				Date trade_time			= (Date)amsMap.get("trade_time");
				String ams_out_trade_id	= (String)amsMap.get("out_trade_id");
				String ams_trade_time  	= trade_time_df.format(trade_time);
				String ams_trade_money	= String.valueOf(amsMap.get("trade_money"));
				String userId			= String.valueOf(amsMap.get("source_userId"));
				//查询通联交易数据
				Map<String,String> bankMap = bankDataMap.get(ams_order_no);
				if(bankMap == null)
					continue;
				String tl_req_sn 	= bankMap.get("REQ_SN");
				String tl_sn		= bankMap.get("SN");
				String tl_req_type	= bankMap.get("REQ_TYPE");
				String tl_req_code	= bankMap.get("REQ_CODE");
				String tl_amount 	= bankMap.get("AMOUNT");
				String tl_code_no 	= bankMap.get("CODE_NO");
				String tl_req_date 	= bankMap.get("REQ_DATE");
				if(ams_trade_money.equals(tl_amount)){
					//完全匹配
				}else{	//生成差异化数据
					differList.add(tl_req_sn + "|"+ tl_sn + "|" + tl_req_type + "|" + tl_req_code + "|"
							+ tl_amount + "|" + tl_code_no + "|" + tl_req_date + " && " 
							+ ams_order_no + "|" + ams_out_trade_id+ "|"+ ams_trade_money + "|" + ams_trade_time + "|" + userId +"\n");
					Map<String,Object> temp = new HashMap<String, Object>();
					temp.put("bizid", amsMap.get("command_no"));
					temp.put("e_trade_money", amsMap.get("trade_money"));
					temp.put("userId", amsMap.get("target_userId"));
					temp.put("trade_time", amsMap.get("trade_time"));
					temp.put("pay_interfaceNo", amsMap.get("pay_interfaceNo"));
					temp.put("trade_type", amsMap.get("trade_type"));
					temp.put("out_trade_id", bankMap.get("REQ_SN"));
					temp.put("out_trade_money", bankMap.get("AMOUNT"));
					temp.put("out_trade_type", bankMap.get("REQ_TYPE"));
					differErrorList.add(temp);
				}
				
				bankDataMap.remove(ams_out_trade_id);	//删除通联集合数据
				amsList.remove(i);					//删除多帐户集合数据
				i--;								//复位
			}
			
		}

		//多帐户集合中是否存在剩余数据
		if( amsList != null){
			for(int i=0; i<amsList.size(); i++){
				Map<String,Object> amsMap = amsList.get(i);
				String ams_order_no	 	= String.valueOf(amsMap.get("command_no"));
				Date trade_time			= (Date)amsMap.get("trade_time");
				String ams_out_trade_id	= (String)amsMap.get("out_trade_id");
				String ams_trade_time  	= trade_time_df.format(trade_time);
				String ams_trade_money	= String.valueOf(amsMap.get("trade_money"));
				String userId			= String.valueOf(amsMap.get("target_userId"));
				
				amsHaveList.add(ams_order_no + "|" + ams_out_trade_id + "|" + ams_trade_money + "|" + ams_trade_time + "|" + userId +"\n");
				amsErrorList.add(amsMap);
			}
		}

		//通联数据集合中是否剩余数据
		if(bankDataMap != null){
			for(String key : bankDataMap.keySet()){
				Map<String,String> bankMap = bankDataMap.get(key);
				String tl_req_sn 	= bankMap.get("REQ_SN");
				String tl_sn		= bankMap.get("SN");
				String tl_req_type	= bankMap.get("REQ_TYPE");
				String tl_req_code	= bankMap.get("REQ_CODE");
				String tl_amount 	= bankMap.get("AMOUNT");
				String tl_code_no 	= bankMap.get("CODE_NO");
				String tl_req_date 	= bankMap.get("REQ_DATE");
	
				bankHaveList.add(tl_req_sn + "|"+ tl_sn + "|" + tl_req_type + "|" + tl_req_code + "|"
						+ tl_amount + "|" + tl_code_no + "|" + tl_req_date +"\n");
				bankErrorList.add(bankMap);
			}
		}

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
		FileUtil.saveToFile(strContent, filePath + File.separatorChar + "INTERFACE-RESULT-" + codeNo+"-"+ settday + ".txt", "UTF-8");
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
				.append(", check_state=:check_state, download_time=:download_time, check_time=:check_time,orgNo=:orgNo where id=:id");
			Query query = session.createQuery(sb.toString());
			query.setParameter("checkDate", checkDate);
			query.setParameter("checkResult", checkResult);
			query.setParameter("outSystemFile", "ALLINPAY-INTERFACE-" + codeNo+"-"+settday + ".txt:" + checkFilePath + "/" + "ALLINPAY-INTERFACE-" + codeNo+"-"+settday + ".txt");
			query.setParameter("checkAccountFile", codeNo+"-"+settday + ".txt:" + checkFilePath + "/" + codeNo+"-"+settday + ".txt");
			query.setParameter("checkResultFile", "INTERFACE-RESULT-" + codeNo+"-"+settday + ".txt:" + checkFilePath + "/" + "INTERFACE-RESULT-" + codeNo+"-"+settday + ".txt");
			query.setParameter("download_state", 1L);
			query.setParameter("check_state", 1L);
			query.setParameter("download_time", downloadDate);
			query.setParameter("check_time", new Date());
			query.setParameter("orgNo",codeNo);
			query.setParameter("id", checkAccountId);
			
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
						errorTradeLogMap.put("bizid", 				amsError.get("command_no").toString());
						errorTradeLogMap.put("e_trade_money", 		amsError.get("trade_money").toString());
						errorTradeLogMap.put("pay_interfaceNo", 	amsError.get("pay_interfaceNo").toString());
						if(Constant.PAY_INTERFACE_TLT_DF.equals(amsError.get("pay_interfaceNo").toString())) {
							errorTradeLogMap.put("pay_interface_name", 	"通联代付");
						} else if(Constant.PAY_INTERFACE_TLT_DK.equals(amsError.get("pay_interfaceNo").toString())) {
							errorTradeLogMap.put("pay_interface_name", 	"通联代扣");
						}
						errorTradeLogMap.put("pay_channelNo", 		Constant.PAY_CHANNEL_TLT);
						errorTradeLogMap.put("trade_type", 			amsError.get("trade_type").toString());
						Long trade_time = ((Date)amsError.get("trade_time")).getTime();
						errorTradeLogMap.put("trade_time", 			trade_time.toString());
						errorTradeLogMap.put("error_type", 			"1");
						errorTradeLogMap.put("idDealwith", Boolean.valueOf(false).toString());
						logger.info(errorTradeLogMap);
						DynamicEntityService.createEntity("AMS_ErrorTradeLog", errorTradeLogMap, null);
					}
					for( int i = 0; i < bankErrorList.size(); i++ ){
						logger.info("--------------bankErrorList----------------");
						Map<String, String> bankError = bankErrorList.get(i);
						Map<String, String> errorTradeLogMap = new HashMap<String, String>();
						errorTradeLogMap.put("out_trade_id", 		bankError.get("REQ_SN"));
						Long money = Long.valueOf(bankError.get("AMOUNT"));
						errorTradeLogMap.put("out_trade_money", 	money.toString());
						errorTradeLogMap.put("pay_channelNo", 		Constant.PAY_CHANNEL_TLT);
						errorTradeLogMap.put("trade_time", 			String.valueOf(trade_time_tlt.parse(bankError.get("REQ_DATE")).getTime()));
						errorTradeLogMap.put("error_type", 			"2");
						errorTradeLogMap.put("out_trade_type", 		bankError.get("REQ_TYPE"));
						errorTradeLogMap.put("idDealwith", Boolean.valueOf(false).toString());
						if("0".equals(bankError.get("REQ_TYPE"))) {
							errorTradeLogMap.put("pay_interfaceNo", 	Constant.PAY_INTERFACE_TLT_DF);
							errorTradeLogMap.put("pay_interface_name", 	"通联代付");
						} else if("1".equals(bankError.get("REQ_TYPE"))) {
							errorTradeLogMap.put("pay_interfaceNo", 	Constant.PAY_INTERFACE_TLT_DK);
							errorTradeLogMap.put("pay_interface_name", 	"通联代扣");
						}
						DynamicEntityService.createEntity("AMS_ErrorTradeLog", errorTradeLogMap, null);
					}
					for( int i = 0; i < differErrorList.size(); i++ ){
						logger.info("--------------differErrorList----------------");
						Map<String, Object> differError = differErrorList.get(i);
						Map<String, String> errorTradeLogMap = new HashMap<String, String>();
						errorTradeLogMap.put("bizid", 				differError.get("bizid").toString());
						errorTradeLogMap.put("e_trade_money", 		differError.get("e_trade_money").toString());
						errorTradeLogMap.put("pay_interfaceNo", 	differError.get("pay_interfaceNo").toString());
						if(Constant.PAY_INTERFACE_TLT_DF.equals(differError.get("pay_interfaceNo").toString())) {
							errorTradeLogMap.put("pay_interface_name", 	"通联代付");
						} else if(Constant.PAY_INTERFACE_TLT_DK.equals(differError.get("pay_interfaceNo").toString())) {
							errorTradeLogMap.put("pay_interface_name", 	"通联代扣");
						}
						errorTradeLogMap.put("pay_channelNo", 		Constant.PAY_CHANNEL_TLT);
						errorTradeLogMap.put("trade_type", 			differError.get("trade_type").toString());
						Long trade_time = ((Date)differError.get("trade_time")).getTime();
						errorTradeLogMap.put("trade_time", 			trade_time.toString());
						errorTradeLogMap.put("out_trade_id", 		differError.get("out_trade_id")==null? null:differError.get("out_trade_id").toString());
						Long money = Long.valueOf(differError.get("out_trade_money").toString());
						errorTradeLogMap.put("out_trade_money", 	money.toString());
						errorTradeLogMap.put("error_type", 			"3");
						errorTradeLogMap.put("out_trade_type", 		differError.get("out_trade_type").toString());
						errorTradeLogMap.put("idDealwith", Boolean.valueOf(false).toString());
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
			logger.error(e.getMessage(),e);
			throw e;
		}finally {
			EntityManagerUtil.closeSession(ctx);
			logger.info("compare end");
		}	
	}
	
	
}