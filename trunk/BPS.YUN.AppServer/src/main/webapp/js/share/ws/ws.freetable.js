/**
 * ���ɱ����󣬿ɿؼ����еĿ�ȡ��ϲ��������С��������е�
 */
ws.freetable = {};

/**
 * �������ɱ�����
 * @param standin ���ɱ���ռλԪ�ض��󣬿�����ռλԪ��IDҲ������ռλԪ�ض�����
 * @param op_nrow ���ɱ�������(��ѡ��Ĭ��Ϊ3��)
 * @param op_ncol ���ɱ�������(��ѡ��Ĭ��Ϊ5��)
 * @param op_className ���ɱ�����ʽ(��ѡ)
 * @param op_cellWidth ��Ԫ��ĳ�ʼ���(��ѡ��Ĭ��Ϊ50px)
 * @return ���ɺ�����ɱ�����
 */
ws.freetable.make = function(standin, op_nrow, op_ncol, op_className, op_cellWidth){
	var nrow = op_nrow || 3, ncol = op_ncol || 5, className = op_className || "", cellWidth = op_cellWidth || "50px";
	
	var ftable= ws.createElement('<table cellspacing=0 cellpadding=0>');
	var tbody = ws.createElement("tbody", ftable);
	var tr, td, headerctl, columnctl, ftablebody;
	
	ftable.className = className;
	
	tr = ws.createElement("tr", tbody);
	td = ws.createElement('<td class="selectall_control" onmousedown="ws.freetable.onSelectAll(this)">', tr);
	td.innerHTML = "&nbsp;";
	td.style.cursor = ws.getCursor(ws.cursor_select_all);
	ftable.selectallctl = td;
	ftable.selectallctl.parent = ftable;

	td = ws.createElement('<td>', tr);
	headerctl = ws.createElement("span", td);
	
	tr = ws.createElement("tr", tbody);
	td = ws.createElement('<td>', tr);
	columnctl = ws.createElement("span", td);
	
	td = ws.createElement('<td>', tr);
	ftablebody = ws.createElement("span", td);

	var el = null;
	if( typeof(standin) == "string" )
		el = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		el = standin;
	if( el != null && el != undefined ){
		el.replaceNode(ftable);
		ftable.id = el.id;
	}
	
	//�������ɱ��ʵ��
	ftable.fbody     = ws.freetable.body.make(ftablebody, nrow, ncol, "fbody", ftable, cellWidth);
	//����ˮƽ����Ԫ��
	ftable.headerctl = ws.freetable.headerctl.make(headerctl, ncol, ftable.fbody, ftable, cellWidth);
	//���ɴ�ֱ����Ԫ��
	ftable.columnctl = ws.freetable.columnctl.make(columnctl, nrow, ftable.fbody, ftable);
	
	ftable.onCellChange   = ws.freetable.onCellChange;
	ftable.checkWH		  = ws.freetable.checkWH;
	ftable.reset		  = ws.freetable.reset;
	ftable.insertTableCol = ws.freetable.insertTableCol;
	ftable.deleteTableCol = ws.freetable.deleteTableCol;
	ftable.insertTableRow = ws.freetable.insertTableRow;
	ftable.deleteTableRow = ws.freetable.deleteTableRow;
	
	ftable.selectedRow = null;	//��ǰѡ�е���(����ֵ)
	ftable.selectedCol = null;	//��ǰѡ�е���(����ֵ)
	ftable.isSelectAll = false; //��ǰ�Ƿ�ѡ�����б��

	ws.addEvent(ftable.fbody, "mousedown", ws.freetable.clearSelection);

	return ftable;
}

ws.freetable.reset = function(){
	var i, j, cell;
	for( i = 0; i < this.fbody.rows.length - 1; i++ ){
		for( j = 0; j < this.fbody.rows[i].cells.length - 1; j++ ){
			cell = this.fbody.rows[i].cells[j];
			cell.innerHTML = "&nbsp;";
			while( cell.colSpan > 1 )
				this.fbody.splitCell(cell, "horz");
			while( cell.rowSpan > 1 )
				this.fbody.splitCell(cell, "vert");
		}
	}
	this.fbody.calcGrid();
	this.checkWH();
}

/**
 * ��������ѡ���¼�
 */
ws.freetable.onSelectAll = function(ctrlTD){
	ctrlTD.parent.selectedRow = null;
	ctrlTD.parent.selectedCol = null;

	if( typeof(ctrlTD.parent.fbody.selectAll) == "function" )
		ctrlTD.parent.fbody.selectAll();
}

/**
 * ���ʵ���ѡ���¼���������С��к���������ѡ��״̬
 */
