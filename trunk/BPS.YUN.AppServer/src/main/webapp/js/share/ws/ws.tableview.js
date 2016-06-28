ws.TableView = {};

ws.TableView.setRadioBoxValue = function(row, box){
	var ncol = box.parentElement.cellIndex;
	var name, value;
	name  = this.columns[ncol].name;
	value = this.columns[ncol].value;

	for( var i = 0; i < this.columns.length; i++){
		if( this.columns[i].name == name ){
			if( this.columns[i].value == value )
				row.cells[i].firstChild.checked = true;
			else
				row.cells[i].firstChild.checked = false;
		}
	}
}
/**
 * ����һ�еļ�¼ֵ
 * @param tRow ���������ж���
 * @param record ��¼����
 * @param op_pattern ���record��������op_pattern����˵��Table������������֮��Ĺ�ϵ
 */
ws.TableView.setRowData = function(tRow, record, op_pattern){
	var pattern = op_pattern || null;
	var value, isArray = false, row, td, link, linkAction, ncol;
	if( typeof( tRow ) == "object" )
		row = tRow;
	else
		row = this.table.tBodies[0].rows[tRow];
	if( !row )
		return;
	if( record == null || record == undefined ){
		row.isEmpty = true;
		return;
	}
	row.isEmpty = false;
	if( record instanceof Array )
		isArray = true;
	for( var i = 0; i < this.columns.length; i++){
		td = row.cells[i];
		link       = this.columns[i].link;
		linkAction = this.columns[i].linkAction;
		if( this.columns[i].type == "Static" )
			value = this.columns[i].value || "";
		else {
			if( isArray ){
				if( pattern )
					ncol = eval("pattern." + this.columns[i].name);
				else
					ncol = i;
				if( ncol == undefined || ncol == null )
					ncol = i;
				value = record[ncol];
			}
			else 
				value = eval("record." + this.columns[i].name);
		}
		if( value == null || value == "undefined")
			value = "";
		if( value instanceof Date ){
			if( this.columns[i].format != "" ){
				value = ws.Date.format(value, this.columns[i].format);
			}
			else {
				if( this.columns[i].type == "Date" )
					value = value.toLocaleDateString();
				else if( this.columns[i].type == "Time" )
					value = value.toLocaleTimeString();
				else 
					value = value.toLocaleString();
			}
		}
		if( this.columns[i].name )
			row[this.columns[i].name] = value;

		if( this.columns[i].type == "CheckBox" ){
			if( td.children.length == 0 )
				td.innerHTML = "<input type='checkbox' >";
			if( ws.toBoolean(value) )
				td.firstChild.checked = true;
			else
				td.firstChild.checked = false;
			continue;
		} 
		else if( this.columns[i].type == "Select" ){
			value = this.getSelectLabel(this.columns[i].name, value);
		}
		else if( this.columns[i].type == "RadioBox" ){
			if( td.children.length == 0 )
				td.innerHTML = "<input type='checkbox' >";
			if( this.columns[i].value == value  )
				td.firstChild.checked = true;
			else
				td.firstChild.checked = false;
			var oThis = this, box = td.firstChild;
			box.onclick = function(){ oThis.setRadioBoxValue(row, event.srcElement); }
			continue;
		}
		if( link != null && link != undefined ){
			html = "<a href=\"" + ws.replaceValue(link, row) + "\"";
			if( linkAction != null && linkAction != undefined )
				html += " onclick=\"" + ws.replaceValue(linkAction, row) + "\"";
			html += ">" + value + "</a>";
			td.innerHTML = html;
		}
		else td.innerHTML = value;
	}
}

/**
 * ��ȡһ�еļ�¼ֵ
 * @param tRow ���������ж���
 * @param op_pattern(��ѡ),���ָ��op_pattern,�򷵻�����Ϊ����,op_pattern����ָ�����ص������±�������֮��Ķ�Ӧ��ϵ;
 * ���δָ��op_pattern,�򷵻�Object���͵Ķ���,������Ϊ��������ԡ�
 * @return ���ɵ����ݻ����
 */
