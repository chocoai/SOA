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
	if( path.substring(path.length - 3) == "css" )
		document.write("<" + "link href=\"" + baseUrl + path + "\" rel=\"stylesheet\"></" + "link>");
	else
		document.write("<" + "script src=\"" + baseUrl + path + "\"></" + "script>");
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

$import(baseUrl, "css/default/qs.m.min.css");

$import(baseUrl, "../lib/qs.m.all.min.js");
$import(baseUrl, "../i18n/zh_CN.js");

