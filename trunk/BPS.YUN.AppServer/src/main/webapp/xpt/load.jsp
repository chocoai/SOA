<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="java.io.*,apf.framework.*,ime.security.*,ime.core.*,ime.core.entity.*,ime.document.*,ime.core.services.*"%>
<%
	String pathname = request.getParameter("pathname");
	String path = Framework.instance().getAbsolutePath("/xpt/root/" + pathname);
	OutputStream os = response.getOutputStream();
	FileInputStream is = new FileInputStream(path);
	byte[] buffer = new byte[1024];
	int len;
	while( (len = is.read(buffer)) != -1 ){
		os.write(buffer, 0, len);
	}
	os.flush();
	is.close();
%>



