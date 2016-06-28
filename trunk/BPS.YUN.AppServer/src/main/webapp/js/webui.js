webui = {
	//主页菜单数据
	home: {},
	//页面模式，share-page | per-page
	pageMode : "share-page",
	//快捷按钮
	shutcutButtons : []
};

/**
 * 菜单数据装入时自动调用
 * @param menus 菜单数据
 * @param home 首页数据
 */
webui.onReady = function(menus, home){
//	var pageStyle = 2;
//	if (pageStyle == 1) {
//		new WebUiPanel(menus);
//	} else {
//		new WebUiTabarPanel(menus);
//	}
	Ext.onReady(function(){
		Ext.BLANK_IMAGE_URL = '../../js/extjs/resources/images/default/s.gif';
		Ext.QuickTips.init();
		Ext.form.Field.prototype.msgTarget = 'side';
		new WebUiTabarPanel(menus);
	});
    /**隐藏loading...*/
	document.getElementById('loading').style.display='none';
}

/**
 * 修改Flex应用当前显示的页面
 * @param frame Flex应用所在的iframe ID
 * @param menu 需要显示的页面菜单数据
 */
webui.changePage = function(frameId, menu){
	var fw = document.frames[frameId];
	if( fw != null )
		fw.webui.changePage(menu);
}

/**
 * 打开ALink链接
 * @param alink ALink URL
 */
webui.openALink = function(frameId, alink){
	var fw = document.frames[frameId];
	if( fw != null )
		fw.webui.openALink(alink);
}
webui.convertMenu = function(menuXml){
	if( xml == null )
		return null;
	var obj = {};
	var attr, n, v;
	if( xml.attributes ){
		for( var i = 0; i < xml.attributes.length; i++ ){
			attr = xml.attributes.item(i);
			n = attr.nodeName;
			v = attr.nodeValue;
			obj[n] = v;
		}
	}
	obj.text = xml.text;
	if( obj.action == null || obj.action == "" ){
		if( obj.xmlName == "QueryPage" || obj.xmlName == "ReportPage" )
			obj.action = "page";
	}	
	var child, childObj;
	if( xml.childNodes ){
		for( var i = 0; i < xml.childNodes.length; i++ ){
			child = xml.childNodes.item(i);
			if( child.nodeName == "#text" )
				continue;
			childObj = xmlToObject(child, childrenName);
			if( obj.children == null )
				obj.children = [];
			obj.children.push(childObj);
		}
	}
	if( obj.children != null )
		obj.children.sort(webui.compairMenu);
	return obj;
} 

webui.compairMenu = function(menu1, menu2){
	var n1 = Number(menu1.order);
	var n2 = Number(menu2.order);
	if( n1 < n2 )
		return -1;
	else if( n1 > n2 )
		return 1;
	else
		return 0;
}