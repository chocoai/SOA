ws.part = {};			//part表示部件，即其中的对象仅作为其他控件的部件，而不是完整的控件。
ws.part.dropdown = {};	//dropdown表示下拉框部件，下拉框由若干列组成，每一列包括一个图标和一行文字。

/**
 * 获取下拉框选项的值
 * @return 选项的值
 */
ws.part.dropdown.getItemValue = function(item){
	return item.value;
}
/**
 * 设置下拉框选项的值
 * @param value 选项的值
 */
ws.part.dropdown.setItemValue = function(item, value){
	item.value = value;
}

/**
 * 获取下拉框选项是否被选中
 * @return true选中/false未选中
 */
ws.part.dropdown.isItemSelected = function(item){
	return item.selected;
}
/**
 * 设置下拉框选项选中状态
 * @param selected 选中状态
 */
ws.part.dropdown.setItemSelected = function(item, selected){
	if( selected == true ){
		item.selected  = true;
		item.className = "ws_selected";
	}
	else {
		item.selected  = false;
		item.className = "ws_unselected";
	}
}

/**
 * 获取下拉框的选中值,如果未选中则返回null
 * @return 下拉框的选中值
 */
ws.part.dropdown.getValue = function(){
	return this.value;
}
/**
 * 设置下拉框的选中值,如果设置的值不在任务一个选项中,则其值变为null
 * @param value 选择框的选中值
 */
ws.part.dropdown.setValue = function(value, op_firechange){
	var selected = null, firechange = op_firechange || false;
	for(var i = 0; i < this.items.length; i++){
		if( this.getItemValue(this.items[i]) == value ){
			this.setItemSelected(this.items[i], false);
			selected = this.items[i].innerHTML;
			this.setItemSelected(this.items[i], true);
			this.value = value;
		} 
		else 
			this.setItemSelected(this.items[i], false);
	}
	if( selected == null )
		this.selectedHTML = "&nbsp;";
	else
		this.selectedHTML = selected;

	if( firechange == true ){
		if( typeof(this.onSelectChanged) == "function" )
			this.onSelectChanged(this, this.selectedHTML);
	}
}

/**
 * 下拉框的鼠标点击事件
 */
ws.part.dropdown.onclick = function(){
	var el = ws.getRealElement(window.event.srcElement, "type", "ws_SelectItem");
	if (el && el.getAttribute("type") == "ws_SelectItem") {
		var dropdown = el.parentElement.parentElement.parentElement.parentElement;
		dropdown.setValue(dropdown.getItemValue(el), true);
		dropdown.show(false);
	}
}
/**
 * 下拉框的鼠标移动事件
 */
ws.part.dropdown.onmouseover = function(){
	if( window.event.toElement == null || window.event.fromElement == null )
		return;
	var toEl = ws.getRealElement(window.event.toElement, "type", "ws_SelectItem");
	var fromEl = ws.getRealElement(window.event.fromElement, "type", "ws_SelectItem");
	if (toEl == fromEl) return;
	
	if (toEl && toEl.getAttribute("type") == "ws_SelectItem") {
		toEl.className = "ws_selected"
	}
	if (fromEl && fromEl.getAttribute("type") == "ws_SelectItem") {
		fromEl.className = "ws_unselected";
	}
	
	for( var i = 0; i < this.items.length; i++ ){
		if( this.items[i] != toEl)
			this.items[i].className = "ws_unselected";
	}
}

/**
 * 显示或隐藏下拉框
 * @param show true显示/false隐藏
 */
ws.part.dropdown.show = function(show){
	if( show == true ){
		if( typeof(ws.effect.fade) == "function" )
			ws.effect.fade(this, true);
		else 
			this.style.visibility = "visible";
	} else {
		if( typeof(ws.effect.fade) == "function" )
			ws.effect.fade(this, false);
		else 
			this.style.visibility = "hidden";
	}
}
/**
 * 追加一个选项
 */
