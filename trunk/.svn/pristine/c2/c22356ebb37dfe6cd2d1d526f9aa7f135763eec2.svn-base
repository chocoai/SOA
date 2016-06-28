ws.form.DetailBox = {};

/**
 * 获取选项的值
 * @return 选项的值
 */
ws.form.DetailBox.getValue = function(){
	return this.firstChild.rows[0].cells[0].firstChild.value;
}
/**
 * 设置选项的值
 * @param value 选项的值
 */
ws.form.DetailBox.setValue = function(value){
	this.firstChild.rows[0].cells[0].firstChild.value = value;
}

/**
 * 设置为只读
 * @param isReadonly 是否只读
 */
ws.form.DetailBox.setReadonly = function(isReadonly){
	this.firstChild.rows[0].cells[0].firstChild.readOnly = isReadonly;
}

/**
 * 点击确定按钮时触发的事件
 */
ws.form.DetailBox.onOK = function(){
	
}
/**
 * 点击取消按钮时触发的事件
 */
ws.form.DetailBox.onCancel = function(){
	
}

/**
 * 打开下拉框
 * @param el DetailBox元素对象
 */
ws.form.DetailBox.showDetail = function(el){
	if( el.disabled)
		return;
	if( typeof(el.onInit) == "function" )
		el.onInit();

	el.detail.show(true);
	if( !el.detail.autosized ){
		el.detail.autoSize();
		el.detail.setCenter();
		el.detail.autosized = true;

		el.detailClient.style.verticalAlign = "top";
	}
}

/**
 * 生成DetailBox对象
 * @param standin 自由表格的占位元素对象，可以是占位元素ID也可以是占位元素对象本身
 * @param op_uires 资源描述符(可选)，如果未指定则使用standin中的uires属性值
 * @return 生成的DetailBox元素
 */
ws.form.DetailBox.make = function(standin, op_uires){
	var uires = op_uires || null;
	var ctrlEl, resNode, titleNode, contentNode, title, content, el;
	var dropdown;
	
	if( typeof(standin) == "string" )
		ctrlEl = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		ctrlEl = standin;
	if( !ctrlEl )
		return null;

	if( !uires )
		uires = ctrlEl.getAttribute("uires");
	resNode = ws.res.findResourceNode(uires);
	if( !resNode )
		return;
	titleNode	= resNode.attributes.getNamedItem("title");
	contentNode = resNode.attributes.getNamedItem("content");

	if( titleNode )
		title = titleNode.value;
	else
		title = "";
	if( contentNode )
		content = contentNode.value;
	else
		content = "";
	
	var str = "";
	str += '<table class="ws_detailbox" cellpadding="0" cellspacing="1" style="width:100%">';
	str += '<tr><td><input type="text" name="" value="" style="width:100%;border:0;"></td>';
	str += '<td class="ws_detailbutton" onclick="ws.form.DetailBox.showDetail(this.parentElement.parentElement.parentElement.parentElement)">…</td></tr>';
	str += '</table>';

	ctrlEl.innerHTML = str;
	ctrlEl.getValue		  = ws.form.DetailBox.getValue;
	ctrlEl.setValue		  = ws.form.DetailBox.setValue;
	ctrlEl.setReadonly	  = ws.form.DetailBox.setReadonly;
	ctrlEl.onOK			  = ws.form.DetailBox.onOK;
	ctrlEl.onCancel		  = ws.form.DetailBox.onCancel;

	ctrlEl.detail = new ws.Dialog("");
	ctrlEl.detail.create();
	ctrlEl.detail.setTitle(title);

	var table, tbody, tr, td, button;
	table = ws.createElement('<table height="100%" width="100%">');
	tbody = ws.createElement("tbody", table);
	tr	  = ws.createElement("tr", tbody);
	td	  = ws.createElement("td", tr);
	if( content )
		el = document.getElementById(content);
	if( el )
		td.appendChild(el);
	else
		td.innerHTML = content;
	ctrlEl.detailClient = td;

	tr	   = ws.createElement("tr", tbody);
	td	   = ws.createElement('<td height="1" align="right" vAlign="bottom" nowrap>', tr);
	button = ws.createElement('<input type="button" value="确定" style="width:60">', td);
	button.onclick = function(){ ctrlEl.onOK(); ctrlEl.detail.show(false); };
	ws.createElement('<span style="width:10">&nbsp;</span>', td);
	button = ws.createElement('<input type="button" value="取消" style="width:60">', td);
	button.onclick = function(){ ctrlEl.onCancel(); ctrlEl.detail.show(false); }
	ctrlEl.detail.setContent(table);

	return ctrlEl;
}