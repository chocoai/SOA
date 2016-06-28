<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="apf.util.*, org.hibernate.*"%>
<%
String operator_id = request.getParameter("operator_id");
String loginId = request.getParameter("loginId");
String photo = "";
SessionContext ctx = null;
try {
	ctx = EntityManagerUtil.currentContext();
	Session hsession = ctx.getHibernateSession();
	StringBuffer sb = new StringBuffer();
	if(operator_id != null && !"".equals(operator_id.trim()))
		sb.append("select photo from Employee where operator_id=:operator_id");
	else if(loginId != null && !"".equals(loginId.trim()))
		sb.append("select photo from Employee where loginId=:loginId");
	
	Query query = hsession.createQuery(sb.toString());
	if(operator_id != null && !"".equals(operator_id.trim()))
		query.setParameter("operator_id", Long.valueOf(operator_id));
	else if(loginId != null && !"".equals(loginId.trim()))
		query.setParameter("loginId", loginId);
	photo = (String)query.uniqueResult();
	if(photo == null)
		photo = "";
}catch (Exception e) {
	e.printStackTrace();
}finally {
	EntityManagerUtil.closeSession(ctx);
}
%>
<%=photo.trim()%>