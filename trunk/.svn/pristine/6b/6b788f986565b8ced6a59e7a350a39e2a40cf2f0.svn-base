(function(qs, $) {
	js.$class("qs.m.Header", qs.ui.Control, [qs.ui.IContentControl], function(){
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._lefter  = null;
			this._righter = null;
			this._content = null;
		};
		js.$public.lefter = function(val){
			if( val === undefined )
				return this._lefter;
			if( js.instanceOf(val, qs.ui.Control) ){
				this._lefter = val;
				if( this.isRendered() ){
					if( this._lefter.isRendered() ){
						this._$lefter.append(this._lefter.$element());
					}
					else {
						var $el = $("<div></div>");
						this._$lefter.append($el);
						this._lefter.render($el);
					}
				}
			}
		}
		js.$public.righter = function(val){
			if( val === undefined )
				return this._righter;
			if( js.instanceOf(val, qs.ui.Control) ){
				this._righter = val;
				if( this.isRendered() ){
					if( this._righter.isRendered() ){
						this._$righter.append(this._righter.$element());
					}
					else {
						var $el = $("<div></div>");
						this._$righter.append($el);
						this._righter.render($el);
					}
				}
			}
		}
		js.$public.content = function(val){
			if( val === undefined )
				return this._content;
			this._content = val;
			if( js.instanceOf(val, qs.ui.Control) ){
				if( this.isRendered() ){
					if( this._content.isRendered() ){
						this._$center.append(this._content.$element());
					}
					else {
						var $el = $("<div></div>");
						this._$center.append($el);
						this._content.render($el);
					}
				}
			}
			else if( js.type(this._content) == "string" && this.isRendered() ){
				this._$center.html(this._content);
			}
		}
		js.$protected.template = function(options){
			return StringUtil.join(
					'<div class="lefter"></div>',
					'<div class="center"></div>',
					'<div class="righter"></div>'
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
			
			$element.addClass("qs-m-header");
			
			this.$super($element, options);
			
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			
			this._$lefter  = $element.children(".lefter");
			this._$center  = $element.children(".center");
			this._$righter = $element.children(".righter");
			
			this.lefter(this._lefter);
			this.righter(this._righter);
			this.content(this._content);
		};
	});
	
})(qs, jQuery);
