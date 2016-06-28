//=======================================================================================
//脚本调用代理
ScriptPropxy = {};
ScriptPropxy.execute = function(hostUID, hostName, script, args){
	if( args == null || args == undefined )
		args = {};
	var host = null;
	var argName;
	switch( hostName ){
	case "ime.appwindow.FormWindow":
		host = new FormWindow(hostUID);
		argName = "form";
		break;
	case "ime.appwindow.PageWindow":
		host = new PageWindow(hostUID);
		argName = "page";
		break;
	case "ime.appwindow.QueryControl":
		host = new QueryControl(hostUID);
		argName = "query";
		break;
	case "ime.appwindow.ReportControl":
		host = new ReportControl(hostUID);
		argName = "report";
		break;
	default:
		window.alert("Unknow host: " + hostName);
		return;
	}
	var name, vals = [], src = "function _ScriptPropxy_runScript_(" + argName;
	vals.push(host);
	for( name in args ){
		src += ", " + name;
		vals.push(args[name]);
	}
	src += "){\n" + script + "\n}; \n(function(){ return _ScriptPropxy_runScript_; })();";
	
	var func_script = null;
	try{
		func_script = eval(src);
	}
	catch(ex){
		window.alert("脚本定义存在错误：" + ex.message　+ "\n" + src);
		return;
	}

	function task(){
		try{
			//调试脚本的断点设在这里
			result = func_script.apply(null, vals);
		}
		catch(ex){
			window.alert("脚本执行时发生错误:" + ex.message);
		}
		ScriptPropxy.invoke(hostUID, "$asyncExecute_finished$", result);
	}
	window.setTimeout(task, 1);
}

ScriptPropxy.execute_json = function(jsonArg){
	var args = JSON.decode(jsonArg);
	
	return ScriptPropxy.execute.apply(null, args);
}
ScriptPropxy.invoke = function(hostUID, method, args){
	if( window.wos && wos.Desktop ){
		return wos.Desktop.runtime.ScriptProxy_invoke.apply(null, [hostUID, method, args]);
	}
	else if( FlexHelper.runtime != null ){
		var result = FlexHelper.runtime.invokeCallback_json("ScriptProxy_invoke", JSON.encode([hostUID, method, args]));
		if( result == null || result == undefined || result == "" )
			return null;
		return JSON.decode(result);		
	}
	else if( config.use_json_invoke == true ){
		var obj  = $(FlexHelper.flexID);
		var jsonArg;
		if( config.encodeJSON == true )
			jsonArg = encodeURIComponent(JSON.encode([hostUID, method, args]));
		else
			jsonArg = JSON.encode([hostUID, method, args]);

		var result = obj.invokeCallback_json("ScriptProxy_invoke", jsonArg);
		if( result == null || result == undefined || result == "" )
			return null;
		if( config.encodeJSON == true )
			return JSON.decode(decodeURIComponent(result));
		else
			return JSON.decode(result);	
	}
	else {
		var obj  = $(FlexHelper.flexID);
		if( typeof(obj["ScriptProxy_invoke"]) == "function" ){
			return obj.ScriptProxy_invoke(hostUID, method, args);
		}
	}
}

ScriptPropxy.getFlex = function(){
	var flex = null;
	if( window.wos && wos.Desktop ){
		flex = wos.Desktop.runtime;
	}
	else if( FlexHelper.runtime != null ){
		flex = {
			ScriptProxy_invoke:function(){
				var result = FlexHelper.runtime.invokeCallback_json("ScriptProxy_invoke", JSON.encode(arguments));
				if( result == null || result == undefined || result == "" )
					return null;
				return JSON.decode(result);
			}
		}
	}
	else if( config.use_json_invoke == true ){
		flex = {
			ScriptProxy_invoke:function(){
				var obj  = $(FlexHelper.flexID);
				var jsonArg;
				if( config.encodeJSON == true )
					jsonArg = encodeURIComponent(JSON.encode(arguments));
				else
					jsonArg = JSON.encode(arguments);
				var result = obj.invokeCallback_json("ScriptProxy_invoke", jsonArg);
				if( result == null || result == undefined || result == "" )
					return null;
				if( config.encodeJSON == true )
					return JSON.decode(decodeURIComponent(result));
				else
					return JSON.decode(result);	
			}
		}
	}
	else
		flex = $(FlexHelper.flexID);

	return flex;
}
function include(path){
	$include("../../docroot/rootdm/applications/js/client/" + path);
}
//=======================================================================================
//表单窗口的ScriptHost接口
function FormWindow(hostUID){
	this.hostUID = hostUID;
	this.flex = ScriptPropxy.getFlex();
}
FormWindow.prototype.getClassName = function(){
	return "ime.appwindow.FormWindow";
}
FormWindow.prototype.invoke = function(method, args){
	return ScriptPropxy.invoke(this.hostUID, method, args);
}
/**
 * 触发远程事件
 * @param eventName(String) 远程事件名称
 * @param args(Object) 远程事件参数，args中的属性名对应参数名，属性值对应参数值
 * @param callbackName(String) 回调事件名称，即在远程事件执行完成后所触发的表单事件名称
 */
