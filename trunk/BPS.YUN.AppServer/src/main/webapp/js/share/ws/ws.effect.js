
ws.effect = {};

//========Begin fade ==========================================================
ws.effect.fadeSteps = 4;				// Number of steps to loop
ws.effect.fademsec = 25;				// The time between each step (note that most computer have problem

ws.effect.fadeArray = new Array();	    // Needed to keep track of wich elements are animating

ws.effect.fade = function (el, fadeIn, steps, msec, onend) {
	if (steps == null) steps = ws.effect.fadeSteps;
	if (msec == null)  msec  = ws.effect.fademsec;
	
	if (el.fadeIndex == null)
		el.fadeIndex = ws.effect.fadeArray.length;
	ws.effect.fadeArray[el.fadeIndex] = el;
	
	if (el.fadeStepNumber == null) {
		if (el.style.visibility == "hidden")
			el.fadeStepNumber = 0;
		else
			el.fadeStepNumber = steps;
		if (fadeIn)
			el.style.filter = "Alpha(Opacity=0)";
		else
			el.style.filter = "Alpha(Opacity=100)";
	}
	window.setTimeout("ws.effect.repeatFade(" + fadeIn + "," + el.fadeIndex + "," + steps + "," + msec + "," + onend + ")", msec);
}

//////////////////////////////////////////////////////////////////////////////////////
//  Used to iterate the fading

ws.effect.repeatFade = function (fadeIn, index, steps, msec, onend) {	
	el = ws.effect.fadeArray[index];
	
	c = el.fadeStepNumber;
	if (el.fadeTimer != null)
		window.clearTimeout(el.fadeTimer);
	if ((c == 0) && (!fadeIn)) {			//Done fading out!
		el.style.visibility = "hidden";		// If the platform doesn't support filter it will hide anyway
		if( typeof(onend) == "function" )
			onend(el);
		return;
	}
	else if ((c==steps) && (fadeIn)) {	//Done fading in!
		el.style.filter = "";
		el.style.visibility = "visible";
		if( typeof(onend) == "function" )
			onend(el);
		return;
	}
	else {
		(fadeIn) ? 	c++ : c--;
		el.style.visibility = "visible";
		el.style.filter = "Alpha(Opacity=" + 100*c/steps + ")";

		el.fadeStepNumber = c;
		el.fadeTimer = window.setTimeout("ws.effect.repeatFade(" + fadeIn + "," + index + "," + steps + "," + msec + "," + onend + ")", msec);
	}
}

//========End fade ============================================================

//========Begin swipe =========================================================
ws.effect.swipeSteps = 4;
ws.effect.swipemsec  = 25;

ws.effect.swipeArray = new Array();	// Needed to keep track of wich elements are animating
//////////////////////////////////////////////////////////////////////////////////////////
// the dir parameter is the direction read from the NumPad on your keyboard

ws.effect.swipe = function(el, dir, steps, msec) {

	if (steps == null) steps = ws.effect.swipeSteps;
	if (msec == null) msec   = ws.effect.swipemsec;

	if (el.swipeIndex == null)
		el.swipeIndex = ws.effect.swipeArray.length;
	if (el.swipeTimer != null)
		window.clearTimeout(el.swipeTimer);
		
	ws.effect.swipeArray[el.swipeIndex] = el;

	el.style.clip = "rect(-99999, 99999, 99999, -99999)";

	if (el.swipeCounter == null) {		// No animation yet!
		el.orgLeft   = el.offsetLeft;
		el.orgTop    = el.offsetTop;
		el.orgWidth  = el.offsetWidth;
		el.orgHeight = el.offsetHeight;
	}
	else if (el.swipeCounter == 0) {	// The Animation has stopped! It's now safe to update the position.
		el.orgLeft   = el.offsetLeft;
		el.orgTop    = el.offsetTop;
		el.orgWidth  = el.offsetWidth;
		el.orgHeight = el.offsetHeight;
	}
	
	el.style.left = el.orgLeft;
	el.style.top  = el.orgTop;
	
	el.swipeCounter = steps;
	el.style.clip = "rect(0,0,0,0)";
			
	window.setTimeout("ws.effect.repeatSwipe(" + dir + "," + el.swipeIndex + "," + steps + "," + msec + ")", msec);
}


ws.effect.repeatSwipe = function(dir, index, steps, msec) {
	el = ws.effect.swipeArray[index];
	var left   = el.orgLeft;
	var top    = el.orgTop;
	var width  = el.orgWidth;
	var height = el.orgHeight;
	
	if (el.swipeCounter == 0) {
		el.style.clip = "rect(-99999, 99999, 99999, -99999)";
		return;
	}
	else {
		el.swipeCounter--;
		el.style.visibility = "visible";
		switch (dir) {
			case 2:		//down (see the numpad)
				el.style.clip = "rect(" + height*el.swipeCounter/steps + "," + width + "," + height + "," + 0 + ")";
				el.style.top  = top - height*el.swipeCounter/steps;
				break;
			case 8:
				el.style.clip = "rect(" + 0 + "," + width + "," + height*(steps-el.swipeCounter)/steps + "," + 0 + ")";
				el.style.top  = top + height*el.swipeCounter/steps;
				break;
			case 6:
				el.style.clip = "rect(" + 0 + "," + width + "," + height + "," + width*(el.swipeCounter)/steps + ")";
				el.style.left  = left - width*el.swipeCounter/steps;
				break;
			case 4:
				el.style.clip = "rect(" + 0 + "," + width*(swipeSteps - el.swipeCounter)/steps + "," + height + "," + 0 + ")";
				el.style.left  = left + width*el.swipeCounter/steps;
				break;
		}
		
		el.swipeTimer = window.setTimeout("ws.effect.repeatSwipe(" + dir + "," + index + "," + steps + "," + msec + ")", msec);
	}
}

ws.effect.hideSwipe = function(el) {
	window.clearTimeout(el.swipeTimer);
	el.style.visibility = "hidden";
	el.style.clip = "rect(-99999, 99999, 99999, -99999)";
	el.swipeCounter = 0;
}