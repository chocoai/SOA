(function(qs, $) {
	qs.m = {};
	
	qs.m.MessageBox = {
		show : function(msg, title){
			alert(msg);
		}
	}
	
	qs.m.isMobile = qs.ui.device.isMobile;
	if( qs.m.isMobile == true ){
		qs.m.events = {
				"touchstart" : "touchstart",
				"touchmove"  : "touchmove",
				"touchend"   : "touchend"
		}
	}else {
		qs.m.events = {
				"touchstart" : "mousedown",
				"touchmove"  : "mousemove",
				"touchend"   : "mouseup"
		}
	}
	
	js.$class("qs.m.Remoting", function(){
		//当前正在执行的调用
		this._calls = [];
		//构造函数
		this.initialize = function(page/*:qs.m.IPage*/){
			this._calls = [];
			if( js.instanceOf(page, qs.m.IPage) ){
				page.addEventListener("deactive", this.onPageDeactive, this);
			}
		};
		this.onPageDeactive = function(event){
			qs.m.waitingBox.reset();
			try{
				for( var i = 0; i < this._calls.length; i++ ){
					this._calls[i].abort();
				}
			}catch(e){
			}
			this._calls.length = 0;
		}
		/**
		 * 执行Ajax调用,如果Remoting所对应的IPage进入后台，则所有不允许后台执行的调用都将被取消
		 * @param settings 参考jQuery.ajax中的settings参数
		 * 		settings.background : 是否允许在后台执行，默认false
		 *		settings.showWaitingBox : 是否显示等待框，默认true
		 */
		js.$public.call = function(settings){
			if( js.isNull(settings) )
				return;
			var showWaitingBox = settings.showWaitingBox;
			var complete = settings.complete;
			var self = this;
			settings.complete = function(jqXHR, textStatus){
				if( settings.background != true ){
					if(self._calls){
						for( var i = 0; i < self._calls.length; i++ ){
							if( self._calls[i] == jqXHR ){
								self._calls.splice(i, 1);
								break;
							}
						}
					}
				}
				if( js.type(complete) == "function" )
					complete(jqXHR, textStatus);
				if(showWaitingBox != false)
					qs.m.waitingBox.close();
			}
			if(showWaitingBox != false)
				qs.m.waitingBox.open();
			var call = $.ajax(settings);
			if( settings.background != true )
				this._calls.push(call);
		}
	});
	/**
	 * 初始化环境
	 * @param $root 根节点
	 */
	qs.m.init = function($root){
		$root.find("[qs-type]").each(function(index){
			try{
				var $el = $(this);
				var data = $el.attr("qs-data");
				if( data ){
					data = JSON.parse(data);
				}
				var instance = js.newInstance($el.attr("qs-type"));
				instance.data("data", data);
				instance.render($el);
			}
			catch(e){
				qs.m.MessageBox.show(e);
			}
		})
	}
	$(document).ready(function(){
		//让界面执行一次动画，可以减少Android中动画异常的出错概率
		var $el = $(".android-bug"); 
		qs.ui.animation.slide($el, {x:-2000}, 0.3, "ease-out", function(){
			$el.css("transform", "translate3d(2000px, 10px, 0px)");
			$el.css("display", "none");
		})
		qs.m.init($("body"));
	});
	
	$.ajaxSetup({
		timeout : 30000
	})
})(qs, jQuery)