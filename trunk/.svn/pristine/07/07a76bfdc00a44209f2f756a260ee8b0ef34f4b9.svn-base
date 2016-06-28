function initPrintSet(left, right, top, bottom) {
	var MM_RATIO = 127.0 / 480.0;
	if(!factory.printing)
		return;
	factory.printing.header = "";
	factory.printing.footer = "&b&p/&P";
	factory.printing.leftMargin = (left == undefined) ? 30.0 * MM_RATIO : Number(left) * MM_RATIO;// 单位毫米
	factory.printing.topMargin = (top == undefined) ? 50.0 * MM_RATIO : Number(top) * MM_RATIO;
	factory.printing.rightMargin = (right == undefined) ? 30.0 * MM_RATIO : Number(right) * MM_RATIO;
	factory.printing.bottomMargin = (bottom == undefined) ? 50.0 * MM_RATIO : Number(bottom) * MM_RATIO;
	//factory.printing.printBackground = false;
	factory.printing.portrait = true;
	//factory.printing.Print(false);
}

function wordPrint() {
	if(!factory.printing){
		alert("打印进程未启动");
		return;
	}
	initPrintSet(paddingLeft, paddingRight, paddingTop, paddingBottom);
	
	var toolbar = document.getElementById("toolbar");
	toolbar.style.visibility = "hidden";
	toolbar.style.position = "absolute";
	factory.printing.Print();
	toolbar.style.visibility = "visible";
	toolbar.style.position = "static";
}
function exponentReplaceFunction() {
	var exp = arguments[0];
	if (exp == "" || exp == null || exp.length <= 3)
		return exp;
	var idx = exp.indexOf("^");
	var n = exp.substring(0, idx);
	var e = exp.substring(idx + 1);

	return '<span>' + n
			+ '<span style="font-size: 8px; vertical-align: top;">' + e
			+ '</span></span>';
}

function convertHtml(flexHtml) {
	if(!flexHtml)
		return "";
	flexHtml = flexHtml.replace(/\d+\^\d+/g, exponentReplaceFunction);
	
	//将HTML代码支离为HTML片段和文字片段
	var tags = /[^<>]+|<(\/?)([A-Za-z]+)([^<>]*)>/g;
	var htmlList = flexHtml.match(tags);
	
	for (var index = 0; index < htmlList.length; index++) {
		if (!/<(?:.|\s)*?>/.test(htmlList[index])) {// 非标签
			var str = htmlList[index].toString();
			str = str.replace(/(\s*$)/g,"");
			htmlList[index] = Exporter.replaceString(Exporter.replaceString(str," ", "&nbsp;"), "\n","<br/>");
		}
	}
	
	flexHtml = htmlList.join("");
	
	var div = document.getElementById("convert_div");
	div.innerHTML = flexHtml;
	
	var fonts = div.getElementsByTagName("FONT");
	for (var i = 0; i < fonts.length; i++) {
		if (fonts[i].size){
			fonts[i].style.fontSize = fonts[i].size + "px";
		}
	}
	
	flexHtml = div.innerHTML == undefined ? "" : div.innerHTML;
	
	return flexHtml;
}

var isIE = false;
var pageHeight = 0;

var Exporter = {};

/**
 * 获取浏览器
 * 
 * @return {}
 */
Exporter.getBrowser = function() {
	var oType = "";
	if (navigator.userAgent.indexOf("MSIE") != -1) {
		oType = "IE";
	} else if (navigator.userAgent.indexOf("Firefox") != -1) {
		oType = "FIREFOX";
	}
	return oType;
}

Exporter.exportReport = function(reportXml) {
	if ("IE" == Exporter.getBrowser())
		isIE = true;
	var html = "";
	var doc = Util.loadXML(reportXml);
	if (!doc)
		return;
		
	var root = doc.documentElement;
	pageHeight = root.getAttribute("height");
	if(!pageHeight)
		pageHeight = 1132;
	var width = root.getAttribute("width");
	if(!width)
		width = 800;
	var o = {};
	o.isIE = isIE;
	o.pageHeight = pageHeight;
	o.doc = doc;
	//"屏幕尺寸："+screen.width+"*"+screen.height	
	var sFeatures = 'dialogHeight: 600px; dialogWidth: ' + (Number(width) + 50).toString()
					+ 'px; center: yes; help: no; resizable: yes; minimize:yes; maximize:yes; status: no; unadorned: yes;';
	window.showModelessDialog('print.html',o,sFeatures);
}

/**
 * 转换XML的文本框为HTML文本框
 * 
 * @param xmlText
 *            文本框的DOM对象
 * @return 包含文本框内容的DIV
 */
