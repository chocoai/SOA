gis           = {};
gis.map       = {};
gis.map.panel = {};
gis.map.img   = {};

/**
 * 创建地图控件
 * @param standin 地图控件的描述对象
 * @return 生成后的地图控件,地图控件元素将替代standin元素所在的位置
 */
gis.map.make = function(standin, mapServletURL){
	var map = null, mapPanel;
	if( typeof(standin) == "string" )
		map = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		map = standin;
	map.innerHTML = "";
	map.style.overflow = "hidden";
	
	if( mapServletURL != null && mapServletURL != undefined )
		map.imageurl = mapServletURL;
		
	var view_width, view_height, map_width, map_height;
	view_width  = ws.toInt(map.view_width);
	view_height = ws.toInt(map.view_height);
	map_width   = ws.toInt(map.map_width);
	map_height  = ws.toInt(map.map_height);

	if( view_width == 0 )
		view_width = 256;
	if( view_height == 0 )
		view_height = 256;
	if( map_width == 0 )
		map_width = 256;
	if( map_height == 0 )
		map_height = 256;

	map.style.pixelWidth  = view_width;
	map.style.pixelHeight = view_height;
	map.map_width  = map_width;
	map.map_height = map_height;
	
	mapPanel = ws.createElement('<div style="position:absolute;">', map);
	mapPanel.initX = -(map_width - view_width) / 2;
	mapPanel.initY = -(map_height - view_height) / 2;
	mapPanel.style.pixelLeft   = mapPanel.initX;
	mapPanel.style.pixelTop    = mapPanel.initY;
	mapPanel.style.pixelWidth  = map_width;
	mapPanel.style.pixelHeight = map_height;
	
	mapPanel.imgA = ws.createElement('<img unselectable="on" galleryImg="no" src="" style="position:absolute;left:0;top:0;visibility:hidden" />', mapPanel);
	mapPanel.imgB = ws.createElement('<img unselectable="on" galleryImg="no" src="" style="position:absolute;left:0;top:0;visibility:hidden" />', mapPanel);
	mapPanel.tip  = ws.createElement('<div class="maptip" nowrap="true" style="visibility:hidden;position:absolute;">', mapPanel);
	
	mapPanel.onStartMove = gis.map.panel.onStartMove;
	mapPanel.onMoved	 = gis.map.panel.onMoved;
	mapPanel.recenterMap = gis.map.panel.recenterMap;
	mapPanel.zoominMap   = gis.map.panel.zoominMap;
	mapPanel.zoomoutMap  = gis.map.panel.zoomoutMap;
	mapPanel.boxzoomMap  = gis.map.panel.boxzoomMap;
	mapPanel.onBoxingEnd = gis.map.panel.onBoxingEnd;
	mapPanel.onDrawEnd	 = gis.map.panel.onDrawEnd;
	mapPanel.imgA.onload = gis.map.img.updateMap;
	mapPanel.imgB.onload = gis.map.img.updateMap;
	
	mapPanel.parent  = map;
	mapPanel.boxing  = false;
	
	var scale 	  = ws.createElement('<div style="position:absolute;left:10px;top:10px">', map);
	var scaleText = ws.createElement('<div style="font:icon">', scale);
	var scaleBar  = ws.createElement('<div style="font-size:0px;width:0px;height:2px;background-color:black;border:1 solid white;">', scale);
	scale.textDiv = scaleText;
	scale.barDiv  = scaleBar;
	map.scale     = scale;
	scale.style.pixelTop = view_height - 20;
	
	var zoombox  = ws.createElement('<div style="position:absolute;border:1 dashed red;font-size:0;margin:0;padding:0;visibility:hidden;z-index:100">', map);
	ws.drawpanel.make(mapPanel, zoombox);

	map.onclick	= function(){ gis.map.panel.doMapAction(mapPanel); };
	map.refresh = gis.map.refresh;
	map.setScale= gis.map.setScale;
	map.setCurrentAction = gis.map.setCurrentAction;
	map.panel	= mapPanel;
	map.zooming = false;

	return map;
}

/**
 * 设置并显示比例尺
 * @param scale 比例尺对象
 */
gis.map.setScale = function(scale){
	this.scale.textDiv.innerHTML = scale.realLength + scale.unit;
	this.scale.barDiv.style.pixelWidth = scale.deviceLength;
	ws.makeTopmost(this.scale);
}
/**
 * 刷新地图
 */
gis.map.refresh = function(){
	this.panel.recenterMap(this.map_width / 2, this.map_height / 2);
}
/**
 * 设置当前对地图控件施加的操作类型
 * @param action 操作类型
 */
