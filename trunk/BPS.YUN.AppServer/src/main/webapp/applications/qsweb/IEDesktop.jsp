<%@ page language="java" contentType="text/html; charset=utf8" pageEncoding="utf8" import="ime.security.*"%>
<%
	LoginSession.setHttpSession(session);
	if( !LoginSession.isLogined() ){
		response.sendRedirect("login.jsp");
		return;
	}
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=EDGE">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
<script type='text/javascript' src="lib/jquery-1.11.1.min.js"></script>
<script type='text/javascript' src="lib/json.js"></script>

<script type='text/javascript' src='loader.min.js'></script>
<script type='text/javascript' src='app/Header.js'></script>
<script type='text/javascript' src='app/qsweb.js'></script>

<script type='text/javascript'>
	$import(rootUrl, "js/share/config.js");
	$import(rootUrl, "dwr/engine.js");
	$import(rootUrl, "dwr/util.js");
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
		
		background-color:#6F90A1;
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
	.qs-app-header .qs-app-link{
		padding : 0px 10px 0 10px;
		margin  : 4px 2px 0 2px;
	}
	.qs-app-header .qs-app-link .border{
		top : 0;
	}
	.qs-app-header .selected.qs-app-link{
		background-color : white;
	}
	.qs-app-header .selected.qs-app-link .border{
		border : solid 1px #808080;
		top : 0;
	}
	
	.qs-calendar-popup {
		border: 1px solid #222222;
	}
	.app-content {
		border-top: 3px solid #8B8179;
		/*
		background: rgb(131, 100, 100);
		background: transparent;
		*/
	}
</style>
</head>
<script type="text/javascript">

function qsweb_config(){
	if( config == null )
		config = {};
	config.logined = true;
	config.rpc = "DWR";
	return config;
}
</script>
<body>
<div id="background">
	<img id="backgroundImage" />
</div>
</body>
<script type="text/javascript" src="lib/echarts.js"></script>
</html>