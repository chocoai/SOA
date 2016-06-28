
MenuHelper = {};
MenuHelper.menuMap = {};
MenuHelper.itemMap = {};
MenuHelper.handleFunction = "MenuHelper.handleItemClick";
MenuHelper.baseUrl = $base_url("MenuHelper.js");

$import(MenuHelper.baseUrl, "/js/share/poslib.js");
$import(MenuHelper.baseUrl, "/js/share/scrollbutton.js");
$import(MenuHelper.baseUrl, "/js/share/menu4.js");

document.write('<link type="text/css" rel="StyleSheet" href="' + MenuHelper.baseUrl + 'skins/menu/officexp/officexp.css"></link>');

/**
 * 创建菜单但不显示
 * @param name 用于表示菜单的标识名称
 * @param xml 菜单定义的XML字符串
 */
MenuHelper.createMenu = function(name, xml){
    Menu.prototype.cssFile = MenuHelper.baseUrl + "/skins/menu/officexp/officexp.css";
    
	var doc = Util.loadXML(xml);

	var menu = MenuHelper.menuMap[name];
	if( menu )
		menu.destroy();
	
	menu = MenuHelper._generateMenu(name, doc.documentElement);

	menu.invalidate();

	MenuHelper.menuMap[name] = menu;
}

MenuHelper.addItem = function(menuName, label, iconUrl, itemId){
	var menu = MenuHelper.menuMap[menuName];
	var item;
	if( menu ){
		if( iconUrl == null || iconUrl == undefined || iconUrl.length == 0 )
			iconUrl = null;

		item = new MenuItem(label, "javascript:" + MenuHelper.handleFunction + "('" + menuName + "', '" + itemId + "')", iconUrl);
		menu.add(item);
		MenuHelper.itemMap[itemId] = item;

		menu.invalidate();

		return true;
	}
	return false;
}

MenuHelper.removeItem = function(menuName, itemId){
	var menu = MenuHelper.menuMap[menuName];
	var item = MenuHelper.itemMap[itemId];
	if( menu && item ){
		menu.remove(item);
		menu.invalidate();
		return true;
	}
	return false;
}
/**
 * 创建菜单条但不显示
 * @param xml 菜单条定义的XML字符串
 */
MenuHelper.createMenuBar = function(xml){
    Menu.prototype.cssFile = MenuHelper.baseUrl + "/skins/menu/officexp/officexp.css";
    
    var doc = Util.loadXML(xml);
    
	var menuBar = MenuHelper._generateMenuBar(doc.documentElement);

	document.body.appendChild(menuBar.create());
}

MenuHelper.destroyMenu = function(name){
	var menu = MenuHelper.menuMap[name];
	if( menu )
		menu.destroy();
}

/**
 * 生成菜单对象(内部方法)
 * @param menuName 菜单名称
 * @param xmlEl 表示菜单定义的DOM对象
 */
MenuHelper._generateMenu = function(menuName, xmlEl){
	var el;
	var menu = new Menu;
	var item, itemId;
	for( var i = 0; i < xmlEl.childNodes.length; i++ ){
		el = xmlEl.childNodes[i];
		if( el.tagName == "menuitem" ){
			var icon = el.getAttribute("iconUrl");
			if( icon != null && icon != undefined && icon.length > 0 )
				icon = MenuHelper.baseUrl + icon;
			else
				icon = null;

			itemId = el.getAttribute("_id_");
			
			if( el.getAttribute("type") == "separator" )
				item = new MenuSeparator();
			else if( el.childNodes.length > 0 )
				item = new MenuItem(el.getAttribute("label"), null, icon, MenuHelper._generateMenu(menuName, el));
			else
				item = new MenuItem(el.getAttribute("label"), "javascript:" + MenuHelper.handleFunction + "('" + menuName + "', '" + itemId + "')", icon);

			menu.add(item);
			MenuHelper.itemMap[itemId] = item;
		}
	}
	return menu;
}

/**
 * 生成菜单条对象(内部方法)
 * @param xmlEl 表示菜单条定义的DOM对象
 */
MenuHelper._generateMenuBar = function(xmlEl){
	var mb = new MenuBar;
	for(var i = 0; i < xmlEl.childNodes.length; i++){
		var elMenu = xmlEl.childNodes[i];
		if( elMenu.tagName == "menuitem" ){
			var menu = MenuHelper._generateMenu("mainbar" + i, elMenu);
			label    = elMenu.getAttribute("label");
			visible  = elMenu.getAttribute("visible");
			if( label ){
				var mbtn = new MenuButton( label, menu );
				visible = (visible == "false" ? false : true);
				mbtn.visible = visible;
				mb.add( mbtn );
			}
		}
	}
	return mb;
}

/**
 * 菜单事件处理函数
 * @param menuName 发生事件的菜单名称
 * @param menuId 发生事件的菜单标识
 */
MenuHelper.handleItemClick = function(menuName, menuId){
	alert(menuName + menuId);
}

/**
 * 显示菜单
 * @param name 菜单标识名称
 * @param x 菜单显示的X坐标
 * @param y 菜单显示的Y坐标
 */
MenuHelper.showMenu = function(name, x, y){
	var left = x ? x : 0;
	var top  = y ? y : 0;
	var menu = MenuHelper.menuMap[name];
	if( menu != null && menu != undefined ){
//		menu.fixSize();
//		menu.updateSizeCache(true);
		menu.show(window.screenLeft + left, window.screenTop + top);
	}
}
