
//设计时控件(Design Time Control)的名称空间
ws.dtc = {};

ws.dtc.getUIResource = function(){
	return null;
}

ws.dtc.setContainerProperty = function(dtcObj, name, value){
	switch(name){
	case "hAlign": 
		if( dtcObj.parentElement.align )
			dtcObj.parentElement.align = value;
		dtcObj.prop["cell"]["hAlign"] = value;
		break;
	case "vAlign": 
		if( dtcObj.parentElement.vAlign )
			dtcObj.parentElement.vAlign = value;
		dtcObj.prop["cell"]["vAlign"] = value;
		break;
	};
}
ws.dtc.getContainerProperty = function(dtcObj){
	if( dtcObj.parentElement.align == "" )
		dtcObj.parentElement.align = "left";
	if( dtcObj.parentElement.vAlign == "" )
		dtcObj.parentElement.vAlign = "middle";
	dtcObj.prop["cell"]["hAlign"] = dtcObj.parentElement.align;
	dtcObj.prop["cell"]["vAlign"] = dtcObj.parentElement.vAlign;
}
ws.dtc.getContainerHTML = function(dtcObj){
	return 'align="' + dtcObj.prop["cell"]["hAlign"] +'" valign="' + dtcObj.prop["cell"]["vAlign"] + '"';
}

ws.dtc.load = function(parent, el){
	var type = el.type;
	if( parent.innerHTML == "&nbsp;" )
		parent.innerHTML = "";
	switch(type){
	case "Label":
		return ws.dtc.label.load(parent, el);
		break;
	case "TextBox":
		return ws.dtc.textbox.load(parent, el);
		break;
	case "button":
		return ws.dtc.button.load(parent, el);
		break;
	case "CheckBox":
		return ws.dtc.checkbox.load(parent, el);
		break;
	case "RadioBox":
		return ws.dtc.radiobox.load(parent, el);
		break;
	case "RadioGroup":
		return ws.dtc.groupbox.load(parent, el);
		break;
	case "ComboBox":
		return ws.dtc.combobox.load(parent, el);
		break;
	case "SelectBox":
		return ws.dtc.selectbox.load(parent, el);
		break;
	}
	if( el.tagName == "FIELDSET" )
		return ws.dtc.groupbox.load(parent, el);
	if( parent.innerHTML == "" )
		parent.innerHTML = "&nbsp;";
	return null;
}
//========Label控件============================================================
ws.dtc.label = {};

ws.dtc.label.prop = new Object();
ws.dtc.label.prop["other"] = new Object();
ws.dtc.label.prop["other"]["label"] = "";

ws.dtc.label.prop["cell"] = new Object();
ws.dtc.label.prop["cell"]["hAlign"] = "";
ws.dtc.label.prop["cell"]["vAlign"] = "";

ws.dtc.label.make = function(el){
	el.prop = ws.clone(ws.dtc.label.prop);
	el.prop["other"]["label"] = "Label";
	el.getProperties = ws.dtc.label.getProperties;
	el.setProperty	 = ws.dtc.label.setProperty;
	el.getProperty	 = ws.dtc.label.getProperty;
	el.getType		 = ws.dtc.label.getType;
	el.toHTML		 = ws.dtc.label.toHTML;
	el.getUIResource = ws.dtc.getUIResource;
	el.getContainerHTML = ws.dtc.getContainerHTML;
}
ws.dtc.label.getType = function(){
	return "Label";
}
ws.dtc.label.getProperties = function(){
	ws.dtc.getContainerProperty(this);
	return this.prop;
}

ws.dtc.label.setProperty = function(name, value, groupName){
	switch(groupName){
	case "other":
		switch(name){
		case "label": 
				this.innerHTML = value;
				this.prop["other"]["label"] = value;
				break;
		};
		break;
	case "cell":
		ws.dtc.setContainerProperty(this, name, value);
		break;
	}
}

ws.dtc.label.getProperty = function(name, groupName){
	var value = "";
	if( this.prop[groupName] )
		return this.prop[groupName][name];
	return "";
}

