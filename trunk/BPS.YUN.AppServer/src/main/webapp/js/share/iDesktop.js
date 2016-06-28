
/* 虚拟桌面的js控制对象 */
iDesktop = {};
/* 接收从虚拟桌面发出的消息的消息映射表 */
iDesktop.eventMap = [];
/* 应用链接处理函数映射表 */
iDesktop.alinkMap = {};
/* 默认ALink处理函数 */
iDesktop.defaultALinkHandler = null;
/* 当前环境中的Flex应用名称 */
iDesktop.flexApps = [];

/* 判断虚拟桌面是否有效 */
iDesktop.isAvailable = function(){
    if (external && typeof(external.version) == "string" && external.version != "unknown")
		return true;
	else
		return false;
}

/**
 * 注册Flex应用程序
 * @param flexApp Flex应用程序的名称
 */
iDesktop.regFlexApplication = function(flexApp){
	for( var i = 0; i < iDesktop.flexApps.length; i++ ){
		if( iDesktop.flexApps[i] == flexApp )
			return;
	}
	iDesktop.flexApps.push(flexApp);
}

/**
 * 调用虚拟桌面中的本地服务 
 * @param request 请求对象，格式为{service:服务名称,method:方法名称,args:[参数数组]}
 * @return 虚拟服务的返回值
 */
iDesktop.call = function(request) {
    if (!iDesktop.isAvailable())
        return null;

    var requestString = JSON.stringify(request);
    var result = external.call(request.service, request.method, requestString);
    if (result && result.length > 0)
        return JSON.parse(result);
    else
        return null;
}

/**
 * 此函数由虚拟桌面程序调用，用于触发虚拟桌面中的一个事件
 * @param jsonEvent JSON格式的事件对象，对象内容为:
 * {"type":事件类型, "source":事件发生源, ...:事件其他相关参数}
 */
function iDesktop_dispatchEvent(jsonEvent){
    var event = JSON.parse(jsonEvent);
	
	var eventEntry, i, j;
	
	//触发已注册的消息处理函数
	window.setTimeout(
		function(){
			for( var i = 0; i < iDesktop.eventMap.length; i++ ){
				eventEntry = iDesktop.eventMap[i];
				if( eventEntry.type == event.type ){
					for( j = 0; j < eventEntry.handles.length; j++ ){
						eventEntry.handles[j].handler(event);
					}
					break;
				}
            }
            var flexName, flex;
            //向Flex应用程序转发消息
            for (i = 0; i < iDesktop.flexApps.length; i++) {
                flexName = iDesktop.flexApps[i];
                flex = $(flexName);
                if (flex && typeof (flex.dispatchEvent) == "function") {
                    flex.dispatchEvent(event.type, event.action, event);
                }
            }
		},
		1
	);
}

/**
 * 触发一个WebOS事件，此函数由Flex应用程序调用
 * @param srcFlexID 产生事件的Flex程序ID
 * @param event 事件对象
 */
iDesktop.dispatchEvent = function(srcFlexID, event){
	var eventEntry, i, j;

	//触发已注册的消息处理函数
	for( i = 0; i < iDesktop.eventMap.length; i++ ){
		eventEntry = iDesktop.eventMap[i];
		if( eventEntry.type == event.type ){
			window.setTimeout(
				function(){
					for( var i = 0; i < eventEntry.handles.length; i++ ){
						if( eventEntry.handles[i].flexID != srcFlexID )
							eventEntry.handles[i].handler(event);
					}
				}, 
				1);
			break;
		}
	}
}

/**
 * 添加一个事件处理函数
 * @param srcFlexID 事件所在的Flex程序ID
 * @param eventType 事件类型
 * @param handler 事件处理函数
 */
iDesktop.addEventListener = function(srcFlexID, eventType, handler){
	var eventEntry, i, j;

	for( i = 0; i < iDesktop.eventMap.length; i++ ){
	    if (iDesktop.eventMap[i].type == eventType) {
			eventEntry = iDesktop.eventMap[i];
			break;
		}
	}
	if( !eventEntry ){
	    eventEntry = { type: eventType, handles: [] };
		iDesktop.eventMap.push(eventEntry);
	}
	if( typeof(handler) != "function" )
		handler = eval(handler);
	if( handler != null && handler != undefined && typeof(handler) == "function" )
		eventEntry.handles.push({flexID:srcFlexID, handler:handler});
}

