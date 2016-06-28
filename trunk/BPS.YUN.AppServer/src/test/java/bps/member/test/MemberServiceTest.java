package bps.member.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bps.common.Constant;
import bps.order.OrderServiceImpl;
import bps.service.OrderService;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import bps.member.MemberServiceImpl;
import bps.service.MemberService;
import qs.spring.config.WebConfig;

@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(classes = WebConfig.class)  
@WebAppConfiguration
public class MemberServiceTest {
	@Test
	public void testCreate() throws Exception {
		MemberService memberService = new MemberServiceImpl();
		
		//Map<String, Object> result = memberService.registerUserByPhone("pc", 3L, "15544545445", "99999", "asd123456", "zxc123456", "", "", null);
		//System.out.println(result);
		//TODO test result
	}
	@Test
	public void testCreate2() throws Exception {
		OrderService orderService = new OrderServiceImpl();
		MemberService memberService = new MemberServiceImpl();
		JSONObject param = new JSONObject();
		String bizOrderNo = System.currentTimeMillis() + "tx";


		String message = "request:"+ param.toString();
		message += "\n";

		Long applicationId = 2L;
		String bizUserId = "zhuc";
		Map<String, Object> memberEntity = memberService.getUserInfo(applicationId,bizUserId);
		Long memberId = (Long)memberEntity.get("id");
		Long amount = 163L;
		Long fee = 10L;
		Long source = 1L;
		String withdrawType = "T1";
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
			JSONObject jb = new JSONObject(response.get("returnValue"));


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
		}

		//Map<String, Object> result=orderService.getDailyLimit("2000201", "00000000  ", 1L);
		//System.out.println(result);
	}
}
