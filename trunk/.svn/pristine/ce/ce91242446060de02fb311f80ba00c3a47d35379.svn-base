ws.drawpanel = {};
/**
 * 允许在元素中执行画区域(如矩形，圆形等)及画多线段和多边形的能力
 * @param panel 目标元素
 * @param op_rectBox 矩形区域元素
 * @param op_ovalBox 椭圆形区域元素
 * @param op_boxMovable 区域是否可移动(可选，默认为不可移动)
 * @return 完成功能添加后的panel元素
 */
ws.drawpanel.make = function(panel, op_rectBox, op_ovalBox, op_boxMovable){
	var boxMovable = op_boxMovable || false;

	panel.boxMovable = boxMovable;
	
	ws.addEvent(panel, "mousedown", function(){ ws.drawpanel.onmousedown(panel); });
	ws.addEvent(panel, "mousemove", function(){ ws.drawpanel.onmousemove(panel); });
	ws.addEvent(panel, "mouseup",   function(){ ws.drawpanel.onmouseup(panel); });
	ws.addEvent(panel, "click",     function(){ ws.drawpanel.onclick(panel); });
	ws.addEvent(panel, "dblclick",  function(){ ws.drawpanel.ondbclick(panel); });
	
	if( op_ovalBox == null || op_ovalBox == undefined )
		panel.ovalBox = ws.createElement('<v:oval id="ovalbox" filled="false" style="position:absolute;visibility:hidden;">', panel.parentElement);
	else
		panel.ovalBox = op_ovalBox;
	if( op_rectBox == null || op_rectBox == undefined )
		panel.rectBox = ws.createElement('<v:rect id="rectbox" filled="false" style="position:absolute;visibility:hidden;">', panel.parentElement);
	else
		panel.rectBox = op_rectBox;

	panel.box = panel.rectBox;

	if( boxMovable ){
		ws.addClass(panel.ovalBox, "ws_movable");
		ws.addClass(panel.rectBox, "ws_movable");
		panel.ovalBox.onMoving = ws.drawpanel.onBoxMoving;
		panel.rectBox.onMoving = ws.drawpanel.onBoxMoving;
	}

	panel.setBox        = ws.drawpanel.setBox;
	panel.clear         = ws.drawpanel.clear;
	panel.setDrawAction = ws.drawpanel.setDrawAction;
	panel.getDrawAction = ws.drawpanel.getDrawAction;

	return panel;
}

/**
 * 清除所画内容
 */
ws.drawpanel.clear = function(){
	if( this.tip )
		this.tip.style.visibility = "hidden";

	if( this.polyline && this.polyline.style.visibility != "hidden" )
		this.polyline.style.visibility = "hidden";
	if( this.ovalBox && this.ovalBox.style.visibility != "hidden" )
		this.ovalBox.style.visibility = "hidden";
	if( this.rectBox && this.rectBox.style.visibility != "hidden" )
		this.rectBox.style.visibility = "hidden";
}

/**
 * 设置区域显示元素，这个功能可以更改区域显示的形状(根据元素的显示形状)
 * @param box 新的区域显示元素
 */
ws.drawpanel.setBox = function(box){
	if( this.box )
		this.box.style.visibility = "hidden";
	if( this.boxMovable ){
		ws.addClass(box, "ws_movable");
		box.onMoving = ws.drawpanel.onBoxMoving;
	}
	box.initX = 0;
	box.initY = 0;
	box.maxX  = this.offsetWidth - 1;
	box.minX  = 0;
	box.maxY  = this.offsetHeight - 1;
	box.minY  = 0;
	this.box = box;
}
/**
 * 设置当前执行的操作
 * @param action 操作类型
 *		- "Polyline" 画多线段
 *		- "ClosedPolyline" 画多边形
 * 		- "RectBox" 矩形区域
 * 		- "OvalBox" 椭圆形区域
 */
