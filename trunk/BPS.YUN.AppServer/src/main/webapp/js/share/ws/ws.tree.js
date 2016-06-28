
ws.tree = {};

/**
 * �������Ϳؼ�
 * @param standin ���Ϳؼ����ڵ�Ԫ��
 * @param rootValue ���ڵ�Ľڵ�����
 * @return �����Ŀؼ�Ԫ��
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
 * ��ȡ��ǰѡ���еĽڵ�
 */
ws.tree.getSelected = function(){
	return this.selected;
}
/**
 * ���ݽڵ�·�����ҽڵ����
 * @return ƥ��Ľڵ�����null
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
 * ��չԪ�ض���Ĺ��ܣ�ʹ���Ϊһ�����ڵ�
 * @param node Ԫ�ض���
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
 * ע�����Ľڵ�����
 * @param typeName �ڵ���������
 * @param hasChild �����͵Ľڵ��Ƿ�����׷���ӽڵ�
 * @param closeIconUrl �ڵ㴦�ڹر�״̬��ͼ���ļ����·��
 * @param openIconUrl �ڵ㴦�ڴ�״̬��ͼ���ļ����·��
 */
ws.tree.regNodeType = function(typeName, hasChild, closeIconUrl, openIconUrl){
	this.nodeTypeReg[typeName] = {hasChild:hasChild, closeIcon:closeIconUrl, openIcon:openIconUrl};
}

/**
 * �򿪻��ϵ�ڵ�
 * @param node �ڵ����
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
 * ѡ�нڵ�
 * @param node �ڵ����
 */
ws.tree.select = function(node){
	ws.tree.focus(node);
}
/**
 * ���ڵ㴦�ڽ���ʱ���¼�
 * @param node �ڵ����
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
 * ���ڵ�ʧȥ����ʱ���¼�
 * @param node �ڵ����
 */
ws.tree.blur = function(node){
	node.labelEl.className = 'selected-inactive';
}

/**
 * �򿪽ڵ�
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
 * �رսڵ�
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
 * �����ڵ��������һ����׷�ӽڵ�ʱ�������һ���ֵܽڵ�Ĵ˷���
 * @param level �ڵ����ڵĲ��
 * @param flag ��ӡ�|����ɾ��
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
 * ׷���ӽڵ�
 * @param type ׷�ӽڵ������
 * @param name ׷�ӽڵ����ʾ����
 * @param value ׷�ӽڵ�ķ���ֵ
 * @param op_tip ׷�ӽڵ�İ���˵��(��ѡ)
 * @return ��׷�ӵĽڵ�
 */
ws.tree.appendNode = function(type, name, value, op_tip){
	return this.insertNode(-1, type, name, value, op_tip);
}
/**
 * �����µ��ӽڵ�
 * @param index ����ڵ��λ��
 * @param type ׷�ӽڵ������
 * @param name ׷�ӽڵ����ʾ����
 * @param value ׷�ӽڵ�ķ���ֵ
 * @param op_tip ׷�ӽڵ�İ���˵��(��ѡ)
 * @return �²���Ľڵ�
 */
ws.tree.insertNode = function(index, type, name, value, op_tip){
	var tip = op_tip || "";
	var _nodeType = this.tree.nodeTypeReg[type];
	if( typeof( _nodeType ) != "object" ){
		//unknow type
		return;
	}
	//���ڵ������Ƿ����ظ�
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
 * ɾ�����е��ӽڵ�
 */
ws.tree.removeAllChildren = function(){
	var count = this._childNode.length;
	for( var i = 0; i < count; i++ ){
		this._childNode[0].remove();
	}
	this._childNode = [];
}
/**
 * �޸Ľڵ����
 */
ws.tree.modify = function(name, tip, value){
	this.labelEl.innerHTML = name;
	this.labelEl.tooltip   = tip;
	this._nodeName   = name;
	this._nodeValue  = value;
}
/**
 * ɾ���ڵ����
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
 * ��ȡ�ڵ�����ȫ·��
 * @return �ڵ�·��
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
 * �����ӽڵ����
 * @param nameArray ���ڵ����������
 * @param index �ӽڵ�������nameArray�е�����ֵ
 * @return �ӽڵ�����null
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
 * ѡ�нڵ�
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