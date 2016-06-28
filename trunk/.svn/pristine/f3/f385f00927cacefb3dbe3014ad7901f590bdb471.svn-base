<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.net.*, java.io.*,ime.security.*,ime.core.*,ime.core.entity.*,ime.document.*,ime.core.services.*"%>
<%
	LoginSession loginSession = new LoginSession(request.getSession());
	Domain mainDomain = Environment.instance().getDomain(Reserved.MAIN_DOMAIN_ID);
	Domain domain = Environment.instance().getDomain(loginSession.getDomainId().longValue());
	
	String rootPath, path;
	
	rootPath = DocumentStorage.getAbsolutePath(mainDomain.getStorageName(), DictionaryService.DICTIONARY_FILE);
	path 	 = DocumentStorage.getAbsolutePath(domain.getStorageName(), DictionaryService.DICTIONARY_FILE);
	File file = new File(path);
	File rootFile = new File(rootPath);
	
	if( !file.exists() || file.lastModified() < rootFile.lastModified() ){
		DictionaryService service = new DictionaryService();
		service.generateDomainXml(domain.getId());
	}
	
	path = DocumentStorage.getWebPath(URLEncoder.encode(domain.getStorageName(), "utf-8"), DictionaryService.DICTIONARY_FILE);
	//response.sendRedirect(path);
	request.getRequestDispatcher(path).forward(request, response);
%>

