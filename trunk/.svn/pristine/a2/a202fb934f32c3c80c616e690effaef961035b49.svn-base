/*
 * @(#)RiskUser.java 2015-4-20 下午03:35:13
 * Copyright 2015 刘志坚, Inc. All rights reserved. 8637.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package bps.process;

import ime.core.services.DynamicEntityService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import bps.common.Constant;
import bps.member.MemberServiceImpl;

/**
 * <p>File：RiskUser.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015 2015-4-20 下午03:35:13</p>
 * <p>Company: allinpay.com</p>
 * @author 陈育秀
 * @version 1.0
 */
public class SafetyLevel {
    private static Logger logger = Logger.getLogger(SafetyLevel.class.getName());
    
    public static Map<String, Object> getSafetyLevelByMemberId(Long member_id, Session session)throws Exception{
        Query query = session.createQuery("from AMS_SafetyLevel where member_id=:member_id");
        query.setParameter("member_id", member_id);
        List list = query.list();
        if(list.size() == 0)
            return null;
        else
            return (Map<String, Object>)list.get(0);
    }

    /**
     * @param memberId
     * @param accountNo_encrypt
     * @param session
     * @return
     */
    public static Map<String, Object> getBankCardByAccountNo(Long memberId,String accountNo_encrypt, Session session)
    {
        Query query = session.createQuery("from AMS_MemberBank where member_id=:member_id and accountNo_encrypt=:accountNo_encrypt");
        query.setParameter("member_id", memberId);
        query.setParameter("accountNo_encrypt", accountNo_encrypt);
        List list = query.list();
        if(list.size() == 0)
            return null;
        else
            return (Map<String, Object>)list.get(0);
    }
    
    public static void addTradingMonitorLog(Long memberId , Long MonitorType ,String memo, Session session) throws Exception{
        Map<String,String> entity = new HashMap<String,String> ();
        Long addDate = new Date().getTime();
        entity.put("member_id", String.valueOf(memberId));
        entity.put("MonitorType",  String.valueOf(MonitorType));
        entity.put("memo",  memo);
        entity.put("addTime",  addDate.toString());
        Map<String, Object> temp = DynamicEntityService.createEntity("AMS_TradingMonitorLog", entity, null);
    }

    /**
     * @param memberId
     * @param riskRule
     * @param state
     * @param session
     * @throws Exception 
     */
    public static void addRiskAttentionLog(Long memberId, Long riskRule,  Session session) throws Exception {
        Map<String,String> entity = new HashMap<String,String> ();
        Long addDate = new Date().getTime();
        entity.put("member_id", String.valueOf(memberId));
        entity.put("riskRule",  String.valueOf(riskRule));
        entity.put("addDate",  addDate.toString());
        Map<String, Object> temp = DynamicEntityService.createEntity("AMS_RiskAttentionLog", entity, null);
    }

    /**
     * @param memberId
     * @param session
     * @throws Exception 
     */
    public static void addRiskAttention(Long memberId, Session session) throws Exception {
        Map<String,String> entity = new HashMap<String,String> ();
        Long addDate = new Date().getTime();
        entity.put("member_id", String.valueOf(memberId));
        entity.put("state",  Constant.RISK_UNTREATED.toString());
        entity.put("addDate",  addDate.toString());
        Map<String, Object> temp = DynamicEntityService.createEntity("AMS_RiskAttention", entity, null);
    }
    
