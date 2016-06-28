<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"  import="java.util.*"%>
<%
	String controlUrl = request.getParameter("url");
	String control = request.getParameter("control");
	String param= request.getParameter("param");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Desktop</title>
<script type='text/javascript' src='../js/share/util.js'></script>
<script type='text/javascript' src='../js/share/handlebars.js'></script>
<script type='text/javascript' src='../js/share/jquery.js'></script>
<script type='text/javascript' src='../js/jscl/core.js'></script>
<script type='text/javascript' src='../js/qs.js'></script>
</head>
<script type="text/javascript">
	var param = {};
	param = <%=param%>;
</script>
<body scroll="no" style="border-style:none" >
	<INPUT TYPE="button" VALUE="getValue" ONCLICK="getValue()" />
	<INPUT TYPE="button" VALUE="setValue" ONCLICK="setValue()" />
	<jsp:include page="<%=controlUrl%>" />
	<div id="container" style="width:100px">
	</div>
</body>
<script type="text/javascript">
	var control = new <%=control%>();
	control.render($("#container"), param);

	function getValue(){
		alert(control.getValue());
	}
	function setValue(){
		control.setValue("hello world");
	}
</script>
</html>
