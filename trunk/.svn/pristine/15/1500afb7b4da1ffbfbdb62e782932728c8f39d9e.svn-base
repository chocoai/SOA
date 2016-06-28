<%@ page language="java" pageEncoding="UTF-8" import="java.util.*,apf.framework.*,ime.document.*,apf.util.*,javax.persistence.*,ime.core.*,ime.security.services.*,ime.security.entity.*"%>
<%
	String id = request.getParameter("id");
	StringBuilder sb = new StringBuilder();

	Map<String, Object> user;
	SessionContext ctx = null;
	try {
		ctx = EntityManagerUtil.currentContext();
		EntityManager em = ctx.getEntityManager();
		
		Query query = em.createQuery("from Employee where operator_id = :id");
		query.setParameter("id", id);
		user = (Map<String, Object>)query.getResultList().get(0);
		
		String photo = (String)user.get("photo");
		if( photo != null ){
			String[] part = photo.split(":");
			if( part.length == 2 )
				photo = part[1];
			else
				photo = null;
		}
		if( photo != null ){
			sb.append(DocumentStorage.getWebPath("", "attachments/")).append(photo);
		}
		else {
			Long sex = (Long)user.get("sex");
			if( sex != null && sex == 2L )
				sb.append("/").append(Framework.instance().getApplicationName()).append("/images/share/user_female.png");
			else
				sb.append("/").append(Framework.instance().getApplicationName()).append("/images/share/user_male.png");
		}
	} catch(Exception e){
		sb.append("/").append(Framework.instance().getApplicationName()).append("/images/share/user_male.png");
	}finally{
		EntityManagerUtil.closeSession(ctx);
	}

	response.sendRedirect(sb.toString());
%>
