(function(qs, $) {
	js.$class("qs.m.SwapView", qs.m.Scrollable, function(){
		qs.events.register("change", this);
		qs.events.register("changed", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			
			this._items = [];
			this._itemRenderer = null;
			this.constraint = false;
			this._v = false;
			this._h = true;
			this.weight = 10;
		};
		js.$public.itemRenderer = function(value){
			if( value === undefined )
				return this._itemRenderer;
			
			this._itemRenderer = value
		};
		js.$public.items = function(data){
			if( data === undefined )
				return this._items;
			
			if( !(data == null || qs.type(data) == "array" || js.instanceOf(data, qs.ArrayList)) )
				throw new TypeError("List.items的值必须是array或qs.ArrayList类型.");
			this._items = data;
			this.callAfterRendered(function(){
				if( this._$scrollable )
					this._$scrollable.empty();
				if( this._$indicator )
					this._$indicator.empty();
				var len = data.length;
				data.forEach(function(item, index){
					var $item = null;
					if( js.type(this._itemRenderer) == "function" ){
						$item = $('<div class="swap-item"></div>');
						this._$scrollable.append($item);
						this._itemRenderer($item, item, index, len);
					}
					else if(item.image) {
						$item = $('<div class="swap-item"><img src="' + item.image + '"></img></div>');
						this._$scrollable.append($item);
					}
					else if( item.html ){
						$item = $('<div class="swap-item">' + item.html + '</div>');
						this._$scrollable.append($item);
					}
					else if( js.instanceOf(item.control, qs.ui.Control) ){
						$item = $('<div class="swap-item"></div>');
						if( !item.control.isRendered() ){
							item.control.render($("<div/>"));
						}
						$item.append(item.control.$element());
						this._$scrollable.append($item);
					}
					if( $item != null )
						$item.data("data", item);
				}, this);
				this._$currentItem = this._$scrollable.children(".swap-item:first-child");
				this.createIndicator();
				this.updateIndicator(this._$currentItem);
			});
		};
		js.$protected.template = function(options){
			return StringUtil.join(
					'<div class="qs-m-swap"></div>'
				);
		};
		/**
		 * 生成控件
		 * @param $element 承载控件的元素
		 * @param options 选项, {classes:附加样式类}
		 */
		js.$public.render = function($element, options){
			var options = $.extend(this._options, options, {
				classes : '',
				position: "relative"
			});
			this.$super($element, options);
			
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			
			this._$scrollable = this._$element.children(".qs-m-swap");
			this._$currentItem = this._$scrollable.children(".swap-item:first-child");
			this.updateIndicator(this._$currentItem);
		}
		js.$protected.onRendered = function($element, options){
			this._$scrollable = this._$element.children(".qs-m-swap");
		}
		js.$protected.createIndicator = function(){
			if( this._$indicator ){
				this._$indicator.remove();
			}
			var html = '<div class="qs-m-indicator">';
			var len = this._$scrollable.children(".swap-item").length;
			for( var i = 0; i < len; i++)
				html += '<div class="indicator-item"></div>'
			html += "</div>";
			this._$indicator = $(html);
			this._$scrollable.parent().append(this._$indicator);
		}
		js.$protected.showScrollBar = function(){
		}
		js.$protected.updateIndicator = function($item){
			if( !this._$indicator ){
				this.createIndicator();
			}
			var index = this._$scrollable.children(".swap-item").index($item) + 1;
			var $indicatorItem = this._$indicator.children(":nth-child(" + index + ")");
			$indicatorItem.addClass("selected");
			if( this._$curIndicatorItem && !this._$curIndicatorItem.is($indicatorItem) )
				this._$curIndicatorItem.removeClass("selected");
			this._$curIndicatorItem = $indicatorItem;
		}
		js.$protected.slideTo = function(pos, to, duration/*Number*/, easing/*String*/){
			if( !this._$currentItem )
				this._$currentItem = this._$scrollable.children(".swap-item:first-child");
			if( this._$currentItem.length == 0 )
				return;
			var curpos = this._$currentItem.offset().left - this._$scrollable.offset().left;
			var tox    = -to.x;
			var width  = this._$currentItem.width();
			
			var item = null;
			if( curpos - tox > width / 2 )
				item = this.getPrevItem();
			else if( tox - curpos > width / 2 )
				item = this.getNextItem();
			else
				item = this._$currentItem;
			
			duration = 0.3;
			easing = "ease-out";
			if( item == null || item.length == 0 ){
				if( this._$currentItem )
					item = this._$currentItem;
				else
					item = this._$scrollable.children(".swap-item:first-child");
			}
			
			if( item != null && item.length > 0 ){
				to.x = -(item.offset().left - this._$scrollable.offset().left);
				var oldItem = this._$currentItem.data("data");
				var newItem = item.data("data");
				this._$currentItem = item;
				qs.ui.animation.slide(this._$scrollable, to, duration, easing, function(){
					this.dispatchEvent("changed", {oldValue : oldItem, newValue : newItem});
				}, this);
				this.dispatchEvent("change", {oldValue : oldItem, newValue : newItem});
			}
			this.updateIndicator(this._$currentItem);
		}
		this.getNextItem = function(){
			if( this._$currentItem )
				return this._$currentItem.next();
			else
				return this._$scrollable.children(".swap-item:first-child");
		}
		this.getPrevItem = function(){
			if( this._$currentItem )
				return this._$currentItem.prev();
		}
		js.$public.selectedItem = function(val, animation){
			if( val === undefined ){
				if( this._$currentItem )
					return this._$currentItem.data("data");
				return null;
			}
			animation = js.param(animation, false);
			
			var items = this._$scrollable.children(".swap-item");
			var $item;
			for( var i = 0; i < items.length; i++ ){
				$item = $(items[i]);
				if( $item.data("data") == val ){
					if( this._$currentItem == $item )
						return;
					var x = -($item.offset().left - this._$scrollable.offset().left);
					var oldItem = this._$currentItem.data("data");
					var newItem = $item.data("data");
					this._$currentItem = $item;
					if( animation == true ){
						qs.ui.animation.slide(this._$scrollable, {x : x}, 0.3, "ease-out");
					}
					else {
						this._$scrollable.css({
							"transform" : "translate(" + x + "px, 0px) translateZ(0px)"
						})
					}
					this.dispatchEvent("change", {oldValue : oldItem, newValue : newItem});
					this.updateIndicator(this._$currentItem);
					return;
				}
			}
		}
	});
})(qs, jQuery);
