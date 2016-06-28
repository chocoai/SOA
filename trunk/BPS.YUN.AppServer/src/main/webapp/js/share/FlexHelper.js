
/* Flex与浏览器交互的辅助脚本 */
FlexHelper = {};
FlexHelper.flexID = "";		//Flex对象的ID
FlexHelper.runtime= null;	//运行时环境
FlexHelper.root   = "/IMEServer/";
FlexHelper.cookie = new Object();
FlexHelper.baseUrl= "";
FlexHelper.remoteServices = new Object();	//已注册的远程服务

/* 设置FlexID */
FlexHelper.init = function(flexID, parentID, baseUrl){
	KillRightClick.init(flexID, parentID);

	if( baseUrl != null && baseUrl != undefined )
		FlexHelper.baseUrl = baseUrl;
	else
		FlexHelper.baseUrl = $base_url("FlexHelper.js");

	FlexHelper.flexID = flexID;
//	dwr.engine.setOrdered(true);

	var pathname = window.location.pathname;
	var index = pathname.indexOf("/applications", 0);
	if( index != -1 ){
		var path = pathname.substring(0, index);
		if( path.charAt(0) != '/' )
			path = "/" + path;
		FlexHelper.root = path;
	}
	
	if( window.MenuHelper != undefined)
		MenuHelper.handleFunction = "FlexHelper.handleMenu";
}
FlexHelper.initWithRuntime = function(runtime){
	FlexHelper.runtime = runtime;
	FlexHelper.baseUrl = $base_url("FlexHelper.js").replace("//", "/");

	var pathname = window.location.pathname;
	var index = pathname.indexOf("/applications", 0);
	if( index != -1 ){
		var path = pathname.substring(0, index);
		if( path.charAt(0) != '/' )
			path = "/" + path;
		FlexHelper.root = path;
	}	
}
FlexHelper.close = function(){
	if( FlexHelper.runtime != null ){
		return FlexHelper.runtime.invokeCallback("desktop_invoke", "close");
	}
	else {
		var flex = $(FlexHelper.flexID);
	
		if( flex && flex.desktop_invoke )
			return flex.desktop_invoke("close");
	}
	return null;
}
FlexHelper.getFlexID = function(){
	return FlexHelper.flexID;
}

