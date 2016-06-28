ws.menu = {};
ws.menu.MenuBar  = {};
ws.menu.Menu     = {};
ws.menu.MenuItem = {};

//创建菜单项
ws.menu.MenuItem.make = function (elMenuItem){
	var menuItem = null;

	var label    = elMenuItem.attributes.getNamedItem("label");
	var action   = elMenuItem.attributes.getNamedItem("action");
	var icon     = elMenuItem.attributes.getNamedItem("icon");
	var disabled = elMenuItem.attributes.getNamedItem("disabled");
	var visible  = elMenuItem.attributes.getNamedItem("visible");
	var tooltip  = elMenuItem.attributes.getNamedItem("tooltip");

	var subMenu  = null;
	var fAction  = null;
	var n;
	if( action ){
		action = action.value;
		n = action.indexOf("js:");
		if( n != -1 ){
			action = action.substr(n + 3);
			fAction = function(){ eval(action) };
		} else fAction = action;
	}
	if( label )
		label = label.value;
	else
		label = "";
	if( tooltip )
		tooltip = tooltip.value;
	else
		tooltip = "";

	if( elMenuItem.childNodes.length > 0 )
		subMenu = ws.menu.Menu.make(elMenuItem);
	if( icon != null && icon != undefined && icon.value != "")
		icon = ws.theme.iconURL(icon.value);
	else
		icon = "";
	menuItem = new MenuItem(label, fAction, icon, subMenu);
	if( disabled != null && disabled != undefined && (disabled.value == true || disabled.value == "true"))
		menuItem.disabled = true;
	if( visible != null && visible != undefined && (visible.value == false || visible.value == "false"))
		menuItem.visible = false;
	menuItem.toolTip = tooltip;
	return menuItem;
}
//创建菜单
ws.menu.Menu.make = function (elMenu){
	var menu = new Menu();
	for(var i = 0; i < elMenu.childNodes.length; i++){
		var elMenuItem = elMenu.childNodes[i];
		if( elMenuItem.tagName == "menuitem" ){
			menuItem = ws.menu.MenuItem.make(elMenuItem);
			menu.add( menuItem );
		} else if(elMenuItem.tagName == "separator" ){
			menu.add( new MenuSeparator() );
		}
	}
	return menu;
}
//创建菜单条
ws.menu.MenuBar.make = function (res){
	var resNode = ws.res.findResourceNode(res);
	if( resNode == null )
		return null;
	if( resNode.tagName != "menubar" )
		return null;

	var skin = resNode.attributes.getNamedItem("skin");
	if( skin )
		Menu.prototype.cssFile = skin.value;
	Menu.prototype.mouseHoverDisabled = true;

	var label, visible;
	var mb = new MenuBar;
	for(var i = 0; i < resNode.childNodes.length; i++){
		var elMenu = resNode.childNodes[i];
		if( elMenu.tagName == "mainmenu" ){
			var menu = ws.menu.Menu.make(elMenu);
			label    = elMenu.attributes.getNamedItem("label");
			visible  = elMenu.attributes.getNamedItem("visible");
			if( label ){
				var mbtn = new MenuButton( label.value, menu );
				if( visible != null && visible != undefined )
					visible = (visible.value == "false" ? false : true);
				else
					visible = true;
				mbtn.visible = visible;
				mb.add( mbtn );
			}
		}
	}
	mb.write();
	return mb;
}

/**
 * 设置是否显示菜单项
 * @param menuBar MenuBar对象
 * @param path 菜单所在位置(用/号分隔)
 * @param visible 是否显示
 */
ws.menu.setVisible = function(menuBar, path, visible){
	var names = path.split("/");
	var barEl = $(menuBar.id);
	if( barEl == null || barEl == undefined || names.length == 0)
		return;
	var menuButton = null, menu = null;
	for( var i = 0; i < barEl.children.length; i++ ){
		menuButton = barEl.children[i];
		try{
			if( menuButton.children[1].innerText == names[0] ){
				if( names.length == 1 ){
					menuButton.style.display = visible ? "" : "none";
					return;
				}
				menu = menuBar.items[i];
				break;
			}
		}
		catch(errorinfo){}
	}
	if( menu == null || menu == undefined )
		return;
	var found;
	for( var i = 1; i < names.length; i++ ){
		menu = menu.subMenu;
		if( menu == null || menu == undefined )
			return;
		found = false;
		for( var j = 0; j < menu.items.length; j++ ){
			if( menu.items[j].text == names[i] ){
				if( names.length == i + 1 ){
					menu.items[j].visible = visible;
					return;
				}
				else {
					menu  = menu.items[j];
					found = true;
					break;
				}
			}
		}
		if( found == false )
			return;
	}
}