ws.part.dropdown.appendItem = function(text, value, icon, tooltip){
	if( this.table == null || this.table == undefined )
		return;
	var tr, td, tbody;
	tbody = this.table.tBodies[0];

	tr = ws.createElement("<tr>", tbody);
	td = ws.createElement('<td nowrap type="ws_SelectItem">', tr);
	if( tooltip )
		src = '<span tooltip="' + tooltip + '">';
	else
		src = "<span>";
	if( icon )
		src += ws.theme.iconHTML(icon);
	src += text;
	src += "</span>";
	td.innerHTML  = src;
	if( value )
		td.value = value;
	else 
		td.value = "";
	this.items.push(td);
}

/**
 * 删除所有选项
 */
ws.part.dropdown.removeAll = function(){
	if( this.table == null || this.table == undefined )
		return;
	var count, tbody;
	tbody = this.table.tBodies[0];
	count = tbody.rows.length;
	for( var i = 0; i < count; i++ )
		tbody.deleteRow(0);
	this.items = [];
	this.selectedHTML = "&nbsp;";
}

ws.part.dropdown.make = function(resNode){
	var valueNode, selectNode, iconNode, tipNode, src;
	var dropdown, items, table, tbody, tr, td, selected = "&nbsp;";

	dropdown = ws.createElement("<div class='ws_dropdown'>");
	dropdown.items = new Array();
	if( resNode ){
		items = resNode.childNodes;
		table = ws.createElement('<table cellpadding="0" cellspacing="0" width="100%">', dropdown);
		tbody = ws.createElement("<tbody>", table);

		for( var i = 0; i < items.length; i++ ){
			if( items[i].nodeName !== "item" )
				continue;
			valueNode = items[i].attributes.getNamedItem("value");
			iconNode  = items[i].attributes.getNamedItem("icon");
			selectNode= items[i].attributes.getNamedItem("selected");
			tipNode   = items[i].attributes.getNamedItem("tooltip");
			tr = ws.createElement("<tr>", tbody);
			td = ws.createElement('<td nowrap type="ws_SelectItem">', tr);
			if( tipNode )
				src = '<span tooltip="' + tipNode.value + '">';
			else
				src = "<span>";
			if( iconNode )
				src += ws.theme.iconHTML(iconNode.value);
			src += items[i].firstChild.text;
			src += "</span>";
			td.innerHTML  = src;
			if( valueNode )
				td.value	 = valueNode.value;
			else 
				td.value	 = "";
			if( selectNode != null && selectNode.value == "true" ){
				td.selected    = true;
				selected	   = td.innerHTML;
				dropdown.value = valueNode.value;
			}
			else
				td.selected = false;
			dropdown.items.push(td);
		}
	}
	dropdown.table = table;

	dropdown.onclick         = ws.part.dropdown.onclick;
	dropdown.onmouseover     = ws.part.dropdown.onmouseover;
	dropdown.show            = ws.part.dropdown.show;
	dropdown.getValue	     = ws.part.dropdown.getValue;
	dropdown.setValue	     = ws.part.dropdown.setValue;
	dropdown.getItemValue    = ws.part.dropdown.getItemValue;
	dropdown.setItemValue    = ws.part.dropdown.setItemValue;
	dropdown.isItemSelected  = ws.part.dropdown.isItemSelected;
	dropdown.setItemSelected = ws.part.dropdown.setItemSelected;
	dropdown.appendItem		 = ws.part.dropdown.appendItem;
	dropdown.removeAll		 = ws.part.dropdown.removeAll;

	dropdown.style.visibility = "hidden";
	dropdown.style.zIndex     = 99;
	dropdown.style.position   = "absolute";	

	return dropdown;
}

/**
 * 关闭所有的下拉框
 */
ws.part.hideDropDowns = function (){
	var divs = document.all.tags("div");
	for( var i = 0; i < divs.length; i++){
		if( divs[i].className == "ws_dropdown" && divs[i].style.visibility == "visible")
			divs[i].show(false);
	}
}
ws.addEvent(document, "click", ws.part.hideDropDowns);