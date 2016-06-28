<%@ page import="ime.security.LoginSession" %>
<%@ page import="ime.core.Reserved" %>
<%@ page import="java.util.Map" %>
<%@ page import="apf.util.EntityManagerUtil" %>
<%@ page import="apf.work.TransactionWork" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.Query" %>
<%@ page import="java.util.List" %>
<%@ page import="ime.security.util.TripleDES" %>
<%@ page import="bps.common.Constant" %>
<%@ page import="bps.common.MD5Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
		Test();
%>
<%!
	void Test() throws Exception{
		LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
		Map<String, Object> app_entity= EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
			public Map<String, Object> doTransaction(Session session, Transaction tx)
					throws Exception {
				String sql = "from AMS_MemberBank";

				Query query = session.createQuery(sql);

				List<Map<String, Object>> list = query.list();
				try {
					for (Map<String, Object> temp : list) {
						System.out.println();
						String userId = (String) temp.get("member_label");
						Long id = (Long) temp.get("id");
						String en_accountNo = (String) temp.get("accountNo_encrypt");
						System.out.println("=========================");
						System.out.println(id);
						System.out.println(en_accountNo);
						String accountNo = TripleDES.decrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, en_accountNo);
						if( accountNo != null ) {
							String accountNo_md5 = MD5Util.MD5(accountNo);
							query = session.createQuery("update AMS_MemberBank set accountNo_md5=:accountNo_md5 where id=:id");
							query.setParameter("accountNo_md5", accountNo_md5);
							query.setParameter("id", id);
							query.executeUpdate();
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				return null;
			}
			//return null;
		});
	}
%>