FormWindow.prototype.fireRemoteEvent = function(eventName, args, callbackName){
	this.flex.ScriptProxy_invoke(this.hostUID, "fireRemoteEvent", eventName, args, callbackName);
}
/**
 * 触发在表单中的客户自定义事件
 * @param eventName(String) 事件名称
 * @param args(Object) 事件参数
 */
FormWindow.prototype.fireCustomerEvent = function(eventName, args){
	this.flex.ScriptProxy_invoke(this.hostUID, "fireCustomerEvent", eventName, args);
}
/**
 * 将表单设置为人工起始任务模式
 * @param definitionName(String) 工作流定义名称
 * @param taskName(String) 人工起始任务名称
 */
FormWindow.prototype.setHumanStartTask = function(definitionName, taskName){
	this.flex.ScriptProxy_invoke(this.hostUID, "setHumanStartTask", definitionName, taskName);
}
/**
 * 设置表单控件的值
 * @param name(String) 控件名称
 * @param value(Object) 控件值
 */
FormWindow.prototype.setFieldValue = function(fieldName, value, fireEvent){
	if( fireEvent != false )
		fireEvent = true;
	this.flex.ScriptProxy_invoke(this.hostUID, "setFieldValue", fieldName, value, fireEvent);
}
/**
 * 获取表单控件的值
 * @param name(String) 控件名称
 * @return 表单控件的值
 */
FormWindow.prototype.getFieldValue = function(fieldName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getFieldValue", fieldName);
}
/**
 * 设置表单控件的对象属性
 * @param fieldName(String) 控件名称
 * @param propName(String) 属性名称，目前支持: enabled
 * @param propValue(Object) 属性值
 */
FormWindow.prototype.setFieldProperty = function(fieldName, propName, propValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setFieldProperty", fieldName, propName, propValue);
}
/**
 * 获取表单窗口当前正在编辑的实体对象
 * @return (Object)实体对象 
 */
FormWindow.prototype.getEntity = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getEntity");
}
/**
 * 更新指定实体对象的值，更新的实体对象会在表单提交时在同一事务中一并提交。
 * @param entity(Object) 实体对象值
 * @param entityName(String) 实体类型名称
 */
FormWindow.prototype.updateEntity = function(entity, entityName){
	this.flex.ScriptProxy_invoke(this.hostUID, "updateEntity", entity, entityName);
}
/**
 * 创建一个新的实体对象，创建的实体对象会在表单提交时在同一事务中一并提交。
 * @param entity(Object) 实体对象值
 * @param entityName(String) 实体类型名称
 */
FormWindow.prototype.createEntity = function(entity, entityName){
	this.flex.ScriptProxy_invoke(this.hostUID, "createEntity", entity, entityName);
}
/**
 * 添加服务执行指令
 * @param instruction 服务指令
 */
FormWindow.prototype.addInstruction = function(instruction){
	this.flex.ScriptProxy_invoke(this.hostUID, "addInstruction", instruction);
}
/**
 * 获取表单当前的提交行为(create | edit)
 * @return (String)提交行为
 */
FormWindow.prototype.getSubmitAction = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getSubmitAction");
}
/**
 * 获取表单当前的状态
 * @return (String)状态名称
 */
FormWindow.prototype.getFormState = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getFormState");
}
/**
 * 设置表单当前状态
 * @param stateName(String) 表单状态
 */
FormWindow.prototype.setFormState = function(stateName){
	this.flex.ScriptProxy_invoke(this.hostUID, "setFormState", stateName);
}
/**
 * 设置表单是否可修改
 * @param editable(Boolean) 是否可修改
 */
FormWindow.prototype.setEditable = function(editable){
	this.flex.ScriptProxy_invoke(this.hostUID, "setEditable", editable);
}
/**
 * 生成自动编码的值
 * @param fieldName 自动编码字段名称
 * @param typecode 类型码(可以为空)
 */
FormWindow.prototype.generateCoding = function(fieldName, typecode){
	if( !typecode )
		typecode = "";
	this.flex.ScriptProxy_invoke(this.hostUID, "generateCoding", fieldName, typecode);
}

/**
 * 获取表达式的值
 * @param exp 表达式
 */
FormWindow.prototype.getExpressionValue = function(exp){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getExpressionValue", exp);
}
/**
 * 获取变量值
 * @param name 变量名称
 * @return 变量值
 */
