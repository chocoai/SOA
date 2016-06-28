
MenuHelper = {};
MenuHelper.menuMap = {};
MenuHelper.itemMap = {};
MenuHelper.handleFunction = "MenuHelper.handleItemClick";
MenuHelper.baseUrl = $base_url();
MenuHelper.menuIndex = 0;

$import(MenuHelper.baseUrl, "js/share/yui/yahoo/yahoo.js");
$import(MenuHelper.baseUrl, "js/share/yui/event/event.js");
$import(MenuHelper.baseUrl, "js/share/yui/dom/dom.js");
$import(MenuHelper.baseUrl, "js/share/yui/container/container.js");
$import(MenuHelper.baseUrl, "js/share/yui/animation/animation.js");
$import(MenuHelper.baseUrl, "js/share/yui/menu/menu.js");


document.write('<link type="text/css" rel="StyleSheet" href="' + MenuHelper.baseUrl + 'js/share/yui/reset/reset.css"></link>');
document.write('<link type="text/css" rel="StyleSheet" href="' + MenuHelper.baseUrl + 'js/share/yui/fonts/fonts.css"></link>');
document.write('<link type="text/css" rel="StyleSheet" href="' + MenuHelper.baseUrl + 'js/share/yui/menu/assets/menu.css"></link>');
document.write('<link type="text/css" rel="StyleSheet" href="' + MenuHelper.baseUrl + 'css/share/yui_menu.css"></link>');

/**
 * 创建菜单但不显示
 * @param name 用于表示菜单的标识名称
 * @param xml 菜单定义的XML字符串
 */
MenuHelper.createMenu = function(name, xml){
	var doc = Util.loadXML(xml);

	var menu = MenuHelper.menuMap[name];
	if( menu )
		menu.destroy();
	
	menu = MenuHelper._generateMenu(name, doc.documentElement);
	if( menu != null ){
		menu.beforeShowEvent.subscribe(onMenuBeforeShow, menu, true);
		menu.showEvent.subscribe(onMenuShow, menu, true);

		menu.render(document.body);

		MenuHelper.menuMap[name] = menu;
	}
}

MenuHelper.onContextMenuItemClick = function(type, args, me){
	if( me.mhelper ){
		if( typeof MenuHelper.handleFunction != "function" ){
			var func = eval(MenuHelper.handleFunction);
			if( func )
				func(me.mhelper.name, me.mhelper.id);
		}
	}
}

MenuHelper.addItem = function(menuName, label, iconUrl, itemId){
	var menu = MenuHelper.menuMap[menuName];
	var item;
	if( menu ){
		if( iconUrl == null || iconUrl == undefined || iconUrl.length == 0 )
			iconUrl = null;

		item = new YAHOO.widget.ContextMenuItem(label);
		menu.addItem(item);
		item.mhelper = {name:menuName, id:itemId};
		item.clickEvent.subscribe( MenuHelper.onContextMenuItemClick, item, true );

		if( iconUrl != null )
			item.element.style.backgroundImage = "url(" + iconUrl + ")";

		MenuHelper.itemMap[itemId] = item;

		return true;
	}
	return false;
}

MenuHelper.removeItem = function(menuName, itemId){
	var menu = MenuHelper.menuMap[menuName];
	var item = MenuHelper.itemMap[itemId];
	if( menu && item ){
		menu.removeItem(item);
		return true;
	}
	return false;
}
/**
 * 创建菜单条但不显示
 * @param xml 菜单条定义的XML字符串
 */
