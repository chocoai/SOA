ws.form = {};
ws.form.TextBox    = {};
ws.form.Hidden     = {};
ws.form.Form       = {};
ws.form.CheckBox   = {};
ws.form.RadioGroup = {};

//====Begin TextBox ===========================================================
/**
 * �����ı��ؼ���ʾֵ(this�����ı�����ؼ�Ԫ��)
 * @param value �ı���ʾֵ
 */
ws.form.TextBox.setValue = function(value){
	this.firstChild.value = value;
}
/**
 * ��ȡ�ı��ؼ�ֵ(this�����ı�����ؼ�Ԫ��)
 * @return �ı��ؼ�ֵ
 */
ws.form.TextBox.getValue = function(){
	return this.firstChild.value;
}
/**
 * ���û������ַ���δ���¿ؼ���ֵ֮ǰ������Ч�Լ��
 */
ws.form.TextBox.inputCheck = function(ctrlEl){
	var dataType = ctrlEl.getAttribute("datatype");
	if( dataType == "Number" ){
		var code = event.keyCode;
		if( (code >= 48 && code <= 57 ) || code == 46 || code == 45 ){
			return;
		}
		else {
			event.cancelBubble = true;
			event.returnValue  = false;
		}
	}
}
/**
 * �Կؼ���ֵ������Ч�Լ�飬������������򷵻ش�����Ϣ
 */
ws.form.TextBox.check = function(){
	var dataType = this.getAttribute("datatype");
	if( dataType == "Number" ){
		var min = this.getAttribute("min");
		var max = this.getAttribute("max");
		if( min == null || min == undefined || min == "" )
			Number.MIN_VALUE;
		else 
			min = ws.toInt(min);
		if( max == null || max == undefined || max == "" )
			max = Number.MAX_VALUE;
		else
			max = ws.toInt(max);
		var value = this.getValue();
		if( value == "" )
			return "";
		value = ws.toInt(value);
		if( value < min || value > max ){
			return "�����ֵ�������" + min + "����С��" + max;
		}
	}
	return "";
}

/**
 * �����ı�����ؼ�,ÿһ���ı�����ؼ�������setValue()��getValue()����,��֧��
 * ͳһ�����û��ȡֵ������.
 * @param elForm �ı�����ؼ�������FormԪ��
 * @param ctrlEl �����ı�����ؼ��Ķ���
 * @return ���ɵ��ı�����ؼ�
 */
ws.form.TextBox.make = function(ctrlEl){
	var expcode, id, el;
	var width;
	id = "txb" + ws.generateId();
	ctrlEl.innerHTML = "";
	
	var lines = ctrlEl.lines;
	if( lines == undefined || lines == null || lines <= 0 )
		lines = 1;
	var password = ctrlEl.password;
	if( password == undefined || password == null )
		password = false;

	if( lines == 1 ){
		if( password == true || password == "true")
			el = ws.createElement('<input type="password" value="">', ctrlEl);
		else
			el = ws.createElement('<input type="text" value="">', ctrlEl);
	}
	else
		el = ws.createElement('<textarea rows="' + lines + '" >', ctrlEl);
	el.id    = id;
	el.name  = ctrlEl.name;
	if( ctrlEl.value != undefined && ctrlEl.value != null )
		el.value = ctrlEl.value;
	else
		el.value = "";
	width = ctrlEl.width;
	if( width )
		el.style.width = width;
	el.className = ctrlEl.getAttribute("css");
	
	el.onkeypress   = function(){ ws.form.TextBox.inputCheck(ctrlEl); }

	ctrlEl.setValue = ws.form.TextBox.setValue;
	ctrlEl.getValue = ws.form.TextBox.getValue;
	ctrlEl.check    = ws.form.TextBox.check;
	return ctrlEl;
}
//====End TextBox =============================================================

//====Begin Hidden ============================================================
ws.form.Hidden.setValue = function(value){
	this.firstChild.value = value;
}
ws.form.Hidden.getValue = function(){
	return this.firstChild.value;
}

ws.form.Hidden.make = function(ctrlEl){
	var expcode, id, el;
	var width;
	ctrlEl.innerHTML = "";
	
	el = ws.createElement('<input type="hidden" value="">', ctrlEl);
	el.name  = ctrlEl.name;
	if( ctrlEl.value != undefined && ctrlEl.value != null )
		el.value = ctrlEl.value;
	else
		el.value = "";
	
	ctrlEl.setValue = ws.form.Hidden.setValue;
	ctrlEl.getValue = ws.form.Hidden.getValue;
	return ctrlEl;
}
//====End Hidden ==============================================================

//====Begin RadioGroup ========================================================
/**
 * ���õ���ѡ������ֵ(this������ѡ���ؼ�Ԫ��)
 * @param value ����ѡ������ֵ
 */
ws.form.RadioGroup.setValue = function(value){
	var radio;
	for( var i = 0; i < this.radios.length; i++){
		radio = this.radios[i];
		if( radio.value == value )
			radio.checked = true;
	}
}
/**
 * ��ȡ����ѡ������ֵ(this������ѡ���ؼ�Ԫ��)
 * @return ����ѡ������ֵ
 */