FormWindow.prototype.getVariable = function(name){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getVariable", name);
}
/**
 * 设置变量值
 * @param name 变量名称
 * @param value 变量值
 */
FormWindow.prototype.setVariable = function(name, value){
	this.flex.ScriptProxy_invoke(this.hostUID, "setVariable", name, value);
}
/**
 * 选中一个Tab页
 * @param tabName Tab页名称
 */
FormWindow.prototype.selectTab = function(tabName){
	this.flex.ScriptProxy_invoke(this.hostUID, "selectTab", tabName);
}
/**
 * 获取表单打开参数
 */
FormWindow.prototype.getOpenParameter = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getOpenParameter");
}
/**
 * 执行远程查询服务
 * @param hql HQL语句
 * @param callbackName 回调的表单事件名称
 */
FormWindow.prototype.callRemoteQuery = function(hql, callbackName){
	this.flex.ScriptProxy_invoke(this.hostUID, "callRemoteQuery", hql, callbackName);
}
/**
 * 设置表单窗口样式
 * @param styleName 样式名称
 * @param value 样式值
 */
FormWindow.prototype.setStyle = function(styleName, value){
	this.flex.ScriptProxy_invoke(this.hostUID, "setStyle", styleName, value);
}
/**
 * 打开ALink链接
 * @param alinkUrl alinkURL
 */
FormWindow.prototype.openALink = function(alinkUrl){
	this.flex.ScriptProxy_invoke(this.hostUID, "openALink", alinkUrl);
}
/**
 * 设置表单属性
 * @param propName 属性名称
 * @param propValue 属性值
 */
FormWindow.prototype.setFormProperty = function(propName, propValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setFormProperty", propName, propValue);
}

/**
 * 获取表单属性
 * @param propName 属性名称
 * @return 属性值
 */
FormWindow.prototype.getFormProperty = function(propName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getFormProperty", propName);
}
/**
 * 验证表单字段是否填写完整
 * @return true/false
 */
FormWindow.prototype.validateForm = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "validateForm");
}

/**
 * 显示消息框
 * @param message 消息内容
 * @param title 标题
 * @param flags 消息框的按钮风格,如YesNo
 * @param callback 消息回调事件名称，在该事件中参数detail表示用户点击的按钮，如YES,NO,OK等
 */
FormWindow.prototype.showMessageBox = function(message, title, flags, callback){
	return this.flex.ScriptProxy_invoke(this.hostUID, "showMessageBox", message, title, flags, callback);
}
/**
 * 提交当前表单,在此情况下将不确发提交前事件
 */
FormWindow.prototype.submit = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "submit");
}
/**
 * 获取脚本对象
 * @param host 脚本对象的引用表达式
 */
FormWindow.prototype.getScriptHost = function(host){
	var ref = this.flex.ScriptProxy_invoke(this.hostUID, "getScriptHost", host);

	host = null;
	if( ref != null ){
		var hostName = ref.hostName;
		var hostUID  = ref.hostUID;
		switch( hostName ){
		case "ime.appwindow.FormWindow":
			host = new FormWindow(hostUID);
			break;
		case "ime.appwindow.PageWindow":
			host = new PageWindow(hostUID);
			break;
		case "ime.appwindow.QueryControl":
			host = new QueryControl(hostUID);
			break;
		case "ime.appwindow.ReportControl":
			host = new ReportControl(hostUID);
			break;
		}
	}
	return host;
}
//=======================================================================================
//页面窗口的ScriptHost接口
function PageWindow(hostUID){
	this.hostUID = hostUID;
	this.flex = ScriptPropxy.getFlex();
}
PageWindow.prototype.getClassName = function(){
	return "ime.appwindow.PageWindow";
}
PageWindow.prototype.invoke = function(method, args){
	return ScriptPropxy.invoke(this.hostUID, method, args);
}
/**
 * 调用页面中指定组件的表达式函数
 * @param compName(String) 页面组件名称
 * @param funcName(String) 表达式函数名称
 * @param args(Array) 函数调用参数，必须与函数定义中的参数顺序一致
 */
PageWindow.prototype.invokeExp = function(compName, funcName, args){
	this.flex.ScriptProxy_invoke(this.hostUID, "invokeExp", compName, funcName, args);
}
/**
 * 获取表达式的值
 * @param exp 表达式
 */
PageWindow.prototype.getExpressionValue = function(exp){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getExpressionValue", exp);
}
/**
 * 调用实体自定义扩展服务事件
 * @param entityName(String) 实体名称
 * @param eventName(String) 自定义服务事件名称
 * @param eventArgs(Object) 服务事件调用参数
 * @param cbEventName(String) 回调事件名称，在回调事件中通过result参数获取返回结果
 */
