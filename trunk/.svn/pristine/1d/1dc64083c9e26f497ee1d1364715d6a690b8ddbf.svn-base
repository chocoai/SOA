package bps.external.soa;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ime.service.client.SOAClient;
import ime.service.util.RSAUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSoaMemberWithoutService {
    private static SOAClient client;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;
    private String ordErexpireDatetime = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //soa服务名
    //private static String soaName = "MemberServiceWithoutConfirm";
    private static String soaName = "OrderServiceWithoutConfirm";
    //业务系统用户编号
    private static String bizUserId = "tongtian";
    //手机号码
    private static String phone = "13123811301";
    //账户集
    private String accountSetNo = "200001";
    //访问终端类型
    private Long source = 1L;
    //行业代码
    private String industryCode = "1010";
    //行业名称
    private String industryName = "保险代理";

    @Before
    public void before(){
        //订单过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 15);
        Date date = calendar.getTime();
        ordErexpireDatetime = sdf.format(date);
    }
    //绑定手机 ok
    @BeforeClass
    public static void beforeClass() throws Exception{
		String serverAddress = "http://10.55.3.236:6003/service/soa";
//        String serverAddress = "http://122.227.225.142:23661/service/soa";
//		String serverAddress = "https://yun.allinpay.com/service/soa";

        String signMethod = "SHA1WithRSA";
        String sysid = "100000000002";
        //密钥密码
        String pwd = "697057";
        //证书名称
        String alias = "100000000002";
        //证书文件路径，用户指定
        String path = "F:\\dpp\\jar\\100000000002.pfx";




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
    //无验证绑定手机
    @Test
    public void testBindPhone(){
        try{
            System.out.println("testBindPhone start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("phone", phone);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "bindPhoneWithoutConfirm", param);
            System.out.println("response:" + response);

            System.out.println("testBindPhone end");
        }catch(Exception e){
            System.out.println("testBindPhone error");
            e.printStackTrace();
        }
    }

    //无验证充值
    @Test
    public void testApplyDeposit() {
        try{
            System.out.println("testApplyDeposit start");

            //支付方式
            //快捷
            JSONObject quickPay = new JSONObject();
            quickPay.put("bankCardNo", rsaEncrypt("6228480318051063456"));
            quickPay.put("amount", 117L);

            //网关
            String frontUrl = "http://122.227.225.142:23661/gateway/getPayFront.jsp";
            JSONObject gatewayPay = new JSONObject();
            gatewayPay.put("bankCode", "vbank");  //虚拟银行，专门用于测试环境
//			gatewayPay.put("bankCode", "boc");  //生产环境
            gatewayPay.put("payType", 1L);
            gatewayPay.put("bankCardNo", rsaEncrypt("6228480318051081870"));
            gatewayPay.put("amount", 117L);

            //组装支付方式
            JSONObject payMethod = new JSONObject();
            payMethod.put("QUICKPAY", quickPay);
            //payMethod.put("GATEWAY", gatewayPay);

            String bizOrderNo = System.currentTimeMillis() + "cz";
            String backUrl = "http://10.55.3.236:6003/test/recievePayBack.jsp";

            String extendInfo = "this is extendInfo";

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("bizOrderNo", bizOrderNo);
            param.put("accountSetNo", accountSetNo);
            param.put("amount", 117L);
            param.put("fee", 15);
            param.put("frontUrl", frontUrl);
            param.put("backUrl", backUrl);
            param.put("ordErexpireDatetime", ordErexpireDatetime);
            param.put("payMethod", payMethod);
            param.put("industryCode", industryCode);
            param.put("industryName", industryName);
            param.put("source", source);
            param.put("summary", "deposit");
            param.put("extendInfo", extendInfo);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "depositWithoutConfirm", param);
            System.out.println("response:" + response);

            System.out.println("testApplyDeposit end");
        }catch(Exception e){
            System.out.println("testApplyDeposit error");
            e.printStackTrace();
        }
    }

    //无验证提现
    @Test
    public void testApplyWithdraw(){
        try{

            String bizOrderNo = System.currentTimeMillis() + "tx";
            String extendInfo = "this is withdraw t+1.";
            String backUrl = "http://10.55.3.236:6003/test/recievePayBack.jsp";

            JSONObject param = new JSONObject();
            param.put("bizUserId", "zxccy");
            param.put("bizOrderNo", bizOrderNo);
            param.put("accountSetNo", "200001");
            param.put("amount", 1051L);
            param.put("fee", 10);
            param.put("backUrl", backUrl);
            param.put("ordErexpireDatetime", ordErexpireDatetime);
            System.out.println(ordErexpireDatetime);
            param.put("bankCardNo", rsaEncrypt("6228480318051081870"));
            param.put("industryCode", industryCode);
            param.put("industryName", industryName);
            param.put("source", 1L);//终端类型输入
            param.put("summary", "withdraw");
            param.put("extendInfo", extendInfo);
         // param.put("withdrawType", "T0");


            JSONObject response = client.request(soaName, "withdrawWithoutMessage", param);
            System.out.println("response:" + response);
        }catch (Exception e){
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
            accountObject1.put("amount", 10000L);
            accountPay.put(accountObject1);

            //快捷
            JSONObject quickPay = new JSONObject();
            quickPay.put("bankCardNo", rsaEncrypt("6228480318051081870"));
            quickPay.put("amount", 1);

            //网关L
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
            gatewayPay.put("payType", 111L);
            //gatewayPay.put("bankCardNo", rsaEncrypt(jjBankCardNo));
            gatewayPay.put("amount", 1);

            //组装支付方式
            JSONObject payMethod = new JSONObject();
//			payMethod.put("QUICKPAY", quickPay);
//			payMethod.put("GATEWAY", gatewayPay);
            payMethod.put("BALANCE", accountPay);

            String bizOrderNo = System.currentTimeMillis() + "xf";
            String backUrl = "http://10.55.3.236:6003/test/recievePayBack.jsp";
            String showUrl = "";
            String extendInfo = "this is extendInfo";

            JSONArray splitRule = new JSONArray();

            JSONObject splistRule1 = new JSONObject();
            splistRule1.put("bizUserId", "zxccy");
            splistRule1.put("amount", 3100L);
            splistRule1.put("fee", 5L);

            JSONArray splitRuleList1 = new JSONArray();
            JSONObject splistRule11 = new JSONObject();
            splistRule11.put("bizUserId", "test1");
            splistRule11.put("amount", 3200L);
            splistRule11.put("fee", 10L);
            splistRule11.put("splitRuleList", new JSONArray());
            splitRuleList1.put(splistRule11);

            splistRule1.put("splitRuleList", splitRuleList1);

            splitRule.put(splistRule1);


//			JSONObject splitRule1 = new JSONObject();
//			splitRule1.put("recieverBizUserId", "dramd@123.com");
//			splitRule1.put("amount", 100);
//			splitRule.put(splitRule1);

            JSONObject param = new JSONObject();
            param.put("payerId", bizUserId);
            param.put("recieverId", "test1");
            param.put("bizOrderNo", bizOrderNo);
            param.put("amount", 10000L);
            param.put("fee", 1L);
            //param.put("splitRule", splitRule);
            param.put("frontUrl", frontUrl);
            param.put("backUrl", backUrl);
            param.put("showUrl", showUrl);
//			param.put("ordErexpireDatetime", ordErexpireDatetime);
            param.put("payMethod", payMethod);
            param.put("goodsName", "test_goodsName");
            param.put("goodsDesc", "test_goodsDesc");
            param.put("industryCode", industryCode);
            param.put("industryName", industryName);
            param.put("source", source);
            param.put("summary", "consume");
            param.put("extendInfo", extendInfo);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "consumeWithoutMessage", param);
            System.out.println("response:" + response);

            System.out.println("testApplyConsume end");
        }catch(Exception e){
            System.out.println("testApplyConsume error");
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

}
