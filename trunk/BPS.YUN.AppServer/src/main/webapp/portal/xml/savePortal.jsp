<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="apf.util.*,javax.persistence.*,ime.security.*,java.util.*,ime.core.services.*, org.hibernate.*"%>
<%
	LoginSession.setHttpSession(session);

	String configS = request.getParameter("config");
	if(configS == null || "".equals(configS))
		return;
	SessionContext ctx = null;
	EntityTransaction tx = null;
	try {
		ctx = EntityManagerUtil.currentContext();
		tx = ctx.getEntityManager().getTransaction();
		Session hsession = ctx.getHibernateSession();
		if(ctx.isCreater())
			tx.begin();

		Long principalID = (Long)LoginSession.getCurrentAttribute(LoginSession.LOGIN_PRINCIPAL_ID);
		String longinName = (String)LoginSession.getCurrentAttribute(LoginSession.LOGIN_NAME);
		
		StringBuilder sb = new StringBuilder();
		sb.append("select id from UserProfile where owner_user_id=:owner_user_id and profile_type=:profile_type");
		org.hibernate.Query query = hsession.createQuery(sb.toString());
		query.setParameter("owner_user_id", principalID);
		query.setParameter("profile_type", "user.portal");
		if(query.list().isEmpty()){
			Map<String, String> entityMap = new HashMap<String, String>();
			entityMap.put("owner_user_id",principalID.toString());
			entityMap.put("owner_user_name",longinName);
			entityMap.put("profile_type","user.portal");
			entityMap.put("profile_content",configS);
			
			DynamicEntityService.createEntity("UserProfile", entityMap, null);
		}else{
			sb.setLength(0);
			sb.append("update UserProfile set profile_content=:profile_content where owner_user_id=:owner_user_id and profile_type=:profile_type");
			query = hsession.createQuery(sb.toString());
			query.setParameter("owner_user_id", principalID);
			query.setParameter("profile_type", "user.portal");
			query.setParameter("profile_content", configS);
			query.executeUpdate();
		}
		
		if(ctx.isCreater())
			tx.commit();
	} catch (Exception e) {
		if(ctx.isCreater())
			tx.rollback();
		e.printStackTrace();
	} finally {
		EntityManagerUtil.closeSession(ctx);
	}
%>