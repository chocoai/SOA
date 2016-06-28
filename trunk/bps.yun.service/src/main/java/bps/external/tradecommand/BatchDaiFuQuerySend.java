package bps.external.tradecommand;


import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.Constant;
import bps.common.JedisUtils;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 发送到批量代付
 * Created by yyy on 2016/3/9.
 */
public class BatchDaiFuQuerySend extends Thread {

    private static Logger logger = Logger.getLogger(BatchDaiFuQuerySend.class);
    private String message;
    private Long waitTimeEach = 1000L*60L*5L;
    private Long queryCount = 5L;

    private Map<String, Object> result;

    public BatchDaiFuQuerySend(String message){
        this.message = message;
    }
//    public BatchDaiFuQuerySend(){};
    public void run(){
        logger.info("-----------开始调用批量代付查询-------------");
        logger.info("message:"+message);
        try{
            String REQ_SN = System.currentTimeMillis() + "cx";
            String query_no = message.split("##")[0];
            String reg_no = message.split("##")[1];
            Map<String, Object> param = new HashMap<>();
            param.put("orgNo", reg_no);
            result = Extension.tltDaiFuService.queryTradeResult(REQ_SN, query_no, param);
            logger.info("查询结果："+result);
            String ret_code1 	=  (String)result.get("RET_CODE1");


            if (ret_code1.equals("0000")){
                TltBatchDaiFu tbf = new TltBatchDaiFu();
                tbf.doCommand(result);

//                BatchDaiFuQuerySend bdfs = new BatchDaiFuQuerySend();
//                bdfs.updateOrderByBatchDaiFuState(query_no,3L);

            }else{
                logger.info("没有成功，重新发送到队列进行查询："+message);
                //使用延时队列：n分钟一次，每次n+1.最多5次。
                String redisKey = Constant.BATCH_DAIFU_QUERY_FAIL_AMOUNT;
                String amount = JedisUtils.hgetCacheByField(redisKey, query_no);

                if ( amount == null || "".equals(amount) ){
                    amount = "0";
                }
                Long _amount = Long.parseLong(amount) + 1;
                if( _amount > queryCount){
                    throw new Exception("这个批次代付没有成功，批次号："+query_no);
                }

                JedisUtils.hsetCache(redisKey, query_no, _amount.toString());

                Long waitTime = waitTimeEach * _amount;
                logger.info("waitTime:"+waitTime);
                String batch_daiFu_query = Environment.instance().getSystemProperty("rabbitmq.queue_name.batch_daiFu_query");
                RabbitProducerManager rpm = RabbitProducerManager.getInstance();
                rpm.send(batch_daiFu_query+"_exc", batch_daiFu_query, batch_daiFu_query, message, waitTime );
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 修改状态
     * @param reg_no    批次号
     * @param state     状态
     * @throws Exception
     */
    public void updateOrderByBatchDaiFuState(final String reg_no, final Long state) throws Exception {
        if(!LoginSession.isLogined())
            LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);

        EntityManagerUtil.execute(new TransactionWork<Map<String,Object>>() {
            @Override
            public Map<String, Object> doTransaction(Session session, Transaction transaction) throws Exception {

                Order.updateOrderByBatchDaiFu(reg_no,state, session);
                return null;
            }

        });
    }
}
