<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8" import="ime.security.*,java.util.*,org.json.*"%>
<%
	String result = "";
	try{
		LoginSession loginSession = new LoginSession(request, response);

		String loginName = request.getParameter("loginName");
		String password = request.getParameter("password");
		String verifycode = request.getParameter("verifycode");
		String domain = request.getParameter("domainName");

		String errorMsg = null;

		String[] part = loginName.split("@");
		if (part.length == 2) {
			loginName = part[0];
			domain = part[1];
		}
		Map<String, Object> loginInfo = null;
		/*
		if (verifycode == null || !verifycode.equals(session.getAttribute("verifycode"))) {
			errorMsg = "验证码错误！";
		} 
		else 
		*/
		{
			loginInfo = loginSession.login(loginName, password, domain);
			JSONObject r = new JSONObject();
			r.put("status", "OK");
			r.put("returnValue", loginInfo);
			result = r.toString();
		}
	}
	catch(Exception e){
		result = "{status:\"error\", message:\"" + e.getMessage() + "\"}";
	}
%>
<%=result%>