ws.form.RadioGroup.getValue = function(){
	var radio;
	for( var i = 0; i < this.radios.length; i++){
		radio = this.radios[i];
		if( radio.checked == true )
			return radio.value;
	}
}

/**
 * ���ɵ���ѡ�����,��ͨ��setValue()��getValue()ֱ�����úͻ�ȡ��ֵ
 * @param elForm ����ѡ�����������FormԪ��
 * @param ctrlEl ��������ѡ�����Ķ���
 * @return ���ɵĵ���ѡ�����
 */
ws.form.RadioGroup.make = function(ctrlEl){
	var childs = ctrlEl.all.tags("span");
	var layout = ctrlEl.getAttribute("layout");
	var ctl, el, id, rdo, group;
	var name = ctrlEl.name + "rdo";
	var radios = new Array();
	
	group = ws.createElement("<fieldset>");
	el	  = ws.createElement("<legend>", group);
	el.innerHTML = ctrlEl.title;
	group.style.cssText = ctrlEl.style.cssText;

	for( var i = 0; i < childs.length; i++ ){
		id = "rdo" + ws.generateId();
		ctl = childs[i];
		if( ctl.type === "RadioBox" ){
			rdo = ws.createElement("<span>", group); 
			el  = ws.createElement("<input type='radio' name='" + name + "'>", rdo);
			el.type    = "radio";
			el.id      = id;
			el.value   = ctl.getAttribute("value");
			el.checked = ctl.getAttribute("checked");

			radios.push(el);
			el = ws.createElement("<label for='" + id + "'>", rdo);
			el.innerHTML  = ctl.innerHTML;
			if( layout == "vertical" )
				ws.createElement("<br>", group); 
		}
	}
	ctrlEl.replaceNode(group);

	group.radios = radios;
	group.setValue = ws.form.RadioGroup.setValue;
	group.getValue = ws.form.RadioGroup.getValue;
	return group;
}
//====End RadioGroup ==========================================================

//====Begin CheckBox ==========================================================
/**
 * ����ѡ���ؼ�ֵ(this����ѡ���ؼ�Ԫ��)
 * @value ѡ����ֵ
 */
ws.form.CheckBox.setValue = function(value){
	if( value == true )
		this.checked = true;
	else this.checked = false;
}
/**
 * ��ȡѡ���ؼ�ֵ(this����ѡ���ؼ�Ԫ��)
 * @return ѡ����ֵ
 */
ws.form.CheckBox.getValue = function(){
	return this.checked;
}
/**
 * ����ѡ���ؼ�,��ͨ��setValue()��getValue()ֱ�����úͻ�ȡ��ֵ
 * @param elForm ѡ���ؼ�������FormԪ��
 * @param ctrlEl ����ѡ���ؼ��Ķ���
 * @return ���ɵ�ѡ���ؼ�
 */
ws.form.CheckBox.make = function(ctrlEl){
	var expcode, id, el, label, text;
	id = "chk" + ws.generateId();
	
	text = ctrlEl.innerHTML;
	ctrlEl.innerHTML = "";

	el = ws.createElement("<input type='checkbox' >", ctrlEl);
	el.id    = id;
	el.name  = ctrlEl.name;
	el.className = ctrlEl.getAttribute("css");
	
	label = ws.createElement("<label for='" + id + "'>", ctrlEl);
	label.innerHTML = text;
	
	el.setValue = ws.form.CheckBox.setValue;
	el.getValue = ws.form.CheckBox.getValue;
	return el;	
}
//====End CheckBox ============================================================

//====Begin Form ==============================================================
/**
 * ��ȡForm��ֵ,Form��ֵ��һ��Object��ʾ,��Object��������Form�е�����Ԫ���������Ӧ,
 * ������ֵҲΪ��ӦԪ�ص�ֵ,��Object��_class��ʾForm��Ӧ������������
 * @return ����Form�и�����Ԫ�����ݵ�Object����
 */
ws.form.Form.getValue = function(){
	var obj = new Object();
	var child, value, exp;
	for(var i = 0; i < this._properties.length; i++){
		child = this._properties[i];
		value = child.getValue();
		exp = "obj." + child.name + "=value";
		eval(exp);
	}
	obj._class = this.type;
	return obj;
}
/**
 * ����Form��ֵ,value��һ��Object����,��������������Form������Ԫ�ص��������Ӧ.
 * value._class������Form.type��һ��.
 * @param value ����Formֵ�Ķ���
 */
ws.form.Form.setValue = function(value){
	var child, itemValue;
	if( value != null && value._class != this.type )
		return;

	for(var i = 0; i < this._properties.length; i++){
		child = this._properties[i];
		if( value == null || value[child.name] == null || value[child.name] == undefined )
			itemValue = "";
		else 
			itemValue = value[child.name];
		child.setValue(itemValue);
	}
}

/**
 * ����Form����չForm����Ϊ,FormҲ֧��ͳһ��setValue()��getValue()����,�Է������ݵ��������ȡ.
 * @param standin formԪ�ػ�form ID
 */
