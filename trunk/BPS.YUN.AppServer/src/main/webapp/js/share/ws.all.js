
function $base(){
	var i, base = "", src = "ws.all.js", scripts = document.getElementsByTagName("script");
	for (i=0; i<scripts.length; i++){
		if (scripts[i].src.match(src)){ 
			base = scripts[i].src.replace(src, "");
			break;
		}
	}
	var index = base.indexOf("js");
	if( index != -1 )
		base = base.substring(0, index);
	return base;
}

function $import(path){
	document.write("<" + "script src=\"" + ws_app_path + "js/" + path + "\"></" + "script>");
}

var ws_app_path = $base();

$import("share/ws/ws.js");
$import("share/ws/ws.util.js");
$import("share/ws/ws.layout.js");
$import("share/ws/ws.part.js");
$import("share/ws/ws.config.js");
$import("share/ws/ws.form.js");
$import("share/ws/ws.form.selectbox.js");
$import("share/ws/ws.form.combobox.js");
$import("share/ws/ws.form.detailbox.js");
$import("share/ws/ws.menu.js");
$import("share/ws/ws.dialog.js");
$import("share/ws/ws.effect.js");
$import("share/ws/ws.tableview.js");
$import("share/ws/ws.tooltip.js");
$import("share/ws/ws.dnd.js");
$import("share/ws/ws.palette.js");
$import("share/ws/ws.res.js");
$import("share/ws/ws.selectable.js");
$import("share/ws/ws.freetable.js");
$import("share/ws/ws.toolbar.js");
$import("share/ws/ws.panel.js");
$import("share/ws/ws.button.js");
$import("share/ws/ws.propgrid.js");
$import("share/ws/ws.theme.js");
$import("share/ws/ws.tabpane.js");
$import("share/ws/ws.slider.js");
$import("share/ws/ws.drawpanel.js");
$import("share/ws/ws.waitingbar.js");
$import("share/ws/ws.tree.js");
$import("share/ws/ws.listbox.js");

