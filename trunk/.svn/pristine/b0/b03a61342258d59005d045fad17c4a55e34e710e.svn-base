var browserExtension;
function setBrowserExtension(ext){
	browserExtension = ext;
}

function get_base_url(refer){
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
	__base_url__ = base;
	return base;
}

function openChatWindow(ocsid, domain, username){
	var url = get_base_url("ocs.js") + "/applications/ocs/messenger.jsp?";
	url += "ocsid=" + encodeURI(ocsid);
	url += "&domain=" + encodeURI(domain);
	url += "&username=" + encodeURI(username);
	window.open(url, "_blank", "directories=no,height=500px,width=400px,menubar=no,status=no,toolbar=no");
}