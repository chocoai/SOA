
ws.button = {};

//===============Begin ImageButton=============================================
ws.button.ImageButton = {};

ws.button.ImageButton.make = function(button, op_className, op_type){
	var className = op_className || null;
	var type = op_type || "";
	var src;

	if( className != null )
		button.className = className;

	button.type = "ws_button";

	if( type == "radio" ){
		button.buttonType = "radio";
		button.checked	= false;
		button.button_img_cku = eval(button.currentStyle.button_img_cku);
		button.button_img_cko = eval(button.currentStyle.button_img_cko);
		button.button_img_ckd = eval(button.currentStyle.button_img_ckd);
		button.button_img_uku = eval(button.currentStyle.button_img_uku);
		button.button_img_uko = eval(button.currentStyle.button_img_uko);
		button.button_img_ukd = eval(button.currentStyle.button_img_ukd);
		src = '<img src="' + button.button_img_uku + '" border="0" >';
	}
	else {
		button.buttonType = "normal";
		button.button_img_u = eval(button.currentStyle.button_img_u);
		button.button_img_o = eval(button.currentStyle.button_img_o);
		button.button_img_d = eval(button.currentStyle.button_img_d);
		src = '<img src="' + button.button_img_u + '" border="0" >';
	}

	button.innerHTML = src;

	ws.addEvent(button, "mousedown", ws.button.ImageButton.mousedown);
	ws.addEvent(button, "mouseup", ws.button.ImageButton.mouseup);
	ws.addEvent(button, "mouseout", ws.button.ImageButton.mouseout);
	ws.addEvent(button, "mouseover", ws.button.ImageButton.mouseover);
	ws.addEvent(button, "click", ws.button.ImageButton.click);
	
	button.setCheck = ws.button.ImageButton.setCheck;

	return button;
}

ws.button.ImageButton.mousedown = function(){
	var button = ws.getRealElement(event.srcElement, "type", "ws_button");
	if( button == null || button == undefined )
		return;
	if( button.buttonType == "radio" ){
		if( button.checked == true )
			button.children[0].src = button.button_img_ckd;
		else 
			button.children[0].src = button.button_img_ukd;
	}
	else 
		button.children[0].src = button.button_img_d;
}
ws.button.ImageButton.mouseover = function(){
	var button = ws.getRealElement(event.srcElement, "type", "ws_button");
	if( button == null || button == undefined )
		return;
	if( button.buttonType == "radio" ){
		if( button.checked == true )
			button.children[0].src = button.button_img_cko;
		else 
			button.children[0].src = button.button_img_uko;
	}
	else 
		button.children[0].src = button.button_img_o;
}
ws.button.ImageButton.mouseup = function(){
	var button = ws.getRealElement(event.srcElement, "type", "ws_button");
	if( button == null || button == undefined )
		return;
	if( button.buttonType == "radio" ){
		if( button.checked == true )
			button.children[0].src = button.button_img_cko;
		else 
			button.children[0].src = button.button_img_uko;
	}
	else 
		button.children[0].src = button.button_img_o;
}
ws.button.ImageButton.mouseout = function(){
	var button = ws.getRealElement(event.srcElement, "type", "ws_button");
	if( button == null || button == undefined )
		return;
	if( button.buttonType == "radio" ){
		if( button.checked == true )
			button.children[0].src = button.button_img_cku;
		else 
			button.children[0].src = button.button_img_uku;
	}
	else 
		button.children[0].src = button.button_img_u;
}

ws.button.ImageButton.click = function(){
	var button = ws.getRealElement(event.srcElement, "type", "ws_button");
	if( button == null || button == undefined )
		return;
	if( button.buttonType == "radio" ){
		if( button.checked == true )
			button.checked = false;
		else 
			button.checked = true;

		if( button.checked == true )
			button.children[0].src = button.button_img_cku;
		else 
			button.children[0].src = button.button_img_uku;
	}
}

ws.button.ImageButton.setCheck = function(checked){
	this.checked = checked;
	if( this.checked == true )
		this.children[0].src = this.button_img_cku;
	else 
		this.children[0].src = this.button_img_uku;
}

//===============End ImageButton===============================================

//===============Begin DropButton==============================================
ws.button.DropButton = {};

ws.button.DropButton.makeRaised = function(el){
	ws.removeClass(el, "button_pressed");
	ws.addClass(el, "button_raised");
	with (el.style) {
		padding      = "1px";
	}
}
ws.button.DropButton.makePressed = function (el){
	ws.removeClass(el, "button_raised");
	ws.addClass(el, "button_pressed");
	with (el.style) {
		paddingTop    = "2px";
		paddingLeft   = "2px";
		paddingBottom = "0px";
		paddingRight  = "0px";
	}
}
ws.button.DropButton.makeFlat = function (el){
	ws.removeClass(el, "button_raised");
	ws.removeClass(el, "button_pressed");
	with (el.style) {
		padding      = "1px";
	}
}

