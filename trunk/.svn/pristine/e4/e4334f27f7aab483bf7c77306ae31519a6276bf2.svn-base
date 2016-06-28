package bps.external.application.trade.its;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;

import bps.common.BizException;
import bps.external.application.Extension;
import bps.external.application.service.trade.UserManageService;

public class UserManageServiceImpl implements UserManageService{
	private static Logger logger = Logger.getLogger("its");
	
	private static IUserAgreement appCenterService = null;
	private static UserManageServiceImpl userManage = null;
	private static String serverUrl = "";
	private static String user_id = "";
	
	static
	{
		logger.info("会员信息初始化");
		serverUrl 	= Extension.paramMap.get("member.serverUrl");
		user_id		= Extension.paramMap.get("member.user_id");
	}
	
	public static UserManageServiceImpl instance() throws Exception{
		if(userManage == null){
			userManage = new UserManageServiceImpl();
//			String url = "http://116.236.252.101:30072/contract-app/index.do?id=com.allinpay.contract.service.IUserAgreement";
			HessianProxyFactory factory = new HessianProxyFactory();
			appCenterService = (IUserAgreement) factory.create(IUserAgreement.class, serverUrl);
		}
		return userManage;
	}

	public UserManageServiceImpl() throws Exception {
//		String url = "http://116.236.252.101:30072/contract-app/index.do?id=com.allinpay.contract.service.IUserAgreement";
		HessianProxyFactory factory = new HessianProxyFactory();
		appCenterService = (IUserAgreement) factory.create(IUserAgreement.class, serverUrl);
	}
		