PageWindow.prototype.fireRemoteEvent = function(entityName, eventName, eventArgs, cbEventName){
	this.flex.ScriptProxy_invoke(this.hostUID, "fireRemoteEvent", entityName, eventName, eventArgs, cbEventName);
}

/**
 * 触发在表单中的客户自定义事件
 * @param eventName(String) 事件名称
 * @param args(Object) 事件参数
 */
PageWindow.prototype.fireCustomerEvent = function(eventName, args){
	this.flex.ScriptProxy_invoke(this.hostUID, "fireCustomerEvent", eventName, args);
}

/**
 * 获取查询结果数据集
 * @param queryUid(String) 查询定义的UID
 * @param condition(Object) 查询条件对象,对象的属性表示查询条件的值
 * @param startNo(Integer) 查询结果的起始行数
 * @param count(Integer) 查询结果的返回行数
 * @param cbEventName(String) 回调事件名称，在回调事件中通过result参数获取返回结果
 */
PageWindow.prototype.executeQuery = function(queryUid, condition, startNo, count, cbEventName){
	this.flex.ScriptProxy_invoke(this.hostUID, "executeQuery", queryUid, condition, startNo, count, cbEventName);
}
/**
 * 查找实体对象
 * @param entityName 实体定义名称
 * @param entityId 实体ID
 * @param cbEventName 回调事件名称
 */
PageWindow.prototype.findEntity = function(entityName, entityId, cbEventName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "findEntity", entityName, entityId, cbEventName);
}
/**
 * 获取脚本对象
 * @param host 脚本对象的引用表达式
 */
PageWindow.prototype.getScriptHost = function(host){
	var ref = this.flex.ScriptProxy_invoke(this.hostUID, "getScriptHost", host);

	host = null;
	if( ref != null ){
		var hostName = ref.hostName;
		var hostUID  = ref.hostUID;
		switch( hostName ){
		case "ime.appwindow.FormWindow":
			host = new FormWindow(hostUID);
			break;
		case "ime.appwindow.PageWindow":
			host = new PageWindow(hostUID);
			break;
		case "ime.appwindow.QueryControl":
			host = new QueryControl(hostUID);
			break;
		case "ime.appwindow.ReportControl":
			host = new ReportControl(hostUID);
			break;
		}
	}
	return host;
}
//=======================================================================================
//查询窗口的ScriptHost接口
function QueryControl(hostUID){
	this.hostUID = hostUID;
	this.flex = ScriptPropxy.getFlex();
}
QueryControl.prototype.getClassName = function(){
	return "ime.appwindow.QueryControl";
}
/**
 * 获取查询条件对象
 * @return 查询条件对象
 */
QueryControl.prototype.getQueryCondition = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getQueryCondition");
}
/**
 * 设置查询条件对象
 * @param cond 查询条件对象
 */
QueryControl.prototype.setQueryCondition = function(cond){
	this.flex.ScriptProxy_invoke(this.hostUID, "setQueryCondition", cond);
}
/**
 * 设置查询条件输入面板中指定控件的属性值
 * @param fieldName 条件面板中的控件名称
 * @param propName 控件的属性名称
 * @param propValue 控件的属性值
 */
QueryControl.prototype.setConditionFormProperty = function(fieldName, propName, propValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setConditionFormProperty", fieldName, propName, propValue);
}

/**
 * 触发远程事件
 * @param eventName(String) 远程事件名称
 * @param args(Object) 远程事件参数
 * @param callbackName(String) 回调事件名称，即在远程事件执行完成后所触发的表单事件名称
 */
QueryControl.prototype.fireRemoteEvent = function(eventName, args, callbackName){
	this.flex.ScriptProxy_invoke(this.hostUID, "fireRemoteEvent", eventName, args, callbackName);
}
/**
 * 触发在查询中的客户自定义事件
 * @param eventName(String) 事件名称
 * @param args(Object) 事件参数
 */
QueryControl.prototype.fireCustomerEvent = function(eventName, args){
	this.flex.ScriptProxy_invoke(this.hostUID, "fireCustomerEvent", eventName, args);
}

/**
 * 执行远程查询服务
 * @param hql HQL语句
 * @param callbackName 回调的表单事件名称
 */
QueryControl.prototype.callRemoteQuery = function(hql, callbackName){
	this.flex.ScriptProxy_invoke(this.hostUID, "callRemoteQuery", hql, callbackName);
}
/**
 * 返回当前窗口中所有的记录
 * @return 记录数组
 */
QueryControl.prototype.getAllRecords = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getAllRecords");
}
/**
 * 返回当前窗口中选中的记录
 * @return 选中的记录数组
 */
