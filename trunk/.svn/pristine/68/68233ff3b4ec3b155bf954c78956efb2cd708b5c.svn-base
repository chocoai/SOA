(function(qs, $) {
	
	js.$class("qs.app.AppHeader", qs.ui.Control, function(){
		//注册qs事件
		qs.events.register("change", this);
		
		this._items = [];
		this._selectedItem  = null;
		this._selectedIndex = -1;
		
		this.initialize = function(options){
			this.$super(options);
			this._$selectedEl = null;
			this._pos = 0;
		};
		js.$public.selectedIndex = function(index){
			if( index === undefined )
				return this._selectedIndex;
			if( !(index >= 0 && index < this._items.length) )
				index = -1;
			
			this._selectedIndex = index;
			if( this._$element != null && this._items != null ){
				if( index == -1 )
					this.selectedItem(null);
				else
					this.selectedItem(this._items[index]);
			}
		};
		js.$public.selectedItem = function(item, fireEvent){
			if( item === undefined )
				return this._selectedItem;
			
			fireEvent = js.param(fireEvent, true);
			
			var oldValue = this._selectedItem;
			this._selectedItem = item;
			if( this._$element != null && this._items != null ){
				var $el = this.findElement(item);
				if( $el == null || $el == undefined || $el.length == 0 )
					return;
				
				if( this._$selectedEl != null && this._$selectedEl[0] != $el[0] ){
					this._$selectedEl.removeClass("selected");
				}
				this._$selectedEl = $el;
				this._$selectedEl.addClass("selected");
				if( fireEvent == true && oldValue !== item )
					this.dispatchEvent("change", {oldValue:oldValue, newValue:item});
				
				var $header = this._$element.children(".qs-app-header");
				this._pos = -this._$selectedEl[0].offsetTop;
				qs.ui.animation.slide($header, {y: this._pos}, 0.3, "ease-out");
			}
		};
		this.findElement = function(item){
			return this._$element.find( ".qs-app-link" ).filter(function( index ) {
			    return $(this).data("data") == item;
			});
		};
		js.$public.items = function(val){
			if( val === undefined )
				return this._items;
			
			this._items = val;
			this.callAfterRendered(function(){
				var item, html, $el;
				var $header = this._$element.children(".qs-app-header");
				for( var i = 0; i < this._items.length; i++ ){
					item = this._items[i];
					html = StringUtil.join(
							'<div class="qs-app-link">',
								'<div class="border" />',
								'<img src="', wos.Desktop.getAbsoluteUrl(item.icon),'"/>',
								'<div class="title">', item.title, '</div>',
							'</div>'
						);
					$el = $(html);
					$el.data("data", item);
					$header.append($el);
				}
				if( $header[0].scrollHeight > $header[0].offsetHeight + 10 ){
					this._$element.children(".qs-app-header-more").css("display", "");
					this._$element.children(".qs-app-header-updown").css("display", "");
				}
			});
		};
		js.$public.updateSize = function(width, height){
			this.$super();
			if( !this.isRendered() )
				return;
			
			var $header = this._$element.children(".qs-app-header");
			
			if( $header[0].scrollHeight > $header[0].offsetHeight + 10 ){
				this._$element.children(".qs-app-header-more").css("display", "");
				this._$element.children(".qs-app-header-updown").css("display", "");
			}
			else{
				this._$element.children(".qs-app-header-more").css("display", "none");
				this._$element.children(".qs-app-header-updown").css("display", "none");
			}
		};
		js.$public.minimize = function(){
			var $header = this._$element.children(".qs-app-header");
			$header.addClass("min");
			this._$element.children(".qs-app-header-updown").addClass("min");
			this._minimized = true;
			this.height(30, false);
			
			this._pos = 0;
			qs.ui.animation.slide($header, {y: 0}, 0.3, "ease-out", function(){
				this.invalidateSize(true);
			}, this);
		}
		js.$public.maximize = function(){
			var $header = this._$element.children(".qs-app-header");
			$header.removeClass("min");
			this._$element.children(".qs-app-header-updown").removeClass("min");
			this._minimized = false;
			this.height(82, false);
			
			this._pos = 0;
			qs.ui.animation.slide($header, {y: 0}, 0.3, "ease-out", function(){
				this.invalidateSize(true);
			}, this);
		}
		js.$protected.template = function(options){
			return StringUtil.join(
				'<div class="qs-app-header"></div>',
				'<div class="qs-app-header-more" style="display:none">更多<div class="dropdown"></div></div>',
				'<div class="qs-app-header-updown" style="display:none">',
					'<div class="up" title="上翻"></div>',
					'<div class="down" title="下翻"></div>',
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
				classes : '',
				text : ''
			}, this._options, options);

			this.$super($element, options);
			this.bindEvent("mousedown", this.mouseDown);
			this.invalidateSize();
		};
		this.mouseDown = function(evt){
			var $element = $(evt.target).closest('.qs-app-link', this._$element);
			if( $element.length > 0 ){
				if( this._$selectedEl != null && $element[0] == this._$selectedEl[0] ){
					return;
				}
				var oldValue = null;
				if( this._$selectedEl != null){
					this._$selectedEl.removeClass("selected");
					oldValue = this._$selectedEl.data('data');
				}
				$element.addClass("selected");
				var newValue = $element.data('data');
				this._$selectedEl = $element;
				this.dispatchEvent("change", {oldValue:oldValue, newValue:newValue});
				return;
			}
			$element = $(evt.target).closest('.qs-app-header-more', this._$element);
			if( $element.length > 0 ){
				var board = new qs.app.AppHeaderBoard(this);
				board.items(this.items());
				board.show();
				return false;
			}
			$element = $(evt.target);
			var $header = this._$element.children(".qs-app-header");
			var height = this._$element.height() + 4;
			if( $element.is(".qs-app-header-updown .up")){
				if( this._pos < 0 ){
					this._pos = Math.min(0, this._pos + height);
					qs.ui.animation.slide($header, {y: this._pos}, 0.3, "ease-out");
				}
			}
			if( $element.is(".qs-app-header-updown .down")){
				if( this._pos > height - this._$element[0].scrollHeight){
					this._pos = Math.max(height - this._$element[0].scrollHeight, this._pos - height);
					qs.ui.animation.slide($header, {y: this._pos}, 0.3, "ease-out");
				}
			}
		};
	});
	
	js.$class("qs.app.AppHeaderBoard", qs.ui.Control, function(){
		this._header = null;
		this.initialize = function(header, options){
			this.$super(options);
			this._header = header;
		};
		js.$public.items = function(val){
			if( val === undefined )
				return this._items;
			
			this._items = val;
			this.callAfterRendered(function(){
				var item, html, $el;
				var $board = this._$element.find(".navboard");
				for( var i = 0; i < this._items.length; i++ ){
					item = this._items[i];
					html = StringUtil.join(
							'<div class="qs-app-link">',
								'<div class="border" />',
								'<img src="', wos.Desktop.getAbsoluteUrl(item.icon),'"/>',
								'<div class="title">', item.title, '</div>',
							'</div>'
						);
					$el = $(html);
					$el.data("data", item);
					$board.append($el);
				}
			});
		};
		js.$protected.template = function(options){
			return StringUtil.join(
				'<div class="qs-app-header-navboard"><div class="navboard"></div></div>'
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
			this.bindEvent("mousedown", this.mouseDown);
			this.addEventListener(qs.ui.Popup.POPUP_HIDE_EVENT, this.onPopupHide);
			this.invalidateSize();
		};
		this.mouseDown = function(evt){
			var $element = $(evt.target).closest('.qs-app-link', this._$element);
			if( $element.length > 0 ){
				var item = $element.data('data');
				this._header.selectedItem(item);
				qs.ui.popup.hide();
			}
		};
		js.$public.show = function(options){
			var x = (qs.ui.windowWidth() - 800) / 2;
			var options = $.extend({
				x : x,
				y : -1000,
				width  : 800,
				height : NaN,
				mask   : true,
				maskOpacity : 0.25,
				fitSize : false,
				classes : ""
			}, options);
			
			qs.ui.popup.show(this, options);
			var $element = qs.ui.popup.$element();
			if( options.mask == true ){
				var $mask = $('<div class="qs-popup-mask"></div>');
				$mask.width(qs.ui.windowWidth());
				$mask.height(qs.ui.windowHeight());
				$mask.insertBefore($element);
				
				this._$mask = $mask;
			}
			var height = $element.height();
			$element.css("top", -height);
			
			if( !js.isNull(this._$mask) )
				this._$mask.css("opacity", options.maskOpacity);
			qs.ui.animation.slide($element, {y: height + 100}, 0.3, "ease-out");
		}
		this.onPopupHide = function(event){
			event.preventDefault();
			
			var $element = qs.ui.popup.$element();
			if( !js.isNull(this._$mask) )
				this._$mask.css("opacity", 0);
			var complete = function(){
				if( !js.isNull(this._$mask) ){
					this._$mask.remove();
					this._$mask = null;
				}
				qs.ui.popup.hide();
			};
			qs.ui.animation.slide($element, {y: -$element.height()}, 0.3, "ease-out", complete, this);
		}
	});
	
})(qs, jQuery);
