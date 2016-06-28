package bps.order.test;

import java.text.SimpleDateFormat;
import java.util.*;

import bps.common.Constant;
import bps.common.MD5Util;

import bps.member.MemberServiceImpl;
import bps.service.MemberService;
import org.json.JSONObject;
import org.junit.BeforeClass;

import apf.framework.Framework;
import bps.order.OrderServiceImpl;
import bps.service.OrderService;
import org.junit.Test;
//import test.TestBank;

public class OrderServiceTest {
	OrderService orderService = new OrderServiceImpl();
	MemberService memberService = new MemberServiceImpl();
//	TestBank testBank = new TestBank();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private static String key = "xQ4mm0YD7o5YBolR5om0";
	@BeforeClass 
	public static void enter() throws Exception{
		//String path = HomeControllerTest.class.getResource("/").getPath();
		//File file = new File(path);
		//String appPath = file.getParent() + "/src/main/webapp/";
		//String appPath = "F:\\ideawork\\YUN\\BPS.YUN.AppServer\\src\\main\\webapp\\";
		//Framework.instance().setApplicationPath(appPath);
		//Framework.instance().setApplicationName("");
		//Framework.instance().initialize();
	}
	@Test
	public void posQuery() throws Exception{


		String request_time = sdf.format(new Date());
		String paycode = "000143";

		String str = "<transaction_header>" +
				"<transaction_type>MP0001</transaction_type>" +
				"<requester>ALLINPAY</requester>" +
				//"<response_time>" + request_time + "</response_time>" +
				"</transaction_header>" +
				"<transaction_body>" +
				"<paycode>" + paycode + "</paycode>" +
				"<mcht_cd>309610148168003</mcht_cd>" +
				"<terminal_id>00000004</terminal_id>" +
				"</transaction_body>" + key;


		String md5 = MD5Util.MD5(str);
		String out = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<transaction>\n" +
				"<transaction_header>\n" +
				"<transaction_type>MP0001</transaction_type>\n" +
				"<requester>ALLINPAY</requester>\n" +
				//"<request_time>"+request_time+"</request_time>\n" +
				"</transaction_header>\n" +
				"<transaction_body>\n" +
				"<paycode>"+paycode+"</paycode>\n" +
				"<terminal_id>00000004</terminal_id>\n" +
				"<mcht_cd>309610148168003</mcht_cd>\n" +
				"</transaction_body>\n" +
				"<sign_type>MD5</sign_type>\n" +
				"<sign>"+md5+"</sign>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
				"</transaction>";
		String ret = Util.httpSend("http://122.227.225.142:23661/PosServlet.do",out);
		System.out.println(ret);
	}
	@Test
	public void posPay()throws Exception{
		String request_time = sdf.format(new Date());
		String paycode = "000142";
		String str = "<transaction_header>" +
				"<transaction_type>MP0002</transaction_type>" +
				"<requester>ALLINPAY</requester>" +
				"</transaction_header>" +
				"<transaction_body>" +
				"<paycode>"+paycode+"</paycode>" +
				"<pay_type>1</pay_type>" +
				"<mcht_cd>309610148168003</mcht_cd>" +
				"<terminal_id>00000004</terminal_id>" +
				"<amt>0.10</amt>" +
				"<trace_no>000061</trace_no>" +
				"<refer_no>000000123454</refer_no>" +
				"<bank_card_no>406252********5270</bank_card_no>" +
				"<bank_code>01050000</bank_code>" +
				"</transaction_body>"+key;
		String md5 = MD5Util.MD5(str);
		String out = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<transaction>\n" +
				"<transaction_header>\n" +
				"<transaction_type>MP0002</transaction_type>\n" +
				"<requester>ALLINPAY</requester>\n" +
				"<request_time>"+request_time+"</request_time>\n" +
				"</transaction_header>\n" +
				"<transaction_body>\n" +
				"<paycode>"+paycode+"</paycode>\n" +
				"<pay_type>1</pay_type>\n" +
				"<trace_no>000061</trace_no>\n" +
				"<refer_no>000000123454</refer_no>\n" +
				"<amt>0.10</amt>\n" +
				"<bank_card_no>406252********5270</bank_card_no>\n" +
				"<bank_code>01050000</bank_code>\n" +
				"<terminal_id>00000004</terminal_id>\n" +
				"<mcht_cd>309610148168003</mcht_cd>\n" +
				"</transaction_body>\n" +
				"<sign_type>MD5</sign_type>\n" +
				"<sign>"+md5+"</sign>\n" +
				"</transaction>\n";
		String ret = Util.httpSend("http://122.227.225.142:23661/PosServlet.do",out);
		System.out.println(ret);
	}
	@Test
	public void bank() throws Exception{
//		testBank.Test();
	}
	@Test
	public void testCreate() throws Exception {

		String bizOrderNo = System.currentTimeMillis() + "tx";

		String message = "\n";

		Long applicationId = 2L;
		String bizUserId = "zhuc";
		Map<String, Object> memberEntity = memberService.getUserInfo(applicationId,bizUserId);
		Long memberId = (Long)memberEntity.get("id");
		Long amount = 1L;
		Long fee = 0L;
		Long source = 1L;
		String withdrawType = "T0";
		String accountSetNo = "200001";
		String bankCardNo = "6228480318051063456";
		String backUrl = "http://10.55.3.236:6003/test/recievePayBack.jsp";
		String ordErexpireDatetime = null;
		String industryCode = "1010";
		String industryName = "保险代理";
		Map<String, Object> bankEntity = memberService.getBindBankCard(memberId,bankCardNo);
		Long bindBankCardId = (Long)bankEntity.get("id");

		//组装数据
		//支付信息
		List<Map<String, Object>> payInterfaceList = new ArrayList<>();
		Map<String, Object> payInterfaceMap = new HashMap<>();
		payInterfaceMap.put("tradeMoney", amount);

		payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_TLT_DF);
		if (withdrawType.equals("T1")){
			payInterfaceMap.put("payInterFaceNo", Constant.PAY_INTERFACE_TLT_BACH_DF);
		}
		payInterfaceMap.put("call_type", Constant.CALL_TYPE_INTERFACE);
		payInterfaceMap.put("bankId", bindBankCardId);
		payInterfaceMap.put("accountSetNo", accountSetNo);
		payInterfaceList.add(payInterfaceMap);

