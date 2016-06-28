
ws.propertygrid = {};

ws.propertygrid.make = function(standin, uires, op_className){
	var resNode, grid, className = op_className || "";

	resNode = ws.res.findResourceNode(uires);
	if( resNode == null )
		return;
	grid = ws.createElement("div");
	grid.className = className;
	grid.groups = new Object();
	grid.panels = new Object();

	var el = null;
	if( typeof(standin) == "string" )
		el = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		el = standin;
	if( el != null && el != undefined ){
		el.replaceNode(grid);
		grid.id = el.id;
	}

	var groups, groupNode, groupPanel, res, label, name;
	groups = resNode.childNodes;
	for( var i = 0; i < groups.length; i++){
		groupNode = groups[i];
		if( groupNode.nodeName !== "group" )
			continue;
		res   = groupNode.attributes.getNamedItem("panel_res");
		label = groupNode.attributes.getNamedItem("label");
		name  = groupNode.attributes.getNamedItem("name");

		groupPanel = ws.createElement("div", grid);

		ws.panel.make(groupPanel, res.value);
		groupPanel.setTitle(label.value);

		grid.groups[name.value] = ws.propertygrid.generatePropertieGroup(groupPanel.client, groupNode.childNodes);
		grid.panels[name.value] = groupPanel;
	}
	
	grid.checkPropertyChange = ws.propertygrid.checkPropertyChange;
	grid.setPropertyOwner	 = ws.propertygrid.setPropertyOwner;
	grid.enableGroup		 = ws.propertygrid.enableGroup;
	grid.enableGroupItem	 = ws.propertygrid.enableGroupItem;
	grid.update				 = ws.propertygrid.update;
	grid.setGroupItemValue	 = ws.propertygrid.setGroupItemValue;
	grid.getGroupItemValue	 = ws.propertygrid.getGroupItemValue;

	ws.addEvent(grid, "deactivate", function(){ grid.checkPropertyChange() });

	return grid;
}

ws.propertygrid.checkPropertyChange = function(){
	var groupName, value, group, propRow, propName;

	if( !this.propOwner )
		return;

	for( groupName in this.groups ){
		group = this.groups[groupName];
		if( group  ){
			for( propName in this.groups[groupName] ){
				propRow = this.groups[groupName][propName];
				if( propRow ){
					value = this.getGroupItemValue(propRow);
					this.propOwner.setProperty(propName, value, groupName);
				}
			}
		}
	}
	if( typeof(this.propOwner.parentElement.fireChange) == "function" )
		this.propOwner.parentElement.fireChange();
}

ws.propertygrid.setPropertyOwner = function(owner){
	if( owner == this.propOwner )
		return;
	this.checkPropertyChange();

	if( owner == null ){
		this.propOwner = null;
	}
	else {
		if( typeof(owner.getProperties) == "function") 
			this.propOwner = owner;
		else 
			this.propOwner = null;
	}
	this.update();
}

ws.propertygrid.update = function(){
	var groupName, props, group, prop, propName;
	if( this.propOwner == null || this.propOwner == undefined ){
		for( groupName in this.panels ){
			this.enableGroup(groupName, false);
		}
		return;
	}
	props = this.propOwner.getProperties();
	for( groupName in this.groups ){
		group = props[groupName];
		if( group == null || group == undefined )
			this.enableGroup(groupName, false);
		else {
			this.enableGroup(groupName, true);
			for( propName in this.groups[groupName] ){
				prop = group[propName];
				if( prop == null || prop == undefined ){
					this.enableGroupItem(this.groups[groupName][propName], false);
				}
				else {
					this.enableGroupItem(this.groups[groupName][propName], true);
					this.setGroupItemValue(this.groups[groupName][propName], prop);
				}
			}
		}
	}
}

ws.propertygrid.generatePropertieGroup = function(parent, propNodes){
	var table = ws.createElement('<table class="ws_propgrid_table" cellpadding=0 cellspacing=0 style="empty-cells:show;>', parent);
	var tbody = ws.createElement("tbody", table);
	var tr, td, node, label, name, type, box, uires;
	var group = new Array();
	for( var i = 0; i < propNodes.length; i++ ){
		node = propNodes[i];
		label = node.attributes.getNamedItem("label");
		name  = node.attributes.getNamedItem("name");
		type  = node.attributes.getNamedItem("type");
		uires = node.attributes.getNamedItem("uires");

		tr = ws.createElement('<tr>', tbody);
		td = ws.createElement('<td class="td_name" nowrap>', tr);
		td.innerHTML = label.value;
		td = ws.createElement('<td class="td_value">', tr);
		switch(type.value){
		case "SelectBox":
			box = ws.createElement('<span class="ws_selectbox" style="width:100%">', td);
			ws.form.SelectBox.make(box, uires.value);
			break;
		case "ItemEditor":
			box = ws.createElement('<span style="width:100%">', td);
			box = ws.itemeditor.make(box, uires.value);
			box.setReadonly(true);
			break;
		default:
			box = ws.createElement('<span type="TextBox" style="width:100%" css="ws_textbox">', td);
			ws.form.TextBox.make(box);
			break;
		}
		
		group[name.value] = tr;
	}
	return group;
}

