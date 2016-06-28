package bps.member.test;

import java.io.File;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import apf.framework.Framework;
import bps.member.Member;
import bps.member.MemberServiceImpl;
import bps.service.MemberService;
import qs.spring.config.WebConfig;
import qs.spring.controller.test.HomeControllerTest;

public class MemberServiceTestByLiuy {
	MemberService memberService = new MemberServiceImpl();
	
	@BeforeClass 
	public static void enter() throws Exception{
		String path = HomeControllerTest.class.getResource("/").getPath();
		File file = new File(path);
		String appPath = file.getParent() + "/src/main/webapp/";
		Framework.instance().setApplicationPath(appPath);
		Framework.instance().setApplicationName("");
		Framework.instance().initialize();
	}
	
	@Test
	public void member()throws Exception{
		Member m=new Member();
		m.setPhone("15967858888");
		System.out.println(m.getUserInfo().get("id"));
	}
	
	@Test
	public void loginByPhone()throws Exception{
		//Map<String, Object> result =memberService.loginByPhone("pc", "15967858888", "99999", "psw", "", null);
		//System.out.println(result);
	}
	
	@Test
	public void registerCompanyMember() throws Exception {
		//Map<String, Object> result = memberService.registerCompanyMember("test@email.com","123456","127.0.0.1", "xarea", null);
		//System.out.println(result);
	}

	@Test
	public void setExtendInfo(){
	}
	
	@Test
	public void getExtendInfo(){
	}
	
	@Test
	public void bindCard(){
	}
	
	@Test
	public void unbindBankCard(){
	}
	
	@Test
	public void modifyPhone(){
	}
	
	@Test
	public void getOutAccount(){
	}
	
	@Test
	public void createMemberOutAccount(){
	}
	
	@Test
	public void changeMemberOutAccountState(){
	}
	
	@Test
	public void createFeedback(){
	}
	
	@Test
	public void getLinkManById(){
	}
	
	@Test
	public void getLinkManByPhone(){
	}
	
	@Test
	public void getLinkManByBankCard(){
	}
	
	@Test
	public void getLinkManList(){
	}
	
	@Test
	public void searchLinkManList(){
	}
	
	@Test
	public void addContact(){
	}
	
	@Test
	public void updateContact(){
	}
	
	@Test
	public void updateContactById(){
	}
	
	@Test
	public void queryAccountList(){
	}
	
	@Test
	public void setMemberState(){
	}
	
	@Test
	public void setUserName(){
	}
	
}
