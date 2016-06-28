package bps.order;

import apf.util.BusinessException;
import apf.util.EntityManagerUtil;
import apf.work.TransactionWork;
import bps.common.Constant;
import bps.common.JedisUtils;
import bps.process.RiskUser;
import bps.service.OrderService;
import com.kinorsoft.ams.ErrorCode;
import com.kinorsoft.ams.services.FreezeService;
import com.kinorsoft.ams.services.QueryService;
import ime.core.Environment;
import ime.core.Reserved;
import ime.security.LoginSession;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 批量代付接口工具类
 * @author 朱成
 * Created by yyy on 2016/3/9.
 */
public class WithdrawOrderToBatchDaiFu {
    private static Logger logger = Logger.getLogger(WithdrawOrderToBatchDaiFu.class.getName());

    private static DateFormat df = new SimpleDateFormat("yyyyMMdd");

    private String redisKey;
    private String redisKeyT0;

    public WithdrawOrderToBatchDaiFu()throws Exception{
        Environment environment = null;
        try {
            environment = Environment.instance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (environment == null){
            throw new Exception("批量代付构造方法出错！");
        }
//        ip 	= environment.getSystemProperty("cache.ip");
//        port	= Integer.valueOf(environment.getSystemProperty("cache.port"));
        redisKey =  environment.getSystemProperty("batch_daiFu_temporary_store");
        redisKeyT0 =  environment.getSystemProperty("batch_daiFu_temporary_store_T0");
    }
    /**
     * 批量代付确认支付调用：冻结金额、发送到队列。
     * @param command   参数
     * @throws Exception
     */
    public void confirmPay(Map<String, Object> command, Long withdrawReserveModel) throws Exception {
        logger.info("-------------提现调用通联通批量确认支付:----------------");
        logger.info("command:" + command);

        if(command.get("bizid") == null)
            throw new Exception("请传入参数 bizid");
        String command_no                = (String)command.get("command_no");
        String bizid                     = (String)command.get("bizid");
        if(command.get("source_userId") == null)
            throw new Exception("请传入参数 source_userId");

        String source_userId		= (String)command.get("source_userId");
        if(command.get("trade_money") == null)
            throw new Exception("请传入参数 trade_money");

        Long trade_money = (Long)command.get("trade_money");
        Long account_type_id	= (Long)command.get("account_type_id");
        if(account_type_id == null)
            throw new Exception("请传入参数 account_type_id");

        String bank_code = (String)command.get("bank_code");
        if(bank_code == null)
            throw new Exception("请传入参数 bank_code");

        String accountNo_encrypt	= (String)command.get("accountNo_encrypt");
        if(accountNo_encrypt == null)
            throw new Exception("请传入参数 accountNo_encrypt");

        String account_name			= (String)command.get("account_name");
        if(account_name == null)
            throw new Exception("请传入参数 account_name");

        Long card_type				= (Long)command.get("card_type");
        if(card_type == null)
            throw new Exception("请传入参数 card_type");

        Long account_prop			= (Long)command.get("account_prop");
        if(account_prop == null)
            throw new Exception("请传入参数 account_prop");
        String orgNo = (String)command.get("orgNo");
        if(orgNo == null)
            throw new Exception("请传入参数orgNo");
        //查询会员账户
        Map<String, Object> member_account = QueryService.queryAccount(source_userId, account_type_id);
        if(member_account == null)
            throw new BusinessException(ErrorCode.ACCOUNT_NO_MONEY, "账户余额不足");
        Long amount = (Long)member_account.get("amount");
        Long freeze_amount	= (Long)member_account.get("freeze_amount");
        if(freeze_amount == null)
            freeze_amount = 0L;
        //冻结订单的所有金额
//        Long to_freeze_money = trade_money;
        OrderService orderService = new OrderServiceImpl();
        Map<String,Object> order_entity = orderService.getOrder(bizid);
//        if(Constant.ORDER_TYPE_DAIFU.equals(order_entity.get("order_type"))||Constant.ORDER_TYPE_BATCH_DAIFU.equals(order_entity.get("order_type"))){
        Long to_freeze_money = (Long)order_entity.get("order_money");
//        }
        //判断账户余额
        if(amount - freeze_amount - to_freeze_money < 0 )
            throw new BusinessException(ErrorCode.ACCOUNT_NO_MONEY, "账户余额不足");

        logger.info("调取多账户冻结金额接口");
        final Map<String, Object> param = new HashMap<>();
        param.put("userId", 			source_userId);
        param.put("account_type_id", 	account_type_id);
        param.put("bizid", 				bizid);
        param.put("freeze_money", 		to_freeze_money);
        param.put("remark", 			"批量代付,冻结金额");

        LoginSession.backUse(null, Reserved.BACKUSER_ID, Reserved.MAIN_DOMAIN_ID);
        EntityManagerUtil.execute(new TransactionWork<Map<String, Object>>() {
            @Override
            public Map<String, Object> doTransaction(Session session, Transaction tx)
                    throws Exception {
                //Map<String, Object> signlog_entity = null;
                FreezeService.freezeMoney(param);
                return null;
            }
        });
        String key 	=  source_userId + Constant.MEMBER_BANK_ENCODE_KEY;
        String accountNo = ime.security.util.TripleDES.decrypt(key, accountNo_encrypt);
        logger.info("银行卡解密后查看是否进入黑名单筛选");
        if(!RiskUser.checkRiskUserInfo("riskBankCardNo", accountNo)) {
            throw new Exception("账号异常已锁定，如有任何疑问请联系客服。");
        }
        JSONObject meg = new JSONObject();
        meg.put("command_no",command_no);
        meg.put("account_name",account_name);
        meg.put("accountNo",accountNo);
        meg.put("trade_money",trade_money);
        meg.put("bank_code",bank_code);
        meg.put("card_type",card_type);
        meg.put("account_prop",account_prop);
        meg.put("orgNo",orgNo);

//        RabbitProducerManager rpm = RabbitProducerManager.getInstance();
//        String batch_daiFu_one = Environment.instance().getSystemProperty("rabbitmq.queue_name.batch_daiFu_one");
//        rpm.send(batch_daiFu_one, batch_daiFu_one, batch_daiFu_one, meg.toString());
        Date now = new Date();
        String fileDt = df.format(now);
        if ( Constant.WITHDRAW_RESERVE_MODEL_ENTRUST.equals(withdrawReserveModel) ){
            redisKey =redisKeyT0;
        }else{
            redisKey = redisKey+"_"+fileDt;
        }
        JedisUtils.hsetCache(redisKey, command_no, meg.toString());
//        putRedis(command_no, meg.toString());
    }

}
