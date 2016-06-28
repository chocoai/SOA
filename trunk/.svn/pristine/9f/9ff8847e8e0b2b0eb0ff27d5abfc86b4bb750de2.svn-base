package bps.order.Command;

import com.kinorsoft.ams.Constant;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/23.
 */
public class Command {
    private static Logger logger = Logger.getLogger(Command.class);

    /**
     * 保存指令的请求时间
     * @param commandId
     * @param extParams
     * @param session
     * @throws Exception
     */
    public static void saveRequstTime(Long commandId, Map<String, Object> extParams, Session session) throws Exception{
        logger.info("saveRequstTime 参数：commandId=" + commandId + ",extParams=" + extParams);
        try{
            //获取参数
            Date date = (Date)extParams.get("requestTime");

            //保存
            String sql = "update AMS_OrderPayDetail set requestTime=:requestTime where id=:commandId and pay_state=:payState";
            Query query = session.createQuery(sql);
            query.setParameter("requestTime", date);
            query.setParameter("commandId", commandId);
            query.setParameter("payState", com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY);
            int n = query.executeUpdate();

            if(n < 1){
                logger.error("saveRequstTime n =" + n);
                throw new Exception("保存指令" + commandId + "的请求时间失败。");
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 保存退款请求时间
     * @param commandId
     * @param extParams
     * @param session
     * @throws Exception
     */
    public static void saveRefundRequstTime(Long commandId, Map<String, Object> extParams, Session session) throws Exception{
        logger.info("saveRefundRequstTime 参数：commandId=" + commandId + ",extParams=" + extParams);
        try{
            //获取参数
            Date date = (Date)extParams.get("requestTime");

            //保存
            String sql = "update AMS_OrderPayDetail set refund_request_time=:refundRequestTime where id=:commandId and pay_state=:payState";
            Query query = session.createQuery(sql);
            query.setParameter("refundRequestTime", date);
            query.setParameter("commandId", commandId);
            query.setParameter("payState", Constant.COMMAND_STATE_SUCESS);
            int n = query.executeUpdate();

            if(n < 1){
                logger.error("saveRefundRequstTime n =" + n);
                throw new Exception("保存指令" + commandId + "的请求时间失败。");
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 保存通道的外部流水号
     * @param commandId
     * @param outTradeId
     * @param params
     * @param session
     * @throws Exception
     */
    public static void saveOutTradeId(Long commandId, String outTradeId, Map<String, Object> params, Session session) throws Exception{
        logger.info("Command.saveOutTradeId commandId=" + commandId + ",outTradeId=" + outTradeId + ",params=" + params);

        try{
            String sql = "update AMS_OrderPayDetail set out_trade_id=:outTradeId where id=:commandId";
            Query query = session.createQuery(sql);
            query.setParameter("outTradeId", outTradeId);
            query.setParameter("commandId", commandId);
            query.executeUpdate();
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 退款保存通道的外部流水号
     * @param commandId
     * @param outTradeId
     * @param params
     * @param session
     * @throws Exception
     */
    public static void saveRefundOutTradeId(Long commandId, String outTradeId, Map<String, Object> params, Session session) throws Exception{
        logger.info("Command.saveRefundOutTradeId commandId=" + commandId + ",outTradeId=" + outTradeId + ",params=" + params);

        try{
            String sql = "update AMS_OrderPayDetail set refund_out_trade_id=:refundOutTradeId where id=:commandId";
            Query query = session.createQuery(sql);
            query.setParameter("refundOutTradeId", outTradeId);
            query.setParameter("commandId", commandId);
            query.executeUpdate();
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 关闭指令（只有未支付的指令才允许关闭）
     * @param commandId
     * @param params
     * @param session
     * @throws Exception
     */
    public static void closeCommand(Long commandId, Map<String, Object> params, Session session) throws Exception{
        logger.info("Command.closeCommand参数commandId=" + commandId + ",params=" + params);

        try{
            String sql ="update AMS_OrderPayDetail set pay_state=:payStateSet where id=:commandId and payState=:payStateWhere";
            Query query = session.createQuery(sql);
            query.setParameter("payStateSet", com.kinorsoft.ams.Constant.COMMAND_STATE_FAIL);
            query.setParameter("payStateWhere", com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY);
            query.setParameter("commandId", commandId);
            int n = query.executeUpdate();

            if(n < 1){
                logger.error("closeCommand n =" + n);
                throw new Exception("closeCommand时失败，指令不正确或者指令状态不为未支付状态。");
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 关闭退款指令（只有未退款的指令才允许关闭）
     * @param commandId
     * @param params
     * @param session
     * @throws Exception
     */
    public static void closeRefundCommand(Long commandId, Map<String, Object> params, Session session) throws Exception{
        logger.info("Command.closeCommand参数commandId=" + commandId + ",params=" + params);

        try{
            String sql ="update AMS_OrderPayDetail set refund_status=:refundStatusSet where id=:commandId and refund_status=:refundStatusWhere";
            Query query = session.createQuery(sql);
            query.setParameter("refundStatusSet", Constant.REFUND_STATE_FAIL);
            query.setParameter("refundStatusWhere", Constant.REFUND_STATE_NODO);
            query.setParameter("commandId", commandId);
            int n = query.executeUpdate();

            if(n < 1){
                logger.error("closeCommand n =" + n);
                throw new Exception("closeRefundCommand，指令不正确或者指令状态不为未支付状态。");
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 通过commandNo关闭指令
     * @param commandNo
     * @param params
     * @param session
     * @throws Exception
     */
    public static void closeCommandByCommandNo(String commandNo, Map<String, Object> params, Session session) throws Exception{
        logger.info("Command.closeCommandByCommandNo参数commandNo=" + commandNo + ",params=" + params);

        try{
            String sql ="update AMS_OrderPayDetail set pay_state=:payStateSet where command_no=:commandNo and payState=:payStateWhere";
            Query query = session.createQuery(sql);
            query.setParameter("payStateSet", com.kinorsoft.ams.Constant.COMMAND_STATE_FAIL);
            query.setParameter("payStateWhere", com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY);
            query.setParameter("commandNo", commandNo);
            int n = query.executeUpdate();

            if(n < 1){
                logger.error("closeCommand n =" + n);
                throw new Exception("closeCommandByCommandNo时失败，指令不正确或者指令状态不为未支付状态。");
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 通过commandNo关闭退款指令
     * @param commandNo
     * @param params
     * @param session
     * @throws Exception
     */
    public static void closeRefundCommandByCommnadNo(String commandNo, Map<String, Object> params, Session session) throws Exception{
        logger.info("Command.closeRefundCommandByCommnadNo参数commandNo=" + commandNo + ",params=" + params);

        try{
            String sql ="update AMS_OrderPayDetail set refund_status=:refundStatusSet where command_no=:commandNo and refund_status=:refundStatusWhere";
            Query query = session.createQuery(sql);
            query.setParameter("refundStatusSet", Constant.REFUND_STATE_FAIL);
            query.setParameter("refundStatusWhere", Constant.REFUND_STATE_NODO);
            query.setParameter("commandNo", commandNo);
            int n = query.executeUpdate();

            if(n < 1){
                logger.error("closeCommand n =" + n);
                throw new Exception("closeRefundCommandByCommnadNo，指令不正确或者指令状态不为未支付状态。");
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