ws.button.DropButton.drop_mousemove = function (el){
	ws.button.DropButton.makeRaised(el);
}
ws.button.DropButton.drop_mousedown = function (el){
	ws.button.DropButton.makePressed(el);
	event.cancelBubble = true;
	event.returnValue  = false;
}
ws.button.DropButton.drop_click = function (el){
	el.parentElement.showDropDown(el.parentElement.dropdown);
}
ws.button.DropButton.drop_mouseup = function (el){
	ws.button.DropButton.makeRaised(el);
}
ws.button.DropButton.drop_mouseout = function (el){
	ws.button.DropButton.makeFlat(el);
}

ws.button.DropButton.db_mousemove = function (el){
	ws.button.DropButton.makeRaised(el.drop);
}
ws.button.DropButton.db_mousedown = function (el){
	ws.button.DropButton.makePressed(el.drop);
}
ws.button.DropButton.db_mouseup = function (el){
	ws.button.DropButton.makeRaised(el.drop);
}
ws.button.DropButton.db_mouseout = function (el){
	ws.button.DropButton.makeFlat(el.drop);
}

ws.button.DropButton.make = function(standin, res, op_group, op_className){
	var resNode = null, className = op_className || "toolbutton", group = op_group || "";
	if( typeof(res) == "object" )
		resNode = res;
	else
		resNode = ws.res.findResourceNode(res);
	if( resNode == null )
		return null;
	var ctrlEl = null;
	if( typeof(standin) == "string" )
		ctrlEl = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		ctrlEl = standin;

	var button, dropbtn, curButton, drop;
	button  = ws.createElement('<div style="width:10px;border:0;padding:0">');
	dropbtn = ws.createElement('<div style="height:100%;border:0;padding:0" nowrap="true" onmousemove="ws.button.DropButton.db_mousemove(this)" onmousedown="ws.button.DropButton.db_mousedown(this)" onmouseout="ws.button.DropButton.db_mouseout(this)" onmouseup="ws.button.DropButton.db_mouseup(this)">', button);
	if( group != "" )
		curButton = ws.createElement('<span nogray="true" style="height:100%;" name="' + group + '" buttontype="radio">', dropbtn);
	else
		curButton = ws.createElement('<span nogray="true">', dropbtn);
	curButton.className = className;
	curButton.innerHTML = "";
	drop = ws.createElement('<span style="height:100%;" onmousemove="ws.button.DropButton.drop_mousemove(this)" onmousedown="ws.button.DropButton.drop_mousedown(this)" onmouseout="ws.button.DropButton.drop_mouseout(this)" onmouseup="ws.button.DropButton.drop_mouseup(this)" onclick="ws.button.DropButton.drop_click(this)">', dropbtn);
	drop.className = "dropbutton";
	
	dropbtn.drop =  drop;

	var value = ctrlEl.value || "";

	var dropdown = ws.part.dropdown.make(resNode);
	dropdown.parent  = dropbtn;
	dropbtn.dropdown = dropdown;
	dropdown.onSelectChanged = ws.button.DropButton.onSelectChanged;
	dropbtn.curButton = curButton;
		
	curButton.onclick = ctrlEl.onclick;
	if( ctrlEl != null && ctrlEl != undefined ){
		ctrlEl.replaceNode(button);
		button.id = ctrlEl.id;
	}
	button.appendChild(dropdown);
	button.dropdown = dropdown;
	button.getButton = ws.button.DropButton.getButton;
	button.setButton = ws.button.DropButton.setButton;
	button.curButton = curButton;
	dropbtn.showDropDown = ws.button.DropButton.showDropDown;

	dropdown.setValue(value, true);
	var img = eval(drop.currentStyle.droparrow_img);
	if( img == undefined )
		img = "downarrow.gif";
	drop.innerHTML = ws.theme.imageHTML(img);

	return button;
}

ws.button.DropButton.onSelectChanged = function(dropdown, selectedHTML){
	var button = dropdown.parent.curButton;
	button.innerHTML = selectedHTML;
	button.id = dropdown.getValue();
	if( typeof(button.group) == "object" && typeof(button.group.onButtonChanged) == "function"){
		if( button.value == true )
			button.group.onButtonChanged(button);
	}
}

ws.button.DropButton.showDropDown = function(dropdown){
	if (dropdown.style.visibility == "")
		dropdown.style.visibility = "hidden";
		
	if (dropdown.style.visibility == "hidden")
		dropdown.show(true);
	else
		dropdown.show(false);
}
ws.button.DropButton.getButton = function(){
	return this.dropdown.getValue();
}
ws.button.DropButton.setButton = function(button){
	this.dropdown.setValue(button);
}
//===============End DropButton================================================