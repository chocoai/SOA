<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"  import="java.util.*,org.json.*"%>
<%
	String url  = request.getParameter("url");
	String name = request.getParameter("name");
	String param= request.getParameter("param");
	JSONObject args = new JSONObject();
	try{
		if( param != null && param.length() > 0 ){
			args = new JSONObject(param);
		}
	}
	catch(Exception e){
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<script type='text/javascript' src='../js/jscl/core.js'></script>
<script type='text/javascript' src='../js/qs.js'></script>
<script type='text/javascript' src='../js/share/json.js'></script>
<script type='text/javascript' src='../js/share/util.js'></script>
<script type='text/javascript' src='../js/share/handlebars.js'></script>
<script type='text/javascript' src='../js/share/jquery.js'></script>
<style type="text/css">
<% if( args.optBoolean("scroll") != true){ %>
body {
    overflow:hidden;
}
<% } %>
</style>
</head>

<body style="border-style:none;padding:0;margin:0;" >
	<jsp:include page="<%=url%>" />
	<div id="container" style="width:100%;height:100%">
	</div>
</body>
<script type="text/javascript">
	var control = new <%=name%>();

	$( document ).ready( function(){
		var options;
		try{
			QS.initExternalRuntime();
			options = invoke_runtime(QS.runtime, "options");
			if( options ){
				control.options(options);
			}
		}catch (e) {
		}
		control.render($("#container"), options);
		control.setValue(invoke_runtime(QS.runtime, "getValue"));
	});
</script>
</html>
