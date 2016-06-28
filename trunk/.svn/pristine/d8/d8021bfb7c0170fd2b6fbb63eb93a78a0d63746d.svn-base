/*
	页面初始化
*/
function initPage(){
	//填充默认参数
	var sysidElement = document.getElementById("sysidText");
	var vElement = document.getElementById("vText");
	sysidElement.value = initSysid;
	vElement.value = initV;

	//把timeStamp默认填写为当前时间
	var timestampEntity = document.getElementById("timestampText");
	timestampEntity.value = getNowFormatDate();

	//显示创建会员的param参数
	var createMemberDivEntity = document.getElementById("createMember");
	createMemberDivEntity.style.display = "block";

	//填充service
	var serviceSelect = document.getElementById("serviceSelect");
	for(var i in serviceList){
		var option = new Option(serviceList[i].text, serviceList[i].value);
		serviceSelect.add(option);
	}

	//填充method
	var methodSelect = document.getElementById("methodSelect");
	for(var i in memberMethodList){
		var option = new Option(memberMethodList[i].text, memberMethodList[i].value);
		methodSelect.add(option);
	}

	//填充请求地址
	var serviceUrlSelect = document.getElementById("serviceUrlSelect");
	for(var i in serviceUrlList){
		var option = new Option(serviceUrlList[i].text, serviceUrlList[i].value);
		serviceUrlSelect.add(option);
	}
}

/*
	获取签名
*/

function getSignOnClick(){
	//获取参数
	var serviceUrlSelect = document.getElementById("serviceUrlSelect");
	var index = serviceUrlSelect.selectedIndex;
	var serviceUrl = serviceUrlSelect[index].value;

	var sysid = document.getElementById("sysidText").value;
	var timestamp = document.getElementById("timestampText").value;
	var v = document.getElementById("vText").value;

	var serviceSelect = document.getElementById("serviceSelect");
	index = serviceSelect.selectedIndex;
	var serviceName = serviceSelect[index].value;

	var methodSelect = document.getElementById("methodSelect");
	index = methodSelect.selectedIndex;
	var methodName = methodSelect[index].value;

	var param = "{";
	var paramList = $("#" + methodName + " .param");
	for(var i = 0; i < paramList.length; i++){
		var spanList= paramList[i].getElementsByTagName("span");
		var emList = spanList[0].getElementsByTagName("em");
		var paramName = emList.length > 0 ? emList[0].innerHTML : spanList[0].innerHTML;
		var paramText = spanList[1].getElementsByTagName("input")[0];
		var paramA = spanList[1].getElementsByTagName("a")[0];

		if(paramText != undefined){
			if(i > 0){
				param += ",";
			}
			param += "\"" + paramName + "\":\"" +  paramText.value + "\"";
		}
		if(paramA != undefined){
			var textareaId = paramA.getAttribute("textareaId");
			var textareaId = paramA.getAttribute("textareaId");
			var textareaValue = $("#" + textareaId)[0].value.replace(/[ ]/g, "").replace(/[\r]/g, "").replace(/[\n]/g, "").replace(/[\t]/g, "");
			if(textareaValue == "" || textareaValue == "null"){
				continue;
			}
			
			if(i > 0){
				param += ",";
			}
			param += "\"" + paramName + "\":" + textareaValue;
		}
	}

	param += "}";

	//document.getElementById("test").innerHTML = param;

	if(sysid == ""){
		alert("sysid不能为空。");
		return;
	}
	if(timestamp == ""){
		alert("timestamp不能为空。");
		return;
	}
	if(v == ""){
		alert("v不能为空。");
		return;
	}
	if(param == ""){
		alert("param不能为空。");
		return;
	}

	//获取签名，在ajax的成功操作中进行SOA的请求
	req = {
				"service" : serviceName, 
				"method" : methodName, 
				"param"   : JSON.parse(param)
	};

	var signCompleteUrl = signUrl + "?sysid=" + sysid + "&req=" + encodeURIComponent(encodeURIComponent(JSON.stringify(req))) + "&timestamp=" + timestamp;

	invokeAjax(signCompleteUrl, httpMethod, ajaxSingSuc, ajaxError);
}

