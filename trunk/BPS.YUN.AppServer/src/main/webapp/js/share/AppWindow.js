var AppWindow = {};
AppWindow.openALink = function(alink) {
	// if (external["openALink"] != null)
	alink = decodeURIComponent(alink);
	alink = alink.replace(/#/g, "\"");
	if (external != null)
		external.openALink(alink);
}
AppWindow.getDesktopValue = function(name) {
	// if (external["getDesktopValue"] != null)
	if (external != null)
		return external.getDesktopValue(name);
}