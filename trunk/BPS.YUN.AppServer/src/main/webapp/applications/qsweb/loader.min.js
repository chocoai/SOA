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
	var index = base.indexOf("js");
	if( index != -1 )
		base = base.substring(0, index);
	__base_url__ = base.replace("//", "/");
	return base;
}

function $import(baseUrl, path, type){
	var version = "";
	if( window.$import_version )
		version = "?ver=" + $import_version;
	
	if( path.substring(path.length - 3) == "css" )
		document.write("<" + "link href=\"" + baseUrl + path + version + "\" rel=\"stylesheet\"></" + "link>");
	else
		document.write("<" + "script src=\"" + baseUrl + path + version + "\"></" + "script>");
}

var baseUrl = $base_url("loader.min.js");
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


$import(baseUrl, "lib/jquery.event.drag-2.2.js");
$import(baseUrl, "lib/jquery-ui-1.10.3.custom.min.js");
$import(baseUrl, "lib/jquery.jsonpath.js");
$import(baseUrl, "lib/slick.core.js");
$import(baseUrl, "lib/slick.grid.js");
$import(baseUrl, "lib/mathquill.min.js");
$import(baseUrl, "lib/echarts.js");

$import(baseUrl, "lib/qs.all.min.js");

$import(baseUrl, "i18n/zh_CN.js");

$import(baseUrl, "css/default/qs.theme.min.css");
$import(baseUrl, "css/default/datagrid.css");
$import(baseUrl, "app/css/qs.app.min.css");



