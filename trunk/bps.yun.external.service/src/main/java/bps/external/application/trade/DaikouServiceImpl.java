package bps.external.application.trade;

import bps.common.Constant;
import bps.common.JedisUtils;
import bps.external.application.Extension;
import bps.external.application.service.trade.DaikouService;
import com.kinorsoft.allinpay.daikou.UnionPayDK;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/5/18.
 */
public class DaikouServiceImpl implements DaikouService {
    private Logger logger = Logger.getLogger(DaikouServiceImpl.class);
    private SimpleDateFormat sdfyyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    private String environment = Extension.paramMap.get("environment");

    @Override
    public Map<String, String> daikou(Map<String, Object> params) throws Exception {
        logger.info("DaikouServiceImpl daikou params=" + params);

        try{
            //获取参数
            String yunDaikouOrderId = (String)params.get("yunDaikouOrderId");
            Long daikouAmount = (Long)params.get("amount");
            String certifId = (String)params.get("certifId");
            String name = (String)params.get("name");
            String phone = (String)params.get("phone");
            String accNo = (String)params.get("accNo");
            String orgNo = (String)params.get("orgNo");
            Date requestTime = (Date)params.get("requestTime");
            String backUrl = Extension.paramMap.get("daikou.union.backurl");

            //检查参数

            //获取通道配置参数
            String piAppConfStr = JedisUtils.getCacheByKey(Constant.REDIS_KEY_PI_APP_CONF);
            JSONObject piAppConfObj = new JSONObject(piAppConfStr);
            String cacheKey = Constant.PAY_INTERFACE_UNION_DAIKOU + "_" + orgNo;
            JSONObject kaikouConf = (JSONObject)piAppConfObj.get(cacheKey);
            String merId = (String)kaikouConf.get("merchant_id");

            //组装参数
            Map<String, String> kaikouParams = new HashMap();
            kaikouParams.put("merId", merId);
            kaikouParams.put("orderId", yunDaikouOrderId);
            kaikouParams.put("txnTime", sdfyyyyMMddHHmmss.format(requestTime));
            kaikouParams.put("txnAmt", daikouAmount.toString());
            kaikouParams.put("certifId", certifId);
            kaikouParams.put("customerNm", name);
            kaikouParams.put("phoneNo", phone);
            kaikouParams.put("accNo", accNo);
            kaikouParams.put("backUrl", backUrl);
            kaikouParams.put("environment", environment);

            //发送
            UnionPayDK unionPayDK = new UnionPayDK();
            logger.info("unionPayDK.daiKou请求：" + kaikouParams);
            Map<String, String> ret = unionPayDK.daiKou(kaikouParams);
            logger.info("unionPayDK.daiKou 返回：" + ret);

            return ret;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Map<String, String> query(Map<String, Object> params) throws Exception {
        logger.info("DaikouServiceImpl query params = " + params);

        try{
            //获取参数
            String orgNo = (String)params.get("orgNo");
            Date requestTime = (Date)params.get("requestTime");
            String commandNo = (String)params.get("commandNo");
            String queryId = (String)params.get("queryId");
            String backUrl = Extension.paramMap.get("daikou.union.backurl");

            //获取通道配置参数
            String piAppConfStr = JedisUtils.getCacheByKey(Constant.REDIS_KEY_PI_APP_CONF);
            JSONObject piAppConfObj = new JSONObject(piAppConfStr);
            String cacheKey = Constant.PAY_INTERFACE_UNION_DAIKOU + "_" + orgNo;
            JSONObject kaikouConf = (JSONObject)piAppConfObj.get(cacheKey);
            String merId = (String)kaikouConf.get("merchant_id");

            //组装参数
            Map<String, String> queryParams = new HashMap();
            queryParams.put("merId", merId);
            queryParams.put("txnTime", requestTime == null ? null : sdfyyyyMMddHHmmss.format(requestTime));
            queryParams.put("orderId", commandNo);
            queryParams.put("queryId", queryId);
            queryParams.put("backUrl", backUrl);

            //请求
            UnionPayDK unionPayDK = new UnionPayDK();
            logger.info("unionPayDK.query 请求：" + queryParams);
            Map<String, String> ret = unionPayDK.query(queryParams);
            logger.info("unionPayDK.query 返回：" + ret);

            return ret;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 退货 支持部分退货
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> refund(Map<String, Object> params) throws Exception {
        logger.info("DaikouServiceImpl refund params = " + params);

        try{
            //获取参数
            String orgNo = (String)params.get("orgNo");
            Date requestTime = (Date)params.get("requestTime");
            String commandNo = (String)params.get("commandNo");
            String oriQueryId = (String)params.get("oriQueryId");
            Long amount = (Long)params.get("amount");
            String backUrl = Extension.paramMap.get("daikou.union.backurl");

            //获取通道配置参数
            String piAppConfStr = JedisUtils.getCacheByKey(Constant.REDIS_KEY_PI_APP_CONF);
            JSONObject piAppConfObj = new JSONObject(piAppConfStr);
            String cacheKey = Constant.PAY_INTERFACE_UNION_DAIKOU + "_" + orgNo;
            JSONObject kaikouConf = (JSONObject)piAppConfObj.get(cacheKey);
            String merId = (String)kaikouConf.get("merchant_id");

            //组装参数
            Map<String, String> refunduParams = new HashMap();
            refunduParams.put("origQryId", oriQueryId);
            refunduParams.put("txnAmt", amount.toString());
            refunduParams.put("orderId", commandNo);
            refunduParams.put("merId", merId);
            refunduParams.put("txnTime", sdfyyyyMMddHHmmss.format(requestTime));
            refunduParams.put("backUrl", backUrl);

            //发送
            UnionPayDK unionPayDK = new UnionPayDK();
            logger.info("unionPayDK.refund：" + refunduParams);
            Map<String, String> ret = unionPayDK.refund(refunduParams);
            logger.info("unionPayDK.refund 返回：" + ret);

            return ret;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }
}
