(function(qs, $) {
	js.$class("qs.m.Scrollable", qs.ui.Control, [qs.ui.IContentControl], function(){
		//构造函数
		this.initialize = function(options){
			this.$super(options);
			this._content = null;
			this._v = true;
			this._h = false;
			this._useTopLeft = false;
			// threshold: Number
			//		drag threshold value in pixels
			this.threshold = 4;
			// scrollBar: Boolean
			//		show scroll bar or not
			this.scrollBar = true;
			// constraint: Boolean
			//		bounce back to the content area
			this.constraint = true;
			// dirLock: Boolean
			//		disable the move handler if scroll starts in the unexpected direction
			this.dirLock = true;
			// weight: Number
			//		frictional drag
			this.weight = 0.6;
		};
		js.$public.content = function(val){
			if( val === undefined )
				return this._content;
			if( js.instanceOf(val, qs.ui.Control) ){
				this._content = val;
				if( this.isRendered() ){
					if( this._content.isRendered() ){
						this._$scrollable.append(this._content.$element());
					}
					else {
						var $el = $("<div></div>");
						this._$scrollable.append($el);
						this._content.render($el);
					}
				}
			}
		}
		js.$public.margin = function(value){
			if( value === undefined ){
				if( this._margin == null ){
					if( this._$scrollable == null )
						return {top:0, bottom:0, left:0, right:0};
					else
						return this._$scrollable.margin();
				}
				else
					return this._margin;
			}
			var type = js.type(value);
			if( type == "number" )
				this._margin = {top:value, left:value, right:value, bottom:value};
			else if( type == "object" )
				this._margin = $.extend({top:0, left:0, right:0, bottom:0}, value);
			
			if( this._$scrollable != null ){
				this._$scrollable.margin(this._margin);
				if( this._$scrollable.css("position") == "absolute" ){
					var m = this.margin();
					if( m.left > 0 )
						this.left(m.left);
					if( m.top > 0 )
						this.top(m.top);
				}
			}
		};
		js.$protected.template = function(options){
			return StringUtil.join(
					'<div class="qs-m-scrollable"></div>'
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
			
			this._$scrollable = this._$element.children(".qs-m-scrollable");
			
			this.bindEvent(qs.m.events.touchstart, this.onTouchStart);
			this.bindEvent(qs.m.events.touchmove, this.onTouchMove);
			this.bindEvent(qs.m.events.touchend, this.onTouchEnd);
			
			var data = this.data('data');
			if( data ){
				if( data.scrollDir == "v" ){
					this._v = true;
					this._h = false;
				}
				else if( data.scrollDir == "h" ){
					this._v = false;
					this._h = true;
				}
				else if( data.scrollDir == "vh" || data.scrollDir == "all" ){
					this._v = true;
					this._h = true;
				}
				this.scrollDir = data.scrollDir;
			}
			var margin = $element.margin();
			this._headerHeight = margin.top;
			this._footerHeight = margin.bottom;
			
			this.content(this._content);
		}
		js.$protected.getPos = function(){
			if( this._useTopLeft ){
				var x = parseInt(this._$scrollable.css("left"));
				var y = parseInt(this._$scrollable.css("top"));
				return {x:x||0, y:y||0};
			}
			else {
				var m = this._$scrollable.getComputedStyle()[jQuery.cssProps["transform"]];//css("transform");
				if(m && m.indexOf("matrix") === 0){
					var arr = m.split(/[,\s\)]+/);
					// IE10 returns a matrix3d
					var i = m.indexOf("matrix3d") === 0 ? 12 : 4;
					return {y:arr[i+1] - 0, x:arr[i] - 0};
				}
				else
					return {x:0, y:0};
			}
		}
		js.$protected.getDim = function(){
			var margin = this._$scrollable.margin();
			this._headerHeight = margin.top;
			this._footerHeight = margin.bottom;
			
			// summary:
			//		Returns various internal dimensional information needed for calculation.
			var el = this._$scrollable[0];
			var parent = this._$scrollable.parent()[0];
			var d = {};
			// content width/height
			d.c = {h : el.offsetHeight, w : el.offsetWidth};

			// view width/height
			d.v = {h : parent.offsetHeight, w : parent.offsetWidth};

			// display width/height
			d.d = {h : d.v.h - this._headerHeight - this._footerHeight, w : d.v.w};

			// overflowed width/height
			d.o = {h : d.c.h - d.v.h + this._headerHeight + this._footerHeight, w : d.c.w - d.v.w};
			return d;
		}
		js.$protected.setScrollTo = function(to){
			var x = to.x;
			var y = to.y;
			if( this._v && !this._h )
				x = 0;
			if( this._h && !this._v )
				y = 0;
			if( this._useTopLeft )
				this._$scrollable.css({top:y, left:x});
			else
				this._$scrollable.css("transform", "translate(" + x + "px, " + y + "px) translateZ(0px)")
				
			this.scrollScrollBarTo(this.calcScrollBarPos(to));
		}
		js.$protected.adjustDestination = function(to, pos, dim){
			// summary:
			//		A stub function to be overridden by subclasses.
			// description:
			//		This function is called from onTouchEnd(). The purpose is to give its
			//		subclasses a chance to adjust the destination position. If this
			//		function returns false, onTouchEnd() returns immediately without
			//		performing scroll.
			// to:
			//		The destination position. An object with x and y.
			// pos:
			//		The current position. An object with x and y.
			// dim:
			//		Dimension information returned by getDim().			

			// subclass may want to implement
			return true; // Boolean
		}
		js.$public.scrollTo = function(x, y){
			x = js.param(x, 0);
			y = js.param(y, 0);
			var pos = this.getPos();
			this._bounce = null;
			this.slideTo(pos, {x:x, y:y}, 0.3, "ease-out");
		}
		js.$protected.slideTo = function(pos, to, duration/*Number*/, easing/*String*/){
			var self = this;
			qs.ui.animation.slide(this._$scrollable, to, duration, easing, function(){
				if( self._bounce ){
					self.slideTo(to, self._bounce, 0.3, "ease-out");
					self._bounce = null;
					setTimeout(function(){
						self.hideScrollBar();
					}, 0.3);
				}
				else
					self.hideScrollBar();
			});
			this.slideScrollBarTo(to, duration, easing);
			if( duration < 0.1 )
				this.hideScrollBar();
		},
		/**
		 * 检查是否是输入控件
		 */
		this.isFormElement = function($el){
			if( $el.is("input") )
				return true;
			return false;
		}
		this.onTouchStart = function(e){
			this._startPos = this.getPos();
			qs.ui.animation.stop(this._$scrollable);
			if( this.isFormElement($(e.target)) )
				return;
			//检查父节点中是否也有Scrollable控件
			if( !this._checkParentScrollable ){
				var parent = this.findAncestor(qs.m.Scrollable);
				if( parent != null && parent._v == this._v && parent._h == this._h )
					this._hasSameParent = true;
				this._checkParentScrollable = true;
			}
			//当父子Scrollable控件方向相同时应阻止事件冒泡
			if( this._hasSameParent == true )
				e.stopPropagation();

			e = e.originalEvent;
			
			this._touchStartX = e.touches ? e.touches[0].pageX : e.clientX;
			this._touchStartY = e.touches ? e.touches[0].pageY : e.clientY;
			this._startTime = (new Date()).getTime();
			this._dim  = this.getDim();
			this._time = [0];
			this._posX = [this._touchStartX];
			this._posY = [this._touchStartY];
			this._locked = false;
			
			this._touchAction = true;
			this.setCapture(this._$element, "touchmove mousemove touchend mouseup");
			this.setScrollTo(this._startPos);
			//return false;
		}
		this.onTouchMove = function(e){
			if(this._locked){ return; }
			
			if( this._touchAction == true ){
				e.preventDefault();
				
				e = e.originalEvent;
				
				var x = e.touches ? e.touches[0].pageX : e.clientX;
				var y = e.touches ? e.touches[0].pageY : e.clientY;
				
				var dx = x - this._touchStartX;
				var dy = y - this._touchStartY;
				var to = {x : this._startPos.x + dx, y:this._startPos.y + dy};
				var dim = this._dim;
				
				dx = Math.abs(dx);
				dy = Math.abs(dy);
				if(this._time.length == 1){ // the first TouchMove after TouchStart
					if(this.dirLock){
						if(this._v && !this._h && dx >= this.threshold && dx >= dy ||
							(this._h || this._f) && !this._v && dy >= this.threshold && dy >= dx){
							this._locked = true;
							return;
						}
					}
					if(this._v && this._h){ // scrollDir="hv"
						if(dy < this.threshold &&
						   dx < this.threshold){
							return;
						}
					}else{
						if(this._v && dy < this.threshold ||
						   (this._h || this._f) && dx < this.threshold){
							return;
						}
					}
					this.showScrollBar();
				}
				this._scrollAction = true;
				
				var weight = this.weight;
				if(this._v){
					if( this.constraint ){
						if(to.y > 0){ // content is below the screen area
							to.y = Math.round(to.y * weight);
						}else if(to.y < -dim.o.h){ // content is above the screen area
							if(dim.c.h < dim.d.h){ // content is shorter than display
								to.y = Math.round(to.y * weight);
							}else{
								to.y = -dim.o.h - Math.round((-dim.o.h - to.y) * weight);
							}
						}
					}
					else {
						if( to.y > 0 )
							to.y = 0;
						var maxY = this._$scrollable[0].scrollHeight - this._$element.height();
						if( -to.y > maxY )
							to.y = -maxY;
					}
				}
				if((this._h || this._f)){
					if( this.constraint ){
						if(to.x > 0){
							to.x = Math.round(to.x * weight);
						}else if(to.x < -dim.o.w){
							if(dim.c.w < dim.d.w){
								to.x = Math.round(to.x * weight);
							}else{
								to.x = -dim.o.w - Math.round((-dim.o.w - to.x) * weight);
							}
						}
					}
					else {
						if( to.x > 0 )
							to.x = 0;
						var maxX = this._$scrollable[0].scrollWidth - this._$element.width();
						if( -to.x > maxX )
							to.x = -maxX;
					}
				}
				
				this.setScrollTo(to);
				
				var max = 10;
				var n = this._time.length; // # of samples
				if(n >= 2){
					// Check the direction of the finger move.
					// If the direction has been changed, discard the old data.
					var d0, d1;
					if(this._v && !this._h){
						d0 = this._posY[n - 1] - this._posY[n - 2];
						d1 = y - this._posY[n - 1];
					}else if(!this._v && this._h){
						d0 = this._posX[n - 1] - this._posX[n - 2];
						d1 = x - this._posX[n - 1];
					}
					if(d0 * d1 < 0){ // direction changed
						// leave only the latest data
						this._time = [this._time[n - 1]];
						this._posX = [this._posX[n - 1]];
						this._posY = [this._posY[n - 1]];
						n = 1;
					}
				}
				if(n == max){
					this._time.shift();
					this._posX.shift();
					this._posY.shift();
				}
				this._time.push((new Date()).getTime() - this._startTime);
				this._posX.push(x);
				this._posY.push(y);
				
				//return false;
			}
		}
		this.onTouchEnd = function(e){
			this.releaseCapture(this._$element, "touchmove mousemove touchend mouseup");
			this._touchAction = false;
			if( this._scrollAction == true ){
				this._scrollAction = false;
				e.preventDefault();
				if(this._locked){ return; }
				
				var pos = this.getPos();
				var speed = this._speed = this.getSpeed();
				var dim = this._dim;
				var pos = this.getPos();
				var to = {}; // destination
				
				if(pos.x == 0 && pos.y == 0){ return; } // initializing
				dim = this.getDim();

				if(this._v){
					to.y = pos.y + speed.y;
				}
				if(this._h || this._f){
					to.x = pos.x + speed.x;
				}

				if(this.adjustDestination(to, pos, dim) === false){ return; }

				if(this.constraint){
					if(this.scrollDir == "v" && dim.c.h < dim.d.h){ // content is shorter than display
						this.slideTo(pos, {y:0}, 0.3, "ease-out"); // go back to the top
						return false;
					}else if(this.scrollDir == "h" && dim.c.w < dim.d.w){ // content is narrower than display
						this.slideTo(pos, {x:0}, 0.3, "ease-out"); // go back to the left
						return false;
					}else if(this._v && this._h && dim.c.h < dim.d.h && dim.c.w < dim.d.w){
						this.slideTo(pos, {x:0, y:0}, 0.3, "ease-out"); // go back to the top-left
						return false;
					}
				}

				var duration, easing = "ease-out";
				var bounce = {};
				if(this._v && this.constraint){
					if(to.y > 0){ // going down. bounce back to the top.
						if(pos.y > 0){ // started from below the screen area. return quickly.
							duration = 0.3;
							to.y = 0;
						}else{
							to.y = Math.min(to.y, 20);
							easing = "linear";
							bounce.y = 0;
						}
					}else if(-speed.y > dim.o.h - (-pos.y)){ // going up. bounce back to the bottom.
						if(pos.y < -dim.o.h){ // started from above the screen top. return quickly.
							duration = 0.3;
							to.y = dim.c.h <= dim.d.h ? 0 : -dim.o.h; // if shorter, move to 0
						}else{
							to.y = Math.max(to.y, -dim.o.h - 20);
							easing = "linear";
							bounce.y = -dim.o.h;
						}
					}
				}
				if((this._h || this._f) && this.constraint){
					if(to.x > 0){ // going right. bounce back to the left.
						if(pos.x > 0){ // started from right of the screen area. return quickly.
							duration = 0.3;
							to.x = 0;
						}else{
							to.x = Math.min(to.x, 20);
							easing = "linear";
							bounce.x = 0;
						}
					}else if(-speed.x > dim.o.w - (-pos.x)){ // going left. bounce back to the right.
						if(pos.x < -dim.o.w){ // started from left of the screen top. return quickly.
							duration = 0.3;
							to.x = dim.c.w <= dim.d.w ? 0 : -dim.o.w; // if narrower, move to 0
						}else{
							to.x = Math.max(to.x, -dim.o.w - 20);
							easing = "linear";
							bounce.x = -dim.o.w;
						}
					}
				}
				this._bounce = (bounce.x !== undefined || bounce.y !== undefined) ? bounce : undefined;

				if(duration === undefined){
					var distance, velocity;
					if(this._v && this._h){
						velocity = Math.sqrt(speed.x*speed.x + speed.y*speed.y);
						distance = Math.sqrt(Math.pow(to.y - pos.y, 2) + Math.pow(to.x - pos.x, 2));
					}else if(this._v){
						velocity = speed.y;
						distance = to.y - pos.y;
					}else if(this._h){
						velocity = speed.x;
						distance = to.x - pos.x;
					}
					if(distance === 0 && !e){ return; } // #13154
					duration = velocity !== 0 ? Math.abs(distance / velocity) : 0.01; // time = distance / velocity
				}
				this.slideTo(pos, to, duration, easing);
				
				return false;
			}
		}
		this.getSpeed = function(){
			// summary:
			//		Returns an object that indicates the scrolling speed.
			// description:
			//		From the position and elapsed time information, calculates the
			//		scrolling speed, and returns an object with x and y.
			var x = 0, y = 0, n = this._time.length;
			// if the user holds the mouse or finger more than 0.5 sec, do not move.
			if(n >= 2 && (new Date()).getTime() - this._startTime - this._time[n - 1] < 500){
				var dy = this._posY[n - (n > 3 ? 2 : 1)] - this._posY[(n - 6) >= 0 ? n - 6 : 0];
				var dx = this._posX[n - (n > 3 ? 2 : 1)] - this._posX[(n - 6) >= 0 ? n - 6 : 0];
				var dt = this._time[n - (n > 3 ? 2 : 1)] - this._time[(n - 6) >= 0 ? n - 6 : 0];
				y = this.calcSpeed(dy, dt);
				x = this.calcSpeed(dx, dt);
			}
			return {x:x, y:y};
		},

		this.calcSpeed = function(distance/*Number*/, time/*Number*/){
			// summary:
			//		Calculate the speed given the distance and time.
			return Math.round(distance / time * 100) * 4;
		}
		
		js.$protected.showScrollBar = function(){
			// summary:
			//		Shows the scroll bar.
			// description:
			//		This function creates the scroll bar instance if it does not
			//		exist yet, and calls resetScrollBar() to reset its length and
			//		position.

			if(!this.scrollBar){ return; }

			var dim = this._dim;
			if(this.scrollDir == "v" && dim.c.h <= dim.d.h){ return; }
			if(this.scrollDir == "h" && dim.c.w <= dim.d.w){ return; }
			if(this._v && this._h && dim.c.h <= dim.d.h && dim.c.w <= dim.d.w){ return; }
			
			var createBar = function(self, dir){
				var bar = self["_$scrollBar" + dir];
				if(!bar){
					var wrapper = $('<div class="qs-m-ScrollbarWrapper" style="position:absolute;overflow:hidden"/>')
					self._$scrollable.parent().append(wrapper);
					if(dir == "V"){
						wrapper.css({right:"2px", width:"5px"});
					}else{
						wrapper.css({bottom:self._footerHeight + 2 + "px", height:"5px"});
					}
					self["_$scrollBarWrapper" + dir] = wrapper;

					bar = $('<div />');
					wrapper.append(bar);
					bar.css({
						opacity: 0.6,
						position: "absolute",
						backgroundColor: "#606060",
						fontSize: "1px",
						borderRadius: "2px",
						transformOrigin: "0 0",
						zIndex: 2147483647 // max of signed 32-bit integer
					});
					if( dir == "V" )
						bar.width(5);
					else
						bar.height(5);
					self["_$scrollBar" + dir] = bar;
				}
				return bar;
			};
			if(this._v ){
				if( this._$scrollBarV ){
					this._$scrollBarV.fadeIn(0);
					this._$scrollBarV.css("opacity", 0.6);
				}
				else
					this._$scrollBarV = createBar(this, "V");
				
			}
			if(this._h){
				if( this._$scrollBarH ){
					this._$scrollBarH.fadeIn(0);
					this._$scrollBarH.css("opacity", 0.6);
				}
				else
					this._$scrollBarH = createBar(this, "H");
			}
			this.resetScrollBar();
		}
		this.resetScrollBar = function(){
			// summary:
			//		Resets the scroll bar length, position, etc.
			var f = function(wrapper, bar, d, c, hd, v){
				if(!bar){ return; }
				var props = {};
				props[v ? "top" : "left"] = hd + 4 + "px"; // +4 is for top or left margin
				var t = (d - 8) <= 0 ? 1 : d - 8;
				props[v ? "height" : "width"] = t + "px";
				wrapper.css(props);
				var l = Math.round(d * d / c); // scroll bar length
				l = Math.min(Math.max(l - 8, 5), t); // -8 is for margin for both ends
				bar.css(v ? "height" : "width", l + "px");
				bar.css("opacity", 0.6);
			};
			var dim = this.getDim();
			f(this._$scrollBarWrapperV, this._$scrollBarV, dim.d.h, dim.c.h, this._headerHeight, true);
			f(this._$scrollBarWrapperH, this._$scrollBarH, dim.d.w, dim.c.w, 0);
			//this.createMask();
		}
		this.hideScrollBar = function(){
			if(this._$scrollBarV){
				this._$scrollBarV.fadeOut(300);
			}
			if(this._$scrollBarH){
				this._$scrollBarH.fadeOut(300);
			}
		}
		this.calcScrollBarPos = function(to/*:Object*/){
			// summary:
			//		Calculates the scroll bar position.
			// description:
			//		Given the scroll destination position, calculates the top and/or
			//		the left of the scroll bar(s). Returns an object with x and y.
			// to:
			//		The scroll destination position. An object with x and y.
			//		ex. {x:0, y:-5}			

			var pos = {};
			var dim = this._dim;
			var f = function(wrapperH, barH, t, d, c){
				var y = Math.round((d - barH - 8) / (d - c) * t);
				if(y < -barH + 5){
					y = -barH + 5;
				}
				if(y > wrapperH - 5){
					y = wrapperH - 5;
				}
				return y;
			};
			if(typeof to.y == "number" && this._$scrollBarV){
				pos.y = f(this._$scrollBarWrapperV[0].offsetHeight, this._$scrollBarV[0].offsetHeight, to.y, dim.d.h, dim.c.h);
			}
			if(typeof to.x == "number" && this._$scrollBarH){
				pos.x = f(this._$scrollBarWrapperH[0].offsetWidth, this._$scrollBarH[0].offsetWidth, to.x, dim.d.w, dim.c.w);
			}
			
			return pos;
		}
		this.scrollScrollBarTo = function(to/*:Object*/){
			// summary:
			//		Moves the scroll bar(s) to the given position without animation.
			// to:
			//		The destination position. An object with x and/or y.
			//		ex. {x:2, y:5}, {y:20}, etc.

			if(!this.scrollBar){ return; }
			if(this._v && this._$scrollBarV && typeof to.y == "number"){
				if( this._useTopLeft )
					this._$scrollBarV.css("top", to.y);
				else
					this._$scrollBarV.css({"transition":"", "transform": "translate(0px, " + to.y + "px) translateZ(0px)"});
			}
			if(this._h && this._$scrollBarH && typeof to.x == "number"){
				if( this._useTopLeft )
					this._$scrollBarH.css("left", to.x);
				else
					this._$scrollBarH.css({"transition":"", "transform": "translate(" + to.x + "px, 0px) translateZ(0px)"});
			}
		}
		this.slideScrollBarTo = function(to/*:Object*/, duration/*:Number*/, easing/*:String*/){
			// summary:
			//		Moves the scroll bar(s) to the given position with the slide animation.
			// to:
			//		The destination position. An object with x and y.
			//		ex. {x:0, y:-5}
			// duration:
			//		Duration of the animation in seconds. (ex. 0.3)
			// easing:
			//		The name of easing effect which webkit supports.
			//		"ease", "linear", "ease-in", "ease-out", etc.

			if(!this.scrollBar){ return; }
			var fromPos = this.calcScrollBarPos(this.getPos());
			var toPos = this.calcScrollBarPos(to);
			if(this._v && this._$scrollBarV){
				qs.ui.animation.slide(this._$scrollBarV, {y:toPos.y}, duration, easing);
			}
			if(this._h && this._$scrollBarH){
				qs.ui.animation.slide(this._$scrollBarH, {x:toPos.x}, duration, easing);
			}
		}
		this.flashScrollBar = function(){
			// summary:
			//		Shows the scroll bar instantly.
			// description:
			//		This function shows the scroll bar, and then hides it 300ms
			//		later. This is used to show the scroll bar to the user for a
			//		short period of time when a hidden view is revealed.
			if(!this._$element){ return; }
			this._dim = this.getDim();
			if(this._dim.d.h <= 0){ return; } // dom is not ready
			this.showScrollBar();
			var _this = this;
			setTimeout(function(){
				_this.hideScrollBar();
			}, 300);
		}
	});
})(qs, jQuery)