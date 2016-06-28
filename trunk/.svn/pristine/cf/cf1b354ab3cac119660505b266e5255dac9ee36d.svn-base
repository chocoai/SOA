(function(qs, $) {
	js.$class("qs.m.List", qs.ui.ItemsControl, function(){
		//注册qs事件
		qs.events.register("click", this);
		qs.events.register("editingAction", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._itemHeight = 24;
			this._labelField = "label";
			this._itemRenderer = null;
			this._editable = false;
			this._editingTool = null;
		};
		js.$public.editable = function(value){
			if( value === undefined )
				return this._editable;
			
			this._editable = value
		};
		js.$public.editingTool = function(value){
			if( value === undefined )
				return this._editingTool;
			
			this._editingTool = value
		};
		js.$public.itemRenderer = function(value){
			if( value === undefined )
				return this._itemRenderer;
			
			if( js.type(value) == "function" )
				this._itemRenderer = value;
			else
				this._itemRenderer = null;
		};
		js.$public.labelField = function(value){
			if( value === undefined )
				return this._labelField;
			
			this._labelField = value
		};
		js.$public.itemHeight = function(val){
			if( val === undefined )
				return this._itemHeight;

			this._itemHeight = val;
			if( this._$element != null && !isNaN(val) ){
				this._$element.find(".qs-list ul li").css("line-height", _itemHeight + "px");
			}
		};
		js.$public.items = function(data){
			if( data === undefined )
				return this._items;
			
			if( !(data == null || qs.type(data) == "array" || js.instanceOf(data, qs.ArrayList)) )
				throw new TypeError("List.items的值必须是array或qs.ArrayList类型.");
			this.clear();
			this._items = data;
			this.callAfterRendered(function(){
				var $ul = this._$element.children("ul");
				if( $ul.length == 0 ){
					$ul = $('<ul />');
					this._$element.append($ul);
				}
				$ul.empty();
				var len = data.length;
				data.forEach(function(item, index){
					this.renderItem($ul, item, index, len);
				}, this);
			});
		};
		this.renderItem = function($ul, item, index, len){
			var label = this._labelField;
			
			var $li = $('<li></li>');
			$ul.append($li);
			if( item.type == "separator" )
				$li.addClass("separator");
			
			if( js.type(this._itemRenderer) == "function" ){
				this._itemRenderer($li, item, index, len);
				$li.data("data", item);
			}
			else {
				if( js.type(item.icon) == "string" )
					$li.append('<img class="icon" src="' + qs.m.app.getAbsoluteUrl(item.icon) + '"></img>');
				$li.append(item[label])
				$li.data("data", item);
				
				if( js.type(item.goPage) == "string" || js.type(item.goUrl) == "string")
					$li.append('<div class="arrow"></div>');
			}
		}
		js.$public.addItem = function(item){
			this.$super(item);
			
			if( this.isRendered() ){
				var $ul = this._$element.children("ul");
				if( $ul.length == 0 ){
					$ul = $("<ul></ul>");
					this._$element.append($ul);
				}
				var index = this._items.indexOf(item);
				var len   = this._items.length;
				this.renderItem($ul, item, index, len);
			}
		};
		js.$public.removeItem = function(item){
			var index = this._items.indexOf(item);
			if( index >= 0 ){
				var $ul = this._$element.children("ul");
				var $li = $ul.children("li:nth-child(" + (index + 1) + ")");
				if( $li.length == 0 )
					return;

				$li.remove();
			}
			this.$super(item);
		};
		js.$public.clear = function(){
			this._items = [];
			if( this.isRendered() ){
				var $ul = this._$element.children("ul");
				$ul.empty();
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
				classes : 'qs-m-list',
				position: ''
			}, this._options, options);
			
			this.$super($element, options);
			
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			
			this.bindEvent(qs.m.events.touchstart, this.onTouchStart);
			this.bindEvent(qs.m.events.touchmove, this.onTouchMove);
			this.bindEvent(qs.m.events.touchend, this.onTouchEnd);
		};
		this.onTouchStart = function(evt){
			this.setCapture(this._$element, "touchmove mousemove touchend mouseup");
			var $li = $(evt.target).closest('li', this._$element);
			if( $li.hasClass("separator") )
				return;
			if( $li.length > 0 ){
				if( !js.isNull(this._$editingLi) ){
					qs.ui.animation.slide(this._$editingLi, {x:0}, 0.3, "ease-out", this.clearHandler, this);
					this._$editingLi = null;
					this._touchAction = null;
					this._$touchLi = null;
					return false;
				}
				this._$touchLi = $li;
				evt = evt.originalEvent;
				this._touchStartX = evt.touches ? evt.touches[0].pageX : evt.clientX;
				this._touchStartY = evt.touches ? evt.touches[0].pageY : evt.clientY;
				this._timeId = setTimeout(function(){
					$li.addClass("qs-m-active");
				}, 30);
				this._touchTime = new Date().getTime();
				this._touchAction = "click";
			}
		}
		this.onTouchMove = function(evt){
			clearTimeout(this._timeId);
			evt = evt.originalEvent;
			var x = evt.touches ? evt.touches[0].pageX : evt.clientX;
			var y = evt.touches ? evt.touches[0].pageY : evt.clientY;
			
			if( this._touchAction == "click" ){
				if( x != this._touchStartX || y != this._touchStartY ){
					this._$touchLi.removeClass("qs-m-active");
					if( this._editable == true && new Date().getTime() - this._touchTime < 300 && this._touchStartX - x > Math.abs(this._touchStartY - y)){
						var item = this._$touchLi.data("data");
						if( js.isNull(item) )
							return;
						if( item.editable == false )
							return;
						
						this._touchAction = "leftSlide";
						if( this._editingTool == null ){
							this._editingTool = new qs.m.Button();
							this._editingTool.text("删除");
							var $el = $('<div class="qs-m-list-tool deleteBtn"></div>');
							this._$element.append($el);
							this._editingTool.render($el, {position:"absolute"});
							this._editingTool.addEventListener("click", this.onDelete, this);
						}
						var $el = this._editingTool.$element();
						this._editingTool.outerHeight(this._$touchLi.outerHeight());
						$el.css({
							"top" : this._$touchLi.offset().top - this._$element.children("ul").offset().top,
							"right" : 0
						});
						this._editingTool.visible(true);
					}
					else
						this._touchAction = null;
				}
			}
			else if( this._touchAction == "leftSlide" ){
				var toX = x - this._touchStartX;
				if( toX > 0 )
					toX = 0;
				this._$touchLi.css("transform", "translate(" + toX + "px, 0px) translateZ(0px)")
				this._slideX = toX;
				return false;
			}
		}
		this.onTouchEnd = function(evt){
			this.releaseCapture(this._$element, "touchmove mousemove touchend mouseup");
			clearTimeout(this._timeId);
			if( this._touchAction == "click" ){
				if( new Date().getTime() - this._touchTime < 500 ){
					var $li = this._$touchLi;
					$li.addClass("qs-m-active");
					setTimeout(function(){
						$li.removeClass("qs-m-active");
					}, 500);
				}
				else
					this._$touchLi.removeClass("qs-m-active");
				var item = this._$touchLi.data("data");
				var event = new qs.events.Event("click");
				this.dispatchEvent(event, {item:item, el:this._$touchLi[0]});
				if( !event.isDefaultPrevented() ){
					if( js.type(item.goPage) == "string" )
						qs.m.app.activePage(item.goPage);
					else if( js.type(item.goUrl) == "string" )
						qs.m.app.loadPage(item.goUrl);
				}
				this._touchAction = null;
				this._$touchLi = null;
				return false;
			}
			else if( this._touchAction == "leftSlide" ){
				var x = this._slideX;
				var toX = x - this._touchStartX;
				if( toX > 0 )
					toX = 0;
				var toolWidth = this._editingTool.outerWidth();
				if( -toX > toolWidth / 3 ){
					qs.ui.animation.slide(this._$touchLi, {x:-toolWidth}, 0.3, "ease-out");
					this._$editingLi = this._$touchLi;
				}
				else {
					qs.ui.animation.slide(this._$touchLi, {x:0}, 0.3, "ease-out", this.clearHandler, this);
				}
				this._touchAction = null;
				this._$touchLi = null;
				return false;
			}
		}
		this.onDelete = function(event){
			var item = this._$editingLi.data("data");
			var e = new qs.events.Event("editingAction");
			this.dispatchEvent(e, {action : "delete", item : item});
			if( !e.isDefaultPrevented() ){
				this.removeItem(item);
			}
			if( !js.isNull(this._editingTool) )
				this._editingTool.visible(false);
			this._$editingLi = null;
			this._touchAction = null;
		}
		this.clearHandler = function(event){
			$(event.target).css({
				"transition": "",
				"transform" : ""
			})
			if( !js.isNull(this._editingTool) )
				this._editingTool.visible(false);
		}
	});
})(qs, jQuery);