ws.TableView.getRowData = function(tRow, op_pattern){
	var pattern = op_pattern || null;
	var value, row, cell, record, ncol;
	if( typeof( tRow ) == "object" )
		row = tRow;
	else
		row = this.table.tBodies[0].rows[tRow];
	if( !row || row.isEmpty)
		return null;
	if( pattern != null )
		record = new Array();
	else
		record = new Object();
	for( var i = 0; i < this.columns.length; i++){
		cell = row.cells[i];
		if( this.columns[i].type == "Static" )
			continue;
		else if( this.columns[i].type == "CheckBox" )
			value = cell.firstChild.checked;
		else if( this.columns[i].type == "RadioBox" ){
			value = cell.firstChild.checked;
			if( value == true )
				value = this.columns[i].value;
			else
				continue;
		}
		else
			value = row[this.columns[i].name];
		if( pattern != null ){
			ncol = eval("pattern." + this.columns[i].name);
			if( ncol != undefined && ncol != null )
				record[ncol] = value;
		}
		else
			record[this.columns[i].name] = value;
	}
	return record;
}

/**
 * ����recordSet��nStart��ʼ������
 * @param recordSet ��¼�����󣬱���������
 * @param op_start ��ʼ��(��ѡ��Ĭ��Ϊ0)
 * @param op_pattern ����˵��Table������������֮��Ĺ�ϵ(��ѡ),���ָ��op_pattern,��ÿһ�е����ݱ�ʾΪ����,
 * ����ÿһ�е����ݱ�ʾΪ�Զ������.
 * @param op_minRow ��С����(��ѡ��Ĭ��Ϊ��ʼ����),ֻ���ڲ���ʾ������ʱ����Ч
 */
ws.TableView.setTableData = function(recordSet, op_start, op_pattern, op_minRow){
	var start = op_start || 0, minRow = op_minRow || this.initRow;
	this.clearAll();
	if( recordSet instanceof Array ){
		if( this.navigatebar != null ){
			for( var i = 0; i < this.navigatebar.rowPerPage; i++ ){
				if( start + i < recordSet.length )
					this.setRowData(i, recordSet[start + i], op_pattern);
				else
					break;
			}
		}
		else {
			var newRow = 0, curRow;
			newRow = recordSet.length - start;
			if( newRow < minRow )
				newRow = minRow;
			curRow = this.table.tBodies[0].rows.length;
			if( newRow > curRow ){
				this.appendEmptyRow(newRow - curRow);
			}
			else if( newRow < curRow ){
				for( var i = 0; i < curRow - newRow; i++ )
					this.deleteRow(-1);
			}
			
			for( var i = 0; i < recordSet.length - start; i++ ){
				this.setRowData(i, recordSet[start + i], op_pattern);
			}
		}
	}
	this.setHeadState();
}

/**
 * ��ȡTableView�б�ʾ�����ݼ�
 * @param op_start ��ʼ��(��ѡ��Ĭ��Ϊ0)
 * @param op_end   ������(��ѡ��Ĭ��Ϊ������һ)
 * @param op_pattern ����˵��Table������������֮��Ĺ�ϵ(��ѡ),���ָ��op_pattern,��ÿһ�е����ݱ�ʾΪ����,
 * ����ÿһ�е����ݱ�ʾΪ�Զ������.
 * @return ���ذ����������ݵ����顣
 */
ws.TableView.getTableData = function(op_pattern, op_start, op_end){
	var start = op_start || 0, end = op_end || this.table.tBodies[0].rows.length - 1;
	var record, recordSet = new Array();
	for( var i = start; i <= end; i++ ){
		record = this.getRowData(i, op_pattern);
		if( record != null )
			recordSet.push(record);
	}
	return recordSet;
}
/**
 * ����һ����¼
 * @param record ��¼����
 * @param op_pattern ���record��������op_pattern����˵��Table������������֮��Ĺ�ϵ 
 * @param op_nRow ����λ��(��ѡ)��Ĭ�ϲ��뵽���һ����¼
 */
