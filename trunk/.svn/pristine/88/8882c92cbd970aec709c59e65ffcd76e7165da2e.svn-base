(function(qs, $) {
	js.$class("qs.m.WaitingBox", qs.ui.Control, function(){
		this._counter = 0;
		this._text = "正在加载...";
		this.initialize = function(options){
			this.$super(options);
		};
		
		js.$protected.template = function(options){
			return StringUtil.join(
				'<div class="qs-m-waitingbox">',
					'<img class="qs-loading" src="', baseUrl, '../images/loading.gif"></img>',
					'<div class="qs-text">', this._text, '</div>',
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
			$element.css("display", "none");
			this.invalidateSize();
		};

		js.$public.open = function(text){
			this._counter++;
			text = js.param(text, "正在加载...");
			this._$element.css("display", "");
			this._$element.find(".qs-text").html(text);
			var x, y;
			var size = this.outerSize();
			x = (qs.ui.windowWidth()  - size.width) / 2;
			y = (qs.ui.windowHeight() - size.height) / 3;
			this.left(x);
			this.top(y);
		};
		/**
		 * 关闭等待框对象
		 */
		js.$public.close = function(){
			this._counter--;
			if(this._counter <= 0){
				this._counter = 0;
				this._$element.css("display", "none");
			}
		};
		
		/**
		 * 重置等待框对象
		 */
		js.$public.reset = function(){
			this._counter = 0;
			this._$element.css("display", "none");
		};
		/**
		 * 设置等待框显示的文本信息
		 * @param message 显示的文本信息
		 */
		js.$public.setMessage = function(message/*:String*/){
			this._text = message;
			if( this.isRendered() ){
				this._$element.find(".qs-text").html(message);
			}
		};
	});
})(qs, jQuery);