/*
	ajax获取签名成功
*/
function ajaxSingSuc(data){
	var dataObj = JSON.parse(data);
	var status = dataObj.status;

	if(status != "0"){
		alert("错误：" + dataObj.message);

		return;
	}
	//获取sign，并赋值到页面
	document.getElementById("signText").value = dataObj.sign;
}


/*
	发送按钮事件
*/
function requestOnClick(){
	//获取参数
	var serviceUrlSelect = document.getElementById("serviceUrlSelect");
	var index = serviceUrlSelect.selectedIndex;
	var serviceUrl = serviceUrlSelect[index].value;

	var sysid = document.getElementById("sysidText").value;
	var timestamp = document.getElementById("timestampText").value;
	var sign = document.getElementById("signText").value;
	var v = document.getElementById("vText").value;

	var serviceSelect = document.getElementById("serviceSelect");
	index = serviceSelect.selectedIndex;
	var serviceName = serviceSelect[index].value;

	var methodSelect = document.getElementById("methodSelect");
	index = methodSelect.selectedIndex;
	var methodName = methodSelect[index].value;

	var param = "{";
	var paramList = $("#" + methodName + " .param");
	for(var i = 0; i < paramList.length; i++){
		var spanList= paramList[i].getElementsByTagName("span");
		var emList = spanList[0].getElementsByTagName("em");
		var paramName = emList.length > 0 ? emList[0].innerHTML : spanList[0].innerHTML;
		var paramText = spanList[1].getElementsByTagName("input")[0];
		var paramA = spanList[1].getElementsByTagName("a")[0];

		if(paramText != undefined){
			if(i > 0){
				param += ",";
			}
			param += "\"" + paramName + "\":\"" +  paramText.value + "\"";
		}
		if(paramA != undefined){
			var textareaId = paramA.getAttribute("textareaId");
			var textareaId = paramA.getAttribute("textareaId");
			var textareaValue = $("#" + textareaId)[0].value.replace(/[ ]/g, "").replace(/[\r]/g, "").replace(/[\n]/g, "").replace(/[\t]/g, "");
			if(textareaValue == "" || textareaValue == "null"){
				continue;
			}
			
			if(i > 0){
				param += ",";
			}
			param += "\"" + paramName + "\":" + textareaValue;
		}
	}

	param += "}";

	if(sysid == ""){
		alert("sysid不能为空。");
		return;
	}
	if(timestamp == ""){
		alert("timestamp不能为空。");
		return;
	}
	if(sign == ""){
		alert("sign不能为空。");
		return;
	}
	if(v == ""){
		alert("v不能为空。");
		return;
	}
	if(param == ""){
		alert("param不能为空。");
		return;
	}

	req = {
				"service" : serviceName, 
				"method" : methodName, 
				"param"   : JSON.parse(param)
	};

	var serviceCompleteUrl = serviceUrl + "?sysid=" + sysid + "&timestamp=" + encodeURIComponent(timestamp) + "&sign=" + encodeURIComponent(sign) + "&v=" + v +  "&req=" + encodeURIComponent(JSON.stringify(req));
	var portCompleteUrl = portUrl + "?serviceCompleteUrl=" + encodeURIComponent(serviceCompleteUrl);

	var serviceCompleteShowUrl = serviceUrl + "?sysid=" + sysid + "&timestamp=" + timestamp + "&sign=" + sign + "&v=" + v +  "&req=" + JSON.stringify(req);
	document.getElementById("requestUrlText").value = serviceCompleteShowUrl;

	//ajax请求
	invokeAjax(portCompleteUrl, httpMethod, ajaxSOASuc, ajaxError);

	//锁定发送按钮
	document.getElementById("SOARequest").href = "javascript:noUser();";
}

