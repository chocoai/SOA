
ws = {};
ws.checkZIndex = true;
ws.moveElement = null;
ws.winX  = 0;
ws.winY  = 0;
ws.uniqueId = 0;
ws.isIE  = ( (navigator.userAgent.toLowerCase().indexOf("msie") != -1) && (navigator.userAgent.toLowerCase().indexOf("opera") == -1) );
ws.isIE5 = document.all != null && document.getElementsByTagName != null; 
ws.errorDialog = null;
ws.msgDialog   = null;
ws.waitingBox  = null;
ws.session     = new Array();

/**
 * 根据元素ID获取元素对象
 */
function $()
{
    var elements = new Array();

    for (var i = 0; i < arguments.length; i++)
    {
        var element = arguments[i];
        if (typeof element == 'string')
        {
            if (document.getElementById)
            {
                element = document.getElementById(element);
            }
            else if (document.all)
            {
                element = document.all[element];
            }
        }

        if (arguments.length == 1) 
        {
            return element;
        }

        elements.push(element);
    }

    return elements;
}
/**
 * 生成维一ID值
 * @return ID值
 */
ws.generateId = function(){
	ws.uniqueId++;
	return ws.uniqueId;
}

ws.toInt = function(arg){
	var result = 0;
	result = parseInt(arg);
	if( isNaN(result) )
		result = 0;
	return result;
}
ws.toBoolean = function(arg){
	if( arg == null || arg == undefined )
		return false;
	if( arg == true || arg == "true" || arg == "yes" || arg == "Y" )
		return true;
	if( arg == false || arg == "false" || arg == "no" || arg == "N" )
		return false;
	if( ws.toInt(arg) != 0 )
		return true;
	else
		return false;
}
ws.removeClass = function(el, className) {
	if (!(el && el.className)) {
		return;
	}
	var cls = el.className.split(" ");
	var ar = new Array();
	for (var i = cls.length; i > 0;) {
		if (cls[--i] != className) {
			ar[ar.length] = cls[i];
		}
	}
	el.className = ar.join(" ");
};

ws.addClass = function(el, className) {
	ws.removeClass(el, className);
	el.className += " " + className;
};

ws.getCursor = function(cursor_loc){
	return "url(" + ws.theme.cursorURL(cursor_loc) + ")";
};

ws.hasClass = function(el, className){
	if (!(el && el.className)) {
		return false;
	}
	if( el.className.indexOf(className) == -1)
		return false;
	else return true;
}

ws.createElement = function(type, parent) {
	var el = null;
	if (document.createElementNS) {
		// use the XHTML namespace; IE won't normally get here unless
		// _they_ "fix" the DOM2 implementation.
		el = document.createElementNS("http://www.w3.org/1999/xhtml", type);
	} else {
		el = document.createElement(type);
	}
	if (typeof parent != "undefined") 
		parent.appendChild(el);
	else
		document.body.appendChild(el);
	return el;
};

ws.addEvent = function(el, evname, func) {
	if( !func )
		return;
	if (ws.isIE) {
		el.attachEvent("on" + evname, func);
	} else {
		el.addEventListener(evname, func, true);
	}
};

ws.removeEvent = function(el, evname, func) {
	if (ws.isIE) {
		el.detachEvent("on" + evname, func);
	} else {
		el.removeEventListener(evname, func, true);
	}
};


ws.getLeftPos = function(el){
	if (ws.isIE5) {
		if (el.currentStyle.left == "auto")
			return 0;
		else return parseInt(el.currentStyle.left);
	}
	else return el.style.pixelLeft;
}

ws.getTopPos = function(el){
	if (ws.isIE5) {
		if (el.currentStyle.top == "auto")
			return 0;
		else return parseInt(el.currentStyle.top);
	} else return el.style.pixelTop;
}

ws.makeTopmost = function(el){
	var daiz;
	var max = 0;
	var da = document.all;
	
	for (var i = 0; i < da.length; i++) {
		daiz = da[i].style.zIndex;
		if (daiz != "" && daiz > max)
			max = daiz;
	}
	
	el.style.zIndex = max + 1;
}

ws.getRealElement = function(el, type, value) {
	temp = el;
	while ((temp != null) && (temp.tagName != "BODY")) {
		if (eval("temp." + type) == value) {
			el = temp;
			return el;
		}
		temp = temp.parentElement;
	}
	return null;
}

