package bps.member;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ime.core.Environment;
import ime.security.Password;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import bps.common.Constant;

public class Security{
	
	/**
	 * 更新登录密码
	 * @param member_id
	 * @param new_password
	 * @param session
	 * @throws Exception
	 */
	public static void changeLoginPwd(Long member_id, String new_password, Session session)throws Exception{
		String login_password 	= Password.encode(new_password, "SHA1WithRSA");
		Query query = session.createQuery("update AMS_Member set login_password=:login_password," +
				"login_fail_amount=null, last_login_fail_time=null where id=:id");
		query.setParameter("login_password", login_password);
		query.setParameter("id", member_id);
		query.executeUpdate();
	}
		
	
	/**
	 * 修改支付密码
	 * @param member_id
	 * @param new_password
	 * @param session
	 * @throws Exception
	 */
	public static void changePayPwd(Long member_id, String new_password, Session session)throws Exception{
		String pay_password 	= Password.encode(new_password, "SHA1WithRSA");
		
		Query query = session.createQuery("update AMS_Member set pay_password=:pay_password, " +
				" pay_fail_amount=null, last_pay_fail_time=null where id=:id");
		query.setParameter("pay_password", pay_password);
		query.setParameter("id", member_id);
		query.executeUpdate();		
	}
	
	
	/**
	 * 验证登录密码
	 * @param member_entity
	 * @param session
	 * @return Map
	 * 	status          : 0:成功；1：帐号不存在；2：密码错误；3：登录帐号已锁定
	 *  remain_amount	：剩余次数
	 *  login_fail_amount：Long登陆失败次数, 最多5次
	 * @throws Exception
	 */
	public static Map<String, Object> checkLoginPassword(Member member, String login_password, Session session)throws Exception{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(member==null || member.getId()==null){
			returnMap.put("status", Constant.CHECK_PWD_UNDEFINED);
			return returnMap;
		}
		//判断用户状态
	    Logger logger = Logger.getLogger("member");
		Long user_state = (Long)member.getUser_state();
		if(!user_state.equals(Constant.USER_STATE_ACTIVATION)){
			returnMap.put("status", Constant.CHECK_PWD_LOCKED);
			return returnMap;
		}
		
		Environment environment = Environment.instance();
		String str_locked_time_value = environment.getSystemProperty(Constant.LOGIN_PASSWORD_LOCKED_TIME);
		if(str_locked_time_value == null)
			throw new Exception("未设置登录密码激活时间");
		Long locked_time_value = Long.valueOf(str_locked_time_value);
		String str_login_fail_value = environment.getSystemProperty(Constant.LOGIN_PASSWORD_FAIL_AMOUNT);
		if(str_login_fail_value == null)
			throw new Exception("未设置限制登录失败次数");
		Long system_login_fail_value = Long.valueOf(str_login_fail_value);
		
		Date now = new Date();
		//最近一次登录时间
		Date last_login_fail_time 	= (Date)member.getLast_login_fail_time();

		//登录失败次数
		Long login_fail_amount		= (Long)member.getlogin_fail_amount();
		if(login_fail_amount == null)	//失败次数
			login_fail_amount = 0L;
			
		if(last_login_fail_time != null && login_fail_amount!=null ){
			Calendar cal = Calendar.getInstance();
			cal.setTime(last_login_fail_time);
			cal.add(Calendar.MINUTE, (int)locked_time_value.longValue());
			
			Date value = cal.getTime();
			
			//判断锁定时间是否小于当前时间
			if(value.getTime() < now.getTime())
				login_fail_amount = 0L;
		}
		//失败次数超过允许次数
		if(login_fail_amount.longValue() - system_login_fail_value >= 0){
			returnMap.put("status", Constant.CHECK_PWD_LOCKED);
			return returnMap;
		}
		
		String rsa_login_password = (String)member.getLogin_password();
		if(ime.security.Password.encode(login_password, "SHA1WithRSA").equals(rsa_login_password)){
			//更新最后一次更新时间
			Query query = session.createQuery("update AMS_Member set" +
					" last_login_fail_time=null, login_fail_amount=:login_fail_amount where id=:id ");
			query.setParameter("login_fail_amount", 0L);
			query.setParameter("id", 				member.getId());
			query.executeUpdate();
			
			returnMap.put("status", Constant.CHECK_PWD_OK);
			return returnMap;
		}else{
			login_fail_amount ++;
			logger.info("****login_fail_amount****"+login_fail_amount);
			//更新登录失败时间
			Query query = session.createQuery("update AMS_Member set last_login_fail_time=:last_login_fail_time, " +
			" login_fail_amount=:login_fail_amount where id=:id ");
			query.setParameter("last_login_fail_time", 	new Date());
			query.setParameter("login_fail_amount", 	login_fail_amount);
			query.setParameter("id", member.getId());
			query.executeUpdate();
			
			if(login_fail_amount.longValue() - system_login_fail_value >= 0){	//超出次数
				returnMap.put("status", Constant.CHECK_PWD_LOCKED);
				return returnMap;
			}else{
				returnMap.put("status", 			Constant.CHECK_PWD_ERROR);
				returnMap.put("login_fail_amount", 	login_fail_amount);
				returnMap.put("remain_amount", 		system_login_fail_value - login_fail_amount);
				return returnMap;
			}
		}
	}
	
