package bps.check.limit;

import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import ime.core.services.DynamicEntityService;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * 提现订单检查方式
 * Created by 朱成 on 2016/4/21.
 */
public class WithdrawOrderLimit {
    private static Logger logger = Logger.getLogger(WithdrawOrderLimit.class.getName());
    /**
     * 检查支付方式、准备金模式和传入支付方式是否合规则。
     * @param applicationId     应用ID
     * @param payType           支付方式
     * @throws BizException
     */
    public static void checkPayType(Long applicationId, String payType) throws BizException{
        logger.info("----------------checkPayType------------");
        boolean is_withdrawType_t0;
        boolean is_withdrawType_t1;
        Long withdrawReserveModel;
        try {
            Map<String,Object> applicationEntity = DynamicEntityService.getEntity(applicationId, "AMS_Organization");
            is_withdrawType_t0 = applicationEntity.get("is_withdrawType_t0") != null && (Boolean) applicationEntity.get("is_withdrawType_t0");
            is_withdrawType_t1 = applicationEntity.get("is_withdrawType_t1") != null && (Boolean) applicationEntity.get("is_withdrawType_t1");
            withdrawReserveModel  = applicationEntity.get("withdrawReserveModel") == null ? 0L: (Long)applicationEntity.get("withdrawReserveModel");
            logger.info("is_withdrawType_t0:"+is_withdrawType_t0);
            logger.info("is_withdrawType_t1:"+is_withdrawType_t1);
            logger.info("withdrawReserveModel:"+withdrawReserveModel);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BizException(ErrorCode.WITHDRAW_PAY_TYPE_NOT_SET,"其他错误！");
        }


        if ( !is_withdrawType_t0 && !is_withdrawType_t1 ){
            throw new BizException(ErrorCode.WITHDRAW_PAY_TYPE_NOT_SET, "没有设置提现支付方式");
        }
        if( withdrawReserveModel.equals(0L) ){
            throw new BizException(ErrorCode.WITHDRAW_PAY_TYPE_NOT_SET, "没有设置提现准备金模式");
        }
        if( payType.equals("T1") && !is_withdrawType_t1 ){
            throw new BizException(ErrorCode.WITHDRAW_PAY_TYPE_NOT_SET, "不支持T+1提现");
        }else if (payType.equals("T0") && !is_withdrawType_t0 ){
            throw new BizException(ErrorCode.WITHDRAW_PAY_TYPE_NOT_SET, "不支持T+0提现");
        }
    }

    /**
     * 通过支付方式和准备金模式返回调用方式
     * @param payType                  支付方式
     * @param withdrawReserveModel      准备金模式
     * @return 调用方式（Long）：1:t+0实时代付，2：t+0批量代付，3：T+1批量代付
     */
    public static Long getPayType(String payType, Long withdrawReserveModel)throws BizException{
        if ("T0".equals(payType)){
            if (Constant.WITHDRAW_RESERVE_MODEL_ENTRUST.equals(withdrawReserveModel)){
                return 2L;
            }else if (Constant.WITHDRAW_RESERVE_MODEL_ACTIVE.equals(withdrawReserveModel)){
                return 1L;
            }
        }else if ("T1".equals(payType)){
            return 3L;
        }
        throw new BizException(ErrorCode.WITHDRAW_PAY_TYPE_NOT_SET, "提现其他错误！");
    }
}