QueryControl.prototype.getSelectedRecords = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getSelectedRecords");
}
QueryControl.prototype.getSelectedRecord = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getSelectedRecord");
}
/**
 * 返回当前窗口中选中记录的索引号
 * @return 选中记录的索引号数组
 */
QueryControl.prototype.getSelectedIndexes = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getSelectedIndexes");
}

/**
 * 设置状态栏显示内容
 * @param html 状态栏显示内容(html格式)
 */
QueryControl.prototype.setStatusHtml = function(html){
	this.flex.ScriptProxy_invoke(this.hostUID, "setStatusHtml", html);
}

/**
 * 清除选中的数据记录
 */
QueryControl.prototype.clearSelection = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "clearSelection");
}
/**
 * 返回当前窗口中所有的实体对象
 * @param entityName 实体类型名称
 * @return 实体数组
 */
QueryControl.prototype.getAllEntities = function(entityName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getAllEntities", entityName);
}
/**
 * 返回当前窗口中选中的实体对象
 * @param entityName 实体类型名称
 * @return 实体数组
 */
QueryControl.prototype.getSelectedEntities = function(entityName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getSelectedEntities", entityName);
}
QueryControl.prototype.getSelectedEntity = function(entityName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getSelectedEntity", entityName);
}
/**
 * 设置当前选中的记录
 * @param index 记录索引号
 */
QueryControl.prototype.setSelectedIndex = function(index){
	return this.flex.ScriptProxy_invoke(this.hostUID, "setSelectedIndex", index);
}

/**
 * 插入一条数据记录
 * @param record 记录数据
 */
QueryControl.prototype.insertRecord = function(record, index){
	if( index == null || index == undefined )
		index = -1;
	this.flex.ScriptProxy_invoke(this.hostUID, "insertRecord", record, index);
}
/**
 * 删除记录
 * @param index 删除记录的索引号
 */
QueryControl.prototype.deleteRecordAt = function(index){
	if( index == null || index == undefined )
		index = -1;
	this.flex.ScriptProxy_invoke(this.hostUID, "deleteRecordAt", index);
}

/**
 * 返回当前窗口中指定序列号的记录
 * @param index 记录所在的序列号
 * @return 记录对象
 */
QueryControl.prototype.getRecordAt = function(index){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getRecordAt", index);
}
/**
 * 设置当前窗口中指定序列号的记录
 * @param record 记录数据
 * @param index 记录所在的序列号
 */
QueryControl.prototype.setRecordAt = function(record, index){
	return this.flex.ScriptProxy_invoke(this.hostUID, "setRecordAt", record, index);
}
/**
 * 返回当前窗口中指定序列号的实体对象
 * @param entityName 实体类型
 * @param index 记录所在的序列号
 * @return 记录对象
 */
QueryControl.prototype.getEntityAt = function(entityName, index){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getEntityAt", entityName, index);
}
/**
 * 设置当前窗口中指定序列号的实体对象
 * @param entity 实体对象
 * @param entityName 实体类型
 * @param index 记录所在的序列号
 */
QueryControl.prototype.setEntityAt = function(entity, entityName, index){
	return this.flex.ScriptProxy_invoke(this.hostUID, "setEntityAt", entity, entityName, index);
}

/**
 * 创建一个实体对象,该对象在执行保存动作时被提交
 * @param entity 实体对象
 * @param entityName 实体类型
 */
QueryControl.prototype.createEntity = function(entity, entityName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "createEntity", entity, entityName);
}
/**
 * 更新一个实体对象,该实体对象通过id属性进行标识，并在执行保存动作时被提交
 * @param entity 实体对象
 * @param entityName 实体类型
 */
QueryControl.prototype.updateEntity = function(entity, entityName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "updateEntity", entity, entityName);
}
/**
 * 添加服务执行指令
 * @param instruction 服务指令
 */
QueryControl.prototype.addInstruction = function(instruction){
	this.flex.ScriptProxy_invoke(this.hostUID, "addInstruction", instruction);
}
/**
 * 调用指定组件的表达式函数
 * @param compExp(String) 组件的引用表达式
 * @param funcName(String) 表达式函数名称
 * @param args(Array) 函数调用参数，必须与函数定义中的参数顺序一致
 */
QueryControl.prototype.invokeExp = function(compExp, funcName, args){
	this.flex.ScriptProxy_invoke(this.hostUID, "invokeExp", compExp, funcName, args);
}
/**
 * 打开一个ALink链接
 * @param strALink ALink字符串
 */
QueryControl.prototype.openALink = function(strALink){
	this.flex.ScriptProxy_invoke(this.hostUID, "openALink", strALink);
}
/**
 * 获取ALink的打开参数
 */
QueryControl.prototype.getOpenParameter = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getOpenParameter");
}
/**
 * 获取表达式的值
 * @param exp 表达式
 */
