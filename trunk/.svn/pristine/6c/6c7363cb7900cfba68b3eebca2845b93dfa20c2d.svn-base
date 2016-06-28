<%@ page import="ime.security.LoginSession" %>
<%@ page import="ime.core.Reserved" %>
<%@ page import="apf.util.EntityManagerUtil" %>
<%@ page import="apf.work.TransactionWork" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="java.io.*" %>
<%@ page import="bps.common.Constant" %>
<%@ page import="bps.member.Member" %>
<%@ page import="ime.security.util.TripleDES" %>
<%@ page import="ime.security.Password" %>
<%@ page import="ime.core.services.DynamicEntityService" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.*" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<%
		String filePath = "/home/bps.yun.test/request.csv";
		String writePath ="/home/bps.yun.test/reply.csv";
		transferAMSMember(filePath,writePath);
	%>
	<%!
		public static Logger logger = Logger.getLogger("member");
		void transferAMSMember(String filePath,String writePath) throws Exception{

			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
			BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( new File(writePath)),"UTF-8"));
			StringBuilder responStr = new StringBuilder();
			String strLine = "";
			int count = 0;
			LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			while((strLine = bufferedReader.readLine())!=null) {
				StringBuilder lineTemp = new StringBuilder();
				count++;
				String strCell[] = strLine.split(",");
				String biz_user_id = strCell[0];
				String phone = strCell[1];
				String member_type = strCell[2];
				String source = strCell[3];
				String create_time = strCell[4];
				String identity_type = strCell[5];
				String identity_no = strCell[6];
				String real_name = strCell[7];
				String is_auth = strCell[8];
				String uid = strCell[9];

				String uuid = UUID.randomUUID().toString();

				Map<String, String> memberMap = new HashMap<String, String>();
				lineTemp.append(biz_user_id).append(",");
				lineTemp.append(uuid).append(",");
				memberMap.put("userId", uuid);

				memberMap.put("member_type", String.valueOf(member_type));
				if (Constant.MEMBER_TYPE_ENTERPRISE.equals(Long.parseLong(member_type))) {
					memberMap.put("user_state", String.valueOf(Constant.USER_STATE_AUDITED_WAIT));
				} else {
					memberMap.put("user_state", String.valueOf(Constant.USER_STATE_ACTIVATION));
				}
				memberMap.put("register_time", String.valueOf(new Date().getTime()));
				memberMap.put("biz_user_id", biz_user_id);
				//测试和正式的application不一样
				//memberMap.put("application_id", "81");
				memberMap.put("application_id", "82");
				memberMap.put("application_label", "中融宝");
				/*String memberNo = Member.getCustomerNo(Long.parseLong(member_type), new Date(), session);
				memberMap.put("memberNo", memberNo);*/
				memberMap.put("source", source);
				//实名
				if (identity_no != null && !"".equals(identity_no)) {
					StringBuffer sb = new StringBuffer();
					sb.setLength(0);
					for (int i = 0, j = identity_no.length() - 4; i < j; i++) {
						sb.append("*");
					}
					sb.append(identity_no.substring(identity_no.length() - 4));
					String identity_cardNo_encrypt = TripleDES.encrypt(uuid + Constant.MEMBER_BANK_ENCODE_KEY, identity_no);
					String identity_cardNo_md5 = Password.encode(identity_no, "MD5");
					memberMap.put("identity_cardNo", sb.toString());

					memberMap.put("identity_cardNo_encrypt", identity_cardNo_encrypt);
					memberMap.put("identity_cardNo_md5", identity_cardNo_md5);
					logger.info("real_name"+real_name);
					memberMap.put("name", real_name);
					if (real_name != null && !"".equals(real_name)) {
						memberMap.put("real_name_time", String.valueOf(new Date().getTime()));
						memberMap.put("isIdentity_checked", "true");


						lineTemp.append("01").append(",");
					} else {
						lineTemp.append("00").append(",");
					}
					lineTemp.append("01").append(",");
					lineTemp.append("1");
				}
				if (phone != null && !"".equals(phone)) {
					memberMap.put("phone", phone);
					memberMap.put("isPhone_checked", "true");
				}
				//单个map组装完毕;
				list.add(memberMap);
				//用于回执;
				responStr.append(lineTemp.toString() + "\r\n");
			}
			logger.info("组装完毕大小为"+list.size());
			//开始数据库操作
			//每一百条发起事务,之后延时1秒；
			//每条插入之前需要查询生成客户号customerNo;
			try{
				int length = list.size();
				logger.info("AMS_Member开始创建");
				final List<Map<String,String>> memberMapTemp = list;
				for(int i = 0;i<length;i+=500){
					final int left = length - i;
					final int index = i;
					EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
						@Override
						public Map<String, Object> doTransaction(Session session, Transaction tx)
								throws Exception {
								if(left<500){
									logger.info("AMS_Member剩余开始创建");
									for(int j = 0;j<left;j++){
										Map<String,String> memberMap = memberMapTemp.get(index+j);
										String member_type = memberMap.get("member_type");
										String memberNo = Member.getCustomerNo(Long.parseLong(member_type), new Date(), session);
										memberMap.put("memberNo", memberNo);
										Map<String, Object> member_entity = DynamicEntityService.createEntity("AMS_Member", memberMap, null);
									}
									logger.info("AMS_Member单个创建成功");
									logger.info("剩余个数为:"+left);
								}else{
									logger.info("AMS_Member单个开始创建");
									long startTime = System.currentTimeMillis();
									for(int j = 0;j<500;j++){
										Map<String,String> memberMap = memberMapTemp.get(index+j);
										String member_type = memberMap.get("member_type");

										long startTime1 = System.currentTimeMillis();
										String memberNo = Member.getCustomerNo(Long.parseLong(member_type), new Date(), session);
										long endTime1 = System.currentTimeMillis();
										logger.info("单个查询时间为"+(endTime1-startTime1));
										memberMap.put("memberNo", memberNo);

										long startTime2 = System.currentTimeMillis();
										Map<String, Object> member_entity = DynamicEntityService.createEntity("AMS_Member", memberMap, null);
										long endTime2 = System.currentTimeMillis();
										logger.info("单个插入时间为"+(endTime2-startTime2));
									}
									long endTime = System.currentTimeMillis();
									logger.info("AMS_Member单个创建成功");
									logger.info("500条导入的时间");
								}

							Thread.sleep(500);
							return null;
						}
					});
				}
				logger.info("AMS_Member插入成功");
			}catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				throw e;
			}
			bufferedWriter.write(responStr.toString());
			bufferedWriter.close();
			bufferedReader.close();
			logger.info("end");
			/*logger.info("transferAMSMember--Start:"+filePath);
			try{
				LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
				for(int i =0; i < count; i += 100){
					final int index = i;
					EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
						@Override
						public Map<String, Object> doTransaction(Session session, Transaction tx)
								throws Exception {


							for(int j = 0; j < 100; j++) {
								index + j


								Map<String, String> empty = new HashMap<String, String>();
								logger.info("AMS_Member开始创建");
								Map<String, Object> member_entity = DynamicEntityService.createEntity("AMS_Member", memberMap, null);
								logger.info("AMS_Member插入成功");


							}
						}
						bufferedWriter.write(responStr.toString());
						bufferedWriter.close();
						bufferedReader.close();

						System.out.println("总记录数为："+count);
						System.out.println("end");
						return null;
					}

				});
				}
			}

			}catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				throw e;
			}*/

		}
	%>

</body>
</html>