//---BEGIN 窗口拖动功能
ws.getMoveElement = function(el){
	var temp = el;
	while ((temp != null) && (temp.tagName != "BODY")) {
		if (ws.hasClass(temp, "ws_movable") || ws.hasClass(temp, "ws_movehandle")){
			el = temp;
			return el;
		}
		temp = temp.parentElement;
	}
	return el;
}

ws.win_onmousedown = function(){
	el = ws.getMoveElement(window.event.srcElement)
	
	if (ws.hasClass(el, "ws_movable") || ws.hasClass(el, "ws_movehandle") ) {
		if (ws.hasClass(el, "ws_movehandle")) {
			win = el.getAttribute("handlefor");
			if (win == null) {
				moveElement = null;
				return;
			} else ws.moveElement = eval(win);
		} else ws.moveElement = el;
		
		if (ws.checkZIndex) 
			ws.makeTopmost(ws.moveElement);
		
		if( typeof(ws.moveElement.onStartMove) == "function" ){
			ws.moveElement.onStartMove();
			ws.winY = window.event.clientY - ws.getTopPos(ws.moveElement);
			ws.winX = window.event.clientX - ws.getLeftPos(ws.moveElement);
		}
		ws.winY = window.event.clientY - ws.getTopPos(ws.moveElement);
		ws.winX = window.event.clientX - ws.getLeftPos(ws.moveElement);
		
		window.event.returnValue  = false;
		window.event.cancelBubble = true;
	} else ws.moveElement = null;
}

ws.win_onmouseup = function(){
	if(ws.moveElement) {
		if( typeof(ws.moveElement.onMoved) == "function" ){
			ws.moveElement.onMoved();
			ws.winY = window.event.clientY - ws.getTopPos(ws.moveElement);
			ws.winX = window.event.clientX - ws.getLeftPos(ws.moveElement);
		}
		ws.moveElement = null;
	}
}

ws.win_onmousemove = function(){
	if (ws.moveElement) {
		if (window.event.clientX >= 0 && window.event.clientY >= 0) {
			if( !ws.toBoolean(ws.moveElement.limitX) )
				ws.moveElement.style.pixelLeft = window.event.clientX - ws.winX;
			if( !ws.toBoolean(ws.moveElement.limitY) )
				ws.moveElement.style.pixelTop  = window.event.clientY - ws.winY;
			if( typeof(ws.moveElement.onMoving) == "function" ){
				ws.moveElement.onMoving();
			}
		}
		window.event.returnValue  = false;
		window.event.cancelBubble = true;
	}
}
//--- END

ws.resizingObj = null; //修改元素大小的对象

ws.ResizeObject = function(){
	this.el     = null;    //元素对象
	this.dir    = "";      //当前方向 (n, s, e, w, ne, nw, se, sw)
	this.grabx  = null;    
	this.graby  = null;
	this.width  = null;
	this.height = null;
	this.left   = null;
	this.top    = null;
}

//Find out what kind of resize! Return a string inlcluding the directions
ws.getDirection = function(el) {
	var xPos, yPos, offset, dir;
	dir = "";

	xPos = window.event.offsetX;
	yPos = window.event.offsetY;

	offset = 8; //The distance from the edge in pixels

	if (yPos<offset) dir += "n";
	else if (yPos > el.offsetHeight-offset) dir += "s";
	if (xPos<offset) dir += "w";
	else if (xPos > el.offsetWidth-offset) dir += "e";

	return dir;
}

ws.resize_onmousedown = function() {
	var el = ws.getRealElement(event.srcElement, "className", "ws_resizable");

	if (ws.hasClass(el, "ws_resizable") != true) {
		resizingObj = null;
		return;
	}		

	dir = ws.getDirection(el);
	if (dir == "") return;

	ws.resizingObj = new ws.ResizeObject();
		
	ws.resizingObj.el  = el;
	ws.resizingObj.dir = dir;

	ws.resizingObj.grabx = window.event.clientX;
	ws.resizingObj.graby = window.event.clientY;
	ws.resizingObj.width = el.offsetWidth;
	ws.resizingObj.height= el.offsetHeight;
	ws.resizingObj.left  = el.offsetLeft;
	ws.resizingObj.top   = el.offsetTop;

	window.event.returnValue  = false;
	window.event.cancelBubble = true;
}

ws.resize_onmouseup = function() {
	if (ws.resizingObj != null) {
		ws.resizingObj = null;
	}
}

