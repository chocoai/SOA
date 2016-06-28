ws.listbox = {};
ws.listbox.make = function(standin, multiselect){
	var oList = null;
	if( typeof(standin) == "string" )
		oList = $(standin);
	else if ( typeof(standin) == "object" )
		oList = standin;
	if( oList == null || oList == undefined )
		return null;
	oList.insertItem = ws.listbox.insertItem;
	oList.deleteItem = ws.listbox.deleteItem;
	oList.deleteAll  = ws.listbox.deleteAll;
	oList.recalcIndex= ws.listbox.recalcIndex;

	ws.selectable.make(oList, multiselect);

	return oList;
}
ws.listbox.insertItem = function(index, text, icon, value){
	var item = ws.createElement('<div class="listitem">');
	var src  = ""
	if( icon != null && icon != undefined )
		src += ws.theme.imageHTML(icon);
	src += text;
	item.innerHTML = src;
	item.value = value;
	
	if( index < 0 || index > this.children.length - 1 )
		this.insertBefore(item);
	else
		this.insertBefore(item, this.children[index]);
	this.recalcIndex();
}
ws.listbox.deleteItem = function(index){
	if( index >= 0 && index <= this.children.length - 1 )
		this.removeChild(this.children[index]);
	this.recalcIndex();
}
ws.listbox.recalcIndex = function(){
	for( var i = 0; i < this.children.length; i++ )
		this.children[i].index = i;
}
ws.listbox.deleteAll = function(){
	var count = this.children.length;
	for(var i = 0; i < count; i++ )
		this.removeChild(this.children[0]);
}
