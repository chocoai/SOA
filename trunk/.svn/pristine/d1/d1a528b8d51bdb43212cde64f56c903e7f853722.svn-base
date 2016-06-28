<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="bps.process.AppDailyStatistics" %>
<%@ page import="org.apache.log4j.Logger" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<%
		try {
			Logger logger = Logger.getLogger("Do Handler" );
			Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2014-01-01");
			Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse("2016-05-18");
			Calendar dd = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			dd.setTime(d1);
			while(dd.getTime().before(d2)){

				String temp = sdf.format(dd.getTime());
				logger.info("现在的日期为："+temp);
				AppDailyStatistics appDailyStatistics = new AppDailyStatistics();
				appDailyStatistics.go(temp);
				dd.add(Calendar.DATE,1);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	%>

</body>
</html>