ws.form.ComboBox = {};

/**
 * 获取组合框的选中值,如果未选中则返回null
 * @return 组合框的选中值
 */
ws.form.ComboBox.getValue = function(){
	return this.input.value;
}
/**
 * 设置组合框的选中值,如果设置的值不在任务一个选项中,则其值变为null
 * @param value 组合框的选中值
 */
ws.form.ComboBox.setValue = function(value){
	var selected = null;
	this.dropdown.setValue(value);
	selected = this.dropdown.getValue();
	if( selected == null ){
		this.input.value = ""
	}
	else 
		this.input.value = selected;
}

/**
 * 打开下拉框
 * @param el SelectBox元素对象
 */
ws.form.ComboBox.dropDown = function(el){
	var dropdown = el.children[1];
	if (dropdown.style.visibility == "")
		dropdown.style.visibility = "hidden";
	dropdown.setValue(el.getValue());
	if (dropdown.style.visibility == "hidden")
		dropdown.show(true);
	else
		dropdown.show(false);
}

ws.form.ComboBox.onSelectChanged = function(dropdown, selectedHTML){
	var selected, ctrlEl = dropdown.parent;
	selected = dropdown.getValue();
	if( selected == null ){
		ctrlEl.input.value = ""
	}
	else 
		ctrlEl.input.value = selected;
}

/**
 * 追加一个选项
 */
ws.form.ComboBox.appendItem = function(text, value, icon, tooltip){
	this.dropdown.appendItem(text, value, icon, tooltip);
}
/**
 * 删除所有选项
 */
ws.form.ComboBox.removeAll = function(){
	this.dropdown.removeAll();
	this.input.value = "";
}

/**
 * 生成ComboBox对象
 * @param standin 自由表格的占位元素对象，可以是占位元素ID也可以是占位元素对象本身
 * @param op_uires 资源描述符(可选)，如果未指定则使用standin中的uires属性值
 * @return 生成的ComboBox元素
 */
ws.form.ComboBox.make = function(standin, op_uires){
	var uires = op_uires || null;
	var ctrlEl, resNode, valueNode, selectNode, iconNode, src;
	var dropdown, items, table, tbody, tr, td;
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
	dropdown.onSelectChanged = ws.form.ComboBox.onSelectChanged;
	ctrlEl.dropdown = dropdown;
	
	var str = "";
	str += '<table class="ws_select_area" cellspacing="0" cellpadding="0">';
	str += '<tr>';
	str += '<td class="ws_selected_item"><input type="text" name="" style="width:100%;border-width:0"></td>';
	str += '<td align="CENTER" valign="MIDDLE" class="ws_select_button" id="' + selBtnId + '"';
	str += ' onmousedown="this.children[0].src=eval(this.currentStyle.button_img_d)"';
	str += ' onmouseup="this.children[0].src=eval(this.currentStyle.button_img_u)"';
	str += ' onmouseleave="this.children[0].src=eval(this.currentStyle.button_img_u)"';
	str += ' onmouseover="this.children[0].src=eval(this.currentStyle.button_img_o)" '
	str += ' onclick="ws.form.ComboBox.dropDown(this.parentElement.parentElement.parentElement.parentElement)" >';
	str += ' <img src="" border="0" ></td>';
	str += '</tr>';
	str += '</table>';

	ctrlEl.innerHTML = str;
	var selBtn = document.getElementById(selBtnId);
	selBtn.children[0].src = eval(selBtn.currentStyle.button_img_u);
	ctrlEl.appendChild(dropdown);
	ctrlEl.getValue = ws.form.ComboBox.getValue;
	ctrlEl.setValue = ws.form.ComboBox.setValue;
	ctrlEl.appendItem = ws.form.ComboBox.appendItem;
	ctrlEl.removeAll  = ws.form.ComboBox.removeAll;
	
	var input  = ctrlEl.children[0].rows[0].cells[0].children[0];
	if( selected != null )
		input.value = selected;
	else
		input.value = value;
	ctrlEl.input = input;
	return ctrlEl;
}

