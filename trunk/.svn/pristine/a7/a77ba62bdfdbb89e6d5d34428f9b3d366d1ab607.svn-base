/*
 javascript的OOP实现
 示例代码：
	js.$interface("IAnimal", function(){
		js.$public.say = function(){};
	});
	//"Animal"是类名，[IAnimal]是实现的接口
	js.$class("Animal", [IAnimal], function(){
		//公有属性
		js.$public.age = 0;
		//构造函数
		this.initialize = function(age){
			this.age = age;
		}
	});
	//"Cat"是类名，Animal是父类，类名、父类、接口可以省略一个或多个
	js.$class("Cat", Animal, function(){
		this.initialize = function(name, age){
			this.$super(age); // 调用父类的构造函数
			this.name = name;
		}
		//定义公有变量prop1; 问题：这里定义的值在多个实例对象间指向的是同一个实例
		js.$public.prop1 = "";
		//定义保护变量prop2(尚未实现，目前可见性与公有变量一样)
		js.$protected.prop2 = "";
		//定义公有函数foo
		js.$public.foo = function(){
		}
		//定义保护函数foo2
		js.$protected.foo2 = function(){
		}
		//定义私有函数foo3
		function foo3(){
			//在这个函数中不能用this获取类实例
		}
		//定义私有变量prop3
		var prop3;
		//实现IAnimal接口
		js.$public.say = function(){
			alert("hello " + this.name);
		}
	});
	
	var tom = new Cat("tom");
	tom.say();
	js.instanceOf(tom, Cat);	//返回true
	js.instanceOf(tom, Animal); //返回true
	js.instanceOf(tom, IAnimal);//返回true
*/

if (!Object.create) {
    Object.create = function (o) {
        function F() {}
        F.prototype = o;
        return new F();
    };
}
if( !String.prototype.trim ){
	String.prototype.trim = function(){
		return this.replace(/(^\s*)|(\s*$)/g, "");
	};
}

var js = {};
//{ 类定义中的辅助对象
js.$public = {};
js.$protected = {};
js.$static = {};
js.next_class_id = 0;
//}
//类原型定义Map<类ID, 类原型>
js.classPrototypeMap = {};
js.classPrototypeMap["Object"] = new Object();

/**
 * 类的方法包装，将类方法的原始定义进行包装后进行额外的控制，如检查是否直接调用受保护的方法等。
 * @param key 方法定义的名称
 * @param method 方法定义
 * @return 方法包装的函数定义
 */
js.wrapMethod = function(key, method, is_$protected, superclass){
	var wrapper = function(){
		//如果this.$caller==null说明是由外部直接调用的
		if (method.$$protected && this.$caller == null) 
			throw new Error('不能调用受保护的方法:"' + key + '".');
		var caller = this.$caller;
		this.$caller = {$name:key, $superclass:superclass};
		var result = method.apply(this, arguments);
		this.$caller = caller;
		return result;
	};
	if( is_$protected == true )
		method.$$protected = true;
	wrapper.$name = key;
	wrapper.$superclass = superclass;
	return wrapper;
};
/**
 * 在类的方法中调用父类方法的定义
 */
js.$super = function(){
	//this.$caller是指方法的包装对象,通过this.$caller.$superclass来传递上父类对象
	if (!this.$caller) 
		throw new Error('不能调用"$super"方法.');
	var name = this.$caller.$name,
		parent = this.$caller.$superclass || this.$superclass,
		method = (parent) ? parent[name] : null;
	if (!method) 
		throw new Error('父类中找不到"' + name + '"方法.');

	var superclass = this.$caller.$superclass;
	this.$caller.$superclass = parent.$superclass;
	var result = method.apply(this, arguments);
	this.$caller.$superclass = superclass;
	return result;
};

