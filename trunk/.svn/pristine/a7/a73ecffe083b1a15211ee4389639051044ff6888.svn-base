package bps.monitor;

import bps.common.Constant;
import ime.core.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import bps.common.RabbitProducerManager;

/**
 * 交易监控
 * @author Administrator
 *
 */
public class TransMonitor {
	private static Logger logger = Logger.getLogger(TransMonitor.class);
	private static String messageType = null;
	private static String messageDirectionReq = null;
	private static String messageDirectionRes = null;
	private static String businessId = null;
	
	static{
		try{
			//报文类型标示
			messageType = Environment.instance().getSystemProperty("monitor.trans.message.type");
			//报文方向-请求
			messageDirectionReq = Environment.instance().getSystemProperty("monitor.trans.message.direction.request");
			//报文方向-应答
			messageDirectionRes = Environment.instance().getSystemProperty("monitor.trans.message.direction.response");
			//业务系统id
			businessId = Environment.instance().getSystemProperty("monitor.trans.business.id");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	public static void monitor(String direction, Map<String, Object> commandParam, Map<String, Object> extendParam) {
		try{


			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateStr = sdf.format(new Date());
			//报文方向
			String messageDirection = "request".equals(direction) ? messageDirectionReq : messageDirectionRes;
			//交易类型id

//			Map<String, ime.core.entity.Dictionary> trade_type = Environment.instance().getDictionaryMap("多账户.交易类型",0L);
//			Map<String, ime.core.entity.Dictionary> sub_trade_type = Environment.instance().getDictionaryMap("多账户.交易子类型",0L);

//			String _trade_type = trade_type.get(commandParam.get("trade_type").toString()).getLabel();
//			String _sub_trade_type = commandParam.get("sub_trade_type") != null ? sub_trade_type.get(commandParam.get("sub_trade_type").toString()).getLabel():null;

			String transType = commandParam.get("sub_trade_type") != null ? (commandParam.get("trade_type") + "_"  + commandParam.get("sub_trade_type")) : commandParam.get("trade_type").toString();

			//机构id
			String outOrgId =  (String)commandParam.get("orgNo");
			//商户id
			String applicationNo = "";//(String)commandParam.get("orgNo");
			//分公司id
			String filialeId = "";
			//渠道id(实际是通道)
			String payInterfaceNo = (String)commandParam.get("pay_interfaceNo");
			//交易应答码
			Long pay_state = commandParam.get("pay_state") == null?-1L:(Long)commandParam.get("pay_state");
			String transResponseCode = "00";
			//交易应答码信息
			String transResponseInfo = "交易成功";
			if (pay_state.equals(2L)){
				transResponseCode = "02";
				transResponseInfo = "交易失败";
			}else if (pay_state.equals(1L)){
				transResponseCode = "01";
				transResponseInfo = "未支付";
			}else if(pay_state.equals(9L)){
				transResponseCode = "99";
				transResponseInfo = "交易中间状态";
			}
//			String transResponseCode = commandParam.get("out_ret_code1") != null ? (String)commandParam.get("out_ret_code1") : "00";
			//业务系统名称
			String orgName = "通联云帐户";//(String)commandParam.get("orgNo");
			//交易类型名称

			Map<String, ime.core.entity.Dictionary> trade_type = Environment.instance().getDictionaryMap("多账户.交易类型",0L);
			Map<String, ime.core.entity.Dictionary> sub_trade_type = Environment.instance().getDictionaryMap("多账户.交易子类型",0L);

			String _trade_type = trade_type.get(commandParam.get("trade_type").toString()).getLabel();
			String _sub_trade_type = commandParam.get("sub_trade_type") != null ? sub_trade_type.get(commandParam.get("sub_trade_type").toString()).getLabel():null;

			String transTypeName = _sub_trade_type != null ? (_trade_type + "_"  + _sub_trade_type) : _trade_type;

			//机构名称
			String outOrgName = "";
			//商户名称
			String applicationName = "";
			//商户类型
			String applicationType = "";
			//分公司名称
			String filialeName = "";
			//渠道名称(上传的是通道名称)
//			/** 通联通单笔代付通道 */
//			public static final String PAY_INTERFACE_TLT_DF		= "2000101";
//			/** 通联通单笔代扣通道 */
//			public static final String PAY_INTERFACE_TLT_DK		= "2000102";
//			/** 通联通批量代付通道 */
//			public static final String PAY_INTERFACE_TLT_BACH_DF	= "2000103";
//			/** 通联网关支付通道 */
//			public static final String PAY_INTERFACE_GETWAY_JJ	= "1000201";
//			/** 通联快捷支付通道 */
//			public static final String PAY_INTERFACE_QUICK		= "2000201";
//			/** 通联快捷支付绑卡通道 */
//			public static final String PAY_INTERFACE_QUICK_CARD	= "2000202";
//
//			/** 认证支付通道 */
//			public static final String PAY_INTERFACE_CERT_PAY   = "2000301";
//
//			/** POS支付通道 */
//			public static final String PAY_INTERFACE_POS   = "3000101";
//
//			/** 银联代扣 */
//			public static final String PAY_INTERFACE_UNION_DAIKOU = "4000101";

//			/** 话费充值通道 */
//			public static final String PAY_INTERFACE_PHONE      = "1000401";
//			/** 通联内部账户支付通道 */
//			public static final String PAY_INTERFACE_AMS        = "9999901";
			String _payInternalName = (String)commandParam.get("pay_interfaceNo");
			String payInternalName = "";
			switch (_payInternalName){
				case Constant.PAY_INTERFACE_TLT_DF:
					payInternalName ="通联通单笔代付通道";
					break;
				case Constant.PAY_INTERFACE_TLT_DK:
					payInternalName ="通联通单笔代扣通道";
					break;
				case Constant.PAY_INTERFACE_TLT_BACH_DF:
					payInternalName ="通联通批量代付通道";
					break;
				case Constant.PAY_INTERFACE_GETWAY_JJ:
					payInternalName ="通联网关支付通道";
					break;
				case Constant.PAY_INTERFACE_QUICK:
					payInternalName ="通联快捷支付通道";
					break;
				case Constant.PAY_INTERFACE_QUICK_CARD:
					payInternalName ="通联快捷支付绑卡通道";
					break;
				case Constant.PAY_INTERFACE_CERT_PAY:
					payInternalName ="认证支付通道";
					break;
				case Constant.PAY_INTERFACE_POS:
					payInternalName ="POS支付通道";
					break;
				case Constant.PAY_INTERFACE_UNION_DAIKOU:
					payInternalName ="银联代扣";
					break;
				case Constant.PAY_INTERFACE_AMS:
					payInternalName ="云帐户内部账户支付通道";
					break;
			}
			//客户号
			String clientNo = "";//(String)commandParam.get("orgNo");
			//系统流水号
			String commandNo = (String)commandParam.get("command_no");
			//交易日期
			String transDate = dateStr.substring(0, 8);
			//交易时间
			String transTime = dateStr.substring(8,14);
			//交易金额
			String amount = String.valueOf((Long)commandParam.get("trade_money"));
			//自定义金额1
			String amount1 = "";
			//自定义金额2
			String amount2 = "";

			//终端号
			String terminalNo = "";
			//终端类型
			String terminalType = "";
			
			//组装
			StringBuilder sb = new StringBuilder();
			sb.append(messageType).append("|");
			sb.append(messageDirection).append("|");
			sb.append(businessId).append("|");
			sb.append(transType).append("|");
			sb.append(outOrgId).append("|");
			sb.append(applicationNo).append("|");
			sb.append(filialeId).append("|");
			sb.append(payInterfaceNo).append("|");
			sb.append(transResponseCode).append("|");
			sb.append(orgName).append("|");
			sb.append(transTypeName).append("|");
			sb.append(outOrgName).append("|");
			sb.append(applicationName).append("|");
			sb.append(applicationType).append("|");
			sb.append(filialeName).append("|");
			sb.append(payInternalName).append("|");
			sb.append(clientNo).append("|");
			sb.append(commandNo).append("|");
			sb.append(transDate).append("|");
			sb.append(transTime).append("|");
			sb.append(amount).append("|");
			sb.append(amount1).append("|");
			sb.append(amount2).append("|");
			sb.append(transResponseInfo).append("|");
			sb.append(terminalNo).append("|");
			sb.append(terminalType);
		
			//发送到队列
			String tem = sb.toString();

//			String lengthStr = String.valueOf(sb.toString().length()+6+outOrgName.length()+transTypeName.length());
			String lengthStr = String.valueOf(tem.getBytes("utf-8").length);
			int lengthStrLength = lengthStr.length();
			for(int i = lengthStrLength; i < 6; i++){
				lengthStr = "0" + lengthStr;
			}
		
			String content = lengthStr + sb.toString();
			RabbitProducerManager rabbitProducerManager = RabbitProducerManager.getInstance();
			logger.info("transactionMonitorQueueSend参数：msg=" + content);
			rabbitProducerManager.transactionMonitorQueueSend(content);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
}