gis.map.setCurrentAction = function(action){
	this.panel.cur_action = action;
	if( action == "move" )
		ws.addClass(this.panel, "ws_movable");
	else 
		ws.removeClass(this.panel, "ws_movable");

	this.panel.setDrawAction("");
	if( action == "zoomin" ){
		this.panel.setDrawAction("RectBox");
		this.panel.style.cursor = ws.getCursor("zoomin.cur");
	}
	else if( action == "zoomout" ){
		this.panel.style.cursor = ws.getCursor("zoomout.cur");
	}
	else if( action == "move" ){
		this.panel.style.cursor = ws.getCursor("hand.cur");
	}
	else if( action == "search" ){
		this.panel.setDrawAction("RectBox");
		this.panel.style.cursor = "crosshair";
	}
	else if( action == "scaleDistance" ){
		this.panel.style.cursor = "auto";
		this.panel.setDrawAction("Polyline");
	}
	else if( action == "scaleArea" ){
		this.panel.style.cursor = "auto";
		this.panel.setDrawAction("ClosedPolyline");
	}
	else {
		this.panel.style.cursor = "auto";
	}
}
/**
 * 当地图开始漫游(移动)时的回调函数，记录地图原始坐标。
 */
gis.map.panel.onStartMove = function(){
	this.prevX = this.style.pixelLeft;
	this.prevY = this.style.pixelTop;
}
/**
 * 当地图结束漫游(移动)后的回调函数，调整地图中心并刷新地图
 */
gis.map.panel.onMoved = function (){
	var x, y;
	x = this.parent.map_width / 2 - (this.style.pixelLeft - this.prevX);
	y = this.parent.map_height / 2 - (this.style.pixelTop - this.prevY);
	
	this.recenterMap(x, y);
}
/**
 * 对地图控件划出一个区域范围后的回调函数，用于开窗放大。
 */
gis.map.panel.onBoxingEnd = function(){
	var box = this.box;
	box.style.visibility  = "hidden";
	if( this.cur_action == "zoomin" ){
		if( box.style.pixelWidth > 5 || box.style.pixelHeight > 5 )
			this.cur_action = "boxzoom";
		else
			this.cur_action = "zoomin";
	}
}
/**
 * 当地图控件结束画多线段或多边形后的回调函数，用于测量多线段的升序和多边形的面积
 * @param polyline 多线段或多边形的元素对象
 */
gis.map.panel.onDrawEnd = function(polyline){
	var oThis = this;
	var curAction = oThis.getDrawAction();
	if( curAction == "Polyline" )
		MapManager.scaleDistance(null, polyline.devicePoints, function(data){ gis.map.panel.showDistance(oThis, polyline, data); } );
	else if( curAction == "ClosedPolyline" )
		MapManager.scaleArea(null, polyline.devicePoints, function(data){ gis.map.panel.showArea(oThis, polyline, data); } );	
}
/**
 * 显示测量距离的结果
 * @param panel 地图控件的panel对象
 * @param polyline 多线段或多边形元素对象
 * @param data 测量结果的显示数据
 */
gis.map.panel.showDistance = function(panel, polyline, data){
	panel.tip.innerHTML = "距离 = " + data;
	panel.tip.style.pixelLeft  = polyline.offsetLeft;
	panel.tip.style.pixelTop   = polyline.offsetTop - panel.tip.offsetHeight - 5;
	if( panel.tip.style.pixelLeft + panel.tip.offsetWidth > panel.offsetWidth )
		panel.tip.style.pixelLeft = panel.offsetWidth - panel.tip.offsetWidth - 2;
	if( panel.tip.style.pixelTop < 0 )
		panel.tip.style.pixelTop = 2;
	panel.tip.style.visibility = "visible";
}
/**
 * 显示测量面积的结果
 * @param panel 地图控件的panel对象
 * @param polyline 多线段或多边形元素对象
 * @param data 测量结果的显示数据
 */
gis.map.panel.showArea = function(panel, polyline, data){
	panel.tip.innerHTML = "面积 = " + data;
	panel.tip.style.pixelLeft  = polyline.offsetLeft;
	panel.tip.style.pixelTop   = polyline.offsetTop - panel.tip.offsetHeight - 5;
	if( panel.tip.style.pixelLeft + panel.tip.offsetWidth > panel.offsetWidth )
		panel.tip.style.pixelLeft = panel.offsetWidth - panel.tip.offsetWidth - 2;
	if( panel.tip.style.pixelTop < 0 )
		panel.tip.style.pixelTop = 2;
	panel.tip.style.visibility = "visible";
}
/**
 * 刷新地图并将中心定位到以(x,y)坐标处
 * @param x 中心坐标的x值
 * @param y 中心坐标的y值
 */