js.instanceOf = function(obj, classOrInterface){
	if( obj == null || obj == undefined )
		return false;
	if( classOrInterface == null || classOrInterface == undefined )
		return false;
	if( classOrInterface.$type == "Interface" ){
		if( obj.$implements ){
			for( var i = 0; i < obj.$implements.length; i++ ){
				if( obj.$implements[i] == classOrInterface )
					return true;
			}
		}
		if( obj.$superclass )
			return js.instanceOf(obj.$superclass, classOrInterface);
		return false;
	}
	else
		return (obj instanceof classOrInterface);
};
js.type = function(obj){
	if( obj == null )
		return "null";
	if( obj == undefined )
		return "undefined";
	if( obj.$type == "Interface" || obj.$type == "Class" )
		return obj.$typename;
	
	var type = typeof(obj);
	if (obj == null)
		return "null";

	if (obj instanceof Date) {
		return "date";
	} else if (obj instanceof Array) {
		return "array";
	}
	return type;
};
js.isClass = function(clazz){
	if( clazz && clazz.$type == "Class" )
		return true;
	return false;
};
js.isInterface = function(clazz){
	if( clazz && clazz.$type == "Interface" )
		return true;
	return false;
};
js.newInstance = function(className){
	var type = js.type(className);
	var parts;
	if( type == "string" )
		parts = className.split(".");
	else
		parts = type.split(".");
	if( parts.length == 0 )
		return null;
	var clazz = window;
	for( var i = 0; i < parts.length; i++){
		clazz = clazz[parts[i]];
		if( clazz === undefined || clazz === null )
			throw new Error(className + " is not defined.");
	}
	if( js.isClass(clazz) ){
		var args = Array.prototype.slice.call(arguments, 1);
	    function F() {
	        return clazz.apply(this, args);
	    }
	    F.prototype = clazz.prototype;
	    return new F();
	};
	throw new Error(className + " is not a class.");
};

/**
 * 检查并写入指定的类是否为纯虚类
 * @param clazz 类定义
 * @visibility private
 */
js.checkAbstractClass = function(clazz){
	var interfaces = [];
	var superclass = clazz;
	while(superclass){
		if( superclass.$implements ){
			for( var i = 0; i < superclass.$implements.length; i++ )
				interfaces.push(superclass.$implements[i]);
		}
		superclass = superclass.$superclass;
	}
	var msg = "", error = [];
	for( var i = 0; i < interfaces.length; i++ ){
		error.length = 0;
		for( var m in interfaces[i] ){
			if( m.charAt(0) == "$" )
				continue;
			if ( typeof clazz.prototype[m] != typeof interfaces[i][m] ) {
				error.push( m );
			}
		}
		if( error.length > 0 )
			msg += interfaces[i].$typename + ":" + error.join(",") + ";";
	}
	
	if( msg.length > 0 ){
		clazz.prototype.$isAbstractClass = true;
		clazz.$isAbstractClass = true;
		clazz.prototype.$unimplements = msg;
		clazz.$unimplements = msg;
	}
	else {
		clazz.$isAbstractClass = false;
		clazz.prototype.$isAbstractClass = false;
	}
};
/**
 * 声明一个接口
 * @param interfaceName 接口名称
 * @param interfaceDef 接口定义,必须是一个function
 */
js.Interface = function(interfaceName, interfaceDef){
	js.$public = {};
	var _interface = new interfaceDef();
	var val;
	for(var n in js.$public ){
		val = js.$public[n];
		_interface[n] = val;
	}
	_interface.$typename = interfaceName;
	_interface.$type = "Interface";
	return _interface;
};
/**
 * 声明一个类, 参数定义为([类名, 父类, 接口], 类定义)，类名、父类和接口可以省略一个或多个。
 * @param classname 类名
 * @param superclass 父类
 * @param interfaces 实现的接口
 * @param subclass 子类定义
 * @return 新声明的类定义
 */