ws.drawpanel.setDrawAction = function(action){
	this.action = action;
	if( action == "Polyline" || action == "ClosedPolyline" )
		this.drawLine = true;
	else 
		this.drawLine = false;
	if( action == "OvalBox" || action == "RectBox")
		this.boxEnabled = true;
	else
		this.boxEnabled = false;
	if( action == "OvalBox" )
		this.setBox(this.ovalBox);
	if( action == "RectBox" )
		this.setBox(this.rectBox);

	if( this.box )
		this.box.style.visibility = "hidden";
	if( this.polyline )
		this.polyline.style.visibility = "hidden";
	if( this.tip )
		this.tip.style.visibility = "hidden";
}
/**
 * 获取当前的操作类型
 * @return 操作类型
 */
ws.drawpanel.getDrawAction = function(){
	return this.action;
}

/**
 * 当区域元素正在移动时的回调函数，此处用于控制box元素的位置以使其不超出父元素的显示范围
 */
ws.drawpanel.onBoxMoving = function(){
	if( this.style.pixelLeft < 0 )
		this.style.pixelLeft = 0;
	if( this.style.pixelTop < 0 )
		this.style.pixelTop = 0;
	if( this.style.pixelLeft + this.style.pixelWidth > this.parentElement.offsetWidth - 1 )
		this.style.pixelLeft = this.parentElement.offsetWidth - this.style.pixelWidth - 1;
	if( this.style.pixelTop + this.style.pixelHeight > this.parentElement.offsetHeight - 1)
		this.style.pixelTop = this.parentElement.offsetHeight - this.style.pixelHeight - 1;
}