ws.freetable.clearSelection = function(){
	var el = event.srcElement;
	while ((el != null) && (el.tagName != "BODY") && (el.type != "ws_fbody")) {
		el = el.parentElement;
	}
	if( el != null && el.tagName == "TABLE" ){
		el.parent.selectedRow = null;
		el.parent.selectedCol = null;
		el.parent.isSelectAll = false;
		if( typeof(el.fireChange) == "function" )
			el.fireChange();
	}
}

/**
 * �����ɱ��ĵ�Ԫ�����仯ʱ�������¼����Ե������п���Ԫ��
 * @param nrow ��Ԫ�����
 * @param ncol ��Ԫ�����
 */
ws.freetable.onCellChange = function(nrow, ncol){
	this.headerctl.sync(ncol);
	this.columnctl.sync(nrow);
}

/**
 * �����ɱ������п���Ԫ�ص�״̬���м�鲢������״̬
 * @param force �Ƿ�ǿ�Ƶ������
 */
ws.freetable.checkWH = function(force){
	if( force || this.headerctl.offsetWidth != this.fbody.offsetWidth ){
		this.headerctl.adjust();
	}
	if( force || this.columnctl.offsetHeight != this.fbody.offsetHeight ){
		this.columnctl.adjust();
	}
	this.style.width  = this.selectallctl.offsetWidth  + this.headerctl.offsetWidth;
	this.style.height = this.selectallctl.offsetHeight + this.columnctl.offsetHeight;
}

ws.freetable.insertTableCol = function(ncol){
	this.headerctl.insertTableCol(ncol);
	this.fbody.insertTableCol(ncol);
	if( this.selectedCol != null )
		this.selectedCol ++;
	this.checkWH();
}
ws.freetable.deleteTableCol = function(ncol){
	if( this.headerctl.rows[0].cells.length > 1 ){
		this.headerctl.deleteTableCol(ncol);
		this.fbody.deleteTableCol(ncol);
		this.selectedCol = null;
		this.checkWH();
	}
}
ws.freetable.insertTableRow = function(nrow){
	this.columnctl.insertTableRow(nrow);
	this.fbody.insertTableRow(nrow);
	if( this.selectedRow != null )
		this.selectedRow ++;
	this.checkWH();
}
ws.freetable.deleteTableRow = function(nrow){
	if( this.columnctl.rows.length > 1 ){
		this.columnctl.deleteTableRow(nrow);
		this.fbody.deleteTableRow(nrow);
		this.selectedRow = null;
		this.checkWH();
	}
}
//�п���Ԫ��============================================================================
ws.freetable.headerctl = {};

/**
 * �������ɱ���ˮƽ����Ԫ��
 * @param standin ռλԪ�ض��󣬿�����ռλԪ��IDҲ������ռλԪ�ض�����
 * @param cols ����
 * @param target �����Ƶı��Ԫ��
 * @param op_parent ���ɱ�����(��ѡ)
 * @param op_cellWidth ��Ԫ��ĳ�ʼ���(��ѡ)
 * @return ���ɵ�ˮƽ����Ԫ��
 */
ws.freetable.headerctl.make = function(standin, cols, target, op_parent, op_cellWidth){
	var parent = op_parent || null, cellWidth = op_cellWidth || "";
	var head  = ws.createElement('<table class="header_control" cellspacing=0 onselectstart="return false;" onmousemove="ws.freetable.headerctl.mousemove(this)" onmousedown="ws.freetable.headerctl.mousedown(this)" onmouseup="ws.freetable.headerctl.mouseup(this)>');
	var tbody = ws.createElement("tbody", head);
	var tr    = ws.createElement("tr", tbody);
	var td;
	
	head.parent = parent;
	
	for( var i = 0; i < cols; i++ ){
		td = ws.createElement("<td nowrap>", tr);
		td.innerHTML = "&nbsp;";
		td.style.width = cellWidth;
	}

	head.targetTable = target;
	head.isResizing  = false;
	head.active_head = null;
	head.hittest	 = "none";
	head.sync		 = ws.freetable.headerctl.sync;
	head.resize		 = ws.freetable.headerctl.resize;
	head.adjust		 = ws.freetable.headerctl.adjust;
	
	head.insertTableCol = ws.freetable.headerctl.insertTableCol;
	head.deleteTableCol = ws.freetable.headerctl.deleteTableCol;
	
	var el = null;
	if( typeof(standin) == "string" )
		el = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		el = standin;
	if( el != null && el != undefined ){
		el.replaceNode(head);
		head.id = el.id;
	}
	return head;
}

/**
 * ˮƽ����Ԫ�ص�����ƶ��¼�
 * @param ghead �����¼��Ŀ�����
 */
