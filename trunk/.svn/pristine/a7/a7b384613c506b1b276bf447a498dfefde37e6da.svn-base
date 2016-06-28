ws.Dialog = function(sTitle){
	this.sTitle   = sTitle || "对话框";	//对话框标题内容
	this.elDialog = null;				//对话框元素
	this.elTitle  = null;				//对话框中的标题元素
	this.elClient = null;				//对话框中的客户区元素
	this.minized  = false;				//表示对话框是否最小化
	this.width    = "100px";			//对话框宽度
	this.height   = "100px";			//对话框高度
}

/**
 * 创建对话框
 * @param op_isModelDialog 是否是模态对话框(可选，默认为true)
 */
ws.Dialog.prototype.create = function(op_isModelDialog){
    var el, isModelDialog = op_isModelDialog || true;
	var id = ws.generateId(); 
	var dlgId   = "dialog" + id;
	var titleId = "title"  + id;
	var clientId= "client" + id;
	var clsbtnId= "clsbtn" + id;
	var minbtnId= "minbtn" + id;
	var html = "", dlgEl;

	dlgEl = ws.createElement("<div id='" + dlgId + "' class='ws_dialog' style='left:0px;top:0px;visibility:hidden'></div>", document.body);

	html += "<div class='ws_movehandle' handlefor='"+ dlgId + "' id='" + titleId + "' nowrap style='right:0;'>";
	html += this.sTitle + "</div>";
	html += "<div class='ws_btnarea'>";
	html += "<div id='" + minbtnId + "' class='ws_minmize_button' onclick='ws.Dialog.on_minbtn_click(this)' ";
	html += "onmousedown='ws.Dialog.on_minbtn_mousedown(this)' onmouseup='ws.Dialog.on_minbtn_mouseup(this)' onmouseout='ws.Dialog.on_minbtn_mouseout(this)' ";
	html += "onmouseover='ws.Dialog.on_minbtn_mouseover(this)' >";
	html += "<img src='' width='17' height='17' border='0' ></div>";
	html += "<span style='width:2px;float:left'></span>";
	html += "<div id='" + clsbtnId + "' class='ws_close_button' onmousedown='ws.Dialog.on_clsbtn_mousedown(this)' ";
	html += "onmouseup='ws.Dialog.on_clsbtn_mouseup(this)' onmouseout='ws.Dialog.on_clsbtn_mouseout(this)' "
	html += "onmouseover='ws.Dialog.on_clsbtn_mouseover(this)' >";
	html += "<img src='' width='17' height='17' border='0' ></div>";
	html += "</div>";
	html += "<div class='ws_dlgclient' id='" + clientId + "'></div>";
	
	dlgEl.innerHTML = html;

	this.elDialog = document.getElementById(dlgId);
	this.elTitle  = document.getElementById(titleId);
	this.elClient = document.getElementById(clientId);
	this.elBtnClose   = document.getElementById(clsbtnId);
	this.elBtnMinmize = document.getElementById(minbtnId);

	this.elBtnClose.children[0].src   = eval(this.elBtnClose.currentStyle.button_img_u);
	this.elBtnMinmize.children[0].src = eval(this.elBtnMinmize.currentStyle.button_img_u);

	this.elDialog.theDialog = this;
	this.elTitle.theDialog  = this;
	this.elClient.theDialog = this;
	this.isModelDialog	    = isModelDialog;
	this.showCover			= ws.Dialog.showCover;
	this.setSize(this.width, this.height);
	this.cover = null;		//当对模态话框显示时用于遮挡用户操作的元素
}

//根据Dialog的子元素取出Dialog元素
ws.Dialog.getDialogElement = function(el){
	temp = el;
	while ((temp != null) && (temp.tagName != "BODY")) {
		if (ws.hasClass(temp, "ws_dialog") == true){
			return temp;
		}
		temp = temp.parentElement;
	}
	return null;
}

//---BEGING 对话框关闭按钮事件处理
ws.Dialog.on_clsbtn_mousedown = function(oBtn){
	oBtn.children[0].src = eval(oBtn.currentStyle.button_img_d);
}
ws.Dialog.on_clsbtn_mouseover = function(oBtn){
	oBtn.children[0].src = eval(oBtn.currentStyle.button_img_o);
}
ws.Dialog.on_clsbtn_mouseup = function(oBtn){
	oBtn.children[0].src = eval(oBtn.currentStyle.button_img_u);
	var dlg = ws.Dialog.getDialogElement(oBtn);
	if( dlg )
		dlg.theDialog.show(false);
}
ws.Dialog.on_clsbtn_mouseout = function(oBtn){
	oBtn.children[0].src = eval(oBtn.currentStyle.button_img_u);
}
//---END

