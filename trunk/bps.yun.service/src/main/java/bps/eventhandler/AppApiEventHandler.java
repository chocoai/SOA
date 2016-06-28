/**   
* @Title: AppApiEventHandler.java 
* @Package bps.eventhandler 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年12月28日 下午2:31:01 
* @version V1.0   
*/
package bps.eventhandler;

import java.util.Map;

import org.hibernate.Session;

import bps.process.AppSoaApiManage;
import ime.core.event.DynamicEntityEvent;
import ime.core.event.IEntityEventHandler;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年12月28日 下午2:31:01  
 */
public class AppApiEventHandler implements IEntityEventHandler{
	

	
	public void preCreate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public void postCreate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		AppSoaApiManage.loadSoaApiInfo();
	}

	
	

	public void preUpdate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public void postUpdate(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		AppSoaApiManage.loadSoaApiInfo();
	}

	
	

	
	public void preRemove(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void postRemove(DynamicEntityEvent arg0, Session arg1)
			throws Exception {
		// TODO Auto-generated method stub
		AppSoaApiManage.loadSoaApiInfo();
	}
	
	
	
	
	
	public Map<String, Object> customerEvent(String arg0, String arg1,
			Map<String, Object> arg2, Session arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

}
