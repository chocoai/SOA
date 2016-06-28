<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="ime.security.*,org.json.*"%>
<%
	LoginSession.setHttpSession(session);
	if( !LoginSession.isLogined() ){
		response.sendRedirect("login.jsp");
		return;
	}
	String initALink = request.getParameter("alink");
	if( initALink == null )
		initALink = "";
	initALink = JSONObject.quote(initALink);
	String userAgent = request.getHeader("User-Agent");
	if( userAgent == null )
		userAgent = "";
	String loader = "loader.min.js";
	if( "true".equals(request.getParameter("debug")) )
		loader = "loader.js";
	String version = request.getParameter("ver");
	if( version == null )
		version = "";
%>
<!doctype html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>快程业务构建平台</title>
<link rel="stylesheet" type="text/css" href="app/css/mathquill.css">

<script type='text/javascript' src="lib/jquery-2.1.1.min.js"></script>
<script type='text/javascript' src="lib/json.js"></script>
<script type='text/javascript'>
var $import_version = "<%=version%>";
</script>
<script type='text/javascript' src='<%=loader%>'></script>
<script type='text/javascript' src='app/Header.js'></script>
<script type='text/javascript' src='app/qsweb.js'></script>

<script type='text/javascript'>
	$import(rootUrl, "js/share/config.js");
	$import(rootUrl, "js/share/ScriptProxy.js");
	$import(rootUrl, "js/share/FlexHelper.js");
	$(document).ready(function(){
		FlexHelper.baseUrl = $base_url("FlexHelper.js");
	});
</script>

<style>
	body{
		user-select: none;
		
		overflow : hidden;
		position: fixed!important;
		padding : 0;
		margin : 0;
		
		height:100%;
		width :100%;
	}
	#background{
		position: absolute;
		height:100%;
		width :100%;
		
		background-image:-moz-linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
		background-image:-o-linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
		background-image:-ms-linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
		background-image:-webkit-linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
		
		background-image: linear-gradient(30deg, rgba(168, 104, 72, 0.4), rgba(73, 32, 32, 0.65));
	}
	.qs-logo{
		width: 260px;
		
		background: gray;
		background-image:url(images/logo.png);
	}
	.app-content {
		border-top: 1px solid rgba(89, 69, 53, 0.84);
		/*
		background: rgb(131, 100, 100);
		background: transparent;
		*/
	}
<% if( userAgent.indexOf("BIDUBrowser") > 0 ){ %>
	.slick-resizable-handle{
		cursor: ew-resize;
	}
<%}%>
</style>
</head>
<script type="text/javascript">
wos.FileLoader.config({version:"<%=version%>"});
function qsweb_config(){
	if( config == null )
		config = {};
	config.logined = true;
	config.initALink = <%=initALink%>;
	
	/*
	config.logout = {
		url : "login.jsp",	//退出登入后的跳转地址,相对于Desktop.jsp
		close : false		//退出登入后是否关闭窗口,url和close只能选择一个,url优先级高于close
	}
	config.header = {
		minimizable : true,					//是否允许最小化
		logo        : "images/logo.png",	//LOGO URL
		minLogo     : "images/min-logo.png"	//最小化后的LOGO URL		
	}
	*/
	return config;
}
</script>
<body>
<div id="background">
	<img id="backgroundImage" />
</div>
<!--
QS扩展插件，可用于在线编辑Office文档，建议在Office的设置高级选项中把“允许后台保存”选项去掉，这样能实时保存在线文档
<object id="qs_ext" type="application/x-qsweb" width="0" height="0" style="position:absolute;left:0;top:0">
</object>
<script type='text/javascript'>
	$(document).ready(function(){
		window.qs_ext = document.getElementById('qs_ext');
	});
</script>
-->
</body>
</html>