ws.dtc.label.toHTML = function(){
	return '<span type="Label">' + this.prop["other"]["label"] + "</span>";
}

ws.dtc.label.load = function(parent, el){
	var dtcEl = ws.createElement("span", parent);
	ws.dtc.label.make(dtcEl);

	dtcEl.setProperty("label",  el.innerHTML, "other");

	dtcEl.setProperty("hAlign", el.parentElement.align,  "cell");
	dtcEl.setProperty("vAlign", el.parentElement.vAlign, "cell");

	return dtcEl;
}

//========TextBox控件==========================================================
ws.dtc.textbox = {};
ws.dtc.textbox.prop = new Object();
ws.dtc.textbox.prop["other"] = new Object();
ws.dtc.textbox.prop["other"]["width"] = "100%";
ws.dtc.textbox.prop["other"]["value"] = "";
ws.dtc.textbox.prop["other"]["name"]  = "";

ws.dtc.textbox.prop["cell"] = new Object();
ws.dtc.textbox.prop["cell"]["hAlign"] = "";
ws.dtc.textbox.prop["cell"]["vAlign"] = "";


ws.dtc.textbox.make = function(el){
	el.prop = ws.clone(ws.dtc.textbox.prop);
	el.prop["other"]["name"] = "txb" + ws.generateId();

	el.getProperties = ws.dtc.textbox.getProperties;
	el.setProperty	 = ws.dtc.textbox.setProperty;
	el.getProperty	 = ws.dtc.textbox.getProperty;
	el.getType		 = ws.dtc.textbox.getType;
	el.toHTML		 = ws.dtc.textbox.toHTML;
	el.getUIResource = ws.dtc.getUIResource;
	el.getContainerHTML = ws.dtc.getContainerHTML;
}

ws.dtc.textbox.getType = function(){
	return "TextBox";
}
ws.dtc.textbox.getProperties = function(){
	ws.dtc.getContainerProperty(this);
	return this.prop;
}

ws.dtc.textbox.setProperty = function(name, value, groupName){
	switch(groupName){
	case "other":
		switch(name){
		case "width": 
				this.style.width = value;
				this.prop["other"]["width"] = value;
				break;
		case "name": 
				this.name = value;
				this.prop["other"]["name"] = value;
				break;
		case "value": 
				this.value = value;
				this.prop["other"]["value"] = value;
				break;
		};
		break;
	case "cell":
		ws.dtc.setContainerProperty(this, name, value);
		break;
	}
}

ws.dtc.textbox.getProperty = function(name, groupName){
	var value = "";
	if( this.prop[groupName] )
		return this.prop[groupName][name];
	return "";
}

ws.dtc.textbox.toHTML = function(){
	return '<span name="' + this.prop["other"]["name"] + '" type="TextBox" ' + 'width="' + this.prop["other"]["width"] + '" '
		+ ' value = "' + this.prop["other"]["value"] + '"></span>';
}

ws.dtc.textbox.load = function(parent, el){
	var dtcEl = ws.createElement('<input type="text" name="" value="" style="cursor:default">', parent);
	ws.dtc.textbox.make(dtcEl);

	dtcEl.setProperty("width", el.width, "other");
	dtcEl.setProperty("name",  el.name,	 "other");
	dtcEl.setProperty("value", el.value, "other");

	dtcEl.setProperty("hAlign", el.parentElement.align,  "cell");
	dtcEl.setProperty("vAlign", el.parentElement.vAlign, "cell");

	return dtcEl;
}

//========CheckBox控件=========================================================
ws.dtc.checkbox = {};
ws.dtc.checkbox.prop = new Object();
ws.dtc.checkbox.prop["other"] = new Object();
ws.dtc.checkbox.prop["other"]["label"] = "";
ws.dtc.checkbox.prop["other"]["value"] = "on";
ws.dtc.checkbox.prop["other"]["checked"] = "false";
ws.dtc.checkbox.prop["other"]["name"]   = "";

ws.dtc.checkbox.prop["cell"] = new Object();
ws.dtc.checkbox.prop["cell"]["hAlign"] = "";
ws.dtc.checkbox.prop["cell"]["vAlign"] = "";

