<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" import="ime.security.*"%>
<%
	String redir = request.getParameter("redir");
	if (redir == null)
		redir = "";
	boolean newWindow = false;
	String nw = request.getParameter("nw");
	if( "true".equals(nw) ){
		newWindow = true;
		nw = "&amp;nw=true";
	}
	else
		nw = "";
	
	LoginSession loginSession = new LoginSession(request, response);
	
	String loginName = request.getParameter("txtLoginId");
	String password = request.getParameter("txtPassword");
	String verifycode = request.getParameter("txtVerified");
	String domain = request.getParameter("cboDomain");
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
		if (verifycode == null || !verifycode.equals(session.getAttribute("verifycode"))) {
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
<!doctype html>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=EDGE">
	<meta charset="utf-8">
	<title>快程业务构建平台</title>
	<script type='text/javascript' src="lib/jquery-1.11.1.min.js"></script>
	<script type='text/javascript' src="lib/json.js"></script>
	<script type='text/javascript' src='loader.min.js'></script>
	<script>
		//检测浏览器版本
		var matched, browser;

		function uaMatch( ua ) {
			ua = ua.toLowerCase();

			var match = /(chrome)[ \/]([\w.]+)/.exec( ua ) ||
				/(webkit)[ \/]([\w.]+)/.exec( ua ) ||
				/(opera)(?:.*version|)[ \/]([\w.]+)/.exec( ua ) ||
				/(msie) ([\w.]+)/.exec( ua ) ||
				ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec( ua ) ||
				[];

			return {
				browser: match[ 1 ] || "",
				version: match[ 2 ] || "0"
			};
		};
		function getInternetExplorerVersion(){
		  var rv = -1;
		  if (navigator.appName == 'Microsoft Internet Explorer'){
			var ua = navigator.userAgent;
			var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
			if (re.exec(ua) != null)
			  rv = parseFloat( RegExp.$1 );
		  }
		  else if (navigator.appName == 'Netscape'){
			var ua = navigator.userAgent;
			var re  = new RegExp("Trident/.*rv:([0-9]{1,}[\.0-9]{0,})");
			if (re.exec(ua) != null)
			  rv = parseFloat( RegExp.$1 );
		  }
		  return rv;
		}

		matched = uaMatch( navigator.userAgent );
		browser = {};

		if ( matched.browser ) {
			browser[ matched.browser ] = true;
			browser.version = matched.version;
		}
		//在IE11中通过uaMatch会检测到错误的内容(matched.browser = mozilla),通过以下方法进行纠正
		var ieVersion = getInternetExplorerVersion();
		if( ieVersion > 0 ){
			browser.mozilla = false;
			browser.msie = true;
			browser.version = ieVersion;
		}
		// Chrome is Webkit, but Webkit is also Safari.
		if ( browser.chrome ) {
			browser.webkit = true;
		} else if ( browser.webkit ) {
			browser.safari = true;
		}

		if( browser.msie && browser.version < 8 )
			window.location.href = "browsers.html";
	</script>
	<script type="text/javascript">
	var Storage = {};
	Storage.cookie = new Object();
	Storage.isNull = function(val){
		if (val == undefined || val == null || (val.replace(/\s/g, "") == "") || val == "null") 
			return true;
		else
			return false;
	}
	/**
	 * 初始化cookie
	 */
	Storage.initCookie = function(){
		var aCookie = document.cookie.split("; ");
		for (var i=0; i < aCookie.length; i++)
		{
			var aCrumb = aCookie[i].split("=");
			if( Storage.cookie[aCrumb[0]] == null || Storage.cookie[aCrumb[0]] == undefined ){
				var val = unescape(aCrumb[1]);
				val = Storage.isNull(val) ? '' : val;
				Storage.cookie[aCrumb[0]] = val;
				if( window.localStorage )
					window.localStorage[aCrumb[0]] = val;
			}
		}
	}
	Storage.get = function(name){
		var val;
		if( window.localStorage )
			val = window.localStorage[name];
		if(!val && val != "null")
			val = Storage.cookie[name];
		return val;
	}
	Storage.set = function(name, value){
		value = Storage.isNull(value) ? '' : value;
		if( window.localStorage )
			window.localStorage[name] = value;
		Storage.cookie[name] = value;
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
	/**
	* 删除cookie
	* @param name cookie名
	*/
	Storage.del = function(name){
		if( window.localStorage )
			delete window.localStorage[name];
		document.cookie = name + "=null; expires=Fri, 31 Dec 1099 23:59:59 GMT;";
		delete Storage.cookie[name];
	}
	Storage.initCookie();
	</script>
	<style>
		body{
			user-select: none;
			
			overflow : hidden;
			position: fixed!important;
			padding : 0;
			margin : 0;
			
			height:100%;
			width:100%;
		}
		.background{
			height:100%;
			width:100%;
			background-color:#6F90A1;
			background-image:linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
			background-image:-moz-linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
			background-image:-o-linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
			background-image:-ms-linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
			background-image:-webkit-linear-gradient(#6F90A1,#9FBAC9, #6F90A1);
		}
		qxml{
			display:none;
		}
	</style>
</head>
<body>
	<div class="background">
	</div>
<% if( newWindow == true || !isLogined ){ %>
<script id="QXML_LoginPanel" type="text/qxml">
	<Panel xmlns:app="qs.app.controls" title="登入">
		<Form name="form" padding="5" action="login.jsp?act=login<%=nw%>">
			<GridLayout columns="[128, 'auto', '*']" rows="[25, 25, 25, 25, 25, 25]" hGap="3" vGap="3" innerAlign="{hAlign:'left', vAlign:'middle'}">
				<Image src="images/login.png" layout="{rowSpan:5}" />
				<Label text="登入区域:" layout="{colIndex:1}" />
				<Label text="用户帐户:" layout="{colIndex:1, rowIndex:1}" />
				<Label text="用户密码:" layout="{colIndex:1, rowIndex:2}" />
				<Label text="验证码:" layout="{colIndex:1, rowIndex:4}" />
				<ComboBox name="cboDomain" editable="true" layout="{colIndex:2}" />
				<TextInput name="txtLoginId" layout="{colIndex:2, rowIndex:1}" />
				<TextInput name="txtPassword" password="true" layout="{colIndex:2, rowIndex:2}" />
				<TextInput name="txtVerified" layout="{colIndex:2, rowIndex:4, hAlign:'left', width:'49%'}" validator="{required:true, showTag:false, message:'请输入验证码'}"/>
				<Image src="../../verifycode.jsp" layout="{colIndex:2, rowIndex:4, hAlign:'right', width:'49%'}" />
				<CheckBox name="chkSave" text="保存密码" layout="{colIndex:3, rowIndex:3, width:'auto'}" />
				<Button text="确定" options="{isDefault:true, icon:'images/icons/ok.png'}" layout="{colIndex:2, rowIndex:5, width:70, height:25, hAlign:'right'}" click="onLogin" />
			</GridLayout>
		</Form>
	</Panel>
</script>
<script type="text/javascript">
var domainsDataProvider = [];
var LoginPanel = js.Class("LoginPanel", qs.ui.Panel, function(){
	this.initialize = function(){
		this.$super();
		this.qxml($("#QXML_LoginPanel").html());
		
		this.width(400);
		var size = this.outerSize();
		var x = ($(window).width() - size.width) / 2;
		var y = ($(window).height() - size.height) / 3;
		this.left(x);
		this.top(y);

		var domain = '<%=domain == null ? "" : domain%>';
		var loginName = '<%=loginName == null ? "" : loginName%>';
		var password = '<%=password == null ? "" : password%>';
		/******构建域数组*****/
		var last_login = Storage.get("the_last_login");
		if(Storage.isNull(last_login)){
			last_login = {};
			last_login.domain = '';
			last_login.loginName = '';
			last_login.password = '';
			last_login.save = false;
		}else{
			last_login = JSON.parse(last_login);
		}
		
		var domains = Storage.get("domainsDataProvider");
		if(Storage.isNull(domains)){
			domains = [];
		}else{
			domains = JSON.parse(domains);
		}
		var selectedItem = null;
		for(var i = 0; i < domains.length; i++){
			var d = domains[i];
			var item = {};
			item.label = d.domain;
			item.value = d.domain;
			item.domain = d.domain;
			item.loginName = d.loginName;
			item.password = d.password;

			if(d.domain == domain)
				selectedItem = item;
			domainsDataProvider.push(item);
		}
		
		this.cboDomain.dataProvider(domainsDataProvider);
		this.cboDomain.addEventListener("change", this.onComboBoxChange, this);
		/*******************/

		if(last_login.save == true){
			this.cboDomain.value(last_login.domain);
			this.txtLoginId.value(last_login.loginName);
			this.txtPassword.value(last_login.password);
			this.chkSave.value(true);
		}else{
			var save = Storage.get("save");
			if( save == "true" ){
				this.cboDomain.value(last_login.domain);
				this.txtLoginId.value(last_login.loginName);
				this.txtPassword.value(last_login.password);
				this.chkSave.value(true);
			}
		}
		
		this.bindEvent("keypress", function(evt){
			if( evt.which == 13 ){
				this.onLogin();
			}
		});
	}

	/**
	 * 当ComboBox中选择的值发生变化时触发的事件
	 */
	this.onComboBoxChange = function(event){
		var val = this.cboDomain.value();
		if(Storage.isNull(val))
			return;

		for(var i = 0; i < domainsDataProvider.length; i++){
			var d = domainsDataProvider[i];
			if(d.domain == val){
				this.txtLoginId.value(d.loginName);
				this.txtPassword.value(d.password);
				break;
			}
		}
	}
	
	this.onLogin = function(event){
		if( !this.form.validate() )
			return;
		var the_last_login = {};
		the_last_login.domain = this.cboDomain.text();
		the_last_login.loginName = this.txtLoginId.value();
		the_last_login.password = this.txtPassword.value();
		the_last_login.save = this.chkSave.value();
		Storage.set("the_last_login", JSON.stringify(the_last_login));
		
		if( this.chkSave.value() == true ){
			Storage.set("save", "true");
		}else
			Storage.del("save");
		if( browser.msie && browser.version == 8 ){
			this.form.action("login.jsp?act=login&redir=IEDesktop.jsp");
		}
		this.form.submit();
	};
});

var panel = new LoginPanel();
$(window).resize(function() {
	var size = panel.outerSize();
	var x = ($(window).width() - size.width) / 2;
	var y = ($(window).height() - size.height) / 3;
	panel.left(x);
	panel.top(y);
});

</script>
<%} %>
<%
	if (!isLogined) {
%>
<%
	if (errorMsg != null) {
%>
<script type="text/javascript">
$(document).ready(function(){
	qs.ui.MessageBox.show("<%=errorMsg%>", "错误", qs.ui.MessageBox.Error);
});
</script>
<%
	}
%>
<%
	} else {
		String version = "20141222";
		String url = "Desktop.jsp";
		if( redir != null && redir.length() > 0 )
			url = redir;
		if( url.indexOf("?") == -1 )
			url += "?ver=" + version;
		else
			url += "&ver=" + version;
%>
<script>
	/******存放域数组*****/
	var domain = '<%=domain == null ? "" : domain%>';
	var loginName = '<%=loginName == null ? "" : loginName%>';
	var password = '<%=password == null ? "" : password%>';
	var domains = Storage.get("domainsDataProvider");
	if(Storage.isNull(domains)){
		domains = [];
	}else{
		domains = JSON.parse(domains);
	}
	if(Storage.isNull(domain))
		domain = "";
	if(!Storage.isNull(domain)){
		var isExist = false;
		for(var i = 0; i < domains.length; i++){
			var d = domains[i];
			if(d.domain == domain){
				isExist = true;
				d.label = domain;
				d.value = domain;
				d.domain = domain;
				d.loginName = loginName;
				d.password = password;
				break;
			}
		}
		if(!isExist){
			var d = {};
			d.label = domain;
			d.value = domain;
			d.domain = domain;
			d.loginName = loginName;
			d.password = password;
			domains.push(d);
		}
	}
	Storage.set("domainsDataProvider", JSON.stringify(domains));
	/*******************/
	
	function openFullscreen(url){
		// get the height correction for IE and set the window height and width
		var height = screen.availHeight;
		var width = screen.availWidth;

		var fullscreen = (document.all) ? "no" : "yes";
		var resizable = "no";
		var toolbar = "no";
		var status = "no";
		var left = 0;
		var top = 0;

		//set window properties
		props = "toolbar=no" +
		",fullscreen=" + fullscreen +
		",status=no" +
		",resizable=no" +
		",scrollbars=no" +
		",menubar=no" +
		",location=no" + ",";

		dims = "width="+ width +
		",height="+ height +
		",left="+ left +
		",top=" + top;

		var win = window.open("", name, props + dims);
		win.resizeTo(width, height);
		win.location.href = url;
		win.focus();
	}
	var url = "<%=url%>";
	var newWindow = <%=newWindow%>;
	if( newWindow ){
		window.logout = {close : true};
		openFullscreen(url);
	}
	else
		document.location.href = url;
</script>
<%
}
%>
</body>
</html>

