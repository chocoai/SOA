package bps.external.tradecommand;

import bps.common.BizException;
import bps.common.JedisUtils;
import bps.util.DateUtil;
import ime.calendar.TrigerHandler;
import ime.core.Environment;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 批量代付-redis方式
 * 定时期触发类:从redis取出对应的天交易的提现订单列表，组成批量List。调用BatchDaiFuSend类来发送批量代付请求。
 * Created by 朱成 on 2016/4/18.
 */
public class BarchDaiFuRedisTriger implements TrigerHandler {
    public static Logger logger = Logger.getLogger(BarchDaiFuRedisTriger.class);
    private static DateFormat df = new SimpleDateFormat("yyyyMMdd");

    private String redisKey;



    private static Long whileSendAmount;
    private static Map<String,List<Map<String,Object>>> detailList = new HashMap<>();

    public BarchDaiFuRedisTriger()throws Exception{
        Environment environment = null;
        try {
            environment = Environment.instance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (environment == null){
            throw new Exception("批量代付定时发送构造方法出错！");
        }
//        ip 	= environment.getSystemProperty("cache.ip");
//        port	= Integer.valueOf(environment.getSystemProperty("cache.port"));
        redisKey =  environment.getSystemProperty("batch_daiFu_temporary_store");
        whileSendAmount = Long.valueOf(environment.getSystemProperty("batch_daiFu_each_amount"));
    }

    /**
     * 设置批量代付列表（清空传null）
     * @param _detailList 批量代付列表
     */
    private static void setDetailList(Map<String,List<Map<String,Object>>> _detailList){
        if (_detailList == null ) {
            detailList = new HashMap<>();
        }else{
            detailList = _detailList;
        }
    }

    @Override
    public void handle() {
        logger.info("-----------BarchDaiFuRedisTriger 定时-----------------------");

        Date now = new Date();
        String fileDt = df.format(now);
        fileDt = DateUtil.getSpecifiedDayBefore(fileDt,"yyyyMMdd");

        go(fileDt);
    }
    public void go(String date){
        String redisKey_now = redisKey+"_"+date;

        JSONArray dfList = getRedis(redisKey_now);
        logger.info("dfList："+dfList);
        for (int i = 0; i < dfList.length(); i++ ){
            JSONObject df = dfList.optJSONObject(i);
            sendList(df);
        }
        sendListByLost();
        try {
            JedisUtils.delCache(redisKey_now);
        } catch (BizException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *  不满足批量的最后一组数据处理
     */
    private static void sendListByLost(){
        logger.info("----sendListByLost begin-------------");
        logger.info("detailList:"+detailList);
        try {
            if( detailList != null && !detailList.isEmpty()) {
                for (String key : detailList.keySet()) {
                    List<Map<String, Object>> list = detailList.get(key);
                    if (list != null) {
                        logger.info("key:" + key);
                        logger.info("list:" + list);
                        //发送信息
                        BatchDaiFuSend batchDaiFuSend = new BatchDaiFuSend(list, key);
                        batchDaiFuSend.start();
                    }
                }
            }
            BarchDaiFuRedisTriger.setDetailList(null);

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }
    private static synchronized void sendList(JSONObject command){
        try {
//            JSONObject command = new JSONObject(message);
            String orgNo = command.optString("orgNo");
            //now_org_no = orgNo;

            List<Map<String, Object>> list;
            if (detailList.get(orgNo) == null) {
                list = new ArrayList<>();
            } else {
                list = detailList.get(orgNo);
            }
            logger.info("---orgNo:" + orgNo);
            logger.info("---list.size:" + list.size());
            if ( list.size() < whileSendAmount ) {
                Map<String, Object> detile = new HashMap<>();

                String ACCOUNT_NAME = command.optString("account_name");
                String ACCOUNT_NO = command.optString("accountNo");
                String AMOUNT = command.optString("trade_money");
                String BANK_CODE = command.optString("bank_code");
                String ACCOUNT_PROP = command.optString("account_prop");
                String ACCOUNT_TYPE = command.optString("card_type");
                String SN = command.optString("command_no");

                detile.put("ACCOUNT_NAME", ACCOUNT_NAME);
                detile.put("ACCOUNT_NO", ACCOUNT_NO);
                detile.put("AMOUNT", AMOUNT);
                detile.put("BANK_CODE", BANK_CODE);
                detile.put("ACCOUNT_PROP", ACCOUNT_PROP);
                detile.put("ACCOUNT_TYPE", ACCOUNT_TYPE);
                detile.put("SN", SN);
                //detile.put("CUST_USERID",orgNo);
                list.add(detile);
                detailList.put(orgNo, list);
            } else {
                detailList.put(orgNo, null);
                Map<String, Object> detile = new HashMap<>();

                String ACCOUNT_NAME = command.optString("account_name");
                String ACCOUNT_NO = command.optString("accountNo");
                String AMOUNT = command.optString("trade_money");
                String BANK_CODE = command.optString("bank_code");
                String ACCOUNT_PROP = command.optString("account_prop");
                String ACCOUNT_TYPE = command.optString("card_type");
                String SN = command.optString("command_no");

                detile.put("ACCOUNT_NAME", ACCOUNT_NAME);
                detile.put("ACCOUNT_NO", ACCOUNT_NO);
                detile.put("AMOUNT", AMOUNT);
                detile.put("BANK_CODE", BANK_CODE);
                detile.put("ACCOUNT_PROP", ACCOUNT_PROP);
                detile.put("ACCOUNT_TYPE", ACCOUNT_TYPE);
                detile.put("SN", SN);
                //detile.put("CUST_USERID",orgNo);
                list.add(detile);
                //发送信息
                BatchDaiFuSend batchDaiFuSend = new BatchDaiFuSend(list, orgNo);
                batchDaiFuSend.start();
            }
        }catch (Exception e){
            logger.info(e.getMessage(),e);
        }
    }

    /**
     * 从redis中取代付列表
     * @return  JSONArray 代付列表
     */
    public JSONArray getRedis(String redisKey_now){

        JSONArray dfList = new JSONArray();
        try {

            Map<String, String> keyList = JedisUtils.hgetCacheByKey(redisKey_now);
            for ( String df : keyList.values() ){
                JSONObject jb = new JSONObject(df);
                dfList.put(jb);
            }

        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return dfList;
    }
}
