
ws.Timer = function(nPauseTime) {
	this._pauseTime = typeof nPauseTime == "undefined" ? 1000 : nPauseTime;
	this._timer = null;
	this._isStarted = false;
}

ws.Timer.prototype.start = function () {
	if (this.isStarted())
		this.stop();
	var oThis = this;
	this._timer = window.setTimeout(function () {
		if (typeof oThis.ontimer == "function")
			oThis.ontimer();
	}, this._pauseTime);
	this._isStarted = false;
};

ws.Timer.prototype.stop = function () {
	if (this._timer != null)
		window.clearTimeout(this._timer);
	this._isStarted = false;
};

ws.Timer.prototype.isStarted = function () {
	return this._isStarted;
};

ws.Timer.prototype.getPauseTime = function () {
	return this._pauseTime;
};

ws.Timer.prototype.setPauseTime = function (nPauseTime) {
	this._pauseTime = nPauseTime;
};

//=============================================================================
ws.Range = function() {
	this._value = 0;
	this._minimum = 0;
	this._maximum = 100;
	this._extent = 0;
	
	this._isChanging = false;
}

ws.Range.prototype.setValue = function (value) {
	value = parseInt(value);
	if (isNaN(value)) return;
	if (this._value != value) {
		if (value + this._extent > this._maximum)
			this._value = this._maximum - this._extent;
		else if (value < this._minimum)
			this._value = this._minimum;
		else
			this._value = value;
		if (!this._isChanging && typeof this.onchange == "function")
			 this.onchange();
	}
};

ws.Range.prototype.getValue = function () {
	return this._value;
};

ws.Range.prototype.setExtent = function (extent) {
	if (this._extent != extent) {
		if (extent < 0)
			this._extent = 0;
		else if (this._value + extent > this._maximum)
			this._extent = this._maximum - this._value;
		else
			this._extent = extent;
		if (!this._isChanging && typeof this.onchange == "function")
			this.onchange();
	}
};

ws.Range.prototype.getExtent = function () {
	return this._extent;
};

ws.Range.prototype.setMinimum = function (minimum) {
	if (this._minimum != minimum) {
		var oldIsChanging = this._isChanging;
		this._isChanging = true;

		this._minimum = minimum;
		
		if (minimum > this._value)
			this.setValue(minimum);
		if (minimum > this._maximum) {
			this._extent = 0;
			this.setMaximum(minimum);
			this.setValue(minimum)
		}
		if (minimum + this._extent > this._maximum)
			this._extent = this._maximum - this._minimum;

		this._isChanging = oldIsChanging;
		if (!this._isChanging && typeof this.onchange == "function")
			this.onchange();
	}
};

ws.Range.prototype.getMinimum = function () {
	return this._minimum;
};

ws.Range.prototype.setMaximum = function (maximum) {
	if (this._maximum != maximum) {
		var oldIsChanging = this._isChanging;
		this._isChanging = true;

		this._maximum = maximum;		
		
		if (maximum < this._value)
			this.setValue(maximum - this._extent);
		if (maximum < this._minimum) {
			this._extent = 0;
			this.setMinimum(maximum);
			this.setValue(this._maximum);
		}		
		if (maximum < this._minimum + this._extent)
			this._extent = this._maximum - this._minimum;
		if (maximum < this._value + this._extent)
			this._extent = this._maximum - this._value;
		
		this._isChanging = oldIsChanging;
		if (!this._isChanging && typeof this.onchange == "function")
			this.onchange();
	}
};

ws.Range.prototype.getMaximum = function () {
	return this._maximum;
};

//=============================================================================
//日期格式化方法
ws.Date = {};
ws.Date.strZf = function(str, l) { return ws.Date.strString('0', l - str.length) + str; }
ws.Date.strString = function(str, l) { var s = '', i = 0; while (i++ < l) { s += str; } return s; }
ws.Date.numZf = function(num, l) { return ws.Date.strZf(num.toString(), l); }

ws.Date.format = function(d, f)
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
            case 'mm':   return ws.Date.numZf(d.getMonth() + 1, 2);
            case 'dd':   return ws.Date.numZf(d.getDate(), 2);
            case 'hh':   return ws.Date.numZf(((h = d.getHours() % 12) ? h : 12), 2);
            case 'nn':   return ws.Date.numZf(d.getMinutes(), 2);
            case 'ss':   return ws.Date.numZf(d.getSeconds(), 2);
            case 'a/p':  return d.getHours() < 12 ? '上午' : '下午';
            }
        }
    );
}

//=============================================================================
//cookie操作方法
ws.cookie = new Object();
ws.initCookie = function(){
	var aCookie = document.cookie.split("; ");
	for (var i=0; i < aCookie.length; i++)
	{
		var aCrumb = aCookie[i].split("=");
		ws.cookie[aCrumb[0]] = unescape(aCrumb[1]);
	}
}
ws.getCookie = function(name){
	return ws.cookie[name];
}
ws.setCookie = function(name, value){
	ws.cookie[name] = value;
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
ws.delCookie = function(name){
	document.cookie = name + "=null; expires=Fri, 31 Dec 1099 23:59:59 GMT;";
}

ws.initCookie();