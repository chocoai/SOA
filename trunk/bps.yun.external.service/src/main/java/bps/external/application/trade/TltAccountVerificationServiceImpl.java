package bps.external.application.trade;

import bps.common.BizException;
import bps.external.application.Extension;
import bps.external.application.service.trade.TltAccountVerificationService;
import com.kinorsoft.allinpay.interfaces.TranxServiceImpl;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 通联通帐户验证接口实现
 * Created by yyy on 2016/6/21.
 */
public class TltAccountVerificationServiceImpl implements TltAccountVerificationService {
    private static Logger logger = Logger.getLogger(TltAccountVerificationServiceImpl.class.getName());

    @Override
    public Map<String, Object> bindBankCard(String tradeNo, String bankCode, String cardType, String accountName, String accountNo, Long accountProp, String id_type, String id_no, Map<String, Object> extParams) throws Exception {
        if(tradeNo==null){
            throw new Exception("tradeNo参数不能为空！");
        }
        if(accountName==null){
            throw new Exception("accountName参数不能为空！");
        }
        if(accountNo==null){
            throw new Exception("accountNo参数不能为空！");
        }
        if(bankCode==null){
            throw new Exception("bankCode参数不能为空！");
        }
        if(cardType==null){
            throw new Exception("cardType参数不能为空！");
        }
        if(accountProp==null){
            throw new Exception("accountProp参数不能为空！");
        }
        if(extParams == null || extParams.isEmpty()){
            throw new Exception("extParams参数不能为空！");
        }

//        String orgNo = (String)extParams.get("orgNo");
//        if(orgNo == null || "".equals(orgNo)){
//            throw new BizException("99999","orgNo参数不能为空！");
//        }
        Map<String, Object> result;

        Map<String, String> reqParam = new HashMap<>();
        reqParam.put("REQ_SN",          tradeNo);
//          reqParam.put("BUSINESS_CODE",   "09400");
        reqParam.put("BUSINESS_CODE",   "09900");

        reqParam.put("ACCOUNT_NAME",    accountName);
        reqParam.put("ACCOUNT_NO",      accountNo);
        reqParam.put("ID_TYPE",         id_type);
        reqParam.put("ID",              id_no);
        reqParam.put("BANK_CODE",       bankCode);
        reqParam.put("ACCOUNT_TYPE",    cardType);
        reqParam.put("ACCOUNT_PROP",    accountProp.toString());
//        reqParam.put("ORG_NO", orgNo);
        try {
            //测试环境或者开发环境，直接返回成功
            String environment = Extension.paramMap.get("environment");

            String serverUrl = Extension.paramMap.get("AlipayConfig.interfaces.serverUrl");
            String userName = Extension.paramMap.get("AlipayConfig.interfaces.userName");
            String passWord = Extension.paramMap.get("AlipayConfig.interfaces.passWord");
            String merchantId = Extension.paramMap.get("AlipayConfig.interfaces.merchantId");
            String tltcerPath = Extension.paramMap.get("AlipayConfig.interfaces.tltcerPath");
            String pfxPath = Extension.paramMap.get("AlipayConfig.interfaces.pfxPath");
            String pfxPassword = Extension.paramMap.get("AlipayConfig.interfaces.pfxPassword");
            String downloadUrl = Extension.paramMap.get("AlipayConfig.interfaces.downloadUrl");

//            if("develop".equals(environment) || "test".equals(environment)){
//                result = new HashMap<>();
//                result.put("RET_CODE1", "0000");
//
//                result.put("ERR_MSG1","");
//                result.put("RET_CODE","0000");
//                result.put("ERR_MSG","");
//                result.put("RET_DETAILS","");
//            }else{
                TranxServiceImpl tranxServiceImpl = new TranxServiceImpl();
                Map<String, String> param = new HashMap<>();
                param.put("serverUrl", serverUrl);
                param.put("userName", userName);
                param.put("passWord", passWord);
                param.put("merchantId", merchantId);
                param.put("tltcerPath", tltcerPath);
                param.put("pfxPath", pfxPath);
                param.put("pfxPassword", pfxPassword);
                param.put("downloadUrl", downloadUrl);
                tranxServiceImpl.setParam(param);
                result = tranxServiceImpl.signApply(reqParam, false);
//            }

            logger.info("result:" + result);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
        return result;
    }
}
