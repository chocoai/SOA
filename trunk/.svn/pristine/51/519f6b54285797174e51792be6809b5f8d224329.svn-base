ws.tabpane = {};
ws.tabpane.tabpage = {};
ws.tabpane.make = function(standin){
	var tabpane = null;
	if( typeof(standin) == "string" )
		tabpane = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		tabpane = standin;
	if( tabpane.type != "ws_tabpane" )
		return null;
	tabpane.className = "ws_tabpane";
	tabpane.pages = [];
	tabpane.selectedIndex = 0;

	tabpane.framepage = null; //当内容是一个html页时用于表示iframe元素

	tabpane.tabRow = ws.createElement('<div class="tab-row">');
	tabpane.insertBefore( tabpane.tabRow, tabpane.firstChild );
	var child, tabpage, nIndex = 0;
	for( var i = 0; i < tabpane.children.length; i++ ){
		child = tabpane.children[i];
		if( child.type == "ws_tabpage" ){
			child.tabPane = tabpane;
			tabpage = ws.tabpane.tabpage.make(child, nIndex);
			tabpane.tabRow.appendChild( tabpage.tab );
			tabpane.pages[nIndex] = tabpage;
			if ( nIndex == tabpane.selectedIndex )
				tabpage.show();
			else
				tabpage.hide();			
			nIndex ++;
		}
	}

	tabpane.getSelectedIndex = ws.tabpane.getSelectedIndex;
	tabpane.setSelectedIndex = ws.tabpane.setSelectedIndex;

	return tabpane;
}
ws.tabpane.getSelectedIndex = function () {
	return this.selectedIndex;
};

ws.tabpane.setSelectedIndex = function ( n ) {
	if (this.selectedIndex != n) {
		if (this.selectedIndex != null && this.pages[ this.selectedIndex ] != null )
			this.pages[ this.selectedIndex ].hide();
		this.selectedIndex = n;
		this.pages[ this.selectedIndex ].show();
	}
};

ws.tabpane.tabpage.make = function(page, nIndex){
	var tab = null;
	for( var i = 0; i < page.children.length; i++ ){
		if( page.children[i].type == "ws_tab" ){
			tab = page.children[i];
			break;
		}
	}
	var url = page.getAttribute("url");
	if( url != "" ){
		if( page.tabPane.framepage == null ){
			page.tabPane.framepage = ws.createElement('<iframe src="" style="display:none" onload="ws.tabpane.onloadframe(this)">');
		}
		page.url = url;
	}
	else
		page.url = null;

	tab.className  = "tab";
	page.className = "tab-page";

	page.index = nIndex;
	page.tab   = tab;
	var a = ws.createElement( "A" );
	a.href = "";
	a.onclick = function () { return false; };
	while ( tab.hasChildNodes() )
		a.appendChild( tab.firstChild );
	tab.appendChild( a );

	tab.onclick     = function () { page.select(); };
	tab.onmouseover = function () { ws.tabpane.tabOver( page ); };
	tab.onmouseout  = function () { ws.tabpane.tabOut( page ); };

	page.show   = ws.tabpane.tabpage.show;
	page.hide   = ws.tabpane.tabpage.hide;
	page.select = ws.tabpane.tabpage.select;
	
	return page;
}

ws.tabpane.tabpage.show = function () {
	ws.removeClass(this.tab, "hover");
	ws.addClass(this.tab, "selected");

	this.style.display = "block";
	if( this.url != null ){
		this.tabPane.framepage.src = this.url;
		this.appendChild(this.tabPane.framepage);
		this.tabPane.framepage.style.display = "block";
	}
};

ws.tabpane.tabpage.hide = function () {
	ws.removeClass(this.tab, "selected");
	this.style.display = "none";
};

ws.tabpane.tabpage.select = function () {
	this.tabPane.setSelectedIndex( this.index );
};

ws.tabpane.tabOver = function ( tabpage ) {
	if( tabpage.index == tabpage.tabPane.selectedIndex )
		return;
	ws.addClass(tabpage.tab, "hover");
};

ws.tabpane.tabOut = function ( tabpage ) {
	ws.removeClass(tabpage.tab, "hover");
};


ws.tabpane.timer = new ws.Timer(1);
ws.tabpane.timer.ontimer = function(){
	var frame = ws.tabpane.timer.frame;
	frame.style.pixelWidth  = frame.contentWindow.document.body.scrollWidth + 10;
	frame.style.pixelHeight = frame.contentWindow.document.body.scrollHeight + 10;
}
ws.tabpane.onloadframe = function(frame){
	frame.contentWindow.document.body.style.border = "0px";
	ws.tabpane.timer.frame = frame;
	ws.tabpane.timer.start();
}