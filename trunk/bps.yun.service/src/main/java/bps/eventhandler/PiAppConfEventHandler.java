package bps.eventhandler;


import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import org.hibernate.Session;
import org.json.JSONObject;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import bps.common.Constant;
import bps.common.JedisUtils;
import ime.core.Environment;
import ime.core.event.DynamicEntityEvent;
import ime.core.event.IEntityEventHandler;

/**
 * 支付通道中应用信息的数据变化事件
 * @author Administrator
 *
 */
public class PiAppConfEventHandler implements IEntityEventHandler {
	private Logger logger = Logger.getLogger(PiAppConfEventHandler.class);
	private static String ip = null;
	private static int port = 0;

	public void preCreate(DynamicEntityEvent event, Session session) throws Exception {
		logger.info("====================PiAppConfEventHandler.preCreate start=========================");
		
		try{
			//获取数据
			Map<String, Object> newPayInterAppConf = event.getEntity();
			logger.info("新添加的支付通道应用信息：" + newPayInterAppConf);
			
			//组装数据并加入缓存
			addOrUpdateCache(newPayInterAppConf);
			logger.info("====================PiAppConfEventHandler.preCreate end=========================");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public void postCreate(DynamicEntityEvent event,Session session) throws Exception {
		try{
			logger.info("==========触发创建后事件====");
			
			Map<String,Object> entity = event.getEntity();
			String orgNo = String.valueOf(entity.get("org_no"));
			String pfx = String.valueOf(entity.get("pfx"));
			String pay_interface_label = String.valueOf(entity.get("pay_interface_label"));
			String tltcer = String.valueOf(entity.get("tltcer"));
			String path = "";			
			String pfxFileName = "";
			String pfxfilePath = "";
			String tltcerFileName = "";
			String tltcerfilePath = "";
			String user = Environment.instance().getSystemProperty("remote.user");
			String pass = Environment.instance().getSystemProperty("remote.pass");
			String host1 = "";
			String host2 = "";
			if(Constant.PAY_INTERFACE_TLT_BACH_DF.equals(pay_interface_label) || Constant.PAY_INTERFACE_TLT_DF.equals(pay_interface_label)){
				host1 = Environment.instance().getSystemProperty("remote.tlt.host152");
				host2 = Environment.instance().getSystemProperty("remote.tlt.host153");
				path = Environment.instance().getSystemProperty("remote.tlt.allinpayPath");
			}
			if(Constant.PAY_INTERFACE_GETWAY_JJ.equals(pay_interface_label)){
				host1 = Environment.instance().getSystemProperty("remote.gateway.host150");
				host2 = Environment.instance().getSystemProperty("remote.gateway.host151");
				path = Environment.instance().getSystemProperty("remote.gateway.allinpayPath");
			}
			if(pfx != null && !pfx.isEmpty()){
				String[]arr = pfx.split(":");
				pfxFileName = arr[0];
				pfxfilePath = arr[1];
			}
			if(tltcer != null && !tltcer.isEmpty()){
				String[]arr = tltcer.split(":");
				tltcerFileName = arr[0];
				tltcerfilePath = arr[1];
			}
//			if(host1 != null && !host1.isEmpty())
//				linuxCall(path,pfxFileName,pfxfilePath,tltcerFileName,tltcerfilePath,orgNo,user,pass,host1);
//			if(host2 != null && !host2.isEmpty())
//				linuxCall(path,pfxFileName,pfxfilePath,tltcerFileName,tltcerfilePath,orgNo,user,pass,host2);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public void preUpdate(DynamicEntityEvent event, Session session) throws Exception {
		logger.info("====================PiAppConfEventHandler.preUpdate start=========================");
		
		try{
			//获取数据
			Map<String, Object> newPayInterAppConf = event.getEntity();
			logger.info("更新的支付通道应用信息：" + newPayInterAppConf);
			
			//组装数据并更新缓存
			addOrUpdateCache(newPayInterAppConf);
			
			logger.info("====================PiAppConfEventHandler.preUpdate end=========================");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public void postUpdate(DynamicEntityEvent event,
			Session session) throws Exception {
		try{
			logger.info("==========触发创建后事件====");
			
			Map<String,Object> entity = event.getEntity();
			String orgNo = (String)entity.get("org_no");
			String pfx = (String)entity.get("pfx");
			String pay_interface_label = (String)entity.get("pay_interface_label");
			String tltcer = (String)entity.get("tltcer");
			String path = "";			
			String pfxFileName = "";
			String pfxfilePath = "";
			String tltcerFileName = "";
			String tltcerfilePath = "";
			String user = Environment.instance().getSystemProperty("remote.user");
			String pass = Environment.instance().getSystemProperty("remote.pass");
			String host1 = "";
			String host2 = "";
			if(Constant.PAY_INTERFACE_TLT_BACH_DF.equals(pay_interface_label) || Constant.PAY_INTERFACE_TLT_DF.equals(pay_interface_label)){
				host1 = Environment.instance().getSystemProperty("remote.tlt.host152");
				host2 = Environment.instance().getSystemProperty("remote.tlt.host153");
				path = Environment.instance().getSystemProperty("remote.tlt.allinpayPath");
			}
			if(Constant.PAY_INTERFACE_GETWAY_JJ.equals(pay_interface_label)){
				host1 = Environment.instance().getSystemProperty("remote.gateway.host150");
				host2 = Environment.instance().getSystemProperty("remote.gateway.host151");
				path = Environment.instance().getSystemProperty("remote.gateway.allinpayPath");
			}
			if(pfx != null && !pfx.isEmpty()){
				String[]arr = pfx.split(":");
				pfxFileName = arr[0];
				pfxfilePath = arr[1];
			}
			if(tltcer != null && !tltcer.isEmpty()){
				String[]arr = tltcer.split(":");
				tltcerFileName = arr[0];
				tltcerfilePath = arr[1];
			}
//			if(host1 != null && !host1.isEmpty())
//				linuxCall(path,pfxFileName,pfxfilePath,tltcerFileName,tltcerfilePath,orgNo,user,pass,host1);
//			if(host2 != null && !host2.isEmpty())
//				linuxCall(path,pfxFileName,pfxfilePath,tltcerFileName,tltcerfilePath,orgNo,user,pass,host2);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public void preRemove(DynamicEntityEvent event, Session session) throws Exception {
		logger.info("====================PiAppConfEventHandler.preRemove start=========================");
		
		try{
			//获取数据
			Map<String, Object> newPayInterAppConf = event.getEntity();
			logger.info("删除的支付通道应用信息：" + newPayInterAppConf);
			
			//删除缓存
			deleteCache(newPayInterAppConf);
			
			logger.info("====================PiAppConfEventHandler.preRemove end=========================");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}

	}

	public void postRemove(DynamicEntityEvent paramDynamicEntityEvent,
			Session paramSession) throws Exception {
		// TODO Auto-generated method stub

	}

	public Map<String, Object> customerEvent(String paramString1,
			String paramString2, Map<String, Object> paramMap,
			Session paramSession) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 更新或者添加
	 * @param entity
	 * @throws Exception
	 */
	private void addOrUpdateCache(Map<String, Object> entity) throws Exception{
		logger.info("======================PiAppConfEventHandler.addOrUpdateCache start=========================");
		
		try{
			//组装数据
			JSONObject value = new JSONObject();
			for(Map.Entry<String, Object> entry : entity.entrySet()){
				if("pay_interface_label".equals(entry.getKey()) || "org_no".equals(entry.getKey())){
					continue;
				}
				
				value.put(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
			}
			
			String key = (String)entity.get("pay_interface_label") + "_" + entity.get("org_no");
			
			//获取缓存数据
			JedisPool pool = null;
			Jedis jedis = null;
			try{
				setIpPort();
				pool = JedisUtils.getPool(ip, port);
				jedis = pool.getResource();
				String oldPayInterAppConf = jedis.get(Constant.REDIS_KEY_PI_APP_CONF);
				JSONObject payInterAppConfObj = new JSONObject(oldPayInterAppConf);
				payInterAppConfObj.put(key, value);
				
				logger.info("新的支付通道应用信息：" + payInterAppConfObj);
				//向缓存中添加数据
				jedis.set(Constant.REDIS_KEY_PI_APP_CONF, payInterAppConfObj.toString());
			}catch(Exception e){
				// 释放redis对象
				pool.returnBrokenResource(jedis);
				logger.error(e.getMessage(), e);
				throw e;
			}finally{
				// 返还到连接池
				JedisUtils.returnResource(pool, jedis);
			}
			
			logger.info("======================PiAppConfEventHandler.addOrUpdateCache end=========================");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 删除cache
	 * @param entity
	 * @throws Exception
	 */
	private void deleteCache(Map<String, Object> entity) throws Exception{
		logger.info("========================PiAppConfEventHandler.deleteCache start=======================");
		
		try{
			//需要删除的redis的key
			String key = (String)entity.get("pay_interface_label") + "_" + entity.get("org_no");
			
			//删除相应的redis
			JedisPool pool = null;
			Jedis jedis = null;
			try{
				setIpPort();
				pool = JedisUtils.getPool(ip, port);
				jedis = pool.getResource();
				
				String oldPayInterAppConf = jedis.get(Constant.REDIS_KEY_PI_APP_CONF);
				JSONObject payInterAppConfObj = new JSONObject(oldPayInterAppConf);
				payInterAppConfObj.remove(key);
				
				logger.info("新的支付通道应用信息：" + payInterAppConfObj);
				
				jedis.set(Constant.REDIS_KEY_PI_APP_CONF, payInterAppConfObj.toString());
			}catch(Exception e){
				// 释放redis对象
				pool.returnBrokenResource(jedis);
				logger.error(e.getMessage(), e);
				throw e;
			}finally{
				// 返还到连接池
				JedisUtils.returnResource(pool, jedis);
			}
			
			logger.info("========================PiAppConfEventHandler.deleteCache end=======================");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 设置redis的ip和port
	 * @throws Exception 
	 */
	private void setIpPort() throws Exception{
		if(ip == null || port == 0){
			ip 	= Environment.instance().getSystemProperty("cache.ip");
			port	= Integer.valueOf(Environment.instance().getSystemProperty("cache.port"));
		}
	}
	
//	private void linuxCall(String path,String pfxFileName,String pfxfilePath,String tltcerFileName,String tltcerfilePath,
//			               String catalogue,String user,String pass,String host) throws Exception{
//		 	Connection con = null;
//	        try {
//	        	con = new Connection(host);
//	        	con.connect();
//	            boolean isAuthed = con.authenticateWithPassword(user, pass);
//	            if(!isAuthed)
//	            	throw new Exception("远程连接失败");
//	            SFTPv3Client sftpClient1 = new SFTPv3Client(con);
//	            Vector vector = sftpClient1.ls(path);
//	            Boolean flag = true;
//	            for(int i = 0;i<vector.size();i++){
//	            	SFTPv3DirectoryEntry entity = (SFTPv3DirectoryEntry)vector.get(i);
//	            	String filename = entity.filename;
//	            	if(filename.equals(catalogue)){
//	    	           flag = false;
//	    	           break;
//	            	}
//	            }
//	            path += catalogue;
//	            if(flag)
//	            	sftpClient1.mkdir(path, 7);    //远程新建目录
//	            SCPClient scpClient = con.createSCPClient();
//	            String localPath = Environment.instance().getSystemProperty("remote.attachments");
//	            if(!pfxFileName.isEmpty() && !pfxfilePath.isEmpty()){
//	            	String localurl = localPath  + pfxfilePath;
//	            	scpClient.put(localurl,pfxFileName, path,"0777"); //从本地复制文件到远程目录
//	            }
//	            if(!tltcerFileName.isEmpty() && !tltcerfilePath.isEmpty()){
//	            	String localurl = localPath +  tltcerfilePath;
//	            	scpClient.put(localurl,tltcerFileName, path,"0777"); //从本地复制文件到远程目录
//	            }
//	            logger.info("===传输结束===");
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	            logger.error(e.getMessage(),e);
//	            throw e;
//	        }finally{
//	        	if(con != null)
//	        		con.close();
//	        }
//	    }
}