ws.dtc.checkbox.make = function(el){
	el.prop = ws.clone(ws.dtc.checkbox.prop);
	el.prop["other"]["label"] = "CheckBox";
	el.prop["other"]["name"]  = "chk" + ws.generateId();

	el.getProperties = ws.dtc.checkbox.getProperties;
	el.setProperty	 = ws.dtc.checkbox.setProperty;
	el.getProperty	 = ws.dtc.checkbox.getProperty;
	el.getType		 = ws.dtc.checkbox.getType;
	el.toHTML		 = ws.dtc.checkbox.toHTML;
	el.getUIResource = ws.dtc.getUIResource;
	el.getContainerHTML = ws.dtc.getContainerHTML;
	el.children[0].onclick = function(){return false;};
}
ws.dtc.checkbox.getType = function(){
	return "CheckBox";
}
ws.dtc.checkbox.getProperties = function(){
	ws.dtc.getContainerProperty(this);
	return this.prop;
}

ws.dtc.checkbox.setProperty = function(name, value, groupName){
	switch(groupName){
	case "other":
		switch(name){
		case "label": 
			this.children[1].innerHTML = value;
			this.prop["other"]["label"] = value;
			break;
		case "name": 
			this.children[0].name = value;
			this.prop["other"]["name"] = value;
			break;
		case "value": 
			this.children[0].value = value;
			this.prop["other"]["value"] = value;
			break;
		case "checked":
			if( value == true || value == "true" )
				this.children[0].checked = true;
			else
				this.children[0].checked = false;
			this.prop["other"]["checked"] = value;
			break;
		};
		break;
	case "cell":
		ws.dtc.setContainerProperty(this, name, value);
		break;
	}
}

ws.dtc.checkbox.getProperty = function(name, groupName){
	var value = "";
	if( this.prop[groupName] )
		return this.prop[groupName][name];
	return "";
}

ws.dtc.checkbox.toHTML = function(){
	return '<span type="CheckBox" name="' + this.prop["other"]["name"] + '" value="' 
		+ this.prop["other"]["value"] + '" checked="' + this.prop["other"]["checked"] + '" >' 
		+ this.prop["other"]["label"] + "</span>";
}

ws.dtc.checkbox.load = function(parent, el){
	var dtcEl = ws.createElement('span', parent);
	var id    = "chk" + ws.generateId();
	ws.createElement('<input type="checkbox" id="' + id + '">', dtcEl);
	ws.createElement('<label for="' + id + '">', dtcEl);

	ws.dtc.checkbox.make(dtcEl);

	dtcEl.setProperty("label",   el.innerHTML, "other");
	dtcEl.setProperty("name",    el.name,	   "other");
	dtcEl.setProperty("value",   el.value,	   "other");
	dtcEl.setProperty("checked", el.checked,   "other");

	dtcEl.setProperty("hAlign", el.parentElement.align,  "cell");
	dtcEl.setProperty("vAlign", el.parentElement.vAlign, "cell");

	return dtcEl;
}
//========RadioBox控件=========================================================
ws.dtc.radiobox = {};
ws.dtc.radiobox.prop = new Object();
ws.dtc.radiobox.prop["other"] = new Object();
ws.dtc.radiobox.prop["other"]["label"] = "";
ws.dtc.radiobox.prop["other"]["name"]  = "";
ws.dtc.radiobox.prop["other"]["value"] = "on";
ws.dtc.radiobox.prop["other"]["checked"] = "false";

ws.dtc.radiobox.prop["cell"] = new Object();
ws.dtc.radiobox.prop["cell"]["hAlign"] = "";
ws.dtc.radiobox.prop["cell"]["vAlign"] = "";

