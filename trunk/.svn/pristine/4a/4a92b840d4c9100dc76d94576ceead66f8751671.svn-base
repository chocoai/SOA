<%@ page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8" import="ime.security.*,ime.core.event.*,ime.document.*,java.util.*,java.io.*,com.oreilly.servlet.multipart.*"
%>
<%
	String ATTACHEMENT_PATH = "attachments";
	Map<String, String> fileMap = new HashMap<String, String>();
	try {
        com.oreilly.servlet.multipart.MultipartParser mp = new com.oreilly.servlet.multipart.MultipartParser(request, 100*1024*1024, false, false, "utf-8");
		LoginSession.setHttpSession(request.getSession());
		String rootPath = DocumentStorage.getAbsolutePath("", ATTACHEMENT_PATH);
		File rootDir = new File(rootPath);
		if( !rootDir.exists() )
			rootDir.mkdirs();
        
		com.oreilly.servlet.multipart.Part part = null;
        String path = null, repath = null;
        com.oreilly.servlet.multipart.FilePart filePart = null;
        String fileName = null, fileExt = null;
		StringBuilder sb = new StringBuilder();
		String fileuid = null;
		String deleteFile = null;
		
        while ((part = mp.readNextPart()) != null) {
			if (part.isFile()) {
				filePart = (com.oreilly.servlet.multipart.FilePart) part;
				fileName = filePart.getFileName();
				if (fileName != null) {
					fileExt = null;
					int index = fileName.lastIndexOf(".");
					if( index > 0 )
						fileExt = fileName.substring(index + 1);
					repath = DocumentStorage.getFilePath(fileName);
					sb.setLength(0);
				    sb.append(rootPath).append(repath);
				    path = sb.toString();
				    File dir = new File(path);
				    if( !dir.exists() )
				    	dir.mkdirs();
				    sb.setLength(0);
				    sb.append(Long.toHexString(System.currentTimeMillis()))
					  .append(Long.toHexString(Math.round(Math.random() * 100)));
				    fileuid = sb.toString();
				    sb.setLength(0);
				    sb.append(path).append("/").append(fileuid);
				    if( fileExt != null )
				    	sb.append(".").append(fileExt);
				    FileOutputStream output = new FileOutputStream(sb.toString());
				    filePart.writeTo(output);
				    output.close();
				    EventManager.instance().fireEvent(new Event(EventType.FILE_EVENT, new File(sb.toString()).getAbsolutePath(), "create"));
				    sb.setLength(0);
			        sb.append(repath).append("/").append(fileuid);
			        if( fileExt != null )
				    	sb.append(".").append(fileExt);
			        fileMap.put(fileName, sb.toString());
				}
			}
			else if( part.isParam() ){
				String name, value;
				name  = ((com.oreilly.servlet.multipart.ParamPart)part).getName();
				value = ((com.oreilly.servlet.multipart.ParamPart)part).getStringValue();
				if( name.equals("deleteFile") )
					deleteFile = value;
			}
		}
        if( deleteFile != null && deleteFile.length() > 0 ){
			String[] deleteFiles = deleteFile.split(":");
			for( int i = 0; i < deleteFiles.length; i++ ){
				sb.setLength(0);
				sb.append(ATTACHEMENT_PATH).append("/").append(deleteFiles[i]);
				path = DocumentStorage.getAbsolutePath("", sb.toString());
				if( path != null ){
					File file = new File(path);
					file.delete();
					EventManager.instance().fireEvent(new Event(EventType.FILE_EVENT, file.getAbsolutePath(), "delete"));
				}
			}
        }
 	} catch (Exception e) {
 		response.getWriter().write(e.getMessage());
    }
%>
<%
	String method = request.getParameter("method");
	if( "iframe".equals(method)){
		response.setContentType("text/html; charset=utf-8");
%>
<html>
<head></head>
<body>
<%}%>
<uploaded>
<% 
	Map.Entry<String, String> entry;
	for(Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator(); iter.hasNext(); ){ 
		entry = iter.next();
%>
	<file name="<%=entry.getKey() %>" url="<%=entry.getValue() %>" />
<% } %>
</uploaded>
<%if( "iframe".equals(method)){ %>
</body>
</html>
<%}%>