	/**
	 * 验证支付密码
	 * @param member_id
	 * @param pay_password
	 * @return Map
	 * 	status          : 0:成功；1：帐号不存在；2：密码错误；3：登录帐号已锁定
	 *  remain_amount	：剩余次数
	 *  pay_fail_amount：Long登陆失败次数, 最多5次
	 * @throws Exception
	 */
	public static Map<String, Object> checkPayPassword(Member member, String pay_password, Session session)throws Exception{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(member==null || member.getId()==null){
			returnMap.put("status", Constant.CHECK_PWD_UNDEFINED);
			return returnMap;
		}
		//判断用户状态
		Long user_state = (Long)member.getUser_state();
		if(!user_state.equals(Constant.USER_STATE_ACTIVATION)){
			returnMap.put("status", Constant.CHECK_PWD_LOCKED);
			return returnMap;
		}
		
		Environment environment = Environment.instance();
		String str_locked_time_value = environment.getSystemProperty(Constant.PAY_PASSWORD_LOCKED_TIME);
		if(str_locked_time_value == null)
			throw new Exception("未设置支付密码激活时间");
		Long locked_time_value = Long.valueOf(str_locked_time_value);
		
		String str_pay_fail_value = environment.getSystemProperty(Constant.PAY_PASSWORD_FAIL_AMOUNT);
		if(str_pay_fail_value == null)
			throw new Exception("未设置支付失败次数");
		Long system_pay_fail_value = Long.valueOf(str_pay_fail_value);
		
		Date now = new Date();
		//最近一次验证支付密码时间
		Date last_pay_fail_time 	= (Date)member.getLast_pay_fail_time();
		//支付验证失败次数
		Long pay_fail_amount		= (Long)member.getPay_fail_amount();
		if(pay_fail_amount == null)
			pay_fail_amount = 0L;
			
		if(last_pay_fail_time != null && pay_fail_amount!=null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(last_pay_fail_time);
			cal.add(Calendar.MINUTE, (int)locked_time_value.longValue());
			
			Date value = cal.getTime();
			
			//判断锁定时间是否小于当前时间
			if(value.getTime() < now.getTime())
				pay_fail_amount = 0L;
		}
		
		//超过允许次数
		if(pay_fail_amount.longValue() - system_pay_fail_value >= 0){
			returnMap.put("status", Constant.CHECK_PWD_LOCKED);
			return returnMap;
		}
			
		String md5_pay_password = member.getPay_password();
		if(ime.security.Password.encode(pay_password, "SHA1WithRSA").equals(md5_pay_password)){
			//更新最后一次更新时间
			Query query = session.createQuery("update AMS_Member set last_pay_fail_time=null, " +
					" pay_fail_amount=:pay_fail_amount where id=:id ");
			query.setParameter("pay_fail_amount", 	0L);
			query.setParameter("id",				member.getId());
			query.executeUpdate();
			
			returnMap.put("status", Constant.CHECK_PWD_OK);
			return returnMap;
		}else{
			pay_fail_amount ++;

			//更新支付失败时间
			Query query = session.createQuery("update AMS_Member set last_pay_fail_time=:last_pay_fail_time, " +
			" pay_fail_amount=:pay_fail_amount where id=:id ");
			query.setParameter("last_pay_fail_time", new Date());
			query.setParameter("pay_fail_amount", 	pay_fail_amount);
			query.setParameter("id", 				member.getId());
			query.executeUpdate();
			
			if(pay_fail_amount.longValue() - system_pay_fail_value >= 0){	//超出次数
				returnMap.put("status", Constant.CHECK_PWD_LOCKED);
				return returnMap;
			}else{
				returnMap.put("status", 			Constant.CHECK_PWD_ERROR);
				returnMap.put("pay_fail_amount", 	pay_fail_amount);
				returnMap.put("remain_amount", 		system_pay_fail_value - pay_fail_amount);
				return returnMap;
			}
		}
	}
	
