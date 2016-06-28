<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 配置文件 -->
<script type="text/javascript" src="UEditor/ueditor.config.js"></script>
<!-- 编辑器源码文件 -->
<script type="text/javascript" src="../js/share/json.js"></script>
<script type="text/javascript" src="UEditor/ueditor.all.min.js"></script>
<!-- 语言包文件(建议手动加载语言包，避免在ie下，因为加载语言失败导致编辑器加载失败) -->
<script type="text/javascript" src="UEditor/lang/zh-cn/zh-cn.js"></script>
<script id="ueditor" name="content" type="text/plain">
</script>
<script type="text/javascript">
	/********
	UEditor修改
	ueditor.css
	.edui-for-qsimage .edui-icon {
	    background-position: -380px 0;
	}
	ueditor.all.js，找到btnCmds数组，添加一个 'qsimage'
	******/
	var $ueditor;
	var UEditor = new js.Class("UEditor", HtmlControl, function(){
		var _value = "";
		var self = this;
		$ueditor = this;
		var $editor;
		var _isEditorReady = false;
		this.initialize = function(){
			//设置编辑器
			window.UEDITOR_CONFIG.fullscreen = true;
			window.UEDITOR_CONFIG.elementPathEnabled = false;
			window.UEDITOR_CONFIG.wordCount = false;
			window.UEDITOR_CONFIG.enableAutoSave = false;
			window.UEDITOR_CONFIG.toolbars = [
     			            ['selectall', 'cleardoc', 'undo', 'redo', '|',
     			             'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch', 'autotypeset', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', '|',
     			             'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
     			             'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
     			             'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|',
     			             'link', 'unlink', '|', 'imagenone', 'imageleft', 'imageright', 'imagecenter', '|',
     			             'qsimage', 'horizontal', 'spechars', '|',
     			             'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', '|',
     			             'searchreplace']
     			     ];

			UE.commands['qsimage'] = {
			    execCommand : function(){
					var range = this.selection.getRange();
			        var img = range.getClosedNode();
					var alink = "alink://ime.AppWindow/HtmlImageWindow?";
					var param = {};
					param.html = "";
					param.$eventReceiver = "$ueditor.setQSImage";
					if(!js.isNull(img) && /img/i.test(img.tagName)) {
						var imgHtml = img.outerHTML;
						param.html = imgHtml.substr(0, imgHtml.length - 1) + ' alt="" >';
					}
					alink += JSON.encode(param);
					QS.openALink(alink);
			    }
			};
		}
		js.$public.setQSImage = function(type, args){
			$editor.execCommand('insertHtml', args.html);
		};
		js.$public.getValue = function(){
			if(_isEditorReady)
				return $editor.getContent();
			return _value;
		}
		js.$public.setValue = function(val){
			_value = js.isNull(val) ? "" : val;
			if(_isEditorReady){
				$editor.setContent(_value);
			}
		}
		js.$public.render = function(container){
			$editor = UE.getEditor('ueditor');
			$editor.addListener('focus contentChange', function(editor) {
				self.fireChangeEvent();
			});
			$editor.addListener('ready', function(editor) {
				_isEditorReady = true;
				$editor.setContent(_value);
			});
		}
	});
</script>