    //以下方法从SafetyLevelService移入
    /**
     * 创建用户的时候，创建安全等级
     * @param memberId
     * @param safety_level
     * @param authentication_level
     * @param authentication_sp
     * @throws Exception
     */
    public static void addOrEditSafetyLevel(Long memberId , Long safety_level,Long authentication_level ,Long authentication_sp,Session session) throws Exception{
        logger.info("MemberService.addOrEditSafetyLevel start");
        //LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
        Map<String,Object> member = SafetyLevel.getSafetyLevelByMemberId(memberId, session);
        if(member == null){//说明没有安全等级，需要创建
            safety_level = safety_level == null?Constant.SAFETY_LEVEL_1:safety_level;
            authentication_level = authentication_level == null?Constant.AUTHENTICATION_LEVEL_V0:authentication_level;
            authentication_sp = authentication_sp == null?Constant.AUTHENTICATION_SP_NOT:authentication_sp;
            Map<String,String> entity = new HashMap<String,String> ();
            entity.put("member_id", String.valueOf(memberId));
            entity.put("safety_level",  String.valueOf(safety_level));
            entity.put("authentication_level",  String.valueOf(authentication_level));
            entity.put("authentication_sp",  String.valueOf(authentication_sp));
            Map<String, Object> temp = DynamicEntityService.createEntity("AMS_SafetyLevel", entity, null);
        }else{//更新安全等级
            String sql = "update AMS_SafetyLevel set safety_level=:safety_level ";
            if(authentication_level != null){
                sql += " ,authentication_level =:authentication_level"; 
            }
            if(authentication_sp != null){
                sql += " ,authentication_sp =:authentication_sp"; 
            }
            sql += " where member_id=:member_id";
            Query query = session.createQuery(sql);
            query.setParameter("member_id", memberId);
            query.setParameter("safety_level", safety_level);
            if(authentication_level != null){
                query.setParameter("authentication_level", authentication_level);
            }
            if(authentication_sp != null){
                query.setParameter("authentication_sp", authentication_sp);
            }
            query.executeUpdate();
        }
    }
    
    
    
    
    /**
     * 修改雇员公司，办公邮箱
     * @param memberId
     * @param company
     * @param email
     * @throws Exception
     */
    public static void addOrEditCompany(Long memberId , String company ,String email,Session session ) throws Exception{
        logger.info("MemberService.addOrEditCompany start");
        Query query = session.createQuery("update AMS_SafetyLevel set company=:company , email=:email where member_id=:member_id");
        query.setParameter("member_id", memberId);
        query.setParameter("company", company);
        query.setParameter("email", email);
        query.executeUpdate();
    }
    
    
    /**
     * 通过认证强度获,补充认证 计算 安全等级
     * @param authentication_level	认证强度
     * @param authentication_sp		补充认证
     * @return
     * @throws Exception
     */
    public static Long calculationSafetyLevel(Long authentication_level,Long authentication_sp ) throws Exception{
        Long safety_level = 1l;
        
        if(authentication_sp.equals(Constant.AUTHENTICATION_SP_NOT)){
            if(authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V0) || authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V1)){
                safety_level = 1l;
            }else if(authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V2) || authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V3)){
                safety_level = 2l;
            }
        }else if(authentication_sp.equals(Constant.AUTHENTICATION_SP_INDUSTRY)){
            if(authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V2) || authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V3)){
                safety_level = 4l;
            }else{
                safety_level = 3l;
            }
        }else if(authentication_sp.equals(Constant.AUTHENTICATION_SP_PHONE) || authentication_sp.equals(Constant.AUTHENTICATION_SP_COMPANY) ){
            if(authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V2) || authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V3)){
                safety_level = 4l;
            }else if(authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V0) || authentication_level.equals(Constant.AUTHENTICATION_LEVEL_V1)){
                safety_level = 1l;
            }
        }
        return safety_level;
    }
    
    
    
    /**
     * 创建安全等级变更日志
     * @param param
     * @return
     * @throws Exception
     */
    public static Long addSafetyLog(Map<String,Object> param) throws Exception{
        logger.info("MemberService.addSafetyLog start");
        Long member_id = (Long)param.get("member_id");
        Long type = (Long)param.get("type");
        Long old_level = (Long)param.get("old_level");
        Long new_level = (Long)param.get("new_level");
        String memo = (String)param.get("memo");
        String operation_type = (String)param.get("operation_type");
        Long deal_result = (Long)param.get("deal_result");
        String add_time = (String)param.get("add_time");
        Map<String, Object> temp = new HashMap<String, Object>();
        //LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
        Map<String,String> entity = new HashMap<String,String> ();
        entity.put("member_id", String.valueOf(member_id));
        entity.put("type", String.valueOf(type));
        entity.put("old_level", String.valueOf(old_level));
        entity.put("new_level", String.valueOf(new_level));
        entity.put("memo", memo);
        entity.put("operation_type", operation_type);
        entity.put("deal_result", String.valueOf(deal_result));
        entity.put("add_time",add_time);
        temp = DynamicEntityService.createEntity("AMS_SafetyLog", entity, null);
            return (Long) temp.get("id");
    }
    
    
    /**
     * 改变一次认证强度，做一次实名认证
     * @param param
     * @throws Exception
     */
    public static void doAuthentication(Map<String,Object> param,Session session) throws Exception{
        logger.info("MemberService.doAuthentication start");
        Long authentication_level = (Long)param.get("authentication_level");
        Long memberId = (Long)param.get("memberId");
        String memo = (String)param.get("memo");
        Long date = new Date().getTime();
        Map<String, Object> SafetyLevel_map = getSafetyLevelByMemberId(memberId,session);
        Long authentication_sp = (Long) SafetyLevel_map.get("authentication_sp");
        Long SafetyLevel = calculationSafetyLevel(authentication_level,authentication_sp);
        Map<String,Object> param_ = new HashMap<String,Object> ();
        param_.put("member_id", memberId);
        param_.put("type", authentication_level);
        param_.put("old_level", SafetyLevel_map.get("safety_level"));
        param_.put("new_level", SafetyLevel);
        param_.put("memo", memo);
        param_.put("operation_type", "1");
        param_.put("deal_result", Constant.DEAL_SUSSESS);
        param_.put("add_time", date.toString());
        addSafetyLog(param_);
        addOrEditSafetyLevel(memberId,SafetyLevel,authentication_level,authentication_sp,session);
    }
    
    
    
    /**
     * 雇员认证
     * @param param
     * @throws Exception
     */
    public static void doAuthentication_employee(Map<String,Object> param,Session session) throws Exception{
        logger.info("MemberService.doAuthentication start");
        
        Long memberId = (Long)param.get("memberId");
        Long memo = (Long)param.get("memo");
        String company = (String)param.get("company");
        String email = (String)param.get("email");
        Long authentication_sp = Constant.AUTHENTICATION_SP_COMPANY;
        
        
        logger.info("memberId"+memberId);
        logger.info("memo"+memo);
        logger.info("company"+company);
        logger.info("email"+email);
        Long date = new Date().getTime();
        Map<String, Object> SafetyLevel_map = getSafetyLevelByMemberId(memberId,session);
        Long authentication_sp_old = (Long)SafetyLevel_map.get("authentication_sp");
        
        if(authentication_sp_old == null){
            authentication_sp_old = Constant.AUTHENTICATION_SP_NOT;
        }
        
        Long authentication_level = (Long)SafetyLevel_map.get("authentication_level");
        if(authentication_sp_old.equals(Constant.AUTHENTICATION_SP_NOT)){//未认证，添加补充认证，
            Long SafetyLevel = calculationSafetyLevel(authentication_level,authentication_sp);
            Map<String,Object> param_ = new HashMap<String,Object> ();
            param_.put("member_id", memberId);
            param_.put("type", Constant.AUTHENTICATION_TYPE_COMPANY);
            param_.put("old_level", SafetyLevel_map.get("safety_level"));
            param_.put("new_level", SafetyLevel);
            param_.put("memo", "雇员认证");
            param_.put("operation_type", "1");
            param_.put("deal_result",  Constant.DEAL_SUSSESS);
            param_.put("add_time", date.toString());
            addSafetyLog(param_);
            addOrEditSafetyLevel(memberId,SafetyLevel,authentication_level,authentication_sp,session);
            addOrEditCompany(memberId,company,email,session);
        }else if(authentication_sp_old.equals(Constant.AUTHENTICATION_SP_COMPANY)){//已经做过雇员认证，修改
            Map<String,Object> param_ = new HashMap<String,Object> ();
            param_.put("member_id", memberId);
            param_.put("type", Constant.AUTHENTICATION_TYPE_COMPANY);
            param_.put("old_level", SafetyLevel_map.get("safety_level"));
            param_.put("new_level", SafetyLevel_map.get("safety_level"));
            param_.put("memo", "雇员认证");
            param_.put("operation_type", "1");
            param_.put("deal_result",  Constant.DEAL_SUSSESS);
            param_.put("add_time", date.toString());
            addSafetyLog(param_);
            addOrEditCompany(memberId,company,email,session);
        }else{//已经做过补充认证，
            throw new Exception("已经做过别的补充认证");
        }
    }
    
    
    
    
    /**
     * 电话认证
     * @param param
     * @return
     * @throws Exception
     */
    public static Long doAuthentication_phone(Map<String,Object> param,Session session) throws Exception{
        logger.info("MemberService.doAuthentication_phone start");
        
        Long memberId = (Long)param.get("memberId");
        Long deal_result = (Long)param.get("deal_result");
        String memo = (String)param.get("memo");
        Long id = 0l;
        Long date = new Date().getTime();
        Map<String, Object> SafetyLevel_map = getSafetyLevelByMemberId(memberId,session);
        Long authentication_sp_old = (Long) SafetyLevel_map.get("authentication_sp");
        
        if(authentication_sp_old == null){
            authentication_sp_old = Constant.AUTHENTICATION_SP_NOT;
        }
        
        Long authentication_level = (Long) SafetyLevel_map.get("authentication_level");
        if(authentication_sp_old.equals(Constant.AUTHENTICATION_SP_NOT)){//未认证，添加补充认证，
            Long SafetyLevel = calculationSafetyLevel(authentication_level,Constant.AUTHENTICATION_SP_PHONE);
            Map<String,Object> param_ = new HashMap<String,Object> ();
            param_.put("member_id", memberId);
            param_.put("type", Constant.AUTHENTICATION_TYPE_PHONE);
            param_.put("old_level", SafetyLevel_map.get("safety_level"));
            param_.put("new_level", SafetyLevel);
            param_.put("memo", memo);
            param_.put("operation_type", "1");
            param_.put("deal_result", deal_result);
            param_.put("add_time", date.toString());
            if(deal_result.equals(Constant.DEAL_SUSSESS)){
                param_.put("new_level", SafetyLevel);
                addOrEditSafetyLevel(memberId,SafetyLevel,authentication_level,Constant.AUTHENTICATION_SP_PHONE, session);
            }else{
                param_.put("new_level", SafetyLevel_map.get("safety_level"));
            }
            id = addSafetyLog(param_);
        }else{//已经做过补充认证，
            throw new Exception("已经做过补充认证");
        }
        return id;
    }
    
    
    
    
    /**
     * 修改原数据
     * @throws Exception
     */
    public static void batchChange(Session session) throws Exception{
        logger.info("MemberService.batchChange start");
        Query query = session.createQuery("from AMS_Member");
        List list = query.list();
        for(Object o : list){
            Map<String ,Object> member = (Map<String ,Object>)o;
            if(member.get("member_type").equals(Constant.MEMBER_TYPE_PERSON)){
                Map<String, Object> SafetyLevel_map = getSafetyLevelByMemberId((Long)member.get("id"),session);
                if(SafetyLevel_map == null){
                    if(member.get("isIdentity_checked") != null && (Boolean)member.get("isIdentity_checked")){
                        addOrEditSafetyLevel((Long)member.get("id"), Constant.SAFETY_LEVEL_2, Constant.AUTHENTICATION_LEVEL_V2, Constant.AUTHENTICATION_SP_NOT, session);
                    }else{
                        addOrEditSafetyLevel((Long)member.get("id"), Constant.SAFETY_LEVEL_1, Constant.AUTHENTICATION_LEVEL_V0, Constant.AUTHENTICATION_SP_NOT, session);
                    }
                }
            }else if(member.get("member_type").equals(Constant.MEMBER_TYPE_ENTERPRISE)){
                Map<String, Object> SafetyLevel_map = getSafetyLevelByMemberId((Long)member.get("id"),session);
                if(SafetyLevel_map == null){
                    addOrEditSafetyLevel((Long)member.get("id"), Constant.SAFETY_LEVEL_4, Constant.AUTHENTICATION_LEVEL_V2, Constant.AUTHENTICATION_SP_NOT, session);
                }
            }
        }
    }
    
    
    
    
    
    
    /**
     * 交易阻断，返回true:不需要阻断，false：需要阻断
     * @param memberId
     * @param session
     * @return
     * @throws Exception
     */
    public static Boolean trade_blockade(Long memberId,Session session) throws Exception{
        logger.info("MemberService.trade_blockade start");
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
        
        Query query = session.createQuery("from AMS_Order where member_id =:member_id and trade_type=:trade_type and create_time >=:create_time and order_state=:order_state");
        query.setParameter("member_id", memberId);
        query.setParameter("create_time", date.getTime());
        query.setParameter("order_state", Constant.ORDER_STATE_SUCCESS);
        query.setParameter("trade_type", Constant.TRADE_TYPE_DEPOSIT);
        List list = query.list();
        boolean flag = true;
        if(list !=null && list.size() > 0){
            Map<String,Object> sf_map = getSafetyLevelByMemberId(memberId,session);
            if(sf_map.get("safety_level") == null || sf_map.get("safety_level").equals(Constant.SAFETY_LEVEL_1) ||  sf_map.get("safety_level").equals(Constant.SAFETY_LEVEL_2)){
                
                for(Object o : list){
                    Map<String ,Object> order = (Map<String ,Object>)o;
                    if(order.get("accountNo_encrypt") != null){
                        Map<String,Object> bank = getBankCardByAccountNo(memberId,(String) order.get("accountNo_encrypt"),session);
                        Map<String,Object> member = new MemberServiceImpl().getUserInfo(memberId);
                        if(member.get("phone")!= null && bank.get("phone") != null && !member.get("phone").equals(bank.get("phone"))){
                            //银行预留号码和注册手机号码不一致
                            flag = false;
                        }
                    }
                    if(!flag){
                        break;
                    }
                }
            }
        }
        if(!flag){
          //银行预留号码和注册手机号码不一致,生成交易阻断日志。
            addTradingMonitorLog(memberId,Constant.MONITORTYPE_TRANSACTION,"交易阻断", session);
        }
        return flag;
    }
    
    
    
    
    
    
    /**
     *  触发一次风控，会员锁定
     * @param memberId
     * @param session
     * @throws Exception
     */
    public static void memberLock(Long memberId,Session session) throws Exception{
        logger.info("MemberService.memberLock start");
        Map<String,Object> sf_map = getSafetyLevelByMemberId(memberId,session);
        if(sf_map.get("safety_level") == null || sf_map.get("safety_level").equals(Constant.SAFETY_LEVEL_1) || sf_map.get("safety_level").equals(Constant.SAFETY_LEVEL_2)|| sf_map.get("safety_level").equals(Constant.SAFETY_LEVEL_3)){
            boolean flag = true;
            Long trigger_times = 0l;
            if(sf_map.get("trigger_times") != null){
                trigger_times = (Long)sf_map.get("trigger_times");
            }
            Date trigger_date = null;
            if(sf_map.get("trigger_date") != null){
                trigger_date = (Date)sf_map.get("trigger_date");
            }
            Date nowDate = new Date();
            Long times = null;
            if(trigger_date != null){
                times = (nowDate.getTime() - trigger_date.getTime())/(1000 * 60 * 60);
            }else{//第一次记录
                trigger_times = 1l;
                flag = false;
            }
            if(times != null && times < 24){
                trigger_times++;
            }else{//刷新记录
                trigger_times = 1l;
                flag = false;
            }
            if(trigger_times > 3){//超过3次
                Query query = session.createQuery("update AMS_Member set user_state=:user_state where id=:member_id");
                query.setParameter("user_state", Constant.USER_STATE_LOCKED);
                query.setParameter("member_id", memberId);
                query.executeUpdate();
                SafetyLevel.addTradingMonitorLog(memberId,Constant.MONITORTYPE_LOCKMEMBER,"会员锁定——进行锁定",session);
            }
            
            String sql ="update AMS_SafetyLevel set trigger_times=:trigger_times ";
            if(!flag || trigger_times.equals(1l)){//次数重置1，更新时间
                sql += " ,trigger_date=:trigger_date";
            }
            sql += " where member_id=:member_id";
            logger.info(sql);
            Query query = session.createQuery(sql);
            query.setParameter("member_id", memberId);
            query.setParameter("trigger_times", trigger_times);
            if(!flag || trigger_times.equals(1l)){
                query.setParameter("trigger_date", nowDate);
            }
            query.executeUpdate();
            //创建日志
            SafetyLevel.addTradingMonitorLog(memberId,Constant.MONITORTYPE_LOCKMEMBER_ONE,"会员锁定——触发风控",session);
        }
    }
    
    
    
    
    public static void phoneCallBackSave(Map<String, Object> eventParam,long logId,Session session) throws Exception{
        logger.info("MemberService.phoneCallBackSave start");
        Long date = ((Date)eventParam.get("callDate")).getTime();
        Map<String,String> entity = new HashMap<String,String> ();
        entity.put("callingNo", eventParam.get("callingNo").toString());
        entity.put("calledNo", eventParam.get("calledNo").toString());
        entity.put("callDate", date.toString());
        entity.put("serviceId", eventParam.get("serviceId").toString());
        entity.put("memo", (String)eventParam.get("memo"));
        logger.info("ssssssssssadas"+eventParam.get("isRecord"));
        entity.put("isRecord", eventParam.get("isRecord").toString());
        
        if(logId!=0){
        	entity.put("safetylog_id",logId+"" );
        }
        Long createTime = new Date().getTime();
        entity.put("state", eventParam.get("backresult").toString());
        entity.put("member_id", eventParam.get("member_id").toString());
        entity.put("createTime", createTime.toString());
        Map<String, Object> temp = DynamicEntityService.createEntity("AMS_PhoneBack", entity, null);
    }
    
    
    
    
    
    
    
    
    
}
