package bps.code;

import ime.core.Environment;
import ime.core.services.DynamicEntityService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import apf.util.EntityManagerUtil;
import apf.util.SessionContext;



import bps.common.Constant;
import bps.order.OrderServiceImpl;

import com.kinorsoft.ams.utils.Util;
import com.sun.org.apache.xpath.internal.operations.Bool;

public class VerificationCode {
	private static Logger logger = Logger.getLogger(VerificationCode.class.getName());
	
	/**
	 * 验证邮箱验证码
	 * @param email
	 * @param verification_code_type
	 * @param verification_code
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> checkEmailVerificationCode(Long applicationId,String email, Long verification_code_type, String verification_code, Session session)throws Exception{
		logger.info("VerificationCode checkEmailVerificationCode start");
		logger.info("email:" + email + " verification_code_type:" + verification_code_type + " verification_code:" + verification_code);
		 
		//查询验证码
		Date now = new Date();
		Query query = session.createQuery("from AMS_VerificationCode where verification_code=:verification_code " +
				"and verification_code_type=:verification_code_type and email=:email and invalid_time>=:now and isChecked=false and application_id=:application_id");
		query.setParameter("verification_code", verification_code);
		query.setParameter("email", email);
		query.setParameter("verification_code_type", verification_code_type);
		query.setParameter("now", now);
		query.setParameter("application_id", applicationId);
		
		List list = query.list();
		if(list.isEmpty())
			return null;
		else{
			return (Map<String, Object>)list.get(0);
		}
	}
	
	/**
	 * 设置验证码无�?
	 * @param verificationCode_id
	 * @throws Exception
	 */
	public static void setVerificationCodeChecked(Long verificationCode_id, Session session) throws Exception{
		logger.info("VerificationCode setVerificationCodeChecked start");
		Date now = new Date();
		Query query = session.createQuery("update AMS_VerificationCode set isChecked=true, check_time=:check_time where id=:id");
		query.setParameter("check_time", now);
		query.setParameter("id", verificationCode_id);
		query.executeUpdate();
		logger.info("VerificationCode setVerificationCodeChecked end");
	}
	
