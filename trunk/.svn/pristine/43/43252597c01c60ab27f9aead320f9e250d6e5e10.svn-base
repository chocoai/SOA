
ws.palette = {};

//保存工具按钮的拖放数据
ws.palette.dragItem  = new Object();
ws.palette.dropItem  = new Object();

/**
 * 显示工具栏
 */
ws.palette.showGroup = function(tdivpid, tdivcid){
	var tdivp = document.getElementById(tdivpid);
	var tdivc = document.getElementById(tdivcid);

	tdivp.style.pixelWidth = tdivp.parentElement.offsetWidth;
	if( tdivp.style.display === "none" ){
		tdivp.style.display = "inline";
		
		tdivc.style.clip = "rect(0 " + tdivp.parentElement.offsetWidth + " 1 0)";
		tdivc.style.pixelLeft = 0;
		tdivc.style.pixelTop  = 0 - tdivc.offsetHeight;
		ws.palette.repeatShow(tdivpid, tdivcid, true);
	} else {
		tdivc.style.clip = "rect(0 " + tdivp.parentElement.offsetWidth + " " + tdivc.offsetHeight + " 0)";
		tdivc.style.pixelLeft = 0;
		tdivc.style.pixelTop  = 0;
		ws.palette.repeatShow(tdivpid, tdivcid, false);
	}
}

/**
 * 显示工具栏的动画函数
 */
ws.palette.repeatShow = function(tdivpid, tdivcid, open){
	var tdivp = document.getElementById(tdivpid);
	var tdivc = document.getElementById(tdivcid);
	var height = tdivp.style.pixelHeight;
	if( open === true ){
		height += 50;
		tdivc.style.clip = "rect(" + (tdivc.offsetHeight - height) + " " + tdivp.parentElement.offsetWidth + " " + tdivc.offsetHeight + " 0)";
		if( height < tdivc.offsetHeight ){
			tdivp.style.pixelHeight = height;
			tdivc.style.pixelTop += 50;
			window.setTimeout("ws.palette.repeatShow('" + tdivpid + "','" + tdivcid + "', true)", 30);
		} else {
			tdivp.style.pixelHeight = tdivc.offsetHeight;
			tdivc.style.pixelLeft = 0;
			tdivc.style.pixelTop  = 0;
		}
	} else {
		height -= 50;
		tdivc.style.clip = "rect(" + (tdivc.offsetHeight - height) + " " + tdivp.parentElement.offsetWidth + " " + tdivc.offsetHeight + " 0)";
		if( height > 0 ){
			tdivp.style.pixelHeight = height;
			tdivc.style.pixelTop -= 50;
			window.setTimeout("ws.palette.repeatShow('" + tdivpid + "','" + tdivcid + "', false)", 30);
		} else {
			tdivp.style.pixelHeight = 0;
			tdivc.style.pixelLeft = 0;
			tdivc.style.pixelTop  = 0 - tdivc.offsetHeight;
			tdivp.style.display = "none";
		}
	}
}

/**
 * 生成palette
 * @param res palette描述资源定位符
 * @param standin 替身元素，生成的对象将占据替换替身元素所在的位置，并且其ID也设为替身元素的ID
 * @return 生成的palette对象
 */
ws.palette.make = function(standin, res){
	var resNode = ws.res.findResourceNode(res);
	if( resNode == null )
		return null;
	var palette = ws.createElement("<div style='width:100%'>");
	palette.className = "ws_palette";

	var groups = resNode.childNodes;
	var group, gtitle, tools, tool, src = "", dragSrc;
	var id, tdivp, tdivc, open_divp, open_divc, openAttr, icon;
	for( var i = 0; i < groups.length; i++ ){
		group = groups[i];
		gtitle = ws.res.findChildNode(group, "label");
		if( gtitle == null )
			return null;
		tdivp = "tdivp" + ws.generateId();
		tdivc = "tdivc" + ws.generateId();
		
		openAttr = group.attributes.getNamedItem("open");
		icon 	 = group.attributes.getNamedItem("icon");
		if( openAttr && openAttr.value == "true" ){
			open_divc = tdivc;
			open_divp = tdivp;
		}

		src += '<table cellpadding="1" cellspacing="1">';
		src += '<tr><td class="group_button" onselectstart="return false;" '
		src += 'onclick="ws.palette.showGroup(\''+tdivp+'\',\''+tdivc+'\')">';
		if( icon )
			src += ws.theme.imageHTML(icon.value, "vertical-align:middle");
		src += gtitle.firstChild.text + '</td></tr>';
		src += '</table>';
		
		src += '<div id="' + tdivp + '" style="display:none;position:relative;">';
		src += '<div id="' + tdivc + '"style="position:absolute;">';
		src += '<table class="group_panel" cellpadding="0" cellspacing="0">';
		id = ws.generateId();
		tools = group.childNodes;

		var label, drag, drop, item, toolType, action;
		for( var j = 0; j < tools.length; j++){
			if( tools[j].nodeName !== "tool" )
				continue;
			item  = tools[j].attributes.getNamedItem("type");
			icon  = tools[j].attributes.getNamedItem("icon");
			label = ws.res.findChildNode(tools[j], "label");
			drag  = ws.res.findChildNode(tools[j], "drag");
			drop  = ws.res.findChildNode(tools[j], "drop");
			action= ws.res.findChildNode(tools[j], "action");

			if( item != null )
				toolType = item.value;
			else toolType = "unknow";
			src += '<tr><td class="tool_button" name="tb' + id + '" buttontype="radio" tooltype="' + toolType + '" ';
			if( drag != null && drop != null ){
				dragSrc = "";
				if( icon )
					dragSrc += ws.theme.imageHTML(icon.value, "vertical-align:middle");
				dragSrc += drag.firstChild.text;
				ws.palette.dragItem[toolType] = dragSrc;
				ws.palette.dropItem[toolType] = drop.firstChild.text;
				src += "onmousedown=\"ws.dnd.doDrag(this, '" + toolType + "'," + "ws.palette.dragItem." + toolType + "," + "ws.palette.dropItem." + toolType + ")\"";
				src += ' onmouseup="ws.dnd.cancelDrag()" onselectstart="return false;" ';
			}
			if( action != undefined && action != null && action.firstChild != null)
				src += ' onclick="' + action.firstChild.text + '"';
			src += '>';
			if( icon )
				src += ws.theme.imageHTML(icon.value, "vertical-align:middle");
			if( label != null )
				src += label.firstChild.text;
			else 
				src += "&nbsp;";

			src += '</td></tr>';
		}
		src += '</table></div></div>';
	}
	palette.innerHTML = src;

	var el = null;
	if( typeof(standin) == "string" )
		el = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		el = standin;
	if( el != null && el != undefined ){
		el.replaceNode(palette);
		palette.id = el.id;
	}
	if( open_divc && open_divp ){
		ws.palette.showGroup(open_divp, open_divc);
	}
	return palette;
}