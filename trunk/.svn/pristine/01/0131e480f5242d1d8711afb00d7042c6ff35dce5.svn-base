(function(qs, $) {
	js.$class("qs.m.SearchBox", qs.ui.Control, function(){
		qs.events.register("clear", this);
		qs.events.register("search", this);
		this._$enter = false;
		//构造函数
		this.initialize = function(options){
			this.$super(options);
		};
		
		js.$protected.template = function(options){
			return StringUtil.join(
					'<div class="qs-m-searchbox">',
						'<input type="text" placeholder="搜索">',
						'<button class="qs-m-searchbox-clear" type="button"/>',
						'<button class="qs-m-searchbox-search" type="button"/>',
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
			
			var $search = $element.children('.qs-m-searchbox');
			var $input = $search.children('input');
			if(!js.isNull(options.placeholder))
				$input.attr("placeholder", options.placeholder);
			
			var $clear = $search.children('.qs-m-searchbox-clear');
			this.bindEvent('input', this.onInput, $input);
			this.bindEvent('click', this.onClear, $clear);
			this.bindEvent('keypress', this.onEnter, $input);
			this.bindEvent('focusout', this.onFocusout, $input);
			this.bindEvent('focusin', this.onFocusIn, $input);
		};
		
		js.$public.value = function(val){
			if(js.isNull(val)){
				var $search = this._$element.children('.qs-m-searchbox');
				return $search.children('input').val();
			}
		};
		
		this.onInput = function(){
			var $search = this._$element.children('.qs-m-searchbox');
			var $key = $search.children('input').val();
			if($key != '')
				$search.children('.qs-m-searchbox-clear').show();
			else{
				$search.children('.qs-m-searchbox-clear').hide();
				var e = new qs.events.Event("clear");
				this.dispatchEvent(e);
			}
		};
		this.onEnter = function(event){
			var key = event.which;
		    if (key == 13) {
		    	this._$enter = true;
		    	this.onSearch();
		    }
		};
		this.onFocusIn = function(event){
			this._$enter = false;
		};
		this.onFocusout = function(event){
			if(this._$enter == true)
				return;
		    this.onSearch();
		};
		
		this.onSearch = function(){
			var $search = this._$element.children('.qs-m-searchbox');
			var $key = $search.children('input').val();
			
			var e = new qs.events.Event("search");
			this.dispatchEvent(e, {item: $key});
		};
		this.onClear = function(){
			var $search = this._$element.children('.qs-m-searchbox');
			$search.children('.qs-m-searchbox-clear').hide();
			$search.children('input').val('');
			
			var e = new qs.events.Event("clear");
			this.dispatchEvent(e);
		};
	});
})(qs, jQuery);
