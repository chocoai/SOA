ws.slider = {};
ws.slider.make = function(standin){
	var slider = null;
	if( typeof(standin) == "string" )
		slider = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		slider = standin;
	if( slider.type != "ws_slider" )
		return null;
	var orientation = slider.orientation;
	if( orientation != "vertical" )
		orientation = "horizontal";
	slider.className = "ws_slider " + orientation;
	slider.orientation = orientation;
	
	var min = slider.min;
	var max = slider.max;
	if( min == undefined || min == null )
		min = 0;
	min = ws.toInt(min);
	if( max == undefined || max == null )
		max = 100;
	max = ws.toInt(max);

	slider.minValue = min;
	slider.maxValue = max;

	var line   = ws.createElement('<div class="line">', slider);
	var handle = ws.createElement('<div class="ws_movable handle">', slider);
	line.unselectable = "on";
	
	handle.ownSlider = slider;
	slider.handle = handle;
	slider.line   = line;
	
	var centerX, centerY;
	centerX = slider.offsetWidth / 2;
	centerY = slider.offsetHeight/ 2;
	if( orientation == "horizontal" ){
		ws.createElement('<div style="width:100%">', line);
		handle.limitY = true;
		if( slider.offsetWidth <= handle.offsetWidth )
			slider.style.pixelWidth = handle.offsetWidth + 10;
		line.style.pixelTop   = centerY - line.offsetHeight / 2;
		line.style.pixelWidth = slider.offsetWidth - handle.offsetWidth;
		line.style.pixelLeft  = handle.offsetWidth / 2;
		handle.style.pixelTop = centerY - handle.offsetHeight / 2;
		handle.style.pixelLeft= 0;
		slider.minX = 0;
		slider.maxX = line.style.pixelWidth;
		slider.rate = (max - min) / line.style.pixelWidth;
		slider.value= min;
	}
	else {
		ws.createElement('<div style="height:100%">', line);
		handle.limitX = true;
		if( slider.offsetHeight <= handle.offsetHeight )
			slider.offsetHeight = handle.offsetHeight + 10;
		line.style.pixelLeft   = centerX - line.offsetWidth / 2;
		line.style.pixelHeight = slider.offsetHeight - handle.offsetHeight;
		line.style.pixelTop    = handle.offsetHeight / 2;
		handle.style.pixelLeft = centerX - handle.offsetWidth / 2;
		handle.style.pixelTop  = 0;
		slider.minY = 0;
		slider.maxY = line.style.pixelHeight;
		slider.rate = (max - min) / line.style.pixelHeight;
		slider.value= min;
	}
	handle.innerHTML = ws.theme.imageHTML(eval(handle.currentStyle.icon), "border:0");
	handle.onMoving  = ws.slider.onHandleMoving;
	
	ws.addEvent(slider, "mousedown", function(){ws.slider.onmousedown(slider);});
	ws.addEvent(slider, "keydown",   function(){ws.slider.onkeydown(slider);});
	slider.getValue = ws.slider.getValue;
	slider.setValue = ws.slider.setValue;

	return slider;
}
ws.slider.getValue = function(){
	return this.value;
}
ws.slider.setValue = function(value){
	if( value < this.minValue )
		value = this.minValue;
	if( value > this.maxValue )
		value = this.maxValue;
	var pos;
	pos = (value - this.minValue) / this.rate;
	if( this.orientation == "horizontal" )
		this.handle.style.pixelLeft = pos;
	else 
		this.handle.style.pixelTop  = pos;
	this.value = value;
}
ws.slider.onHandleMoving = function(){
	var slider = this.ownSlider;
	var oldValue = slider.value;
	var pos;
	if( slider.orientation == "horizontal" ){
		if( this.style.pixelLeft < slider.minX )
			this.style.pixelLeft = slider.minX;
		if( this.style.pixelLeft > slider.maxX )
			this.style.pixelLeft = slider.maxX;
		slider.value = (this.style.pixelLeft * slider.rate + slider.minValue).toFixed();
		pos = (slider.value - slider.minValue) / slider.rate;
		this.style.pixelLeft = pos;
	}
	else {
		if( this.style.pixelTop < slider.minY )
			this.style.pixelTop = slider.minY;
		if( this.style.pixelTop > slider.maxY )
			this.style.pixelTop = slider.maxY;
		slider.value = (this.style.pixelTop * slider.rate + slider.minValue).toFixed();
		pos = (slider.value - slider.minValue) / slider.rate;
		this.style.pixelTop  = pos;
	}
	if( oldValue != slider.value && typeof(slider.onValueChanged) == "function" )
		slider.onValueChanged();
}
ws.slider.onmousedown = function(slider){
	if( slider.orientation == "horizontal" )
		slider.handle.style.pixelLeft = event.x - slider.handle.offsetWidth / 2;
	else 
		slider.handle.style.pixelTop  = event.y - slider.handle.offsetHeight / 2;
	slider.handle.onMoving();
}
ws.slider.onkeydown = function(slider){
	var value = slider.getValue();
	if( slider.orientation == "horizontal" ){
		switch(event.keyCode){
		case 39://right
			value++;
			break;
		case 37://left
			value--;
			break;
		}
	}
	else {
		switch(event.keyCode){
		case 40://down
			value++;
			break;
		case 38://up
			value--;
			break;
		}
	}
	slider.setValue(value);
}
