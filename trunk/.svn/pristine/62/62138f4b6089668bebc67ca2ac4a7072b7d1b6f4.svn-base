package bps.order.component;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.member.Member;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/17.
 */
public class GenerateAgentDetail extends Thread {
    private Map<String, Object> orderMap;
    private Logger logger = Logger.getLogger(GenerateAgentDetail.class);

    public GenerateAgentDetail(Map<String,Object> orderMap){
        this.orderMap = orderMap;
    }

    public void run(){
        logger.info("GenerateAgentDetail start");

        LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
        try{
            EntityManagerUtil.execute(new TransactionWork<String>() {
                @Override
                public String doTransaction(Session session, Transaction transaction) throws Exception {
                    try{
                        Query query = session.createQuery("delete AMS_AgentDetail where agentOrder_id=:agentOrder_id");
                        query.setParameter("agentOrder_id", orderMap.get("application_id"));
                        query.executeUpdate();
                        String recieverList = (String)orderMap.get("recieverListClob");
                        JSONArray jsonArray = new JSONArray(recieverList);
                        Map<String,Object> checkAgent = new HashMap<String,Object>();
                        for(int i=0;i<jsonArray.length();i++){
                        	try{
                        		JSONObject o = jsonArray.getJSONObject(i);
                                Member member = new Member();
                                member.setApplication_id((Long)orderMap.get("application_id"));
                                member.setBizUserId(o.getString("bizUserId"));
                                Map<String,String> agentDetail = new HashMap<String,String>();
                                agentDetail.put("agentOrder_id", String.valueOf(orderMap.get("id")));
                                agentDetail.put("agentOrder_label", (String)orderMap.get("orderNo"));
                                agentDetail.put("bizUserId", o.getString("bizUserId"));
                                agentDetail.put("userId", member.getUserId());
                                agentDetail.put("sk_amount", String.valueOf(o.get("amount")));
                                agentDetail.put("ys_amount", "0");
                                agentDetail.put("tk_amount", "0");
                                if(checkAgent.get(member.getUserId())==null){
                                    checkAgent.put(member.getUserId(), true);
                                }else{
                                    throw new Exception("收款人员" + o.getString("bizUserId") + "重复");
                                }
                                DynamicEntityService.createEntity("AMS_AgentDetail", agentDetail, null);
                        	}catch(Exception e){
                        		logger.error(e.getMessage(), e);
                        	}
                        }

                        return null;
                    }catch(Exception e){
                        logger.error(e.getMessage(), e);
                        throw e;
                    }
                }
            });

            logger.info("GenerateAgentDetail end");
        }catch(Exception e){

        }
    }
}