ws.freetable.headerctl.mousemove = function(ghead){
	var head, rect;
	
	if( ghead.isResizing == true && ghead.active_head != undefined && ghead.active_head != null){
		ghead.resize(event);
		return;
	}
	if( ghead.active_head != undefined && ghead.active_head != null ){
		head = ghead.active_head;
		rect = head.getBoundingClientRect();
		if( event.x >= rect.right - 3 && event.x <= rect.right + 3 ){
			ghead.style.cursor = ws.getCursor(ws.cursor_resize_EW);
			ghead.hittest  = "resize";
			return;
		}
		else if( rect.left + 3 <= event.x && rect.right - 3 >= event.x ){
			ghead.style.cursor = ws.getCursor(ws.cursor_select_col);
			ghead.hittest	   = "select_col";
			return;
		}
		else {
			ghead.style.cursor = "default";
			ghead.hittest	   = "none";
		}
	}
	ghead.active_head = null;
	var heads = ghead.rows[0].cells;
	for( var i = 0; i < heads.length; i++){
		head = heads[i];
		rect = head.getBoundingClientRect();
		if( event.x >= rect.right - 3 && event.x <= rect.right + 3 ){
			ghead.style.cursor = ws.getCursor(ws.cursor_resize_EW);
			ghead.active_head  = head;
			ghead.hittest	   = "resize";
			return;
		}
		else if( event.x >= rect.left + 3 && event.x <= rect.right - 3 ){
			ghead.style.cursor = ws.getCursor(ws.cursor_select_col);
			ghead.active_head  = head;
			ghead.hittest	   = "select_col";
			return;
		}
		else {
			ghead.style.cursor = "default";
			ghead.hittest	   = "none";
		}
	}
}

/**
 * ˮƽ����Ԫ�ص�����¼�
 * @param ghead �����¼��Ŀ�����
 */
ws.freetable.headerctl.mousedown = function(ghead){
	if( ghead.hittest == "resize" && ghead.active_head != undefined && ghead.active_head != null ){
		ghead.prevX = event.clientX;
		ghead.isResizing = true;
		ghead.setCapture(true);
	}
	else if( ghead.hittest == "select_col" && ghead.active_head != undefined && ghead.active_head != null ){
		ghead.parent.selectedCol = ghead.active_head.cellIndex;
		ghead.parent.selectedRow = null;
		ghead.parent.isSelectAll = false;
		if( typeof(ghead.targetTable.selectCol) == "function" ){
			ghead.targetTable.selectCol(ghead.active_head.cellIndex);
			ghead.targetTable.setActive();
		}
	}
}

/**
 * ˮƽ����Ԫ�ص�����¼�
 * @param ghead �����¼��Ŀ�����
 */
ws.freetable.headerctl.mouseup = function(ghead){
	ghead.isResizing = false;
	ghead.active_head = null;
	ghead.releaseCapture();
}

/**
 * ˮƽ����Ԫ�صĵ�����С�¼�
 * @param e �¼�����
 */
ws.freetable.headerctl.resize = function(e){
	var width = this.active_head.offsetWidth;
	var space = 0, curStyle;
	var diffX = this.prevX - e.clientX;

	if( diffX < 0 )
		this.parent.style.width = this.parent.selectallctl.offsetWidth + this.offsetWidth - diffX;

	curStyle = this.active_head.currentStyle;
	space = ws.toInt(curStyle.paddingLeft) + ws.toInt(curStyle.paddingRight) + ws.toInt(curStyle.borderLeftWidth) + ws.toInt(curStyle.borderRightWidth);
	width -= diffX;
	width -= space;
	if( width < 5 )
		width = 5;
	this.active_head.style.pixelWidth = width;
	
	var row = this.targetTable.rows[this.targetTable.rows.length - 1];
	var cell = null;
	if( row != undefined && row != null ){
		cell = row.cells[this.active_head.cellIndex];
		if( cell != undefined && cell != null ){
			curStyle = cell.currentStyle;
			space = ws.toInt(curStyle.paddingLeft) + ws.toInt(curStyle.paddingRight) + ws.toInt(curStyle.borderLeftWidth) + ws.toInt(curStyle.borderRightWidth);
			cell.style.pixelWidth = width;
			this.active_head.style.pixelWidth = cell.offsetWidth - space;
			if( cell.offsetWidth - space == width )
				this.prevX = e.clientX;
		}
	}
	if( this.parent != undefined && this.parent != null )
		this.parent.checkWH();
	if( diffX > 0 )
		this.parent.style.width = this.parent.selectallctl.offsetWidth + this.offsetWidth;
}

/**
 * ����ˮƽ����Ԫ���������Ʊ����һ����
 */
ws.freetable.headerctl.adjust = function(){
	var space, curStyle, cell, tcell;
	for( var i = 0; i < this.rows[0].cells.length; i++ ){
		tcell = this.targetTable.rows[this.targetTable.rows.length - 1].cells[i];
		cell  = this.rows[0].cells[i];
		curStyle = tcell.currentStyle;
		space = ws.toInt(curStyle.paddingLeft) + ws.toInt(curStyle.paddingRight) + ws.toInt(curStyle.borderLeftWidth) + ws.toInt(curStyle.borderRightWidth);
		cell.style.width = tcell.offsetWidth - space;
	}
}

