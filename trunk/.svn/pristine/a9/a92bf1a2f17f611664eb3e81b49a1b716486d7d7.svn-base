var SelectWindow = Ext.extend(Ext.Window, {
			constructor : function(el) {
				var global = this;
				var sm = new Ext.grid.CheckboxSelectionModel({
					singleSelect : false
				});
				var columns = [sm,{
							header : '名称',
							dataIndex : 'name'
						}];

				var store = new Ext.data.JsonStore({
							remoteSort : false,
							fields : ['name'],
							proxy : new Ext.data.HttpProxy({
										url : "xml/portalQuery.jsp"
									})
						});
				var grid = new Ext.grid.GridPanel({
							stripeRows : true,
							viewConfig : {
								forceFit : true
							},
							frame : true,
							loadMask : {
								msg : '数据正在加载中...，请稍后'
							},
							store : store,
							sm: sm,
							columns : columns,
							listeners : {
								'render' : function(e) {
									store.load();
								},
								delay : 200,
								scope : this
							}
						});

				SelectWindow.superclass.constructor.call(this, {
							title : '用户选择Panel窗口',
							layout:'fit',
							closable : true,
							width : 600,
							height : 400,
							shadow : false,
							plain : true, // true则主体背景透明，false则主体有小差别的背景色，默认为false
							closeAction : 'close', // 枚举值为：close(默认值)，当点击关闭后，关闭window窗口;hide,关闭后，只是hidden窗口
							border : false,
							draggable : true, // false 不可拖动
							constrain : false, // true则强制此window控制在viewport，默认为false
							modal : true, // true为模式窗口，后面的内容都不能操作，默认为false
							resizable : true,// 是否可以改变大小
							items : [grid],
							buttons : [{
										text : '确定',
										disabled : false,
										handler : function() {
											var records = grid.getSelectionModel().getSelections();
											if(records.length != 0){
												el.addItems(records);
												global.close();
											}
										}
									}, {
										text : '取消',
										disabled : false,
										handler : function() {
											global.close();
										}
									}],
							listeners : {
								'afterlayout' : function() {
									this.center();
								}
							}
						});
			}
		});