ws.resize_onmousemove = function() {
	var el, xPos, yPos, str, xMin, yMin;
	xMin = 8; //The smallest width possible
	yMin = 8; //             height

	el = ws.getRealElement(event.srcElement, "className", "ws_resizable");

	if (ws.hasClass(el, "ws_resizable") == true) {
		str = ws.getDirection(el);
		if (str == "") str = "default";
		else str += "-resize";
		el.style.cursor = str;
	}
	
//Dragging starts here
	if(ws.resizingObj != null) {
		if (dir.indexOf("e") != -1)
			ws.resizingObj.el.style.width = Math.max(xMin, ws.resizingObj.width + window.event.clientX - ws.resizingObj.grabx) + "px";
	
		if (dir.indexOf("s") != -1)
			ws.resizingObj.el.style.height = Math.max(yMin, ws.resizingObj.height + window.event.clientY - ws.resizingObj.graby) + "px";

		if (dir.indexOf("w") != -1) {
			ws.resizingObj.el.style.left = Math.min(ws.resizingObj.left + window.event.clientX - ws.resizingObj.grabx, ws.resizingObj.left + ws.resizingObj.width - xMin) + "px";
			ws.resizingObj.el.style.width = Math.max(xMin, ws.resizingObj.width - window.event.clientX + ws.resizingObj.grabx) + "px";
		}
		if (dir.indexOf("n") != -1) {
			ws.resizingObj.el.style.top = Math.min(ws.resizingObj.top + window.event.clientY - ws.resizingObj.graby, ws.resizingObj.top + ws.resizingObj.height - yMin) + "px";
			ws.resizingObj.el.style.height = Math.max(yMin, ws.resizingObj.height - window.event.clientY + ws.resizingObj.graby) + "px";
		}
		
		window.event.returnValue  = false;
		window.event.cancelBubble = true;
	} 
}

/**
 * 将文本中出现的取值表达式替换为对象属性的值，表达式的形式为${property}
 * 其中property代表对象的属性名
 * @param txt 包含属性表达式的文本
 * @param obj 用于替换值的对象
 * @return 替换后的文本
 */
ws.replaceValue = function(txt, obj){
	var n1, n2, pos;
	var prop, value, result = "";
	n1 = txt.indexOf("${");
	n2 = txt.indexOf("}", n1);
	pos = 0;
	while( n1 != -1 && n2 != -1 ){
		prop  = txt.substring(n1 + 2, n2);
		value = eval("obj." + prop);
		result += txt.substring(pos, n1);
		result += value;
		pos = n2 + 1;
		n1 = txt.indexOf("${", n2 + 1);
		n2 = txt.indexOf("}", n1);
	}
	result += txt.substring(pos);
	return result;
}

//=====Begin 资源文件处理======================================================
ws.resframes = new Array();		//装入资源的iframe对象数组

/**
 * 装入html资源文件
 * @param url html资源文件的位置
 */
ws.loadResource = function(url){
	var resframe = ws.createElement('<iframe>');
	resframe.style.cssText = "visibility:hidden;position:absolute";
	resframe.src = url;
	ws.resframes.push(resframe); 
	document.body.appendChild(resframe);
}

/**
 * 获取html资源的元素对象,html资源文件是一个普通的html文件,通过元素的ID
 * 获取元素的内容.
 * @param resText 资源描述文本，格式为"htmlres:资源ID",其中资源ID即html
 * 资源文件中的元素ID
 * @return 如果resText不符合资源描述格式，则返回resText本身；如果在html
 * 资源文件中找不到相应的元素，则返回null；否则返回元素对象。
 */
ws.getResource = function(resText){
	if( resText == null )
		return null;
	if( resText.indexOf("htmlres") != 0 )
		return resText;

	var res;
	var key = resText.substring(8);
	for( var i = 0; i < ws.resframes.length; i++ ){
		if( ws.resframes[i].document != null && ws.resframes[i].document != undefined ){
			res = ws.resframes[i].contentWindow.document.getElementById(key);
			if( res != null && res != undefined )
				return res;
		}
	}
	return null;
}

/**
 * 获取资源的字符串形式内容
 * @param resText 资源描述文本
 * @return 如果resText不符合资源描述格式，则返回resText本身；如果在html
 * 资源文件中找不到相应的元素，则返回""；否则返回元素对象的innerHTML值。
 */
