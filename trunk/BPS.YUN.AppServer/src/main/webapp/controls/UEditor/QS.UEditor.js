(function(qs, $) {
	//设置编辑器
	var editor_id = 0;
	js.$class("qs.ext.UEditor", qs.ui.FormControl, function(){
		this._value = "";
		this._isEditorReady = false;
		this.initialize = function(options){
			this.$super(options);
			this.editorId = "ueditor" + (editor_id++);
		};
		js.$public.value = function(val){
			if( val === undefined ){
				if( !js.isNull(this._editor) && this._isEditorReady == true){
					try{
						return this._editor.getContent();
					}catch (e) {
						return "";
					}
				}else
					return this._value;
			}
			this._value = js.isNull(val) ? "" : val;
			if( !js.isNull(this._editor) && this._isEditorReady == true){
				try{
					this._editor.setContent(this._value);
				}catch (e) {
				}
			}
		}
		js.$public.updateSize = function(width, height){
			this.$super(width, height);
			if( !this.isRendered() )
				return;
			this.callAfterRendered(function(){
				if( !js.isNull(this._editor) ){
					var size = this.outerSize();
					try{
						this._editor.setHeight(size.height - this._$element.find(".edui-editor-toolbarbox").height() - 5);
					}catch (e) {
					}
				}
			});
		};
		js.$public.template = function(options){
			return StringUtil.join(
				'<div style="position: relative; width: 100%; white-space: normal;">',
					'<div id="', this.editorId, '" name="', this.editorId, '" style="width:100%;height:100%">',
					'</div>',
				'</div>'
			);
		};
		/**
		 * 生成控件
		 * @param $element 承载控件的元素
		 * @param options 选项, {classes:附加样式类}
		 */
		js.$public.render = function($element, options){
			if (typeof options == 'undefined') options = {};
			var options = $.extend({
				classes : ''
			}, this._options, options);
			
			this.$super($element, options);
			var win = this.findAncestor(qs.ui.Window);
			if( !js.isNull(win) ){
				win.addEventListener(qs.ui.Window.WINDOW_OPENED, this.onWindowOpen, this);
				win.addEventListener(qs.ui.Window.WINDOW_CLOSED, this.onWindowClose, this);
			}
			
			this.callAfterRendered(function(){
				this.createEditor();
			});
			
			this.invalidateSize();
		};
		this.onWindowOpen = function(){
			if( js.isNull(this._editor) ){
				this._$element.html(this.template());
				this.createEditor();
			}
		};
		this.onWindowClose = function(){
			if( !js.isNull(this._editor) ){
				this._$element.html("");
				try{
					this._editor.destroy();
				}catch (e) {
					$("#" + this.editorId).empty();
					this.editorId = "ueditor" + (editor_id++);
				}
				this._editor = null;
				this._isEditorReady = false;
			}
		};
		this.createEditor = function(){
			var self = this;
			window.UEDITOR_CONFIG.UEDITOR_HOME_URL = baseUrl + "../../controls/UEditor/";
			window.UEDITOR_CONFIG.fullscreen = false;
			window.UEDITOR_CONFIG.elementPathEnabled = false;
			window.UEDITOR_CONFIG.wordCount = false;
			window.UEDITOR_CONFIG.scaleEnabled = false;
			window.UEDITOR_CONFIG.autoHeightEnabled = false;
			window.UEDITOR_CONFIG.enableAutoSave = false;
			
			if(js.isNull(this._options) || js.isNull(this._options.toolbars)){
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
			}else if(this._options.toolbars == "richTextBars"){
				window.UEDITOR_CONFIG.toolbars = [
  			            ['bold', 'italic', 'underline', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', '|',
  			             'fontfamily', 'fontsize', '|',
  			             'qsimage', 'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify']
  			     ];
			}
			
			this._editor = UE.getEditor(this.editorId, {langPath : baseUrl + "../../controls/UEditor/lang/", lang : "zh-cn"});
			this._editor.commands['qsimage'] = {
			    execCommand : this.execCommand,
			    queryCommandState : function(){
                }
			};
			$(this._editor.container).css("box-sizing", "border-box");
			var tabView = this.findAncestor(qs.ui.TabView);
			if( !js.isNull(tabView) ){
				tabView.addEventListener(qs.ui.ItemsControl.SELECTED_ITEM_CHANGED, this.onTabChanged, this);
			}
			this._editor.addListener('focus contentChange', function(editor){
				self.dispatchEvent("change");
			});
			this._editor.addListener('ready', function(editor) {
				self._isEditorReady = true;
				if( !js.isNull(self._value) )
					self._editor.setContent(self._value);
				
				if( !js.isNull(self._editor) ){
					var size = self.outerSize();
					var $toolbarbox = $(self._editor.ui.getDom('toolbarbox'));
					self._editor.setHeight(size.height - $toolbarbox.height() - 5);
				}
			});
		};
		
		this.execCommand = function(){
			var range = this.selection.getRange();
	        var img = range.getClosedNode();
			var alink = "alink://ime.AppWindow/HtmlImageWindow?";
			var param = {};
			param.html = "";
			param.control = this;
			param.$eventReceiver = function(type, args){
				args.param.control.execCommand('insertHtml', args.html);
			};
			if(!js.isNull(img) && /img/i.test(img.tagName)) {
				var imgHtml = img.outerHTML;
				param.html = imgHtml.substr(0, imgHtml.length - 1) + ' alt="" >';
			}
			
			wos.Desktop.openALink("", alink, param);
		};
		
		this.onTabChanged = function(){
			var self = this;
			setTimeout(function(){
				var size = self.outerSize();
				var $toolbarbox = $(self._editor.ui.getDom('toolbarbox'));
				try{
					self._editor.setHeight(size.height - $toolbarbox.height() - 5);
				}catch (e) {
				}
			}, 10);
		}
	});
})(qs, jQuery);
