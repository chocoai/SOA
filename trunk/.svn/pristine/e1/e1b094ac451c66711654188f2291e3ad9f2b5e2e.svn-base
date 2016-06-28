var __base_url__ = "";

function $base_url(refer){
	if( !refer )
		return __base_url__;

	var i, base = "", src = refer, scripts = document.getElementsByTagName("script");
	for (i=0; i<scripts.length; i++){
		if (scripts[i].src.match(src)){ 
			base = scripts[i].src.replace(src, "");
			break;
		}
	}
	var index = base.indexOf("js");
	if( index != -1 )
		base = base.substring(0, index);
	__base_url__ = base.replace("//", "/");
	return base;
}

function $import(baseUrl, path){
	document.write("<" + "script src=\"" + baseUrl + path + "\"></" + "script>");
}

var __include_path__ = {};
function $include(path){
	if(__include_path__[path]){
		return;
	}
	var http_request;  
    if (window.XMLHttpRequest) { // Mozilla, Safari,...  
		http_request = new XMLHttpRequest();  
		if (http_request.overrideMimeType) {  
			http_request.overrideMimeType('text/xml');  
		}  
	} else if (window.ActiveXObject) { // IE  
		try {  
			http_request = new ActiveXObject("Msxml2.XMLHTTP");  
		} catch (e) {
			try {  
				http_request = new ActiveXObject("Microsoft.XMLHTTP");  
			} catch (e) {}  
		}  
	}  

	if (!http_request) {    
		return; 
	}   
	http_request.open('GET', path, false);  
	http_request.send(null);

	var oHead = document.getElementsByTagName('head').item(0); 
    var oScript = document.createElement("script"); 
    oScript.type = "text/javascript"; 
	oScript.text = http_request.responseText;
    oHead.appendChild(oScript);
	__include_path__[path] = true;
}

function $()
{
    var elements = new Array();

    for (var i = 0; i < arguments.length; i++)
    {
        var element = arguments[i];
        if (typeof element == 'string')
        {
            if (document.getElementById)
            {
				if( document.getElementById(element) )
					element = document.getElementById(element);
				else {
					element = document.getElementsByName(element);
					if( element && element.length > 0 )
						element = element[0];
				}
				
            }
            else if (document.all)
            {
                element = document.all[element];
            }
        }

        if (arguments.length == 1) 
        {
            return element;
        }

        elements.push(element);
    }

    return elements;
}

var _DOMDocument = ["Msxml2.DOMDocument.6.0", "Msxml2.DOMDocument.5.0", "Msxml2.DOMDocument.4.0", "Msxml2.DOMDocument.3.0", "MSXML2.DOMDocument", "MSXML.DOMDocument", "Microsoft.XMLDOM"];

function newActiveXObject(axarray) {
	var returnValue;  
	for (var i = 0; i < axarray.length; i++) {
		try {
			returnValue = new ActiveXObject(axarray[i]);
			break;
		}
		catch (ex) {
		}
	}
	return returnValue;
};

/**
 * 以JSON格式的字符串为输入输出参数调用JavaScript方法
 * @param functionName JavaScript的方法名，可以有一个.，如"Util.formatDate"
 * @param json_args JSON格式字符串表示的参数，必须是数组类型，如"[0,1]"
 * @return JSON格式的返回数据
 */
function json_invoke(functionName, json_args)
{
	var args = JSON.decode(json_args);
	if( args == null || !(args instanceof Array) )
		args = [];
	
	var ret = null;
	var part = functionName.split(".");
	if( part.length == 1 ){
		var func = eval(functionName);
		if( func == null )
			return null;
		ret = func.apply(null, args)
	}
	else {
		var obj = eval(part[0]);
		if( obj != null && obj[part[1]] != null )
			ret = obj[part[1]].apply(obj, args);
	}
	return JSON.encode(ret);
}

Util = {};

Util.loadXML = function(xml)
{
	var dom;
	if (window.DOMParser) {
		var parser = new DOMParser();
		dom = parser.parseFromString(xml, "text/xml");
		if (!dom.documentElement || dom.documentElement.tagName == "parsererror") {
			var message = dom.documentElement.firstChild.data;
			message += "\n" + dom.documentElement.firstChild.nextSibling.firstChild.data;
			throw message;
		}
		return dom;
	}
	else if (window.ActiveXObject) {
		dom = newActiveXObject(_DOMDocument);
		dom.loadXML(xml);
		// What happens on parse fail with IE?
		if( !dom.documentElement )
			return null;
		return dom;
	}
	else {
		var div = document.createElement('div');
		div.innerHTML = xml;
		return div;
	}
}