ws.TableView.insertRow = function(record, op_pattern, op_nRow){
	var index = op_nRow || -1;

	if( this.navigatebar == null || this.navigatebar == undefined ){
		var row = this.table.tBodies[0].insertRow(index);
		row.isEmpty = false;
		for( var i = 0; i < this.columns.length; i++){
			td = ws.createElement("<td nowrap>", row);
			td.innerText = " ";
		}	
		this.setRowData(row.rowIndex, record, op_pattern);
		this.setRowStyle();
	}
}
/**
 * ׷�Ӽ�¼��������ڿ��������ÿ��е�ֵ
 * @param record ��¼����
 * @param op_pattern ���record��������op_pattern����˵��Table������������֮��Ĺ�ϵ 
 */
ws.TableView.appendRow = function(record, op_pattern){
	var pos = -1;

	for( var i = 0; i < this.table.tBodies[0].rows.length; i++ ){
		if( this.table.tBodies[0].rows[i].isEmpty == true ){
			pos = i;
			break;
		}
	}
	if( this.navigatebar == null || this.navigatebar == undefined ){
		if( pos != -1 )
			this.setRowData(pos, record, op_pattern);
		else 
			this.insertRow(record, op_pattern, -1);
	}
	else {
		if( this.curPage == this.lastPage && pos != -1 ){
			this.setRowData(pos, record, op_pattern);
			this.setTotalRows(this.totalRows + 1);
		}
		else {
			this.setTotalRows(this.totalRows + 1);
			this.goPage(this.lastPage);
		}
		this.curPage = this.lastPage;
		this.setNavigateBarState();
	}
}
/**
 * ׷�ӿ���
 * @param count ���е�����
 */
ws.TableView.appendEmptyRow = function(count){
	var row;
	for( var i = 0; i < count; i++ ){
		row = this.table.tBodies[0].insertRow();
		row.isEmpty = true;
		for( var j = 0; j < this.columns.length; j++){
			td = ws.createElement("<td nowrap>", row);
			td.innerText = " ";
		}
	}
	this.setRowStyle();
}
/**
 * ɾ��һ����¼
 * @param iIndex ɾ����¼���ڵ�˳���
 */
ws.TableView.deleteRow = function(iIndex){
	if( this.navigatebar == null || this.navigatebar == undefined ){
		this.table.tBodies[0].deleteRow(iIndex);
	}
	else {
		var curPage = this.curPage;
		this.setTotalRows(this.totalRows - 1);
		this.goPage(curPage);
	}
}
/**
 * ɾ�����м�¼
 */
ws.TableView.deleteAll = function(){
	var count = this.table.tBodies[0].rows.length;
	for( var i = 0; i < count; i++ )
		this.table.tBodies[0].deleteRow(0);
}

/**
 * ������м�¼��ֵ
 */
ws.TableView.clearAll = function(){
	var row, td;
	for( var i = 0; i < this.table.tBodies[0].rows.length; i++ ){
		row = this.table.tBodies[0].rows[i];
		row.isEmpty = true;
		for( var j = 0; j < this.columns.length; j++ ){
			td = row.cells[j];
			td.innerHTML = "&nbsp;";
			if( this.columns[j].name )
				eval("row." + this.columns[j].name + "=null");
		}
	}
}

/**
 * ��ȡ��Ԫ���о���ָ��ֵ���е�˳���
 * @param colName ���бȽϵ�����
 * @param cellValue ��Ԫ���е�ֵ
 * @param startRow ��ʼ�Ƚϵ���[��ѡ]��Ĭ�ϴӵ�0�п�ʼ
 * @return ˳���,���û�з��ϵ����򷵻�-1
 */
ws.TableView.getRowIndex = function(colName, cellValue, startRow){
	var start = startRow  || 0;
	var value = cellValue || "";
	var col = null;
	for( var i = 0; i < this.columns.length; i++ ){
		if( this.columns[i].name == colName ){
			col = this.columns[i];
			break;
		}
	}
	if( col == null )
		return -1;
	
	var rows  = this.table.tBodies[0].rows;
	var result, cellVal;
	for( var i = start; i < rows.length; i++){
		cellVal = eval("rows[i]." + colName);
		result = ws.TableView.compare(col, cellVal, value); 
		if( result == 0 )
			return i;
	}
	return -1;
}
/**
 * �Ƚ�ָ����������ֵ�Ĵ�С
 * @param col ��������
 * @param value1 ��ֵ
 * @param value2 ��ֵ
 * @return -1С��/0����/1����
 */