	/**
	 * 验证手机支付密码
	 * @param member_id
	 * @param pay_password
	 * @param session 
	 * @return Map
	 * 	status          : 0:成功；1：帐号不存在；2：密码错误；3：登录帐号已锁定
	 *  remain_amount	：剩余次数
	 *  phone_pay_fail_amount：Long登陆失败次数, 最多5次
	 * @throws Exception
	 */
//    public static Map<String, Object> checkPhonePayPassword(Member member, String pay_password, Session session)throws Exception{
//    	Map<String, Object> returnMap = new HashMap<String, Object>();
//    	if(member==null || member.getId()==null){
//			returnMap.put("status", Constant.CHECK_PWD_UNDEFINED);
//			return returnMap;
//		}
//		//判断用户状态
//		Long user_state = (Long)member.getUser_state();
//		if(!user_state.equals(Constant.USER_STATE_ACTIVATION)){
//			returnMap.put("status", Constant.CHECK_PWD_LOCKED);
//			return returnMap;
//		}
//        
//        Environment environment = Environment.instance();
//        String str_locked_time_value = environment.getSystemProperty(Constant.PHONE_PAY_PASSWORD_LOCKED_TIME);
//        if(str_locked_time_value == null)
//            throw new Exception("未设置手机支付密码激活时间");
//        Long locked_time_value = Long.valueOf(str_locked_time_value);
//        
//        String str_pay_fail_value = environment.getSystemProperty(Constant.PHONE_PAY_PASSWORD_FAIL_AMOUNT);
//        if(str_pay_fail_value == null)
//            throw new Exception("未设置手机支付密码失败次数");
//        Long system_pay_fail_value = Long.valueOf(str_pay_fail_value);
//        
//        Date now = new Date();
//        //最近一次验证支付密码时间
//        Date last_pay_fail_time     = (Date)member.getLast_phone_pay_fail_time();
//        //支付验证失败次数
//        Long pay_fail_amount        = (Long)member.getPhone_pay_fail_amount();
//        if(pay_fail_amount == null)
//            pay_fail_amount = 0L;
//            
//        if(last_pay_fail_time != null && pay_fail_amount!=null){
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(last_pay_fail_time);
//            cal.add(Calendar.MINUTE, (int)locked_time_value.longValue());
//            
//            Date value = cal.getTime();
//            
//            //判断锁定时间是否小于当前时间
//            if(value.getTime() < now.getTime())
//                pay_fail_amount = 0L;
//        }
//        
//        if(pay_fail_amount.longValue() - system_pay_fail_value >= 0){
//        	returnMap.put("status", Constant.CHECK_PWD_LOCKED);
//			return returnMap;
//        }
//
//            
//        String md5_pay_password = (String)member.getPhone_pay_password();
//        if(ime.security.Password.encode(pay_password, "SHA1WithRSA").equals(md5_pay_password)){
//            //更新最后一次更新时间
//            Query query = session.createQuery("update AMS_Member set last_phone_pay_fail_time=null, " +
//                    " phone_pay_fail_amount=:pay_fail_amount where id=:id ");
//            query.setParameter("pay_fail_amount", 	0L);
//            query.setParameter("id", 				member.getId());
//            query.executeUpdate();
//            
//            returnMap.put("status", Constant.CHECK_PWD_OK);
//			return returnMap;
//        }else{
//            pay_fail_amount ++;
//
//            //更新支付失败时间
//            Query query = session.createQuery("update AMS_Member set last_phone_pay_fail_time=:last_pay_fail_time, " +
//            " phone_pay_fail_amount=:pay_fail_amount where id=:id ");
//            query.setParameter("last_pay_fail_time", new Date());
//            query.setParameter("pay_fail_amount", 	pay_fail_amount);
//            query.setParameter("id", 				member.getId());
//            query.executeUpdate();
//            
//            if(pay_fail_amount.longValue() - system_pay_fail_value >= 0){	//超出次数
//				returnMap.put("status", Constant.CHECK_PWD_LOCKED);
//				return returnMap;
//			}else{
//				returnMap.put("status", 			Constant.CHECK_PWD_ERROR);
//				returnMap.put("pay_fail_amount", 	pay_fail_amount);
//				returnMap.put("remain_amount", 		system_pay_fail_value - pay_fail_amount);
//				return returnMap;
//			}
//        }
//    }
//    
//    /**
//     * 修改手机支付密码
//     * @param member_id        用户编号
//     * @param new_password     新密码
//     * @param session
//     * @throws Exception
//     * @author 施建波     2015年5月19日 下午2:44:15
//     */
//    public static void changePhonePayPwd(Long member_id, String new_password, Session session)throws Exception{
//        String pay_password     = Password.encode(new_password, "SHA1WithRSA");
//        
//        Query query = session.createQuery("update AMS_Member set phone_pay_password=:pay_password, " +
//                " phone_pay_fail_amount=null, last_phone_pay_fail_time=null where id=:id");
//        query.setParameter("pay_password", pay_password);
//        query.setParameter("id", member_id);
//        query.executeUpdate();      
//    }
    //--------------APP接口关闭-------------------------
	
	
}