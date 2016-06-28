/**   
* @Title: Appliction.java 
* @Package bps.application 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年12月22日 下午5:20:45 
* @version V1.0   
*/
package bps.application;

import ime.core.services.DynamicEntityService;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorCode;
import org.hibernate.Query;
import org.hibernate.Session;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年12月22日 下午5:20:45  
 */
public class Appliction {
	private static Logger logger = Logger.getLogger(Appliction.class.getName());
	
	public static List<Map<String,Object>> getAppConif(Long applictionId, Long configType) throws Exception{
		final Long _applictionId = applictionId;
		final Long _configType = configType;
		
		return EntityManagerUtil.execute(new QueryWork<List<Map<String,Object>>>() {
			@Override
			public List<Map<String,Object>> doQuery(Session session) throws Exception {
				Query query = session.createQuery("from AMS_AppConfig where application_id=:application_id and config_type=:config_type");
	            query.setParameter("application_id", _applictionId);
	            query.setParameter("config_type", _configType);
	            List<Map<String,Object>> list =  query.list();
	            return list;
			}
		});
	}
	
	/**
	 * 获取应用用户的实体
	 * @param applicationId
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getApplicationMemberEntity(Long applicationId) throws Exception{
		try{
			Map<String, Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
			if(applicationEntity == null || applicationEntity.isEmpty()){
				throw new Exception("应用不存在。");
			}
			Long applicationMemberId = (Long)applicationEntity.get("member_id");
			return DynamicEntityService.getEntity(applicationMemberId, "AMS_Member");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
}