/**
 * ����ˮƽ����Ԫ�ص�ĳһ���������Ʊ����һ����
 * @param ncol ������ֵ
 */
ws.freetable.headerctl.sync = function(ncol){
	var row = this.targetTable.rows[0];
	var cell = null, space = 0, curStyle = null;

	if( row != undefined && row != null ){
		cell = row.cells[ncol];
//		this.parent.style.width = this.parent.selectallctl.offsetWidth + this.offsetWidth + cell.offsetWidth - this.rows[0].cells[ncol].offsetWidth;
		this.parent.style.width = this.parent.offsetWidth + 50;
		if( cell != undefined && cell != null ){
			curStyle = cell.currentStyle;
			space = ws.toInt(curStyle.paddingLeft) + ws.toInt(curStyle.paddingRight) + ws.toInt(curStyle.borderLeftWidth) + ws.toInt(curStyle.borderRightWidth);
			this.rows[0].cells[ncol].style.width = cell.offsetWidth - space;
		}
	}
}

ws.freetable.headerctl.insertTableCol = function(ncol){
	var cell = this.rows[0].insertCell(ncol);
	cell.innerHTML = "&nbsp;";
}
ws.freetable.headerctl.deleteTableCol = function(ncol){
	var cell = this.rows[0].cells[ncol];
	if( cell != undefined && cell != null )
		this.rows[0].removeChild(cell);
	this.active_head = null;
}
//�п���Ԫ��============================================================================
ws.freetable.columnctl = {};

/**
 * �������ɱ��Ĵ�ֱ����Ԫ��
 * @param standin ռλԪ�ض��󣬿�����ռλԪ��IDҲ������ռλԪ�ض�����
 * @param rows ����
 * @param target �����Ƶı��Ԫ��
 * @param op_parent ���ɱ�����(��ѡ)
 * @return ���ɵĴ�ֱ����Ԫ��
 */
ws.freetable.columnctl.make = function(standin, rows, target, op_parent){
	var parent = op_parent || null;
	var col   = ws.createElement('<table class="column_control" cellspacing=0 onselectstart="return false;" onmousemove="ws.freetable.columnctl.mousemove(this)" onmousedown="ws.freetable.columnctl.mousedown(this)" onmouseup="ws.freetable.columnctl.mouseup(this)>');
	var tbody = ws.createElement("tbody", col);
	var tr, td;
	
	col.parent = parent;
	
	for( var i = 0; i < rows; i++ ){
		tr = ws.createElement("tr", tbody);
		td = ws.createElement("<td nowrap>", tr);
		td.innerHTML = "&nbsp;";
	}

	col.targetTable = target;
	col.isResizing  = false;
	col.active_row  = null;
	col.hittest     = "none";
	col.sync		= ws.freetable.columnctl.sync;
	col.resize		= ws.freetable.columnctl.resize;
	col.adjust		= ws.freetable.columnctl.adjust;
	
	col.insertTableRow = ws.freetable.columnctl.insertTableRow;
	col.deleteTableRow = ws.freetable.columnctl.deleteTableRow;
	
	var el = null;
	if( typeof(standin) == "string" )
		el = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		el = standin;
	if( el != null && el != undefined ){
		el.replaceNode(col);
		col.id = el.id;
	}
	return col;
}

/**
 * ˮƽ����Ԫ�ص�����ƶ��¼�
 * @param gcol �����¼��Ŀ�����
 */
ws.freetable.columnctl.mousemove = function(gcol){
	var row, rect;
	
	if( gcol.isResizing == true && gcol.active_row != undefined && gcol.active_row != null){
		gcol.resize(event);
		return;
	}
	if( gcol.active_row != undefined && gcol.active_row != null ){
		row = gcol.active_row;
		rect = row.getBoundingClientRect();
		if( event.y >= rect.bottom - 3 && event.y <= rect.bottom + 3 ){
			gcol.style.cursor = ws.getCursor(ws.cursor_resize_NS);
			gcol.hittest	  = "resize";
		}
		else if( rect.top + 3 <= event.y && rect.bottom - 3 >= event.y ){
			gcol.style.cursor = ws.getCursor(ws.cursor_select_row);
			gcol.hittest	  = "select_row";
			return;
		}
		else {
			gcol.style.cursor = "default";
			gcol.hittest	  = "none";
		}
	}
	gcol.active_row = null;
	var rows = gcol.rows;
	for( var i = 0; i < rows.length; i++){
		row = rows[i];
		rect = row.getBoundingClientRect();
		if( event.y >= rect.bottom - 3 && event.y <= rect.bottom + 3 ){
			gcol.style.cursor = ws.getCursor(ws.cursor_resize_NS);
			gcol.active_row   = rows[i];
			gcol.hittest	  = "resize";
			return;
		}
		else if( rect.top + 3 <= event.y && rect.bottom - 3 >= event.y ){
			gcol.style.cursor = ws.getCursor(ws.cursor_select_row);
			gcol.active_row   = rows[i];
			gcol.hittest	  = "select_row";
			return;
		}
		else {
			gcol.style.cursor = "default";
			gcol.hittest	  = "none";
		}
	}
}

