ws.toolbar = {};
ws.toolbar.group = {};

/**
 * 生成toolbar
 * @param res toolbar描述资源定位符
 * @param standin 替身元素，生成的对象将占据替换替身元素所在的位置，并且其ID也设为替身元素的ID
 * @param op_className 样式名称(可选，默认为ws_toolbar)
 * @return 生成的toolbar对象
 */
ws.toolbar.make = function(standin, res, op_className){
	var resNode = ws.res.findResourceNode(res), className = op_className || "ws_toolbar";
	if( resNode == null )
		return null;

	var toolbar   = ws.createElement('<div style="width:10;border :1px solid Silver;">');
	var toolTable = ws.createElement('<table class="' + className + '" cellspacing=1 cellpadding=0 border=0 unselectable="on">', toolbar);
	var tbody	  = ws.createElement("tbody", toolTable);
	var tr		  = ws.createElement("tr", tbody);
	var td;
	
	toolbar.buttons = new Array();

	td = ws.createElement("<td nowrap>", tr);
	td.innerHTML = '<span class="handle"></span><span class="handle"></span>';

	var tools = resNode.childNodes;
	var tool = null, icon = null, action = null, group = null, name = null, tip = null, type = null, value = null, id = null, disabled = null, src = "";
	for( var i = 0; i < tools.length; i++ ){
		tool   = tools[i];
		if( tool.tagName == "separator" ){
			td = ws.createElement("<td nowrap>", tr);
			td.innerHTML = '<span class="separator"></span>';
		}
		else if( tool.tagName == "tool" ){
			icon   = ws.res.findChildNode(tool, "icon");
			action = ws.res.findChildNode(tool, "action");
			id     = tool.attributes.getNamedItem("id");
			name   = tool.attributes.getNamedItem("name");
			group  = tool.attributes.getNamedItem("group");
			tip    = tool.attributes.getNamedItem("tooltip");
			type   = tool.attributes.getNamedItem("type");
			disabled = tool.attributes.getNamedItem("disabled");

			if( group ){
				var groupName = group.value;
				var groupObj  = toolbar[groupName];
				if( !groupObj ){
					groupObj = new Object();
					groupObj.name = groupName;
					groupObj.onButtonChanged = ws.toolbar.group.onButtonChanged;
					groupObj.parent = toolbar;
					toolbar[groupName] = groupObj;
				}
			}

			if( type != null && type.value == "DropButton" ){
				td  = ws.createElement('<td nowrap="true" valign="middle">', tr);
				value = tool.attributes.getNamedItem("value");
				if( value != null )
					td.innerHTML = '<span value="' + value.value + '"></span>';
				else
					td.innerHTML = '<span></span>';
				var btn;
				if( group )
					btn = ws.button.DropButton.make(td.children[0], tool, "tb" + group.value);
				else
					btn = ws.button.DropButton.make(td.children[0], tool);
				if( btn != null && btn != undefined ){
					btn.style.height = "100%";
					if( disabled != null && disabled.value == "true" )
						btn.cooldisabled = "true";
				}
				if( group ){
					var groupName = group.value;
					var groupObj  = toolbar[groupName];
					if( groupObj != null && groupObj != undefined ){
						btn.curButton.group = groupObj;
					}
				}
			}
			else {
				src = '<td nowrap="true" class="toolbutton" nogray="true" valign="middle" ';
				if( disabled != null && disabled.value == "true" )
					src += 'cooldisabled="true" ';

				if( id != undefined && id != null )
					src += ' id="' + id.value + '"';
				if( tip != undefined && tip != null )
					src += ' tooltip="' + tip.value +'"';
				if( action != undefined && action != null && action.firstChild != null)
					src += ' onclick="' + action.firstChild.text + '"';
				if( group )
					src += 'name="tb' + group.value + '" buttontype="radio"';
				src += ">";
				td  = ws.createElement(src, tr);
				src = "";
				
				if( icon != undefined && icon != null && icon.text != "")
					src += ws.theme.iconHTML(icon.text);
				if( name != undefined && name != null )
					src += name.value;
				td.innerHTML = src;
				if( group ){
					var groupName = group.value;
					var groupObj  = toolbar[groupName];
					if( groupObj != null && groupObj != undefined ){
						td.group = groupObj;
					}
				}
			}
			
			if( id != undefined && id != null )
				toolbar.buttons[id.value] = td;
		}
	}
	
	toolbar.enableButton = ws.toolbar.enableButton;

	var el = null;
	if( typeof(standin) == "string" )
		el = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		el = standin;
	if( el != null && el != undefined ){
		el.replaceNode(toolbar);
		toolbar.id = el.id;
	}
	return toolbar;
}

ws.toolbar.enableButton = function(buttonID, enable){
	var button = this.buttons[buttonID];
	if( button != undefined && button != null ){
		if( enable == true || enable == "true" ){
			if( typeof(button.enable) != "undefined" )
				button.enable();
		}
		else {
			if( typeof(button.disable) != "undefined" )
				button.disable();
		}
	}
}

ws.toolbar.group.onButtonChanged = function(button){
	if( typeof(this.parent.onGroupButtonChanged) == "function" )
		this.parent.onGroupButtonChanged(this.name, button.id);
}