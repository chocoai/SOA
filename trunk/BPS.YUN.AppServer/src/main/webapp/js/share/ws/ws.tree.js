
ws.tree = {};

/**
 * 创建树型控件
 * @param standin 树型控件所在的元素
 * @param rootValue 根节点的节点数据
 * @return 创建的控件元素
 */
ws.tree.make = function(standin, rootValue){
	var tree = null, root = null, icon = null, label = null, container = null;
	var rootText, isOpen, rootIcon;

	if( typeof(standin) == "string" )
		tree = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		tree = standin;

	tree.config = ws.config.tree;
	
	rootText = tree.getAttribute("root_text");
	if( rootText == null || rootText == undefined || rootText == "" )
		rootText = "root";
	rootIcon = tree.getAttribute("root_icon");
	if( rootIcon == null || rootIcon == undefined || rootIcon == "" )
		rootIcon = tree.config.rootIcon;
	isOpen = tree.getAttribute("open");
	if( isOpen == "true" )
		isOpen = true;
	else
		isOpen = false;

	root = ws.createElement('<div class="ws_tree_item" ondblclick="ws.tree.toggle(this);" onkeydown="return ws.tree.keydown(this, event)">', tree);
	
	icon = ws.createElement('<img class="ws_tree_icon" src="" onclick="ws.tree.select(this.parentElement);">', root);
	icon.src = ws.theme.imageURL(rootIcon);
	root.iconEl   = icon;

	label = ws.createElement('<a href="' + tree.config.defaultAction + '" onclick="return false;" onfocus="ws.tree.focus(this.parentElement)" onblur="ws.tree.blur(this.parentElement)">', root);
	label.innerHTML = rootText;
	root.labelEl    = label;

	container = ws.createElement('<div class="ws_tree_container" style="display:' + (isOpen ? 'block' : 'none') + '">', tree);
	root.container = container;
	root.isOpen    = isOpen;
	tree.root      = root;
	
	root._parentNode = null;
	root._childNode  = [];
	root._nodeType   = "root";
	root._nodeName   = rootText;
	root._nodeValue  = rootValue;
	root.tree		 = tree;
	tree.nodeTypeReg = new Object();
	
	tree.nodeTypeReg["root"]   = {hasChild:true, closeIcon:rootIcon, openIcon:rootIcon};
	tree.nodeTypeReg["folder"] = {hasChild:true, closeIcon:tree.config.closeFolderIcon, openIcon:tree.config.openFolderIcon};
	tree.nodeTypeReg["file"]   = {hasChild:false, closeIcon:tree.config.fileIcon, openIcon:tree.config.fileIcon};
	
	ws.tree.becomeNode(root);
	root.isLast = true;

	tree.regNodeType = ws.tree.regNodeType;
	tree.findNode	 = ws.tree.findNode;
	tree.getSelected = ws.tree.getSelected;
	tree.selected    = null;
	return tree;
}

/**
 * 获取当前选择中的节点
 */
ws.tree.getSelected = function(){
	return this.selected;
}
/**
 * 根据节点路径查找节点对象
 * @return 匹配的节点对象或null
 */
ws.tree.findNode = function(path){
	var split = path.split("/");
	var index;
	if( split[0] == "" )
		index = 1;
	else 
		index = 0;
		
	for( var i = 0; i < this.root._childNode.length; i++ ){
		if( split[index] == this.root._childNode[i]._nodeName ){
			if( split.length > index + 1 )
				return this.root._childNode[i].findChild(split, index + 1);
			else
				return this.root._childNode[i];
		}
	}
	return null;
}

/**
 * 扩展元素对象的功能，使其成为一个树节点
 * @param node 元素对象
 */
