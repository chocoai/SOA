
ws.selectable = {};

ws.selectable.make = function(standin, bMultiple, type){
	var oElement = null;
	if( typeof(standin) == "string" )
		oElement = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		oElement = standin;

	if (oElement == null)
		return;

	oElement.isMultiple = Boolean(bMultiple);

	oElement.selectedItems = [];
	oElement.isFireChange  = true;

	var oThis = oElement;
	oElement.onmousedown = function (e) {
		if (e == null) e = oElement.ownerDocument.parentWindow.event;
		oThis.click(e);
	};
	if( oElement.onselectstart == undefined )
		oElement.onselectstart = function(){return false};
	
	oElement.setItemSelected	= ws.selectable.setItemSelected;
	oElement.setItemSelectedUi	= ws.selectable.setItemSelectedUi;
	oElement.getItemSelected	= ws.selectable.getItemSelected;
	oElement.fireChange			= ws.selectable.fireChange;
	oElement.click				= ws.selectable.click;
	oElement.getSelectedItems	= ws.selectable.getSelectedItems;
	oElement.destroy			= ws.selectable.destroy;
	oElement.isBefore			= ws.selectable.isBefore;
	oElement.getSelectedIndexes	= ws.selectable.getSelectedIndexes;
	oElement.clear				= ws.selectable.clear;

	oElement.isBatchSelect = true;
	if( type == "tablerow" ){
		oElement.isItem			= ws.selectable.tablerow.isItem;
		oElement.getItems		= ws.selectable.tablerow.getItems;
		oElement.getItem		= ws.selectable.tablerow.getItem;
		oElement.getItemIndex	= ws.selectable.tablerow.getItemIndex;
		oElement.getNext		= ws.selectable.getNext;
		oElement.getPrevious	= ws.selectable.getPrevious;
	}
	else if( type == "tablecell" ){
		oElement.isBatchSelect  = false;
		oElement.isItem			= ws.selectable.tablecell.isItem;
		oElement.isBefore		= ws.selectable.tablecell.isBefore;
		oElement.getItems		= ws.selectable.tablecell.getItems;
		oElement.getItem		= ws.selectable.tablecell.getItem;
		oElement.getItemIndex	= ws.selectable.tablecell.getItemIndex;
		oElement.getNext		= ws.selectable.tablecell.getNext;
		oElement.getPrevious	= ws.selectable.tablecell.getPrevious;
		oElement.selectRow		= ws.selectable.tablecell.selectRow;
		oElement.selectCol		= ws.selectable.tablecell.selectCol;
		oElement.selectAll		= ws.selectable.tablecell.selectAll;
	}
	else {
		oElement.isItem			= ws.selectable.isItem;
		oElement.getItems		= ws.selectable.getItems;
		oElement.getItem		= ws.selectable.getItem;
		oElement.getItemIndex	= ws.selectable.getItemIndex;
		oElement.getNext		= ws.selectable.getNext;
		oElement.getPrevious	= ws.selectable.getPrevious;
	}
}

ws.selectable.clear = function(){
	var items = this.getSelectedItems();
	for( var i = 0; i < items.length; i++ )
		this.setItemSelected(items[i], false);
}

ws.selectable.setItemSelected = function (oEl, bSelected) {
	if( bSelected && typeof( this.beforeSelection ) == "function" ){
		var result = this.beforeSelection(oEl);
		if( result == false ){
			this.selectedItems = [];
			return;
		}
	}
	if (!this.isMultiple) {
		if (bSelected) {
			var old = this.selectedItems[0]
			if (oEl == old)
				return;
			if (old != null)
				this.setItemSelectedUi(old, false);
			this.setItemSelectedUi(oEl, true);
			this.selectedItems = [oEl];
			this.fireChange();
		}
		else {
			if (this.selectedItems[0] == oEl) {
				this.setItemSelectedUi(oEl, false);
				this.selectedItems = [];
			}
		}
	}
	else {
		if (Boolean(oEl.isSelected) == Boolean(bSelected))
			return;

		this.setItemSelectedUi(oEl, bSelected);

		if (bSelected)
			this.selectedItems[this.selectedItems.length] = oEl;
		else {
			// remove
			var tmp = [];
			var j = 0;
			for (var i = 0; i < this.selectedItems.length; i++) {
				if (this.selectedItems[i] != oEl)
					tmp[j++] = this.selectedItems[i];
			}
			this.selectedItems = tmp;
		}
		this.fireChange();
	}
};

// This method updates the UI of the item
ws.selectable.setItemSelectedUi = function (oEl, bSelected) {
	if (bSelected)
		ws.addClass(oEl, "selected");
	else
		ws.removeClass(oEl, "selected");

	oEl.isSelected = bSelected;
};

ws.selectable.getItemSelected = function (oEl) {
	return Boolean(oEl.isSelected);
};

