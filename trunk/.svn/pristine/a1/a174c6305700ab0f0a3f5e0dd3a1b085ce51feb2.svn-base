<script type="text/javascript" src="CKEditor/ckeditor.js"></script>
<script type="text/javascript" src="../js/share/json.js"></script>
<script id="page-template" type="text/x-handlebars-template">
	<textarea id="ckedit" name="ckedit" style="width:100%;height:100%"></textarea>
	<style>
		.cke_toolgroup, .cke_combo_button {margin : 0px 2px 2px 0px;}
		.cke_top.cke_reset_all{padding:2px 2px 0px 2px;}
		.cke_button__selfimage_icon{background-image:url('CKEditor/plugins/icons.png?t=DAED');background-position:0 -360px;background-size:auto;}
	</style>
</script>
<script language="JavaScript" type="text/javascript">
	function onEditorLoaded() {
		var height = $("#cke_ckedit").height();
		var $content = $("#cke_ckedit .cke_contents");
		var contentHeight = $content.height();
		$content.height($(document).height() - (height - contentHeight)-2);
	};
	var editor;
	var CKEditor = new js.Class("CKEditor", HtmlControl, function(){
		this.container = null;
		//构造函数
		this.initialize = function(){
		}
		//获取控件的值，定义为public方法，实现IFormComponent中的getValue方法
		js.$public.getValue = function(){
			return CKEDITOR.instances.ckedit.getData();
		}
		//获取控件的值，定义为public方法，实现IFormComponent中的setValue方法
		js.$public.setValue = function(value){
			CKEDITOR.instances.ckedit.setData(value);
		}
		js.$public.render = function(container){
			this.container = container;
			var source   = $("#page-template").html();
			var template = Handlebars.compile(source);
			container.html(template());
			editor = CKEDITOR.replace( 'ckedit',
			{
				
				extraPlugins: 'qsImage,qsResource',
				language : 'zh-cn' ,
				width :  document.body.clientWidth-3,
				fullPage:true,
				//height:  500,
				toolbar : [
							['Source','-','Preview'],
							['Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo'],
							['-','Find','Replace','-','SelectAll'],
							['Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat'],
							['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
							['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
							['Link','Unlink','Anchor'],
							['qsImage','Table','HorizontalRule','Smiley','SpecialChar'],
							['Styles','Format','Font','FontSize'],
							['TextColor','BGColor'],
							[ 'ShowBlocks']
						]
			});
			editor.on('instanceReady',onEditorLoaded);
			var self = this;
			editor.on('change',function(){
				self.fireChangeEvent();
			});
		}
	});
</script>