<%@ page import="bps.common.Constant" %>
<%@ page import="ime.security.util.TripleDES" %>
<%@ page import="bps.common.MD5Util" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<%
		String filePath = "/home/bps.yun.test/request.csv";
		String writePath ="/home/bps.yun.test/reply.csv";

		String userId = "bdc9da85-9994-4c3e-a62b-8ca6cac67b85";
		String accountNo = "11014825688006";
		String en_accountNo = TripleDES.encrypt(userId + Constant.MEMBER_BANK_ENCODE_KEY, accountNo);
		String account_md5 = MD5Util.MD5(accountNo);

		out.println("des=" + en_accountNo);
		out.println("md5=" + account_md5);
	%>

</body>
</html>