	/**
	 * 保存协议
	 * @param param
	 */
	public Map saveContract(Map<String, String> param) throws Exception{
		logger.info("UserManage.saveContract start ");
		logger.info("param:" + param);
		
		String ORG_ID 	= param.get("ORG_ID");
		String ORG_NAME = param.get("ORG_NAME");
		String CARD_NO	= param.get("CARD_NO");
		String ORG_CODE	= param.get("ORG_CODE");
		String ACCOUNT_TYPE	= param.get("ACCOUNT_TYPE"); 
		String ORG_DESC		= param.get("ORG_DESC");
		
		String USER_NAME	= param.get("USER_NAME");
		String MOBILE_NO	= param.get("MOBILE_NO"); 
		String ID_NO		= param.get("ID_NO");
		String ITS_CONN_ID	= param.get("ITS_CONN_ID");
		String CONTRACT_NO	= param.get("CONTRACT_NO");
		
		if(ORG_ID == null)
			throw new Exception("请传入参数 ORG_ID");
		if(ORG_NAME == null)
			throw new Exception("请传入参数 ORG_NAME");
		if(CARD_NO == null)
			throw new Exception("请传入参数 CARD_NO");
		if(ORG_CODE == null)
			throw new Exception("请传入参数 ORG_CODE");
		if(ACCOUNT_TYPE == null)
			throw new Exception("请传入参数 ACCOUNT_TYPE");
		if(ORG_DESC == null)
			throw new Exception("请传入参数 ORG_DESC");
		if(USER_NAME == null)
			throw new Exception("请传入参数 ACCOUNT_TYPE");
		if(MOBILE_NO == null)
			throw new Exception("请传入参数 ORG_DESC");
		if(ID_NO == null)
			throw new Exception("请传入参数 ID_NO");
		if(ITS_CONN_ID == null)
			throw new Exception("请传入参数 ITS_CONN_ID");
		
		Map<String, String> orderMap = new HashMap<String, String>();
		orderMap.put("YWMA", 		"2012"); 	// 业务码
		orderMap.put("JYMA", 		"0002"); 	//交易码
		orderMap.put("CZMA", 		"01"); 		//操作码
		orderMap.put("BUSINESS_TYPE","85");		//业务类型
		orderMap.put("USER_ID",		user_id);
		orderMap.put("CARD_NO", 	CARD_NO);
		orderMap.put("ORG_DESC", 	ORG_DESC.toLowerCase());
		orderMap.put("AGREEMENT_TYPE", "02");
		orderMap.put("AUTH_PAY_TYPE", 	"02");
		orderMap.put("ORG_ID", 			ORG_ID);
		orderMap.put("ORG_NAME",		ORG_NAME);
		orderMap.put("ORG_CODE", 		ORG_CODE);
		orderMap.put("ACCOUNT_TYPE", 	ACCOUNT_TYPE);
		orderMap.put("ITS_CONN_ID", 	ITS_CONN_ID);
		
		orderMap.put("USER_NAME", 	USER_NAME);
		orderMap.put("MOBILE_NO", 	MOBILE_NO);
		orderMap.put("ID_NO", 		ID_NO);
		orderMap.put("ID_TYPE", 	"01");
		if(CONTRACT_NO != null)
			orderMap.put("CONTRACT_NO", CONTRACT_NO);
		
		logger.info("发送信息:" + orderMap);
		try {
			Map result = appCenterService.service(orderMap);
			logger.info("result:" + result);
			return result;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * 重复签约
	 * @param param
	 */
	public Map repeatContract(Map<String, String> param)throws Exception{
		logger.info("UserManage.repeatContract start ");
		logger.info("param:" + param);
		
		String ORG_ID 	= param.get("ORG_ID");
		String ORG_NAME = param.get("ORG_NAME");
		String CARD_NO	= param.get("CARD_NO");
		String ORG_CODE	= param.get("ORG_CODE");
		String ACCOUNT_TYPE	= param.get("ACCOUNT_TYPE");
		String USER_NAME	= param.get("USER_NAME");
		String MOBILE_NO	= param.get("MOBILE_NO");
		String ORG_DESC		= param.get("ORG_DESC");
		String ID_NO		= param.get("ID_NO");
		String ITS_CONN_ID	= param.get("ITS_CONN_ID");
		String CONTRACT_NO	= param.get("CONTRACT_NO");

		
		if(ORG_ID == null)
			throw new Exception("请传入参数 ORG_ID");
		if(ORG_NAME == null)
			throw new Exception("请传入参数 ORG_NAME");
		if(CARD_NO == null)
			throw new Exception("请传入参数 CARD_NO");
		if(ORG_CODE == null)
			throw new Exception("请传入参数 ORG_CODE");
		if(ACCOUNT_TYPE == null)
			throw new Exception("请传入参数 ACCOUNT_TYPE");
		if(USER_NAME == null)
			throw new Exception("请传入参数 USER_NAME");
		if(MOBILE_NO == null)
			throw new Exception("请传入参数 MOBILE_NO");
		if(ORG_DESC == null)
			throw new Exception("请传入参数 ORG_DESC");
		if(ID_NO == null)
			throw new Exception("请传入参数 ID_NO");
		if(ITS_CONN_ID == null)
			throw new Exception("请传入参数 ITS_CONN_ID");
		
		Map<String, String> orderMap = new HashMap<String, String>();
		orderMap.put("YWMA", 	"2012"); 		//业务码
		orderMap.put("JYMA", 	"0002"); 		//交易码
		orderMap.put("CZMA", 	"10"); 			//操作码

		orderMap.put("BUSINESS_TYPE","85");		//业务类型

		orderMap.put("USER_ID", 	user_id);
		orderMap.put("CARD_NO", 	CARD_NO);
		
		orderMap.put("ORG_DESC", 		ORG_DESC.toLowerCase());
		orderMap.put("AGREEMENT_TYPE", 	"02");
		orderMap.put("AUTH_PAY_TYPE", 	"02");
		orderMap.put("ORG_ID", 			ORG_ID);
		orderMap.put("ORG_NAME", 		ORG_NAME);
		orderMap.put("ORG_CODE", 		ORG_CODE);
		orderMap.put("USER_NAME", 		USER_NAME);
		orderMap.put("MOBILE_NO", 		MOBILE_NO);
		orderMap.put("ACCOUNT_TYPE", 	ACCOUNT_TYPE);
		orderMap.put("ID_NO", 		ID_NO);
		orderMap.put("ID_TYPE", 	"01");
		orderMap.put("ITS_CONN_ID", 	ITS_CONN_ID);

		if(CONTRACT_NO != null)
			orderMap.put("CONTRACT_NO", CONTRACT_NO);
		
		logger.info("发送信息:" + orderMap);
		try {
			Map result = appCenterService.service(orderMap);
			logger.info("result:" + result);
			return result;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}
    
    /**
     * 解除签约
     * @param param
     */
    public void removeContract(Map<String, String> param)throws Exception{
        logger.info("UserManage.removeContract start ");
        logger.info("param:" + param);
        
        String CARD_NO  = param.get("CARD_NO");
        
        if(CARD_NO == null)
            throw new Exception("请传入参数 CARD_NO");
        
        Map<String, String> orderMap = new HashMap<String, String>();
        orderMap.put("YWMA",    "2012"); // 业务码
        orderMap.put("JYMA",    "0001"); // 交易码
        orderMap.put("CZMA",    "02"); // 操作码
        orderMap.put("USER_ID",     user_id);
        orderMap.put("AUTH_PAY_TYPE",   "02");
        orderMap.put("CARD_NO",     CARD_NO);
        try {
	        logger.info("发送信息:" + orderMap);
	        Map result = appCenterService.service(orderMap);
	        logger.info("result:" + result);
	        String JYJG  = result.get("JYJG").toString();
	        if(JYJG.equals("0000")) {
	            List list = (ArrayList)result.get("CXJG");
	            if(list.isEmpty()) {
	                throw new Exception("该银行卡未绑定，无法解绑");
	            }
	            logger.info("list:" + list);
	            Map temp = (HashMap)list.get(0);
	            logger.info("temp:" + temp);
	            logger.info("AGREEMENT_ID:" + temp.get("AGREEMENT_ID"));
	            
	            String AGREEMENT_ID  = temp.get("AGREEMENT_ID").toString();
	            
	            orderMap = new HashMap<String, String>();
	            orderMap.put("YWMA",    "2012"); // 业务码
	            orderMap.put("JYMA",    "0002"); // 交易码
	            orderMap.put("CZMA",    "09"); // 操作码
	            orderMap.put("USER_ID",     user_id);
	            orderMap.put("AGREEMENT_TYPE",  "02");
	            orderMap.put("AUTH_PAY_TYPE",   "02");
	            orderMap.put("AGREEMENT_ID",   AGREEMENT_ID);
	            
	            logger.info("发送信息:" + orderMap);
	            result = appCenterService.service(orderMap);
	            logger.info("result:" + result);
	            JYJG  = result.get("JYJG").toString();
	            if(!JYJG.equals("0000")) {
	                throw new Exception(result.get("JYMS").toString());
	            }
	        } else {
	            throw new Exception(result.get("JYMS").toString());
	        }
        } catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
    }
    
    /**
     * 查询签约
     * @param param
     */
    public Map selectContract(Map<String, String> param)throws Exception{
        logger.info("UserManage.removeContract start ");
        logger.info("param:" + param);
        
        String CARD_NO  = param.get("CARD_NO");
        
        if(CARD_NO == null)
            throw new Exception("请传入参数 CARD_NO");
        
        Map<String, String> orderMap = new HashMap<String, String>();
        orderMap.put("YWMA",    "2012"); // 业务码
        orderMap.put("JYMA",    "0001"); // 交易码
        orderMap.put("CZMA",    "02"); // 操作码
        orderMap.put("USER_ID",     user_id);
        orderMap.put("AUTH_PAY_TYPE",   "02");
        orderMap.put("CARD_NO",     CARD_NO);
        try {
	        logger.info("发送信息:" + orderMap);
	        Map result = appCenterService.service(orderMap);
	        logger.info("result:" + result);
	        String JYJG  = result.get("JYJG").toString();
	        if(JYJG.equals("0000")) {
	            List list = (ArrayList)result.get("CXJG");
	            if(list.isEmpty()) {
	                throw new Exception("该银行卡未绑定，无法解绑");
	            }
	            logger.info("list:" + list);
	            Map temp = (HashMap)list.get(0);
	            logger.info("temp:" + temp);
	            logger.info("AGREEMENT_ID:" + temp.get("AGREEMENT_ID"));
	            
	            //String CONTRACT_NO  = temp.get("CONTRACT_NO").toString();
	            return temp;
	        } else {
	            throw new Exception(result.get("JYMS").toString());
	        }
        } catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} 
    }
}