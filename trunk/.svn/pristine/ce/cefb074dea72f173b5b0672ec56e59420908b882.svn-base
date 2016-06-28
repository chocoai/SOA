package bps.external.tradecommand;

import bps.common.Constant;
import bps.order.OrderServiceImpl;
import bps.process.Extension;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Administrator on 2016/5/20.
 */
public class DaikouQueryTradeResult extends Thread {
    private static Logger logger = Logger.getLogger(DaikouQueryTradeResult.class);
    private static long[] sleepSecondList = {1000,2000,4000,8000,16000,32000,300000};
    private Map<String, Object> queryParams = new HashMap();
    private String commandNo = null;

    public DaikouQueryTradeResult(String orgNo, Date requestTime, String queryId, String commandNo){
        this.commandNo = commandNo;

        queryParams.put("orgNo", orgNo);
        queryParams.put("requestTime", requestTime);
        queryParams.put("commandNo", commandNo);
        queryParams.put("queryId", queryId);
    }

    public void run(){
        logger.info("run sleepSecondList.length=" + sleepSecondList.length);

        try{
            for(int i = 0;  i < sleepSecondList.length; i++){
                sleep(sleepSecondList[i]);

                logger.info("Extension.daikouService.query 参数=" + queryParams);
                Map<String, String> response = Extension.daikouService.query(queryParams);
                logger.info("Extension.daikouService.query 返回=" + response);

                //对返回结果做处理
                boolean isHandle =handleQueryResponse(response);
                if(isHandle == true){
                    break;
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 处理查询结果
     * @param params
     * @throws Exception
     */
    private boolean handleQueryResponse(Map<String, String> params) throws Exception {
        String respCode = params.get("respCode");
        String origRespCode = params.get("origRespCode");
        String origRespMsg = params.get("origRespMsg");
        String payAmount = params.get("txnAmt"); //代扣金额
        String outTradeId = params.get("queryId"); //代扣金额

        if(("00").equals(respCode)){  //查询调用成功

            if("00".equals(origRespCode)){    //代扣成功
                //触发外部
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("orderNo", commandNo);
                param.put("orderMoney", Long.valueOf(payAmount));
                param.put("outTradeId", outTradeId);
                param.put("payChannelNo", Constant.PAY_CHANNEL_UNION_DAIKOU);
                param.put("payInterfaceNo", Constant.PAY_INTERFACE_UNION_DAIKOU);
                param.put("tradeTime", new Date());
                OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
                orderServiceImpl.payChannelDeposit(param);

                return true;
            }else if(("03").equals(respCode)||
                    ("04").equals(respCode)||
                    ("05").equals(respCode)) {  //代扣进行中

                return false;
            }else{                              //代扣失败
                OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
                orderServiceImpl.closeCommandOrder(commandNo, null);

                return true;
            }
        }else if(("03").equals(respCode)||
                ("04").equals(respCode)||
                ("05").equals(respCode)){    //查询调用进行中，不进行处理，容易引起死循环

                return false;
        }else{                               //查询调用失败
                return false;
        }
    }
}