ws.tree.becomeNode = function(node){
	node.appendNode = ws.tree.appendNode;
	node.insertNode = ws.tree.insertNode;
	node.expand		= ws.tree.expand;
	node.collapse	= ws.tree.collapse;
	node.remove		= ws.tree.remove;
	node.getPath	= ws.tree.getPath;
	node.findChild	= ws.tree.findChild;
	node.select		= ws.tree.nodeSelect;
	node.adjustIndent = ws.tree.adjustIndent;
	node.getType	= function(){ return this._nodeType; };
	node.getName	= function(){ return this._nodeName; };
	node.getValue	= function(){ return this._nodeValue; };
	node.modify		= ws.tree.modify;
	node.removeAllChildren = ws.tree.removeAllChildren;
}

/**
 * 注册树的节点类型
 * @param typeName 节点类型名称
 * @param hasChild 此类型的节点是否允许追加子节点
 * @param closeIconUrl 节点处于关闭状态的图标文件相对路径
 * @param openIconUrl 节点处于打开状态的图标文件相对路径
 */
ws.tree.regNodeType = function(typeName, hasChild, closeIconUrl, openIconUrl){
	this.nodeTypeReg[typeName] = {hasChild:hasChild, closeIcon:closeIconUrl, openIcon:openIconUrl};
}

/**
 * 打开或关系节点
 * @param node 节点对象
 */
ws.tree.toggle = function(node){
	if( node.isOpen == true )
		node.collapse();
	else
		node.expand();
}
ws.tree.keydown = function(node, event){
}
/**
 * 选中节点
 * @param node 节点对象
 */
ws.tree.select = function(node){
	ws.tree.focus(node);
}
/**
 * 当节点处于焦点时的事件
 * @param node 节点对象
 */
ws.tree.focus = function(node){
	if ((node.tree.selected) && (node.tree.selected != node)){
		node.tree.selected.labelEl.className = '';
	}
	if (node.tree.selected != node && typeof( node.tree.onSelected ) == "function" )
		node.tree.onSelected(node);
	node.tree.selected = node;
	node.labelEl.className = "selected";
	node.labelEl.focus();
}
/**
 * 当节点失去焦点时的事件
 * @param node 节点对象
 */
ws.tree.blur = function(node){
	node.labelEl.className = 'selected-inactive';
}

/**
 * 打开节点
 */
ws.tree.expand = function(){
	var nodeType = this.tree.nodeTypeReg[this._nodeType];
	if( !nodeType.hasChild )
		return;

	this.container.style.display = "block";
	this.isOpen = true;
	
	this.iconEl.src = ws.theme.imageURL(nodeType.openIcon);

	if( this.isLast ){
		if(this.toggleEl ) 
			this.toggleEl.src = ws.theme.imageURL(this.tree.config.lMinusIcon);
	}
	else {
		if(this.toggleEl ) 
			this.toggleEl.src = ws.theme.imageURL(this.tree.config.tMinusIcon);
	}
	if( this.isFirstExpand != false ){
		this.isFirstExpand = false;
		if( typeof(this.tree.onLoadChild) == "function" )
			this.tree.onLoadChild(this);
	}
	if( typeof( this.tree.onResize ) == "function" )
		this.tree.onResize();
}
/**
 * 关闭节点
 */
ws.tree.collapse = function(){
	var nodeType = this.tree.nodeTypeReg[this._nodeType];
	if( !nodeType.hasChild )
		return;

	this.container.style.display = "none";
	this.isOpen = false;

	var nodeType = this.tree.nodeTypeReg[this._nodeType];
	this.iconEl.src = ws.theme.imageURL(nodeType.closeIcon);

	if( this.isLast ){
		if(this.toggleEl ) 
			this.toggleEl.src = ws.theme.imageURL(this.tree.config.lPlusIcon);
	}
	else {
		if(this.toggleEl ) 
			this.toggleEl.src = ws.theme.imageURL(this.tree.config.tPlusIcon);
	}
	if( typeof( this.tree.onResize ) == "function" )
		this.tree.onResize();
}

/**
 * 调整节点的缩进，一般在追加节点时需调用上一个兄弟节点的此方法
 * @param level 节点所在的层次
 * @param flag 添加“|”或删除
 */