	/**
	 * 验证手机验证码
	 * @param phone
	 * @param verification_code_type
	 * @param verification_code
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> checkPhoneVerificationCode(Long applicationId,String phone, Long verification_code_type, String verification_code, Session session) throws Exception{
		logger.info("VerificationCode checkPhoneVerificationCode start");
		logger.info("phone:" + phone + " verification_code_type:" + verification_code_type + " verification_code:" + verification_code);
		
		//查询验证码
		Date now = new Date();
		Query query = session.createQuery("from AMS_VerificationCode where verification_code=:verification_code " +
				"and verification_code_type=:verification_code_type and phone=:phone and invalid_time>=:now and isChecked=false and application_id=:application_id");
		query.setParameter("verification_code", verification_code);
		query.setParameter("phone", phone);
		query.setParameter("verification_code_type", verification_code_type);
		query.setParameter("now", now);
		query.setParameter("application_id", applicationId);
		List list = query.list();
		logger.info("###size###"+list.size());
		if(list.isEmpty())
			return null;
		else{
			return (Map<String, Object>)list.get(0);
		}
	}
	
	public static Map<String, Object> getPhoneVerificationCode(Long applicationId,String phone, Long verification_code_type, String verification_code, Session session) throws Exception{
		logger.info("VerificationCode checkPhoneVerificationCode start");
		logger.info("phone:" + phone + " verification_code_type:" + verification_code_type + " verification_code:" + verification_code);
		
		//查询验证码
		Date now = new Date();
		Query query = session.createQuery("from AMS_VerificationCode where verification_code=:verification_code " +
				"and verification_code_type=:verification_code_type and phone=:phone and application_id=:application_id");
		query.setParameter("verification_code", verification_code);
		query.setParameter("phone", phone);
		query.setParameter("verification_code_type", verification_code_type);
		query.setParameter("application_id", applicationId);
		List list = query.list();
		logger.info("###size###"+list.size());
		if(list.isEmpty())
			return null;
		else{
			return (Map<String, Object>)list.get(0);
		}
	}
	
	/**
	 * 生成邮箱验证码
	 * @param email
	 * @param verification_code_type
	 * @param session
	 * @throws Exception
	 */
	public static Map<String, Object> generateEmailVerificationCode(Map<String, Object> param, Boolean isSend, Session session)throws Exception{
		logger.info("VerificationCode generateEmailVerificationCode start");
		logger.info("param:" + param);
		Map<String, Object> result = new HashMap<String, Object>();
		String email = (String) param.get("email");
		Long verification_code_type = (Long) param.get("verification_code_type");
        Long member_id = (Long) param.get("member_id");
        Long applicationId = (Long)param.get("applicationId");
		if(!Util.isValidStr(email))
			throw new Exception("无效的邮件");
		Map<String,Object> app_entity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
		if(app_entity==null){
			throw new Exception("应用不存在");
		}
		try {
			Calendar cal = Calendar.getInstance();
			Date now = cal.getTime();
			//查询验证码是否生成过
			Query query = session.createQuery("from AMS_VerificationCode where email=:email " +
					"and verification_code_type=:verification_code_type and invalid_time>=:now and isChecked=false and application_id=:application_id");
			
			query.setParameter("email", email);
			query.setParameter("verification_code_type", verification_code_type);
			query.setParameter("now", now);
			query.setParameter("application_id", applicationId);
			List list = query.list();
			
			String verification_code = "";
			if(list.isEmpty()){
				Environment environment = Environment.instance();
				String valid_time = environment.getSystemProperty("mail.valid.time.normal"); 
				
				cal.add(Calendar.DAY_OF_MONTH, (int)Long.valueOf(valid_time).longValue());			
				Date invalid_time = cal.getTime();
				for(int i=0; i<12; i++)
					verification_code += Util.getRandom();
				
				//生成验证码
				Map<String, String> verificationCodeMap = new HashMap<String, String>();
				verificationCodeMap.put("email", email);
				verificationCodeMap.put("member_id", member_id.toString());
				verificationCodeMap.put("verification_code_type",String.valueOf(verification_code_type));
				verificationCodeMap.put("create_time", 			String.valueOf(now.getTime()));
				verificationCodeMap.put("isChecked", 			"false");
				verificationCodeMap.put("invalid_time", 		String.valueOf(invalid_time.getTime()));
				verificationCodeMap.put("verification_code", 	verification_code);
				verificationCodeMap.put("application_id", 	String.valueOf(app_entity.get("id")));
				verificationCodeMap.put("application_label", 	String.valueOf(app_entity.get("name")));
				DynamicEntityService.createEntity("AMS_VerificationCode", verificationCodeMap, null);
			}else{
				Map<String, Object>verificationCode_entity = (Map<String, Object>)list.get(0);
				verification_code = (String)verificationCode_entity.get("verification_code");
			}
			param.put("email", email);
			param.put("verification_code_type", verification_code_type);
			param.put("verification_code", verification_code);
			param.put("member_id", member_id);
			
			Map<String,String> email_resolve = textResolve(param);
			String html = email_resolve.get("html");
			String subject = email_resolve.get("theme");
			result.put("html", html);
			result.put("subject", subject);
			logger.info("subject=================================:"+subject);
			logger.info("subject=================================:222"+email_resolve.get("theme"));
			logger.info("html:" + html);
			if(isSend != null && isSend) {
				//SendMail sm  = new SendMail(email, subject, html);
				//sm.start();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		logger.info("VerificationCode generateEmailVerificationCode end");
		return result;
	}
	
	/**
	 * 生成手机号码验证码
	 * @param phone
	 * @param verification_code_type
	 * @param session
	 * @throws Exception
	 */
	public static String generatePhoneVerificationCode(Map<String, Object> param, Boolean isSend, Session session)throws Exception{
		logger.info("VerificationCode generatePhoneVerificationCode start："+param);
		Long applicationId = (Long)param.get("applicationId");
		String phone = param.get("phone").toString();
		Long verification_code_type = (Long)param.get("verification_code_type");
		logger.info("phone:" + phone + " verification_code_type:" + verification_code_type);
		if(phone.length() != 11)
			throw new Exception("无效的手机号码");
		if(!Util.isNumeric(phone))
			throw new Exception("无效的手机号码");
		Map<String,Object> app_entity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
		if(app_entity==null){
			throw new Exception("应用不存在");
		}

		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		//查询验证码是否生成过
		Query query = session.createQuery("from AMS_VerificationCode where phone=:phone " +
				"and verification_code_type=:verification_code_type and invalid_time>=:now and isChecked=false and application_id=:application_id");
		query.setParameter("phone", phone);
		query.setParameter("verification_code_type", verification_code_type);
		query.setParameter("now", now);
		query.setParameter("application_id", applicationId);
		List list = query.list();
		
		String verification_code = "";
		if(list.isEmpty()){
			Environment environment = Environment.instance();
			String valid_time = environment.getSystemProperty("short.message.valid.time.normal"); 
			if(valid_time == null)
				throw new Exception("请设置短信有效期参数 valid_time");
			
			cal.add(Calendar.SECOND, (int)Long.valueOf(valid_time).longValue());
			Date invalid_time = cal.getTime();
			if(param.get("login_password")!=null){
				verification_code = (String)param.get("login_password");
			}else{
				for(int i=0; i<6; i++)
					verification_code += Util.getRandom();
			}
			//生成验证码数据
			Map<String, String> verificationCodeMap = new HashMap<String, String>();
			verificationCodeMap.put("phone", 				phone);
			verificationCodeMap.put("create_time", 			String.valueOf(now.getTime()));
			verificationCodeMap.put("isChecked", 			"false");
			verificationCodeMap.put("invalid_time", 		String.valueOf(invalid_time.getTime()));
			verificationCodeMap.put("verification_code_type", String.valueOf(verification_code_type));
			verificationCodeMap.put("verification_code", 	verification_code);
			verificationCodeMap.put("application_id", 	String.valueOf(app_entity.get("id")));
			verificationCodeMap.put("application_label", 	String.valueOf(app_entity.get("name")));
			DynamicEntityService.createEntity("AMS_VerificationCode", verificationCodeMap, null);
		}else{
			Map<String, Object>verificationCode_entity = (Map<String, Object>)list.get(0);
			verification_code = (String)verificationCode_entity.get("verification_code");
		}
		//生成发送模板
		

		String conent = "验证码是：" + verification_code;
		param.put("conent", conent);
		param.put("verification_code", verification_code);
		//发送短信
		try{
			conent = textResolve(param).get("html");
            logger.info("短信模版："+conent);
            if(isSend != null && isSend) {
            	//SendSM sm = new SendSM(phone, conent);
            	//sm.start();
            }
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		logger.info("VerificationCode generatePhoneVerificationCode end");
		return conent;
	}
	/**
     * 模版解析
     * @param param
     * @throws Exception
     */
    public static Map<String, String> textResolve(Map<String, Object> param) throws Exception {
        logger.info("textResolve start");
        logger.info(param);
        String text = "";
        String theme = "";
        Map<String, Object> templet = null;
        SessionContext ctx = null;
        try {
            ctx = EntityManagerUtil.currentContext();
            Session session     = ctx.getHibernateSession();    //获取会话
            Query query = null;
            //获取模版
            Long applicationId = (Long)param.get("applicationId");
            if(param.get("verification_code_type") != null && param.get("verification_code_type") != "") {
                if(param.get("phone") != null && param.get("phone") != "") {
                    query = session.createQuery("from AMS_SMTemplet where verification_code_type=:verification_code_type and application_id=:application_id");
                    query.setParameter("verification_code_type", (Long)param.get("verification_code_type"));
                    query.setParameter("application_id", applicationId);
                } else if(param.get("email") != null && param.get("email") != "") {
                    query = session.createQuery("from AMS_EmailTemplet where verification_code_type=:verification_code_type and application_id=:application_id");
                    query.setParameter("verification_code_type", (Long)param.get("verification_code_type"));
                    query.setParameter("application_id", applicationId);
                }
            } else if(param.get("codeNo") != null && param.get("codeNo") != "") {
                if(param.get("phone") != null && param.get("phone") != "") {
                    query = session.createQuery("from AMS_SMTemplet where codeNo=:codeNo");
                    query.setParameter("codeNo", (String)param.get("codeNo"));
                } else if(param.get("email") != null && param.get("email") != "") {
                    query = session.createQuery("from AMS_EmailTemplet where codeNo=:codeNo");
                    query.setParameter("codeNo", (String)param.get("codeNo"));
                }
            }
            List list = new ArrayList();
            if(query != null) 
                list = query.list();
            if(!list.isEmpty() && list.size() == 1) {
                templet = (Map<String, Object>) list.get(0);
                text = templet.get("contents").toString();
                theme = (String)templet.get("theme");
                logger.info("theme=================================:"+theme);
                text = text.replaceAll("%7B", "{");
                text = text.replaceAll("%7D", "}");
                logger.info("替换前" + text);
                //解析模版
                Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
                Map<String, Object> entity = null;
                int prev = 0;
                for(int i = 0; i < 1000; i ++) {
                    prev = text.indexOf("{", prev + 1);
                    if(prev < 0) {
                        break;
                    }
                    if(prev > 0 && "!".equals(text.charAt(prev - 1)+"")) {
                        continue;
                    }
                    int next = text.indexOf("}", prev);
                    String keyword = text.substring(prev + 1, next);
                    String[] strs = keyword.split("\\.");
                    String value = "";
                    if(strs.length > 1) {
                        if(map.get(strs[0]) == null) {
                            entity = getEntity(strs[0], param, templet, session);
                            if(entity == null) {
                                entity = new HashMap<String,Object>();
                            }
                            map.put(strs[0], entity);
                        } else {
                            entity = map.get(strs[0]);
                        }
                        if(entity.get(strs[1]) == null) {
                            value = keyword;
                            if(strs[1].equals("name")) {
                                value = entity.get("phone").toString();
                            }
                        } else {
                            value = entity.get(strs[1]).toString();
                        }
                    }
                    text = text.replace("{" + keyword + "}", value);
                    prev = 0;
                }
                text.replaceAll("!\\{", "\\{");

            } else {
                text = param.get("conent").toString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            EntityManagerUtil.closeSession(ctx);
            logger.info("textResolve end");
        }
        Map<String, String> map = new  HashMap<String, String>();
        map.put("theme", theme);
        logger.info("theme=================================:2"+theme);
        map.put("html", text);
        
        return map;
    }
    
	public static Map<String, Object> getEntity(String shotTableName, Map<String, Object> param, Map<String, Object> templet, Session session) throws Exception {
		Map<String, Object> map = null;
		Query query = null;
		if(shotTableName.equals("MB")) {
		    if(param.get("member_id") == null) {
		        throw new Exception("请传入参数member_id");
		    }
			map = DynamicEntityService.getEntity((Long)param.get("member_id"), "AMS_Member");
			if(map.get("name") == null) {
				map.put("name", "用户");
			}
		} else if(shotTableName.equals("SYSTEM")) {
			map = new HashMap<String, Object>();
			map.put("code", param.get("verification_code"));
			if(templet.get("email_url") != null) {
				map.put("email_url", templet.get("email_url").toString());
			}
		} else if(shotTableName.equals("NEW")) {
			map = param;
		} else if(shotTableName.equals("ORDER")) {
            if(param.get("orderNo") == null) {
                throw new Exception("请传入参数orderNo");
            }
		    String orderNo = param.get("orderNo").toString();
            logger.info("orderNo1="+orderNo);
		    int index = orderNo.indexOf(Constant.COMMAND_SPLIT_SIGN); //判断是否多指令集  
		    if(index > 0){
		        orderNo = orderNo.substring(0, index);
		    }
		    logger.info("orderNo2="+orderNo);
			Map<String, Object> entity =new OrderServiceImpl().getOrder(orderNo);
			map = new HashMap<String, Object>();
			for(String key : entity.keySet()) {
				if(key.equals("order_money")) {
					Long orderMoney = (Long)entity.get("order_money");
					Double orderMoney_d = orderMoney / 100.0;
					map.put(key, orderMoney_d);
				} else
					map.put(key, entity.get(key));
			}
		} else if(shotTableName.equals("USER")) {
	//		Map<String, Object> remain_user = Trade.getRemain((Long)param.get("member_id"), session);
	//		double money = (Long)remain_user.get("amount") - (Long)remain_user.get("freeze_amount");
	//		money = money / 100.0;
	//		map = new HashMap<String, Object>();
	//		map.put("balance", money);
		}
			
		return map;
	}
	
}