var XPT = {};

/**
 * 定义名称空间
 */
XPT.namespace = function(ns){
	var part = ns.split("\.");
	var script = "";
	if( part.length > 0 ){
		var curns = part[0];
		script += "if( !window." + curns + " ) window." + curns + " = {};\n";
		for( var i = 1; i < part.length; i++ ){
			curns += "." + part[i];
			script += "if( !window." + curns + " ) window." + curns + " = {};\n";
		}
		window.eval(script);
	}
}


var AjaxSOA = function(url, service, method, param){
	this.req = {service:service, method:method};
	if( param )
		this.req.param = param;
	else
		this.req.param = {};
	this.url = url;
	
	this.invoke = function(param, success_callback, error_callback){
		var req = {};
		req.service = this.req.service;
		req.method  = this.req.method;
		req.param  = {};
		for( var n in this.req.param ){
			req.param[n] = this.req.param[n];
		}
		for( var k in param ){
			req.param[k] = param[k];
		}
		var url = this.url + "?req=" + encodeURIComponent(JSON.stringify(req)) +"&encode=utf-8&random="+Math.random();
		jQuery.ajax({
			url: url,
			type: 'GET',
			dataType : 'json',
			timeout: 200000,
			error: function(jqXHR, textStatus, errorThrown){
				error_callback(textStatus);
			},
			success: function(data){
				if( data != null ){
					if( data.status == "OK" )
						success_callback(data.returnValue);
					else
						error_callback(data.message);
				}
				else
					error_callback("系统错误。");
			}
		});
	}
}