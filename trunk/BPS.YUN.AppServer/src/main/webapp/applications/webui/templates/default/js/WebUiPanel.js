/*******************************************************************************
 * 版权所有 : 宁波金奥 版本 : 1.0 创建时间: 2009-8-29 创建者: 赵颖杰
 ******************************************************************************/
Ext.BLANK_IMAGE_URL = '../../js/extjs/resources/images/default/s.gif';
Ext.QuickTips.init();
var pageTab = null;// action = page
var pageTabBool = false;// action = page
var northHeight = 67;// Toolbar的高度
/**
 * 主面板
 * 
 * @class WebUiPanel
 * @extends Ext.Viewport
 */
WebUiPanel = Ext.extend(Ext.Viewport, {
	tabarList : null,// 工具栏
	contentPanel : null,// 右边具体功能面板区
	constructor : function(_cfg, home_cfg) {
		Ext.useShims = true;// 默认情况下，Ext 会自动决定是否填充浮动的元素。如果使用 Flash，需要把它设置为 true。
		this.contentPanel = new Ext.TabPanel({
			region : 'center',
			enableTabScroll : true,
			activeTab : 0,
			border : false,
			frame : true,
			defaults : {
//				autoWidth : true,
//				autoHeight : true,
				animScroll : true
			},
			items : [{
						id : 'homePage',
						title : '首页',
						autoScroll : true,
						autoLoad : {
							url : 'webui.jsp',
							scripts : true
						}
					}],
			listeners : {
				// 传进去的三个参数分别为:这个_tabpanel,当前标签页,事件对象e
				"contextmenu" : function(_tabpanel, _tabitem, e) {
					var menu = new Ext.menu.Menu({
						shadow : false,// 关闭菜单栏背影
						items : [{
									text : "关闭当前页",
									iconCls : 'db-icn-abrogate',
									handler : function() {
										if (!(_tabitem.id == 'homePage'))
											_tabpanel.remove(_tabitem);
									}
								}, {
									text : "关闭其他所有页",
									iconCls : 'db-icn-clear',
									handler : function() {
										// 循环遍历
										_tabpanel.items.each(function(item) {
													if (item.closable
															&& item != _tabitem) {
														// 可以关闭的其他所有标签页全部关掉
														_tabpanel.remove(item);
													}
												});
									}
								}]
					});
					// 显示在当前位置
					menu.showAt(e.getPoint());
				},
				/** tab切换事件 */
				'tabchange' : function(_tabpanel, _tabitem) {
					if (pageTab != null) {
						if (_tabitem.id != pageTab.id) {
							imeDivShow(0);
							pageTabBool = false;
						} else {
							pageTabBool = true;
							imeDivShow(1);
						}
					}
				},
				'afterlayout' : function() {
					if (pageTab && pageTabBool) {
						imeDivShow(1);
					}
				}
			}

		});
		var datatime = function() {
			var tmpDate = new Date();
			var date = tmpDate.getDate();
			var month = tmpDate.getMonth() + 1;
			var year = tmpDate.getYear();
			var data_time = year + "年" + month + "月" + date + "日"

			var myArray = new Array(6);
			myArray[0] = "星期日";
			myArray[1] = "星期一";
			myArray[2] = "星期二";
			myArray[3] = "星期三";
			myArray[4] = "星期四";
			myArray[5] = "星期五";
			myArray[6] = "星期六";
			var weekday = tmpDate.getDay();
			if (weekday == 0 | weekday == 6) {
				return (data_time + " " + myArray[weekday]);
			} else {
				return (data_time + " " + myArray[weekday]);
			}
		};
		WebUiPanel.superclass.constructor.call(this, {
			layout : 'border',
			items : [{
				region : 'north',
				height : northHeight,
				border : false,
				html : '<div style="position:absolute;Top:41px;left:378px;width:400px;"><span style="font-size: 12px;color: #225CB0;">Copyright&copy;2009-2010 宁波金奥软件科技有限公司 版权所有</span></div>'
						+ '<div id="systemInformation" style="position:absolute;Top:5px; right:20px;width:400px; text-align: right;">'
						+ '<span id="header-font">'
						+ userSession
						+ '&nbsp;&nbsp;&nbsp;&nbsp;欢迎登录系统 | 系统时间：'
						+ datatime()
						+ '</span></div>'
						+ '<div id="systemInformationImage" style="position:absolute;top:30px; width:72px; height:21px;"> <a href="../../login.jsp"><img src="../../images/help.gif" border="0" /></a></div>'
						+ '<div id="_systemInformationImage" style="position:absolute;top:30px; width:72px; height:21px;"> <a href="../../login.jsp"><img src="../../images/exit.gif" border="0" /></a></div>'
						+ '<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" background="../../images/limianbj.jpg">'
						+ '<tr>'
						+ '<td valign="middle" style="BACKGROUND: url(../../images/limiantop-webui.jpg) no-repeat left top;HEIGHT: 67px;">'
						+ '</td>' + '</tr>' + '</table>'

			}, {
				region : 'center',
				id : 'imetabcenter',
				layout : 'fit',
				items : [this.contentPanel]

			}, {
				region : 'west',
				id : 'all_menus',
				title : '菜单',
				width : 200,
				border : true,// 边框
				collapsible : false,
				split : true,
				layout : 'fit',
				items : [new Ext.Panel({
							layout : 'accordion',
							border : false,// 边框
							layoutConfig : {
								activeOnTop : false,
								fill : true,
								hideCollapseTool : false,
								titleCollapse : true,
								animate : true
							},
							items : [this.getMenus(_cfg)]
						})],
				listeners : {
					'expand' : function() {
						if (pageTab && pageTabBool) {
							imeDivShow(1);
						}
					},
					'collapse' : function() {
						if (pageTab && pageTabBool)
							imeDivShow(1);
					}
				}
			}, {
				region : 'south',
				height : 30,
				bodyStyle : "background-color:#dfe8f6;",
				border : false
			}],
			listeners : {
				'afterlayout' : function() {
					var systemInformation = document
							.getElementById('systemInformation');
					var systemInformationImage = document
							.getElementById('systemInformationImage');
					var _systemInformationImage = document
							.getElementById('_systemInformationImage');

					if (systemInformation != undefined) {
						if (document.body.offsetWidth
								- systemInformation.style.pixelWidth <= 778) {
							systemInformation.style.pixelLeft = 778;
						} else {
							systemInformation.style.pixelLeft = document.body.offsetWidth
									- systemInformation.style.pixelWidth - 20;
						}
					}
					if (systemInformationImage != undefined) {
						if (document.body.offsetWidth
								- systemInformationImage.style.pixelWidth <= 778) {
							systemInformationImage.style.pixelLeft = 1106;
						} else {
							systemInformationImage.style.pixelLeft = systemInformation.style.pixelLeft
									+ 240;
						}
					}
					if (_systemInformationImage != undefined) {
						if (document.body.offsetWidth
								- _systemInformationImage.style.pixelWidth <= 778) {
							_systemInformationImage.style.pixelLeft = 1029;
						} else {
							_systemInformationImage.style.pixelLeft = systemInformation.style.pixelLeft
									+ 240
									+ _systemInformationImage.style.pixelWidth
									+ 5;
						}
					}
				}
			}
		});
	},
	/**
	 * 动态生成菜单栏 (形式产用树形结构) 关于EXT的accordion中放treepanel在IE中不显示横向滚动条的问题解决
	 * Viewport->westPanel，然后westPanel的layout为accordion，
	 * 然后在westPanel中放多个treePanel， 这时在IE，显示如果树超出了显示范围只会出现纵向滚动条而不会出现横向滚动条，
	 * 如果把westPanel继续拖小，连纵向滚动条也会被遮住。
	 * 多次尝试后的处理方式是在每个treePanel外面再包一层layout为border的Panel，treePanel的region:
	 * 'center', 然后把这个新加的Panel加到westPanel中就好了。
	 * =================================== 官方有开发人员回贴了，有了更好的解决方案，
	 * 在jsp或html中增加如下CSS： <style type="text/css"> .ext-ie .x-tree .x-panel-body
	 * {position: relative;} .ext-ie .x-tree .x-tree-root-ct {position:
	 * absolute;} </style>
	 * 
	 * 
	 */
	getMenus : function(_cfg) {
		var data = _cfg;
		var menusList = [];
		for (var i = 0; i < data.length; i++) {
			var root = new Ext.tree.TreeNode({
						id : i,
						text : ''
					});
			var tree = new Ext.tree.TreePanel({
						region : 'center',
						split : true,
						root : root,
						rootVisible : false,
						lines : false,
						layout : 'fit',
						autoScroll : true,
						border : false
					});
			tree.on('click', function(_node, event) {
						this.addTab(_node, event);// 左边树单击右边网格同步
					}, this);
			menusList[i] = new Ext.Panel({
						id : data[i].uid,
						title : data[i].label,
						iconCls : data[i].iconUrl,
						layout : 'border',
						items : this.childrenMenu(data[i].children, root, tree)
					});
		}
		return menusList;
	},
	/**
	 * 动态生成子菜单(对应相应的父菜单)(结构产用树形结构) 子部门 自定义属性 alink : data[i].alink addTab :
	 * function(_node, event)中取值用_node.attributes.alink
	 * 
	 * @param {}
	 *            data 菜单数据
	 * @param {}
	 *            root 树形根节点对象
	 * @param {}
	 *            tree 树形对象
	 * @return {}
	 */
	childrenMenu : function(data, root, tree) {
		if (data == undefined)
			return tree;
		for (var i = 0; i < data.length; i++) {
			var c1 = new Ext.tree.TreeNode({
						id : data[i].uid,
						text : data[i].label,
						iconCls : "db-icn-meun",
						alink : data[i].alink,
						action : data[i].action,
						url : data[i].url,
						property : data[i]
					});
			if (!(data[i].children == undefined))
				this.childrenMenu(data[i].children, c1, tree);
			root.appendChild(c1);
		}
		return tree;
	},
	/**
	 * 增加TabPanel页面
	 * 
	 * @param {}
	 *            _node 树节点
	 * @param {}
	 */
	addTab : function(_node, event) {
		event.stopEvent();
		var n = this.contentPanel.getComponent(_node.id);
		if (_node.attributes.action == 'alink') {
			if (!n) { // 判断是否已经打开该面板
				n = this.contentPanel.add({
							id : _node.id,
							title : _node.text,
							closable : true,
							autoLoad : {
								url : webui.openALink('imeframe',
										_node.attributes.alink),
								scripts : true
							}
						});
			}
			this.contentPanel.setActiveTab(n);
		}
		if (_node.attributes.action == 'page') {
			if (pageTab == null) {
				if (!n) { // 判断是否已经打开该面板
					n = this.contentPanel.add({
								id : _node.id,
								title : _node.text,
								closable : true,
								listeners : {
									'destroy' : function() {
										pageTab = null;
										pageTabBool = false;
										imeDivShow(0);
									}
								}
							});
				}
				pageTabBool = true;
				pageTab = n;
			} else {
				pageTab.setTitle(_node.text);
				n = pageTab;
				pageTabBool = true;
			}
			this.contentPanel.setActiveTab(n);
			imeDivShow(1);
			webui.changePage('imeframe', _node.attributes.property);
		}
		if (_node.attributes.action == 'webpage') {
			if (!n) {
				n = this.contentPanel.add({
					id : _node.id,
					title : _node.text,
					closable : true,
					layout : 'border',
					items : [
						new Ext.Panel({
							region : 'center',
							autoScroll : true,
							autoLoad : {
								url : _node.attributes.url,
								nocache : true,
								scripts : true
							}
						})]
				});
			}
			this.contentPanel.setActiveTab(n);
		}
	}
});
/**
 * iframe控制窗口大小位置的方法
 * 
 * @param {}
 *            _show 传递类型 int型,(0:是隐藏,1:显示大小,2:菜单栏收缩/展开,3:菜单栏左右拖动和web页面大小改变)
 */
function imeDivShow(_show) {
	var imeTabCenter = document.getElementById("imetabcenter");
	var imeDiv = document.getElementById("imediv");
	var all_menus = document.getElementById("all_menus");
	if (_show == 0) {
		imeDiv.style.pixelTop = 0;
		imeDiv.style.pixelLeft = 0;
		imeDiv.style.pixelWidth = 0;
		imeDiv.style.pixelHeight = 0;
	} else {
		imeDiv.style.pixelTop = northHeight + 29;

		if (all_menus.offsetWidth == 0) {
			imeDiv.style.pixelLeft = 27 + 6;
			imeDiv.style.pixelWidth = document.body.offsetWidth - 27 - 7;
		} else {
			imeDiv.style.pixelLeft = all_menus.offsetWidth + 6;
			imeDiv.style.pixelWidth = document.body.offsetWidth
					- all_menus.offsetWidth - 7;
		}
		imeDiv.style.pixelHeight = document.body.clientHeight - 60
				- northHeight;
	}
}