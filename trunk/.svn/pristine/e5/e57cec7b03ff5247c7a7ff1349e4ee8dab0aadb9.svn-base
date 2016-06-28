<?xml version="1.0" encoding="utf-8" ?>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="ime.core.Environment"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>在线客服</title>
<script type='text/javascript' src='../../js/share/util.js'></script>
<style type="text/css">
body { 
	margin  : 0px; 
	overflow: hidden;
	font    : Icon;
}
</style>
<script language="JavaScript" type="text/javascript">

var baseUrl = $base_url("util.js");

$import(baseUrl, "/js/share/AC_OETags.js");
$import(baseUrl, "/dwr/engine.js");
$import(baseUrl, "/dwr/util.js");
$import(baseUrl, "/js/share/FlexHelper.js");

<%
	String server  = Environment.instance().getSystemProperty("webcallcenter.openfire.server");
	String ocsName = request.getParameter("ocsid");
	String userName= request.getParameter("username");
	String domain  = request.getParameter("domain");
%>
function getConfig(){
	var config = {};
	config.server   = "<%=server%>";
	config.ocsName  = "<%=ocsName%>";
	config.userName = "<%=userName%>";
	config.domain   = "<%=domain%>";
	return config;
}

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
var requiredMajorVersion = 9;
// Minor version of Flash required
var requiredMinorVersion = 0;
// Minor version of Flash required
var requiredRevision = 124;

// Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
var hasProductInstall = DetectFlashVer(6, 0, 65);

// Version check based upon the values defined in globals
var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);

var wmode = (isIE == true) ? "window" : "window";

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
		"id", "playerProductInstall",
		"quality", "high",
		"bgcolor", "#FFFFFF",
		"name", "playerProductInstall",
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
		"src", "WebMessenger",
		"width", "100%",
		"menu", "false",
		"height", "100%",
		"align", "middle",
		"id", "WebMessenger",
		"wmode", wmode,
		"quality", "high",
		"bgcolor", "#FFFFFF",
		"name", "WebMessenger",
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
FlexHelper.init("WebMessenger", "FlexContent");
window.onbeforeunload = function(){
	var webMessenger = $("WebMessenger");
	if( webMessenger != null && webMessenger.onClose != null )
		webMessenger.onClose();
}
</script>
</html>