QueryControl.prototype.getExpressionValue = function(exp){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getExpressionValue", exp);
}
/**
 * 刷新当前页中的显示数据
 */
QueryControl.prototype.refreshCurrentPage = function(){
	this.flex.ScriptProxy_invoke(this.hostUID, "refreshCurrentPage");
}
/**
 * 刷新查询结果，并自动跳转到第一页显示
 */
QueryControl.prototype.refresh = function(refreshAll){
	if( refreshAll == null || refreshAll == undefined )
		refreshAll = true;
	this.flex.ScriptProxy_invoke(this.hostUID, "refresh", refreshAll);
}
/**
 * 获取变量值
 * @param name 变量名称
 * @return 变量值
 */
QueryControl.prototype.getVariable = function(name){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getVariable", name);
}
/**
 * 设置变量值
 * @param name 变量名称
 * @param value 变量值
 */
QueryControl.prototype.setVariable = function(name, value){
	this.flex.ScriptProxy_invoke(this.hostUID, "setVariable", name, value);
}

/**
 * 设置控件属性
 * @param propertyName 属性名称
 * @param propertyValue 属性值 
 */
QueryControl.prototype.setProperty = function(propertyName, propertyValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setProperty", propertyName, propertyValue);
}
/**
 * 设置数据显示列属性
 * @param columnName 数据列名称，必须是主实体的字段名称
 * @param propertyName 属性名称，目前支持enabled,required两个属性
 * @param propertyValue 属性值 
 */
QueryControl.prototype.setColumnProperty = function(columnName, propertyName, propertyValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setColumnProperty", columnName, propertyName, propertyValue);
}

/**
 * 设置工具条按钮属性
 * @param btnName 按钮索引值或按钮名称
 * @param propertyName 属性名称，目前支持enabled属性
 * @param propertyValue 属性值
 */
QueryControl.prototype.setToolButtonProperty = function(btnName, propertyName, propertyValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setToolButtonProperty", btnName, propertyName, propertyValue);
}

/**
 * 显示指定列
 * @param entityName 显示列所属的实体名称
 * @param fieldName 显示列对应的字段名称
 * @param columnLabel 列头显示名称(可选)
 */
QueryControl.prototype.showColumn = function(entityName, fieldName, columnLabel){
	if( columnLabel == null || columnLabel == undefined )
		columnLabel = "";
	this.flex.ScriptProxy_invoke(this.hostUID, "showColumn", entityName, fieldName, columnLabel);
}

/**
 * 隐藏指定列
 * @param entityName 隐藏列所属的实体名称
 * @param fieldName 隐藏列对应的字段名称
 */
QueryControl.prototype.hideColumn = function(entityName, fieldName){
	this.flex.ScriptProxy_invoke(this.hostUID, "hideColumn", entityName, fieldName);
}

/**
 * 保存当前数据
 */
QueryControl.prototype.save = function(){
	this.flex.ScriptProxy_invoke(this.hostUID, "save");
}

/**
 * 设置当前编辑焦点的位置
 * @param rowIndex 行索引
 * @param column 列索引或数据列名称(格式为 实体.字段)
 */
QueryControl.prototype.setEditPosition = function(rowIndex, column){
	this.flex.ScriptProxy_invoke(this.hostUID, "setEditPosition", rowIndex, column);
}
/**
 * 显示消息框
 * @param message 消息内容
 * @param title 标题
 * @param flags 消息框的按钮风格,如YesNo
 * @param callback 消息回调事件名称，在该事件中参数detail表示用户点击的按钮，如YES,NO,OK等
 */
QueryControl.prototype.showMessageBox = function(message, title, flags, callback){
	return this.flex.ScriptProxy_invoke(this.hostUID, "showMessageBox", message, title, flags, callback);
}
/**
 * 获取脚本对象
 * @param host 脚本对象的引用表达式
 */
QueryControl.prototype.getScriptHost = function(host){
	var ref = this.flex.ScriptProxy_invoke(this.hostUID, "getScriptHost", host);

	host = null;
	if( ref != null ){
		var hostName = ref.hostName;
		var hostUID  = ref.hostUID;
		switch( hostName ){
		case "ime.appwindow.FormWindow":
			host = new FormWindow(hostUID);
			break;
		case "ime.appwindow.PageWindow":
			host = new PageWindow(hostUID);
			break;
		case "ime.appwindow.QueryControl":
			host = new QueryControl(hostUID);
			break;
		case "ime.appwindow.ReportControl":
			host = new ReportControl(hostUID);
			break;
		}
	}
	return host;
}

