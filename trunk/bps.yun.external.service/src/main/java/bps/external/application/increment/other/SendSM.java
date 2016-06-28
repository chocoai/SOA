package bps.external.application.increment.other;

import java.io.*;
import java.net.*;
import java.util.Date;

import org.apache.log4j.Logger;

import bps.common.MD5Util;
import bps.external.application.Extension;


public class SendSM extends Thread {
	private static Logger logger = Logger.getLogger(SendSM.class.getName());
	
	private String _mobiles;
	private String _content;
	
	private static String appId;      					//企业ID
	private static String target_url;					//登录帐号
	private static String msgSign;						//密码，MD5(CorpID+帐号密码+TimeStamp)
	
	/*
	private Long _seqid;
	
	private static long corpID;      					//企业ID
	private static String loginName;					//登录帐号
	private static String password;						//密码，MD5(CorpID+帐号密码+TimeStamp)
	private static String timeStamp = "0514170214";		//时间戳，MMDDHHMMSS（月日时分秒),如0514094912
	private static String addNum ="";						//扩展子号，附加于端口号后。
	private static String timer = "2012-04-1 17:10:00"; //定时时间：yyyy-MM-dd HH:mm:ss 如:2010-05-14 10:30:00
	private static long longSms = 1;					//是否以长短信方式发送,0-否；1-是
	private static MobileListGroup[] mobileList;		//接收号码列表，由MobileListGroup组成，为防止超时，每次提交短信，接收号码数量建议不要超过50个。
	private static StringHolder errMsg;					//错误信息，用于返回函数调用结果的文字描述
	private static ArrayOfSmsIDListHolder smsIDList;	//短信ID列表，用于返回发送成功的短信记录ID，此短信ID可用于状态报告匹配的识别。
	private static LongHolder count ;					//调用函数的返回值：发送短信，返回短信ID(SmsID)
	
	
	private static String cdkey;						//账号
	private static String password;						//密码
	private static String target_url ;							//接口地址
	private static String enterprise ;					//
	*/
	
	public SendSM(String mobiles, String content){
		_mobiles = mobiles;
		_content = content;
		//_seqid = new Date().getTime();
	}
	
	public void run() {
		


		appId = Extension.paramMap.get("allinpay.sMessage.appId");
		target_url = Extension.paramMap.get("allinpay.sMessage.target_url");
		msgSign = Extension.paramMap.get("allinpay.sMessage.msgSign");
		
		/*
		corpID = Long.valueOf(environment.getSystemProperty("sms.mail.company.id"));
		String server_address = environment.getSystemProperty("sms.mail.server.address");
		loginName = environment.getSystemProperty("sms.mail.login.account");
		password = environment.getSystemProperty("sms.mail.login.password");
		cdkey = environment.getSystemProperty("short.message.softwareSerialNo");
		password = environment.getSystemProperty("short.message.key");
		target_url = environment.getSystemProperty("short.message.address");
		enterprise = environment.getSystemProperty("short.message.enterprise");
		*/
		//SendMsg(_mobiles, _content, _seqid);
		SendMsg(_mobiles, _content);
	}
	/*
	public void SendMsg(String mobiles,String content,Long seqid){
		URL url = null;
		StringBuffer sb = new StringBuffer();
		HttpURLConnection httpurlconnection = null;
		try {
			url = new URL(target_url+"?cdkey="+cdkey+"&password="+password+"&phone="+mobiles+"&message=【"+enterprise+"】"+content+"&seqid="+seqid.toString());
			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setUseCaches(false);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
			httpurlconnection.connect(); 

			String line = br.readLine(); 
			sb.setLength(0);
			while(line != null){
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			logger.info("短信发送成功");
		} catch(Exception e1) {
			logger.info("发动短信失败" + mobiles + " 主题:" + content);
			logger.error(e1.getMessage(), e1);
		}finally{
			if(httpurlconnection != null){
				httpurlconnection.disconnect();
			}
		}
	}
	*/

	public void SendMsg(String mobiles,String content){
		logger.info("------------SendMsg-----------");
		logger.info("mobiles:"+mobiles);
		logger.info("content:"+content);
		URL url = null;
		StringBuffer sb = new StringBuffer();
		HttpURLConnection httpurlconnection = null;
		Long msgSeq = new Date().getTime();
		String Strmd5 = appId+ ","+ mobiles+ ","+ content;
		
		try {
			Strmd5 = MD5Util.MD5(Strmd5);
			
			url = new URL(target_url+"?appId="+appId+"&mobile="+mobiles+"&content="+content+"&sendTime=&msgSign="+msgSign+"&msgSeq="+msgSeq+"&Strmd5="+Strmd5);
			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setUseCaches(false);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
			httpurlconnection.connect(); 
	
			String line = br.readLine(); 
			sb.setLength(0);
			while(line != null){
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			if(sb.toString().equals("00")) {
				logger.info("短信发送成功");
			} else {
				throw new Exception("短信发送失败"+sb.toString());
			}
			
		} catch(Exception e1) {
			logger.info("发动短信失败" + mobiles + " 主题:" + content);
			logger.error(e1.getMessage(), e1);
		}finally{
			if(httpurlconnection != null){
				httpurlconnection.disconnect();
			}
		}
	}
	
	/*
	public msmResultBean SendMsg(String mobiles,String content){
		//获得实例化对象
		MobsetApiSoap mobset = DataObjectFactory.getMobsetApi();
		Date aDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss"); 

		//初始化参数
		timeStamp = formatter.format(aDate); 
		errMsg = new StringHolder();
		smsIDList = new ArrayOfSmsIDListHolder();
		count = new LongHolder();
		msmResultBean msmBean = new msmResultBean();
		
		//获取帐号信息
		//DataObjectBean bean = DataObjectFactory.getInstance();
		//corpID = new Long(bean.getCordId());
		//loginName = bean.getUserName();
		//password = bean.getPasswd();
		
		//将手机号码字符串分解到数组中
		String [] mobileArray= StringUtils.replace(mobiles, "；", ";").split(";");
		mobileList = new MobileListGroup[mobileArray.length];
		
		for (int i = 0;i<mobileList.length;i++) {
			mobileList[i] = new MobileListGroup();
			mobileList[i].setMobile(mobileArray[i]);
		}
		
		//MD5密码加密
		MD5 md5 = new MD5();
		password = md5.getMD5ofStr(corpID+password+timeStamp);
		
		try {
			//调用发送方法进行短信下发
			mobset.sms_Send(corpID, loginName, password, timeStamp, addNum, timer, longSms, mobileList, content, count, errMsg, smsIDList);
			msmBean.setErrMsg(errMsg);
			msmBean.setMobileList(mobileList);
			msmBean.setSmsIDList(smsIDList);
			logger.info("短信发送成功");
		} catch (RemoteException e) {
			logger.info("发动短信失败" + mobiles + " 主题:" + content);
			logger.error(e);
		}
		return msmBean;
	}
	*/
}