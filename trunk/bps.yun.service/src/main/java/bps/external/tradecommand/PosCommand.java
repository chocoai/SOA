package bps.external.tradecommand;

import bps.common.Constant;
import bps.process.PayChannelManage;
import com.kinorsoft.ams.ITradeCommand;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pos支付
 * 目前只使用到手续费计算
 * Created by yyy on 2016/6/13.
 */
public class PosCommand implements ITradeCommand {
    private static Logger tlt_logger = Logger.getLogger("tlt");
    private final static String payInterfaceNo = Constant.PAY_INTERFACE_POS;
    @Override
    public String getPayInterfaceNo() {
        return payInterfaceNo;
    }

    @Override
    public Map<String, Object> doCommand(Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> doCommands(List<Map<String, Object>> list) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> undoCommand(Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> undoCommands(List<Map<String, Object>> list) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> calculateFees(Map<String, Object> param) throws Exception {
        tlt_logger.info("计算手续费");
        tlt_logger.info("param=" + param);
        String pay_interfaceNo = (String) param.get("pay_interfaceNo");
        Long money = (Long) param.get("trade_money");


        Map<String, Object> result = new HashMap<>();
        Long fee_money = 0L;
        JSONObject payInterface_entity = PayChannelManage.getPayInterfaceInfoPure(pay_interfaceNo);
        tlt_logger.info("payInterface_entity=" + payInterface_entity);
        if(payInterface_entity != null) {
            Long handling_type = null;
            if(payInterface_entity.get("handling_type") != null)
                handling_type = payInterface_entity.optLong("handling_type");
            Long handling_mode = null;
            if(payInterface_entity.get("handling_mode") != null)
                handling_mode = payInterface_entity.optLong("handling_mode");
            Double handling_rate = null;
            if(payInterface_entity.get("handling_rate") != null)
                handling_rate = payInterface_entity.optDouble("handling_rate");
            if(handling_rate == null) {
                handling_rate = 0D;
            }
            Long handling_each = null;
            if(payInterface_entity.get("handling_each") != null)
                handling_each = payInterface_entity.optLong("handling_each");
            tlt_logger.info("handling_each="+handling_each);
            if(handling_each == null) {
                handling_each = 0L;
            }
            tlt_logger.info("handling_each="+handling_each);
            if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_PERCENTAGE)) {
                tlt_logger.info("111111111=");
                fee_money = Math.round(money * handling_rate);
            } else if(handling_type != null && handling_type.equals(Constant.FEE_TYPE_EACH)) {
                tlt_logger.info("222222222=");
                fee_money = handling_each;
            }
            result.put("handling_mode", handling_mode);
        }
        result.put("fee_money", fee_money);
        tlt_logger.info("result=" + result);
        return result;
    }
}
