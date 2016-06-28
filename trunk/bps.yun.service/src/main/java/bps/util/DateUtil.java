package bps.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * 获取当前时间
	 * @return data
	 */
	public static Date getNowTime(){
		return new Date();
	}
	/**
	 * 获取当前时间
	 * @param dateFormat   		日期格式
	 * @return String
	 */
	public static String getNowTime(String dateFormat){
		Date now = DateUtil.getNowTime();
		return DateUtil.getDateFormat(now, dateFormat);
	}
	/**
	 * 返回时间的指定格式
	 * @param date				date
	 * @param dateFormat		日期格式
	 * @return String
	 */
	public static String getDateFormat( String date, String dateFormat, String newDateFormat ){
		if( newDateFormat == null || "".equals(newDateFormat) )
			newDateFormat = "yyyy-MM-dd";
		SimpleDateFormat df=new SimpleDateFormat(newDateFormat);
		return df.format(getStringToDate(date, dateFormat));
	}
	/**
	 * 返回时间的指定格式
	 * @param date				date
	 * @param dateFormat		日期格式
	 * @return String
	 */
	public static String getDateFormat(Date date, String dateFormat ){
		if( dateFormat == null || "".equals(dateFormat) )
			dateFormat = "yyyy-MM-dd";
		SimpleDateFormat df=new SimpleDateFormat(dateFormat);
		return df.format(date);
	}

	/**
	 * String转Date
	 * @param oledate    	日期
	 * @param dateFormat	日期格式
	 * @return Date
	 */
	public static Date getStringToDate(String oledate, String dateFormat){
		if( dateFormat == null || "".equals(dateFormat))
			dateFormat = "yyyyMMdd";
		if(!dateFormat.contains("yy")){  //没有年份，默认当前年
			dateFormat = "yyyy"+dateFormat;
			String year = DateUtil.getNowTime("yyyy");
			oledate = year+oledate;
		}
		SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);//小写的mm表示的是分钟  
		try {
			return sdf.parse(oledate);
		} catch (ParseException e) {
			System.out.println(oledate);
			//e.printStackTrace();
		}
		return new Date();
	}
	 /** 
     * 获得指定日期的前一天 
     * @param specifiedDay 指定日期
	 * @param datyType    yyyyMMdd
     * @return String
     */  
    public static String getSpecifiedDayBefore(String specifiedDay, String datyType) {//可以用new Date().toLocalString()传递参数  
        Calendar c = Calendar.getInstance();
		Date date = new Date();
        try {  
            date = new SimpleDateFormat(datyType).parse(specifiedDay);  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day - 1);

		return new SimpleDateFormat(datyType).format(c.getTime());
    }

	/**
	 * 获得指定日期的N天前
	 * @param specifiedDay		指定日期
	 * @param n					天
	 * @param datyType			日期格式
	 * @return String
	 */
	public static String getSpecifiedDayByNDay(String specifiedDay,int n, String datyType){
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		try {
			date = new SimpleDateFormat(datyType).parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - n);

		return new SimpleDateFormat(datyType).format(c.getTime());
	}

	/**
	 *
	 * @param date		日期
	 * @param filedt	时间
	 * @return
	 */
    public static String getFullDateFormat(String date,String filedt){
    	if(date == null || date.equals("")){
    		return date;
    	}else if(date.length() == 4){
    		date = filedt.substring(0, 4) + date + "000000";
    	}else if(date.length() == 10){
    		date = filedt.substring(0, 4) + date;
    	}
    	return date;
    }
}
