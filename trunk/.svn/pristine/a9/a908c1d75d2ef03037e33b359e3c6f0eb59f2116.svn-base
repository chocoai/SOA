<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="java.io.*,ime.web.xpt.*,org.dom4j.*,org.dom4j.io.SAXReader,apf.framework.*,ime.security.*,ime.core.*,ime.core.entity.*,ime.document.*,ime.core.services.*"%>
<%
	XPTServlet.registerPart(request, response);

	String xml = request.getParameter("xptXml");
	SAXReader xreader = new SAXReader();
	org.dom4j.Document xmldoc = xreader.read(new StringReader(xml));
	
	XPTEngine.render(xmldoc, "/xpt", request, response);
%>



