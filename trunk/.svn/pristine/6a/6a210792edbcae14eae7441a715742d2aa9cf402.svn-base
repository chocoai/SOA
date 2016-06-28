package bps.common;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import apf.framework.Framework;

public class Util{
	private static Logger logger = Logger.getLogger(Util.class.getName());
	private static Long CUSTOMERNO = 1l;
	
	// 金额验证正则表达式
    public static final String REGULAR_MONEY = "^((([1-9]{1}\\d{0,9})|([0]{1}))(\\.(\\d){1,2})?)?$";
    
	/**
	 * 产生0-9的随机数
	 * @return
	 * @throws Exception
	 */
	public static String getRandom() throws Exception{
		java.util.Random random = new java.util.Random();
		
		return String.valueOf(Math.abs(random.nextInt()) % 10);
		
	}
	
	public static String getRandom(Integer number){
	    StringBuilder sb = new StringBuilder();
	    Random random = new Random();
	    for(int i=0;i<number;i++){
	        sb.append(random.nextInt(10));
	    }
	    return sb.toString();
	}
	
	/**
	 * 判断是否数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		return pattern.matcher(str).matches();    
	}
	
	/**
	 * 判断是否有效字符�?
	 * @param str
	 * @return
	 */
	public static boolean isValidStr(String str){
		int index1 = str.indexOf("<");
		int index2 = str.indexOf(">");
		if(index1>=0 || index2>=0)
			return false;
		else
			return true;
	}
	/**
	 * 生成订单编号
	 * @param Long
	 * @return
	 */
	public static String getOrderNo(Long id) throws Exception{
		Date newdate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		String number  = "";
		String order_id = id.toString();
		for(int i=0; i<10-order_id.length(); i++){
			number += getRandom();
		}
		return df.format(newdate) + number + order_id;
	}
	
	/**
	 * 生成机构号
	 * @param Long
	 * @return
	 */
	public static String getOrgNo(Long id) throws Exception{
		Date newdate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		String number  = "";
		String order_id = id.toString();
		for(int i=0; i<10-order_id.length(); i++){
			number += getRandom();
		}
		return df.format(newdate) + number + order_id;
	}
	
	/**
	 * 判断字符串是否为空
	 * @param obj
	 * @return
	 */
	public static boolean stringIsNull(Object obj) {
		if (obj == null || "".equals(obj.toString().trim()) || "null".equals(obj.toString().trim()) 
				|| "undefined".equals(obj.toString().trim())) {
			return true;
		} else
			return false;
	}
	
	/**
	 * 获取参数值
	 * @param param_name
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static String getParamConfig(String param_name, Session session)throws Exception{
		logger.info("getParamConfig start");
		logger.info("param_name:" + param_name);
		Query query = session.createQuery("from AMS_ParamConfig where param_name=:param_name");
		query.setParameter("param_name", param_name);
		List list = query.list();
		if(list.size() == 0)
			return null;
		else if(list.size() > 1)
			throw new Exception("设置参数名重复");
		else
			return (String)((Map<String, Object>)list.get(0)).get("param_value");
	}

	/**
	 * 获取登录失败次数
	 * @return
	 * @throws Exception
	 */
	public static Long getLoginErrorAmount()throws Exception{
		Long login_error_amount = Long.valueOf(ime.core.Environment.instance().getSystemProperty(Constant.LOGIN_PASSWORD_FAIL_AMOUNT));
		return login_error_amount;
	}

	/**
	 * 获取支付失败次数
	 * @return
	 * @throws Exception
	 */
	public static Long getPayErrorAmount()throws Exception{
		Long pay_error_amount = Long.valueOf(ime.core.Environment.instance().getSystemProperty(Constant.PAY_PASSWORD_FAIL_AMOUNT));
		return pay_error_amount;
	}
	private static String appName;