ws.dtc.radiobox.make = function(el){
	el.prop = ws.clone(ws.dtc.radiobox.prop);
	el.prop["other"]["label"] = "RadioBox";
	el.prop["other"]["name"]  = "rdo" + ws.generateId();

	el.getProperties = ws.dtc.radiobox.getProperties;
	el.setProperty	 = ws.dtc.radiobox.setProperty;
	el.getProperty	 = ws.dtc.radiobox.getProperty;
	el.getType		 = ws.dtc.radiobox.getType;
	el.toHTML		 = ws.dtc.radiobox.toHTML;
	el.getUIResource = ws.dtc.getUIResource;
	el.getContainerHTML = ws.dtc.getContainerHTML;
	el.children[0].onclick = function(){return false;};
}
ws.dtc.radiobox.getType = function(){
	return "RadioBox";
}
ws.dtc.radiobox.getProperties = function(){
	ws.dtc.getContainerProperty(this);
	return this.prop;
}

ws.dtc.radiobox.setProperty = function(name, value, groupName){
	switch(groupName){
	case "other":
		switch(name){
		case "label": 
			this.children[1].innerHTML = value;
			this.prop["other"]["label"] = value;
			break;
		case "name": 
			this.children[0].name = value;
			this.prop["other"]["name"] = value;
			break;
		case "value": 
			this.children[0].value = value;
			this.prop["other"]["value"] = value;
			break;
		case "checked":
			if( value == true || value == "true" )
				this.children[0].checked = true;
			else
				this.children[0].checked = false;
			this.prop["other"]["checked"] = value;
			break;
		};
		break;
	case "cell":
		ws.dtc.setContainerProperty(this, name, value);
		break;
	}
}

ws.dtc.radiobox.getProperty = function(name, groupName){
	var value = "";
	if( this.prop[groupName] )
		return this.prop[groupName][name];
	return "";
}

ws.dtc.radiobox.toHTML = function(){
	return '<span type="RadioBox" name="' + this.prop["other"]["name"] + '" value="' 
		+ this.prop["other"]["value"] + '" checked="' + this.prop["other"]["checked"] + '" >' 
		+ this.prop["other"]["label"] + "</span>";
}
ws.dtc.radiobox.load = function(parent, el){
	var dtcEl = ws.createElement('span', parent);
	var id    = "rdo" + ws.generateId();
	ws.createElement('<input type="radio" id="' + id + '">', dtcEl);
	ws.createElement('<label for="' + id + '">', dtcEl);

	ws.dtc.radiobox.make(dtcEl);

	dtcEl.setProperty("label",   el.innerHTML, "other");
	dtcEl.setProperty("name",    el.name,	   "other");
	dtcEl.setProperty("value",   el.value,	   "other");
	dtcEl.setProperty("checked", el.checked,   "other");

	dtcEl.setProperty("hAlign", el.parentElement.align,  "cell");
	dtcEl.setProperty("vAlign", el.parentElement.vAlign, "cell");

	return dtcEl;
}
//========Button控件===========================================================
ws.dtc.button = {};
ws.dtc.button.prop = new Object();
ws.dtc.button.prop["other"] = new Object();
ws.dtc.button.prop["other"]["label"] = "";
ws.dtc.button.prop["other"]["width"] = "";

ws.dtc.button.prop["cell"] = new Object();
ws.dtc.button.prop["cell"]["hAlign"] = "";
ws.dtc.button.prop["cell"]["vAlign"] = "";

ws.dtc.button.make = function(el){
	el.prop = ws.clone(ws.dtc.button.prop);
	el.prop["other"]["label"] = "Button";
	el.getProperties = ws.dtc.button.getProperties;
	el.setProperty	 = ws.dtc.button.setProperty;
	el.getProperty	 = ws.dtc.button.getProperty;
	el.getType		 = ws.dtc.button.getType;
	el.toHTML		 = ws.dtc.button.toHTML;
	el.getUIResource = ws.dtc.getUIResource;
	el.getContainerHTML = ws.dtc.getContainerHTML;
	el.onclick = function(){return false;};
}
ws.dtc.button.getType = function(){
	return "Button";
}
ws.dtc.button.getProperties = function(){
	ws.dtc.getContainerProperty(this);
	return this.prop;
}

