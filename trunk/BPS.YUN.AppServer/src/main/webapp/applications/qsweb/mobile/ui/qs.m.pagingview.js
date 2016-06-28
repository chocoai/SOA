(function(qs, $) {
	js.$class("qs.m.PagingView", qs.m.Scrollable, function(){
		qs.events.register("refresh", this);
		qs.events.register("loadmore", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._hasMore = true;
		};
		js.$public.hasMore = function(val){
			if( val === undefined )
				return this._hasMore;
			this._hasMore = val;
			
			if( this.isRendered() ){
				if( this._hasMore == true )
					this._$element.find(".pv-more").css("display", "");
				else
					this._$element.find(".pv-more").css("display", "none");
			}
		}
		js.$public.completeRefresh = function(){
			var pos = this.getPos();
			this.slideTo(pos, {y:0}, 0.3, "ease-out");
		}
		js.$protected.template = function(options){
			return StringUtil.join(
					'<div class="qs-m-scrollable" style="position:relative">',
						'<div class="pv-refresh">',
							'<div class="icon"></div>',
							'<div class="text">下拉刷新数据</div>',
						'</div>',
						'<div class="pv-more" style="display:none">',
							'<div class="icon"></div>',
							'<div class="text">正在加载更多数据</div>',
						'</div>',
					'</div>'
				);
		};
		js.$protected.onRendered = function($element, options){
			$element.addClass("qs-m-pagingview");
			if( this._hasMore == true ){
				$element.find(".pv-more").css("display", "");
			}
			this._$refresh = $element.find(".pv-refresh");
			this._$refreshIcon = this._$refresh.children(".icon");
			this._$refreshText = this._$refresh.children(".text");
			
			this._$loadmore= $element.find(".pv-more");
			this._$scrollable = $element.children(".qs-m-scrollable");
			
			this._dim = this.getDim();
		}
		js.$protected.setScrollTo = function(to){
			this.$super(to);
			if( this._$refresh ){
				this._refreshActive = false;
				if( !(this._refreshHeight > 0) ){
					this._refreshHeight = Math.abs(parseInt(this._$refresh.css("top"))) + this._$refresh.outerHeight();
					if( !(this._refreshHeight > 0 ))
						this._refreshHeight = this._$refresh.outerHeight();
				}
				if( to.y > 0 ){
					this._$refreshText.html("下拉刷新数据");
					var h = this._refreshHeight;
					var y = to.y > h ? h : to.y;
					var percent = y / h * 100;
					var deg = -1.8 * percent + 360;
					this._$refreshIcon.css("transform", "rotate(" + deg + "deg)");
					if( deg == 180 ){
						this._$refreshText.html("释放刷新数据");
						this._refreshActive = true;
					}
				}
			}
			
			if( this._$loadmore ){
				this._loadmoreActive = false;
				if( this._hasMore ){
					if( -to.y + this._dim.d.h > this._dim.c.h )
						this._loadmoreActive = true;
				}
			}
			
		}
		js.$protected.adjustDestination = function(to, pos, dim){
			if( !this._$refresh )
				return true;
			
			if( this._refreshActive == true ){
				this._refreshActive = false;				
				var event = new qs.events.Event("refresh");
				this.dispatchEvent(event);
				if( event.isDefaultPrevented() )
					return false;
			}
			if( this._loadmoreActive == true ){
				this._loadmoreActive = false;
				this.dispatchEvent("loadmore");
			}
			return true;
		}
	});
})(qs, jQuery);
