/**   
* @Title: CodeUtil.java 
* @Package bps.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年9月23日 上午10:21:26 
* @version V1.0   
*/
package bps.util;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年9月23日 上午10:21:26  
 */
public class CodeUtil {
	private static Logger logger = Logger.getLogger(CodeUtil.class);
	
	/***
	 * code生成方法，原子操作，超过10次  就失败
	* @param prefix
	* @param codeType
	* @param length
	* @return
	* @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public  static String getCode(String prefix,Long codeType,int length) throws Exception{
		logger.info("==========================CodeUtil.getCode start=============================");
		logger.info("参数prefix=" + prefix + ",codeType=" + codeType + ",length=" + length);
		
		final Long _codeType = codeType;
		final String _prefix = prefix;
		final int _length = length;
		String code = EntityManagerUtil.execute(new TransactionWork<String>() {
			@Override
			public String doTransaction(Session session, Transaction tx)
					throws Exception {
				// TODO Auto-generated method stub
				int limit = 0;
				while(true){
					if(limit>10){
						throw new Exception("生成code出错");
					}
					Query query = session.createQuery("from AMS_CodeNo where codeType=:codeType");
					query.setParameter("codeType", _codeType);
					List<Map<String,Object>> list = query.list();
					if(list.size() != 1){
						throw new Exception("所属Code不存在");
					}
					Map<String,Object> codeEntity= list.get(0);
					logger.info("codeEntity=" + codeEntity);
					Long version = (Long)codeEntity.get("version");
					Long num = (Long)codeEntity.get("num");
					num = num +1;
					String code = _prefix;
					int len = _length - code.length() - (num.toString().length());
					String zerofill = "";
					for(int i=0;i<len ;i++){
						zerofill= zerofill+"0";
					}
					String retcode = _prefix + zerofill + num;
					query = session.createQuery("update AMS_CodeNo set num=:num , code=:code,version=version+1 where codeType=:codeType and version=:version");
					query.setParameter("num", num);
					query.setParameter("code", retcode);
					query.setParameter("codeType", _codeType);
					query.setParameter("version", version);
					
					logger.info("num=" + num + ",code=" + code + ",codeType=" + _codeType + ",version=" + version);
					
					int i = query.executeUpdate();
					if(i==1){
						return retcode;
					}
					if(i >1){
						throw new Exception("生成code出错");
					}
					limit+= 1;
				}
			}
		});
		
		logger.info("==========================CodeUtil.getCode end=============================");
		logger.info("返回code=" + code);
		return code;
	}
}