/**
 * 获取URL中的属性值
 * @param attrName 属性名
 * @return 属性值
 */
Util.getURLAttribute = function(attrName){
	var url = window.location.search;
	var re = eval("/" + attrName + "=([^&]*)/");
		if (re.test(url)){
			return RegExp.$1;
		}
		else{
			return "";
	}
}
Util.getURLHeader = function(){
	return location.protocol + "//" + location.host;
}

Util.getURL = function(){
	return window.location.search;
}

//=============================================================================
//日期格式化方法
_Util_Date_strZf = function(str, l) { return _Util_Date_strString('0', l - str.length) + str; }
_Util_Date_strString = function(str, l) { var s = '', i = 0; while (i++ < l) { s += str; } return s; }
_Util_Date_numZf = function(num, l) { return _Util_Date_strZf(num.toString(), l); }

Util.formatDate = function(d, f)
{
    if (!d.valueOf())
        return '';

    return f.replace(/(yyyy|yy|mm|dd|hh|nn|ss|a\/p)/gi,
        function($1)
        {
            switch ($1.toLowerCase())
            {
            case 'yyyy': return d.getFullYear();
			case 'yy':   return d.getFullYear().toString().substr(2, 2);
            case 'mm':   return _Util_Date_numZf(d.getMonth() + 1, 2);
            case 'dd':   return _Util_Date_numZf(d.getDate(), 2);
            case 'hh':   return _Util_Date_numZf(((h = d.getHours() % 12) ? h : 12), 2);
            case 'nn':   return _Util_Date_numZf(d.getMinutes(), 2);
            case 'ss':   return _Util_Date_numZf(d.getSeconds(), 2);
            case 'a/p':  return d.getHours() < 12 ? '上午' : '下午';
            }
        }
    );
}

/**
 * 将JavaScript对象转化成XML
 */
Util.toXml = function(obj, name, isArrayItem)
{
	if( name == undefined )
		name = "";
	if( isArrayItem == undefined )
		isArrayItem = false;

	var type = Util.getObjectType(obj);
	if (type == "string") {
		if( isArrayItem )
			return "<item><![CDATA[" + obj + "]]></item>";
		else
			return "<string name=\"" + name + "\"><![CDATA[" + obj + "]]></string>";
	} else if (type == "undefined") {
		if( isArrayItem )
			return "<item></item>";
		else
			return "<undefined/>";
	} else if (type == "int") {
		if( isArrayItem )
			return "<item>" + obj + "</item>";
		else
			return "<int name=\"" + name + "\">" + obj + "</int>";
	} else if (type == "float" ){
		if( isArrayItem )
			return "<item>" + obj + "</item>";
		else
			return "<float name=\"" + name + "\">" + obj + "</float>";
	} else if (type == "null") {
		if( isArrayItem )
			return "<item></item>";
		else
			return "<null name=\"" + name + "\"/>";
	} else if (type == "boolean") {
		if( isArrayItem )
			return "<item>" + obj ? "true" : "false" + "</item>";
		else
			return obj ? "<boolean name=\"" + name + "\">true</boolean>" : "<boolean name=\"" + name + "\">false</boolean>";
	} else if (type == "date" ) {
		if( isArrayItem )
			return "<item>" + obj.toString() + "</item>";
		else
			return "<datetime name=\"" + name + "\">" + obj.toString() + "</datetime>";
	} else if (type == "array" ) {
		return Util._arrayToXml(obj, name, isArrayItem);
	} else if (type == "object") {
		return Util._objectToXml(obj, name, isArrayItem);
	} else {
		if( isArrayItem )
			return "<item><![CDATA[" + obj + "]]></item>";
		else
			return "<string name=\"" + name + "\"><![CDATA[" + obj + "]]></string>";
	}
}
Util.codes2string = function(codes){
	var str = "";
	for( var i = 0; i < codes.length; i++ ){
		str += String.fromCharCode(codes[i])
	}
	return str;
}
Util.getObjectType = function(obj)
{
	var type = typeof(obj);
	if (type == "string") {
		return "string"
	} else if (type == "undefined") {
		return "undefined";
	} else if (type == "number") {
		if( Math.floor(obj) == obj )
			return "int";
		else
			return "float";
	} else if (obj == null) {
		return "null";
	} else if (type == "boolean") {
		return "boolean";
	} else if (obj instanceof Date) {
		return "date";
	} else if (obj instanceof Array) {
		return "array";
	} else if (type == "object") {
		return "object";
	} else {
		return "string";
	}
}
Util._arrayToXml = function(array, name, isArrayItem)
{
	if( isArrayItem == undefined )
		isArrayItem = false;

	if( array.length == 0 ){
		if( isArrayItem )
			return "<item type=\"string\"></item>";
		else
			return "<array name=\"" + name + "\" type=\"string\"></array>";
	}
	var xml = "";
	if( isArrayItem )
		xml += "<item type=\"" + Util.getObjectType(array[0]) + "\">";
	else
		xml += "<array name=\"" + name + "\" type=\"" + Util.getObjectType(array[0]) + "\">";
	for( var i = 0; i < array.length; i++ ){
		xml += Util.toXml(array[i], "", true);
	}
	if( isArrayItem )
		xml += "</item>";
	else
		xml += "</array>";
	return xml;
}