ws.dtc.button.setProperty = function(name, value, groupName){
	switch(groupName){
	case "other":
		switch(name){
		case "label": 
			this.value = value;
			this.prop["other"]["label"] = value;
			break;
		case "width": 
			this.style.width = value;
			this.prop["other"]["width"] = value;
			break;
		};
		break;
	case "cell":
		ws.dtc.setContainerProperty(this, name, value);
		break;
	}
}

ws.dtc.button.getProperty = function(name, groupName){
	var value = "";
	if( this.prop[groupName] )
		return this.prop[groupName][name];
	return "";
}

ws.dtc.button.toHTML = function(){
	return '<input type="button" value="' + this.prop["other"]["label"] + '" ' + 'style="width:' + this.prop["other"]["width"] + '" >';
}

ws.dtc.button.load = function(parent, el){
	var dtcEl = ws.createElement('<input type="button">', parent);

	ws.dtc.button.make(dtcEl);

	dtcEl.setProperty("label", el.value,       "other");
	dtcEl.setProperty("width", el.style.width, "other");

	dtcEl.setProperty("hAlign", el.parentElement.align,  "cell");
	dtcEl.setProperty("vAlign", el.parentElement.vAlign, "cell");

	return dtcEl;
}
//========GroupBox控件=========================================================
ws.dtc.groupbox = {};
ws.dtc.groupbox.prop = new Object();
ws.dtc.groupbox.prop["other"] = new Object();
ws.dtc.groupbox.prop["other"]["label"] = "";
ws.dtc.groupbox.prop["other"]["name"]  = "";
ws.dtc.groupbox.prop["other"]["width"] = "";
ws.dtc.groupbox.prop["other"]["group_type"] = "RadioGroup";

ws.dtc.groupbox.prop["cell"] = new Object();
ws.dtc.groupbox.prop["cell"]["hAlign"] = "";
ws.dtc.groupbox.prop["cell"]["vAlign"] = "";

ws.dtc.groupbox.make = function(el){
	el.prop = ws.clone(ws.dtc.groupbox.prop);
	el.prop["other"]["label"] = "GroupBox";
	el.prop["other"]["name"]  = "grp" + ws.generateId();

	el.getProperties = ws.dtc.groupbox.getProperties;
	el.setProperty	 = ws.dtc.groupbox.setProperty;
	el.getProperty	 = ws.dtc.groupbox.getProperty;
	el.getType		 = ws.dtc.groupbox.getType;
	el.getUIResource = ws.dtc.getUIResource;
	el.getContainerHTML = ws.dtc.getContainerHTML;
	el.toHTML		 = ws.dtc.groupbox.toHTML;
}

ws.dtc.groupbox.getType = function(){
	return "GroupBox";
}
ws.dtc.groupbox.getProperties = function(){
	ws.dtc.getContainerProperty(this);
	return this.prop;
}

ws.dtc.groupbox.setProperty = function(name, value, groupName){
	switch(groupName){
	case "other":
		switch(name){
		case "label": 
			this.firstChild.innerHTML = value;
			this.prop["other"]["label"] = value;
			break;
		case "name": 
			this.name = value;
			this.prop["other"]["name"] = value;
			var childs = this.all.tags("INPUT");
			for( var i = 0; i < childs.length; i++ ){
				if( childs[i].type == "radio" )
					childs[i].parentElement.setProperty(name, value, groupName);
			}
			break;
		case "width": 
			this.style.width = value;
			this.prop["other"]["width"] = value;
			break;
		case "group_type":
			this.prop["other"]["group_type"] = value;
			break;
		};
		break;
	case "cell":
		ws.dtc.setContainerProperty(this, name, value);
		break;
	}
}

ws.dtc.groupbox.getProperty = function(name, groupName){
	var value = "";
	if( this.prop[groupName] )
		return this.prop[groupName][name];
	return "";
}

