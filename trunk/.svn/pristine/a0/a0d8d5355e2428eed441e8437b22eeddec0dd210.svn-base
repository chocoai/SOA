/*
 * @(#)RiskUser.java 2015-4-20 下午03:35:13
 * Copyright 2015 刘志坚, Inc. All rights reserved. 8637.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package bps.process;

import ime.core.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import bps.common.JedisUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import apf.util.EntityManagerUtil;
import apf.util.SessionContext;

/**
 * <p>File：RiskUser.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015 2015-4-20 下午03:35:13</p>
 * <p>Company: allinpay.com</p>
 * @author 陈育秀
 * @version 1.0
 */
public class RiskUser {
    private static Logger logger = Logger.getLogger(RiskUser.class.getName());
	private static String ip = null;
	private static int port = 0;
    
    public static boolean checkRiskUserInfo(String key, String value)throws Exception{
        logger.info("getRiskUserInfo start");
        logger.info("key=="+key+"----value="+value);
        
        if(ip == null) {
			Environment environment = null;
			try {
				environment = Environment.instance();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			ip 	= environment.getSystemProperty("cache.ip");
			port	= Integer.valueOf(environment.getSystemProperty("cache.port"));
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();
			return !jedis.sismember(key, value);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new Exception("Redis批量插入异常");
		} finally {
			JedisUtils.returnResource(pool, jedis);
		}
    }
    
    /**
     * 加载黑名单
     */
    public static void loadRiskUserInfo(){
        SessionContext ctx  = null;
        try{
            ctx             = EntityManagerUtil.currentContext();
            Session session = ctx.getHibernateSession();
            
            Query query = session.createQuery("from AMS_RiskUserList");
            List<Map<String, Object>> riskUserList = query.list();
            TreeSet<String> bankCardNo = new TreeSet<String>();
            TreeSet<String> mobile = new TreeSet<String>();
            TreeSet<String> certificateID = new TreeSet<String>();
            TreeSet<String> userId = new TreeSet<String>();
            TreeSet<String> IP = new TreeSet<String>();
            for(Map<String, Object> entity : riskUserList) {
            	if(entity.get("bankCardNo") != null)
	            	bankCardNo.add((String)entity.get("bankCardNo"));
            	if(entity.get("mobile") != null)
	            	mobile.add((String)entity.get("mobile"));
            	if(entity.get("certificateID") != null)
	            	certificateID.add((String)entity.get("certificateID"));
            	if(entity.get("userId") != null)
	            	userId.add((String)entity.get("userId"));
            	if(entity.get("IP") != null)
	            	IP.add((String)entity.get("IP"));
            }
            bachAdd("riskBankCardNo", bankCardNo);
            bachAdd("riskMobile", mobile);
            bachAdd("riskCertificateID", certificateID);
            bachAdd("riskUserId", userId);
            bachAdd("riskIP", IP);
            
        }catch(Exception e){
            logger.info(e.getMessage(), e); 
        }finally{
            logger.info("黑名单加载成功");
            EntityManagerUtil.closeSession(ctx);
        }
    }  
    
    /**
	 * 批量插入
	 * @param key
	 * @param set 
	 * @throws Exception
	 */
	public static boolean bachAdd(String key ,TreeSet<String> set) throws Exception{
    	if(ip == null) {
			Environment environment = null;
			try {
				environment = Environment.instance();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			ip 	= environment.getSystemProperty("cache.ip");
			port	= Integer.valueOf(environment.getSystemProperty("cache.port"));
		}
		JedisPool pool = null;
		Jedis jedis = null;
		Pipeline pipeline = null;
		Boolean flag = true;
		try {
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();
			pipeline = jedis.pipelined();
			Iterator< String> it = set.iterator();
			int i = 0;
			while(it.hasNext()){
				i++;
				pipeline.sadd(key, it.next());
				if(i%1000==0){
					pipeline.sync();
				}
			}
				pipeline.sync();
				flag = true;
		} catch (Exception e) {
			flag = false;
			pool.returnBrokenResource(jedis);
			throw new Exception("Redis批量插入异常");
		} finally {
			JedisUtils.returnResource(pool, jedis);
		}
		return flag;
	}
    
}
