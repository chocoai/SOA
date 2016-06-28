var __base_url__ = "";

function $base_url(refer){
	if( !refer )
		return __base_url__;

	var i, base = "", src = refer, scripts = document.getElementsByTagName("script");
	for (i=0; i<scripts.length; i++){
		if (scripts[i].src.match(src)){ 
			base = scripts[i].src.replace(src, "");
			break;
		}
	}
	__base_url__ = base.replace("//", "/");
	return base;
}

function $import(baseUrl, path, type){
	if( path.substring(path.length - 3) == "css" )
		document.write("<" + "link href=\"" + baseUrl + path + "\" rel=\"stylesheet\"></" + "link>");
	else
		document.write("<" + "script src=\"" + baseUrl + path + "\"></" + "script>");
}

var baseUrl = $base_url("loader.js");
var rootUrl = "";
(function() {
	var pathname = window.location.pathname;
	var index = pathname.indexOf("/applications", 0);
	if( index != -1 ){
		var path = pathname.substring(0, index);
		if( path.length > 0 && path.charAt(0) != '/' )
			path = "/" + path;
		rootUrl = path + "/";
	}
})();

$import(baseUrl, "css/default/style.css");
$import(baseUrl, "css/default/page.css");
$import(baseUrl, "css/default/list.css");
$import(baseUrl, "css/default/button.css");
$import(baseUrl, "css/default/swapview.css");
$import(baseUrl, "css/default/tabpage.css");
$import(baseUrl, "css/default/waitingbox.css");
$import(baseUrl, "css/default/pagingview.css");
$import(baseUrl, "css/default/navboard.css");
$import(baseUrl, "css/default/searchbox.css");
$import(baseUrl, "css/default/switchbox.css");
$import(baseUrl, "css/default/popup.css");

$import(baseUrl, "../core/oop.js");
$import(baseUrl, "../core/qs.js");
$import(baseUrl, "../core/qs.events.js");
$import(baseUrl, "../core/util.js");
$import(baseUrl, "../core/qs.collections.js");
$import(baseUrl, "../core/qs.xml.js");
$import(baseUrl, "../core/qs.timer.js");
$import(baseUrl, "../core/qs.storage.js");
$import(baseUrl, "../core/qs.i18n.js");

$import(baseUrl, "../utils/StringUtil.js");
$import(baseUrl, "../wos/crypt/Base64.js");
$import(baseUrl, "../wos/crypt/MD5.js");

$import(baseUrl, "../layout/qs.layout.js");
$import(baseUrl, "../layout/qs.layout.flow.js");
$import(baseUrl, "../layout/qs.layout.grid.js");
$import(baseUrl, "../layout/qs.layout.horizontal.js");
$import(baseUrl, "../layout/qs.layout.vertical.js");

$import(baseUrl, "../ui/qs.ui.core.js");
$import(baseUrl, "../ui/qs.ui.animation.js");
$import(baseUrl, "../ui/qs.ui.control.js");
$import(baseUrl, "../ui/qs.ui.button.js");
$import(baseUrl, "../ui/qs.ui.form.js");
$import(baseUrl, "../ui/qs.ui.textinput.js");
$import(baseUrl, "../ui/qs.ui.itemscontrol.js");
$import(baseUrl, "../ui/qs.ui.container.js");
$import(baseUrl, "../ui/qs.ui.popup.js");

$import(baseUrl, "ui/qs.m.core.js");
$import(baseUrl, "ui/qs.m.waitingbox.js");
$import(baseUrl, "ui/qs.m.app.js");
$import(baseUrl, "ui/qs.m.page.js");
$import(baseUrl, "ui/qs.m.header.js");
$import(baseUrl, "ui/qs.m.button.js");
$import(baseUrl, "ui/qs.m.scrollable.js");
$import(baseUrl, "ui/qs.m.swapview.js");
$import(baseUrl, "ui/qs.m.list.js");
$import(baseUrl, "ui/qs.m.tabpage.js");
$import(baseUrl, "ui/qs.m.html.js");
$import(baseUrl, "ui/qs.m.pagingview.js");
$import(baseUrl, "ui/qs.m.navboard.js");
$import(baseUrl, "ui/qs.m.searchbox.js");
$import(baseUrl, "ui/qs.m.switchbox.js");
$import(baseUrl, "ui/qs.m.popupview.js");

$import(baseUrl, "../ui/qs.ui.qxml.js");

$import(baseUrl, "../i18n/zh_CN.js");
