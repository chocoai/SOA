<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java" import="jxl.Sheet"%>
<%@ page language="java" import="jxl.Workbook"%>
<%@ page language="java" import="ime.security.util.TripleDES"%>
<%@ page language="java" import="ime.security.Password"%>
<%@ page language="java" import="ime.core.services.DynamicEntityService"%>
<%@ page language="java" import="bps.member.Member"%>
<%@ page language="java" import="bps.common.Constant"%>
<%@ page language="java" import="apf.work.TransactionWork"%>
<%@ page language="java" import="apf.util.EntityManagerUtil"%>
<%@ page language="java" import="org.hibernate.Transaction"%>
<%@ page language="java" import="org.hibernate.Session"%>
<%@ page language="java" import="org.apache.log4j.Logger"%>
<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="java.io.*"%>
<%@ page language="java" import="ime.security.LoginSession"%>
<%@ page language="java" import="ime.core.Reserved"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

</body>
</html>
<%
String filePath = "/home/bps.yun/jboss-4.2.3.GA/server/default/deploy/IMEServer.war/imp/sjctimp.xls";
//String outPath = "/home/bps.yun/jboss-4.2.3.GA/server/default/deploy/IMEServer.war/imp/sjctimp.xls";
File file = new File("/home/bps.yun/jboss-4.2.3.GA/server/default/deploy/IMEServer.war/imp/out.txt");
if(!file.exists()){
	file.createNewFile(); 
}
importUser(filePath,file);
String state = "ok";
%>
<%=state %>

<%!
private static Logger imp_logger = Logger.getLogger("imp");
public void importUser(String filePath,File outFile)throws Exception{
	
	try{
		final String _filePath = filePath;
		final File _outFile = outFile;
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
        EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				
				InputStream is = new FileInputStream(_filePath);  
		        Workbook rwb = Workbook.getWorkbook(is);  
		        Sheet st = rwb.getSheet(0);
		        imp_logger.info("start");
		        FileWriter fileWriter=new FileWriter(_outFile, true);
				for(int index=2;index<st.getRows();index++){//行
					String member_type = st.getCell(1,index).getContents();
					String biz_user_id = st.getCell(0,index).getContents();
					String emial = st.getCell(2,index).getContents();
					String country = st.getCell(3,index).getContents();
					String province = st.getCell(4,index).getContents();
					String area = st.getCell(5,index).getContents();
					String name = st.getCell(6,index).getContents();
					String idNo = st.getCell(7,index).getContents();
					String isRealName = st.getCell(8,index).getContents();
					String phone = st.getCell(9,index).getContents();
					String isBindphone = st.getCell(10,index).getContents();
					Map<String, String> memberMap = new HashMap<String, String>();
			         String uuid = UUID.randomUUID().toString();
			         memberMap.put("userId",             uuid);
			         memberMap.put("member_type",        String.valueOf(member_type));
			         if(Constant.MEMBER_TYPE_ENTERPRISE.equals(Long.parseLong(member_type))){
			        	 memberMap.put("user_state",         String.valueOf(Constant.USER_STATE_AUDITED_WAIT));
			         }else{
			        	 memberMap.put("user_state",         String.valueOf(Constant.USER_STATE_ACTIVATION));
			         }
			         memberMap.put("register_time",      String.valueOf(new Date().getTime()));
			         memberMap.put("biz_user_id", biz_user_id);
			         memberMap.put("application_id", "101");
			         memberMap.put("application_label", "世纪创投");
			         String memberNo =  Member.getCustomerNo(Long.parseLong(member_type),new Date(),session);
			         memberMap.put("memberNo",           memberNo);
			         memberMap.put("source", "9");
			         if(emial!=null&&!"".equals(emial)){
			        	 memberMap.put("emial", emial);
			         }
			         if(country!=null&&!"".equals(country)){
			        	 memberMap.put("country", country);
			         }
			         if(province!=null&&!"".equals(province)){
			        	 memberMap.put("province", province);
			         }
			         if(area!=null&&!"".equals(area)){
			        	 memberMap.put("area", area);
			         }
			         //实名
			         if(idNo!=null&&!"".equals(idNo)){
			        	StringBuffer sb = new StringBuffer();
			 			sb.setLength(0);
			 			for(int i = 0, j = idNo.length() - 4; i < j; i ++) {
			 				sb.append("*");
			 			}
			 			sb.append(idNo.substring(idNo.length() - 4));
			 			String identity_cardNo_encrypt = TripleDES.encrypt(uuid + Constant.MEMBER_BANK_ENCODE_KEY, idNo);
			 			String identity_cardNo_md5 = Password.encode(idNo, "MD5");
			 			memberMap.put("identity_cardNo", sb.toString());
			 			
			 			memberMap.put("identity_cardNo_encrypt", identity_cardNo_encrypt);
			 			memberMap.put("identity_cardNo_md5", identity_cardNo_md5);
			 			memberMap.put("name", name);
			 			if(isRealName!=null &&!"".equals(isRealName)){
			 				memberMap.put("real_name_time", String.valueOf(new Date().getTime()));
			 				memberMap.put("isIdentity_checked", "true");
			 			}
			         }
			         if(phone != null && !"".equals(phone)){
			        	 memberMap.put("phone", phone);
			        	 if(isBindphone!=null &&!"".equals(isBindphone)){
				 				memberMap.put("isPhone_checked", "true");
				 			}
			         }
			         Map<String,Object> member_entity = DynamicEntityService.createEntity("AMS_Member", memberMap, null);
			         
			         //fileWriter.write(uuid+"\r\n");
			         fileWriter.write(uuid+"\n");
				}
				fileWriter.flush();
				   fileWriter.close();
				imp_logger.info("end");
				 return null;
				
			}
		});
    
	}catch(Exception e){
		e.printStackTrace();
		imp_logger.error(e.getMessage(), e);
		throw e;
	}
}



%>