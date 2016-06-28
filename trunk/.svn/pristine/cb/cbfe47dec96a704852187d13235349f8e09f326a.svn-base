var AppWindow = {};
AppWindow.openALink = function(alink) {
	// if (external["openALink"] != null)
	alink = decodeURIComponent(alink);
	alink = alink.replace(/#/g, "\"");
	if (external != null)
		external.openALink(alink);
}
AppWindow.getDesktopValue = function(name) {
	// if (external["getDesktopValue"] != null)
	if (external != null)
		return external.getDesktopValue(name);
}

var PortalViewport = Ext.extend(Ext.Viewport, {
	actions : null,
	tools : null,
	configuration : null,
	constructor : function(_config) {
		// Ext.useShims = true;// 默认情况下，Ext 会自动决定是否填充浮动的元素。如果使用
		// Flash，需要把它设置为 true。
		this.configuration = _config;
		this.actions = [new Ext.Action({// 0
					text : '添加',
					iconCls : 'db-icn-add',
					handler : function() {
						var win = new SelectWindow(global);
						win.show();
					},
					scope : this
				}), new Ext.Action({
							text : '刷新',
							iconCls : 'db-icn-refresh',
							handler : function() {
								location.reload();
							},
							scope : this
						})];
		this.tools = [{
					id : 'refresh',
					qtip : '刷新',
					handler : function(e, target, panel) {
						if(panel.items.items[0].store){
							panel.items.items[0].refresh();
						}
					}
				}, {
					id : 'close',
					handler : function(e, target, panel) {
						Ext.Msg.confirm("请确认", "确定要删除【" + panel.title
										+ "】配置页面吗？", function(button,
										text) {
									if (button == "yes") {
										panel.ownerCt.remove(panel,
												true);
										global
												.save(global.items.items[0]);
									}
								});
					}
				}];

		var items = [];
		for (var i = 0; i < _config.length; i++) {
			var cf = _config[i];
			var item = {};
			item.style = "padding:2px 2px 2px 2px";
			item.items = [];
			item.columnWidth = 1 / _config.length;
			// item.border = false;
			// item.autoScroll = true;
			items.push(item);
			if (cf instanceof Array) {
				this.createItems(cf, item.items);
			} else {
				this.createItem(cf, item.items);
			}
		}
		var global = this;
		PortalViewport.superclass.constructor.call(this, {
					layout : 'border',
					items : [{
								xtype : 'portal',
								region : 'center',
								margins : '2 2 2 2',
								tbar : [this.actions[0], '-',
										this.actions[1]],
								items : items,
								listeners : {
									'drop' : function(dd) {
										global.saveConfig(dd);
									}
								}
							}]
				});
	},
	createItems : function(items, panel) {
		for (var i = 0; i < items.length; i++) {
			var cf = items[i];
			if (cf instanceof Array) {
				this.createItems(cf, panel);
			} else {
				this.createItem(cf, panel);
			}
		}
	},
	createItem : function(value, panel) {
		var item = {};
		item.title = value.title;
		item.name = value.name;
		item.tools = this.tools;
		item.frame = true;
		item.collapsible = true;
		item.draggable = true;
		item.cls = 'x-portlet';
		item.layout = "fit";
		// if (value.width)
		// item.width = parseInt(value.width);
		if (value.height)
			item.height = parseInt(value.height);

		if (value.type == "panel") {
			if (value.htmlUrl) {
				var url = {};
				url.url = value.htmlUrl;
				item.autoLoad = url;
			} else if (value.html) {
				item.html = value.html;
			}
			item.autoScroll = true;
		} else if (value.type == "grid") {
			item.items = new SampleGrid(value);
		}

		panel.push(item);
	},
	saveConfig : function(dd) {
		this.save(dd.portal);
	},
	save : function(portal) {
		var profile = [];
		var items = portal.items.items;
		for (var i = 0; i < items.length; i++) {
			var _items = items[i].items;
			if (_items.items.length > 0) {
				var _is = _items.items;
				if (_is.length > 1) {
					var array = [];
					profile.push(array);
					for (var j = 0; j < _is.length; j++) {
						array.push(_is[j].name);
					}
				} else {
					profile.push(_is[0].name);
				}
			} else
				profile.push([]);
		}

		Ext.Ajax.request({
					url : 'xml/savePortal.jsp',
					method : 'post',
					success : function(resp, opts) {

					},
					failure : function(resp, opts) {

					},
					params : {
						config : Ext.util.JSON.encode(profile)
					},
					scope : this
				});
	},
	findItem : function(name, items) {
		for (var i = 0; i < items.length; i++) {
			if (items[i] instanceof Array) {
				var item = this.findItem(name, items[i]);
				if (item)
					return item;
			} else {
				if (items[i].name == name) {
					return items[i];
				}
			}
		}

		return null;
	},
	findPortalItem : function(name, items) {
		for (var i = 0; i < items.length; i++) {
			var _item = items[i].items;
			if (_item.items.length > 0) {
				var _is = _item.items;
				for (var j = 0; j < _is.length; j++) {
					if (name == _is[j].name)
						return _is[j];
				}
			}
		}

		return null;
	},
	createItemPanel : function(value) {
		var item = new Ext.Panel();
		item.title = value.title;
		item.name = value.name;
		item.tools = this.tools;
		item.frame = true;
		item.collapsible = true;
		item.draggable = true;
		item.cls = 'x-portlet';
		item.layout = "fit";
		// if (value.width)
		// item.width = parseInt(value.width);
		if (value.height)
			item.height = parseInt(value.height);

		if (value.type == "panel") {
			if (value.htmlUrl) {
				var url = {};
				url.url = value.htmlUrl;
				item.autoLoad = url;
			} else if (value.html) {
				item.html = value.html;
			}
		} else if (value.type == "grid") {
			item.add(new SampleGrid(value));
			item.doLayout();
		}

		return item;
	},
	addItems : function(items) {
		var n = 0;
		for (var i = 0; i < items.length; i++) {
			var profile = items[i].json;
			var item = this.findPortalItem(profile.name,
					this.items.items[0].items.items);
			if (item) {
				continue;
			}
			this.addItem(profile, n);
			n++;
			if (n == 3)
				n = 0;
		}

		this.save(this.items.items[0]);
	},
	addItem : function(item, n) {
		var items = this.items.items[0].items.items;
		var panel = this.createItemPanel(item);

		Ext.getCmp(items[n].id).add(panel);
		this.doLayout();
	}
});