gis.map.panel.recenterMap = function(x, y){
	var url = this.parent.imageurl + "?md=recenter&" + "x=" + x + "&" + "y=" + y;
	this.imgB.goTop = true;
	this.imgB.src   = url;
}
/**
 * 刷新地图、将中心定位到以(x,y)坐标处并放大step倍
 * @param x 中心坐标的x值
 * @param y 中心坐标的y值
 * @param step 放大的倍数
 */
gis.map.panel.zoominMap = function(x, y, step){
	var url = this.parent.imageurl + "?md=zoomin&" + "x=" + x + "&y=" + y + "&step=" + step;
	this.imgB.goTop = true;
	this.imgB.src   = url;
}
/**
 * 刷新地图、将中心定位到以(x,y)坐标处并缩小step倍
 * @param x 中心坐标的x值
 * @param y 中心坐标的y值
 * @param step 缩小的倍数
 */
gis.map.panel.zoomoutMap = function(x, y, step){
	var url = this.parent.imageurl + "?md=zoomout&" + "x=" + x + "&y=" + y + "&step=" + step;
	this.imgB.goTop = true;
	this.imgB.src   = url;
}
/**
 * 对地图进行开窗放大
 * @param x1 地图显示的左上角x坐标
 * @param y1 地图显示的左上角y坐标 
 * @param x2 地图显示的右下角x坐标
 * @param y2 地图显示的右下角y坐标 
 * @param rmap 开窗放大的地图名称
 */
gis.map.panel.boxzoomMap = function(x1, y1, x2, y2, rmap){
	var rmapName = rmap || "";
	var url = this.parent.imageurl + "?md=boxzoom&" + "x=" + x1 + "&y=" + y1 + "&x2=" + x2 + "&y2=" + y2 + "&rmap=" + rmapName;
	this.imgB.goTop = true;
	this.imgB.src   = url;
}
/**
 * 执行用户对地图控件的操作行为
 * @param panel 地图控件的panel对象
 */
gis.map.panel.doMapAction = function(panel){
	var zoombox = panel.box;
	if( panel.cur_action == "zoomin" ){
		panel.zoominMap(event.offsetX, event.offsetY, 1.8);
	}
	else if( panel.cur_action == "zoomout" ){
		panel.zoomoutMap(event.offsetX, event.offsetY, 1.8);
	}
	else if( panel.cur_action == "boxzoom" ){
		var x1, y1, x2, y2;
		x1 = zoombox.style.pixelLeft - panel.style.pixelLeft;
		y1 = zoombox.style.pixelTop - panel.style.pixelTop;
		x2 = x1 + zoombox.style.pixelWidth;
		y2 = y1 + zoombox.style.pixelHeight;
		panel.boxzoomMap(x1, y1, x2, y2);
		panel.cur_action = "zoomin";
	} if( panel.cur_action == "search" ){
		var x1, y1, x2, y2;
		x1 = zoombox.style.pixelLeft - panel.style.pixelLeft;
		y1 = zoombox.style.pixelTop - panel.style.pixelTop;
		x2 = x1 + zoombox.style.pixelWidth;
		y2 = y1 + zoombox.style.pixelHeight;	
		if( typeof(panel.parent.onQueryResult) == "function" ){
			if( zoombox.style.pixelWidth < 3 && zoombox.style.pixelHeight < 3 )
				MapManager.searchAtPoint(x1, y1, panel.parent.onQueryResult);
			else
				MapManager.searchWithinRect(x1, y1, x2, y2, panel.parent.onQueryResult);
		}
	}
}
/**
 * 当地图控件的img元素完成图像加载后触发的回调函数，这时将已完成加载的img元素放到前台用以显示
 */
gis.map.img.updateMap = function(){
	var img = event.srcElement;
	if( !img.goTop ){
		img.style.visibility = "visible";
		return;
	}
	var mapPanel = img.parentElement;

	mapPanel.style.pixelLeft = mapPanel.initX;
	mapPanel.style.pixelTop  = mapPanel.initY;
	
	mapPanel.imgA.style.visibility = "hidden";
	mapPanel.imgB.style.visibility = "visible";
	var temp = mapPanel.imgA;
	mapPanel.imgA = mapPanel.imgB;
	mapPanel.imgB = temp;
	img.goTop = false;
	if( typeof(mapPanel.parent.onMapUpdated) == "function" )
		mapPanel.parent.onMapUpdated();
	MapManager.getScale(null, function(data){ mapPanel.parent.setScale(data); });
}