ws.getStringResource = function(resText){
	var res = ws.getResource(resText);
	if( typeof(res) == "string" )
		return res;
	if( res == null )
		return "";
	return res.innerHTML;
}
//=====End 资源文件处理========================================================

/**
 * 复制对象
 * @param object 源对象
 * @return 复制后的对象
 */
ws.clone = function(object){
	if(typeof(object) != "object"){
		return object;
	}
	var cloneDepth = ((arguments.length >= 1)?((isNaN(parseInt(arguments[0])))?(null):parseInt(arguments[0])):null);
	if (cloneDepth)
		cloneDepth = ((cloneDepth <= 0)?(null):(cloneDepth));

	var cloneObject = null;
	var thisConstructor = object.constructor;
	var thisConstructorPrototype = thisConstructor.prototype;
	if (thisConstructor == Array)
		cloneObject = new Array();
	else if(thisConstructor == Object)
		cloneObject = new Object();
	else {
		try{
			cloneObject = new thisConstructor;
		} catch(exception) {
			cloneObject = new Object();
			cloneObject.constructor = thisConstructor;
			cloneObject.prototype = thisConstructorPrototype;
		}
	}

	var propertyName = "";
	var newObject=null;
 
	for (propertyName in object){
		newObject = object[propertyName];
		if (!thisConstructorPrototype[propertyName]){
			if (typeof(newObject)=="object"){
				if (newObject === null){
					cloneObject[propertyName] = null;
				} else {
					if(cloneDepth){
						if(cloneDepth == 1)
							cloneObject[propertyName] = null;
						else
							cloneObject[propertyName] = newObject.clone(--cloneDepth);
					} else {
						cloneObject[propertyName] = ws.clone(newObject);
					}
				}
			} else {
				cloneObject[propertyName] = newObject;
			}
		}
	}
 	return cloneObject;
}

//页面初始化函数
onInitialize = null;

//当页面装入完毕时调用初始化函数
ws.onInitPage = function(){
	if( onInitialize != null && onInitialize != "undefined" && typeof(onInitialize) == "function" )
		onInitialize();

	//初始化Panel对象
	var panels;
	panels = document.all.tags("div");
	for( var i = 0; i < panels.length; i++ ){
		if( panels[i].getAttribute("type") == "ws_panel" ){
			ws.panel.make(panels[i]);
		}
	}
}

ws.onError = function(msg, url, line){
	if( ws.errorDialog == null ){
		var clientDiv, msgEl, lineEl, urlEl;
		ws.errorDialog = new ws.Dialog("错误!");
		ws.errorDialog.create();
		ws.errorDialog.setSize(250, 100);
		clientDiv = ws.createElement("<div>");
		msgEl  = ws.createElement("<span>", clientDiv);
		lineEl = ws.createElement("<span>", clientDiv);
		urlEl  = ws.createElement("<span>", clientDiv);
		ws.errorDialog.setContent(clientDiv);
		ws.errorDialog.msg = msgEl;
		ws.errorDialog.line = lineEl;
		ws.errorDialog.url = urlEl;
	}
	ws.errorDialog.msg.innerHTML  = msg;
	ws.errorDialog.line.innerHTML = line;
	ws.errorDialog.url.innerHTML  = url;
	ws.msgDialog.autoSize();
	ws.errorDialog.show(true);
	return false;
}

ws.showMessageBox = function(msg, op_type, op_title){
	var type = op_type || "error", title = op_title || "提示!";

	if( ws.msgDialog == null ){
		var src;
		ws.msgDialog = new ws.Dialog("提示!");
		ws.msgDialog.create();
		ws.msgDialog.setSize(250, 100);
		src = '<table height="100%">';
		src += '<tr><td><img src=""></img></td><td></td></tr>';
		src += '<tr><td colspan=2 align="right" vAlign="bottom">';
		src += '<input type="button" value="确定" style="width:60" onclick="ws.msgDialog.show(false);">';
		src += '</td></tr>';
		src += '</table>'
		ws.msgDialog.setContent(src);
		ws.msgDialog.setCenter();
	}
	switch(type){
	case "warning":
		ws.msgDialog.elClient.firstChild.rows[0].cells[0].firstChild.src = ws.theme.iconURL("warning.gif");
		break;
	case "info":
		ws.msgDialog.elClient.firstChild.rows[0].cells[0].firstChild.src = ws.theme.iconURL("info.gif");
		break;
	case "error":
		ws.msgDialog.elClient.firstChild.rows[0].cells[0].firstChild.src = ws.theme.iconURL("error.gif");
		break;
	}

	ws.msgDialog.setTitle(title);
	ws.msgDialog.elClient.firstChild.rows[0].cells[1].innerHTML = msg;
	ws.msgDialog.setSize(250, 100);
	ws.msgDialog.autoSize();
	ws.msgDialog.show(true);
	return false;
}

