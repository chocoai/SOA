<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="java.io.*,apf.framework.*,ime.security.*,ime.core.*,ime.core.entity.*,ime.document.*,ime.core.services.*"%>
<%
	LoginSession.setHttpSession(session);

	String result = "{status:'OK'}";
	try{
		String pathname = request.getParameter("pathname");
		String content  = request.getParameter("content");
		
		String path = Framework.instance().getAbsolutePath("/xpt/root/" + pathname);
		File file = new File(path);
		File dir  = file.getParentFile();
		if( dir != null && !dir.exists() )
			dir.mkdirs();
		OutputStreamWriter outputWriter = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
		outputWriter.write(content);
		outputWriter.close();
	}
	catch(Exception e){
		result = "{status:'error', message:'" + e.getMessage() + "'}";
	}
%>
<%=result%>