ws.TableView.compare = function(col, value1, value2){
	var v1, v2;
	if( col.type == "Date" ){
		if( value1 instanceof Date )
			v1 = value1.toLocaleDateString();
		else v1 = value1;
		if( value2 instanceof Date )
			v2 = value2.toLocaleDateString();
		else v2 = value2;
	} else if( col.type == "Time" ){
		if( value1 instanceof Date )
			v1 = value1.toLocaleTimeString();
		else v1 = value1;
		if( value2 instanceof Date )
			v2 = value2.toLocaleTimeString();
		else v2 = value2;
	} else if( col.type == "DateTime" ){
		if( value1 instanceof Date )
			v1 = value1.toLocaleString();
		else v1 = value1;
		if( value2 instanceof Date )
			v2 = value2.toLocaleString();
		else v2 = value2;
	} else {
		v1 = value1;
		v2 = value2;
	}
	if( v1 > v2 )
		return 1;
	else if( v1 == v2 )
		return 0;
	else return -1;
}
/**
 * ��ָ�����н���������
 * @param colName ����
 * @param bAscending�Ƿ�����[��ѡ]��Ĭ�ϲ�����������
 */
ws.TableView.sort = function(colName, bAscending){
	var ascending = bAscending || false;
	var col = null, ncol, tmp, pattern = new Object();
	if( colName == null || colName == undefined || colName == "" )
		return;
	for( var i = 0; i < this.columns.length; i++ ){
		pattern[this.columns[i].name] = i;
		if( this.columns[i].name == colName ){
			col  = this.columns[i];
			ncol = i;
		}
	}
	if( col == null )
		return;
	var rows = this.table.tBodies[0].rows;
	var v1, v2, result, length;
	if( this.navigatebar != null && this.navigatebar != undefined && this.curPage == this.lastPage ){
		length = this.totalRows % this.navigatebar.rowPerPage;
		if( length == 0 )
			length = rows.length;
		else length++;
	} else length = rows.length;

	var data = this.getTableData(pattern);
	for( var i = 0; i < data.length; i++ ){
		for( var j = 0; j < i; j++ ){
			v1 = data[i][ncol];
			v2 = data[j][ncol];
			if( v1 == null || v2 == null )
				continue;
			result = ws.TableView.compare(col, v1, v2);
			if( (ascending && result == -1) || (!ascending && result == 1) ){
				tmp = data[i];
				data[i] = data[j];
				data[j] = tmp;
			}
		}
	}
	this.setTableData(data, 0, pattern);
	if( typeof(this.table.clear) == "function" )
		this.table.clear();
}

/**
 * ������ż�е���ʽ
 */
ws.TableView.setRowStyle = function(){
	var rows = this.table.tBodies[0].rows;
	for( var i = 0; i < rows.length; i++ ){
		if( (i % 2) == 0 ){
			ws.removeClass(rows[i], "even_row");
			ws.addClass(rows[i], "odd_row");
		}
		else {
			ws.removeClass(rows[i], "odd_row");
			ws.addClass(rows[i], "even_row");
		}
	}
}

/**
 * �����ͷ����������¼�������
 */
ws.TableView.onsort = function(){
	var el = window.event.target || window.event.srcElement;
	while (el.tagName != "TD")
		el = el.parentNode;
	var colName = el.name;
	var tv = el.parentNode;
	while( tv.getAttribute("type") != "ws_tableview" )
		tv = tv.parentNode;
	if( colName == null || colName == undefined )
		return;
	var ascending;
	if( el.ascending == true )
		ascending = false;
	else 
		ascending = true;
	tv.sort(colName, ascending);
	el.ascending = ascending;
	tv.setHeadState(el, ascending);
	
	tv.setRowStyle();
}

/**
 * ���ñ�ͷ����״̬
 * @param tdHead ��ͷԪ�ض���
 * @param ascending �Ƿ�����
 */
