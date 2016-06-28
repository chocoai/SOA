
//以下代码实现拖放功能
ws.dnd = {};

ws.dnd.dragTimeID = null;	//发起拖放事件的时间ID
ws.dnd.isDragging = false;	//标识当前是否正在拖放
ws.dnd.canDrop    = false;	//标识当前拖放的目标元素是否接受拖放对象
ws.dnd.dragSource = null;	//当前拖放的源元素
ws.dnd.dragTarget = null;	//当前拖放的目标元素
ws.dnd.dragType	  = null;	//当前拖放的元素类型
ws.dnd.dragItem	  = null;	//当前拖放的元素显示内容
ws.dnd.dropItem	  = null;	//当前拖放的对象内容
ws.dnd.targetElements = new Array(); //存放拖放目标元素的数组

/**
 * 初始化拖放行为
 */
ws.dnd.init = function(){
	ws.dnd.dragEl = document.createElement("<div class='ws_movable drag_object' style='position:absolute;visibility:hidden;'>");
	ws.dnd.dragEl.innerHTML = "<span>drag object</span>";
	document.body.appendChild(ws.dnd.dragEl);
	ws.dnd.targetElements.length = 0;
}

/**
 * 注册拖放目标元素，拖放的目标元素必须实现dragEnter、dragLeave和doDrop三个方法。
 * 当拖放的对象进入到目标元素中是将调用dragEnter方法，如果dragEnter方法返回true，
 * 则表示此目标元素可以接受拖放对象，否则表示不能接受拖放对象；当拖放对象从目标
 * 元素中离开时将调用dragLeave方法；当拖放对象进入到目标元素，并释放鼠标左键时将
 * 调用doDrop方法。方法定义如下：
 * function dragEnter(sourceType, dropITem) [return true/false]
 * function dragLeave(sourceType, dropItem)
 * function doDrop(sourceType, dropItem)
 * sourceType表示拖放对象的类型，dropItem表示拖放对象的值内容
 * @param el 拖放目标元素
 */
ws.dnd.regDragTarget = function(el){
	ws.dnd.targetElements.push(el);
}

/**
 * 清除注册的拖放目标元素
 */
ws.dnd.clearDragTarget = function(){
	ws.dnd.targetElements.length = 0;
}

/**
 * 准备发起拖放行为
 * @param sourceElement 拖放的源对象元素
 * @param sourceType 拖放对象的类型
 * @param dragItem 拖放对象的显示内容(HTML文本)
 * @param dropItem 拖放对象的值内容
 */
ws.dnd.doDrag = function(sourceElement, sourceType, dragItem, dropItem){
	ws.dnd.dragSource = sourceElement;
	ws.dnd.dragType   = sourceType;
	ws.dnd.dragItem	  = dragItem;
	ws.dnd.dropItem   = dropItem;

	ws.dnd.dragTimeID = setTimeout("ws.dnd.dragStart(" + event.clientX + "," + event.clientY + ")", 200);
}

/**
 * 取消拖放行为，当已经发起拖放操作但未实际执行时可取消拖放行为。
 */
ws.dnd.cancelDrag = function(){
	if( ws.dnd.dragTimeID != null )
		clearTimeout(ws.dnd.dragTimeID);
	ws.dnd.dragTimeID = null;
}

/**
 * 在施放过程中触发的事件
 */
ws.dnd.onDragging = function(){
	if( !ws.dnd.isDragging ){
		ws.dnd.cancelDrag();
		return;
	}

	if( ws.dnd.dragTarget != null && ws.dnd.dragTarget != undefined){
		if( ws.dnd.isInbound(ws.dnd.dragTarget, event.clientX, event.clientY) )
			return;
		if( typeof(ws.dnd.dragTarget.dragLeave) == "function")
			ws.dnd.dragTarget.dragLeave(ws.dnd.dragEl.sourceType, ws.dnd.dragEl.dropItem);
		ws.dnd.dragTarget = null;
		ws.dnd.dragEl.style.cursor = "not-allowed";
	}
	
	var dragTarget = null;
	for( var i = 0; i < ws.dnd.targetElements.length; i++ ){
		dragTarget = ws.dnd.targetElements[i];
		if( ws.dnd.isInbound(dragTarget, event.clientX, event.clientY) ){
			if( dragTarget != null && dragTarget != undefined && typeof(dragTarget.dragEnter) == "function"){
				ws.dnd.canDrop = dragTarget.dragEnter(ws.dnd.dragEl.sourceType, ws.dnd.dragEl.dropItem);
				if( ws.dnd.canDrop == true )
					ws.dnd.dragEl.style.cursor = "default";
				else ws.dnd.dragEl.style.cursor = "not-allowed";
			}
			ws.dnd.dragTarget = dragTarget;
			return;
		}
	}
}

/**
 * 执行拖放行为
 */
ws.dnd.dragStart = function(x, y){
	ws.dnd.isDragging = true;
	ws.dnd.dragEl.innerHTML = '<span class="drag_item" >' + ws.dnd.dragItem + '</span>';
	ws.dnd.dragEl.dropItem  = ws.dnd.dropItem;
	ws.dnd.dragEl.sourceType= ws.dnd.dragType;
	ws.dnd.dragEl.style.pixelLeft = document.body.scrollLeft + x - (ws.dnd.dragEl.clientWidth / 2);
	ws.dnd.dragEl.style.pixelTop  = document.body.scrollTop  + y - (ws.dnd.dragEl.clientHeight / 2);
	ws.dnd.dragEl.style.visibility = "visible";
	ws.dnd.dragEl.fireEvent("onmousedown");
}

/**
 * 结束拖放行为
 * @param sourceType 拖放对象的类型
 * @param dragItem 拖放对象的显示内容(HTML文本)
 * @param dropItem 拖放对象的值内容 
 */
ws.dnd.dragEnd = function(){
	if( ws.dnd.isDragging == true ){
		ws.dnd.isDragging = false;
		ws.dnd.dragEl.style.visibility = "hidden";
		if( ws.dnd.canDrop == true && ws.dnd.dragTarget != null && typeof(ws.dnd.dragTarget.doDrop) == "function"){
			if( ws.dnd.dragSource != null && typeof(ws.dnd.dragSource.dragEnd) == "function" )
				ws.dnd.dragSource.dragEnd(ws.dnd.dragTarget);
			if( ws.dnd.dragSource.parentElement != ws.dnd.dragTarget )
				ws.dnd.dragTarget.doDrop(ws.dnd.dragEl.sourceType, ws.dnd.dragEl.dropItem);
		}
		ws.dnd.dragSource = null;
		ws.dnd.dragTarget = null;
		ws.dnd.dragType	  = null;
		ws.dnd.dragItem	  = null;
		ws.dnd.dropItem	  = null;
	}
}

/**
 * 判断当前的拖放对象是否拖到了元素上面
 * @param el 拖放目标的元素对象
 * @param x 拖放对象的x座标
 * @param y 拖放对象的y座标
 */
ws.dnd.isInbound = function (el, x, y){
	x = x + document.body.scrollLeft;
	y = y + document.body.scrollTop;

	var rect = el.getBoundingClientRect();
	if( x > rect.left && x < rect.right && y > rect.top && y < rect.bottom )
		return true;
	else return false;
}

ws.addEvent(document, "mouseup",   ws.dnd.dragEnd);
ws.addEvent(document, "mousemove", ws.dnd.onDragging);
ws.addEvent(window, "load", ws.dnd.init);