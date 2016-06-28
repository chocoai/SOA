(function(qs, $) {
	js.$class("qs.m.PopupView", qs.ui.Control, [qs.ui.IContentControl], function(){
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._content = null;
			this._$mask = null;
		};
		js.$public.content = function(val){
			if( val === undefined )
				return this._content;
			if( js.instanceOf(val, qs.ui.Control) ){
				this._content = val;
				if( this.isRendered() ){
					if( this._content.isRendered() ){
						this._$element.append(this._content.$element());
					}
					else {
						var $el = $("<div></div>");
						this._$element.append($el);
						this._content.render($el);
					}
				}
			}
		}
		js.$protected.template = function(options){
			return "";
		};

		js.$protected.onRendered = function($element, options){
			$element.addClass("qs-m-popupview");
			
			this.content(this._content);
			this.addEventListener(qs.ui.Popup.POPUP_HIDE_EVENT, this.onPopupHide);
		};
		
		/**
		 * 显示弹出控件
		 * @param [dock = "bottom"] {string} 显示时的依靠位置，可选值为left,right,top,bottom
		 * @param [options] 选项
		 * @param [options.width] {number} 弹出控件显示的宽度
		 * @param [options.height] {number} 弹出控件显示的高度
		 * @param [options.movePage = false] {boolean} 显示时是否同时移动页面
		 * @param [options.movePage = false] {boolean} 显示时是否同时移动页面
		 * @param [options.mask = true] {boolean} 是否显示遮罩
		 * @param [options.maskOpacity = 0.25] {number} 遮罩的透明度(0~1)
		 */
		js.$public.show = function(dock, options){
			var windowWidth  = qs.ui.windowWidth();
			var windowHeight = qs.ui.windowHeight();
			
			var options = $.extend({
				x : 0,
				y : windowHeight + 10,
				width  : NaN,
				height : NaN,
				mask   : true,
				maskOpacity : 0.25,
				classes : ""
			}, options);
			
			this._dock = js.param(dock, "up");
			
			qs.ui.popup.show(this, options);
			var $element = qs.ui.popup.$element();
			
			if( options.mask == true ){
				var $mask = $('<div class="qs-popup-mask"></div>');
				$mask.width(windowWidth);
				$mask.height(windowHeight);
				$mask.insertBefore($element);
				
				this._$mask = $mask;
			}
			
			var x, y, pagePos = {};
			switch( this._dock ){
				case "top":
				case "down":
					$element.css({top : -$element.outerHeight(), width : windowWidth});
					x = 0;
					y = $element.outerHeight();
					pagePos.y = y;
					break;
				case "left":
					$element.css({left : -$element.outerWidth(), top : 0, height : windowHeight});
					x = $element.outerWidth();
					y = 0;
					pagePos.x = x;
					break;
				case "right":
					$element.css({left : windowWidth, top : 0, height : windowHeight});
					x = -$element.outerWidth();
					y = 0;
					pagePos.x = x;
					break;
				case "bottom":
				case "up":
					$element.css({top : windowHeight, width : windowWidth});
					x = 0;
					y = -$element.outerHeight();
					pagePos.y = y;
					break;
				default:
					return;
			}
			if( !js.isNull(this._$mask) )
				this._$mask.css("opacity", options.maskOpacity);
			qs.ui.animation.slide($element, {x:x, y:y}, 0.3, "ease-out");
			
			this._movePage = false;
			if( options.movePage == true ){
				this._movePage = true;
				qs.ui.animation.slide(qs.m.app.activePage().$element(), pagePos, 0.3, "ease-out");
			}
		}
		js.$public.close = function(){
			qs.ui.popup.hide();
		}
		
		this.onPopupHide = function(event){
			event.preventDefault();
			
			var $element = qs.ui.popup.$element();
			if( !js.isNull(this._$mask) )
				this._$mask.css("opacity", 0);
			if( this._movePage == true )
				qs.ui.animation.slide(qs.m.app.activePage().$element(), {x:0, y:0}, 0.3, "ease-out");
			qs.ui.animation.slide($element, {x:0, y:0}, 0.3, "ease-out", function(){
				if( js.instanceOf(this._content, qs.ui.Control) )
					this._content.$element().detach();
				if( !js.isNull(this._$mask) ){
					this._$mask.remove();
					this._$mask = null;
				}
				qs.ui.popup.hide();
				this.isRendered(false);
			}, this);
		}
	});
})(qs, jQuery);
