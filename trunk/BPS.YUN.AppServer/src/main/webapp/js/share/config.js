var config = {
				domain: {
					showList:[{label:"主域", value:"ime.com"}],	//显示域列表
					/*
					limit:"ime.com",		//如果设置该值则域名不显示并且默认登入为limit指定的域
					 */
					blank:""
				},
				background:{
					scale : true,	//是否拉伸背景图片
					image : "/images/background.jpg"
				},
				properties:[
								{name:"browser.home", value:"http://www.91quick.com"}
						   ],
				/* 图标位置 left, bottom*/
				desktopLinkAlign: "left",
				/* 远程调用方法：DWR, AMF */
				rpc: "AMF",
				/* 远程调用超时时间，单位秒，如果为0表示不设超时时间 */
				rpc_timeout : 30,
				/* 是否要用JSON格式传参 */
				use_json_invoke : false,
				/* 是否对JSON传参采用URI编码 */
				encodeJSON : false, 
				homesite:true,
				/* 系统菜单
				systemMenu: '<root>' +
							   '<menuitem label="系统" >' +
							   '<menuitem label="工作面板" appName="ime.WorkPanel" url="/pages/workpanel/WorkPanel.swf" autoRun="true" show="minimized" iconUrl="/images/share/ws/icons/workpanel.png" iconSize="16" x="120" y="50" width="400" height="500" tray="true" />' +
							   '<menuitem label="我的信息" action="alink" alink="alink://ime.AppWindow/EntityForm?{formState:&quot;state3&quot;,action:&quot;edit&quot;,entityUID:&quot;031AF3CC-A7C6-4B57-3FB7-769A2ABAB234.76A0ECE61239&quot;,formUID:&quot;031AF3CC-A7C6-4B57-3FB7-769A2ABAB234.7F6F46B1EFBD&quot;, entityId:${user.id}}" />' + 
							   '<menuitem label="修改密码" action="changePassword" iconUrl="/images/share/key.png" iconSize="16" />' +
							   '<menuitem type="separator"/>' +
							   '<menuitem label="重登入" action="relogin"/>' +
							   '<menuitem label="注销" action="restart" iconUrl="/images/share/logout.png" iconSize="16"/>' +
							   '<menuitem label="关闭" action="shutdown" iconUrl="/images/share/shutdown.png" iconSize="16"/>' +
								'</menuitem>' +
							'</root>',
				*/
				applicationTitle : {
										imageUrl:"",
										text : "",
										fontFamily : "宋体",
										fontSize : "60",
										fontWeight : "bold",
										color : "#ffffff"
								   },
				watermark :{
								imageUrl:"",
								text : "",
								fontFamily : "Verdana, Arial, Helvetica, sans-serif",
								fontSize : "12",
								fontWeight : "bold",
								color : "#ffffff"
						   },
				desktopLink:[
								{
									appName : "ime.Calendar",
									url		: "/pages/calendar/CalendarApp.swf",
									show	: "maximized",
									iconUrl	: "/images/share/calendar.png",
									title	: "日程管理",
									single	: true,
									tray	: false
								}
								/*,
								{
									appName : "ime.AppWindow",
									url		: "/pages/core/AppWindow.swf",
									show	: "maximized",
									iconUrl	: "/images/share/application.png",
									title	: "业务应用",
									single	: true,
									tray	: false,
									menu    : "应用程序/业务应用",
									alink	: "alink://ime.AppWindow/Application?{application:'DRP'}"
								}*/
							]
			 };