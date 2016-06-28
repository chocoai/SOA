/**   
* @Title: ApplicationServiceImpl.java 
* @Package bps.application 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年9月17日 上午9:16:54 
* @version V1.0   
*/
package bps.application;

import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.BizException;
import bps.common.ErrorCode;
import bps.common.Util;
import bps.member.Member;
import bps.service.ApplicationService;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年9月17日 上午9:16:54  
 */
public class ApplicationServiceImpl implements ApplicationService{

	public static Logger logger = Logger.getLogger(ApplicationServiceImpl.class.getName());
	
	/**
	 * 
	* (非 Javadoc) 
	* <p>Title: crateApplication</p> 
	* <p>Description:开发者创建应用 </p> 
	* @param memberId
	* @param appType
	* @param appName
	* @param extendInfo
	* @return
	* @throws BizException
	 */
	public Map<String, Object> createApplication(Long memberId, Long appType,
			String appName, Map<String, Object> extendInfo)throws BizException {
		// TODO Auto-generated method stub
		logger.info("crateApplication start");
        logger.info("memberId:"+memberId+";appType:"+appType+";appName:"+appName+";extendInfo:"+extendInfo);
		try {
			if(memberId == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数memberId不能为空");
			if(appType == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数appType不能为空");
			if(appName == null)
				throw new BizException(ErrorCode.OTHER_ERROR,"内部参数appName不能为空");

        	final Map<String, Object> param=new HashMap<String, Object>();
            param.put("memberId", memberId);
            param.put("appType", appType);
            param.put("appName", appName);
            param.put("extendInfo",extendInfo );
            final Member meb=new Member(memberId);
            if(meb.getUserId() == null){
            	throw new BizException(ErrorCode.USER_NOTEXSIT, "该用户不存在");
            }
            boolean isIdentity_check = false;
            if(meb.getIsIdentity_checked() != null){
            	isIdentity_check = meb.getIsIdentity_checked();
            }
            if(isIdentity_check)
            	throw new BizException(ErrorCode.USER_IS_NO_REALNAME, "用户未进行过实名认证");
            boolean isDeveloper = false;
            if(meb.getIsDeveloper() != null){
            	isDeveloper = meb.getIsDeveloper();
            }
            if(isDeveloper)
            	throw new BizException(ErrorCode.USER_IS_NO_DEVELOPER, "用户未成为开发者，请先成为开发者");
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
            Map<String, Object> app_entity= 
    		EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
    			@Override
    			public Map<String, Object> doTransaction(Session session, Transaction tx)
    					throws Exception {
    				Map<String, String> appMap = new HashMap<String, String>();
    				appMap.put("name", (String)param.get("appName"));
    				appMap.put("app_type", String.valueOf(param.get("appType")));
    				appMap.put("member_id", String.valueOf(meb.getId()));
    				appMap.put("member_label", meb.getName());
    				appMap.put("sysid", UUID.randomUUID().toString());
    				if(param.get("extendInfo")!= null){
    					Map<String,Object> extendInfo = (Map<String,Object>)param.get("extendInfo"); 
    					if(Util.isParam(extendInfo, "remark")){
    						appMap.put("remark", (String)extendInfo.get("remark"));
    					}
    					if(Util.isParam(extendInfo, "ipc_path")){
    						appMap.put("ipc_path", (String)extendInfo.get("ipc_path"));
    					}
    					if(Util.isParam(extendInfo, "license_path")){
    						appMap.put("license_path", (String)extendInfo.get("license_path"));
    					}
    					if(Util.isParam(extendInfo, "credit_path")){
    						appMap.put("credit_path", (String)extendInfo.get("credit_path"));
    					}
    					if(Util.isParam(extendInfo, "web_url")){
    						appMap.put("web_url", (String)extendInfo.get("web_url"));
    					}
    					
    				}
    				Map<String,Object> app_entity = DynamicEntityService.createEntity("AMS_Organization", appMap, null);
    				String codeNo = Util.getOrgNo((Long)app_entity.get("id"));
    				Query query = session.createQuery("update AMS_Organization set codeNo=:codeNo where id=:id");
					query.setParameter("codeNo", codeNo);
					query.setParameter("id", app_entity.get("id"));
					query.executeUpdate();
					app_entity.put("codeNo", codeNo);
    				return app_entity;
    			}
    		});
            logger.info("app_entity:"+app_entity);
    		return app_entity;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, e.getMessage());
		}
	}
}
