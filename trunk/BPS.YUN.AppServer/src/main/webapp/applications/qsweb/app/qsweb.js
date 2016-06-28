(function(qs, $) {
	
	var QS = {};
	QS.openALink = function(alink) {
		try{
			alink = decodeURIComponent(alink);
		}
		catch(e){
			
		}
		alink = alink.replace(/#/g, "\"");
		wos.Desktop.openALink("", alink);
	}
	QS.selectApp = function(url){
		var qsweb = QSWeb.instance();
		var items = qsweb.header.items();
		for( var i = 0; i < items.length; i++ ){
			if( items[i].url == url || items[i].alink == url ){
				qsweb.header.selectedItem(items[i]);
				return true;
			}
		}
		return false;
	}
	window.QS = QS;

	var template = new qs.Xml(StringUtil.join(
			'<Container xmlns:app="qs.app" xmlns:html="qs.html">',
				'<VLayout width="100%" height="100%" vGap="0">',
					'<HLayout name="top" width="100%" hGap="2" height="82">',
						'<html:div name="logo" class="qs-logo" layout="{height:\'100%\'}"/>',
						'<app:AppHeader name="header" change="onAppSelected" layout="{width:\'*\'}"/>',
						'<GridLayout name="topRight" width="150" height="82" columns="[35, \'*\']" rows="[\'*\', \'auto\']" hGap="3" vGap="0">',
							'<html:div name="info" class="qs-info" height="40" layout="{height:40, width:150, colSpan:2}" />',
							'<html:div name="minmax" title="缩小" class="qs-header-minmax max" layout="{height:16, width:16, rowIndex:1, hAlign:\'center\', vAlign:\'middle\'}" />',
							'<ButtonBar name="topWidgets" options="{classes:\'qs-buttonbar top-widgets\'}" margin="{left:2, right:8, bottom:4}" layout="{rowIndex:1, colIndex:1, hAlign:\'right\', vAlign:\'bottom\', height:28}" />',
						'</GridLayout>',
					'</HLayout>',
					'<html:div name="workarea" class="app-content" layout="{width:\'100%\', height:\'*\'}"/>',
					//'<html:div name="footer" class="qs-app-footer" layout="{width:\'100%\', height:24}"/>',
				'</VLayout>',
			'</Container>'
		));
	js.$class("QSWeb", qs.ui.Container, function(){
		js.$static.WIDGET_ACTION_EVENT = "WidgetAction";
		
		/** 业务应用注册，<uid, qs.ui.Control> */
		js.$static.applicationMap/*:Object*/ = {};
		js.$static._instance = null;
		
		this._config = {};
		this._activeApp = null;
		this.menudata = {
				label : "",
				menuitem : [
				            { label : "修改密码", action : "setPassword",   iconUrl : "images/icons/key.png"},
				            { label : "注销", action : "logout", iconUrl : "images/icons/logout.png"}
				           ]
			};
		this.sysMenu/*:Menu*/ = null;
		
		this.initialize = function(options){
			this.$super(options);
			
			this.sysMenu = new qs.ui.Menu();
			this.sysMenu.menudata(this.menudata);
			this.sysMenu.addEventListener("itemClick", this.onSystemMenuAction, this);

			wos.Desktop.instance().addEventListener(wos.Desktop.READY_EVENT, this.onReady, this);
			//this.info.append('<iframe src="http://weather.news.sina.com.cn/chajian/iframe/weatherStyle0.html?city=%E5%AE%81%E6%B3%A2" width="140" height="20" marginwidth="0" marginheight="0" hspace="0" vspace="0" frameborder="0" scrolling="no"/>');
		};
		/**
		 * 获取桌面小控件
		 * @param id 桌面小控件的ID
		 * @return 桌面小控件或null
		 */
		js.$public.getWidget = function(id){
			var ret = null, data;
			this.topWidgets.forEach(false, function(child, parent){
				data = child.data("data");
				if( !js.isNull(data) && data.id == id ){
					ret = child;
					return false;
				}
			});
			return ret;
		}
		this.onTopWidgetAction = function(event, args){
			this.topRight.invalidateSize(true);
			if( args.data.action == "system" ){
				var offset = args.button.offset();
				this.sysMenu.show(offset.left, offset.top + args.button.outerHeight() + 2);
			}
			else
				wos.Desktop.instance().dispatchEvent(QSWeb.WIDGET_ACTION_EVENT, args);
		}
		this.onSystemMenuAction = function(event, item){
			switch( item.action ){
			case "setPassword":
				var $win = new qs.app.PasswordWindow();
				$win.open();
				break;
			case "logout":
				wos.Desktop.instance().logout();
				break;
			}
		}
		js.$static.getApplication = function(uid/*:String*/)/*:qs.ui.Control*/{
			if( QSWeb.applicationMap.hasOwnProperty(uid) )
				return QSWeb.applicationMap[uid];
			else
				return null;
		};
		
		js.$static.addApplication = function(uid/*:String*/, app){
			if( QSWeb._instance._activeApp == app )
				return;
			
			QSWeb.applicationMap[uid] = app;
			var $activeEl = null;
			var windowWidth = qs.ui.windowWidth();
			if( QSWeb._instance._activeApp != null ){
				$activeEl = QSWeb._instance._activeApp.$element();
				$activeEl.removeClass("qs-app-animate");
				$activeEl.css("left", "0px");
				$activeEl.css("transform", "");
				//QSWeb._instance._activeApp.visible(false);
			}
			
			if( !app.isRendered() ){
				var $el = $("<div />");
				QSWeb._instance.workarea.append($el);
				app.render($el);
			}
			else if( app.$element().parent()[0] != QSWeb._instance.workarea[0] ){
				QSWeb._instance.workarea.append(app.$element());
			}
			
			var size = {};
			size.width  = QSWeb._instance.workarea.width();
			size.height = QSWeb._instance.workarea.height();
			app.outerSize(size);
			
			QSWeb._instance._activeApp = app;

			//if( !app.visible() )
			//	app.visible(true);
			if( $activeEl != null ){
				var $appEl = app.$element();
				if( $.browser.msie && $.browser.version <= 8 ){
					$appEl.css("display", "");
					$activeEl.css("display", "none");
				}
				else {
					$appEl.removeClass("qs-app-animate");
					$appEl.css("transform", "");
					$appEl.css("left", windowWidth + "px");
					
					$activeEl.addClass("qs-app-animate");
					$activeEl.css("transform", "translateX(" + (-windowWidth) + "px)");
					$activeEl.css("opacity", 0);
					
					$appEl.addClass("qs-app-animate");
					$appEl.css("transform", "translateX(" + (-windowWidth) + "px)");
					$appEl.css("opacity", 1);
				}
			}
		};
		js.$public.updateSize = function(width, height){
			this.$super(width, height);
			
			if( this._activeApp != null ){
				var size = {};
				size.width  = QSWeb._instance.workarea.width();
				size.height = QSWeb._instance.workarea.height();
				this._activeApp.outerSize(size);
				var self = this;
				var fn = function(){
					var osize = self._activeApp.outerSize();
					if( size.width != osize.width || size.height != osize.height )
						setTimeout(fn, 50);
					else
						self._activeApp.invalidateSize(true);
				}
				setTimeout(fn, 50);
			}
		};
		js.$public.init = function(){
			var $el = $("<div></div>");
			$("body").append($el);
			var waitingBox = new qs.app.WaitingBox();
			waitingBox.render($el);
			wos.Desktop.regWaitingBox(waitingBox);
			
			this.qxml(template);
			
			this.bindEvent("click", this.onMinMax, this.minmax);

			this._config = qsweb_config();
			wos.Desktop.instance().config(this._config);
			
			var buttons = [
							   {
								  id    : "chat",
								  label : "即时消息",
								  icon  : "images/icons/chat.png",
								  action: "chat",
								  visible : false
							   },
	                           {
	                         	  id    : "workflow_task",
	                         	  label : "工作流任务",
	                         	  icon  : "images/icons/task.png",
	                         	  action: "task"
	                           },
	                           {
	                         	  label : "系统",
	                         	  icon  : "images/icons/setting.png",
	                         	  action: "system"
	                           }
                           ];
			
			if( this._config && this._config.header ){
				if( js.type(this._config.header.buttons) == "array" )
					buttons = this._config.header.buttons;
				if( this._config.header.minimizable == true ){
					this.minmax.css("display", "block");
				}
				if( !js.isEmpty(this._config.header.logo) ){
					this.logo.css("background-image", "url(" + this._config.header.logo + ")");
				}
			}
			this.topWidgets.dataProvider(buttons);
			this.topWidgets.addEventListener("buttonClick", this.onTopWidgetAction, this);
			this.topWidgets.height(24);
		};
		this.onMinMax = function(event){
			if( this._minimized == true ){
				//最大化
				this._minimized = false;
				this.minmax.removeClass("min");
				this.minmax.addClass("max");
				this.minmax.attr("title", "缩小")
				
				this.info.css("display", "");
				this.topRight.height(82);
				this.topRight.$element().removeClass("min");
				this.top.height(82);
				this.header.maximize();
				this.top.data("layout").height = 82;
				
				if( this._config && this._config.header && !js.isEmpty(this._config.header.logo) ){
					this.logo.css("background-image", "url(" + this._config.header.logo + ")");
				}
			}
			else {
				//最小化
				this._minimized = true;
				this.minmax.removeClass("max");
				this.minmax.addClass("min");
				this.minmax.attr("title", "放大")
				
				this.info.css("display", "none");
				this.topRight.height(30);
				this.topRight.$element().addClass("min");
				this.top.height(30);
				this.header.minimize();
				this.top.data("layout").height = 30;
				
				if( this._config && this._config.header && !js.isEmpty(this._config.header.minLogo) ){
					this.logo.css("background-image", "url(" + this._config.header.minLogo + ")");
				}
			}
			this.invalidateSize(true);
		} 
		js.$static.instance = function(inst){
			if( js.instanceOf(inst, QSWeb) )
				QSWeb._instance = inst;
			else {
				if( QSWeb._instance == null )
					QSWeb._instance = new QSWeb();
				return QSWeb._instance;
			}
		};
		js.$public.selecteApp = function(alink){
			if( alink.param.AppSelected == true )
				return;
			var items = this.header.items();
			var item;
			for( var i = 0; i < items.length; i++ ){
				item = items[i];
				if( js.isEmpty(item.alink) )
					continue;
				if( js.isNull(item.alinkObj) )
					item.alinkObj = wos.ALink.parseALink(item.alink);
				if( alink.application == item.alinkObj.application ){
					if( !js.isEmpty(alink.param.rootMenu) && alink.param.rootMenu != item.alinkObj.param.rootMenu ){
						continue;
					}
					if( js.type(alink.param.showMenus) == "array" && !js.equals(alink.param.showMenus, item.alinkObj.param.showMenus) ){
						continue;
					}
					this.header.selectedItem(item, false);
					return true;
				}
			}
		}
		this.onAppSelected = function(event, args){
			var app = args.newValue;
			if( !js.isNull(app.alink) )
				wos.Desktop.openALink("QSWeb", app.alink, {AppSelected : true});
			else if( !js.isNull(app.url) && app.url != "" && app.url.length > 0 ){
				if( app.show == "newwindow" ){
					window.open(app.url, "_blank");
					this.header.selectedItem(args.oldValue);
				}
				else if( StringUtil.endWith(app.url, ".swf") ){
					var appFrame = QSWeb.getApplication(app.url);
					//open flex iframe
					if( !js.instanceOf(appFrame, qs.app.AppFrame) ){
						appFrame = new qs.app.AppFrame();
						var url = wos.Desktop.getAbsoluteUrl("/applications/terminal/AppLoader.jsp?url=" + app.url);
						appFrame.src(url);
					}
					QSWeb.addApplication(app.url, appFrame);
				}
				else {
					//open iframe
					var appFrame = QSWeb.getApplication(app.url);
					if( !js.instanceOf(appFrame, qs.app.AppFrame) ){
						appFrame = new qs.app.AppFrame();
						appFrame.src(wos.Desktop.getAbsoluteUrl(app.url));
					}
					QSWeb.addApplication(app.url, appFrame);
				}
			}
		};
		
		
		js.$public.onReady = function(){
			this.header.items(wos.Desktop.instance().desktopLinks());

			if( js.type(this._config.initALink) == "string" && this._config.initALink.length > 0 ){
				if( !QS.selectApp(this._config.initALink) ){
					this.header.selectedIndex(0);
					wos.Desktop.openALink("", this._config.initALink);
				}
			}
			else {
				this.header.selectedIndex(0);
			}
			
			this.info.append($('<div class="info-time"></div><div class="info-user"></div>'))
			var $infoTime = this.info.children(".info-time");
			
			var now = new Date();
			var serverNow = wos.expression.ExpressionFunction.now();
			$infoTime.html(DateUtil.format(serverNow, "YYYY-MM-DD 星期E HH:mm"));
			setTimeout(function(){
				serverNow = wos.expression.ExpressionFunction.now();
				$infoTime.html(DateUtil.format(serverNow, "YYYY-MM-DD 星期E HH:mm"));
				setInterval(function(){
					serverNow = wos.expression.ExpressionFunction.now();
					$infoTime.html(DateUtil.format(serverNow, "YYYY-MM-DD 星期E HH:mm"));
				}, 60000);
			}, 60000 - serverNow.getSeconds() * 1000);
			
			var $infoUser = this.info.children(".info-user");
			var loginInfo = wos.Desktop.getAttribute("loginInfo");
			var domainLabel = loginInfo.domainLabel;
			if ( js.isNull(domainLabel) || domainLabel == "ime.com")
				domainLabel = "";
			if ( !js.isNull(loginInfo.groupName) )
				$infoUser.html(domainLabel + " " + loginInfo.groupName + "  " + loginInfo.principalName);
			else
				$infoUser.html(domainLabel + " " + loginInfo.principalName);
			
			if( loginInfo["service.xmpp.websocket"] == "true" ){
				var chat = null;
				this.topWidgets.forEach(false, function(child, parent){
					data = child.data("data");
					if( !js.isNull(data) && data.id == "chat" ){
						chat = child;
						return false;
					}
				});
				if( chat != null ){
					chat.visible(true);
					this.topWidgets.invalidateSize(true);
				}
			}
		};
	});
	$(document).ready(function(){
		var qsweb = QSWeb.instance();
		qsweb.init();
		qsweb.outerSize({width:qs.ui.windowWidth(), height:qs.ui.windowHeight()});
		var timerId = 0;
		$(window).bind("resize", function(event){
			clearTimeout(timerId);
			timerId = setTimeout(function(){
				qsweb.outerSize({width:qs.ui.windowWidth(), height:qs.ui.windowHeight()}, true);
			}, 100);
		});
		
		document.oncontextmenu = function(evt) {
			var $el;
			if( evt && evt.target )
				$el = $(evt.target);
			else if( event && event.srcElement )
				$el = $(event.srcElement);
			else
				return false;
			if( !$el.is("textarea, input[type='text']") )
				return false;
		};
	});
})(qs, jQuery);
