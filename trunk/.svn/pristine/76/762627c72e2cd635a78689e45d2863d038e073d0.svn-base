(function(qs, $) {
	js.$class("qs.m.Button", qs.ui.Control, function(){
		//注册qs事件
		qs.events.register("click", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._text = "";
			this._iconWidth  = 32;
			this._iconHeight = 32;
			this._icon = "";
			this._counter = 0;	//通知计数
		};
		js.$public.icon = function(val){
			if( val === undefined )
				return this._icon;
			
			this._icon = val;
			if( this._$element != null && this._icon != "" && this._icon != null && this._icon != undefined ){
				this._$element.children(".qs-icon").attr("src", this._icon);
			}
		};

		js.$public.text = function(text){
			if( text === undefined )
				return this._text;
			else {
				this._text = text;
				if( this._$element )
					this._$element.children('.qs-text').text(text);
			}
		};
		js.$public.enabled = function(enabled){
			if( enabled === undefined )
				return this._enabled;
			this.$super(enabled);
			this._enabled = enabled;
			if( this._$element ){
				if( enabled == false )
					this._$element.addClass("qs-button-disabled");
				else
					this._$element.removeClass("qs-button-disabled");
			}
		};
		
		js.$public.updateSize = function(width, height){
			this.$super();
			if( !this.isRendered() )
				return;
			this._$element.css("line-height", height - 2 + "px");
		};
		
		js.$protected.template = function(options){
			return StringUtil.join(
					'<img class="qs-icon"></img>',
					'<span class="qs-text">', this._text, '</span>'
				);
		};
		/**
		 * 生成控件
		 * @param $element 承载控件的元素
		 * @param options 选项, {text:文本, classes:附加样式类, icon:图标URL, iconWidth:图标宽度, iconHeight:图标高度}
		 */
		js.$public.render = function($element, options){
			if (typeof options == 'undefined') options = {};
			var options = $.extend({
				text: "",
				classes : '',
				position: ''
			}, this._options, options);
			
			if( StringUtil.isEmpty(this._text) && !StringUtil.isEmpty(options.text) )
				this._text = options.text;

			this.$super($element, options);
			$element.addClass("qs-m-button");
			
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			
			if( options.iconWidth > 0 )
				this._iconWidth = options.iconWidth;
			if( options.iconHeight > 0 )
				this._iconHeight = options.iconHeight;
			if( options.icon )
				this._icon = options.icon;
			
			if( this._icon != "" && this._icon != null && this._icon != undefined ){
				$element.children(".qs-icon").attr("src", this._icon);
			}
			else {
				$element.children(".qs-icon").css("display", "none");
			}
			var self = this;
			this.bindEvent(qs.m.events.touchstart, this.onTouchStart);
			$element.click(function(evt){
				self.dispatchEvent("click");
			});
		};
		this.onTouchStart = function(evt){
			
		}
	});
	
	
	js.$class("qs.m.ButtonBar", qs.ui.Control, function(){
		//注册qs事件
		qs.events.register("change", this);
		qs.events.register("buttonClick", this);
		
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._itemRenderer = null;
			this._selectedItem = null;
			this._items  = [];
			this._toggleEnabled = false;
			this._equalWidth = false;
		};
		js.$public.equalWidth = function(value){
			if( value === undefined )
				return this._equalWidth;
			
			this._equalWidth = value
		};
		js.$public.toggleEnabled = function(value){
			if( value === undefined )
				return this._toggleEnabled;
			
			this._toggleEnabled = value
		};
		js.$public.itemRenderer = function(value){
			if( value === undefined )
				return this._itemRenderer;
			
			this._itemRenderer = value
		};
		js.$public.items = function(data){
			if( data === undefined )
				return this._items;
			
			if( !(data == null || qs.type(data) == "array" || js.instanceOf(data, qs.ArrayList)) )
				throw new TypeError("ButtonBar.items的值必须是array或qs.ArrayList类型.");
			this._items = data;
			this.callAfterRendered(function(){
				var $tr = this._$element.find("table tr");
				
				var len = data.length;
				data.forEach(function(item, index){
					this.addButton($tr, item, index, len);
				}, this);
				if( this._equalWidth == true ){
					this._$element.find("table tr td").css("width", (100 / len) + "%");
				}
			});
		};
		this.addButton = function($tr, item, index, len){
			if( js.type(this._itemRenderer) == "function" ){
				var $td = $('<td></td>');
				$tr.append($td);
				this._itemRenderer($td, item, index, len);
				$td.data("data", item);
			}
			else {
				var icon = "";
				if( item.icon )
					icon = '<img width="72" src="' + item.icon + '"></img>';
				var html = StringUtil.join(
						'<td>',
							'<div class="icon">', icon, '</div>',
							'<div class="text">', item.label, '</div>',
						'</td>'
					);
				var $td = $(html);
				$tr.append($td);
				$td.data("data", item);
			}
		}
		js.$public.addItem = function(item){
			var index = this._items.indexOf(item);
			if( index != -1 )
				return;
			this._items.push(item);
			this.callAfterRendered(function(){
				var $tr = this._$element.find("table tr");
				var len = this._items.length;
				this.addButton($tr, item, len - 1, len);
				if( this._equalWidth == true ){
					this._$element.find("table tr td").css("width", (100 / len) + "%");
				}
			});
		};
		js.$public.removeItem = function(item){
			var index = this._items.indexOf(item);
			if( index == -1 )
				return;
			this._items.splice(index, 1);
			if( this.isRendered() ){
				var $tds = this._$element.find("table tr td");
				var $td;
				for( var i = 0; i < $tds.length; i++ ){
					$td = $($tds[i]);
					if( $td.data("data") == item ){
						$td.empty();
						$td.remove();
						if( this._equalWidth == true ){
							this._$element.find("table tr td").css("width", (100 / len) + "%");
						}
						return;
					}
				}
			}
		};
		js.$public.selectedItem = function(item){
			if( item === undefined )
				return this._selectedItem;
			
			if( this._items.indexOf(item) == -1 )
				return;
			var oldValue = this._selectedItem;
			this._selectedItem = item;
			this.callAfterRendered(function(){
				var $tds = this._$element.find("table tr td");
				var $td;
				for( var i = 0; i < $tds.length; i++ ){
					$td = $($tds[i]);
					if( $td.data("data") == item ){
						this.selectedEl($td);
						if( oldValue !== this._selectedItem )
							this.dispatchEvent("change", {oldValue:oldValue, newValue:this._selectedItem});
						return;
					}
				}
			});
		};
		js.$protected.template = function(options){
			return StringUtil.join(
				'<table class="qs-m-buttonbar">',
					'<tr>',
					'</tr>',
				'</table>'
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
				position: ''
			}, this._options, options);
			
			this.$super($element, options);
			
			if( options.classes.length > 0 )
				$element.addClass(options.classes);
			
			this.bindEvent(qs.m.events.touchstart, this.onTouchStart);
			this.bindEvent(qs.m.events.touchend, this.onTouchEnd);
		};
		this.onTouchStart = function(evt){
		}
		this.onTouchEnd = function(evt){
			var $td = $(evt.target).closest('td', this._$element);
			if( $td.length == 0 )
				return;
			var item = $td.data("data");
			if( !js.isNull(item) ){
				if( this._toggleEnabled == true && item.toggle != false ){
					this.selectedEl($td);
					return;
				}
			}
			this.dispatchEvent("buttonClick", {button:$td[0], data:item});
		}
		this.selectedEl = function($el){
			if( $el === undefined ){
				if( !js.isNull(this._$element) )
					return this._$element.find("td.selected");
				else
					return null;
			}
			var index = $el.index();
			var $tds = this._$element.find("table tr td");
			var $td;
			for( var i = 0; i < $tds.length; i++ ){
				$td = $($tds[i]);
				$td.removeClass("left right selected");
				if( i == index - 1 )
					$td.addClass("left");
				else if( i == index + 1 )
					$td.addClass("right");
			}
			$el.addClass("selected");
			
			var oldValue = this._selectedItem;
			this._selectedItem = this._items[index];
			if( oldValue !== this._selectedItem )
				this.dispatchEvent("change", {oldValue:oldValue, newValue:this._selectedItem});
		}
	});
})(qs, jQuery);
