package bps.external.soa;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ime.service.client.SOAClient;
import ime.service.util.RSAUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestSoaOrderService {
	private static SOAClient client;
	private static PublicKey publicKey;
	private static PrivateKey privateKey;

	private String soaName = "OrderService";
	private String bizUserId = "zxccyzqx";
	private String sellerBizUserId = "test1";
	private String ordErexpireDatetime = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String bankCardNo = "";
	private String accountSetNo = "200001";
	//借记卡卡号
//	private String jjBankCardNo = "6214835743207027";
	private String jjBankCardNo = "6228480318051081871";
	//行业代码s
	private String industryCode = "1010";
	//行业名称
	private String industryName = "保险代理";
	//访问终端类型
	private Long source = 1L;

	@BeforeClass
	public static void beforeClass() throws Exception{
//		String serverAddress = "http://10.55.3.236:6003/service/soa";
		String serverAddress = "http://122.227.225.142:23661/service/soa";
//		String serverAddress = "https://yun.allinpay.com/service/soa";

		//商户号
		String sysid = "100000000002";
		//密钥密码
		String pwd = "697057";
		//证书名称
		String alias = "100000000002";
		//证书文件路径，用户指定
		String path = "E:\\pfx\\100000000002.pfx";

//		String sysid = "100000000006";
//		//密钥密码
//		String pwd = "753960";
//		//证书名称
//		String alias = "100000000006";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000006.pfx";

//		String sysid = "100000000003";
//		//密钥密码
//		String pwd = "782326";
//		//证书名称
//		String alias = "100000000003";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000003.pfx";

//		String sysid = "100000000010";
//		//密钥密码
//		String pwd = "886047";
//		//证书名称
//		String alias = "100000000010";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000010.pfx";

//		String sysid = "100000000054";
//		//密钥密码
//		String pwd = "557789";
//		//证书名称
//		String alias = "100000000054";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000054.pfx";

//		String sysid = "100000000007";
//		//密钥密码
//		String pwd = "503222";
//		//证书名称
//		String alias = "100000000007";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000007.pfx";

//		String sysid = "100009000034";
//		//密钥密码
//		String pwd = "124973";
//		//证书名称
//		String alias = "100009000034";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100009000034.pfx";

		//正式
//		String sysid = "100000000001";
//		//密钥密码
//		String pwd = "502175";
//		//证书名称
//		String alias = "100000000001";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\form\\100000000001.pfx";

//		String sysid = "100000000004";
//		//密钥密码
//		String pwd = "345599";
//		//证书名称
//		String alias = "100000000004";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\form\\100000000004.pfx";

//		String sysid = "100000000005";
//		//密钥密码
//		String pwd = "004056";
//		//证书名称
//		String alias = "100000000005";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\form\\100000000005.pfx";

//		String sysid = "100000000006";
//		//密钥密码
//		String pwd = "210090";
//		//证书名称
//		String alias = "100000000006";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\form\\100000000006.pfx";

		//中融宝
//		String sysid = "100000000007";
//		//密钥密码
//		String pwd = "856313";
//		//证书名称
//		String alias = "100000000007";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\form\\100000000007.pfx";

		String signMethod = "SHA1WithRSA";

		try{
			privateKey = RSAUtil.loadPrivateKey(alias, path, pwd);
			publicKey = RSAUtil.loadPublicKey(alias, path, pwd);

			client = new SOAClient();
			client.setServerAddress(serverAddress);
			client.setSignKey(privateKey);
			client.setPublicKey(publicKey);
			client.setSysId(sysid);
			client.setSignMethod(signMethod);

			System.out.println("beforeClass success.");
		}catch(Exception e){
			System.out.println("beforeClass error.");
			e.printStackTrace();
		}
	}

	@Before
	public void before(){
		//订单过期时间
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 15);
		Date date = calendar.getTime();
		ordErexpireDatetime = sdf.format(date);
	}

	//充值申请
	@Test
	public void testApplyDeposit() {
		try{
			System.out.println("testApplyDeposit start");

			//支付方式
			//快捷
			JSONObject quickPay = new JSONObject();
			quickPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			quickPay.put("amount", 10);

			//网关
			String frontUrl = "http://122.227.225.142:23661/gateway/getPayFront.jsp";
			JSONObject gatewayPay = new JSONObject();
//			gatewayPay.put("bankCode", "vbank");  //虚拟银行，专门用于测试环境
			gatewayPay.put("bankCode", "cmb");  //生产环境
			gatewayPay.put("payType", 1L);
//			gatewayPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			gatewayPay.put("amount", 1);

			//代扣
			JSONObject daikouPay = new JSONObject();
			daikouPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			daikouPay.put("amount", 10);

			//组装支付方式
			JSONObject payMethod = new JSONObject();
//			payMethod.put("QUICKPAY", quickPay);
//			payMethod.put("GATEWAY", gatewayPay);
			payMethod.put("DAIKOU", daikouPay);

			String bizOrderNo = System.currentTimeMillis() + "cz";
			String backUrl = "http://122.227.225.142:23661/test/recievePayBack.jsp";

			String extendInfo = "this is extendInfo";

			JSONObject param = new JSONObject();
			param.put("bizUserId", bizUserId);
			param.put("bizOrderNo", bizOrderNo);
			param.put("accountSetNo", accountSetNo);
			param.put("amount", 10);
			param.put("fee", 0);
			param.put("frontUrl", frontUrl);
			param.put("backUrl", backUrl);
//			param.put("ordErexpireDatetime", ordErexpireDatetime);
			param.put("payMethod", payMethod);
			param.put("industryCode", industryCode);
			param.put("industryName", industryName);
			param.put("source", 2);
			param.put("summary", "deposit");
			param.put("extendInfo", extendInfo);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "depositApply", param);
			System.out.println("response:" + response);

			System.out.println("testApplyDeposit end");
		}catch(Exception e){
			System.out.println("testApplyDeposit error");
			e.printStackTrace();
		}
	}

	//提现申请
	@Test
	public void testApplyWithdraw() {
		try{
			System.out.println("testApplyWithdraw start");

			String bizOrderNo = System.currentTimeMillis() + "tx";
			String extendInfo = "this is withdraw.";
			String backUrl = "http://122.227.225.142:23661/test/recievePayBack.jsp";

			JSONObject param = new JSONObject();
			param.put("bizUserId", bizUserId);
			param.put("bizOrderNo", bizOrderNo);
			param.put("accountSetNo", accountSetNo);
			param.put("amount", 1);
			param.put("fee", 0);
			param.put("backUrl", backUrl);
			param.put("ordErexpireDatetime", ordErexpireDatetime);
			param.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			param.put("industryCode", industryCode);
			param.put("industryName", industryName);
			param.put("source", source);
			param.put("summary", "withdraw");
			param.put("extendInfo", extendInfo);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "withdrawApply", param);
			System.out.println("response:" + response);

			System.out.println("testApplyWithdraw end");
		}catch(Exception e){
			System.out.println("testApplyWithdraw error");
			e.printStackTrace();
		}
	}

	//消费申请
	@Test
	public void testApplyConsume() {
		try{
			System.out.println("testApplyConsume start");

			//支付方式
			//账户余额
			JSONArray accountPay = new JSONArray();
			JSONObject accountObject1 = new JSONObject();
			accountObject1.put("accountSetNo", accountSetNo);
			accountObject1.put("amount", 10);
			accountPay.put(accountObject1);

			//快捷
			JSONObject quickPay = new JSONObject();
			quickPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			quickPay.put("amount", 10);

			//网关
//			String frontUrl = "http://122.227.225.142:23661/service/gateway/getPayFront.jsp";
//			JSONObject gatewayPay = new JSONObject();
////		gatewayPay.put("bankCode", "vbank");  //虚拟银行，专门用于测试
//			gatewayPay.put("bankCode", "cmb");  //招商银行，生产环境
//			//gatewayPay.put("bankCardType", 1L);
//			gatewayPay.put("payType", 1L);
//			gatewayPay.put("amount", 1);
//
			String frontUrl = "http://122.227.225.142:23661/gateway/getPayFront.jsp";
			JSONObject gatewayPay = new JSONObject();
			gatewayPay.put("bankCode", "vbank");  //虚拟银行，专门用于测试环境
			//gatewayPay.put("bankCode", "abc");  //招商银行，生产环境
			gatewayPay.put("payType", 11L);
			gatewayPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			gatewayPay.put("amount", 10);

			//代扣
			JSONObject daikouPay = new JSONObject();
			daikouPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			daikouPay.put("amount", 10);

			//组装支付方式
			JSONObject payMethod = new JSONObject();
			payMethod.put("QUICKPAY", quickPay);
//			payMethod.put("GATEWAY", gatewayPay);
//			payMethod.put("BALANCE", accountPay);
//			payMethod.put("DAIKOU", daikouPay);

			String bizOrderNo = System.currentTimeMillis() + "xf";
			String backUrl = "http://10.55.3.236:6003/test/recievePayBack.jsp";
			String showUrl = "";
			String extendInfo = "this is extendInfo";

			JSONArray splitRule = new JSONArray();

			JSONObject splistRule1 = new JSONObject();
			splistRule1.put("bizUserId", "zxccy");
			splistRule1.put("amount", 6L);
			splistRule1.put("fee", 1L);
			splistRule1.put("remark", "aaaaa");

//			JSONObject splistRule1 = new JSONObject();
//			splistRule1.put("bizUserId", "zxccy");
//			splistRule1.put("amount", 30L);
//			splistRule1.put("fee", 5L);
//
//			JSONArray splitRuleList1 = new JSONArray();
//			JSONObject splistRule11 = new JSONObject();
//			splistRule11.put("bizUserId", "test1");
//			splistRule11.put("amount", 20L);
//			splistRule11.put("fee", 10L);
//			splistRule11.put("splitRuleList", new JSONArray());
//			splitRuleList1.put(splistRule11);
//
//			splistRule1.put("splitRuleList", splitRuleList1);
//
			splitRule.put(splistRule1);


//			JSONObject splitRule1 = new JSONObject();
//			splitRule1.put("recieverBizUserId", "dramd@123.com");
//			splitRule1.put("amount", 100);
//			splitRule.put(splitRule1);

			JSONObject param = new JSONObject();
			param.put("payerId", bizUserId);
			param.put("recieverId", sellerBizUserId);
			param.put("bizOrderNo", bizOrderNo);
			param.put("amount", 10);
			param.put("fee", 0);
//			param.put("splitRule", splitRule);
			param.put("frontUrl", frontUrl);
			param.put("backUrl", backUrl);
			param.put("showUrl", showUrl);
			param.put("ordErexpireDatetime", ordErexpireDatetime);
			param.put("payMethod", payMethod);
			param.put("goodsName", "test_goodsName");
			param.put("goodsDesc", "test_goodsDesc");
			param.put("industryCode", industryCode);
			param.put("industryName", industryName);
			param.put("source", source);
			param.put("summary", "consume");
			param.put("extendInfo", extendInfo);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "consumeApply", param);
			System.out.println("response:" + response);

			System.out.println("testApplyConsume end");
		}catch(Exception e){
			System.out.println("testApplyConsume error");
			e.printStackTrace();
		}
	}

	//代收申请
	@Test
	public void testApplyHostCollect() {
		try{
			System.out.println("testApplyHostCollect start");

			String tradeCode = "1001";

			//支付方式
			//账户余额
			JSONArray accountPay = new JSONArray();
			JSONObject accountObject1 = new JSONObject();
			accountObject1.put("accountSetNo", accountSetNo);
			accountObject1.put("amount", 1);
			accountPay.put(accountObject1);

			//快捷
			JSONObject quickPay = new JSONObject();
			quickPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			quickPay.put("amount", 1);

			//网关
			String frontUrl = "http://122.227.225.142:23661/gateway/getPayFront.jsp";
			JSONObject gatewayPay = new JSONObject();
			gatewayPay.put("bankCode", "vbank");  //虚拟银行，专门用于测试
			//gatewayPay.put("bankCardType", 1L);
			gatewayPay.put("payType", 1L);
			gatewayPay.put("amount", 10);

			//代金券
			JSONObject couponPay = new JSONObject();
			couponPay.put("amount", 1);

			//pos
			JSONObject posPay = new JSONObject();
			posPay.put("amount", 10);

			//代扣
			JSONObject daikouPay = new JSONObject();
			daikouPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			daikouPay.put("amount", 10);

			//组装支付方式
			JSONObject payMethod = new JSONObject();
			payMethod.put("QUICKPAY", quickPay);
//			payMethod.put("GATEWAY", gatewayPay);
//			payMethod.put("BALANCE", accountPay);
//			payMethod.put("COUPON", couponPay);
//			payMethod.put("POSPAY", posPay);
//			payMethod.put("DAIKOU", daikouPay);

			JSONArray recieverList = new JSONArray();

//			for(int i = 0; i < 1000; i++){
				JSONObject reciever1 = new JSONObject();
//				bizUserId = "testDS" + i;
				reciever1.put("bizUserId", bizUserId);
				reciever1.put("amount", 1);

				recieverList.put(reciever1);
//			}
//			JSONObject reciever1 = new JSONObject();
//			reciever1.put("bizUserId", bizUserId);
//			reciever1.put("amount", 4);
//
//			recieverList.put(reciever1);

			String bizOrderNo = System.currentTimeMillis() + "ds";
			String backUrl = "http://10.55.3.236:6003/test/recievePayBack.jsp";
			String showUrl = "";
			String extendInfo = "this is extendInfo";

			JSONObject param = new JSONObject();
			param.put("bizOrderNo", bizOrderNo);
			param.put("payerId", bizUserId);
			param.put("recieverList", recieverList);
//			param.put("goodsType", 1L);
//			param.put("goodsNo", "fadf");
			param.put("tradeCode", tradeCode);
			param.put("amount", 1);
			param.put("fee", 0);
			param.put("frontUrl", frontUrl);
			param.put("backUrl", backUrl);
			param.put("showUrl", showUrl);
			param.put("ordErexpireDatetime", ordErexpireDatetime);
			param.put("payMethod", payMethod);
			param.put("goodsName", "test_goodsName");
			param.put("goodsDesc", "test_goodsDesc");
			param.put("industryCode", industryCode);
			param.put("industryName", industryName);
			param.put("source", source);
			param.put("summary", "consume");
			param.put("extendInfo", extendInfo);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "agentCollectApply", param);
			System.out.println("response:" + response);

			System.out.println("testApplyHostCollect end");
		}catch(Exception e){
			System.out.println("testApplyHostCollect error");
			e.printStackTrace();
		}
	}

	//单笔代付
	@Test
	public void testApplyHostPay() {
		try{
			System.out.println("testApplyHostPay start");

			JSONArray collectPayList = new JSONArray();

			JSONObject collectPay1 = new JSONObject();
			collectPay1.put("bizOrderNo", "1464965224042ds");
			collectPay1.put("amount", 1);
			collectPayList.put(collectPay1);

//			JSONObject collectPay2 = new JSONObject();
//			collectPay2.put("bizOrderNo", "1446080576077ds");
//			collectPay2.put("amount", 0);
//			collectPayList.put(collectPay2);

			JSONArray splitRuleList = new JSONArray();

			JSONObject splistRule1 = new JSONObject();
			splistRule1.put("bizUserId", bizUserId);
			splistRule1.put("accountSetNo", accountSetNo);
			splistRule1.put("amount", 4L);
			splistRule1.put("fee", 0L);
			splistRule1.put("remark", "aaaa");
//
//			JSONArray splitRuleList1 = new JSONArray();
//			JSONObject splistRule11 = new JSONObject();
//			splistRule11.put("bizUserId", "test1");
//			splistRule11.put("amount", 10L);
//			splistRule11.put("fee", 5L);
//			splistRule11.put("splitRuleList", new JSONArray());
//			splitRuleList1.put(splistRule11);
//
//			splistRule1.put("splitRuleList", splitRuleList1);
//
			splitRuleList.put(splistRule1);

			JSONObject payToBankCardInfo = new JSONObject();
			String backUrl = "http://10.55.3.236:6003/test/recievePayBack.jsp";
			payToBankCardInfo.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			payToBankCardInfo.put("amount", 1);
			payToBankCardInfo.put("backUrl", backUrl);

			String bizOrderNo = System.currentTimeMillis() + "dbdf";

			JSONObject param = new JSONObject();
			param.put("bizOrderNo", bizOrderNo);
			param.put("bizUserId", bizUserId);
			param.put("collectPayList", collectPayList);
			param.put("accountSetNo", accountSetNo);
//			param.put("payToBankCardInfo", payToBankCardInfo);
			param.put("amount", 1L);
			param.put("fee", 0L);
//			param.put("splitRuleList", splitRuleList);
			//param.put("goodsType", 1L);
			//param.put("goodsNo", "1512280687994044");
			param.put("tradeCode", "2001");
			param.put("summary", "单笔代付");
			param.put("extendInfo", "this is extendInfo");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "signalAgentPay", param);
			System.out.println("response:" + response);

			System.out.println("testApplyHostPay end");
		}catch(Exception e){
			System.out.println("testApplyHostPay error");
			e.printStackTrace();
		}
	}

	//批量代付
	@Test
	public void testBatchAgentPay(){
		try{
			System.out.println("testBatchAgentPay start");

			JSONArray batchPayList = new JSONArray();

			JSONObject pay1 = new JSONObject();
			JSONArray collectPayList1 = new JSONArray();
			JSONObject collectPay11 = new JSONObject();
			collectPay11.put("bizOrderNo", "1466609753575ds");
			collectPay11.put("amount", 1);
			collectPayList1.put(collectPay11);
			JSONArray splitRuleList1 = new JSONArray();

			JSONObject splistRule1 = new JSONObject();
			splistRule1.put("bizUserId", "#yunBizUserId_application#");
			splistRule1.put("accountSetNo", "100002");
			splistRule1.put("amount", 8L);
			splistRule1.put("fee", 1L);

			splitRuleList1.put(splistRule1);

//			JSONObject payToBankCardInfo1 = new JSONObject();
//			String backUrl1 = "http://10.55.3.236:6003/test/recievePayBack.jsp";
//			payToBankCardInfo1.put("bankCardNo", rsaEncrypt(jjBankCardNo));
//			payToBankCardInfo1.put("amount", 90);
//			payToBankCardInfo1.put("backUrl", backUrl1);

			pay1.put("bizOrderNo", System.currentTimeMillis() + "df1");
			pay1.put("bizUserId", bizUserId);
			pay1.put("collectPayList", collectPayList1);
			pay1.put("accountSetNo", accountSetNo);
			pay1.put("backUrl", "http://122.227.225.142:23661/test/recievePayBack.jsp");
//			pay1.put("payToBankCardInfo", payToBankCardInfo1);
			pay1.put("amount", 1);
			pay1.put("fee", 0);
//			pay1.put("splitRuleList", splitRuleList1);
			pay1.put("summary", "单笔代付1");
			pay1.put("extendInfo", "this is extendInfo");
			batchPayList.put(pay1);

			JSONObject pay2 = new JSONObject();
			JSONArray collectPayList2 = new JSONArray();
			JSONObject collectPay21 = new JSONObject();
			collectPay21.put("bizOrderNo", "1463584799426ds");
			collectPay21.put("amount", 1);
			collectPayList2.put(collectPay21);
			JSONArray splitRuleList2 = new JSONArray();

			JSONObject payToBankCardInfo2 = new JSONObject();
			String backUrl2 = "http://122.227.225.142:23661/test/recievePayBack.jsp";
			payToBankCardInfo2.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			payToBankCardInfo2.put("amount", 100);
			payToBankCardInfo2.put("backUrl", backUrl2);

			pay2.put("bizOrderNo", System.currentTimeMillis() + "df2");
			pay2.put("bizUserId", bizUserId);
			pay2.put("collectPayList", collectPayList2);
			pay2.put("accountSetNo", accountSetNo);

//			pay2.put("payToBankCardInfo", payToBankCardInfo2);
			pay2.put("amount", 1);
			pay2.put("fee", 0);
//			pay2.put("splitRuleList", splitRuleList2);
			pay2.put("summary", "单笔代付2");
			pay2.put("extendInfo", "this is extendInfo");
//			batchPayList.put(pay2);

			String bizBatchNo = System.currentTimeMillis() + "pldf";

			JSONObject param = new JSONObject();
			param.put("bizBatchNo", bizBatchNo);
			param.put("batchPayList", batchPayList);
			param.put("tradeCode", "2001");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "batchAgentPay", param);
			System.out.println("response:" + response);

			System.out.println("testBatchAgentPay end");
		}catch(Exception e){
			System.out.println("testBatchAgentPay error");
			e.printStackTrace();
		}
	}

	//强实名认证
	@Test
	public void testHigherCardAuthApply() {
		try{
			System.out.println("testHigherCardAuthApply start");

			String bizOrderNo = System.currentTimeMillis() + "cz";
//			String backUrl = "http://10.55.3.236:6003/test/recievePayBack.jsp";
			String backUrl = "http://122.227.225.142:23661/test/recievePayBack.jsp";

			String extendInfo = "this is extendInfo";

			JSONObject param = new JSONObject();
			param.put("bizOrderNo", bizOrderNo);
			param.put("bizUserId", bizUserId);
			param.put("accountSetNo", accountSetNo);
			param.put("bankCardNo", rsaEncrypt(jjBankCardNo));
			param.put("payType", 27);
//			param.put("bankCode", );
//			param.put("ordErexpireDatetime", );
//			param.put("frontUrl", ordErexpireDatetime);
			param.put("backUrl", backUrl);
			param.put("summary", "");
			param.put("extendInfo", "");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "higherCardAuthApply", param);
			System.out.println("response:" + response);

			System.out.println("testHigherCardAuthApply end");
		}catch(Exception e){
			System.out.println("testHigherCardAuthApply error");
			e.printStackTrace();
		}
	}

	//后台支付
	@Test
	public void testPay() {
		try{
			System.out.println("testPay start");

			JSONObject param = new JSONObject();
			param.put("bizUserId", bizUserId);
			param.put("bizOrderNo", "1466991510465xf");
			param.put("tradeNo", "1606274854839470D1");
			param.put("verificationCode", "777630");
			param.put("consumerIp", "192.168.11.11");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "pay", param);
			System.out.println("response:" + response);

			System.out.println("testPay end");
		}catch(Exception e){
			System.out.println("testPay error");
			e.printStackTrace();
		}
	}

	//商品录入
	@Test
	public void testEntryGoods(){
		try{
			System.out.println("testEntryGoods start");

			String bizGoodsNo = System.currentTimeMillis() + "splr";

			//代还款信息
			JSONArray repayInfo = new JSONArray();

			JSONObject repay1 = new JSONObject();
			repay1.put("period", 1);
			repay1.put("amount", 1100);
			repay1.put("interest", 100);
			repay1.put("principal", 1000);
			repay1.put("overplusPrincipal", 2000);
			JSONObject repay2 = new JSONObject();
			repay2.put("period", 2);
			repay2.put("amount", 2200);
			repay2.put("interest", 200);
			repay2.put("principal", 2000);
			repay2.put("overplusPrincipal", 0);

			repayInfo.put(repay1);
			repayInfo.put(repay2);

			//标的信息
			JSONObject goodsParams = new JSONObject();
			goodsParams.put("totalAmount", 10000);
			goodsParams.put("annualYield", 0.12);
			goodsParams.put("beginDate", "2015-11-16");
			goodsParams.put("endDate", "2015-11-17");
			goodsParams.put("repayPeriodNumber", 12);
			goodsParams.put("repayType", 1);
			goodsParams.put("guaranteeType", 1);
			goodsParams.put("repayInfo", repayInfo);

			JSONObject param = new JSONObject();
			param.put("bizUserId", bizUserId);
			param.put("goodsType", 1);
			param.put("bizGoodsNo", bizGoodsNo);
			param.put("goodsName", "标的1");
			param.put("goodsDetail", "我是标的1的详细信息。");
			param.put("goodsParams", goodsParams);
			param.put("showUrl", "showUrl");
			param.put("extendInfo", "this is extendInfo");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "entryGoods", param);
			System.out.println("response:" + response);

			System.out.println("testEntryGoods end");
		}catch(Exception e){
			System.out.println("testEntryGoods error");
			e.printStackTrace();
		}
	}

	//冻结金额
	@Test
	public void testFreezeMoney(){
		try{
			System.out.println("testFreezeMoney start");

			String bizFreezenNo = System.currentTimeMillis() + "dj";

			JSONObject param = new JSONObject();
			param.put("bizUserId", bizUserId);
			param.put("bizFreezenNo", bizFreezenNo);
			param.put("accountSetNo", accountSetNo);
			param.put("amount", 2);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "freezeMoney", param);
			System.out.println("response:" + response);

			System.out.println("testFreezeMoney end");
		}catch(Exception e){
			System.out.println("testFreezeMoney error");
			e.printStackTrace();
		}
	}

	//解冻金额
	@Test
	public void testUnfreezeMoney(){
		try{
			System.out.println("testUnfreezeMoney start");

			JSONObject param = new JSONObject();
			param.put("bizUserId", bizUserId);
			param.put("bizFreezenNo", "1455690205508dj");
			param.put("accountSetNo", accountSetNo);
			param.put("amount", 2);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "unfreezeMoney", param);
			System.out.println("response:" + response);

			System.out.println("testUnfreezeMoney end");
		}catch(Exception e){
			System.out.println("testUnfreezeMoney error");
			e.printStackTrace();
		}
	}

	//退款
	@Test
	public void testRefund(){
		try{
			System.out.println("testRefund start");

			String bizOrderNo = System.currentTimeMillis() + "tk";

			JSONObject refund1 = new JSONObject();
			refund1.put("bizUserId", bizUserId);
			refund1.put("amount", 10);

			JSONArray refundList = new JSONArray();
			refundList.put(refund1);

			JSONObject param = new JSONObject();
			param.put("bizOrderNo", bizOrderNo);
			param.put("oriBizOrderNo", "1466574440260xf");
			param.put("bizUserId", bizUserId);
			param.put("refundList", refundList);
			param.put("amount", 10);
			param.put("couponAmount", 0);
			param.put("feeAmount", 0);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "refund", param);
			System.out.println("response:" + response);

			System.out.println("testRefund end");
		}catch(Exception e){
			System.out.println("testRefund error");
			e.printStackTrace();
		}
	}

	//流标退款
	@Test
	public void testFailureBidRefund(){
		try{
			System.out.println("testFailureBidRefund start");

			String bizBatchNo = System.currentTimeMillis() + "lbtk";

			JSONArray batchRefundList = new JSONArray();

			JSONObject batchRefund1 = new JSONObject();
			String batchRefundNo1 = System.currentTimeMillis() + "tklb1";
			batchRefund1.put("bizOrderNo", batchRefundNo1);
			batchRefund1.put("oriBizOrderNo", "1457682253913ds");
			batchRefund1.put("summary", "aaaa");

			JSONObject batchRefund2 = new JSONObject();
			String batchRefundNo2 = System.currentTimeMillis() + "tklb2";
			batchRefund2.put("bizOrderNo", batchRefundNo2);
			batchRefund2.put("oriBizOrderNo", "1451273070827ds");
			batchRefund2.put("summary", "bbbb");

			batchRefundList.put(batchRefund1);
//			batchRefundList.put(batchRefund2);

			JSONObject param = new JSONObject();
			param.put("bizBatchNo", bizBatchNo);
			param.put("goodsType", 1);
			param.put("goodsNo", "fadf");
			param.put("batchRefundList", batchRefundList);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "failureBidRefund", param);
			System.out.println("response:" + response);

			System.out.println("testFailureBidRefund end");
		}catch(Exception e){
			System.out.println("testFailureBidRefund error");
			e.printStackTrace();
		}
	}

	//平台转账
	@Test
	public void testApplicationTransfer(){
		try{
			System.out.println("testApplicationTransfer start");

			JSONObject param = new JSONObject();
			param.put("bizTransferNo", "eew");
			param.put("sourceAccountSetNo", "2000000");
			param.put("targetBizUserId", bizUserId);
			param.put("targetAccountSetNo", accountSetNo);
			param.put("amount", 1);
			param.put("remark", "平台转账");
//			param.put("extendInfo", "");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "applicationTransfer", param);
			System.out.println("response:" + response);

			System.out.println("testApplicationTransfer end");
		}catch(Exception e){
			System.out.println("testApplicationTransfer error");
			e.printStackTrace();
		}
	}

	//查询余额
	@Test
	public void testQueryBalance(){
		try{
			System.out.println("testQueryBalance start");

			JSONObject param = new JSONObject();
			param.put("bizUserId", "308");
			param.put("accountSetNo", "200006");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "queryBalance", param);
			System.out.println("response:" + response);

			System.out.println("testQueryBalance end");
		}catch(Exception e){
			System.out.println("testQueryBalance error");
			e.printStackTrace();
		}
	}

	//查询订单状态
	@Test
	public void testGetOrderDetail(){
		try{
			System.out.println("testGetOrderDetail start");

			JSONObject param = new JSONObject();
			param.put("bizUserId", bizUserId);
			param.put("bizOrderNo", "1460426914766cz");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "getOrderDetail", param);
			System.out.println("response:" + response);

			System.out.println("testGetOrderDetail end");
		}catch(Exception e){
			System.out.println("testGetOrderDetail error");
			e.printStackTrace();
		}
	}


	//查询订单详情