ws.selectable.fireChange = function () {
	if (!this.isFireChange)
		return;
	if (typeof this.onSelectionChanged == "string")
		this.onSelectionChanged = new Function(this.onSelectionChanged);
	if (typeof this.onSelectionChanged == "function")
		this.onSelectionChanged();
};

ws.selectable.click = function (e) {
	var oldFireChange = this.isFireChange;
	this.isFireChange = false;

	// create a copy to compare with after changes
	var selectedBefore = this.getSelectedItems();	// is a cloned array

	// find row
	var el = e.target != null ? e.target : e.srcElement;
	while (el != null && !this.isItem(el))
		el = el.parentNode;

	if (el == null) {	// happens in IE when down and up occur on different items
		this.isFireChange = oldFireChange;
		return;
	}

	var rIndex = el;
	var aIndex = this.anchorIndex;

	// test whether the current row should be the anchor
	if (this.selectedItems.length == 0 || (e.ctrlKey && !e.shiftKey && this.isMultiple)) {
		aIndex = this.anchorIndex = rIndex;
	}

	if (!e.ctrlKey && !e.shiftKey || !this.isMultiple) {
		// deselect all
		var items = this.selectedItems;
		for (var i = items.length - 1; i >= 0; i--) {
			if (items[i].isSelected && items[i] != el)
				this.setItemSelectedUi(items[i], false);
		}
		this.anchorIndex = rIndex;
		//this.setItemSelectedUi(el, true);
		//this.selectedItems = [el];
		this.setItemSelected(el, true);
	}
	// ctrl
	else if (this.isMultiple && e.ctrlKey && !e.shiftKey) {
		this.setItemSelected(el, !el.isSelected);
		this.anchorIndex = rIndex;
	}
	// ctrl + shift
	else if (this.isBatchSelect && this.isMultiple && e.ctrlKey && e.shiftKey) {
		// up or down?
		var dirUp = this.isBefore(rIndex, aIndex);

		var item = aIndex;
		while (item != null && item != rIndex) {
			if (!item.isSelected && item != el)
				this.setItemSelected(item, true);
			item = dirUp ? this.getPrevious(item) : this.getNext(item);
		}

		if (!el.isSelected)
			this.setItemSelected(el, true);
	}
	// shift
	else if (this.isBatchSelect && this.isMultiple && !e.ctrlKey && e.shiftKey) {
		// up or down?
		var dirUp = this.isBefore(rIndex, aIndex);

		// deselect all
		var items = this.selectedItems;
		for (var i = items.length - 1; i >= 0; i--)
			this.setItemSelectedUi(items[i], false);
		this.selectedItems = [];

		// select items in range
		var item = aIndex;
		while (item != null) {
			this.setItemSelected(item, true);
			if (item == rIndex)
				break;
			item = dirUp ? this.getPrevious(item) : this.getNext(item);
		}
	}

	// find change!!!
	var found;
	var changed = selectedBefore.length != this.selectedItems.length;
	if (!changed) {
		for (var i = 0; i < selectedBefore.length; i++) {
			found = false;
			for (var j = 0; j < this.selectedItems.length; j++) {
				if (selectedBefore[i] == this.selectedItems[j]) {
					found = true;
					break;
				}
			}
			if (!found) {
				changed = true;
				break;
			}
		}
	}

	this.isFireChange = oldFireChange;
	if (changed && this.isFireChange)
		this.fireChange();
};

ws.selectable.getSelectedItems = function () {
	//clone
	var items = this.selectedItems;
	var l = items.length;
	var tmp = new Array(l);
	for (var i = 0; i < l; i++)
		tmp[i] = items[i];
	return tmp;
};

ws.selectable.isItem = function (node) {
	return node != null && node.nodeType == 1 && node.parentNode == this;
};

ws.selectable.destroy = function () {
	this.onmousedown   = null;
	this.selectedItems = null;
};

/* Traversable Collection Interface */

ws.selectable.getNext = function (el) {
	var n = el.nextSibling;
	if (n == null || this.isItem(n))
		return n;
	return this.getNext(n);
};

ws.selectable.getPrevious = function (el) {
	var p = el.previousSibling;
	if (p == null || this.isItem(p))
		return p;
	return this.getPrevious(p);
};

ws.selectable.isBefore = function (n1, n2) {
	var next = this.getNext(n1);
	while (next != null) {
		if (next == n2)
			return true;
		next = this.getNext(next);
	}
	return false;
};

/* End Traversable Collection Interface */

/* Indexable Collection Interface */

ws.selectable.getItems = function () {
	var tmp = [];
	var j = 0;
	var cs = this.childNodes;
	var l = cs.length;
	for (var i = 0; i < l; i++) {
		if (cs[i].nodeType == 1)
			tmp[j++] = cs[i]
	}
	return tmp;
};

ws.selectable.getItem = function (nIndex) {
	var j = 0;
	var cs = this.childNodes;
	var l = cs.length;
	for (var i = 0; i < l; i++) {
		if (cs[i].nodeType == 1) {
			if (j == nIndex)
				return cs[i];
			j++;
		}
	}
	return null;
};

