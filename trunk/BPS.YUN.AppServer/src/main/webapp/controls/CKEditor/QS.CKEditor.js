(function(qs, $) {

	var editor_id = 0;
	js.$class("qs.ext.CKEditor", qs.ui.FormControl, function(){
		this._value = "";
		this.initialize = function(options){
			this.$super(options);
			
			this.editorId = "ckedit" + (editor_id++);
		};
		js.$public.value = function(val){
			if( val === undefined ){
				if( !js.isNull(this.editor) )
					return this.editor.getData();
				else
					return this._value;
			}
			this._value = val;
			if( !js.isNull(this.editor) )
				this.editor.setData(val);
		}
		js.$public.updateSize = function(width, height){
			this.$super(width, height);
			if( !this.isRendered() )
				return;
			
			if( !js.isNull(this.editor) ){
				var size = this.outerSize();
				this.editor.resize(size.width, size.height, false);
			}
		};
		
		js.$public.template = function(options){
			return StringUtil.join(
				'<textarea id="', this.editorId, '" name="', this.editorId, '"></textarea>'
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
			
			this.createEditor();
			
			var window = this.findAncestor(qs.ui.Window);
			if( !js.isNull(window) ){
				window.addEventListener(qs.ui.Window.WINDOW_OPENED, this.onWindowOpen, this);
				window.addEventListener(qs.ui.Window.WINDOW_CLOSED, this.onWindowClose, this);
			}
			
			this.invalidateSize();
		};
		this.onWindowOpen = function(){
			if( js.isNull(this.editor) ){
				this._$element.html(this.template());
				
				this.createEditor();
			}
		};
		this.onWindowClose = function(){
			if( !js.isNull(this.editor) ){
				this._$element.html("");
				this.editor = null;
			}
		};
		this.createEditor = function(){
			this.editor = CKEDITOR.replace( this.editorId,
			{
				extraPlugins: 'qsImage',
				language : 'zh-cn' ,
				fullPage:true,
				toolbar : [
							['Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo'],
							['Bold','Italic','Underline'],
							['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
							['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
							['Link','Unlink'],
							['qsImage','Table','HorizontalRule','Smiley','SpecialChar'],
							['Styles','Format','Font','FontSize'],
							['TextColor','BGColor']
						]
			});
			var tabView = this.findAncestor(qs.ui.TabView);
			if( !js.isNull(tabView) ){
				tabView.addEventListener(qs.ui.ItemsControl.SELECTED_ITEM_CHANGED, this.onTabChanged, this);
			}
			var self = this;
			this.editor.on('instanceReady', function(){
				var size = self.outerSize();
				self.editor.resize(size.width - 2, size.height - 2, false);
				/*
				var $editor = self._$element.children("#cke_" + self.editorId);
				$editor.width(size.width - 4);

				var $content = $editor.find(".cke_contents");
				var top    = $editor.find(".cke_top").height();
				var bottom = $editor.find(".cke_bottom").height();
				
				$content.height(size.height - (top + bottom) - 4);
				*/
				if( !js.isNull(self._value) )
					self.editor.setData(self._value);
			});
			this.editor.on('change',function(){
				self.dispatchEvent("change");
			});
		}
		this.onTabChanged = function(){
			var $editor = this._$element.children("#cke_" + this.editorId);
			var size = this.outerSize();
			$editor.width(size.width - 4);

			var $content = $editor.find(".cke_contents");
			var top    = $editor.find(".cke_top").height();
			var bottom = $editor.find(".cke_bottom").height();
			
			$content.height(size.height - (top + bottom) - 4);
		}
	});
	
})(qs, jQuery);