/**
 * 显示或关闭等待窗口
 */
ws.showWaitingBox = function(show, op_msg, op_color, op_alpha){
	var msg   = op_msg   || "正在操作中，请稍候..."
	var alpha = op_alpha || 0;
	var color = op_color || "#6A220F";
	if( ws.waitingBox == null ){
		var box, msgel, bar, cover;
		box = ws.createElement('<div class="ws_waiting_box" >', document.body);
		msgel = ws.createElement('<span class="msg">', box);
		msgel.innerHTML = msg;
		bar   = ws.createElement('<span type="ws_WaitingBar" style="width:100%;border:1 solid #ffa800" color="' + color + '" light_color="#FFFFFF">', box);
		ws.waitingbar.make(bar);
		cover = ws.createElement('<div style="position:absolute;visibility:hidden;background-color:#C0C0C0;filter:Alpha(opacity=20);">', document.body);
		cover.style.pixelLeft = 0;
		cover.style.pixelTop  = 0;
		
		ws.waitingBox = ws.makeRound(box);
		ws.waitingBox.style.position   = "absolute";
		ws.waitingBox.style.visibility = "hidden";
		ws.waitingBox.msgEl = msgel;
		ws.waitingBox.bar	= bar;
		ws.waitingBox.cover = cover;
		ws.waitingBox.enterCount = 0;

	}
	ws.waitingBox.cover.style.filter = "Alpha(opacity=" + alpha + ")";

	ws.waitingBox.style.left = document.body.clientWidth / 2 - ws.toInt(ws.waitingBox.offsetWidth) / 2;
	var top  = document.body.clientHeight / 2 - ws.toInt(ws.waitingBox.offsetHeight) / 2;
	if( top > 180 )
		ws.waitingBox.style.top = 180;
	else
		ws.waitingBox.style.top = top;

	if( show == true ){
		if( ws.waitingBox.enterCount < 0 )
			ws.waitingBox.enterCount = 0;
		ws.waitingBox.enterCount++;

		ws.waitingBox.cover.style.pixelWidth  = document.body.scrollWidth;
		if( document.body.scrollHeight < document.body.clientHeight )
			ws.waitingBox.cover.style.pixelHeight = document.body.clientHeight;
		else
			ws.waitingBox.cover.style.pixelHeight = document.body.scrollHeight;

		ws.makeTopmost(ws.waitingBox.cover);
		ws.waitingBox.style.zIndex = ws.waitingBox.cover.style.zIndex + 1;
		ws.waitingBox.style.visibility = "visible";
		ws.waitingBox.cover.style.visibility = "visible";
	}
	else {
		ws.waitingBox.enterCount--;
		if( ws.waitingBox.enterCount == 0){
			ws.waitingBox.style.visibility = "hidden";
			ws.waitingBox.cover.style.visibility = "hidden";
		}
	}
}

//使元素被圆角矩形包围
ws.makeRound = function (el){
	var parent = ws.createElement('div', el.parentElement);
	parent.className = "round_box";

	parent.style.pixelWidth = el.offsetWidth + 12;

	var head = ws.createElement('div', parent);
	head.className = "top";

	ws.layout.makeThreeColumn(head, "left", "middle", "right");

	var client = ws.createElement('div', parent);
	client.className = "client";
	client.appendChild(el);

	var foot = ws.createElement('div', parent);
	foot.className = "foot";

	ws.layout.makeThreeColumn(foot, "left", "middle", "right");

	return parent;
}

if (document.all) { 
	ws.addEvent(document, "mousedown", ws.win_onmousedown);
	ws.addEvent(document, "mouseup", ws.win_onmouseup);
	ws.addEvent(document, "mousemove", ws.win_onmousemove);
	ws.addEvent(document, "mousedown", ws.resize_onmousedown);
	ws.addEvent(document, "mouseup", ws.resize_onmouseup);
	ws.addEvent(document, "mousemove", ws.resize_onmousemove);
	ws.addEvent(window, "load", ws.onInitPage);
}
//window.onerror = ws.onError;