ws.tree.adjustIndent = function(level, flag){
	var nodeType = this.tree.nodeTypeReg[this._nodeType];

	child = this.children[level];
	if( child && child.getAttribute("type") == "indent" ){
		if( flag )
			child.src = ws.theme.imageURL(this.tree.config.iIcon);
		else
			child.src = ws.theme.imageURL(this.tree.config.blankIcon);
	}
	if( nodeType.hasChild ){
		for( var i = 0; i < this._childNode.length; i++ )
			this._childNode[i].adjustIndent(level, flag);
	}
}

/**
 * 追加子节点
 * @param type 追加节点的类型
 * @param name 追加节点的显示名称
 * @param value 追加节点的符加值
 * @param op_tip 追加节点的帮助说明(可选)
 * @return 新追加的节点
 */
ws.tree.appendNode = function(type, name, value, op_tip){
	return this.insertNode(-1, type, name, value, op_tip);
}
/**
 * 插入新的子节点
 * @param index 插入节点的位置
 * @param type 追加节点的类型
 * @param name 追加节点的显示名称
 * @param value 追加节点的符加值
 * @param op_tip 追加节点的帮助说明(可选)
 * @return 新插入的节点
 */
ws.tree.insertNode = function(index, type, name, value, op_tip){
	var tip = op_tip || "";
	var _nodeType = this.tree.nodeTypeReg[type];
	if( typeof( _nodeType ) != "object" ){
		//unknow type
		return;
	}
	//检查节点名称是否有重复
	for( var i = 0; i < this._childNode.length; i++ ){
		if( name == this._childNode[i]._nodeName )
			return null;
	}
	var foo = this, level = 0;
	var indent = "";
	while (foo._parentNode) {
		indent = '<img src="' + ws.theme.imageURL(((foo.isLast) ? this.tree.config.blankIcon : this.tree.config.iIcon)) + '" type="indent">' + indent;
		foo = foo._parentNode;
		level ++;
	}
	var node = ws.createElement('<div class="ws_tree_item" ondblclick="ws.tree.toggle(this);" onkeydown="return ws.tree.keydown(this, event)" >');
	node.innerHTML = indent;

	node.toggleEl = ws.createElement('<img src="" onclick="ws.tree.toggle(this.parentElement);">', node);
	node.iconEl   = ws.createElement('<img class="ws_tree_icon" src="' + ws.theme.imageURL(_nodeType.closeIcon) + '" onclick="ws.tree.select(this.parentElement);">', node);
	node.labelEl  = ws.createElement('<a href="' + this.tree.config.defaultAction + '" tooltip="' + tip + '" onclick="return false;" onfocus="ws.tree.focus(this.parentElement);" onblur="ws.tree.blur(this.parentElement);">', node);
	node.labelEl.innerHTML = name;

	if( _nodeType.hasChild ){
		node.container = ws.createElement('<div class="ws_tree_container" style="display:none" >');
		node._childNode = [];
	}
	node.isOpen = false;
	node.tree   = this.tree;
	node.level	= level;
	node._parentNode = this;
	node._nodeType   = type;
	node._nodeName   = name;
	node._nodeValue  = value;

	ws.tree.becomeNode(node);

	if( index == -1 || index >= this._childNode.length ){ //append
		var imgUrl;
		if( _nodeType.hasChild )
			imgUrl = this.tree.config.lPlusIcon;
		else 
			imgUrl = this.tree.config.lIcon;
		node.toggleEl.src = ws.theme.imageURL(imgUrl);

		this.container.appendChild(node);
		if( node.container )
			this.container.appendChild(node.container);
		if( this._childNode.length > 0 ){
			var preNode  = this._childNode[this._childNode.length - 1];
			var nodeType = this.tree.nodeTypeReg[preNode._nodeType];
			preNode.isLast = false;
			if( nodeType.hasChild )
				preNode.toggleEl.src = ws.theme.imageURL(preNode.isOpen ? this.tree.config.tMinusIcon : this.tree.config.tPlusIcon);
			else
				preNode.toggleEl.src = ws.theme.imageURL(this.tree.config.tIcon);
			preNode.adjustIndent(level, true);
		}
		this._childNode.push(node);
		node.isLast = true;
	}
	else {
		var imgUrl;
		if( _nodeType.hasChild )
			imgUrl = this.tree.config.tPlusIcon;
		else 
			imgUrl = this.tree.config.tIcon;
		node.toggleEl.src = ws.theme.imageURL(imgUrl);

		this.container.insertBefore(node, this._childNode[index]);
		if( node.container )
			this.container.insertBefore(node.container, this._childNode[index]);
		this._childNode.splice(index, 0, node);
		node.isLast = false;
	}
	return node;
}
/**
 * 删除所有的子节点
 */