//=======================================================================================
//报表窗口的ScriptHost接口
function ReportControl(hostUID){
	this.hostUID = hostUID;
	this.flex = ScriptPropxy.getFlex();
}
ReportControl.prototype.getClassName = function(){
	return "ime.appwindow.ReportControl";
}
/**
 * 获取查询条件对象
 * @return 查询条件对象
 */
ReportControl.prototype.getQueryCondition = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getQueryCondition");
}
/**
 * 获取查询条件对象
 * @param cond 查询条件对象
 */
ReportControl.prototype.setQueryCondition = function(cond){
	this.flex.ScriptProxy_invoke(this.hostUID, "setQueryCondition", cond);
}
/**
 * 调用实体自定义扩展服务事件
 * @param entityName(String) 实体名称
 * @param eventName(String) 自定义服务事件名称
 * @param eventArgs(Object) 服务事件调用参数
 * @param cbEventName(String) 回调事件名称，在回调事件中通过result参数获取返回结果
 */
ReportControl.prototype.fireRemoteEvent = function(entityName, eventName, eventArgs, cbEventName){
	this.flex.ScriptProxy_invoke(this.hostUID, "fireRemoteEvent", entityName, eventName, eventArgs, cbEventName);
}
/**
 * 触发在报表中的客户自定义事件
 * @param eventName(String) 事件名称
 * @param args(Object) 事件参数
 */
ReportControl.prototype.fireCustomerEvent = function(eventName, args){
	this.flex.ScriptProxy_invoke(this.hostUID, "fireCustomerEvent", eventName, args);
}
/**
 * 设置查询条件输入面板中指定控件的属性值
 * @param fieldName 条件面板中的控件名称
 * @param propName 控件的属性名称
 * @param propValue 控件的属性值
 */
ReportControl.prototype.setConditionFormProperty = function(fieldName, propName, propValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setConditionFormProperty", fieldName, propName, propValue);
}
/**
 * 获取数据源对象
 * @param dsName 数据源对象名称
 */
ReportControl.prototype.getDataSource = function(dsName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getDataSource", dsName);
}
/**
 * 设置数据源对象
 * @param dsName 数据源对象名称
 * @param dsValue 数据源对象值
 */
ReportControl.prototype.setDataSource = function(dsName, dsValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setDataSource", dsName, dsValue);
}
/**
 * 设置数据源对象
 * @param dsName 数据源对象名称
 * @param fieldName 数据源对象的字段名称
 * @param fieldValue 数据源对象的字段值
 */
ReportControl.prototype.setDataSourceField = function(dsName, fieldName, fieldValue){
	this.flex.ScriptProxy_invoke(this.hostUID, "setDataSourceField", dsName, fieldName, fieldValue);
}
/**
 * 打开ALink链接
 * @param alinkUrl alinkURL
 */
ReportControl.prototype.openALink = function(alinkUrl){
	this.flex.ScriptProxy_invoke(this.hostUID, "openALink", alinkUrl);
}
/**
 * 在当前数据位置显示上下文菜单
 * @param menuData 菜单数据定义，类型为对象数组，每一个对象代表一个菜单项，
 * 在菜单项中label表示菜单名;action表示菜单执行动作,执行动作中可以触发事件，也可以打开ALink，
 * 触发事件的定义为event:{事件名称}[?参数1=xx&参数2=xx...]，打开ALink的动作直接用action表
 * 示ALink链接即可;在菜单中也可指定iconUrl表示图标所在地址，地址路径为绝对路径。
 */
ReportControl.prototype.showContextMenu = function(menuData){
	this.flex.ScriptProxy_invoke(this.hostUID, "showContextMenu", menuData);
}

/**
 * 获取ALink的打开参数
 */
ReportControl.prototype.getOpenParameter = function(){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getOpenParameter");
}
/**
 * 设置数据透视表的生成方式
 * @param tableName 表格名称
 * @param xPivots 列轴定义，每个轴的定义为：{label:显示名称, field:字段名称}
 * @param yPivots 行轴定义，每个轴的定义为：{label:显示名称, field:字段名称}
 * @param dataField 统计结果数据的字段名
 * @param sql 获取数据的SQL语句，该SQL语句中必须拥有列轴定义和行轴定义中的所有字段
 * @param style 表格样式数据:{dataLink:数据链接, dataStyle:数据单元格样式(name1=value1;name2=value2)}
 * @param recordSet 透视表数据集, 与sql字段互斥
 */
ReportControl.prototype.setPivotTable = function(tableName, xPivots, yPivots, dataField, sql, style, recordSet){
	if( recordSet == undefined )
		recordSet = null;
	return this.flex.ScriptProxy_invoke(this.hostUID, "setPivotTable", tableName, xPivots, yPivots, dataField, sql, style, recordSet);
}

/**
 * 获取表格单元格数据
 * @param tableName 表格名称
 * @param rowIndex 单元格行号
 * @param colIndex 单元格列号
 * @return 单元格数据对象
 */
