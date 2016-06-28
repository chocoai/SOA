(function(qs, $) {
	js.$class("qs.m.SwitchBox", qs.ui.Control, function(){
		//注册qs事件
		qs.events.register("change", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._value = false;
		};
		js.$public.value = function(val){
			if( val === undefined )
				return this._value;
			this._value = val;
			if( this.isRendered() ){
				if( val == true ){
					this._$input.attr("checked", this._value);
					this._$switchBox.addClass("checked");
				}
				else{
					this._$input.removeAttr("checked");
					this._$switchBox.removeClass("checked");
				}
			}
		}
		js.$protected.template = function(options){
			var on = "是", off = "否";
			if( !js.isNull(options) ){
				if( js.type(options.on) == "string" )
					on = options.on;
				if( js.type(options.off) == "string" )
					off = options.off;
			}
			return StringUtil.join(
					'<label class="qs-m-switchbox switch-green">',
						'<input type="checkbox" class="switch-input">',
				      	'<span class="switch-label" data-on="', on, '" data-off="', off, '"></span>',
				      	'<span class="switch-handle"></span>',
				    '</label>'
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
				classes : '',
				position: ''
			}, this._options, options);
			
			this.$super($element, options);
			
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			this._$switchBox = $element.children(".qs-m-switchbox");
			this._$input = $element.find("input");
			this.bindEvent("change", function(){
				var oldValue = this._value;
				this.value(this._$input[0].checked);
				
				this.dispatchEvent("change", {oldValue:oldValue, newValue:this._value});
			}, this._$input);
			this.value(this.value());
		};
	});

})(qs, jQuery);
