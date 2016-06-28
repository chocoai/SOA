CKEDITOR.plugins.add('qsResource',{
	        icons:'qsResource',
	        init:function (a){
	            a.addCommand("qsResource", {
					exec : function(editor){
						var url = "../pages/cms/htmlview/resource_manager.jsp";
						if( control._options && control._options.channel_id && control._options.channel_label ) {
							url += "?channel_id=" + control._options.channel_id + "&channel_label=" + encodeURIComponent(control._options.channel_label);
						}
						var o = {};
						var sFeatures = 'dialogHeight: 450px; dialogWidth: 770px; center: yes; help: no; resizable: yes; minimize:yes; maximize:yes; status: no; unadorned: yes;';
						var returnValue = window.showModalDialog(url, o, sFeatures);
						if(returnValue){
							var list = JSON.parse(returnValue);
							for(var i = 0; i < list.length; i++){
								var imgHtml = '<img src="' + list[i] + '"/>';
								CKEDITOR.instances.ckedit.insertHtml(imgHtml);
							}
						}
					}
				});
	            a.ui.addButton(
	                "qsResource",
	                {
	                    label:"资源",
	                    command:"qsResource"
	                });
        }
    }
);

