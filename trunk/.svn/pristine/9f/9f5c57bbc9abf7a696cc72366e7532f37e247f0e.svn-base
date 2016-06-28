<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"  import="java.util.*,org.json.*"%>
<%
	String url  = request.getParameter("url");
	String name = request.getParameter("name");
	String param= request.getParameter("param");
	String depends = request.getParameter("depends");
	
	String[] jsFiles = null;
	if( depends != null && depends.length() > 0 )
		jsFiles = depends.split(";");
	
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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<script type='text/javascript' src='../js/jscl/core.js'></script>
<script type='text/javascript' src='../js/qs.js'></script>
<script type='text/javascript' src='../js/share/json.js'></script>
<script type='text/javascript' src='../js/share/util.js'></script>
<script type='text/javascript' src='../js/share/jquery.js'></script>
<script type='text/javascript' src='../applications/qsweb/loader.js'></script>
<% if( jsFiles != null){
	for( int i = 0; i < jsFiles.length; i++ ){
%>
<script type='text/javascript' src='<%=jsFiles[i]%>'></script>
<% }} %>

<script type='text/javascript' src='<%=url%>'></script>

<style type="text/css">
<% if( args.optBoolean("scroll") != true){ %>
body {
    overflow:hidden;
}
<% } %>
</style>
</head>

<body style="border-style:none;padding:0;margin:0;" >
	<div id="container" style="width:100%;height:100%">
	</div>
</body>
<script type="text/javascript">
	var control = js.newInstance("<%=name%>");
	control.addEventListener("change", function(){
		invoke_runtime(QS.runtime, "setValue", control.value());
	}, this);
	
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
		control.value(invoke_runtime(QS.runtime, "getValue"));
	});
</script>
</html>