ws.dtc.groupbox.toHTML = function(){
	var childs, src = "";
	var isRadioGroup = this.prop["other"]["group_type"] == "RadioGroup" ? true : false;
	if( isRadioGroup ){
		src += '<span name="' + this.prop["other"]["name"] + '" type="RadioGroup" title="' + this.prop["other"]["label"] + '"';
		src += 'style="width:' + this.prop["other"]["width"] + '" ';
		src += '>';
		for( var i = 0; i < this.children.length; i++ ){
			if( this.children[i].tagName == "LEGEND" )
				continue;
			src += this.children[i].toHTML();
		}
		src += '</span>';
	} else {
		src += '<fieldset style="width:' + this.prop["other"]["width"] + '" ';
		src += 'name="' + this.prop["other"]["name"] + '">';
		src += '<legend>' + this.prop["other"]["label"] + '</legend>';
		for( var i = 0; i < this.children.length; i++ ){
			if( this.children[i].tagName == "LEGEND" )
				continue;
			src += this.children[i].toHTML();
		}
		src += '</fieldset>';
	}
	return src;
}

ws.dtc.groupbox.load = function(parent, el){
	var dtcEl = ws.createElement('<fieldset>', parent);
	var label = ws.createElement('<legend style="cursor:default">', dtcEl);
	var child, childNo = 0;

	ws.dtc.groupbox.make(dtcEl);
	if( el.type == "RadioGroup" ){
		dtcEl.setProperty("label", el.title, "other");
		dtcEl.setProperty("group_type", "RadioGroup", "other");
	}
	else {
		dtcEl.setProperty("label", el.firstChild.innerHTML, "other");
		dtcEl.setProperty("group_type", "CommonGroup", "other");
	}
	dtcEl.setProperty("name",  el.name, "other");
	dtcEl.setProperty("width", el.style.width, "other");

	if( el.parentElement.align )
		dtcEl.setProperty("hAlign", el.parentElement.align,  "cell");
	if( el.parentElement.vAlign )
		dtcEl.setProperty("vAlign", el.parentElement.vAlign, "cell");

	for( var i = 0; i < el.children.length; i++ ){
		child = el.children[i];
		if( child.tagName == "LEGEND" )
			continue;
		ws.dtc.load(dtcEl, child);
		childNo++;
	}
	if( childNo == 0 )
		dtcEl.innerHTML += "&nbsp;";
	return dtcEl;
}
//========ComboBox控件=========================================================
ws.dtc.combobox = {};
ws.dtc.combobox.prop = new Object();
ws.dtc.combobox.prop["other"] = new Object();
ws.dtc.combobox.prop["other"]["width"] = "100%";
ws.dtc.combobox.prop["other"]["value"] = "";
ws.dtc.combobox.prop["other"]["name"]  = "";
ws.dtc.combobox.prop["other"]["items"] = null;

ws.dtc.combobox.prop["cell"] = new Object();
ws.dtc.combobox.prop["cell"]["hAlign"] = "";
ws.dtc.combobox.prop["cell"]["vAlign"] = "";

ws.dtc.combobox.make = function(el){
	el.prop = ws.clone(ws.dtc.combobox.prop);
	el.prop["other"]["items"] = new Array();
	el.prop["other"]["name"]  = "cbo" + ws.generateId();

	el.getProperties = ws.dtc.combobox.getProperties;
	el.setProperty	 = ws.dtc.combobox.setProperty;
	el.getProperty	 = ws.dtc.combobox.getProperty;
	el.getType		 = ws.dtc.combobox.getType;
	el.toHTML		 = ws.dtc.combobox.toHTML;
	el.getUIResource = ws.dtc.combobox.getUIResource;
	el.getContainerHTML = ws.dtc.getContainerHTML;
}
ws.dtc.combobox.getType = function(){
	return "ComboBox";
}
ws.dtc.combobox.getProperties = function(){
	ws.dtc.getContainerProperty(this);
	return this.prop;
}

ws.dtc.combobox.setProperty = function(name, value, groupName){
	switch(groupName){
	case "other":
		switch(name){
		case "width": 
				this.style.width = value;
				this.prop["other"]["width"] = value;
				break;
		case "name": 
				this.name = value;
				this.prop["other"]["name"] = value;
				break;
		case "value": 
				this.rows[0].cells[0].firstChild.value = value;
				this.prop["other"]["value"] = value;
				break;
		case "items": 
				this.prop["other"]["items"] = value;
				break;
		};
		break;
	case "cell":
		ws.dtc.setContainerProperty(this, name, value);
		break;
	}
}