ws.propertygrid.enableGroup = function(groupName, bEnable){
	var panel = this.panels[groupName];
	var items = this.groups[groupName];
	var item, row;

	if( panel == null || panel == undefined || items == null || items == undefined )
		return;
	
	for( item in items ){
		row = items[item];
		this.enableGroupItem(row, bEnable);
	}
	if( bEnable == true ){
		panel.client.firstChild.style.display = "inline";
		panel.expendClient(true);
		panel.disabled = false;
	}
	else {
		panel.client.firstChild.style.display = "none";
		panel.disabled = true;
	}
}

ws.propertygrid.enableGroupItem = function(itemRow, bEnable){
	if( itemRow == null || itemRow == undefined )
		return;

	if( bEnable == true )
		itemRow.style.display = "inline";
	else 
		itemRow.style.display = "none";
}

ws.propertygrid.setGroupItemValue = function(itemRow, value){
	itemRow.cells[1].firstChild.setValue(value);
}

ws.propertygrid.getGroupItemValue = function(itemRow){
	var value = itemRow.cells[1].firstChild.getValue();
	if( !value )
		value = "";
	return value;
}

//==========ItemEditor=========================================================

ws.itemeditor = {};

ws.itemeditor.make = function(standin, uires){
	var detailBox = ws.form.DetailBox.make(standin, uires);
	if( !detailBox )
		return null;
	var table, tbody, tr, td, toolbar, sep;
	var client = ws.createElement('<div >');
	
	toolbar = ws.createElement('<div style="border:1 solid Silver">', client);
	table = ws.createElement('<table class="ws_toolbar" cellpadding="0" cellspacing="1">', toolbar);
	tbody = ws.createElement('tbody', table);
	tr	  = ws.createElement('tr', tbody);
	td	  = ws.createElement('<td class="toolbutton" style="height:20" nogray="true">', tr);
	td.innerHTML = ws.theme.iconHTML("add_item.gif") + '增加&nbsp;';
	td.onclick = function(){ ws.itemeditor.insertItem(client); };
	td	  = ws.createElement('<td class="toolbutton" nogray="true">', tr);
	td.innerHTML = ws.theme.iconHTML("modify_item.gif") + '修改&nbsp;';
	td.onclick = function(){ ws.itemeditor.modifyItem(client); };
	td	  = ws.createElement('<td class="toolbutton" nogray="true">', tr);
	td.innerHTML = ws.theme.iconHTML("delete.gif") + '删除&nbsp;';
	td.onclick = function(){ ws.itemeditor.deleteItem(client); };
	td	  = ws.createElement('<td class="toolbutton" nogray="true">', tr);
	td.innerHTML = ws.theme.iconHTML("move_up.gif") + '上移&nbsp;';
	td.onclick = function(){ ws.itemeditor.moveUp(client); };
	td	  = ws.createElement('<td class="toolbutton" nogray="true">', tr);
	td.innerHTML = ws.theme.iconHTML("move_down.gif") + '下移&nbsp;';
	td.onclick = function(){ ws.itemeditor.moveDown(client); };

	table = ws.createElement('<table class="ws_propgrid_table" style="width:100%" cellpadding="0" cellspacing="0">', client);
	tbody = ws.createElement('tbody', table);
	tr	  = ws.createElement('tr', tbody);
	td	  = ws.createElement('<td class="td_name" style="width:60%">', tr);
	td.innerHTML = "显示内容";
	td	  = ws.createElement('<td class="td_name" style="width:40%">', tr);
	td.innerHTML = "值";

	tr	  = ws.createElement('tr', tbody);
	td	  = ws.createElement('<td>', tr);
	td.innerHTML = '<input type="text" name="display" style="padding:0;width:100%;border:1 solid #E0E0E0;">';
	td	  = ws.createElement('<td>', tr);
	td.innerHTML = '<input type="text" name="value" style="width:100%;border:0;border:1 solid #E0E0E0;">';
	
	client.editorTable = table;

	sep = ws.createElement('span', client);
	sep.innerHTML = '<table><tr style="height:2"><td></td></tr></table>';

	table = ws.createElement('<table class="ws_propgrid_table" style="cursor:default" width="100%" cellpadding="0" cellspacing="0">', client);
	tbody = ws.createElement('tbody', table);
	
	client.previewTable = table;

	detailBox.detailClient.innerHTML = "";
	detailBox.detailClient.appendChild(client);
	detailBox.itemEditor = client;

	ws.selectable.make(client.previewTable, false, "tablerow");

	table.onchange = function(){ ws.itemeditor.onPreviewItemChange(client); };

	detailBox.onOK	   = ws.itemeditor.onOK;
	detailBox.onCancel = ws.itemeditor.onCancel;
	detailBox.onInit   = ws.itemeditor.onInit;
	detailBox.getValue = ws.itemeditor.getValue;
	detailBox.setValue = ws.itemeditor.setValue;

	detailBox.value = new Array();

	return detailBox;
}
ws.itemeditor.getValue = function(){
	return this.value;
}
ws.itemeditor.setValue = function(value){
	this.firstChild.rows[0].cells[0].firstChild.value = "Items(" + value.length + ")";
	this.value = value;
}
ws.itemeditor.onOK = function(){
	var editor  = this.itemEditor.editorTable;
	var preview = this.itemEditor.previewTable;
	var label, value;
	this.value.length = 0;
	for( var i = 0; i < preview.rows.length; i++ ){
		label = preview.rows[i].cells[0].innerHTML;
		value = preview.rows[i].cells[1].innerText;
		this.value.push({label:label, value:value});
	}
	this.firstChild.rows[0].cells[0].firstChild.value = "Items(" + this.value.length + ")";
	editor.rows[1].cells[0].firstChild.value = "";
	editor.rows[1].cells[1].firstChild.value = "";
}
ws.itemeditor.onCancel = function(){
	var editor  = this.itemEditor.editorTable;
	editor.rows[1].cells[0].firstChild.value = "";
	editor.rows[1].cells[1].firstChild.value = "";
}
ws.itemeditor.onInit = function(){
	var preview = this.itemEditor.previewTable;
	var label, value, tr, td;
	for( var i = preview.rows.length - 1; i >= 0; i-- ){
		preview.deleteRow(i);
	}
	for( var i = 0; i < this.value.length; i++ ){
		label = this.value[i].label;
		value = this.value[i].value;
		tr = preview.insertRow();
		td = ws.createElement('<td style="width:60%">', tr);
		td.innerHTML = label;
		td = ws.createElement('<td style="width:40%">', tr);
		td.innerText = value;
	}
}
ws.itemeditor.insertItem = function(client){
	var editor  = client.editorTable;
	var preview = client.previewTable;
	if( !editor.rows[1].cells[0].firstChild.value || !editor.rows[1].cells[1].firstChild.value )
		return;
	
	var tr, td;
	tr = preview.insertRow();
	td = ws.createElement('<td style="width:60%">', tr);
	td.innerHTML = editor.rows[1].cells[0].firstChild.value;
	td = ws.createElement('<td style="width:40%">', tr);
	td.innerText = editor.rows[1].cells[1].firstChild.value;

	editor.rows[1].cells[0].firstChild.value = "";
	editor.rows[1].cells[1].firstChild.value = "";
}
ws.itemeditor.deleteItem = function(client){
	var editor  = client.editorTable;
	var preview = client.previewTable;
	var items   = preview.getSelectedItems();
	
	if( items && items.length > 0 ){
		preview.deleteRow(items[0].rowIndex);
		editor.rows[1].cells[0].firstChild.value = "";
		editor.rows[1].cells[1].firstChild.value = "";
	}
}
ws.itemeditor.modifyItem = function(client){
	var editor  = client.editorTable;
	var preview = client.previewTable;
	if( !editor.rows[1].cells[0].firstChild.value || !editor.rows[1].cells[1].firstChild.value )
		return;
	var items   = preview.getSelectedItems();
	
	if( items && items.length > 0 ){
		items[0].cells[0].innerHTML = editor.rows[1].cells[0].firstChild.value;
		items[0].cells[1].innerText = editor.rows[1].cells[1].firstChild.value;
	}
}
ws.itemeditor.onPreviewItemChange = function(client){
	var editor  = client.editorTable;
	var preview = client.previewTable;
	var items   = preview.getSelectedItems();
	
	if( items && items.length > 0 ){
		editor.rows[1].cells[0].firstChild.value = items[0].cells[0].innerHTML;
		editor.rows[1].cells[1].firstChild.value = items[0].cells[1].innerText;
	}	
}
ws.itemeditor.moveUp = function(client){
	var preview = client.previewTable;
	var items   = preview.getSelectedItems();
	
	if( items && items.length > 0 ){
		if( items[0].rowIndex > 0 )
			preview.moveRow(items[0].rowIndex, items[0].rowIndex - 1);
	}	
}
ws.itemeditor.moveDown = function(client){
	var preview = client.previewTable;
	var items   = preview.getSelectedItems();
	
	if( items && items.length > 0 ){
		if( items[0].rowIndex < preview.rows.length - 1)
			preview.moveRow(items[0].rowIndex, items[0].rowIndex + 1);
	}	
}