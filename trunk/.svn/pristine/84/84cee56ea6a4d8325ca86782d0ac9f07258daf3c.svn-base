package bps.external.tradecommand;

import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.order.Order;
import bps.process.Extension;
import bps.common.RabbitProducerManager;
import ime.core.Environment;
import ime.core.Reserved;
import ime.core.services.DynamicEntityService;
import ime.security.LoginSession;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发送到批量代付
 * Created by yyy on 2016/3/9.
 */
public class BatchDaiFuSend extends Thread {

    private static Logger logger = Logger.getLogger(BatchDaiFuSend.class);

    private String now_org_no="";

    private List<Map<String,Object>> list;

    public BatchDaiFuSend(List<Map<String,Object>> list,String now_org_no){
        this.list = list;
        this.now_org_no = now_org_no;
    }

    public void run(){
        logger.info("---------发送批量代付请求---------------------");
        logger.info("orgNo:"+now_org_no);
        logger.info("list:"+list);
        try{
            String REQ_SN;
            Long sumAmount = 0L;

            Map<String, Object> result;
            JSONArray ja = new JSONArray();
            for (Map<String, Object> temp : list){
                ja.put(temp.get("SN"));
                sumAmount += Long.parseLong(temp.get("AMOUNT").toString());
            }
            REQ_SN = System.currentTimeMillis()+"D"+list.get(0).get("SN");

            final Map<String, String> entityMap = new HashMap<>();
            entityMap.put("req_no",REQ_SN);
            entityMap.put("total_item",String.valueOf(list.size()));
            entityMap.put("total_sum",String.valueOf(sumAmount));
            entityMap.put("submit_time",String.valueOf(System.currentTimeMillis()));
            entityMap.put("commands",ja.toString());
            entityMap.put("state","1");
            entityMap.put("org_no",now_org_no);
            createOrder(entityMap);

            if(!LoginSession.isLogined())
                LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);

            Order.createOrderByBatchDaiFu(entityMap);
            
            Map<String, Object> param = new HashMap<>();
            param.put("orgNo",now_org_no);
            try {
                result = Extension.tltDaiFuService.batchDaiFu(REQ_SN, sumAmount, list, param);
                logger.info("----------------批量返回："+result);
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
            //不管是否有异常都执行查询
            RabbitProducerManager rpm = RabbitProducerManager.getInstance();
            String batch_daiFu_query = Environment.instance().getSystemProperty("rabbitmq.queue_name.batch_daiFu_query");
            rpm.send(batch_daiFu_query, batch_daiFu_query, batch_daiFu_query, REQ_SN + "##" + now_org_no);

        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 创建批量支付批次量
     * @param entityMap 实体
     * @throws Exception
     */
    public static void createOrder(final Map<String, String> entityMap) throws Exception {
        if(!LoginSession.isLogined())
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);

        EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
            @Override
            public Map<String, Object> doTransaction(Session session, Transaction transaction) throws Exception {
                DynamicEntityService.createEntity("AMS_TLTBatchDaiFuOrder", entityMap, null);
                //Long id = (Long)orderEntity.get("id");
                return null;
            }

        });
    }
}
