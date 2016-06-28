var qsImage_current_editor = null;
CKEDITOR.plugins.add('qsImage',{
	        icons:'qsImage',
	        init:function (a){
	            a.addCommand("qsImage", {
					exec : function(editor){
						qsImage_current_editor = editor;

						var selection = editor.getSelection();
						editor.lockSelection(selection);
						var alink = "alink://ime.AppWindow/HtmlImageWindow?";
						var param = {};
						param.$eventReceiver = "qsImage_callback";
						param.html = "";
						if(selection.getSelectedElement() != "" && selection.getSelectedElement() != null) {
							param.html = selection.getSelectedElement().getOuterHtml()
						}
						alink += JSON.encode(param);
						QS.openALink(alink);
					}
				});
	            a.ui.addButton(
	                "qsImage",
	                {
	                    label:"图片",
	                    command:"qsImage"
	                });
        }
    }
);
function qsImage_callback(type, args) {
	qsImage_current_editor.unlockSelection();
	var element = CKEDITOR.dom.element.createFromHtml(args.html);
	qsImage_current_editor.insertElement(element);
}

