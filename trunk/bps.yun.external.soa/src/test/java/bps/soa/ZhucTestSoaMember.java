package bps.soa;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import ime.service.client.SOAClient;
import ime.service.util.RSAUtil;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class ZhucTestSoaMember {
    private static SOAClient client;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    //soa服务名
    private static String soaName = "MemberService";
    //业务系统用户编号


    private static String bizUserId = "zhuc";

    //手机号码

    private static String phone = "18969859976";

    //更换的手机号码
    private static String changePhone = "18969859976";
    //借记卡卡号
    //private static String jjBankCardNo = "6214835743207027";
//	private static String jjBankCardNo = "6228480318051081800";
//	private static String jjBankCardNo = "214317681728";
    private static String jjBankCardNo = "6228490318051063458";
    //信用卡卡号
    private static String xyBankCardNo = "";
    //信用卡有效期
    private static String validityPeriod = "";
    //信用卡CVV2
    private static String vericationValue = "";
    //身份证号码
    private static String identityNo = "330227198609281492";

    @BeforeClass
    public static void beforeClass() throws Exception{

        String serverAddress = "http://10.55.3.236:6003/service/soa";
//		String serverAddress = "http://122.227.225.142:23661/service/soa";
//		String serverAddress = "https://yun.allinpay.com/service/soa";



        String sysid = "100000000002";
        //密钥密码
        String pwd = "697057";
        //证书名称
        String alias = "100000000002";
        //证书文件路径，用户指定
        String path = "E:\\pfx\\100000000002.pfx";

        //商户号
//		String sysid = "100000000001";
//		//密钥密码
//		String pwd = "502175";
//		//证书名称
//		String alias = "100000000001";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000001.pfx";



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


        //zhengshi
//
//		String sysid = "100000000001";
//		//密钥密码
//		String pwd = "502175";
//		//证书名称
//		String alias = "100000000001";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000001.pfx";

//		开发测试
//		String sysid = "000000000001";
//		//密钥密码
//		String pwd = "203156";
//		//证书名称
//		String alias = "000000000001";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\000000000001.pfx";
//		商户号
//		String sysid = "100000000006";
//		//密钥密码
//		String pwd = "210090";
//		//证书名称
//		String alias = "100000000006";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000006.pfx";



//		String sysid = "100000000024";
//		//密钥密码
//		String pwd = "014926";
//		//证书名称
//		String alias = "100000000024";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000024.pfx";

//		String sysid = "100000000024";
//		//密钥密码
//		String pwd = "014926";
//		//证书名称
//		String alias = "100000000024";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000024.pfx";

//		String sysid = "100000000048";
//		//密钥密码
//		String pwd = "901639";
//		//证书名称
//		String alias = "100000000048";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000048.pfx";
//
// 		String sysid = "100000000007";
//		//密钥密码
//		String pwd = "856313";
//		//证书名称
//		String alias = "100000000007";
//		//证书文件路径，用户指定
//		String path = "E:\\pfx\\100000000007.pfx";




        String signMethod = "SHA1WithRSA";

        try{
            privateKey = RSAUtil.loadPrivateKey(alias, path, pwd);

//			byte[] keyBytes = privateKey.getEncoded();
//            String s = (new BASE64Encoder()).encode(keyBytes);
//			System.out.println("aa:" + s);

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

    //创建会员
    @Test
    public void testCreateMember() {
        try{
            System.out.println("testCreateMember start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("memberType", "3");
            param.put("source", "2");
            param.put("extendParam", new JSONObject());

            System.out.println("request:" + param);

            JSONObject response = client.request(soaName, "createMember", param);
            System.out.println("response:" + response);

            System.out.println("testCreateMember end");
        }catch(Exception e){
            System.out.println("testCreateMember error");
            e.printStackTrace();
        }
    }

    //发送短信验证码  ok
    @Test
    public void testSendVerificationCode(){
        try{
            System.out.println("testSendVerificationCode start");

            JSONObject extendParams = new JSONObject();

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("phone", phone);
            param.put("verificationCodeType", 9);
            param.put("extendParams", extendParams);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "sendVerificationCode", param);
            System.out.println("response:" + response);

            System.out.println("testSendVerificationCode end");
        }catch(Exception e){
            System.out.println("testSendVerificationCode error");
            e.printStackTrace();
        }
    }

    //验证短信验证码 ok
    @Test
    public void testValidateVerificationCode(){
        try{
            System.out.println("testValidateVerificationCode start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("phone", phone);
            param.put("verificationCodeType", 6);
            param.put("verificationCode", "164214");

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "validateVerificationCode", param);
            System.out.println("response:" + response);

            System.out.println("testValidateVerificationCode end");
        }catch(Exception e){
            System.out.println("testValidateVerificationCode error");
            e.printStackTrace();
        }
    }

    //实名认证 ok
    @Test
    public void testSetRealName(){
        try{
            System.out.println("testSetRealName start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);

            param.put("name", "朱成");

            param.put("identityType", 1);
            param.put("identityNo", rsaEncrypt(identityNo));

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "setRealName", param);
            System.out.println("response:" + response);

            String value = response.getString("signedValue");
            String sign = response.getString("sign");
            System.out.println(RSAUtil.verify(publicKey, value, sign));

            System.out.println("testSetRealName end");
        }catch(Exception e){
            System.out.println("testSetRealName error");
            e.printStackTrace();
        }
    }

    //绑定手机 ok
    @Test
    public void testBindPhone(){
        try{
            System.out.println("testBindPhone start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("phone", phone);
            param.put("verificationCode", "758337");

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "bindPhone", param);
            System.out.println("response:" + response);

            System.out.println("testBindPhone end");
        }catch(Exception e){
            System.out.println("testBindPhone error");
            e.printStackTrace();
        }
    }

    //设置企业会员信息 ok
    @Test
    public void testSetCompanyInfo(){
        try{
            System.out.println("testSetCompanyInfo start");

            JSONObject param = new JSONObject();

            JSONObject companyBasicInfo = new JSONObject();
            companyBasicInfo.put("companyName", "通联");
            companyBasicInfo.put("companyAddress", "浙江省宁波市");
            companyBasicInfo.put("businessLicense", "111111");
            companyBasicInfo.put("organizationCode", "222222");
            companyBasicInfo.put("telephone", "333333");
            companyBasicInfo.put("legalName", "zx_company8");
            companyBasicInfo.put("identityType", 1);
            companyBasicInfo.put("legalIds", rsaEncrypt("444444"));
            companyBasicInfo.put("legalPhone", "555555");
            companyBasicInfo.put("accountNo", rsaEncrypt("214317681728"));
            companyBasicInfo.put("parentBankName", "中国银行");
            companyBasicInfo.put("bankCityNo", "777777");
            companyBasicInfo.put("bankName", "宁波支行");

            param.put("bizUserId", bizUserId);
            param.put("companyBasicInfo", companyBasicInfo);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "setCompanyInfo", param);
            System.out.println("response:" + response);

            System.out.println("testSetCompanyInfo end");
        }catch(Exception e){
            System.out.println("testSetCompanyInfo error");
            e.printStackTrace();
        }
    }

    //设置个人会员信息 ok
    @Test
    public void testSetMemberInfo(){
        try{
            System.out.println("testSetMemberInfo start");

            JSONObject param = new JSONObject();

            JSONObject userInfo = new JSONObject();
            userInfo.put("name", "zx");
            userInfo.put("country", "zhongguo");
            userInfo.put("province", "zhejiang");
            userInfo.put("area", "ningbo");
            userInfo.put("address", "jiangshan");

            param.put("bizUserId", bizUserId);
            param.put("userInfo", userInfo);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "setMemberInfo", param);
            System.out.println("response:" + response);

            System.out.println("testSetMemberInfo end");
        }catch(Exception e){
            System.out.println("testSetMemberInfo error");
            e.printStackTrace();
        }
    }

    //获取会员信息（个人和企业） ok
    @Test
    public void testGetMemberInfo(){
        try{
            System.out.println("testGetMemberInfo start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "getMemberInfo", param);
            System.out.println("response:" + response);

            System.out.println("testGetMemberInfo end");
        }catch(Exception e){
            System.out.println("testGetMemberInfo error");
            e.printStackTrace();
        }
    }

    //获取卡bin ok
    @Test
    public void testGetBankCardBin(){
        try{
            System.out.println("testGetBankCardBin start");

            JSONObject param = new JSONObject();
            param.put("cardNo", rsaEncrypt("6228480306407181669"));

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "getBankCardBin", param);
            System.out.println("response:" + response);

            System.out.println("testGetBankCardBin end");
        }catch(Exception e){
            System.out.println("testGetBankCardBin error");
            e.printStackTrace();
        }
    }

    //绑定银行卡申请
    @Test
    public void testApplyBindBankCard(){
        try{
            System.out.println("testApplyBindBankCard start");

            //借记卡
            JSONObject param = new JSONObject();
//            param.put("bizUserId", bizUserId);
            param.put("bizUserId", "62016000007240257484");
            param.put("cardNo", rsaEncrypt("6228480306407181669"));
//            param.put("phone", phone);
            param.put("phone", "13963177997");
            param.put("name", "冯娅");
            param.put("cardType", 1L);
            param.put("bankCode", "01030000");
//            param.put("bankCode", "03080000");
            param.put("identityType", 1);
            param.put("identityNo", rsaEncrypt("370481198204040021"));
            param.put("isSafeCard", false);
//			param.put("cvv2", rsaEncrypt("563"));
//			param.put("validate", rsaEncrypt("2010"));

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "applyBindBankCard", param);
            System.out.println("response:" + response);

            System.out.println("testApplyBindBankCard end");
        }catch(Exception e){
            System.out.println("testApplyBindBankCard error");
            e.printStackTrace();
        }
    }

    //绑定银行卡确认
    @Test
    public void testBindBankCard(){
        try{
            System.out.println("testBindBankCard start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("tranceNum", "D201605278482");
            param.put("transDate", "20160527");
//            param.put("phone", phone);
            param.put("phone", "13963177997");
            param.put("verificationCode", "684135");

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "bindBankCard", param);
            System.out.println("response:" + response);

            System.out.println("testBindBankCard end");
        }catch(Exception e){
            System.out.println("testBindBankCard error");
            e.printStackTrace();
        }
    }

    //查询绑定银行卡
    @Test
    public void testQueryBankCard(){
        try{
            System.out.println("testQueryBankCard start");

            //查询单张卡
            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("cardNo", rsaEncrypt(jjBankCardNo));

            //查询全部
//			JSONObject param = new JSONObject();
//			param.put("bizUserId", bizUserId);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "queryBankCard", param);
            System.out.println("response:" + response);

            System.out.println("testQueryBankCard end");
        }catch(Exception e){
            System.out.println("testQueryBankCard error");
            e.printStackTrace();
        }
    }

    //解绑绑定银行卡
    @Test
    public void testUnbindBankCard(){
        try{
            System.out.println("testUnbindBankCard start");

            JSONObject param = new JSONObject();
//            param.put("bizUserId", bizUserId);
            param.put("bizUserId", "62016000007240257484");
//			param.put("cardNo", rsaEncrypt(jjBankCardNo));
            param.put("cardNo", rsaEncrypt("6228480306407181669 "));

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "unbindBankCard", param);
            System.out.println("response:" + response);

            System.out.println("testUnbindBankCard end");
        }catch(Exception e){
            System.out.println("testUnbindBankCard error");
            e.printStackTrace();
        }
    }

    //更改绑定手机 ok
    @Test
    public void testChangeBindPhone(){
        try{
            System.out.println("testChangeBindPhone start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
            param.put("oldPhone", phone);
            param.put("oldVerificationCode", "455059");
            param.put("newPhone", changePhone);
            param.put("newVerificationCode", "921206");

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "changeBindPhone", param);
            System.out.println("response:" + response);

            System.out.println("testChangeBindPhone end");
        }catch(Exception e){
            System.out.println("testChangeBindPhone error");
            e.printStackTrace();
        }
    }

    //锁定用户 ok
    @Test
    public void testLockMember(){
        try{
            System.out.println("testLockMember start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "lockMember", param);
            System.out.println("response:" + response);

            System.out.println("testLockMember end");
        }catch(Exception e){
            System.out.println("testLockMember error");
            e.printStackTrace();
        }
    }

    //解锁用户 ok
    @Test
    public void testUnlockMember(){
        try{
            System.out.println("testUnlockMember start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "unlockMember", param);
            System.out.println("response:" + response);

            System.out.println("testUnlockMember end");
        }catch(Exception e){
            System.out.println("testUnlockMember error");
            e.printStackTrace();
        }
    }
    @Test
    public void testSetSafeCard(){
        try{
            System.out.println("testSetSafeCard start");

            JSONObject param = new JSONObject();
            param.put("bizUserId", bizUserId);
//            param.put("cardNo",  rsaEncrypt(jjBankCardNo));
            param.put("cardNo",  rsaEncrypt("6228491234567890124"));
            param.put("setSafeCard", true);

            System.out.println("request:" + param);
            JSONObject response = client.request(soaName, "setSafeCard", param);
            System.out.println("response:" + response);

            System.out.println("testSetSafeCard end");
        }catch(Exception e){
            System.out.println("testSetSafeCard error");
            e.printStackTrace();
        }
    }
    //RSA加密
    @Test
    public void testRsaEncrypt() throws Exception{
        try{
            System.out.println("rsaEncrypt start");

            String str = "6210999922228980128";

            RSAUtil rsaUtil = new RSAUtil((RSAPublicKey)publicKey, (RSAPrivateKey)privateKey);
            String encryptStr = rsaUtil.encrypt(str);
            System.out.println(encryptStr);
        }catch(Exception e){
            System.out.println("rsaEncrypt error");
            e.printStackTrace();
            throw e;
        }
    }

    //RSA加密
    public String rsaEncrypt(String str) throws Exception{
        try{
            System.out.println("rsaEncrypt start");

            RSAUtil rsaUtil = new RSAUtil((RSAPublicKey)publicKey, (RSAPrivateKey)privateKey);
            String encryptStr = rsaUtil.encrypt(str);
            return encryptStr;
        }catch(Exception e){
            System.out.println("rsaEncrypt error");
            e.printStackTrace();
            throw e;
        }
    }

    //RSA解密
    public String rsaDecrypt(String signStr) throws Exception{
        try{
            System.out.println("rsaDecrypt start");

            RSAUtil rsaUtil = new RSAUtil((RSAPublicKey)publicKey, (RSAPrivateKey)privateKey);
            String dencryptStr = rsaUtil.dencrypt(signStr);
            System.out.println("解密：" + dencryptStr);
            return dencryptStr;
        }catch(Exception e){
            System.out.println("rsaDecrypt error");
            e.printStackTrace();
            throw e;
        }
    }
}