/**
 * ˮƽ����Ԫ�ص�����¼�
 * @param gcol �����¼��Ŀ�����
 */
ws.freetable.columnctl.mousedown = function(gcol){
	if( gcol.hittest == "resize" && gcol.active_row != undefined && gcol.active_row != null ){
		gcol.prevY = event.clientY;
		gcol.isResizing = true;
		gcol.setCapture(true);
	}
	else if( gcol.hittest == "select_row" && gcol.active_row != undefined && gcol.active_row != null ){
		gcol.parent.selectedRow = gcol.active_row.rowIndex;
		gcol.parent.selectedCol = null;
		gcol.parent.isSelectAll = false;
		if( typeof(gcol.targetTable.selectRow) == "function" ){
			gcol.targetTable.selectRow(gcol.active_row.rowIndex);
			gcol.targetTable.setActive();
		}
	}
}

/**
 * ˮƽ����Ԫ�ص�����¼�
 * @param gcol �����¼��Ŀ�����
 */
ws.freetable.columnctl.mouseup = function(gcol){
	gcol.isResizing = false;
	gcol.active_row = null;
	gcol.releaseCapture();
}

/**
 * ��ֱ����Ԫ�صĵ�����С�¼�
 * @param e �¼�����
 */
ws.freetable.columnctl.resize = function(e){
	var height = this.active_row.cells[0].offsetHeight;
	var diffY  = this.prevY - e.clientY;
	var space  = 0, curStyle;
	curStyle = this.active_row.cells[0].currentStyle;
	height -= diffY;
	height -= space;
	if( height < 5 )
		height = 5;
	this.active_row.cells[0].style.height = height;

	var row = this.targetTable.rows[this.active_row.rowIndex];
	var cell = null;
	if( row != undefined && row != null ){
		cell = row.cells[row.cells.length - 1];
		if( cell != undefined && cell != null ){
			cell.style.height = height;
			curStyle = cell.currentStyle;
			this.active_row.cells[0].style.height = cell.offsetHeight - space;
			if( cell.offsetHeight - space == height )
				this.prevY = e.y;
		}
	}
	if( this.parent != undefined && this.parent != null )
		this.parent.checkWH();
}

/**
 * ������ֱ����Ԫ���������Ʊ����һ����
 */
ws.freetable.columnctl.adjust = function(){
	var space = 0, curStyle, cell, tcell;
	for( var i = 0; i < this.rows.length; i++ ){
		tcell = this.targetTable.rows[i].cells[this.targetTable.rows[i].cells.length - 1];
		cell  = this.rows[i].cells[0];
		curStyle = tcell.currentStyle;
		cell.style.height = tcell.offsetHeight - space;
	}
}

/**
 * ������ֱ����Ԫ�ص�ĳһ���������Ʊ����һ����
 * @param nrow ������ֵ
 */
ws.freetable.columnctl.sync = function(nrow){
	var row = this.targetTable.rows[nrow];
	var cell = null, space = 0, curStyle = null;

	if( row != undefined && row != null ){
		cell = row.cells[0];
		if( cell != undefined && cell != null ){
			curStyle = cell.currentStyle;
			this.rows[nrow].cells[0].style.height = cell.offsetHeight - space;
		}
	}
}

ws.freetable.columnctl.insertTableRow = function(nrow){
	var row  = this.insertRow(nrow);
	var cell = row.insertCell();
	cell.innerHTML = "&nbsp;";
}
ws.freetable.columnctl.deleteTableRow = function(nrow){
	this.deleteRow(nrow);
	this.active_row = null;
}
//���ɱ��ʵ��==========================================================================

ws.freetable.body = {};

/**
 * �������ɱ���ʵ��
 * @param standin ռλԪ�ض��󣬿�����ռλԪ��IDҲ������ռλԪ�ض�����
 * @param op_nrow ����(��ѡ��Ĭ��Ϊ3��)
 * @param op_ncol ����(��ѡ��Ĭ��Ϊ3��)
 * @param op_className ���ʵ�����ʽ(��ѡ)
 * @param op_parent ���ɱ�����(��ѡ)
 * @param op_cellWidth ��Ԫ��ĳ�ʼ���(��ѡ)
 * @return ���ɵı��ʵ��Ԫ��
 */