ws.dtc.combobox.getProperty = function(name, groupName){
	var value = "";
	if( this.prop[groupName] )
		return this.prop[groupName][name];
	return "";
}
ws.dtc.combobox.getUIResource = function(id){
	var src = "", items = this.prop["other"]["items"];
	src += '<combobox id="' + id + '">'
	for( var i = 0; i < items.length; i++ ){
		src += '<item value="' + items[i].value + '" ';
		if( items[i].value == this.prop["other"]["value"] )
			src += ' selected="true" ';
		src += '>';
		src += '<!--' + items[i].label + '-->';
		src += '</item>';
	}
	src += '</combobox>';
	return src;
}
ws.dtc.combobox.toHTML = function(uires){
	var src = "";
	src += '<span name="' + this.prop["other"]["name"] + '" type="ComboBox" ';
	src += 'style="width:' + this.prop["other"]["width"] + '" ';
	src += 'value="' + this.prop["other"]["value"] + '" ';
	src += 'uires="' + uires + '"></span>';
	return src;
}
ws.dtc.combobox.load = function(parent, el){
	var doc = el.ownerDocument;
	var resNode = ws.res.findResourceNode(el.uires, doc);
	var table, tbody, tr, td, items, itemNodes, valueNode;
	table = ws.createElement('<table cellpadding="0" cellspacing="0" style="border:1 solid #7F9DB9;background-color:white;width:100%">', parent);
	tbody = ws.createElement('tbody', table);
	tr    = ws.createElement('tr', tbody);
	td    = ws.createElement('<td style="padding:0;width:100%;border:0">', tr);
	td.innerHTML = '<input type="text" name="" style="width:100%;border:0;cursor:default">';

	td    = ws.createElement('<td style="padding:0;width:1;border:0">', tr);
	td.innerHTML = ws.theme.imageHTML("DropDownBtnN.jpg", "vertical-align:middle");
	
	ws.dtc.combobox.make(table);

	if( resNode ){
		items = new Array();
		itemNodes = resNode.childNodes;
		for( var i = 0; i < itemNodes.length; i++ ){
			if( itemNodes[i].nodeName !== "item" )
				continue;
			valueNode = itemNodes[i].attributes.getNamedItem("value");

			if( valueNode )
				items.push({label:itemNodes[i].firstChild.text, value:valueNode.value});
		}
	}
	table.setProperty("name",  el.name, "other");
	table.setProperty("value", el.value, "other");
	table.setProperty("items", items, "other");
	table.setProperty("width", el.style.width, "other");
	
	if( el.parentElement.align )
		table.setProperty("hAlign", el.parentElement.align,  "cell");
	if( el.parentElement.vAlign )
		table.setProperty("vAlign", el.parentElement.vAlign, "cell");
	return table;
}
//========SelectBox控件========================================================
ws.dtc.selectbox = {};
ws.dtc.selectbox.prop = new Object();
ws.dtc.selectbox.prop["other"] = new Object();
ws.dtc.selectbox.prop["other"]["width"] = "100%";
ws.dtc.selectbox.prop["other"]["value"] = "";
ws.dtc.selectbox.prop["other"]["name"]  = "";
ws.dtc.selectbox.prop["other"]["items"] = null;

ws.dtc.selectbox.prop["cell"] = new Object();
ws.dtc.selectbox.prop["cell"]["hAlign"] = "";
ws.dtc.selectbox.prop["cell"]["vAlign"] = "";

