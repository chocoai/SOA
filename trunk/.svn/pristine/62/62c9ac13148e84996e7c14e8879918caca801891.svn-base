<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="java.io.*,apf.framework.*,ime.security.*,ime.core.*,ime.core.entity.*,ime.document.*,ime.core.services.*"%>
<%
	String site = request.getParameter("site");
	String pathname = "/" + site + "/template.xml";
	String path = Framework.instance().getAbsolutePath("/xpt/root/" + pathname);
	String contentXml;
	File file = new File(path);
	if( !file.exists() ){
		contentXml = "<error message=\"站点尚未建立。\" />";
	}
	else {
		OutputStream os = response.getOutputStream();
		FileInputStream is = new FileInputStream(path);
		byte[] buffer = new byte[1024];
		int len;
		while( (len = is.read(buffer)) != -1 ){
			os.write(buffer, 0, len);
		}
		os.flush();
		is.close();
	}
%>