function noUser(){

}

/*
	ajax请求SOA接口成功
*/
function ajaxSOASuc(data){
	var dataObj = JSON.parse(decodeURIComponent(data));

	var status = dataObj.status;

	if(status != "0"){
		alert("错误：" + dataObj.message);

		return;
	}

	document.getElementById("headerDiv").innerHTML = dataObj.header.replace(/\+/g, " ");
	document.getElementById("bodyTestarea").value = JSON.stringify(JSON.parse(dataObj.body), null, 4);

	//锁定发送按钮
	document.getElementById("SOARequest").href = "javascript:requestOnClick();";
}

/*
	加密
*/
function encryptionDecryption(){
	var beforeStr = document.getElementById("beforeText").innerHTML;

	var encryptionDecryptionSelectEntity = document.getElementById("encryptionDecryptionSelect");
	var index = encryptionDecryptionSelectEntity.selectedIndex;
	var type = encryptionDecryptionSelectEntity[index].value;  //1：加密。2：解密。

	var encryptionDecryptionCompleteUrl = encryptionDecryptionUrl + "?str=" + encodeURIComponent(encodeURIComponent(beforeStr)) + "&type=" + type;
	//ajax请求
	invokeAjax(encryptionDecryptionCompleteUrl, httpMethod, ajaxEncryptionDecryptionSuc, ajaxError);
}

/*
	ajax请求加密解密成功
*/
function ajaxEncryptionDecryptionSuc(data){
	var dataObj = JSON.parse(data);
	var status = dataObj.status;

	if(status != "0"){
		alert("请检查数据。");

		return;
	}

	document.getElementById("afterText").value = decodeURIComponent(dataObj.str);
}

/*
	ajax请求失败
*/
function ajaxError(httpRequestObj, message, exceptionObj){
	alert("错误：" + message);
}

/*
	service改变事件
*/
function serviceChange(){
	//获取参数
	var serviceSelect = document.getElementById("serviceSelect");
	var index = serviceSelect.selectedIndex;
	var serviceName = serviceSelect[index].value;
	var methodSelect = document.getElementById("methodSelect");
	methodSelect.innerHTML = "";
	
	//改变method select中的内容
	if(serviceName == serviceList[0].value){
		for(var i in memberMethodList){
			var option = new Option(memberMethodList[i].text, memberMethodList[i].value);
			methodSelect.add(option);
		}
	}
	if(serviceName == serviceList[1].value){
		for(var i in orderMethodList){
			var option = new Option(orderMethodList[i].text, orderMethodList[i].value);
			methodSelect.add(option);
		}
	}

	methodChange();
}

/*
	method改变事件
*/
function methodChange(){
	//获取参数
	var methodSelectEntity = document.getElementById("methodSelect");
	var index = methodSelectEntity.selectedIndex;
	var methodName = methodSelectEntity[index].value;

	//隐藏所有param参数
	var paramTdDivList = $(".param-td");
	for(var i = 0; i < paramTdDivList.length; i++){
		paramTdDivList[i].style.display = "none";
	}

	//改变param参数
	var showParamTdDiv = $("#" + methodName)[0];
	showParamTdDiv.style.display = "block";

	//清空
    document.getElementById("requestUrlText").value = "";
	document.getElementById("headerDiv").innerHTML = "";
	document.getElementById("bodyTestarea").value = "";

}

/*
	json内容弹出事件
*/
function jsonObjClick(textareaId){
	//把所有的json textarea变为不可见
	$(".text_div textarea").each(function(index, element) {
		element.style.display = "none";
	});

	$("#" + textareaId)[0].style.display = "block";

	$('#alert').fadeIn();
}

/*
	json内容确认
*/
function jsonObjSure(){
	$('#alert').fadeOut();
}