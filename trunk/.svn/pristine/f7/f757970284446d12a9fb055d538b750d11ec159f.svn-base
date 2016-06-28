/**   
* @Title: TradeRule.java 
* @Package bps.rule 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年12月31日 下午3:20:17 
* @version V1.0   
*/
package bps.rule;

import ime.core.services.DynamicEntityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.order.Order;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年12月31日 下午3:20:17  
 */
public class TradeRule {
	private static Logger logger = Logger.getLogger(TradeRule.class.getName());
	
	
	
	/***
	 * 
	* 交易规则检查
	* @param tradeType
	* @param subTradeType
	* @param payInterfaceList
	* @param accountList
	* @param applicationEntity
	* @throws Exception
	 */
	public static void checkTradeRule(Long tradeType,Long subTradeType,List<Map<String,Object>> payInterfaceList,List<Map<String,Object>> accountList,Map<String,Object> applicationEntity) throws Exception{
		List<Map<String, Object>> avaliablePayInterfaceNo = getOrgTradeRule((String)applicationEntity.get("codeNo"), tradeType, subTradeType);
		if(payInterfaceList != null){
			for(int j=0;j<payInterfaceList.size();j++){
				Map<String, Object> payInterfaceMap = (Map<String, Object>) payInterfaceList.get(j);
				boolean flag = true;
				for(Map<String, Object> entity : avaliablePayInterfaceNo) {//验证支付规则是否支持
					if(((String)payInterfaceMap.get("payInterFaceNo")).equals(entity.get("pay_interfaceNo"))) {
						if(payInterfaceMap.get("payInterFaceNo").equals(Constant.PAY_INTERFACE_QUICK)){
							Map<String,Object> bank_card = DynamicEntityService.getEntity((Long)payInterfaceMap.get("bankId"), "AMS_MemberBank");
			                if(bank_card == null)
			                    throw new Exception("会员银行卡不存在");
			                if(Constant.BANK_CARD_XY.longValue()==(Long)bank_card.get("card_type")){
			                	if(entity.get("credit")!=null&&!(Boolean)entity.get("credit")){
			                		flag = false;
			                		break;
			                	}
			                }else if(Constant.BANK_CARD_CX.longValue()==(Long)bank_card.get("card_type")){
			                	if(entity.get("debit")!=null&&!(Boolean)entity.get("debit")){
			                		flag = false;
			                		break;
			                	}
			                }
							
						}else if(payInterfaceMap.get("payInterFaceNo").equals(Constant.PAY_INTERFACE_GETWAY_JJ)){
							if(Constant.GATWAY_PAY_TYPE_XY.longValue()==((Long)payInterfaceMap.get("pay_type"))){
								if(entity.get("credit")!=null&&!(Boolean)entity.get("credit")){
			                		flag = false;
			                		break;
			                	}
							}else if(Constant.GATWAY_PAY_TYPE_CX.longValue()==((Long)payInterfaceMap.get("pay_type"))||Constant.GATWAY_PAY_TYPE_RZ.longValue()==((Long)payInterfaceMap.get("pay_type"))){
								if(entity.get("debit")!=null&&!(Boolean)entity.get("debit")){
			                		flag = false;
			                		break;
			                	}
							}
						}
					}
				}
				if(!flag){//不支持
					throw new BizException(ErrorCode.TRANSACTION_ROUTING_NO_ALLOW,"交易失败，交易规则受限");
				}
			}
		}
		
		if(accountList != null){
			for(int j=0;j<accountList.size();j++){
				boolean flag = true;
				Map<String, Object> accountMap = (Map<String, Object>) accountList.get(j);
				for(Map<String, Object> entity : avaliablePayInterfaceNo) {
					if(accountMap.get("accountSetNo").equals(entity.get("accountNo"))){
						if(entity.get("account")!=null&&!(Boolean)entity.get("account")){
	                		flag = false;
	                		break;
	                	}
					}
				}
				if(!flag){
					throw new BizException(ErrorCode.TRANSACTION_ROUTING_NO_ALLOW,"交易失败，交易规则受限");
				}
			}
		}
		
		
	}
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getOrgTradeRule(String orgNo,
			Long tradeType, Long subTradeType) throws BizException {
		logger.info("OrderServiceImpl.getOrgTradeRule start");
		try {
			final  Map<String, Object> param=new HashMap<String,Object>();
			param.put("orgNo", orgNo);
			param.put("trade_type", tradeType);
			param.put("sub_trade_type", subTradeType);
			
			List<Map<String, Object>> res=
			EntityManagerUtil.execute(new QueryWork<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> doQuery(Session session) throws Exception {
					return getOrgTradeRule(param, session);
				}
				
			});
			return res;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}

	}
	
	/**
     * 查询机构交易路由
     * @param param
     * @param session
     * @throws Exception
     * @return List
     */
    public static List getOrgTradeRule(Map<String, Object> param, Session session)throws Exception{
    	 List<Map<String,Object>> tr_list = Order.getTradeRule(param, session);
        
         List list = new ArrayList();
         logger.info("orgNogetOrgTradeRule"+(String)param.get("orgNo"));
    	 StringBuffer sb = new StringBuffer();
    	 sb.setLength(0);
         sb.append("from AMS_OrgPayPermission where orgNo=:orgNo and trade_type=:trade_type");
         if(param.get("sub_trade_type") != null) {
             sb.append(" and sub_trade_type=:sub_trade_type");
         }
         Query query = session.createQuery(sb.toString());
         query.setParameter("orgNo", (String)param.get("orgNo"));
         query.setParameter("trade_type", (Long)param.get("trade_type"));
         if(param.get("sub_trade_type") != null) {
             query.setParameter("sub_trade_type", (Long)param.get("sub_trade_type"));
         }
         List<Map<String,Object>> opp_list = query.list();//机构支持的交易规则
        
         logger.info("====opp_list="+opp_list);
         logger.info("====tr_list="+tr_list);
         //总的交易规则  和 各机构的交易规则进行合并，如果两种交易规则都有，就进行交集
         for(Map<String, Object> tr_entity : tr_list){
        	 if(tr_entity.get("debit") == null){
        		 tr_entity.put("debit", false);
        	 }
        	 if(tr_entity.get("credit") == null){
        		 tr_entity.put("credit", false);
        	 }
        	 if(tr_entity.get("account") == null){
        		 tr_entity.put("account", false);
        	 }
        	 
        	 for(Map<String,Object> opp_entity : opp_list) {
        		 if(opp_entity.get("debit") == null){
    				 opp_entity.put("debit", false);
            	 }
            	 if(opp_entity.get("credit") == null){
            		 opp_entity.put("credit", false);
            	 }
            	 if(opp_entity.get("account") == null){
            		 opp_entity.put("account", false);
            	 }
        		 
        		 if(tr_entity.get("pay_interfaceNo") != null && ((String)tr_entity.get("pay_interfaceNo")).equals((String)opp_entity.get("pay_interfaceNo"))) {
                	 if((Boolean)tr_entity.get("debit") && (Boolean)opp_entity.get("debit")){
                		 tr_entity.put("debit", true);
                	 }else{
                		 tr_entity.put("debit", false);
                	 }
                	 if((Boolean)tr_entity.get("credit") && (Boolean)opp_entity.get("credit")){
                		 tr_entity.put("credit", true);
                	 }else{
                		 tr_entity.put("credit", false);
                	 }
                 } else if(tr_entity.get("accountNo") != null && ((String)tr_entity.get("accountNo")).equals((String)opp_entity.get("accountNo"))) {
                	 if((Boolean)tr_entity.get("account") && (Boolean)opp_entity.get("account")){
                		 tr_entity.put("account", true);
                	 }else{
                		 tr_entity.put("account", false);
                	 }
                     list.add(tr_entity);
                     break;
                 }
        	 }
        	 list.add(tr_entity);
         }
         //如果机构的交易规则在总的交易规则里面没有就添加
         for(Map<String,Object> opp_entity : opp_list){
        	 boolean noadd = true;
        	 for(Map<String, Object> tr_entity : tr_list){
        		 if(tr_entity.get("pay_interfaceNo") != null && ((String)tr_entity.get("pay_interfaceNo")).equals((String)opp_entity.get("pay_interfaceNo"))) {
        			 noadd = false;
        		 }else if(tr_entity.get("accountNo") != null && ((String)tr_entity.get("accountNo")).equals((String)opp_entity.get("accountNo"))) {
        			 noadd = false;
        		 }
        	 }
        	 if(noadd){
        		 list.add(opp_entity);
        	 }
        	 
         }
         logger.info("====list="+list);
         return list;
    }

}
