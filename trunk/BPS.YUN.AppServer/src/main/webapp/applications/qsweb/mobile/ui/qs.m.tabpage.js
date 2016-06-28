(function(qs, $) {
	js.$class("qs.m.TabPage", qs.ui.Control, [qs.m.IPage, qs.ui.ITabView], function(){
		//注册qs事件
		qs.events.register("loaded", this);
		qs.events.register("active", this);
		qs.events.register("deactive", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._title  = "";
			this._header = null;
			this._items  = [];
			this._currentPage = null;
			this._cache   = true;
			this._tabControl = new qs.m.ButtonBar({classes:"qs-m-tabbar"});
			this._tabControl.toggleEnabled(true);
			this._tabControl.equalWidth(true);
			this._tabControl.addEventListener("change", this.onTabChange, this);
			this._tabControl.addEventListener("buttonClick", this.onTabClick, this);
			
			this.addEventListener("loaded", this.onPageLoaded);
			
			this.remoting = new qs.m.Remoting(this);
		};
		js.$public.cache = function(val){
			if( val === undefined )
				return this._cache;
			this._cache = val;
		}
		js.$public.tabControl = function(){
			return this._tabControl;
		}
		js.$public.visible = function(value){
			if( value === undefined )
				return this.$super();
			this.$super(value);
			this._tabControl.visible(value);
		}
		this.onPageLoaded = function(event, args){
			if( event.target() != this ){
				event.stopPropagation();
				event.stopImmediatePropagation();
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
		js.$public.items = function(value){
			if( value === undefined )
				return this._items;
			this._items = value;
		};
		js.$public.addItem = function(item){
			var index = this._items.indexOf(item);
			if( index != -1 )
				return;
			this._items.push(item);
			if( js.instanceOf(item.content, qs.ui.Control) ){
				if( this.isRendered() ){
					var $view = this._$element.children(".qs-m-tabview");
					if( item.content.isRendered() ){
						$view.append(item.content.$element());
						item.content.$element().addClass("content");
					}
					else {
						var $el = $('<div style="display:none" class="content"></div>');
						$view.append($el);
						item.content.render($el);
					}
					var margin = {};
					if( !js.isNull(this._header) ){
						if( !js.instanceOf(item.content, qs.m.IPage) || js.isNull(item.content.header()) )
							margin.top = this._header.outerHeight();
					}
					margin.bottom = this._tabControl.outerHeight();
					item.content.margin(margin);
					if( js.type(item.style) == "object" )
						item.content.$element().css(item.style);
				};
			}
			else
				item.toggle = false;
			this._tabControl.addItem(item);
		};
		js.$public.removeItem = function(item){
			this._tabControl.removeItem(item);
			if( js.instanceOf(item.content, qs.ui.Control) )
				item.content.remove();
		};
		js.$public.selectedItem = function(item){
			if( item === undefined )
				return this._tabControl.selectedItem();
			this._tabControl.selectedItem(item);
		};
		js.$public.header = function(val){
			if( val === undefined )
				return this._header;
			this._header = val;
		}
		/**
		 * 执行入场动画
		 * @param animation 入场动画名称(enter | return)
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
			
			this._$element.css("transition", "none");
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
			
			var $tab = this._tabControl.$element();
			var height = $tab.height();
			$tab.removeClass("qs-m-ani");
			$tab.css({"transform" : "translate3d(0px, " + height + "px, 0px)", "display" : ""});
			setTimeout(function(){
				$tab.addClass("qs-m-ani");
				$tab.css("transform", "translate3d(0px, 0px, 0px)");
			}, 0);
		}
		/**
		 * 执行出场动画
		 * @param animation 出场动画名称(enter | return)
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
					self._tabControl.remove();
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
			
			var $tab = this._tabControl.$element();
			var height = $tab.height();
			$tab.removeClass("qs-m-ani");
			$tab.css("transform", "translate3d(0px, 0px, 0px)");
			setTimeout(function(){
				$tab.addClass("qs-m-ani");
				$tab.css({"transform" : "translate3d(0px, " + height + "px, 0px)", "display" : ""});
			}, 0);
		};
		js.$public.updateSize = function(width, height){
			this.$super(width, height);
			var $view = this._$element.children(".qs-m-tabview");
			$view.children().css("height", height - this._tabControl.outerHeight());
		};
		js.$protected.template = function(options){
			return StringUtil.join(
					'<div class="qs-m-tabview"></div>'
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
				position: "absolute"
			}, this._options, options);
			
			this.$super($element, options);
		};
		js.$protected.onRendered = function($element, options){
			$element.addClass("qs-m-tabpage");
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			var $tab = $('<div class="qs-m-tab"></div>');
			qs.m.app.$element().append($tab);
			if( !this.visible() || $element.css("display") == "none" )
				$tab.css("display", "none");
			this._tabControl.render($tab);

			var $view = this._$element.children(".qs-m-tabview");
			if( js.instanceOf(this._header, qs.ui.Control) ){
				if( this._header.isRendered() ){
					$view.before(this._header.$element());
				}
				else {
					var $el = $("<div></div>");
					$view.before($el);
					this._header.render($el);
				}
				this._header.$element().addClass("page-header");
			}

			var item;
			for( var i = 0; i < this._items.length; i++ ){
				item = this._items[i];
				if( js.instanceOf(item.content, qs.ui.Control) ){
					if( item.content.isRendered() ){
						$view.append(item.content.$element());
						item.content.$element().addClass("content");
					}
					else {
						var $el = $('<div style="display:none" class="content"></div>');
						$view.append($el);
						item.content.render($el);
					}
					var margin = {};
					if( !js.isNull(this._header) ){
						if( !js.instanceOf(item.content, qs.m.IPage) || js.isNull(item.content.header()) )
							margin.top = this._header.outerHeight();
					}
					margin.bottom = this._tabControl.outerHeight();
					item.content.margin(margin);
					if( js.type(item.style) == "object" )
						item.content.$element().css(item.style);
				}
			}
			
			if( js.isNull(this.selectedItem()) && this._items.length > 0 ){
				this.selectedItem(this._items[0]);
			}
			if( !js.isNull(this._header) ){
				if( !js.isNull(this._title) && this._title != "")
					this._header.content(this._title);
				else
					this._header.content(qs.m.app.title());
				this.bindEvent(qs.m.events.touchstart, this.onHeaderTouch, this._header.$element());
			}
			this.dispatchEvent("loaded");
		}
		this.onHeaderTouch = function(event){
			var content = this._currentPage;
			if( js.instanceOf(this._currentPage, qs.m.IPage) )
				content = this._currentPage.content();
			if( js.instanceOf(content, qs.m.Scrollable) && qs.ui.Control.findOwnerControl(event.target) == this._header ){
				content.scrollTo(0, 0);
			}
		}
		this.onTabChange = function(event, args){
			var item = args.newValue;
			if( this._currentPage == null ){
				this._currentPage = item.content;
				this._currentPage.$element().css("transform", "translate3d(0px, 0px, 0px)");
				this._currentPage.visible(true);
				return;
			}
			this._currentPage = item.content;
			
			var oldIndex = this._items.indexOf(args.oldValue);
			var newIndex = this._items.indexOf(args.newValue);
			
			var transation;
			if( oldIndex < newIndex )
				transation = "left";
			else
				transation = "right";
			
			this.switchPage(args.oldValue.content.$element(), args.newValue.content.$element(), transation);
			if( !js.isNull(this._header) ){
				if( js.instanceOf(this._currentPage, qs.m.IPage) && !js.isNull(this._currentPage.header()) ){
					this.hideHeader(transation);
				}
				else {
					this.showHeader(transation);
				}
			}
		}
		this.hideHeader = function(transation){
			if( !this._header.visible() )
				return;
			transation = js.param(transation, "left");
			var viewWidth = this._$element.width();
			var $header = this._header.$element();
			
			$header.removeClass("qs-m-ani");
			
			if( transation == "left") {
				$header.css({"transform" : "translate3d(0px, 0px, 0px)", "z-index" : 100});
				
				setTimeout(function(){
					$header.addClass("qs-m-ani");
					$header.css("transform", "translate3d(" + (-viewWidth / 3) + "px, 0px, 0px)");
				}, 0);
			}
			else if( transation == "right") {
				$header.css({"transform" : "translate3d(0px, 0px, 0px)", "z-index" : 100});
				
				setTimeout(function(){
					$header.addClass("qs-m-ani");
					$header.css("transform", "translate3d(" + viewWidth + "px, 0px, 0px)");
				}, 0);
			}
			var self = this;
			$header.unbind(qs.events.transitionEnd);
			$header.bind(qs.events.transitionEnd, function() {
				self._header.visible(false);
				$header.unbind(qs.events.transitionEnd);
			});
		}
		this.showHeader = function(transation){
			if( this._header.visible() )
				return;
			
			transation = js.param(transation, "left");
			var viewWidth = this._$element.width();
			var $header = this._header.$element();

			$header.removeClass("qs-m-ani");
			
			if( transation == "left") {
				$header.css({"transform" : "translate3d(" + viewWidth + "px, 0px, 0px)", "display" : "", "z-index" : 900});
				this._header.visible(true);
				setTimeout(function(){
					$header.addClass("qs-m-ani");
					$header.css("transform", "translate3d(0px, 0px, 0px)");
				}, 0);
			}
			else if( transation == "right") {
				$header.css({"transform" : "translate3d(" + ( -viewWidth / 3 ) + "px, 0px, 0px)", "display" : "", "z-index" : 900});
				this._header.visible(true);
				setTimeout(function(){
					$header.addClass("qs-m-ani");
					$header.css("transform", "translate3d(0px, 0px, 0px)");
				}, 0);
			}
		}
		this.onTabClick = function(event, args){
			var buttonEl = args.button;
			var item = args.data;
		}
		/**
		 * 切换两个页面
		 * @param $pageFrom 当前显示的页面
		 * @param $pageTo 奖要显示的页面
		 * @param transation 切换方向(left | right)
		 */
		this.switchPage = function($pageFrom, $pageTo, transation){
			transation = js.param(transation, "left");
			var viewWidth = this._$element.width();

			$pageFrom.removeClass("qs-m-ani");
			$pageTo.removeClass("qs-m-ani");
			
			if( transation == "left") {
				$pageTo.css({"transform" : "translate3d(" + viewWidth + "px, 0px, 0px)", "display" : "", "z-index" : 101});
				$pageFrom.css({"transform" : "translate3d(0px, 0px, 0px)", "z-index" : 100});
				
				setTimeout(function(){
					$pageTo.addClass("qs-m-ani");
					$pageFrom.addClass("qs-m-ani");

					$pageTo.css("transform", "translate3d(0px, 0px, 0px)");
					$pageFrom.css("transform", "translate3d(" + (-viewWidth ) + "px, 0px, 0px)");
				}, 0);
			}
			else if( transation == "right") {
				$pageTo.css({"transform" : "translate3d(" + ( -viewWidth ) + "px, 0px, 0px)", "display" : "", "z-index" : 101});
				$pageFrom.css({"transform" : "translate3d(0px, 0px, 0px)", "z-index" : 100});
				
				setTimeout(function(){
					$pageTo.addClass("qs-m-ani");
					$pageFrom.addClass("qs-m-ani");

					$pageTo.css("transform", "translate3d(0px, 0px, 0px)");
					$pageFrom.css("transform", "translate3d(" + ( viewWidth ) + "px, 0px, 0px)");
				}, 0);
			}
			$pageFrom.unbind(qs.events.transitionEnd);
			$pageFrom.bind(qs.events.transitionEnd, function() {
				$pageFrom.css("display", "none");
				$pageFrom.unbind(qs.events.transitionEnd);
			});
		}
	});
})(qs, jQuery);