Util._objectToXml = function(obj, name, isArrayItem)
{
	if( isArrayItem == undefined )
		isArrayItem = false;

	var xml = "";
	if( isArrayItem )
		xml += "<item>";
	else
		xml += "<object name=\"" + name + "\">";
	for (var prop in obj) {
		xml += Util.toXml(obj[prop], prop);
	}
	if( isArrayItem )
		xml += "</item>";
	else
		xml += "</object>";
	return xml;
}
Util.createUUID = function() {
  var s = [], itoh = '0123456789ABCDEF';

  // Make array of random hex digits. The UUID only has 32 digits in it, but we
  // allocate an extra items to make room for the '-'s we'll be inserting.
  for (var i = 0; i < 36; i++) s[i] = Math.floor(Math.random()*0x10);

  // Conform to RFC-4122, section 4.4
  s[14] = 4;  // Set 4 high bits of time_high field to version
  s[19] = (s[19] & 0x3) | 0x8;  // Specify 2 high bits of clock sequence

  // Convert to hex chars
  for (var i = 0; i < 36; i++) s[i] = itoh.charAt(s[i]);

  // Insert '-'s
  s[8] = s[13] = s[18] = s[23] = '-';

  return s.join('');
}
Util.clone = function(obj) {
  var newObj = (obj instanceof Array) ? [] : {};
  for (i in obj) {
    if (i == 'clone') continue;
    if (obj[i] && typeof obj[i] == "object") {
      newObj[i] = obj[i].clone();
    } else newObj[i] = obj[i]
  } return newObj;
};

(function() {
  // Private array of chars to use
  var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');

  Math.uuid = function (len, radix) {
    var chars = CHARS, uuid = [], i;
    radix = radix || chars.length;

    if (len) {
      // Compact form
      for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    } else {
      // rfc4122, version 4 form
      var r;

      // rfc4122 requires these characters
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
      uuid[14] = '4';

      // Fill in random data.  At i==19 set the high bits of clock sequence as
      // per rfc4122, sec. 4.1.5
      for (i = 0; i < 36; i++) {
        if (!uuid[i]) {
          r = 0 | Math.random()*16;
          uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
        }
      }
    }

    return uuid.join('');
  };

  // A more performant, but slightly bulkier, RFC4122v4 solution.  We boost performance
  // by minimizing calls to random()
  Math.uuidFast = function() {
    var chars = CHARS, uuid = new Array(36), rnd=0, r;
    for (var i = 0; i < 36; i++) {
      if (i==8 || i==13 ||  i==18 || i==23) {
        uuid[i] = '-';
      } else if (i==14) {
        uuid[i] = '4';
      } else {
        if (rnd <= 0x02) rnd = 0x2000000 + (Math.random()*0x1000000)|0;
        r = rnd & 0xf;
        rnd = rnd >> 4;
        uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
      }
    }
    return uuid.join('');
  };

  // A more compact, but less performant, RFC4122v4 solution:
  Math.uuidCompact = function() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
      return v.toString(16);
    });
  };
})();

var XMLUtil = {};
/**
 * 将XML对象转换成JavaScript对象，在每个对象中xmlName表示XML元素的节点名称，text表示XML元素
 * 的内容。
 * @param xml XML DOM对象
 * @param childrenName 子元素的属性名，如果指定该值(例如children)，则子元素通过转换成的对象
 * 通过该属性获取，如obj.children存放子元素列表。
 */