ws.freetable.body.make = function(standin, op_nrow, op_ncol, op_className, op_parent, op_cellWidth){
	var nrow = op_nrow || 3, ncol = op_ncol || 5, className = op_className || "", parent = op_parent || null, cellWidth = op_cellWidth || "";
	var fbody = ws.createElement('<table cellspacing=0 cellpadding=2 type="ws_fbody">');
	fbody.className = className;
	fbody.parent	= parent;
	var tbody  = ws.createElement("tbody", fbody);
	var tr, td;
	for( var i = 0; i < nrow; i++ ){
		tr = ws.createElement("tr", tbody);
		for( var j = 0; j < ncol; j++ ){
			td = ws.createElement("<td nowrap>", tr);
			td.innerHTML	= "&nbsp;"
			td.ownertTable	= fbody;
			td.fireChange	= ws.freetable.body.fireChange;
			td.style.width	= "5";
		}
		td = ws.createElement('<td style="width:0;visibility:hidden;margin-left:0;margin-right:0;padding-left:0;padding-right:0;border-left:0;border-right:0;">', tr);
		td.boundaryCell = true;
	}
	tr = ws.createElement('<tr style="visibility:hidden;height:0;margin-top:0;margin-bottom:0;padding-top:0;padding-bottom:0;border-top:0;border-bottom:0;">', tbody);
	for( var j = 0; j < ncol; j++ ){
		td = ws.createElement('<td style="height:0;margin-top:0;margin-bottom:0;padding-top:0;padding-bottom:0;border-top:0;border-bottom:0;">', tr);
		td.style.width  = cellWidth;
		td.boundaryCell = true;
	}

	var el = null;
	if( typeof(standin) == "string" )
		el = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		el = standin;
	if( el != null && el != undefined ){
		el.replaceNode(fbody);
		fbody.id = el.id;
	}

	fbody.calcGrid	     = ws.freetable.body.calcGrid;
	fbody.mergeCell      = ws.freetable.body.mergeCell;
	fbody.splitCell	     = ws.freetable.body.splitCell;
	fbody.insertCell     = ws.freetable.body.insertCell;
	fbody.insertTableCol = ws.freetable.body.insertTableCol;
	fbody.deleteTableCol = ws.freetable.body.deleteTableCol;
	fbody.insertTableRow = ws.freetable.body.insertTableRow;
	fbody.deleteTableRow = ws.freetable.body.deleteTableRow;

	fbody.calcGrid();
	
	return fbody;
}

/**
 * ���ɱ��Ԫ���е��¼�������Ԫ������ݷ����仯ʱ�������Ե������ɱ�����пؼ���״̬
 */
ws.freetable.body.fireChange = function(){
	if( this.ownertTable != undefined && this.ownertTable != null ){
		var ftable = this.ownertTable.parent;
		if( ftable != undefined && ftable != null ){
			if( typeof(ftable.onCellChange) == "function" ){
				for( var i = this.parentElement.rowIndex; i < this.parentElement.rowIndex + this.rowSpan; i++ )
					ftable.onCellChange(i, this.gridcol);
				for( var i = this.gridcol; i < this.gridcol + this.colSpan; i++ )
					ftable.onCellChange(this.parentElement.rowIndex, i);
			}
			ftable.checkWH();
		}
	}
}

/**
 * �Ա�����ÿһ����Ԫ�������ֵ
 * grid[i][j].realcell ��ʾ��ǰλ���Ƿ������ʵ�ĵ�Ԫ��
 * grid[i][j].row ��ʾ��䵱ǰλ�õĵ�Ԫ�����ڵ���
 * grid[i][j].col ��ʾ��䵱ǰλ�õĵ�Ԫ�����ڵ���
 */
ws.freetable.body.calcGrid = function (){
	if( this.grid != undefined && this.grid != null ){
		for( var i = 0; i < this.rows.length; i++ ){
			if( this.grid[i] != undefined && this.grid[i] != null )
				this.grid[i].length = 0;
			else this.grid[i] = new Array();
		}
	}
	else {
		this.grid = new Array();
		for( var i = 0; i < this.rows.length; i++ )
			this.grid[i] = new Array();
	}
	var cell, gcol;
	for( var i = 0; i < this.rows.length; i++ ){
		gcol = 0;
		for( var j = 0; j < this.rows[i].cells.length; j++ ){
			while( this.grid[i][gcol] != undefined && this.grid[i][gcol] != null )
				gcol++;
			
			cell = this.rows[i].cells[j];
			cell.gridcol = gcol;
			if( cell.colSpan > 1 ){
				for( var k = gcol + 1; k < gcol + cell.colSpan; k++ ){
					this.grid[i][k] = {row:i, col:j, realcell:false};
				}
			}

			if( cell.rowSpan > 1 ){
				for( var k = i + 1; k < i + cell.rowSpan; k++ ){
					for( var h = gcol; h < gcol + cell.colSpan; h++ ){
						this.grid[k][h] = {row:i, col:j, realcell:false};
					}
				}
			}
			this.grid[i][gcol] = {row:i, col:j, realcell:true};
			gcol += cell.colSpan;
		}
	}
}

