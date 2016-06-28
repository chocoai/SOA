ws.layout = {};

ws.layout.makeThreeColumn = function(parent, op_lclass, op_cclass, op_rclass){
	var lclass = op_lclass || "", cclass = op_cclass || "", rclass = op_rclass || "";
	var oParent = null;
	if( typeof(parent) == "string" )
		oParent = $(parent);
	else if ( typeof(parent) == "object" )
		oParent = parent;
	if( oParent == null || oParent == undefined )
		return null;
	var ldiv, cdiv, rdiv;
	ldiv = ws.createElement('div', oParent);
	ldiv.className = lclass;
	ldiv.style.cssText = "height:100%;float:left;";

	rdiv = ws.createElement('div', oParent);
	rdiv.className = rclass;
	rdiv.style.cssText = "height:100%;float:right;";

	cdiv = ws.createElement('div', oParent);
	cdiv.className = cclass;
	cdiv.style.cssText = "height:100%;white-space:nowrap;";

	if( ws.isIE ){
		cdiv.style.position   = "absolute";
		cdiv.style.margin	  = "0px";
		cdiv.style.pixelWidth = oParent.offsetWidth - rdiv.offsetWidth - ldiv.offsetWidth;
	}
	else {
		cdiv.style.margin = "0px " + ldiv.offsetWidth + "px 0px " + rdiv.offsetWidth + "px";
	}
	oParent.leftPanel   = ldiv;
	oParent.middlePanel = cdiv;
	oParent.rightPanel  = rdiv;

	return oParent;
}
