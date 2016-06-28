
ws.tooltip = {};

ws.tooltip.delayTime = 700;
ws.tooltip.showTime  = 5000;

ws.tooltip.tooltipWindow = null;
ws.tooltip.showTimeout = 0;
ws.tooltip.hideTimeout = 0;
ws.tooltip.shown = false;
ws.tooltip.x;
ws.tooltip.y;

ws.tooltip.getRealElement = function(el) {
	temp = el;
	while ((temp != null) && (temp.tagName != "BODY")) {
		if (temp.getAttribute("tooltip")) {
			el = temp;
			return el;
		}
		temp = temp.parentElement;
	}
	return el;
}

ws.tooltip.onmousemove = function() {
	ws.tooltip.x = window.event.x;
	ws.tooltip.y = window.event.y;
}

ws.tooltip.onmouseover = function() {
	fromEl = ws.tooltip.getRealElement(event.fromElement);
	toEl   = ws.tooltip.getRealElement(event.toElement);
	if ((toEl.getAttribute("tooltip")) && (toEl != fromEl)) {
		ws.tooltip.showTimeout = window.setTimeout("ws.tooltip.displayTooltip(toEl)", ws.tooltip.delayTime);
	}
}

ws.tooltip.onmouseout = function() {
	fromEl = ws.tooltip.getRealElement(event.fromElement);
	toEl   = ws.tooltip.getRealElement(event.toElement);
	if ((fromEl.getAttribute("tooltip")) && (toEl != fromEl)) {
		window.clearTimeout(ws.tooltip.showTimeout);
		ws.tooltip.hideTooltip();
	}
}

ws.tooltip.displayTooltip = function(el) {
	var src;
	if (ws.tooltip.tooltipWindow == null) {
		var resText = el.getAttribute("tooltip");
		var res     = ws.getStringResource(resText);
		if( res == null || res == "" )
			return;
		ws.tooltip.tooltipWindow = ws.createElement("<div class='ws_tooltip' style='position:absolute'>", document.body);
		ws.tooltip.tooltipWindow.innerHTML = res;
	}
	else {
		var resText = el.getAttribute("tooltip");
		var res     = ws.getStringResource(resText);
		if( res == null || res == "" )
			return;
		ws.tooltip.tooltipWindow.innerHTML = res;
	}

	ws.tooltip.tooltipWindow.style.left = ws.tooltip.x - 3;  //This is placed for the hand cursor :-(
	ws.tooltip.tooltipWindow.style.top  = ws.tooltip.y + 20;
	
	dir = ws.tooltip.getDirection();	//This also fixes the position if the tooltip is outside the window.

	if (typeof(ws.effect.swipe) == "function")
		window.setTimeout("ws.effect.swipe(ws.tooltip.tooltipWindow, dir);",1);	
	else if (typeof(ws.effect.fade) == "function")
		window.setTimeout("ws.effect.fade(ws.tooltip.tooltipWindow, true, 5)",1);
	else
		ws.tooltip.tooltipWindow.style.visibility = "visible";

	ws.tooltip.shown = true;
	ws.tooltip.hideTimeout = window.setTimeout("ws.tooltip.hideTooltip()", ws.tooltip.showTime);
}

ws.tooltip.hideTooltip = function() {
//	if (ws.tooltip.shown) {
//		window.clearTimeout(ws.tooltip.hideTimeout);
		if( ws.tooltip.tooltipWindow ){
			ws.tooltip.tooltipWindow.style.visibility = "hidden";
			ws.tooltip.shown = false;
		}
//	}
}

ws.tooltip.getDirection = function() {
	var pageWidth, pageHeight, scrollTop;
	pageHeight    = document.body.clientHeight;
	pageWidth     = document.body.clientWidth;
	toolTipTop    = ws.tooltip.tooltipWindow.style.pixelTop;
	toolTipLeft   = ws.tooltip.tooltipWindow.style.pixelLeft;
	toolTipHeight = ws.tooltip.tooltipWindow.offsetHeight;
	toolTipWidth  = ws.tooltip.tooltipWindow.offsetWidth;
	scrollTop     = document.body.scrollTop;
	scrollLeft    = document.body.scrollLeft;

	if (toolTipWidth > pageWidth)
		ws.tooltip.tooltipWindow.style.left = scrollLeft;
	else if (toolTipLeft + toolTipWidth - scrollLeft > pageWidth)
		ws.tooltip.tooltipWindow.style.left = pageWidth - toolTipWidth + scrollLeft;
		
	if (toolTipTop + toolTipHeight - scrollTop > pageHeight) {
		ws.tooltip.tooltipWindow.style.top = toolTipTop - toolTipHeight - 22;
		return 8;
	}
	return 2;
}

if (document.all) { 
	ws.addEvent(document, "mouseover", ws.tooltip.onmouseover);
	ws.addEvent(document, "mouseout",  ws.tooltip.onmouseout);
	ws.addEvent(document, "mousemove", ws.tooltip.onmousemove);
}