ReportControl.prototype.getTableCell = function(tableName, rowIndex, colIndex){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getTableCell", tableName, rowIndex, colIndex);
}

ReportControl.prototype.getTableProperties = function(tableName){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getTableProperties", tableName);
}
/**
 * 设置表格单元格数据
 * @param tableName 表格名称
 * @param rowIndex 单元格行号
 * @param colIndex 单元格列号
 * @param cellObj 单元格数据，例:{fillColor:"#FF0000"},其中fillColor表示单元格背景色
 */
ReportControl.prototype.setTableCell = function(tableName, rowIndex, colIndex, cellObj){
	this.flex.ScriptProxy_invoke(this.hostUID, "setTableCell", tableName, rowIndex, colIndex, cellObj);
}
/**
 * 向表格最右端追加列
 * @param tableName 表格名称
 */
ReportControl.prototype.appendTableCol = function(tableName){
	this.flex.ScriptProxy_invoke(this.hostUID, "appendTableCol", tableName);
}
/**
 * 向表格最下方追加行
 * @param tableName 表格名称
 */
ReportControl.prototype.appendTableRow = function(tableName){
	this.flex.ScriptProxy_invoke(this.hostUID, "appendTableRow", tableName);
}

/**
 * 向表格指定的列之前插入新列
 * @param tableName 表格名称
 * @param colIndex 列号
 */
ReportControl.prototype.insertTableColBefor = function(tableName, colIndex){
	this.flex.ScriptProxy_invoke(this.hostUID, "insertTableColBefor", tableName, colIndex);
}
/**
 * 向表格指定的行之前插入新行
 * @param tableName 表格名称
 * @param rowIndex 行号
 */
ReportControl.prototype.insertTableRowBefor = function(tableName, rowIndex){
	this.flex.ScriptProxy_invoke(this.hostUID, "insertTableRowBefor", tableName, rowIndex);
}
/**
 * 向表格指定的行之后插入新行
 * @param tableName 表格名称
 * @param rowIndex 行号
 */
ReportControl.prototype.insertTableRowAfter = function(tableName, rowIndex){
	this.flex.ScriptProxy_invoke(this.hostUID, "insertTableRowAfter", tableName, rowIndex);
}
/**
 * 合并表格单元格
 * @param tableName 表格名称
 * @param x1 最左单元格列号
 * @param y1 最上单元格行号
 * @param x2 最右单元格列号
 * @param y2 最下单元格行号
 */
ReportControl.prototype.mergeTableCell = function(tableName, x1, y1, x2, y2){
	this.flex.ScriptProxy_invoke(this.hostUID, "mergeTableCell", tableName, x1, y1, x2, y2);
}
/**
 * 设置表格指定列的宽度
 * @param tableName 表格名称
 * @param colIndex 列号
 * @param width 宽度
 */
ReportControl.prototype.setTableColWidth = function(tableName, colIndex, width){
	this.flex.ScriptProxy_invoke(this.hostUID, "setTableColWidth", tableName, colIndex, width);
}
/**
 * 设置表格指定行的高度
 * @param tableName 表格名称
 * @param rowIndex 行号
 * @param height 高度
 */
ReportControl.prototype.setTableRowHeight = function(tableName, rowIndex, height){
	this.flex.ScriptProxy_invoke(this.hostUID, "setTableRowHeight", tableName, rowIndex, height);
}
/**
 * 获取表达式的值
 * @param exp 表达式
 */
ReportControl.prototype.getExpressionValue = function(exp){
	return this.flex.ScriptProxy_invoke(this.hostUID, "getExpressionValue", exp);
}
/**
 * 刷新当前页中的显示数据
 */
ReportControl.prototype.refresh = function(){
	this.flex.ScriptProxy_invoke(this.hostUID, "refresh");
}
/**
 * 获取脚本对象
 * @param host 脚本对象的引用表达式
 */
ReportControl.prototype.getScriptHost = function(host){
	var ref = this.flex.ScriptProxy_invoke(this.hostUID, "getScriptHost", host);

	host = null;
	if( ref != null ){
		var hostName = ref.hostName;
		var hostUID  = ref.hostUID;
		switch( hostName ){
		case "ime.appwindow.FormWindow":
			host = new FormWindow(hostUID);
			break;
		case "ime.appwindow.PageWindow":
			host = new PageWindow(hostUID);
			break;
		case "ime.appwindow.QueryControl":
			host = new QueryControl(hostUID);
			break;
		case "ime.appwindow.ReportControl":
			host = new ReportControl(hostUID);
			break;
		}
	}
	return host;
}


function include(path){
	$include("../../docroot/rootdm/applications/js/client/" + path);
}