XMLUtil.toObject = function(xml, childrenName){
	if( xml == null )
		return null;
	var obj = {};
	var attr, n, v;
	if( xml.attributes ){
		for( var i = 0; i < xml.attributes.length; i++ ){
			attr = xml.attributes.item(i);
			n = attr.nodeName;
			v = attr.nodeValue;
			obj[n] = v;
		}
	}
	obj.text = xml.text;
	obj.xmlName = xml.nodeName;
	
	var child, childObj;
	if( xml.childNodes ){
		for( var i = 0; i < xml.childNodes.length; i++ ){
			child = xml.childNodes.item(i);
			if( child.nodeName == "#text" )
				continue;
			childObj = xmlToObject(child, childrenName);
			if( childrenName ){
				if( obj[childrenName] == null )
					obj[childrenName] = [];
				obj[childrenName].push(childObj);
			}
			else {
				if( obj[child.nodeName] == null )
					obj[child.nodeName] = childObj;
				else {
					obj[child.nodeName] = [obj[child.nodeName]];
					obj[child.nodeName].push(childObj);
				}
			}
		}
	}
	return obj;
}

/**
 * 以指定小数精度的方式转换数字
 * @param num 要转换的数字
 * @param scale 小数精度
 * @return 转换后的数字
 */
Number.toScale = function(num, scale){ 
	var len = 0; 
	try{len = num.toString().split(".")[0].length}catch(e){} 
	return Number(num).toPrecision(len + scale);
}

var ALink = {};
ALink.toString = function(target, form, param){
	if( param )
		return "alink://" + target + "/" + form + "?" + JSON.stringify(param);
	else
		return "alink://" + target + "/" + form + "?{}";
}

/*--------DateUtil-------------------------------*/
var DateUtil = {};
//日期格式化方法
_Util_Date_strZf = function(str, l) { return _Util_Date_strString('0', l - str.length) + str; };
_Util_Date_strString = function(str, l) { var s = '', i = 0; while (i++ < l) { s += str; } return s; };
_Util_Date_numZf = function(num, l) { return _Util_Date_strZf(num.toString(), l); };

DateUtil.format = function(d, f)
{
    if (!d.valueOf())
        return '';

    return f.replace(/(yyyy|YYYY|yy|YY|MM|mm|MI|dd|DD|HH|hh|ss|SS|E|a\/p)/gi,
        function($1)
        {
            switch ($1)
            {
            case 'yyyy': 
            case 'YYYY':
            	return _Util_Date_numZf(d.getFullYear(), 4);
			case 'yy':
			case 'YY':
				return _Util_Date_numZf(d.getFullYear(), 4).substr(2, 2);
            case 'MM':   
            	return _Util_Date_numZf(d.getMonth() + 1, 2);
            case 'dd':
            case 'DD':
            	return _Util_Date_numZf(d.getDate(), 2);
            case 'E':   
            	return "日一二三四五六"[d.getDay()];
			case 'HH':   
				return _Util_Date_numZf(d.getHours(), 2);
            case 'hh':   
            	return _Util_Date_numZf(((h = d.getHours() % 12) ? h : 12), 2);
            case 'mm':
            case 'MI':
            	return _Util_Date_numZf(d.getMinutes(), 2);
            case 'ss':
            case 'SS':
            	return _Util_Date_numZf(d.getSeconds(), 2);
            case 'a/p':  
            	return d.getHours() < 12 ? '上午' : '下午';
            }
        }
    );
};
DateUtil.parse = function(s, f)
{
	var patt = new RegExp("yyyy|YYYY|yy|YY|MM|mm|MI|dd|DD|HH|hh|ss|SS|a\/p","gi");
	var result;
	var yyyy = 0, MM = 0, dd = 0, HH = 0, mm = 0, ss = 0, ap = 0;
	while ((result = patt.exec(f)) != null){
		switch(result[0])
		{
		case 'yyyy': 
		case 'YYYY':
			yyyy = parseInt(s.substr(result.index, 4)); break;
		case 'yy':
		case 'YY':
			yyyy = parseInt('20' + s.substr(result.index, 2)); break;
		case 'MM':
			MM = parseInt(s.substr(result.index, 2)); break;
		case 'dd':
		case 'DD':
			dd = parseInt(s.substr(result.index, 2)); break;
		case 'HH':   
			HH = parseInt(s.substr(result.index, 2)); break;
		case 'hh':   
			HH = ap + parseInt(s.substr(result.index, 2)); break;
		case 'mm':   
		case 'MI':
			mm = parseInt(s.substr(result.index, 2)); break;
		case 'ss':
		case 'SS':
			ss = parseInt(s.substr(result.index, 2)); break;
		case 'a/p':  
			if( s.substr(result.index, 2) == '下午' ){
				ap = 12;
				HH += 12;
			}
			break;
		}
	}
	return new Date(yyyy, MM - 1, dd, HH, mm, ss);
};