Exporter.convertText = function(xmlText) {
	var x = xmlText.getAttribute("x");
	var y = Number(xmlText.getAttribute("y"));
	var width = xmlText.getAttribute("width");
	var height = xmlText.getAttribute("height");
	
	if(xmlText.parentNode.nodeName == "page"){
		var parentNode = xmlText.parentNode;
		var pageNo = parentNode.getAttribute("pageNo");
		y = Number(y) + pageHeight * (Number(pageNo));
	}
	var style = "position:absolute;";
	style += "top:" + y + "px;";
	style += "left:" + x + "px;";
	style += "width:" + width + "px;";
	style += "height:" + height + "px;";

	var div = "";
	div = '<div style="' + style + '">';
	var content = (isIE == true ? xmlText.text : xmlText.textContent);
	div += convertHtml(content);
	div += "</div>";
	return div;
}

/**
 * 转换XML的表格为HTML表格
 * 
 * @param xmlTable
 *            表格的DOM对象
 * @return Table
 */
Exporter.convertTable = function(xmlTable) {
	var x = xmlTable.getAttribute("x");
	var y = Number(xmlTable.getAttribute("y"));
	var width = xmlTable.getAttribute("width");
	var height = xmlTable.getAttribute("height");

	if(xmlTable.parentNode.nodeName == "page"){
		var parentNode = xmlTable.parentNode;
		var pageNo = parentNode.getAttribute("pageNo");
		y = Number(y) + pageHeight * (Number(pageNo));
	}
	
	var style = "position:absolute;";
	style += "top:" + y + "px;";
	style += "left:" + x + "px;";
	style += "width:" + width + "px;";
	style += "height:" + height + "px;";

	var div = "";
	div = '<div style="' + style + '">';
	var table = '<table width="' + width + 'px" height="' + height + 'px" ';
	table += 'style="border:solid black; border-collapse:collapse; border-width:1px 0px 0px 1px; table-layout:fixed;word-wrap: break-word;">';
	var childNodes = xmlTable.childNodes;
	for (var index = 0; index < childNodes.length; index++) {
		var node = childNodes[index];
		if (node.nodeName == 'tr') {
			table += '<tr>';
			table += Exporter.converTD(node);
			table += '</tr>';
		}
	}
	table += '</table>';
	div += table;
	div += "</div>";
	return div;
}

/**
 * 全部替换字符串中的字符
 * 
 * @param repStr 原字符串
 * @param rgExp 要替换的字符串
 * @param replaceText 替换字符串
 * @return
 */
Exporter.replaceString = function(repStr, rgExp, replaceText) {
	var regS = new RegExp(rgExp,"gi");
	var str = repStr.replace(regS, replaceText);
	return str;
}

Exporter.converTD = function(xmlTr) {
	var tr = '';
	var childNodes = xmlTr.childNodes;
	for (var index = 0; index < childNodes.length; index++) {
		var node = childNodes[index];
		if (node.nodeName == 'td') {
			var width = node.getAttribute("width");
			var height = node.getAttribute("height");
			var style = 'width="' + width + '"px ';
			style += 'height="' + height + '"px ';

			var colSpan = node.getAttribute("colSpan");
			if (colSpan)
				style += 'colSpan="' + colSpan + '" ';
			var rowSpan = node.getAttribute("rowSpan");
			if (rowSpan)
				style += 'rowSpan="' + rowSpan + '" ';

			var textAlign = node.getAttribute("textAlign");
			var verticalAlign = node.getAttribute("verticalAlign");
			var fillColor = node.getAttribute("fillColor");
			var borderWidth = node.getAttribute("borderWidth");
			var textDecoration = node.getAttribute("textDecoration");
			var fontSize = node.getAttribute("fontSize");
			var fontFamily = node.getAttribute("fontFamily");
			var fontStyle = node.getAttribute("fontStyle");
			var fontVariant = node.getAttribute("fontVariant");
			var fontWeight = node.getAttribute("fontWeight");
			

			style += 'style="border:solid black; padding:2px; ';
			if (fillColor)
				style += 'background-color:' + fillColor + '; ';
			if (borderWidth)
				style += 'border-width:' + borderWidth + '; ';
			else
				style += 'border-width:0px 1px 1px 0px;';
			if (textDecoration)
				style += 'text-decoration:' + textDecoration + '; ';
			if (fontSize)
				style += 'font-size:' + fontSize + '; ';
			if (fontFamily)
				style += 'font-family:' + fontFamily + '; ';
			if (fontStyle)
				style += 'font-style:' + fontStyle + '; ';
			if (fontVariant)
				style += 'font-variant:' + fontVariant + '; ';
			if (fontWeight)
				style += 'font-weight:' + fontWeight + '; ';
			if (textAlign)
				style += 'text-align:' + textAlign + '; ';
			if (verticalAlign)
				style += 'vertical-align:' + verticalAlign + ' ';

			style += '"';

			var td = '<td ' + style + '>';
			var content = (isIE == true ? node.text : node.textContent);
			td += convertHtml(content);
			td += '</td>';

			tr += td;
		}
	}
	return tr;
}