js.Class = function(){
	var classname, superclass, interfaces, subclass;
	if( arguments.length == 0 )
		throw new Error("缺少类定义。");
	subclass = arguments[arguments.length - 1];
	if( typeof subclass != "function" )
		throw new Error("类定义必须是一个Function。");
	for( var i = 0; i < arguments.length - 1; i++ ){
		if( typeof arguments[i] == "string" )
			classname = arguments[i];
		else if( typeof arguments[i] == "function" )
			superclass = arguments[i];
		else if( arguments[i] instanceof Array )
			interfaces = arguments[i];
	}
	superclass = superclass || js.classPrototypeMap["Object"];
	if( superclass.$classId && js.classPrototypeMap[superclass.$classId] )
		superclass = js.classPrototypeMap[superclass.$classId];
	
	subclass.prototype = superclass;
	
	var init_obj = {};
	var clazz = function(){
		if( arguments.length == 1 && arguments[0] === "$create class prototype" )
			return this;
		if( this.$isAbstractClass == true )
			throw new Error('不能实例化纯虚类"' + this.$typename + '", 尚未实现以下接口"' + this.$unimplements);
		//初始化对象
		for( var n in init_obj ){
			this[n] = js.clone(init_obj[n]);
		}
		this.$class = clazz;
		//对构造函数也进行包装
		var caller = this.$caller;
		this.$caller = {$name:"initialize", $superclass:superclass};
		var result = this.initialize ? this.initialize.apply(this, arguments) || this : this;
		this.$caller = caller;
		return result;
	};
	js.$public = {};
	js.$protected = {};
	js.$static = {};
	js.classname = "";

	var subclass_prototype = new subclass();
	var val;
	for( var n in subclass_prototype ){
		val = subclass_prototype[n];
		if( typeof val !== "function" && subclass_prototype.hasOwnProperty(n) ){
			init_obj[n] = subclass_prototype[n];
			delete subclass_prototype[n];
		}
	}
	
	subclass_prototype.$superclass = superclass;
	for(var n in js.$public ){
		val = js.$public[n];
		if( typeof val == "function" )
			subclass_prototype[n] = js.wrapMethod(n, js.$public[n], false, superclass);
		else
			init_obj[n] = val;
	}
	for(var n in js.$protected ){
		val = js.$protected[n];
		if( typeof val == "function" )
			subclass_prototype[n] = js.wrapMethod(n, js.$protected[n], true, superclass);
		else
			init_obj[n] = val;
	}
	for(var n in js.$static ){
		clazz[n] = js.$static[n];
	}

	clazz.prototype = subclass_prototype;
	clazz.prototype.$super = js.$super;

	clazz.$classId = "class" + js.next_class_id;
	js.next_class_id ++;
	if( classname ){
		clazz.prototype.$typename = classname;
		clazz.$typename = classname;
	}
	clazz.prototype.$type = "Class";
	clazz.$type = "Class";

	clazz.prototype.$superclass = superclass;
	clazz.$superclass = superclass;
	
	if( interfaces ){
		clazz.prototype.$implements = interfaces;
		clazz.$implements = interfaces;
	}
	js.checkAbstractClass(clazz);

	js.classPrototypeMap[clazz.$classId] = new clazz("$create class prototype");

	js.$public = {};
	js.$protected = {};
	js.$static = {};

	return clazz;
};
/**
 * 定义Class, 等同于new js.Class(...)
 */
js.$class = function(){
	var className = arguments[0];
	var args = Array.prototype.slice.call(arguments, 0);
    function F() {
        return js.Class.apply(this, args);
    }
    F.prototype = js.Class.prototype;
    var clazz = new F();
	var parts;
	parts = className.split(".");
	if( parts.length == 0 )
		return null;
	var ns = window;
	for( var i = 0; i < parts.length - 1; i++){
		if( ns[parts[i]] )
			ns = ns[parts[i]];
		else {
			ns[parts[i]] = {};
			ns = ns[parts[i]];
		}
	}
	ns[parts[parts.length - 1]] = clazz;
};
/**
 * 定义Interface, 等同于new js.Interface(...)
 */
js.$interface = function(){
	var className = arguments[0];
	var args = Array.prototype.slice.call(arguments, 0);
    function F() {
        return js.Interface.apply(this, args);
    }
    F.prototype = js.Class.prototype;
    var clazz = new F();
	var parts;
	parts = className.split(".");
	if( parts.length == 0 )
		return null;
	var ns = window;
	for( var i = 0; i < parts.length - 1; i++){
		if( ns[parts[i]] )
			ns = ns[parts[i]];
		else {
			ns[parts[i]] = {};
			ns = ns[parts[i]];
		}
	}
	ns[parts[parts.length - 1]] = clazz;
};

