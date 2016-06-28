var SampleGrid = Ext.extend(Ext.grid.GridPanel, {
	cfg : null,
	constructor : function(_config) {
		cfg = _config;
		var columns = [];
		columns.push(new Ext.grid.RowNumberer());
		var fields = [];
		if (_config.columns) {
			var cols = _config.columns;
			for (var i = 0; i < cols.length; i++) {
				var col = {};
				col.header = cols[i].title;
				col.dataIndex = cols[i].name;
				// col.menuDisabled = true;
				if (cols[i].sort) {
					col.sortable = cols[i].sort;
				} else {
					col.sortable = false;
				}

				if (cols[i].width) {
					col.width = cols[i].width;
				}

				if (cols[i].type == "checkbox") {
					col.renderer = this.rendererCheckbox;
				} else if (cols[i].type == "time") {
					col.renderer = Ext.util.Format.dateRenderer('Y-m-d H:i:s');
				} else if (cols[i].type == "alink_template") {
					col.renderer = this.rendererAlink;
				} else if (cols[i].type == "alink_field") {
					col.renderer = this.rendererAlinkFree;
				}

				columns.push(col);

				var field = {};
				field.name = cols[i].name;
				if (cols[i].type == "alink_field"
						|| cols[i].type == "alink_template") {
					field.defaultValue = cols[i].alink;
				}
				fields.push(field);
			}
		}

		var store = null;
		if (_config.dataUrl) {
			/*
			 * if (_config.isPage == "true") { store = new Ext.data.JsonStore({
			 * root : 'data', totalProperty : 'totol', remoteSort : false,
			 * fields : fields, proxy : new Ext.data.HttpProxy({ url :
			 * _config.dataUrl }) }); } else { store = new Ext.data.JsonStore({
			 * remoteSort : false, fields : fields, proxy : new
			 * Ext.data.HttpProxy({ url : _config.dataUrl }) }); }
			 */
			store = new Ext.data.JsonStore({
						root : 'data',
						totalProperty : 'total',
						remoteSort : false,
						fields : fields,
						proxy : new Ext.data.HttpProxy({
									url : _config.dataUrl
								})
					});
		} else if (_config.data) {
			store = new Ext.data.Store({
						reader : new Ext.data.ArrayReader({}, fields),
						data : _config.data
					});
		}

		var bbar = null;
		if (_config.isPage == "true") {
			bbar = new Ext.PagingToolbar({
						store : store,
						pageSize : _config.pageSize,
						displayInfo : true,
						displayMsg : '第 {0} - {1} 条  共 {2} 条',
						emptyMsg : "没有记录"
					});
		}
		SampleGrid.superclass.constructor.call(this, {
					stripeRows : true,
					viewConfig : {
						forceFit : true
					},
					loadMask : {
						msg : '数据正在加载中...，请稍后'
					},
					store : store,
					columns : columns,
					bbar : bbar,
					listeners : {
						'render' : function(e) {
							if (_config.isPage == "true") {
								store.load({
											params : {
												start : 0,
												limit : _config.pageSize
											}
										});
							} else {
								store.load();
							}
						},
						delay : 200,
						scope : this
					}
				});
	},
	refresh : function() {
		if (cfg.isPage == "true") {
			this.store.load({
						params : {
							start : 0,
							limit : cfg.pageSize
						}
					});
		} else {
			this.store.load();
		}
	},
	rendererCheckbox : function(val, meta, record) {
		return val ? '是' : '否';
	},
	rendererAlink : function(val, meta, record) {
		var data = record.json;
		var col = meta.id - 1;
		var c = record.fields.items[col];
		var alinkS = c.defaultValue;
		var alinkValue = alinkS.replace('${record.id}', data.id);
		var href = '<a href="javascript:void(0);" style="text-decoration:none;" onclick="AppWindow.openALink(\''+ encodeURIComponent(alinkValue) + '\');">' + val + '</a>';
		return href;
	},
	rendererAlinkFree : function(val, meta, record) {
		var data = record.json;
		var col = meta.id - 1;
		var c = record.fields.items[col];
		var alinkS = c.defaultValue;
		var href = '<a href="javascript:void(0);" style="text-decoration:none;" onclick="AppWindow.openALink(\''+ encodeURIComponent(data[alinkS]) + '\');">' + val + '</a>';
		return href;
	}
});