		//其他信息
		Map<String, Object> extParams = new HashMap<>();
		extParams.put("bizOrderNo", bizOrderNo);
		extParams.put("bankCardId", bindBankCardId);
		extParams.put("fee", fee);
		extParams.put("backUrl", backUrl);
		extParams.put("ordErexpireDatetime", ordErexpireDatetime);
		extParams.put("industry_code", industryCode);
		extParams.put("industry_name", industryName);
		extParams.put("source", source);
		extParams.put("summary", "");
		extParams.put("extend_info", "");
		Map<String,Object> response = orderService.applyOrder(applicationId, memberId, bizOrderNo, amount, Constant.ORDER_TYPE_WITHDRAW, Constant.TRADE_TYPE_WITHDRAW, source, extParams, null, payInterfaceList, null);
		String phone = (String)memberEntity.get("phone");
		message += "response:"+ response.toString();
		message += "\n";
		String status = (String)response.get("status");
		System.out.println("response:"+response);
		if( status.equals("OK")){
			JSONObject jb;
			jb = new JSONObject(response.get("returnValue"));
			Map<String, Object> orderEntity = orderService.getOrder(applicationId, bizOrderNo);
			String orderNo = (String)orderEntity.get("orderNo");

			//如果有快捷支付，则去获取银行卡预留手机
			List<Map<String, Object>> commandsList = orderService.getCommands(orderNo);
			if(!(commandsList == null || commandsList.isEmpty())){
				for(Map<String, Object> temp : commandsList){
					if("1".equals(temp.get("seq_no").toString()) && Constant.PAY_INTERFACE_QUICK.equals(temp.get("pay_interfaceNo").toString())){
						phone = (String)temp.get("phone");
						break;
					}
				}
			}


			Map<String,Object> response2 = orderService.confirmPay(memberId,orderNo,jb.get("tradeNo").toString(),"",phone,"");

			message += "payResponse:"+response+"\n";
			status = (String)response2.get("status");
			if( !status.equals("OK")){
				String errorCode = (String)response.get("errorCode");
				String _message = (String)response.get("message");
				throw new Exception("errorCode:"+errorCode+";message:"+_message);
			}
			System.out.println(message);
		}

		//Map<String, Object> result=orderService.getDailyLimit("2000201", "00000000  ", 1L);
		//System.out.println(result);
	}
}