/**
 * �ϲ���Ԫ��
 * @param cellA Ҫ�ϲ��ĵ�Ԫ��Ԫ�ض���
 * @param cellB Ҫ�ϲ��ĵ�Ԫ��Ԫ�ض���
 * @return �ϲ���ĵ�Ԫ��
 */
ws.freetable.body.mergeCell = function (cellA, cellB){
	if( this.grid == undefined || this.grid == null )
		return null;
	var mergeType = null, temp;
	if( Math.abs(cellA.cellIndex - cellB.cellIndex) == 1 && cellA.parentElement.rowIndex == cellB.parentElement.rowIndex)
		mergeType = "mergecol";
	else if( cellA.parentElement.rowIndex + cellA.rowSpan == cellB.parentElement.rowIndex || cellB.parentElement.rowIndex + cellB.rowSpan == cellA.parentElement.rowIndex )
		mergeType = "mergerow";
	if( mergeType == null )
		return null;
	
	if( mergeType == "mergecol" ){
		if( cellA.rowSpan != cellB.rowSpan )
			return null;
	
		if( cellA.cellIndex > cellB.cellIndex ){
			temp  = cellA;
			cellA = cellB;
			cellB = temp;
		}
		if( cellA.innerHTML == "&nbsp;" )
			cellA.innerHTML = "";
		if( cellB.innerHTML == "&nbsp;" )
			cellB.innerHTML = "";
		for( var i = 0; i < cellB.children.length; i++ )
			cellA.appendChild(cellB.children[i]);
		if( cellA.innerHTML == "" )
			cellA.innerHTML = "&nbsp;";
		cellA.colSpan += cellB.colSpan;
		cellB.parentElement.removeChild(cellB);
		if( typeof(this.setItemSelected) == "function" )
			this.setItemSelected(cellB, false);
		if( typeof( cellA.fireChange ) == "function" )
			cellA.fireChange();
	}
	else {
		if( cellA.colSpan != cellB.colSpan || cellA.gridcol != cellB.gridcol)
			return null;
	
		if( cellA.parentElement.rowIndex > cellB.parentElement.rowIndex ){
			temp  = cellA;
			cellA = cellB;
			cellB = temp;
		}
		if( cellA.innerHTML == "&nbsp;" )
			cellA.innerHTML = "";
		if( cellB.innerHTML == "&nbsp;" )
			cellB.innerHTML = "";
		for( var i = 0; i < cellB.children.length; i++ )
			cellA.appendChild(cellB.children[i]);
		if( cellA.innerHTML == "" )
			cellA.innerHTML = "&nbsp;";
		cellA.rowSpan += cellB.rowSpan;
		cellB.parentElement.removeChild(cellB);
		if( typeof(this.setItemSelected) == "function" )
			this.setItemSelected(cellB, false);
		if( typeof( cellA.fireChange ) == "function" )
			cellA.fireChange();
	}
	if( this.parent != undefined && this.parent != null )
		this.parent.checkWH();

	this.calcGrid();
	return cellA;
}

/**
 * ��ֵ�Ԫ��
 * @param cell Ҫ��ֵĵ�Ԫ��Ԫ�ض���
 * @param op_splitDir ��ַ�ʽ(��ѡ��Ĭ��Ϊ"auto")����ЧֵΪhorz:ˮƽ��֣�vert��ֱ��֣�auto:�Զ�������ַ���
 * @return ��ֺ���µ�Ԫ��
 */
ws.freetable.body.splitCell = function (cell, op_splitDir){
	var splitDir = op_splitDir || "auto";
	var newcell = null, nrow;
	
	if( splitDir == "auto" ){
		if( cell.colSpan > 1 )
			splitDir = "horz";
		else if( cell.rowSpan > 1 )
			splitDir = "vert";
	}
	
	nrow = cell.parentElement.rowIndex;
	if( cell.colSpan > 1 && splitDir == "horz" ){
		newcell = this.insertCell(nrow, cell.gridcol + cell.colSpan - 1);
		cell.colSpan -= 1;
		newcell.innerHTML = "&nbsp;";
		newcell.rowSpan   = cell.rowSpan;
	}
	else if( cell.rowSpan > 1 && splitDir == "vert" ){
		newcell = this.insertCell(nrow + cell.rowSpan - 1, cell.gridcol);
		cell.rowSpan -= 1;
		newcell.innerHTML = "&nbsp;";
		newcell.colSpan   = cell.colSpan;
	}
	else return null;
	if( this.parent != undefined && this.parent != null )
		this.parent.checkWH();

	this.calcGrid();
	return newcell;
}

