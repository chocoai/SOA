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

function $import(baseUrl, path, type){
	if( path.substring(path.length - 3) == "css" )
		document.write("<" + "link href=\"" + baseUrl + path + "\" rel=\"stylesheet\"></" + "link>");
	else
		document.write("<" + "script src=\"" + baseUrl + path + "\"></" + "script>");
}

var baseUrl = $base_url("loader.js");
var rootUrl = "";
(function() {
	var pathname = window.location.pathname;
	var index = pathname.indexOf("/applications", 0);
	if( index != -1 ){
		var path = pathname.substring(0, index);
		if( path.length > 0 && path.charAt(0) != '/' )
			path = "/" + path;
		rootUrl = path + "/";
	}
})();

$import(baseUrl, "css/default/calendar.css");
$import(baseUrl, "css/default/popup.css");
$import(baseUrl, "css/default/textinput.css");
$import(baseUrl, "css/default/numberinput.css");
$import(baseUrl, "css/default/datefield.css");
$import(baseUrl, "css/default/button.css");
$import(baseUrl, "css/default/container.css");
$import(baseUrl, "css/default/list.css");
$import(baseUrl, "css/default/combobox.css");
$import(baseUrl, "css/default/scrollview.css");
$import(baseUrl, "css/default/colorpicker.css");
$import(baseUrl, "css/default/textarea.css");
$import(baseUrl, "css/default/checkbox.css");
$import(baseUrl, "css/default/radiobox.css");
$import(baseUrl, "css/default/window.css");
$import(baseUrl, "css/default/menu.css");
$import(baseUrl, "css/default/messagebox.css");
$import(baseUrl, "css/default/rule.css");
$import(baseUrl, "css/default/fieldset.css");
$import(baseUrl, "css/default/panel.css");
$import(baseUrl, "css/default/progressbar.css");
$import(baseUrl, "css/default/tabview.css");
$import(baseUrl, "css/default/treeview.css");
$import(baseUrl, "css/default/validate.css");
$import(baseUrl, "css/default/accordion.css");
$import(baseUrl, "css/default/splitter.css");
$import(baseUrl, "css/default/slider.css");
$import(baseUrl, "css/default/style.css");

$import(baseUrl, "css/default/slick.grid.css");
$import(baseUrl, "css/default/datagrid.css");

$import(baseUrl, "core/oop.js");
$import(baseUrl, "core/qs.js");
$import(baseUrl, "core/qs.events.js");
$import(baseUrl, "core/util.js");
$import(baseUrl, "core/qs.collections.js");
$import(baseUrl, "core/qs.xml.js");
$import(baseUrl, "core/qs.timer.js");
$import(baseUrl, "core/qs.storage.js");
$import(baseUrl, "core/qs.i18n.js");

$import(baseUrl, "i18n/zh_CN.js");

$import(baseUrl, "utils/StringUtil.js");

$import(baseUrl, "layout/qs.layout.js");
$import(baseUrl, "layout/qs.layout.flow.js");
$import(baseUrl, "layout/qs.layout.grid.js");
$import(baseUrl, "layout/qs.layout.horizontal.js");
$import(baseUrl, "layout/qs.layout.vertical.js");

$import(baseUrl, "ui/qs.ui.core.js");
$import(baseUrl, "ui/qs.ui.animation.js");
$import(baseUrl, "ui/qs.ui.qxml.js");
$import(baseUrl, "ui/qs.ui.control.js");
$import(baseUrl, "ui/qs.ui.itemscontrol.js");
$import(baseUrl, "ui/qs.ui.content.js");
$import(baseUrl, "ui/qs.ui.container.js");
$import(baseUrl, "ui/qs.ui.form.js");
$import(baseUrl, "ui/qs.ui.popup.js");
$import(baseUrl, "ui/qs.ui.calendar.js");
$import(baseUrl, "ui/qs.ui.textinput.js");
$import(baseUrl, "ui/qs.ui.colorpicker.js");
$import(baseUrl, "ui/qs.ui.numberinput.js");
$import(baseUrl, "ui/qs.ui.datefield.js");
$import(baseUrl, "ui/qs.ui.button.js");
$import(baseUrl, "ui/qs.ui.combobox.js");
$import(baseUrl, "ui/qs.ui.list.js");
$import(baseUrl, "ui/qs.ui.scrollview.js");
$import(baseUrl, "ui/qs.ui.textarea.js");
$import(baseUrl, "ui/qs.ui.checkbox.js");
$import(baseUrl, "ui/qs.ui.radiobox.js");
$import(baseUrl, "ui/qs.ui.window.js");
$import(baseUrl, "ui/qs.ui.menu.js");
$import(baseUrl, "ui/qs.ui.messagebox.js");
$import(baseUrl, "ui/qs.ui.rule.js");
$import(baseUrl, "ui/qs.ui.fieldset.js");
$import(baseUrl, "ui/qs.ui.panel.js");
$import(baseUrl, "ui/qs.ui.progressbar.js");
$import(baseUrl, "ui/qs.ui.image.js");
$import(baseUrl, "ui/qs.ui.label.js");
$import(baseUrl, "ui/qs.ui.tabview.js");
$import(baseUrl, "ui/qs.ui.treeview.js");
$import(baseUrl, "ui/qs.ui.validate.js");
$import(baseUrl, "ui/qs.ui.accordion.js");
$import(baseUrl, "ui/qs.ui.splitter.js");
$import(baseUrl, "ui/qs.ui.slider.js");
$import(baseUrl, "ui/qs.ui.html.js");
$import(baseUrl, "ui/qs.ui.focusmanager.js");

