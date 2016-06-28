(function(qs, $) {
	var page_id = 1;
	
	js.$interface("qs.m.IPage", function(){
		js.$public.title = function(value){};
		js.$public.header = function(value){};
		js.$public.animateIn = function(animation){};
		js.$public.animateOut = function(animation){};
	});
	
	js.$class("qs.m.App", qs.ui.Control, function(){
		//注册qs事件
		qs.events.register("deviceready", this);
		qs.events.register("pause", this);
		qs.events.register("resume", this);
		qs.events.register("backbutton", this);
		qs.events.register("menubutton", this);
		qs.events.register("searchbutton", this);

		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._title = "";
			this._activePage = null;
			this._history = [];
			this._pages = {};
			this._includePage = {};
			this._relativeUrl = baseUrl;
			this._isLoaded = false;
			this._zoom = 1;
			
			var self = this;
			document.addEventListener("deviceready", function(){
				self.dispatchEvent("deviceready");
				
				document.addEventListener("pause", function(){
					self.dispatchEvent("pause");
				}, false);
				document.addEventListener("resume", function(){
					self.dispatchEvent("resume");
				}, false);
				document.addEventListener("backbutton", function(){
					self.dispatchEvent("backbutton");
				}, false);
				document.addEventListener("menubutton", function(){
					self.dispatchEvent("menubutton");
				}, false);
				document.addEventListener("searchbutton", function(){
					self.dispatchEvent("searchbutton");
				}, false);
				
			}, false);
		};
		js.$public.title = function(val){
			if( val === undefined )
				return this._title;
			
			this._title = val;
		}
		js.$public.relativeUrl = function(val){
			if( val === undefined )
				return this._relativeUrl;
			
			this._relativeUrl = val;
		}
		js.$public.zoom = function(val){
			if( val === undefined )
				return this._zoom;
			
			if( js.type(val) == "number" ){
				this._zoom = val;
				$("body").css("zoom", val);
			}
		}
		/**
		 * 获取绝对地址
		 * @param url 相对地址
		 */
		js.$public.getAbsoluteUrl = function(url/*:String*/)/*:String*/{
			if( !js.isNull(url) && url.indexOf("http") == 0 )
				return url;
			var u/*:String*/ = this._relativeUrl;
			if( url == null || url.length == 0 )
				return u;
			if( u.charAt(u.length - 1) == "/" && url.charAt(0) == "/" )
				return u + url.substr(1);
			else if( u.charAt(u.length - 1) != "/" && url.charAt(0) != "/" )
				return u + "/" + url;
			else
				return u + url;
		};
		js.$public.activePage = function(page){
			if( page === undefined )
				return this._activePage;
			if( js.type(page) == "string" ){
				var name = page;
				page = this.getPage(name);
				if( !js.instanceOf(page, qs.m.IPage) ){
					var pageXml = this._includePage[name];
					if( js.instanceOf(pageXml, qs.Xml) ){
						this.loadPage(pageXml.attr("url"), {name:name, pageXml:pageXml});
					}
					return;
				}
			}
			if( this._$screen.children().length > 0 )
				return;
			if( js.instanceOf(page, qs.m.IPage) && this._activePage != page && page.isRendered() ){
				var viewWidth = qs.ui.windowWidth();
				var screenLeft = -viewWidth / 3;
				var startTime = 0;
				var options = {
					from : { x : viewWidth, y : 0 },
					to   : { x : viewWidth / 3, y : 0},
					timing_func : "cubic-bezier(0.36, 0.66, 0.04, 1)",
					duration : 0.4
				};
				
				if( qs.ui.device.os == "android" ){
					options.from = { x : viewWidth, y : 0},
					options.to   = { x : viewWidth, y : 0}
					//options.timing_func = "linear";
					options.duration = 0.7;
					screenLeft = -viewWidth;
					startTime  = 300;
				}
				
				this._$screen.css({
					"transition" : "none",
					"transform"  : "translate3d(0px, 0px, 0px)"
				});
				this._$screen.append(this._activePage.$element());
				this._activePage.$element().css({
					"z-index" : 100
				});
				this._$screen.append(page.$element());
				page.$element().css({
					"transition" : "none",
					"transform"  : "translate3d(" + viewWidth + "px, 0px, 0px)",
					"display"    : "",
					"z-index"    : 101
				});
				
				var activePage = this._activePage;
				var self = this;
				var onFinished = function(){
					activePage.$element().css("display", "none");
					
					self._$element.append(activePage.$element());
					self._$element.append(page.$element());
					page.$element().css({
						"transition" : "none",
						"transform"  : "translate3d(0px, 0px, 0px)",
						"z-index"    : 100
					});
					self._$screen.css({
						"transition" : "none",
						"transform"  : "translate3d(0px, 0px, 0px)"
					});
					page.invalidateSize(true);
				}
				setTimeout(function(){
					var entered = false, outered = false, slided = false;
					page.animateIn("enter", options, function(){
						entered = true;
						if( outered == true && slided == true ){
							onFinished();
						}
					});
					
					options = {
						duration : 0.5,
						from : {x : 0, y : 0},
						to   : {x : 0, y : 0}
					};
					
					activePage.animateOut("enter", options, function(){
						outered = true;
						if( entered == true && slided == true ){
							onFinished();
						}
					});
					
					self._history.push(activePage);
					setTimeout(function(){
						qs.ui.animation.slide(self._$screen, {x:screenLeft}, 0.4, "ease-out", function(){
							slided = true;
							if( entered == true && outered == true ){
								onFinished();
							}
						});
					}, 0);
					
					self._activePage = page;
				}, startTime);
			}
		}
		js.$public.removePage = function(page){
			if( this._activePage == page )
				return;
			for( var i = this._history.length - 1; i >= 0; i-- ){
				if( this._history[i] == page ){
					this._history.splice(i, 1);
				}
			}
			for( var n in this._pages ){
				if( this._pages[n] == page )
					delete this._pages[n];
			}
			page.remove();
		}
		/**
		 * 回退
		 * @param page 回退到的页面名称或页面对象(可选),默认回退到上一个页面
		 */
		js.$public.back = function(page){
			if( js.type(page) == "string" )
				page = this.getPage(page);
			if( js.instanceOf(page, qs.m.IPage) ){
				for( var i = this._history.length - 1; i >= 0; i-- ){
					if( this._history[i] === page ){
						this._history.length = i + 1;
						break;
					}
				}
			}
			if( this._$screen.children().length > 0 )
				return;

			page = this._history.pop();
			while( js.isNull(page) || !page.isRendered() ){
				if( this._history.length == 0 )
					break;
				page = this._history.pop();
			}
			if( !js.isNull(page) && page.isRendered() ){
				/*
				var options = null;
				if( qs.ui.device.os == "android" ){
					options = {
						from : { x : 0, y : 0 },
						to   : { x : 0, y : 0}
					};
				}
				page.animateIn("return", options);
				if( this._activePage ){
					options = null;
					if( qs.ui.device.os == "android" ){
						options = {
							timing_func : "linear",
							duration    : 0.5
						};
					}
					this._activePage.animateOut("return", options);
				}
				
				this._activePage = page;
				*/
				var viewWidth = qs.ui.windowWidth();
				var screenLeft = -viewWidth / 3;
				var startTime  = 0;
				var options = {
					duration : 0.4,
					from : {x : 0, y : 0},
					timing_func : "cubic-bezier(0.36, 0.66, 0.04, 1)",
					to   : {x : 0, y : 0}
				};
				if( qs.ui.device.os == "android" ){
					screenLeft = -viewWidth;
					startTime  = 300;
				}
				this._$screen.css({
					"transition" : "none",
					"transform"  : "translate3d(" + screenLeft + "px, 0px, 0px)"
				});
				this._$screen.append(this._activePage.$element());
				this._activePage.$element().css({
					"transition" : "none",
					"transform"  : "translate3d(" + (-screenLeft) + "px, 0px, 0px)",
					"display"    : "",
					"z-index"    : 101
				});
				
				this._$screen.append(page.$element());
				page.$element().css({
					"transition" : "none",
					"transform"  : "translate3d(0px, 0px, 0px)",
					"display"    : "",
					"z-index"    : 100
				});
				
				var activePage = this._activePage;
				var self = this;
				var onFinished = function(){
					activePage.$element().css("display", "none");
					
					self._$element.append(activePage.$element());
					self._$element.append(page.$element());
					page.$element().css({
						"transition" : "none",
						"transform"  : "translate3d(0px, 0px, 0px)",
						"z-index"    : 100
					});
					self._$screen.css({
						"transition" : "none",
						"transform"  : "translate3d(0px, 0px, 0px)"
					});
					page.invalidateSize(true);
				}
				setTimeout(function(){
					var entered = false, outered = false, slided = false;
					page.animateIn("return", options, function(){
						entered = true;
						if( outered == true && slided == true ){
							onFinished();
						}
					});
					
					options = {
						from : { x : viewWidth / 3, y : 0 },
						to   : { x : viewWidth, y : 0},
						timing_func : "cubic-bezier(0.36, 0.66, 0.04, 1)",
						duration : 0.5
					};
					
					if( qs.ui.device.os == "android" ){
						options.from = { x : viewWidth, y : 0},
						options.to   = { x : viewWidth, y : 0}
						//options.timing_func = "linear";
						options.duration = 0.7;
					}
					
					activePage.animateOut("return", options, function(){
						outered = true;
						if( entered == true && slided == true ){
							onFinished();
						}
					});
					
					setTimeout(function(){
						qs.ui.animation.slide(self._$screen, {x:0}, 0.4, "ease-out", function(){
							slided = true;
							if( entered == true && outered == true ){
								onFinished();
							}
						});
					}, 0);
					
					self._activePage = page;
				}, startTime);
				return page;
			}
			return null;
		}
		this.onIncludePageLoaded = function(url, options, content){
			var page = this._pages[url];
			if( js.instanceOf(page, qs.ui.Control) ) {
				page.remove();
			}
			try{
				var xml = new qs.Xml(content);
				if( !js.isNull(options) && !js.isNull(options.name) )
					xml.attr("name", options.name);
				page = this.createPage(xml);
			}
			catch(e){
				if( js.type(options.error) == "function" )
					options.error("页面装入失败:" + e.message, e);
				else
					qs.m.MessageBox.show("页面装入失败:" + e.message);
				return;
			}
			
			this._pages[url] = page;
			
			if( !js.isNull(options) && !js.isNull(options.name) )
				page.name(options.name);
			this.addPage(page);

			this.activePage(page);

			if( js.type(options.success) == "function")
				options.success();
		}
		js.$public.loadPage = function(url, options){
			var options = $.extend({
				reload : false
			}, options);
			
			var page = this._pages[url];
			var self = this;
			if(options.reload || !page ){
				if( this._loadingUrl == url ){
					return;
				}
				qs.m.waitingBox.open();
				this._loadingUrl = url;
				var activePage = this._activePage;
				$.ajax({
					url : url, 
					type : "POST", 
					async : true,
					data  : {"_" : new Date().getTime()},
					dataType: "text",
					success : function(content){
						if( self.activePage() == activePage )
							js.callInternal(self, self.onIncludePageLoaded, [url, options, content]);
					},
					error : function(jqXHR, textStatus, errorThrown){
						if( js.type(options.error) == "function" )
							options.error(textStatus, errorThrown);
						else
							qs.m.MessageBox.show("页面装入失败:" + textStatus);
					},
					complete : function(jqXHR, textStatus){
						self._loadingUrl = null;
						qs.m.waitingBox.close();
					}
				})
			}
			else {
				this.activePage(page);
				if( js.type(options.success) == "function")
					options.success();
			}
		}
		function findNS(ns){
			var parts = ns.split(".");
			var parent = window;
			for( var i = 0; i < parts.length; i++){
				parent = parent[parts[i]];
				if( parent === undefined || parent === null )
					return null;
			}
			return parent;
		}
		this.createPage = function(xml){
			var name  = xml.name();
			var nsURI = xml.nsURI();
			if( nsURI === undefined || nsURI === null || nsURI === "" )
				nsURI = "qs.m";
			var ns = findNS(nsURI);
			if( ns === null || qs.type(ns[name]) !== "Class" )
				throw new Error("找不到控件定义:" + nsURI + "." + name);
			var clazz = ns[name];
			var page = new clazz();
			
			var $el = $('<div style="display:none"></div>');
			this._$element.append($el);
			qs.ui.qxml.apply(page, [xml, $el, {defaultNS:"qs.m", checkType:false, defaultOptions:{position:""}}]);
			this.addPage(page);
			return page;
		}
		js.$protected.addPage = function(page){
			var name = page.name();
			if( js.isNull(name) || name == "" ){
				name = "page" + (page_id++);
				page.name(name);
			}
			if( js.instanceOf(this._pages[name], qs.ui.Control) && this._pages[name] != page )
				this._pages[name].remove();
			this._pages[name] = page;
		}
		js.$public.getPage = function(name){
			return this._pages[name];
		}
		/**
		 * 装载APP定义
		 * @param $app 生成APP内容的根元素(jQuery对象)
		 * @param xml APP的QXML定义
		 * @param options APP初始化选项(可选)
		 * 		 -startPageName APP的起始页名称
		 */
		js.$public.load = function($app, xml, options){
			$app.css("display", "none");
			this.render($app);
			
			var options = $.extend({
			}, options);
			
			this._title = xml.attr("title");
			
			var startPageName = xml.attr("startPage");
			var page, chXml, startPage, optStartPage, firstPage;
			var children = xml.children();
			var name, parts;
			for( var i = 0; i < children.length; i++ ){
				chXml = children[i];
				name  = chXml.name();
				parts = name.split(".");
				if( parts.length == 2 ){
					qs.m._doAppNode.call(this, parts[0], parts[1], chXml);
				}
				else if( name == "IncludePage" ){
					this._includePage[chXml.attr("name")] = chXml;
				}
				else {
					page = this.createPage(chXml);
					if( !firstPage )
						firstPage = page;
					if( startPageName != "" && startPageName == page.name() )
						startPage = page;
					if( !js.isNull(options.startPageName) && options.startPageName == page.name() )
						optStartPage = page;
				}
			}
			if( optStartPage ){
				if( startPage && startPage != optStartPage )
					this._history.push(startPage);
				startPage = optStartPage;
			}
			else if( !startPage )
				startPage = firstPage;
			
			$app.css("display", "");
			if( !js.isNull(startPage) ){
				this._activePage = startPage;
				this._activePage.visible(true);
				this._activePage.invalidateSize(true);
				this._activePage.dispatchEvent("active");
			}
			this._isLoaded = true;
		}
		js.$public.isLoaded = function(){
			return this._isLoaded;
		}
		js.$protected.template = function(options){
			return '<div class="qs-m-screen qs-m-ani"></div>';
		};
		/**
		 * 插件调用
		 * @param pluginName 插件名称
		 * @param pluginMethod 插件方法
		 * @param pluginArgs 插件参数 array
		 * @param successCallback 成功回调
		 * @param errorCallback 失败回调
		 */
		js.$public.callPlugin = function(pluginName, pluginMethod, pluginArgs, successCallback, errorCallback){
			try{
				cordova.exec(function(param) {
					if(js.type(successCallback) == "function"){
						successCallback(param);
					}
				}, function(error) {
					if(js.type(errorCallback) == "function"){
						errorCallback(error);
					}
				}, pluginName, pluginMethod, pluginArgs);
			}catch (e) {
			}
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
			
			$element.addClass("qs-m-app");
			
			this.$super($element, options);
			
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			
			this._$screen = $element.children(".qs-m-screen");
			
			//创建等待框
			var $el = $("<div></div>");
			$("body").append($el);
			
			var waitingBox = new qs.m.WaitingBox();
			waitingBox.render($el);
			qs.m.waitingBox = waitingBox;
			this.bindEvent(qs.m.events.touchstart, this.onTouchStart);
		};
		this.onTouchStart = function(event){
			
		}
		js.$static.instance = function(){
			return _app;
		};
	});
	var _app = new qs.m.App();
	qs.m.app = _app;
	
	var $window = $(window);
	/**
	 * 获取当前窗口区域的宽度
	 */
	qs.ui.windowWidth = function(){
		var width = $window.width() / _app.zoom();
		if( !isNaN(width) )
			return width;
		else
			return $window.width();
	}
	/**
	 * 获取当前窗口区域的高度
	 */
	qs.ui.windowHeight = function(){
		var height = $window.height() / _app.zoom();
		if( !isNaN(height) )
			return height;
		else
			return $window.height();
	}
	
	/*
	qs.m.app.width(qs.ui.windowWidth());
	qs.m.app.height(qs.ui.windowHeight());

	var timerId = 0;
	$window.bind("resize", function(event){
		clearTimeout(timerId);
		timerId = setTimeout(function(){
			qs.m.app.width(qs.ui.windowWidth());
			qs.m.app.height(qs.ui.windowHeight());
		}, 100);
	});
	*/
})(qs, jQuery)