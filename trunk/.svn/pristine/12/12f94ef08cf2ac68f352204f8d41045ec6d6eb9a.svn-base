ws.waitingbar = {};

/**
 * 生成等待条
 * @param standin 替身元素，生成的对象将占据替换替身元素所在的位置，并且其ID也设为替身元素的ID
 * @return 生成的等待条元素
 */
ws.waitingbar.make = function(standin){
	var ctrlEl = null;
	if( typeof(standin) == "string" )
		ctrlEl = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		ctrlEl = standin;
	
	var lightRGB = new Array(255,255,255);
	var lightColor = ctrlEl.getAttribute("light_color");
	if( lightColor != null && lightColor != undefined && lightColor != "" )
		lightRGB = ws.waitingbar.toColorArray(lightColor);
	
	var color = ctrlEl.getAttribute("color");
	if( color == null || color == undefined || color == "" )
		color = "#6A220F";
	
	var bar = ws.createElement('<div class="ws_waitingbar">');
	var maq = ws.createElement('<marquee direction="right" scrollamount="10">', bar);
	var table, tbody, tr, td;
	table = ws.createElement('<table class="bar">', maq);
	tbody = ws.createElement('tbody', table);
	tr    = ws.createElement('tr',    tbody);

	bar.blocks = new Array();
	for( var i = 0; i < 5; i++ ){
		td = ws.createElement('td', tr);
		bar.blocks.push(td);
	}
	bar.lightRGB  = lightRGB;
	bar.initColor = ws.waitingbar.initColor;
	
	bar.initColor(color);
	ctrlEl.replaceNode(bar);
	bar.id = ctrlEl.id;
	bar.style.cssText = ctrlEl.style.cssText;

	return bar;
}

ws.waitingbar.initColor = function (color){
	var opArray = new Array(.10, .25, .50, .75, 1.0);
	var baseColor = ws.waitingbar.toColorArray(color);
	var nColor,nHex;
	for(var i = 0; i < 5; i++) {
		nColor = ws.waitingbar.setColorHue(baseColor, opArray[i], this.lightRGB);
		nHex   = ws.waitingbar.toHex(nColor[0]) + ws.waitingbar.toHex(nColor[1]) + ws.waitingbar.toHex(nColor[2]);
		this.blocks[i].bgColor = "#" + nHex;
	}
}

ws.waitingbar.setColorHue = function (originColor, opacityPercent, maskRGB){
	var returnColor = new Array();
	for(var i = 0; i < originColor.length; i++) 
		returnColor[i] = Math.round(originColor[i] * opacityPercent) + Math.round(maskRGB[i] * (1.0 - opacityPercent));
	return returnColor;
}

ws.waitingbar.toColorArray = function (strColor) {
	var r, g, b;
	r = parseInt(strColor.substring(1, 3), 16);
	g = parseInt(strColor.substring(3, 5), 16);
	b = parseInt(strColor.substring(5, 7), 16);
	return new Array(r,g,b);
}

ws.waitingbar.toHex = function (dec) {
	hex=dec.toString(16);
	if(hex.length==1)
		hex="0"+hex;
	if(hex==100)
		hex="FF";
	return hex.toUpperCase();
}