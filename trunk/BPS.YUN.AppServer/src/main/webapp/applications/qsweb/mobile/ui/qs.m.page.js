(function(qs, $) {

	js.$class("qs.m.Page", qs.ui.Control, [qs.m.IPage, qs.ui.IContentControl], function(){
		//注册qs事件
		qs.events.register("loaded",    this);
		qs.events.register("active",    this);
		qs.events.register("actived",   this);
		qs.events.register("deactive",  this);
		qs.events.register("deactived", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._header  = null;
			this._footer  = null;
			this._content = null;
			this._title   = "";
			this._cache   = true;
			this.remoting = new qs.m.Remoting(this);
		};
		js.$public.cache = function(val){
			if( val === undefined )
				return this._cache;
			this._cache = val;
		}
		js.$public.header = function(val){
			if( val === undefined )
				return this._header;
			if( js.instanceOf(val, qs.ui.Control) ){
				this._header = val;
				if( this.isRendered() ){
					if( this._header.isRendered() ){
						this._$element.append(this._header.$element());
					}
					else {
						var $el = $("<div></div>");
						this._$element.append($el);
						this._header.render($el);
					}
					this._header.$element().addClass("page-header");
					this.bindEvent(qs.m.events.touchstart, this.onHeaderTouch, this._header.$element());
					this.update();
				}
			}
		}
		this.onHeaderTouch = function(event){
			if( js.instanceOf(this._content, qs.m.Scrollable) && qs.ui.Control.findOwnerControl(event.target) == this._header ){
				this._content.scrollTo(0, 0);
			}
		}
		js.$public.footer = function(val){
			if( val === undefined )
				return this._footer;
			if( js.instanceOf(val, qs.ui.Control) ){
				this._footer = val;
				if( this.isRendered() ){
					if( this._footer.isRendered() ){
						this._$element.append(this._footer.$element());
					}
					else {
						var $el = $("<div></div>");
						this._$element.append($el);
						this._footer.render($el);
					}
					this._footer.$element().addClass("page-footer");
					this.update();
				}
			}
		}
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
					this._content.$element().addClass("page-content");
					this.update();
				}
			}
		}
		this.update = function(){
			if( js.instanceOf(this._content, qs.ui.Control) && this._content.isRendered()){
				var top = 0; bottom = 0;
				if( js.instanceOf(this._header, qs.ui.Control) ){
					top += this._header.outerHeight();
				}
				if( js.instanceOf(this._footer, qs.ui.Control) ){
					bottom += this._footer.outerHeight();
				}
				if( js.instanceOf(this.parent(), qs.m.TabPage) ){
					bottom += this.parent().tabControl().outerHeight();
				}
				
				this._content.css({top:top, bottom:bottom});
			}
		}
		
		js.$public.title = function(val){
			if( val === undefined )
				return this._title;
			this._title = val;
			if( this.isRendered() && !js.isNull(this._header) ){
				var old = this._header.content();
				if( js.isNull(old) || js.type(old) == "string" )
					this._header.content(this._title);
			}
		}
		/**
		 * 执行入场动画
		 * @param animation 入场动画名称(enter | return)
		 * @param options 选项
		 */
		js.$public.animateIn = function(animation, options, callback){
			if( !this.isRendered() )
				return;
			var viewWidth;
			var parent = this.parent();
			if( js.instanceOf(parent, qs.ui.Control) )
				viewWidth = parent.outerWidth();
			else
				viewWidth = qs.ui.windowWidth();

			var self = this;
			//首次进入的入场动画
			if( animation == "enter" ){
				options = $.extend({
					from        : {x : viewWidth, y : 0},
					to          : {x : 0, y : 0},
					duration    : 0.4,
					timing_func : "ease-out",
					zIndex      : 101
				}, options);
			}
			//返回至本页的入场动画
			else if( animation == "return" ){
				options = $.extend({
					from        : {x : -viewWidth / 3, y : 0},
					to          : {x : 0, y : 0},
					duration    : 0.4,
					timing_func : "ease-out",
					zIndex      : 100
				}, options);
			}
			
			this._$element.css("transition", "none");

			var endFired = false;
			var onTransitionEnd = function() {
				if( endFired == true )
					return;
				endFired = true;
				self._$element.unbind(qs.events.transitionEnd);
				self.dispatchEvent("actived");
				if( js.type(callback) == "function" ){
					callback();
				}
				return false;
			}
			if( options.from == options.to || ( options.from.x == options.to.x && options.from.y == options.to.y )){
				self._$element.css({
					"transform" : "translate3d(" + options.to.x + "px, " + options.to.y + "px, 0px)",
					"display"   : "", 
					"z-index"   : options.zIndex
				});
				self.dispatchEvent("active");
				
				this._animateInTimeId = setTimeout(onTransitionEnd, options.duration * 1000);
			}
			else {
				//setTimeout可以防止手机锁屏之后打开 页面效果无效的问题
				setTimeout(function(){
					self._$element.css({
						"transform" : "translate3d(" + options.from.x + "px, " + options.from.y + "px, 0px)", 
						"display"   : "", 
						"z-index"   : options.zIndex
					});
					self.dispatchEvent("active");
					setTimeout(function(){
						self._$element.css({
							"transition" : "all " + options.duration + "s " + options.timing_func,
							"transform"  : "translate3d(" + options.to.x + "px, " + options.to.y + "px, 0px)"
						});
						self._$element.unbind(qs.events.transitionEnd);
						self._$element.bind(qs.events.transitionEnd, onTransitionEnd);
						if( self._animateOutTimeId > 0 )
							clearTimeout(self._animateInTimeId);
						self._animateInTimeId = setTimeout(onTransitionEnd, options.duration * 1000);
					}, 0);
				}, 0);				
			}
		}
		/**
		 * 执行出场动画
		 * @param animation 出场动画名称(enter | return)
		 * @param options 选项
		 */
		js.$public.animateOut = function(animation, options, callback){
			if( !this.isRendered() )
				return;
			var viewWidth;
			var parent = this.parent();
			if( js.instanceOf(parent, qs.ui.Control) )
				viewWidth = parent.outerWidth();
			else
				viewWidth = qs.ui.windowWidth();
			
			var options = $.extend({
				viewWidth   : viewWidth,
				duration    : 0.4,
				timing_func : "ease-out"
			}, options);
			
			var self = this;
			//由于其他页面进入而导致的出场动画
			if( animation == "enter" ){
				options = $.extend({
					from        : {x : 0, y : 0},
					to          : {x : -viewWidth / 3, y : 0},
					duration    : 0.4,
					timing_func : "ease-out",
					zIndex      : 100
				}, options);
			}
			//由于返回到其他页面而导致的出场动画
			else if( animation == "return" ){
				options = $.extend({
					from        : {x : 0, y : 0},
					to          : {x : viewWidth + 10, y : 0},
					duration    : 0.4,
					timing_func : "ease-out",
					zIndex      : 101
				}, options);
			}
			
			var endFired = false;
			var onTransitionEnd = function() {
				if( endFired == true )
					return;
				endFired = true;
				self._$element.unbind(qs.events.transitionEnd);
				self._$element.css("display", "none");
				self.dispatchEvent("deactived");
				if( js.type(callback) == "function" ){
					if( callback() == false )
						return false;
				}
				if( animation == "return" && self._cache == false ){
					qs.m.app.removePage(self);
				}
				return false;
			}
			
			this._$element.css("transition", "none");
			if( options.from == options.to || ( options.from.x == options.to.x && options.from.y == options.to.y )){
				self._$element.css({
					"transform" : "translate3d(" + options.to.x + "px, " + options.to.y + "px, 0px)",
					"z-index"   : options.zIndex
				});
				self.dispatchEvent("deactive");
				
				this._animateOutTimeId = setTimeout(onTransitionEnd, options.duration * 1000);
			}
			else {
				//setTimeout可以防止手机锁屏之后打开 页面效果无效的问题
				setTimeout(function(){
					self._$element.css({
						"transform" : "translate3d(" + options.from.x + "px, " + options.from.y + "px, 0px)",
						"z-index"   : options.zIndex
					});
					self.dispatchEvent("deactive");
					setTimeout(function(){
						self._$element.css({
							"transition" : "all " + options.duration + "s " + options.timing_func,
							"transform"  : "translate3d(" + options.to.x + "px, " + options.to.y + "px, 0px)"
						});
						
						self._$element.unbind(qs.events.transitionEnd);
						self._$element.bind(qs.events.transitionEnd, onTransitionEnd);
						if( self._animateOutTimeId > 0 )
							clearTimeout(self._animateOutTimeId);
						self._animateOutTimeId = setTimeout(onTransitionEnd, options.duration * 1000);
					}, 0);
				}, 0);
			}
		}
		js.$protected.template = function(options){
			return "";
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
				position: "absolute"
			}, this._options, options);
			
			$element.addClass("qs-m-page qs-m-ani");
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			
			this.$super($element, options);
			
			this.header(this._header);
			this.footer(this._footer);
			this.content(this._content);
			this.title(this._title);
			if( !js.isNull(this._header) && (js.isNull(this._title) || this._title == "") ){
				this._header.content(qs.m.app.title());
			}
			this.dispatchEvent("loaded");
			
			qs.Timer.callLater(100, function(){
				if( js.instanceOf(this._content, qs.ui.Container) )
					this._content.invalidateSize(true);
			}, this);
		};
	});
})(qs, jQuery)