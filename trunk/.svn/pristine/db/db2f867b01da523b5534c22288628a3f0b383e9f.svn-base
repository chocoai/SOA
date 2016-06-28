<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" import="ime.security.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="css/login.css">
<script type='text/javascript' src='js/share/util.js'></script>
<script type='text/javascript' src='js/share/config.js'></script>
<title>登入</title>
</head>
<script>
var cookie = {};
function initCookie(){
	var aCookie = document.cookie.split("; ");
	for (var i=0; i < aCookie.length; i++)
	{
		var aCrumb = aCookie[i].split("=");
		cookie[aCrumb[0]] = unescape(aCrumb[1]);
	}
}
function getCookie(name){
	return cookie[name];
}

function setCookie(name, value){
	cookie[name] = value;
	var argv = arguments;
	var argc = arguments.length;
	var path    = (2 < argc) ? argv[2] : null;
	var expires = (3 < argc) ? argv[3] : null;
	var domain  = (4 < argc) ? argv[4] : null;
	var secure  = (5 < argc) ? argv[5] : false;
	
	if( expires == null )
		expires = new Date(3000, 1, 1);

	document.cookie = name + "=" + escape (value) +
		((expires == null) ? "" : ("; expires=" + expires.toGMTString())) +
		((path    == null) ? "" : ("; path=" + path)) +
		((domain  == null) ? "" : ("; domain=" + domain)) +
		((secure  == true) ? "; secure" : "");
}
function deleteCookie(name){
	document.cookie = name + "=null; expires=Fri, 31 Dec 1099 23:59:59 GMT;";
	cookie[name] = null;
}
initCookie();

function checkLoginForm(){
	if( $("loginName").value == "" ){
		alert("请输入用户名");
		return false;
	}
	if( $("password").value == "" ){
		alert("请输入密码");
		return false;
	}
	if( $("verifycode").value == "" ){
		alert("请输入验证码");
		return false;
	}
	if( $("savePassword").checked == true ){
		setCookie("loginName", $("loginName").value);
		setCookie("password", $("password").value);
		setCookie("domainName", $("domainName").value);
		setCookie("save", "true");
	}
	else
		deleteCookie("save");
	return true;
}

</script>
<%
	String redir = request.getParameter("redir");
	if (redir == null)
		redir = "";

	LoginSession loginSession = new LoginSession(request, response);

	String loginName = request.getParameter("loginName");
	String password = request.getParameter("password");
	String verifycode = request.getParameter("verifycode");
	String domain = request.getParameter("domainName");
	boolean isLogin = true;

	if (loginName != null && password != null)
		isLogin = true;
	else
		isLogin = false;

	boolean isLogined = false;
	String errorMsg = null;
	if (isLogin == true) {

		String[] part = loginName.split("@");
		if (part.length == 2) {
			loginName = part[0];
			domain = part[1];
		}
		if (verifycode == null
				|| !verifycode.equals(session
						.getAttribute("verifycode"))) {
			errorMsg = "验证码错误！";
		} else {
			try {
				loginSession.login(loginName, password, domain);
				isLogined = true;
			} catch (Exception e) {
				errorMsg = e.getMessage();
			}
		}
	}
%>
<body>
<%
	if (!isLogined) {
%>
<%
	if (errorMsg != null) {
%>
<script type="text/javascript">
	alert("<%=errorMsg%>");
</script>
<%
	}
%>
<form action="login.jsp" method="post" align="center" id="form"
	name="form">
	<input type="hidden" name="redir" value="<%=redir%>" />
<table width="100%" height="100%" border="0" cellpadding="0"
	cellspacing="0">
	<tr>
		<td height="786" width="100%">
		<table width="516" border="0" align="center" cellpadding="0"
			cellspacing="0">
			<tr>
				<td colspan="2"><img src="images/logintop.jpg" width="523"
					height="82" /></td>
			</tr>
			<tr>
				<td width="160"><img src="images/loginleft.jpg" width="160"
					height="256" /></td>
				<td width="363" background="images/loginright.jpg">
				<table width="315" border="0" align="center" cellpadding="0"
					cellspacing="0">
					<tr>
						<td width="71" height="35">企业域名：<br />
						</td>
						<td width="244" height="35"><label> <select
							name="domainName" id="domainName"
							style="border: 1px solid #8EB5DC;width:150px">
							<script type="text/javascript">
							var domainName = document.getElementById("domainName"); 
							var label = '';
							var value = '';
							if(config.domain.limit != undefined){
								label = '';
								value = config.domain.limit;
								domainName.options.add(new Option(label,value));
							}else{
								var showlist = config.domain.showList;
								for(var index = 0; index < showlist.length; index++){
									if(typeof(showlist[index])=="string"){
										label = showlist[index];
										value = showlist[index];
										domainName.options.add(new Option(label,value));
									}
									if(typeof(showlist[index])=="object"){
										label = showlist[index].label;
										value = showlist[index].value;
										domainName.options.add(new Option(label,value));
									}
								}
							}
							</script>
						</select> </label></td>
					</tr>
					<tr>
						<td height="35">登录用户：</td>
						<td height="35"><input type="text" id="loginName"
							name="loginName" style="border: 1px solid #8EB5DC; width: 148px"
							size="25" /></td>
					</tr>
					<tr>
						<td height="35">用户密码：</td>
						<td height="35"><input type="password" id="password"
							name="password" style="border: 1px solid #8EB5DC; width: 148px"
							size="25" /></td>
					</tr>
					<tr>
						<td height="35">验证码：</td>
						<td height="35"><input name="verifycode" type="text"
							id="verifycode" style="border: 1px solid #8EB5DC" size="4" class="box"/>
							<img src="verifycode.jsp" align="absmiddle" ></img></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td height="20"><span id="sprycheckbox1"> <label>
						<input type="checkbox" name="savePassword" id="savePassword"
							checked="checked" /> </label> </span>保存密码</td>
					</tr>
					<tr>
						<td height="35">&nbsp;</td>
						<td height="35"><input type="image" name="imageField"
							id="imageField" src="images/login.jpg" onclick="javascript: return checkLoginForm()"/>  
						  <a href="#" onclick="document.form.reset();"> <img src="images/reset.jpg" border="0"/></a></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2"><img src="images/loginbottom.jpg" width="523"
					height="14" /></td>
			</tr>
		</table>
		<table width="531" border="0" align="center" cellpadding="0"
			cellspacing="0">
			<tr>
				<td width="531" height="82" align="center"
					background="images/logincoryright.jpg" class="white">Copyright
				&copy;2009-2010 宁波金奥软件科技有限公司 版权所有</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>
<script>
var save = getCookie("save");
if( save == "true" ){
	$("savePassword").checked = true;
	$("domainName").value   = getCookie("domainName");
	$("loginName").value    = getCookie("loginName");
	$("password").value     = getCookie("password");
}

</script>
<%
	} else {
		String url = "./applications/webui/index.jsp";
		if( redir != null && redir.length() > 0 )
			url = redir;
%>
<script>
	document.location.href = "<%=url%>";
</script>
<%
	}
%>
</body>
</html>