ws.TableView.setHeadState = function(tdHead, ascending){
	var tds = this.table.tHead.rows[0].all.tags("td");
	for( var i = 0; i < tds.length; i++){
		if( tds[i] == tdHead ){
			tds[i].className = "active";
			tds[i].ascending = ascending;
			if( ascending )
				tds[i].sortImg.src = eval(this.table.tHead.currentStyle.sortimg_asc);
			else 
				tds[i].sortImg.src = eval(this.table.tHead.currentStyle.sortimg_des);
		} else {
			tds[i].className = "deactive";
			tds[i].ascending = null;
			tds[i].sortImg.src = eval(this.table.tHead.currentStyle.sortimg_blank);
		}
	}
}
/**
 * �������ݵ����������������ɷ�ҳ������Ϣ
 * @param totalRows ����������
 */
ws.TableView.setTotalRows = function(totalRows){
	if( this.navigatebar == null || this.navigatebar == undefined )
		return;
	if( totalRows < 0 )
		totalRows = 0;
	this.totalRows = totalRows;
	this.lastPage  = Math.ceil( totalRows / this.navigatebar.rowPerPage - 1 );
	if( this.lastPage < 0 )
		this.lastPage = 0;
	this.curPage   = 0;
	this.appendEmptyRow(this.navigatebar.rowPerPage - this.table.tBodies[0].rows.length);
	this.setNavigateBarState();
}
/**
 * ��ת��ָ��ҳ
 * @param page ҳ��
 */
ws.TableView.goPage = function(page){
	if( this.navigatebar == null || this.navigatebar.pageAction == null )
		return;

	if( page < 0 )
		this.curPage = 0;
	else if( page > this.lastPage )
		this.curPage = this.lastPage;
	else this.curPage = page;

	this.clearAll();

	var result;
	var code = "result = " + this.navigatebar.pageAction + "(" + this.curPage * this.navigatebar.rowPerPage + "," + this.navigatebar.rowPerPage + ")";
	eval(code);
	
	//��������
	if( result instanceof Array ){
		this.setTableData(result);
	}
	//�ָ���ͷ״̬
	var tds = this.table.tHead.rows[0].all.tags("td");
	for( var i = 0; i < tds.length; i++){
		tds[i].ascending = null;
		tds[i].sortImg.src = eval(this.table.tHead.currentStyle.sortimg_blank);
	}

	this.setNavigateBarState();
}
/**
 * ���ݵ�ǰ���ݵ����ڵ�ҳ��Ƶ�������״̬
 */
ws.TableView.setNavigateBarState = function(){
	if( this.curPage == 0 ){
		this.navigatebar.btnFirst.disable();
		this.navigatebar.btnPrev.disable();
		this.navigatebar.btnQPrev.disable();
	} else {
		this.navigatebar.btnFirst.enable();
		this.navigatebar.btnPrev.enable();
		this.navigatebar.btnQPrev.enable();
	}
	if( this.curPage == this.lastPage ){
		this.navigatebar.btnLast.disable();
		this.navigatebar.btnNext.disable();
		this.navigatebar.btnQNext.disable();
	} else {
		this.navigatebar.btnLast.enable();
		this.navigatebar.btnNext.enable();
		this.navigatebar.btnQNext.enable();
	}
	var pageButtons = this.navigatebar.pageButtons;
	var startPage;
	if( this.curPage < pageButtons[0].pageNo ){
		startPage = this.curPage;
	} else if( this.curPage > pageButtons[this.navigatebar.pageNum - 1].pageNo ){
		startPage = this.curPage - this.navigatebar.pageNum + 1;
	} else {
		startPage = pageButtons[0].pageNo;
	}
	for( var i = 0; i < pageButtons.length; i++ ){
		pageButtons[i].enable();
		pageButtons[i].innerText = i + startPage + 1;
		pageButtons[i].pageNo = i + startPage;
		if( pageButtons[i].pageNo == this.curPage )
			pageButtons[i].setCheck(true);
		else pageButtons[i].setCheck(false);

		if( pageButtons[i].pageNo > this.lastPage )
			pageButtons[i].disable();
		else pageButtons[i].enable();
	}
}