ws.dtc.selectbox.make = function(el){
	el.prop = ws.clone(ws.dtc.selectbox.prop);
	el.prop["other"]["items"] = new Array();
	el.prop["other"]["name"] = "slb" + ws.generateId();

	el.getProperties = ws.dtc.selectbox.getProperties;
	el.setProperty	 = ws.dtc.selectbox.setProperty;
	el.getProperty	 = ws.dtc.selectbox.getProperty;
	el.getType		 = ws.dtc.selectbox.getType;
	el.toHTML		 = ws.dtc.selectbox.toHTML;
	el.getUIResource = ws.dtc.selectbox.getUIResource;
	el.getContainerHTML = ws.dtc.getContainerHTML;
}
ws.dtc.selectbox.getType = function(){
	return "SelectBox";
}
ws.dtc.selectbox.getProperties = function(){
	ws.dtc.getContainerProperty(this);
	return this.prop;
}

ws.dtc.selectbox.setProperty = function(name, value, groupName){
	switch(groupName){
	case "other":
		switch(name){
		case "width": 
				this.style.width = value;
				this.prop["other"]["width"] = value;
				break;
		case "name": 
				this.name = value;
				this.prop["other"]["name"] = value;
				break;
		case "value": 
				for( var i = 0; i < this.prop["other"]["items"].length; i++ ){
					if( this.prop["other"]["items"][i].value == value )
						this.rows[0].cells[0].innerHTML = this.prop["other"]["items"][i].label;
				}
				this.prop["other"]["value"] = value;
				break;
		case "items": 
				this.prop["other"]["items"] = value;
				break;
		};
		break;
	case "cell":
		ws.dtc.setContainerProperty(this, name, value);
		break;
	}
}

ws.dtc.selectbox.getProperty = function(name, groupName){
	var value = "";
	if( this.prop[groupName] )
		return this.prop[groupName][name];
	return "";
}
ws.dtc.selectbox.getUIResource = function(id){
	var src = "", items = this.prop["other"]["items"];
	src += '<selectbox id="' + id + '">'
	for( var i = 0; i < items.length; i++ ){
		src += '<item value="' + items[i].value + '" ';
		if( items[i].value == this.prop["other"]["value"] )
			src += ' selected="true" ';
		src += '>';
		src += '<!--' + items[i].label + '-->';
		src += '</item>';
	}
	src += '</selectbox>';
	return src;
}
ws.dtc.selectbox.toHTML = function(uires){
	var src = "";
	src += '<span name="' + this.prop["other"]["name"] + '" type="SelectBox" ';
	src += 'style="width:' + this.prop["other"]["width"] + '" ';
	src += 'value="' + this.prop["other"]["value"] + '" ';
	src += 'uires="' + uires + '"></span>';
	return src;
}
ws.dtc.selectbox.load = function(parent, el){
	var doc = el.ownerDocument;
	var resNode = ws.res.findResourceNode(el.uires, doc);
	var table, tbody, tr, td, items, itemNodes, valueNode;
	table = ws.createElement('<table cellpadding="0" cellspacing="0" style="border:1 solid #7F9DB9;background-color:white;width:100%">', parent);
	tbody = ws.createElement('tbody', table);
	tr    = ws.createElement('tr', tbody);
	td    = ws.createElement('<td style="padding:0;width:100%;border:0">', tr);
	td.innerHTML = '&nbsp';

	td    = ws.createElement('<td style="padding:0;width:1;border:0">', tr);
	td.innerHTML = ws.theme.imageHTML("DropDownBtnN.jpg", "vertical-align:middle");
	
	ws.dtc.selectbox.make(table);

	if( resNode ){
		items = new Array();
		itemNodes = resNode.childNodes;
		for( var i = 0; i < itemNodes.length; i++ ){
			if( itemNodes[i].nodeName !== "item" )
				continue;
			valueNode = itemNodes[i].attributes.getNamedItem("value");

			if( valueNode )
				items.push({label:itemNodes[i].firstChild.text, value:valueNode.value});
		}
	}
	table.setProperty("name",  el.name, "other");
	table.setProperty("value", el.value, "other");
	table.setProperty("items", items, "other");
	table.setProperty("width", el.style.width, "other");
	
	if( el.parentElement.align )
		table.setProperty("hAlign", el.parentElement.align,  "cell");
	if( el.parentElement.vAlign )
		table.setProperty("vAlign", el.parentElement.vAlign, "cell");
	return table;
}