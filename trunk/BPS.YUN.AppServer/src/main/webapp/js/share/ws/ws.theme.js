ws.theme = {};

ws.theme.iconHTML = function(file){
	return '<img style="vertical-align:middle" alt="" src="' + ws.theme.iconURL(file) + '"></img>';
}
ws.theme.iconURL = function(file){
	return ws_app_path + 'images/ws/icons/' + file;
}
ws.theme.cursorURL = function(file){
	return ws_app_path + 'images/ws/cursor/' + file;
}

ws.theme.imageHTML = function(file, op_style){
	var style = op_style || "";
	return '<img style="' + style + '" alt="" src="' + ws.theme.imageURL(file) + '"></img>';
}
ws.theme.imageURL = function(file){
	return ws_app_path + 'images/ws/' + file;
}