MenuHelper.createMenuBar = function(name, xml){
    var doc = Util.loadXML(xml);

	YAHOO.widget.MenuBarItem.prototype.SUBMENU_INDICATOR_IMAGE_PATH =
                "promo/m/irs/blank.gif";
	YAHOO.widget.MenuBarItem.prototype.SELECTED_SUBMENU_INDICATOR_IMAGE_PATH =
                "promo/m/irs/blank.gif";
	YAHOO.widget.MenuBarItem.prototype.DISABLED_SUBMENU_INDICATOR_IMAGE_PATH = 
                "promo/m/irs/blank.gif";

	var menuBar = MenuHelper.menuMap[name];
	if( menuBar )
		menuBar.destroy();
	
	menuBar = MenuHelper._generateMenuBar(name, doc.documentElement);

	MenuHelper.menuMap[name] = menuBar;

	setupMenuAnimation(menuBar);

	var mb = document.getElementById("menuBar");
	if( mb )
		menuBar.render(mb);
	else
		menuBar.render(document.body);
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
 * @param isContext 是否是上下文件菜单
 */
MenuHelper._generateMenu = function(menuName, xmlEl, isContext){
	var el;
	var menu;
	if( isContext == undefined )
		isContext = true;
	if( isContext )
		menu = new YAHOO.widget.ContextMenu(menuName + MenuHelper.menuIndex);
	else
		menu = new YAHOO.widget.Menu(menuName + MenuHelper.menuIndex);

	MenuHelper.menuIndex++;
	
	var item, itemId, menuGroup = 0, config, childCount = 0;
	for( var i = 0; i < xmlEl.childNodes.length; i++ ){
		el = xmlEl.childNodes[i];
		if( el.tagName == "menuitem" ){
			var iconUrl = el.getAttribute("iconUrl");
			if( iconUrl != null && iconUrl != undefined && iconUrl.length > 0 )
				iconUrl = MenuHelper.baseUrl + iconUrl;
			else
				iconUrl = null;

			itemId = el.getAttribute("_id_");
			if( el.getAttribute("enable") == "false" )
				config = {disabled:true};
			else
				config = undefined;

			if( el.getAttribute("type") == "separator" ){
				menuGroup++;
				continue;
			}
			else if( el.childNodes.length > 0 ){
				if( isContext )
					item = new YAHOO.widget.ContextMenuItem(el.getAttribute("label"), config);
				else
					item = new YAHOO.widget.MenuItem(el.getAttribute("label"), config);
				item.submenu = MenuHelper._generateMenu(menuName, el, isContext);
				item.mhelper = {name:menuName, id:itemId};
				item.clickEvent.subscribe( MenuHelper.onContextMenuItemClick, item, true );

				if( iconUrl != null )
					item.element.style.backgroundImage = "url(" + iconUrl + ")";
			}
			else {
				if( isContext )
					item = new YAHOO.widget.ContextMenuItem(el.getAttribute("label"), config);
				else
					item = new YAHOO.widget.MenuItem(el.getAttribute("label"), config);
				item.mhelper = {name:menuName, id:itemId};
				item.clickEvent.subscribe( MenuHelper.onContextMenuItemClick, item, true );

				if( iconUrl != null )
					item.element.style.backgroundImage = "url(" + iconUrl + ")";
			}

			menu.addItem(item, menuGroup);
			MenuHelper.itemMap[itemId] = item;
			childCount ++;
		}
	}
	if( childCount > 0 )
		return menu;
	else
		return null;
}

/**
 * 生成菜单条对象(内部方法)
 * @param menuName 菜单名称
 * @param xmlEl 表示菜单条定义的DOM对象
 */
MenuHelper._generateMenuBar = function(menuName, xmlEl){
	var mb = new YAHOO.widget.MenuBar("_menuBar", { autosubmenudisplay:true, showdelay:250, hidedelay:750, lazyload:true });
	for(var i = 0; i < xmlEl.childNodes.length; i++){
		var elMenu = xmlEl.childNodes[i];
		if( elMenu.tagName == "menuitem" ){
			var menu = MenuHelper._generateMenu(menuName, elMenu, false);
			if( menu == null )
				continue;
			label    = elMenu.getAttribute("label");
			visible  = elMenu.getAttribute("visible");
			if( label ){
				var item = new YAHOO.widget.MenuBarItem(label, { submenu: menu });
				mb.addItem(item);

				visible = (visible == "false" ? "hidden" : "visible");
				item.element.style.visibility = visible;
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
//		menu.moveTo(window.screenLeft + left, window.screenTop + top);
		menu.moveTo(left, top + 27);
		_currentMenu = menu;
		window.setTimeout("_showMenu()", 0);
	}
}
var _currentMenu;
function _showMenu(){
	if( _currentMenu != null && _currentMenu != undefined ){
		_currentMenu.show();
	}
}

var oAnim;


// Utility function used to setup animation for submenus

function setupMenuAnimation(p_oMenu) {

	if(!p_oMenu.animationSetup) {

		var aItems = p_oMenu.getItemGroups();

		if(aItems && aItems[0]) {

			var i = aItems[0].length - 1;
			var oSubmenu;

			do {

				oSubmenu = p_oMenu.getItem(i).cfg.getProperty("submenu");

				if(oSubmenu) {

					oSubmenu.beforeShowEvent.subscribe(onMenuBeforeShow, oSubmenu, true);
					oSubmenu.showEvent.subscribe(onMenuShow, oSubmenu, true);
				}
			
			}
			while(i--);
		
		}

		p_oMenu.animationSetup = true;

	}

}


// "beforeshow" event handler for each submenu of the menu bar

function onMenuBeforeShow(p_sType, p_sArgs, p_oMenu) {

	if(oAnim && oAnim.isAnimated()) {
	
		oAnim.stop();
		oAnim = null;
	
	}

	YAHOO.util.Dom.setStyle(this.element, "overflow", "hidden");
	YAHOO.util.Dom.setStyle(this.body, "marginTop", ("-" + this.body.offsetHeight + "px"));

}

// "show" event handler for each submenu of the menu bar

function onMenuShow(p_sType, p_sArgs, p_oMenu) {

	oAnim = new YAHOO.util.Anim(
		this.body, 
		{ marginTop: { to: 0 } },
		.3, 
		YAHOO.util.Easing.easeOutStrong
	);

	oAnim.animate();

	var me = this;
		
	function onTween() {

		me.cfg.refireEvent("iframe");
	
	}

	function onAnimationComplete() {

		YAHOO.util.Dom.setStyle(me.body, "marginTop", ("0px"));
		YAHOO.util.Dom.setStyle(me.element, "overflow", "visible");

		setupMenuAnimation(me);

	}
	

	/*
		 Refire the event handler for the "iframe" 
		 configuration property with each tween so that the  
		 size and position of the iframe shim remain in sync 
		 with the menu.
	*/

	if(this.cfg.getProperty("iframe") == true) {

		oAnim.onTween.subscribe(onTween);

	}

	oAnim.onComplete.subscribe(onAnimationComplete);

}