//====BEGIN ������ť�¼�������=============================================
ws.TableView.doPage = function(){
	var el = event.target || event.srcElement;
	while (el.tagName != "TD")
		el = el.parentNode;
	var tv = el.parentNode;
	while( tv.getAttribute("type") != "ws_tableview" )
		tv = tv.parentNode;	
	tv.goPage(el.pageNo);
}
ws.TableView.doPrevPage = function(){
	var tv = ws.TableView.getTableView(window.event);
	tv.goPage(tv.curPage - 1);
}
ws.TableView.doNextPage = function(){
	var tv = ws.TableView.getTableView(window.event);
	tv.goPage(tv.curPage + 1);
}
ws.TableView.doQPrevPage = function(){
	var tv = ws.TableView.getTableView(window.event);
	tv.goPage(tv.curPage - tv.navigatebar.pageNum);
}
ws.TableView.doQNextPage = function(){
	var tv = ws.TableView.getTableView(window.event);
	tv.goPage(tv.curPage + tv.navigatebar.pageNum);
}
ws.TableView.doFirstPage = function(){
	var tv = ws.TableView.getTableView(window.event);
	tv.goPage(0);
}
ws.TableView.doLastPage = function(){
	var tv = ws.TableView.getTableView(window.event);
	tv.goPage(tv.lastPage);
}
//====END ������ť�¼�������===============================================

/**
 * ���ݷ�����TD�ϵ��¼���ȡ������TableView����
 * @param event �¼�
 * @return TableView����
 */
ws.TableView.getTableView = function(event){
	var el = event.target || event.srcElement;
	while (el.tagName != "TD")
		el = el.parentNode;
	var tv = el.parentNode;
	while( tv.getAttribute("type") != "ws_tableview" )
		tv = tv.parentNode;
	return tv;
}

/**
 * ���ݴ���ֵ���Ҵ�������
 * @param colName ����
 * @param value ����ֵ
 * @return ��������
 */
ws.TableView.getSelectLabel = function(colName, value){
	var selcode = this.select_code[colName];
	if( selcode == null || selcode == undefined )
		return "&nbsp;";
	var result = selcode["val_" + value];
	if( result == null || result == undefined )
		result = "&nbsp;";
	return result;
}
/**
 * ���ô���ѡ���������͵Ĵ��붨��
 * @param colName ����
 * @param value ����ֵ
 * @param label ��������
 */
ws.TableView.setSelectCode = function(colName, value, label){
	var selcode = this.select_code[colName];
	if( selcode == null || selcode == undefined ){
		selcode = new Object();
		this.select_code[colName] = selcode;
	}
	selcode["val_" + value] = label;
}
/**
 * װ�����ѡ���������͵Ĵ��붨��
 */
ws.TableView.loadSelectCode = function(res_url){
	var resNode = ws.res.findResourceNode(res_url);
	if( !resNode )
		return null;
	var value, label, result = new Object();
	var items = resNode.childNodes;
	for( var i = 0; i < items.length; i++){
		value = items[i].attributes.getNamedItem("value");
		label = items[i].attributes.getNamedItem("label");
		if( value )
			value = value.value;
		else
			value = 0;
		if( label )
			label = label.value;
		else
			label = "";
		result["val_" + value] = label;
	}
	return result;
}
/**
 * ����TableView����
 * @param ctrlEl TableView���������Ԫ��
 * @param op_uires ��Դ������
 */