ws.drawpanel.onclick = function (panel){
	if( panel.drawLine ){
		if( panel.curPolyline == undefined || panel.curPolyline == null ){
			var dx = 0, dy = 0;
			
			if( panel.tagName != "BODY" ){
				var rect = panel.getBoundingClientRect();
				dx = rect.left;
				dy = rect.top;
			}
			var x = event.clientX - dx;
			var y = event.clientY - dy;
			if( panel.tip != null && panel.tip != undefined )
				panel.tip.style.visibility = "hidden";
			if( panel.polyline == undefined || panel.polyline == null )
				panel.polyline = ws.createElement('<v:polyline points="0,0 1,1" filled="false" strokecolor="red" style="position:absolute">', panel);
			if( panel.action == "ClosedPolyline" ){
				panel.polyline.filled = "true";
				panel.polyline.style.cssText = "position:absolute;filter:Alpha(opacity=50);"
			}
			else {
				panel.polyline.filled = "false";
				panel.polyline.style.cssText = "position:absolute;"
			}
			panel.polyline.style.visibility = "visible";
			panel.curPolyline = panel.polyline;
			panel.curPolyline.points.value = "0,0 1,1";
			panel.curPolyline.dx = dx;
			panel.curPolyline.dy = dy;

			var item = panel.curPolyline.points.item(0);
			item.value = x + "px," + y + "px";
			var item = panel.curPolyline.points.item(1);
			item.value = (x + 1) + "px," + (y + 1) + "px";
			
			var devicePoints = new Array();
			devicePoints[0] = x
			devicePoints[1] = y
			devicePoints[2] = x
			devicePoints[3] = y
			panel.curPolyline.devicePoints = devicePoints;
		}
		else {
			var x = event.clientX - panel.curPolyline.dx;
			var y = event.clientY - panel.curPolyline.dy;
			if( x < 0 || x > panel.offsetWidth || y < 0 || y > panel.offsetHeight)
				return;
			panel.curPolyline.points.value += " 0,0";
			var item = panel.curPolyline.points.item(panel.curPolyline.points.length - 1);
			item.value = x + "px," + y + "px";
			panel.curPolyline.devicePoints.push(x);
			panel.curPolyline.devicePoints.push(y);
		}
		panel.setCapture();
	}
}
ws.drawpanel.ondbclick = function (panel){
	if( panel.drawLine ){
		if( panel.curPolyline != undefined && panel.curPolyline != null ){
			panel.releaseCapture();
			if( panel.action == "ClosedPolyline" ){
				var x, y;
				x = panel.curPolyline.devicePoints[0];
				y = panel.curPolyline.devicePoints[1];
				panel.curPolyline.points.value += " 0,0";
				var item = panel.curPolyline.points.item(panel.curPolyline.points.length - 1);
				item.value = x + "px," + y + "px";
				panel.curPolyline.devicePoints.push(x);
				panel.curPolyline.devicePoints.push(y);
			}
			panel.curPolyline  = null;
			if( typeof(panel.onDrawEnd) == "function" )
				panel.onDrawEnd(panel.polyline);
		}
	}
}
ws.drawpanel.onmousedown = function(panel){
	if( panel.drawLine )
		return;
	if( !panel.boxEnabled )
		return;
	var box = panel.box;
	if( box.parentElement.tagName != "BODY" ){
		var rect = box.parentElement.getBoundingClientRect();
		box.dx = rect.left;
		box.dy = rect.top;
	}
	else {
		box.dx = 0;
		box.dy = 0;
	}
	var x, y;
	x = event.clientX - box.dx;
	y = event.clientY - box.dy;
	if( panel.boxMovable && box.style.visibility == "visible"){
		if( x >= box.style.pixelLeft && x <= box.style.pixelLeft + box.style.pixelWidth &&
			y >= box.style.pixelTop && y <= box.style.pixelTop + box.style.pixelHeight ){
			event.cancelBubble = true;
			event.returnValue  = false;
			box.fireEvent("onmousedown");
			return;
		}
	}
	panel.boxing = true;
	box.initX = x;
	box.initY = y;
	box.style.pixelLeft   = x;
	box.style.pixelTop    = y;
	box.style.pixelWidth  = 0;
	box.style.pixelHeight = 0;
	box.style.visibility  = "visible";
	ws.makeTopmost(box);
	panel.setCapture(true);
}
ws.drawpanel.onmousemove = function(panel){
	if( panel.drawLine ){
		if( panel.curPolyline != undefined && panel.curPolyline != null ){
			var x = event.clientX - panel.curPolyline.dx;
			var y = event.clientY - panel.curPolyline.dy;
			var item = panel.curPolyline.points.item(panel.curPolyline.points.length - 1);
			item.value = x + "px," + y + "px";
			var i = panel.curPolyline.devicePoints.length - 1;
			panel.curPolyline.devicePoints[i - 1] = x;
			panel.curPolyline.devicePoints[i] = y;
		}
		return;
	}
	if( !panel.boxEnabled || !panel.boxing )
		return;
	var box = panel.box;
	var x, y;
	x = event.clientX - box.dx;
	y = event.clientY - box.dy;
		
	if( x < box.initX ){
		if( x < box.minX ){
			box.style.pixelLeft  = box.minX;
			box.style.pixelWidth = box.initX - box.minX;
		}
		else {
			box.style.pixelLeft  = x;
			box.style.pixelWidth = box.initX - x;
		}
	}
	else {
		box.style.pixelLeft  = box.initX;
		if( x > box.maxX )
			box.style.pixelWidth = box.maxX - box.initX;	
		else
			box.style.pixelWidth = x - box.initX;
	}
	if( y < box.initY ){
		if( y < box.minY ){
			box.style.pixelTop    = box.minY;
			box.style.pixelHeight = box.initY - box.minY;
		}
		else {
			box.style.pixelTop    = y;
			box.style.pixelHeight = box.initY - y;
		}
	}
	else {
		box.style.pixelTop    = box.initY;
		if( y > box.maxY )
			box.style.pixelHeight = box.maxY - box.initY;	
		else
			box.style.pixelHeight = y - box.initY;
	}
}
ws.drawpanel.onmouseup = function(panel){
	if( panel.drawLine )
		return;
	if( !panel.boxEnabled || !panel.boxing )
		return;
	var box = panel.box;

	panel.boxing = false;
	panel.releaseCapture();
	if( typeof(panel.onBoxingEnd) == "function" )
		panel.onBoxingEnd();
}