/**
 * ��ָ����λ�ò���һ���µ�Ԫ��
 * @param nrow ��
 * @param ncol ��
 * @return �µ�Ԫ��Ԫ��
 */
ws.freetable.body.insertCell = function(nrow, ncol){
	var row = this.rows[nrow];
	if( row == undefined || row == null )
		return null;
	var index = 0, cell, found = false;
	for( index = 0; index < row.cells.length; index ++ ){
		cell = row.cells[index];
		if( cell.gridcol > ncol ){
			found = true;
			break;
		}
	}
	if( found != true )
		index = -1;
	cell = row.insertCell(index);
	cell.gridcol = ncol;
	cell.ownertTable = this;
	cell.fireChange	 = ws.freetable.body.fireChange;
	return cell;
}

ws.freetable.body.insertTableCol = function(ncol){
	var gridObj, cell;
	for( var i = 0; i < this.rows.length; i++ ){
		gridObj = this.grid[i][ncol];
		if( gridObj.realcell == true ){
			cell = this.rows[i].insertCell(gridObj.col);
			cell.gridcol = ncol;
			cell.ownertTable = this;
			cell.fireChange	 = ws.freetable.body.fireChange;
			cell.innerHTML   = "&nbsp;";
		} else {
			if( ncol == gridObj.col ){
				cell = this.rows[i].insertCell(gridObj.col);
				cell.gridcol = ncol;
				cell.ownertTable = this;
				cell.fireChange	 = ws.freetable.body.fireChange;
				cell.innerHTML   = "&nbsp;";
			}
			else if( gridObj.row == i )
				this.rows[gridObj.row].cells[gridObj.col].colSpan ++;
		}
	}
	this.rows[this.rows.length - 1].cells[ncol].style.cssText = "height:0;margin-top:0;margin-bottom:0;padding-top:0;padding-bottom:0;border-top:0;border-bottom:0;";
	this.rows[this.rows.length - 1].cells[ncol].innerHTML = "";
	this.calcGrid();
	this.parent.style.width = this.parent.selectallctl.offsetWidth + this.offsetWidth + 100;
}
ws.freetable.body.deleteTableCol = function(ncol){
	var gridObj, cell;
	for( var i = 0; i < this.rows.length; i++ ){
		gridObj = this.grid[i][ncol];
		if( gridObj.realcell == true ){
			if( this.rows[gridObj.row].cells[gridObj.col].colSpan > 1 )
				this.rows[gridObj.row].cells[gridObj.col].colSpan --;
			else this.rows[gridObj.row].removeChild(this.rows[gridObj.row].cells[gridObj.col]);
		} else {
			if( ncol != gridObj.col && gridObj.row == i)
				this.rows[gridObj.row].cells[gridObj.col].colSpan --;
		}
	}
	this.calcGrid();
	this.parent.style.width = this.parent.selectallctl.offsetWidth + this.offsetWidth;
}

ws.freetable.body.insertTableRow = function(nrow){
	var row = this.insertRow(nrow);
	var cell, gridObj, preGrid;
	for( var i = 0; i < this.rows[this.rows.length - 1].cells.length; i++){
		gridObj = this.grid[nrow][i];
		if( gridObj.realcell == true ){
			cell = row.insertCell();
			cell.ownertTable = this;
			cell.fireChange	 = ws.freetable.body.fireChange;
			cell.innerHTML   = "&nbsp;";
		} else {
			if( nrow == gridObj.row ){
				cell = row.insertCell();
				cell.ownertTable = this;
				cell.fireChange	 = ws.freetable.body.fireChange;
				cell.innerHTML   = "&nbsp;";
			}
			else {
				if( i > 0 )
					preGrid = this.grid[nrow][i - 1];
				else 
					preGrid = null;
				if( !preGrid || preGrid.row != gridObj.row || preGrid.col != gridObj.col )
					this.rows[gridObj.row].cells[gridObj.col].rowSpan ++;
			}
		}
	}
	this.calcGrid();
}

ws.freetable.body.deleteTableRow = function(nrow){
	var gridObj, cell, row;
	for( var i = 0; i < this.rows[this.rows.length - 1].cells.length; i++ ){
		gridObj = this.grid[nrow][i];
		if( gridObj.realcell == true ){
			cell = this.rows[nrow].cells[gridObj.col];
			if( cell.rowSpan > 1 ){
				var temp = this.insertCell(nrow + 1, gridObj.col);
				temp.replaceNode(cell);
				cell.rowSpan --;
			}
		} else {
			if( gridObj.row != nrow && gridObj.col == i )
				this.rows[gridObj.row].cells[gridObj.col].rowSpan --;
		}
	}
	this.deleteRow(nrow);
	this.calcGrid();
}