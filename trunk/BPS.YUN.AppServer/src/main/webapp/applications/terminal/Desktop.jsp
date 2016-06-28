<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="ime.security.*"%>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Desktop</title>
<script type='text/javascript' src='../../js/share/util.js'></script>
<script type='text/javascript' src='../../js/share/json.js'></script>
<script type='text/javascript' src='../../js/share/config.js'></script>
<style type="text/css">
body { 
	margin  : 0px; 
	overflow: hidden;
	font    : Icon;
}
.trMenuBar{
	border : 1 solid black;
	height : 24px;
}
#menuBar{
	padding: 1px 24px 1px 24px;
	border : 1 solid black;
	z-index:100;
}
#Desktop{
	z-index:1;
}
</style>
<script language="JavaScript" type="text/javascript">

var baseUrl = $base_url("util.js");

$import(baseUrl, "js/share/AC_OETags.js");
$import(baseUrl, "dwr/engine.js");
$import(baseUrl, "dwr/util.js");
$import(baseUrl, "js/share/iDesktop.js");
$import(baseUrl, "js/share/FlexHelper.js");
$import(baseUrl, "js/share/ScriptProxy.js");

<%
	String loginId = null, domain = null;
	LoginSession.setHttpSession(session);
	if( LoginSession.isLogined() ){
		String fullLoginId = (String)LoginSession.getCurrentAttribute(LoginSession.FULL_LOGIN_ID);
		if( fullLoginId != null ){
			String[] part = fullLoginId.split("@");
			if( part.length == 2 ){
				loginId= part[0];
				domain = part[1];
			}
		}
	}
	if( loginId == null || domain == null ){
		response.sendRedirect("../../login.jsp");
		return;
	}
%>
var loginId= "<%=loginId%>";
var domain = "<%=domain%>";
function getDesktopConfig(){
	if( loginId != null && domain != null ){
		if( config == null )
			config = {};
		config.loginedUser = {};
		config.loginedUser.loginId= loginId;
		config.loginedUser.domain = domain;
	}
	if( config ){
		if( config.applicationTitle && config.applicationTitle.text && config.applicationTitle.text.length > 0 )
			document.title = config.applicationTitle.text;
		return config;
	}
	else
		return {};
}

self.moveTo(0,0);
self.resizeTo(screen.availWidth, screen.availHeight);
/*
if (top.frames.length != 0) 
	top.location = self.document.location; 
*/

</script>
</head>

<body scroll="no" style="border-style:none" >
<table style="width:100%;height:100%;border: 0;" cellspacing="0" cellpadding="0">
<tr><td id="FlexContent">
<script type="text/javascript" >

function onContextMenu(){
	return false;
}
document.oncontextmenu=onContextMenu; 

// Globals
// Major version of Flash required
var requiredMajorVersion = 10;
// Minor version of Flash required
var requiredMinorVersion = 0;
// Minor version of Flash required
var requiredRevision = 22;

// Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
var hasProductInstall = DetectFlashVer(6, 0, 65);

// Version check based upon the values defined in globals
var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);

//var wmode = "opaque";
var wmode = "window";

// Check to see if a player with Flash Product Install is available and the version does not meet the requirements for playback
if ( hasProductInstall && !hasRequestedVersion ) {
	// MMdoctitle is the stored document.title value used by the installation process to close the window that started the process
	// This is necessary in order to close browser windows that are still utilizing the older version of the player after installation has completed
	// DO NOT MODIFY THE FOLLOWING FOUR LINES
	// Location visited after installation is complete if installation is required
	var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
	var MMredirectURL = window.location;
    document.title = document.title.slice(0, 47) + " - Flash Player Installation";
    var MMdoctitle = document.title;

	AC_FL_RunContent(
		"src", "playerProductInstall",
		"FlashVars", "MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&MMdoctitle='+MMdoctitle+"",
		"width", "100%",
		"height", "100%",
		"align", "middle",
		"id", "Desktop",
		"quality", "high",
		"bgcolor", "#FFFFFF",
		"name", "Desktop",
		"allowScriptAccess","sameDomain",
		"allowFullScreen", "true",
		"type", "application/x-shockwave-flash",
		"pluginspage", "http://www.adobe.com/go/getflashplayer",
		"codebase", "/IMEServer/flash/swflash.cab"
	);
} else if (hasRequestedVersion) {
	// if we've detected an acceptable version
	// embed the Flash Content SWF when all tests are passed
	AC_FL_RunContent(
		"src", "Desktop",
		"width", "100%",
		"menu", "false",
		"height", "100%",
		"align", "middle",
		"id", "Desktop",
		"wmode", wmode,
		"quality", "high",
		"bgcolor", "#FFFFFF",
		"name", "Desktop",
		"allowScriptAccess","always",
		"allowFullScreen", "true",
		"type", "application/x-shockwave-flash",
		"pluginspage", "http://www.adobe.com/go/getflashplayer",
		"codebase", "/IMEServer/flash/swflash.cab"
	);
  } else {  // flash is too old or we can't detect the plugin
    var alternateContent = 'Alternate HTML content should be placed here. '
  	+ 'This content requires the Adobe Flash Player. '
   	+ '<a href=http://www.adobe.com/go/getflash/>Get Flash</a>';
    document.write(alternateContent);  // insert non-flash content
  }
</script>
</td></tr>
</table>
</body>
<script type="text/javascript">
	FlexHelper.init("Desktop", "FlexContent");
</script>
<script for="window" event="onunload">
	FlexHelper.close();
</script>
</html>