ws.tree.removeAllChildren = function(){
	var count = this._childNode.length;
	for( var i = 0; i < count; i++ ){
		this._childNode[0].remove();
	}
	this._childNode = [];
}
/**
 * 修改节点对象
 */
ws.tree.modify = function(name, tip, value){
	this.labelEl.innerHTML = name;
	this.labelEl.tooltip   = tip;
	this._nodeName   = name;
	this._nodeValue  = value;
}
/**
 * 删除节点对象
 */
ws.tree.remove = function(){
	if( this._nodeType == "root" )
		return;

	var index = null;
	if( this.isLast && this._parentNode._childNode.length > 1){
		var preNode  = this._parentNode._childNode[this._parentNode._childNode.length - 2];
		var nodeType = this.tree.nodeTypeReg[preNode._nodeType];
		preNode.isLast = true;
		if( nodeType.hasChild )
			preNode.toggleEl.src = ws.theme.imageURL(preNode.isOpen ? this.tree.config.lMinusIcon : this.tree.config.lPlusIcon);
		else
			preNode.toggleEl.src = ws.theme.imageURL(this.tree.config.lIcon);
		preNode.adjustIndent(this.level, false);
		index = this._parentNode._childNode.length - 1;
	}
	else {
		for( var i = 0; i < this._parentNode._childNode.length; i++ ){
			if( this._parentNode._childNode[i] == this ){
				index = i;
				break;
			}
		}
	}
	if( index == null )
		return;
	if( this.tree.selected == this ){
		this.tree.selected = null;
		if (typeof( this.tree.onSelected ) == "function" )
			this.tree.onSelected(null);
	}

	this.removeNode(true);
	if( this.container ){
		this.container.removeNode(true);
	}
	this._parentNode._childNode.splice(index, 1);
}
/**
 * 获取节点对象的全路径
 * @return 节点路径
 */
ws.tree.getPath = function(){
	if( this._nodeType == "root" )
		return "/";
	var path = this._nodeName;
	var foo = this._parentNode;
	while( foo ){
		if( foo._nodeType != "root" )
			path = foo._nodeName + "/" + path;
		foo = foo._parentNode;
	}
	path = "/" + path;
	return path;
}
/**
 * 查找子节点对象
 * @param nameArray 各节点的名称数组
 * @param index 子节点名称在nameArray中的索引值
 * @return 子节点对象或null
 */
ws.tree.findChild = function(nameArray, index){
	for( var i = 0; i < this._childNode.length; i++ ){
		if( nameArray[index] == this._childNode[i]._nodeName ){
			if( index < nameArray.length - 1 )
				return this._childNode[i].findChild(nameArray, index + 1);
			else
				return this._childNode[i];
		}
	}
	return null;
}
/**
 * 选中节点
 */
ws.tree.nodeSelect = function(){
	var foo = this._parentNode;
	while( foo ){
		if( foo.isOpen != true )
			foo.expand();
		foo = foo._parentNode;
	}
	
	ws.tree.select(this);
}