ws.selectable.getSelectedIndexes = function () {
	var items = this.getSelectedItems();
	var l = items.length;
	var tmp = new Array(l);
	for (var i = 0; i < l; i++)
		tmp[i] = this.getItemIndex(items[i]);
	return tmp;
};


ws.selectable.getItemIndex = function (el) {
	var j = 0;
	var cs = this.childNodes;
	var l = cs.length;
	for (var i = 0; i < l; i++) {
		if (cs[i] == el)
			return j;
		if (cs[i].nodeType == 1)
			j++;
	}
	return -1;
};

//=========begin tablerow=======================================================
ws.selectable.tablerow = function(){};

ws.selectable.tablerow.isItem = function (node) {
	return node != null && node.tagName == "TR" &&
		node.parentNode.tagName == "TBODY" &&
		node.parentNode.parentNode == this;
};

ws.selectable.tablerow.getItems = function () {
	return this.rows;
};

ws.selectable.tablerow.getItemIndex = function (el) {
	return el.rowIndex;
};

ws.selectable.tablerow.getItem = function (i) {
	return this.rows[i];
};
//=========end tablerow=========================================================

//=========begin tablecell======================================================
ws.selectable.tablecell = function(){};

ws.selectable.tablecell.isItem = function (node) {
	return node != null && node.tagName == "TD" &&
			node.parentNode.parentNode.tagName == "TBODY" &&
			node.parentNode.parentNode.parentNode == this;
};
ws.selectable.tablecell.isBefore = function (n1, n2) {
	if( n2.parentElement.rowIndex > n1.parentElement.rowIndex )
		return true;
	else if( n2.parentElement.rowIndex < n1.parentElement.rowIndex )
		return false;
	else if( n2.cellIndex > n1.cellIndex )
		return true;
	return false;
};
ws.selectable.tablecell.getNext = function (el) {
	var i = this.getItemIndex(el);
	try {
		return this.getItem(i + 1);
	}
	catch (ex) {
		return null;
	}
};

ws.selectable.tablecell.getPrevious = function (el) {
	var i = this.getItemIndex(el);
	try {
		return this.getItem(i - 1);
	}
	catch (ex) {
		return null;
	}
};


ws.selectable.tablecell.getItems = function () {
	var rows = this.rows;
	var rl = rows.length;
	var tmp = [];
	var j = 0;
	var cells, cl;
	for (var y = 0; y < rl; y++) {
		cells = rows[y].cells;
		cl = cells.length;
		for (var x = 0; x < cl; x++) {
			tmp[j++] = cells[x];
		}
	}
	return tmp;
};

ws.selectable.tablecell.getItem = function (index) {
	for( var i = 0; i < this.rows.length; i++ ){
		if( this.rows[i].cells.length > index )
			return this.rows[i].cells[index];
		else index -= this.rows[i].cells.length;
	}
	return null;
};

ws.selectable.tablecell.getItemIndex = function (el) {
	var index = 0;
	for( var i = 0; i < el.parentElement.rowIndex; i++ ){
		index += el.parentElement.cells.length;
	}
	index += el.cellIndex;
	return index;
};

ws.selectable.tablecell.selectRow = function (nrow) {
	var row = this.rows[nrow];
	var isFireChange = this.isFireChange;

	this.isFireChange = false;
	this.clear();

	if( row != undefined && row != null ){
		for( var i = 0; i < row.cells.length; i++ )
			this.setItemSelected(row.cells[i], true);
	}
	this.isFireChange = isFireChange;
	this.fireChange();
}
ws.selectable.tablecell.selectCol = function (ncol) {
	var col = null, gridObj;
	var isFireChange = this.isFireChange;

	this.isFireChange = false;
	this.clear();

	if( this.grid != undefined && this.grid != null ){
		for( var i = 0; i < this.rows.length; i++ ){
			gridObj = this.grid[i][ncol];
			if( gridObj.realcell == true ){
				col = this.rows[gridObj.row].cells[gridObj.col];
				if( col != undefined && col != null )
					this.setItemSelected(col, true);
			}
		}
	}
	else {
		for( var i = 0; i < this.rows.length; i++ ){
			col = this.rows[i].cells[ncol];
			if( col != undefined && col != null )
				this.setItemSelected(col, true);
		}
	}
	this.isFireChange = isFireChange;
	this.fireChange();
}
ws.selectable.tablecell.selectAll = function (){
	var isFireChange = this.isFireChange;

	this.isFireChange = false;
	this.clear();

	for( var i = 0; i < this.rows.length; i++ ){
		for( var j = 0; j < this.rows[i].cells.length; j++ )
			this.setItemSelected(this.rows[i].cells[j], true);
	}
	this.isFireChange = isFireChange;
	this.fireChange();
}

//=========end tablecell========================================================