//---BEGING 对话框最小化按钮事件处理
ws.Dialog.on_minbtn_click = function(oBtn){
	var dlg = ws.Dialog.getDialogElement(oBtn);
	if( dlg ){
		if( dlg.theDialog.minized == false ){
			dlg.theDialog.elClient.style.display = "none";
			dlg.theDialog.elDialog.style.height  = "0px";
			dlg.theDialog.minized = true;
		} else {
			dlg.theDialog.elClient.style.display = "inline";
			dlg.theDialog.elDialog.style.height  = dlg.theDialog.height;
			dlg.theDialog.minized = false;
		}
	}
}
ws.Dialog.on_minbtn_mousedown = function(oBtn){
	var dlg = ws.Dialog.getDialogElement(oBtn);
	if( dlg.theDialog.minized == false )
		oBtn.children[0].src = eval(oBtn.currentStyle.button_img_di);
	else oBtn.children[0].src = eval(oBtn.currentStyle.button_img_da);
}
ws.Dialog.on_minbtn_mouseover = function(oBtn){
	var dlg = ws.Dialog.getDialogElement(oBtn);
	if( dlg.theDialog.minized == false )
		oBtn.children[0].src = eval(oBtn.currentStyle.button_img_oi);
	else oBtn.children[0].src = eval(oBtn.currentStyle.button_img_oa);
}
ws.Dialog.on_minbtn_mouseup = function(oBtn){
	oBtn.children[0].src = eval(oBtn.currentStyle.button_img_u);
}
ws.Dialog.on_minbtn_mouseout = function(oBtn){
	oBtn.children[0].src = eval(oBtn.currentStyle.button_img_u);
}
//---END

//设置对话框标题
ws.Dialog.prototype.setTitle = function(sTitle){
	this.sTitle = sTitle;
	if( this.elTitle )
		this.elTitle.innerHTML = sTitle;
}
//设置对话框大小
ws.Dialog.prototype.setSize = function(width, height){
	var rect = this.elDialog.getBoundingClientRect();
	if( width < rect.right - rect.left )
		width = rect.right - rect.left;

	this.width  = width;
	this.height = height;
	this.elDialog.style.width  = width;
	this.elDialog.style.height = height;
	this.elClient.style.width  = parseInt(width) - 4;
	this.elClient.style.height = height;
}
//自动调整窗口大小
ws.Dialog.prototype.autoSize = function(){
	var rect = this.elDialog.getBoundingClientRect();
	this.width  = rect.right - rect.left;
	this.height = rect.bottom - rect.top;
	this.elDialog.style.width  = this.width;
	this.elDialog.style.height = this.height;
	this.elClient.style.width  = parseInt(this.width) - 4;
	this.elClient.style.height = this.height;
}
//显示或隐藏对话框
ws.Dialog.prototype.show = function(showMode, effect){
	if( effect === null || effect === undefined )
		effect = ws.config.dialog.effect;

	if( showMode == true ){
		if( effect === "fade" )
			ws.effect.fade(this.elDialog, showMode, 2, 25, ws.Dialog.endShow);
		else 
			this.elDialog.style.visibility = "visible";
		if( this.isModelDialog == true )
			this.showCover(true, "#C0C0C0", 20);
		ws.makeTopmost(this.elDialog);
	}
	else {
		if( effect === "fade" )
			ws.effect.fade(this.elDialog, showMode, 2, 25, ws.Dialog.endShow);
		else 
			this.elDialog.style.visibility = "hidden";

		if( this.isModelDialog == true )
			this.showCover(false);
	}
}
//设置对话框内容
ws.Dialog.prototype.setContent = function(content){
	if( typeof( content ) == "object" ){
		this.elClient.innerHTML = "";
		this.elClient.appendChild(content);
	}
	else {
		var temp = document.getElementById(content);
		if( temp ){
			this.elClient.innerHTML = "";
			oChild = this.elClient.children(0);
			if( oChild )
				this.elClient.removeChild(oChild);
			this.elClient.appendChild(temp);
		} 
		else 
			this.elClient.innerHTML = content;
	}
}

ws.Dialog.prototype.setCenter = function(){
	this.elDialog.style.left = document.body.clientWidth / 2 - ws.toInt(this.width) / 2;
	var top  = document.body.clientHeight / 2 - ws.toInt(this.height) / 2;
	if( top > 160 )
		this.elDialog.style.top = 160;
	else
		this.elDialog.style.top = top;
}

ws.Dialog.prototype.setPosition = function(x, y){
	this.elDialog.style.left = x;
	this.elDialog.style.top  = y;
}

ws.Dialog.endShow = function(elDialog){
	elDialog.style.filter = "progid:DXImageTransform.Microsoft.Shadow(strength=4,color=#808080,enabled=true,direction=135)";
}

ws.Dialog.showCover = function(show, op_color, op_alpha){
	var alpha = op_alpha || 20;
	var color = op_color || "#C0C0C0";
	if( this.cover == null ){
		this.cover = ws.createElement('<div style="position:absolute;visibility:hidden;background-color:#C0C0C0;filter:Alpha(opacity=20);">', document.body);
		this.cover.style.pixelLeft = 0;
		this.cover.style.pixelTop  = 0;
	}
	this.cover.style.filter			 = "Alpha(opacity=" + alpha + ")";
	this.cover.style.backgroundColor = color;

	if( show == true ){
		this.cover.style.pixelWidth  = document.body.scrollWidth;
		if( document.body.scrollHeight < document.body.clientHeight )
			this.cover.style.pixelHeight = document.body.clientHeight;
		else
			this.cover.style.pixelHeight = document.body.scrollHeight;

		ws.makeTopmost(this.cover);
		this.cover.style.visibility = "visible";
	}
	else
		this.cover.style.visibility = "hidden";
}

