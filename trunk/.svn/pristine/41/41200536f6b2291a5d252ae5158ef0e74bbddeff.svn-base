<%@ page language="java" contentType="application/octet-stream"
    pageEncoding="utf-8"
    import="ime.security.*,ime.document.*,java.util.*,java.io.*"
%>
<%
	String strFile = request.getParameter("file");
	String strName = request.getParameter("name");
	
	if( strFile.indexOf("..") != -1 )
		throw new Exception("非法的请求文件.");
	
	LoginSession loginSession = new LoginSession(request.getSession());
	if( !LoginSession.isLogined() )
		throw new Exception("请先登入.");
	
	String path = DocumentStorage.getAbsolutePath("", "attachments/" + strFile);
	
	BufferedInputStream filein = null;
	BufferedOutputStream outputs = null;
	try {
		File file = new File(path);
		if( strName == null || strName.length() == 0 )
			strName = file.getName();
		byte b[] = new byte[2048];
		int len = 0;
		filein = new BufferedInputStream(new FileInputStream(file));
		outputs = new BufferedOutputStream(response.getOutputStream());
		response.setHeader("Content-Length", "" + file.length());
		response.setContentType("application/force-download");
		response.setHeader("Content-Disposition", "attachment; filename=" + strName);
		response.setHeader("Content-Transfer-Encoding", "binary");
		while ((len = filein.read(b)) > 0) {
			outputs.write(b, 0, len);
			outputs.flush();
		}
		filein.close();
	}
	catch(Exception e){
		
	}
%>