/**
 * 注册应用链接处理函数
 * @param appName 应用程序名称
 * @param handler 处理函数
 */
iDesktop.registerALinkHandler = function(appName, handler){
	var entry, i, j;

	if( typeof(handler) != "function" )
		handler = eval(handler);
	if( handler != null && handler != undefined && typeof(handler) == "function" )
		iDesktop.alinkMap[appName] = handler;
}

/**
 * 注册默认ALink处理函数
 * @param handler 默认ALink处理函数
 */
iDesktop.registerDefaultALinkHandler = function(handler){
	if( typeof(handler) != "function" )
		handler = eval(handler);
	if( handler != null && handler != undefined && typeof(handler) == "function" )
		iDesktop.defaultALinkHandler = handler;
}

/**
 * 打开一个应用链接
 * @param alink 应用链接对象
 */
iDesktop.openALink = function(alink){
	var handler = iDesktop.alinkMap[alink.target];
	
	if( handler != null && handler != undefined && typeof(handler) == "function" )
		window.setTimeout(function(){handler(alink);}, 1);
	else if( iDesktop.defaultALinkHandler != null && typeof(iDesktop.defaultALinkHandler) == "function" )
		window.setTimeout(function(){iDesktop.defaultALinkHandler(alink)}, 1);
}

/**
 * 将javascript对象转换成xml的表示形式
 * @param name 对象名称
 * @param data 对象数据
 * @return 转换后的xml文本
 */
iDesktop.toXml = function(name, data){
	var element, xml = "";

	switch (typeof data){
	case "boolean":
		xml = "<" + name + " type=\"boolean\">" + data + "</" + name + ">";
		break;
	case "number":
		xml = "<" + name + " type=\"number\">" + data + "</" + name + ">";
		break;
	case "string":
		xml = "<" + name + " type=\"string\">" + iDesktop._escapeXML(data) + "</" + name + ">";
		break;
	case "object":
		if (data instanceof String) 
			xml += "<" + name + " type=\"string\">" + iDesktop._escapeXML(data) + "</" + name + ">";
		else if (data instanceof Boolean)
			xml += "<" + name + " type=\"boolean\">" + data + "</" + name + ">";
		else if (data instanceof Number)
			xml += "<" + name + " type=\"number\">" + data + "</" + name + ">";
		else if (data instanceof Date)
			xml += iDesktop._serializeDateXml(name, data);
		else if (data instanceof Array)
			xml += iDesktop._serializeArrayXml(name, data);
		else 
			xml += iDesktop._serializeObjectXml(name, data);
		break;
	}
	return xml;
}

iDesktop._escapeXML = function(s){
	return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&apos;");
}
/**
 * 将javascript的Object对象转换成xml的表示形式
 * @param name 对象名称
 * @param data 对象数据
 * @return 转换后的xml文本
 */
iDesktop._serializeObjectXml = function(name, data){
	var xml = "<" + name + " type=\"object\">";
	for (element in data) {
	    xml += iDesktop.toXml(element, data[element]);
	}
	xml += "</" + name + ">";
	return xml;
}

/**
 * 将javascript的Date对象转换成xml的表示形式
 * @param name 对象名称
 * @param data 对象数据
 * @return 转换后的xml文本
 */
iDesktop._serializeDateXml = function(name, data){
	var xml = "<" + name + " type=\"date\">";
	xml += data.getFullYear() + "-";
	xml += (data.getMonth() + 1) + "-";
	xml += data.getDate() + " ";
	xml += data.getHours() + ":";
	xml += data.getMinutes() + ":";
	xml += data.getSeconds();
	xml += "</" + name + ">";
	return xml;
}

/**
 * 将javascript的Array对象转换成xml的表示形式
 * @param name 对象名称
 * @param data 对象数据
 * @return 转换后的xml文本
 */
iDesktop._serializeArrayXml = function(name, data){
	var xml = "<" + name + " type=\"array\">";
	for (var i = 0; i < data.length; i++) {
		xml += iDesktop.toXml("item", data[i]);
	}
	xml += "</" + name + ">";
	return xml;
}
