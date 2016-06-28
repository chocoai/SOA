(function(qs, $) {
	function onFrameLoaded(event){
		try{
			var iframe = event.target;
			var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
			iframeWin.qs = qs;
		}
		catch(e){
			console.trace(e);
		}
	}
	
	js.$class("qs.m.Html", qs.ui.Control, function(){
		qs.events.register("htmlLoaded", this);
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._iframe = false;
			this._url = "";
			this._param = {};
			this._html = "";
		};
		js.$public.url = function(val){
			if( val === undefined )
				return this._url;
			this._url = val;
		}
		js.$public.iframe = function(val){
			if( val === undefined )
				return this._iframe;
			this._iframe = val;
		}
		js.$public.html = function(val){
			if( val === undefined )
				return this._html;
			this._html = val;
			this.callAfterRendered(function(){
				this._$element.html(this._html);
			});
		}
		js.$public.param = function(val){
			if( val === undefined )
				return this._param;
			this._param = val;
		}
		js.$protected.template = function(options){
			return "";
		};
		this.onHtmlLoaded = function(text){
			this._$element.html(text);
			this._html = text;
			this.dispatchEvent("htmlLoaded");
		}
		js.$public.refresh = function(){
			if( !js.isNull(this._url) && this._url != "" ){
				if( this._iframe == true ){
					var $iframe = this._$element.find("iframe");
					$iframe.attr("src", this._url);
					return;
				}
				var self = this;
				var param = $.extend({
					"_" : new Date().getTime()
				}, this._param );

				qs.m.waitingBox.open();
				$.ajax({
						url : this._url,
						type: "POST",
						data: param,
						async : true,
						dataType: "text",
						success : function(content){
							js.callInternal(self, self.onHtmlLoaded, [content]);
						},
						error : function(jqXHR, textStatus, errorThrown){
							qs.m.MessageBox.show("装入失败:" + textStatus);
						},
						complete : function(jqXHR, textStatus){
							qs.m.waitingBox.close();
						}
				});
			}
		}
		js.$protected.onRendered = function($element, options){
			$element.addClass("qs-m-html");
			var options = $.extend({
				classes : ''
			}, this._options, options);
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			if( this._iframe == true ){
				
				var html = StringUtil.join(
				'<div style="width: 100%; height: 100%; overflow: scroll !important;-webkit-overflow-scrolling:touch !important;">',
				    '<iframe scrolling="yes" style="width:100%;height:100%;border:none">',
				    '</iframe>',
				'</div>');
				$element.append(html);
				var $iframe = $element.find("iframe");
				$iframe.bind("load", onFrameLoaded);
			}
			this.refresh();
		};
		
		/**
		 * 自定义的QXML解析函数，控件类中可以通过定义QXMLParser方法重载QXML解析器对该控件中子控件的处理
		 * @param control 控件实例
		 * @param xml QXML的定义
		 * @param contents QXML定义中的子控件定义(XML)数组
		 */
		js.$static.QXMLParser = function(control/*:qs.ui.Control*/, xml/*:qs.Xml*/, contents/*:Array*/){
			if( contents.length == 0 )
				return;
			var html = "";
			for( var i = 0; i < contents.length; i++ ){
				html += contents[i].toString();
			}
			control.html(html);
		}
	});
})(qs, jQuery);
