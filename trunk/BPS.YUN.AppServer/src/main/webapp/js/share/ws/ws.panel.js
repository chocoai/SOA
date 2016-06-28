ws.panel = {};

ws.panel.make = function(panel, op_uires){
	var src, tr, td, tbody, title, titleCell;
	var resNode, uires = op_uires || null, stdclassPrefix, className;

	if( uires == null )
		uires = panel.getAttribute("uires");
	if( uires != null && uires != undefined )
		resNode = ws.res.findResourceNode(uires);
	var titleNode, node;
	
	stdclassPrefix = "";
	if( resNode != null && resNode != undefined ){
		titleNode = ws.res.findChildNode(resNode, "title_text");
		node = resNode.attributes.getNamedItem("class");
		if( node != null && node != undefined )
			panel.className = node.value;
		node = resNode.attributes.getNamedItem("style");
		if( node != null && node != undefined )
			panel.style.cssText = node.value;
		node = resNode.attributes.getNamedItem("stdclassPrefix");
		if( node != null && node != undefined )
			stdclassPrefix = node.value;
	}
	className = stdclassPrefix + "panel_top";
	var head = ws.createElement('<table class="' + className +'" width="100%" cellpadding=0 cellspacing=0 style="empty-cells:show;">');
	tbody = ws.createElement("tbody", head);
	tr    = ws.createElement("tr", tbody);
	
	className = stdclassPrefix + "panel_tl";
	ws.createElement('<td class="' + className + '">', tr);

	className = stdclassPrefix + "panel_tc";
	td = ws.createElement('<td class="' + className + '">', tr);
	td.innerHTML = '<table width="100%" cellpadding=0 cellspacing=0 style="empty-cells:show;"><tr><td type="ws_title" style="padding-left:2"></td></tr></table>';
	titleCell = td.children[0].rows[0].cells[0];

	if( titleNode != null && titleNode != undefined ){
		title = titleNode.firstChild.text;
		node = titleNode.attributes.getNamedItem("class");
		if( node != null && node != undefined )
			titleCell.className = node.value;
		node = titleNode.attributes.getNamedItem("style");
		if( node != null && node != undefined )
			titleCell.style.cssText = node.value;
	}
	else title = panel.getAttribute("title");
	if( title == undefined || title == null || title == "")
		title = "&nbsp";

	titleCell.innerHTML = title;

	panel.headRow   = td.children[0].rows[0];
	panel.titleCell = titleCell;

	className = stdclassPrefix + "panel_tr";
	ws.createElement('<td class="' + className + '">', tr);

	className = stdclassPrefix + "panel_client";
	var client = ws.createElement('<div class="' + className + '" >');
	if( resNode != null && resNode != undefined ){
		node = resNode.attributes.getNamedItem("clientStyle");
		if( node != null && node != undefined )
			client.style.cssText = node.value;
	}
	if( client.style.width == "" )
		client.style.width  = panel.currentStyle.width;
	if( client.style.height == "" )
		client.style.height = panel.currentStyle.height;

	className = stdclassPrefix + "panel_bottom";
	var foot = ws.createElement('<table class="' + className + '" width="100%" cellpadding=0 cellspacing=0 style="empty-cells:show;">');
	tbody = ws.createElement("tbody", foot);
	tr    = ws.createElement("tr", tbody);

	className = stdclassPrefix + "panel_bl";
	ws.createElement('<td class="' + className + '">', tr);
	className = stdclassPrefix + "panel_bc";
	ws.createElement('<td class="' + className + '">', tr);
	className = stdclassPrefix + "panel_br";
	ws.createElement('<td class="' + className + '">', tr);

	panel.head   = head;
	panel.client = client;
	panel.foot   = foot;
	
	var nchild = panel.children.length;
	for( var i = 0; i < nchild; i++ ){
		client.appendChild(panel.children[0]);
	}
	panel.appendChild(head);
	panel.appendChild(client);
	panel.appendChild(foot);

	panel.setTitle = ws.panel.setTitle;
	panel.insertTitleButton =  ws.panel.insertTitleButton;
	panel.expendClient	= ws.panel.expendClient;

	var buttons, className, type, action, position, button;
	if( resNode != null && resNode != undefined ){
		buttons = resNode.childNodes;
		for( var i = 0; i < buttons.length; i++){
			if( buttons[i].nodeName !== "title_button" )
				continue;
			className = buttons[i].attributes.getNamedItem("class");
			type	  = buttons[i].attributes.getNamedItem("type");
			action	  = buttons[i].attributes.getNamedItem("action");
			position  = buttons[i].attributes.getNamedItem("position");
			button = panel.insertTitleButton(className.value, position.value, type.value);
			if( button != null && button != undefined && action != null && action != undefined)
				ws.addEvent(button, "click", eval(action.value));
		}
	}
	var rect = panel.getBoundingClientRect();
	panel.style.pixelWidth = rect.right - rect.left;

	return panel;
}

ws.panel.setTitle = function(title){
	this.titleCell.innerHTML = title;
}

ws.panel.insertTitleButton = function(className, position, type){
	var button;
	if( position == "right" )
		button = this.headRow.insertCell(-1);
	else if( position == "left" ){
		button = this.headRow.insertCell(this.titleCell.cellIndex);
	}
	else return null;
	
	if( button != undefined && button != null ){
		ws.button.ImageButton.make(button, className, type);
		button.ownerPanel = this;
	}
	return button;
}

ws.panel.expendClient = function(bExpend){
	if( bExpend == true ){
		if( this.client.style.display == "none" ){
			this.client.style.display = "inline";
			if( this.client.oldWidth != null && this.client.oldWidth != undefined )
				this.client.style.width = this.client.oldWidth;
		}
	}
	else {
		if( this.client.style.display != "none" ){
			this.client.oldWidth = this.client.offsetWidth;
			this.client.style.display = "none";
		}
	}
}

ws.panel.expendButtonClick = function(){
	var button = ws.getRealElement(event.srcElement, "type", "ws_button");
	if( button == null || button == undefined )
		return;
	
	if( button.ownerPanel.client.style.display == "none" ){
		button.setCheck( true );
		button.ownerPanel.expendClient(true);
	}
	else {
		button.setCheck( false );
		button.ownerPanel.expendClient(false);
	}
}