    /**
     * 将图片控件中保存的值转换为图片的Url地址
     * 
     * @param imageValue
     * @return
     * @throws Exception
     */
    public static String getImageUrl(String imageValue) throws Exception {
        if (imageValue == null)
            return "";
        int idx = imageValue.indexOf(":");
        if (idx != -1) {
            StringBuilder sb = new StringBuilder();
            if (appName == null) {
                appName = Framework.instance().getApplicationName();
                if (appName == null) {
                    appName = "";
                }
            }
            if (appName.length() == 0) {
                sb.append("/docroot/attachments").append(imageValue.substring(idx + 1));
            } else {
                sb.append("/").append(appName).append("/docroot/attachments").append(imageValue.substring(idx + 1));
            }
            return sb.toString();
        }
        return imageValue;
    }
    
    /**
     * 生成客户号
     * @param Long
     * @return
     * 第1位：会员类型
     * 第2-7位：注册日期，YYMMDD
     * 第8-14位：顺序号，当日支持最多注册生一千万用户
     * 第15 位：校验码，自动随机生成
     */
    public static String getCustomerNo(Long id,Date date) throws Exception{
        String customerNo = "";
        
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        DecimalFormat dft = new DecimalFormat("0000000");
        String random  = getRandom();
        
        if(CUSTOMERNO.equals(0L)){
        	List list = null;
            //List list = MemberService.getMemberByRegister_time(date);
            if(list == null || list.isEmpty()){
                customerNo += id + df.format(date) + dft.format(CUSTOMERNO) + random;
                CUSTOMERNO += 1;
            }else{
                Map<String,Object> map = (Map<String, Object>) list.get(0);
                String memberNo = (String) map.get("memberNo");
                memberNo = memberNo.substring(7, 13);
                logger.info("memberNo" + memberNo);
                CUSTOMERNO = Long.valueOf(memberNo) + 1;
                customerNo += id + df.format(date) + dft.format(CUSTOMERNO) + random;
            }
        }else{
            customerNo += id + df.format(date) + dft.format(CUSTOMERNO) + random;
            CUSTOMERNO += 1;
        }
        return customerNo;
    }
    
    /**
     * 正则检验
     * @param value
     * @param regexp
     * @return
     * @author 施建波     2015年4月9日 下午1:57:58
     */
    public static Boolean matchRegexp(String value, String regexp)
    {
        Pattern pattern = Pattern.compile(regexp); 
        return pattern.matcher(value).matches();
    }
    
    public static String objectToStr(Object obj){
        String value = "";
        if(null != obj){
            value = obj.toString();
        }
        return value;
    }
    
    /**
     * 排序字符串，并且按升序排序
     * @param json
     * @return
     * @author 施建波     2015年4月13日 上午10:15:14
     */
    public static String getSortStr(JSONObject json){
        StringBuilder sb = new StringBuilder();
        if(null != json && json.length() > 0){
            List<String> list = new ArrayList<String>();
            //String[] strAry = new String[json.length()];
            Iterator<String> iterator = json.keys();
            //Integer num = 0;
            while(iterator.hasNext()){
                String key = iterator.next();
                if(!"accounting".equals(key)){ 
                    list.add(key);
                    //strAry[num] = key;
                    //num++;
                }
            }
            String[] strAry = list.toArray(new String[]{});
            Arrays.sort(strAry);
            for(String key:strAry){
                String value = json.optString(key);
                if(StringUtils.isNotBlank(value)){
                    sb.append("&").append(key).append("=").append(value);
                }
            }
            return sb.substring(1);
        }
        return null;
    }
    
    /**
     * 截取支付订单的支付指令的后十位，作为通联卡的流水号
     * @param commandNo 支付订单的支付指令
     * @return
     */
    public static String getFlowNoForWsCard(String oriFlowNo){
    	int howManyDigit = 10;
    	int length = oriFlowNo.length();
    	
    	return oriFlowNo.substring(length - howManyDigit, length);
    }
    
    public static boolean isParam(Map<String,Object> param , String name){
    	Object value = param.get(name);
    	if(value == null||"".equals(value.toString())){
    		return false;
    	}
    	return true;
    	
    }
}