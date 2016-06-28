package bps.eventhandler;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.kinorsoft.ams.trade.AdjustAccount;

import ime.core.event.DynamicEntityEvent;
import ime.core.event.IEntityEventHandler;
import ime.core.services.DynamicEntityService;

public class AdjustAccountEventHandler implements IEntityEventHandler {
	private static Logger logger = Logger.getLogger(AdjustAccountEventHandler.class);
	

	public Map<String, Object> customerEvent(String entityName, String eventName, Map<String, Object> params, Session session) throws Exception {

		return null;
	}

	public void postCreate(DynamicEntityEvent event, Session session)
			throws Exception {
		
	}

	public void postRemove(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void postUpdate(DynamicEntityEvent event, Session session)
			throws Exception {
		logger.info("开始调账。");
		
		try{
			Map<String, Object> eventEntity = event.getEntity();
			logger.info("调账参数：" + eventEntity);
			
			Integer status = (Integer)eventEntity.get("WF_State");
			if(status == 4){
				Long targetMemberId = (Long)eventEntity.get("target_member_id");
				Map<String, Object> targetMemberEntity = DynamicEntityService.getEntity(targetMemberId, "AMS_Member");
				Long targetApplicationId = (Long)targetMemberEntity.get("application_id");
				Map<String, Object> targetApplication = DynamicEntityService.getEntity(targetApplicationId, "AMS_Organization");
				String sysid = (String)targetApplication.get("sysid");
				eventEntity.put("orgNo", sysid);
				
				AdjustAccount adjustAccount = new AdjustAccount(eventEntity, session);
	            adjustAccount.run();
			}
		}catch(Exception e){
			logger.info(e.getMessage());
			throw e;
		}

	}

	public void preCreate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void preRemove(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void preUpdate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}
}