(function(stickySymbol) {
	  RegExp.prototype.clone = function(options) {
	    // If the options are not in string format...
	    if(options + "" !== options) {
	      // If the options evaluate to true, use the properties to construct
	      // the flags.
	      if(options) {
	        options = (options.ignoreCase ? "i" : "")
	          + (options.global ? "g" : "")
	          + (options.multiline ? "m" : "")
	          + (options.sticky ? "y" : "");
	      }
	      // If the options evaluate to false, use the current flags.
	      else {
	        options = (this + "").replace(/[\s\S]+\//, "");
	      }
	    }
	     
	    // Return the new regular expression, making sure to only include the
	    // sticky flag if it is available.
	    return new RegExp(this.source, options.replace("y", stickySymbol));
	  };
})("sticky" in /s/ ? "y" : "");

js.clone = function(obj) {
    // Handle the 3 simple types, and null or undefined
    if (null == obj || "object" != typeof obj) return obj;
    
    if( js.type(obj.clone) == "function" )
    	return obj.clone();
    
    // Handle Date
    if (obj instanceof Date) {
        var copy = new Date();
        copy.setTime(obj.getTime());
        return copy;
    }

    // Handle Array
    if (obj instanceof Array) {
        var copy = [];
        var len = obj.length;
        for (var i = 0; i < len; i++ ){
            copy[i] = js.clone(obj[i]);
        }
        return copy;
    }
    //__$cloning_obj表示正在克隆的对象, 加入这个处理是为了防止循环引用
    if( obj.__$cloning_obj !== undefined && obj.__$cloning_obj != null )
    	return obj.__$cloning_obj;
    
    // Handle Object
    if (obj instanceof Object) {
        var copy = new obj.constructor();
        if( obj.$type == "Class" ){
        	copy = new obj.$class();
        }
        obj.__$cloning_obj = copy;
        for (var attr in obj) {
        	if( attr == "__$cloning_obj" )
        		continue;
            if (obj.hasOwnProperty(attr)) copy[attr] = js.clone(obj[attr]);
        }
        delete obj.__$cloning_obj;
     //   if( obj.$type == "Interface" || obj.$type == "Class" ){
     //   	copy.prototype = obj.prototype;
     //   }
        return copy;
    }

    throw new Error("Unable to copy obj! Its type isn't supported.");
};
js.copy = function(source/*:Object*/, target/*:Object*/){
	var v;
	for(var i in source) {
		v = source[i];
		target[i] = v;
    }
};

js.equals = function( x, y ) {
  if ( x === y ) return true;
    // if both x and y are null or undefined and exactly the same

  if ( ! ( x instanceof Object ) || ! ( y instanceof Object ) ) return false;
    // if they are not strictly equal, they both need to be Objects

  if ( x.prototype !== y.prototype ) return false;

  for ( var p in x ) {
    if ( ! x.hasOwnProperty( p ) ) continue;
      // other properties were tested using x.constructor === y.constructor

    if ( ! y.hasOwnProperty( p ) ) return false;
      // allows to compare x[ p ] and y[ p ] when set to undefined

    if ( x[ p ] === y[ p ] ) continue;
      // if they have the same strict value or identity then they are equal

    if ( typeof( x[ p ] ) !== "object" ) return false;
      // Numbers, Strings, Functions, Booleans must be strictly equal

    if ( ! js.equals( x[ p ],  y[ p ] ) ) return false;
      // Objects and Arrays must be tested recursively
  }

  for ( p in y ) {
    if ( y.hasOwnProperty( p ) && ! x.hasOwnProperty( p ) ) return false;
      // allows x[ p ] to be set to undefined
  }
  return true;
};

js.param = function(arg, defaultVal){
	return (typeof arg === 'undefined' ? defaultVal : arg);
};
js.$param = js.param;

js.isNull = function(val){
	if( val === null || val === undefined )
		return true;
	return false;
};
/**
 * 定义名称空间
 * @param ns 名称空间,如com.mycompany
 * @return 生成的名称空间对象
 */
js.$namespace = function(ns){
	if (!ns || !ns.length) {
		return null;
	}

	var levels = ns.split(".");
	var nsobj = window;

	for(var i = 0; i < levels.length; i++){
        nsobj[levels[i]] = nsobj[levels[i]] || {};
        nsobj = nsobj[levels[i]];
    }
	return nsobj;
};

/**
 * 导入名称空间中的所有定义
 * @param ns 名称空间, 如com.mycompany
 * @param scope 导入的目标区域
 */
js.$import = function(ns, scope){
	if (!ns || !ns.length) {
		return;
	}
	scope = js.param(scope, window);
	
	var nsobj = window;
	var levels = ns.split(".");
	
	for(var i = 0; i < levels.length; i++){
        nsobj = nsobj[levels[i]];
        if( nsobj === null || nsobj === undefined )
        	return;
    }
	if( nsobj.$type === "Class" || nsobj.$type === "Interface" ){
		scope[levels[levels.length - 1]] = nsobj;
		return;
	}
	for( var n in nsobj){
		if( nsobj.hasOwnProperty(n) )
			scope[n] = nsobj[n];
	}
};