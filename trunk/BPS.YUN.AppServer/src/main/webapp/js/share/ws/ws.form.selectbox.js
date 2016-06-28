ws.form.SelectBox = {};

/**
 * 获取选择框的选中值,如果未选中则返回null
 * @return 选择框的选中值
 */
ws.form.SelectBox.getValue = function(){
	return this.dropdown.getValue();
}
/**
 * 设置选择框的选中值,如果设置的值不在任务一个选项中,则其值变为null
 * @param value 选择框的选中值
 */
ws.form.SelectBox.setValue = function(value){
	var selected;
	this.dropdown.setValue(value);

	this.children[0].rows[0].cells[0].innerHTML = this.dropdown.selectedHTML;
	
	selected = this.children[0].rows[0].cells[0].children[0];
	if( selected != null && selected != "undefined")
		selected.removeAttribute("type");
}

/**
 * 打开下拉框
 * @param el SelectBox元素对象
 */
ws.form.SelectBox.dropDown = function(el){
	if( el.disabled)
		return;

	var dropdown = el.dropdown;
	if (dropdown.style.visibility == "")
		dropdown.style.visibility = "hidden";
		
	if (dropdown.style.visibility == "hidden")
		dropdown.show(true);
	else
		dropdown.show(false);
}

ws.form.SelectBox.onSelectChanged = function(dropdown, selectedHTML){
	var selected, ctrlEl = dropdown.parent;
	ctrlEl.children[0].rows[0].cells[0].innerHTML = selectedHTML;
	
	selected = this.children[0].rows[0].cells[0].children[0];
	if( selected != null && selected != "undefined")
		selected.removeAttribute("type");
}

/**
 * 追加一个选项
 */
ws.form.SelectBox.appendItem = function(text, value, icon, tooltip){
	this.dropdown.appendItem(text, value, icon, tooltip);
}
/**
 * 删除所有选项
 */
ws.form.SelectBox.removeAll = function(){
	this.dropdown.removeAll();
	this.children[0].rows[0].cells[0].innerHTML = "&nbsp;";
}

/**
 * 生成SelectBox对象
 * @param standin 自由表格的占位元素对象，可以是占位元素ID也可以是占位元素对象本身
 * @param op_uires 资源描述符(可选)，如果未指定则使用standin中的uires属性值
 * @return 生成的SelectBox元素
 */
ws.form.SelectBox.make = function(standin, op_uires){
	var uires = op_uires || null;
	var ctrlEl, resNode, src;
	var dropdown;
	var selected = null;
	var selBtnId  = "btn" + ws.generateId();
	
	if( typeof(standin) == "string" )
		ctrlEl = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		ctrlEl = standin;
	if( !ctrlEl )
		return null;
	var value = ctrlEl.value || "";

	if( !uires )
		uires = ctrlEl.getAttribute("uires");
	resNode = ws.res.findResourceNode(uires);
	
	dropdown = ws.part.dropdown.make(resNode);
	dropdown.setValue(value);
	dropdown.parent = ctrlEl;
	dropdown.onSelectChanged = ws.form.SelectBox.onSelectChanged;
	
	var str = "";
	str += '<table class="ws_select_area" cellspacing="0" cellpadding="0" ';
	str += ' onclick="ws.form.SelectBox.dropDown(this.parentElement)">';
	str += '<tr>';
	if( selected != null )
		str += '<td class="ws_selected_item">'+ selected+'</td>';
	else str += '<td class="ws_selected_item">&nbsp;</td>';
	str += '<td align="CENTER" valign="MIDDLE" class="ws_select_button" id="' + selBtnId + '"';
	str += ' onmousedown="this.children[0].src=eval(this.currentStyle.button_img_d)"';
	str += ' onmouseup="this.children[0].src=eval(this.currentStyle.button_img_u)"';
	str += ' onmouseleave="this.children[0].src=eval(this.currentStyle.button_img_u)"';
	str += ' onmouseover="this.children[0].src=eval(this.currentStyle.button_img_o)">';
	str += ' <img src="" border="0" ></td>';
	str += '</tr>';
	str += '</table>';

	ctrlEl.innerHTML = str;
	var selBtn = document.getElementById(selBtnId);
	selBtn.children[0].src = eval(selBtn.currentStyle.button_img_u);
	ctrlEl.appendChild(dropdown);
	ctrlEl.dropdown = dropdown;
	ctrlEl.getValue = ws.form.SelectBox.getValue;
	ctrlEl.setValue = ws.form.SelectBox.setValue;
	ctrlEl.appendItem = ws.form.SelectBox.appendItem;
	ctrlEl.removeAll  = ws.form.SelectBox.removeAll;

	selected = ctrlEl.children[0].rows[0].cells[0].children[0];
	if( selected != null && selected != "undefined")
		selected.removeAttribute("type");
	return ctrlEl;
}


