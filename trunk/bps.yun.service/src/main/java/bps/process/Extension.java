package bps.process;

import bps.external.application.service.trade.*;
import bps.external.tradecommand.*;
import ime.core.Environment;
import ime.core.event.EventManager;
import ime.core.ext.IExtension;
import ime.security.cert.GenX509Cert;
import ime.security.cert.UserInfo;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import com.kinorsoft.ams.TradeCommandManager;

import bps.common.JedisUtils;
import bps.eventhandler.OrderComplatePayEventHandler;
import bps.external.application.service.checkID.TltIDCheckService;
//import bps.external.application.service.company.CompanyPicService;
import bps.external.application.service.increment.OtherService;
import bps.order.OrderServiceImpl;

public class Extension implements IExtension {
	public static Map<String, String> paramMap;
	public Logger logger = Logger.getLogger(Extension.class);
	public static ItsService itsService;
	public static UserManageService manageService;
	public static TltDaiFuService tltDaiFuService;
	public static OtherService otherService;
	public static GatewayService gatewayService;
	public static TltIDCheckService tltIDCheckService;
//	public static CompanyPicService companyPicService;
	public static DaikouService daikouService;
	public static TltAccountVerificationService tltAccountVerificationService;

	public Map<String, String> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public void handleLoad() throws Exception {
		// TODO Auto-generated method stub
		logger.info("dubbo启动开始");
		//启动dubbo 
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-provider.xml"});    
		context.start();
		logger.info("dubbo启动结束");


//		PayChannelManage.loadPayInterfaceInfo();
		
		//监听订单支付完成事件
		EventManager.instance().addEventHandler(com.kinorsoft.ams.Constant.EVENT_TYPE_ORDERCOMPLETEPAY, new OrderComplatePayEventHandler());
//        //监听积分事件
//        EventManager.instance().addEventHandler(Constant.EVENT_TYPE_BONUS, new BonusEvent());

		//注册通联通单笔代付通道
		TradeCommandManager.instance().registerPayInterface(new TltDaiFu());
		//注册网关支付通道
		TradeCommandManager.instance().registerPayInterface(new Gateway());
		//注册移动认证支付通道
		TradeCommandManager.instance().registerPayInterface(new CertPay());
		//注册ITS支付通道
		TradeCommandManager.instance().registerPayInterface(new ItsTradeCommand());
		//注册代扣支付通道
		TradeCommandManager.instance().registerPayInterface(new DaikouCommand());
		//注册POS支付通道
		TradeCommandManager.instance().registerPayInterface(new PosCommand());

		//黑名单加载
        RiskUser.loadRiskUserInfo();
        //支付渠道加载
        PayChannelManage.loadPayInterfaceInfo();
        //支付通道应用配置文件加载
        PayInterfaceAppConfigCache.loadToCache();
        //加载SOA
        AppSoaApiManage.loadSoaApiInfo();


		//加载批量代付查询消费者
		BatchDaiFuQueryRabbitCousumer bdfqc = new BatchDaiFuQueryRabbitCousumer();
		bdfqc.start();

		//加载代扣查询消费者
		DaikouRabbitConsumer daikouRabbitConsumer = new DaikouRabbitConsumer();
		daikouRabbitConsumer.start();

        //生成根证书
        Environment environment = Environment.instance();
        String path = environment.getSystemProperty("ca.root.path");
        File file = new File(path);
        logger.info("path:"+path);
        if(!file.exists()) {
            logger.info("生成根证书");
            String alias = environment.getSystemProperty("ca.root.alias");
            String password = environment.getSystemProperty("ca.root.password");
            String country = environment.getSystemProperty("ca.root.country");
            String locality = environment.getSystemProperty("ca.root.locality");
            String name = environment.getSystemProperty("ca.root.name");
            String organization = environment.getSystemProperty("ca.root.organizationUnit");
            String organizationUnit = environment.getSystemProperty("ca.root.organizationUnit");
            String state = environment.getSystemProperty("ca.root.state");
            
            UserInfo user = new UserInfo();
            user.country = country;
            user.locality = locality;
            user.name = name;
            user.organization = organization;
            user.organizationUnit = organizationUnit;
            user.state = state;
            long expYear = Long.valueOf(environment.getSystemProperty("ca.root.expYear"));
            GenX509Cert g = new GenX509Cert();
            g.createRootCA(alias, password, user, path, expYear);
        }


		String ip 	= environment.getSystemProperty("cache.ip");
		int port	= Integer.valueOf(environment.getSystemProperty("cache.port"));

		//设置redis联接参数
		JedisUtils.setParam(ip,port);

		//加载机构数据
        loadSOA();
        
        ClassPathXmlApplicationContext CTX = new ClassPathXmlApplicationContext(new String[] { "dubbo-comsumer.xml" });
		itsService = (ItsService)CTX.getBean("its");
		manageService = (UserManageService)CTX.getBean("userManage");
		tltDaiFuService = (TltDaiFuService)CTX.getBean("tltDaiFu");
		otherService = (OtherService)CTX.getBean("other");
		gatewayService = (GatewayService)CTX.getBean("gateway");
		tltIDCheckService = (TltIDCheckService)CTX.getBean("tltIDCheck");
//		companyPicService = (CompanyPicService)CTX.getBean("companyPic");
		daikouService = (DaikouService)CTX.getBean("daikou");

		tltAccountVerificationService = (TltAccountVerificationService)CTX.getBean("tltAccountVerificationService");



		logger.info("初始化完毕");
		
//		CompanyPicDownloadTask companyPicDownloadTask = new CompanyPicDownloadTask();
//		companyPicDownloadTask.handle();
	}

	public void handleUnload() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void setProperties(Map<String, String> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void loadSOA(){
		logger.info("加载机构数据");

        try {
            OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
            List list = orderServiceImpl.getOrgList();
            JSONArray array = new JSONArray(list);
            logger.info("orgList:"+array);
			JedisUtils.setCache("orgList", array.toString());
        } catch(Exception e) {
        	logger.error(e.getMessage(), e);
        }
        
    }
	
}