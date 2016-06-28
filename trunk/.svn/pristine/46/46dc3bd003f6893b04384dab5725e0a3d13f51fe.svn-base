
ws.res = {};

ws.res.regNode = /(\w+(?=(\.|$)))|([\w]\w*(?=\[))/g;	//匹配节点名称的正则表达式
ws.res.regId   = /\w+(?=\])/g;							//匹配节点ID的正则表达式

/**
 * 根据资源定位符查找本关资源的节点对象
 * @param nodeRes 资源定位符，如parent.child[id]，"parent"表示父节点名称，"child"表示子节点名称，"id"表示子节点中的id属性值
 * @param op_document 资源所在的document对象(可选)
 * @return 节点对象或null如果不存在相应的节点对象
 */
ws.res.findResourceNode = function( nodeRes, op_document ){
	var doc = op_document || document;
	var lastIdx = -1;
	var nodeMtch, idMtch, node, prevNode, nodeObj = null;

	idMtch = ws.res.regId.exec(nodeRes);
	if( idMtch != null )
		idMtch.lastMatch = RegExp.lastMatch;
	while (lastIdx != 0)
	{
		nodeMtch = ws.res.regNode.exec(nodeRes);
		
		if (ws.res.regNode.lastIndex != 0)
		{
			node = RegExp.lastMatch;
			if( idMtch != null && idMtch.lastIndex != 0 && nodeMtch.index + node.length + 1 == idMtch.index ){
				if( nodeObj == null )
					return null;
				else nodeObj = ws.res.findChildNodeById(nodeObj, node, idMtch.lastMatch);
				idMtch = ws.res.regId.exec(nodeRes);
				if( idMtch != null )
					idMtch.lastMatch = RegExp.lastMatch;
			} else {
				if( nodeObj == null )
					nodeObj = doc.getElementById(node);
				else nodeObj = ws.res.findChildNode(nodeObj, node);
			}
		} else if( ws.res.regId.lastIndex != 0 ){
			if( nodeObj == null )
				return null;
			else nodeObj = ws.res.findChildNodeById(nodeObj, prevNode, idMtch.lastMatch);
		}
		prevNode = node;
		lastIdx  = ws.res.regNode.lastIndex;
	}
	return nodeObj
}

/**
 * 查找父节点中节点名为childNodeName的子节点对象
 * @param parentNode 父节点对象
 * @param childNodeName 子节点名称
 * @return 子节点对象或null如果不存在这样的子节点
 */
ws.res.findChildNode = function(parentNode, childNodeName){
	var child;
	for( var i = 0; i < parentNode.childNodes.length; i++ ){
		child = parentNode.childNodes[i];
		if( child.tagName == childNodeName)
			return child;
	}
}

/**
 * 查找父节点中节点名为childNodeName并且id属性值为nodeId的子节点对象
 * @param parentNode 父节点对象
 * @param childNodeName 子节点对象
 * @param nodeId 子节点的id属性值
 * @return 子节点对象或null如果不存在这样的子节点
 */
ws.res.findChildNodeById = function(parentNode, childNodeName, nodeId){
	var child, item;
	for( var i = 0; i < parentNode.childNodes.length; i++ ){
		child = parentNode.childNodes[i];
		if( child.tagName == childNodeName ){
			item = child.attributes.getNamedItem("id");
			if( item != null && item.value == nodeId )
				return child;
		}
	}
	return null;
}