ws.form.Form.make = function(standin){
	var elForm = null;
	if( typeof(standin) == "string" )
		elForm = document.getElementById(standin);
	else if ( typeof(standin) == "object" )
		elForm = standin;
	if( elForm == null )
		return;

	var childs = elForm.all.tags("span");
	var _properties = new Array();
	var _items     = new Object();
	var _required  = new Object();
	var ctrlEl, el;
	for( var i = 0; i < childs.length; i++ ){
		ctrlEl = childs[i];
		if( ctrlEl.type == "TextBox" ){
			el = ws.form.TextBox.make(ctrlEl);
			_properties.push(el);
		} else if( ctrlEl.type == "RadioGroup" ){
			el = ws.form.RadioGroup.make(ctrlEl);
			_properties.push(el);
		} else if( ctrlEl.type == "CheckBox" ){
			el = ws.form.CheckBox.make(ctrlEl);
			_properties.push(el);
		} else if( ctrlEl.type == "SelectBox" ){
			el = ws.form.SelectBox.make(ctrlEl);
			_properties.push(el);
		} else if( ctrlEl.type == "ComboBox" ){
			el = ws.form.ComboBox.make(ctrlEl);
			_properties.push(el);
		} else if( ctrlEl.type == "Hidden" ){
			el = ws.form.Hidden.make(ctrlEl);
			_properties.push(el);
		} else if( ctrlEl.type == "Label" ){
			var name = ctrlEl.getAttribute("name");
			if( name != undefined && name != "" ){
				if( _items[name] != undefined && _items[name] != null ){
					_items[name].text    = ctrlEl.innerText;
					_items[name].labelEl = ctrlEl.ctrlEl;
				}
				else
					_items[name] = {text: ctrlEl.innerText, labelEl: ctrlEl};
			}
			continue;
		}else continue;

		var name = ctrlEl.getAttribute("name");
		if( name != undefined && name != "" ){
			var requiredValue = ctrlEl.getAttribute("required");
			if( requiredValue == "true" )
				_required[name] = true;
			if( _items[name] != undefined && _items[name] != null )
				_items[name].inputEl = ctrlEl;
			else
				_items[name] = {input: ctrlEl};
		}
	}
	for( var i in _required ){
		var value = _required[i];
		if( value == true && _items[i] != null && _items[i] != undefined ){
			var el = _items[i].labelEl;
			if( typeof( el ) == "object" ){
				ws.form.Form.setRequiredLabel(el);
			}
		}
	}
	elForm._properties = _properties;
	elForm._items	   = _items;
	elForm._required   = _required;
	elForm.getValue    = ws.form.Form.getValue;
	elForm.setValue    = ws.form.Form.setValue;
	elForm.check	   = ws.form.Form.check;
	elForm.getItemValue= ws.form.Form.getItemValue;
	elForm.setItemValue= ws.form.Form.setItemValue;

	return elForm;
}

ws.form.Form.getItemValue = function(itemName){
	var item = this._items[itemName];
	if( item != null && item != undefined )
		item = item.inputEl;
	if( item != null && item != undefined )
		return item.getValue();
	else
		return null;
}
ws.form.Form.setItemValue = function(itemName, itemValue){
	var item = this._items[itemName];
	if( item != null && item != undefined )
		item = item.inputEl;
	if( item != null && item != undefined )
		return item.setValue(itemValue);
}

ws.form.Form.setRequiredLabel = function(el){
	ws.addClass(el, "required");
	el.innerHTML += '<span class="required_flag">*</span>';
}
ws.form.Form.check = function(){
	var resultMsg = "", msg = "";
	for( var i in this._items ){
		var tmp = "";
		if( this._items[i].inputEl && typeof(this._items[i].inputEl.check) == "function" )
			tmp = this._items[i].inputEl.check();
		if( tmp != "" ){
			if( this._items[i].text != undefined && this._items[i].text != "")
				msg += '<b>' + this._items[i].text + ":</b>" + tmp + "<br/>";
			else
				msg += '<b>' + i + ":</b>" + tmp + "<br/>";
		}
	}
	if( msg != "" )
		resultMsg += '<span style="font:icon;"><span style="color:red">������������</span><br/>' + msg + '</span>';;
		
	msg = "";
	for( var i in this._required ){
		var value = this._required[i];
		if( value == true && this._items[i] != null && this._items[i] != undefined && this._items[i].inputEl != undefined && this._items[i].inputEl != null){
			var value = this._items[i].inputEl.getValue();
			if( value == null || value == "" || value == undefined ){
				if( msg != "" )
					msg += ",";
				if( this._items[i] != null && this._items[i] != undefined && this._items[i].text != undefined && this._items[i].text != "")
					msg += '<b>' + this._items[i].text + "</B>";
				else
					msg += '<b>' + i + "</b>";
			}
		}
	}
	if( msg != "" )
		resultMsg += '<span style="font:icon;"><span style="color:red">�������������ݣ�</span><br/>' + msg + '</span>';
	
	if( resultMsg == "" )
		return true;
	ws.showMessageBox(resultMsg);
	return false;
}
//====End Form ================================================================

