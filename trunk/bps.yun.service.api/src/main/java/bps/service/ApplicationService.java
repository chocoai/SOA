/**   
* @Title: ApplicationService.java 
* @Package bps.service 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年9月17日 上午8:39:52 
* @version V1.0   
*/
package bps.service;

import java.util.Map;

import bps.common.BizException;

/** 
 * 应用的创建
 * @author huadi
 * @date 2015年9月17日 上午8:39:52  
 */
public interface ApplicationService {

	/**
	 * 
	* 创建应用接口
	* @param memberId 会员ID  *必填
	* @param appType  应用类型(Long) 1web  2 app *必填
	* @param appName  应用名称(String 长度 100) 	*必填
	* @param extendInfo  扩展信息  		*非必填
	* 			ipc_path(ICP备案许可证)(String 长度 100) 	*非必填
	* 			license_path(行业许可证)(String 长度 500) *非必填
	* 			credit_path(信用机构代码证)(String 长度 500) *非必填
	* 			web_url(网址)(String 长度 500)				*非必填
	* 			remark(备注)(String 长度 500)   *非必填
	* @return
	* @throws BizException
	 */
	public Map<String,Object> createApplication(Long memberId,Long appType,String appName,
			Map<String,Object> extendInfo )throws BizException ;
}