FlexHelper.openDocument = function(url, type){
	try{
		if( type == "word" ){
			var word = new ActiveXObject("Word.Application");
			word.Visible = true;
			word.Documents.Open(url);
			word.Application.Activate();
			//打开修订
			word.CommandBars("Reviewing").Visible = true;
			word.ActiveDocument.TrackRevisions    = true;
			word.ActiveDocument.ActiveWindow.View.RevisionsView = 0;           
			word.ActiveDocument.ActiveWindow.View.ShowRevisionsAndComments = true;
			word = null;
		}
	}
	catch(e){
		alert(e.message);
	}
}
FlexHelper.openXecmDocument = function(server, file){
	file = file.replace(/\//g, "\\");
	var url = "\\\\" + server + "\\root\\" + file;
	window.open(url);
}
/**
 * 重新装入当前页面
 */
FlexHelper.reloadWindow = function(){
	var url = window.location.href;
	var parts = url.split("?");
	if( parts.length > 0 && parts[0] != "" )
		window.location.href = parts[0];
	else
		window.location.reload();
}

FlexHelper.getClipboard = function(){
	return window.clipboardData.getData("Text");
}
/**
 * 获取当前应用的相对目录
 * @return 相对目录
 */
FlexHelper.getRelativeURL = function(){
	return FlexHelper.baseUrl;
}
FlexHelper.getRootName = function(){
	return FlexHelper.root;
}
FlexHelper.addFavorite = function(title){
	var url = window.location.href;
	var i = url.indexOf("applications/terminal");
	if( i != -1 ){
		url = url.substring(0, i - 1);
		window.external.AddFavorite(url, title);
	}
}
/**
 * 注册远程服务
 * @param serviceArray 远程服务的名称数组
 */
FlexHelper.regRemoteService = function(serviceArray){
}

/**
 * 获取会话中的属性对象
 * @param name 属性名称
 * @return 属性值
 */
FlexHelper.setAttribute = function(name, value){
	var win = window.top;
	
	if( win == null )
		win = window;
		
	if( !win.session )
		win.session = {};
	win.session[name] = value;
}
/**
 * 设置会话中的属性对象
 * @param name 属性名称
 * @param value 属性值
 */
FlexHelper.getAttribute = function(name){
	var win = window.top, attr;
	
	if( win == null )
		win = window;
		
	if( win.session )
		attr = win.session[name];
	if( attr ){
		return FlexHelper.resolveType(attr);
	}
	else
		return eval(name);
}

FlexHelper.resolveType = function(val){
	if( val == null )
		return val;
	else if(val instanceof Date) {
        return val;
    } else if (val instanceof Array) {
        return val
    } else if (typeof(val) == "object") {
        if( val.pop != undefined && typeof(val.length) == "number" )
            val = Array(val);
		for( var prop in val ){
			val[prop] = FlexHelper.resolveType(val[prop]);
		}
    }

    return val;
}

/**
 * 初始化cookie
 */
FlexHelper.initCookie = function(){
	var aCookie = document.cookie.split("; ");
	for (var i=0; i < aCookie.length; i++)
	{
		var aCrumb = aCookie[i].split("=");
		if( FlexHelper.cookie[aCrumb[0]] == null || FlexHelper.cookie[aCrumb[0]] == undefined )
			FlexHelper.cookie[aCrumb[0]] = unescape(aCrumb[1]);
	}
}
FlexHelper.initCookie();
/**
 * 获取cookie值
 */
FlexHelper.getCookie = function(name){
	return FlexHelper.cookie[name];
}
/**
 * 设置cookie值
 * @param name cookie名
 * @param value cookie值
 * @param path 路径(可选)
 * @param expire 过期期限(可选)
 * @param domain 域(可选)
 * @param secure 安全标记(可选)
 */
FlexHelper.setCookie = function(name, value){
	FlexHelper.cookie[name] = value;
	var argv = arguments;
	var argc = arguments.length;
	var path    = (2 < argc) ? argv[2] : null;
	var expires = (3 < argc) ? argv[3] : null;
	var domain  = (4 < argc) ? argv[4] : null;
	var secure  = (5 < argc) ? argv[5] : false;
	
	if( expires == null )
		expires = new Date(3000, 1, 1);

	document.cookie = name + "=" + escape (value) +
		((expires == null) ? "" : ("; expires=" + expires.toGMTString())) +
		((path    == null) ? "" : ("; path=" + path)) +
		((domain  == null) ? "" : ("; domain=" + domain)) +
		((secure  == true) ? "; secure" : "");
}
/**
 * 删除cookie
 * @param name cookie名
 */
FlexHelper.deleteCookie = function(name){
	document.cookie = name + "=null; expires=Fri, 31 Dec 1099 23:59:59 GMT;";
	FlexHelper.cookie[name] = null;
}

/* 调用远程服务 
 * @param serviceName 远程服务类及服务名
 * @param callUID 函数调用的唯一ID
 * @vararg_params 服务参数(多参)
 */
FlexHelper.callRemote = function(serviceName, callUID, vararg_params){
	var args = [];
	for (var i = 0; i < arguments.length - 2; i++) {
		args[i] = arguments[i + 2];
	}

	try{
		var part = serviceName.split(".");
		if( part.length != 2 ){
			window.alert("错误的远程方法名:" + serviceName);
			return false;
		}
		var code = "dwr.engine._execute('" + FlexHelper.root + "/dwr/','" + part[0] + "','" + part[1] + "',";
	
		for( var i = 0; i < args.length; i++ )
			code += "args[" + i + "],";
		code += "function(data){ FlexHelper.callback(callUID, data); })";

		eval(code);
	}
	catch(e){
		window.alert(e.name + ":" + e.message);
	}
	return true;
}

/* 开启远程服务的批调用处理 */
FlexHelper.beginBatchCall = function(){
	dwr.engine.beginBatch();
}

/* 结束远程服务的批调用处理，在该调用之前及beginBatchCall调用之后的所有远程服务调用将被
 * 同时提交给后台处理。
 * @param callback 批处理结束后的flex回调函数名或事件名
 * @param type 回调方式，有效值为"callUID"或"function"，如果type值为callUID表示callback是回调函数的唯一ID，
 * 这种方式通过flexID.remote_callback回调；如果type值为function表示callback是回调函数的名称，通过flexID.callback
 * 回调。
 */
FlexHelper.endBatchCall = function(callback, type){
	var options = {};
	if( callback ){
		if( type == "callUID" )
			options.postHook = function(){ FlexHelper.callback(callback, null); }
		else if( type == "function" )
			options.postHook = function(){ FlexHelper.event_callback(callback, undefined); }
	}
	dwr.engine.endBatch(options);
}

/* 远程服务调用结束后的回调函数，将返回数据传给flex的回调函数
 * @param callUID 函数调用的唯一ID
 * @param data 远程服务的返回数据
 */
FlexHelper.callback = function(callUID, data){
	if( FlexHelper.runtime != null ){
		FlexHelper.runtime.invokeCallback_json("remote_callback", JSON.encode([callUID, data]));
	}
	else {
		var obj  = $(FlexHelper.flexID);
		if( typeof(obj["remote_callback"]) == "function" ){
			obj.remote_callback(callUID, data);
		}
	}
}

FlexHelper.event_callback = function(callback, data){
	if( callback && callback.length > 0 ){
		if( FlexHelper.runtime != null ){
			if( data === undefined )
				FlexHelper.runtime.invokeCallback(callback);
			else
				FlexHelper.runtime.invokeCallback_json(callback, JSON.encode([data]));
		}
		else {
			var obj  = $(FlexHelper.flexID);
			if( typeof(obj[callback]) == "function" ){
				var code = "obj.";
				if( data === undefined )
					code += callback + "()";
				else
					code += callback + "(data)";
	
				window.setTimeout(
					function(){
						eval(code);
					},
					1
				);
			}
		}
	}
}

/**
 * 设置远程方法调用前的回调函数
 * @param prehook 在Flex应用中的回调函数名称
 */
FlexHelper.regRemoteCallPreHook = function(prehook){
	dwr.engine.setPreHook(function(){ FlexHelper.event_callback(prehook, undefined); });
}

/**
 * 设置远程方法调用后的回调函数
 * @param prehook 在Flex应用中的回调函数名称
 */
FlexHelper.regRemoteCallPostHook = function(posthook){
	dwr.engine.setPostHook(function(){ FlexHelper.event_callback(posthook, undefined); });
}

/**
 * 设置远程方法错误处理函数
 * @param prehook 在Flex应用中的函数名称
 */
FlexHelper.regRemoteCallErrorHandler = function(errorHandler){
	dwr.engine.setErrorHandler(function(msg){ FlexHelper.event_callback(errorHandler, msg); });
}

/* 脚本错误的处理函数 */
FlexHelper.onError = function(msg) {
	var message;
	if( msg instanceof Error )
		message = msg.name + ":" + msg.message;
	else
		message = msg;
	window.alert(message);
}

if( window.dwr )
	dwr.engine.setErrorHandler(FlexHelper.onError);

/**
 * 触发Flex的右键菜单功能
 */
FlexHelper_onContextMenu = function(){
	if( FlexHelper.runtime != null ){
		return FlexHelper.runtime.invokeCallback("onRightClick");
	}
	else {
		var flex = $(FlexHelper.flexID);
		if( flex && flex.onRightClick )
			return flex.onRightClick();
	}
	return false;
}

/**
 * 菜单事件处理函数
 * @param menuName 发生事件的菜单名称
 * @param menuId 发生事件的菜单标识
 */
FlexHelper.handleMenu = function(menuName, menuId){
	if( FlexHelper.runtime != null ){
		return FlexHelper.runtime.invokeCallback("handleMenuItemClick", menuName, menuId);
	}
	else {
		var flex = $(FlexHelper.flexID);
	
		if( flex && flex.handleMenuItemClick )
			flex.handleMenuItemClick(menuName, menuId);
	}
}

/**
 * 显示菜单
 * @param name 菜单标识名称
 * @param x 菜单显示的X坐标(相对于Flex控件)
 * @param y 菜单显示的Y坐标(相对于Flex控件)
 */
FlexHelper.showMenu = function(name, x, y){
	var flex = $(FlexHelper.flexID);

	if( window.MenuHelper != undefined )
		MenuHelper.showMenu(name, x + flex.offsetLeft, y + flex.offsetTop);
}

/**
 * 计算字符串表达式的值
 * @param exp 字符串表达式
 * @return 计算后的值
 */
FlexHelper.getExpressionValue = function(exp){
	var o;
	try{
		if( typeof exp == "string" ){
			if( exp.charAt(0) == "{" )
				o = eval("[" + exp + "]")[0];
			else
				o = eval(exp);
		}
		else
			o = exp;
    }
    catch(ex){
        o = null;
    }	
	return o;
}
FlexHelper.getExpressionValue_json = function(exp){
	var o;
	try{
		if( typeof exp == "string" ){
			if( exp.charAt(0) == "{" )
				o = eval("[" + exp + "]")[0];
			else
				o = eval(exp);
		}
		else
			o = exp;
    }
    catch(ex){
        o = null;
    }
    return JSON.encode(o);
}

/**
 * 执行一段javascript脚本，在该脚本中不能再调用Flex中定义的方法
 */
FlexHelper.runScript = function(script, arg){
	var o;
	try{
		eval("function _FlexHelper_runScript_(arg){\n" + script + "\n}");
		o = _FlexHelper_runScript_(arg);
	}
	catch(ex){
		window.alert(ex.message);
	}
	return o;
}

desktop = {};	//由javascript调用全局桌面对象
desktop.getDictionary = function(path){
	if( window.wos && wos.Desktop ){
		return wos.Desktop.getDictionary(path);
	}
	else if( FlexHelper.runtime != null ){
		var result = FlexHelper.runtime.invokeCallback_json("desktop_invoke", JSON.encode(["getDictionary", path]));
		if( result == null || result == undefined || result == "" )
			return null;
		return JSON.decode(result);
	}
	else {
		var flex = $(FlexHelper.flexID);
	
		if( flex && flex.desktop_invoke )
			return flex.desktop_invoke("getDictionary", path);
	}
	return null;
}

Dictionary = {};	//数据字典操作对象
/**
 * 根据字典表值查找对应的显示名称
 * @param dict 字典表数据对象，由desktop.getDictionary()得到
 * @param value 字典表值
 * @return 与字典表值对应的显示名称
 */
Dictionary.getLabel = function(dict, value){
	if( dict == null )
		return "";
	for( var i = 0; i < dict.length; i++ ){
		if( dict[i].value == value )
			return dict[i].label;
	}
	return "";
}
/**
 * 根据字典表显示名称查找对应的字典表值
 * @param dict 字典表数据对象，由desktop.getDictionary()得到
 * @param label 字典表显示名称
 * @return 与字典表显示名称对应的值
 */
Dictionary.getValue = function(dict, label){
	if( dict == null )
		return null;
	for( var i = 0; i < dict.length; i++ ){
		if( dict[i].label == label )
			return dict[i].value;
	}
	return null;
}

//=======================================================================================
//远程调用服务
Remoting = {};
Remoting.path = "/IMEServer/dwr";
Remoting.hook = {};

Remoting.init = function(){
	var pathname = window.location.pathname;
	pathname = pathname.replace("//", "/");
	var index = pathname.indexOf("/applications", 0);
	if( index != -1 ){
		var path = pathname.substring(0, index) + "/dwr";
		if( path.charAt(0) != '/' )
			path = "/" + path;
		Remoting.path = path;
	}
	Remoting.path = Remoting.path.replace("//", "/");
}

Remoting.init();

/**
 * 设置函数挂钩
 * @param hookType 挂钩类型: call
 * @param hookFunction 挂钩函数,"call" Hook的函数定义为 boolean function(serviceName, callUID, vararg_params);
 * 如果返回true，则表示不执行默认行为。如果hookFunction取代默认行为，则在取得数据后必须调用Remoting.callback,在发生异
 * 常时必须调用Remoting.exceptionHandler
 */
Remoting.addHook = function(hookType, hookFunction){
	if( Remoting.hook[hookType] == null )
		Remoting.hook[hookType] = [];
	Remoting.hook[hookType].push(hookFunction);
}
/* 调用远程服务 
 * @param serviceName 远程服务类及服务名
 * @param callUID 函数调用的唯一ID
 * @vararg_params 服务参数(多参)
 */
Remoting.callRemote = function(serviceName, callUID, vararg_params){
	var hooks = Remoting.hook["call"];
	if( hooks != null ){
		var hooked = false;
		for( var i = 0; i < hooks.length; i++ ){
			if( hooks[i].apply(null, arguments) == true )
				hooked = true;
		}
		if( hooked == true )
			return;
	}
	var args = [];
	for (var i = 0; i < arguments.length - 2; i++) {
		args[i] = arguments[i + 2];
	}
	try{
		var part = serviceName.split(".");
		if( part.length != 2 ){
			window.alert("错误的远程方法名:" + serviceName);
			return false;
		}
		var code = "dwr.engine._execute('" + Remoting.path + "','" + part[0] + "','" + part[1] + "',";
		for( var i = 0; i < args.length; i++ )
			code += "args[" + i + "],";
	
		if( serviceName == "SOAService.call" )
			code += "{callback:function(data){ Remoting.callback(callUID, data, true); },exceptionHandler:function(errorString, exception){Remoting.exceptionHandler(callUID, errorString, exception);}})";
		else
			code += "{callback:function(data){ Remoting.callback(callUID, data); },exceptionHandler:function(errorString, exception){Remoting.exceptionHandler(callUID, errorString, exception);}})";

		eval(code);
	}
	catch(e){
		window.alert(e.name + ":" + e.message);
	}
	return true;
}
/* 通过代理调用远程服务 
 * @param proxy 代理名称或地址
 * @param serviceName 远程服务类及服务名
 * @param callUID 函数调用的唯一ID
 * @vararg_params 服务参数(多参)
 */
Remoting.callProxy = function(proxy, serviceName, callUID, vararg_params){
	var hooks = Remoting.hook["call"];
	if( hooks != null ){
		var hooked = false;
		for( var i = 0; i < hooks.length; i++ ){
			if( hooks[i].apply(null, arguments) == true )
				hooked = true;
		}
		if( hooked == true )
			return;
	}
	var args = [];
	for (var i = 0; i < arguments.length - 3; i++) {
		args[i] = arguments[i + 3];
	}
	try{
		var part = serviceName.split(".");
		if( part.length != 2 ){
			window.alert("错误的远程方法名:" + serviceName);
			return false;
		}
		var proxyPath = Remoting.path + "Proxy/" + proxy;
		var code = "dwr.engine._execute('" + proxyPath + "','" + part[0] + "','" + part[1] + "',";
		for( var i = 0; i < args.length; i++ )
			code += "args[" + i + "],";
		
		if( serviceName == "SOAService.call" )
			code += "{callback:function(data){ Remoting.callback(callUID, data, true); },exceptionHandler:function(errorString, exception){Remoting.exceptionHandler(callUID, errorString, exception);}})";
		else
			code += "{callback:function(data){ Remoting.callback(callUID, data); },exceptionHandler:function(errorString, exception){Remoting.exceptionHandler(callUID, errorString, exception);}})";

		eval(code);
	}
	catch(e){
		window.alert(e.name + ":" + e.message);
	}
	return true;
}
Remoting.callSOA = function(callUID, url, retName){
	var s = document.createElement("script");
	s.src = url;
    s.type = "text/javascript";
    s.onreadystatechange = function()
    {    
        switch(this.readyState)
        {
            case "complete":
            case "loaded":
            	eval("Remoting.callback(callUID, " + retName + ")");
                document.body.removeChild(s);
                break;
            case "loading":
                break;
        }
    }
    s.onload = function() {
    	eval("Remoting.callback(callUID, " + retName + ")");
    	document.body.removeChild(s);
    }
    document.body.appendChild(s);
}

Remoting.callRemote_json = function(jsonArg){
	var args = JSON.decode(jsonArg);
	
	return Remoting.callRemote.apply(null, args);
}
Remoting.callProxy_json = function(jsonArg){
	var args = JSON.decode(jsonArg);
	
	return Remoting.callProxy.apply(null, args);
}
/* 开启远程服务的批调用处理 */
Remoting.beginBatchCall = function(){
	dwr.engine.beginBatch();
}

/* 结束远程服务的批调用处理，在该调用之前及beginBatchCall调用之后的所有远程服务调用将被
 * 同时提交给后台处理。
 * @param callUID 批处理函数调用的唯一ID
 */
Remoting.endBatchCall = function(callUID){
	var options = {};
	if( callUID ){
		options.postHook = function(){ Remoting.callback(callUID, null); }
		options.exceptionHandler = function(errorString, exception){ Remoting.exceptionHandler(callUID, errorString, exception); }
	}
	dwr.engine.endBatch(options);
}

/* 远程服务调用结束后的回调函数，将返回数据传给flex的回调函数
 * @param callUID 函数调用的唯一ID
 * @param data 远程服务的返回数据
 * @param decodeResultAsJson 是否将data字符串以JSON格式转换成Object对象
 */
Remoting.callback = function(callUID, data, decodeResultAsJson){
	if( decodeResultAsJson == true )
		data = JSON.decode(data);
	
	if( FlexHelper.runtime != null ){
		return FlexHelper.runtime.invokeCallback_json("remoting_callback", JSON.encode([callUID, data]));
	}
	else {
		var obj  = $(FlexHelper.flexID);
		if( typeof(obj["remoting_callback"]) == "function" ){
			obj.remoting_callback(callUID, data);
		}
	}
}
Remoting.exceptionHandler = function(callUID, errorString, exception){
	if( FlexHelper.runtime != null ){
		return FlexHelper.runtime.invokeCallback_json("remoting_exception", JSON.encode([callUID, errorString, exception]));
	}
	else {
		var obj  = $(FlexHelper.flexID);
		if( typeof(obj["remoting_exception"]) == "function" ){
			obj.remoting_exception(callUID, errorString, exception);
		}
	}
}

//=======================================================================================
var KillRightClick = {
    /**
     *  Constructor
     */
    init: function (flashId, contentId) {
        this.FlashObjectID    = flashId;
        this.FlashContainerID = contentId;
        this.Cache = this.FlashObjectID;
        if(window.addEventListener){
             window.addEventListener("mousedown", this.onGeckoMouse(), true);
        } else {
            document.getElementById(this.FlashContainerID).onmouseup = function() { document.getElementById(KillRightClick.FlashContainerID).releaseCapture(); }
            document.oncontextmenu = function(){ if(window.event.srcElement.id == KillRightClick.FlashObjectID) { return false; } else { KillRightClick.Cache = "nan"; }return false;}
            document.getElementById(this.FlashContainerID).onmousedown = KillRightClick.onIEMouse;
        }
    },

    /**
     * GECKO / WEBKIT event overkill
     * @param {Object} eventObject
     */
    killEvents: function(eventObject) {
        if(eventObject) {
            if (eventObject.stopPropagation) eventObject.stopPropagation();
            if (eventObject.preventDefault) eventObject.preventDefault();
            if (eventObject.preventCapture) eventObject.preventCapture();
            if (eventObject.preventBubble) eventObject.preventBubble();
        }
    },

    /**
     * GECKO / WEBKIT call right click
     * @param {Object} ev
     */
    onGeckoMouse: function(ev) {
        return function(ev) {
        if (ev.button != 0) {
			KillRightClick.killEvents(ev);
            //if(ev.target.id == KillRightClick.FlashObjectID && KillRightClick.Cache == KillRightClick.FlashObjectID) {
                KillRightClick.call();
            //}
            KillRightClick.Cache = ev.target.id;
        }
      }
    },

    /**
     * IE call right click
     * @param {Object} ev
     */
    onIEMouse: function() {
        if (event.button > 1 || event.button == 0 ) {
          //  if(window.event.srcElement.id == KillRightClick.FlashObjectID && KillRightClick.Cache == KillRightClick.FlashObjectID) {
                if( KillRightClick.call() )
                	return;
          //  }
            document.getElementById(KillRightClick.FlashContainerID).setCapture();
            if(window.event.srcElement.id)
            KillRightClick.Cache = window.event.srcElement.id;
        }
    },

    /**
     * Main call to Flash External Interface
     */
    call: function() {
    	if( FlexHelper.runtime != null ){
			return FlexHelper.runtime.invokeCallback("onRightClick");
		}
		else {
			var flex = $(FlexHelper.flexID);
			if( flex && flex.onRightClick && flex.onRightClick() )
				return true;
			else
				return false;
		}
    }
}
function onOK(callUID, data){
	alert(callUID + ":" + data)
}


//====    OfficeUtil   ================================================================================================
OfficeUtil = {};
OfficeUtil.getSharePoint_OpenDocuments = function () {
	if (window.SharePoint_OpenDocuments) {
		return window.SharePoint_OpenDocuments;
	}
	else {
		var progids = ["SharePoint.OpenDocuments.3", "SharePoint.OpenDocuments.2", "SharePoint.OpenDocuments.1"];
		for (var i = 0; i < progids.length; ++i) {
			try {
				return (window.SharePoint_OpenDocuments = new ActiveXObject(progids[i]));
			}
			catch (e)
			{ }
		}
		return null;
	}
}

OfficeUtil.OpenOfficeDocument = function(url, readOnly)
{
	var openDocObj; 
    openDocObj = OfficeUtil.getSharePoint_OpenDocuments(); 
	if( openDocObj != null && openDocObj != undefined ){
		if( readOnly == true )
			openDocObj.ViewDocument(url);
		else
		    openDocObj.EditDocument(url);
	}
}
/**
 * 在线打开Excel文档
 * @param url 文档路径
 * @param user 用户名称
 * @param readOnly 是否只读
 * @param trackRevisions 是否保留编辑痕迹
 */
OfficeUtil.OpenExcel = function(url, user, readOnly, trackRevisions) 
{
	var oExcel = null;
	var oWorkbook = null;
	try{
		oExcel = new ActiveXObject("Excel.Application");
		oWorkbook = oExcel.Workbooks.Open(url, false, readOnly);
		oExcel.DisplayAlerts = false;
		oExcel.Visible = true;
		oExcel.UserName = user;
		oWorkbook.KeepChangeHistory = trackRevisions;

		eval("function oWorkbook::Close() { OfficeUtil.onAppClosed('" + url + "');}");
	}
	catch(e){
		alert("文档打开错误：" + e.message);
		if( oExcel != null )
			oExcel.Quit();
	}
	return false;
}
OfficeUtil.onAppClosed = function(url)
{
	if( FlexHelper.runtime != null ){
		FlexHelper.runtime.invokeCallback_json("desktop_invoke", JSON.encode(["dispatchEvent", "OfficeApplicationEvent", "Closed", url]));
	}
	else {
		var flex = $(FlexHelper.flexID);
	
		if( flex && flex.desktop_invoke )
			return flex.desktop_invoke("dispatchEvent", "OfficeApplicationEvent", "Closed", url);
	}
}
/**
 * 在线打开Word文档
 * @param url 文档路径
 * @param user 用户名称
 * @param readOnly 是否只读
 * @param trackRevisions 是否保留编辑痕迹
 */
OfficeUtil.OpenWord = function(url, user, readOnly, trackRevisions) 
{
	var wApp = null;
	var wDocument = null;
	try{
		wApp = new ActiveXObject("Word.Application");       
		
		wApp.Documents.Open(url, false, readOnly, false, "", "", false, "", "", 0);
		wApp.Visible = true;
		wApp.Activate();
		
		wDocument = wApp.ActiveDocument;

		if( trackRevisions ){ //保留痕迹
			 wDocument.TrackRevisions = true ;
			 wDocument.ShowRevisions = true  ;
		}
		else{
			 wDocument.TrackRevisions = false ;
			 wDocument.ShowRevisions = false  ;           
		}      
		wDocument.Application.UserName= user;
		eval("function wDocument::Close() { OfficeUtil.onAppClosed('" + url + "');}");
		return true;
	}
	catch(e){
		alert("文档打开错误：" + e.message);
		if( wApp != null )
			wApp.Quit();
	}
	return false;
}