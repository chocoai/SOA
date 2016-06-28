/*
 * @(#)Organization.java 2015-4-10 下午04:22:09
 * Copyright 2015 刘志坚, Inc. All rights reserved. 8637.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package bps.eventhandler;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import bps.common.Constant;
import bps.common.JedisUtils;
import bps.common.Util;
import bps.order.OrderServiceImpl;
import ime.core.Environment;
import ime.core.event.DynamicEntityEvent;
import ime.core.event.IEntityEventHandler;
import ime.document.DocumentStorage;
import ime.security.cert.GenX509Cert;
import ime.security.cert.KeyPairWrapper;
import ime.security.cert.UserInfo;
import ime.security.util.RSAUtil;
import ime.soa.SystemManager;

/**
 * <p>File：Organization.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015 2015-4-10 下午04:22:09</p>
 * <p>Company: allinpay.com</p>
 * @author 陈育秀
 * @version 1.0
 */
public class Organization implements IEntityEventHandler
{
    private static Logger logger = Logger.getLogger(MemberEntity.class.getName());

    public void preCreate (DynamicEntityEvent event, Session session)throws Exception{
        Map<String, Object> organization_entity = event.getEntity();
//        KeyPair keyPair = RSAUtil.generateKeyPair();
        ime.security.cert.KeyPairWrapper keyPair11 = new ime.security.cert.KeyPairWrapper();
        keyPair11.genKey();

        String publicKey = RSAUtil.getPublicKeyString((RSAPublicKey)keyPair11.getPublic());
        String privateKey = RSAUtil.getPrivateKeyString((RSAPrivateKey)keyPair11.getPrivate());
        organization_entity.put("public_key", publicKey);
        organization_entity.put("private_key", privateKey);
        

        GenX509Cert gen = new GenX509Cert();
        UserInfo signerInfo = new UserInfo();
        UserInfo userInfo = new UserInfo();
        
        //装入公钥和私钥
        /*
        KeyPairWrapper keyPair11 = new KeyPairWrapper();
        PrivateKey privateKey11 = RSAUtil.getPrivateKey(privateKey);
        PublicKey publicKey11 = RSAUtil.getPublicKey(publicKey);
        keyPair11.load(publicKey11, privateKey11);
        **/
        
        Environment environment = Environment.instance();
        String rootAlias = environment.getSystemProperty("ca.root.alias");
        String rootPassword = environment.getSystemProperty("ca.root.password");
        String rootPath = environment.getSystemProperty("ca.root.path");
        String ip = environment.getSystemProperty("ca.root.ip");
        long expYear = Long.valueOf(environment.getSystemProperty("ca.root.expYear"));
        
        String certPath = environment.getSystemProperty("ca.cert.path");
//        String certAlias = (String) organization_entity.get("member_uuid");
        String certAlias = (String) organization_entity.get("codeNo");
        logger.info("certAlias:"+certAlias);
        certPath += "/" + certAlias + ".pfx";
        String certPassword = Util.getRandom(6);

        logger.info("cert_password:"+certPassword);
        organization_entity.put("cert_password", certPassword);
        logger.info("cert:"+certAlias + ".pfx:" + certPath);
        organization_entity.put("cert", certAlias + ".pfx:" + certPath);
        
        String fileRootPath = DocumentStorage.getAbsolutePath("", "attachments");
        certPath = fileRootPath + certPath;
        logger.info("certPath:"+certPath);
        
        //设置颁发机构信息
        String country = environment.getSystemProperty("ca.root.country");
        String locality = environment.getSystemProperty("ca.root.locality");
        String name = environment.getSystemProperty("ca.root.name");
        String organization = environment.getSystemProperty("ca.root.organizationUnit");
        String organizationUnit = environment.getSystemProperty("ca.root.organizationUnit");
        String state = environment.getSystemProperty("ca.root.state");
        signerInfo.country = country;
        signerInfo.locality = locality;
        signerInfo.name = name;
        signerInfo.organization = organization;
        signerInfo.organizationUnit = organizationUnit;
        signerInfo.state = state;
        
        //设置用户基本信息
        userInfo.country = (String) organization_entity.get("country");
        userInfo.locality = (String) organization_entity.get("locality");
        userInfo.name = (String) organization_entity.get("name");
        userInfo.organization = (String) organization_entity.get("organization");
        userInfo.organizationUnit = (String) organization_entity.get("organizationUnit");
        userInfo.state = (String) organization_entity.get("org_state");
        try {
            logger.info("生成证书开始");
            logger.info("rootAlias="+rootAlias);
            logger.info("rootPassword="+rootPassword);
            logger.info("rootPath="+rootPath);
            logger.info("certPath="+certPath);
            logger.info("keyPair11="+keyPair11);
            logger.info("certAlias="+certAlias);
            logger.info("signerInfo="+signerInfo);
            logger.info("userInfo="+userInfo);
            logger.info("certPassword="+certPassword);
            logger.info("expYear="+expYear);
            logger.info("ip="+ip);
            gen.createCert(rootAlias, rootPassword, rootPath, certPath, keyPair11, certAlias, signerInfo, userInfo, certPassword, expYear, ip);
            logger.info("生成证书结束");
        } catch(RuntimeException e){
            logger.info("生成证书错误RuntimeException");
            logger.error(e.getMessage(), e);
            throw e;
        } catch(Exception e) {
            logger.info("生成证书错误");
            logger.error(e.getMessage(), e);
            throw e;
        }
        SystemManager systemManager = SystemManager.getInstance();
        systemManager.addSystem((String) organization_entity.get("sysid"), publicKey, "", "", 0, "SHA1WithRSA");  
    }
    
    public void postCreate(DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    public void preUpdate (DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    public void postUpdate(DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    public void preRemove (DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    public void postRemove(DynamicEntityEvent event, Session session)throws Exception{
        
    }
    
    /**
     * 客户自定义事件处理
     * @param entityName 事件所在的动态实体类型名称
     * @param eventName  事件名称
     * @param eventParam 事件参数
     * @param session Hibernate Session对象
     * @return 事件处理结果
     */
    public Map<String, Object> customerEvent(String entityName, String eventName, Map<String, Object> eventParam, Session session)throws Exception{
        Map<String,Object> map = new HashMap<>();
    	if("leadRedis".equals(eventName)){
        	logger.info("加载机构数据");

            try {
                OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
                List list = orderServiceImpl.getOrgList();
                JSONArray array = new JSONArray(list);
                JedisUtils.setCache("orgList", array.toString());
            	map.put("result", "ok");
            	logger.info("====map:"+map);
            } catch(Exception e) {
            	map.put("result", "error");
            	logger.error(e.getMessage(), e);
            	throw e;
            }
        }
    	logger.info("====map:"+map);
        return map;
    }
}
