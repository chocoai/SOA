/**
 * 由运行环境内部调用，执行JS环境中的相关方法
 * @param functionName JS函数名,如XXX.XXX
 * @param json_args 以JSON格式定义的方法参数
 * @return 以JSON格式定义的返回值
 */
function runtime_invoke_json(functionName, json_args){
	var args = JSON.parse(json_args);
	if( args == null || !(args instanceof Array) )
		args = [];

	if( functionName == "window.open" ){
		window.open(args[0]);
		return;
	}

	var ret = null;
	var part = functionName.split(".");
	if( part.length == 1 ){
		var func = eval(functionName);
		if( func == null )
			return null;
		ret = func.apply(null, args);
	}
	else {
		var obj = eval(part[0]);
		if( obj != null && obj[part[1]] != null )
			ret = obj[part[1]].apply(obj, args);
	}
	return JSON.stringify(ret);
}

/**
 * 调用指定运行环境的内部方法
 * @param runtime 运行环境
 * @param method 内部方法名
 * @param arguments 多参
 * @return 返回值
 */
function invoke_runtime(runtime, method){
	var args = [];
	for (var i = 0; i < arguments.length - 2; i++) {
		args[i] = arguments[i + 2];
	}
	var result = runtime.js_call_json(method, JSON.stringify(args));
	if( result == null || result == undefined || result == "" )
		return null;
	return JSON.parse(result);
}

var QS = {};
QS.initExternalRuntime = function(){
	QS.setRuntime(external);
}
QS.setRuntime = function(runtime){
	QS.runtime = runtime;
	QS.baseUrl = $base_url("qs.js").replace("//", "/");

	var pathname = window.location.pathname;
	var index = pathname.indexOf("/applications", 0);
	if( index != -1 ){
		var path = pathname.substring(0, index);
		if( path.charAt(0) != '/' )
			path = "/" + path;
		QS.root = path;
	}	
}
QS.invoke = function(method){
	if( window.wos && wos.Desktop.runtime )
		return wos.Desktop.runtime.call(method);
	var args = [];
	for (var i = 0; i < arguments.length - 1; i++) {
		args[i] = arguments[i + 1];
	}
	var result = QS.runtime.js_call_json(method, JSON.stringify(args));
	if( result == null || result == undefined || result == "" )
		return null;
	return JSON.parse(result);
}
QS.openALink = function(alink) {
	//alink = decodeURIComponent(alink);
	alink = alink.replace(/#/g, "\"");
	if (QS.runtime != null && QS.runtime != undefined )
		QS.runtime.openALink(alink);
	else if( window.wos )
		window.wos.Desktop.openALink("", alink);
	else if( window.parent && window.parent.wos )
		window.parent.wos.Desktop.openALink("", alink);
}

if( !window.js && (window.parent && window.parent.js)){
	var importNS = function(ns, scope, srcNS){
		if (!ns || !ns.length) {
			return;
		}
		scope = scope || window;
		srcNS = srcNS || window;
		var nsobj  = srcNS;
		var levels = ns.split(".");
		
		for(var i = 0; i < levels.length; i++){
	        nsobj = nsobj[levels[i]];
	        if( nsobj === null || nsobj === undefined )
	        	return;
	    }
		if( nsobj.$type === "Class" || nsobj.$type === "Interface" ){
			scope[levels[levels.length - 1]] = nsobj;
			return;
		}
		for( var n in nsobj){
			if( nsobj.hasOwnProperty(n) )
				scope[n] = nsobj[n];
		}
	};
	window.StringUtil = window.parent.StringUtil;
	window.js = {};
	importNS("js", window.js, window.parent);
	window.qs = {};
	importNS("qs", window.qs, window.parent);
	window.wos = {};
	importNS("wos", window.wos, window.parent);
	
	if( qs.ui.device.isMobile ){
		$(document).bind("touchend", function(event){
			qs.ui.popup._checkExternalClick(event);
		});
	}
	else {
		$(document).bind("mousedown", function(event){
			qs.ui.popup._checkExternalClick(event);
		});
	}
}
var IFormComponent = js.Interface("IFormComponent", function(){
	js.$public.getValue = function(){};
	js.$public.setValue = function(value){};
});

var HtmlControl = new js.Class("HtmlControl", [IFormComponent], function(){
	js.$public.options = function(val){
		if( val === undefined )
			return this._options;
		this._options = val;
		if( this._options == null )
			this._options = {};
	};
	//验证控件的输入是否正确, return true/false
	js.$public.validate = function(){
		return true;
	};
	//获取控件的值，定义为public方法，实现IFormComponent中的getValue方法
	js.$public.getValue = function(){
	};
	//设置控件的值，定义为public方法，实现IFormComponent中的setValue方法
	js.$public.setValue = function(value){
	};

	js.$public.fireChangeEvent = function(){
		if( QS.runtime && QS.runtime.type == "HtmlControlRuntime" )
			invoke_runtime(QS.runtime, "setValue", this.getValue());
	};
	js.$public.render = function(container, options){};
});