//		@Test
//		public void testQueryOrderDetail(){
//			try{
//				System.out.println("testQueryOrderDetail start");
//
//				JSONObject param = new JSONObject();
//				param.put("bizUserId", bizUserId);
////				param.put("bizOrderNo", "");
//				param.put("orderType", 4);
//				param.put("orderStatus", 1);
//				param.put("dateStart", "2015-11-04 14:08:09");
//				param.put("dateEnd", "2015-12-04 14:08:09");
//				param.put("numberStart", 0);
//				param.put("numberEnd", 10);
//
//				System.out.println("request:" + param);
//				JSONObject response = client.request(soaName, "queryOrderDetail", param);
//				System.out.println("response:" + response);
//
//				System.out.println("testQueryOrderDetail end");
//			}catch(Exception e){
//				System.out.println("testQueryOrderDetail error");
//				e.printStackTrace();
//			}
//		}

	/**
	 * 查询支付详情
	 */
	@Test
	public void testQueryOrderPayDetail(){
		try{
			System.out.println("testQueryOrderPayDetail start");

			JSONObject param = new JSONObject();
			param.put("bizUserId", "pfweida10000000010");
			param.put("bizOrderNo", "dpkj0018");

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "queryOrderPayDetail", param);
			System.out.println("response:" + response);

			System.out.println("testQueryOrderPayDetail end");
		}catch(Exception e){
			System.out.println("testQueryOrderPayDetail error");
			e.printStackTrace();
		}
	}

	//查询收支明细
	@Test
	public void testQueryInExpDetail(){
		try{
			System.out.println("testQueryInExpDetail start");

			JSONObject param = new JSONObject();
			param.put("bizUserId", bizUserId);
			param.put("accountSetNo", "200001");
			param.put("dateStart", "2016-02-02");
			param.put("dateEnd", "2016-02-17");
			param.put("startPosition", 1);
			param.put("queryNum", 9);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "queryInExpDetail", param);
			System.out.println("response:" + response);

			System.out.println("testQueryInExpDetail end");
		}catch(Exception e){
			System.out.println("testQueryInExpDetail error");
			e.printStackTrace();
		}
	}

	//跨应用转账
	@Test
	public void testTransferCrossApplication(){
		try{
			System.out.println("testTransferCrossApplication start");

			String bizOrderNo = System.currentTimeMillis() + "kyyzz";

			JSONObject param = new JSONObject();
			param.put("bizOrderNo", bizOrderNo);
			param.put("bizUserId", bizUserId);
			param.put("accountSetNo", "200002");
			param.put("opponentSysid", "100000000003");
			param.put("opponentBizUserId", "zxccy");
			param.put("opponentAccountSetNo", "200001");
			param.put("amount", 1);
			param.put("source", 1);
			param.put("summary", "aaaa");
			//param.put("extendInfo", null);

			System.out.println("request:" + param);
			JSONObject response = client.request(soaName, "transferCrossApplication", param);
			System.out.println("response:" + response);

			System.out.println("testTransferCrossApplication end");
		}catch(Exception e){
			System.out.println("testTransferCrossApplication error");
			e.printStackTrace();
		}
	}

	//RSA加密
	public String rsaEncrypt(String signStr) throws Exception{
		try{
			System.out.println("rsaEncrypt start");

			RSAUtil rsaUtil = new RSAUtil((RSAPublicKey)publicKey, (RSAPrivateKey)privateKey);
			return rsaUtil.encrypt(signStr);
		}catch(Exception e){
			System.out.println("rsaEncrypt error");
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testAdjustAccount() throws Exception{

		JSONObject param = new JSONObject();
		param.put("bizNo", "a2");
		param.put("sysid", "100000000002");
		param.put("sourceMemberId", 2L);
		param.put("sourceAccountTypeId", 1L);
		param.put("targetMemberId", 17L);
		param.put("targetAccountTypeId", 6L);
		param.put("amount", 10L);
		param.put("remark", "zz");

		JSONObject response = client.request(soaName, "cancel", param);

		System.out.println(response);
		System.out.println("over");
	}
}
