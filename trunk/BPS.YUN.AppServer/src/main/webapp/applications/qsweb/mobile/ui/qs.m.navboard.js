(function(qs, $) {
	js.$class("qs.m.NavBoard", qs.ui.Control, function(){
		//注册qs事件
		qs.events.register("click", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._columnSize = 3;
			this._labelField = "label";
			this._itemRenderer = null;
		};
		js.$public.columnSize = function(value){
			if( value === undefined )
				return this._columnSize;
			
			this._columnSize = value
		};
		js.$public.itemRenderer = function(value){
			if( value === undefined )
				return this._itemRenderer;
			
			this._itemRenderer = value
		};
		js.$public.labelField = function(value){
			if( value === undefined )
				return this._labelField;
			
			this._labelField = value
		};
		js.$public.items = function(data){
			if( data === undefined )
				return this._items;
			
			if( !(data == null || qs.type(data) == "array" || js.instanceOf(data, qs.ArrayList)) )
				throw new TypeError("List.items的值必须是array或qs.ArrayList类型.");
			this.clear();
			this._items = data;
			this.callAfterRendered(function(){
				var $table = this._$element.children("table");
				if( $table.length == 0 ){
					$table = $('<table />');
					this._$element.append($table);
				}
				$table.empty();
				var len = data.length;
				var item, $tr, curRow = 0;
				$tr = $('<tr></tr>');
				$table.append($tr);
				for( var i = 0; i < this._items.length; i++ ){
					item = this._items[i];
					if( Math.floor(i / this._columnSize) > curRow ){
						curRow = Math.floor(i / this._columnSize);
						$tr = $('<tr></tr>');
						$table.append($tr);
					}
					this.renderItem($tr, item, i, len);
				}
			});
		};
		this.renderItem = function($tr, item, index, len){
			var label = this._labelField;
			
			var $td = $('<td></td>');
			$tr.append($td);
			
			if( js.type(this._itemRenderer) == "function" ){
				this._itemRenderer($td, item, index, len);
				$td.data("data", item);
			}
			else {
				if( js.type(item.icon) == "string" )
					$td.append('<div class="icon"><img src="' + qs.m.app.getAbsoluteUrl(item.icon) + '"></img></div>');
				else
					$td.append('<div class="icon"><img src="' + qs.m.app.getAbsoluteUrl("images/default-navpage.png") + '"></img></div>');
				if( item.counter > 0 ){
					if( item.counter > 100 )
						$td.children(".icon").append('<span class="qs-counter red">...</span>');
					else
						$td.children(".icon").append('<span class="qs-counter red">' + item.counter + '</span>');
				}
				$td.append('<div class="text">' + item[label] + '</div>');
				$td.data("data", item);
			}
		}
		js.$public.updateItem = function(item){
			if( js.isNull(this._$element) )
				return;
			var label = this._labelField;
			var $td = this._$element.find( "td" ).filter(function( index ) {
			    return $(this).data("data") == item;
			});
			if( $td.length == 0 )
				return;
			var index = this._items.indexOf(item);
			var len   = this._items.length;
			if( js.type(this._itemRenderer) == "function" ){
				this._itemRenderer($td, item, index, len);
			}
			else {
				var $img = $td.find(".icon img");
				if( $img.length == 1 ){
					if( js.type(item.icon) == "string" )
						$img.attr("src", qs.m.app.getAbsoluteUrl(item.icon));
					else
						$img.attr("src", qs.m.app.getAbsoluteUrl("images/default-navpage.png"));
				}
				var $counter = $td.find(".qs-counter");
				if( item.counter > 0 ){
					var counter = item.counter;
					if( counter > 100 )
						counter = "...";
					if( $counter.length == 0 )
						$td.children(".icon").append('<span class="qs-counter red">' + counter + '</span>');
					else
						$counter.text(counter);
				}
				else
					$counter.remove();
				
				$td.find('.text').html(item[label]);
				$td.data("data", item);
			}
		}
		js.$public.clear = function(){
			this._items = [];
			if( this.isRendered() ){
				var $table = this._$element.children("table");
				$table.empty();
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
				classes : 'qs-m-navboard',
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
			var $td = $(evt.target).closest('td', this._$element);
			if( $td.length == 0 ){
				return;
			}
			this._$touchTd = $td;
			evt = evt.originalEvent;
			this._touchStartX = evt.touches ? evt.touches[0].pageX : evt.clientX;
			this._touchStartY = evt.touches ? evt.touches[0].pageY : evt.clientY;
			this._timeId = setTimeout(function(){
				$td.addClass("qs-m-active");
			}, 30);
			this._touchTime = new Date().getTime();
			return;
		}
		this.onTouchMove = function(evt){
			clearTimeout(this._timeId);
			evt = evt.originalEvent;
			var x = evt.touches ? evt.touches[0].pageX : evt.clientX;
			var y = evt.touches ? evt.touches[0].pageY : evt.clientY;
			if( this._$touchTd && x != this._touchStartX && y != this._touchStartY ){
				this._$touchTd.removeClass("qs-m-active");
				this._$touchTd = null;
			}
		}
		this.onTouchEnd = function(evt){
			this.releaseCapture(this._$element, "touchmove mousemove touchend mouseup");
			clearTimeout(this._timeId);
			if( this._$touchTd ){
				if( new Date().getTime() - this._touchTime < 500 ){
					var $td = this._$touchTd;
					$td.addClass("qs-m-active");
					setTimeout(function(){
						$td.removeClass("qs-m-active");
					}, 500);
				}
				else
					this._$touchTd.removeClass("qs-m-active");
				var item = this._$touchTd.data("data");
				if( !js.isNull(item) ){
					var event = new qs.events.Event("click");
					this.dispatchEvent(event, {item:item, el:this._$touchTd[0]});
					if( !event.isDefaultPrevented() ){
						if( js.type(item.goPage) == "string" ){
							qs.m.app.activePage(item.goPage);
						}
						else if( js.type(item.goUrl) == "string" )
							qs.m.app.loadPage(item.goUrl);
					}
				}
				this._$touchTd = null;
				return false;
			}
		}
	});
})(qs, jQuery);