ws.TableView.make = function(standin, op_uires){
	var uires  = op_uires || null;
	var ctrlEl = null;

	if( typeof(standin) == "string" )
		ctrlEl = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		ctrlEl = standin;
	if( ctrlEl.type != "ws_tableview" )
		return null;
	
	if( !uires )
		uires = ctrlEl.getAttribute("uires");
	var resNode = ws.res.findResourceNode(uires);
	if( !resNode )
		return null;

	ctrlEl.appendRow	= ws.TableView.appendRow;
	ctrlEl.insertRow    = ws.TableView.insertRow;
	ctrlEl.deleteRow    = ws.TableView.deleteRow;
	ctrlEl.deleteAll	= ws.TableView.deleteAll;
	ctrlEl.setRowData   = ws.TableView.setRowData;
	ctrlEl.getRowData   = ws.TableView.getRowData;
	ctrlEl.setTableData = ws.TableView.setTableData;
	ctrlEl.getTableData = ws.TableView.getTableData;
	ctrlEl.clearAll		= ws.TableView.clearAll;
	ctrlEl.sort         = ws.TableView.sort;
	ctrlEl.setRowStyle  = ws.TableView.setRowStyle;
	ctrlEl.setHeadState = ws.TableView.setHeadState;
	ctrlEl.getRowIndex  = ws.TableView.getRowIndex;
	ctrlEl.setTotalRows = ws.TableView.setTotalRows;
	ctrlEl.goPage       = ws.TableView.goPage;
	ctrlEl.setSelectCode  = ws.TableView.setSelectCode;
	ctrlEl.getSelectLabel = ws.TableView.getSelectLabel;
	ctrlEl.loadSelectCode = ws.TableView.loadSelectCode;
	ctrlEl.appendEmptyRow = ws.TableView.appendEmptyRow;
	ctrlEl.setRadioBoxValue	= ws.TableView.setRadioBoxValue;
	ctrlEl.setNavigateBarState = ws.TableView.setNavigateBarState;
	
	ctrlEl.select_code = new Object();

	var columns = new Array();
	var cols = resNode.childNodes;
	var col, nameNode, labelNode, typeNode, valueNode, widthNode, linkNode, linkActionNode, styleNode, formatNode;
	var navNode;
	for( var i = 0; i < cols.length; i++){
		if( cols[i].tagName != "column" ){
			if( cols[i].tagName == "navigatebar" )
				navNode = cols[i];
			continue;
		}
		col = new Object();
		nameNode  = cols[i].attributes.getNamedItem("name");
		labelNode = cols[i].attributes.getNamedItem("label");
		typeNode  = cols[i].attributes.getNamedItem("type");
		formatNode= cols[i].attributes.getNamedItem("format");
		valueNode = cols[i].attributes.getNamedItem("value");
		widthNode = cols[i].attributes.getNamedItem("width");
		linkNode  = cols[i].attributes.getNamedItem("link");
		styleNode = cols[i].attributes.getNamedItem("style");
		linkActionNode = cols[i].attributes.getNamedItem("linkAction");
		if( nameNode )
			col.name = nameNode.value;
		else
			col.name = "";
		if( labelNode )
			col.label = labelNode.value;
		else
			col.label = "";
		if( typeNode )
			col.type = typeNode.value;
		else
			col.type = "";
		if( formatNode )
			col.format = formatNode.value;
		else
			col.format = "";
		if( valueNode )
			col.value = valueNode.value;
		else
			col.value = "";
		if( widthNode )
			col.width = ws.toInt(widthNode.value);
		else
			col.width = 0;
		if( linkNode )
			col.link = linkNode.value;
		else
			col.link = null;
		if( styleNode )
			col.cssText = styleNode.value;
		if( linkActionNode )
			col.linkAction = linkActionNode.value;
		else
			col.linkAction = "";
		if( col.type == "Select" ){
			var codeNode = cols[i].attributes.getNamedItem("select_code");
			if( codeNode )
				ctrlEl.select_code[col.name] = ctrlEl.loadSelectCode(codeNode.value);
		}
		columns.push(col);
	}
	ctrlEl.columns = columns;
	var table = ws.createElement("<table cellspacing='0' >", ctrlEl);
	table.className = "ws_tableview";
	var colgroup = ws.createElement("colgroup", table);
	for( var i = 0; i < columns.length; i++){
		col = ws.createElement("col", colgroup);
		col.style.cssText = columns[i].cssText;
	}
	//���ɱ�ͷ
	var thead = ws.createElement("thead", table);
	var tr = ws.createElement("tr", thead);
	var td, img;
	for( var i = 0; i < columns.length; i++){
		td = ws.createElement("<td nowrap class='deactive'>", tr);
		if( columns[i].width > 0 )
			td.width = columns[i].width;
		td.innerText = columns[i].label;
		td.name      = columns[i].name;
		img        = ws.createElement('img', td);
		td.sortImg = img;
		img.src    = eval(thead.currentStyle.sortimg_blank);

		ws.addEvent(td, "click", ws.TableView.onsort);
	}
	//����tbody
	ws.createElement("tbody", table);
	ctrlEl.table = table;
	
	//���ɵ�����
	var nav = null, pageNode, pagenumNode, actionNode;
	if( navNode ){
		nav = new Object();
		pageNode    = navNode.attributes.getNamedItem("rowPerPage");
		pagenumNode = navNode.attributes.getNamedItem("pageNum");
		actionNode  = navNode.attributes.getNamedItem("action");
		if( pageNode )
			nav.rowPerPage = ws.toInt(pageNode.value);	//ÿҳ��ʾ������
		else
			nav.rowPerPage = 10;
		if( pagenumNode )
			nav.pageNum  = ws.toInt(pagenumNode.value);		//ͬʱ��ʾ��ҳ�浼����ť
		else 
			nav.pageNum	 = 5;
		if( actionNode )
			nav.pageAction = actionNode.value;				//�������¼��Ļص�����,�ص������ĵ�һ�������Ǽ�¼��,
		else												//�ڶ��������ǻ�ȡ�ļ�¼����,���ؼ�¼����.
			nav.pageAction = null;

		var tfoot = ws.createElement("tfoot", table);
		tr = ws.createElement("tr", tfoot);
		td = ws.createElement("<td colspan=" + columns.length + ">", tr);
		var barTable = ws.createElement('<table style="margin-left: 10px;" cellspacing="0px">', td);
		var tbody = ws.createElement("tbody", barTable);
		tr = ws.createElement("tr", tbody);
		td = ws.createElement('<td class="navbutton" nowrap style="text-align:center;">', tr);
		td.innerText = "��ҳ";
		td.onclick = ws.TableView.doFirstPage;
		nav.btnFirst = td;

		td = ws.createElement('<td class="navbutton" nowrap style="text-align:center;">', tr);
		td.innerText = "��һҳ";
		td.onclick = ws.TableView.doPrevPage;
		nav.btnPrev  = td;

		td = ws.createElement('<td class="navbutton" nowrap style="text-align:center;">', tr);
		td.innerText = "<<";
		td.onclick = ws.TableView.doQPrevPage;
		nav.btnQPrev = td;
		
		var pageButtons = new Array();
		for( var i = 0; i < nav.pageNum; i++ ){
			td = ws.createElement('<td class="navbutton" name="pagenum" nowrap buttontype="radio" style="text-align:center;">', tr);
			td.innerText = i + 1;
			td.pageNo = i;
			td.onclick = ws.TableView.doPage;
			pageButtons.push(td);
		}
		nav.pageButtons = pageButtons;		//ҳ�浼����ť

		td = ws.createElement('<td class="navbutton" nowrap style="text-align:center;">', tr);
		td.innerText = ">>";
		td.onclick = ws.TableView.doQNextPage;
		nav.btnQNext = td;

		td = ws.createElement('<td class="navbutton" nowrap style="text-align:center;">', tr);
		td.innerText = "��һҳ";
		td.onclick = ws.TableView.doNextPage;
		nav.btnNext  = td;

		td = ws.createElement('<td class="navbutton" nowrap style="text-align:center;">', tr);
		td.innerText = "βҳ";
		td.onclick = ws.TableView.doLastPage;
		nav.btnLast  = td;

		ctrlEl.navigatebar = nav;
		ctrlEl.appendEmptyRow(ctrlEl.navigatebar.rowPerPage);
	}
	else {
		ctrlEl.navigatebar = null;
		var initRowNode = resNode.attributes.getNamedItem("initrow");
		if( initRowNode == null || initRowNode == undefined )
			ctrlEl.initRow = 5;
		else
			ctrlEl.initRow = ws.toInt(initRowNode.value);
		ctrlEl.appendEmptyRow(ctrlEl.initRow);
	}

	ctrlEl.totalRows = 0;		//��¼����
	ctrlEl.curPage   = 0;		//��ǰ��ʾ��ҳ��
	ctrlEl.lastPage  = 0;		//���һ��ҳ��

	return ctrlEl;
}
