/*******************************************************************************
 * 版权所有 : 宁波金奥 版本 : 1.0 创建时间: 2009-8-29 创建者: 赵颖杰
 ******************************************************************************/

var pageTabar = null;// action = page
var pageTabarBool = false;// action = page
var toolbarHeight = 92;// Toolbar的高度
var tabarListTop = 67;// this.tabarList置顶高度

var articlelistrefreshObject = null;//删除知识库中的内容是列表刷新的对象
var _categoryId = 0;
/**
 * 主面板
 * 
 * @class WebUiTabarPanel
 * @extends Ext.Viewport
 */
WebUiTabarPanel = Ext.extend(Ext.Viewport, {
	tabarList : null,// 工具栏
	contentPanel : null,// 中间具体功能面板区
	constructor : function(_cfg, home_cfg) {
		Ext.useShims = true;// 默认情况下，Ext 会自动决定是否填充浮动的元素。如果使用 Flash，需要把它设置为 true。

		this.contentPanel = new Ext.TabPanel({
			region : 'center',
			enableTabScroll : true,
			activeTab : 0,
			border : false,
			autoScroll : true,
			resizeTabs : true,
			defaults : {
				animScroll : true
			},
			items : [{
						id : 'homePage',
						title : '首页',
						autoScroll : true,
						html : ''
					}],
			listeners : {
				// 传进去的三个参数分别为:这个_tabpanel,当前标签页,事件对象e

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
					if (pageTabar != null) {
						if (_tabitem.id != pageTabar.id) {
							imeDivShow_1(0);
							pageTabarBool = false;
						} else {
							pageTabarBool = true;
							imeDivShow_1(1);
						}
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
		}
		var panel = new Ext.Panel({
			region : 'north',
			border : false,
			height : 65,
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
		});
		this.tabarList = new Ext.Toolbar({
			region : 'center',
			border : false,
			autoShow : true,
			autoScroll : true,
			items : [
					new Ext.Toolbar.TextItem('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'),
					new Ext.Action({// 0
						text : '知识库',
						tooltip : '知识库',
						tooltipType : 'qtip',
						iconCls : 'db-icn-contacts_shared',
						handler : function() {
							var object = {};
							object.id = '1234567890',
							object.text = '知识库',
							object.iconCls = 'db-icn-contacts_shared',
							object.alink = null,
							object.action = 'repository',
							object.url = null,
							object.property = null,
							this.addTab(object, null);
						},
						scope : this
					})]
		});
		this.getMenus(_cfg);

		WebUiTabarPanel.superclass.constructor.call(this, {
			layout : 'border',
			items : [{
						region : 'north',
						height : toolbarHeight,
						border : false,
						layout : 'border',
						items : [panel, this.tabarList]
					}, {
						region : 'center',
						id : 'imetabcenter',
						layout : 'fit',
						border : false,
						items : [this.contentPanel],
						listeners : {
							'afterlayout' : function() {
								if (pageTabar && pageTabarBool) {
									imeDivShow_1(1);
								}
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
								- systemInformation.style.pixelWidth <= 820) {
							systemInformation.style.pixelLeft = 800;
						} else {
							systemInformation.style.pixelLeft = document.body.offsetWidth
									- systemInformation.style.pixelWidth - 20;
						}
					}
					if (systemInformationImage != undefined) {
						if (document.body.offsetWidth
								- systemInformationImage.style.pixelWidth <= 820) {
							systemInformationImage.style.pixelLeft = 1106;
						} else {
							systemInformationImage.style.pixelLeft = systemInformation.style.pixelLeft
									+ 240;
						}
					}
					if (_systemInformationImage != undefined) {
						if (document.body.offsetWidth
								- _systemInformationImage.style.pixelWidth <= 820) {
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
	/** 动态生成导航栏 */
	getMenus : function(_cfg) {
		var data = _cfg;
		for (var i = 0; i < data.length; i++) {
			this.tabarList.add({
						id : data[i].uid,
						text : data[i].label,
						iconCls : data[i].iconUrl,
						alink : data[i].alink,
						action : data[i].action,
						url : data[i].url,
						property : data[i],
						menu : this.childrenMenu(data[i].children)
					});
			if (i != data.length - 1)
				this.tabarList.add('-');
		}
	},
	/**
	 * 动态生成子菜单(对应相应的父菜单)
	 * 
	 * @param {}
	 *            data
	 * @return {}
	 */
	childrenMenu : function(data) {
		var childrenMenu = new Ext.menu.Menu({
					shadow : false,// 关闭菜单栏背影
					items : []
				});
		if (data == undefined)
			return childrenMenu;
		for (var i = 0; i < data.length; i++) {
			var object = {};

			object.id = data[i].uid;
			object.text = data[i].label;
			object.iconCls = data[i].iconUrl;
			object.alink = data[i].alink;
			object.action = data[i].action;
			object.url = data[i].url;
			object.property = data[i];

			if (!(data[i].children == undefined))
				object.menu = this.childrenMenu(data[i].children);

			childrenMenu.addItem(object);
		}
		childrenMenu.on('itemclick', function(_node, event) {
					this.addTab(_node, event);
				}, this);
		return childrenMenu;
	},
	/**
	 * 增加TabPanel页面
	 * 
	 * @param {}
	 *            _node menu节点
	 * @param {}
	 */
	addTab : function(_node, event) {
		//event.stopEvent();
		var n = this.contentPanel.getComponent(_node.id);
		if (_node.action == 'alink') {
			if (!n) { // 判断是否已经打开该面板
				n = this.contentPanel.add({
							id : _node.id,
							title : _node.text,
							closable : true,
							autoScroll : true,
							autoLoad : {
								url : webui.openALink('imeframe', _node.alink),
								scripts : true
							}
						});
			}
			this.contentPanel.setActiveTab(n);
		}
		if (_node.action == 'page') {
			if (pageTabar == null) {
				if (!n) { // 判断是否已经打开该面板
					n = this.contentPanel.add({
								id : _node.id,
								title : _node.text,
								closable : true,
								autoScroll : true,
								listeners : {
									'destroy' : function() {
										pageTabar = null;
										pageTabarBool = false;
										imeDivShow_1(0);
									}
								}
							});
				}
				pageTabarBool = true;
				pageTabar = n;
			} else {
				pageTabar.setTitle(_node.text);
				n = pageTabar;
				pageTabarBool = true;
			}
			this.contentPanel.setActiveTab(n);
			imeDivShow_1(1);
			webui.changePage('imeframe', _node.property);
		}
		if (_node.action == 'webpage') {
			if (!n) {
				/**
				 * 跨域访问
				 */
				// proxy : new Ext.data.ScriptTagProxy({
				// url : 'http://extjs.com/forum/topics-remote.php'
				// })
			}
			this.contentPanel.setActiveTab(n);
		}
		if (_node.action == 'repository') {
			if (!n) {
				var kmtreepanel = new KMTreePanel();
				var kmpanel = new KMPanel(kmtreepanel);
				n = this.contentPanel.add({
							id : _node.id,
							title : _node.text,
							closable : true,
							layout : 'fit',
							autoScroll : true,
							items : [kmpanel]
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
 *            _show 传递类型 int型,(0:是隐藏,1:显示大小,web页面大小改变改变时)
 */
function imeDivShow_1(_show) {
	var imeDiv_1 = document.getElementById("imediv");
	if (_show == 0) {
		imeDiv_1.style.pixelTop = 0;
		imeDiv_1.style.pixelLeft = 0;
		imeDiv_1.style.pixelWidth = 0;
		imeDiv_1.style.pixelHeight = 0;
	} else {
		imeDiv_1.style.pixelTop = toolbarHeight + 29;
		imeDiv_1.style.pixelLeft = 1;
		imeDiv_1.style.pixelWidth = document.body.offsetWidth - 1;
		imeDiv_1.style.pixelHeight = document.body.clientHeight - 60
				- toolbarHeight;
	}
}
