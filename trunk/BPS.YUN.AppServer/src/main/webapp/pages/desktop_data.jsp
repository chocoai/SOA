<%@ page language="java"
    pageEncoding="utf-8" import="org.json.*"%>
<%
	/*
	提供由桌面使用的数据，采用JSON格式如
	{
		refresh : 300, //刷新时间(秒)
		sideBar : 
		[
			{
				name: "",
				icon: "",
				place:"top|right",
				data: [ {title:"", text:"", alink:""}, {title:"", text:"", alink:""}]
			}
		]
	}
	*/
%>