$import(baseUrl, "lib/jquery.event.drag-2.2.js");
$import(baseUrl, "lib/jquery-ui-1.10.3.custom.min.js");
$import(baseUrl, "lib/jquery.jsonpath.js");
$import(baseUrl, "lib/slick.core.js");
$import(baseUrl, "lib/slick.grid.js");
$import(baseUrl, "lib/mathquill.min.js");
$import(baseUrl, "lib/echarts.js");
$import(baseUrl, "ui/qs.ui.datagrid.js");

$import(baseUrl, "wos/events.js");
$import(baseUrl, "wos/Desktop.js");
$import(baseUrl, "wos/amf.js");
$import(baseUrl, "wos/crypt/Base64.js");
$import(baseUrl, "wos/crypt/xxtea.js");
$import(baseUrl, "wos/crypt/MD5.js");
$import(baseUrl, "wos/Remoting.js");
$import(baseUrl, "wos/FileLoader.js");
$import(baseUrl, "wos/PermissionUtil.js");
$import(baseUrl, "wos/DictionaryLoader.js");
$import(baseUrl, "wos/expression/ExpressionParser.js");
$import(baseUrl, "wos/expression/ExpressionFunction.js");
$import(baseUrl, "wos/expression/ValueProviderFilter.js");
$import(baseUrl, "wos/ALink.js");
$import(baseUrl, "wos/script/ScriptProxy.js");

$import(baseUrl, "app/css/imagefield.css");
$import(baseUrl, "app/css/waitingbox.css");
$import(baseUrl, "app/css/attachment.css");
$import(baseUrl, "app/css/entityfield.css");
$import(baseUrl, "app/css/principalfield.css");
$import(baseUrl, "app/css/gridnav.css");
$import(baseUrl, "app/css/header.css");
$import(baseUrl, "app/css/xmlform.css");
$import(baseUrl, "app/css/qsweb.css");
$import(baseUrl, "app/css/report.css");
$import(baseUrl, "app/css/query.css");
$import(baseUrl, "app/css/workflow.css");

$import(baseUrl, "app/Workspace.js");
$import(baseUrl, "app/AppWindow.js");
$import(baseUrl, "app/AppFrame.js");
$import(baseUrl, "app/Page.js");
$import(baseUrl, "app/HtmlImageWindow.js");
$import(baseUrl, "app/PageWindow.js");
$import(baseUrl, "app/CodingPattern.js");
$import(baseUrl, "app/WaitingBox.js");
$import(baseUrl, "app/QueryControl.js");
$import(baseUrl, "app/ReportControl.js");
$import(baseUrl, "app/QueryPage.js");
$import(baseUrl, "app/ReportPage.js");
$import(baseUrl, "app/FormWindow.js");
$import(baseUrl, "app/PasswordWindow.js");
$import(baseUrl, "app/EntityRuleManager.js");
$import(baseUrl, "app/OfficeDocumentManager.js");
$import(baseUrl, "app/controls/ImageField.js");
$import(baseUrl, "app/controls/CurrencyInput.js");
$import(baseUrl, "app/controls/AttachmentList.js");
$import(baseUrl, "app/controls/EntityField.js");
$import(baseUrl, "app/controls/MultilevelTree.js");
$import(baseUrl, "app/controls/PrincipalSelectField.js");
$import(baseUrl, "app/controls/XmlForm.js");
$import(baseUrl, "app/controls/NavigateBar.js");
$import(baseUrl, "app/form/SubmitButton.js");
$import(baseUrl, "app/form/SubmitPanel.js");
$import(baseUrl, "app/query/XmlQuery.js");
$import(baseUrl, "app/query/SQLUtil.js");
$import(baseUrl, "app/query/DataGridEx.js");
$import(baseUrl, "app/query/DataGridNavigator.js");
$import(baseUrl, "app/query/QueryExportForm.js");
$import(baseUrl, "app/query/ColumnVisibleForm.js");
$import(baseUrl, "app/data/DataView.js");
$import(baseUrl, "app/data/GroupingProvider.js");
$import(baseUrl, "app/data/GroupData.js");
$import(baseUrl, "app/data/DateGroupingProvider.js");
$import(baseUrl, "app/data/DictionaryGroupingProvider.js");
$import(baseUrl, "app/data/EntityGroupingProvider.js");
$import(baseUrl, "app/data/PrincipalGroupingProvider.js");
$import(baseUrl, "app/table/XmlCell.js");
$import(baseUrl, "app/table/XmlTable.js");
$import(baseUrl, "app/workflow/TaskList.js");
$import(baseUrl, "app/workflow/FlowViewWindow.js");
$import(baseUrl, "app/workflow/DeliverWorkItemWindow.js");
$import(baseUrl, "app/report/FillArea.js");
$import(baseUrl, "app/report/PivotTable.js");
$import(baseUrl, "app/report/ReportGenerator.js");
$import(baseUrl, "app/report/ReportView.js");
$import(baseUrl, "app/report/ReportDocumentService.js");
$import(baseUrl, "app/report/ReportExporter.js");

$import(baseUrl, "app/admin/SecurityManager.js");
$import(baseUrl, "app/admin/SetRoleWindow.js");
$import(baseUrl, "app/admin/SetPasswordWindow.js");
