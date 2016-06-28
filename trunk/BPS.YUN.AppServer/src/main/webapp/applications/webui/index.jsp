<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"
	import="ime.security.*,ime.webui.*,org.apache.velocity.*"%>
<%
	LoginSession.setHttpSession(session);
	if (!LoginSession.isLogined()) {
		response.sendRedirect("../../login.jsp?redir=applications/webui/index.jsp");
		return;
	}

	String theme = (String) session.getAttribute("theme");
	if (theme == null) {
		theme = "default";
		session.setAttribute("theme", theme);
	}

	Template template = TemplateEngine.getTemplate(theme + "/index.vm");
	VelocityContext context = TemplateEngine.newContext();

	context.put("theme", theme);
	context.put("userSession",LoginSession.getCurrentAttribute(LoginSession.LOGIN_NAME));
	context.put("rootUrl", TemplateEngine.getRootUrl());
	context.put("appUrl", TemplateEngine.getAppUrl());
	context.put("themeUrl", TemplateEngine.getTemplateRootUrl() + theme
			+ "/");

	TemplateEngine.mergeTemplate(template, context, response);
%>
